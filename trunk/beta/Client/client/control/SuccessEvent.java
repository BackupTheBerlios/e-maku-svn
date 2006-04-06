package client.control;

import java.util.EventObject;

/**
 * SuccessListener.java Creado el 22-mar-2005
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
 * Se necesita que cualquier clase pueda saber cuando llega un paquete 
 * Success, para ello se crea esta clase la cual se encargara de generar
 * de forma de eventos la llegada de estos paquetes
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian_david@universia.net.co'>Cristian David Cepeda</A>
 */
public class SuccessEvent extends EventObject {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4783712232952864800L;
	// Este objeto almacenara el idPackage de cada trasaccion o de cada query
    private String idPackage;

    public SuccessEvent(Object source, String idPackage) {
        super(source);
        this.idPackage = idPackage;
    }

    
    /**
     * Asigna el idPackage al objeto
     */
    
    public synchronized String getIdPackage() {
        return idPackage;
    }
    
    /**
     * Retorna el idPackage del objeto
     * @param idPackage
     */
    
    public synchronized void setIdPackage(String idPackage) {
        this.idPackage = idPackage;
    }
} 
