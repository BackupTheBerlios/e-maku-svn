package jmclient.gui.formas;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * JMClientSplash.java Creado el 16-oct-2004
 * 
 * Este archivo es parte de JMServerII <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
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
public class JMClientSplash extends JWindow {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 3002744150779988330L;
	private JLayeredPane JLPanel;

    public JMClientSplash(JFrame jf) {
        super(jf);
        JLPanel = new JLayeredPane();

        JLPanel.add(new JLabel(
                new ImageIcon(this.getClass().getResource("/icons/e-maku_splash.png"))),
                JLayeredPane.PALETTE_LAYER);
        JLPanel.add(new JLabel("Etiqueta"),JLayeredPane.MODAL_LAYER);
        JLPanel.setVisible(true);
        this.getContentPane().add(new JPanel().add(JLPanel));
        
        this.setLocation(100,100);
        this.setSize(300,341);
        this.setVisible(true);
    }
}