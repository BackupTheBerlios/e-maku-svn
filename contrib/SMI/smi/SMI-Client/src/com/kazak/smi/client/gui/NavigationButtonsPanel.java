package com.kazak.smi.client.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class NavigationButtonsPanel extends JPanel{
	
	private static final long serialVersionUID = 8682203073690265957L;
	private JButton JBNext; 
	private JButton JBPrevius;
	private JButton JBReply;
	private JButton JBClose;
	
	public NavigationButtonsPanel() {
		super(new FlowLayout(FlowLayout.RIGHT));
		GUIFactory guiFactory = new GUIFactory();
		JBPrevius= guiFactory.createButton("Ver Anterior", 'A',"previus","previus.png",SwingConstants.RIGHT);
		JBNext   = guiFactory.createButton("Ver Siguiente",'S',"next","next.png",SwingConstants.LEFT);
		JBReply  = guiFactory.createButton("Responder",    'R',"reply","reply.png",SwingConstants.LEFT);
		JBClose  = guiFactory.createButton("Cerrar",       'C',"close","close.png",SwingConstants.LEFT);
		add(JBPrevius);
		add(JBNext);
		add(JBReply);
		add(JBClose);
	}
	
	public void setActioListener(ActionListener ac) {
		JBPrevius.addActionListener(ac);
		JBNext.addActionListener(ac);
		JBReply.addActionListener(ac);
		JBClose.addActionListener(ac);
	}
}