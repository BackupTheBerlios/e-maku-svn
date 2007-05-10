package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.gui.table.UserModel;

public class UserTable {
	
	
	private static final long serialVersionUID = 4182381331394270487L;
	private UserModel model;
	private JFrame frame;
	private JTable table;
	private JPanel panel;
	private JButton JBAdd;
	private JButton JBDel;
	private JButton JBSearch;
	private WorkStationsManager ws;
	
	public UserTable(JFrame parent) {
		frame = parent;
		panel =  new JPanel(new BorderLayout());
		model = new UserModel();
		table = new JTable(model);
		GUIFactory factory = new GUIFactory();
		JBAdd = factory.createButton("add.png");
		JBDel = factory.createButton("remove.png");
		JBSearch = factory.createButton("search.png");
		table.setGridColor(Color.BLACK);
		table.setDefaultEditor(String.class,new CellEditor());
		table.setSurrendersFocusOnKeystroke(true);
		ws = new WorkStationsManager();
		JBAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addRow();
			}
		});
		
		JBDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getRowCount() > 0 ) {
					int selected = table.getSelectedRow();
					if (selected == -1) {
						model.remove(table.getRowCount()-1);
					}
					else {
						model.remove(selected);	
					}
				}
			}
		});
		JBSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!ws.isVisible()) {
					ws.clean();
					ws.search(table);
				}
			}
		});
		JPanel jpbuttons =new JPanel();
		jpbuttons.setLayout(new BoxLayout(jpbuttons,BoxLayout.Y_AXIS));
		jpbuttons.add(JBAdd);
		jpbuttons.add(JBDel);
		jpbuttons.add(JBSearch);
		
		JScrollPane js  = new JScrollPane(table);
		
		panel.add(js,BorderLayout.CENTER);
		panel.add(jpbuttons,BorderLayout.WEST);
	}
	
	public void disableButtons() {
		JBAdd.setEnabled(false);
		JBDel.setEnabled(false);
		JBSearch.setEnabled(false);
	}
	
	public void setEnabled(boolean b) {
		table.setEnabled(b);
	}
	
	public void addData(String code,String name,Boolean b) {
		model.addRow(code,name,b);
	}
	
	public void clear() {
		model.clear();
	}
	
	public JPanel getPanel() {
		return panel; 
	}
	
	public int getRowCount () {
		return model.getRowCount();
	}
	
	public Vector<Vector> getData() {
		return model.getData();
	}
	
	class CellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = -3511583773152512776L;

		public CellEditor() {
			super(new JTextField());
		}
		
	    public Object getCellEditorValue() {
	    	String value = ((JTextField)getComponent()).getText();
	        if (!Cache.containsWsByCode(value)) {
	        	JOptionPane.showMessageDialog(frame,"El codigo no existe");
	        	((JTextField)getComponent()).setText("");
	        	return "";
	        }
	        return value;
	    }
	}

	public void enableButtons() {
		JBAdd.setEnabled(true);
		JBDel.setEnabled(true);
		JBSearch.setEnabled(true);
	}
}