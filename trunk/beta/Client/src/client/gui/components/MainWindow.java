package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import client.Run;
import client.gui.xmlmenu.MenuLoader;
import client.gui.xmltoolbar.EmakuButtonGroup;
import client.gui.xmltoolbar.ToolBarLoader;
import client.misc.ClientConstants;

import common.misc.CommonConstants;
import common.misc.language.Language;

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
    private static JDesktopPane desktopPane;

    /**
     * Vectores de las barras de herramientas
     */
    private static Vector toolbarVector;
    private static JFrame frame = null;
    
    public MainWindow(final String jarDirectory,String title) {
        setTitle(title);
        MainWindow.frame = this;
        this.setBounds(0, 0, ClientConstants.MAX_WIN_SIZE_WIDTH,
                ClientConstants.MAX_WIN_SIZE_HEIGHT);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        
        /**
         * Creando el panel central
         */

        JPanel JPcentral = new JPanel(new BorderLayout());

        StatusBar statusBar = new StatusBar();
        /**
         * Creando e Instanciando del Menu y la Barras de herramientas 
         */
        Thread t = new Thread () {
        	public void run() {
        		URL url= null;
                MenuLoader menu=null;
                ToolBarLoader toolbar1=null;
                try {
        			url = new URL(jarDirectory+"/menu.xml");
        			menu = new MenuLoader(url);
        			if (menu.loadMenu()) {
        		        menu.setDisabledAll();
        		        frame.setJMenuBar(menu);
        			}
        	        url = new URL(jarDirectory+"/toolbar.xml");
        	        toolbar1 = new ToolBarLoader(url);
        	        toolbarVector = toolbar1.load();
        	        frame.getContentPane().add(toolbar1, BorderLayout.NORTH);
        	        setDisabledAll();
        		} catch (MalformedURLException e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
        	}
        };
        //t.start();
        try {
			SwingUtilities.invokeAndWait(t);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}

        /**
         * Adicionando componentes al panel central
         */

        desktopPane = new JDesktopPane() {
			private static final long serialVersionUID = -2087223016881101556L;
			@Override
			public Component add(Component comp) {
				Component component = super.add(comp);
				if (component instanceof JInternalFrame && 
					!((JInternalFrame)component).isIcon()) {
					component.setLocation(
							(desktopPane.getWidth()/2)-(component.getWidth()/2),
							(desktopPane.getHeight()/2)-(component.getHeight()/2));
				}
				return component;
			}
		};
        JPcentral.add(desktopPane,BorderLayout.CENTER);
        desktopPane.setBackground(Color.LIGHT_GRAY);
        
        
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
                Run.exit();
            }
        });
        /**
         * Inicializando servicio de impresion
         */
        CommonConstants.lookupDefaultPrintService();
        
	    /**
	     * Ventana Visible
	     */
        //this.setVisible(true);
        
        this.setExtendedState(JFrame.NORMAL);
        
   }

    /**
     * Este metodo se encarga de deshabilitar todos los items del Toolbar,
     * exceptuando los que tienn la etiqueta activos
     */

    public void setDisabledAll() {
    	int max = toolbarVector!=null ? toolbarVector.size() : 0;
        for (int i = 0; i < max; i++) {
        	EmakuButtonGroup button = (EmakuButtonGroup) toolbarVector.elementAt(i); 
            if (!button.getActivo()) {
                button.setEnabled(false);
            }
        }
    }

    public static Vector getVtoolbar1() {
        return toolbarVector;
    }
    public static void setToolBarVector(Vector barVector) {
        toolbarVector = barVector;
    }
    public static JLayeredPane getPane() {
        return desktopPane;
    }
    public static int getWidthValue(){
        return desktopPane.getWidth();
    }
    public static int getHeightValue(){
        return desktopPane.getHeight();
    }
    public static JDesktopPane getDesktopPane(){
        return desktopPane;
    }
    
    public static JFrame getFrame() {
    	return frame;
    }
}