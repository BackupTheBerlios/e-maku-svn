package com.kazak.comeet.admin.gui.managers.tools.pos;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

import com.kazak.comeet.admin.control.Cache;
import com.kazak.comeet.admin.control.Cache.WorkStation;
import com.kazak.comeet.admin.gui.managers.tools.Transactions.PosDocument;
import com.kazak.comeet.admin.gui.misc.AutoCompleteComboBox;
import com.kazak.comeet.admin.gui.misc.GUIFactory;
import com.kazak.comeet.admin.gui.managers.tools.MainForm;
import com.kazak.comeet.admin.gui.managers.tools.ButtonBar;
import com.kazak.comeet.admin.gui.managers.tools.ToolsConstants;
import com.kazak.comeet.admin.gui.managers.tools.Operation;
import com.kazak.comeet.admin.gui.table.UserPosTable;
import com.kazak.comeet.lib.misc.FixedSizePlainDocument;

public class PosPanel extends JPanel implements ActionListener, KeyListener { 
	
	private static final long serialVersionUID = 1L;
	private MainForm mainFrame;
	private int action;
	private String target;
	private ArrayList<Component> componentsList = new ArrayList<Component>();
	private AutoCompleteComboBox nameField;
	private JButton searchButton;
	private JTextField codeField;
	private JTextField ipField;
	private JComboBox groupsCombo;
	private String[] labels = {"Nombre: ","Código: ","IP: ","Grupo: "};
	private String newCode = "";
	private UserPosTable table;

	public PosPanel(MainForm mainFrame, int action, String target, UserPosTable table) {
		this.mainFrame = mainFrame;
		//this.buttonBar = buttonBar;
		this.action = action;
		this.target = target;
		this.table = table;
		initInterface();
		packPanels();
	}
	
	private void initInterface() {
		GUIFactory gui = new GUIFactory();
		searchButton = gui.createButton("search.png");
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);
		componentsList.add(nameField = new AutoCompleteComboBox(Cache.getWorkStationsList(),false,50,searchButton));
		componentsList.add(codeField = new JTextField());
		codeField.setEditable(false);
		componentsList.add(ipField   = new JTextField());
		ipField.setEditable(false);
		ipField.setName("IP");
		groupsCombo = new JComboBox(Cache.getGroupsList());
		groupsCombo.removeItem("COMEET");
		componentsList.add(groupsCombo);
		ipField.setDocument(new FixedSizePlainDocument(15));		
		setInitMode();
		this.setVisible(true);
	}
	
	private void setInitMode(){
		switch(action) {
			// To Add
		case ToolsConstants.ADD:
			mainFrame.setTitle("Nuevo Punto de Colocación");
			newCode = target;
			codeField.setText(newCode);
			ipField.setEditable(true);
			ButtonBar.setEnabledAcceptButton(false);
			break;
			// To Edit
		case ToolsConstants.EDIT:
			mainFrame.setTitle("Editar Punto de Colocación");
			ipField.setEditable(true);
			ipField.setFocusTraversalKeysEnabled(false);
			groupsCombo.setEnabled(true);
			break;
			// Edit pre-filled
		case ToolsConstants.EDIT_PREFILLED:
			mainFrame.setTitle("Editar Punto de Colocación");
			nameField.setSelectedItem(target);
			ipField.setEditable(true);
			ipField.setFocusTraversalKeysEnabled(false);
			groupsCombo.setEnabled(true);
			fillForm();
			break;
			// To Delete
		case ToolsConstants.DELETE:
			mainFrame.setTitle("Borrar Punto de Colocación");
			ButtonBar.setEnabledAcceptButton(false);
			groupsCombo.setEnabled(false);
			break;
			// Delete pre-filled
		case ToolsConstants.DELETE_PREFILLED:
			nameField.setSelectedItem(target);
			mainFrame.setTitle("Borrar Punto de Colocación");
			groupsCombo.setEnabled(false);
			fillForm();
			break;
			// To Search
		case ToolsConstants.SEARCH:
			mainFrame.setTitle("Buscar Punto de Colocación");
			disableFields();
			break;
			// Search pre-filled
		case ToolsConstants.SEARCH_PREFILLED:
			mainFrame.setTitle("Buscar Punto de Colocación");		
			doFilledSearch();
			disableFields();
			break;
		case ToolsConstants.LINK:
			mainFrame.setTitle("Asignar Punto a Usuario");
			ButtonBar.setEnabledAcceptButton(false);
			disableFields();
			break;			
		}	
	}
	
	private void fillForm() {
		if (Cache.containsWs(target)) {
			WorkStation ws = Cache.getWorkStation(target);
			switch(action) {
			case ToolsConstants.ADD:
				//ipField.setEditable(true);
				break;
			case ToolsConstants.EDIT:
			case ToolsConstants.EDIT_PREFILLED:
				ipField.setEditable(true);
				groupsCombo.setEnabled(true);
				codeField.setText(ws.getCode());
				ipField.setText(ws.getIp());
				groupsCombo.setSelectedItem(ws.getGroupName());
				ipField.addKeyListener(this);
				ipField.requestFocus();
				break;
			case ToolsConstants.DELETE:
			case ToolsConstants.DELETE_PREFILLED:
				ButtonBar.setEnabledAcceptButton(true);
			case ToolsConstants.SEARCH:
			case ToolsConstants.SEARCH_PREFILLED:
			case ToolsConstants.LINK:
				codeField.setText(ws.getCode());
				ipField.setText(ws.getIp());
				groupsCombo.setSelectedItem(ws.getGroupName());
				nameField.requestFocus();
				if(!ButtonBar.isAcceptButtonActive()) {
					ButtonBar.setEnabledAcceptButton(true);
				}
				break;
			}
		} else {
			if ((action != ToolsConstants.ADD)) {
				JOptionPane.showMessageDialog(mainFrame,"El punto de venta no existe. ");
				reset();
			} else {
				codeField.setText(newCode);
				ipField.setEditable(true);
				groupsCombo.setEnabled(true);
				ipField.requestFocus();
				ipField.addKeyListener(this);
				ButtonBar.setEnabledAcceptButton(true);
			}
		}			
	}
	
	public String[] getFormData(){
		String[] data = new String[5];
		
		data[0] = target;
		data[1] = codeField.getText();
		data[2] = ipField.getText();
		data[3] = (String) groupsCombo.getSelectedItem();
		data[4] = nameField.getText();
		
		return data;
	}
		
	public void executeOperation() {
		PosData pos = new PosData(mainFrame,getFormData(),ipField,action);
		if (!pos.verifyData()) {
			return;
		}
		PosDocument doc = new PosDocument(getFormData());
		switch(action) {
			// To Add
		case ToolsConstants.ADD:
			Operation.execute(doc.getDocumentToAdd());
			mainFrame.dispose();
			break;
			// To Edit
		case ToolsConstants.EDIT:
			// To Edit (filled)
		case ToolsConstants.EDIT_PREFILLED:
			Operation.execute(doc.getDocumentToEdit());
			mainFrame.dispose();
			break;
			// To Delete
		case ToolsConstants.DELETE:
			Operation.execute(doc.getDocumentToDelete());
			mainFrame.dispose();
			break;
			// To Delete (filled)
		case ToolsConstants.DELETE_PREFILLED:
			Operation.execute(doc.getDocumentToDelete());
			mainFrame.dispose();
			break;
		case ToolsConstants.LINK:
			table.insertData(doc.getDocumentToLink());
			mainFrame.dispose();
			break;
		}	   
	}
	
	private void packPanels() {
		JPanel labelsPanel = new JPanel(new GridLayout(labels.length,0));
		JPanel fieldsPanel = new JPanel(new GridLayout(labels.length,0));
		
		for (int i=0 ; i< labels.length ; i++) {
			labelsPanel.add(new JLabel(labels[i]));
			fieldsPanel.add(componentsList.get(i));
		}
		
		JPanel searchPanel = new JPanel();		
		searchPanel.setLayout(new BoxLayout(searchPanel,BoxLayout.Y_AXIS));
		searchPanel.setPreferredSize(new Dimension(40,90));
		searchPanel.add(searchButton);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(labelsPanel,BorderLayout.WEST);
		centerPanel.add(fieldsPanel,BorderLayout.CENTER);

		this.add(centerPanel,BorderLayout.CENTER);
		this.add(searchPanel,BorderLayout.EAST);
	}
		
	public void clean() {
		switch(action) {
			// To Add
		case ToolsConstants.ADD:
			ipField.setText("");
			ipField.setEditable(true);
			break;
			// To Edit, to Delete (unfilled,filled)
		case ToolsConstants.LINK:
		case ToolsConstants.EDIT:
		case ToolsConstants.EDIT_PREFILLED:
		case ToolsConstants.DELETE:
		case ToolsConstants.DELETE_PREFILLED:
			resetPanel();
			break;
			// To Search
		case ToolsConstants.SEARCH:
		case ToolsConstants.SEARCH_PREFILLED:
			reset();
			break;
		}	
	}
		
	private void reset() {
		nameField.setEditable(true);
		nameField.blankTextField();
		codeField.setText("");
		ipField.setText("");
		ipField.setEditable(false);
		groupsCombo.setSelectedIndex(0);
		groupsCombo.setEnabled(false);	
	}
	
	private void resetPanel() {
		reset();
		ButtonBar.setEnabledAcceptButton(false);
	}
	
	private void disableFields() {
		codeField.setEditable(false);
		ipField.setEditable(false);
		groupsCombo.setEnabled(false);
	}
	
	private void doFilledSearch() {
		nameField.setSelectedItem(target);
		doSearch();
	}
	
	private void doSearch() {
		target = nameField.getText();
		if(target.length() > 0) {
			fillForm();		
		} else {
			resetPanel();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("search")) {
			doSearch();
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
        if (keyCode==KeyEvent.VK_ENTER || keyCode==KeyEvent.VK_TAB){ 
        	JTextField ipText = (JTextField) e.getSource();
        	String name = ipText.getName();
        	if (name.equals("IP")) {
        		groupsCombo.requestFocus();
        	}
        }
	}

	public void keyTyped(KeyEvent e) {
	}
}
