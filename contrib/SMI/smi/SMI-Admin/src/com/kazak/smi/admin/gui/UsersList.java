package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.Cache.Group;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;

/*
 *  Esta clase muestra la lista de usuarios en linea de forma agrupada
 */

public class UsersList extends JFrame implements ActionListener,PopupMenuListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private JButton close;
	private JButton update;
	private JLabel jGroups;
	private JComboBox groups;
	private JTable table;
	private OnLineModel model;
	private int uTotal = 0;
	private HashMap<String,String> hashGroup;
	
	public UsersList() {
		this.setLayout(new BorderLayout());
		this.setSize(600,400);
		
		initInterface();
		String date = getFormattedDate();
		
		this.setTitle("Usuarios en Linea: " + uTotal + " / " + date);
		
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public void initInterface() {
		
		jGroups = new JLabel("Grupos:");
		Object[] obj = Cache.getList().toArray();
		groups = new JComboBox(Cache.getGroupsList());
		hashGroup = new HashMap<String,String>();
		for (Object infoGroup:obj) {
			Group g = (Group)infoGroup;
			hashGroup.put(g.getName(), g.getId());
			//System.out.println(g.getName()+" "+g.getId());
		}
		
		groups.addPopupMenuListener(this);
		loadUserList();
		loadTotal();
		requestUsersTotal();
		requestOnlineUsers(groups.getSelectedItem().toString());
		
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

	private void loadTotal() {
		class Monitor  extends Thread {
			
			Document doc= null;
			public void run() {
				try {
					doc = QuerySender.getResultSetST("TOTAL");
					XMLOutputter xmlOutputter = new XMLOutputter();
		            xmlOutputter.setFormat(Format.getPrettyFormat());
		    		String date = getFormattedDate();
		    		Element elm = doc.getRootElement();
		    		List list = elm.getChildren("row");
		    		Element col = (Element)list.get(0);
		    		String uTotal = col.getValue();
		    		setTitle("Usuarios en Linea: " + uTotal + " / " + date);
				} catch (QuerySenderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		new Monitor().start();		
	}

	private void loadUserList() {
		class Monitor  extends Thread {
			
			Document doc= null;
			public void run() {
				try {
					doc = QuerySender.getResultSetST("LIST");
					model.setQuery(doc);
				} catch (QuerySenderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		new Monitor().start();		
	}
	
	public void requestUsersTotal() {
		// Enviando comando al servidor para ser aprobado
		Cache.getGroup("key");
		Element onlist = new Element("ONLINELIST");
		Element id = new Element("id").setText("TOTAL");
		onlist.addContent(id);
		Document document = new Document(onlist);

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
	
	public void requestOnlineUsers(String group) {
		// Enviando comando al servidor para ser aprobado
		Cache.getGroup("key");
		Element onlist = new Element("ONLINELIST");
		Element id = new Element("id").setText("LIST");
		onlist.addContent(id);
		Document document = new Document(onlist);
        Element args = new Element("args");
        onlist.addContent(hashGroup.get(group));
        args.setText(group);

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
	
	
    public static String getFormattedDate() {

    	SimpleDateFormat now = new SimpleDateFormat("E, dd MMM yyyy - H:m");
    	Date date = new Date();
    	
    	return now.format(date);
    }

	public void popupMenuCanceled(PopupMenuEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub
		loadUserList();
		requestOnlineUsers(((JComboBox)e.getSource()).getSelectedItem().toString());
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}