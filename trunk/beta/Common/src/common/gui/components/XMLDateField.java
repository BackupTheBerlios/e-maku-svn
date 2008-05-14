package common.gui.components;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdom.Document;
import org.jdom.Element;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import common.gui.forms.EndEventGenerator;
import common.gui.forms.GenericForm;
import common.gui.forms.InstanceFinishingListener;
import common.gui.forms.NotFoundComponentException;
import common.transactions.TransactionServerException;
import common.transactions.TransactionServerResultSet;

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

public class XMLDateField extends JDateChooser 
implements Couplable,KeyListener, DocumentListener, AnswerListener, InstanceFinishingListener,FocusListener {

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
	private String dateFormatString="yyyy/MM/dd HH:mm:ss";
	private JTextFieldDateEditor editor;
	private boolean onPanel = true;
	private Vector<String> sqlCode = null;
	private Vector<String> constantValue = null;
	private Vector<AnswerListener> answerListener = new Vector<AnswerListener>();
	
    public XMLDateField(GenericForm GFforma, Document doc) {
    	super("yyyy/MM/dd HH:mm:ss", "####/##/## ##:##:##",'_');
    	this.GFforma=GFforma;
        driverEvent = new Vector<String>();
        keySQL = new Vector<String>();
        importValue = new Vector<String>();
        sqlCode = new Vector<String>();
        Element parameters = doc.getRootElement();
        Iterator i = parameters.getChildren().iterator();
        
        while (i.hasNext()) {
            Element subargs = (Element) i.next();
            String value = subargs.getValue();
            if ("dateFormatString".equals(subargs.getAttributeValue("attribute"))) {
            	dateFormatString=value;
            }
            else if ("maskPattern".equals(subargs.getAttributeValue("attribute"))) {
            
            }
            else if ("placeholder".equals(subargs.getAttributeValue("attribute"))) {
            	
            }
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
			else if ("sqlCode".equals(subargs.getAttributeValue("attribute"))) {
				this.sqlCode.add(subargs.getValue());
			} 
			else if ("constantValue".equals(subargs.getAttributeValue("attribute"))) {
				if (constantValue == null) {
					constantValue = new Vector<String>();
				}
				constantValue.addElement(subargs.getValue());
			}
        }
        
        if (onPanel) {
        	panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        	panel.add(this);
        }
        editor = (JTextFieldDateEditor)this.getComponent(1);
        editor.getDocument().addDocumentListener(this);
        editor.setColumns(20);
        editor.addFocusListener(this);
        editor.addKeyListener(this);
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

    public Element getPrintPackage() {
    	Element pack = new Element("package");
        if (!this.getDate().equals("")) {
            Element field = new Element("field");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            field.setText(sdf.format(this.getDate()));
            pack.addContent(field);
        }
        return pack;
    }
    
    public Element getPackage() throws VoidPackageException {
        Element pack = new Element("package");
        if (this.getDate()!=null && !this.getDate().equals("")) {
            Element field = new Element("field");
            SimpleDateFormat sdf = new SimpleDateFormat(this.getDateFormatString());
            field.setText(sdf.format(this.getDate()));
            pack.addContent(field);
        }
        return pack;
    }

    public Element getOnlyDatePackage() throws VoidPackageException {
        Element pack = new Element("package");
        if (this.getDate()!=null && !this.getDate().equals("")) {
            Element field = new Element("field");
            SimpleDateFormat sdf = new SimpleDateFormat(this.getDateFormatString());
            field.setText(sdf.format(this.getDate()).substring(0,11));
            pack.addContent(field);
        }
        return pack;
    }

    public void arriveAnswerEvent(AnswerEvent AEe) {
    	DateFormat  df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	String date = "";
    	try {
			Document doc = AEe.getDocument();
	        date = doc.getRootElement().getChild("row").getChildText("col");
	        this.setDate(df.parse(date));
	        exportar();
        }
		catch (NullPointerException NPEe) {
			//this.removeAllItems();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    

    private void exportar() {
		if (exportValue!=null) {
			try {
	            SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);
				GFforma.setExternalValues(exportValue,sdf.format(this.getDate()));
			}
			catch(NullPointerException NPEe) {}
		}
	}

	public void initiateFinishEvent(EndEventGenerator e) {
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
		editor.setText("");
	}

	public void focusLost(FocusEvent e) {
		exportar();
		if (sqlCode.size()>0) {
			searchOthersSqls();
		}
	}

	/**
	 * Metodo encargado de generar sentencias sql de otros componentes
	 */
	private void searchOthersSqls() {
		class SearchingSQL extends Thread {

			private String[] args;

			public SearchingSQL(String[] args) {
				this.args = args;
			}

			public void run() {

				String sql;
				for (int i = 0; i < sqlCode.size(); i++) {
					Document doc = null;
					sql = (String) sqlCode.get(i);
					try {
						doc = TransactionServerResultSet.getResultSetST(sql, args);
					} catch (TransactionServerException e) {
						e.printStackTrace();
					}
					AnswerEvent event = new AnswerEvent(this, sql, doc);
					notificando(event);
				}
			}
		}
		/*-----------------------------------------------------------*/
		int sizeConstantValue = 0;
		if (constantValue!= null) {
			sizeConstantValue = constantValue.size();
		}
		String[] argumentos = new String[importValue.size()
				+ sizeConstantValue + 1];
		
		String[] XMLimpValues = importValue.toArray(new String[importValue.size()]);

		int i = 0;
		for (i = 0; i < sizeConstantValue; i++) {
			argumentos[i] = constantValue.get(i);
		}

		for (; i < importValue.size(); i++) {
			argumentos[i] = GFforma.getExternalValueString(XMLimpValues[i]);
		}
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);

        if (this.getDate()!=null) {
			argumentos[i] = sdf.format(this.getDate());
			new SearchingSQL(argumentos).start();
        }
	}
	
	public void addAnswerListener(AnswerListener listener) {
		answerListener.addElement(listener);
	}

	public void removeAnswerListener(AnswerListener listener) {
		answerListener.removeElement(listener);
	}
	
	private void notificando(AnswerEvent event) {
		for(AnswerListener l:answerListener) {
			if (l.containSqlCode(event.getSqlCode())) {
				l.arriveAnswerEvent(event);
			}
		}
	}
	
	public void insertUpdate(DocumentEvent e) {
		exportar();
		if (sqlCode.size()>0) {
			searchOthersSqls();
		}

	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if ( (keyCode==KeyEvent.VK_ENTER) || (keyCode==KeyEvent.VK_TAB)) {
			editor.transferFocus();
		}
	}
	
	public void focusGained(FocusEvent e) {}
	
	public void removeUpdate(DocumentEvent e) {}

	public void keyPressed(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}

	public void changedUpdate(DocumentEvent e) {}

	public boolean containSqlCode(String sqlCode) {
		if (keySQL.contains(sqlCode))
			return true;
		else
			return false;
	}

	
	public Element getPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void validPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
