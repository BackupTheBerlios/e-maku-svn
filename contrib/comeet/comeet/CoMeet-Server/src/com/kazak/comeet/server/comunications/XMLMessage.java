package com.kazak.comeet.server.comunications;

import org.jdom.Document;
import org.jdom.Element;

/**
 * XMLMessage.java Creado el 23-jul-2004
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase es una estructura de datos de tipo XML para mensajes
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class XMLMessage extends Document {
    
	private static final long serialVersionUID = -2088374124969454747L;

	/**
     * Este metodo genera el documento listo para ser enviado al cliente, con su
     * estructura XML correspondiente.
     * 
     * @param message
     *            Mensaje de error
     */
    public XMLMessage(String message) {
        this.setRootElement(new Element("MESSAGE"));
        Element msg = new Element("msg");
        msg.setText(message);
        this.getRootElement().addContent(msg);
    }

}