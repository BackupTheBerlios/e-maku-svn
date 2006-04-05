package jmlib.gui.formas;

import java.util.EventObject;

/**
 * AnswerEvent.java Creado el 14-abr-2005
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
 * Clase necesaria para el manejo de eventos a la llegada de un paquete answer
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class ChangeExternalValueEvent extends EventObject {

	private static final long serialVersionUID = -8818469504649345508L;
	private String externalValue;
	private String externalKey;
	
	public ChangeExternalValueEvent(Object source) {
        super(source);
    }

	public String getExternalValue() {
		return externalValue;
	}

	public void setExternalValue(String externalValue) {
		this.externalValue = externalValue;
	}

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}
}
