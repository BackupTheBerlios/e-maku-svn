package com.kazak.smi.admin.gui.managers.tools.user;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.kazak.smi.admin.gui.main.MainWindow;
import com.kazak.smi.admin.gui.managers.tools.ButtonBar;
import com.kazak.smi.admin.gui.managers.tools.user.InternalPanel;

public class UserDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private int action;
	private String target;
	private ButtonBar buttonBar;
	private InternalPanel dynamicPanel;
	private boolean dynamicPanelIsVisible = false;
	private UserPanel userPanel;
	
	public UserDialog(int action, String target) {
		super();
		this.action = action;
		this.target = target;
		this.setLayout(new BorderLayout());		
		setPanels();		
		this.pack();
		this.setResizable(false);
		this.setSize(380,100);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setVisible(true);
	}
	
	private void setPanels() {
	    buttonBar = new ButtonBar(this, action);		
		userPanel = new UserPanel(this,buttonBar,action,target);
		this.add(userPanel,BorderLayout.NORTH);
		this.add(buttonBar,BorderLayout.SOUTH);
	}
	
	public void clean() {
		if (dynamicPanelIsVisible) {
			this.userPanel.clean();
			buttonBar.setEnabledClearButton(false);
		}
	}
	
	public void executeOperation() {
		
	}
	
	public void collapseInternalPanel()  {
		//  Remove the details panel from the dialog
		getContentPane().remove (dynamicPanel);

		//  Shrink the dialog the height of the removed panel
		Dimension detailsPanelSize = dynamicPanel.getSize ();
		Dimension dialogSize = getSize ();
		dialogSize.height -= detailsPanelSize.height;
		setSize (dialogSize);

		//  Cause the new layout to take effect
		invalidate ();
		validate ();
		
		if (dynamicPanelIsVisible) {
			dynamicPanelIsVisible = false;
		}
		centerDialog(dialogSize);
	}
	
	public void expandInternalPanel(String user)  {

		boolean isAdmin = true;
		
		if(user.startsWith("CV")) {
			isAdmin = false;
		}
		
		buttonBar.setEnabledClearButton(true);
		
		dynamicPanel = new InternalPanel(this,user,isAdmin,action);

		//  Add the details panel to the dialog
		getContentPane().add (dynamicPanel, BorderLayout.CENTER);

		//  Resize the dialog to accomidate the added size
		Dimension dialogSize = getSize ();
		Dimension detailSize = null;
		
		if (dynamicPanelIsVisible)  {
			//  The details area already has a size from the last time it was displayed
			detailSize = dynamicPanel.getSize ();
		}  else  {
			//  The details area has yet to be made visible
			detailSize = dynamicPanel.getPreferredSize ();
		}

		//  Increase the height of the dialog to fit the added details area
		dialogSize.height += detailSize.height;
		setSize(dialogSize);

		//  Cause the new layout to take effect
		invalidate ();
		validate ();
		
		if (!dynamicPanelIsVisible) {
			dynamicPanelIsVisible = true;
		}
		
		centerDialog(dialogSize);
	}
	
	private void centerDialog(Dimension dialogSize)  {
		JFrame owner = MainWindow.getFrame();
		if (owner == null  ||  !owner.isVisible ())  {
			Dimension screenDimension = Toolkit.getDefaultToolkit ().getScreenSize ();
			setLocation (screenDimension.width / 2 - dialogSize.width / 2,
					screenDimension.height / 2 - dialogSize.height / 2);
		}
	}
		
}
