package client.gui.components;

/**
 * SQLComboBox.java Creado el 25-Nov-2005
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
 * Esta clase es una extencion de JComboBox, su funcionalidad adicional es para
 * permitir interaccion con consultas recibidas desde el Servidor de Transacciones,
 * 
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda </A>
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import common.gui.components.AnswerEvent;
import common.gui.components.AnswerListener;
import common.gui.formas.ChangeExternalValueEvent;
import common.gui.formas.ChangeExternalValueListener;
import common.gui.formas.FinishEvent;
import common.gui.formas.GenericForm;
import common.gui.formas.InitiateFinishListener;
import common.gui.formas.NotFoundComponentException;
import common.miscelanea.idiom.Language;
import common.transactions.STException;
import common.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

public class SQLComboBox extends JComboBox implements
		ChangeExternalValueListener,PopupMenuListener, AnswerListener , InitiateFinishListener{

	private static final long serialVersionUID = -1928808717779731109L;
	private String sqlCombo;
	private ArrayList<String> sqlCode;
	private Vector<String> importValue = null;
	private Vector<String> importValueCode = null;
	private String exportValue = null;
	private String[] keys;
	private GenericForm GFforma;
	private String labelName="";
	private int selected;
	private String key;
	private Vector<String> keysCombo = new Vector<String>();

	private boolean saveKey = false;
	private boolean clean = false;
	private String nameField = null;
	private String driverEvent;
	private String keySQL;
	private boolean whitPanel = true;
	private int preferredLength =0;
	private boolean returnNullValue = false;
	private String name = "";
    private Vector<AnswerListener> AnswerListener = new Vector<AnswerListener>();
    
	/**
	 * Este constructor es invocado desde un componente
	 * @param GFforma
	 * @param sqlCombo
	 * @param saveKey
	 */
	
	public SQLComboBox(GenericForm GFforma,String sqlCombo,boolean saveKey) {
		super();
		this.sqlCombo = sqlCombo;
		this.GFforma = GFforma;
		this.saveKey=saveKey;
        this.GFforma.addChangeExternalValueListener(this);
    		this.addPopupMenuListener(this);
        generar();
	}
	
	/**
	 * Este constructor es invocado desde un componente
	 * @param GFforma
	 * @param sqlCombo
	 */
	
	public SQLComboBox(GenericForm GFforma,String sqlCombo) {
		super();
		this.sqlCombo = sqlCombo;
		this.GFforma = GFforma;
        this.GFforma.addChangeExternalValueListener(this);
    		this.addPopupMenuListener(this);
        generar();
	}

	/**
	 * Este constructor parametriza el una consulta y el valor exportable por su seleccion 
	 * @param GFforma
	 * @param sqlCombo
	 * @param exportValue
	 */
	
	public SQLComboBox(GenericForm GFforma,String sqlCombo,String exportValue) {
		super();
		this.sqlCombo = sqlCombo;
		this.GFforma = GFforma;
		this.exportValue=exportValue;
        this.GFforma.addChangeExternalValueListener(this);
    		this.addPopupMenuListener(this);
        generar();
	}

	/**
	 * Este constructor parametriza una consulta con argumentos
	 * @param GFforma
	 * @param sqlCombo
	 * @param args
	 */
	
	public SQLComboBox(GenericForm GFforma,String sqlCombo, String[] args) {
		super();
		this.sqlCombo = sqlCombo;
		this.keys = args;
		this.GFforma = GFforma;
        this.GFforma.addChangeExternalValueListener(this);
    		this.addPopupMenuListener(this);
        generar();
	}

	/**
	 * Este constructor parametriza una consulta con argumentos y exporta su valor por seleccion
	 * @param GFforma
	 * @param sqlCombo
	 * @param args
	 * @param exportValue
	 */
	public SQLComboBox(GenericForm GFforma,String sqlCombo, String[] args,String exportValue) {
		super();
		this.sqlCombo = sqlCombo;
		this.keys = args;
		this.GFforma = GFforma;
		this.exportValue=exportValue;
        this.GFforma.addChangeExternalValueListener(this);
    		this.addPopupMenuListener(this);
        generar();
	}

	/**
	 * Este constructor lee una parametrizacion, cuando XMLComboBox es generado desde codigo <xml/>
	 * @param GFforma
	 * @param doc
	 */
	
	public SQLComboBox(GenericForm GFforma, Document doc) {
		super();
		this.GFforma = GFforma;
		this.GFforma.addChangeExternalValueListener(this);
		Iterator i = doc.getRootElement().getChildren().iterator();
		int orden = -1;
		importValue = new Vector<String>();
		importValueCode = new Vector<String>();
		sqlCode = new ArrayList<String>();
		
		while(i.hasNext()) {
			Element element = (Element) i.next();
			String value = element.getValue();
			if ("sqlCombo".equals(element.getAttributeValue("attribute"))) {
				sqlCombo = value;
			}
			if ("sqlCode".equals(element.getAttributeValue("attribute"))) {
				sqlCode.add(value);
			}
			else if ("label".equals(element.getAttributeValue("attribute"))) {
				labelName = value;
			}
			else if ("name".equals(element.getAttributeValue("attribute"))) {
				String text = Language.getWord(value);
				name = !"".equals(text)?text:value;
			}
			else if ("Panel".equals(element.getAttributeValue("attribute"))) {
				whitPanel  = Boolean.parseBoolean(value);
			}
			else if ("selectedIndex".equals(element.getAttributeValue("attribute"))) {
				selected = Integer.parseInt(value);
			}
			else if ("importValue".equals(element.getAttributeValue("attribute"))) {
				importValue.add(value);
			}
			else if ("importValueCode".equals(element.getAttributeValue("attribute"))) {
				importValueCode.add(value);
			}
			else if ("exportValue".equals(element.getAttributeValue("attribute"))) {
				exportValue = value;
			}
			else if ("addAttribute".equals(element.getAttributeValue("attribute"))) {
				key = value;
			}
			else if ("clean".equals(element.getAttributeValue("attribute"))) {
				clean = Boolean.parseBoolean(value);
			}
         	else if ("exportField".equals(element.getAttributeValue("attribute"))) {
         		try {
         			orden = Integer.parseInt(value);
         		}
         		catch (NumberFormatException NFEe) {
         		}
         	}
         	else if ("nameField".equals(element.getAttributeValue("attribute"))) {
                 nameField = value;
         	}
         	else if ("enabled".equals(element.getAttributeValue("attribute"))) {
                this.setEnabled(Boolean.parseBoolean(value));
         	}
         	else if ("driverEvent".equals(element.getAttributeValue("attribute"))) {
         		String id="";
         		if (element.getAttributeValue("id")!= null) {
         			id=element.getAttributeValue("id");
            		}
                driverEvent = value+id;
         	}
         	else if ("keySQL".equals(element.getAttributeValue("attribute"))) {
                keySQL = value;
         	}
         	else if ("saveKey".equals(element.getAttributeValue("attribute"))) {
                saveKey = Boolean.parseBoolean(value);
         	}
         	else if ("preferredLength".equals(element.getAttributeValue("attribute"))) {
                preferredLength = Integer.parseInt(value);
         	}
         	else if ("nullValue".equals(element.getAttributeValue("attribute"))) {
                returnNullValue = Boolean.parseBoolean(value);
         	}
		}
		if (orden > -1 && exportValue!=null) {
			GFforma.addExportField(orden,exportValue);
		}
		if (importValue.size()>0) {
			keys= new String[importValue.size()];
			for (int j = 0 ; j < keys.length ; j++ ) {
				keys[j] = importValue.get(j);
			}
		}
			
	    	this.addPopupMenuListener(this);
	    	this.GFforma.addInitiateFinishListener(this);
		generar();
	}

	/**
	 * Este metodo construye un combo, apartir de su parametrizacion
	 *
	 */
	private void generar() {
		class buildCombo extends Thread {
			public void run() {
				try {
					Document doc = null;
					String [] args = null;
					if (keys!=null) {
						args= new String[keys.length];
					
						for (int i = 0 ; i < args.length ; i++ ) {
							args[i] = GFforma.getExteralValuesString(keys[i]);
						}
					}
					
					doc = STResultSet.getResultSetST(sqlCombo, args);
					Iterator i = doc.getRootElement().getChildren().iterator();
					CargarCombo(i);
					setSelectedIndex(selected);
					exportar();
				} catch (STException e) {
					e.printStackTrace();
				} catch (IndexOutOfBoundsException IOBEe) {
					setSelectedIndex(0);
				}
			}

		}
		new buildCombo().start();
	}

	/**
	 * Este metodo se encarga de cargar la informacion obtenida de una consulta
	 * @param i
	 */
	
	private void CargarCombo(Iterator i) {
		this.removeAllItems();
		this.addItem("");
		keysCombo.removeAllElements();
		keysCombo.addElement("");
		while (i.hasNext()) {
			Element e = (Element) i.next();
			String nombre = e.getName();
			if (nombre.equals("row")) {
				Iterator j = e.getChildren().iterator();
				String code = ((Element) j.next()).getValue();
				String name = ((Element) j.next()).getValue().trim();
				if (saveKey) {
					keysCombo.addElement(code);
					this.addItem(name);
				}
				else {
					this.addItem(name+" "+code);
				}
			}
		}
	}

	/**
	 * Este metodo se deriva de la implementacion de ChangeExternalValueListener y se
	 * llama cuando un valor externo cambio.
	 */
	public void changeExternalValue(ChangeExternalValueEvent e) {
		
		if (keys!=null) {
			if (!e.getExternalValue().equals(exportValue) && importValue==null) {
				generar();
			}
			if (importValue!=null && importValue.contains(e.getExternalValue())) {
				generar();
			}
		}
	}

	/**
	 * Este metodo retorna el Label del XMLComboBox
	 * @return
	 */
	
	public String getLabelName() {
		return labelName;
	}

	public String getNameCombo() {
		return !"".equals(name)?name:labelName;
	}
	
	/**
	 * Este metodo asigna el Label de un XMLComboBox
	 * @param labelName
	 */
	
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	
	/**
	 * Este metodo se sobreescribe de JComboBox, para retornar de forma transparente
	 * el contenido de JComboBox.
	 */
	public Object getItemAt(int index) {
		if (saveKey) {
			return keysCombo.get(index);
		} else {
			StringTokenizer stk = new StringTokenizer((String) super.getItemAt(index)," ");
			String tok="";
			while(true) {
				try {
					tok=stk.nextToken();
				}
				catch(NoSuchElementException NSEe) {
					break;
				}
			}
			return tok;
		}
	}
	
	/**
	 * Este metodo se encarga de validar si el componente se debe limpiar o no, en el 
	 * momento de limpiar la forma.
	 * @return
	 */
	public boolean isClean() {
		return clean;
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

	/**
	 * Este metodo se desprende de la implementacion de PopupMenuListener y se 
	 * encarga de generar una logica despues de la seleccion del JComboBox, 
	 * por ejemplo exportar un valor despues de la selecci�n.
	 */
	
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
       	exportar();
	}
	
	private void exportar() {
		if (exportValue!=null) {
       		if (!saveKey) {
				StringTokenizer stk = new StringTokenizer((String) getSelectedItem()," ");
				String tok="";
				while(true) {
					try {
						tok=stk.nextToken();
					}
					catch(NoSuchElementException NSEe) {
						break;
					}
				}
				GFforma.setExternalValues(exportValue,tok);
       		}
       		else  {
				GFforma.setExternalValues(exportValue,keysCombo.get(getSelectedIndex()));
       		}
       		
		}
		if (sqlCode!=null) {
			class SearchingSQL extends Thread {
	            
		        private String[] args;
	            public SearchingSQL(String[] args) {
	                 this.args=args;
	            }
	            
	            public void run() {

	                String sql;
	                
			        for (int i=0;i<sqlCode.size();i++) {
			            Document doc = null;
	                    sql = sqlCode.get(i);
	                    try {
	                        doc = STResultSet.getResultSetST(sql,args);
	                    }
	                    catch (STException e) {
	                        e.printStackTrace();
	                    }
	                    AnswerEvent event = new AnswerEvent(this,sql,doc);
	                    notificando(event);
			        }
	            }
	        }
	        /*-----------------------------------------------------------*/
	        Object [] XMLimpValues = importValueCode.toArray();
			String [] impValues = new String[XMLimpValues.length+1];
			
			int i = 0;
			for ( i = 0; i < impValues.length -1 ; i++) {
				impValues[i] =  GFforma.getExteralValuesString((String)XMLimpValues[i]);
			}
			impValues[i] =  (String) getItemAt(getSelectedIndex());
	        new SearchingSQL(impValues).start();
		}
	}
	public void popupMenuCanceled(PopupMenuEvent e) {}

	/**
	 * Este metodo retorna una llave :S
	 * @return
	 */
	public String getKey() {
		return key;
	}

	public String getNameField() {
		return nameField;
	}

	public void setNameField(String nameField) {
		this.nameField = nameField;
	}

	public void arriveAnswerEvent(AnswerEvent AEe) {
		if (AEe.getSqlCode().equals(keySQL)) {
			try {
				Document doc = AEe.getDocument();
		        String select = doc.getRootElement().getChild("row").getChildText("col");
		        this.setSelectedItem(select);
	        }
			catch (NullPointerException NPEe) {
				//this.removeAllItems();
			}
		}
	}
	
	public void initiateFinishEvent(FinishEvent e) {
		try {
			if (driverEvent!=null) {
			   GFforma.invokeMethod(driverEvent,"addAnswerListener",new Class[]{AnswerListener.class},new Object[]{this});
			}
			if (preferredLength>0) {
				setPreferredSize(new Dimension(preferredLength,getHeight()));
			}
		}
		catch(NotFoundComponentException NFCEe) {
			NFCEe.printStackTrace();
		} 
		catch (InvocationTargetException ITEe) {
			ITEe.printStackTrace();
		}
	}

	public boolean isWhitPanel() {
		return whitPanel;
	}

	public boolean isReturnNullValue() {
		return returnNullValue;
	}
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }

	public String getExportValue() {
		return exportValue;
	}
	
	public synchronized void addAnswerListener(AnswerListener listener ) {
        AnswerListener.addElement(listener);
    }

    public synchronized void removeAnswerListener(AnswerListener listener ) {
        AnswerListener.removeElement(listener);
    }
    private synchronized void notificando(AnswerEvent event) {
        Vector lista;
        lista = (Vector)AnswerListener.clone();
        for (int i=0; i<lista.size();i++) {
            AnswerListener listener = (AnswerListener)lista.elementAt(i);
            listener.arriveAnswerEvent(event);
        }
    }
}
