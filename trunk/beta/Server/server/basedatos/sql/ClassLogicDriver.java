package server.basedatos.sql;

import org.jdom.Document;

/**
 * ClassLogicDriver.java Creado el 18-ene-2005
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
 * Esta clase almacenara los drivers y metodos de cada transaccion con sus respectivos
 * argumentos
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class ClassLogicDriver {

    private String driver=null;
    private Document arg_driver=null;
    private String method=null;
    private Document arg_method=null;
    
    public ClassLogicDriver(String driver,Document arg_driver,String method,Document arg_method) {
        this.driver=driver;
        this.arg_driver=arg_driver;
        this.method=method;
        this.arg_method=arg_method;
    }
    
    
    public Document getArg_driver() {
        return arg_driver;
    }
    public Document getArg_method() {
        return arg_method;
    }
    public String getDriver() {
        return driver;
    }
    public String getMethod() {
        return method;
    }
}
