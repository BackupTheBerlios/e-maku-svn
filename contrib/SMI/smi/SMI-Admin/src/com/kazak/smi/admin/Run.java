package com.kazak.smi.admin;

import java.text.ParseException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.kazak.smi.admin.gui.LoginWindow;
import com.kazak.smi.admin.gui.MainWindow;

import de.javasoft.plaf.synthetica.SyntheticaBlueIceLookAndFeel;

public class Run {
	
	public static String login = "";
	
	public static void main(String[] args) {
		try {
			String envMode = System.getenv("METAL");
			if (envMode==null) {
				UIManager.setLookAndFeel(new SyntheticaBlueIceLookAndFeel());
			}
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		new LoginWindow();
	}
	
	public static void exit() {
		int op = JOptionPane.showConfirmDialog(
				MainWindow.getFrame(),
				"Realmente desea Salir?",
				"Salir",
				JOptionPane.YES_NO_OPTION);
		if (op==JOptionPane.YES_OPTION) {
			System.exit(0);			
		}
	}
}
