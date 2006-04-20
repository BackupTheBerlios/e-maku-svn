package client.gui.components;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import common.misc.language.Language;

import org.jdom.Element;

/**
 * XMLCheckBox.java Creado el 13-oct-2004
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
public class XMLCheckBox {

    /**
     * 
     */
    private JCheckBox JCBcheck;
    private JPanel JPcheck;

    public XMLCheckBox(String label) {

        JPcheck = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCBcheck=new JCheckBox(Language.getWord(label)) {
			private static final long serialVersionUID = 4280920151276796683L;
			public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        JPcheck.add(JCBcheck);

/*        JCBcheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCBcheck.transferFocus();
            }
        });
*/    }

    public Element getElementCheck() {
        if (isSelected())
            return new Element("field").setText("1");
        else
            return new Element("field").setText("0");
    }

    public String getTextCheck() {
        if (isSelected())
            return "1";
        else
            return "0";
    }

    public JPanel getJPcheck() {
        return JPcheck;
    }
    
    public JCheckBox getJCBcheck() {
        return JCBcheck;
    }
    
    public boolean isSelected() {
        return JCBcheck.isSelected();
    }

    public void setSelected(boolean b) {
        JCBcheck.setSelected(b);
    }

    public void addJPanel(Component comp) {
        JPcheck.add(comp);
    }
    
    public void setEnabled(boolean b) {
        JCBcheck.setEnabled(b);
    }
}
