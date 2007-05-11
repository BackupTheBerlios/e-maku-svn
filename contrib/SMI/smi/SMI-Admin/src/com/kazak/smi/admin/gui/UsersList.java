package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.PopupMenuEvent;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;
import com.kazak.smi.admin.gui.table.GroupsSearchPanel;
import com.kazak.smi.admin.gui.table.UserSearchPanel;

/*
 *  This class searchs for users online and shows them as a list
 */

public class UsersList extends JFrame {  
	
	private static final long serialVersionUID = 3920757441925057976L;
	private GroupsSearchPanel groupPanel;
	private UserSearchPanel userPanel;
	
	public UsersList() {
		this.setLayout(new BorderLayout());
		this.setSize(600,400);
		initInterface();
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
	}
	
	public void initInterface() {		
		userPanel = new UserSearchPanel(this);
		groupPanel = new GroupsSearchPanel(this);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Búsqueda por Código", userPanel);		
		tabbedPane.add("Listado por Grupos", groupPanel);
		add(tabbedPane,BorderLayout.CENTER);
		
		updateGroupTable();
	}

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
                                      groupPanel.updateUserList(doc);
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
	public void requestOnlineUsers() {
		// Enviando comando al servidor para ser aprobado
		Cache.getGroup("key");
		Element onlist = new Element("ONLINELIST");
		Element id = new Element("id").setText("LIST");
		onlist.addContent(id);
		Document document = new Document(onlist);
        Element args = new Element("args");
        onlist.addContent(groupPanel.getGroupID());
        args.setText(groupPanel.getGroupsSelection());

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
		
    public static String getFormattedDate() {
    	SimpleDateFormat now = new SimpleDateFormat("E, dd MMM yyyy - HH:mm");
    	Date date = new Date();
    	return now.format(date);
    }
    
    public void setWindowLabel(String total) {
    	String date = getFormattedDate();
    	this.setTitle("Usuarios en Linea: " + total + " / " + date);
    }

	public void popupMenuCanceled(PopupMenuEvent e) {
		
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		updateGroupTable();
	}
	
	private void setWindowTitle() {
		loadTotal();
		requestUsersTotal();
	}
	
	public void updateGroupTable() {
		loadUserList();
		requestOnlineUsers();
		setWindowTitle();
		groupPanel.getTable().initHeader();
	}
}