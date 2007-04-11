package com.kazak.smi.admin.gui;

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
import java.util.Vector;

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
import com.kazak.smi.admin.gui.TreeManagerGroups.SortableTreeNode;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;

public class GroupsManager extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private AutoCompleteComboBox FieldName;
	private JCheckBox JCheckVisible;
	private JCheckBox JCheckZone;
	
	private JPanel JPCenter;
	private JPanel JPSouth;
	private JButton JBAccept;
	private JButton JBCancel;
	private JButton JBClean;
	private ArrayList<Component> listComps = new ArrayList<Component>();

	private JButton JBSearch;
	private JPanel jpsearch;
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
		initComps();
		addComps();
		
		JBSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String gname = FieldName.getText().toUpperCase();
				Cache.Group group = Cache.getGroup(gname);
				
				JCheckVisible.setSelected(false);
				JCheckZone.setSelected(false);
				
				if (group!=null) {
					oldGroupName = FieldName.getText().toUpperCase();
					JCheckVisible.setSelected(group.getVisible());
					JCheckZone.setSelected(group.getZone());
					TreeManagerGroups.currTpath = new TreePath(
							new Object[] {
										new SortableTreeNode(MainWindow.getAppOwner()),
										new SortableTreeNode(oldGroupName)});
					TreeManagerGroups.expand();
					if (ACTION == ACTIONS.ADD) {
						FieldName.setEditable(false);
						JCheckVisible.setEnabled(false);
						JCheckZone.setEnabled(false);
						FieldName.setEnabled(false);
						JOptionPane.showMessageDialog(
								frame,
								"El grupo:\""+gname+"\" ya existe");
					}
					if (ACTION == ACTIONS.EDIT) {
						FieldName.setEditable(false);
						JCheckVisible.setEnabled(true);
						JCheckZone.setEnabled(true);
						JBAccept.setEnabled(true);
					}
					if (ACTION == ACTIONS.DELETE) {
						FieldName.setEditable(false);
						JBAccept.setEnabled(true);
					}
				}
			}
		});
	}
	
	public void add() {
		this.setTitle("Nuevo Grupo");
		JBAccept.setActionCommand("accept");
		JBCancel.setActionCommand("cancel");
		this.ACTION = ACTIONS.ADD;
		this.setVisible(true);
	}
	
	public void edit() {
		this.setTitle("Editar Grupo");
		JBAccept.setActionCommand("save");
		JBCancel.setActionCommand("cancel");
		JCheckVisible.setEnabled(false);
		JCheckZone.setEnabled(false);
		this.ACTION = ACTIONS.EDIT;
		this.setVisible(true);
	}
	
	public void delete() {
		this.setTitle("Borrar Grupo");
		JCheckVisible.setEnabled(false);
		JCheckZone.setEnabled(false);
		JBAccept.setEnabled(false);
		JBAccept.setActionCommand("remove");
		JBCancel.setActionCommand("cancel");
		this.ACTION = ACTIONS.DELETE;
		this.setVisible(true);
	}
	
	public void search() {
		this.setTitle("Buscar Grupo");
		JCheckVisible.setEnabled(false);
		JCheckZone.setEnabled(false);
		JBAccept.setEnabled(false);
		JBCancel.setActionCommand("cancel");
		this.ACTION = ACTIONS.SEARCH;
		this.setVisible(true);
	}
	
	private void initComps() {
		
		listComps.add(FieldName   = new AutoCompleteComboBox(new Vector<String>(Cache.getListKeys()),false,40));
		listComps.add(JCheckVisible= new JCheckBox());
		listComps.add(JCheckZone   = new JCheckBox());
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
		JPanel center = new JPanel(new BorderLayout());

		center.add(JPCenter,BorderLayout.NORTH);
		
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
		if (command.equals("cancel")) {
			this.dispose();
		}
		else if (command.equals("clean")) {
			this.clean();
		}
		else if (command.equals("accept")) {
			String key = FieldName.getText().toUpperCase();
			TreeManagerGroups.currTpath = new TreePath(
					new Object[] {
								new SortableTreeNode(MainWindow.getAppOwner()),
								new SortableTreeNode(key)});
			if (!Cache.containsGroup(key) && !"".equals(key)) {
				document = getDocumentForAdd();
				this.dispose();	
			}
			else {
				if ("".equals(key)) {
					JOptionPane.showMessageDialog(
							frame,
							"<html>" +
							"<h3>" +
							"Debe ingresar por lo menos el nombre del grupo"+
							"<html>");
				}
				else {
				JOptionPane.showMessageDialog(
						frame,
						"<html>" +
						"<h3>" +
						"Ya existe un grupo con ese nombre.<br>" +
						"Ingrese uno diferente o compruebe <br>" +
						"con el botón de búsqueda"+
						"<html>");
				}
			}
		}
		else if (command.equals("save")) {
			String key = FieldName.getText().toUpperCase();
			TreeManagerGroups.currTpath = new TreePath(
					new Object[] {
								new SortableTreeNode(MainWindow.getAppOwner()),
								new SortableTreeNode(key)});
			if (!Cache.containsGroup(key)) {
				document = getDocumentForEdit();
				this.dispose();	
			}
			else if (key.equals(oldGroupName)){
				document = getDocumentForEdit();
				this.dispose();
			}
			else {
				JOptionPane.showMessageDialog(
						frame,
						"<html>" +
						"<h3>" +
						"Ya existe un grupo con ese nombre.<br>" +
						"Ingrese uno diferente o compruebe <br>" +
						"con el botón de búsqueda"+
						"<html>");
			}
		}
		else if (command.equals("remove")) {
			if (!FieldName.getText().equals("")) {
				String key = FieldName.getText().toUpperCase();
				TreeManagerGroups.currTpath = new TreePath(
						new Object[] {
									new SortableTreeNode(MainWindow.getAppOwner()),
									new SortableTreeNode(key)});
				int nrus = Cache.getGroup(key).getUsers().size();
				int nrws = Cache.getGroup(key).getWorkStations().size();
				if (nrus==0 && nrws==0) {
					document = getDocumentForDelete();
					this.dispose();	
				}
				else {
					JOptionPane.showMessageDialog(
							frame,
							"<html>" +
							"<h3>" +
							"El Grupo a eliminar no esta vació.<br>" +
							"Para poder eliminar un grupo no debe tener<br>" +
							"usuarios ni puntos de colocación asignados" +
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
		FieldName.setSelectedIndex(0);
		if (ACTION == ACTIONS.ADD || ACTION == ACTIONS.EDIT) {
			JCheckVisible.setEnabled(true);
			JCheckZone.setEnabled(true);
			FieldName.setEnabled(true);
			FieldName.setEditable(true);
		}
		JCheckVisible.setSelected(false);
		JCheckZone.setSelected(false);
		FieldName.requestFocus();
	}
	
	private Document getDocumentForAdd() {
		
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR004");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		String visibleValue = Boolean.valueOf(JCheckVisible.isSelected()).toString();
		String zoneValue = Boolean.valueOf(JCheckZone.isSelected()).toString();

		pack.addContent(createField(FieldName.getText().toUpperCase()));
		pack.addContent(createField(visibleValue));
		pack.addContent(createField(zoneValue));

		transaction.addContent(pack);
		
		return doc;
	}
	
	private Document getDocumentForEdit() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		Element driver = new Element("driver");
        driver.setText("TR005");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		
		String visibleValue = Boolean.valueOf(JCheckVisible.isSelected()).toString();
		String zoneValue = Boolean.valueOf(JCheckZone.isSelected()).toString();

		pack.addContent(createField(FieldName.getText().toUpperCase()));
		pack.addContent(createField(visibleValue));
		pack.addContent(createField(zoneValue));
		pack.addContent(createField(oldGroupName));
		
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
        driver.setText("TR006");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		pack.addContent(createField(FieldName.getText().toUpperCase()));
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

	public JCheckBox getJCheckVisible() {
		return JCheckVisible;
	}

	public JCheckBox getJCheckZone() {
		return JCheckZone;
	}

	public JButton getJBAccept() {
		return JBAccept;
	}
}