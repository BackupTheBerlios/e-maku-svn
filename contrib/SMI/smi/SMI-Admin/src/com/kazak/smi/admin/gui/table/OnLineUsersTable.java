package com.kazak.smi.admin.gui.table;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.kazak.smi.admin.gui.table.OnLineModel;
import com.kazak.smi.admin.gui.table.SortButtonRenderer;
import com.kazak.smi.admin.gui.table.MessagesDialog;

// This class shows the online users list as a JTable 

public class OnLineUsersTable extends JTable implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	private OnLineModel model;
	private SortButtonRenderer renderer;
	private JTableHeader header = new JTableHeader();
	private TableHeaderListener listener;
	private JFrame frame;
	private JButton listButton;
	
	// Users Table Constructor
	public OnLineUsersTable(JFrame frame) {
		this.frame = frame;
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
		int columnWidth[] = {110,240,120,160};
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
			new MessagesDialog(frame,login);
		} 		
	}
	
	public void setListButton(JButton listButton) {
		this.listButton = listButton;
	}
	
	public void initHeader() {
		listener.initHeader();
	}
	
	public int getGroupSize() {
		return model.getGroupSize();
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
