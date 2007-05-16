package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
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
import com.kazak.smi.lib.misc.FixedSizePlainDocument;

public class WorkStationsManager extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private AutoCompleteComboBox fieldName;
	private JTextField fieldCode;
	private JTextField fieldIP;
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
			"Nombre",
			"Codigo",
			"IP",
			"Grupo"};
	private JTable table;
		
	public WorkStationsManager() {
		this.setLayout(new BorderLayout());
		this.setSize(300,180);
		this.setLocationByPlatform(true);
		this.setAlwaysOnTop(true);		
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComponents();
		addComponents();
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);
	}
	
	private void initComponents() {
		
		componentsList.add(fieldName = new AutoCompleteComboBox(Cache.getWorkStationsList(),false,50));
		componentsList.add(fieldCode = new JTextField());
		componentsList.add(fieldIP   = new JTextField());
		componentsList.add(groupsCombo = new JComboBox(Cache.getGroupsList()));
		
		fieldCode.setDocument(new NumericDataValidator(4));
		fieldIP.setDocument(new FixedSizePlainDocument(15));
		
		fieldIP.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String input =  fieldIP.getText();
				if (!"".equals(input)) {
					String ipFields[] = input.split("\\.");
					try {
						if (Integer.parseInt(ipFields[0]) < 255 &&
							Integer.parseInt(ipFields[1]) < 255 &&
							Integer.parseInt(ipFields[2]) < 255 &&
							Integer.parseInt(ipFields[3]) < 255) {
							fieldIP.setBackground(Color.WHITE);
						}
						else {
							Toolkit.getDefaultToolkit().beep();
							fieldIP.setBackground(Color.RED);
						}
						
					} catch (NumberFormatException ex) {
						Toolkit.getDefaultToolkit().beep();
						fieldIP.setBackground(Color.RED);
					}
				}
				else {
					fieldIP.setBackground(Color.WHITE);
				}
			}
		});
		fieldName.addItemListener(this);
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
		acceptButton.setEnabled(false);
		ACTION = ACTIONS.ADD;
		this.setVisible(true);
	}
	
	public void edit() {
		this.setTitle("Editar Punto de Colocación");
		acceptButton.setActionCommand("save");
		cancelButton.setActionCommand("cancel");
		ACTION = ACTIONS.EDIT;
		this.setVisible(true);
	}
	
	public void delete() {
		this.setTitle("Borrar Punto de Colocación");
		fieldCode.setEditable(false);
		fieldIP.setEditable(false);
		groupsCombo.setEnabled(false);
		ACTION = ACTIONS.DELETE;
		acceptButton.setActionCommand("remove");
		cancelButton.setActionCommand("cancel");
		this.setVisible(true);
	}
	
	public void search() {
		this.setTitle("Buscar Punto de Colocación");
		fieldCode.setEditable(false);
		fieldIP.setEditable(false);
		groupsCombo.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		acceptButton.setEnabled(false);
		cancelButton.setActionCommand("cancel");
		ACTION = ACTIONS.SEARCH;
		this.setVisible(true);
	}
	
	public void search(JTable jTable) {
		this.table = jTable;
		this.setTitle("Buscar Punto de Colocación");
		fieldCode.setEditable(false);
		fieldIP.setEditable(false);
		groupsCombo.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		acceptButton.setActionCommand("search_code");
		acceptButton.setEnabled(false);
		cancelButton.setActionCommand("cancel");
		ACTION = ACTIONS.SEARCH;
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				int lastRow = table.getRowCount()-1;
				System.out.println("Fila: " + lastRow);
				if (lastRow>=0) {
					table.setValueAt(fieldCode.getText(),lastRow,0);
					table.setValueAt(fieldName.getText(),lastRow,1);
					table.requestFocusInWindow();
					table.changeSelection(lastRow,0,true,true);
				}
			}
		});
		this.setVisible(true);
	}	

	private void searchConfirmation() {
		String key = fieldName.getText().toUpperCase();
		oldCode = key;

		if (Cache.containsWs(key)) {
			WorkStation ws = Cache.getWorkStation(key);
			fieldCode.setText(ws.getCode());
			fieldIP.setText(ws.getIp());
			groupsCombo.setSelectedItem(ws.getGroupName());
			acceptButton.setEnabled(true);

			if (ACTION == ACTIONS.ADD) {
				fieldName.setEditable(false);
				fieldIP.setEditable(false);
				groupsCombo.setEnabled(false);
				acceptButton.setEnabled(false);
			}
			if (ACTION == ACTIONS.DELETE) {
				fieldCode.setEditable(false);
				fieldName.setEditable(false);
				fieldIP.setEditable(false);
				groupsCombo.setEnabled(false);
				acceptButton.setEnabled(true);

			}
			if (ACTION == ACTIONS.EDIT) {
				fieldName.setEditable(false);
			}
		}
		else {
			if (ACTION == ACTIONS.ADD) {
				fieldName.setEditable(true);
				fieldIP.setEditable(true);
				acceptButton.setEnabled(true);
				groupsCombo.setEnabled(true);
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
			int row = table.getRowCount()-1;
			System.out.println("Row: " + row);
			//table TODO: Search the way to delete a row
			
			this.dispose();
		}
		if (command.equals("search_code")) {
			int lastRow = table.getRowCount()-1;
			if (lastRow>=0) {
				table.setValueAt(fieldCode.getText(),lastRow,0);
				table.setValueAt(fieldName.getText(),lastRow,1);
				table.requestFocusInWindow();
				table.changeSelection(lastRow,0,true,true);
			}
			this.dispose();
		}
		else if (command.equals("clean")) {
			this.clean();
		}
		else if (command.equals("accept")) {
			String name = fieldName.getText().toUpperCase();
			WorkStation ws = Cache.getWorkStation(name);
						
			if (!"".equals(name) && ws==null && fieldCode.getText().length() == 4) {
				document = getDocumentForAdd();
				this.dispose();
			}
			else {
				if ("".equals(name)) {
					JOptionPane.showMessageDialog(
							this,
							"<html>" +
							"<h3>" +
							"Debe ingresar por lo menos el código y el nombre.<br>" +
							"del punto de colocación"+
							"<html>");
				}
				else if (fieldCode.getText().length() < 4) {
					JOptionPane.showMessageDialog(
							this,
							"<html>" +
							"<h3>" +
							"Debe ingresar un código con 4 dígitos.<br>"+
							"<html>");
				}
				else if (ws!=null) {
					JOptionPane.showMessageDialog(
						this,
						"<html>" +
						"<h3>" +
						"El código o el nombre ya existe.<br>" +
						"Verifique con el botón de búsqueda."+
						"<html>");
				}
			}
		}
		else if (command.equals("save")) {
			String key = fieldCode.getText();
			String name = fieldName.getText().toUpperCase();
			if (!"".equals(key) && !"".equals(name)) {
				document = getDocumentForEdit();
				this.dispose();
			}
			else {
				if ("".equals(key) || "".equals(name)) {
					JOptionPane.showMessageDialog(
							this,
							"<html>" +
							"<h3>" +
							"Debe ingresar por lo menos el código y el nombre<br>" +
							"del punto de colocación."+
							"<html>");
				}
				else if (key.length() < 4) {
					JOptionPane.showMessageDialog(
							this,
							"<html>" +
							"<h3>" +
							"Debe ingresar un código con 4 dígitos.<br>"+
							"<html>");
				}
			}
		}
		else if (command.equals("remove")) {
			String name = fieldName.getText().toUpperCase();
			WorkStation ws = Cache.getWorkStation(name);
			if (ws!=null) {
				if (ws.getUsers().size() == 0) {
					document = getDocumentForDelete();
					this.dispose();		
				}
				else {
					JOptionPane.showMessageDialog(
							this,
							"<html>" +
							"<h3>" +
							"El punto a eliminar no esta vació.<br>" +
							"Para poder eliminar un punto no debe tener<br>" +
							"puntos de colocación asignados." +
							"<html>");
				}
			}
		}
		// Enviando comando al servidor para ser aprobado
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
		if (ACTION == ACTIONS.ADD) {
			fieldName.setEditable(true);
			fieldIP.setEditable(true);
			acceptButton.setEnabled(true);
		}
		if (ACTION == ACTIONS.DELETE) {
			acceptButton.setEnabled(true);
		}
		fieldIP.setBackground(Color.WHITE);
		fieldCode.setEnabled(true);
		fieldName.setEditable(true);
		fieldCode.setText("");
		fieldName.setSelectedIndex(0);
		fieldIP.setText("");
		fieldName.requestFocus();
		acceptButton.setEnabled(false);
	}
	
	private Document getDocumentForAdd() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR007");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		String ip = 
			"".equals(fieldIP.getText()) ? 
			"127.0.0.1" : 
			fieldIP.getText();
		Cache.Group g = Cache.getGroup((String)groupsCombo.getSelectedItem());
		pack.addContent(createField(fieldCode.getText()));
		pack.addContent(createField(fieldName.getText().toUpperCase()));
		pack.addContent(createField(ip));
		pack.addContent(createField(g.getId()));
		
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Document getDocumentForEdit() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		Element driver = new Element("driver");
        driver.setText("TR008");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		pack.addContent(createField(fieldCode.getText()));
		pack.addContent(createField(oldCode));
		transaction.addContent(pack);
		
		pack = new Element("package");
		String ip = 
			"".equals(fieldIP.getText()) ? 
			"127.0.0.1" : 
			fieldIP.getText();
		
		Cache.Group group = Cache.getGroup((String)groupsCombo.getSelectedItem());
		pack.addContent(createField(fieldCode.getText()));
		pack.addContent(createField(fieldName.getText().toUpperCase()));
		pack.addContent(createField(ip));
		pack.addContent(createField(group.getId()));
		pack.addContent(createField(oldCode));
		
		transaction.addContent(pack);
		
		pack = new Element("package");
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
        driver.setText("TR009");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		pack.addContent(createField(fieldCode.getText()));
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Element createField(String text) {
		Element element = new Element("field");
		element.setText(text);
		return element;
	}

	public void itemStateChanged(ItemEvent e) {
	}
	
	public void setFieldName(String name) {
		fieldName.setSelectedItem(name);
		fieldName.setEditable(false);
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public JTextField getFieldCode() {
		return fieldCode;
	}

	public JTextField getFieldIp() {
		return fieldIP;
	}

	public JComboBox getGroupsCombo() {
		return groupsCombo;
	}
}