package com.kazak.comeet.admin.control;

import java.awt.Cursor;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.admin.gui.main.LoginWindow;
import com.kazak.comeet.admin.gui.main.MainTreeManager;
import com.kazak.comeet.admin.gui.main.MainWindow;
import com.kazak.comeet.admin.network.SocketHandler;
import com.kazak.comeet.admin.transactions.QuerySender;
import com.kazak.comeet.lib.network.ArrivedPackageEvent;
import com.kazak.comeet.lib.network.PackageComingListener;

public class HeadersValidator implements PackageComingListener {

    private static Element root;
    
    public void validPackage(ArrivedPackageEvent arrivedPackageEvent) {
    	
   		Document doc = arrivedPackageEvent.getDoc();
        root = doc.getRootElement();
        String name = root.getName();
        
        /* Temporal debugging code
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        try {
            xmlOutputter.output(doc,System.out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/
		
    	if(name.equals("ACPBegin")) {
    		LoginWindow.setLogged(true);
            LoginWindow.setVisible(false);
            new MainWindow(root.getChildText("AppOwner"),root.getChildText("UserLevel"));
        }
    	else if(name.equals("LogContentInit")) {
    		LogServerViewer.reset();
        }
    	else if(name.equals("LogContent")) {
    		LogServerViewer.loadLog(doc);
        }
        else if(name.equals("ACPFAILURE")) {
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
        else if(name.equals("ANSWER") || name.equals("SEARCHRESULT") 
        		|| name.equals("USERLIST") || name.equals("CONTROLRESULT")
        		|| name.equals("MAILINFO")) {
            String id = root.getChildText("id");
            QuerySender.putResultOnPool(id,doc);
        }
        else if(name.equals("ERROR")) {
        	SyncManager.errorHappened = true;
			displayError();
        }
        else if(name.equals("RELOADTREE")) {
        	Cache.loadInfoTree(1);
        	MainWindow.updateGrid(MainTreeManager.getSelectedPath());
        }
        else if(name.equals("SUCCESS")) {
            String id = root.getChildText("id");
            QuerySender.putResultOnPool(id,doc);
            String message = root.getChildText("successMessage");
            JOptionPane.showMessageDialog(null, message);
        }
        else if(name.equals("SUCCESSSYNC")) {
        	SyncManager.isSuccessful = true;
        	JOptionPane.showMessageDialog(null, "Base de datos sincronizada con éxito.");
        }
        else if(name.equals("ERRSYNC")) {
        	SyncManager.errorHappened = true;
            String message = root.getChildText("message");
            JOptionPane.showMessageDialog(null, message);
        }
        else if(name.equals("ERROR")) {
			displayError();
        }        
        else {
        	System.out.println("ERROR: Inconsistencia en el formato del protocolo: " + name);
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
