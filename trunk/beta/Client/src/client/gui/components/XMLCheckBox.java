package client.gui.components;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jdom.Document;
import org.jdom.Element;

import common.gui.components.AnswerEvent;
import common.gui.components.AnswerListener;
import common.gui.components.VoidPackageException;
import common.gui.forms.FinishEvent;
import common.gui.forms.GenericForm;
import common.gui.forms.InitiateFinishListener;
import common.gui.forms.NotFoundComponentException;
import common.misc.language.Language;

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
public class XMLCheckBox extends JCheckBox implements ActionListener, AnswerListener, InitiateFinishListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2513833821051059850L;
	/**
     * 
     */
    private GenericForm GFforma;
    private JPanel JPcheck;
    private String driverEvent;
    private String keySQL;
	private String exportValue = null;

    public XMLCheckBox(GenericForm GFforma, Document doc) {
    	this.GFforma = GFforma;
        keySQL = new String();
        int orden = -1;
        
        Element parameters = doc.getRootElement();
        Iterator i = parameters.getChildren().iterator();
        
        while (i.hasNext()) {
        	
            Element args = (Element) i.next();
            String name = args.getName();
            if (name.equals("subarg")) {
            	Iterator j = args.getChildren().iterator();
            	while (j.hasNext()) {
            		Element subargs = (Element)j.next();
            		String value = subargs.getValue(); 
		            if ("enabled".equals(subargs.getAttributeValue("attribute"))) {
		            	this.setEnabled(Boolean.parseBoolean(value));
		            }
		            else if ("label".equals(subargs.getAttributeValue("attribute"))) {
		            	if (!Language.getWord(value).equals("")) {
		            		this.setText(Language.getWord(value));
		            	}
		            	else {
		            		this.setText(value);
		            	}
		            }
		            else if ("keySQL".equals(subargs.getAttributeValue("attribute"))) {
		            	keySQL = value; 
		            }
		            else if ("driverEvent".equals(subargs.getAttributeValue("attribute"))) {
		         		String id="";
		         		if (subargs.getAttributeValue("id")!= null) {
		         			id=subargs.getAttributeValue("id");
		            		}
		                driverEvent = value+id;
		            }
					else if ("exportValue".equals(subargs.getAttributeValue("attribute"))) {
						exportValue = value;
					}
		         	else if ("exportField".equals(subargs.getAttributeValue("attribute"))) {
		         		try {
		         			orden = Integer.parseInt(value);
		         		}
		         		catch (NumberFormatException NFEe) {
		         		}
		         	}
		            
            	}
            }
        }
		if (orden > -1 && exportValue!=null) {
			GFforma.addExportField(orden,exportValue);
			GFforma.setExternalValues(exportValue,"0");
		}

		JPcheck = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPcheck.add(this);
        this.addActionListener(this);
        this.GFforma.addInitiateFinishListener(this);
    }

    public XMLCheckBox(String label) {

        super(Language.getWord(label));
        JPcheck = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPcheck.add(this);

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

    public Component getPanel() {
        return JPcheck;
    }
    
    public JPanel getJPcheck() {
        return JPcheck;
    }
    
    public JCheckBox getJCBcheck() {
        return this;
    }
    
    public void addJPanel(Component comp) {
        JPcheck.add(comp);
    }
    
	public void arriveAnswerEvent(AnswerEvent AEe) {
		if (AEe.getSqlCode().equals(keySQL)) {
			try {
				Document doc = AEe.getDocument();
		        String selected = doc.getRootElement().getChild("row").getChildText("col");
		        if (selected.toLowerCase().equals("true") || 
	        		selected.toLowerCase().equals("t") ||
	        		selected.equals("1")) {
		        	this.setSelected(true);
		        }
		        else {
		        	this.setSelected(false);
		        }
	        }
			catch (NullPointerException NPEe) {
				this.setSelected(false);
			}
		}
		
	}

	public void initiateFinishEvent(FinishEvent e) {
		try {
			if (driverEvent!=null) {
			   GFforma.invokeMethod(driverEvent,"addAnswerListener",new Class[]{AnswerListener.class},new Object[]{this});
			}
		}
		catch(NotFoundComponentException NFCEe) {
			NFCEe.printStackTrace();
		} 
		catch (InvocationTargetException ITEe) {
			ITEe.printStackTrace();
		}
		
	}
	
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }

	public void actionPerformed(ActionEvent e) {
		if (exportValue!=null) {
			GFforma.setExternalValues(exportValue,getTextCheck());
		}
		
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
	
    public void clean() {
    	this.setSelected(false);
    }
    
    /**
     * Metodo encargado de retornar un <package/>
     * @return
     * @throws VoidPackageException
     */
    public Element getPackage() throws VoidPackageException {
        Element pack = new Element("package");
        if (isSelected()) {
        	pack.addContent(new Element("field").setText("1"));
        }
        else {
        	pack.addContent(new Element("field").setText("0"));
        }
    	return pack;
    }

	
}
