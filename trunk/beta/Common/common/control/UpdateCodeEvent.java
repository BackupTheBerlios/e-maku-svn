package common.control;

import java.util.EventObject;

/**
 * UpdateCodeEvent.java Creado el 28-jun-2005
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
 * Esta clase es necesaria para la generacion de eventos por la llegada de un
 * paquete UPDATECODE
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class UpdateCodeEvent extends EventObject {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -521235939092224547L;
	private String idDocument;
    private String consecutive;
    /**
     * 
     */
    public UpdateCodeEvent(Object source, String idDocument,String consecutive) {
        super(source);
        this.idDocument=idDocument;
        this.consecutive=consecutive;
    }

    /**
     * Asigna la llave del documento al objeto
     */
    
    public synchronized String getIdDocument() {
        return idDocument;
    }
    
    /**
     * Retorna la llave del documento
     * @param idDocument contiene la fecha
     */
    
    public synchronized void setIdDocument(String idDocument) {
        this.idDocument=idDocument;
    }

    /**
     * Asigna el consecutivo del documento al objeto
     */
    
    public synchronized String getConsecutive() {
        return consecutive;
    }
    
    /**
     * Retorna el consecutivo del objeto
     * @param consecutive contiene el consecutivo
     */
    
    public synchronized void setConsecutive(String consecutive) {
        this.consecutive=consecutive;
    }
}

