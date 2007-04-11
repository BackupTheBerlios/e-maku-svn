package com.kazak.smi.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MessageArea extends JPanel {

	private static final long serialVersionUID = -4602441410611695614L;
	private JTextArea textArea;
	
	public MessageArea(boolean enabled) {
		super(new BorderLayout());
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEnabled(enabled);
		textArea.setDisabledTextColor(Color.BLACK);
		
		JScrollPane jscroll = new JScrollPane(textArea);
		this.add(jscroll,	  BorderLayout.CENTER);
		this.add(new JPanel(),BorderLayout.NORTH);
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(new JPanel(),BorderLayout.EAST);
		this.add(new JPanel(),BorderLayout.SOUTH);
		this.setVisible(true);
	}
	
	public void requestFocus() {
		textArea.requestFocus();
	}
	
	public String getText() {
		return textArea.getText();
	}
	
	public void setText(String text) {
		textArea.setText(text);
	}
}
