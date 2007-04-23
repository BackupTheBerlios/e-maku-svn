package com.kazak.smi.admin.gui;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.DefaultCellEditor;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;

public class UsersList extends JFrame implements ActionListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private JButton close;
	private JButton update;
	private JLabel jGroups;
	private JComboBox groups;
	private JTable table;
	private OnLineModel model;
	private int uTotal = 0;
	
	public UsersList() {
		this.setLayout(new BorderLayout());
		this.setSize(600,400);
		
		initInterface();
		requestOnlineUsers();
		
		this.setTitle("Usuarios en Linea: " + uTotal);
		
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public void initInterface() {
		
		jGroups = new JLabel("Grupos:");
		String[] list = {"Uno","Dos","Tres"};
		groups = new JComboBox(list);
		
		model = new OnLineModel();
		table = new JTable(model);
		table.setGridColor(Color.BLACK);
		table.setDefaultEditor(String.class,new CellEditor());
		table.setSurrendersFocusOnKeystroke(true);

		update = new JButton("Actualizar");
		update.setActionCommand("update");
		update.addActionListener(this);

		close = new JButton("Cerrar");
		close.setActionCommand("close");
		close.addActionListener(this);

		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		top.add(jGroups);
		top.add(groups);
		
		JScrollPane js  = new JScrollPane(table);
		JPanel center = new JPanel();
		center.add(js,BorderLayout.CENTER);
		JPanel down = new JPanel();
		down.setLayout(new FlowLayout(FlowLayout.CENTER));
		down.add(update);
		down.add(close);
		
		add(top,BorderLayout.NORTH);
		add(center,BorderLayout.CENTER);
		add(down,BorderLayout.SOUTH);
	}
	
	public void requestOnlineUsers() {
		// Enviando comando al servidor para ser aprobado
		Element transaction = new Element("Transaction");
		Document document = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR014");
        transaction.addContent(driver);

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
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("update")) {
			
		}
		if (command.equals("close")) {
			this.dispose();
		}
	}
	
	class CellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = -3511583773152512776L;

		public CellEditor() {
			super(new JTextField());
		}
		
	    public Object getCellEditorValue() {
	    	String value = ((JTextField)getComponent()).getText();
	        return value;
	    }
	}
	
}
