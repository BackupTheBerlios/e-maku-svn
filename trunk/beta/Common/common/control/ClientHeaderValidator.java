package common.control;

import java.util.ArrayList;
import java.util.Vector;

import common.transactions.Cache;
import common.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * ClientHeaderValidator.java Creado el 22-jul-2004
 * 
 * Este archivo es parte de JMClient <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * JMClient es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de Validar las cabeceras (raices) de los paquetes XML
 * que llegan al Servidor de transacciones. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */

public class ClientHeaderValidator {

    private static Element raiz;
    private static Vector <DateListener>dateListener = new Vector<DateListener>();
    private static Vector <UpdateCodeListener>updateCodeListener = new Vector<UpdateCodeListener>();
    private static ArrayList<ReportListener> reportListener = new ArrayList<ReportListener>();
    
    /**
     * Este metodo se encarga de revisar toda las raices de los documentos que
     * llegan al servidor de transacciones.
     * 
     * @param doc
     *            Documento a validar
     */

    
    public static boolean validGeneral(Document doc) {

        /*
         * Obtenemos la raiz del documento si el documento no tiene raiz
         */
        raiz = doc.getRootElement();
        String nombre = raiz.getName();
        
        
        /*
         *  Validacion paquete ANSWER 
         */
        if(nombre.equals("ANSWER")) {
            String id = raiz.getChildText("id");
            STResultSet.putSpoolQuery(id,doc);
            return true;
        }
        
        
        /*
         *  Validacion paquetes MESSAGE
         */
        
        else if(nombre.equals("MESSAGE")) {
            System.out.println("Recibi un paquete MESSAGE");
            return true;
        }
        
        /*
         *  Validaci�n paquetes UPDATECODE
         */
        
        else if(nombre.equals("UPDATECODE")) {
            String key = raiz.getChildText("idDocument");
            String consecutive = raiz.getChildText("consecutive");
                       UpdateCodeEvent event = new UpdateCodeEvent(new ClientHeaderValidator(),key,consecutive);
            notifyUpdateCode(event);
            return true;
        }
        
        /*
         *  Validaci�n paquetes DATE
         */
        
        else if(nombre.equals("DATE")) {
            String systemDate = raiz.getChildText("systemDate");
            DateEvent event = new DateEvent(new ClientHeaderValidator(),systemDate);
            notifyDate(event);
            return true;
        }
        else if(nombre.equals("REPORT")) {
			// Aqui se debe notificar que llego el reporte
        	Element element = null;
        	element = raiz.getChild("data");
			ReportEvent report = new ReportEvent(
										new ClientHeaderValidator(),
										raiz.getChildText("idReport"),
										raiz.getChildText("titleReport"),
										element);
			notifyReport(report);
			return true;
        }
        /*
         *  Validaci�n paquetes CACHE-ANSWER
         */
        
        else if (nombre.equals("CACHE-ANSWER")) {
            new Cache(doc);
            return true;
        }
        
        return false;
    }
    
    private static synchronized void notifyReport(ReportEvent event) {
        for (int i=0; i<reportListener.size();i++) {
            ReportListener listener = reportListener.get(i);
            listener.arriveReport(event);
        }
    }
    
    public synchronized static void addReportListener(ReportListener listener ) {
        reportListener.add(listener);
    }

	public synchronized static void removeReportListener(ReportListener listener ) {
        reportListener.add(listener);
    }
	

    public static synchronized void addDateListener(DateListener listener ) {
        dateListener.addElement(listener);
    }

    public static synchronized void removeDateListener(DateListener listener ) {
        dateListener.removeElement(listener);
    }

    private static synchronized void notifyDate(DateEvent event) {
        Vector lista;
        lista = (Vector)dateListener.clone();
        for (int i=0; i<lista.size();i++) {
            DateListener listener = (DateListener)lista.elementAt(i);
            listener.cathDateEvent(event);
        }
    }

    public static synchronized void addUpdateCodeListener(UpdateCodeListener listener ) {
        updateCodeListener.addElement(listener);
    }

    public static synchronized void removeUpdateCodeListener(UpdateCodeListener listener ) {
        updateCodeListener.removeElement(listener);
    }

    private static synchronized void notifyUpdateCode(UpdateCodeEvent event) {
        Vector lista;
        lista = (Vector)updateCodeListener.clone();
        for (int i=0; i<lista.size();i++) {
            UpdateCodeListener listener = (UpdateCodeListener)lista.elementAt(i);
            listener.cathUpdateCodeEvent(event);
        }
    }

}