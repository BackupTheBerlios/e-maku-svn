package com.kazak.smi.admin.gui.managers.tools.user;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.Cache.User;
import com.kazak.smi.admin.gui.managers.tools.Operation;
import com.kazak.smi.admin.gui.managers.tools.ToolsConstants;
import com.kazak.smi.admin.gui.managers.tools.Transactions.UserDocument;
import com.kazak.smi.admin.gui.managers.tools.user.UserData;
import com.kazak.smi.admin.gui.managers.tools.ButtonBar;
import com.kazak.smi.admin.gui.misc.AutoCompleteComboBox;
import com.kazak.smi.admin.gui.misc.GUIFactory;

public class UserPanel extends JPanel implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = 1L;
	private UserDialog userDialog;
	private int action;
	private String target;
	private ButtonBar buttonBar;
	private ArrayList<Component> componentsList = new ArrayList<Component>();
	private AutoCompleteComboBox nameField;
	private JButton searchButton;
	private JPanel tmp = new JPanel();
	
	public UserPanel(UserDialog userDialog, ButtonBar buttonBar,int action, String target) {
		this.setLayout(new BorderLayout());
		this.userDialog = userDialog;
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

		nameField = new AutoCompleteComboBox(Cache.getUsersList(),false,50,searchButton);
		
		setInitMode();
		this.setVisible(true);
	}
	
	private void setInitMode(){
		switch(action) {
		// To Add
		case ToolsConstants.ADD:
			userDialog.setTitle("Nuevo Usuario");
			buttonBar.setEnabledAcceptButton(false);
			break;
			// To Edit
		case ToolsConstants.EDIT:
			userDialog.setTitle("Editar Usuario");
			break;
			// Edit pre-filled
		case ToolsConstants.EDIT_PREFILLED:
			userDialog.setTitle("Editar Usuario");
			nameField.setSelectedItem(target);
			break;
			// To Delete
		case ToolsConstants.DELETE:
			userDialog.setTitle("Borrar Usuario");
			buttonBar.setEnabledAcceptButton(false);
			break;
			// Delete pre-filled
		case ToolsConstants.DELETE_PREFILLED:
			nameField.setSelectedItem(target);
			userDialog.setTitle("Borrar Usuario");
			break;
			// To Search
		case ToolsConstants.SEARCH:
			userDialog.setTitle("Buscar Usuario");
			break;
			// Search pre-filled
		case ToolsConstants.SEARCH_PREFILLED:
			userDialog.setTitle("Buscar Usuario");			
			break;
		}			
	}
	
	private void packPanels() {
		Border border = BorderFactory.createEtchedBorder();
		
		JPanel labelsPanel = new JPanel(new GridLayout(1,0));
		JPanel fieldsPanel = new JPanel(new GridLayout(1,0));
		JPanel searchPanel = new JPanel(new GridLayout(1,0));
		
		labelsPanel.add(new JLabel("Login: "));
		fieldsPanel.add(nameField);
		searchPanel.add(searchButton); 
		
		JPanel internalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		internalPanel.add(labelsPanel);
		internalPanel.add(fieldsPanel);
		internalPanel.add(searchPanel);
				
		JPanel externalPanel = new JPanel(new BorderLayout());
		externalPanel.add(internalPanel,BorderLayout.CENTER);
		externalPanel.setBorder(border);
		
		this.add(externalPanel,BorderLayout.CENTER);
	}
	
	private String[] getFormData(){
		String[] data = new String[5];
		data[0] = target;
		data[1] = "";
		data[2] = "";
		data[3] = "";
		data[4] = "";
		
		return data;
	}
	
	public void executeOperation() {
		UserData user = new UserData();
		if (!user.verifyData()) {
			return;
		}

		UserDocument doc = new UserDocument(getFormData());
		switch(action) {
			// To Add
		case ToolsConstants.ADD:
			Operation.execute(doc.getDocumentToAdd());
			userDialog.dispose();
			break;
			// To Edit
		case ToolsConstants.EDIT:
			// To Edit (filled)
		case ToolsConstants.EDIT_PREFILLED:
			Operation.execute(doc.getDocumentToEdit());
			userDialog.dispose();
			break;
			// To Delete
		case ToolsConstants.DELETE:
			Operation.execute(doc.getDocumentToDelete());
			userDialog.dispose();
			break;
			// To Delete (filled)
		case ToolsConstants.DELETE_PREFILLED:
			Operation.execute(doc.getDocumentToDelete());
			userDialog.dispose();
			break;
		}	   		
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
	
	private void doSearch() {
		target = nameField.getText();
		userDialog.expandInternalPanel(target);
		//fillForm();		
	}

	private void fillForm() {
		if (Cache.containsUser(target)) {
			User user = Cache.getUser(target);
			
			switch(action) {
			case ToolsConstants.ADD:
				JOptionPane.showMessageDialog(userDialog,"El usuario " + target + " ya existe. ");
				nameField.blankTextField();
				nameField.requestFocus();
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
				tmp.add(new JLabel("Bullet proof!"));
				tmp.updateUI();
				break;
			}
		} else {
			if (action != ToolsConstants.ADD) {
				JOptionPane.showMessageDialog(userDialog,"El usuario " + target + " no existe. ");
				//resetPanel();
			} else {
				buttonBar.setEnabledAcceptButton(true);
			}
		}			
	}
	
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("search")) {
			doSearch();
		}
	}

}
