package com.kazak.smi.admin.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonsPanel extends JPanel{
	
	private static final long serialVersionUID = 8682203073690265957L;
	private JButton addButton; 
	private JButton editButton;
	private JButton deleteButton;
	
	public ButtonsPanel() {
		super(new FlowLayout(FlowLayout.CENTER));
		GUIFactory guiFactory = new GUIFactory();
		addButton    = guiFactory.createButton("Adicionar",'A',"add");
		editButton   = guiFactory.createButton("Editar",'S',"edit");
		deleteButton = guiFactory.createButton("Eliminar",'R',"delete");
		add(addButton);
		add(editButton);
		add(deleteButton);
	}
	
	public void setActioListener(ActionListener action) {
		addButton.addActionListener(action);
		editButton.addActionListener(action);
		deleteButton.addActionListener(action);
	}
}