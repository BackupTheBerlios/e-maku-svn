package client.gui.components;

import common.miscelanea.idiom.Language;

/**
 * ArgsException.java Creado el 20-abr-2005
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
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian_david@universia.net.co'>Cristian David Cepeda</A>
 */
public class ArgsException  extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5480595316516467520L;

	/**
     * 
     */
    public ArgsException() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public String getMessage() {
        return Language.getWord("ARGS_EXCEPTION");
    }

}
