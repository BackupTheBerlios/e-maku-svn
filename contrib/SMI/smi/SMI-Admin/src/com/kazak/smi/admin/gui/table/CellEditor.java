package com.kazak.smi.admin.gui.table;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

public class CellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = -3511583773152512776L;

		public CellEditor() {
			super(new JTextField());
		}
		
	    public Object getCellEditorValue() {
	    	String value = ((JTextField)getComponent()).getText();
	        return value;
	    }
}
