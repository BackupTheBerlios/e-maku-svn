package com.kazak.smi.admin.gui.managers.tools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonBar extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JButton acceptButton;
	private JButton cancelButton;
	private JButton cleanButton;
	private MainForm mainFrame;
	private int component;
	private int action;
	
	public ButtonBar(MainForm mainFrame, int component, int action) {
		super();
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.mainFrame = mainFrame;
		this.component = component;
		this.action = action;
		initButtons();		
		addButtons();
	}

	private void initAcceptButton() {
		System.out.println("Iniciando boton aceptar...");
		acceptButton = new JButton("Aceptar");
		acceptButton.setMnemonic('A');
		acceptButton.addActionListener(this);
		acceptButton.addKeyListener(this);
		acceptButton.setName("accept");
	}
	
	private void initCleanButton() {
		cleanButton = new JButton("Limpiar");
		cleanButton.setMnemonic('L');
		cleanButton.addActionListener(this);
		cleanButton.setActionCommand("clean");
		cleanButton.addKeyListener(this);
		cleanButton.setName("clean");		
	}

	private void initCancelButton() {
		cancelButton = new JButton("Cancelar");
		cancelButton.setMnemonic('C');
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");
		cancelButton.addKeyListener(this);
		cancelButton.setName("cancel");
	}

	private void initButtons() {		
		
		initAcceptButton();
		initCleanButton();
		initCancelButton();
		
		acceptButton.setActionCommand("exec");
		
		switch(action) {
			// To Add
		case ToolsConstants.ADD:
			acceptButton.setToolTipText("Adicionar");
			break;
			// To Edit
		case ToolsConstants.EDIT:
			acceptButton.setToolTipText("Editar");
			break;
			// To Edit (filled)
		case ToolsConstants.EDIT_PREFILLED:
			acceptButton.setToolTipText("Editar");
			break;
			// To Delete
		case ToolsConstants.DELETE:
			acceptButton.setToolTipText("Eliminar");
			break;
			// To Delete (filled)
		case ToolsConstants.DELETE_PREFILLED:
			acceptButton.setToolTipText("Eliminar");
			break;
			// To Search
		case ToolsConstants.SEARCH:
			acceptButton.setToolTipText("Buscar");
			break;
			// To Search (filled)
		case ToolsConstants.SEARCH_PREFILLED:
			acceptButton.setToolTipText("Buscar");
			break;
		}
	}
	
	private void addButtons(){
		switch(action) {
		// To Add, To Edit, To Delete (unfilled/filled)
		case ToolsConstants.ADD:
		case ToolsConstants.EDIT:
		case ToolsConstants.EDIT_PREFILLED:
		case ToolsConstants.DELETE:
		case ToolsConstants.DELETE_PREFILLED:
			System.out.println("Añadiendo botones...");
			this.add(acceptButton);
			this.add(cleanButton);		
			this.add(cancelButton);
			break;
			// To Search (unfilled/filled)
		case ToolsConstants.SEARCH:			
		case ToolsConstants.SEARCH_PREFILLED:
			this.add(cleanButton);
			this.add(cancelButton);
			break;
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		processButtonEvent(command);
	}
	
	private void processButtonEvent(String command) {
		if (command.equals("exec")) {
			callComponentOperation();
		}

		if (command.equals("clean")) {
			cleaner(component);
		}
		
		if (command.equals("cancel")) {
			mainFrame.dispose();
		}			
	}

	private void callComponentOperation() {
		switch(component) {
			// Group
		case ToolsConstants.GROUP:
			mainFrame.getGroupPanel().executeOperation();
			break;
			// Pos
		case ToolsConstants.POS:
			mainFrame.getPosPanel().executeOperation();
			break;
			// User
		case ToolsConstants.USER:
			mainFrame.getUserPanel().executeOperation();
			break;
		}
	}
	
	private void cleaner(int component) {
		switch(component) {
			// To Group
		case ToolsConstants.GROUP:
			mainFrame.clean(0);
			break;
			// To Pos
		case ToolsConstants.POS:
			mainFrame.clean(1);
			break;
			// To User
		case ToolsConstants.USER:
			mainFrame.clean(2);
			break;
		}
	}
	
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {	
		int keyCode = e.getKeyCode();
		JButton button = (JButton) e.getSource();
		String actionString = button.getActionCommand();
		
		if (keyCode==KeyEvent.VK_ENTER){
			String buttonName = button.getName();
			if (buttonName.equals("accept")) {
				this.requestFocus();
				processButtonEvent(actionString);
			}
			if (buttonName.equals("clean")) {
				cleaner(1);
			}
			if (buttonName.equals("cancel")) {
				mainFrame.dispose();
			}
		}
	}

	public void keyTyped(KeyEvent e) {
	}	
	
	public void setEnabledAcceptButton(boolean active) {
		if(acceptButton != null)
			acceptButton.setEnabled(active);
		else
			System.out.println("El boton es nulo");
	}
}
