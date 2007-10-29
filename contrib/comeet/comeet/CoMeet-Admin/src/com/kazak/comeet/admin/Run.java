package com.kazak.comeet.admin;

import java.text.ParseException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.kazak.comeet.admin.gui.main.LoginWindow;
import com.kazak.comeet.admin.gui.main.MainWindow;

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
		int option = JOptionPane.showConfirmDialog(
				MainWindow.getFrame(),
				"Realmente desea Salir?",
				"Salir",
				JOptionPane.YES_NO_OPTION);
		
		if (option==JOptionPane.YES_OPTION) {
			System.exit(0);			
		}
	}
}
