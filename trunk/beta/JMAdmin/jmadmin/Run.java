package jmadmin;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jmadmin.gui.formas.MainWindow;
import jmadmin.miscelanea.JMAdminCons;
import jmadmin.miscelanea.configuracion.ConfigFile;
import jmadmin.miscelanea.configuracion.ConfigFileNotLoadException;
import jmlib.gui.formas.FirstDialog;

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
    
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
            Font f = new Font("ARIAL", Font.PLAIN, 12);
            UIManager.put("Menu.font", f);
            UIManager.put("MenuItem.font", f);
            UIManager.put("Button.font", f);
            UIManager.put("Label.font", f);
            UIManager.put("TextField.font", f);
            UIManager.put("ComboBox.font", f);
            UIManager.put("CheckBox.font", f);
            UIManager.put("TextPane.font", f);
            UIManager.put("TextArea.font", f);
            UIManager.put("List.font", f);
            UIManager.put("Slider.font", f);
            UIManager.put("TitledBorder.font", f);
            UIManager.put("RadioButton.font", f);
            UIManager.put("InternalFrame.font", f);
            UIManager.put("Table.font", f);
            UIManager.put("TabbedPane.font", f);
            ConfigFile.Cargar();
            new MainWindow();
        }
        catch (UnsupportedLookAndFeelException ULAFEe) {
            ULAFEe.printStackTrace();
        }
        catch (ConfigFileNotLoadException e) {
            FirstDialog dialogo = new FirstDialog(new JFrame(),JMAdminCons.KeyAdmin);
            dialogo.setLocationRelativeTo(dialogo.getParent());
            dialogo.setVisible(true);
        }
    }

    public void salir() {
        System.exit(0);
    }
}