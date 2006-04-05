package jmclient.gui.formas;

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import jmclient.miscelanea.JMClientCons;

/**
 * Splash.java Creado el 22-abr-2005
 * 
 * Este archivo es parte de JMServerII
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
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
 * @author <A href='mailto:cristian_david@universia.net.co'>Cristian David Cepeda</A>
 */

public class Splash {

    private static JWindow window;
    
    /**
     * 
     */
    public static void ShowSplash() {
        
        window = new JWindow();
        window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JPanel pe = new JPanel();
        pe.setLayout(new BorderLayout());
        JLabel labelx = new JLabel(new ImageIcon(window.getClass().getResource("/icons/e-maku_splash.png")),JLabel.CENTER);
        pe.add(labelx,BorderLayout.CENTER);
        window.getContentPane().add(pe);
        window.setBounds(((int)JMClientCons.MAX_WIN_SIZE_WIDTH/2)-150,
                		(int)(JMClientCons.MAX_WIN_SIZE_HEIGHT/2)-143,300,341);
        window.setVisible(true);
    }
    
    public static void DisposeSplash() {
        window.dispose();
    }
}
