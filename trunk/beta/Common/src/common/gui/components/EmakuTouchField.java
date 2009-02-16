package common.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.IllegalComponentStateException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.jdom.Document;
import org.jdom.Element;

import common.gui.forms.EndEventGenerator;
import common.gui.forms.ExternalValueChangeEvent;
import common.gui.forms.ExternalValueChangeListener;
import common.gui.forms.GenericForm;
import common.gui.forms.InstanceFinishingListener;
import common.gui.forms.NotFoundComponentException;
import common.transactions.TransactionServerException;
import common.transactions.TransactionServerResultSet;

public class EmakuTouchField extends XMLTextField  implements 
Couplable, ActionListener, InstanceFinishingListener, ExternalValueChangeListener, 
KeyListener, FocusListener, AnswerListener {

	private static final long serialVersionUID = -8059783384089564101L;
	private TouchButtons touchButtons;
	private JButton JBTouch = new JButton();
	private JButton JBimportButton = new JButton();
	private JButton JBcard= new JButton();
	private CreditCardButtons creditCard;
	protected JPopupMenu popupTouch;
	private Font font;
	private Vector<RecordListener> recordListener = new Vector<RecordListener>();
	private Vector<AnswerListener> AnswerListener = new Vector<AnswerListener>();
	private String importValueButton;
	private String cardSelection;
	private GenericForm GFforma;
	private int sequence = 0;
	private boolean notified;
	private Vector<String> keySQL;
	private boolean withoutButton;
	private boolean withBill;
	private ArrayList<String> cleanImportValue;
	
	public EmakuTouchField(GenericForm genericForm, Document doc) {
		Element rootElement = doc.getRootElement();
		this.GFforma = genericForm;
		Vector<String> importValue = null;
		Iterator args = rootElement.getChildren().iterator();
		popupTouch = new JPopupMenu();
		Vector<String> sqlCode = new Vector<String>();
		this.getCaret().setBlinkRate(500);
		URL url = null;
		while (args.hasNext()) {
			final Element elm = (Element) args.next();
			if ("sqlCode".equals(elm.getAttributeValue("attribute"))) {
				sqlCode.add(elm.getValue());
			}
			else if ("sqlLocal".equals(elm.getAttributeValue("attribute"))) {
				setSqlLocal(elm.getValue());
			} else if ("label".equals(elm.getAttributeValue("attribute"))) {
				setStringLabel(elm.getValue());
			} else if ("format".equals(elm.getAttributeValue("attribute"))) {
				setType(elm.getValue());
			}
			else if ("name".equals(elm.getAttributeValue("attribute"))) {
				setName(elm.getValue());
			}
			else if ("mask".equals(elm.getAttributeValue("attribute"))) {
				setMask(elm.getValue());
			} else if ("size".equals(elm.getAttributeValue("attribute"))) {
				setColumns( Integer.parseInt(elm.getValue()));
			} else if ("maxlength".equals(elm.getAttributeValue("attribute"))) {
				setNChars(Integer.parseInt(elm.getValue()));
			} else if ("exportValue".equals(elm.getAttributeValue("attribute"))) {
				this.setExportvalue(elm.getValue());
			} else if ("importValue".equals(elm.getAttributeValue("attribute"))) {
				if (importValue == null) {
					importValue = new Vector<String>();
				}
				importValue.addElement(elm.getValue());
			} else if ("cleanImportValue".equals(elm.getAttributeValue("attribute"))) {
				if (cleanImportValue==null) {
					cleanImportValue=new ArrayList<String>();
				}
				StringTokenizer iv = new StringTokenizer(elm.getValue(),",");
				cleanImportValue.add(iv.nextToken()+iv.nextToken());
			}
			else if ("maxValue".equals(elm.getAttributeValue("attribute"))) {
				setMaxValue(elm.getValue());
			}
			 else if ("maxValue".equals(elm.getAttributeValue("attribute"))) {
					setMaxValue(elm.getValue());
				}else if ("enabled".equals(elm.getAttributeValue("attribute"))) {
				setEnabled(Boolean.parseBoolean(elm.getValue()));
			} else if ("font".equals(elm.getAttributeValue("attribute"))) {
				try {
					StringTokenizer STfont = new StringTokenizer(elm
							.getValue(), ",");
					font = new Font(
			                STfont.nextToken(),
			                Integer.parseInt(STfont.nextToken()),
			                Integer.parseInt(STfont.nextToken()));
					setFont(font);
				}
				catch (NumberFormatException NFEe) {}
				catch (NoSuchElementException NSEEe) {}
			} else if ("foreground".equals(elm.getAttributeValue("attribute"))) {
				setForeground(getColor(elm.getValue()));
			} else if ("background".equals(elm.getAttributeValue("attribute"))) {
				setBackground(getColor(elm.getValue()));
			} else if ("alignment".equals(elm.getAttributeValue("attribute"))) {
				String alignment = elm.getValue();
				if (alignment.equals("RIGHT")) {
					setHorizontalAlignment(SwingConstants.RIGHT);
				} else if (alignment.equals("CENTER")) {
					setHorizontalAlignment(SwingConstants.CENTER);
				}
			} else if ("clean".equals(elm.getAttributeValue("attribute"))) {
				setClean(Boolean.parseBoolean(elm.getValue()));
			} else if ("sendRecord".equals(elm.getAttributeValue("attribute"))) {
				setSendRecord(elm.getValue());
			} else if ("calculateExportValue".equals(elm.getAttributeValue("attribute"))) {
				setCalculateExportValue(elm.getValue());
			} else if ("keySQL".equals(elm.getAttributeValue("attribute"))) {
				if (keySQL == null) {
					keySQL = new Vector<String>();
				}
				keySQL.add(elm.getValue());
			} else if ("iconButton".equals(elm.getAttributeValue("attribute"))) {
				url = this.getClass().getResource(elm.getValue());
			}
			else if ("withoutButton".equals(elm.getAttributeValue("attribute"))) {
				withoutButton = Boolean.parseBoolean(elm.getValue());
			}
			else if ("withBill".equals(elm.getAttributeValue("attribute"))) {
				withBill = Boolean.parseBoolean(elm.getValue());
			}
			else if ("importButton".equals(elm.getAttributeValue("attribute"))) {
				importValueButton=elm.getValue();
			}
			else if ("card".equals(elm.getAttributeValue("attribute"))) {
				cardSelection=elm.getValue();
			}

		}
		
		if (getExportvalue()!=null) {
			if (getType().equals("NUMERIC")) {
				GFforma.setExternalValues(getExportvalue(),0);
			}
			else {
				GFforma.setExternalValues(getExportvalue(),"");
			}
		}
		
		setSqlCode(sqlCode);
		generar();
		JPanel JPeast = new JPanel(new BorderLayout());
		getJPtext().add(JPeast,BorderLayout.EAST);
		if (!withoutButton) {
			touchButtons = new TouchButtons(this,font,withBill);
			ImageIcon icon = url!=null ? new ImageIcon(url) : null;
			JBTouch = icon!=null ? new JButton(icon) : new JButton("X");
			JBTouch.setFocusable(false);
			JBTouch.setActionCommand("display");
			JBTouch.addActionListener(this);
			JPeast.add(JBTouch,BorderLayout.WEST);
			popupTouch.add(touchButtons);
		}
		if (importValueButton!=null && cardSelection==null) {
			JBimportButton = new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_aceptar_32x32.png")));
			JBimportButton.setFocusable(false);
			JBimportButton.setActionCommand("importValue");
			JBimportButton.addActionListener(this);
			JPeast.add(JBimportButton,BorderLayout.EAST);
		}

		if (cardSelection!=null) {
			creditCard = new CreditCardButtons(genericForm,this,font,importValueButton);
			JBcard = new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_card_32x32.png")));
			JBcard.setFocusable(false);
			JBcard.setActionCommand("card");
			JBcard.addActionListener(this);
			JPeast.add(JBcard,BorderLayout.CENTER);
			popupTouch.add(creditCard);
		}
		
		
		setImportValue(importValue);
		addKeyListener(this);
		addFocusListener(this);
	}

	private Color getColor(String color) {
		try {
			StringTokenizer STcolor = new StringTokenizer(color, ",");
			int r = Integer.parseInt(STcolor.nextToken());
			int g = Integer.parseInt(STcolor.nextToken());
			int v = Integer.parseInt(STcolor.nextToken());
			return new Color(r, g, v);
		} catch (NumberFormatException NFEe) {
			return null;
		} catch (NoSuchElementException NSEEe) {
			return null;
		}
	}

	public void clean() {
		this.setText("");
		setNotified(false);
	}

	public boolean containData() {
		return false;
	}

	public Element getPackage(Element args) throws Exception {
		return this.getPackage();
	}

	public Element getPackage() throws VoidPackageException {
		return null;
	}

	public Component getPanel() {
		return this.getJPtext();
	}

	public Element getPrintPackage() {
		return null;
	}

	public void validPackage(Element args) throws Exception {
		
	}

	public void initiateFinishEvent(EndEventGenerator e) {
		GFforma.addChangeExternalValueListener(this);
	}
	
	
	public void doFormat() {
		format();
		sendData(false);
		disposePopup();
	}
	
	public void format() {
		if ("NUMERIC".equals(getType())) {
			String text = getText();
			try {
				NumberFormat nf = NumberFormat.getNumberInstance();
    			DecimalFormat form = (DecimalFormat) nf;
    			form.applyPattern("###,###,##0.00");
    			this.setText(nf.format(Double.parseDouble(text)));
    			this.setNumberValue(nf.parse(text).doubleValue());
    			if (isExportvalue()) {
    				try {
    					GFforma.setExternalValues(getExportvalue(),getNumberValue());
    				}
    				catch (NumberFormatException NFE) {}
    			}
			}
			catch (NumberFormatException NFEe) {}
			catch (ParseException Pe) {}
		} else {
			if (isExportvalue()) {
				try {
					GFforma.setExternalValues(getExportvalue(),getText());
				}
				catch (NumberFormatException NFE) {}
			}
		}
	}

	public void disposePopup() {
		popupTouch.setVisible(false);
		this.requestFocus();
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("display".equals(command) || "card".equals(command)) {
			if ("NUMERIC".equals(getType())) {
				this.setNumberValue(0);
			}
			this.setText("");
			displayButtons();
		} 
		if ("importValue".equals(command)) {
			this.setText(GFforma.getExternalValueString(importValueButton));
			doFormat();
		}
	}
	
	public void cleanImportComponentes() {
		if (cleanImportValue!=null) {
			Iterator<String> i = cleanImportValue.iterator();
			while (i.hasNext()) {
				String componente = i.next();
				try {
					GFforma.invokeMethod(componente,"clean");
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotFoundComponentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void displayButtons() {
		setNotified(false);
		if (!popupTouch.isVisible()) {
			updateUI();
			int psize = (int) popupTouch.getPreferredSize().getWidth();
			int x = this.getWidth() - psize;
			int y = this.getHeight();
			try {
				popupTouch.show(this,x,y);
				if ("NUMERIC".equals(getType())) {
					this.setText(String.valueOf((int)getNumberValue()));
				}
			}
			catch (IllegalComponentStateException ex) {}
		}
	}
	
	protected synchronized void notificando(XMLTextField XMLTFtext, String value) {
		Element element = new Element("table");
		Element row = new Element("row");
		element.addContent(row);
		StringTokenizer stk = new StringTokenizer(XMLTFtext.getSendRecord(),",");

		while (true) {
			try {
				String tok = stk.nextToken();
				Element col = new Element("col");
				if ("value".equals(tok)) {
					col.setText(value);
				}
				else if (this.containImportValue(tok)) {
					col.setText(GFforma.getExternalValueString(tok));
				}
				else if ("sequence".equals(tok)) {
					col.setText(String.valueOf(++sequence));
				} else {
					col.setText(tok.substring(1, tok.length() - 1));
				}
				row.addContent(col);
			} catch (NoSuchElementException NSEe) {
				break;
			}
		}

		RecordEvent event = new RecordEvent(this, element);
		int max = recordListener.size();
		for (int i = 0; i < max; i++) {
			RecordListener listener = recordListener.get(i);
			listener.arriveRecordEvent(event);
		}
	}
	
	
	public void addRecordListener(RecordListener listener) {
		recordListener.addElement(listener);
	}

	public void removeRecordListener(RecordListener listener) {
		recordListener.removeElement(listener);
	}

	public void searchOthersSqls(EmakuTouchField field) {
		/*-----------------------------------------------------------*/
		final String[] argumentos = new String[field.getImportValues().length
				+ field.getConstantSize() + 1];
		String[] XMLimpValues = field.getImportValues();
		
		int i = 0;
		for (i = 0; i < field.getConstantSize(); i++) {
			argumentos[i] = field.getConstantValue(i);
		}

		for (; i < field.getImportValues().length; i++) {
			argumentos[i] = GFforma.getExternalValueString(XMLimpValues[i]);
		}
		argumentos[i] = field.getText();
		Vector<String> sqlCode = field.getSqlCode();
		
		for (int j = 0; j < sqlCode.size(); j++) {
			class SearchingSQL extends Thread {

				private int j;
				private Vector<String> sqlCode;
				public SearchingSQL(Vector<String> sqlCode,int j) {
					this.j=j;
					this.sqlCode=sqlCode;
				}

				public void run() {
					Document doc = null;
					String sql = sqlCode.get(j);
					try {
						doc = TransactionServerResultSet.getResultSetST(sql, argumentos);
					} catch (TransactionServerException e) {
						e.printStackTrace();
					}
					AnswerEvent event = new AnswerEvent(this, sql, doc);
					notificando(event);
				}
			}
			new SearchingSQL(sqlCode,j).start();
		}
	}
	
	public void arriveAnswerEvent(AnswerEvent AEe) {
	}

	public void addAnswerListener(AnswerListener listener) {
		AnswerListener.addElement(listener);
	}

	public void removeAnswerListener(AnswerListener listener) {
		AnswerListener.removeElement(listener);
	}
	
	private void notificando(AnswerEvent event) {
		for (AnswerListener l:AnswerListener) {
			if (l.containSqlCode(event.getSqlCode())) {
				l.arriveAnswerEvent(event);
			}
		}
	}
	
	public void changeExternalValue(ExternalValueChangeEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch(keyCode) {
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_TAB:
			format();
			sendData(true);
			break;
		}
	}

	public void sendData(boolean notified) {
		String text = getText();
		if (isSendRecord() && !"".equals(text)) {
			if ("NUMERIC".equals(getType())) {
				text = String.valueOf(getNumberValue());
			}
			notificando(this, text);
			setNotified(notified);
			searchOthersSqls(this);
			this.setText("");
		}
	}
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void focusGained(FocusEvent e) {}

	public void focusLost(FocusEvent e) {
		//Object next = e.getOppositeComponent();
		if (isNotified()) {
			requestFocus();
			setNotified(false);
		}
	}
	
	public JButton getJButtonOk() {
		return touchButtons.getJBOk();
	}
	
	public int getHeightPopup() {
		return popupTouch.getHeight();
	}

	public boolean isNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}
	public boolean containSqlCode(String sqlCode) {
		if (keySQL.contains(sqlCode))
			return true;
		else
			return false;
	}

}