package com.kazak.smi.server.database.sql;

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
    private Document driverArgs=null;
    private String method=null;
    private Document methodArgs=null;
    
    public ClassLogicDriver(String driver,Document driverArgs,String method,Document methodArgs) {
        this.driver=driver;
        this.driverArgs=driverArgs;
        this.method=method;
        this.methodArgs=methodArgs;
    }
    
    public Document getDriverArgs() {
        return driverArgs;
    }
    public Document getMethodArgs() {
        return methodArgs;
    }
    public String getDriver() {
        return driver;
    }
    public String getMethod() {
        return method;
    }
}
