package com.kazak.comeet.admin.gui.managers.tools.user;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.kazak.comeet.admin.control.Cache;
import com.kazak.comeet.admin.control.Cache.User;
import com.kazak.comeet.admin.gui.managers.tools.ButtonBar;
import com.kazak.comeet.admin.gui.managers.tools.ToolsConstants;
import com.kazak.comeet.admin.gui.table.UserPosTable;
import com.kazak.comeet.lib.misc.MD5Tool;

public class InternalPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<Component> componentsList = new ArrayList<Component>();
	private String[] adminLabels = {"Clave: ","Nombres: ","E-Mail: ", "Administrador: ","Auditor: ","Grupo: "};
	private String[] posLabels = {"Clave: ","Nombres: ","Control de Acceso por IP "};
	private JPasswordField passwdField;
	private JTextField nameField;
	private JTextField mailField;
	private JCheckBox sysAdminCheck;
	private JCheckBox auditCheck;
	private JCheckBox ipControlCheck;
	private JComboBox groupsCombo;
	private UserPosTable table;
	private JFrame dialog;
	private int action;
	private boolean isAdmin;
	private User user;
	private String username;

	public InternalPanel(JFrame dialog, String username, boolean isAdmin, int action) {
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
		componentsList.add(sysAdminCheck  = new JCheckBox());
		componentsList.add(auditCheck  = new JCheckBox());
		componentsList.add(groupsCombo = new JComboBox(Cache.getGroupsList()));
		setAdminContext();		
	}

	private void initUserComponents() {
		componentsList.add(ipControlCheck = new JCheckBox());
		setUserContext();
	}

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
			southPanel.add(new JLabel("Puntos de Trabajo",JLabel.CENTER),BorderLayout.NORTH);
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

	private void activeAdminPanel(boolean flag) {
		sysAdminCheck.setEnabled(flag);
		auditCheck.setEnabled(flag);
		nameField.setEditable(flag);
		groupsCombo.setEnabled(flag);
		mailField.setEditable(flag);
		passwdField.setEnabled(flag);
	}
	
	private void setAdminDataPanel() {
		sysAdminCheck.setSelected(user.getAdmin());
		auditCheck.setSelected(user.getAudit());
		groupsCombo.setSelectedItem(user.getGroupName());
		mailField.setText(user.getEmail());
		nameField.setText(user.getName());		
	}
	
	public void setAdminContext() {	
		switch(action) {
		// To Add
		case ToolsConstants.ADD:
			break;
			// To Edit
			// To Edit (filled)
		case ToolsConstants.EDIT:
		case ToolsConstants.EDIT_PREFILLED:
			setAdminDataPanel();
			activeAdminPanel(true);
			break;
			// To Delete - To Delete (filled) - To Search - To Search (filled)
		case ToolsConstants.DELETE:
		case ToolsConstants.DELETE_PREFILLED:
			ButtonBar.setEnabledAcceptButton(true);
		case ToolsConstants.SEARCH:
		case ToolsConstants.SEARCH_PREFILLED:
			setAdminDataPanel();
			activeAdminPanel(false);
			break;
		}
	}
	
	private void activeUserPanel(boolean flag) {
		nameField.setEditable(flag);
		passwdField.setEnabled(flag);
		ipControlCheck.setEnabled(flag);
	}
	
	private void setUserPanel(boolean flag) {
		ipControlCheck.setSelected(user.getValidIp());
		nameField.setText(user.getName());
		activeUserPanel(flag);
		table = new UserPosTable(dialog,username,false,action);
	}
	
	public void setUserContext() {	
		switch(action) {
		// To Add
		case ToolsConstants.ADD:
			table = new UserPosTable(dialog,username,true,action);
			break;
			// To Edit
			// To Edit (filled)
		case ToolsConstants.EDIT:
		case ToolsConstants.EDIT_PREFILLED:
			setUserPanel(true);
			break;
			// To Delete - To Delete (filled) - To Search - To Search (filled)
		case ToolsConstants.DELETE:
		case ToolsConstants.DELETE_PREFILLED:
			ButtonBar.setEnabledAcceptButton(true);
		case ToolsConstants.SEARCH:
		case ToolsConstants.SEARCH_PREFILLED:
			setUserPanel(false);
			break;
		}
	}
	
	public String getPasswd() {
		String passwd = new String(passwdField.getPassword()).trim();
		if(passwd.length() > 0) {
			MD5Tool md5 = new MD5Tool(passwd);
			passwd = md5.getDigest();
		} else {
			passwd = "";
		}
		return passwd;
	}
	
	public String getUserName() {
		return nameField.getText();
	}
	
	public String getUserMail() {
		if(isAdmin) {
			return mailField.getText();
		} else {
			return "";
		}
	}
	
	public String getUserGroup() {
		if(isAdmin) {
			String group = groupsCombo.getSelectedItem().toString();
			Cache.Group groupObject = Cache.getGroup(group);
			return groupObject.getId();
		} else {
			return "1";
		}
	}

	public boolean doIPCheck() {
		if(isAdmin) {
			return false;
		} else {
			return ipControlCheck.isSelected();
		}
	}
	
	public String isAdmin() {
		if(isAdmin) {
			return String.valueOf(sysAdminCheck.isSelected());
		}
		else {
			return "f";
		}
	}

	public String isAuditor() {
		if(isAdmin) {
			if(auditCheck.isSelected()) {
				return "t";
			}
			else {
				return "f";
			} 
		} else {
			return "f";
		}
	}
	
	public Vector<String> getPosCodes() {
		return table.getPosCodeVector();
	}

}
