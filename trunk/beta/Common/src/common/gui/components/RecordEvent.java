package common.gui.components;

import java.util.*;

import org.jdom.*;

public class RecordEvent extends EventObject {

	private static final long serialVersionUID = -9028662223570110161L;
	private Element element;
	private Hashtable<String,Integer> rowsLoaded;
	private boolean recalculable = true;
	
	public RecordEvent(Object source,Element e) {
		super(source);
		this.element = e;
	}
	
	public Element getElement() {
		return element;
	}
	
	public boolean isRowsLoaded() {
		if (rowsLoaded!=null) {
			return true;
		}
		return false;
	}
	
	public Hashtable<String,Integer> getRowsLoaded() {
		return rowsLoaded;
	}
	
	public void setRowsLoaded(Hashtable<String,Integer> hash) {
		this.rowsLoaded = hash;
	}

	public boolean isRecalculable() {
		return recalculable;
	}

	public void setRecalculable(boolean recalculable) {
		this.recalculable = recalculable;
	}
}
