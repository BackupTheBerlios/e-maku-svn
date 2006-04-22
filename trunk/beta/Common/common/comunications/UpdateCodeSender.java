package common.comunications;

import org.jdom.Document;
import org.jdom.Element;

/**
 * UpdateCodeSender.java Creado el 28-jun-2005
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
 * Esta clase se encarga de generar un paquete UPDATECODE, el cual solicita
 * al servidor de transacciones el codigo actual de un documento especifico
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class UpdateCodeSender {

    public static Document getPackage(String idDocument) {
        Document doc = new Document();
        doc.setRootElement(new Element("UPDATECODE"));
        doc.getRootElement().addContent(new Element("idDocument").setText(idDocument));
        return doc;

    }

}
