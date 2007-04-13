package com.kazak.smi.admin.control;

import java.awt.Cursor;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
//import org.jdom.output.Format;
//import org.jdom.output.XMLOutputter;

import com.kazak.smi.admin.gui.LoginWindow;
import com.kazak.smi.admin.gui.MainWindow;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.lib.network.ArrivePackageEvent;
import com.kazak.smi.lib.network.ArrivePackageListener;


public class HeadersValidator implements ArrivePackageListener {

    private static Element root;
    
    public void validPackage(ArrivePackageEvent APe) {
    	
   		Document doc = APe.getDoc();
        root = doc.getRootElement();
        String nombre = root.getName();
        
    	/*XMLOutputter out = new XMLOutputter();
    	out.setFormat(Format.getPrettyFormat());
    	try {
			out.output(doc,System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
    	if(nombre.equals("ACPBegin")) {
    		LoginWindow.setLoged(true);
            LoginWindow.setVisible(false);
            new MainWindow(root.getChildText("AppOwner"),root.getChildText("UserLevel"));
        }
    	else if(nombre.equals("LogContentInit")) {
    		LogServerViewer.reset();
        }
    	else if(nombre.equals("LogContent")) {
    		LogServerViewer.loadLog(doc);
        }
        else if(nombre.equals("ACPFAILURE")) {
            String message = root.getChildText("message");
            JOptionPane.showMessageDialog(null,message);
            LoginWindow.setEnabled();
            LoginWindow.setCursorState(Cursor.DEFAULT_CURSOR);
            try {
				SocketHandler.getSock().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        else if(nombre.equals("ANSWER")) {
            String id = root.getChildText("id");
            QuerySender.putSpoolQuery(id,doc);
        }
        else if(nombre.equals("ERROR")) {
        	Sync.successssyncerror = true;
			displayError();
        }
        else if(nombre.equals("RELOADTREE")) {
        	Cache.load();
        }
        else if(nombre.equals("SUCCESS")) {
            String id = root.getChildText("id");
            QuerySender.putSpoolQuery(id,doc);
            String message = root.getChildText("successMessage");
            JOptionPane.showMessageDialog(null, message);
        }
        else if(nombre.equals("SUCCESSSYNC")) {
        	Sync.successsync = true;
        	JOptionPane.showMessageDialog(Sync.getDialog(), "Base de datos Sincronizada con exito");
        }
        else if(nombre.equals("ERRSYNC")) {
        	Sync.successssyncerror = true;
            String message = root.getChildText("message");
            JOptionPane.showMessageDialog(Sync.getDialog(), message);
        }
        else if(nombre.equals("ERROR")) {
			displayError();
        }
        
        else {
        	System.out.println("Error en el formato del protocolo");
        }
    }
    
    private static void displayError() {
        JOptionPane.showMessageDialog(
        						MainWindow.getFrame(),
        						root.getChild("errorMsg").getText(),
        						"",
        						JOptionPane.ERROR_MESSAGE);
    }
}