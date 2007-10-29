package com.kazak.comeet.admin.control;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.admin.gui.main.MainWindow;
import com.kazak.comeet.admin.network.SocketHandler;
import com.kazak.comeet.admin.network.SocketWriter;

public class SyncManager {
	
	private static JDialog dialog;
	public static boolean isSuccessful = false;
	public static boolean errorHappened = false;
	
	public void startSync() {
		JLabel label = new JLabel();
		label.setText("<html>"+
				"Realmente desea sincronizar la base de datos?"+
				"</html>");
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(label);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		int option = JOptionPane.showConfirmDialog(
				MainWindow.getFrame(),
				panel,
				"Sincronización",
				JOptionPane.YES_NO_OPTION);
		if (option== JOptionPane.YES_OPTION) {
			dialog = new JDialog(MainWindow.getFrame());
			dialog.setResizable(false);
			dialog.setSize(200,100);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLocationByPlatform(true);
			dialog.setLocationRelativeTo(null);
			panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			label = new JLabel("     Sincronizando...");
			panel.add(label);
			dialog.add(panel,BorderLayout.CENTER);
			Element transaction = new Element("Synchronization");
			Document doc = new Document(transaction);
	        try {
				SocketWriter.write(SocketHandler.getSock(),doc);
			} catch (IOException ex) {
				System.out.println("ERROR: Falla de entrada/salida");
				System.out.println("Causa: " + ex.getMessage());
				ex.printStackTrace();
			}
			dialog.setVisible(true);
			
			Thread t = new Thread() {
				public void run() {
					
					int times = 0;
					while(true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (SyncManager.isSuccessful || SyncManager.errorHappened) {
							dialog.setVisible(false);
							return;
						}
						if (times==120) {
							dialog.setVisible(false);
							dialog.dispose();
							JOptionPane.showMessageDialog(
									MainWindow.getFrame(),
									"<html>" +
									"<b>" +
									"<p aling=center>" +
									"Error Sincronizando la base de datos<br>" +
									"Tiempo de espera agotado."+
									"<p></b></html>",
									"Sincronización",
									JOptionPane.YES_NO_OPTION);
							break;
						}
						times++;
					}
				}
			};
			t.start();
		}
	}

	public static JDialog getDialog() {
		return dialog;
	}
}