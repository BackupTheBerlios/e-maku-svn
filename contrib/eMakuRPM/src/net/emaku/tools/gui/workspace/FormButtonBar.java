package net.emaku.tools.gui.workspace;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.emaku.tools.gui.ReportManagerGUI;

//This class contains the button bar for the workspace

public class FormButtonBar extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton jbuttonReload;
	private JButton jbuttonSave;
	private JButton jbuttonClose;
	private JButton jbuttonPreview;	
	private ReportManagerGUI gui;
	
	public FormButtonBar(ReportManagerGUI gui) {

		this.gui = gui;

		setLayout(new FlowLayout(FlowLayout.LEFT));	

		jbuttonPreview = new JButton("Preview");
		jbuttonPreview.setActionCommand("preview");
		jbuttonPreview.setMnemonic(KeyEvent.VK_P);
		jbuttonPreview.addActionListener(this);

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

		add(jbuttonPreview);
		add(jbuttonReload);
		add(jbuttonSave);
		add(jbuttonClose);
	}
	
	public void setButtonsState(boolean flag) {
		jbuttonPreview.setEnabled(false);
        jbuttonReload.setEnabled(flag);
        jbuttonSave.setEnabled(flag);
        jbuttonClose.setEnabled(flag);
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		
		if ("preview".equals(action)) {
			gui.previewForm();
		} else if ("reload".equals(action)) {
			gui.reloadForm();
		} else if ("save".equals(action)) {
            gui.saveForm();
		} else if ("close".equals(action)) {
			gui.closeObjectTab(gui.FORM);
		}
	}
}