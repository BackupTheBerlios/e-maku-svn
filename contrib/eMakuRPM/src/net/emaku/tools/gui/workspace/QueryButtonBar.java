package net.emaku.tools.gui.workspace;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.emaku.tools.gui.ReportManagerGUI;

//This class contains the button bar for the query workspace

public class QueryButtonBar extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton jbuttonReload;
	private JButton jbuttonSave;
	private JButton jbuttonClose;	
	private ReportManagerGUI gui;
	
	public QueryButtonBar(ReportManagerGUI gui) {

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

		setButtonsState(false);

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
		
		if ("reload".equals(action)) {
			gui.reloadQuery();
		} else if ("save".equals(action)) {
            gui.saveQuery();
		} else if ("close".equals(action)) {
			gui.closeObjectTab(gui.QUERY);
		}
	}
}