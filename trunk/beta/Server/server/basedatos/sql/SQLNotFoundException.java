package server.basedatos.sql;

import common.miscelanea.idiom.Language;

/**
 * SQLNotFoundException.java Creado el 15-jul-2004
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
 * Pendiente 
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class SQLNotFoundException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4504541462017118808L;
	private String codigo;

    public SQLNotFoundException(String codigo) {
        this.codigo = codigo;
    }

    public String getMessage() {
        return Language.getWord("ERR_COD_NOT_FOUND") + this.codigo;
    }
}