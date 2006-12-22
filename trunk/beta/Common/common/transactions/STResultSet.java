package common.transactions;

import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import common.comunications.SocketConnector;
import common.comunications.SocketWriter;

import org.jdom.Document;
import org.jdom.Element;

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
public class STResultSet {

    private static Hashtable <String,Document>HspoolTransactions = new Hashtable<String,Document>();
    private static long id = 0;

    public static Document getResultSetSTCache(Document doc) throws STException  {
        
        /*
         *  Se verifica que la consulta solicitada no exista en cache
         */
        String sql = doc.getRootElement().getChildText("sql");
        Document doc_cache = null;
         
        if (Cache.containsKey(sql)) {
            /*
             * Dado el caso de que exista se la retorna desde la cache de tablas
             */
            Element raiz = doc.getRootElement();
            List Lparametros = raiz.getChildren("param");
            Iterator i = Lparametros.iterator();
            String key_sql="";
            while (i.hasNext()) {
                Element datos = (Element) i.next();
                key_sql+=datos.getValue();
            }
            
            doc_cache = Cache.getAnswer(sql,key_sql);
            
            if (doc_cache!=null)
                return doc_cache;
            else
                return getResultSetST(doc);
            
            
        }
        else {
            return getResultSetST(doc);
        }
    }
     
    /**
     * Este metodo es invocado en caso de que la consulta solicitada no se encuentre en
     * cache.
     * @param key identifica si la solicituid de esta clase es de JMClient o de JMAdmin
     * @param doc envia la peticion de una transaccion o una solicitud de un query
     * @return returna la transaccion o query solicitado.
     * @throws STException
     */
    private static Document getResultSetST(Document doc) throws STException {
        String id = "Q"+getId();
        doc.getRootElement().addContent(new Element("id").setText(id));
        
        SocketChannel socket = SocketConnector.getSock();
        SocketWriter.writing(socket,doc);
        int i=0;
        while (!HspoolTransactions.containsKey(id)) {
            try {
            	//System.out.print(".");
                Thread.sleep(100);
                i++;
                if (i>300) {
                    throw new STException();
                }
            }
            catch(InterruptedException e) {
                doc = new Document();
                doc.setRootElement(new Element("ERROR"));
                SocketWriter.writing(socket,doc);
                throw new STException();
            }
        }
        Document retorno = (Document)HspoolTransactions.get(id);
        HspoolTransactions.remove(id);
        return retorno;
    }


    public static Document getResultSetST(String codigo)
    throws STException {
        return getResultSetST(codigo,null);
    }
    
    public static Document getResultSetST(String codigo, String [] args)
    throws STException {
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
        return getResultSetSTCache(doc);
    }
    /**
     * Este metodo adiciona retorno paquetes answer, success o error provenientes de la
     * solicitud de una consulta o una transaccion
     * @param id identificador de solicitud de consulta.
     * @param doc paquete answer,success o error retornado por el ST.
     */
    public static void putSpoolQuery(String id, Document doc) {
        HspoolTransactions.put(id,doc);
    }
    /*
    public static Document getResultSetST(String codigo,String pass, String [] args)
    throws STException {
		System.out.println("codigo solicitado getResultSetST2: "+codigo);
        Document doc = getResultSetST(codigo, args);
        doc.getRootElement().addContent(new Element("password").setText(pass));
        return getResultSetST(doc);
    }
    */
    public static synchronized String getId() {
        return String.valueOf(++id);
    }
}
