package com.kazak.smi.admin.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonsPanel extends JPanel{
	
	private static final long serialVersionUID = 8682203073690265957L;
	private JButton JBAdd; 
	private JButton JBEdit;
	private JButton JBDelete;
	
	public ButtonsPanel() {
		super(new FlowLayout(FlowLayout.CENTER));
		GUIFactory guiFactory = new GUIFactory();
		JBAdd   = guiFactory.createButton("Adicionar", 'A',"add");
		JBEdit  = guiFactory.createButton("Editar",'S',"edit");
		JBDelete= guiFactory.createButton("Eliminar",    'R',"delete");
		add(JBAdd);
		add(JBEdit);
		add(JBDelete);
	}
	
	public void setActioListener(ActionListener ac) {
		JBAdd.addActionListener(ac);
		JBEdit.addActionListener(ac);
		JBDelete.addActionListener(ac);
	}
}