package com.kazak.smi.server.comunications;

import org.jdom.Document;
import org.jdom.Element;

/**
 * XMLError.java Creado el 14-jul-2004
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
 * Esta clase se encarga de generar la estructura ERROR de tal forma que
 * pueda ser enviada directamente al cliente que corresponda. 
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class XMLError extends Document {

    /**
	 * 
	 */
	private static final long serialVersionUID = -701210422232334783L;

	/**
     * Este metodo genera el documento listo para ser enviado al
     * cliente, con su estructura XML correspondiente.
     * @param code Codigo del Error
     * @param empresa Hace referencia a la base de datos de la empresa que se esta manejando
     * @param message Mensaje de error
     * @return Retorna el Doument ErrorXML 
     */
	public Document returnErrorMessage(int code, String message) {
		return returnErrorMessage(code,null,message);
	}

	public Document returnErrorMessage(int code,String returnID,String message) {
		this.setRootElement(new Element("ERROR"));
		Element	errorCode = new Element("errorCode");
		Element id        = new Element("id");
		Element errorMsg  = new Element("errorMsg");

		errorCode.setText(String.valueOf(code));
		errorMsg.setText(message);
		this.getRootElement().addContent(errorCode);
		
		if (returnID!=null) {
		    id.setText(returnID);
			this.getRootElement().addContent(id);
		}
		
		this.getRootElement().addContent(errorMsg);
		
		return this;
	}
}