package server.businessrules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

import common.comunications.SocketWriter;
import common.misc.language.Language;
import common.misc.log.LogAdmin;
import server.comunications.ErrorXML;
import server.comunications.SocketServer;
import server.comunications.SuccessXML;
import server.database.sql.CacheEnlace;
import server.database.sql.ClassLogicDriver;
import server.database.sql.InstruccionesSQL;
import server.misc.ServerConst;

import org.jdom.Document;
import org.jdom.Element;

/**
 * RunTransaction.java Creado el 18-ene-2005
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
 * Esta clase se encarga de llamar las clases y metodos encargados de ejecutar
 * la logica de negocios que procesara la informaci�n del paquete transaction
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class RunTransaction {

    private String transaction_code;
    private String id_transaction;
    
    private Class[] type_args_constructor;
    private Class[] type_args_method;
    private Object[] args_constructor;
    private Object[] args_method;
    private Element pack = null;
    
    private SocketChannel sock;
    
    public RunTransaction(SocketChannel sock,Document transaction) {

        this.sock=sock;
        Element elm = transaction.getRootElement();
        List listaRaiz = elm.getChildren();
        Iterator i = listaRaiz.iterator();
        String password="";
        pack = new Element("source");
        /*
         * Se separa el codigo de la transaccion de los datos
         */
        
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String nombre = e.getName();
            if (nombre.equals("driver"))
                transaction_code=e.getValue();
            else if (nombre.equals("password"))
                password = e.getValue();
            else if (nombre.equals("id"))
                id_transaction=e.getValue();
            else if (nombre.equals("package"))
                pack.addContent((Element)e.clone());
        }
        /*
         * Con esta informaci�n se verifica si el usuario tiene
         * permisos para efectuar la transaccion
         */
        
        String bd = SocketServer.getBd(sock);
        String login = SocketServer.getLoging(sock);
        
        
        if (InstruccionesSQL.permisoTransaccion(bd,login,transaction_code,password)){
        		System.out.println("bd: "+bd+" transaction_code: "+transaction_code+" Driver: "+CacheEnlace.getDriver(bd,transaction_code));
	        callDriver(CacheEnlace.getDriver(bd,transaction_code),
	                   id_transaction);
        }
        else
            errorMessage(sock,
	                  	 id_transaction,
	                  	 Language.getWord("TRANSACTION_ACCESS_DENIED"));
    }

    private void callDriver(ClassLogicDriver CLDdriver,
            			    String id_transaction) {
        try {
            if (CLDdriver.getDriver() != null) {
                Class cls = Class.forName(CLDdriver.getDriver());
                if (CLDdriver.getArg_driver()!=null) {
                    type_args_constructor = new Class[] { SocketChannel.class,
                            							  Document.class,
                            							  Element.class, 
                            							  String.class};
                    
                    args_constructor = new Object[] { sock,
                            						  CLDdriver.getArg_driver(),
                            						  pack,
                            						  id_transaction};
                } else {
                    type_args_constructor = new Class[] { SocketChannel.class,
                            							  Element.class,
                            							  String.class};
                    args_constructor = new Object[] { sock,
                            						  pack,
                            						  id_transaction};
                }

                Constructor cons = cls.getConstructor(type_args_constructor);
                Object obj = cons.newInstance(args_constructor);

                if (CLDdriver.getMethod() != null) {
                    if (CLDdriver.getArg_method()!=null) {
                        type_args_method = new Class[] { Document.class};
                        args_method = new Object[] { CLDdriver.getArg_method() };
                    } 
                    Method meth = cls.getMethod(CLDdriver.getMethod(), type_args_method);
                    meth.invoke(obj, args_method);
                } 
            }
            else {
                errorMessage(sock,
		                   	 id_transaction,
		                	 Language.getWord("ERR_MODULE_LG")+"\n"+
		                	 Language.getWord("ERR_NOT_DRIVER"));
            }
        }

        /*
         * Se define las excepciones del caso y rogemos a dios que ninguna de
         * estas ocurra a menos de que el documento este mal construido. El
         * codigo es bueno, no tienen porque..... ;-)
         */

        catch (ClassNotFoundException CNFEe) {
            CNFEe.printStackTrace();
            errorMessage(sock,
		               	 id_transaction,
		            	 Language.getWord("ERR_MODULE_LG")+"\n"+
		            	 CNFEe.getMessage());
        }
        catch (NoSuchMethodException NSMEe) {
            NSMEe.printStackTrace();
            errorMessage(sock,
		               	 id_transaction,
		            	 Language.getWord("ERR_MODULE_LG")+"\n"+
		            	 NSMEe.getMessage());
        }
        catch (InstantiationException IEe) {
            IEe.printStackTrace();
            errorMessage(sock,
		               	 id_transaction,
		            	 Language.getWord("ERR_MODULE_LG")+"\n"+
		            	 IEe.getMessage());
        }
        catch (IllegalAccessException IAEe) {
            IAEe.printStackTrace();
            errorMessage(sock,
		               	 id_transaction,
		            	 Language.getWord("ERR_MODULE_LG")+"\n"+
		            	 IAEe.getMessage());
        }
        catch (InvocationTargetException ITEe) {
            ITEe.printStackTrace();
            errorMessage(sock,
		               	 id_transaction,
		            	 Language.getWord("ERR_MODULE_LG")+"\n"+
		            	 ITEe.getMessage());
        }
        catch(NullPointerException NPEe) {
            NPEe.printStackTrace();
            errorMessage(sock,
                    	 id_transaction,
                    	 Language.getWord("ERR_MODULE_LG")+"\n"+
                    	 Language.getWord("ERR_FOUND_DRIVER_LG"));
        }
    }
    
    public static void errorMessage(SocketChannel sock,
            					     String id_transaction,
            					     String message) {
        ErrorXML error = new ErrorXML();
        LogAdmin.setMessage(message, ServerConst.ERROR);
        SocketWriter.writing(sock, 
                		    error.returnError(ServerConst.ERROR,
                		            	      SocketServer.getBd(sock),
                		            	      id_transaction,
                		            	      message));

    }
    
    public static void successMessage(SocketChannel sock,
									  String id_transaction,
									  String message) {
		SuccessXML success = new SuccessXML();
		LogAdmin.setMessage(message, ServerConst.MESSAGE);
		SocketWriter.writing(sock, 
			    		    success.returnSuccess(id_transaction,message));
    }

}
