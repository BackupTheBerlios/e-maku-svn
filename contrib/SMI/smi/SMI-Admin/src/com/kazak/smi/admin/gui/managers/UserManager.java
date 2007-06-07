package com.kazak.smi.admin.gui.managers;

import com.kazak.smi.admin.gui.managers.tools.user.UserDialog;
import com.kazak.smi.admin.gui.managers.tools.ToolsConstants;

public class UserManager {
	public UserDialog form;
	
	public UserManager() {
	}

	public void addUser() {
		form = new UserDialog(ToolsConstants.ADD,"");
	}
	
	public void editUser() {
		form = new UserDialog(ToolsConstants.EDIT,"");
	}

	public void editUser(String target) {
		form = new UserDialog(ToolsConstants.EDIT_PREFILLED,target);
	}

	public void deleteUser() {
		form = new UserDialog(ToolsConstants.DELETE,"");
	}

	public void deleteUser(String target) {
		form = new UserDialog(ToolsConstants.DELETE_PREFILLED,target);
	}
	
	public void searchUser() {
		form = new UserDialog(ToolsConstants.SEARCH,"");
	}
	
	public void searchUser(String target) {
		form = new UserDialog(ToolsConstants.SEARCH_PREFILLED,target);
	}
}
