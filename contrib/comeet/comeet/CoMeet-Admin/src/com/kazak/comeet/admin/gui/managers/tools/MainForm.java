package com.kazak.comeet.admin.gui.managers.tools;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.kazak.comeet.admin.gui.main.MainWindow;
import com.kazak.comeet.admin.gui.managers.tools.pos.PosPanel;
import com.kazak.comeet.admin.gui.managers.tools.group.GroupPanel;
//import com.kazak.comeet.admin.gui.managers.tools.user.UserPanel;
import com.kazak.comeet.admin.gui.table.UserPosTable;

public class MainForm extends JFrame {

	private static final long serialVersionUID = 1L;
	private String target;
	private ButtonBar buttonBar;
	private GroupPanel groupPanel;
	private PosPanel posPanel;
	private UserPosTable table;
	
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
			groupPanel = new GroupPanel(this,action,target);
			this.add(groupPanel,BorderLayout.CENTER);
			break;
			// Pos
		case ToolsConstants.POS:
			posPanel = new PosPanel(this,action,target,table);
			this.add(posPanel,BorderLayout.CENTER);
			break;
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
		}
	}
	
	public GroupPanel getGroupPanel() {
		return groupPanel;
	}
	
	public PosPanel getPosPanel() {
		return posPanel;
	}

	public ButtonBar getButtonBar(){
		return buttonBar;
	}
}
