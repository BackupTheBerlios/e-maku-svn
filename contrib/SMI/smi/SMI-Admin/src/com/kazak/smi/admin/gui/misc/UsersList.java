package com.kazak.smi.admin.gui.misc;

import java.awt.BorderLayout;
import java.awt.Cursor;
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
import com.kazak.smi.admin.gui.main.MainWindow;

/*
 *  This class searchs for users online and shows them as a list
 */

public class UsersList extends JFrame {  
	
	private static final long serialVersionUID = 3920757441925057976L;
	private GroupsSearchPanel groupPanel;
	private UserSearchPanel userPanel;
	
	public UsersList() {
		this.setLayout(new BorderLayout());
		this.setSize(650,400);
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

	// Gets the total of users online
	private void loadTotal() {
		class Monitor extends Thread {
			Document doc= null;
			public void run() {
				try {
					doc = QuerySender.getResultSetFromST("TOTAL");
					XMLOutputter xmlOutputter = new XMLOutputter();
		            xmlOutputter.setFormat(Format.getPrettyFormat());
		    		Element element = doc.getRootElement();
		    		List list = element.getChildren("row");
		    		Element columns = (Element)list.get(0);
		    		String usersTotal = columns.getValue();
		    		setWindowLabel(usersTotal);
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}
		}
		new Monitor().start();		
	}
	
    // Catch the list of users requested  
	private void loadUserList() {
                class Monitor  extends Thread {			
                        Document doc= null;
                        public void run() {
                                try {
                                      doc = QuerySender.getResultSetFromST("RESULT");
                                      groupPanel.updateUserList(doc);
                                } catch (QuerySenderException e) {
                                      e.printStackTrace();
                                }
                        }
                }
                new Monitor().start();		
     }
	
	// This method requests the total of users online 
	public void requestUsersTotal() {
		Element onlist = new Element("ONLINELIST");
		Element id = new Element("id").setText("TOTAL");
		onlist.addContent(id);
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
	
	// This method requests the list of users online
	public void requestOnlineUsers() {
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
				SocketWriter.write(SocketHandler.getSock(),document);
			} catch (IOException ex) {
				System.out.println("Error de entrada y salida");
				System.out.println("Causa: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		
	}
		
    public static String getFormattedDate() {
    	SimpleDateFormat now = new SimpleDateFormat("E, dd MMM yyyy - HH:mm a");
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
	
	public void setWindowTitle() {
		loadTotal();
		requestUsersTotal();
	}
	
	public void updateGroupTable() {
		int typeCursor = Cursor.WAIT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		setCursor(cursor);
		loadUserList();
		requestOnlineUsers();
		setWindowTitle();
		groupPanel.getTable().initHeader();
		groupPanel.setEnableListButton();
		typeCursor = Cursor.DEFAULT_CURSOR;
		cursor = Cursor.getPredefinedCursor(typeCursor);
		setCursor(cursor);
	}
}