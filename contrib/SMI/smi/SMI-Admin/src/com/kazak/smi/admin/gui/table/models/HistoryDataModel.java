package com.kazak.smi.admin.gui.table.models;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class HistoryDataModel extends AbstractTableModel {

	private static final long serialVersionUID = -6111771046854883088L;
	
	private String[] titles = {
			"No","Fecha","Hora","Remitente","Destinatario","Asunto","Mensaje","Leído"
	};
	
	private Class[] types = {
			Integer.class,String.class,String.class,String.class,
			String.class,String.class,String.class,Boolean.class};
	
	private int[] width = {40,80,60,80,80,194,0,40};
	private Vector<Vector<Object>> dataVector;
	
	public HistoryDataModel(Vector<Vector<Object>> data) {
		this.dataVector = data;
	}
	
	public String getColumnName(int index) {
		return titles[index];
	}
	
	public int getColumnCount() {
		return titles.length;
	}

	public int getRowCount() {
		return dataVector.size();
	}

	public Class<?> getColumnClass(int columnIndex) {
		return types[columnIndex];
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = dataVector.get(rowIndex).get(columnIndex);
		if (columnIndex==7) {
			return Boolean.parseBoolean(value.toString());
		}
		return value;
	}
	
	public int getWidth(int i) {
		return width[i];
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}