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

import server.comunications.EmakuServerSocket;
import server.database.connection.ConnectionsPool;
import server.database.sql.LinkingCache;
import server.database.sql.QueryRunner;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;
import server.database.sql.StatementsClosingHandler;

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
public class ACPSender extends Thread{
    
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
	private XMLOutputter xmlout = new XMLOutputter();
	
	public ACPSender(SocketChannel sock, String bd, String login) {
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
			st = ConnectionsPool.getConnection(bd).createStatement();
		
			QueryRunner runquery = new QueryRunner(bd,"SCS0042",Arrlogin);
	        rs = runquery.ejecutarSELECT();
	        rs.next();
	        String numTrans = rs.getString(1);
	        
	        runquery = new QueryRunner(bd,"SCS0043",Arrlogin);
	        rs = runquery.ejecutarSELECT();
	        rs.next();
	        String numQuerys = rs.getString(1);
	        
			String company = LinkingCache.getCompanyData(EmakuServerSocket.getCompanyNameKey(sock));
			String companyID = LinkingCache.getCompanyData(EmakuServerSocket.getCompanyIDKey(sock));	        
	        
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
        StatementsClosingHandler.close(rs);
        StatementsClosingHandler.close(st);
        rs=null;
        st=null;
	}
	
	public void run() {
        try {
        	st = ConnectionsPool.getConnection(bd).createStatement();
            
        	QueryRunner runquery = new QueryRunner(bd,"SCS0005",Arrlogin);
            rs = runquery.ejecutarSELECT();
            
            /* Transmision de setencias SQL*/
            Document cnx = new Document();
        	Element selement = new Element("SACPData");

        	Element qdata = new Element("ACPData");
            Element query = new Element("query");
            while(rs.next()){
                Element sql = new Element("sql");
                sql.setText(rs.getString("codigo"));
                sql.setAttribute("type",validPass(rs.getString("password")));
                query.addContent(sql);
            }
            qdata.addContent(query);
            selement.addContent(qdata);
            rs.close();
            rs = null;
            
            
            runquery = new QueryRunner(bd,"SCS0004",Arrlogin);
            rs = runquery.ejecutarSELECT();
        	
            while(rs.next()){
                Element element = new Element("ACPData");
                try {
	                Element transaction = new Element("transaction");
	
	                Element driver = new Element("driver");
	                driver.setText(rs.getString("codigo").trim());
	                driver.setAttribute("type",validPass(rs.getString("password")));
	                
	                ByteArrayInputStream bufferIn;
	                bufferIn = new ByteArrayInputStream(rs.getString("perfil").getBytes());
	                
	                SAXBuilder builder = new SAXBuilder(false);
	                Document form = builder.build(bufferIn);
	                transaction.addContent(driver);
	                transaction.addContent(form.detachRootElement());
	                element.addContent(transaction);
	                selement.addContent(element);
                }
/*                catch (MalformedByteSequenceException e) {
                	e.printStackTrace();
                    System.out.println("Buffer:"+rs.getString("perfil"));
                }
*/                catch (JDOMException e) {
                    e.printStackTrace();
                    System.out.println("Buffer:"+rs.getString("perfil"));
                } catch (IOException e) {
					e.printStackTrace();
				}
            }
            
        	cnx.setRootElement(selement);
            SocketWriter.writing(sock,compressDocument(cnx));
    		StatementsClosingHandler.close(rs);
            StatementsClosingHandler.close(st);
            rs=null;
            st=null;
            
        	System.gc();
        	synchronized(sock) {
            Document prueba = new Document();
            prueba.setRootElement(new Element("ACPData"));
    		Element el = new Element("CACHE-QUERY").setText("CH00003");
    		prueba.getRootElement().addContent(el);
            SocketWriter.writing(sock,compressDocument(prueba));
 
            Document prueba2 = new Document();
            prueba2.setRootElement(new Element("ACPData"));
    		Element el2 = new Element("CACHE-QUERY").setText("CH00004");
    		prueba2.getRootElement().addContent(el2);
            SocketWriter.writing(sock,compressDocument(prueba2));
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
	}
	
	private Document compressDocument(Document data) throws IOException {
		Document docZip = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		xmlout.output(data,baos);
        baos.close();
        ZipHandler zip = new ZipHandler(baos,"data.xml");
    	docZip.setRootElement(zip.getElementDataEncode("ACPZip"));

        return docZip;
		
	}	
}
