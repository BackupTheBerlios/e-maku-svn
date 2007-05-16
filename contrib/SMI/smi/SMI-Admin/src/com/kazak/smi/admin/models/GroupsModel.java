package com.kazak.smi.admin.models;

import java.util.Collection;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.Cache.Group;

public class GroupsModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private String[] titles = {"GID","NOMBRE DE GRUPO","MOSTRABLE","ZONA"};
	private Class[] types   = {Integer.class,String.class,Boolean.class,Boolean.class};
	private Vector<Group> values;
	
	public GroupsModel() {
		Collection<Group> data = Cache.getList();
		values = new Vector<Group>(data);
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
		Group g = values.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return Integer.valueOf(g.getId());
		case 1:
			return g.getName();
		case 2:
			return g.isVisible();
		case 3:
			return  g.isZone();
		}
		return null;
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}	
}