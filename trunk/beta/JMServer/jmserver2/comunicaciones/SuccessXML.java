package jmserver2.comunicaciones;

import org.jdom.Document;
import org.jdom.Element;

/**
 * SuccessXML.java Creado el 25-ene-2005
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
 * Esta clase es la encargada de generar paquetes Success
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class SuccessXML extends Document {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5465540432378721520L;

	public Document returnSuccess(String id_return,String Mensaje) {
        this.setRootElement(new Element("SUCCESS"));
		Element idreturn = new Element("id");
        Element msg = new Element("successMessage");
	    idreturn.setText(id_return);
        msg.setText(Mensaje);
        this.getRootElement().addContent(idreturn);
        this.getRootElement().addContent(msg);
        return this;
    }

}