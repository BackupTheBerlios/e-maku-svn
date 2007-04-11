package com.kazak.smi.admin.gui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class HistoryDataModel extends AbstractTableModel {

	private static final long serialVersionUID = -6111771046854883088L;
	
	private String[] titles = {
			"No","Fecha","Hora","Remitente","Destinatario","Asunto","Mensaje","Leido"
	};
	
	private Class[] types = {
			Integer.class,String.class,String.class,String.class,
			String.class,String.class,String.class,Boolean.class};
	
	private int[] width = {30,80,60,100,100,170,0,40};
	private Vector<Vector<Object>> data;
	
	public HistoryDataModel(Vector<Vector<Object>> data) {
		this.data = data;
	}
	
	public String getColumnName(int index) {
		return titles[index];
	}
	
	public int getColumnCount() {
		return titles.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Class<?> getColumnClass(int columnIndex) {
		return types[columnIndex];
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object val = data.get(rowIndex).get(columnIndex);
		if (columnIndex==7) {
			return Boolean.parseBoolean(val.toString());
		}
		return val;
	}
	
	public int getWidth(int i) {
		return width[i];
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}