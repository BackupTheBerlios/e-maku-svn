package com.kazak.comeet.admin.gui.table;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.jdom.Document;

import com.kazak.comeet.admin.gui.table.models.OfflineUsersHeaderListener;
import com.kazak.comeet.admin.gui.table.models.OfflineUsersModel;
import com.kazak.comeet.admin.gui.table.models.SortButtonRenderer;

public class OfflineUsersTable extends JTable { 

	private static final long serialVersionUID = 1L;
	private static OfflineUsersModel model;
	private SortButtonRenderer renderer;
	private JTableHeader header = new JTableHeader();
	private OfflineUsersHeaderListener listener;
	
	public OfflineUsersTable(Document doc) {
		model = new OfflineUsersModel();
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
	    listener = new OfflineUsersHeaderListener(header,model,renderer);
		header.addMouseListener(listener);
	}
			
	public OfflineUsersModel getModel() {
		return model;
	}
	
	public int getRowCount() {
		return model.getRowCount();
	}

}