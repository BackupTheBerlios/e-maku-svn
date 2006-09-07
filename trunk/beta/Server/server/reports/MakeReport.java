package server.reports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRBaseFiller;
import net.sf.jasperreports.engine.fill.JRHorizontalFiller;
import net.sf.jasperreports.engine.fill.JRVerticalFiller;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import server.comunications.SocketServer;
import server.control.ReportsStore;
import server.database.sql.CacheEnlace;
import server.database.sql.RunQuery;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;
import server.misc.ServerConst;
import server.misc.settings.ConfigFile;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import common.comunications.SocketWriter;
import common.misc.ZipHandler;
import common.misc.text.DateValidator;

public class MakeReport extends Thread {

	private String codigo;
	private String id;
	private String[] args;
	private Element element;
	private SocketChannel socket;

	public MakeReport(Element element, SocketChannel sock) {
		this.element = element;
		this.socket = sock;
	}

	public void run() {
		try {
			ResultSet rs;
			codigo = element.getChildText("driver");
			rs = new RunQuery(
					SocketServer.getBd(socket),
					"SEL0208",
					new String[] { codigo }).ejecutarSELECT();
			boolean next = rs.next();
			String title = null;
			String sql = null;
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Document docZip = new Document();
			Element titleReport;
			if (next) {
				XMLOutputter xmout = new XMLOutputter();
				xmout.setFormat(Format.getPrettyFormat());
				xmout.output(element,System.out);
				//System.out.println("Generando reporte No. " + codigo);
				//InputStream iosTemplate = rs.getAsciiStream(1);
				title = rs.getString(1);
				sql = rs.getString(2);
			
				this.id = element.getChildText("id");
				List list = element.getChild("package").getChildren();
				Iterator it = list.iterator();
				args = new String[list.size()];
				rs.close();
				rs = null;
				if (list.size() > 0) {
					for (int i = 0; it.hasNext(); i++) {
						Element arg = (Element) it.next();
						args[i] = arg.getValue();
					}
					rs = new RunQuery(SocketServer.getBd(socket), sql, args).ejecutarSELECT();
				} else {
					rs = new RunQuery(SocketServer.getBd(socket), sql).ejecutarSELECT();
				}
				JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);
				
				Map <String,String>parameters = new HashMap<String,String>();
				
				String company = CacheEnlace.getCompanyData(SocketServer.getCompanyNameKey(socket));
				String idCompany = CacheEnlace.getCompanyData(SocketServer.getCompanyIDKey(socket));
				
				if (company == null)
					company = "Dato no encontrado";
				
				if (idCompany == null)
					idCompany = "Dato no encontrado";
				
				parameters.put("Empresa", company);
				parameters.put("Nit", idCompany);
				parameters.put("Fecha", DateValidator.getFormattedDate());
	
				JRBaseFiller filler = createFiller(ReportsStore.getReportClass(codigo));
				System.out.println(" * createFiller(jasperReport)");
				JasperPrint jasperPrint = filler.fill(parameters,jrRS);
				System.out.println(" * filler.fill(parameters,jrRS)");
				JasperExportManager.exportReportToPdfStream(jasperPrint,os);
				System.out.println(" * exportado a pdf");
				titleReport = new Element("titleReport").setText(title);
			}
			else {
				com.lowagie.text.Document pdfDoc;
				pdfDoc = new com.lowagie.text.Document(new Rectangle(400,200),10,10,10,10);

				PdfWriter.getInstance(pdfDoc,os);
				pdfDoc.open();
				Chunk chunck = new Chunk(codigo);
				chunck.setFont(new Font(Font.HELVETICA, 12));
				Paragraph p = new Paragraph();
				p.setAlignment(Paragraph.ALIGN_CENTER);
				p.add(chunck);
				Image png = Image.getInstance(
						ServerConst.EMAKU_HOME+"/reports/images/report_nf_"+
						ConfigFile.getLocal()+".png");
				png.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
				pdfDoc.add(png);
				pdfDoc.add(p);
				pdfDoc.close();
				titleReport = new Element("titleReport").setText("Error");
			}
			os.close();
			Element root = new Element("REPORT");
			Element id = new Element("id").setText(this.id);
			Element idReport = new Element("idReport").setText(codigo);
			ZipHandler zip = new ZipHandler(os,"report.pdf");
			root.addContent(id);
			root.addContent(idReport);
			root.addContent(titleReport);
			root.addContent(zip.getElementDataEncode("data"));
	    	docZip.setRootElement(root);
			SocketWriter.writing(this.socket,docZip);
	        rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (SQLNotFoundException e) {
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public JRBaseFiller createFiller(JasperReport jasperReport) throws JRException {
		JRBaseFiller filler = null;

		switch (jasperReport.getPrintOrder()) {
			case JRReport.PRINT_ORDER_HORIZONTAL :{
				filler = new HorizontalFiller(jasperReport);
				break;
			}
			case JRReport.PRINT_ORDER_VERTICAL :{
				filler = new VerticalFiller(jasperReport);
				break;
			}
		}
		return filler;
	}
}

class HorizontalFiller extends JRHorizontalFiller {

	protected HorizontalFiller(JasperReport arg0) throws JRException {
		super(arg0);
	}
}
class VerticalFiller extends JRVerticalFiller {

	protected VerticalFiller(JasperReport arg0) throws JRException {
		super(arg0);
	}
}