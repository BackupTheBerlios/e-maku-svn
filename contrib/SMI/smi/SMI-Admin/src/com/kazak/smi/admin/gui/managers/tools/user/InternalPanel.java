package com.kazak.smi.admin.gui.managers.tools.user;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.Cache.User;
import com.kazak.smi.admin.gui.managers.tools.ToolsConstants;
import com.kazak.smi.admin.gui.table.UserPosTable;

public class InternalPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<Component> componentsList = new ArrayList<Component>();
	private String[] adminLabels = {"Clave: ","Nombres: ","E-Mail: ", "Administrador: ","Auditor: ","Grupo: "};
	private String[] posLabels = {"Clave: ","Nombres: ","Control de Acceso por IP "};
	private JPasswordField passwdField;
	private JTextField nameField;
	private JTextField mailField;
	private JCheckBox adminCheck;
	private JCheckBox auditCheck;
	private JCheckBox ipControlCheck;
	private JComboBox groupsCombo;
	private UserPosTable table;
	private JDialog dialog;
	private int action;
	private boolean isAdmin;
	private User user;
	private String username;

	public InternalPanel(JDialog dialog, String username, boolean isAdmin, int action) {
		super();
		this.username = username;
		user = Cache.getUser(username);
		this.dialog = dialog;
		this.action = action;
		this.isAdmin = isAdmin;
		this.setLayout(new BorderLayout());
		initComponents();
		addComponents();
	}

	private void initComponents() {
		componentsList.add(passwdField = new JPasswordField());
		componentsList.add(nameField   = new JTextField());

		if(isAdmin) {
			initAdminComponents();
		} else {
			initUserComponents();
		}
	}
	
	private void initAdminComponents() {
		componentsList.add(mailField   = new JTextField());
		componentsList.add(adminCheck  = new JCheckBox());
		componentsList.add(auditCheck  = new JCheckBox());
		componentsList.add(groupsCombo = new JComboBox(Cache.getGroupsList()));
		setAdminContext();		
	}

	private void initUserComponents() {
		componentsList.add(ipControlCheck = new JCheckBox());
		setUserContext();
	}
	
	/*
	private boolean includeButtons() {
		switch(action) {
		case ToolsConstants.ADD:
		case ToolsConstants.EDIT:
		case ToolsConstants.EDIT_PREFILLED:
			return true;
		default:
			return false;
		}		
	}*/
	
	private void addComponents(){
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel labelsPanel = null;
		JPanel fieldsPanel = null;
		Border border = BorderFactory.createEtchedBorder();

		if(isAdmin) {		
			labelsPanel = new JPanel(new GridLayout(6,0));
			fieldsPanel = new JPanel(new GridLayout(6,0));

			for (int i=0 ; i< adminLabels.length ; i++) {
				labelsPanel.add(new JLabel(adminLabels[i]));
				fieldsPanel.add(componentsList.get(i));
			}
		} else {
			labelsPanel = new JPanel(new GridLayout(3,0));
			fieldsPanel = new JPanel(new GridLayout(3,0));

			for (int i=0 ; i< posLabels.length ; i++) {
				labelsPanel.add(new JLabel(posLabels[i]));
				fieldsPanel.add(componentsList.get(i));
			}

			JPanel southPanel = new JPanel(new BorderLayout());
			southPanel.add(new JPanel(),BorderLayout.WEST);
			southPanel.add(table.getPanel(),BorderLayout.CENTER);
			southPanel.add(new JPanel(),BorderLayout.EAST);
			centerPanel.add(southPanel,BorderLayout.SOUTH);
		}

		centerPanel.add(labelsPanel,BorderLayout.WEST);
		centerPanel.add(fieldsPanel,BorderLayout.CENTER);

		JPanel finalPanel = new JPanel(new BorderLayout());
		finalPanel.add(new JPanel(),BorderLayout.WEST);
		finalPanel.add(centerPanel,BorderLayout.CENTER);
		finalPanel.add(new JPanel(),BorderLayout.EAST);
		finalPanel.setBorder(border);
		this.add(finalPanel,BorderLayout.CENTER);

	}
	
	public void setAdminContext() {	
		switch(action) {
		// To Add
		case ToolsConstants.ADD:
			break;
			// To Edit
		case ToolsConstants.EDIT:
			break;
			// To Edit (filled)
		case ToolsConstants.EDIT_PREFILLED:
			break;
			// To Delete
		case ToolsConstants.DELETE:
			break;
			// To Delete (filled)
		case ToolsConstants.DELETE_PREFILLED:
			break;
			// To Search - To Search (filled)
		case ToolsConstants.SEARCH:
		case ToolsConstants.SEARCH_PREFILLED:
			adminCheck.setSelected(user.getAdmin());
			auditCheck.setSelected(user.getAudit());
			groupsCombo.setSelectedItem(user.getGroupName());
			mailField.setText(user.getEmail());
			nameField.setEditable(false);
			passwdField.setEnabled(false);
			groupsCombo.setEnabled(false);
			nameField.setText(user.getName());
			break;
		}
	}

	public void setUserContext() {	
		switch(action) {
		// To Add
		case ToolsConstants.ADD:
			table = new UserPosTable(dialog,username,true);
			break;
			// To Edit
		case ToolsConstants.EDIT:
			break;
			// To Edit (filled)
		case ToolsConstants.EDIT_PREFILLED:
			break;
			// To Delete
		case ToolsConstants.DELETE:
			break;
			// To Delete (filled)
		case ToolsConstants.DELETE_PREFILLED:
			break;
			// To Search - To Search (filled)
		case ToolsConstants.SEARCH:
		case ToolsConstants.SEARCH_PREFILLED:
			ipControlCheck.setSelected(user.getValidIp());
			nameField.setText(user.getName());
			nameField.setEditable(false);
			passwdField.setEnabled(false);
			ipControlCheck.setEnabled(false);
			table = new UserPosTable(dialog,username,false);
			break;
		}
	}
	
}
