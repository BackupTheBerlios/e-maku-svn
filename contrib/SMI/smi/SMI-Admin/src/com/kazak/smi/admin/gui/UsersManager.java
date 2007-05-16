package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.Cache.Group;
import com.kazak.smi.admin.control.Cache.User;
import com.kazak.smi.admin.gui.TreeManagerGroups.SortableTreeNode;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.lib.misc.FixedSizePlainDocument;
import com.kazak.smi.lib.misc.MD5Tool;

public class UsersManager extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private AutoCompleteComboBox loginField;
	private JPasswordField passwdField;
	private JTextField nameField;
	private JTextField mailField;
	private JCheckBox adminCheck;
	private JCheckBox auditCheck;
	private JPanel centerPanel;
	private JPanel southPanel;
	private JButton acceptButton;
	private JButton cancelButton;
	private JButton cleanButton;
	private ArrayList<Component> componentsList = new ArrayList<Component>();
	private UserTable table;
	private JComboBox groupsCombo;
	private JButton searchButton;
	private JPanel searchPanel;
	private String oldLogin;
	private enum ACTIONS  {ADD,EDIT,SEARCH,DELETE};
	private ACTIONS ACTION;

	private String[]
    labels = {
			"Login",
			"Clave",
			"Nombres",
			"E-Mail",
			"Administrador",
			"Auditor",
			"Grupo"};
	
	public UsersManager() {
		this.setLayout(new BorderLayout());
		this.setSize(350,370);
		this.setLocationByPlatform(true);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComponents();
		addComponents();
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CodeSearchEngine(loginField.getText());
			}
		});
	}
	
	public void add() {
		this.setTitle("Nuevo Usuario");
		this.setVisible(true);
		this.ACTION = ACTIONS.ADD;
		acceptButton.setActionCommand("accept");
		cancelButton.setActionCommand("cancel");
	}
	
	public void edit() {
		this.setTitle("Editar Usuario");
		for (int i=1 ; i< labels.length ; i++) {
			Component component = componentsList.get(i);
			component.setEnabled(false);
			if (component instanceof JTextField) {
				((JTextField)component).setDisabledTextColor(Color.BLACK);
			}
		}
		table.disableButtons();
		table.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		acceptButton.setActionCommand("save");
		cancelButton.setActionCommand("cancel");
		acceptButton.setEnabled(false);
		this.ACTION = ACTIONS.EDIT;
	}
		
	public void delete() {
		this.setTitle("Borrar Usuario");
		for (int i=1 ; i< labels.length ; i++) {
			Component component = componentsList.get(i);
			component.setEnabled(false);
			if (component instanceof JTextField) {
				((JTextField)component).setDisabledTextColor(Color.BLACK);
			}
		}
		table.disableButtons();
		table.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		acceptButton.setActionCommand("remove");
		cancelButton.setActionCommand("cancel");
		acceptButton.setEnabled(false);
		this.ACTION = ACTIONS.DELETE;
	}
	
	public void search() {
		this.setTitle("Buscar Usuario");
		for (int i=1 ; i< labels.length ; i++) {
			Component component = componentsList.get(i);
			component.setEnabled(false);
			if (component instanceof JTextField) {
				((JTextField)component).setDisabledTextColor(Color.BLACK);
			}
		}
		table.disableButtons();
		table.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		
		acceptButton.setEnabled(false);
		cancelButton.setActionCommand("cancel");
		this.ACTION = ACTIONS.SEARCH;
	}
	
	private void initComponents() {
		
		componentsList.add(loginField  = new AutoCompleteComboBox(Cache.getUsersList(),true,30));
		componentsList.add(passwdField = new JPasswordField());
		componentsList.add(nameField   = new JTextField());
		componentsList.add(mailField   = new JTextField());
		componentsList.add(adminCheck  = new JCheckBox());
		componentsList.add(auditCheck  = new JCheckBox());
		componentsList.add(groupsCombo = new JComboBox(Cache.getGroupsList()));
		
		loginField.addItemListener(this);
		passwdField.setDocument(new FixedSizePlainDocument(10));
		nameField.setDocument(new FixedSizePlainDocument(100));
		mailField.setDocument(new FixedSizePlainDocument(200));
		
		table = new UserTable(this);
		acceptButton = new JButton("Aceptar");
		cancelButton = new JButton("Cancelar");
		GUIFactory gui = new GUIFactory();
		searchButton = gui.createButton("search.png");
		searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel,BoxLayout.Y_AXIS));
		searchPanel.add(searchButton);
		
		acceptButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		adminCheck.addItemListener(new ItemListener() {		
			public void itemStateChanged(ItemEvent e) {
				if (adminCheck.isSelected()){
					auditCheck.setSelected(false);
				}
			}
		});
		
		auditCheck.addItemListener(new ItemListener() {		
			public void itemStateChanged(ItemEvent e) {
				if (auditCheck.isSelected()){
					adminCheck.setSelected(false);
				}
			}
		});
		
		centerPanel = new JPanel(new BorderLayout());
		southPanel  = new JPanel(new FlowLayout(FlowLayout.CENTER));
	}
	
	private void addComponents() {
		JPanel labelsPanel = new JPanel(new GridLayout(labels.length,0));
		JPanel fieldsPanel = new JPanel(new GridLayout(labels.length,0));
		
		for (int i=0 ; i< labels.length ; i++) {
			labelsPanel.add(new JLabel(labels[i]));
			fieldsPanel.add(componentsList.get(i));
		}
		
		centerPanel.add(labelsPanel,BorderLayout.WEST);
		centerPanel.add(fieldsPanel,BorderLayout.CENTER);

		JPanel center = new JPanel(new BorderLayout());
		center.add(centerPanel,BorderLayout.NORTH);
		center.add(table.getPanel(),BorderLayout.CENTER);
		
		southPanel.add(acceptButton);
		southPanel.add(cancelButton);
		
		cleanButton = new JButton("Limpiar");
		cleanButton.setActionCommand("clean");
		cleanButton.addActionListener(this);
		southPanel.add(cleanButton);
		
		this.add(center,BorderLayout.CENTER);
		this.add(southPanel,BorderLayout.SOUTH);
		this.add(new JPanel(),BorderLayout.NORTH);
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(searchPanel,BorderLayout.EAST);
		
		this.setVisible(true);
	}
	
	
	// Manejo de Eventos segun la operacion a realizar (Adicion, Edicion, Eliminacion)
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Document document = null;
		TreeManagerGroups.currentTreePath = new TreePath(
				new Object[] {
							new SortableTreeNode(MainWindow.getAppOwner()),
							new SortableTreeNode(getGroupsCombo().getSelectedItem()),
							new SortableTreeNode(loginField.getText())});
		if (command.equals("cancel")) {
			this.dispose();
		}
		else if (command.equals("clean")) {
			this.clean();
		}
		else if (command.equals("accept")) {
			String login = loginField.getText();
			if (!"".equals(login)) {
				User user = Cache.getUser(login);
				if (user==null) {
					String names = nameField.getText();
					if(names.length() == 0) {
						JOptionPane.showMessageDialog(
								this,
								"<html><center>" +
								"Debe asociar un nombre al usuario." +
								"</center></html>");
						nameField.requestFocus();
						return;						
					}
					String string = groupsCombo.getSelectedItem().toString();
					Group group = Cache.getGroup(string);
					if (group.isZone() && table.getRowCount() <= 0) {
						JOptionPane.showMessageDialog(
								this,
								"<html><center>" +
								"Ha seleccionado un grupo que es zona.<br>" +
								"Por favor, adicione un punto de colocación." +
								"</center></html>");
						return;
					}
					document = getDocumentToAdd();
					this.dispose();
				}
				else {
					JOptionPane.showMessageDialog(
							this,
							"<html><center>" +
							"Ya existe un usuario con ese login.<br>" +
							"Por favor, ingrese uno diferente o compruebe <br>" +
							"con el botón de búsqueda."+
							"</center></html>");
					loginField.blankTextField();
				}
			}
			else {
				JOptionPane.showMessageDialog(
					this,
					"<html><center>" +
					"Por lo menos debe digitar el login de usuario,<br>" + 
					"su nombre y seleccionar un grupo." +
					"</center></html>");
				loginField.requestFocus();
			}
		}
		else if (command.equals("save")) {
			String login = loginField.getText();
			if (!"".equals(login)) {
				User user = Cache.getUser(oldLogin);
				if (oldLogin.equals(login)) {
					user = null;
				}
				if (user==null) {
					String string = groupsCombo.getSelectedItem().toString();
					Group group = Cache.getGroup(string);
					if (group.isZone() && table.getRowCount() <= 0) {
						JOptionPane.showMessageDialog(
								this,
								"<html><center>" +
								"Ha seleccionado un grupo que es zona.<br>" +
								"Por favor, adicione un punto de colocación." +
								"</center></html>");
						return;
					}
					document = getDocumentToEdit();
					this.dispose();
				}
				else {
					JOptionPane.showMessageDialog(
							this,
							"<html><center>" +
							"Ya existe un usuario con ese login.<br>" +
							"Por favor, ingrese uno diferente o compruebe <br>" +
							"con el botón de búsqueda."+
							"</center></html>");
					loginField.blankTextField();
				}
			}
			else {
				JOptionPane.showMessageDialog(
					this,
					"<html><center>" +
					"Al menos debe digitar el login de usuario," +
					"su nombre y seleccionar un grupo.<br>" +
					"</center></html>");
					loginField.requestFocus();
			}
			
		}
		else if (command.equals("remove")) {
			document = getDocumentToDelete();
			this.dispose();
		}
		if (document!=null) {
			try {
				SocketWriter.write(SocketHandler.getSock(),document);
			} catch (IOException ex) {
				System.out.println("ERROR: Falla de entrada/salida");
				System.out.println("Causa: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}
	
	public void clean() {
		for (int i=1 ; i< labels.length ; i++) {
			Component component = componentsList.get(i);
			component.setEnabled(false);
			if (component instanceof JTextField) {
				((JTextField)component).setText("");
			}
			else if (component instanceof JCheckBox) {
				((JCheckBox)component).setSelected(false);
			}
		}
		if (ACTION==ACTIONS.ADD) {
			for (int i=1 ; i< labels.length ; i++) {
				Component component = componentsList.get(i);
				component.setEnabled(true);
			}
			loginField.setEditable(true);
		}
		
		loginField.setSelectedIndex(0);
		loginField.setEditable(true);
		loginField.requestFocus();
	}
	
	private Document getDocumentToAdd() {
		
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR001");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		String password = null;
		String adminValue = adminCheck.isSelected() ? "true" : "false";
		String auditorValue = auditCheck.isSelected() ? "true" : "false";
		password = new String(passwdField.getPassword());
		MD5Tool md5 = new MD5Tool(password);
		Cache.Group group = Cache.getGroup((String)groupsCombo.getSelectedItem());

		pack.addContent(createField(loginField.getText()));
		pack.addContent(createField(md5.getDigest()));
		pack.addContent(createField(nameField.getText()));
		pack.addContent(createField(mailField.getText()));
		pack.addContent(createField(adminValue));
		pack.addContent(createField(auditorValue));
		pack.addContent(createField(group.getId()));
		
		transaction.addContent(pack);
		
		pack = new Element("package");
		Vector<Vector> vector = table.getData();
		int max = vector.size();
		for (int i=0 ; i < max ; i++) {
			Vector rowsVector = vector.get(i);
			Element subpackage = new Element("subpackage");
			subpackage.addContent(createField(loginField.getText()));
			subpackage.addContent(createField((String)rowsVector.get(0).toString()));
			subpackage.addContent(createField((String)rowsVector.get(2).toString()));
			pack.addContent(subpackage);
		}
		
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Document getDocumentToEdit() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		Element driver = new Element("driver");
        driver.setText("TR002");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		String password = null;
		String adminValue = adminCheck.isSelected() ? "true" : "false";
		String auditorValue = auditCheck.isSelected() ? "true" : "false";
		password = new String(passwdField.getPassword());
		MD5Tool md5 = new MD5Tool(password);
		Cache.Group group = Cache.getGroup((String)groupsCombo.getSelectedItem());
		String id = Cache.getUser(oldLogin).getId();
		Element field = createField(md5.getDigest());
		if ("".equals(new String(passwdField.getPassword()).trim())) {
			field.setAttribute(new Attribute("arg","edit"));
		}
		pack.addContent(createField(loginField.getText()));
		pack.addContent(field);
		pack.addContent(createField(nameField.getText()));
		pack.addContent(createField(mailField.getText()));
		pack.addContent(createField(adminValue));
		pack.addContent(createField(auditorValue));
		pack.addContent(createField(group.getId()));
		pack.addContent(createField(id));
		transaction.addContent(pack);
		
		pack = new Element("package");
		pack.addContent(createField(id));
		transaction.addContent(pack);
		
		pack = new Element("package");
		Vector<Vector> vector = table.getData();
		int max = vector.size();
		for (int i=0 ; i < max ; i++) {
			Vector rowsVector = vector.get(i);
			Element subpackage = new Element("subpackage");
			subpackage.addContent(createField(loginField.getText()));
			subpackage.addContent(createField((String)rowsVector.get(0).toString()));
			subpackage.addContent(createField((String)rowsVector.get(2).toString()));
			pack.addContent(subpackage);
		}
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Document getDocumentToDelete() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR003");
        transaction.addContent(driver);
        Cache.User user = Cache.getUser(loginField.getText());
		Element pack = new Element("package");
		pack.addContent(createField(user.getId()));
		transaction.addContent(pack);
		
		pack = new Element("package");
		pack.addContent(createField(user.getId()));
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Element createField(String text) {
		Element element = new Element("field");
		element.setText(text);
		return element;
	}
	
	class CodeSearchEngine extends Thread {
		private String code;
		
		public CodeSearchEngine(String code) {
			this.code = code;
			start();
		}
		
		public void run() {
			
			Cache.User user = Cache.getUser(code);
			if (user!=null) {
				oldLogin = loginField.getText();
				nameField.setText(user.getName());
				mailField.setText(user.getEmail());
				adminCheck.setSelected(user.getAdmin());
				auditCheck.setSelected(user.getAudit());
				groupsCombo.setSelectedItem(user.getGroupName());
				table.clear();
				ArrayList<Cache.POS> posList = Cache.getWorkStationsListByUser(code);
				
				for (Cache.POS upv : posList) {
					table.addData(upv.getPOSCode(),upv.getName(),upv.getValidIP());
				}
				if (ACTION == ACTIONS.EDIT ) {
					for (int i=1 ; i< labels.length ; i++) {
						Component comp = componentsList.get(i);
						comp.setEnabled(true);
					}
					loginField.setEditable(false);
					table.enableButtons();
					table.setEnabled(true);
					acceptButton.setEnabled(true);
				}
				if (ACTION == ACTIONS.DELETE) {
					acceptButton.setEnabled(true);
				}
				if (ACTION == ACTIONS.ADD) {
					JOptionPane.showMessageDialog(
							UsersManager.this,
							"<html><center>El usuario \"" 
							+ loginField.getText() 
							+ "\" ya existe.<br>Por favor, intente con otro login." +
							"</center></html>");
					loginField.blankTextField();
				}
			}
			else {
				if (ACTION == ACTIONS.ADD) {
					passwdField.setText("");
					passwdField.requestFocus();
					nameField.setText("");
					mailField.setText("");
					adminCheck.setSelected(false);
					auditCheck.setSelected(false);
					table.clear();
					groupsCombo.setSelectedIndex(0);
				}
				if (ACTION == ACTIONS.SEARCH) {
					JOptionPane.showMessageDialog(
							UsersManager.this,
							"<html><center>El usuario " 
							+ loginField.getText() 
							+ " no existe." +
							"</center></html>");
					loginField.blankTextField();
				} else if (ACTION == ACTIONS.EDIT) {
					for (int i=1 ; i< labels.length ; i++) {
						Component component = componentsList.get(i);
						component.setEnabled(false);
					}
					table.disableButtons();
					table.setEnabled(false);
					acceptButton.setEnabled(false);
					JOptionPane.showMessageDialog(
							UsersManager.this,
							"<html><center>El usuario \"" 
							+ loginField.getText() 
							+ "\" no existe.<br>" 
							+ "Por favor, compruebe el login del usuario<br>"
							+ " con la opción de búsqueda." +
							"</center></html>");
					loginField.blankTextField();
				}
			}

		}
	}	
	
	public void itemStateChanged(ItemEvent e) {	
	}

	public AutoCompleteComboBox getFieldLogin() {
		return loginField;
	}

	public void setFieldLogin(String fieldLogin) {
		loginField.setSelectedItem(fieldLogin);
		loginField.setEditable(false);
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public JTextField getFieldMail() {
		return mailField;
	}

	public JTextField getFieldNames() {
		return nameField;
	}

	public JPasswordField getFieldPassword() {
		return passwdField;
	}

	public JComboBox getGroupsCombo() {
		return groupsCombo;
	}

	public JCheckBox getAdminCheck() {
		return adminCheck;
	}

	public UserTable getTable() {
		return table;
	}
}