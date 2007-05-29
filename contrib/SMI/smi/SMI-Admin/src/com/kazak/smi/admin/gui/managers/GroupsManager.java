package com.kazak.smi.admin.gui.managers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.gui.main.TreeManagerGroups.SortableTreeNode;
import com.kazak.smi.admin.gui.main.MainWindow;
import com.kazak.smi.admin.gui.main.TreeManagerGroups;
import com.kazak.smi.admin.gui.misc.GUIFactory;
import com.kazak.smi.admin.gui.misc.AutoCompleteComboBox;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;

public class GroupsManager extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private AutoCompleteComboBox nameField;
	private JCheckBox visibleCheck;
	private JCheckBox zoneCheck;
	
	private JPanel centerPanel;
	private JPanel southPanel;
	private JButton acceptButton;
	private JButton cancelButton;
	private JButton cleanButton;
	private ArrayList<Component> componentsList = new ArrayList<Component>();

	private JButton searchButton;
	private JPanel searchPanel;
	private String oldGroupName;
	private enum ACTIONS  {ADD,EDIT,SEARCH,DELETE};
	private ACTIONS ACTION;
	final JFrame frame = this;
	private String[] labels = { 
			"Nombre ",
			"Visible para clientes",
			"Es Zona              "
			};
	
	public GroupsManager() {
		this.setLayout(new BorderLayout());
		this.setSize(340,160);
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComponents();
		addComponents();
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);
	}				
					
	public void add() {
		this.setTitle("Nuevo Grupo");
		acceptButton.setActionCommand("accept");
		cancelButton.setActionCommand("cancel");
		this.ACTION = ACTIONS.ADD;
		this.setVisible(true);
	}
	
	public void edit() {
		this.setTitle("Editar Grupo");
		acceptButton.setActionCommand("save");
		cancelButton.setActionCommand("cancel");
		acceptButton.setEnabled(false);
		visibleCheck.setEnabled(false);
		zoneCheck.setEnabled(false);
		this.ACTION = ACTIONS.EDIT;
		this.setVisible(true);
	}
	
	public void delete() {
		this.setTitle("Borrar Grupo");
		visibleCheck.setEnabled(false);
		zoneCheck.setEnabled(false);
		acceptButton.setEnabled(false);
		acceptButton.setActionCommand("remove");
		cancelButton.setActionCommand("cancel");
		this.ACTION = ACTIONS.DELETE;
		this.setVisible(true);
	}
	
	public void search() {
		this.setTitle("Buscar Grupo");
		visibleCheck.setEnabled(false);
		zoneCheck.setEnabled(false);
		acceptButton.setEnabled(false);
		cancelButton.setActionCommand("cancel");
		this.ACTION = ACTIONS.SEARCH;
		this.setVisible(true);
	}
	
	private void initComponents() {
		String[] comboList = Cache.getGroupsList();
		componentsList.add(nameField   = new AutoCompleteComboBox(comboList,false,40));
		componentsList.add(visibleCheck= new JCheckBox());
		componentsList.add(zoneCheck   = new JCheckBox());
		nameField.addItemListener(this);
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
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(centerPanel,BorderLayout.NORTH);
		
		southPanel.add(acceptButton);
		southPanel.add(cancelButton);
		
		cleanButton = new JButton("Limpiar");
		cleanButton.setActionCommand("clean");
		cleanButton.addActionListener(this);
		southPanel.add(cleanButton);
		
		this.add(mainPanel,BorderLayout.CENTER);
		this.add(southPanel,BorderLayout.SOUTH);
		this.add(new JPanel(),BorderLayout.NORTH);
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(searchPanel,BorderLayout.EAST);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Document document = null;
		if (command.equals("cancel")) {
			this.dispose();
		}
		else if (command.equals("search")) {
			searchConfirmation();
		}		
		else if (command.equals("clean")) {
			this.clean();
		}
		else if (command.equals("accept")) {
			String key = nameField.getText().toUpperCase();
			TreeManagerGroups.currentTreePath = new TreePath(
					new Object[] {
								new SortableTreeNode(MainWindow.getAppOwner()),
								new SortableTreeNode(key)});
			if (!Cache.containsGroup(key) && !"".equals(key)) {
				document = getDocumentToAdd();
				this.dispose();	
			}
			else {
				if ("".equals(key)) {
					JOptionPane.showMessageDialog(
							frame,
							"Por favor, ingrese el nombre del grupo.");
				}
				else {
					nameField.blankTextField();
					JOptionPane.showMessageDialog(
							frame,
							"<html><center>El grupo \"" + key + "\" ya existe.<br>" +
							"Por favor, ingrese uno diferente y compruebelo<br>" +
					"con el botón de búsqueda.</center></html>");
				}
			}
		}
		else if (command.equals("save")) {
			String key = nameField.getText().toUpperCase();
			TreeManagerGroups.currentTreePath = new TreePath(
					new Object[] {
								new SortableTreeNode(MainWindow.getAppOwner()),
								new SortableTreeNode(key)});
			if (!Cache.containsGroup(key)) {
				document = getDocumentToEdit();
				this.dispose();	
			}
			else if (key.equals(oldGroupName)){
				document = getDocumentToEdit();
				this.dispose();
			}
			else {
				nameField.blankTextField();
				JOptionPane.showMessageDialog(
						frame,
						"<html><center>El grupo \"" + key + "\" ya existe.<br>" +
						"Por favor, ingrese uno diferente y<br>" +
						"compruebelo con el botón de búsqueda." +
						"</center></html>");
			}
		}
		else if (command.equals("remove")) {
			if (!nameField.getText().equals("")) {
				String key = nameField.getText().toUpperCase();
				TreeManagerGroups.currentTreePath = new TreePath(
						new Object[] {
									new SortableTreeNode(MainWindow.getAppOwner()),
									new SortableTreeNode(key)});
				int usersTotal = Cache.getGroup(key).getUsers().size();
				int wsTotal = Cache.getGroup(key).getWorkStations().size();
				if (usersTotal==0 && wsTotal==0) {
					document = getDocumentToDelete();
					this.dispose();	
				}
				else {
					nameField.blankTextField();
					JOptionPane.showMessageDialog(
							frame,
							"<html><center>" +
							"El grupo " + key + " no se encuentra vació.<br>" +
							"Para poder eliminar un grupo no debe tener<br>" +
							"usuarios ni puntos de colocación asignados." +
							"</center></html>");
				}
			}
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

	public void searchConfirmation() {
		String groupName = nameField.getText().toUpperCase();
		
		if (groupName.length() == 0) {
			JOptionPane.showMessageDialog(
					GroupsManager.this,
					"<html><center>" +
					"Por favor, ingrese un valor en el campo Nombre." +
					"</center></html>");
			nameField.requestFocus();
			return;
		}
		
		Cache.Group group = Cache.getGroup(groupName);
		visibleCheck.setEnabled(true);
		zoneCheck.setEnabled(true);
		
		if (group!=null) {
			oldGroupName = nameField.getText().toUpperCase();
			visibleCheck.setSelected(group.isVisible());
			zoneCheck.setSelected(group.isZone());

			if (ACTION == ACTIONS.SEARCH) {
				visibleCheck.setEnabled(false);
				zoneCheck.setEnabled(false);
			} else if (ACTION == ACTIONS.ADD) {
				visibleCheck.setEnabled(false);
				zoneCheck.setEnabled(false);
				acceptButton.setEnabled(false);
				nameField.blankTextField();
				JOptionPane.showMessageDialog(
						frame,
						"El grupo \"" + groupName 
						+ "\" ya existe.\nPor favor, Intente otro nombre.");
				frame.requestFocus();
			} else if (ACTION == ACTIONS.EDIT) {
				nameField.setEditable(false);
				visibleCheck.setEnabled(true);
				zoneCheck.setEnabled(true);
				acceptButton.setEnabled(true);
			} else if (ACTION == ACTIONS.DELETE) {
				nameField.setEditable(false);
				acceptButton.setEnabled(true);
			}
		}
		else {
			if (ACTION == ACTIONS.SEARCH) {
				visibleCheck.setSelected(false);
				zoneCheck.setSelected(false);
				visibleCheck.setEnabled(false);
				zoneCheck.setEnabled(false);
				JOptionPane.showMessageDialog(
						frame,
						"El grupo \"" + groupName 
						+ "\" no existe.\nPor favor, intente otro nombre.");				
			} else if (ACTION == ACTIONS.ADD) {
				acceptButton.setEnabled(true);
			} else if (ACTION == ACTIONS.EDIT) {
				visibleCheck.setSelected(false);
				zoneCheck.setSelected(false);
				visibleCheck.setEnabled(false);
				zoneCheck.setEnabled(false);
				nameField.blankTextField();
				JOptionPane.showMessageDialog(
						frame,
						"El grupo \"" + groupName 
						+ "\" no existe.\nPor favor, intente otro nombre.");				
			} else if (ACTION == ACTIONS.DELETE) {
				visibleCheck.setSelected(false);
				zoneCheck.setSelected(false);
				visibleCheck.setEnabled(false);
				zoneCheck.setEnabled(false);
				JOptionPane.showMessageDialog(
						frame,
						"El grupo \"" + groupName 
						+ "\" no existe.\nPor favor, intente otro nombre.");				
			}
		}
	}	
	
	public void clean() {
		nameField.setSelectedIndex(0);
		if (ACTION == ACTIONS.ADD || ACTION == ACTIONS.EDIT) {
			visibleCheck.setEnabled(true);
			zoneCheck.setEnabled(true);
			nameField.setEnabled(true);
			nameField.setEditable(true);
		}
		visibleCheck.setSelected(false);
		zoneCheck.setSelected(false);
		nameField.blankTextField();
	}
	
	private Document getDocumentToAdd() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR004");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		String visibleValue = Boolean.valueOf(visibleCheck.isSelected()).toString();
		String zoneValue = Boolean.valueOf(zoneCheck.isSelected()).toString();

		pack.addContent(createField(nameField.getText().toUpperCase()));
		pack.addContent(createField(visibleValue));
		pack.addContent(createField(zoneValue));

		transaction.addContent(pack);
		
		return doc;
	}
	
	private Document getDocumentToEdit() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		Element driver = new Element("driver");
        driver.setText("TR005");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		
		String visibleValue = Boolean.valueOf(visibleCheck.isSelected()).toString();
		String zoneValue = Boolean.valueOf(zoneCheck.isSelected()).toString();

		pack.addContent(createField(nameField.getText().toUpperCase()));
		pack.addContent(createField(visibleValue));
		pack.addContent(createField(zoneValue));
		pack.addContent(createField(oldGroupName));
		
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
        driver.setText("TR006");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		pack.addContent(createField(nameField.getText().toUpperCase()));
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
		nameField.setSelectedItem(name);
		nameField.setEditable(false);
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public JCheckBox getVisibleCheck() {
		return visibleCheck;
	}

	public JCheckBox getZoneCheck() {
		return zoneCheck;
	}

	public JButton getAcceptButton() {
		return acceptButton;
	}

}