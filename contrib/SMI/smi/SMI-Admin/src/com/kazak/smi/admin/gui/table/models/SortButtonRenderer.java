package com.kazak.smi.admin.gui.table.models;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class SortButtonRenderer extends JButton implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	public static final int NONE = 0;
	public static final int DOWN = 1;
	public static final int UP   = 2;

	int pushedColumn;
	Hashtable <Integer,Integer>state ;
	JButton downButton,upButton;

	public SortButtonRenderer() {
		pushedColumn   = -1;
		state = new Hashtable<Integer,Integer>();

		setMargin(new Insets(0,0,0,0));
		setHorizontalTextPosition(LEFT);
		setIcon(new BlankIcon());

		downButton = new JButton();
		downButton.setMargin(new Insets(0,0,0,0));
		downButton.setHorizontalTextPosition(LEFT);
		downButton.setIcon(new ArrowIcon(ArrowIcon.DOWN, false, false));
		downButton.setPressedIcon(new ArrowIcon(ArrowIcon.DOWN, false, true));

		upButton = new JButton();
		upButton.setMargin(new Insets(0,0,0,0));
		upButton.setHorizontalTextPosition(LEFT);
		upButton.setIcon(new ArrowIcon(ArrowIcon.UP, false, false));
		upButton.setPressedIcon(new ArrowIcon(ArrowIcon.UP, false, true));

	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JButton button = this;
		Object object = state.get(new Integer(column));
		if (object != null) {
			if (((Integer)object).intValue() == DOWN) {
				button = downButton;
			} else {
				button = upButton;
			}
		}
		button.setText((value == null) ? "" : value.toString());
		boolean isPressed = (column == pushedColumn);
		button.getModel().setPressed(isPressed);
		button.getModel().setArmed(isPressed);
		
		return button;
	}

	public void setPressedColumn(int column) {
		pushedColumn = column;
	}

	public void setSelectedColumn(int column) {
		if (column < 0) 
			return;
		Integer value = null;
		Object object = state.get(new Integer(column));
		if (object == null) {
			value = new Integer(UP);
		} else {
			if (((Integer)object).intValue() == DOWN) {
				value = new Integer(UP);
			} else {
				value = new Integer(DOWN);
			}
		}
		state.clear();
		state.put(new Integer(column), value);
	} 
	
	public void setColumnForUpdate() {
		state.clear();
		state.put(new Integer(0), new Integer(UP));
	} 	

	public int getState(int column) {
		int retValue;
		Object obj = state.get(new Integer(column));
		if (obj == null) {
			retValue = NONE;
		} else {
			if (((Integer)obj).intValue() == DOWN) {
				retValue = DOWN;
			} else {
				retValue = UP;
			}
		}
		return retValue;
	} 
}
