package com.kazak.smi.client.control;

import java.awt.Cursor;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.kazak.smi.client.gui.LoginWindow;
import com.kazak.smi.client.gui.MessageViewer;
import com.kazak.smi.client.gui.TrayManager;
import com.kazak.smi.client.network.QuerySender;
import com.kazak.smi.client.network.SocketHandler;
import com.kazak.smi.lib.network.ArrivePackageEvent;
import com.kazak.smi.lib.network.ArrivePackageListener;


public class HeadersValidator implements ArrivePackageListener {

    private static Element root;
    private static ArrayList<MessageListener> messageListeners = new ArrayList<MessageListener>();
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
    		LoginWindow.quit();
    		LoginWindow.setLoged(true);
            Cache.loadCacheGroups();
            Cache.loadHistoryMessages();
            Element e = new Element("root");
            e.addContent(new Element("type").setText("connection"));
            TrayManager.setLoged(true);
        }
    	else if(nombre.equals("Message")) {
    		Cache.addMessages(root);
    		root.addContent(new Element("type").setText("message"));
            MessageEvent evt = new MessageEvent(root,root);
            notifyMessage(evt);
            MessageViewer.show();
        }
        else if(nombre.equals("ACPFAILURE")) {
            String message = root.getChildText("message");
            JOptionPane.showMessageDialog(LoginWindow.getFrame(),message);
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
        else if(nombre.equals("SUCCESS")) {
            String id = root.getChildText("id");
            QuerySender.putSpoolQuery(id,doc);
            String message = root.getChildText("successMessage");
            JOptionPane.showMessageDialog(null, message);
        }
        else if(nombre.equals("ERROR")) {
			displayError();
        }
        else {
        	System.out.println("Error en el formato del protocolo");
        	XMLOutputter out = new XMLOutputter();
        	out.setFormat(Format.getPrettyFormat());;
        	try {
    			out.output(doc,System.out);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
    }
    
    private static void displayError() {
        JOptionPane.showMessageDialog(
        						null,
        						root.getChild("errorMsg").getText(),
        						"",
        						JOptionPane.ERROR_MESSAGE);
    }
    
    private static void notifyMessage(MessageEvent event) {
		for (int i = 0; i < messageListeners.size(); i++) {
			MessageListener listener = messageListeners.get(i);
			listener.arriveMessage(event);
		}
	}

	public static synchronized void addMessageListener(MessageListener l) {
		messageListeners.add(l);
	}

	public static synchronized void removeACPFormListener(MessageListener l) {
		messageListeners.remove(l);
	}
}