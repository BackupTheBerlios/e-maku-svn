package com.kazak.smi.admin.gui.managers;

import com.kazak.smi.admin.gui.managers.tools.MainForm;
import com.kazak.smi.admin.gui.managers.tools.ToolsConstants;

public class GroupManager {
	public MainForm form;
	
	public GroupManager() {
	}

	public void addGroup() {
		form = new MainForm(ToolsConstants.GROUP,ToolsConstants.ADD,"");
	}
	
	public void editGroup() {
		form = new MainForm(ToolsConstants.GROUP,ToolsConstants.EDIT,"");
	}

	public void editGroup(String target) {
		form = new MainForm(ToolsConstants.GROUP,ToolsConstants.EDIT_PREFILLED,target);
	}

	public void deleteGroup() {
		form = new MainForm(ToolsConstants.GROUP,ToolsConstants.DELETE,"");
	}

	public void deleteGroup(String target) {
		form = new MainForm(ToolsConstants.GROUP,ToolsConstants.DELETE_PREFILLED,target);
	}
	
	public void searchGroup() {
		form = new MainForm(ToolsConstants.GROUP,ToolsConstants.SEARCH,"");
	}
	
	public void searchGroup(String target) {
		form = new MainForm(ToolsConstants.GROUP,ToolsConstants.SEARCH_PREFILLED,target);
	}
}
