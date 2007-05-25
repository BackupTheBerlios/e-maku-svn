package com.kazak.smi.admin.gui.table;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.jdom.Document;

import com.kazak.smi.admin.gui.table.models.CellEditor;
import com.kazak.smi.admin.gui.table.models.LateUsersHeaderListener;
import com.kazak.smi.admin.gui.table.models.LateUsersModel;
import com.kazak.smi.admin.gui.table.models.SortButtonRenderer;

public class LateUsersTable extends JTable { 

	private static final long serialVersionUID = 1L;
	private static LateUsersModel model;
	private SortButtonRenderer renderer;
	private JTableHeader header = new JTableHeader();
	private LateUsersHeaderListener listener;
	
	public LateUsersTable(Document doc) {
		model = new LateUsersModel();
		model.setQuery(doc);
		this.setModel(model);
		
		this.setGridColor(Color.BLACK);
		//this.setDefaultEditor(String.class,new CellEditor());
		this.setSurrendersFocusOnKeystroke(true);
		this.setAutoCreateColumnsFromModel(false);
				
		renderer = new SortButtonRenderer();
	    TableColumnModel columnModel = this.getColumnModel();
	    int n = model.getColumnCount(); 
	    System.out.println("n: " + n);
		int columnWidth[] = {70,200,150,150};
	    for (int i=0;i<n;i++) {
	      columnModel.getColumn(i).setHeaderRenderer(renderer);
	      columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
	    }
	    
	    header = this.getTableHeader();
	    listener = new LateUsersHeaderListener(header,model,renderer);
		header.addMouseListener(listener);
	}
			
	public LateUsersModel getModel() {
		return model;
	}
	
	public int getRowCount() {
		return model.getRowCount();
	}
}