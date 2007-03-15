package client;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import bsh.Interpreter;
import client.gui.components.MainWindow;
import client.gui.forms.Connection;
import client.gui.forms.SettingsDialog;
import client.misc.ClientConstants;
import client.misc.settings.ConfigFileHandler;
import client.misc.settings.ConfigFileNotLoadException;

import common.comunications.SocketConnector;
import common.misc.language.Language;

/**
 * Run.java Creado el 29-jul-2004
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
 * Esta clase se encarga de iniciar el cliente E-maku
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class Run {
	
	public static final Interpreter shellScript = new Interpreter();;

	public static void main(String[] args) {

        try {
        	ConfigFileHandler.loadSettings();
        	String look = ConfigFileHandler.getClassForLookAndFeel();
        	String pathjar = ConfigFileHandler.getURLJarForLookAndFeel();
        	if (look!=null && pathjar!=null && !"".equals(look) && !"".equals(pathjar)) {
        		System.out.println("Cargando Look And Feel "+look);
        		URL urls [] = {};
                JarFileLoader cl = new JarFileLoader (urls);
                cl.addFile (pathjar);
                cl.loadClass (look);
            	ConfigFileHandler.getClassForLookAndFeel();
            	UIManager.setLookAndFeel(look);	
        	}        	
//        	InputStream is = new FileInputStream(new File("/home/felipe/bionic.ttf"));
//	        Font fo = Font.createFont(
//	                               Font.TRUETYPE_FONT, is);
//	        Font f = fo.deriveFont(12f);
        	Font f = new Font("Tahoma", Font.PLAIN, 12);
        	Font f2 = new Font("Tahoma",Font.BOLD,14);
        	UIManager.put("Menu.font",			f);
        	UIManager.put("MenuItem.font",		f);
        	UIManager.put("Button.font",		f);
        	UIManager.put("Label.font",			f);
        	UIManager.put("TextField.font",		f);
        	UIManager.put("ComboBox.font",		f);
        	UIManager.put("CheckBox.font",		f);
        	UIManager.put("TextPane.font",		f);
        	UIManager.put("TextArea.font",		f);
        	UIManager.put("List.font",			f);
        	UIManager.put("Slider.font",		f);
        	UIManager.put("TitledBorder.font",	f2);
        	UIManager.put("RadioButton.font",	f);
        	UIManager.put("InternalFrame.font",	f2);
        	UIManager.put("Table.font",			f);
        	UIManager.put("TabbedPane.font",	f);
        	UIManager.put("DesktopColor.color",	Color.GRAY);
           
            
            
            new Connection();

        }

        catch (ConfigFileNotLoadException e) {
            SettingsDialog dialogo = new SettingsDialog(new JFrame(),ClientConstants.KeyClient,SettingsDialog.CREATE);
            dialogo.setLocationRelativeTo(dialogo.getParent());
            dialogo.pack();
            dialogo.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exit() {
    	int confirm = -1;
        try {
        	confirm=JOptionPane.showConfirmDialog(
        									MainWindow.getRefWindow(),
        									Language.getWord("CLOSE_CURRENT_APP"),
        									"",JOptionPane.YES_NO_OPTION);
    		if(confirm==JOptionPane.YES_OPTION){
    			SocketConnector.getSock().close();
    		}
        }
        catch (NullPointerException e) {}
        catch (IOException e) {}
        finally {
        	if (confirm == JOptionPane.YES_OPTION) {
        		System.exit(0);
        	}
        }
    }
}

class JarFileLoader extends URLClassLoader {
    public JarFileLoader (URL[] urls) {
        super (urls);
    }

    public void addFile (String path) throws MalformedURLException {
        String urlPath = "jar:file://" + path + "!/";
        addURL (new URL (urlPath));
    }
}