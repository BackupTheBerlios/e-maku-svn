package server.businessrules;

import common.miscelanea.idiom.Language;

/**
 * LNErrorProcecuteException.java Creado el 26-jul-2005
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
 * Esta clase se encarga de generar una excepcion generica para todos los
 * componentes personalizados de procesamiento de informacion en el servidor
 * de transacciones.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class LNErrorProcecuteException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7614988565136405777L;
	private String codigo;

    public LNErrorProcecuteException(String codigo) {
        this.codigo = codigo;
    }

    public String getMessage() {
        return Language.getWord("ERR_PROCECUTE_EXCEPTION") + this.codigo;
    }

}
