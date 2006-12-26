package common.gui.components;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import common.gui.forms.GenericForm;
import common.misc.language.Language;

import org.jdom.Document;
import org.jdom.Element;

/**
 * XMLLabel.java Creado el 16-may-2005
 * 
 * Este archivo es parte de JClient
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
 * Componente que representa a un JLabel.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class XMLLabel extends JLabel{

	private static final long serialVersionUID = -3279052585377122227L;
    private JPanel panel;

    public XMLLabel(GenericForm GFforma,Document doc) {
        Iterator parameters = doc.getRootElement().getChildren().iterator();
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        while (parameters.hasNext()) {
            Element e = (Element)parameters.next();
            String name = e.getName();
            if ("arg".equals(name) && e.getAttributeValue("attribute").equals("text")) {
            	String text = Language.getWord(e.getValue());
                this.setText(!text.equals("")?text:e.getValue());
            } else if ("arg".equals(name) && e.getAttributeValue("attribute").equals("alignment")) {
                if (e.getValue().equals("CENTER")) {
                	layout.setAlignment(FlowLayout.CENTER);
                }
                else if (e.getValue().equals("LEFT")) {
                	layout.setAlignment(FlowLayout.LEFT);
                }
                else if (e.getValue().equals("RIGTH")) {
                	layout.setAlignment(FlowLayout.RIGHT);
                }
            }
            else if ("arg".equals(name) && e.getAttributeValue("attribute").equals("font")) {
            	try {
					StringTokenizer STfont = new StringTokenizer(e.getValue(),",");
					Font font = new Font(STfont.nextToken(),
					        		Integer.parseInt(STfont.nextToken()),
					        		Integer.parseInt(STfont.nextToken()));
		                setFont(font);
            	}
                catch(NumberFormatException NFEe) {
                }
                catch(NoSuchElementException NSEEe) {
                }
            }
        }
        panel = new JPanel(layout);
        panel.add(this);
    }

    public Component getPanel() {
        return panel;
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }
}
