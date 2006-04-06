package client.gui.formas;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import client.gui.components.HTMLdoc;
import client.gui.components.MainWindow;
import common.miscelanea.idiom.Language;

/**
 * About.java Creado el 22-abr-2005
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
public class About extends JInternalFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3286345655294599519L;
	private JTabbedPane JTPpestanas;
    int ancho_imagen; 
    int alto_imagen;
    
    /**
     * 
     */
    
    public About() {
        this.setTitle(Language.getWord("ABOUT"));
        this.setVisible(true);
        
        
        this.setBounds((MainWindow.getAncho()/2)-200,(MainWindow.getAlto()/2)-210,420,410);
        this.setFrameIcon(new ImageIcon(this.getClass().getResource("/icons/ico_acerca.png")));
        this.setClosable(true);
        JTPpestanas = new JTabbedPane();
        JTPpestanas.addTab(Language.getWord("ABOUT"),new PanelAbout());
        JTPpestanas.addTab(Language.getWord("LICENSE"),new HTMLdoc(this.getClass().getResource("/LICENSE-GNU")));
        JTPpestanas.addTab(Language.getWord("CREDITS"),new HTMLdoc(this.getClass().getResource("/STAFF-MIDAS")));

        this.getContentPane().add(JTPpestanas,BorderLayout.CENTER);

        MainWindow.getJDPanel().add(this);
        try {
            this.setSelected(true);
        }
        catch (java.beans.PropertyVetoException PVEe) {
        }

 
    }

}

class PanelAbout extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8498056921689027057L;
	private JLabel JLacercade;
    
    PanelAbout() {
        try {
            ImageIcon icon = new ImageIcon(this.getClass().getResource("/icons/e-maku_splash.png"));
            JLacercade = new JLabel(icon);
            JLacercade.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
            this.setBackground(Color.white);
        }
        catch(NullPointerException NPEe) {
            System.out.println(NPEe.getMessage());
        }
        this.add(JLacercade);
     }
}