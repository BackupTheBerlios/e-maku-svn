package com.kazak.smi.admin.gui.misc;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class NavigationButtonsPanel extends JPanel{
	
	private static final long serialVersionUID = 8682203073690265957L;
	private JButton nextButton; 
	private JButton previousButton;
	private JButton closeButton;
	private JButton saveButton;
	
	public NavigationButtonsPanel() {
		super(new FlowLayout(FlowLayout.RIGHT));
		GUIFactory guiFactory = new GUIFactory();
		previousButton = guiFactory.createButton("Ver Anterior", 'A',"previous","previous.png",SwingConstants.RIGHT);
		nextButton     = guiFactory.createButton("Ver Siguiente",'S',"next","next.png",SwingConstants.LEFT);
		closeButton    = guiFactory.createButton("Cerrar",'C',"close","close.png",SwingConstants.LEFT);
		saveButton     = guiFactory.createButton("Guardar",'G',"save","save.png",SwingConstants.LEFT);
		add(previousButton);
		add(nextButton);
		add(saveButton);
		add(closeButton);
	}
	
	public void setActioListener(ActionListener action) {
		previousButton.addActionListener(action);
		nextButton.addActionListener(action);
		closeButton.addActionListener(action);
		saveButton.addActionListener(action);
	}
}