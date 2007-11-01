package com.kazak.comeet.admin.gui.managers;

import com.kazak.comeet.admin.gui.managers.tools.user.UserDialog;
import com.kazak.comeet.admin.gui.managers.tools.ToolsConstants;

public class UserManager {
	UserDialog form;

	public UserManager() {
	}

	public void addUser() {
		form = new UserDialog(ToolsConstants.ADD,"",false);
	}
	public void editUser() {
		form = new UserDialog(ToolsConstants.EDIT,"",false);
	}
	public void editUser(String target) {
		form = new UserDialog(ToolsConstants.EDIT_PREFILLED,target,false);
	}
	public void deleteUser() {
		form = new UserDialog(ToolsConstants.DELETE,"",false);
	}
	public void deleteUser(String target) {
		form = new UserDialog(ToolsConstants.DELETE_PREFILLED,target,false);
	}
	public void searchUser() {
		form = new UserDialog(ToolsConstants.SEARCH,"",false);
	}
	public void searchUser(String target) {
		form = new UserDialog(ToolsConstants.SEARCH_PREFILLED,target,false);
	}
	
	public void addAdmin() {
		form = new UserDialog(ToolsConstants.ADD,"",true);
	}
	public void editAdmin() {
		form = new UserDialog(ToolsConstants.EDIT,"",true);
	}
	public void editAdmin(String target) {
		form = new UserDialog(ToolsConstants.EDIT_PREFILLED,target,true);
	}
	public void deleteAdmin() {
		form = new UserDialog(ToolsConstants.DELETE,"",true);
	}
	public void deleteAdmin(String target) {
		form = new UserDialog(ToolsConstants.DELETE_PREFILLED,target,true);
	}
	public void searchAdmin() {
		form = new UserDialog(ToolsConstants.SEARCH,"",true);
	}
	public void searchAdmin(String target) {
		form = new UserDialog(ToolsConstants.SEARCH_PREFILLED,target,true);
	}	
}
