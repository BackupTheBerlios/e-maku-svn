package com.kazak.comeet.admin.gui.table;

import javax.swing.JScrollPane;
import javax.swing.JTable;

public class DataGrid extends JTable {

	private static final long serialVersionUID = 1L;
	
	public JScrollPane getScrollPane() {
		JScrollPane jscroll = new JScrollPane(this);
		return jscroll;
	}
}