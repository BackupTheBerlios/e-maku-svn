package com.kazak.smi.admin.gui.managers.tools.group;

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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.Cache.Group;
import com.kazak.smi.admin.gui.managers.tools.Transactions.GroupDocument;
import com.kazak.smi.admin.gui.managers.tools.Operation;
import com.kazak.smi.admin.gui.managers.tools.MainForm;
import com.kazak.smi.admin.gui.managers.tools.ButtonBar;
import com.kazak.smi.admin.gui.managers.tools.ToolsConstants;
import com.kazak.smi.admin.gui.misc.AutoCompleteComboBox;
import com.kazak.smi.admin.gui.misc.GUIFactory;

public class GroupPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private MainForm mainFrame;
	private int action;
	private String target;
	private JButton searchButton;
	private ButtonBar buttonBar;
	private ArrayList<Component> componentsList = new ArrayList<Component>();
	private AutoCompleteComboBox nameField;
	private JCheckBox visibleCheck;
	private JCheckBox zoneCheck;
	private String[] labels = {"Nombre: ","Visible para clientes","Es Zona"};

	public GroupPanel(MainForm mainFrame, ButtonBar buttonBar, int action, String target) {
		this.mainFrame = mainFrame;
		this.buttonBar = buttonBar;
		this.action = action;
		this.target = target;
		initInterface();
		packPanels();		
	}
	
	private void initInterface() {
		GUIFactory gui = new GUIFactory();
		searchButton = gui.createButton("search.png");
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);

		componentsList.add(nameField = new AutoCompleteComboBox(Cache.getGroupsList(),false,50,searchButton));
		componentsList.add(visibleCheck= new JCheckBox());
		componentsList.add(zoneCheck   = new JCheckBox());
		
		setInitMode();
		this.setVisible(true);
	}
	
	private void setInitMode(){
		switch(action) {
			// To Add
		case ToolsConstants.ADD:
			mainFrame.setTitle("Nuevo Grupo");
			buttonBar.setEnabledAcceptButton(false);
			break;
			// To Edit
		case ToolsConstants.EDIT:
			mainFrame.setTitle("Editar Grupo");
			break;
			// Edit pre-filled
		case ToolsConstants.EDIT_PREFILLED:
			mainFrame.setTitle("Editar Grupo");
			nameField.setSelectedItem(target);
			fillForm();
			break;
			// To Delete
		case ToolsConstants.DELETE:
			mainFrame.setTitle("Borrar Grupo");
			buttonBar.setEnabledAcceptButton(false);
			break;
			// Delete pre-filled
		case ToolsConstants.DELETE_PREFILLED:
			nameField.setSelectedItem(target);
			mainFrame.setTitle("Borrar Grupo");
			fillForm();
			break;
			// To Search
		case ToolsConstants.SEARCH:
			mainFrame.setTitle("Buscar Grupo");
			disableFields();
			break;
			// Search pre-filled
		case ToolsConstants.SEARCH_PREFILLED:
			mainFrame.setTitle("Buscar Grupo");			
			doFilledSearch();
			disableFields();
			break;
		}	
	}
	
	private void fillForm() {
		if (Cache.containsGroup(target)) {
			Group group = Cache.getGroup(target);
			
			switch(action) {
			case ToolsConstants.ADD:
				break;
			case ToolsConstants.EDIT:
			case ToolsConstants.EDIT_PREFILLED:
				break;
			case ToolsConstants.DELETE:
			case ToolsConstants.DELETE_PREFILLED:
				buttonBar.setEnabledAcceptButton(true);
			case ToolsConstants.SEARCH:
			case ToolsConstants.SEARCH_PREFILLED:
				nameField.requestFocus();
				visibleCheck.setSelected(group.isVisible());
				zoneCheck.setSelected(group.isZone());
				break;
			}
		} else {
			if (action != ToolsConstants.ADD) {
				JOptionPane.showMessageDialog(mainFrame,"El grupo no existe. ");
				reset();
			} else {
				buttonBar.setEnabledAcceptButton(true);
			}
		}			
	}

	public String[] getFormData(){
		String[] data = new String[5];	
		data[0] = target;
		data[1] = "";
		data[2] = "";
		data[3] = "";
		data[4] = "";
		
		return data;
	}	
	
	public void executeOperation() {
				
		GroupDocument doc = new GroupDocument(getFormData());
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
		searchPanel.setPreferredSize(new Dimension(40,69));
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
			break;
			// To Edit
		case ToolsConstants.EDIT:
			break;
			// To Delete
		case ToolsConstants.DELETE:
			break;
			// To Search
		case ToolsConstants.SEARCH:
			break;
		}	
	}
	
	private void reset() {
		nameField.setEditable(true);
		nameField.blankTextField();
	}
	
	private void resetPanel() {
		reset();
		buttonBar.setEnabledAcceptButton(false);
	}
	
	private void disableFields() {
	}

	private void doFilledSearch() {
		nameField.setSelectedItem(target);
		doSearch();
	}
	
	private void doSearch() {
		target = nameField.getText();
		fillForm();		
	}
	
	public void keyPressed(KeyEvent e) {	
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {	
	}
	
	public void actionPerformed(ActionEvent e) {	
	}
}
