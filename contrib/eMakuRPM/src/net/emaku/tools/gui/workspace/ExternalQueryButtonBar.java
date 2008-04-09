package net.emaku.tools.gui.workspace;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

//This class contains the button bar for the external editor

public class ExternalQueryButtonBar extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton jbuttonReload;
	private JButton jbuttonSave;
	private JButton jbuttonClose;	
	private QueryWorkSpace gui;
	
	public ExternalQueryButtonBar(QueryWorkSpace gui) {

		this.gui = gui;

		setLayout(new FlowLayout(FlowLayout.LEFT));	

		jbuttonReload 	= new JButton("Reload");
		jbuttonReload.setActionCommand("reload");
		jbuttonReload.setMnemonic(KeyEvent.VK_R);
		jbuttonReload.addActionListener(this);

		jbuttonSave 	= new JButton("Save");
		jbuttonSave.setActionCommand("save");
		jbuttonSave.setMnemonic(KeyEvent.VK_S);
		jbuttonSave.addActionListener(this);

		jbuttonClose 	= new JButton("Close");
		jbuttonClose.setActionCommand("close");
		jbuttonClose.setMnemonic(KeyEvent.VK_C);
		jbuttonClose.addActionListener(this);

		add(jbuttonReload);
		add(jbuttonSave);
		add(jbuttonClose);
	}
	
	public void setButtonsState(boolean flag) {
        jbuttonReload.setEnabled(flag);
        jbuttonSave.setEnabled(flag);
        jbuttonClose.setEnabled(flag);
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ("close".equals(action)) {
			gui.closeZoom();
		}
		if ("save".equals(action)) {
            gui.saveQuery();
		}
		if ("reload".equals(action)) {
			gui.reloadQuery();
		}

	}
}