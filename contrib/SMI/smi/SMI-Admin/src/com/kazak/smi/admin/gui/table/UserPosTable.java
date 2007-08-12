package com.kazak.smi.admin.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
//import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.gui.table.models.UserModel;
import com.kazak.smi.admin.gui.managers.PosManager;
//import com.kazak.smi.admin.gui.managers.WorkStationsManager;
import com.kazak.smi.admin.gui.misc.GUIFactory;

public class UserPosTable extends JTable implements ActionListener {

	private static final long serialVersionUID = 4182381331394270487L;
	private UserModel model;
	//private JDialog frame;
	private JPanel panel;
	private JButton addButton;
	private JButton deleteButton;
	//private WorkStationsManager ws;
	private String username;
	
	public UserPosTable(JDialog frame, String username, boolean withButtons) {
		this.username = username;
		//this.frame = frame;
		panel =  new JPanel(new BorderLayout());
		model = new UserModel();
		this.setModel(model);
		this.setGridColor(Color.BLACK);
		this.setSurrendersFocusOnKeystroke(true);

		fillTable();
		
		JScrollPane jscroll = new JScrollPane(UserPosTable.this);
		panel.add(jscroll,BorderLayout.CENTER);
		
		if (withButtons) {
			panel.add(getButtonsPanel(),BorderLayout.WEST);
		}
		
		panel.setPreferredSize(new Dimension(120,100));
	}
	
	private void fillTable() {
		ArrayList<Cache.POS> posList = Cache.getWorkStationsListByUser(username);
		for (Cache.POS upv : posList) {
			addData(upv.getPOSCode(),upv.getName());
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("ADD")) {
			System.out.println("Abrir la opcion de adicionar punto");
			PosManager pos = new PosManager();
			pos.linkPos(this);
		}
		if (command.equals("DEL")) {
			int rows = getRowCount()-1;
			if (rows >= 0 ) {
				if (rows == 0){
					deleteButton.setEnabled(false);
				}
				int selected = getSelectedRow();
				if (selected == -1) {
					model.remove(getRowCount()-1);
				}
				else {
					model.remove(selected);	
				}
			}			
		}
	}

	private JPanel getButtonsPanel() {
		GUIFactory factory = new GUIFactory();
		addButton = factory.createButton("add.png");
		deleteButton = factory.createButton("remove.png");		
		deleteButton.setEnabled(false);
		addButton.setActionCommand("ADD");
		addButton.addActionListener(this);
		deleteButton.setActionCommand("DEL");
		deleteButton.addActionListener(this);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.Y_AXIS));
		buttonsPanel.add(addButton);
		buttonsPanel.add(deleteButton);

		return buttonsPanel;
	}
	
	public void disableButtons() {
		addButton.setEnabled(false);
		deleteButton.setEnabled(false);
	}
	
	public void enableDeleteButton() {
		deleteButton.setEnabled(true);
	}
	
	public void disableDeleteButton() {
		deleteButton.setEnabled(false);
	}
	
	public void addData(String code,String name) {
		model.addRow(code,name);
	}
	
	public boolean isAlreadyIn(String code) {
		return model.isAlreadyIn(code);
	}
	
	public void addRow() {
		model.addRow();
	}
	
	public UserModel getModel() {
		return model;
	}
	
	public void clean() {
		model.clear();
	}
	
	public JPanel getPanel() {
		return panel; 
	}
	
	public int getRowCount () {
		return model.getRowCount();
	}
	
	public void removeRow(int rows) {
		if (rows != -1) {
			model.remove(UserPosTable.this.getRowCount()-1);
			if (rows == 0){
				deleteButton.setEnabled(false);
			}
		}
	}
	
	public Vector<Vector> getData() {
		return model.getData();
	}
	
	public void enableButtons() {
		addButton.setEnabled(true);
		deleteButton.setEnabled(true);
	}
	
	public void insertData(Document doc){
		Element root = doc.getRootElement();
		root.getChildText("AppOwner");
	}

}