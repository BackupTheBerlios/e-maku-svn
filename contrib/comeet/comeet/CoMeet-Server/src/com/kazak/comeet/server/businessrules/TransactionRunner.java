package com.kazak.comeet.server.businessrules;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.lib.misc.Language;
import com.kazak.comeet.server.comunications.XMLError;
import com.kazak.comeet.server.comunications.SocketWriter;
import com.kazak.comeet.server.comunications.XMLAuditor;
import com.kazak.comeet.server.control.TransactionsCache;
import com.kazak.comeet.server.misc.LogWriter;
import com.kazak.comeet.server.misc.ServerConstants;

/**
 * TransactionRunner.java Creado el 18-ene-2005
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

    private String transactionCode;
    private String transactionID;
    
    private Class[] ArgsTypeConstructor;
    private Object[] argsConstructor;
    private Element pack = null;
    
    private SocketChannel sock;
    
    public TransactionRunner(SocketChannel sock,Document transaction) {

        this.sock = sock;
        Element element = transaction.getRootElement();
        List rootList = element.getChildren();
        Iterator iterator = rootList.iterator();
        pack = new Element("source");
        /*
         * Se separa el codigo de la transaccion de los datos
         */
        
        while (iterator.hasNext()) {
            Element e = (Element) iterator.next();
            String name = e.getName();
            if (name.equals("driver"))
                transactionCode = e.getValue();
            else if (name.equals("id"))
                transactionID = e.getValue();
            else if (name.equals("package"))
                pack.addContent((Element)e.clone());
        }
        final TransactionsCache.Transaction operation;
        operation = TransactionsCache.getTransaction(transactionCode);
        Thread t = new Thread() {
        	public void run() {
        		callDriver(operation,transactionID);
        	}
        };
        t.start();
    }

    private void callDriver(TransactionsCache.Transaction transaction,
            			    String transactionID) {
        try {
            if (transaction.getDriver() != null) {
                Class<?> cls = Class.forName(transaction.getDriver());
                ArgsTypeConstructor = new Class[] {
                		SocketChannel.class,
                		Element.class,
                		Element.class,
                		String.class};
                    
                 argsConstructor = new Object[] {
                		 sock,
                		 transaction.getArgs(),
                		 pack,
                		 transactionID};
                Constructor constructor = cls.getConstructor(ArgsTypeConstructor);
                constructor.newInstance(argsConstructor);
            }
            else {
                notifyErrorMessage(sock,
		                   	 transactionID,
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
            notifyErrorMessage(sock,
		               	 transactionID,
		            	 Language.getWord("ERR_MODULE_LG")+"\n"+
		            	 CNFEe.getMessage());
        }
        catch (NoSuchMethodException NSMEe) {
            NSMEe.printStackTrace();
            notifyErrorMessage(sock,
		               	 transactionID,
		            	 Language.getWord("ERR_MODULE_LG")+"\n"+
		            	 NSMEe.getMessage());
        }
        catch (InstantiationException IEe) {
            IEe.printStackTrace();
            notifyErrorMessage(sock,
		               	 transactionID,
		            	 Language.getWord("ERR_MODULE_LG")+"\n"+
		            	 IEe.getMessage());
        }
        catch (IllegalAccessException IAEe) {
            IAEe.printStackTrace();
            notifyErrorMessage(sock,
		               	 transactionID,
		            	 Language.getWord("ERR_MODULE_LG")+"\n"+
		            	 IAEe.getMessage());
        }
        catch (InvocationTargetException ITEe) {
            ITEe.printStackTrace();
            notifyErrorMessage(sock,
		               	 transactionID,
		            	 Language.getWord("ERR_MODULE_LG")+"\n"+
		            	 ITEe.getMessage());
        }
        catch(NullPointerException NPEe) {
            NPEe.printStackTrace();
            notifyErrorMessage(sock,
                    	 transactionID,
                    	 Language.getWord("ERR_MODULE_LG")+"\n"+
                    	 Language.getWord("ERR_FOUND_DRIVER_LG"));
        }
    }
    
    public static void notifyErrorMessage(SocketChannel sock,
            					     String transactionID,
            					     String message) {
        XMLError error = new XMLError();
        try {
        	Document doc = error.returnErrorMessage(
        			ServerConstants.ERROR,
        			transactionID,
        			message);
        	SocketWriter.write(sock,doc);
        } catch (IOException e) {
			LogWriter.write("ERROR: Falla de entrada/salida");
			LogWriter.write("Causa: " + e.getMessage());
			e.printStackTrace();
		}

    }
    
    public static void notifyMessageReception(SocketChannel sock,
									  String transactionID,
									  String message, String type) {
		XMLAuditor auditor = new XMLAuditor();
		LogWriter.write("INFO: Operacion realizada exitosamente ["+type+"]");
		try {
			SocketWriter.write(sock,auditor.returnSuccessMessage(transactionID,message));
		} catch (IOException e) {
			LogWriter.write("ERROR: Falla de entrada/salida");
			LogWriter.write("Causa: " + e.getMessage());
			e.printStackTrace();
		} 
	    
    }
    
    public static void notifyMessageReception(
    							SocketChannel sock,
    							String transactionID,
    							String message,
    							Element element) {
		XMLAuditor auditor = new XMLAuditor();
		LogWriter.write(message);
		try {
			SocketWriter.write(sock,auditor.returnSuccessMessage(transactionID,message,element));
		} catch (IOException e) {
			LogWriter.write("ERROR: Falla de entrada/salida");
			LogWriter.write("Causa: " + e.getMessage());
			e.printStackTrace();
		}
    }
}
