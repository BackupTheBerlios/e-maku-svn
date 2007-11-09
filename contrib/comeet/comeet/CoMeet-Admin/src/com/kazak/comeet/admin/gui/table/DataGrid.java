package com.kazak.comeet.admin.gui.table;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class DataGrid extends JTable {

	private static final long serialVersionUID = 1L;
	
	public JScrollPane getScrollPane() {
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane jscroll = new JScrollPane(this);
		return jscroll;
	}
}