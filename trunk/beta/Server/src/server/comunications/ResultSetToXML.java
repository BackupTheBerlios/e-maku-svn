package server.comunications;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import server.database.connection.ConnectionsPool;
import server.database.connection.PooledConnections;
import server.database.sql.QueryRunner;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLFormatAgent;
import server.database.sql.SQLNotFoundException;
import server.database.sql.StatementsClosingHandler;
import server.misc.ServerConstants;

import common.comunications.SocketWriter;
import common.misc.language.Language;
import common.misc.log.LogAdmin;

/**
 * ResultSetToXML.java Creado el 14-jul-2004
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
 * Esta clase se encarga de generar la estructura ANSWER de tal forma que pueda
 * ser enviada directamente al cliente que corresponda.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class ResultSetToXML extends Document implements Runnable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6067284901344862308L;
	private String bd;
    private String sql;
    private String [] args;
    private SocketChannel sock;
    private String id;
    
    private ByteArrayOutputStream bufferSocket = null;

    /**
     * Este constructor inicializa los objetos sql y bd nesesario para ejecutar
     * la consulta
     * 
     * @param bd
     *            Base de datos
     * @param sql
     *            Sentencia SQL
     */

    public ResultSetToXML(String bd, String sql,SocketChannel sock, String id) {
        this.bd = bd;
        this.sql = sql;
        this.sock = sock;
        this.id=id;
    }
    public ResultSetToXML(String bd, String sql, String [] args,SocketChannel sock, String id) {
        this.bd = bd;
        this.sql = sql;
        this.args = args; 
        this.sock = sock;
        this.id=id;
    }

    /**
     * Metodo encargado de ejcutar y transmitir la sentencia sql
     */
    public void run() {
    		Connection conn = ConnectionsPool.getMultiConnection(bd);
    		System.out.println("bd: "+bd);
    		Statement st = null;
        	ResultSet RSdatos	= null;
        	try {
            	String sqlCode = null;
            	st = conn.createStatement();
            	
	            bufferSocket = new ByteArrayOutputStream();
                XMLOutputter XMLformat = new XMLOutputter();
	            
	
	            if(args==null ) {
	                sqlCode = SQLFormatAgent.getSentencia(bd,sql);
	            }
	            else {
	                sqlCode = SQLFormatAgent.getSentencia(bd, sql, args);
	            }
	            System.out.println("ejecutando "+sql);
	            // Con JDBC2 en adelante ya no funciona RSdatos = st.executeQuery(sqlCode);
	            st.execute(sqlCode);
	            while ((RSdatos = st.getResultSet())==null) {
	            	int rowCount = st.getUpdateCount();
	            	if (rowCount ==-1) {
	            		break;
		        	}
	            	st.getMoreResults();
	            }
	            
	            ResultSetMetaData RSMDinfo = RSdatos.getMetaData();
                int columnas = RSMDinfo.getColumnCount();
                System.out.println("preparando para transmitir");
                synchronized(sock) {
                    System.out.println("Transmitiendo");
	                writeBufferSocket(sock,
	                        ServerConstants.CONTEN_TYPE+
	                        ServerConstants.TAGS_ANSWER[0]+
	                        ServerConstants.TAGS_ID[0]+id+ServerConstants.TAGS_ID[1]+
	                        ServerConstants.TAGS_HEAD[0]);
	                
	                for (int i = 1; i <= columnas; i++) {
	                    writeBufferSocket(sock,
	                            ServerConstants.TAGS_COL_HEAD[0]+
	                            XMLformat.escapeAttributeEntities(RSMDinfo.getColumnTypeName(i))+
	                            ServerConstants.TAGS_COL_HEAD[1]+
	                            escapeCharacters(RSMDinfo.getColumnName(i))+
	                            ServerConstants.TAGS_COL[1]);
	                }
	                writeBufferSocket(sock,ServerConstants.TAGS_HEAD[1]);
	                
	                /**
	                 * Se recorre el resulset para a�adir los datos que contenga, y
	                 * se escriben directamente en el socket en formato XML
	                 */
	                byte [] res;
	                while (RSdatos.next()) {
	                    writeBufferSocket(sock,ServerConstants.TAGS_ROW[0]);
	                    for (int j = 1; j <= columnas; j++) {
	                        
	                        res = RSdatos.getBytes(j);
	                        
	                        if (res==null)
	                            res= new String("").getBytes();
	                        
	                        writeBufferSocket(sock,ServerConstants.TAGS_COL[0] + 
	                                escapeCharacters(new String(res,"UTF-8"))+
	                                ServerConstants.TAGS_COL[1]
	                                );
	                    }
	                    writeBufferSocket(sock,ServerConstants.TAGS_ROW[1]);
	                }
	                writeBufferSocket(sock,ServerConstants.TAGS_ANSWER[1]);
	                bufferSocket.write(new String ("\n\r\f").getBytes());
	                SocketWriter.writing(EmakuServerSocket.getHchannelclients(),sock,bufferSocket);
	                bufferSocket.close();
	                StatementsClosingHandler.close(RSdatos);
	//	                LogAdmin.setMessage(Language.getWord("OK_CREATING_XML"),
	//	                        ServerConst.MESSAGE);
                }

	        }
	        catch (SQLNotFoundException QNFEe) {
	            String err = QNFEe.getMessage();
	            LogAdmin.setMessage(err, ServerConstants.ERROR);
	            ErrorXML error = new ErrorXML();
	            SocketWriter.writing(EmakuServerSocket.getHchannelclients(),sock,error.returnError(ServerConstants.ERROR, bd, id,err));
	            QNFEe.printStackTrace();
	
	        } 
            catch (SQLException SQLEe) {
                String err =
                    Language.getWord("ERR_RS") + " " +sql+" "+SQLEe.getMessage();
                LogAdmin.setMessage(err, ServerConstants.ERROR);
                ErrorXML error = new ErrorXML();
                SocketWriter.writing(EmakuServerSocket.getHchannelclients(),sock,error.returnError(ServerConstants.ERROR, bd, id,err));
                SQLEe.printStackTrace();
            }
	        catch (SQLBadArgumentsException QBAEe) {
	            String err = QBAEe.getMessage();
	            LogAdmin.setMessage(err, ServerConstants.ERROR);
	            ErrorXML error = new ErrorXML();
	            SocketWriter.writing(EmakuServerSocket.getHchannelclients(),sock,error.returnError(ServerConstants.ERROR, bd, id,err));
	            QBAEe.printStackTrace();
	        } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
	            String err = e.getMessage();
				e.printStackTrace();
	            LogAdmin.setMessage(err, ServerConstants.ERROR);
	            ErrorXML error = new ErrorXML();
	            SocketWriter.writing(EmakuServerSocket.getHchannelclients(),sock,error.returnError(ServerConstants.ERROR, bd, id,err));
			} catch (IOException e) {
	            String err = e.getMessage();
				e.printStackTrace();
	            LogAdmin.setMessage(err, ServerConstants.ERROR);
	            ErrorXML error = new ErrorXML();
	            SocketWriter.writing(EmakuServerSocket.getHchannelclients(),sock,error.returnError(ServerConstants.ERROR, bd, id,err));
			}

	        finally {
	            try {
	            	if (RSdatos!=null) {
	            		RSdatos.close();
	            	}
	            	if (st!=null) {
	            		st.close();
	            	}
	            	ConnectionsPool.freeMultiConnection(bd, conn);
                    System.out.println("Fin de la transmision");

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
        
    }
    
    private String escapeCharacters(String word) {
		word = word.replaceAll("&","&amp;");
		word = word.replaceAll("ñ","&#241;");
		word = word.replaceAll("Ñ","&#209;");

		word = word.replaceAll("á","&#225;");
		word = word.replaceAll("é","&#233;");
		word = word.replaceAll("í","&#237;");
		word = word.replaceAll("ó","&#243;");
		word = word.replaceAll("ú","&#250;");
		
		word = word.replaceAll("Á","&#201;");
		word = word.replaceAll("É","&#241;");
		word = word.replaceAll("Í","&#205;");
		word = word.replaceAll("Ó","&#211;");
		word = word.replaceAll("Ú","&#218;");
		
		word = word.replaceAll("<","&lt;");
		word = word.replaceAll(">","&gt;");

		word = word.replaceAll("'","&apos;");
		word = word.replaceAll("\"","&quot;");
		return word;
	}
    private void writeBufferSocket(SocketChannel sock,String data) {

        byte[] bytes = data.getBytes();

        for (int i=0;i<bytes.length;i++) {
        
            if (bufferSocket.size()<8192) {
                bufferSocket.write(bytes[i]);
            }
            else {
                SocketWriter.writing(EmakuServerSocket.getHchannelclients(),sock,bufferSocket);
				bufferSocket = new ByteArrayOutputStream();
                i--;
            }
        }
   }   
}