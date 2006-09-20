package client.control;

import java.awt.Cursor;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import client.gui.components.MainWindow;
import client.gui.forms.Connection;
import client.gui.forms.Splash;

import common.comunications.ArrivePackageEvent;
import common.comunications.ArrivePackageListener;
import common.comunications.SocketConnector;
import common.control.ClientHeaderValidator;
import common.transactions.STResultSet;

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

public class HeadersValidator implements ArrivePackageListener {

    private static Element raiz;

    /**
     * Este metodo se encarga de revisar toda las raices de los documentos que
     * llegan al servidor de transacciones.
     * 
     * @param doc
     *            Documento a validar
     */

    public HeadersValidator(){
    	
    }
    
    public void validPackage(ArrivePackageEvent APe) {
    	
    		Document doc = APe.getDoc();

        /*
         * Obtenemos la raiz del documento si el documento no tiene raiz
         */
        raiz = doc.getRootElement();
        String nombre = raiz.getName();
        
        /*
         * Se validan las cabeceras genericas, que necesitan acceso desde el paquete
         * common
         */
        
        if (!ClientHeaderValidator.validGeneral(doc)) {
	        /*
	         *  Validacion paquete ACP
	         */
	        if(nombre.equals("ACPBegin")) {
	            Connection.dispose();
	            Splash.ShowSplash();
	            ACPHandler.ACPBegin(doc);
	            String company = raiz.getChildText("companyName");
	            String companyID = raiz.getChildText("companyID");
	            String title = "Emaku - Qhatu Ltda";
	            
	            if(company != null && companyID != null)
	            	title = company + " - Nit: " + companyID;
	            
	            new MainWindow(title);
	        }
	        
	        else if(nombre.equals("ACPZip")) {
	        	ACPHandler.ACPData(doc);
	        }
	        else if(nombre.equals("ACPFAILURE")) {
	            String message = raiz.getChildText("message");
	            JOptionPane.showMessageDialog(null,message);
	            Connection.setEnabled();
	            Connection.setCursorState(Cursor.DEFAULT_CURSOR);
	            try {
					SocketConnector.getSock().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
	            if(id!=null) {
	                /*
	                 * si fue una consulta entonces ...
	                 */
	                if ("Q".equals(id.substring(0,1))) 
	                    STResultSet.putSpoolQuery(id,doc);
	                /*
	                 * si por el contrario fue una transaccion entonces ...
	                 */
	                else if ("T".equals(id.substring(0,1))){
						displayError();
	            	}
	            }
	            else {
					displayError();
	        	}
	        }
	        
	        
	        
	        
	        /*
	         *  Si no corresponde a ninguno de los paquetes anteriores es porque 
	         *  el formato del protocolo esta errado.
	         */
	        
	        else {
	            System.out.println("Error en el formato del protocolo");
		        	XMLOutputter out = new XMLOutputter();
		        	out.setFormat(Format.getPrettyFormat());
		        	try {
						out.output(doc,System.out);
					} catch (IOException e) {
						e.printStackTrace();
					}
	
		        }
        }
    }
    
    private static void displayError() {
        JFrame jf = new JFrame();
        JOptionPane.showMessageDialog(
                jf,raiz.getChild("errorMsg").getText(),
                "",JOptionPane.ERROR_MESSAGE);
    }
}