package com.kazak.smi.admin.gui.table.models;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class UserModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private String[] titles = { "Código Punto","Nombre","Validar IP"};
	private Class[] types = {String.class,String.class,Boolean.class};
	private Vector<Vector> data = new Vector<Vector>();
	
	@SuppressWarnings("unchecked")
	public void addRow() {
		Vector v = new Vector();
		v.add("");
		v.add("");
		v.add(false);
		data.add(v);
		fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void addRow(String code,String name,Boolean b) {
		Vector v = new Vector();
		v.add(code);
		v.add(name);
		v.add(b);
		data.add(v);
		fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void clear() {
		data.clear();
		fireTableDataChanged();
	}
	
	public Vector<Vector> getData() {
		return data;
	}
	public void remove(int index) {
		data.remove(index);
		fireTableDataChanged();
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
	
	public boolean isAlreadyIn(String code) {
		for(int i=0;i<data.size();i++){
			Vector<Object> records = (Vector<Object>)data.get(i);
			String posCode = (String) records.get(0);
			System.out.println(code + " : " + posCode);
			if(posCode.equals(code)) {
				return true;
			}
		}
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex).get(columnIndex);
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		return types[columnIndex];
	}
	
	@SuppressWarnings("unchecked")
	public void setValueAt(Object element,int rowIndex, int columnIndex) {
		data.get(rowIndex).set(columnIndex,element);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex==2)
			return true;
		return false;
	}
}
