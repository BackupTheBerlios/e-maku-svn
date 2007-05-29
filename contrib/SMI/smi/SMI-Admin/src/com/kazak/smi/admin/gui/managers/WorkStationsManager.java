package com.kazak.smi.admin.gui.managers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.Cache.WorkStation;
import com.kazak.smi.admin.misc.NumericDataValidator;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.gui.table.UserTable;
import com.kazak.smi.admin.gui.misc.AutoCompleteComboBox;
import com.kazak.smi.admin.gui.main.MainWindow;
import com.kazak.smi.admin.gui.misc.GUIFactory;
import com.kazak.smi.lib.misc.FixedSizePlainDocument;

public class WorkStationsManager extends JFrame implements ActionListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private AutoCompleteComboBox nameField;
	private JTextField codeField;
	private JTextField ipField;
	private JPanel centerPanel;
	private JPanel southPanel;
	private JButton acceptButton;
	private JButton cancelButton;
	private JButton cleanButton;
	private ArrayList<Component> componentsList = new ArrayList<Component>();
	private JComboBox groupsCombo;
	private JButton searchButton;
	private JPanel searchPanel;
	private enum ACTIONS  {ADD,EDIT,SEARCH,DELETE};
	private ACTIONS ACTION;
	private String oldCode;
	private String[]
    labels = {
			"Nombre ",
			"Código ",
			"IP ",
			"Grupo "};
	private UserTable table;
		
	public WorkStationsManager() {
		this.setLayout(new BorderLayout());
		this.setSize(300,180);
		this.setLocationByPlatform(true);		
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComponents();
		addComponents();
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);
	}
	
	private void initComponents() {
		
		componentsList.add(nameField = new AutoCompleteComboBox(Cache.getWorkStationsList(),false,50));
		componentsList.add(codeField = new JTextField());
		componentsList.add(ipField   = new JTextField());
		componentsList.add(groupsCombo = new JComboBox(Cache.getGroupsList()));
		
		codeField.setDocument(new NumericDataValidator(4));
		ipField.setDocument(new FixedSizePlainDocument(15));
		
		acceptButton = new JButton("Aceptar");
		cancelButton = new JButton("Cancelar");
		GUIFactory gui = new GUIFactory();
		searchButton = gui.createButton("search.png");
		searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel,BoxLayout.Y_AXIS));
		searchPanel.add(searchButton);
		
		acceptButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
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

		southPanel.add(acceptButton);
		southPanel.add(cancelButton);
		
		cleanButton = new JButton("Limpiar");
		cleanButton.setActionCommand("clean");
		cleanButton.setMnemonic('L');
		cleanButton.addActionListener(this);
		southPanel.add(cleanButton);
		
		this.add(centerPanel,BorderLayout.CENTER);
		this.add(southPanel,BorderLayout.SOUTH);
		this.add(new JPanel(),BorderLayout.NORTH);
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(searchPanel,BorderLayout.EAST);
	}
	
	public void add() {
		this.setTitle("Nuevo Punto de Colocación");
		acceptButton.setActionCommand("accept");
		cancelButton.setActionCommand("cancel");
		
		resetWsForm();

		ACTION = ACTIONS.ADD;
		this.setVisible(true);
	}
	
	public void edit() {
		this.setTitle("Editar Punto de Colocación");
		acceptButton.setActionCommand("save");
		cancelButton.setActionCommand("cancel");
		resetWsForm();
		
		ACTION = ACTIONS.EDIT;
		this.setVisible(true);
	}
	
	public void editFromTree() {
		this.setTitle("Editar Punto de Colocación");
		acceptButton.setActionCommand("save");
		cancelButton.setActionCommand("cancel");
		
		ACTION = ACTIONS.EDIT;
		this.setVisible(true);
	}
		
	public void delete() {
		this.setTitle("Borrar Punto de Colocación");
		codeField.setEditable(false);
		ipField.setEditable(false);
		groupsCombo.setEnabled(false);
		ACTION = ACTIONS.DELETE;
		acceptButton.setActionCommand("remove");
		cancelButton.setActionCommand("cancel");
		acceptButton.setEnabled(false);
		this.setVisible(true);
	}
	
	public void search() {
		this.setTitle("Buscar Punto de Colocación");
		codeField.setEditable(false);
		ipField.setEditable(false);
		groupsCombo.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		acceptButton.setEnabled(false);
		cancelButton.setActionCommand("cancel");
		ACTION = ACTIONS.SEARCH;
		this.setVisible(true);
	}
	
	public void searchPOS(UserTable jTable) {
		table = jTable;
		setTitle("Buscar Punto de Colocación");
		codeField.setEditable(false);
		ipField.setEditable(false);
		groupsCombo.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		acceptButton.setActionCommand("search_code");
		acceptButton.setEnabled(false);
		cancelButton.setActionCommand("cancel");
		ACTION = ACTIONS.SEARCH;
		this.setVisible(true);
	}	

	private void searchConfirmation() {
		String key = nameField.getText().toUpperCase();
		
		if(key.length() == 0) {
			JOptionPane.showMessageDialog(
					this,
					"<html><center>" +
					"Por favor, ingrese un valor en el campo Nombre. " +
					"</center></html>");
			nameField.requestFocus();
			return;
		}
		
		oldCode = key;

		if (Cache.containsWs(key)) {
			WorkStation ws = Cache.getWorkStation(key);
			codeField.setText(ws.getCode());
			ipField.setText(ws.getIp());
			groupsCombo.setSelectedItem(ws.getGroupName());
			acceptButton.setEnabled(true);

			if (ACTION == ACTIONS.ADD) {
				nameField.setEditable(true);
				acceptButton.setEnabled(false);
				JOptionPane.showMessageDialog(
						this,
						"<html><center>" +
						"El punto de colocación \"" + key + "\" ya existe. " +
				"</center></html>");
				nameField.requestFocus();
				groupsCombo.setEnabled(false);
				ipField.setEditable(false);
				codeField.setEditable(false);
			} else if (ACTION == ACTIONS.DELETE) {
				codeField.setEditable(false);
				nameField.setEditable(false);
				ipField.setEditable(false);
				groupsCombo.setEnabled(false);
				acceptButton.setEnabled(true);
			} else if (ACTION == ACTIONS.EDIT) {
				nameField.setEditable(false);
				ipField.setEditable(true);
				groupsCombo.setEnabled(true);
				acceptButton.setEnabled(true);
				codeField.setEnabled(true);
				codeField.requestFocus();
			} else if (ACTION == ACTIONS.SEARCH) {
				acceptButton.setEnabled(false);
			}
		}
		else {
			if (ACTION == ACTIONS.ADD) {
				nameField.setEnabled(true);
				nameField.setEditable(true);
				ipField.setEditable(true);
				groupsCombo.setSelectedIndex(0);
				groupsCombo.setEnabled(true);
				acceptButton.setEnabled(true);
				codeField.setEnabled(true);
				codeField.setEditable(true);
				codeField.setText("");
				codeField.requestFocus();
			}
			else if ((ACTION == ACTIONS.SEARCH) || (ACTION == ACTIONS.EDIT)
					|| (ACTION == ACTIONS.DELETE)) {
				JOptionPane.showMessageDialog(
						this,
						"<html><center>" +
						"El punto de colocación \"" + key + "\" no existe. " +
				"</center></html>");
				nameField.requestFocus();
			}
		}
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		Document document = null;
		
		if (command.equals("search")) {
			searchConfirmation();
		}
		if (command.equals("cancel")) {
			this.dispose();
		}
		if (command.equals("search_code")) {
			table.addRow();
			int lastRow = table.getRowCount()-1;
			if (lastRow>=0) {
				if (lastRow==0) {
					table.enableDeleteButton();
				}
				if (!table.isAlreadyIn(codeField.getText())) {
					table.setValueAt(codeField.getText(),lastRow,0);
					table.setValueAt(nameField.getText(),lastRow,1);
					table.requestFocusInWindow();
					table.changeSelection(lastRow,0,true,true);
				} else {
					table.removeRow(lastRow);
					JOptionPane.showMessageDialog(
							this,
							"<html><center>" +
							"Este punto de colocación ya fue asociado al usuario. " +
							"</center></html>");
					return;
				}
			}
			this.dispose();
		}
		else if (command.equals("clean")) {
			this.clean();
		}
		else if (command.equals("accept")) {
			String name = nameField.getText().toUpperCase();
			
			WorkStation ws = Cache.getWorkStation(name);
						
			if (!"".equals(name) && ws==null && codeField.getText().length() == 4) {
				String ip = ipField.getText();
				if(!isAValidIP(ip)) {
					return;
				}
				document = getDocumentToAdd();
				this.dispose();
			}
			else {
				if ("".equals(name)) {
					JOptionPane.showMessageDialog(
							this,
							"<html><center>" +
							"Debe ingresar por lo menos el código y el nombre <br>" +
							"del punto de colocación. "+
							"</center></html>");
				}
				else if (codeField.getText().length() < 4) {
					JOptionPane.showMessageDialog(
							this,
							"<html><center>" +
							"Debe ingresar un código con 4 dígitos."+
							"</center></html>");
					codeField.requestFocus();
				}
				else if (ws!=null) {
					JOptionPane.showMessageDialog(
						this,
						"<html><center>" +
						"El código o el nombre ya existe. <br>" +
						"Verifique con el botón de búsqueda. "+
						"</center></html>");
				}
			}
		}
		else if (command.equals("save")) {
			String key = codeField.getText();
			String name = nameField.getText().toUpperCase();
			
			if (!"".equals(key) && !"".equals(name)) {
				String ip = ipField.getText();
				if(!isAValidIP(ip)) {
					return;
				} 
				document = getDocumentToEdit();
				this.dispose();
			}
			else {
				if ("".equals(key) || "".equals(name)) {
					JOptionPane.showMessageDialog(
							this,
							"<html><center>" +
							"Debe ingresar por lo menos el código y el nombre <br>" +
							"del punto de colocación. "+
							"</center></html>");				
					codeField.requestFocus();
				}
				else if (key.length() < 4) {
					JOptionPane.showMessageDialog(
							this,
							"<html><center>" +
							"Debe ingresar un código con 4 dígitos."+
							"</center><html>");
					codeField.requestFocus();
				}
			}
		}
		else if (command.equals("remove")) {
			String name = nameField.getText().toUpperCase();
			WorkStation ws = Cache.getWorkStation(name);
			if (ws!=null) {
				if (ws.getUsers().size() == 0) {
					document = getDocumentToDelete();
					this.dispose();		
				}
				else {
					JOptionPane.showMessageDialog(
							this,
							"<html><center>" +
							"El punto a eliminar no esta vacío. <br>" +
							"Para poder eliminar un punto no debe tener <br>" +
							"puntos de colocación asignados. " +
							"</center></html>");
				}
			}
		}
		// Sending command to the server to be approved
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
	
	private void resetWsForm() {
		nameField.setEditable(true);
		nameField.blankTextField();
		codeField.setText("");
		codeField.setEnabled(false);
		ipField.setText("");
		ipField.setEditable(false);
		groupsCombo.setSelectedIndex(0);
		groupsCombo.setEnabled(false);
		acceptButton.setEnabled(false);		
	}
	
	public void clean() {
		if (ACTION == ACTIONS.ADD || ACTION == ACTIONS.EDIT 
				|| ACTION == ACTIONS.DELETE) {
			resetWsForm();
		} else {
		ipField.setBackground(Color.WHITE);
		codeField.setEnabled(true);
		nameField.setEditable(true);
		codeField.setText("");
		nameField.setSelectedIndex(0);
		groupsCombo.setSelectedIndex(0);
		ipField.setText("");
		nameField.requestFocus();
		acceptButton.setEnabled(false);
		}
	}
	
	private Document getDocumentToAdd() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR007");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		 
		String ip = ipField.getText();
					
		Cache.Group g = Cache.getGroup((String)groupsCombo.getSelectedItem());
		pack.addContent(createField(codeField.getText()));
		pack.addContent(createField(nameField.getText().toUpperCase()));
		pack.addContent(createField(ip));
		pack.addContent(createField(g.getId()));
		
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Document getDocumentToEdit() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		Element driver = new Element("driver");
        driver.setText("TR008");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		pack.addContent(createField(codeField.getText()));
		pack.addContent(createField(oldCode));
		transaction.addContent(pack);
		
		pack = new Element("package");
		String ip = ipField.getText();
		
		Cache.Group group = Cache.getGroup((String)groupsCombo.getSelectedItem());
		pack.addContent(createField(codeField.getText()));
		pack.addContent(createField(nameField.getText().toUpperCase()));
		pack.addContent(createField(ip));
		pack.addContent(createField(group.getId()));
		pack.addContent(createField(oldCode));
		
		transaction.addContent(pack);
		
		pack = new Element("package");
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
        driver.setText("TR009");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		pack.addContent(createField(codeField.getText()));
		transaction.addContent(pack);
		
		return doc;
	}
	
	public boolean isAValidIP(String input) {
		if (!"".equals(input)) {
			String ipFields[] = input.split("\\.");
			
			if (ipFields.length < 4) {
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(
						WorkStationsManager.this,
						"<html><center>" +
						"Formato inválido de dirección IP. <br>Por favor, ingrese un valor apropiado. <br>Ej: 127.0.0.1 " +
				"</center></html>");
				ipField.setText("");
				ipField.requestFocus();
				return false;				
			}
			
			try {
				if (Integer.parseInt(ipFields[0]) < 255 &&
						Integer.parseInt(ipFields[1]) < 255 &&
						Integer.parseInt(ipFields[2]) < 255 &&
						Integer.parseInt(ipFields[3]) < 255) {
					return true;
				}
				else {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(
							WorkStationsManager.this,
							"<html><center>" +
							"Formato inválido de dirección IP. <br>Por favor, ingrese un valor apropiado. <br>Ej: 127.0.0.1 " +
					"</center></html>");
					ipField.requestFocus();
					return false;
				}

			} catch (NumberFormatException ex) {
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(
						WorkStationsManager.this,
						"<html><center>" +
						"Rangos inválidos de dirección IP. <br>Por favor, corrija los valores incorrectos. <br>Ej: 127.0.0.1 " +
				"</center></html>");
				ipField.requestFocus();
				return false;
			}
		}
		else {
			JOptionPane.showMessageDialog(
					WorkStationsManager.this,
					"<html><center>" +
					"Debe ingresar una dirección IP. <br>Ej: 127.0.0.1 " +
			"</center></html>");
			ipField.requestFocus();
			return false;
		}
	}
	
	private Element createField(String text) {
		Element element = new Element("field");
		element.setText(text);
		return element;
	}

	public void setFieldName(String name) {
		nameField.setSelectedItem(name);
		nameField.setEditable(false);
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public JTextField getFieldCode() {
		return codeField;
	}

	public JTextField getFieldIp() {
		return ipField;
	}

	public JComboBox getGroupsCombo() {
		return groupsCombo;
	}
}