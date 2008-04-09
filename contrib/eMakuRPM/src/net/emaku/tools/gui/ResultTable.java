package net.emaku.tools.gui;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

public class ResultTable extends JTable { 

	private static final long serialVersionUID = 1L;
	private static ResultModel model;
	
	public ResultTable(Vector<Vector<String>> data) {
		model = new ResultModel();
		this.setModel(model);
		this.getModel().setQuery(data);
		this.setGridColor(Color.BLACK);
		this.setSurrendersFocusOnKeystroke(true);
		this.setAutoCreateColumnsFromModel(false);
		
	    TableColumnModel columnModel = this.getColumnModel();
	    int n = model.getColumnCount(); 
		int columnWidth[] = {80,200};
	    for (int i=0;i<n;i++) {
	      columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
	    }
	}

	public ResultModel getModel() {
		return model;
	}
	
	public int getRowCount() {
		return model.getRowCount();
	}
}
