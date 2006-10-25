package client;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import bsh.Interpreter;
import client.gui.components.MainWindow;
import client.gui.forms.Connection;
import client.gui.forms.FirstDialog;
import client.misc.ClientConst;
import client.misc.settings.ConfigFile;
import client.misc.settings.ConfigFileNotLoadException;

import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;

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
        	
             
        	NimRODLookAndFeel look = new NimRODLookAndFeel();
        	NimRODTheme theme = new NimRODTheme();

        	NimRODLookAndFeel.setCurrentTheme(theme);
        	UIManager.setLookAndFeel(look);
        	Font f = new Font("Tahoma", Font.PLAIN, 12);
        	
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
        	UIManager.put("TitledBorder.font",	f);
        	UIManager.put("RadioButton.font",	f);
        	UIManager.put("InternalFrame.font",	f);
        	UIManager.put("Table.font",			f);
        	UIManager.put("TabbedPane.font",	f);
        	UIManager.put("WindowBackground.color",	Color.GRAY);
           
            ConfigFile.loadSettings();
            
            new Connection();

        }

        catch (ConfigFileNotLoadException e) {
            FirstDialog dialogo = new FirstDialog(new JFrame(),ClientConst.KeyClient,FirstDialog.CREATE);
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