package com.kazak.smi.admin.gui.table;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.jdom.Document;

import com.kazak.smi.admin.gui.table.models.ApprovedUsersHeaderListener;
import com.kazak.smi.admin.gui.table.models.ApprovedUsersModel;
import com.kazak.smi.admin.gui.table.models.SortButtonRenderer;

public class ApprovedUsersTable extends JTable { 

	private static final long serialVersionUID = 1L;
	private static ApprovedUsersModel model;
	private SortButtonRenderer renderer;
	private JTableHeader header = new JTableHeader();
	private ApprovedUsersHeaderListener listener;
	
	public ApprovedUsersTable(Document doc) {
		model = new ApprovedUsersModel();
		model.setQuery(doc);
		this.setModel(model);
		
		this.setGridColor(Color.BLACK);
		this.setSurrendersFocusOnKeystroke(true);
		this.setAutoCreateColumnsFromModel(false);
				
		renderer = new SortButtonRenderer();
	    TableColumnModel columnModel = this.getColumnModel();
	    int n = model.getColumnCount(); 
	    System.out.println("n aut: " + n);
		int columnWidth[] = {70,200,150,150};
	    for (int i=0;i<n;i++) {
	      columnModel.getColumn(i).setHeaderRenderer(renderer);
	      columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
	    }
	    
	    header = this.getTableHeader();
	    listener = new ApprovedUsersHeaderListener(header,model,renderer);
		header.addMouseListener(listener);
	}
			
	public ApprovedUsersModel getModel() {
		return model;
	}
	
	public int getRowCount() {
		return model.getRowCount();
	}

}