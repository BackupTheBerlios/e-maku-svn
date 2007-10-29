package com.kazak.comeet.client.gui;

import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;

import com.kazak.comeet.lib.misc.FixedSizePlainDocument;

public class GUIFactory {
	
	public JButton createButton(
			String name,
			char mnemonic,
			String command,
			String icon,
			int alignment) {
		URL url = this.getClass().getResource("/icons/"+icon);
		ImageIcon imageIcon = new ImageIcon(url);
		JButton button = createButton(name, mnemonic, command);
		button.setHorizontalTextPosition(alignment);
		button.setIcon(imageIcon);
		return button;
	}
	
	public JButton createButton(String name,char mnemonic, String command) {
		JButton button = new JButton(name);
		button.setActionCommand(command);
		button.setMnemonic(mnemonic);
		return button;
	}

	public JTextField createTextField(int cols) {
		JTextField textField = new JTextField(cols);
		Color color = Color.BLACK;
		textField.setEnabled(false);		
		textField.setDisabledTextColor(color);
		return textField;
	}
	
	public JTextField createTextField(int cols,boolean enabled) {
		JTextField textField = new JTextField(cols);
		textField.setDocument(new FixedSizePlainDocument(cols));
		Color color = Color.BLACK;
		textField.setEnabled(enabled);
		textField.setDisabledTextColor(color);
		return textField;
	}
}
