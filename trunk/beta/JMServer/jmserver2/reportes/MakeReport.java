package jmserver2.reportes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jmlib.comunicaciones.WriteSocket;
import jmlib.miscelanea.ZipHandler;
import jmserver2.basedatos.sql.RunQuery;
import jmserver2.basedatos.sql.SQLBadArgumentsException;
import jmserver2.basedatos.sql.SQLNotFoundException;
import jmserver2.comunicaciones.SocketServer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.jdom.Document;
import org.jdom.Element;

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
			System.out.println("Generando reporte No. " + codigo);
			InputStream iosTemplate = rs.getAsciiStream(1);
			String sql = rs.getString(2);
			this.id = element.getChildText("id");
			List list = element.getChildren("subarg");
			Iterator it = list.iterator();
			args = new String[list.size()];
			if (list.size() > 0) {
				for (int i = 0; it.hasNext(); i++) {
					Element arg = (Element) it.next();
					args[i] = arg.getValue();
				}
				rs = new RunQuery(SocketServer.getBd(socket), sql, args)
						.ejecutarSELECT();
			} else {
				rs = new RunQuery(SocketServer.getBd(socket), sql)
						.ejecutarSELECT();
			}

			JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Map <String,String>parameters = new HashMap<String,String>();
			parameters.put("Title", "BALANCE GENERAL / ENERO 31 DE 2006");

			System.out.println(" * Cargando entrada XML...");
			JasperDesign jasperDesign = JRXmlLoader.load(iosTemplate);
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrRS);

			System.out.println(" * Creando reporte XML...");
			
			JasperExportManager.exportReportToPdfStream(jasperPrint,os);
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

}
