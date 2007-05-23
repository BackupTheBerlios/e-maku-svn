package com.kazak.smi.admin.gui.table;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdom.Document;
import org.jdom.Element;
//import org.jdom.output.Format;
//import org.jdom.output.XMLOutputter;

import com.kazak.smi.admin.gui.table.OnLineUsersTable;
import com.kazak.smi.admin.gui.GUIFactory;
import com.kazak.smi.admin.gui.UsersList;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;

// This class shows the Search Panel for users online

public class UserSearchPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JButton close, viewMsg;
	private UsersList frame;
	private OnLineUsersTable table;
	private JButton searchButton;
	private JTextField searchTextField;
	private JComboBox fieldCombo;
	
	// Search Panel Constructor
	public UserSearchPanel(UsersList frame) {
		this.frame = frame;
		setLayout(new BorderLayout());

		JLabel search = new JLabel("Buscar:");
		searchTextField = new JTextField(15);
		searchTextField.addKeyListener(this);
		JLabel in = new JLabel(" en ");
		GUIFactory gui = new GUIFactory();
		searchButton = gui.createButton("search.png");
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);
		String[] options = {"Códigos","Nombres","Direcciones IP"};
		fieldCombo = new JComboBox(options);
		
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		top.add(search);
		top.add(searchTextField);
		top.add(in);
		top.add(fieldCombo);
		top.add(searchButton);
					
		table = new OnLineUsersTable(frame);
		table.setModelTab(0);
		JScrollPane jscroll = new JScrollPane(table);
		jscroll.setPreferredSize(new Dimension(500,300));
		jscroll.setAutoscrolls(true);
	
		viewMsg = new JButton("Ver Mensajes");
		viewMsg.setActionCommand("view");
		viewMsg.addActionListener(this);
		viewMsg.setEnabled(false);
		table.setListButton(viewMsg);		
		
		close = new JButton("Cerrar");
		close.setActionCommand("close");
		close.addActionListener(this);
		
		JPanel down = new JPanel();
		down.setLayout(new FlowLayout(FlowLayout.CENTER));
		down.add(viewMsg);
		down.add(close);
		
		add(top,BorderLayout.NORTH);
		add(jscroll,BorderLayout.CENTER);
		add(down,BorderLayout.SOUTH);
	}

	// This method handles action events
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("search")) {
			startSearch();
		}	
		if (command.equals("view")) {
			int row = table.getSelectedRow();
			if(row != -1) {
				String login = (String) table.getModel().getValueAt(row, 0);
				new MessagesDialog(frame,login);
			}
		}
		if (command.equals("close")) {
			frame.dispose();
		}	
	}

	private void startSearch() {
		int typeCursor = Cursor.WAIT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		setCursor(cursor);
		String pattern = searchTextField.getText();
		
		if (pattern.length() > 0) {
			String field = Integer.toString(fieldCombo.getSelectedIndex());
			if ((field.equals("2")) && (!hasIPFormat(pattern))) {
				setNormalCursor();
	            JOptionPane.showMessageDialog(
	                    this,
	                    "Por favor, Ingrese una dirección IP válida.");	
	            return;
			} else {
				frame.setWindowTitle();
				loadSearchResult();
				requestASearch(pattern,field);
				setNormalCursor();
			}
		} else {
			setNormalCursor();
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, Ingrese un valor en el campo de búsqueda.");
            return;
		}
	}
	
	private void setNormalCursor() {
		int typeCursor = Cursor.DEFAULT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		setCursor(cursor);		
	}
	
	public boolean hasIPFormat(String s) {
		   for(int i = 0; i < s.length(); i++) { 
		       char c = s.charAt(i);
		       if (!Character.isDigit(c) && (c != '.')) {
		    	   System.out.println("C " + c);
		           return false;
		       }
		     }
		   return true;
	 }
	
	private void loadSearchResult() {
		class Monitor extends Thread {
			Document doc= null;
			public void run() {
				try {
					doc = QuerySender.getResultSetFromST("RESULT");
					updateUserList(doc);		            
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}
		}
		new Monitor().start();		
	}
	
	// Solicita el total de usuarios conectados
	public void requestASearch(String pattern,String area) {
		// Enviando comando al servidor para ser aprobado
		Element onlist = new Element("SEARCH");
		Element id = new Element("id").setText("query");
		Element element1 = new Element("pattern").setText(pattern);
		Element element2 = new Element("area").setText(area);
		onlist.addContent(id);
		onlist.addContent(element1);
		onlist.addContent(element2);
		Document document = new Document(onlist);

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
	
	public void updateUserList(Document doc) {
        table.getModel().setQuery(doc);
        if (table.getGroupSize() == 0 && viewMsg.isEnabled()) {
        	viewMsg.setEnabled(false);
        }
 	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode==KeyEvent.VK_ENTER) {
			startSearch();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
	
}
