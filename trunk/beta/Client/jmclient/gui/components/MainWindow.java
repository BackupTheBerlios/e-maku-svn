package jmclient.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import jmclient.Run;
import jmclient.gui.formas.Splash;
import jmclient.gui.menuXML.LoadMenu;
import jmclient.gui.toolbarXML.JButtonXML;
import jmclient.gui.toolbarXML.LoadToolbar;
import jmclient.miscelanea.JMClientCons;
import jmlib.miscelanea.idiom.Language;

/**
 * MainWindow.java Creado el 03-ago-2004
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

public class MainWindow extends JFrame {


	private static final long serialVersionUID = -5660450407954687999L;
	/**
     * Objeto para pocicion de JInternalFrames
     */
    private static JDesktopPane JDPpanel;

    /**
     * Vectores de las barras de herramientas
     */
    private static Vector Vtoolbar1;
    private static JFrame refWindow = null;
    
    public MainWindow() {
        
        super("E-Maku - Cliente");

        this.setBounds(0, 0, JMClientCons.MAX_WIN_SIZE_WIDTH,
                JMClientCons.MAX_WIN_SIZE_HEIGHT);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        /**
         * Creando el panel central
         */

        JPanel JPcentral = new JPanel(new BorderLayout());

        StatusBar statusBar = new StatusBar();
        /**
         * Creando e Instanciando del Menu y la Barras de herramientas 
         */

        LoadMenu menu = 
            new LoadMenu(this.getClass().getResource("/jmclient/xml-midas/menu.xml"));
        
        menu.Loading();
        menu.setDisabledAll();

        LoadToolbar toolbar1 = new LoadToolbar(this.getClass().getResource(
                "/jmclient/xml-midas/toolbar.xml"));

        Vtoolbar1 = toolbar1.Loading();
        setDisabledAll();

        /**
         * Adicionando componentes al panel central
         */

        JDPpanel = new JDesktopPane() {
			private static final long serialVersionUID = -2087223016881101556L;
			@Override
			public Component add(Component comp) {
				Component component = super.add(comp);
				if (component instanceof JInternalFrame && !((JInternalFrame)component).isIcon()) {
					component.setLocation(
							(JDPpanel.getWidth()/2)-(component.getWidth()/2),
							(JDPpanel.getHeight()/2)-(component.getHeight()/2));
				}
				return component;
			}
		};
        JPcentral.add(JDPpanel,BorderLayout.CENTER);
        JDPpanel.setForeground(Color.darkGray);

        this.setJMenuBar(menu);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(toolbar1, BorderLayout.NORTH);
        this.getContentPane().add(JPcentral, BorderLayout.CENTER);
        this.getContentPane().add(statusBar, BorderLayout.SOUTH);

        /**
         * Barra de Estado
         */
        
        StatusBar.setLabel1(Language.getWord("ESTADO"));
        StatusBar.setLabel2(Language.getWord("ON_LINE"));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Run.salir();
            }
        });

	    /**
	     * Ventana Visible
	     */
        this.setVisible(true);
        Splash.DisposeSplash();
        this.setExtendedState(JFrame.NORMAL);
        MainWindow.refWindow = this;
   }

    /**
     * Este metodo se encarga de deshabilitar todos los items del Toolbar,
     * exceptuando los que tienn la etiqueta activos
     */

    public void setDisabledAll() {
        for (int i = 0; i < Vtoolbar1.size(); i++) {
            if (!((JButtonXML) Vtoolbar1.elementAt(i)).getActivo())
                ((JButtonXML) Vtoolbar1.elementAt(i)).setEnabled(false);
        }
    }

    public static Vector getVtoolbar1() {
        return Vtoolbar1;
    }
    public static void setVtoolbar1(Vector vtoolbar1) {
        Vtoolbar1 = vtoolbar1;
    }
    public static JLayeredPane getJDPpanel() {
        return JDPpanel;
    }
    public static int getAncho(){
        return JDPpanel.getWidth();
    }
    public static int getAlto(){
        return JDPpanel.getHeight();
    }
    public static JDesktopPane getJDPanel(){
        return JDPpanel;
    }
    
    public static JFrame getRefWindow() {
    	return refWindow;
    }
}