package server.basedatos.conexion;

import common.miscelanea.idiom.Language;


/**
 * PoolNotLoadException.java Creado el 13-jul-2004
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
 * Esta clase se utiliza para generar una excepcion en caso de que
 * el pool de conexiones no haya cargado satisfactoriamente las bases de datos.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class PoolNotLoadException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2685008709731534977L;
	private String bd;

    public PoolNotLoadException(String bd) {
        this.bd = bd;
    }
    public String getMessage() {
        return
        	Language.getWord("ERR_POOL") + this.bd;
    }
}