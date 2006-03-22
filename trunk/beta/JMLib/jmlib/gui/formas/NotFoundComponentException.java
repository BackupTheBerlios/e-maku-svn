package jmlib.gui.formas;

import jmlib.miscelanea.idiom.Language;

/**
 * NotFoundComponentException.java Creado el 27-jun-2005
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
 * Esta clase es la encargada de generar una excepcion, cuando se quiere invocar
 * un metodo de un componente que no se encuentra en la tabla de componentes 
 * instanciados en GenericForm
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class NotFoundComponentException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8967446860746060180L;
    private String mensaje;
    
    /**
     * 
     */
    public NotFoundComponentException(String component) {
        super();
        this.mensaje = Language.getWord("ERR_NOT_FOUND_COMPONENT") + component;
        // TODO Auto-generated constructor stub
    }

    public String getMessage() {
        return  mensaje;
    }

}
