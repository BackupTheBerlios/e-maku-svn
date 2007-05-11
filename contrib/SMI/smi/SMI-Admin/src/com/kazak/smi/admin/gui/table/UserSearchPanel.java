package com.kazak.smi.admin.gui.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/* import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import java.io.IOException;
import java.util.List;
import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException; */

import com.kazak.smi.admin.gui.table.UsersTable;
import com.kazak.smi.admin.gui.GUIFactory;
import com.kazak.smi.admin.gui.UsersList;

// This class shows the Search Panel for users online

public class UserSearchPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton close;
	private UsersList frame;
	private UsersTable table;
	private JButton searchBT;
	private JTextField searchTF;
	JComboBox field;
	
	// Search Panel Constructor
	public UserSearchPanel(UsersList frame) {
		this.frame = frame;
		setLayout(new BorderLayout());

		JLabel search = new JLabel("Buscar:");
		searchTF = new JTextField(15);
		JLabel in = new JLabel(" en ");
		GUIFactory gui = new GUIFactory();
		searchBT = gui.createButton("search.png");
		searchBT.setActionCommand("search");
		searchBT.addActionListener(this);
		String[] options = {"Código","Nombre","Dirección IP"};
		field = new JComboBox(options);
		
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		top.add(search);
		top.add(searchTF);
		top.add(in);
		top.add(field);
		top.add(searchBT);
					
		table = new UsersTable();
		JScrollPane js = new JScrollPane(table);
		js.setPreferredSize(new Dimension(500,300));
		js.setAutoscrolls(true);
				
		close = new JButton("Cerrar");
		close.setActionCommand("close");
		close.addActionListener(this);
		
		JPanel down = new JPanel();
		down.setLayout(new FlowLayout(FlowLayout.CENTER));
		down.add(close);
		
		add(top,BorderLayout.NORTH);
		add(js,BorderLayout.CENTER);
		add(down,BorderLayout.SOUTH);
	}

	// This method handles action events
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("search")) {
			//frame.updateList("SIN GRUPO");
		}	
		if (command.equals("close")) {
			frame.dispose();
		}	
	}
	
	/*
	public void updateList(String group) {
		loadUserList();
		loadTotal();
		requestUsersTotal();
		requestOnlineUsers(group);
		if (oneTable) {
			table.getRenderer().setPressedColumn(0);
			table.getRenderer().setSelectedColumn(0);
			table.getTableHeader().repaint();
		}
	}*/
	/*
	private void loadTotal() {
		class Monitor extends Thread {
			Document doc= null;
			public void run() {
				try {
					doc = QuerySender.getResultSetST("TOTAL");
					XMLOutputter xmlOutputter = new XMLOutputter();
		            xmlOutputter.setFormat(Format.getPrettyFormat());
		    		Element elm = doc.getRootElement();
		    		List list = elm.getChildren("row");
		    		Element col = (Element)list.get(0);
		    		String uTotal = col.getValue();
		    		setWindowLabel(uTotal);
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}
		}
		new Monitor().start();		
	}

	// Captura la lista de usuarios solicitada  
	private void loadUserList() {
		class Monitor  extends Thread {			
			Document doc= null;
			public void run() {
				try {
					doc = QuerySender.getResultSetST("LIST");
					table.getModel().setQuery(doc);
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}
		}
		new Monitor().start();		
	}
	
	// Solicita el total de usuarios conectados
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
	
	// Solicita la lista de usuarios de un grupo determinado
	public void requestOnlineUsers(String group) {
		// Enviando comando al servidor para ser aprobado
		Cache.getGroup("key");
		Element onlist = new Element("ONLINELIST");
		Element id = new Element("id").setText("LIST");
		onlist.addContent(id);
		Document document = new Document(onlist);
        Element args = new Element("args");
        onlist.addContent("5");
        args.setText(group);

		if (document!=null) {
			try {
				SocketWriter.writing(SocketHandler.getSock(),document);
			} catch (IOException ex) {
				System.out.println("Error de entrada y salida");
				System.out.println("Causa: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		
	}
	
    public void setWindowLabel(String total) {
    	String date = UsersList.getFormattedDate();
    	frame.setTitle("Usuarios en Linea: " + total + " / " + date);
    } 

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {		
	}

	public void mousePressed(MouseEvent e) {		
	}

	public void mouseReleased(MouseEvent e) {		
	} */
}
