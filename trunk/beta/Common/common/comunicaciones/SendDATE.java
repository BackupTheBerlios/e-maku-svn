package common.comunicaciones;

import org.jdom.Document;
import org.jdom.Element;

/**
 * SendDATE.java Creado el 27-jun-2005
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
 * Esta clase se encarga de generar un paquete DATE, encargado de solicitar
 * la fecha al servidor de Transacciones
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian_david@universia.net.co'>Cristian David Cepeda</A>
 */
public class SendDATE {

    public static Document getPackage() {
        Document doc = new Document();
        doc.setRootElement(new Element("DATE"));
        return doc;

    }

}
