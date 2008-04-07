package client.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
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
 * This class make and display the splash window, it's contains the progress
 * bar for indicate the load for the program
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

public class Splash {

    private static JWindow		splashWindow= new JWindow();
    private static JProgressBar	progressBar	= new JProgressBar(); 
    
    /**
     * This static method make the GUI components 
     */
    public static void create() {
    	
    	splashWindow.setLayout(new BorderLayout());
        JLayeredPane layeredPane = new JLayeredPane();
        splashWindow.add(layeredPane,BorderLayout.CENTER);
        
        splashWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        URL url = Splash.class.getResource("/icons/e-maku_splash.png");
        System.out.println("url: "+url);
        ImageIcon icon = new ImageIcon(url);
        int width  = icon.getIconWidth();
        int height = icon.getIconHeight();
        
        int x = (ClientConstants.MAX_WIN_SIZE_WIDTH/2)-(width/2);
        int y = (ClientConstants.MAX_WIN_SIZE_HEIGHT/2)-(height/2);
        
        splashWindow.setLocation(x, y);
        splashWindow.setSize(width,height);

        JLabel labelIcon = new JLabel(icon,SwingConstants.CENTER);
        labelIcon.setOpaque(false);
        labelIcon.setBounds(0, 0, width, height);
        
        width  = splashWindow.getHeight()-12;
        height = splashWindow.getWidth()-20;
        
        progressBar.setOpaque(false);
        progressBar.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        progressBar.setBounds(10,width,height,10);
        progressBar.setStringPainted(false);
        progressBar.setIndeterminate(true);
        progressBar.setForeground(new Color(255,162,0));

        layeredPane.add(labelIcon,JLayeredPane.PALETTE_LAYER);
        layeredPane.add(progressBar,JLayeredPane.MODAL_LAYER);
    }
    
    public static void show() {
        splashWindow.setVisible(true);
    }
    
	public static void hide() {
        splashWindow.dispose();
    }
	
    public static void prepareForIncrement() {
    	progressBar.setIndeterminate(false);	
    }
	
    public static synchronized void  incrementProgresValue() {
    	int currentValue = progressBar.getValue();
    	progressBar.setValue(++currentValue);
    	if (currentValue == progressBar.getMaximum()) {
    		Splash.resetProgresValue();
    	}
    }
    
    public static void setProgresRank(int min, int max) {
    	progressBar.setMinimum(min);
    	progressBar.setMaximum(max);
    }
    
    public static void resetProgresValue() {
    	progressBar.setValue(0);
    }   
}