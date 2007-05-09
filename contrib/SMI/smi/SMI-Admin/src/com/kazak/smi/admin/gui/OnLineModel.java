package com.kazak.smi.admin.gui;

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
	private Vector<Vector> data = new Vector<Vector>();
	
	@SuppressWarnings("unchecked")
	public void addRow() {
		Vector v = new Vector();
		v.add("");
		v.add("");
		v.add("");
		data.add(v);
		fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void addRow(String code,String name,String ip) {
		Vector v = new Vector();
		v.add(code);
		v.add(name);
		v.add(ip);
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
		return false;
	}

    public synchronized void setQuery(Document doc) {

    	class LoadData extends Thread {
    		private Document doc;

    		LoadData(Document doc) {
    			this.doc=doc;
    		}

    		public void run() {
    			
		        List Lrows = doc.getRootElement().getChildren("row");
		        Iterator Irows = Lrows.iterator();
		        int max = Lrows.size();
		        if (max>0) {
		            /*
		             * Se limpia la tabla antes de desplegar la consulta nueva
		             */
		            clear();
		            /*
		             * Cargando informacion
		             */
		            
		            int i=0;		            
		            for (;Irows.hasNext();i++) {
		                Element Erow = (Element) Irows.next();
		                List Lcol = Erow.getChildren();
		                if (data.size() <= i) {
		    				Vector<String> col = new Vector<String>();
		        			for (int k=0;k<3;k++) {
		        			    col.add(((Element)Lcol.get(k)).getText());
		        			}
		        			/* Se adiciona la nueva fila al vector de filas */
		        			data.add(col);
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
		        
		        doc = null;
		        System.gc();
    		}
    	}
    	new LoadData(doc).start();
    }

}
