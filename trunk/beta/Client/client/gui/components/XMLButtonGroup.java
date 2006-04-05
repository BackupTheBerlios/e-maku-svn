package client.gui.components;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import org.jdom.Element;

/**
 * XMLButtonGroup.java Creado el 22-oct-2004
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
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class XMLButtonGroup extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5014998091273730395L;
	/**
     * 
     */
    private String[] labels = null;
    private String defaultlabel = null;
    private String selected = "";
    
    private ButtonGroup BGgroup;
    private Vector<IDRadioButton> Vradio;
    
    public XMLButtonGroup(String [] labels,int rows,int cols) {
        super(new GridLayout(rows,cols));
        this.labels=labels;
        generar();
    }

    public XMLButtonGroup(String [] labels,String defaultlabel,int rows,int cols) {
        super(new GridLayout(rows,cols));
        this.labels=labels;
        this.defaultlabel=defaultlabel;
        this.selected=defaultlabel;
        generar();
    }

    private void generar() {
        Vradio = new Vector<IDRadioButton>();
        BGgroup = new ButtonGroup();
        
        for (int i=0;i<labels.length;i++) {
            IDRadioButton IDradio = new IDRadioButton(labels[i]);
            IDradio.addActionListener(this);
            if (labels[i].equals(defaultlabel))
                IDradio.getRadio().setSelected(true);
            
            Vradio.add(IDradio);
            BGgroup.add(IDradio.getRadio());
            this.add(new JPanel().add(IDradio.getRadio()));
        }
        
    }

    public void setSelected(String label) {
        for (int i=0;i<Vradio.size();i++) {
            if (Vradio.get(i).getId().equals(label)) {
                Vradio.get(i).setSelected(true);
                this.selected=label;
            }
        }
    }
    
    public Element getElementText() {
        return new Element("field").setText(selected);
    }

    public Vector getVradio() {
        return Vradio;
    }
    
    public String getText() {
        return selected;
    }

    public JPanel getJPgroup() {
        return this;
    }
    
    public void actionPerformed(ActionEvent e) {
        selected(e);
    }
    
    public void selected(ActionEvent e) {
        selected = ((IDRadioButton)e.getSource()).getId();
    }
}
