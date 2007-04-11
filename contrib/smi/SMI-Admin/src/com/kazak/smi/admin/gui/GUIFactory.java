package com.kazak.smi.admin.gui;

import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;

public class GUIFactory {
	
	public JButton createButton(
			String name,
			char mnemonic,
			String command,
			String icon ,
			int alignment) {
		URL url = this.getClass().getResource("/resources/"+icon);
		JButton button = createButton(name, mnemonic, command);
		if (url!=null) {
			ImageIcon imageIcon = new ImageIcon(url);
			button.setIcon(imageIcon);
		}
		button.setHorizontalTextPosition(alignment);
		
		return button;
	}
	
	public JButton createButton(String icon) {
		URL url = this.getClass().getResource("/resources/"+icon);
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

	public JTextField createTextField(int cols) {
		JTextField jtf = new JTextField(cols);
		Color color = Color.BLACK;
		jtf.setEnabled(false);		
		jtf.setDisabledTextColor(color);
		return jtf;
	}
	
	public JTextField createTextField(int cols,boolean enabled) {
		JTextField jtf = new JTextField(cols);
		Color color = Color.BLACK;
		jtf.setEnabled(enabled);		
		jtf.setDisabledTextColor(color);
		return jtf;
	}
}
