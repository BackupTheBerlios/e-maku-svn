package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdom.Document;
import org.jdom.Element;

import common.gui.components.VoidPackageException;
import common.gui.forms.GenericForm;
import common.misc.language.Language;

/**
 * XMLComboBox.java Creado el 18-sep-2004
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase fue reescrita total mente el 23 de Noviembre de 2005, se encarga 
 * de generar un componente XMLComboBox, para ser parametrizado en la construccion
 * de una transaccion, o utilizada desde un componente.
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class XMLComboBox extends SQLComboBox {

	private static final long serialVersionUID = -6189985727776478913L;

	private JLabel JLlabel;
    private JPanel panel;
    private JPanel jpCombo;
    
    /**
     * Constructor encargado de generar un combo apartir de una parametrizacion
     * recibida en </xml>
     * @param GFforma
     * @param doc
     */
    
    public XMLComboBox(GenericForm GFforma,Document doc) {
    	super(GFforma,doc);
    	construir();
    }
   
    /**
     * Constructor encargado de generar un combo apartir de una parametrizacion
     * recibida desde un componente, entre sus parametros estan el codigo de 
     * la consulta a generar y la etiqueta.
     * @param GFforma
     * @param sqlCombo
     * @param Label
     */
    public XMLComboBox(GenericForm GFforma,String sqlCombo,String Label) {
    	super(GFforma,sqlCombo);
    	setLabelName(Label);
		construir();
	}
    
    /**
     * Constructor encargado de generar un combo apartir de una paremetrizacion 
     * recibida desde un componente, entre sus parametros entan el codigo de
     * la consulta a generar, la etiqueta y una bandera de llaves :S
     * @param GFforma
     * @param sqlCombo
     * @param Label
     * @param saveKey
     */
    
    public XMLComboBox(GenericForm GFforma,String sqlCombo,String Label,boolean saveKey) {
    	super(GFforma,sqlCombo,saveKey);
    	setLabelName(Label);
		construir();
	}
    
    /**
     * Constructor encargado de generar un combo apartir del codigo de la consulta xml y 
     * sus argumetos.
     * @param GFforma
     * @param sqlCombo
     * @param args
     */
	public XMLComboBox(GenericForm GFforma,String sqlCombo, String[] args) {
		super(GFforma,sqlCombo,args);
		construir();
	}
	
	/**
	 * Este metodo se encarga de construir el XMLComboBox.
	 */
	
	private void construir(){
		panel = new JPanel(new BorderLayout());
    	jpCombo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	jpCombo.add(this);
    	panel.add(jpCombo,BorderLayout.CENTER); 
    	
    	String text = Language.getWord(getLabelName());
    	JLlabel = new JLabel(!text.equals("")?text:getLabelName()) {
			private static final long serialVersionUID = 1326711205695035680L;
			public void paintComponent(Graphics g) {
    	        Graphics2D g2 = (Graphics2D)g;
    	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    	                            RenderingHints.VALUE_ANTIALIAS_ON);
    	        super.paintComponent(g);
    	    }
    	};
	    if (!"".equals(getLabel().getText())) {
	    	panel.add(JLlabel,BorderLayout.WEST);
    	}
	}
    
	/**
	 * Este metodo retorna un JPanel, sobre el que esta el Combo
	 * @return
	 */
	public JPanel getJPcombo() {
    	return jpCombo; 
    }
    
	/**
	 * Metodo encargado de retornar el panel sobre el que esta el combo y la 
	 * etiqueta.
	 * @return
	 */
    public Component getPanel() {
    	if (isWhitPanel())
    		return (Component)panel;
    	return (Component)this;
    }
    
    /**
     * Metodo encargado de retornar un <package/>
     * @return
     * @throws VoidPackageException
     */
    public Element getPackage() throws VoidPackageException {
        Element pack = new Element("package");
        pack.addContent(getElementCombo());
    	return pack;
    }
    
    public Element getPrintPackage() throws VoidPackageException {
        Element pack = new Element("package");
        Element field = new Element("field");
        field.setText(getSelectedItem().toString());
        pack.addContent(field);
    	return pack;
    }

    /**
     * Metodo encargado de limpiar el combo
     *
     */
    public void clean() {
    	if (isClean())
    		removeAllItems();
    	else {
    		setSelectedIndex(getSelectedIndex());
    		exportar();
    	}
    }

    /**
     * Metodo encargado de retornar la JLabel de XMLComboBox
     * @return
     */
    
    public JLabel getLabel() {
    	return JLlabel;
    }


    /**
     * Metodo encargado de retornar un <field/>
     * @return
     */
    
    public Element getElementCombo() throws VoidPackageException {
    	Element element;
    	if (getKey()!=null) {
    		element = new Element("field").setAttribute("attribute",getKey());
    	}
    	else {
    		element = new Element("field");
    	}
    	if (getNameField()!=null) {
    		element.setAttribute("name",getNameField());
    	}
    	String text = String.valueOf(getItemAt(getSelectedIndex()));
    	if ("".equals(text.trim()) && !isReturnNullValue()) {
    		throw new VoidPackageException(getNameCombo());
    	}
    	element.setText(text);
    	return element;
    }
    
    public Element getNullValue() {
    	Element elm = new Element("field");
    	elm.setAttribute("attribute","NULL");
    	return elm;
    	
    }
    public String getStringCombo() {
    	return (String) getItemAt(getSelectedIndex());
    }

	public void addPanel(Component component) {
		
	}
	
	public boolean containData() {
		try {
			Element elm = getPackage();
			if (elm.getChildren().size() > 0) {
				return true;
			}
		} catch (VoidPackageException e) {
			return false;
		}
		return false;
	}
}