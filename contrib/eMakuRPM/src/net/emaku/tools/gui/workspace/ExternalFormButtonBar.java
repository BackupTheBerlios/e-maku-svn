package net.emaku.tools.gui.workspace;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

//This class contains the button bar for the workspace

public class ExternalFormButtonBar extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton jbuttonReload;
	private JButton jbuttonSave;
	private JButton jbuttonClose;
	private JButton jbuttonPreview;	
	private FormWorkSpace gui;
	
	public ExternalFormButtonBar(FormWorkSpace gui) {

		this.gui = gui;

		setLayout(new FlowLayout(FlowLayout.LEFT));	

		jbuttonPreview = new JButton("Preview");
		jbuttonPreview.setActionCommand("preview");
		jbuttonPreview.setMnemonic(KeyEvent.VK_P);
		jbuttonPreview.addActionListener(this);
		jbuttonPreview.setEnabled(false);

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
		if ("close".equals(action)) {
			gui.closeZoom();
		}
		if ("save".equals(action)) {
            gui.saveForm();
		}
		if ("reload".equals(action)) {
			gui.reloadForm();
		}
		/*
		if ("preview".equals(action)) {
			gui.previewForm();
		}*/
	}
}