package net.emaku.tools.gui;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class FormsResultTable extends JTable { 

	private static final long serialVersionUID = 1L;
	private static ResultModel model;
	
	public FormsResultTable(Vector<Vector<String>> data) {
		model = new ResultModel();
		this.setModel(model);
		this.getModel().setQuery(data);
		this.setGridColor(Color.BLACK);
		this.setSurrendersFocusOnKeystroke(true);
		this.setAutoCreateColumnsFromModel(false);
		this.getColumnModel().getColumn(0).setMinWidth(80);
		this.getColumnModel().getColumn(0).setMaxWidth(110);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public ResultModel getModel() {
		return model;
	}
	
	public int getRowCount() {
		return model.getRowCount();
	}
}
