package com.kazak.smi.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.kazak.smi.client.control.Cache;

/**
 * @author     cristian
 */
public class MessageViewer implements ActionListener {
	
	private static JFrame frame;
	private HistoryMessagePanel historyMessagePanel;
	private HistoryDataPanel historyDataPanel;
	private NavigationButtonsPanel buttons;
	private static boolean displayed = false;
	
	public static void show() {
		if (!displayed) {
			MessageViewer.displayed = true;
			new MessageViewer();
		}
		else {
			frame.setState(JFrame.NORMAL);
		}
	}
	
	public MessageViewer() {
		frame = new JFrame("Mensajes Recibidos");
		frame.setSize(620,500);
		frame.setLocationByPlatform(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				MessageViewer.displayed = false;
			}
			public void windowIconified(WindowEvent e) {
				frame.setState(JFrame.NORMAL);
			}
		});
		historyMessagePanel = new HistoryMessagePanel();
		historyDataPanel = new HistoryDataPanel(historyMessagePanel);
		buttons = new NavigationButtonsPanel();
		buttons.setActioListener(this);
		
		frame.add(historyDataPanel,BorderLayout.NORTH);
		frame.add(historyMessagePanel,BorderLayout.CENTER);
		frame.add(buttons,BorderLayout.SOUTH);
		
		Dimension dimesion = historyDataPanel.getSize();
		dimesion.setSize(dimesion.getWidth(),180);
		historyDataPanel.setPreferredSize(dimesion);
		frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("next".equals(command)) {
			JTable table = historyDataPanel.getJTData();
			int rowIndex = table.getSelectedRow();
			if (rowIndex+1 < table.getRowCount()) {
				table.changeSelection((rowIndex+1),0,false,false);
			}
		}
		else if ("previus".equals(command)) {
			JTable table = historyDataPanel.getJTData();
			int rowIndex = table.getSelectedRow();
			if (rowIndex >= 0) {
				table.changeSelection((rowIndex-1),0,false,false);
			}
		}
		else if ("reply".equals(command)) {
			
			if (Cache.getMessages().size()>0 ) {
				String from = historyMessagePanel.getFrom();
				String subject = historyMessagePanel.getSubject();
				if (!"".equals(subject)) {
					MessageWindow msgWindow = new MessageWindow();
					msgWindow.forReply(from,subject);
			        msgWindow.setVisible(true);
			        MessageViewer.displayed = false;
			        frame.dispose();
				}
				else {
					JOptionPane.showMessageDialog(
							frame,
							"Debe seleccionar un mensaje");
				}
			}
		}
		else if ("close".equals(command)) {
			MessageViewer.displayed = false;
			frame.dispose();
		}
	}
}