package common.gui.components;

import common.misc.language.Language;

/**
 * ErrorDataException.java Creado el 13-abr-2005
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
 * Clase encargada de generar una excepcion cuando al componer un paquete
 * existen errores de consistencia
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class DataErrorException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4679116406143181554L;
    private String mensaje;
    
    /**
     * 
     */

    public DataErrorException(String component) {
        this.mensaje = Language.getWord("ERR_DATA_PACKAGE") + component;
    }

    public String getMessage() {
        return  mensaje;
    }

}
