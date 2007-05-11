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
	private JComboBox field;
	
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
		String[] options = {"Códigos","Nombres","Direcciones IP"};
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
			
		}	
		if (command.equals("close")) {
			frame.dispose();
		}	
	}
	
}
