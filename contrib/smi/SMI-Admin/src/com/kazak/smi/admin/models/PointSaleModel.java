package com.kazak.smi.admin.models;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.kazak.smi.admin.control.Cache.WorkStation;

public class PointSaleModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] titles = 
	{"CODIGO","NOMBRE DEL PUNTO","IP"};
	private Class[] types   =  
	{String.class,String.class,String.class};
	private Vector<WorkStation> values;
	
	public PointSaleModel(Vector<WorkStation> vws) {
		values = vws;
	}
	
	public String getColumnName(int index) {
		return titles[index];
	}
	
	public int getColumnCount() {
		return titles.length;
	}

	public int getRowCount() {
		return values.size();
	}

	public Class<?> getColumnClass(int columnIndex) {
		return types[columnIndex];
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		WorkStation ws = values.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return ws.getCode();
		case 1:
			return ws.getName();
		case 2:
			return ws.getIp();
		}
		return null;
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}