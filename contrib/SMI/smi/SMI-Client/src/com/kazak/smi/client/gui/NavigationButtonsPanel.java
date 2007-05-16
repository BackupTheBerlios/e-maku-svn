package com.kazak.smi.client.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class NavigationButtonsPanel extends JPanel{
	
	private static final long serialVersionUID = 8682203073690265957L;
	private JButton nextButton; 
	private JButton previousButton;
	private JButton replyButton;
	private JButton closeButton;
	
	public NavigationButtonsPanel() {
		super(new FlowLayout(FlowLayout.RIGHT));
		GUIFactory guiFactory = new GUIFactory();
		previousButton = guiFactory.createButton("Ver Anterior", 'A',"previus","previus.png",SwingConstants.RIGHT);
		nextButton     = guiFactory.createButton("Ver Siguiente",'S',"next","next.png",SwingConstants.LEFT);
		replyButton    = guiFactory.createButton("Responder",    'R',"reply","reply.png",SwingConstants.LEFT);
		closeButton    = guiFactory.createButton("Cerrar",       'C',"close","close.png",SwingConstants.LEFT);
		add(previousButton);
		add(nextButton);
		add(replyButton);
		add(closeButton);
	}
	
	public void setActioListener(ActionListener action) {
		previousButton.addActionListener(action);
		nextButton.addActionListener(action);
		replyButton.addActionListener(action);
		closeButton.addActionListener(action);
	}
}