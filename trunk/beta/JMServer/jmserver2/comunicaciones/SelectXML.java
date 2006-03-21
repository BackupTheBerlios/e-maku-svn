package jmserver2.comunicaciones;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import jmserver2.basedatos.sql.CloseSQL;
import jmserver2.basedatos.sql.RunQuery;
import jmserver2.basedatos.sql.SQLBadArgumentsException;
import jmserver2.basedatos.sql.SQLNotFoundException;
import jmserver2.miscelanea.JMServerIICons;

import jmlib.miscelanea.log.AdminLog;
import jmlib.comunicaciones.WriteSocket;
import jmlib.miscelanea.idiom.Language;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 * SelectXML.java Creado el 14-jul-2004
 * 
 * Este archivo es parte de JMServerII
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
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
public class SelectXML extends Document {

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

    public SelectXML(String bd, String sql) {
        this.bd = bd;
        this.sql = sql;
    }
    public SelectXML(String bd, String sql, String [] args) {
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
	            RunQuery rselect;
	
	            if(args==null ) {
	                rselect = new RunQuery(bd, sql);
	            }
	            else {
	                rselect = new RunQuery(bd, sql,args);
	            }
	            
	            ResultSet RSdatos = rselect.ejecutarSELECT();
	
	            try {
	
	                ResultSetMetaData RSMDinfo = RSdatos.getMetaData();
	                int columnas = RSMDinfo.getColumnCount();
	                writeBufferSocket(sock,
	                        JMServerIICons.CONTEN_TYPE+
	                        JMServerIICons.TAGS_ANSWER[0]+
	                        JMServerIICons.TAGS_ID[0]+id+JMServerIICons.TAGS_ID[1]+
	                        JMServerIICons.TAGS_HEAD[0]);
	                
	                for (int i = 1; i <= columnas; i++) {
	                    writeBufferSocket(sock,
	                            JMServerIICons.TAGS_COL_HEAD[0]+
	                            XMLformat.escapeAttributeEntities(RSMDinfo.getColumnTypeName(i))+
	                            JMServerIICons.TAGS_COL_HEAD[1]+
	                            XMLformat.escapeAttributeEntities(RSMDinfo.getColumnName(i))+
	                            JMServerIICons.TAGS_COL[1]);
	                }
	                writeBufferSocket(sock,JMServerIICons.TAGS_HEAD[1]);
	                
	                /**
	                 * Se recorre el resulset para añadir los datos que contenga, y
	                 * se escriben directamente en el socket en formato XML
	                 */
	                byte [] res;
	                while (RSdatos.next()) {
	                    writeBufferSocket(sock,JMServerIICons.TAGS_ROW[0]);
	                    for (int j = 1; j <= columnas; j++) {
	                        
	                        res = RSdatos.getBytes(j);
	                        
	                        if (res==null)
	                            res= new String("").getBytes();
	                        
	                        writeBufferSocket(sock,JMServerIICons.TAGS_COL[0] + 
	                                XMLformat.escapeAttributeEntities(new String(res))+
	                                JMServerIICons.TAGS_COL[1]
	                                );
	                    }
	                    writeBufferSocket(sock,JMServerIICons.TAGS_ROW[1]);
	                }
	                writeBufferSocket(sock,JMServerIICons.TAGS_ANSWER[1]);
	                bufferSocket.write(new String ("\n\r\f").getBytes());
	                WriteSocket.writing(sock,bufferSocket);
	                bufferSocket.close();
	                CloseSQL.close(RSdatos);
	                AdminLog.setMessage(Language.getWord("OK_CREATING_XML"),
	                        JMServerIICons.MESSAGE);
	
	            }
	            catch (SQLException SQLEe) {
	                String err =
	                    Language.getWord("ERR_RS") + " " + SQLEe.getMessage();
	                AdminLog.setMessage(err, JMServerIICons.ERROR);
	                ErrorXML error = new ErrorXML();
	                WriteSocket.writing(sock,error.returnError(JMServerIICons.ERROR, bd, err));
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
	            AdminLog.setMessage(err, JMServerIICons.ERROR);
	            ErrorXML error = new ErrorXML();
	            WriteSocket.writing(sock,error.returnError(JMServerIICons.ERROR, bd, err));
	            QNFEe.printStackTrace();
	
	        } 
	        catch (SQLException SQLEe) {
	            String err = Language.getWord("ERR_ST") + " " + SQLEe.getMessage();
	            AdminLog.setMessage(err, JMServerIICons.ERROR);
	            ErrorXML error = new ErrorXML();
	            WriteSocket.writing(sock,error.returnError(JMServerIICons.ERROR, bd, err));
	            SQLEe.printStackTrace();
	        }
	        catch (SQLBadArgumentsException QBAEe) {
	            String err = QBAEe.getMessage();
	            AdminLog.setMessage(err, JMServerIICons.ERROR);
	            ErrorXML error = new ErrorXML();
	            WriteSocket.writing(sock,error.returnError(JMServerIICons.ERROR, bd, err));
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
                WriteSocket.writing(sock,bufferSocket);
                bufferSocket = new ByteArrayOutputStream();
                i--;
            }
        }
   }   
}