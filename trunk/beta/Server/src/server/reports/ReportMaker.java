package server.reports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRBaseFiller;
import net.sf.jasperreports.engine.fill.JRHorizontalFiller;
import net.sf.jasperreports.engine.fill.JRVerticalFiller;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;

import org.jdom.Document;
import org.jdom.Element;
//import org.jdom.output.Format;
//import org.jdom.output.XMLOutputter;

import server.comunications.EmakuServerSocket;
import server.control.ReportsStore;
import server.database.sql.LinkingCache;
import server.database.sql.QueryRunner;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;
import server.misc.ServerConstants;
import server.misc.settings.ConfigFileHandler;

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

public class ReportMaker extends Thread {

	private String codigo;
	private String id;
	private String[] args;
	private Element element;
	private SocketChannel socket;
	private String dataBase;
	private boolean reportType;
	
	public ReportMaker(Element element, SocketChannel sock, boolean reportType) {
		this.element = element;
		this.socket = sock;
		this.dataBase = EmakuServerSocket.getBd(socket);
		this.reportType = reportType;
		start();
	}

	public ReportMaker() {
		Thread thread = new Thread() {
			public void run() {
				try {
					Map <String,String>parameters = new HashMap<String,String>();
					JRBaseFiller filler = createFiller(ReportsStore.getReportClass("REP0000"));
					JasperPrint jasperPrint = filler.fill(parameters,new PhantomDataSource());
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					JasperExportManager.exportReportToPdfStream(jasperPrint,os);
					os.close();
				}
				catch (JRException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	public void run() {
		try {
			ResultSet rs;
			codigo = element.getChildText("driver");
			rs = new QueryRunner(
					dataBase,
					"SCS0050",
					new String[] { codigo }).ejecutarSELECT();
			boolean next = rs.next();
			String title = null;
			String sql = null;
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Document docZip = new Document();
			Element titleReport;
			if (next) {
				
				/* XMLOutputter xmout = new XMLOutputter();
				xmout.setFormat(Format.getPrettyFormat());
				xmout.output(element,System.out);
				System.out.println("Generando reporte No. " + codigo);
				InputStream iosTemplate = rs.getAsciiStream(1); */
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
					rs = new QueryRunner(EmakuServerSocket.getBd(socket), sql, args).ejecutarSELECT();
				} else {
					rs = new QueryRunner(EmakuServerSocket.getBd(socket), sql).ejecutarSELECT();
				}
				
				// TODO: Si rs es un conjunto vacio... imprima una hoja con el mensaje "Este reporte no contiene registros"
				
				JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);
				
				Map <String,String>parameters = new HashMap<String,String>();
				
				String company = LinkingCache.getCompanyData(EmakuServerSocket.getCompanyNameKey(socket));
				String companyID = LinkingCache.getCompanyData(EmakuServerSocket.getCompanyIDKey(socket));
				
				if (company == null)
					company = "Dato no encontrado";
				
				if (companyID == null)
					companyID = "Dato no encontrado";
				
				parameters.put("Empresa", company);
				parameters.put("Nit", companyID);
				parameters.put("Fecha", DateValidator.getFormattedDate());
				parameters.put("Qhatu", ServerConstants.QHATU);
				parameters.put("Url", ServerConstants.QHATU_URL);
				
				JRBaseFiller filler = createFiller(ReportsStore.getReportClass(codigo));
				JasperPrint jasperPrint = filler.fill(parameters,jrRS);
						
				if (reportType) {
					
					ResultSetMetaData rsmd = rs.getMetaData();
					int cols = rsmd.getColumnCount();
					String headers ="";
					for (int i=1;i <= cols;i++){
						headers+=rsmd.getColumnClassName(i)+"\t";
					}
					headers+="\n";
					os.write(headers.getBytes());
									
				    JRCsvExporter exporter = new JRCsvExporter();
				    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
				    exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER,"\t");
				    exporter.exportReport();

				} else {
					JasperExportManager.exportReportToPdfStream(jasperPrint,os);
				}
				
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
						ServerConstants.EMAKU_HOME+"/reports/images/report_nf_"+
						ConfigFileHandler.getLocal()+".png");
				png.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
				pdfDoc.add(png);
				pdfDoc.add(p);
				pdfDoc.close();
				titleReport = new Element("titleReport").setText("Error");
			}
			os.close();
			String fileType = "REPORT";
			String fileName = "report.pdf";
			
			if (reportType) {
				fileType = "PLAINREPORT";
				fileName = "report.csv";
			} 
			
			Element root = new Element(fileType);
			Element id = new Element("id").setText(this.id);
			Element idReport = new Element("idReport").setText(codigo);
			ZipHandler zip = new ZipHandler(os,fileName);
			root.addContent(id);
			root.addContent(idReport);
			root.addContent(titleReport);
			root.addContent(zip.getElementDataEncode("data"));
	    	docZip.setRootElement(root);
	    	/* System.out.println("escribiendo paquete ....");
	    	XMLOutputter out = new XMLOutputter(); 
	    	out.setFormat(Format.getPrettyFormat());
	    	out.output(docZip, System.out); */
			SocketWriter.writing(this.socket,docZip);
			//System.out.println("paquete escrito ..."); 
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


class PhantomDataSource implements JRDataSource {
	private Object[][] data = new Object[10000][1];
	private int index = -1;
	
	public PhantomDataSource() {
		for (int i=0; i < data.length ; i++) {
			data[i][0] = i;
		}
	}
	
	public boolean next() throws JRException {
		index++;
		return (index < data.length);
	}
	public Object getFieldValue(JRField field) throws JRException {
		return data[index][0];
	}
}