package com.kazak.comeet.admin.gui.table;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;

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

import com.kazak.comeet.admin.gui.table.UsersOnlineTable;
import com.kazak.comeet.admin.gui.misc.GUIFactory;
import com.kazak.comeet.admin.gui.misc.UsersOnlineFrame;
import com.kazak.comeet.admin.network.SocketHandler;
import com.kazak.comeet.admin.network.SocketWriter;
import com.kazak.comeet.admin.transactions.QuerySender;
import com.kazak.comeet.admin.transactions.QuerySenderException;

// This class shows the Search Panel for users online

public class UserSearchPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JButton close, viewMsg;
	private UsersOnlineFrame frame;
	private UsersOnlineTable userPanelTable;
	private JButton searchButton;
	private JTextField searchTextField;
	private JComboBox fieldCombo;
	private JLabel resultsLabel;
	
	// Search Panel Constructor
	public UserSearchPanel(UsersOnlineFrame frame) {
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
		String[] options = {"Códigos","Nombres","Puntos de Venta","Direcciones IP"};
		fieldCombo = new JComboBox(options);
		resultsLabel = new JLabel("");
		
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		top.add(search);
		top.add(searchTextField);
		top.add(in);
		top.add(fieldCombo);
		top.add(searchButton);
		top.add(resultsLabel);
					
		userPanelTable = new UsersOnlineTable(frame);
		userPanelTable.setModelTab(0);
		JScrollPane jscroll = new JScrollPane(userPanelTable);
		jscroll.setPreferredSize(new Dimension(500,300));
		jscroll.setAutoscrolls(true);
	
		viewMsg = new JButton("Ver Mensajes");
		viewMsg.setMnemonic('V');
		viewMsg.setActionCommand("view");
		viewMsg.addActionListener(this);
		viewMsg.setEnabled(false);
		userPanelTable.setListButton(viewMsg);		
		
		close = new JButton("Cerrar");
		close.setMnemonic('C');
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
			int row = userPanelTable.getSelectedRow();
			if(row != -1) {
				String login = (String) userPanelTable.getModel().getValueAt(row, 0);
				userPanelTable.getMessages(login);
				//new MessagesDialog(frame,login);
			}
		}
		if (command.equals("close")) {
			frame.dispose();
		}	
	}
	
	public void getFocus() {
		searchTextField.requestFocus();
	}

	private void startSearch() {
		int typeCursor = Cursor.WAIT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		setCursor(cursor);
		String pattern = searchTextField.getText();
		
		if (pattern.length() > 0) {
			String field = Integer.toString(fieldCombo.getSelectedIndex());
			if ((field.equals("3")) && (!hasIPFormat(pattern))) {
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
	
	private int getResultSize(Document doc) {
        List messagesList = doc.getRootElement().getChildren("row");
        return messagesList.size();
	}
	
	private void updateUserList(Document doc) {
        userPanelTable.getModel().setQuery(doc);
        int total = getResultSize(doc);
        if(total==0) {
            if (viewMsg.isEnabled()) {
            	viewMsg.setEnabled(false);
            }
            resultsLabel.setText("(No hubo resultados)");
        } else {
        	String letter = "";
        	if (total>1) {
        		letter = "s";
        	}
            resultsLabel.setText("(" + total + " usuario"+letter+")");
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
