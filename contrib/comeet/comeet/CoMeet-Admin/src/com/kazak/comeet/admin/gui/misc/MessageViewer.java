package com.kazak.comeet.admin.gui.misc;

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
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.kazak.comeet.admin.gui.misc.ExtensionFilter;

/**
 * @author     Cristian Cepeda
 */
public class MessageViewer implements ActionListener {
	
	private JFrame frame;
	private HistoryMessagePanel historyMessagePanel;
	private HistoryDataPanel historyDataPanel;
	private NavigationButtonsPanel buttons;
	
	public MessageViewer(Vector<Vector<Object>> data) {
		frame = new JFrame();
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
		
		frame.setTitle(historyDataPanel.getMessagesTotal()+" Mensajes");
		
		Dimension dimesion = historyDataPanel.getSize();
		dimesion.setSize(dimesion.getWidth(),180);
		historyDataPanel.setPreferredSize(dimesion);
		frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("next".equals(command)) {
			JTable table = historyDataPanel.getTable();
			int rowIndex = table.getSelectedRow();
			if (rowIndex+1 < table.getRowCount()) {
				table.changeSelection((rowIndex+1),0,false,false);
			}
		}
		else if ("previous".equals(command)) {
			JTable table = historyDataPanel.getTable();
			int rowIndex = table.getSelectedRow();
			if (rowIndex >= 0) {
				table.changeSelection((rowIndex-1),0,false,false);
			}
		}
		else if ("close".equals(command)) {
			frame.dispose();
		}
		else if ("save".equals(command)) {
			JFileChooser chooser = new JFileChooser();
			ExtensionFilter filter = new ExtensionFilter("txt","Texto Plano");
			chooser.addChoosableFileFilter(filter);
			chooser.setSelectedFile(new File("mensajes.txt"));
			
			while(true) {
				boolean write = true;
				int returnVal = chooser.showSaveDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					int result = -1;
					File file = chooser.getSelectedFile();
					if (file.exists()) {
						// Overwrite file?
						result = JOptionPane.showConfirmDialog(frame,"Desea sobreescribir el archivo \"" 
								+ file.getName() + "\"?");
						if(result == 2 || result == 1) {
							write = false;
						}			    		
					}
					if (write) {
						createMessagesFile(file);
						break;
					} 
				} else {
					break;
				} // end if returnVal
			} // end while
		}
	}
	
	public void createMessagesFile(File file) {
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			JTable table = historyDataPanel.getTable(); 
			TableColumnModel columnModel = table.getColumnModel();
			int maxRows = table.getRowCount();
			int maxCols = table.getColumnCount();
			byte[] endOfLine = "\n".getBytes();
			String bar = "**********************************************************************\n";
			String header = "                        MENSAJES - CoMeet\n";
			outputStream.write(bar.getBytes());
			outputStream.write(header.getBytes());
			outputStream.write(bar.getBytes());
			for (int i=0; i < maxRows ; i++) {
				for (int j=0; j < maxCols ; j++) {
					String value = table.getValueAt(i,j).toString().trim();
					TableColumn tmp = columnModel.getColumn(j);
					String title = (String) tmp.getHeaderValue() + ": ";
					outputStream.write(title.getBytes());
					if(title.equals("Mensaje: ")) {
						outputStream.write(endOfLine);
					}
					if(title.equals("Leído: ")) {
						String flag = "Si";
						if(value.equals("false")) {
							flag = "No";
						}	
						outputStream.write(flag.getBytes());
					} else {
						outputStream.write(value.getBytes());
					}
					outputStream.write(endOfLine);
				}
				outputStream.write(bar.getBytes());
			}
			outputStream.close();
			JOptionPane.showMessageDialog(
					frame,
			"Archivo guardado con éxito. ");
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(
					frame,
					"Error: No se pudo guardar el archivo.\n" +
					"Causa: " + ex.getLocalizedMessage());
			ex.printStackTrace();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(
					frame,
					"Error: No se pudo guardar el archivo.\n" +
					"Causa: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
