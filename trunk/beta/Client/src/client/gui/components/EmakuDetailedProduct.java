package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.IllegalComponentStateException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import common.gui.components.EmakuUIFieldFiller;
import common.gui.components.XMLTextField;
import common.gui.forms.ExternalValueChangeEvent;
import common.gui.forms.ExternalValueChangeListener;
import common.gui.forms.GenericForm;
import common.misc.Icons;
import common.misc.language.Language;

public class EmakuDetailedProduct extends JComponent implements FocusListener,KeyListener, ActionListener,ExternalValueChangeListener {

	private static final long serialVersionUID = 8386003217712730190L;
	private JPanel contentPane;
	private JPopupMenu JPMpopup;
	private XMLTextField XMLTFCode;
	private XMLTextField XMLTFDescription;
	private ComboBoxFiller comboBoxCatalogue;
	private ComboBoxFiller comboBoxWarehouse;
	private XMLTextField XMLTFAmount;
	private XMLTextField XMLTFIdProd;
	private XMLTextField XMLTFValue;
	private XMLTextField XMLTFTotal;
	private JRadioButton JRDebit;
	private JRadioButton JRCredit;
	private JPanel JPFields;
	private JPanel JPLabels;
	private JLabel statusLabel;
	private EmakuDataSearch dataSearch;
	private GenericForm genericForm;
	private JButton JBAccept;
	private JButton JBCancel;
	private JTable table;
	private int rowIndex;
	protected int columnIndex;
	private boolean visible = false;
	
	private short columnDebit;
	private short columnCredit;
	private short columnAmount;
	private short columnIdProdServ;
	private short columnIdWareHouse;
	private Vector<XMLTextField> VFields = new Vector<XMLTextField>();
	
	public EmakuDetailedProduct(GenericForm GFforma,
							   String sql,
							   String[] externalValues,
							   String keyValue,
							   boolean blankArgs,
							   boolean dataBeep,
							   String dataMessage,
							   int selected,
							   int repeatData,final JTable table) {
		this.genericForm = GFforma;
		this.table = table;
		dataSearch = new EmakuDataSearch(
							genericForm,sql,externalValues,
							keyValue,blankArgs,dataBeep,
							dataMessage,selected,repeatData) {
			private static final long serialVersionUID = 6294167396739991593L;
			public void storeData() {
				super.storeData();
				String s = getValue(); 
				setCode(s);
				genericForm.setExternalValues(XMLTFCode.getExportvalue(),s);
				forceFocus();
			}
		};
		dataSearch.addKeyListener(this);
		this.addKeyListener(this);
		genericForm.addChangeExternalValueListener(this);
		initComps();
	}
	
	public void forceFocus() {
		Thread t = new Thread () {
			public void run() {
				table.requestFocus();
				table.changeSelection(rowIndex, columnIndex, true,false);
				showDetailed();
			}
		};
		SwingUtilities.invokeLater(t);
	}
	
	private void initComps() {
		contentPane = new JPanel(new BorderLayout());
		statusLabel = new JLabel(" ");
		statusLabel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		JPFields = new JPanel(new GridLayout(8,1));
		JPLabels = new JPanel(new GridLayout(8,1));

		JPMpopup = new JPopupMenu() {
			private static final long serialVersionUID = -8347328110027850041L;
			public void setVisible(boolean b) {
				super.setVisible(visible);	
			}
		};
		
		JPMpopup.setLayout(new BorderLayout());
		JPMpopup.setLightWeightPopupEnabled(true);
		JPMpopup.setBorderPainted(true);
		JPMpopup.add(contentPane,BorderLayout.CENTER);
		JPMpopup.add(new JPanel(),BorderLayout.NORTH);
		JPMpopup.add(new JPanel(),BorderLayout.WEST);
		
		XMLTFCode 		 = new XMLTextField("CODE", 15, 10, XMLTextField.TEXT);
		
		XMLTFDescription = new XMLTextField("DESCRIPCION",15, 100, XMLTextField.TEXT);
		//Compra,Inventario,Catalogos, Hay que parametrizar la consulta
		comboBoxCatalogue= new ComboBoxFiller(genericForm,"SEL0398","catalogue",true);
		comboBoxWarehouse= new ComboBoxFiller(genericForm,"SEL0088","warehouse",false);
		
		XMLTFAmount		 = new XMLTextField("CANTIDAD", 15, 10, XMLTextField.NUMERIC);
		XMLTFIdProd		 = new XMLTextField("IDPROD", 15, 10, XMLTextField.NUMERIC);
		XMLTFValue		 = new XMLTextField("VALOR", 15, 10, XMLTextField.NUMERIC);
		XMLTFTotal		 = new XMLTextField("TOTAL", 15, 10, XMLTextField.NUMERIC);
		JRDebit			 = new JRadioButton();
		JRCredit		 = new JRadioButton();
		JRDebit.setSelected(true);
		XMLTFCode.addKeyListener(this);
		XMLTFCode.setExportvalue("code");
		XMLTFAmount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calculateAmount();
			}
		});
		
		Dimension d = new Dimension(170,20);
		comboBoxCatalogue.setPreferredSize(d);
		comboBoxWarehouse.setPreferredSize(d);
		
		XMLTFAmount.setHorizontalAlignment(SwingConstants.RIGHT);
		XMLTFValue.setHorizontalAlignment(SwingConstants.RIGHT);
		XMLTFTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		
		XMLTFDescription.setEnabled(false);
		XMLTFValue.setEnabled(false);
		XMLTFTotal.setEnabled(false);
		
		XMLTFCode.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String s = getValue(); 
				genericForm.setExternalValues(XMLTFCode.getExportvalue(),s);
			}
		});
		
		XMLTFValue.setSqlLocal("SEL0399");
		XMLTFDescription.setSqlLocal("SEL0400");
		
		ButtonGroup group = new ButtonGroup();
		group.add(JRDebit);
		group.add(JRCredit);
		
		JPanel p1 = new JPanel();
		p1.add(new JLabel(Language.getWord("DEBITO")));
		p1.add(JRDebit);
		
		JPanel p2 = new JPanel();
		p2.add(new JLabel(Language.getWord("CREDIT")));
		p2.add(JRCredit);
		ImageIcon i1 = new ImageIcon(getClass().getResource(Icons.getIcon("DELETE")));
		ImageIcon i2 = new ImageIcon(getClass().getResource(Icons.getIcon("EXIT")));
		
		JBAccept = new JButton(i1);
		JBAccept.setActionCommand("accept");
		JBAccept.addActionListener(this);
		
		JBCancel = new JButton(i2);
		JBCancel.setActionCommand("cancel");
		JBCancel.addActionListener(this);
		
		JPanel jpbuttons = new JPanel();
		jpbuttons.add(JBAccept);
		jpbuttons.add(JBCancel);
		
		JPLabels.add(XMLTFCode.getLabel());
		JPLabels.add(XMLTFDescription.getLabel());
		/* Pendiente de traducir */
		JPLabels.add(new JLabel("Bodega"));
		JPLabels.add(new JLabel("Precio"));
		JPLabels.add(XMLTFAmount.getLabel());
		JPLabels.add(XMLTFValue.getLabel());
		JPLabels.add(XMLTFTotal.getLabel());
		JPLabels.add(p1);
		
		JPanel panelPrice = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelPrice.add(comboBoxCatalogue);
		
		JPanel panelWareHouse = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelWareHouse.add(comboBoxWarehouse);
		
		JPFields.add(XMLTFCode.getJPtext());
		JPFields.add(XMLTFDescription.getJPtext());
		JPFields.add(panelWareHouse);
		JPFields.add(panelPrice);
		JPFields.add(XMLTFAmount.getJPtext());
		JPFields.add(XMLTFValue.getJPtext());
		JPFields.add(XMLTFTotal.getJPtext());
		JPFields.add(p2);
		
		contentPane.add(JPLabels,BorderLayout.WEST);
		contentPane.add(JPFields,BorderLayout.CENTER);
		JPMpopup.add(jpbuttons,BorderLayout.SOUTH);
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					showDetailed();
				}
			}
		});
	}
	
	public void showDetailed() {
		if (!JPMpopup.isVisible()) {
			updateUI();
			visible = true;
			clean();
			int psize = (int) JPMpopup.getPreferredSize().getWidth();
			int x = this.getWidth() - psize;
			int y = this.getY();
			try { JPMpopup.show(this,x,y); }
			catch (IllegalComponentStateException e) { e.printStackTrace(); }
		}
		XMLTFCode.requestFocus();
	}

	public String getValue() {
		return XMLTFCode.getText();
	}
	
	public void setCode(String code) {
		if ("".equals(code)) { 
			clean();
		}
		else {
			XMLTFCode.setText(code);	
		}
	}
	
	public void showDataSearch() {
		if (!dataSearch.getPopup().isVisible()) {
			updateUI();
			int psize = (int) dataSearch.getPopup().getPreferredSize().getWidth();
			int x = this.getWidth() - psize;
			int y = this.getHeight();
			try {
				dataSearch.getPopup().show(this,x,y);
				dataSearch.getXMLTFkey().requestFocus();
				dataSearch.setDataSelected(false);
			}
			catch (IllegalComponentStateException e) {}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		Object s = e.getSource();
		switch(keyCode) {
			case KeyEvent.VK_ESCAPE:
				JPMpopup.setVisible(false);
				break;
			case KeyEvent.VK_F2:
				if (s.equals(XMLTFCode)) {
					showDataSearch();
				} else {
					showDetailed();
				}
				break;
			default :
				if (((keyCode >=60 &&  keyCode<=71) ||  keyCode>=65 &&  keyCode<=126)) {
					showDetailed();
				}
				break;
		}
	}

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("accept".equals(command)) {
			boolean b = JRDebit.isSelected();
			int index = comboBoxWarehouse.getSelectedIndex();
			table.setValueAt(comboBoxWarehouse.getItemAt(index), rowIndex, columnIdWareHouse);
			table.setValueAt(XMLTFAmount.getText(), rowIndex, columnAmount);
			table.setValueAt(XMLTFIdProd.getText(), rowIndex, columnIdProdServ);
			table.setValueAt(
					XMLTFValue.getNumberValue(),
					rowIndex,
					b ? columnDebit : columnCredit);
			close();
		}
		else if ("cancel".equals(command)) {
			close();
		}
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	public void close() {
		visible = false;
		JPMpopup.setVisible(visible);
		table.requestFocus();
		table.changeSelection(rowIndex, columnIndex, false,false);
		table.getCellEditor().stopCellEditing();
	}

	public void calculateAmount() {
		try {
			int cant = Integer.valueOf(XMLTFAmount.getText());
			double val = XMLTFValue.getNumberValue();
			String total = String.valueOf(cant*val);
			XMLTFTotal.setText(total);
			fieldFormater(XMLTFTotal);
		}
		catch (NumberFormatException NFE) {}
	}
	
	public void changeExternalValue(ExternalValueChangeEvent e) {
		String key = e.getExternalValue();
		String value = genericForm.getExteralValuesString(key);
		if ("".equals(value)) { return; }
		if (key.equals("catalogue")) {
			Vector<String> vconst = new Vector<String>();
			vconst.add(XMLTFCode.getText().trim());
			vconst.add(value);
			XMLTFValue.setConstantValue(vconst);
			VFields.add(XMLTFValue);
			processQuery(XMLTFValue);
			calculateAmount();
		}
		if (key.equals("code")) {
			Vector<String> vconst = new Vector<String>();
			vconst.add(XMLTFCode.getText().trim());
			XMLTFDescription.setConstantValue(vconst);
			VFields.add(new XMLTextField());
			VFields.add(XMLTFDescription);
			VFields.add(XMLTFIdProd);
			processQuery(XMLTFDescription);
		}
	}
	
	private synchronized void processQuery(XMLTextField field) {
		String sqlLocal = field.getSqlLocal();
		if (sqlLocal != null) {
			String[] imps = getArgsForQuery(field);
			EmakuUIFieldFiller filler = null; 
			filler = new EmakuUIFieldFiller(genericForm,null,true,sqlLocal,
											imps,null,VFields);
			if (!filler.searchQuery()) {
				String t = field.getType();
				if (t.equals("NUMERIC")) {
					field.setText("0.0");	
				}
			} 
			fieldFormater(field);
		}
		VFields.clear();
	}
	
	private String[] getArgsForQuery(XMLTextField field) {
		if (field.isWithOutArgsQuery()) { return null; }
		String[] impValues = null;
		int argumentos = field.getConstantSize();
		impValues = new String[argumentos];
		int i = 0;
		for (; i < argumentos ; i++) {
			impValues[i] = field.getConstantValue(i);
		}
		return impValues;
	}
	
	public void clean() {
		XMLTFCode.setText("");
		XMLTFDescription.setText("");
		XMLTFAmount.setText("");
		XMLTFValue.setText("");
		XMLTFTotal.setText("");
		JRDebit.setSelected(false);
		comboBoxCatalogue.setSelectedIndex(0);
		comboBoxWarehouse.setSelectedIndex(0);
	}
	
	public void fieldFormater(XMLTextField field) {
		if (field.getType().equals("NUMERIC")) {
			double value = field.getNumberValue();
			
			BigDecimal bd = new BigDecimal(value);
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			
			NumberFormat nf = NumberFormat.getNumberInstance();
			DecimalFormat form = (DecimalFormat) nf;
			form.applyPattern("###,###,##0.00");
			field.setNumberValue(bd.doubleValue());
			field.setText(form.format(bd));
		}
	}
	
	public void focusGained(FocusEvent e) {}
	public void focusLost(FocusEvent e) {}

	public short getColumnAmount() {
		return columnAmount;
	}

	public void setColumnAmount(short columnAmount) {
		this.columnAmount = columnAmount;
	}

	public short getColumnCredit() {
		return columnCredit;
	}

	public void setColumnCredit(short columnCredit) {
		this.columnCredit = columnCredit;
	}

	public short getColumnDebit() {
		return columnDebit;
	}

	public void setColumnDebit(short columnDebit) {
		this.columnDebit = columnDebit;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public short getColumnIdProdServ() {
		return columnIdProdServ;
	}

	public void setColumnIdProdServ(short columnIdProdServ) {
		this.columnIdProdServ = columnIdProdServ;
	}

	public short getColumnIdWareHouse() {
		return columnIdWareHouse;
	}

	public void setColumnIdWareHouse(short columnIdWareHouse) {
		this.columnIdWareHouse = columnIdWareHouse;
	}
}