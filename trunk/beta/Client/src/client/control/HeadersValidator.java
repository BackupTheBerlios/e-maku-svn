package client.control;

import java.awt.Cursor;
import java.awt.HeadlessException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import client.gui.components.MainWindow;
import client.gui.forms.Connection;
import client.misc.settings.ConfigFileHandler;

import common.comunications.ArrivedPackageEvent;
import common.comunications.ArrivedPackageListener;
import common.comunications.SendReloadPackage;
import common.comunications.SocketConnector;
import common.control.ClientHeaderValidator;
import common.control.ErrorMessageException;
import common.misc.parameters.EmakuParametersStructure;

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

public class HeadersValidator implements ArrivedPackageListener {

    private static Element root;
    private String title;

    /**
     * Este metodo se encarga de revisar toda las raices de los documentos que
     * llegan al servidor de transacciones.
     * 
     * @param doc
     *            Documento a validar
     */

    public HeadersValidator(){
    	
    }
    
    public void validPackage(ArrivedPackageEvent APe) {
    	
    	Document doc = APe.getDoc();

        /*
         * Obtenemos la raiz del documento si el documento no tiene raiz
         */
        root = doc.getRootElement();
        String nombre = root.getName();
        
        /*
         * Se validan las cabeceras genericas, que necesitan acceso desde el paquete
         * common
         */
        
        try {
        	
			if (!ClientHeaderValidator.validGeneral(doc)) {
			    /*
			     *  Validacion paquete ACP
			     */
			    if(nombre.equals("ACPBegin")) {
			        Connection.dispose();          
			        ACPHandler.ACPBegin(doc);
			        String company = root.getChildText("companyName");
			        String companyID = root.getChildText("companyID");
			        title = "[ eMaku ] - ";

			        /*
			         * Cargando configuración dependiendo de la empresa ...
			         */
			        String dataBase = EmakuParametersStructure.getParameter("dataBase");
			        ConfigFileHandler.loadJarFile(dataBase);

			        if(company != null && companyID != null) {
			        	title += company + " / Nit: " + companyID;
			        }
			   		new MainWindow(ConfigFileHandler.getJarDirectory()+"/conf",title);
			    }
			    
			    else if(nombre.equals("ACPZip")) {
			    	ACPHandler.ACPData(doc);
			    }
			    else if(nombre.equals("ACPFAILURE")) {
			        String message = root.getChildText("message");
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
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorMessageException e) {
			// TODO Auto-generated catch block
			displayError();
		}
    }
    
    private static void displayError() {
    	final String msg = root.getChild("errorMsg").getText();
    	Runnable showModalDialog = new Runnable() {
            public void run() {
            	JOptionPane.showMessageDialog(
						MainWindow.getFrame(),
						msg,
						"",
						JOptionPane.ERROR_MESSAGE);
            }
        };
        SwingUtilities.invokeLater(showModalDialog);
    	System.out.println("error: "+root.getChild("errorMsg").getText());
    }
}