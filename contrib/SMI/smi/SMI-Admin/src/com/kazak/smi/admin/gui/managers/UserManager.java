package com.kazak.smi.admin.gui.managers;

import com.kazak.smi.admin.gui.managers.tools.MainForm;
import com.kazak.smi.admin.gui.managers.tools.ToolsConstants;

public class UserManager {
	public MainForm form;
	
	public UserManager() {
	}

	public void addUser() {
		form = new MainForm(ToolsConstants.USER,ToolsConstants.ADD,"");
	}
	
	public void editUser() {
		form = new MainForm(ToolsConstants.USER,ToolsConstants.EDIT,"");
	}

	public void editUser(String target) {
		form = new MainForm(ToolsConstants.USER,ToolsConstants.EDIT_PREFILLED,target);
	}

	public void deleteUser() {
		form = new MainForm(ToolsConstants.USER,ToolsConstants.DELETE,"");
	}

	public void deleteUser(String target) {
		form = new MainForm(ToolsConstants.USER,ToolsConstants.DELETE_PREFILLED,target);
	}
	
	public void searchUser() {
		form = new MainForm(ToolsConstants.USER,ToolsConstants.SEARCH,"");
	}
	
	public void searchUser(String target) {
		form = new MainForm(ToolsConstants.USER,ToolsConstants.SEARCH_PREFILLED,target);
	}
}
