package com.kazak.smi.admin.gui.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.jdom.Document;
import org.jdom.Element;

public class OnLineModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] titles = { "Codigo Usuario","Nombre","Dirección IP"};
	private Class[] types = {String.class,String.class,String.class};
	private Vector<Vector> tableData = new Vector<Vector>();
	
	@SuppressWarnings("unchecked")
	public void addRow() {
		Vector v = new Vector();
		v.add("");
		v.add("");
		v.add("");
		tableData.add(v);
		fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void addRow(String code,String name,String ip) {
		Vector v = new Vector();
		v.add(code);
		v.add(name);
		v.add(ip);
		tableData.add(v);
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
        Collections.sort(tableData, new ColumnSorter(index,asc));
        fireTableStructureChanged();		
	}

	// This method fills the table of users online
    public synchronized void setQuery(Document doc) {

    	class LoadData extends Thread {
    		private Document doc;

    		LoadData(Document doc) {
    			this.doc=doc;
    		}

    		public void run() {
		        List usersList = doc.getRootElement().getChildren("row");
		        Iterator userData = usersList.iterator();
		        int max = usersList.size();
		        if (max>0) {
		             // Cleaning the table
		            clear();
		             // Loading new info 
		            int i=0;		            
		            for (;userData.hasNext();i++) {
		                Element oneUser = (Element) userData.next();
		                List userDetails = oneUser.getChildren();
		                if (tableData.size() <= i) {
		    				Vector<String> tableRow = new Vector<String>();
		        			for (int k=0;k<3;k++) {
		        			     tableRow.add(((Element)userDetails.get(k)).getText());
		        			}
		        			// Adding a new row into the table 
		        			tableData.add(tableRow);
		        			fireTableDataChanged();
		    			}
		        		for (int j=0;j<3;j++) {
		        			fireTableCellUpdated(i, j);
		        		}

		            }
		        }
		        else {
		        	clear();
		        }
		        
		        updateTable(0, true);
		        doc = null;
		        System.gc();
    		}
    	}
    	new LoadData(doc).start();
    }

}

class ColumnSorter implements Comparator {
    int colIndex;
    boolean ascending;
    
    ColumnSorter(int colIndex, boolean ascending) {
        this.colIndex = colIndex;
        this.ascending = ascending;
    }
    public int compare(Object a, Object b) {
        Vector v1 = (Vector)a;
        Vector v2 = (Vector)b;
        Object o1 = v1.get(colIndex);
        Object o2 = v2.get(colIndex);

        // Treat empty strains like nulls
        if (o1 instanceof String && ((String)o1).length() == 0) {
            o1 = null;
        }
        if (o2 instanceof String && ((String)o2).length() == 0) {
            o2 = null;
        }

        // Sort nulls so they appear last, regardless
        // of sort order
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            return 1;
        } else if (o2 == null) {
            return -1;
        } else if (o1 instanceof Comparable) {
            if (ascending) {
                return ((Comparable)o1).compareTo(o2);
            } else {
                return ((Comparable)o2).compareTo(o1);
            }
        } else {
            if (ascending) {
                return o1.toString().compareTo(o2.toString());
            } else {
                return o2.toString().compareTo(o1.toString());
            }
        }
    }
}

