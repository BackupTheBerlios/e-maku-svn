package client.gui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * HTMLdoc.java Creado el 22-abr-2005
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
public class HTMLViewer extends JScrollPane implements HyperlinkListener  {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1558803372358974284L;
    private JEditorPane JEPhtml;
    private JViewport JVport;
    /**
     * 
     */
    public HTMLViewer(URL URLtext) {
        
        JEPhtml = new JEditorPane(){
			private static final long serialVersionUID = 7074280906269929326L;
			public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        try {
            JEPhtml.setPage(URLtext);
        }
        catch (IOException IOEe) {
            IOEe.printStackTrace();
        }
            //creamos el browser html cargando el texto principal
        JEPhtml.addHyperlinkListener(this);
        JEPhtml.setEditable(false);
        
        //Creamos un scrooll para podernos desplazar en el texto
        
        JVport = this.getViewport();
        
        JVport.add(JEPhtml);
       
        
        // TODO Auto-generated constructor stub
    }
    /* (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        // TODO Auto-generated method stub
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                JEPhtml.setPage(e.getURL());
            }
            catch (IOException IOEe) {
                IOEe.printStackTrace();
            }
        }
    }

}
