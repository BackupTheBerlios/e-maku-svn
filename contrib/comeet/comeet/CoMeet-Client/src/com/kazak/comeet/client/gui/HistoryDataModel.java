package com.kazak.comeet.client.gui;

import javax.swing.table.AbstractTableModel;

import com.kazak.comeet.client.control.Cache;

public class HistoryDataModel extends AbstractTableModel {

	private static final long serialVersionUID = -6111771046854883088L;
	
	private String[] titles = {
			"No","Fecha","Hora","Remitente","Asunto","Mensaje","Leído"
	};
	private Class[] types = {
			Integer.class,String.class,String.class,
			String.class,String.class,String.class,Boolean.class};
	
	private int[] width = {30,100,80,100,230,0,30};
	
	public String getColumnName(int index) {
		return titles[index];
	}
	
	public int getColumnCount() {
		return titles.length;
	}

	public int getRowCount() {
		return Cache.getMessages().size();
	}

	public Class<?> getColumnClass(int columnIndex) {
		return types[columnIndex];
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Cache.getMessages().get(rowIndex).getAt(columnIndex);
	}
	
	public void setValueAt(Object element,int rowIndex, int columnIndex) {
		Cache.getMessages().get(rowIndex).setAt(columnIndex, element);
	}
	
	public int getWidth(int i) {
		return width[i];
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}