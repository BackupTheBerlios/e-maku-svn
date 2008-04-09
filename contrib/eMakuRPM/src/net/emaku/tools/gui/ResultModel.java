package net.emaku.tools.gui;

import java.util.Collections;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class ResultModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private String[] titles = {"Code","Description"};
	private Class[] types = {String.class,String.class};
	private Vector<Vector<String>> tableData = new Vector<Vector<String>>();
	//private Vector<String> recordsVector = new Vector<String>();
		
	@SuppressWarnings("unchecked")
	public void addRow() {
		Vector<String> vector = new Vector<String>();
		vector.add("");
		vector.add("");
		tableData.add(vector);
		fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void addRow(String code, String description) {
		Vector vector = new Vector();
		vector.add(code);
		vector.add(description);
		tableData.add(vector);
		fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void clear() {
		tableData.clear();
		fireTableDataChanged();
   	}
		
	public Vector<Vector<String>> getData() {
		return tableData;
	}
	
	public void remove(int index) {
		tableData.remove(index);
		fireTableDataChanged();
	}
	
	public String getColumnName(int index) {
		return titles[index];
	}
	
	public int getColumnCount() {
		return titles.length;
	}
	
	public int getRowCount() {
		return tableData.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return tableData.get(rowIndex).get(columnIndex);
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		return types[columnIndex];
	}
	
	@SuppressWarnings("unchecked")
	public void setValueAt(Object element,int rowIndex, int columnIndex) {
		tableData.get(rowIndex).set(columnIndex,element.toString());
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public void updateTable(int index, boolean asc) {
        Collections.sort(tableData, new ColumnSorter(index,asc)); // TODO: Eliminar este warning
        fireTableStructureChanged();
	}
		
	// This method fills the table of users online
    public synchronized void setQuery(Vector<Vector<String>> data) {

    	class LoadData extends Thread {
    		private Vector<Vector<String>> data;

    		LoadData(Vector<Vector<String>> data) {
    			this.data=data;
    		}

    		public void run() {
    			// Cleaning the table
    			clear();
    			// Loading new info
    			tableData = data;
    			fireTableDataChanged();
    			updateTable(0, true);
    			System.gc();
    		}
      	}
    	new LoadData(data).start();
    }
}
