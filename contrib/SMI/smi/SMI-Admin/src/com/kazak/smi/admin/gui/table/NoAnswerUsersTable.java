package com.kazak.smi.admin.gui.table;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.jdom.Document;

import com.kazak.smi.admin.gui.table.models.NoAnswerUsersHeaderListener;
import com.kazak.smi.admin.gui.table.models.NoAnswerUsersModel;
import com.kazak.smi.admin.gui.table.models.SortButtonRenderer;

public class NoAnswerUsersTable extends JTable { 

	private static final long serialVersionUID = 1L;
	private static NoAnswerUsersModel model;
	private SortButtonRenderer renderer;
	private JTableHeader header = new JTableHeader();
	private NoAnswerUsersHeaderListener listener;
	
	public NoAnswerUsersTable(Document doc) {
		model = new NoAnswerUsersModel();
		model.setQuery(doc);
		this.setModel(model);
		
		this.setGridColor(Color.BLACK);
		this.setSurrendersFocusOnKeystroke(true);
		this.setAutoCreateColumnsFromModel(false);
				
		renderer = new SortButtonRenderer();
	    TableColumnModel columnModel = this.getColumnModel();
	    int n = model.getColumnCount(); 
		int columnWidth[] = {70,200,150};
	    for (int i=0;i<n;i++) {
	      columnModel.getColumn(i).setHeaderRenderer(renderer);
	      columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
	    }
	    
	    header = this.getTableHeader();
	    listener = new NoAnswerUsersHeaderListener(header,model,renderer);
		header.addMouseListener(listener);
	}
			
	public NoAnswerUsersModel getModel() {
		return model;
	}
	
	public int getRowCount() {
		return model.getRowCount();
	}
}