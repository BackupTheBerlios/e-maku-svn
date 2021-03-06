package common.gui.components;

import common.misc.language.Language;

/**
 * VoidPackageException.java Creado el 28-sep-2004
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
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class VoidPackageException extends Exception {


	private static final long serialVersionUID = -4658473311250735174L;
    private String message;
    
    public VoidPackageException(String message,boolean single) {
    	if (single) {
    		this.message = message;
    	}
    	else {
    		this.message = Language.getWord("VOID_PACKAGE") + message;	
    	}
    }
    
    public VoidPackageException(String message) {
    	this.message = Language.getWord("VOID_PACKAGE") + message;
    }

    public String getMessage() {
        return  message;
    }
}