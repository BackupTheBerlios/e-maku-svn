package client.gui.forms;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import client.misc.ClientConstants;

/**
 * Splash.java Creado el 22-abr-2005
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

public class Splash {

    private static JWindow window;
    public static JProgressBar JPBbarra = new JProgressBar(); 
    
    /**
     * 
     */
    public static void ShowSplash() {
        
        window = new JWindow();
        window.setAlwaysOnTop(false);
        window.setLayout(new BorderLayout());
        window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JPanel pe = new JPanel();
        pe.setLayout(new BorderLayout());
        URL url = window.getClass().getResource("/icons/e-maku_splash.png");
        ImageIcon icon = new ImageIcon(url);
        JLabel labelx = new JLabel(icon,JLabel.CENTER);
        pe.add(labelx,BorderLayout.CENTER);
        window.add(pe,BorderLayout.CENTER);
        window.add(JPBbarra,BorderLayout.SOUTH);
        JPBbarra.setFont(new Font("Monospace",Font.PLAIN,10));
        
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        int x = ((int)ClientConstants.MAX_WIN_SIZE_WIDTH/2)-(width/2);
        int y = ((int)ClientConstants.MAX_WIN_SIZE_HEIGHT/2)-(height/2);
        window.setLocationByPlatform(true);
        window.setLocation(x, y);
        window.setSize(width,height);
        window.setVisible(true);
    }
    
    public static void DisposeSplash() {
        window.dispose();
    }
    
    public static void  incrementProgresValue() {
    	Thread t = new Thread() {
    		public void run() {
    			JPBbarra.setValue(JPBbarra.getValue()+1);
    	    	if (JPBbarra.getValue() == JPBbarra.getMaximum()) {
    	    		resetProgresValue();
    	    		JPBbarra.setStringPainted(false);
    	    	}
    		}
    	};
    	t.start();
    } 
    
    public static void setProgresRank(int min, int max) {
        JPBbarra.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    	JPBbarra.setMinimum(min);
    	JPBbarra.setMaximum(max);
    	JPBbarra.setStringPainted(true);
    }
    
    public static void resetProgresValue() {
    	JPBbarra.setValue(0);
    }
    
}

