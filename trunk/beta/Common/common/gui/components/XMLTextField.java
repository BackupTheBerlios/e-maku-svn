package common.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import common.control.DateEvent;
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
	private boolean key;
	private boolean returnValue = true;
	private boolean nullValue;
	private String totalCol;
	private String keyExternalValue = "";
	private String idDocument;
	private boolean clean = true;
	private double numberValue = 0;
	private String ExportValue = null;
	private Vector ImportValue = null;
	private String callExternalClass;
	private String callExternalMethod;
	private String calculateExportValue = null;
	private String maxValue = null;
	private String type;
	private String formatDate;
	private int chars;
	private boolean typed;
	private String nameField = null;
	private boolean cleaning = false;
	private boolean systemDate = false;
	private String calculateDate = null;
	private String addAttribute = null;
	
	public XMLTextField(String label, int length, int chars) {
		super(length);
		this.chars = chars;
		Generar(label, length);
		this.setDocument(new TextDataValidator(chars));
	}

	public XMLTextField(String label, int length, int chars, String TYPE) {
		super(length);
		this.chars = chars;
		Generar(label, length);
		if (TYPE.equals(NUMERIC)) {
			this.setDocument(new LimitDocument(chars));
		} else {
			this.setDocument(new TextDataValidator(chars));
		}
		type = TYPE;
	}

	public XMLTextField(String label, int length, int chars, String TYPE,String Mask) {
		super(length);
		this.chars = chars;
		Generar(label, length);
		if (TYPE.equals(NUMERIC)) {
			this.setDocument(new LimitDocument(chars));
		} else {
			if (Mask.equals("NUMERIC"))
				this.setDocument(new LimitDocument(chars));
			else {
				this.setDocument(new TextDataValidator(chars));
			}
		}
		type = TYPE;
	}

	private void Generar(String label, int length) {
		String name = Language.getWord(label);
		
		JLlabel = new JLabel(!name.equals("")?name:label) {
			
			private static final long serialVersionUID = -8298738505192648533L;
			public void paintComponent(Graphics g) {
		        Graphics2D g2 = (Graphics2D)g;
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                            RenderingHints.VALUE_ANTIALIAS_ON);
		        super.paintComponent(g);
		    }
		
		};
		JLlabel.setName(Language.getWord(label));
		JPtext = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPtext.add(this);
		
		this.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				int keyCode = e.getKeyChar();
				typed=true;
				if (NUMERIC.equals(getType()) && keyCode>=48 && keyCode<=57) {
					if (cleaning) {
						setText("");
						cleaning = false;
					}
				}
			}
		});
		
		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				transferFocus();
			}
		});
		this.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
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
	
	public boolean  isSystemDate() {
		return this.systemDate;
	}
	
	public void setIdDocument(String idDocument) {
		this.idDocument = idDocument;
		ClientHeaderValidator.addUpdateCodeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see common.control.DateListener#cathDateEvent(common.control.DateEvent)
	 */
	public void cathDateEvent(DateEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see common.control.UpdateCodeListener#cathUpdateCodeEvent(common.control.UpdateCodeEvent)
	 */
	public void cathUpdateCodeEvent(UpdateCodeEvent e) {
		// TODO Auto-generated method stub
		if (idDocument.equals(e.getIdDocument())) {
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
		} else {
			return false;
		}
	}

	public boolean isExportvalue() {
		if (ExportValue != null) {
			return true;
		} else {
			return false;
		}

	}

	public void setExportvalue(String exportvalue) {
		this.ExportValue = exportvalue;
	}

	public String getExportvalue() {
		return this.ExportValue;
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
		if (ImportValue != null) {
			return true;
		} else {
			return false;
		}

	}

	public String[] getImportValue() {
		try {
			String[] retorno = new String[ImportValue.size()];
			for (int i = 0; i < ImportValue.size(); i++) {
				retorno[i] = (String) ImportValue.get(i);
			}
			return retorno;
		} catch (NullPointerException NPEe) {
			return new String[] {};
		}
	}

	public void setImportValue(Vector inportValue) {
		ImportValue = inportValue;
	}

	public boolean isMaxValue() {
		if (maxValue != null) {
			return true;
		} else {
			return false;
		}

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
		return chars;
	}
	
	public void setText(String s) {
		if (NUMERIC.equals(getType())) {
			try {
				setNumberValue(Double.parseDouble(s));
			}
			catch (NumberFormatException NFEe) {}
		}
		super.setText(s);
	}
	
	class LimitDocument extends PlainDocument {

	    /**
		 * 
		 */
		private static final long serialVersionUID = 4060866073050927871L;
		private int limit;
	    public LimitDocument(int limit) {
	        super();
	        setLimit(limit); // store the limit����
	    }
	    
	    public final int getLimit() {
	        return limit;
	    }
	    
	    public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
	    	int longitud = s.length();
	    	if (typed) {
		        if (this.getLength() < limit) {
		            if (longitud <= limit - offset) {
		                if (offset < limit) {
		                    String Snum = new String();
		                    for (int i = 0; i < s.length(); i++) {
			                    try {
		                            Snum = Snum + s.substring(i, i + 1);
		                            Integer.parseInt(Snum);
			                        super.insertString(offset, s, attributeSet);
			                    }
			                    catch (NumberFormatException NFEe) {
			                    	/*if (!s.substring(i, i + 1).equals(".") && !s.substring(i, i + 1).equals(",")) {
			                    		Toolkit.getDefaultToolkit().beep();
			                    	}*/
			                    	if (super.getText(0,super.getLength()).indexOf(".")==-1) {
		                    			super.insertString(offset, s, attributeSet);
			                    	}
			                    	else {
			                    		Toolkit.getDefaultToolkit().beep();
			                    	}
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
	
	public boolean isTyped() {
		return typed;
	}

}
