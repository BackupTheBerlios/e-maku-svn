package com.kazak.smi.admin.gui.managers.tools;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.kazak.smi.admin.gui.main.MainWindow;
import com.kazak.smi.admin.gui.managers.tools.pos.PosPanel;
import com.kazak.smi.admin.gui.managers.tools.group.GroupPanel;
//import com.kazak.smi.admin.gui.managers.tools.user.UserPanel;
import com.kazak.smi.admin.gui.table.UserPosTable;

public class MainForm extends JFrame {

	private static final long serialVersionUID = 1L;
	private String target;
	private ButtonBar buttonBar;
	private GroupPanel groupPanel;
	private PosPanel posPanel;
	private UserPosTable table;
	// private UserPanel userPanel;
	
	public MainForm(int component, int action, String target) {
		super();
		this.target = target;
		this.setLayout(new BorderLayout());
		
		setPanels(component,action);		
		
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setVisible(true);
	}
	
	public MainForm(int component, int action, String target, UserPosTable table) {
		super();
		this.target = target;
		this.table = table;
		this.setLayout(new BorderLayout());
		
		setPanels(component,action);		
		
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setVisible(true);
	}	
	
	private void setPanels(int component,int action) {
		
	    buttonBar = new ButtonBar(this,component,action);
	    
		switch(component) {
			// Group
		case ToolsConstants.GROUP:
			System.out.println("Creando panel de grupo...");
			groupPanel = new GroupPanel(this,buttonBar,action,target);
			this.add(groupPanel,BorderLayout.CENTER);
			break;
			// Pos
		case ToolsConstants.POS:
			posPanel = new PosPanel(this,buttonBar,action,target,table);
			this.add(posPanel,BorderLayout.CENTER);
			break;
			// User
		/* case ToolsConstants.USER:
			userPanel = new UserPanel(this,buttonBar,action,target);
			this.add(userPanel,BorderLayout.CENTER);
			break; */
		}
		
		this.add(buttonBar,BorderLayout.SOUTH);
	}
	
	public void clean(int component) {
		switch(component) {
			// To Group
		case ToolsConstants.GROUP:
			groupPanel.clean();
			break;
			// To Pos
		case ToolsConstants.POS:
			posPanel.clean();
			break;
			// To User
	/*	case ToolsConstants.USER:
			userPanel.clean();
			break; */
		}
	}
	
	public GroupPanel getGroupPanel() {
		return groupPanel;
	}
	
	public PosPanel getPosPanel() {
		return posPanel;
	}

	/*
	public UserPanel getUserPanel() {
		return userPanel;
	}*/
	
	public ButtonBar getButtonBar(){
		return buttonBar;
	}
}
