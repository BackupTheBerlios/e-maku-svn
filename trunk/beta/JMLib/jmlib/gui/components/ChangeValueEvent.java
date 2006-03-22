package jmlib.gui.components;

import java.util.EventObject;

/**
 * ChangeValueEvent.java Creado el 24-jun-2005
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
 * Esta clase es necesaria para lanzar el evento de cambio de valor de un
 * componente en la clase TableDataField
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class ChangeValueEvent extends EventObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8274940942773279037L;

	/**
     * 
     */
    public ChangeValueEvent(Object source) {
        super(source);
    }

}
