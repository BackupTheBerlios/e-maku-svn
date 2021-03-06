package com.kazak.comeet.admin.gui.managers.tools.user;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.kazak.comeet.admin.control.Cache;
import com.kazak.comeet.admin.gui.managers.tools.ToolsConstants;
import com.kazak.comeet.admin.gui.managers.tools.ButtonBar;
import com.kazak.comeet.admin.gui.misc.CaseLessCombo;
import com.kazak.comeet.admin.gui.misc.GUIFactory;

public class UserPanel extends JPanel implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = 1L;
	private UserDialog userDialog;
	private int action;
	private String target;
	private CaseLessCombo nameField;
	private JButton searchButton;
	private boolean isAdmin;
	private String secondName = "Operativo";
	
	public UserPanel(UserDialog userDialog, int action, String target, boolean isAdmin) {
		this.setLayout(new BorderLayout());
		this.userDialog = userDialog;
		this.action = action;
		this.target = target;
		this.isAdmin = isAdmin;
		if(isAdmin) {
			secondName = "Administrativo";
		}
		initInterface();
		packPanels();		
	}
	
	private void initInterface() {
		GUIFactory gui = new GUIFactory();
		searchButton = gui.createButton("search.png");
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);
		if(isAdmin) {
			nameField = new CaseLessCombo(Cache.getAdminUsersList(),50,searchButton);
		} else {
			nameField = new CaseLessCombo(Cache.getOperativeUsersList(),50,searchButton);
		}
		setInitMode();
		this.setVisible(true);
	}
	
	private void setInitMode(){
		switch(action) {
		// To Add
		case ToolsConstants.ADD:
			userDialog.setTitle("Nuevo Usuario " + secondName);
			break;
			// To Edit
		case ToolsConstants.EDIT:
			userDialog.setTitle("Editar Usuario " + secondName);
			break;
			// Edit pre-filled
		case ToolsConstants.EDIT_PREFILLED:
			userDialog.setTitle("Editar Usuario " + secondName);
			nameField.setSelectedItem(target);
			break;
			// To Delete
		case ToolsConstants.DELETE:
			userDialog.setTitle("Borrar Usuario " + secondName);
			ButtonBar.setEnabledAcceptButton(false);
			break;
			// Delete pre-filled
		case ToolsConstants.DELETE_PREFILLED:
			nameField.setSelectedItem(target);
			userDialog.setTitle("Borrar Usuario " + secondName);
			break;
			// To Search
		case ToolsConstants.SEARCH:
			userDialog.setTitle("Buscar Usuario " + secondName);
			
			break;
			// Search pre-filled
		case ToolsConstants.SEARCH_PREFILLED:
			userDialog.setTitle("Buscar Usuario " + secondName);
			nameField.setSelectedItem(target);
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
	
	public void clean() {
			nameField.activeCombo(true);
			nameField.blankTextField();
			nameField.requestFocus();
			searchButton.setEnabled(true);
			if(userDialog.isPanelVisible()) {
				userDialog.collapseInternalPanel();
			}
	}
		
	private void doSearch() {
		target = nameField.getText();
		if (isValidUser()) {
			if(!isAdmin) {
				userDialog.setAdminFlag(false);
			}
			else { 
				userDialog.setAdminFlag(true);
			}
			nameField.setEnabled(false);
			searchButton.setEnabled(false);
			userDialog.expandInternalPanel(target);
		}
	}

	private boolean isValidUser() {
		if(target.length() == 0) {
			return false;
		}
		
		if(isAdmin) {
			if (Cache.isOperatorUser(target)) {
				JOptionPane.showMessageDialog(userDialog,"El usuario " + target + " es operativo. ");
				return false;
			}
			if (Cache.isAdminUser(target)) {
				switch(action) {
				case ToolsConstants.ADD:
					if(!nameField.eventFromCombo()) {
						JOptionPane.showMessageDialog(userDialog,"El usuario " + target + " ya existe. ");
					}
					return false;
				}	
			} else {
				if (action != ToolsConstants.ADD) {
					JOptionPane.showMessageDialog(userDialog,"El usuario " + target + " no existe. ");
					return false;
				} 
			}
			
		} else {
			if (Cache.isAdminUser(target)) {
				JOptionPane.showMessageDialog(userDialog,"El usuario " + target + " es administrativo. ");
				return false;
			}
			if (Cache.isOperatorUser(target)) {
				switch(action) {
				case ToolsConstants.ADD:
					if(!nameField.eventFromCombo()) {
						JOptionPane.showMessageDialog(userDialog,"El usuario " + target + " ya existe. ");
					}
					return false;
				}	
			} else {
				if (action != ToolsConstants.ADD) {
					JOptionPane.showMessageDialog(userDialog,"El usuario " + target + " no existe. ");
					return false;
				} 
			}			
		}
	
		return true;
	}
	
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("search")) {
			doSearch();
		}
	}
	
	public String getLogin() {
		return target;
	}
	
	public void enablePanel(boolean flag) {
		nameField.setEditable(flag);
		nameField.activeCombo(flag);
		searchButton.setEnabled(flag);
	}
}
