package jmserver2.control;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.jdom.Document;
import org.jdom.Element;

/**
 * SendDATE.java Creado el 28-jun-2005
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
 * Esta clase se encarga de retornar un paquete DATE
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class SendDATE {

    /**
     * 
     */
    public static Document getPackage() {
        
        GregorianCalendar fecha = new GregorianCalendar();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Document doc = new Document();
        doc.setRootElement(new Element("DATE"));
        doc.getRootElement().addContent(new Element("systemDate").setText(formato.format(fecha.getTime())));
        return doc;

    }

}
