package com.kazak.smi.admin.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class NavigationButtonsPanel extends JPanel{
	
	private static final long serialVersionUID = 8682203073690265957L;
	private JButton JBNext; 
	private JButton JBPrevius;
	private JButton JBClose;
	private JButton JBSave;
	
	public NavigationButtonsPanel() {
		super(new FlowLayout(FlowLayout.RIGHT));
		GUIFactory guiFactory = new GUIFactory();
		JBPrevius= guiFactory.createButton("Ver Anterior", 'A',"previus","previus.png",SwingConstants.RIGHT);
		JBNext   = guiFactory.createButton("Ver Siguiente",'S',"next","next.png",SwingConstants.LEFT);
		JBClose  = guiFactory.createButton("Cerrar",       'C',"close","close.png",SwingConstants.LEFT);
		JBSave   = guiFactory.createButton("Guardar",      'G',"save","save.png",SwingConstants.LEFT);
		add(JBPrevius);
		add(JBNext);
		add(JBSave);
		add(JBClose);
	}
	
	public void setActioListener(ActionListener ac) {
		JBPrevius.addActionListener(ac);
		JBNext.addActionListener(ac);
		JBClose.addActionListener(ac);
		JBSave.addActionListener(ac);
	}
}