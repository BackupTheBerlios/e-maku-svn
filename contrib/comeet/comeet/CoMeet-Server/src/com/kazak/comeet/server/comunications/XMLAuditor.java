package com.kazak.comeet.server.comunications;

import org.jdom.Document;
import org.jdom.Element;

/**
 * XMLAuditor.java Creado el 25-ene-2005
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
 * Esta clase es la encargada de generar paquetes Success
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class XMLAuditor extends Document {

	private static final long serialVersionUID = -5465540432378721520L;

	public Document returnSuccessMessage(String id,String message) {
        this.setRootElement(new Element("SUCCESS"));
		Element returnID = new Element("id");
        Element msg = new Element("successMessage");
	    returnID.setText(id);
        msg.setText(message);
        this.getRootElement().addContent(returnID);
        this.getRootElement().addContent(msg);

        return this;
    }

	public Document returnSuccessMessage(String id,String message,Element element) {
        this.setRootElement(new Element("SUCCESS"));
		Element returnID = new Element("id");
		Element msg = new Element("successMessage");
	    returnID.setText(id);
	    msg.setText(message);
        this.getRootElement().addContent(returnID);
        this.getRootElement().addContent(msg);
        this.getRootElement().addContent(element);
        
        return this;
    }
}