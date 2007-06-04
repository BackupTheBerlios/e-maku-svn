package com.kazak.smi.admin.gui.managers.tools.user;

import javax.swing.JPanel;
import com.kazak.smi.admin.gui.managers.tools.MainForm;
import com.kazak.smi.admin.gui.managers.tools.Operation;
import com.kazak.smi.admin.gui.managers.tools.ToolsConstants;
import com.kazak.smi.admin.gui.managers.tools.Transactions.UserDocument;
import com.kazak.smi.admin.gui.managers.tools.user.UserData;
import com.kazak.smi.admin.gui.managers.tools.ButtonBar;

public class UserPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private MainForm mainFrame;
	private ButtonBar buttonBar;
	private int action;
	private String target;
	
	public UserPanel(MainForm mainFrame, ButtonBar buttonBar,int action, String target) {
		this.mainFrame = mainFrame;
		this.buttonBar = buttonBar;
		this.action = action;
		this.target = target;
	}
	
	public String[] getFormData(){
		String[] data = new String[5];
		data[0] = target;
		data[1] = "";
		data[2] = "";
		data[3] = "";
		data[4] = "";
		
		return data;
	}
	
	public void executeOperation() {
		UserData user = new UserData();
		if (!user.verifyData()) {
			return;
		}

		UserDocument doc = new UserDocument(getFormData());
		switch(action) {
			// To Add
		case ToolsConstants.ADD:
			Operation.execute(doc.getDocumentToAdd());
			mainFrame.dispose();
			break;
			// To Edit
		case ToolsConstants.EDIT:
			// To Edit (filled)
		case ToolsConstants.EDIT_PREFILLED:
			Operation.execute(doc.getDocumentToEdit());
			mainFrame.dispose();
			break;
			// To Delete
		case ToolsConstants.DELETE:
			Operation.execute(doc.getDocumentToDelete());
			mainFrame.dispose();
			break;
			// To Delete (filled)
		case ToolsConstants.DELETE_PREFILLED:
			Operation.execute(doc.getDocumentToDelete());
			mainFrame.dispose();
			break;
		}	   		
	}
	
	public void clean() {
		switch(action) {
			// To Add
		case ToolsConstants.ADD:
			break;
			// To Edit
		case ToolsConstants.EDIT:
			break;
			// To Delete
		case ToolsConstants.DELETE:
			break;
			// To Search
		case ToolsConstants.SEARCH:
			break;
		}	
	}

}
