package com.kazak.smi.admin.gui.table.models;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.kazak.smi.admin.control.Cache.User;

public class UsersModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] titles = {"UID","LOGIN","NOMBRES","CORREO","ADMINISTRADOR"};
	private Class[] types   = {String.class,String.class,String.class,String.class,Boolean.class};
	private Vector<User> usersVector;
	
	public UsersModel(Vector<User> vector) {
		usersVector = vector;
	}
	
	public String getColumnName(int index) {
		return titles[index];
	}
	
	public int getColumnCount() {
		return titles.length;
	}

	public int getRowCount() {
		return usersVector.size();
	}

	public Class<?> getColumnClass(int columnIndex) {
		return types[columnIndex];
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		User user = usersVector.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return user.getId();
		case 1:
			return user.getLogin();
		case 2:
			return user.getName();
		case 3:
			return user.getEmail();
		case 4:
			return user.getAdmin();
		}
		
		return null;
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}