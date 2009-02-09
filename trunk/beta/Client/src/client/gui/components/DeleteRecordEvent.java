package client.gui.components;

import java.util.EventObject;

public class DeleteRecordEvent extends EventObject {

	private int row;
	
	public DeleteRecordEvent(Object arg0,int row) {
		super(arg0);
		this.row=row;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6451537168198710914L;

	public int getRow() {
		return row;
	}

}
