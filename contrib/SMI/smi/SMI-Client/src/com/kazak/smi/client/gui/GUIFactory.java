package com.kazak.smi.client.gui;

import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;

import com.kazak.smi.lib.misc.FixedSizePlainDocument;

public class GUIFactory {
	
	public JButton createButton(
			String name,
			char mnemonic,
			String command,
			String icon ,
			int alignment) {
		URL url = this.getClass().getResource("/com/kazak/smi/client/resources/"+icon);
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
		JTextField jtf = new JTextField(cols);
		Color color = Color.BLACK;
		jtf.setEnabled(false);		
		jtf.setDisabledTextColor(color);
		return jtf;
	}
	
	public JTextField createTextField(int cols,boolean enabled) {
		JTextField jtf = new JTextField(cols);
		jtf.setDocument(new FixedSizePlainDocument(cols));
		Color color = Color.BLACK;
		jtf.setEnabled(enabled);
		jtf.setDisabledTextColor(color);
		return jtf;
	}
}
