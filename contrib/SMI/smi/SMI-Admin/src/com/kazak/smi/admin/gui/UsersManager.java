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
	private AutoCompleteComboBox FieldLogin;
	private JPasswordField FieldPassword;
	private JTextField FieldNames;
	private JTextField FieldMail;
	private JCheckBox JCheckAdm;
	private JCheckBox JCheckAudit;
	private JPanel JPCenter;
	private JPanel JPSouth;
	private JButton JBAccept;
	private JButton JBCancel;
	private JButton JBClean;
	private ArrayList<Component> listComps = new ArrayList<Component>();
	private UserTable table;
	private JComboBox JCBGroups;
	private JButton JBSearch;
	private JPanel jpsearch;
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
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComps();
		addComps();
		JBSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SearcherCode(FieldLogin.getText());
			}
		});
	}
	
	public void add() {
		this.setTitle("Nuevo Usuario");
		this.setVisible(true);
		this.ACTION = ACTIONS.ADD;
		JBAccept.setActionCommand("accept");
		JBCancel.setActionCommand("cancel");
	}
	
	public void edit() {
		this.setTitle("Editar Usuario");
		for (int i=1 ; i< labels.length ; i++) {
			Component comp = listComps.get(i);
			comp.setEnabled(false);
			if (comp instanceof JTextField) {
				((JTextField)comp).setDisabledTextColor(Color.BLACK);
			}
		}
		table.disableButtons();
		table.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		JBAccept.setActionCommand("save");
		JBCancel.setActionCommand("cancel");
		JBAccept.setEnabled(false);
		this.ACTION = ACTIONS.EDIT;
		this.setVisible(true);
	}
	
	public void delete() {
		this.setTitle("Borrar Usuario");
		for (int i=1 ; i< labels.length ; i++) {
			Component comp = listComps.get(i);
			comp.setEnabled(false);
			if (comp instanceof JTextField) {
				((JTextField)comp).setDisabledTextColor(Color.BLACK);
			}
		}
		table.disableButtons();
		table.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		JBAccept.setActionCommand("remove");
		JBCancel.setActionCommand("cancel");
		JBAccept.setEnabled(false);
		this.ACTION = ACTIONS.DELETE;
		this.setVisible(true);
	}
	
	public void search() {
		this.setTitle("Buscar Usuario");
		for (int i=1 ; i< labels.length ; i++) {
			Component comp = listComps.get(i);
			comp.setEnabled(false);
			if (comp instanceof JTextField) {
				((JTextField)comp).setDisabledTextColor(Color.BLACK);
			}
		}
		table.disableButtons();
		table.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		
		
		JBAccept.setEnabled(false);
		JBCancel.setActionCommand("cancel");
		this.setVisible(true);
	}
	
	private void initComps() {
		
		listComps.add(FieldLogin       = new AutoCompleteComboBox(Cache.getListKeysUsers(),true,30));
		listComps.add(FieldPassword    = new JPasswordField());
		listComps.add(FieldNames       = new JTextField());
		listComps.add(FieldMail        = new JTextField());
		listComps.add(JCheckAdm        = new JCheckBox());
		listComps.add(JCheckAudit      = new JCheckBox());
		listComps.add(JCBGroups        = new JComboBox(Cache.getListKeys()));
		
		FieldLogin.addItemListener(this);
		FieldPassword.setDocument(new FixedSizePlainDocument(10));
		FieldNames.setDocument(new FixedSizePlainDocument(100));
		FieldMail.setDocument(new FixedSizePlainDocument(200));
		
		table = new UserTable(this);
		JBAccept = new JButton("Aceptar");
		JBCancel = new JButton("Cancelar");
		GUIFactory gui = new GUIFactory();
		JBSearch = gui.createButton("search.png");
		jpsearch = new JPanel();
		jpsearch.setLayout(new BoxLayout(jpsearch,BoxLayout.Y_AXIS));
		jpsearch.add(JBSearch);
		
		JBAccept.addActionListener(this);
		JBCancel.addActionListener(this);
		
		JCheckAdm.addItemListener(new ItemListener() {		
			public void itemStateChanged(ItemEvent e) {
				if (JCheckAdm.isSelected()){
					JCheckAudit.setSelected(false);
				}
			}
		});
		
		JCheckAudit.addItemListener(new ItemListener() {		
			public void itemStateChanged(ItemEvent e) {
				if (JCheckAudit.isSelected()){
					JCheckAdm.setSelected(false);
				}
			}
		});
		
		JPCenter = new JPanel(new BorderLayout());
		JPSouth  = new JPanel(new FlowLayout(FlowLayout.CENTER));
	}
	
	private void addComps() {
		JPanel jplabels = new JPanel(new GridLayout(labels.length,0));
		JPanel jpfields = new JPanel(new GridLayout(labels.length,0));
		
		for (int i=0 ; i< labels.length ; i++) {
			jplabels.add(new JLabel(labels[i]));
			jpfields.add(listComps.get(i));
		}
		
		JPCenter.add(jplabels,BorderLayout.WEST);
		JPCenter.add(jpfields,BorderLayout.CENTER);
		JPanel center = new JPanel(new BorderLayout());

		center.add(JPCenter,BorderLayout.NORTH);
		center.add(table.getPanel(),BorderLayout.CENTER);
		
		JPSouth.add(JBAccept);
		JPSouth.add(JBCancel);
		
		JBClean = new JButton("Limpiar");
		JBClean.setActionCommand("clean");
		JBClean.addActionListener(this);
		JPSouth.add(JBClean);
		
		this.add(center,BorderLayout.CENTER);
		this.add(JPSouth,BorderLayout.SOUTH);
		this.add(new JPanel(),BorderLayout.NORTH);
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(jpsearch,BorderLayout.EAST);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Document document = null;
		TreeManagerGroups.currTpath = new TreePath(
				new Object[] {
							new SortableTreeNode(MainWindow.getAppOwner()),
							new SortableTreeNode(getJCBGroups().getSelectedItem()),
							new SortableTreeNode(FieldLogin.getText())});
		if (command.equals("cancel")) {
			this.dispose();
		}
		else if (command.equals("clean")) {
			this.clean();
		}
		else if (command.equals("accept")) {
			String login = FieldLogin.getText();
			if (!"".equals(login)) {
				User user = Cache.searchUser(login);
				if (user==null) {
					String str = JCBGroups.getSelectedItem().toString();
					Group group = Cache.getGroup(str);
					if (group.getZone() && table.getRowCount() <= 0) {
						JOptionPane.showMessageDialog(
								this,
								"<html>" +
								"<h3>" +
								"Ha seleccionado un grupo que es zona.<br>" +
								"Debe adicionar un punto de colocación." +
								"<html>");
						return;
					}
					document = getDocumentForAdd();
					this.dispose();
				}
				else {
					JOptionPane.showMessageDialog(
							this,
							"<html>" +
							"<h3>" +
							"Ya existe un usuario con ese login.<br>" +
							"Ingrese uno diferente o compruebe <br>" +
							"con el botón de búsqueda"+
							"<html>");
				}
			}
			else {
				JOptionPane.showMessageDialog(
					this,
					"<html>" +
					"<h3>" +
					"Por lo menos debe digitar el login de usuario y seleccionar un grupo.<br>" +
					"<html>");
			}
		}
		else if (command.equals("save")) {
			String login = FieldLogin.getText();
			if (!"".equals(login)) {
				User user = Cache.searchUser(oldLogin);
				if (oldLogin.equals(login)) {
					user = null;
				}
				if (user==null) {
					String str = JCBGroups.getSelectedItem().toString();
					Group group = Cache.getGroup(str);
					if (group.getZone() && table.getRowCount() <= 0) {
						JOptionPane.showMessageDialog(
								this,
								"<html>" +
								"<h3>" +
								"Ha seleccionado un grupo que es zona.<br>" +
								"Debe adicionar un punto de colocación." +
								"<html>");
						return;
					}
					document = getDocumentForEdit();
					this.dispose();
				}
				else {
					JOptionPane.showMessageDialog(
							this,
							"<html>" +
							"<h3>" +
							"Ya existe un usuario con ese login.<br>" +
							"Ingrese uno diferente o compruebe <br>" +
							"con el botón de búsqueda"+
							"<html>");
				}
			}
			else {
				JOptionPane.showMessageDialog(
					this,
					"<html>" +
					"<h3>" +
					"Por lo menos debe digitar el login de usuario y seleccionar un grupo.<br>" +
					"<html>");
			}
			
		}
		else if (command.equals("remove")) {
			document = getDocumentForDelete();
			this.dispose();
		}
		if (document!=null) {
			try {
				SocketWriter.writing(SocketHandler.getSock(),document);
			} catch (IOException ex) {
				System.out.println("Error de entrada y salida");
				System.out.println("mensaje: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}
	
	public void clean() {
		for (int i=1 ; i< labels.length ; i++) {
			Component comp = listComps.get(i);
			comp.setEnabled(false);
			if (comp instanceof JTextField) {
				((JTextField)comp).setText("");
			}
			else if (comp instanceof JCheckBox) {
				((JCheckBox)comp).setSelected(false);
			}
		}
		if (ACTION==ACTIONS.ADD) {
			for (int i=1 ; i< labels.length ; i++) {
				Component comp = listComps.get(i);
				comp.setEnabled(true);
			}
			FieldLogin.setEditable(true);
		}
		
		FieldLogin.setSelectedIndex(0);
		FieldLogin.requestFocus();
	}
	
	private Document getDocumentForAdd() {
		
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
		String admValue = JCheckAdm.isSelected() ? "true" : "false";
		String auditValue = JCheckAudit.isSelected() ? "true" : "false";
		password = new String(FieldPassword.getPassword());
		MD5Tool md = new MD5Tool(password);
		Cache.Group g = Cache.getGroup((String)JCBGroups.getSelectedItem());

		pack.addContent(createField(FieldLogin.getText()));
		pack.addContent(createField(md.getDigest()));
		pack.addContent(createField(FieldNames.getText()));
		pack.addContent(createField(FieldMail.getText()));
		pack.addContent(createField(admValue));
		pack.addContent(createField(auditValue));
		pack.addContent(createField(g.getId()));
		
		transaction.addContent(pack);
		
		pack = new Element("package");
		Vector<Vector> v = table.getData();
		int max = v.size();
		for (int i=0 ; i < max ; i++) {
			Vector vrow = v.get(i);
			Element subpackage = new Element("subpackage");
			subpackage.addContent(createField(FieldLogin.getText()));
			subpackage.addContent(createField((String)vrow.get(0).toString()));
			subpackage.addContent(createField((String)vrow.get(2).toString()));
			pack.addContent(subpackage);
		}
		
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Document getDocumentForEdit() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		Element driver = new Element("driver");
        driver.setText("TR002");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		String password = null;
		String admValue = JCheckAdm.isSelected() ? "true" : "false";
		String auditValue = JCheckAudit.isSelected() ? "true" : "false";
		password = new String(FieldPassword.getPassword());
		MD5Tool md = new MD5Tool(password);
		Cache.Group g = Cache.getGroup((String)JCBGroups.getSelectedItem());
		String id = Cache.searchUser(oldLogin).getId();
		Element field = createField(md.getDigest());
		if ("".equals(new String(FieldPassword.getPassword()).trim())) {
			field.setAttribute(new Attribute("arg","edit"));
		}
		pack.addContent(createField(FieldLogin.getText()));
		pack.addContent(field);
		pack.addContent(createField(FieldNames.getText()));
		pack.addContent(createField(FieldMail.getText()));
		pack.addContent(createField(admValue));
		pack.addContent(createField(auditValue));
		pack.addContent(createField(g.getId()));
		pack.addContent(createField(id));
		transaction.addContent(pack);
		
		pack = new Element("package");
		pack.addContent(createField(id));
		transaction.addContent(pack);
		
		pack = new Element("package");
		Vector<Vector> v = table.getData();
		int max = v.size();
		for (int i=0 ; i < max ; i++) {
			Vector vrow = v.get(i);
			Element subpackage = new Element("subpackage");
			subpackage.addContent(createField(FieldLogin.getText()));
			subpackage.addContent(createField((String)vrow.get(0).toString()));
			subpackage.addContent(createField((String)vrow.get(2).toString()));
			pack.addContent(subpackage);
		}
		
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Document getDocumentForDelete() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR003");
        transaction.addContent(driver);
        Cache.User user = Cache.searchUser(FieldLogin.getText());
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
	
	class SearcherCode extends Thread {
		private String code;
		
		public SearcherCode(String code) {
			this.code = code;
			start();
		}
		
		public void run() {
			
			Cache.User user = Cache.searchUser(code);
			if (user!=null) {
				oldLogin = FieldLogin.getText();
				FieldNames.setText(user.getName());
				FieldMail.setText(user.getEmail());
				JCheckAdm.setSelected(user.getAdmin());
				JCheckAudit.setSelected(user.getAudit());
				JCBGroups.setSelectedItem(user.getGidname());
				table.clear();
				ArrayList<Cache.UserPVenta> pvs = Cache.getLisWorksStationsForUser(code);
				if (pvs.size() > 0) {
					TreeManagerGroups.currTpath = new TreePath(
							new Object[] {
										new SortableTreeNode(MainWindow.getAppOwner()),
										new SortableTreeNode(getJCBGroups().getSelectedItem()),
										new SortableTreeNode(pvs.get(0).getName()),
										new SortableTreeNode(FieldLogin.getText())});	
				}
				else {
					TreeManagerGroups.currTpath = new TreePath(
							new Object[] {
										new SortableTreeNode(MainWindow.getAppOwner()),
										new SortableTreeNode(getJCBGroups().getSelectedItem()),
										new SortableTreeNode(FieldLogin.getText())});
				}
				
				//TreeManagerGroups.expand();
				System.out.println("Actualizando Arbol desde UsersManager...");
				
				for (Cache.UserPVenta upv : pvs) {
					table.addData(upv.getCodepv(),upv.getName(),upv.getValidip());
				}
				if (ACTION == ACTIONS.EDIT ) {
					for (int i=1 ; i< labels.length ; i++) {
						Component comp = listComps.get(i);
						comp.setEnabled(true);
					}
					FieldLogin.setEditable(false);
					table.enableButtons();
					table.setEnabled(true);
					JBAccept.setEnabled(true);
				}
				if (ACTION == ACTIONS.DELETE) {
					JBAccept.setEnabled(true);
				}
			}
			else {
				if (ACTION == ACTIONS.EDIT) {
					for (int i=1 ; i< labels.length ; i++) {
						Component comp = listComps.get(i);
						comp.setEnabled(false);
					}
					table.disableButtons();
					table.setEnabled(false);
					JBAccept.setEnabled(false);
				}
			}

		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		JBSearch.doClick();	
	}

	public AutoCompleteComboBox getFieldLogin() {
		return FieldLogin;
	}

	public void setFieldLogin(String fieldLogin) {
		FieldLogin.setSelectedItem(fieldLogin);
		FieldLogin.setEditable(false);
	}

	public JButton getJBSearch() {
		return JBSearch;
	}

	public JTextField getFieldMail() {
		return FieldMail;
	}

	public JTextField getFieldNames() {
		return FieldNames;
	}

	public JPasswordField getFieldPassword() {
		return FieldPassword;
	}

	public JComboBox getJCBGroups() {
		return JCBGroups;
	}

	public JCheckBox getJCheckAdm() {
		return JCheckAdm;
	}

	public UserTable getTable() {
		return table;
	}
}