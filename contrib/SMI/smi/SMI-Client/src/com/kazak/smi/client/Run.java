package com.kazak.smi.client;

//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.kazak.smi.client.control.Cache;
import com.kazak.smi.client.gui.LoginWindow;
import com.kazak.smi.client.gui.TrayManager;
import com.kazak.smi.lib.misc.ConfigFileClient;
//import com.kazak.smi.lib.misc.ConfigFileNotLoadException;
import com.nilo.plaf.nimrod.NimRODLookAndFeel;

/**
 * @author    cristian
 */
public class Run {
	public static boolean newconfigfile;
	public static String user;
	public Run() {
		//try {
			ConfigFileClient.loadSettings();
			Run.instanceGUI();
		/*} catch (ConfigFileNotLoadException e) {
			JLabel label = new JLabel(
					"No se encontró el archivo \n"+
					"de configuración.\n" +
					"Contacte al administrador del sistema.");
			label.setHorizontalTextPosition(JLabel.CENTER);
			JOptionPane.showMessageDialog(
					null,
					label,
					"Error al iniciar",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}*/
	}

	public static void instanceGUI() {
		new Cache();
		new TrayManager();
		LoginWindow.Show();
	}
	
	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel(new NimRODLookAndFeel());
			new Run();
		}catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}
