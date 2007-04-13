package com.kazak.smi.admin.transactions;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;


/**
 * STResultSet.java Creado el 30-ago-2004
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

    private static Hashtable <String,Document> HspoolTransactions = new Hashtable<String,Document>();
    private static long id = 0;

     
    /**
     * Este metodo es invocado en caso de que la consulta solicitada no se encuentre en
     * cache.
     * @param key identifica si la solicituid de esta clase es de JMClient o de JMAdmin
     * @param doc envia la peticion de una transaccion o una solicitud de un query
     * @return returna la transaccion o query solicitado.
     * @throws QuerySenderException
     */
    public static Document getResultSetST(Document doc) throws QuerySenderException {
    	
        String id = "Q"+getId();
        doc.getRootElement().addContent(new Element("id").setText(id));
        SocketChannel socket = SocketHandler.getSock();
        try {        
        	SocketWriter.writing(socket,doc);
        } catch (IOException e) {
        	System.out.println("Error de entrada y salida");
        	System.out.println("mensaje: " + e.getMessage());
        	e.printStackTrace();
        }
        int i=0;
        while (!HspoolTransactions.containsKey(id)) {
            try {
                Thread.sleep(100);
                i++;
                if (i>20000) {
                    throw new QuerySenderException();
                }
            }
            catch(InterruptedException e) {
                throw new QuerySenderException();
            }
        }
    	
        Document result = (Document)HspoolTransactions.get(id);
        HspoolTransactions.remove(id);
        return result;
    }

    public static Document getResultSetST(String codigo, String [] args)
    throws QuerySenderException {
        Document doc = new Document();
        doc.setRootElement(new Element("QUERY"));
        doc.getRootElement().addContent(new Element("sql").setText(codigo));
        
        if( args != null ) {
            Element params = new Element("params");
            for (int i=0; i< args.length ; i++) {
                params.addContent(new Element("arg").setText(args[i]));
            }
            doc.getRootElement().addContent(params);
        }
        return getResultSetST(doc);
    }
    
    /**
     * Este metodo adiciona retorno paquetes answer, success o error provenientes de la
     * solicitud de una consulta o una transaccion
     * @param id identificador de solicitud de consulta.
     * @param doc paquete answer,success o error retornado por el ST.
     */
    public static synchronized void putSpoolQuery(String id, Document doc) {
        HspoolTransactions.put(id,doc);
    }
    
    public static synchronized String getId() {
        return String.valueOf(++id);
    }
}