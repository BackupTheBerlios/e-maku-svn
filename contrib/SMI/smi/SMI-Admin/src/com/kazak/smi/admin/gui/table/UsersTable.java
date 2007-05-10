package com.kazak.smi.admin.gui.table;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.gui.table.OnLineModel;
import com.kazak.smi.admin.gui.table.SortButtonRenderer;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;

// This class shows the online users list as a JTable 

public class UsersTable extends JTable implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	private OnLineModel model;
	private SortButtonRenderer renderer;
	private JTableHeader header = new JTableHeader();
		
	// Users Table Constructor
	public UsersTable() {
		model = new OnLineModel();
		this.setModel(model);
		this.setGridColor(Color.BLACK);
		this.setDefaultEditor(String.class,new CellEditor());
		this.setSurrendersFocusOnKeystroke(true);
		this.addMouseListener(this);
		this.setAutoCreateColumnsFromModel(false);
		renderer = new SortButtonRenderer();
	    TableColumnModel columnModel = this.getColumnModel();
	    int n = model.getColumnCount(); 
		int columnWidth[] = {110,240,150};
	    for (int i=0;i<n;i++) {
	      columnModel.getColumn(i).setHeaderRenderer(renderer);
	      columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
	    }
	    
	    //this.setColumnModel(columnModel);
	    header = this.getTableHeader();
				
		header.addMouseListener(new TableHeaderListener(header,model,renderer));
	}
	
	// This method gets the inbox mail titles from a user
	public static void getMessages(final String user) {
		Thread t = new Thread() {
			public void run() {
				try {
					String[] args = {user};
					System.out.println("Consultando mensajes recibidos por " + user);
					Document doc = QuerySender.getResultSetST("SEL0011",args);
					Element root = doc.getRootElement();
					Iterator it = root.getChildren("row").iterator();
					while (it.hasNext()) {
						Element el = (Element)it.next();
						Iterator itCols = el.getChildren().iterator();
						String date    = ((Element)itCols.next()).getValue();
						String hour    = ((Element)itCols.next()).getValue();
						String sender  = ((Element)itCols.next()).getValue();
						String subject = ((Element)itCols.next()).getValue();  
						System.out.println("Record: " + date + " / " + hour + " / " + sender + " / " + subject);
					}
				} catch (QuerySenderException e) {
					System.out.println("ERROR: No se pudieron consultar los mensajes del usuario: " + user);
					e.printStackTrace();
				}
			}
		};
		t.start();
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
		if (e.getClickCount() == 2) {
			String login = (String) model.getValueAt(((JTable)e.getSource()).getSelectedRow(), 0);
			getMessages(login);
		} 		
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
