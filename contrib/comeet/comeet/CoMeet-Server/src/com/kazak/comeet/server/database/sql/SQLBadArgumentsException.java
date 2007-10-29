package com.kazak.comeet.server.database.sql;

import com.kazak.comeet.lib.misc.Language;

/**
 * SQLBadArgumentsException.java Creado el 26-jul-2004
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
public class SQLBadArgumentsException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 9165655132281876186L;
	private String codigo;

    public SQLBadArgumentsException(String codigo) {
        this.codigo = codigo;
    }

    public String getMessage() {
        return Language.getWord("ERR_COD_ARGS") + ": " + this.codigo;
    }

}
