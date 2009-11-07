package common.control;

//import java.io.IOException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;

import common.comunications.SendReloadPackage;
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
    private static Vector<ErrorListener> errorListener = new Vector<ErrorListener>();
    
    /**
     * Este metodo se encarga de revisar toda las raices de los documentos que
     * llegan al servidor de transacciones.
     * 
     * @param doc
     *            Documento a validar
     */

    public ClientHeaderValidator() {}
    public static boolean validGeneral(Document doc) throws ErrorMessageException {

        /*
         * Obtenemos la raiz del documento si el documento no tiene raiz
         */
        raiz = doc.getRootElement();
        String nombre = raiz.getName();
        
  /*      XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        try {
            if(!nombre.equals("ACPZip")) 
			out.output(raiz,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        
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
			/*
        	try {
				new SendReloadPackage("localhost",9117,"mt1010","emaku","rosa");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/
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
            SuccessEvent event = new SuccessEvent(new ClientHeaderValidator(),id,ndocument,message);

            notifySuccess(event);
	        System.gc();
            return true;
        }
        /*
         *  Validación paquetes CACHE-ANSWER
         */
        
        else if (nombre.equals("CACHE-ANSWER")) {
            new Cache(doc);
            return true;
        }
        /*
         *  Validacion paquetes de Error
         */
        
        else if(nombre.equals("ERROR")) {
            String id = raiz.getChildText("id");
            /*
             * Si existe id quiere decir que una consulta o una transaccion
             * retorno error
             */
/*	        	XMLOutputter out = new XMLOutputter();
        	out.setFormat(Format.getPrettyFormat());
        	try {
				out.output(doc,System.out);
			} catch (IOException e) {
				e.printStackTrace();
			}
*/
            String ndocument = "";
            String message  = "";
            
            if(id!=null && "Q".equals(id.substring(0,1))) {
                /*
                 * si fue una consulta entonces ...
                 */
                TransactionServerResultSet.putSpoolQuery(id,doc);
            }
            /*
             * si por el contrario fue una transaccion entonces ...
             */
            else if (id!=null && "T".equals(id.substring(0,1))){
            	Element EerrorMessage = raiz.getChild("errorMessage");
            	Element Endocument = raiz.getChild("ndocument");
            	message = EerrorMessage!=null?EerrorMessage.getText():"";
            	ndocument = Endocument!=null?Endocument.getValue():"";
            	System.out.println("numero del documento: "+ndocument+" id transaccion: "+id);
        	}
            ErrorEvent event = new ErrorEvent(new ClientHeaderValidator(),id,ndocument,message);
            notifyError(event);
			throw new ErrorMessageException(id);
        }
        
        return false;
    }
    
    private static void notifyReport(ReportEvent event) {
        for (ReportListener l : reportListener) {
            l.arriveReport(event);
        }
    }
    
    public static void addReportListener(ReportListener listener ) {
        reportListener.add(listener);
    }

	public static void removeReportListener(ReportListener listener ) {
        reportListener.add(listener);
    }
	

    public static void addDateListener(DateListener listener ) {
        dateListener.addElement(listener);
    }

    public static void removeDateListener(DateListener listener ) {
        dateListener.removeElement(listener);
    }

    private static void notifyDate(DateEvent event) {
    	synchronized (dateListener) {
	    	for (DateListener l : dateListener) {
	    		if (l!=null) {
	    			l.cathDateEvent(event);
	    		}
	    		else {
	    			dateListener.remove(l);
	    		}
	        }
    	}
    }

    public static void addUpdateCodeListener(UpdateCodeListener listener ) {
        updateCodeListener.addElement(listener);
    }

    public static void removeUpdateCodeListener(UpdateCodeListener listener ) {
        updateCodeListener.removeElement(listener);
    }

    private static void notifyUpdateCode(UpdateCodeEvent event) {
    	synchronized(updateCodeListener){
	        for (UpdateCodeListener l : updateCodeListener) {
	            l.cathUpdateCodeEvent(event);
	        }
    	}
    }

    public static void addSuccessListener(SuccessListener listener ) {
        successListener.addElement(listener);
    }

    public static void removeSuccessListener(SuccessListener listener ) {
        successListener.removeElement(listener);
    }

    private static void notifySuccess(SuccessEvent event) {
        for (SuccessListener l : successListener) {
            l.cathSuccesEvent(event);
        }
    }
    
    public static void addErrorListener(ErrorListener listener ) {
        errorListener.addElement(listener);
    }

    public static void removeErrorListener(ErrorListener listener ) {
        errorListener.removeElement(listener);
    }

    private static synchronized void notifyError(ErrorEvent event) {
        for (ErrorListener l : errorListener) {
            l.cathErrorEvent(event);
        }
    }
}