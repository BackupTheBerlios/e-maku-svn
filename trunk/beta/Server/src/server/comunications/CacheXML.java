package server.comunications;

import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;

import server.database.sql.StatementsClosingHandler;
import server.database.sql.QueryRunner;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;
import server.misc.ServerConstants;

import common.misc.language.Language;
import common.misc.log.LogAdmin;
import common.comunications.SocketWriter;

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
        	System.out.println("transmitiendo cache..");
            QueryRunner rselect;
            
            /*
             * Se obtiene las llaves del cache que se va a generar
             * la consulta para obtener las llaves es la SCS0006,
             * recibe como argumentos el codigo de CACHE 
             */
            String[] args = {codigo};
            String SQL;
            ResultSet RSdatos = new QueryRunner(bd, "SCS0007",args).ejecutarSELECT();
            RSdatos.next();
            SQL= RSdatos.getString("codigo");
            StatementsClosingHandler.close(RSdatos);
            
            rselect = new QueryRunner(bd, "SCS0006",args);
            
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
	            
	            StatementsClosingHandler.close(RSdatos);
	            
	            /*
	             * A medida que se empieza a generar el paquete CACHE-ANSWER, se empieza
	             * a transmitir 
	             */
	            
	            RSdatos = new QueryRunner(bd, "SCS0008",args).ejecutarSELECT();
	            RSdatos.next();
	            String cache_sql = RSdatos.getString("sentencia_cache");
	            
	            RSdatos = new QueryRunner(bd, codigo, cache_sql).ejecutarSELECT();
                ResultSetMetaData RSMDinfo = RSdatos.getMetaData();
                int columnas = RSMDinfo.getColumnCount();
                
                /*
                 * Generacion de etiquetas
                 * <CACHE-ANSWER>
                 * 		<header>
                 */
                
            	System.out.println("primera cabecera ...");
                
                SocketWriter.writing(sock,
                        ServerConstants.CONTEN_TYPE+
                        ServerConstants.TAGS_CACHE_ANSWER[0]+
                        ServerConstants.TAGS_SQL[0]+ SQL +
                        ServerConstants.TAGS_SQL[1]+
                        ServerConstants.TAGS_HEAD[0]);
                System.out.println("columnas: "+columnas);
                for (int i = 1; i <= columnas; i++) {
                    /*
                     * Se escribe las cabeceras diferentes a las llaves
                     * generacion de etiquetas
                     * 		<col type="tipo">nombre</col>
                     */
                    
                    if (!keys.containsKey(RSMDinfo.getColumnName(i)))
	                    SocketWriter.writing(sock,
	                            ServerConstants.TAGS_COL_HEAD[0]+
	                            RSMDinfo.getColumnTypeName(i)+
	                            ServerConstants.TAGS_COL_HEAD[1]+
	                            RSMDinfo.getColumnName(i)+
	                            ServerConstants.TAGS_COL[1]);
                }

                /*
                 * Generacion de etiquetas
                 * 		</header>
                 * 		<value>
                 */
                SocketWriter.writing(sock,ServerConstants.TAGS_HEAD[1]);
                        	                
                /*
                 * Se recorre el resulset para a�adir los datos que contenga, y
                 * se escriben directamente en el socket en formato XML
                 */
                
                String new_key_data="";
                String old_key_data="";
                boolean close_tags=false;
                System.out.println("Adicionando datos, numero de llaves: "+num_col_keys);
                while (RSdatos.next()) {
                    new_key_data="";
                    for (int j = 1; j <= num_col_keys; j++) {
                        new_key_data+=RSdatos.getString(j).trim();
                        System.out.println("new_key_data: "+new_key_data);
                        close_tags=true;
                    }
                    
                    
                    if (!new_key_data.equals(old_key_data) || !close_tags==false) {
                        SocketWriter.writing(sock,ServerConstants.TAGS_VALUE[0]+
                                			ServerConstants.TAGS_KEY[0]+
                                			new_key_data+
                                			ServerConstants.TAGS_KEY[1]+
                                			ServerConstants.TAGS_ANSWER[0]
                                			);
                    }
                    /*
                     * <row>
                     * 	<col>value</col>
                     */
                    SocketWriter.writing(sock,ServerConstants.TAGS_ROW[0]);
                    for (int j = num_col_keys+1; j <= columnas; j++) {
                        SocketWriter.writing(sock,ServerConstants.TAGS_COL[0] + 
                                RSdatos.getString(j).trim()+
                                ServerConstants.TAGS_COL[1]
                            );
                    }
                    SocketWriter.writing(sock,ServerConstants.TAGS_ROW[1]);
                    /*
                     * </row>
                     */
                    
                    if (!new_key_data.equals(old_key_data) && !old_key_data.equals("")) {
                        SocketWriter.writing(sock,ServerConstants.TAGS_ANSWER[1]+
                                			ServerConstants.TAGS_VALUE[1]
                                			);
                        close_tags=false;
                    } else
                        close_tags=true;

                    old_key_data=new_key_data;
                    
                }
                
                if (close_tags)
                    SocketWriter.writing(sock,ServerConstants.TAGS_ANSWER[1]+
                			ServerConstants.TAGS_VALUE[1]);
                			
                        
                SocketWriter.writing(sock,ServerConstants.TAGS_CACHE_ANSWER[1]);
            	System.out.println("cabeceras cerradas ....");

                StatementsClosingHandler.close(RSdatos);
                LogAdmin.setMessage(Language.getWord("OK_CREATING_XML"),
                        ServerConstants.MESSAGE);

            }
            catch (SQLException SQLEe) {
                String err =
                    Language.getWord("ERR_RS") + " " + SQLEe.getMessage();
                LogAdmin.setMessage(err, ServerConstants.ERROR);
                ErrorXML error = new ErrorXML();
                SocketWriter.writing(sock,error.returnError(ServerConstants.ERROR, bd, err));
            }
            rselect.closeStatement();
        }
        catch (SQLNotFoundException QNFEe) {
            String err = QNFEe.getMessage();
            LogAdmin.setMessage(err, ServerConstants.ERROR);
            ErrorXML error = new ErrorXML();
            SocketWriter.writing(sock,error.returnError(ServerConstants.ERROR, bd, err));

        } 
        catch (SQLException SQLEe) {
            String err = Language.getWord("ERR_ST") + " " + SQLEe.getMessage();
            LogAdmin.setMessage(err, ServerConstants.ERROR);
            ErrorXML error = new ErrorXML();
            SocketWriter.writing(sock,error.returnError(ServerConstants.ERROR, bd, err));
        }
        catch (SQLBadArgumentsException QBAEe) {
            String err = QBAEe.getMessage();
            LogAdmin.setMessage(err, ServerConstants.ERROR);
            ErrorXML error = new ErrorXML();
            SocketWriter.writing(sock,error.returnError(ServerConstants.ERROR, bd, err));
        }
    }
}
