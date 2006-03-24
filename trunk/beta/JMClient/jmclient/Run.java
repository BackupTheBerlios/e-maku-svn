package jmclient;

import java.awt.Font;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import jmclient.gui.components.MainWindow;
import jmclient.gui.formas.Conexion;
import jmclient.miscelanea.JMClientCons;
import jmclient.miscelanea.configuracion.ConfigFile;
import jmclient.miscelanea.configuracion.ConfigFileNotLoadException;
import jmlib.comunicaciones.SocketConnect;
import jmlib.gui.formas.FirstDialog;
import jmlib.miscelanea.idiom.Language;

//import com.l2fprod.gui.plaf.skin.Skin;
//import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;

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
 * Informacion de la clase <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class Run {

    /*
     * Chambonadas que toca hacer cuando no sabes como se deben hacer las cosas ...
     */

    public static void main(String[] args) {

        try {
    
        	//new Run();
        	/*
        	PlasticXPLookAndFeel.setTabStyle(PlasticXPLookAndFeel.TAB_STYLE_METAL_VALUE);
            PlasticXPLookAndFeel.setMyCurrentTheme(new SkyBluer());
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
        	/*Run run = new Run();
            Skin skin = SkinLookAndFeel.loadThemePack(run.getClass().getResource("/coronaHthemepack.zip"));
            SkinLookAndFeel.setSkin(skin);
            UIManager.setLookAndFeel(new SkinLookAndFeel());*/
             
            Font f = new Font("ARIAL", Font.PLAIN, 12);
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

            ConfigFile.Cargar();
            new Conexion();

        }

        catch (ConfigFileNotLoadException e) {
            FirstDialog dialogo = new FirstDialog(new JFrame(),JMClientCons.KeyClient);
            dialogo.setLocationRelativeTo(dialogo.getParent());
            dialogo.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void salir() {
    	int confirm = -1;
        try {
        	confirm=JOptionPane.showConfirmDialog(
        			MainWindow.getRefWindow(),
        			Language.getWord("CLOSE_CURRENT_APP"),
        			"",JOptionPane.YES_NO_OPTION);
    		if(confirm==JOptionPane.YES_OPTION){
    			SocketConnect.getSock().close();
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