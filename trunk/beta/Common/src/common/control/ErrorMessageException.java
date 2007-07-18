package common.control;

import common.misc.language.Language;

public class ErrorMessageException extends Exception {
/**
	 * 
	 */
	private static final long serialVersionUID = 5363412429515039450L;
private String mensaje;
    
    public ErrorMessageException(String component) {
        this.mensaje = Language.getWord("ERROR") + component;
    }

    public String getMessage() {
        return  mensaje;
    }
}
