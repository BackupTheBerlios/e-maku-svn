package server.reportes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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

import server.basedatos.sql.RunQuery;
import server.basedatos.sql.SQLBadArgumentsException;
import server.basedatos.sql.SQLNotFoundException;
import server.comunicaciones.SocketServer;
import server.control.ReportsStore;

import common.comunicaciones.WriteSocket;
import common.miscelanea.ZipHandler;

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
			rs.next();
			XMLOutputter xmout = new XMLOutputter();
			xmout.setFormat(Format.getPrettyFormat());
			xmout.output(element,System.out);
			//System.out.println("Generando reporte No. " + codigo);
			//InputStream iosTemplate = rs.getAsciiStream(1);
			String title = rs.getString(1);
			String sql = rs.getString(2);
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
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Map <String,String>parameters = new HashMap<String,String>();
			parameters.put("Title", title);
			parameters.put("Empresa", SocketServer.getBd(socket));
			parameters.put("Nit", "");
			parameters.put("Fecha", new Date().toString());

			//System.out.println(" * Cargando entrada XML...");
			//JasperDesign jasperDesign = JRXmlLoader.load(iosTemplate);
			//ObjectInputStream obj = new ObjectInputStream(new FileInputStream("/home/midas/report.jasper"));
			//JasperReport jasperReport = null;
			/*try {
				jasperReport = (JasperReport)obj.readObject();
				System.out.println(" * jasperReport = (JasperReport)obj.readObject();");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}*/
			//JasperCompileManager.compileReportToStream(jasperDesign,new FileOutputStream("/home/pastuxso/report.jasper"));
			
			
			//JasperPrint jasperPrint = FillManager.fillReport(jasperReport, parameters, jrRS);
			
			//JRBaseFiller filler = createFiller(jasperReport);
			JRBaseFiller filler = createFiller(ReportsStore.getReportClass(codigo));
			System.out.println(" * createFiller(jasperReport)");
			JasperPrint jasperPrint = filler.fill(parameters,jrRS);
			System.out.println(" * filler.fill(parameters,jrRS)");
			JasperExportManager.exportReportToPdfStream(jasperPrint,os);
			System.out.println(" * exportado a pdf");
			os.close();
			
			Document docZip = new Document();
			Element root = new Element("REPORT");
			Element id = new Element("id").setText(this.id);
			Element idReport = new Element("idReport").setText(codigo);
			Element titleReport = new Element("titleReport").setText(parameters.get("Title"));
			
			ZipHandler zip = new ZipHandler(os,"report.pdf");
			root.addContent(id);
			root.addContent(idReport);
			root.addContent(titleReport);
			root.addContent(zip.getElementDataEncode("data"));
	    	docZip.setRootElement(root);
	        WriteSocket.writing(this.socket,docZip);
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