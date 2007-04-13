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
import javax.swing.tree.TreePath;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.Cache.WorkStation;
import com.kazak.smi.admin.gui.TreeManagerGroups.SortableTreeNode;
import com.kazak.smi.admin.misc.NumericDataValidator;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.lib.misc.FixedSizePlainDocument;

public class WorkStationsManager extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private AutoCompleteComboBox FieldName;
	private JTextField FieldCode;
	private JTextField FieldIp;
	private JPanel JPCenter;
	private JPanel JPSouth;
	private JButton JBAccept;
	private JButton JBCancel;
	private JButton JBClean;
	private ArrayList<Component> listComps = new ArrayList<Component>();
	private JComboBox JCBGroups;
	private JButton JBSearch;
	private JPanel jpsearch;
	private enum ACTIONS  {ADD,EDIT,SEARCH,DELETE};
	private ACTIONS ACTION;
	private String oldCode;

	final JFrame frame = this;
	
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
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComps();
		addComps();
		JBSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = FieldName.getText().toUpperCase();
				oldCode = key;
				
				if (Cache.containsWs(key)) {
					WorkStation ws = Cache.getWorkStation(key);
					FieldCode.setText(ws.getCode());
					FieldIp.setText(ws.getIp());
					JCBGroups.setSelectedItem(ws.getGidName());
					System.out.println("*** Actualizando arbol desde WorkStation (busqueda)");
					/*
					TreeManagerGroups.currTpath = new TreePath(
							new Object[] {
										new SortableTreeNode(MainWindow.getAppOwner()),
										new SortableTreeNode(ws.getGidName()),
										new SortableTreeNode(ws.getName())});*/
										
					//TreeManagerGroups.expand();
					if (ACTION == ACTIONS.ADD) {
						FieldName.setEditable(false);
						FieldIp.setEditable(false);
						JCBGroups.setEnabled(false);
						JBAccept.setEnabled(false);
					}
					if (ACTION == ACTIONS.DELETE) {
						FieldCode.setEditable(false);
						FieldName.setEditable(false);
						FieldIp.setEditable(false);
						JCBGroups.setEnabled(false);
						JBAccept.setEnabled(true);
						
					}
					if (ACTION == ACTIONS.EDIT) {
						FieldName.setEditable(false);
					}
				}
				else {
					if (ACTION == ACTIONS.ADD) {
						FieldName.setEditable(true);
						FieldIp.setEditable(true);
						JBAccept.setEnabled(true);
						JCBGroups.setEnabled(true);
					}
				}
			}
		});
	}
	
	public void add() {
		this.setTitle("Nuevo Punto de Colocación");
		JBAccept.setActionCommand("accept");
		JBCancel.setActionCommand("cancel");
		ACTION = ACTIONS.ADD;
		this.setVisible(true);
		
	}
	
	public void edit() {
		this.setTitle("Editar Punto de Colocación");
		JBAccept.setActionCommand("save");
		JBCancel.setActionCommand("cancel");
		ACTION = ACTIONS.EDIT;
		this.setVisible(true);
	}
	
	public void delete() {
		this.setTitle("Borrar Punto de Colocación");
		FieldCode.setEditable(false);
		FieldIp.setEditable(false);
		JCBGroups.setEnabled(false);
		ACTION = ACTIONS.DELETE;
		JBAccept.setActionCommand("remove");
		JBCancel.setActionCommand("cancel");
		this.setVisible(true);
	}
	
	public void search() {
		this.setTitle("Buscar Punto de Colocación");
		FieldCode.setEditable(false);
		FieldIp.setEditable(false);
		JCBGroups.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		JBAccept.setEnabled(false);
		JBCancel.setActionCommand("cancel");
		ACTION = ACTIONS.SEARCH;
		this.setVisible(true);
	}
	public void search(JTable t) {
		this.table = t;
		this.setTitle("Buscar Punto de Colocación");
		FieldCode.setEditable(false);
		FieldIp.setEditable(false);
		JCBGroups.setEnabled(false);
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		JBAccept.setActionCommand("search_code");
		JBCancel.setActionCommand("cancel");
		ACTION = ACTIONS.SEARCH;
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				int lastrow = table.getRowCount()-1;
				if (lastrow>=0) {
					table.setValueAt(FieldCode.getText(),lastrow,0);
					table.setValueAt(FieldName.getText(),lastrow,1);
					table.requestFocusInWindow();
					table.changeSelection(lastrow,0,true,true);
				}
			}
		});
		this.setVisible(true);
	}
	private void initComps() {
		
		listComps.add(FieldName = new AutoCompleteComboBox(Cache.getListKeysWs(),false,50));
		listComps.add(FieldCode = new JTextField());
		listComps.add(FieldIp   = new JTextField());
		listComps.add(JCBGroups = new JComboBox(Cache.getListKeys()));
		
		FieldCode.setDocument(new NumericDataValidator(4));
		FieldIp.setDocument(new FixedSizePlainDocument(15));
		
		FieldIp.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String entrada =  FieldIp.getText();
				if (!"".equals(entrada)) {
					String octetos[] = entrada.split("\\.");
					try {
						if (Integer.parseInt(octetos[0]) < 255 &&
							Integer.parseInt(octetos[1])  < 255 &&
							Integer.parseInt(octetos[2])  < 255 &&
							Integer.parseInt(octetos[3])  < 255) {
							FieldIp.setBackground(Color.WHITE);
						}
						else {
							Toolkit.getDefaultToolkit().beep();
							FieldIp.setBackground(Color.RED);
						}
						
					} catch (NumberFormatException ex) {
						Toolkit.getDefaultToolkit().beep();
						FieldIp.setBackground(Color.RED);
					}
				}
				else {
					FieldIp.setBackground(Color.WHITE);
				}
			}
		});
		FieldName.addItemListener(this);
		JBAccept = new JButton("Aceptar");
		JBCancel = new JButton("Cancelar");
		GUIFactory gui = new GUIFactory();
		JBSearch = gui.createButton("search.png");
		jpsearch = new JPanel();
		jpsearch.setLayout(new BoxLayout(jpsearch,BoxLayout.Y_AXIS));
		jpsearch.add(JBSearch);
		
		JBAccept.addActionListener(this);
		JBCancel.addActionListener(this);
		
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

		JPSouth.add(JBAccept);
		JPSouth.add(JBCancel);
		
		JBClean = new JButton("Limpiar");
		JBClean.setActionCommand("clean");
		JBClean.setMnemonic('L');
		JBClean.addActionListener(this);
		JPSouth.add(JBClean);
		
		this.add(JPCenter,BorderLayout.CENTER);
		this.add(JPSouth,BorderLayout.SOUTH);
		this.add(new JPanel(),BorderLayout.NORTH);
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(jpsearch,BorderLayout.EAST);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		Document document = null;
		if (command.equals("cancel")) {
			this.dispose();
		}
		if (command.equals("search_code")) {
			int lastrow = table.getRowCount()-1;
			if (lastrow>=0) {
				table.setValueAt(FieldCode.getText(),lastrow,0);
				table.setValueAt(FieldName.getText(),lastrow,1);
				table.requestFocusInWindow();
				table.changeSelection(lastrow,0,true,true);
			}
			this.dispose();
		}
		else if (command.equals("clean")) {
			this.clean();
		}
		else if (command.equals("accept")) {
			String name = FieldName.getText().toUpperCase();
			WorkStation ws = Cache.getWorkStation(name);
			/* String group = TreeManagerGroups.getGroupNode();
			System.out.println("Grupo XXX: " + group);
			System.out.println("Nodo XXX: " + name); */
			
			TreeManagerGroups.currTpath = new TreePath(
					new Object[] {
								new SortableTreeNode(MainWindow.getAppOwner()),
								new SortableTreeNode(ws.getGidName()),
								new SortableTreeNode(ws.getName())});  
			
			/* TreeManagerGroups.currTpath = new TreePath(
					new Object[] {
								new SortableTreeNode(MainWindow.getAppOwner()),
								new SortableTreeNode(group),
								new SortableTreeNode(name)}); */
			
			if (!"".equals(name) && ws==null && FieldCode.getText().length() == 4) {
				document = getDocumentForAdd();
				this.dispose();
			}
			else {
				if ("".equals(name)) {
					JOptionPane.showMessageDialog(
							frame,
							"<html>" +
							"<h3>" +
							"Debe ingresar por lo menos el código y el nombre<br>" +
							"del punto de colocación"+
							"<html>");
				}
				else if (FieldCode.getText().length() < 4) {
					JOptionPane.showMessageDialog(
							frame,
							"<html>" +
							"<h3>" +
							"Debe ingresar un código con 4 digitos<br>"+
							"<html>");
				}
				else if (ws!=null) {
					JOptionPane.showMessageDialog(
						frame,
						"<html>" +
						"<h3>" +
						"El código o el nombre ya existe.<br>" +
						"Verifique con el botón de busqueda"+
						"<html>");
				}
			}
		}
		else if (command.equals("save")) {
			String key = FieldCode.getText();
			String name = FieldName.getText().toUpperCase();
			TreeManagerGroups.currTpath = new TreePath(
					new Object[] {
								new SortableTreeNode(MainWindow.getAppOwner()),
								new SortableTreeNode(JCBGroups.getSelectedItem()),
								new SortableTreeNode(name)});
			if (!"".equals(key) && !"".equals(name)) {
				document = getDocumentForEdit();
				this.dispose();
			}
			else {
				if ("".equals(key) || "".equals(name)) {
					JOptionPane.showMessageDialog(
							frame,
							"<html>" +
							"<h3>" +
							"Debe ingresar por lo menos el código y el nombre<br>" +
							"del punto de colocación"+
							"<html>");
				}
				else if (key.length() < 4) {
					JOptionPane.showMessageDialog(
							frame,
							"<html>" +
							"<h3>" +
							"Debe ingresar un código con 4 digitos<br>"+
							"<html>");
				}
			}
		}
		else if (command.equals("remove")) {
			String name = FieldName.getText().toUpperCase();
			WorkStation ws = Cache.getWorkStation(name);
			if (ws!=null) {
				if (ws.getUsers().size() == 0) {
					document = getDocumentForDelete();
					this.dispose();		
				}
				else {
					JOptionPane.showMessageDialog(
							frame,
							"<html>" +
							"<h3>" +
							"El punto a eliminar no esta vació.<br>" +
							"Para poder eliminar un punto no debe tener<br>" +
							"puntos de colocación asignados" +
							"<html>");
				}
			}
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
		if (ACTION == ACTIONS.ADD) {
			FieldName.setEditable(true);
			FieldIp.setEditable(true);
			JBAccept.setEnabled(true);
		}
		if (ACTION == ACTIONS.DELETE) {
			JBAccept.setEnabled(true);
		}
		FieldIp.setBackground(Color.WHITE);
		FieldCode.setEnabled(true);
		FieldName.setEditable(true);
		FieldCode.setText("");
		FieldName.setSelectedIndex(0);
		FieldIp.setText("");
		FieldName.requestFocus();
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
			"".equals(FieldIp.getText()) ? 
			"127.0.0.1" : 
			FieldIp.getText();
		Cache.Group g = Cache.getGroup((String)JCBGroups.getSelectedItem());
		pack.addContent(createField(FieldCode.getText()));
		pack.addContent(createField(FieldName.getText().toUpperCase()));
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
		pack.addContent(createField(FieldCode.getText()));
		pack.addContent(createField(oldCode));
		transaction.addContent(pack);
		
		pack = new Element("package");
		String ip = 
			"".equals(FieldIp.getText()) ? 
			"127.0.0.1" : 
			FieldIp.getText();
		
		Cache.Group g = Cache.getGroup((String)JCBGroups.getSelectedItem());
		pack.addContent(createField(FieldCode.getText()));
		pack.addContent(createField(FieldName.getText().toUpperCase()));
		pack.addContent(createField(ip));
		pack.addContent(createField(g.getId()));
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
		pack.addContent(createField(FieldCode.getText()));
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Element createField(String text) {
		Element element = new Element("field");
		element.setText(text);
		return element;
	}

	public void itemStateChanged(ItemEvent e) {
		JBSearch.doClick();	
	}
	
	public void setFieldName(String name) {
		FieldName.setSelectedItem(name);
		FieldName.setEditable(false);
	}

	public JButton getJBSearch() {
		return JBSearch;
	}

	public JTextField getFieldCode() {
		return FieldCode;
	}

	public JTextField getFieldIp() {
		return FieldIp;
	}

	public JComboBox getJCBGroups() {
		return JCBGroups;
	}
}