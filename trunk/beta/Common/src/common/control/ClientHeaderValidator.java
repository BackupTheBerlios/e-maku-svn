package common.control;

//import java.io.IOException;
import java.io.IOException;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
//import org.jdom.output.Format;
//import org.jdom.output.XMLOutputter;

import common.transactions.Cache;
import common.transactions.TransactionServerResultSet;

/**
 * ClientHeaderValidator.java Creado el 22-jul-2004
 * 
 * Este archivo es parte de E-Maku <A
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
    private static Vector <ReportListener> reportListener = new Vector<ReportListener>();
    private static Vector<SuccessListener> successListener = new Vector<SuccessListener>();
    
    /**
     * Este metodo se encarga de revisar toda las raices de los documentos que
     * llegan al servidor de transacciones.
     * 
     * @param doc
     *            Documento a validar
     */

    public ClientHeaderValidator() {}
    public static boolean validGeneral(Document doc) {

        /*
         * Obtenemos la raiz del documento si el documento no tiene raiz
         */
        raiz = doc.getRootElement();
        String nombre = raiz.getName();
        
         XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        try {
            if(!nombre.equals("ACPZip")) 
			out.output(raiz,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*
         *  Validacion paquete ANSWER 
         */
        if(nombre.equals("ANSWER")) {
            String id = raiz.getChildText("id");
            TransactionServerResultSet.putSpoolQuery(id,doc);
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
         *  Validación paquetes UPDATECODE
         */
        
        else if(nombre.equals("UPDATECODE")) {
            String key = raiz.getChildText("idDocument");
            String consecutive = raiz.getChildText("consecutive");
                       UpdateCodeEvent event = new UpdateCodeEvent(new ClientHeaderValidator(),key,consecutive);
            notifyUpdateCode(event);
            return true;
        }
        
        /*
         *  Validación paquetes DATE
         */
        
        else if(nombre.equals("DATE")) {
            String systemDate = raiz.getChildText("systemDate");
            DateEvent event = new DateEvent(new ClientHeaderValidator(),systemDate);
            notifyDate(event);
            return true;
        }
        else if(nombre.equals("PLAINREPORT") || nombre.equals("REPORT")) {
			// Aqui se debe notificar que llego el reporte
        	
        	//System.out.println("Entrando a PlainReport desde ClientHeaderValidator...");
        	Element element = null;
        	element = raiz.getChild("data");
			ReportEvent report;
			boolean plain = false;

			if(nombre.equals("PLAINREPORT")) {
				plain = true;
			}

			report= new ReportEvent(
					new ClientHeaderValidator(),
					raiz.getChildText("idReport"),
					raiz.getChildText("titleReport"),
					element,
					plain);

			notifyReport(report);
			return true;
        }        
        /*
         *  Validación paquetes SUCCESS
         */
        
        else if(nombre.equals("SUCCESS")) {
            String id = raiz.getChildText("id");
            String ndocument = "";
            String message  = "";
            
            if ("Q".equals(id.substring(0,1))) {
                TransactionServerResultSet.putSpoolQuery(id,doc);
            }
            
            /*
             * si por el contrario fue una transaccion entonces ...
             */
            else if ("T".equals(id.substring(0,1))){
            	Element EsuccessMessage = raiz.getChild("successMessage");
            	Element Endocument = raiz.getChild("ndocument");
            	message = EsuccessMessage.getText();
            	ndocument = Endocument!=null?Endocument.getValue():"";
        	}
            SuccessEvent event = new SuccessEvent(new ClientHeaderValidator(),id);
            if (!"".equals(ndocument)) {
            	event.setNdocument(ndocument);
            }
            if (!"".equals(message)) {
            	event.setMessage(message);
            }

            notifySuccess(event);
            return true;
        }
        /*
         *  Validación paquetes CACHE-ANSWER
         */
        
        else if (nombre.equals("CACHE-ANSWER")) {
        	System.out.println("llego un paquete de cache ...");
            new Cache(doc);
            return true;
        }
        
        return false;
    }
    
    private static synchronized void notifyReport(ReportEvent event) {
    	//System.out.println("*** Notificando evento...");
    	Vector lista;
        lista = (Vector)reportListener.clone();
        for (int i=0; i<lista.size();i++) {
        	ReportListener listener = reportListener.get(i);
            listener.arriveReport(event);
        }
    }
    
    public static void addReportListener(ReportListener listener ) {
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

    public static synchronized void addSuccessListener(SuccessListener listener ) {
        successListener.addElement(listener);
    }

    public static synchronized void removeSuccessListener(SuccessListener listener ) {
        successListener.removeElement(listener);
    }

    private static synchronized void notifySuccess(SuccessEvent event) {
        /*Vector lista;
        lista = (Vector)successListener.clone();*/
        for (int i=0; i<successListener.size();i++) {
            SuccessListener listener = successListener.elementAt(i);
            listener.cathSuccesEvent(event);
        }
    }
}