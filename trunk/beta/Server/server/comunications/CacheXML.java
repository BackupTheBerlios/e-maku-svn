package server.comunications;

import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;

import server.database.sql.CloseSQL;
import server.database.sql.RunQuery;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;
import server.misc.ServerConst;

import common.misc.language.Language;
import common.misc.log.AdminLog;
import common.comunications.WriteSocket;

import org.jdom.Document;

/**
 * CacheXML.java Creado el 06-sep-2004
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
 * Esta clase se encarga de generar los paquetes cache-answer, apartir del codigo
 * solicitado.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class CacheXML extends Document {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3555258118537262861L;
	private String bd;
    private String codigo;

    public CacheXML(String bd,String codigo) {
        this.bd=bd;
        this.codigo=codigo;
    }

    /**
     * Metodo encargado de ejcutar y transmitir la sentencia sql
     */
    public void transmition(SocketChannel sock) {
        try {            
            RunQuery rselect;
            
            /*
             * Se obtiene las llaves del cache que se va a generar
             * la consulta para obtener las llaves es la SEL0007,
             * recibe como argumentos el codigo de CACHE 
             */
            String[] args = {codigo};
            String SQL;
            ResultSet RSdatos = new RunQuery(bd, "SEL0009",args).ejecutarSELECT();
            RSdatos.next();
            SQL= RSdatos.getString("codigo");
            CloseSQL.close(RSdatos);
            
            rselect = new RunQuery(bd, "SEL0007",args);
            
            RSdatos = rselect.ejecutarSELECT();

            /*
             *  Se captura las llaves en una tabla hash y en un String
             */
            
            Hashtable <String,String>keys = new Hashtable<String,String>();
            String skey = "";
            String llave="";
            int num_col_keys=0;
            try {
	            while (RSdatos.next()) {
	                llave = RSdatos.getString("codigo").trim();
                    keys.put(llave,"");
                    skey+=llave;
                    num_col_keys++;
	            }
	            
	            CloseSQL.close(RSdatos);
	            /*
	             * A medida que se empieza a generar el paquete CACHE-ANSWER, se empieza
	             * a transmitir 
	             */
	            
	            RSdatos = new RunQuery(bd, "SEL0010",args).ejecutarSELECT();
	            RSdatos.next();
	            String cache_sql = RSdatos.getString("sentencia_cache");
	            
	            RSdatos = new RunQuery(bd, codigo, cache_sql).ejecutarSELECT();
                ResultSetMetaData RSMDinfo = RSdatos.getMetaData();
                int columnas = RSMDinfo.getColumnCount();
                
                /*
                 * Generacion de etiquetas
                 * <CACHE-ANSWER>
                 * 		<header>
                 */
                
                
                WriteSocket.writing(sock,
                        ServerConst.CONTEN_TYPE+
                        ServerConst.TAGS_CACHE_ANSWER[0]+
                        ServerConst.TAGS_SQL[0]+ SQL +
                        ServerConst.TAGS_SQL[1]+
                        ServerConst.TAGS_HEAD[0]);
                
                for (int i = 1; i <= columnas; i++) {
                    /*
                     * Se escribe las cabeceras diferentes a las llaves
                     * generacion de etiquetas
                     * 		<col type="tipo">nombre</col>
                     */
                    
                    if (!keys.containsKey(RSMDinfo.getColumnName(i)))
	                    WriteSocket.writing(sock,
	                            ServerConst.TAGS_COL_HEAD[0]+
	                            RSMDinfo.getColumnTypeName(i)+
	                            ServerConst.TAGS_COL_HEAD[1]+
	                            RSMDinfo.getColumnName(i)+
	                            ServerConst.TAGS_COL[1]);
                }

                /*
                 * Generacion de etiquetas
                 * 		</header>
                 * 		<value>
                 */
                WriteSocket.writing(sock,ServerConst.TAGS_HEAD[1]);
                        	                
                /*
                 * Se recorre el resulset para a�adir los datos que contenga, y
                 * se escriben directamente en el socket en formato XML
                 */
                
                String new_key_data="";
                String old_key_data="";
                boolean close_tags=true;
                
                while (RSdatos.next()) {
                    new_key_data="";
                    for (int j = 1; j <= num_col_keys; j++) {
                        new_key_data+=RSdatos.getString(j).trim();

                    }
                    
                    
                    if (!new_key_data.equals(old_key_data)) {
                        WriteSocket.writing(sock,ServerConst.TAGS_VALUE[0]+
                                			ServerConst.TAGS_KEY[0]+
                                			new_key_data+
                                			ServerConst.TAGS_KEY[1]+
                                			ServerConst.TAGS_ANSWER[0]
                                			);
                    }
                    /*
                     * <row>
                     * 	<col>value</col>
                     */
                    WriteSocket.writing(sock,ServerConst.TAGS_ROW[0]);
                    for (int j = num_col_keys+1; j <= columnas; j++) {
                        WriteSocket.writing(sock,ServerConst.TAGS_COL[0] + 
                                RSdatos.getString(j).trim()+
                                ServerConst.TAGS_COL[1]
                            );
                    }
                    WriteSocket.writing(sock,ServerConst.TAGS_ROW[1]);
                    /*
                     * </row>
                     */
                    
                    if (!new_key_data.equals(old_key_data) && !old_key_data.equals("")) {
                        WriteSocket.writing(sock,ServerConst.TAGS_ANSWER[1]+
                                			ServerConst.TAGS_VALUE[1]
                                			);
                        close_tags=false;
                    } else
                        close_tags=true;

                    old_key_data=new_key_data;
                    
                }
                
                if (close_tags)
                    WriteSocket.writing(sock,ServerConst.TAGS_ANSWER[1]+
                			ServerConst.TAGS_VALUE[1]);
                			
                        
                WriteSocket.writing(sock,ServerConst.TAGS_CACHE_ANSWER[1]);
                
                CloseSQL.close(RSdatos);
                AdminLog.setMessage(Language.getWord("OK_CREATING_XML"),
                        ServerConst.MESSAGE);

            }
            catch (SQLException SQLEe) {
                String err =
                    Language.getWord("ERR_RS") + " " + SQLEe.getMessage();
                AdminLog.setMessage(err, ServerConst.ERROR);
                ErrorXML error = new ErrorXML();
                WriteSocket.writing(sock,error.returnError(ServerConst.ERROR, bd, err));
            }
            rselect.closeStatement();
        }
        catch (SQLNotFoundException QNFEe) {
            String err = QNFEe.getMessage();
            AdminLog.setMessage(err, ServerConst.ERROR);
            ErrorXML error = new ErrorXML();
            WriteSocket.writing(sock,error.returnError(ServerConst.ERROR, bd, err));

        } 
        catch (SQLException SQLEe) {
            String err = Language.getWord("ERR_ST") + " " + SQLEe.getMessage();
            AdminLog.setMessage(err, ServerConst.ERROR);
            ErrorXML error = new ErrorXML();
            WriteSocket.writing(sock,error.returnError(ServerConst.ERROR, bd, err));
        }
        catch (SQLBadArgumentsException QBAEe) {
            String err = QBAEe.getMessage();
            AdminLog.setMessage(err, ServerConst.ERROR);
            ErrorXML error = new ErrorXML();
            WriteSocket.writing(sock,error.returnError(ServerConst.ERROR, bd, err));
        }
    }
}
