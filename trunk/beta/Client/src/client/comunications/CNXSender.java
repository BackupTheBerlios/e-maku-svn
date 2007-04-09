package client.comunications;

import org.jdom.Document;
import org.jdom.Element;

/**
 * SendCNX.java Creado el 29-jul-2004
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
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class CNXSender {
    
    /**
     *  Este metodo recibe los argumentos necesarios para crear el paquete CNX
     * 
     * @param bd contiene el nombre de la base de datos a la que el cliente se conectara
     * @param login contiene el login del cliente
     * @param password contiene el password del cliente
     * @return Retorna el paquete CNX en formato XML @see org.jdom.Document
     */
    
    public static Document getPackage(String bd, String login, String password ){
        
    	Element rootNode = new Element("CNX");
        Document doc = new Document(rootNode);
        
        rootNode.addContent(new Element("db").setText(bd));
        rootNode.addContent(new Element("login").setText(login));
        rootNode.addContent(new Element("password").setText(password));
        
        return doc;
    }
    
}
