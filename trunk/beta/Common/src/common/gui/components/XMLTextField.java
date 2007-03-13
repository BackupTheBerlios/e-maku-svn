package common.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.jdom.Element;

import common.control.ClientHeaderValidator;
import common.control.UpdateCodeEvent;
import common.control.UpdateCodeListener;
import common.misc.language.Language;
import common.misc.text.TextDataValidator;

/**
 * XMLTextField.java Creado el 27-sep-2004
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
 * Informacion de la clase <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class XMLTextField extends JTextField implements UpdateCodeListener {

	private static final long serialVersionUID = -3627108803100237428L;
	public static final String NUMERIC = "NUMERIC";
	public static final String TEXT = "TEXT";
	private JPanel JPtext;
	private JLabel JLlabel;
	private String stringLabel;
	private String mask;
	private boolean key;
	private boolean returnValue = true;
	private boolean nullValue;
	private String totalCol;
	private String keyExternalValue = "";
	private String idDocument;
	private boolean clean = true;
	private double numberValue = 0;
	private String exportValue = null;
	private Vector<String> importValue = null;
	private Vector<String> constantValue = null;
	private String callExternalClass;
	private String callExternalMethod;
	private String calculateExportValue = null;
	private String calculateBSExportValue = null;
	private String maxValue = null;
	private String type;
	private String formatDate;
	private int nChars;
	private boolean typed;
	private String nameField = null;
	private boolean cleaning;
	private boolean systemDate;
	private boolean printable;
	private String calculateDate = null;
	private String addAttribute = null;
	private String sendRecord;
	private boolean sendQuery;
	private boolean queryOnInit;
	private boolean withOutArgsQuery;
	private String sqlInit = null;
	private String sqlLocal = null;
	private Vector<String> sqlCode = null;
	
	public XMLTextField() {}
	public XMLTextField(String label, int length, int chars) {
		super(length);
		String name = Language.getWord(label);
		stringLabel = !name.equals("")?name:label;
		this.nChars = chars;
		generar();
		this.setDocument(new TextDataValidator(chars));
	}

	public XMLTextField(String label, int length, int chars, String TYPE) {
		super(length);
		this.nChars = chars;
		String name = Language.getWord(label);
		stringLabel = !name.equals("")?name:label;
		type = TYPE;
		generar();
	}

	public XMLTextField(String label, int length, int chars, String TYPE,String Mask) {
		super(length);
		this.nChars = chars;
		String name = Language.getWord(label);
		stringLabel = !name.equals("")?name:label;
		this.mask = Mask;
		type = TYPE;
		generar();
	}

	void generar() {
		if (type!=null && type.equals(NUMERIC)) {
			this.setDocument(new LimitDocument(nChars));
		} else {
			if (mask!=null && mask.equals("NUMERIC")) {
				this.setDocument(new LimitDocument(nChars));
			}
			else {
				this.setDocument(new TextDataValidator(nChars));
			}
		}
		JLlabel = new JLabel(stringLabel) {
			
			private static final long serialVersionUID = -8298738505192648533L;
			public void paintComponent(Graphics g) {
		        Graphics2D g2 = (Graphics2D)g;
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                            RenderingHints.VALUE_ANTIALIAS_ON);
		        super.paintComponent(g);
		    }
		
		};
		JLlabel.setName(stringLabel);
		JPtext = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPtext.add(this);
		
		this.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				int keyCode = e.getKeyCode();
				typed=true;
				if (NUMERIC.equals(getType()) &&
					((keyCode>=48 && keyCode<=57) ||( keyCode==0))) {
					if (cleaning) {
						setText("");
						cleaning = false;
					}					
				}
				
			}
			
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				typed = false;
				if ( (keyCode==KeyEvent.VK_ENTER) || (keyCode==KeyEvent.VK_TAB)) {
					transferFocus();
				}
			}
		});
		
		/*this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				transferFocus();
			}
		});*/
		
		this.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				cleaning = true;
			}
		});
	}

	public Element getElementText() {
		Element element = new Element("field");
		if (key) {
			element.setAttribute("attribute", "key");
		}
		if (nameField!=null) {
			element.setAttribute("name",nameField);
		}
		if (NUMERIC.equals(getType())) {
			element.setText(String.valueOf(this.getNumberValue()));
		}
		else {
			element.setText(this.getText());
		}
		return element;
	}

	public Element getElementText(String value) {
		Element elm = new Element("field");
		if (NUMERIC.equals(getType())) {
			elm.setText(String.valueOf(this.getNumberValue()));
		}
		else {
			elm.setText(this.getText());
		}
		
		elm.setAttribute("attribute", value);
		if (nameField!=null) {
			elm.setAttribute("name",nameField);
		}
		return elm;
	}

	public JLabel getLabel() {
		return JLlabel;
	}

	public JPanel getJPtext() {
		return JPtext;
	}

	public JTextField getthis() {
		return this;
	}

	public void addJPanel(Component comp) {
		JPtext.add(comp);
	}


	public void isKey(boolean key) {
		this.key = key;
	}

	public boolean isReturnValue() {
		return returnValue;
	}

	public void setReturnValue(boolean returnValue) {
		this.returnValue = returnValue;
	}

	public String getTotalCol() {
		return totalCol;
	}

	public void setTotalCol(String totalCol) {
		this.totalCol = totalCol;
	}

	public String getKeyExternalValue() {
		return keyExternalValue;
	}

	public void setKeyExternalValue(String keyExternalValue) {
		this.keyExternalValue = keyExternalValue;
	}

	public void setSystemDate(boolean systemDate) {
		this.systemDate = systemDate;
	}
	
	public boolean isSystemDate() {
		return this.systemDate;
	}
	
	public void setIdDocument(String idDocument) {
		this.idDocument = idDocument;
		ClientHeaderValidator.addUpdateCodeListener(this);
	}

	public void cathUpdateCodeEvent(UpdateCodeEvent e) {
		if (idDocument.equals(e.getIdDocument()) && !sendQuery) {
			this.setText(e.getConsecutive());
		}
	}

	public boolean isNullValue() {
		return nullValue;
	}

	public void setNullValue(boolean nullValue) {
		this.nullValue = nullValue;
	}

	public boolean isClean() {
		return clean;
	}

	public void setClean(boolean clean) {
		this.clean = clean;
	}

	public double getNumberValue() {
		return numberValue;
	}

	public void setNumberValue(double numberValue) {
		this.numberValue = numberValue;
	}

	public void setExternalCall(String callExternalClass,
			String callExternalMethod) {
		this.callExternalClass = callExternalClass;
		this.callExternalMethod = callExternalMethod;
	}

	public String getCallExternalClass() {
		return callExternalClass;
	}

	public void setCallExternalClass(String callExternalClass) {
		this.callExternalClass = callExternalClass;
	}

	public String getCallExternalMethod() {
		return callExternalMethod;
	}

	public void setCallExternalMethod(String callExternalMethod) {
		this.callExternalMethod = callExternalMethod;
	}

	public boolean isCallExternalEvent() {
		if (callExternalClass != null && callExternalMethod != null) {
			return true;
		}
		return false;
	}

	public boolean isExportvalue() {
		if (exportValue != null) {
			return true;
		}
		return false;
	}

	public void setExportvalue(String exportvalue) {
		this.exportValue = exportvalue;
	}

	public String getExportvalue() {
		return this.exportValue;
	}

	public boolean isCalculateExportvalue() {
		if (calculateExportValue != null) {
			return true;
		} else {
			return false;
		}
	}

	public String getCalculateExportValue() {
		return calculateExportValue;
	}

	public void setCalculateExportValue(String calculateExportValue) {
		this.calculateExportValue = calculateExportValue;
	}

	public boolean isImportvalue() {
		if (importValue != null) {
			return true;
		}
		return false;
	}

	public boolean isConstantValue() {
		if (constantValue != null) {
			return true;
		}
		return false;
	}

	public String[] getImportValues() {
		try {
			return importValue.toArray(new String[importValue.size()]);
		} catch (NullPointerException NPEe) {
			return new String[] {};
		}
	}

	public String getConstantValue(int index) {
		try {
			return constantValue.get(index);
		} catch (NullPointerException NPEe) {
			return "";
		}
	}

	public int getConstantSize() {
		try {
			return constantValue.size();
		} catch (NullPointerException NPEe) {
			return 0;
		}
	}

	public void setImportValue(Vector<String> importValue) {
		this.importValue = importValue;
	}

	public void setConstantValue(Vector<String> constantValue) {
		this.constantValue = constantValue;
	}

	public boolean isMaxValue() {
		if (maxValue != null) {
			return true;
		}
		return false;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getMaxValue() {
		return this.maxValue;
	}

	public String getType() {
		return type;
	}

	public int getChars() {
		return nChars;
	}
	
	public void setText(String s) {
		if (NUMERIC.equals(getType())) {
			try {
				setNumberValue(Double.parseDouble(s));
			}
			catch (NumberFormatException NFEe) {
			}
		}
		super.setText(s);
	}
	
	class LimitDocument extends PlainDocument {

		private static final long serialVersionUID = 4060866073050927871L;
		private int limit;
	    public LimitDocument(int limit) {
	        super();
	        setLimit(limit);
	    }
	    
	    public final int getLimit() {
	        return limit;
	    }
	    
	    public synchronized void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
	    	int longitud = s.length();
	    	if (typed) {
		        if (this.getLength() < limit) {
		            if (longitud <= limit - offset) {
		                if (offset < limit) {
		                    String Snum = new String();
		                    for (int i = 0; i < s.length(); i++) {
			                    try {
		                            Snum = Snum + s.substring(i, i + 1);
		                            if (!("-".equals(Snum) || ".".equals(Snum)))
		                            	Integer.parseInt(Snum);
			                        super.insertString(offset, s, attributeSet);
			                    }
			                    catch (NumberFormatException NFEe) {
		                    		Toolkit.getDefaultToolkit().beep();
			                    }
		                    }
		                } else {
		                    Toolkit.getDefaultToolkit().beep();
		                }
		            } else {
		                Toolkit.getDefaultToolkit().beep();
		            }
		        } else {
		            Toolkit.getDefaultToolkit().beep();
		        }
		        typed = false;
	    	}
	    	else {
	    		super.insertString(offset, s, attributeSet);
	    	}
	    }


	    public final void setLimit(int newValue) {
	        this.limit = newValue;
	    }
	}

	public String getNameField() {
		return nameField;
	}

	public void setNameField(String nameField) {
		this.nameField = nameField;
	}

	public void setFormatDate(String formatDate) {
		this.formatDate = formatDate;
	}

	public String getFormatDate() {
		return formatDate;
	}

	public void setCalculateDate(String formula) {
		this.calculateDate=formula;
	}
	
	public String getCalculateDate() {
		return calculateDate;
	}
	
	public boolean isCalculateDate() {
		if (calculateDate!=null)
			return true;
		return false;
	}

	public String getAddAttribute() {
		return addAttribute;
	}

	public void setAddAttribute(String addAttribute) {
		this.addAttribute = addAttribute;
	}
	
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		super.setDisabledTextColor(Color.BLACK);
	}
	
	/*public boolean isTyped() {
		return typed;
	}*/

	public boolean isPrintable() {
		return printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

	public String getSendRecord() {
		return sendRecord;
	}
	
	public boolean isSendRecord() {
		if (sendRecord!=null) {
			return true;
		}
		return false;
	}

	public void setSendRecord(String sendRecord) {
		this.sendRecord = sendRecord;
	}

	public String getCalculateBSExportValue() {
		return calculateBSExportValue;
	}
	public boolean isCalculateBSExportValue() {
		if((calculateBSExportValue!=null) && (!"".equals(calculateBSExportValue))){
			return true;
		}
		return false;
	}
	public void setCalculateBSExportValue(String calculateBSExportValue) {
		this.calculateBSExportValue = calculateBSExportValue;
	}

	public boolean isSendQuery() {
		return sendQuery;
	}

	public void setSendQuery(boolean sendQuery) {
		this.sendQuery = sendQuery;
	}

	public boolean isQueryOnInit() {
		return queryOnInit;
	}

	public void setQueryOnInit(boolean queryOnInit) {
		this.queryOnInit = queryOnInit;
	}

	public boolean isWithOutArgsQuery() {
		return withOutArgsQuery;
	}

	public void setWithOutArgsQuery(boolean withOutArgsQuery) {
		this.withOutArgsQuery = withOutArgsQuery;
	}

	public String getSqlInit() {
		return sqlInit;
	}

	public void setSqlInit(String sqlInit) {
		this.sqlInit = sqlInit;
	}

	public Vector<String> getSqlCode() {
		return sqlCode;
	}

	public void setSqlCode(Vector<String> sqlCode) {
		this.sqlCode = sqlCode;
	}

	public String getSqlLocal() {
		return sqlLocal;
	}

	public void setSqlLocal(String sqlLocal) {
		this.sqlLocal = sqlLocal;
	}
	public String getStringLabel() {
		return stringLabel;
	}
	
	public void setStringLabel(String label) {
		String name = Language.getWord(label);
		stringLabel = !name.equals("")?name:label;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	public String getMask() {
		return mask;
	}
	public void setMask(String mask) {
		this.mask = mask;
	}
	
	public int getNChars() {
		return nChars;
	}
	public void setNChars(int chars) {
		nChars = chars;
	}

}
