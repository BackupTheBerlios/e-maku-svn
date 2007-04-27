package com.kazak.smi.server.businessrules;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.lib.misc.Language;
import com.kazak.smi.server.comunications.ErrorXML;
import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.comunications.SuccessXML;
import com.kazak.smi.server.control.TransactionsCache;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.ServerConst;

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
public class TransactionRunner {

    private String transaction_code;
    private String id_transaction;
    
    private Class[] type_args_constructor;
    private Object[] args_constructor;
    private Element pack = null;
    
    private SocketChannel sock;
    
    public TransactionRunner(SocketChannel sock,Document transaction) {

        this.sock=sock;
        Element elm = transaction.getRootElement();
        List listaRaiz = elm.getChildren();
        Iterator i = listaRaiz.iterator();
        pack = new Element("source");
        /*
         * Se separa el codigo de la transaccion de los datos
         */
        
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String nombre = e.getName();
            if (nombre.equals("driver"))
                transaction_code=e.getValue();
            else if (nombre.equals("id"))
                id_transaction=e.getValue();
            else if (nombre.equals("package"))
                pack.addContent((Element)e.clone());
        }
        final TransactionsCache.Transaction tr;
        tr = TransactionsCache.getTransaction(transaction_code);
        Thread t = new Thread() {
        	public void run() {
        		callDriver(tr,id_transaction);
        	}
        };
        t.start();
    }

    private void callDriver(TransactionsCache.Transaction tr,
            			    String id_transaction) {
        try {
            if (tr.getDriver() != null) {
                Class<?> cls = Class.forName(tr.getDriver());
                type_args_constructor = new Class[] {
                		SocketChannel.class,
                		Element.class,
                		Element.class,
                		String.class};
                    
                 args_constructor = new Object[] {
                		 sock,
                		 tr.getArgs(),
                		 pack,
                		 id_transaction};
                Constructor cons = cls.getConstructor(type_args_constructor);
                cons.newInstance(args_constructor);
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
        LogWriter.write(message);
        try {
        	Document doc = error.returnError(
        			ServerConst.ERROR,
        			id_transaction,
        			message);
        	SocketWriter.writing(sock,doc);
        } catch (IOException e) {
			LogWriter.write("Error de entrada y salida");
			LogWriter.write("mensaje: " + e.getMessage());
			e.printStackTrace();
		}

    }
    
    public static void successMessage(SocketChannel sock,
									  String id_transaction,
									  String message) {
		SuccessXML success = new SuccessXML();
		LogWriter.write(message);
		try {
			SocketWriter.writing(sock,success.returnSuccess(id_transaction,message));
		} catch (IOException e) {
			LogWriter.write("Error de entrada y salida");
			LogWriter.write("mensaje: " + e.getMessage());
			e.printStackTrace();
		} 
	    
    }
    
    public static void successMessage(
    							SocketChannel sock,
    							String id_transaction,
    							String message,
    							Element element) {
		SuccessXML success = new SuccessXML();
		LogWriter.write(message);
		try {
			SocketWriter.writing(sock,success.returnSuccess(id_transaction,message,element));
		} catch (IOException e) {
			LogWriter.write("Error de entrada y salida");
			LogWriter.write("mensaje: " + e.getMessage());
			e.printStackTrace();
		}
    }
}
