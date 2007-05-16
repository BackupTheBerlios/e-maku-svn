package com.kazak.smi.client.network;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;

import org.jdom.Document;
import org.jdom.Element;

/**
 * QuerySender.java Creado el 30-ago-2004
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
 * Esta clase se encarga de solicitar al ST de forma transparente para clase
 * que lo solicite, una consulta o una transaccion.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

public class QuerySender {

    private static Hashtable <String,Document> poolTransactionsHash = new Hashtable<String,Document>();
    private static long id = 0;
     
    /**
     * Este metodo es invocado en caso de que la consulta solicitada no se encuentre en
     * cache.
     * @param key identifica si la solicituid de esta clase es de JMClient o de JMAdmin
     * @param doc envia la peticion de una transaccion o una solicitud de un query
     * @return returna la transaccion o query solicitado.
     * @throws QuerySenderException
     */
    public static Document getResultSetFromST(Document doc) throws QuerySenderException {
    	
        String id = "Q"+getId();
        doc.getRootElement().addContent(new Element("id").setText(id));
        SocketChannel socket = SocketHandler.getSock();
        try {
        	SocketWriter.writing(socket,doc);
        } catch (IOException e) {
			System.out.println("ERROR: Error de entrada y salida");
			System.out.println("Causa: " + e.getMessage());
			e.printStackTrace();
		}    
        int i=0;
        while (!poolTransactionsHash.containsKey(id)) {
            try {
                Thread.sleep(100);
                i++;
                if (i>300) {
                    throw new QuerySenderException();
                }
            }
            catch(InterruptedException e) {
                throw new QuerySenderException();
            }
        }
    	
        Document result = (Document)poolTransactionsHash.get(id);
        poolTransactionsHash.remove(id);
        return result;
    }

    public static Document getResultSetFromST(String code, String[] argsArray)
    throws QuerySenderException {
        Document doc = new Document();
        doc.setRootElement(new Element("QUERY"));
        doc.getRootElement().addContent(new Element("sql").setText(code));
        
        if( argsArray != null ) {
            Element element = new Element("params");
            for (int i=0; i< argsArray.length ; i++) {
                element.addContent(new Element("arg").setText(argsArray[i]));
            }
            doc.getRootElement().addContent(element);
        }
        return getResultSetFromST(doc);
    }
    
    /**
     * Este metodo adiciona retorno paquetes answer, success o error provenientes de la
     * solicitud de una consulta o una transaccion
     * @param id identificador de solicitud de consulta.
     * @param doc paquete answer,success o error retornado por el ST.
     */
    public static void putResultOnPool(String id, Document doc) {
        poolTransactionsHash.put(id,doc);
    }
    
    public static synchronized String getId() {
        return String.valueOf(++id);
    }
}