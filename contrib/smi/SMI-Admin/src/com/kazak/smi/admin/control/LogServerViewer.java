package com.kazak.smi.admin.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.gui.MainWindow;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;

public class LogServerViewer extends Thread {
	
	private static JTextArea textArea;
	private static JScrollPane scroll;
	private static JPanel panel;
	public static boolean requestReceived = false;
	private static Listener listener;
	private Document document;
	
	public LogServerViewer() {
		document = new Document(new Element("RequestLogContent"));
		LogServerViewer.requestReceived = false;
	}
	
	
	public static void reset() {
		textArea.setText("");
		LogServerViewer.requestReceived = true;
	}
	
	public static void loadLog(Document doc) {
		Element e  = doc.getRootElement().getChild("text");
		String text = e.getValue();
		textArea.append(text);
		textArea.setCaretPosition(textArea.getText().length());
	}
	
	public static void loadGUI(){
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setBackground(Color.WHITE);
		final JPopupMenu popup = new JPopupMenu();
		textArea.addMouseListener(new MouseAdapter() {	
			public void mousePressed(MouseEvent e) {
                if ( e.getButton() == MouseEvent.BUTTON3) {
                	popup.show(e.getComponent(), e.getX(), e.getY());
                }
			}
		});
		
		scroll = new JScrollPane(textArea);
		listener = new Listener();
		
		popup.add(new JLabel("Edición"));
		popup.add(new JSeparator());
		
		JMenuItem item = new JMenuItem("Copiar");
		item.setActionCommand("copy");
		item.addActionListener(listener);
		popup.add(item);
				
		item = new JMenuItem("Seleccionar Todo");
		item.setActionCommand("selectAll");
		item.addActionListener(listener);
		popup.add(item);
		
		item = new JMenuItem("Limpiar texto");
		item.setActionCommand("clean");
		item.addActionListener(listener);
		popup.add(item);
		
		popup.add(new JSeparator());
		
		item = new JMenuItem("Ver Registro del servidor");
		item.setActionCommand("query");
		item.addActionListener(listener);
		popup.add(item);
		
		
		
		panel = new JPanel(new BorderLayout());
		panel.add(scroll,BorderLayout.CENTER);
	}
	
	public void run() {
		try {
			SocketWriter.writing(SocketHandler.getSock(),document);
			int typeCursor = Cursor.WAIT_CURSOR;
			Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
			int times = 0;
			while(!requestReceived){
				textArea.setCursor(cursor);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				times++;
				if (times==300){
					JOptionPane.showMessageDialog(
							MainWindow.getFrame(),
							"<html><h3>Tiempo de espera agotado para la solicitud del" +
							"registro del servidor</h3></html>");
					break;
				}
			}
			textArea.setCursor(Cursor.getDefaultCursor());
		} catch (IOException ex) {
			System.out.println("Error de entrada y salida");
			System.out.println("mensaje: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static JScrollPane getScroll() {
		return scroll;
	}

	public static JTextArea getTextArea() {
		return textArea;
	}

	public static Component getPanel() {	
		return panel;
	}
	
	public static class Listener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if ("copy".equals(command)) {
				textArea.copy();
			}
			else if ("selectAll".equals(command)) {
				textArea.setSelectionStart(0);
				textArea.setSelectionEnd(textArea.getText().length());
			}
			else if ("query".equals(command)) {
				new LogServerViewer().start();
			}
			else if ("clean".equals(command)) {
				textArea.setText("");
			}
		}	
	}
}
