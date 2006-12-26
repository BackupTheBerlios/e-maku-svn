package server.comunications;

import org.jdom.Document;
import org.jdom.Element;

/**
 * ErrorXML.java Creado el 14-jul-2004
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
public class ErrorXML extends Document {

    /**
	 * 
	 */
	private static final long serialVersionUID = -701210422232334783L;

	/**
     * Este metodo genera el documento listo para ser enviado al
     * cliente, con su estructura XML correspondiente.
     * @param Codigo Codigo del Error
     * @param empresa Hace referencia a la base de datos de la empresa que se esta manejando
     * @param Mensaje Mensaje de error
     * @return Retorna el Doument ErrorXML 
     */
	public Document returnError(int Codigo,String empresa, String Mensaje) {

		return returnError(Codigo,empresa,null,Mensaje);
	}

	public Document returnError(int Codigo,String empresa, String id_return,String Mensaje) {
		this.setRootElement(new Element("ERROR"));
		
		Element	errorCode 	= new Element("errorCode");
		Element idreturn = new Element("id");
		Element errorMsg 	= new Element("errorMsg");

		errorCode.setText(String.valueOf(Codigo));
		errorMsg.setText(Mensaje);
		
		this.getRootElement().addContent(errorCode);
		
		if (id_return!=null) {
		    idreturn.setText(id_return);
			this.getRootElement().addContent(idreturn);
		}
		
		this.getRootElement().addContent(errorMsg);
		
		return this;
	}
	
}