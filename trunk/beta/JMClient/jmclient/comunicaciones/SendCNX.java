package jmclient.comunicaciones;

import org.jdom.Document;
import org.jdom.Element;

/**
 * SendCNX.java Creado el 29-jul-2004
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
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class SendCNX {
    
    /**
     *  Este metodo recibe los argumentos necesarios para crear el paquete CNX
     * 
     * @param bd contiene el nombre de la base de datos a la que el cliente se conectara
     * @param login contiene el login del cliente
     * @param password contiene el password del cliente
     * @return Retorna el paquete CNX en formato XML @see org.jdom.Document
     */
    
    public static Document getPackage(String bd, String login, char [] password ){
        
        String pass = new String(password);
        Document doc = new Document();
        doc.setRootElement(new Element("CNX"));
        
        doc.getRootElement().addContent(new Element("db").setText(bd));
        doc.getRootElement().addContent(new Element("login").setText(login));
        doc.getRootElement().addContent(new Element("password").setText(pass));
        return doc;
    }
    
}
