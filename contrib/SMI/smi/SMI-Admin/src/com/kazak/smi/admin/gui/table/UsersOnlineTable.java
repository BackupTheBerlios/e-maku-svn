package com.kazak.smi.admin.gui.table;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.jdom.Document;

import com.kazak.smi.admin.gui.table.MessagesDialog;
import com.kazak.smi.admin.gui.table.models.CellEditor;
import com.kazak.smi.admin.gui.table.models.OnLineModel;
import com.kazak.smi.admin.gui.table.models.SortButtonRenderer;
import com.kazak.smi.admin.gui.table.models.TableHeaderListener;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;

// This class shows the online users list as a JTable 

public class UsersOnlineTable extends JTable implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	private OnLineModel model;
	private SortButtonRenderer renderer;
	private JTableHeader header = new JTableHeader();
	private TableHeaderListener listener;
	private static JFrame frame;
	private JButton listButton;
	
	// Users Table Constructor
	public UsersOnlineTable(JFrame frame) {
		UsersOnlineTable.frame = frame;
		model = new OnLineModel();
		this.setModel(model);
		this.setGridColor(Color.BLACK);
		this.setDefaultEditor(String.class,new CellEditor());
		this.setSurrendersFocusOnKeystroke(true);
		this.addMouseListener(this);
		this.setAutoCreateColumnsFromModel(false);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		renderer = new SortButtonRenderer();
	    TableColumnModel columnModel = this.getColumnModel();
	    int n = model.getColumnCount(); 
		int columnWidth[] = {110,240,240,150,190};
	    for (int i=0;i<n;i++) {
	      columnModel.getColumn(i).setHeaderRenderer(renderer);
	      columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
	    }
	    
	    header = this.getTableHeader();
	    listener = new TableHeaderListener(header,model,renderer);
		header.addMouseListener(listener);
	}
		
	// This method returns the JTable's model
	public OnLineModel getModel() {
		return model;
	}
	
	// This method returns the JTable's renderer
	public SortButtonRenderer getRenderer() {
		return renderer;
	}

	// This method catches a mouse event: "click"
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			listButton.setEnabled(true);
		}
		if (e.getClickCount() == 2) {
			String login = (String) model.getValueAt(((JTable)e.getSource()).getSelectedRow(), 0);
			getMessages(login);
		} 		
	}
	
	// This method gets the inbox mail titles from a user
	public void getMessages(final String login) {
		Thread t = new Thread() {
			public void run() {
				try {
					String[] args = {login};
					Document doc = QuerySender.getResultSetFromST("SEL0011",args);
					if (!queryIsEmpty(doc)) {
						// If there are messages, show them
						new MessagesDialog(frame,login,doc);
					} else {
						// Show no messages dialog
						JOptionPane.showMessageDialog(
								frame,
								"<html><center>" +
								"El usuario \"" + login 
								+ "\" no tiene mensajes en su buzón. " +
								"</center></html>");
					}
				} catch (QuerySenderException e) {
					System.out.println("ERROR: No se pudieron consultar los mensajes del usuario: " + login);
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	private static boolean queryIsEmpty(Document doc) {
        List messagesList = doc.getRootElement().getChildren("row");
        if (messagesList.size() == 0) {
        	return true;
        }
        return false;
	}
	
	public void setListButton(JButton listButton) {
		this.listButton = listButton;
	}
	
	public void initHeader() {
		listener.initHeader();
	}
	
	public int getResultSize() {
		return model.getResultSize();
	}
	
	public void setModelTab(int tab) {
		model.setTab(tab);
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
