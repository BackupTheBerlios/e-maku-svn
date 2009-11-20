package server.reports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
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
import server.database.connection.ConnectionsPool;
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
import common.misc.language.Language;

/**
 * ReportMaker.java Creado el 29-jun-2004
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * 
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

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
		//start();
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
			Calendar calendar = Calendar.getInstance();
			long init = calendar.getTimeInMillis();
			
			codigo = element.getChildText("driver");
			Connection conn;
        	while (true) {
        		try {
            		conn = ConnectionsPool.getMultiConnection(dataBase);
            		if (conn!=null) {
            			break;
            		}
            		else {
            			System.out.println("Conexiones agotadas al motor de base de datos...");
            			Thread.sleep(1000);
            		}
        		}
        		catch(InterruptedException e) {
        			e.printStackTrace();
        		}
        	}

			ResultSet rs = new QueryRunner(
											dataBase,
											"SCS0050",
											new String[] { codigo }).ejecutarMTSELECT(conn);
			boolean next = rs.next();
			String title = null;
			String sql = null;
			Element titleReport;
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Document docZip = new Document();
			
			if (next) {
				System.out.println("Generando reporte No. " + codigo);
				
				/* XMLOutputter xmout = new XMLOutputter();
				xmout.setFormat(Format.getPrettyFormat());
				xmout.output(element,System.out);
				InputStream iosTemplate = rs.getAsciiStream(1); */
				title = rs.getString(1);
				sql = rs.getString(2).trim();
			
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
					rs = new QueryRunner(EmakuServerSocket.getBd(socket), sql, args).ejecutarMTSELECT(conn);
				} else {
					rs = new QueryRunner(EmakuServerSocket.getBd(socket), sql).ejecutarMTSELECT(conn);
				}
				// invalidos para jdbc2 o superior
				//int resultSize = rs.getFetchSize();
				//rs.getFetchSize();
				if (rs==null) {
					os = paintPDFMessage(os, "/reports/images/report_noresult_",false);
				} 
				else {
					
					  JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);

					  Map <String,String>parameters = new HashMap<String,String>();

					  String company = LinkingCache.getCompanyData(EmakuServerSocket.getCompanyNameKey(socket));
					  String companyID = LinkingCache.getCompanyData(EmakuServerSocket.getCompanyIDKey(socket));
					  String address = LinkingCache.getAddress(EmakuServerSocket.getBd(socket));
					  String phone = LinkingCache.getPhone(EmakuServerSocket.getBd(socket));
					  String city = LinkingCache.getCity(EmakuServerSocket.getBd(socket));

					  if (company == null) {
						  company = Language.getWord("DATANOTFOUND");
					  }

					  if (companyID == null) {
						  companyID = Language.getWord("DATANOTFOUND");
					  }

					  parameters.put("Empresa", company);
					  parameters.put("Nit", companyID);
					  parameters.put("Direccion", address);
					  parameters.put("Telefono", phone);
					  parameters.put("Ciudad", city);
					  parameters.put("Fecha", DateValidator.getFormattedDate());
					  parameters.put("Qhatu", ServerConstants.QHATU);
					  //parameters.put("Url", ServerConstants.QHATU_URL);

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
				}
				titleReport = new Element("titleReport").setText(title);
			}
			else {
				os = paintPDFMessage(os, "/reports/images/report_nf_",true);
				titleReport = new Element("titleReport").setText(Language.getWord("REP_ERROR"));
			}
			os.close();
			String fileType = "REPORT";
			String fileName = "report.pdf";
			
			if (reportType) {
				fileType = "PLAINREPORT";
				fileName = "report.csv";
			} 
			System.out.println("Empaquetando Reporte...");
			Element root = new Element(fileType);
			Element id = new Element("id").setText(this.id);
			Element idReport = new Element("idReport").setText(codigo);
			root.addContent(id);
			root.addContent(idReport);
			root.addContent(titleReport);
			
			ZipHandler zip = new ZipHandler(os,fileName);
			System.out.println("Reporte empaquetado...");
			
			root.addContent(zip.getElementDataEncode("data"));
	    	docZip.setRootElement(root);
			calendar = Calendar.getInstance();
			long end = calendar.getTimeInMillis();
			System.out.println("Generador en " + (end-init)/1000 + " segundos ");
	    	
	    	System.out.println("Escribiendo paquete ....");
			calendar = Calendar.getInstance();
			init = calendar.getTimeInMillis();
	    	/* 
	    	XMLOutputter out = new XMLOutputter(); 
	    	out.setFormat(Format.getPrettyFormat());
	    	out.output(docZip, System.out); */
			SocketWriter.writing(EmakuServerSocket.getHchannelclients(),this.socket,docZip);
			calendar = Calendar.getInstance();
			end = calendar.getTimeInMillis();
			System.out.println("paquete escrito en "+((end-init)/1000)+" segundos"); 
	        rs.close();
        	ConnectionsPool.freeMultiConnection(dataBase, conn);
	        rs = null;
	        conn=null;
	        os.close();
	        os = null;
	        System.gc();
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

	public ByteArrayOutputStream paintPDFMessage(ByteArrayOutputStream os, String path, boolean flag) {
		com.lowagie.text.Document pdfDoc;
		pdfDoc = new com.lowagie.text.Document(new Rectangle(400,200),10,10,10,10);

		try {
			PdfWriter.getInstance(pdfDoc,os);
			pdfDoc.open();
			Image png = Image.getInstance(
			ServerConstants.EMAKU_HOME + path + ConfigFileHandler.getLocal()+".png");
			png.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
			pdfDoc.add(png);
			
			if (flag) {
				Chunk chunck = new Chunk(Language.getWord("REFERENCE") + " " + codigo);
				chunck.setFont(new Font(Font.HELVETICA, 12));
				Paragraph p = new Paragraph();
				p.setAlignment(Paragraph.ALIGN_CENTER);
				p.add(chunck);			
				pdfDoc.add(p);
			}
			
			pdfDoc.close();
		} 
		catch (DocumentException e) {
			e.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return os;
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