package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.IllegalComponentStateException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;

import common.gui.components.EmakuUIFieldFiller;
import common.gui.components.XMLTextField;
import common.gui.forms.ExternalValueChangeEvent;
import common.gui.forms.ExternalValueChangeListener;
import common.gui.forms.GenericForm;
import common.misc.Icons;

public class EmakuTextileProduct extends XMLTextField 
implements FocusListener,KeyListener, ActionListener,ExternalValueChangeListener {

	private GenericForm genericForm;
	private JTable table;
	private JPanel panel;
	private JPanel JPFields;
	private JPanel JPLabels;
	private JPopupMenu JPMpopup;
	private boolean visible = false;
	private ComboBoxFiller comboBoxSeccion;
	private ComboBoxFiller comboBoxGrupo;
	private ComboBoxFiller comboBoxSGrupo;
	private ComboBoxFiller comboBoxMarca;
	private ComboBoxFiller comboBoxTalla;
	private ComboBoxFiller comboBoxColor;
	private XMLTextField barra;
	private JButton JBOk;
	private JButton JBCancel;
	private int rowIndex;
	protected int columnIndex;
	private Vector<XMLTextField> vfields;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1460278672241748778L;

	public EmakuTextileProduct(GenericForm GFforma,final JTable table) {
		this.genericForm=GFforma;
		this.table=table;
		this.addKeyListener(this);
		genericForm.addChangeExternalValueListener(this);

		panel = new JPanel(new BorderLayout());
		JPFields = new JPanel(new GridLayout(6,1));
		JPLabels = new JPanel(new GridLayout(6,1));

		JPMpopup = new JPopupMenu() {
			private static final long serialVersionUID = -8347328110027850041L;
			public void setVisible(boolean b) {
				super.setVisible(visible);	
			}
		};
		
		JPMpopup.setLayout(new BorderLayout());
		JPMpopup.setLightWeightPopupEnabled(true);
		JPMpopup.setBorderPainted(true);
		JPMpopup.add(panel,BorderLayout.CENTER);
		JPMpopup.add(new JPanel(),BorderLayout.NORTH);
		JPMpopup.add(new JPanel(),BorderLayout.WEST);
		
		comboBoxSeccion = new ComboBoxFiller(genericForm,"SEL0368","seccion",true);
		comboBoxGrupo = new ComboBoxFiller(genericForm,"SEL0370",new String[]{"seccion"},"grupo",true);
		comboBoxSGrupo = new ComboBoxFiller(genericForm,"SEL0372",new String[]{"seccion","grupo"},"sgrupo",true);
		comboBoxMarca = new ComboBoxFiller(genericForm,"MTSEL0041",new String[]{"seccion","grupo","sgrupo"},"marca",true);
		comboBoxTalla = new ComboBoxFiller(genericForm,"MTSEL0047",new String[]{"marca"},"talla",true);
		comboBoxColor = new ComboBoxFiller(genericForm,"MTSEL0048",new String[]{"marca"},"color",true);
		this.setSqlLocal("MTSEL0043");
		Dimension d = new Dimension(170,20);
		comboBoxSeccion.setPreferredSize(d);
		comboBoxGrupo.setPreferredSize(d);
		comboBoxSGrupo.setPreferredSize(d);
		comboBoxMarca.setPreferredSize(d);
		comboBoxTalla.setPreferredSize(d);
		comboBoxColor.setPreferredSize(d);
		vfields = new Vector<XMLTextField>();
		vfields.add(this);
		
/*		
		XMLTFCode.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String s = getValue(); 
				genericForm.setExternalValues(XMLTFCode.getExportvalue(),s);
				comboBoxCatalogue.exportar();
				comboBoxWarehouse.exportar();
			}
		});
		
*/	
		ImageIcon i1 = new ImageIcon(getClass().getResource(Icons.getIcon("ACEPTAR")));
		ImageIcon i2 = new ImageIcon(getClass().getResource(Icons.getIcon("EXIT")));
		
		JBOk = new JButton(i1);
		JBOk.setActionCommand("ok");
		JBOk.addActionListener(this);
		
		JBCancel = new JButton(i2);
		JBCancel.setActionCommand("cancel");
		JBCancel.addActionListener(this);
		
		JPanel buttons = new JPanel();
		buttons.add(JBOk);
		buttons.add(JBCancel);
		
		JPLabels.add(new JLabel("Seccion"));
		JPLabels.add(new JLabel("Grupo"));
		JPLabels.add(new JLabel("SubGrupo"));
		JPLabels.add(new JLabel("Marca"));
		JPLabels.add(new JLabel("Talla"));
		JPLabels.add(new JLabel("Color"));
				
		JPFields.add(new JPanel(new FlowLayout(FlowLayout.LEFT)).add(comboBoxSeccion));
		JPFields.add(new JPanel(new FlowLayout(FlowLayout.LEFT)).add(comboBoxGrupo));
		JPFields.add(new JPanel(new FlowLayout(FlowLayout.LEFT)).add(comboBoxSGrupo));
		JPFields.add(new JPanel(new FlowLayout(FlowLayout.LEFT)).add(comboBoxMarca));
		JPFields.add(new JPanel(new FlowLayout(FlowLayout.LEFT)).add(comboBoxTalla));
		JPFields.add(new JPanel(new FlowLayout(FlowLayout.LEFT)).add(comboBoxColor));
		
		panel.add(JPLabels,BorderLayout.WEST);
		panel.add(JPFields,BorderLayout.CENTER);
		JPMpopup.add(buttons,BorderLayout.SOUTH);
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!isEditable() && e.getClickCount() == 2) {
					showDetailed();
				}
			}
		});

	}
	
	public void showDetailed() {
		if (isEnabled() && isValid() &&!JPMpopup.isVisible()) {
			updateUI();
			visible = true;
			clean();
			int x = 0;
			int y = this.getHeight();
			try { JPMpopup.show(this,x,y); }
			catch (IllegalComponentStateException e) { }
		}
		comboBoxSeccion.requestFocus();
	}
	
	public void clean() {
		comboBoxSeccion.setSelectedIndex(0);
		comboBoxGrupo.removeAllItems();
		comboBoxSGrupo.removeAllItems();
		comboBoxMarca.removeAllItems();
		comboBoxTalla.removeAllItems();
		comboBoxColor.removeAllItems();
	}

	public String getValue() {
		return this.getText().trim();
	}

	public void setCode() {
		
	}

	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch(keyCode) {
			/*case KeyEvent.VK_ESCAPE:
				JPMpopup.setVisible(false);
				break;*/
			case KeyEvent.VK_F2:
					showDetailed();
				break;
			default :
				if (((keyCode >=60 &&  keyCode<=71) ||  keyCode>=65 &&  keyCode<=126)) {
					showDetailed();
				}
				break;
		}
		
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("ok".equals(command)) {
			close();
		}
		else if ("cancel".equals(command)) {
			close();
		}		
	}
	
	public void close() {
		visible = false;
		JPMpopup.setVisible(visible);
		table.requestFocus();
		table.changeSelection(rowIndex, columnIndex, false,false);
		try {
			table.getCellEditor().stopCellEditing();
		}
		catch(NullPointerException NPEe) {}
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	

	public void changeExternalValue(ExternalValueChangeEvent e) {
		String key = e.getExternalValue();
		if (key.equals("marca") || 
		    key.equals("talla") || 
			key.equals("color")) {
			EmakuUIFieldFiller filler = new EmakuUIFieldFiller(genericForm,
																null,
																true,
																"MTSEL0045",
																new String[]{genericForm.getExternalValueString("marca"),
																			genericForm.getExternalValueString("talla"),
																			genericForm.getExternalValueString("color")},null,
																vfields);
			filler.searchQuery();
		}
	}

}
