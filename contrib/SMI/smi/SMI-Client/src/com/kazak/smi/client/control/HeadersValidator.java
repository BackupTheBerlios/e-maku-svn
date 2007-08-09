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
import com.kazak.smi.lib.network.ArrivedPackageEvent;
import com.kazak.smi.lib.network.PackageComingListener;


public class HeadersValidator implements PackageComingListener {

    private static Element root;
    private static ArrayList<MessageListener> msgListenersList = new ArrayList<MessageListener>();
    
    public void validPackage(ArrivedPackageEvent APe) {	
   		Document doc = APe.getDoc();
        root = doc.getRootElement();
        String name = root.getName();
        
        // Temporal code for degugging
    	XMLOutputter out2 = new XMLOutputter();
    	out2.setFormat(Format.getPrettyFormat());
    	try {
			out2.output(doc,System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	if(name.equals("ACPBegin")) {
    		LoginWindow.quit();
    		LoginWindow.setLogged(true);
            Cache.loadGroupsCache();
            Cache.loadMessagesHistory();
            Element element = new Element("root");
            element.addContent(new Element("type").setText("connection"));
            TrayManager.setLogged(true);
        }
    	else if(name.equals("Message")) {
    		Cache.addMessages(root);
    		root.addContent(new Element("type").setText("message"));
            MessageEvent msgEvent = new MessageEvent(root,root);
            notifyMessage(msgEvent);
            MessageViewer.show();
        }
        else if(name.equals("ACPFAILURE")) {
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
        else if(name.equals("ANSWER")) {
            String id = root.getChildText("id");
            QuerySender.putResultOnPool(id,doc);
        }
        else if(name.equals("SUCCESS")) {
            String id = root.getChildText("id");
            QuerySender.putResultOnPool(id,doc);
            String message = root.getChildText("successMessage");
            JOptionPane.showMessageDialog(null, message);
        }
        else if(name.equals("ERROR")) {
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
		for (int i = 0; i < msgListenersList.size(); i++) {
			MessageListener listener = msgListenersList.get(i);
			listener.getANewMessage(event);
		}
	}

	public static synchronized void addMessageListener(MessageListener l) {
		msgListenersList.add(l);
	}

	public static synchronized void removeACPFormListener(MessageListener l) {
		msgListenersList.remove(l);
	}
}