package com.kazak.comeet.admin.gui.table;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import org.jdom.Document;

import com.kazak.comeet.admin.gui.table.ControlTable;
import com.kazak.comeet.admin.gui.table.ControlMessageReport;
import com.kazak.comeet.admin.transactions.QuerySender;
import com.kazak.comeet.admin.transactions.QuerySenderException;

// This class shows the list of control messages sent during a date range

public class ControlDialog extends JFrame implements ActionListener, MouseListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private ControlTable table;
	private JButton closeButton;
	private JButton viewButton;
	private JTextArea messageArea;
	private static JFrame frame;
	private static JDialog dialog;
	
	public ControlDialog(JFrame frame, String dates, Document doc) {
		//super(frame, true);
		ControlDialog.frame = frame;
		setTitle("Mensajes de Control [" + dates + "]");
		table = new ControlTable(doc);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(this);
		table.addKeyListener(this);
		JScrollPane jscroll = new JScrollPane(table);
		jscroll.setAutoscrolls(true);
		
		messageArea = new JTextArea();
		messageArea.setEditable(false);
		JScrollPane jscroll2 = new JScrollPane(messageArea);
		jscroll2.setAutoscrolls(true);
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(jscroll);
		split.setBottomComponent(jscroll2);
		jscroll.setPreferredSize(new Dimension(660,300));
		split.setDividerLocation(160);
	
		viewButton = new JButton("Ver Reporte");
		viewButton.setMnemonic('V');
		viewButton.setActionCommand("view");
		viewButton.addActionListener(this);
		viewButton.setEnabled(false);
		closeButton = new JButton("Cerrar");
		closeButton.setMnemonic('C');
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(viewButton);
		southPanel.add(closeButton);
		
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(split, BorderLayout.CENTER);
		main.add(southPanel, BorderLayout.SOUTH);		
		getContentPane().add(main);
        pack();
        setSize(570,380);
        setResizable(false);
        setLocationRelativeTo(frame);
        setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("view")) {
			loadReportData();			
		}		
		if (command.equals("close")) {
			this.dispose();
		}		
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			setMessage((ControlTable)e.getSource(),0);
		}
		if (e.getClickCount() == 2) {
			loadReportData();
		}		
	}

	private void loadReportData() {
		String date = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
		String hour = (String) table.getModel().getValueAt(table.getSelectedRow(), 1);
		openWaitingDialog();
		getControlReportData(date,hour);
	}
	
	private void openWaitingDialog() {
		int typeCursor = Cursor.WAIT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		setCursor(cursor);
		dialog = new JDialog(frame);
		dialog.setTitle("Generando Informe...");
		dialog.setResizable(false);
		dialog.setSize(250,60);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLocationByPlatform(true);
		dialog.setLocationRelativeTo(frame);
		dialog.setAlwaysOnTop(true);
		JLabel label1 = new JLabel("Por favor, espere un momento.",JLabel.CENTER);
		JPanel main = new JPanel(new FlowLayout(FlowLayout.CENTER));
		main.add(label1);
		dialog.add(main);
		dialog.setVisible(true);		
	}
	
	// This method gets the list of users who answer the message control
	private static void getControlReportData(final String date, final String hour) {
		Thread t = new Thread() {
			public void run() {
				try {
					String[] args = {date,hour};
					// Good users
					Document[] docs = new Document[4];
					docs[0] = QuerySender.getResultSetFromST("SEL0032",args);
					// Late bad users
					docs[1] = QuerySender.getResultSetFromST("SEL0030",args);
					// No answer bad users
					docs[2] = QuerySender.getResultSetFromST("SEL0031",args);
					// Offline users
					docs[3] = QuerySender.getResultSetFromST("SEL0033",args);
					
					dialog.setVisible(false);
					setNormalCursor();
					new ControlMessageReport(frame,docs,date,hour);
					
				} catch (QuerySenderException e) {
					dialog.setVisible(false);
					setNormalCursor();
					// TODO: poner un mensaje de error si algo sucede aqui
					System.out.println("ERROR: No se pudo consultar el reporte de mensajes de control");
					e.printStackTrace();
				} 
			}
		};
		t.start();
	}
	
	private static void setNormalCursor() {
		int typeCursor = Cursor.DEFAULT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		ControlDialog.dialog.setCursor(cursor);		
	}
		
	private void setMessage(ControlTable table, int i) {
		int index = table.getSelectedRow();
		int max = table.getRowCount();
		index = index + i;
		if (index != -1 && index < max) {
			messageArea.setText(table.getModel().getMessage(index));
			messageArea.setCaretPosition(0);
			if (!viewButton.isEnabled()) {
				viewButton.setEnabled(true);
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == 38) {
			setMessage((ControlTable)e.getSource(),-1);		    
		}
		if (code == 40) {
			setMessage((ControlTable)e.getSource(),1);		    
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
}
