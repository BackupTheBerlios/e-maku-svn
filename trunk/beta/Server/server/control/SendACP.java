package server.control;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import server.comunications.SocketServer;
import server.database.connection.PoolConexiones;
import server.database.sql.CacheEnlace;
import server.database.sql.CloseSQL;
import server.database.sql.RunQuery;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

import common.comunications.SocketWriter;
import common.misc.ZipHandler;

/**
 * SendACP.java Creado el 10-ago-2004
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
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class SendACP extends Thread{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -757058034217759942L;
	private Document docACPBegin;
	private String bd;
	private String [] Arrlogin;
	private ResultSet rs;
	private Statement st;
	private SocketChannel sock;
	public SendACP(SocketChannel sock, String bd, String login) {
        this.sock = sock;
		this.bd = bd;
		this.Arrlogin = new String[]{login};
		SendACPBegin();
    }
    
    private String validPass(String pass){
        if(pass==null){
            return "trust";
        }
        else{
            return "password";
        }
    }

	public void SendACPBegin() {
		
        Element element = new Element("ACPBegin");
        try {
			st = PoolConexiones.getConnection(bd).createStatement();
		
			RunQuery runquery = new RunQuery(bd,"SEL0108",Arrlogin);
	        rs = runquery.ejecutarSELECT();
	        rs.next();
	        String numTrans = rs.getString(1);
	        
	        runquery = new RunQuery(bd,"SEL0109",Arrlogin);
	        rs = runquery.ejecutarSELECT();
	        rs.next();
	        String numQuerys = rs.getString(1);
	        
			String company = CacheEnlace.getCompanyData(SocketServer.getCompanyNameKey(sock));
			String companyID = CacheEnlace.getCompanyData(SocketServer.getCompanyIDKey(sock));	        
	        
	        docACPBegin = new Document();
	        docACPBegin.setRootElement(element);
	        element.addContent(new Element("companyName").setText(company));
	        element.addContent(new Element("companyID").setText(companyID));
	        element.addContent(new Element("transactions").setText(numTrans));
	        element.addContent(new Element("querys").setText(numQuerys));
			SocketWriter.writing(sock,this.docACPBegin);
        }
        catch (SQLException e) {
			e.printStackTrace();
		} catch (SQLNotFoundException e) {
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
		}
        CloseSQL.close(rs);
        CloseSQL.close(st);
	}
	
	public void run() {
        try {
        	st = PoolConexiones.getConnection(bd).createStatement();
            
        	RunQuery runquery = new RunQuery(bd,"SEL0006",Arrlogin);
            rs = runquery.ejecutarSELECT();
            
            /* Transmision de setencias SQL*/
            Document doc = new Document();
            doc.setRootElement(new Element("ACPData"));
            Element query = new Element("query");
            while(rs.next()){
                Element sql = new Element("sql");
                sql.setText(rs.getString("codigo"));
                sql.setAttribute("type",validPass(rs.getString("password")));
                query.addContent(sql);
            }
            doc.getRootElement().addContent(query);
            SocketWriter.writing(sock,compressDocument(doc,query.getName()));
            rs.close();
            rs = null;
            runquery = new RunQuery(bd,"SEL0005",Arrlogin);
            rs = runquery.ejecutarSELECT();
            
            while(rs.next()){
            	doc = new Document();
                Element element = new Element("ACPData");
            	doc.setRootElement(element);
                try {
	                Element transaction = new Element("transaction");
	
	                Element driver = new Element("driver");
	                driver.setText(rs.getString("codigo"));
	                driver.setAttribute("type",validPass(rs.getString("password")));
	                
	                ByteArrayInputStream bufferIn;
	                bufferIn = new ByteArrayInputStream(rs.getString("perfil").getBytes());
	                
	                SAXBuilder builder = new SAXBuilder(false);
	                Document form = builder.build(bufferIn);
	                transaction.addContent(driver);
	                transaction.addContent(form.detachRootElement());
	                element.addContent(transaction);
	                
	                SocketWriter.writing(sock,compressDocument(doc,driver.getValue()));
                }
                catch (JDOMException e) {
                    e.printStackTrace();
                    System.out.println("Buffer:"+rs.getString("perfil"));
                } catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        catch (SQLException e) {
			e.printStackTrace();
		} catch (SQLNotFoundException e) {
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CloseSQL.close(rs);
        CloseSQL.close(st);
	}
	public Document compressDocument(Document doc, String item) throws IOException {
    	
		Document docZip = new Document();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLOutputter xmlout = new XMLOutputter();
        xmlout.output(doc,baos);
        baos.close();
        
        ZipHandler zip = new ZipHandler(baos,item+".xml");
    	docZip.setRootElement(zip.getElementDataEncode("ACPZip"));

        return docZip;
	}
}
