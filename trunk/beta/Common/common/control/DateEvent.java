package common.control;

import java.util.EventObject;

/**
 * DateEvent.java Creado el 28-jun-2005
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
 * @author <A href='mailto:cristian_david@universia.net.co'>Cristian David Cepeda</A>
 */
public class DateEvent extends EventObject {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1735645715116581362L;
	private String date;
    /**
     * 
     */
    public DateEvent(Object source, String date) {
        super(source);
        this.date=date;
    }

    /**
     * Asigna la fecha al objeto
     */
    
    public synchronized String getDate() {
        return date;
    }
    
    /**
     * Retorna la fecha del objeto
     * @param date contiene la fecha
     */
    
    public synchronized void setIdPackage(String date) {
        this.date=date;
    }

}
