package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;



/**
 * @author     cristian
 */
public class MessageViewer implements ActionListener {
	
	private JFrame frame;
	private HistoryMessagePanel historyMessagePanel;
	private HistoryDataPanel historyDataPanel;
	private NavigationButtonsPanel buttons;
	
	public MessageViewer(Vector<Vector<Object>> data) {
		frame = new JFrame("Mensajes");
		frame.setSize(620,500);
		frame.setLocationByPlatform(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		historyMessagePanel = new HistoryMessagePanel();
		historyDataPanel = new HistoryDataPanel(historyMessagePanel,data);
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
		else if ("close".equals(command)) {
			frame.dispose();
		}
		else if ("save".equals(command)) {
			JFileChooser chooser = new JFileChooser("mensajes.txt");
		    int returnVal = chooser.showSaveDialog(frame);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	File file = chooser.getSelectedFile();
		    	try {
					FileOutputStream fos = new FileOutputStream(file);
					JTable table = historyDataPanel.getJTData(); 
					int maxRows = table.getRowCount();
					int maxCols = table.getColumnCount();
					byte[] tab = "\t".getBytes();
					byte[] nl = "\n".getBytes();
					for (int i=0; i < maxRows ; i++) {
						for (int j=0; j < maxCols ; j++) {
							String val = table.getValueAt(i,j).toString().trim();
							fos.write(val.getBytes());
							if ( j< (maxCols-1) ) {
								fos.write(tab);	
							}
						}
						fos.write(nl);
					}
					fos.close();
					JOptionPane.showMessageDialog(
							frame,
							"Archivo guardado");
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
		    	
		    }
		    
		}
	}
}