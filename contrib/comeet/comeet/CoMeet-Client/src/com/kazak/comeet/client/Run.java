package com.kazak.comeet.client;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.kazak.comeet.client.control.Cache;
import com.kazak.comeet.client.gui.LoginWindow;
import com.kazak.comeet.client.gui.TrayManager;
import com.kazak.comeet.lib.misc.ClientConfigFile;
import com.nilo.plaf.nimrod.NimRODLookAndFeel;

/**
 * @author    Cristian Cepeda
 */
public class Run {
	public static boolean newConfigFile;
	public static String user;
	
	public Run() {
			ClientConfigFile.loadSettings();
			Run.instanceGUI();
	}

	public static void instanceGUI() {
		new Cache();
		new TrayManager();
		LoginWindow.show();
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new NimRODLookAndFeel());
			new Run();
		}catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}
