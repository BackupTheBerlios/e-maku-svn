package com.kazak.smi.admin.gui.table.models;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.jdom.Document;
import org.jdom.Element;


public class ControlModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private String[] titles = {"Fecha","Hora","Asunto","Remitente","Nombre"};
	private Class[] types = {String.class,String.class,String.class,String.class,String.class};
	private Vector<Vector> tableData = new Vector<Vector>();
	private Vector<String> messagesVector = new Vector<String>();
		
	@SuppressWarnings("unchecked")
	public void addRow() {
		Vector vector = new Vector();
		vector.add("");
		vector.add("");
		vector.add("");
		vector.add("");
		vector.add("");
		tableData.add(vector);
		fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void addRow(String date, String hour, String subject, String sender, String name) {
		Vector vector = new Vector();
		vector.add(date);
		vector.add(hour);
		vector.add(subject);
		vector.add(sender);
		vector.add(name);
		tableData.add(vector);
		fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void clear() {
		tableData.clear();
		fireTableDataChanged();
   	}
		
	public Vector<Vector> getData() {
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
		tableData.get(rowIndex).set(columnIndex,element);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public void updateTable(int index, boolean asc) {
        Collections.sort(tableData, new ColumnSorter(index,asc)); // TODO: Eliminar este warning
        fireTableStructureChanged();
	}
	
	public String getMessage(int index) {
		return (String) messagesVector.get(index);
	}
		
	// This method fills the table of users online
    public synchronized void setQuery(Document doc) {

    	class LoadData extends Thread {
    		private Document doc;

    		LoadData(Document doc) {
    			this.doc=doc;
    		}

    		public void run() {
    			List messagesList = doc.getRootElement().getChildren("row");
    			Iterator messageIterator = messagesList.iterator();
    			// Cleaning the table
    			clear();
    			// Loading new info 
    			for (;messageIterator.hasNext();) {
    				Element oneMessage = (Element) messageIterator.next();
    				List messagesDetails = oneMessage.getChildren();
    				Vector<Object> tableRow = new Vector<Object>();
    				for (int k=0;k<6;k++) {
    					String data = ((Element)messagesDetails.get(k)).getText();
    					if (k==5) {
    						messagesVector.add(data);
    					}
    					else { 
    						tableRow.add(data);
    					}	
    				}
    				// Adding a new row into the table 
    				tableData.add(tableRow);
    			}
    			fireTableDataChanged();
    			updateTable(0, true);
    			doc = null;
    			System.gc();
    		}
    	}
    	new LoadData(doc).start();
    }
}