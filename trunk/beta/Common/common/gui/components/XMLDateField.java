package common.gui.components;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;

import org.jdom.Document;
import org.jdom.Element;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import common.gui.forms.FinishEvent;
import common.gui.forms.GenericForm;
import common.gui.forms.InitiateFinishListener;
import common.gui.forms.NotFoundComponentException;

/**
 * XMLTextField.java Creado el 13-nov-2006
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
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
 * Esta clase se encarga de generar un objeto JDateChooser a partir de una 
 * parametrizacion <xml/><br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class XMLDateField extends JDateChooser implements AnswerListener, InitiateFinishListener,FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -156496730279770021L;
    private GenericForm GFforma;
    private Vector <String>driverEvent;
    private Vector <String>keySQL;
	private Vector<String> importValue = null;
	private String exportValue = null;
	private JPanel panel;
	private boolean onPanel = true;

    public XMLDateField(GenericForm GFforma, Document doc) {
    	super("yyyy/MM/dd", "####/##/##",'_');
    	this.GFforma=GFforma;
        driverEvent = new Vector<String>();
        keySQL = new Vector<String>();
        Element parameters = doc.getRootElement();
        Iterator i = parameters.getChildren().iterator();

        while (i.hasNext()) {
            Element subargs = (Element) i.next();
            String value = subargs.getValue();
            if ("enabled".equals(subargs.getAttributeValue("attribute"))) {
            	this.setEnabled(Boolean.parseBoolean(value));
            }
            else if ("keySQL".equals(subargs.getAttributeValue("attribute"))) {
            	keySQL.addElement(value); 
            }
            else if ("driverEvent".equals(subargs.getAttributeValue("attribute"))) {
            	String id="";
            	if (subargs.getAttributeValue("id")!= null) {
            		id=subargs.getAttributeValue("id");
            	}
            	if (!driverEvent.contains(value+id))
            		driverEvent.addElement(value+id);
            }
			else if ("importValue".equals(subargs.getAttributeValue("attribute"))) {
				importValue.add(value);
			}
			else if ("exportValue".equals(subargs.getAttributeValue("attribute"))) {
				exportValue = value;
			}
			else if ("panel".equals(subargs.getAttributeValue("panel"))) {
				onPanel = Boolean.parseBoolean(value);
			}
        }
        
        if (onPanel) {
        	panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        	panel.add(this);
        }
        
        JTextFieldDateEditor editor = (JTextFieldDateEditor)this.getComponent(1);
        editor.setColumns(20);
        editor.addFocusListener(this);
        this.GFforma.addInitiateFinishListener(this);
    }
    
    public Component getPanel() {
    	if (onPanel) {
    		return panel;
    	}
    	else {
    		return this;
    	}
    }

    public Element getPackage() {
        Element pack = new Element("package");
        if (!this.getDate().equals("")) {
            Element field = new Element("field");
            field.setText(this.getDate().toString());
            pack.addContent(field);
        }
        return pack;
    }

    public void arriveAnswerEvent(AnswerEvent AEe) {
		if (AEe.getSqlCode().equals(keySQL)) {
			try {
				Document doc = AEe.getDocument();
		        String date = doc.getRootElement().getChild("row").getChildText("col");
		        this.setDateFormatString(date);
		        exportar();
	        }
			catch (NullPointerException NPEe) {
				//this.removeAllItems();
			}
		}
	}

    private void exportar() {
		if (exportValue!=null) {
			try {
				GFforma.setExternalValues(exportValue,this.getDate().toString());
			}
			catch(NullPointerException NPEe) {}
		}
	}

	public void initiateFinishEvent(FinishEvent e) {
		try {
			for (int i=0 ; i < driverEvent.size() ; i++) {
				GFforma.invokeMethod(
						(String)driverEvent.get(i),
						"addAnswerListener",
						new Class[]{AnswerListener.class},new Object[]{this});
			}
		}
		catch(NotFoundComponentException NFCEe) {
			NFCEe.printStackTrace();
		} 
		catch (InvocationTargetException ITEe) {
			ITEe.printStackTrace();
		}
	}

	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent e) {
		exportar();
	}
}
