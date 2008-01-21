package client.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.jdom.*;

import common.gui.components.*;
import common.gui.forms.*;
import common.transactions.TransactionServerException;
import common.transactions.TransactionServerResultSet;

/**
 * TableFindData.java Creado el 26-oct-2007
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
 * Esta clase se de generar un componente de busqueda de rapida de codigos de 
 * diferentes tipos, sea productos, terceros o cuentas contables, dependiendo
 * de su parametrizacion
 *  <br>
 * 
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda Piscal</A>
 */

public class EmakuSearchField extends JPanel implements Couplable, KeyListener,PopupMenuListener,FocusListener {

	private static final long serialVersionUID = 246103248621691834L;

	private JPanel JPNorth;
	private XMLTextField XMLTField;
	private XMLTextField XMLTFkey;
	private ComboBoxFiller SQLCBselection;
	private JPopupMenu JPMpopup;
	private GenericForm GFforma;
	private boolean dataSelected;
	private String keyValue;
	private ArrayList<String> externalValues = new ArrayList<String>();
	private Vector<AnswerListener> answerListener = new Vector<AnswerListener>();
	private int repeatKey = 1;
	private String sqlCombo = null;
	private boolean dataBeep;
	private boolean blankArgs;
	private int selectedIndex;
	private String dataMessage;
	private String labelName="";
	private int size;
	private int maxlength;
	private boolean withPanel = true;
	private String exportValue;
	private Vector<String> sqlCode = new Vector<String>();
	private boolean searchQuery = false;

	public EmakuSearchField(GenericForm GFforma,Document doc) {
		this.GFforma=GFforma;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		Iterator iterator = doc.getRootElement().getChildren().iterator();
		while (iterator.hasNext()) {
			Element elm = (Element) iterator.next();
			String value = elm.getValue();
			String name = elm.getAttributeValue("attribute");
			
			if ("sqlCombo".equals(name)) {
				sqlCombo = value;
			} else if ("noDataBeep".equals(name)) {
				dataBeep = Boolean.parseBoolean(value);
			} else if ("blankArgs".equals(name)) {
				blankArgs = Boolean.parseBoolean(value);
			} else if ("Panel".equals(name)) {
				withPanel = Boolean.parseBoolean(value);
			} else if ("selectedIndex".equals(name)) {
				selectedIndex = Integer.parseInt(value);
			} else if ("noDataMessage".equals(name)) {
				dataMessage = value;
			} else if ("label".equals(name)) {
				labelName = value;
			} else if ("size".equals(name)) {
				size = Integer.parseInt(value);
			} else if ("maxlength".equals(name)) {
				maxlength =  Integer.parseInt(value);
			} else if ("repeatData".equals(name)) {
				this.repeatKey=Integer.parseInt(value);
			} else if ("keyDataSearch".equals(name)) {
				this.keyValue=value;
			} else if ("exportValue".equals(name)) {
				this.exportValue=value;
			}
			else if ("sqlCode".equals(elm.getAttributeValue("attribute"))) {
				sqlCode.add(elm.getValue());
				searchQuery = true;
			} 		
		}
		
		XMLTField = new XMLTextField(labelName,size,maxlength);
		XMLTField.addKeyListener(this);
		XMLTField.addFocusListener(this);
		
		XMLTFkey = new XMLTextField("KEY",size,maxlength);
		XMLTFkey.addFocusListener(this);
		XMLTFkey.addKeyListener(this);
		
		String[] args = new String[repeatKey+externalValues.size()];
		
		for (int i=0;i<externalValues.size();i++) {
			args[i]=externalValues.get(i);
		}
		for (int i=externalValues.size();i<args.length;i++) {
			args[i]=keyValue;
		}
		
		SQLCBselection = new ComboBoxFiller(GFforma,sqlCombo,args,blankArgs,dataBeep,selectedIndex,dataMessage);
		SQLCBselection.addPopupMenuListener(this);
		SQLCBselection.addKeyListener(this);
		SQLCBselection.addFocusListener(this);
		SQLCBselection.setPreferredSize(new Dimension(400,20));
		JPMpopup = new JPopupMenu() {
			private static final long serialVersionUID = -6078272560337577761L;
			public void setVisible(boolean b) {
				Boolean isCanceled = (Boolean) getClientProperty("JPopupMenu.firePopupMenuCanceled");
				if (b
						|| (!b && dataSelected)
						|| ((isCanceled != null) && !b && isCanceled.booleanValue()) ) {
					super.setVisible(b);
				}
			}
		};
		XMLTField.setComponentPopupMenu(JPMpopup);
		JPMpopup.setLightWeightPopupEnabled(true);
		JPMpopup.add(dataSearch());
		if (labelName!=null && !"".equals(labelName)) {
			this.add(XMLTField.getLabel());
		}
		this.add(XMLTField);
	}

	private JPanel dataSearch() {
		JPanel JPdataSearch = new JPanel(new BorderLayout());
		JPNorth = new JPanel(new BorderLayout());
		JPNorth.add(XMLTFkey.getLabel(), BorderLayout.WEST);
		JPNorth.add(XMLTFkey.getJPtext(), BorderLayout.CENTER);
		JPdataSearch.add(SQLCBselection, BorderLayout.SOUTH);
		JPdataSearch.add(JPNorth, BorderLayout.CENTER);
		return JPdataSearch;
	}

	/**
	 * Este metodo limpia los elementos del panel de busqueda
	 */

	public void clean() {
		XMLTField.setText("");
		XMLTFkey.setText("");
		SQLCBselection.clear();
	}
	
	/**
	 * Este metodo retorna el valor resultante de la busqueda
	 * @return retorna el codigo consultado
	 */
	protected String getValue() {
		return XMLTField.getText().trim();
	}

	/**
	 * Este metodo visualiza el menu emergente con sus coordenadas respectivas
	 */
	
	public void showDataSearch() {
		if (!JPMpopup.isVisible()) {
			updateUI();
			int x = 0;
			int y = this.getHeight();
			try {
				JPMpopup.show(XMLTField,x,y);
				Thread t = new Thread() {
					public void run() {
						XMLTFkey.requestFocus();	
					}
				};
				SwingUtilities.invokeLater(t);
				dataSelected = false;
			}
			catch (IllegalComponentStateException e) {}
		}
		
	}
	
	public JPopupMenu getPopup() {
		return JPMpopup;
	}
	
	public void popupMenuCanceled(PopupMenuEvent e) {}

	/**
	 * Este metodo se genera al terminar el evento de seleccion en el combo
	 * y transfiere el codigo obtenido por la seleccion al componente principal
	 */
	
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		if (JPMpopup.isVisible()) {
			storeData();
		}
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
	public void focusGained(FocusEvent e) {}

	/**
	 * Este metodo se genera cuando el field de consulta por palabra clave pierde
	 * el foco, se encarga de exportar su valor con la llave correspondiente para
	
	 * que luego el combo genere la respectiva consulta
	 */
	public void focusLost(FocusEvent e) { 
		GFforma.setExternalValues(keyValue,XMLTFkey.getText());
		Object s = e.getSource();
		if(s.equals(SQLCBselection) && JPMpopup.isVisible()) {
			storeData();
		}
		if(s.equals(XMLTField))  {
			GFforma.setExternalValues(exportValue,XMLTField.getText());
		}
		if (searchQuery) {
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
							doc = TransactionServerResultSet.getResultSetST(sql, new String[]{XMLTField.getText()});
						} catch (TransactionServerException e) {
							e.printStackTrace();
						}
						AnswerEvent event = new AnswerEvent(this, sql, doc);
						notificando(event);
					}
				}
				SwingUtilities.invokeLater(new SearchingSQL(sqlCode,j));
				//TODO Por el problema del chekout
				//new SearchingSQL(sqlCode,j).start();
			}
		}
	}
	
	/**
	 * Metodo encargado de notificar la llegada de un paquete <answer/> 
	 * @param event
	 */
	
	private void notificando(AnswerEvent event) {
		for(AnswerListener l:answerListener) {
			if (l.containSqlCode(event.getSqlCode())) {
				//System.out.println("Notificando a =>"+l);
				l.arriveAnswerEvent(event);
			}
		}
	}

	public void storeData() {
		dataSelected = true;
		XMLTField.setText(SQLCBselection.getStringCombo().trim());
		XMLTFkey.setText("");
		JPMpopup.setVisible(false);
	}
	
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		Object s = e.getSource();
		switch(keyCode) {
			case KeyEvent.VK_ESCAPE:
				dataSelected = true;
				XMLTFkey.setText("");
				JPMpopup.setVisible(false);
				break;
			case KeyEvent.VK_ENTER:
				if((s.equals(SQLCBselection)) && JPMpopup.isVisible()) {
					storeData();
				}
				break;
			case KeyEvent.VK_F2:
				showDataSearch();
				break;
			default :
				if (!XMLTField.isEditable() &&
					((keyCode >=60 &&  keyCode<=71) ||  keyCode >=65 &&  keyCode<=126)) {
					showDataSearch();
				}
				break;
		}
	}
	
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}

	public XMLTextField getXMLTFkey() {
		return XMLTFkey;
	}

	public void setDataSelected(boolean b) {
			dataSelected = b;
	}

	@Override
	public boolean containData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Element getPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getPackage() throws VoidPackageException {
        Element pack = new Element("package");
        pack.addContent(XMLTField.getText());
    	return pack;
	}

	@Override
	public Component getPanel() {
		if (!withPanel) {
			return XMLTField;
		}
		return this;
	}

	@Override
	public Element getPrintPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initiateFinishEvent(EndEventGenerator e) {
		// TODO Auto-generated method stub
		
	}
	
	public void addAnswerListener(AnswerListener listener ) {
		 answerListener.addElement(listener);
	}

	public void removeAnswerListener(AnswerListener listener ) {
		 answerListener.removeElement(listener);
	}

}
