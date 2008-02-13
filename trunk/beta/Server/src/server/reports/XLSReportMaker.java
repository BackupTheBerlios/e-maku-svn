package server.reports;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.sql.*;
import java.util.*;

import net.sf.jxls.exception.*;
import net.sf.jxls.transformer.*;

import org.apache.commons.beanutils.*;
import org.apache.poi.hssf.usermodel.*;
import org.jdom.*;

import server.comunications.*;
import server.database.sql.*;

import common.comunications.*;
import common.misc.*;

/**
 * 
 * XLSReportMaker.java Created at 05-feb-2008
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * This class handles requests for a report on xls format <br>
 * 
 * @author Cristian Cepeda [cristian@qhatu.net]
 */
public class XLSReportMaker extends Thread {

	private Element rootNode;
	private SocketChannel socket;
	private String dataBase;
	private String transactionId;

	public XLSReportMaker(Element root, SocketChannel sock) {
		this.rootNode = root;
		this.socket = sock;
		this.dataBase = EmakuServerSocket.getBd(socket);
		start();
	}

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			Calendar calendar = Calendar.getInstance();
			long init = calendar.getTimeInMillis();
			this.transactionId = rootNode.getChildText("id");
			String templateCode = rootNode.getChildText("driver");
			String jarDirectory = rootNode.getChildText("jarDirectory");
			String jarFile = rootNode.getChildText("jarFile");
			String [] args = new String[]{ templateCode };
			QueryRunner qr = new QueryRunner(dataBase,"SCS0089",args); 
			ResultSet rs = qr.ejecutarSELECT();
			boolean next = rs.next();
			if (next) {
				System.out.println("Generando reporte No. " + templateCode);
				
				String sql = rs.getString(1).trim();
				String template = rs.getString(2).trim();
				String title = rs.getString(3).trim();
			
				List<Element> list = rootNode.getChild("package").getChildren();
				Iterator<Element> it = list.iterator();
				args = new String[list.size()];
				rs.close();
				rs = null;
				if (list.size() > 0) {
					for (int i = 0; it.hasNext(); i++) {
						Element arg = it.next();
						args[i] = arg.getValue();
					}
					rs = new QueryRunner(EmakuServerSocket.getBd(socket), sql, args).ejecutarSELECT();
				} else {
					rs = new QueryRunner(EmakuServerSocket.getBd(socket), sql).ejecutarSELECT();
				}
				int rows = rs.getFetchSize();
				if (rs!=null) {		
					RowSetDynaClass rsdc = new RowSetDynaClass(rs, false);
		            Map<String, List<?>> beans = new HashMap<String, List<?>>();
		            beans.put("foo", rsdc.getRows());
		            XLSTransformer transformer = new XLSTransformer();
		            String path = "jar:file:"+System.getenv("EMAKU_HOME")+"/lib"+File.separator+
		                          "emaku/"+jarFile+"!/"+jarDirectory+template;
		            try {
		            	URL url = new URL(path);
		            	System.out.println("Read template =>" + url);
		            	InputStream in = url.openStream();
		        		InputStream is = new BufferedInputStream(in);
						HSSFWorkbook workbook = transformer.transformXLS(is, beans);
						if (rows==0) {
							HSSFRow row     = workbook.getSheetAt(0).createRow((short)3); 
							HSSFCell cell   = row.createCell((short)1);
							
							String str = "El reporte no contiene registros, cambie " +
									     "los parámetros y vuelva a intentarlo";
							HSSFRichTextString text = new HSSFRichTextString(str);
							cell.setCellValue(text);
						}
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						workbook.write(bos);
						in.close();
						bos.close();
						ZipHandler ziph = new ZipHandler(bos,template);
						Element xlsData = ziph.getElementDataEncode("data");
						Element rootXLS = new Element("REPORT");
						Element idReport = new Element("idReport").setText(transactionId);
						Element titleReport = new Element("titleReport").setText(title);
						rootXLS.addContent(xlsData);
						rootXLS.addContent(idReport);
						rootXLS.addContent(titleReport);
						Document doc = new Document(rootXLS);
						synchronized (socket) {
							SocketWriter.writing(socket, doc);
							calendar = Calendar.getInstance();
							long end = calendar.getTimeInMillis();
							System.out.println("paquete escrito en "+((end-init)/1000)+" segundos");
						}
						
					} catch (ParsePropertyException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else {
					//TODO Pendiente de validar
					System.out.println("Reporte "+templateCode+" sin registros");
				}
				rs.close();
			}
			else {
				//TODO Pendiente de validar
				System.out.println("No tiene persmisos para el reporte " + templateCode);
			}
			
		} catch (SQLNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
		}
	}
}
