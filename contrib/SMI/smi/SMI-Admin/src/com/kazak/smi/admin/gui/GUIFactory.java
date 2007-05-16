package com.kazak.smi.admin.gui;

import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;

public class GUIFactory {
	
	public JButton createButton(
			String name, char mnemonic,	String command,	String icon , int alignment) {
		
		URL url = this.getClass().getResource("/icons/"+icon);
		JButton button = createButton(name, mnemonic, command);
		if (url!=null) {
			ImageIcon imageIcon = new ImageIcon(url);
			button.setIcon(imageIcon);
		}
		button.setHorizontalTextPosition(alignment);
		
		return button;
	}
	
	public JButton createButton(String icon) {
		URL url = this.getClass().getResource("/icons/"+icon);
		ImageIcon imageIcon = new ImageIcon(url);
		JButton button = new JButton(imageIcon);

		return button;
	}
	
	public JButton createButton(String name,char mnemonic, String command) {
		JButton button = new JButton(name);
		button.setActionCommand(command);
		button.setMnemonic(mnemonic);

		return button;
	}

	public JTextField createTextField(int columns) {
		JTextField textField = new JTextField(columns);
		Color color = Color.BLACK;
		textField.setEnabled(false);		
		textField.setDisabledTextColor(color);

		return textField;
	}
	
	public JTextField createTextField(int columns,boolean enabled) {
		JTextField textField = new JTextField(columns);
		Color color = Color.BLACK;
		textField.setEnabled(enabled);		
		textField.setDisabledTextColor(color);

		return textField;
	}
}
