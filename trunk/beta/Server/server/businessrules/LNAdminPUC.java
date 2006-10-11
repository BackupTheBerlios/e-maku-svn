package server.businessrules;

import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;

import server.comunications.SocketServer;
import server.database.sql.LinkingCache;
import server.database.sql.RunQuery;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

import common.misc.language.Language;

/**
 * LNAdminPUC.java Creado el 07-mar-2005
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
 * Esta clase procesara las transacciones encargadas de la administracion de 
 * cuentas contables, tanto crearlas, editarlas como eliminarlas.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class LNAdminPUC {

    private SocketChannel sock;
    private String id_transaction;
    private LNGenericSQL LNGtransaccion;
    private String argValue;
    private String SQLpadre;
    private String SQLhijo;
    private String SQLenableAccount;
    private String SQLdisableAccount;
    private String SQLenableFather;
    private String SQLdisableFather;
    
    /**
     * Todos los constructores del paquete Logica de negocios constan de los mismos
     * argumentos, ellos son
     * @param sock Este almacenara el socket por el cual la clase retornara los paquetes
     *             de control, ya sea <ERROR>, <SUCCEFUSS> o dado el caso <MESSAGE>
     * @param doc Este objeto almacena los argumentos necesarios para poder procesar la 
     *            transaccion, en ellos se encuentra parametros de funcion, como NEW, EDIT
     *            o DELETE, como tambien subargumentos que almacenan los codigos de las 
     *            sentencias para procesar dicha transaccion.
     * @param pack Este objeto almacena el corazon de la transaccion, es decir solo los datos
     *             que seran procesados, en ellos se encuentran etiquetas de la forma <PACKAGE>
     *             y <SUBPACKAGE>.
     * @param id_transaction Este string almacena el id de la transaccion, con el cual se 
     *                       etiquetaran los paquetes que retornen del resultado del procesamiento
     *                       de la transaccion.            
     */
    public LNAdminPUC(SocketChannel sock,
			 Document doc,
			 Element pack,
			 String id_transaction) {
        
        this.sock=sock;
        this.id_transaction=id_transaction;

        
        LNGtransaccion = new LNGenericSQL(sock);
        LNGtransaccion.setAutoCommit(false);
        
        /**
         * Los argumentos de esta clase seran de la forma
         * 
         * <container>
         * 		<!-- Con esta etiqueta se define la funcion que realizara la clase -->
         * 		<arg>NEW</arg>
         * 		<!-- Este subpaquete almacenara los codigos de las sentencias necesarias
         *           para procesar la transaccion -->
         * 		<subargs>
         * 			<arg>INS000</arg>
         * 			<arg>UPD000</arg>
         * 			<arg>DEL000</arg>
         * 		</subargs>
         * </container>
         */

        /*
         * Se obtiene el argumento de funcion, en caso de que no se pueda, se va por la excepcion
         * por la cual se retorna un error indicando que los argumentos de la clase son invalidos
         */
        try {
	        argValue = ((Element)doc.getRootElement().getChildren("arg").iterator().next()).getValue();
	        Element subargs = (Element)(doc.getRootElement().getChildren("subargs").iterator().next());
	        Iterator i = subargs.getChildren().iterator();
	        Iterator j = pack.getChildren().iterator();
	        boolean detalle = false;
            Element subpack = null;
	        
	        /*
	         * Si se va a crear una nueva cuenta contable, entonces ...
	         */
	        if (argValue.equals("NEW")) {
	            for (int k=0;i.hasNext();k++) {
	                Element arg = (Element)i.next();
	                
	                /*
	                 * Se adiciona la informacion a la tabla cuentas
	                 */
	                if (k==0) {
	                    subpack = (Element)j.next();
	                    getTransaction(arg.getValue(),subpack);
	                }
	                /*
	                 * Se adiciona la informacion a la tabla perfiles
	                 */
	                else if (k==1) {
	                    subpack = (Element)j.next();
	                    if (subpack.getContentSize()>0) {
	                        getTransaction(arg.getValue(),subpack);
	                        detalle=true;
	                    }
	                }
	                /*
	                 * Se almacena el codigo de la sentencia que consulta los padres
	                 * de las cuentas
	                 */
	                else if (k==2) {
	                    SQLpadre = arg.getValue();
	                }
	                /*
	                 *  Se almacena el codigo de la sentencia que retorna el padre de
	                 *  la cuenta actual
	                 */
	                
	                else if (k==3) {
	                    SQLhijo = arg.getValue();
	                }
		            /*
		             * Se activa las cuentas padres, en el caso de que
		             * la cuenta creada hubiece sido una cuenta de detalle
		             */
	                else if (k==4) {
	                    if (detalle) {
	                        enableAccount(arg.getValue(),SQLpadre,SQLhijo);
	                    }
	                }
	            }
	            /*
	             * Si la cuenta es de detalle se proceda a adicionar la informacion 
	             * a los caches
	             */
	            if (detalle) {
	                LinkingCache.loadPerfilCta(SocketServer.getBd(sock),"SEL0319",new String[]{LNGtransaccion.getKey(0)});
	            }
	        }
	        /*
	         * Si se va a editar una cuenta contable entonces ...
	         */
	        else if (argValue.equals("EDIT")) { 
	            for (int k=0;i.hasNext();k++) {
	                Element arg = (Element)i.next();
	                /*
	                 * Se captura el codigo de la sentencia que consulta la existencia de hijos
	                 */
	                if (k==0) {
	                    SQLpadre = arg.getValue();
	                }
	                /*
	                 * Se actualiza la cuenta mayor
	                 */
	                else if (k==1) { 
	                    subpack = (Element)j.next();
	                    String key =((Element)subpack.getChildren().get(3)).getValue();
	                    String tipo = ((Element)subpack.getChildren().get(2)).getValue();
	                    boolean bchildren = haveChildren(SQLpadre,key);
		                if (tipo.equals("1") || (!bchildren && tipo.equals("0"))) {
		                    getTransaction(arg.getValue(),subpack);
		                }
		                else {
		                    RunTransaction.errorMessage(sock,
		    	                   	 id_transaction,
		    	                   	 Language.getWord("ERR_HAVE_CHILDREN_EDIT"));
		    	               		 LNGtransaccion.rollback();
		    	               		 break;
		                }
		                    
	                }
	                /*
	                 * Dependiendo de los datos de actualizacion, si la cuenta tiene
	                 * como parametro ser Mayor, entonces se verifica si tiene hijos
	                 * si es asi, entonces termina la actualizacion, si no entonces 
	                 * es porque la cuenta pudo haber sido de detalle y paso a mayor
	                 * por tanto se la desactiva y se procede a desactivar sus padres;
	                 * si por el contrario es una cuenta de detalle se procede a activar
	                 * a sus padres
	                 */
	                
	                /*
	                 * Se almacena el codigo de la sentencia que consulta los padres
	                 * de las cuentas
	                 */
	                else if (k==2) {
	                    SQLdisableFather = arg.getValue();
	                }
	                else if (k==3) {
	                    SQLenableFather = arg.getValue();
	                }
	                /*
	                 *  Se almacena el codigo de la sentencia que retorna el padre de
	                 *  la cuenta actual
	                 */
	                
	                else if (k==4) {
	                    SQLhijo = arg.getValue();
	                }

	                /*
	                 *  Se almacena el codigo de la sentencia para de desactivacion de cuentas
	                 */
	                else if (k==5) {
	                    SQLenableAccount = arg.getValue();
	                }
	                
	                /*
	                 * Se valida el tipo y se procede a activar o desactivar las cuenta
	                 */
	                
	                else if(k==6) {
		                /*
		                 * Se almacena el codigo de la sentencia para activacion de cuentas
		                 * 
		                 */
	                    SQLdisableAccount = arg.getValue();
	                    String tipo = LNGtransaccion.getArg(3);
	                    /*
	                     * Si tipo es mayor entonces
	                     */
	                    if (tipo.equals("1")) {
			                disableAccount(LNGtransaccion.getKey(0),SQLdisableAccount,SQLdisableFather);
	                    }
	                    /*
	                     * Si no es porque tipo es detalle, entonces ...
	                     */
	                    else {
	                        enableAccount(SQLenableAccount,SQLenableFather,SQLhijo);
		                    LinkingCache.removePerfilCta(SocketServer.getBd(sock),new String[]{LNGtransaccion.getKey(0)});
	                        detalle = true;
	                    }
	                }
	                
	                /*
	                 * Se elimina el registro de 
	                 * su perfil anterior y ... 
	                 */
	                
	                else if(k==7) {
	                    getTransaction(arg.getValue(),new String[]{LNGtransaccion.getKey(0)});
	                    
	                }
	                
	                /*
	                 * se procede a crear uno nuevo perfil ..
	                 */
	                
	                else if(k==8) {
	                    subpack = (Element)j.next();
	                    if (subpack.getContentSize()>0) {
	                        getTransaction(arg.getValue(),subpack);
	                    }
	                }
	            }
	            if (detalle) {
	                LinkingCache.loadPerfilCta(SocketServer.getBd(sock),"SEL0319",new String[]{LNGtransaccion.getKey(0)});
	            }
	        }

	        /*
	         * Si se eliminar una nueva cuenta contable, entonces ...
	         */
	        else if (argValue.equals("DELETE")) { 
	            for (int k=0;i.hasNext();k++) {
	                Element arg = (Element)i.next();
	                
	                /*
	                 * Se selecciona la consulta que retornara si la tabla
	                 * tiene hijos o no y se procesa
	                 */
	                if (k==0) {
	                    subpack = (Element)j.next();
	                    SQLpadre = arg.getValue();
		                String key = subpack.getChild("field").getValue();
		                if (haveChildren(SQLpadre,key)) {
	    	                RunTransaction.errorMessage(sock,
	    	                   	 id_transaction,
	    	                   	 Language.getWord("ERR_HAVE_CHILDREN"));
	    	               		 LNGtransaccion.rollback();
	    	               		 break;
	    	            }
	                    LinkingCache.removePerfilCta(SocketServer.getBd(sock),new String[]{key});
                    }
	                /*
	                 * Se procede a eliminar la cuenta de la tabla perfiles
	                 */
	                else if (k==1) {
	                    getTransaction(arg.getValue(),subpack);
	                }
	                /*
	                 * Se procede a eliminar la cuenta de la tabla cuentas
	                 */
	                else if (k==2) {
	                    try {
	                        getTransaction(arg.getValue(),subpack);
	                    }
	                    catch (SQLException SQLEe) {
	                        RunTransaction.errorMessage(sock,
		    	                   	 id_transaction,
		    	                   	 Language.getWord("ERR_HAVE_DATA"));
		    	               		 LNGtransaccion.rollback();
		    	               		 break;
	                    }
	                }
	                /*
	                 * Se adiciona el codigo de la sentencia sql de herencia
	                 */
	                else if(k==3) {
	                    SQLpadre = arg.getValue();
	                }
	                
	                /*
	                 * Se desactivan las cuentas mayores en el caso que la
	                 * cuenta eliminada hubiera sido su unico hijo
	                 */
	                else if (k==4) {
		                disableAccount(LNGtransaccion.getArg(0),arg.getValue(),SQLpadre);
	                    
	                }
	            }	            
	        }
            
	        /*
	         * Si la transaccion se procesa con exito, entonces se hace un commit y se envia el
	         * paquete <SUCCESS> :-D
	         */
	        LNGtransaccion.commit();
	        RunTransaction.successMessage(sock,
							          	  id_transaction,
							          	  Language.getWord("TRANSACTION_OK"));

        }
        catch(NullPointerException NPEe) {
            RunTransaction.errorMessage(sock,
                	 id_transaction,
                	 Language.getWord("ERR_INVALID_ARGS"));
            		 LNGtransaccion.rollback();

        }
        catch (SQLNotFoundException SQLNFEe) {
            RunTransaction.errorMessage(sock,
					                 	 id_transaction,
					                 	 SQLNFEe.getMessage());
            LNGtransaccion.rollback();
        }
        catch (SQLBadArgumentsException SQLBAEe) {
            RunTransaction.errorMessage(sock,
                	 id_transaction,
                	 SQLBAEe.getMessage());
            LNGtransaccion.rollback();
        }
        catch (SQLException SQLe) {
            SQLe.printStackTrace();
            RunTransaction.errorMessage(sock,
               	 id_transaction,
               	 SQLe.getMessage());
            LNGtransaccion.rollback();
        }
        
        /*
         * Una vez terminado el procesamiento de la transaccion, sin importar si su resultado fue
         * exitoso, se cambia a AutoCommit el modo de procesamiento de instrucciones sql en el ST
         */
        
        LNGtransaccion.setAutoCommit(true);
    }

    /**
     * Metodo encargado de verificar si la cuenta tiene hijos
     * @param SQLpadre contiene el codigo de la instruccion sql
     * @param key contiene el argumento de la instruccion
     * @return retorna true en caso de que la instruccion tenga hijos y falso si no los tiene
     * @throws SQLNotFoundException
     * @throws SQLBadArgumentsException
     * @throws SQLException
     */
    
	private boolean haveChildren(String SQLpadre,String key) 
	throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
        String bd = SocketServer.getBd(sock);
        RunQuery RQpadre = new RunQuery(bd,SQLpadre,
     		   new String[]{key});
		ResultSet RSpadre = RQpadre.ejecutarSELECT();
		RSpadre.next();
		String padre = RSpadre.getString(1);
		if (padre.equals("0")) 
		    return false;
		else 
		    return true;
	}
	
	/**
	 * Metodo encargado de activar las cuentas padres 
	 * @param arg contiene los argumentos de la sentencia
	 * @param SQLpadre contiene el codigo de la sentenica que consulta los padres
	 * @param SQLhijo contene el codigo de la sentencia que consulta el registro actual
	 * @throws SQLNotFoundException
	 * @throws SQLBadArgumentsException
	 * @throws SQLException
	 */
	
    private void enableAccount(String arg,String SQLpadre,String SQLhijo) 
    throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
        String bd = SocketServer.getBd(sock);
        RunQuery RQpadre = new RunQuery(bd,SQLhijo,
                		   new String[]{LNGtransaccion.getKey(0)});
        ResultSet RSpadre = RQpadre.ejecutarSELECT();
        RSpadre.next();
        String padre = RSpadre.getString(1);
    	String argUpdate[] = new String[2];
        argUpdate[0]="true";
        while (!padre.equals("0")) {
            argUpdate[1]=padre;
            getTransaction(arg,argUpdate);
            RQpadre = new RunQuery(bd,SQLpadre,
                         new String[]{padre});
            
            RSpadre = RQpadre.ejecutarSELECT();
            RSpadre.next();
            padre = RSpadre.getString(1);			                
        }
    }
    
    /**
     * Metodo encargado de desactivar las cuentas padres de la cuenta referenciada
     * @param padre contiene el codigo de la cuenta padre
     * @param arg contiene los argumentos de las sentencias
     * @param SQLpadre contiene el codigo de la sentencia
     * @throws SQLException 
     * @throws SQLNotFoundException
     * @throws SQLBadArgumentsException
     */
    
    private void disableAccount(String padre,String arg,String SQLpadre) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
        String bd = SocketServer.getBd(sock);
        String argUpdate[] = new String[2];
        argUpdate[0]="false";
        RunQuery RQpadre;
        String descendencia;
        RQpadre = new RunQuery(bd,SQLpadre,
     		   new String[]{padre});
         ResultSet RSpadre = RQpadre.ejecutarSELECT();
         RSpadre.next();
         descendencia = RSpadre.getString(1);
        while (descendencia.equals("0")){
            argUpdate[1]=padre;
            getTransaction(arg,argUpdate);
            
           /* Validacion para:
            * 2 caracteres:	Grupo
            * 4 caracteres:	Cuenta
            * 6 caracteres:	SubCuenta
            * 8 caracteres:	Auxiliar
            * 10 caracteres:	subAuxiliar
            */
            if (padre.length()>2)
                padre=padre.substring(0,padre.length()-2);
           /*
            *  Validacion para:
            *  1 caracter: Clase
            */ 
            else if(padre.length()>1)
                padre=padre.substring(0,padre.length()-1);
            
            /*
             * Si sobrepasa la clase entonces sale del ciclo
             */
            else if (padre.length()>0)
                break;
            
            RQpadre = new RunQuery(bd,SQLpadre,
            		   new String[]{padre});
            RSpadre = RQpadre.ejecutarSELECT();
            RSpadre.next();
            descendencia = RSpadre.getString(1);
        } 
    }
    
    /**
     * Metodo encargado de generar una transaccion simple
     * @param sql contiene la sentencia sql a procesar
     * @param pack contiene los argumentos de la sentencia
     */
    
    private void getTransaction(String sql,Element pack) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
        LNGtransaccion.setArgs(pack,id_transaction);
        LNGtransaccion.generar(sql);
    }

    /**
     * Metodo encargado de generar una transaccion simple
     * @param sql contiene la sentencia sql a procesar
     * @param argUpdate contiene los argumentos de la sentencia en un arreglo de Strings
     */
    
    private void getTransaction(String sql,String[] argUpdate) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
        LNGtransaccion.setArgs(argUpdate,id_transaction);
        LNGtransaccion.generar(sql);
    }
}
