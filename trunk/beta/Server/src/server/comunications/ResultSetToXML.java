package server.comunications;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import server.database.sql.StatementsClosingHandler;
import server.database.sql.QueryRunner;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;
import server.misc.ServerConstants;

import common.misc.language.Language;
import common.misc.log.LogAdmin;
import common.comunications.SocketWriter;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

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
public class ResultSetToXML extends Document {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6067284901344862308L;
	private String bd;
    private String sql;
    private String [] args;
    
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

    public ResultSetToXML(String bd, String sql) {
        this.bd = bd;
        this.sql = sql;
    }
    public ResultSetToXML(String bd, String sql, String [] args) {
        this.bd = bd;
        this.sql = sql;
        this.args = args; 
    }

    /**
     * Metodo encargado de ejcutar y transmitir la sentencia sql
     */
    public void transmition(SocketChannel sock, String id) {

        synchronized(sock) {
	        try {
	            bufferSocket = new ByteArrayOutputStream();
                XMLOutputter XMLformat = new XMLOutputter();
	            QueryRunner rselect;
	
	            if(args==null ) {
	                rselect = new QueryRunner(bd, sql);
	            }
	            else {
	                rselect = new QueryRunner(bd, sql,args);
	            }
	            
	            ResultSet RSdatos = rselect.ejecutarSELECT();
	
	            try {
	
	                ResultSetMetaData RSMDinfo = RSdatos.getMetaData();
	                int columnas = RSMDinfo.getColumnCount();
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
	                            XMLformat.escapeAttributeEntities(RSMDinfo.getColumnName(i))+
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
	                                XMLformat.escapeAttributeEntities(new String(res))+
	                                ServerConstants.TAGS_COL[1]
	                                );
	                    }
	                    writeBufferSocket(sock,ServerConstants.TAGS_ROW[1]);
	                }
	                writeBufferSocket(sock,ServerConstants.TAGS_ANSWER[1]);
	                bufferSocket.write(new String ("\n\r\f").getBytes());
	                SocketWriter.writing(sock,bufferSocket);
	                bufferSocket.close();
	                StatementsClosingHandler.close(RSdatos);
//	                LogAdmin.setMessage(Language.getWord("OK_CREATING_XML"),
//	                        ServerConst.MESSAGE);
	
	            }
	            catch (SQLException SQLEe) {
	                String err =
	                    Language.getWord("ERR_RS") + " " +sql+" "+SQLEe.getMessage();
	                LogAdmin.setMessage(err, ServerConstants.ERROR);
	                ErrorXML error = new ErrorXML();
	                SocketWriter.writing(sock,error.returnError(ServerConstants.ERROR, bd, id,err));
	                SQLEe.printStackTrace();
	            }
	            catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            rselect.closeStatement();
	        }
	        catch (SQLNotFoundException QNFEe) {
	            String err = QNFEe.getMessage();
	            LogAdmin.setMessage(err, ServerConstants.ERROR);
	            ErrorXML error = new ErrorXML();
	            SocketWriter.writing(sock,error.returnError(ServerConstants.ERROR, bd, id,err));
	            QNFEe.printStackTrace();
	
	        } 
	        catch (SQLException SQLEe) {
	            String err = Language.getWord("ERR_ST") + " "+sql+" "+ SQLEe.getMessage();
	            LogAdmin.setMessage(err, ServerConstants.ERROR);
	            ErrorXML error = new ErrorXML();
	            SocketWriter.writing(sock,error.returnError(ServerConstants.ERROR, bd, id,err));
	            SQLEe.printStackTrace();
	        }
	        catch (SQLBadArgumentsException QBAEe) {
	            String err = QBAEe.getMessage();
	            LogAdmin.setMessage(err, ServerConstants.ERROR);
	            ErrorXML error = new ErrorXML();
	            SocketWriter.writing(sock,error.returnError(ServerConstants.ERROR, bd, id,err));
	            QBAEe.printStackTrace();
	        }
        }
    }
    
    private void writeBufferSocket(SocketChannel sock,String data) {

        byte[] bytes = data.getBytes();

        for (int i=0;i<bytes.length;i++) {
        
            if (bufferSocket.size()<8192) {
                bufferSocket.write(bytes[i]);
            }
            else {
                SocketWriter.writing(sock,bufferSocket);
                bufferSocket = new ByteArrayOutputStream();
                i--;
            }
        }
   }   
}