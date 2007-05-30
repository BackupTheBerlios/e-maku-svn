package com.kazak.smi.admin.gui.misc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.gui.main.MainWindow;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;
import com.kazak.smi.lib.misc.FixedSizePlainDocument;
import com.toedter.calendar.JDateChooser;

public class MessageSearcher extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private JTextField fromField;
	private JTextField toField;
	private JDateChooser fieldDate;
	private JTextField subjectField;
	
	private JPanel centerPanel;
	private JPanel southPanel;
	
	private JButton cancelButton;
	private JButton searchButton;
	private ArrayList<Component> componentsList = new ArrayList<Component>();

	final JFrame frame = this;
	private JDialog dialog;
	
	private String[]
    labels = {
			"Remitente ",
			"Destino ",
			"Fecha",
			"Asunto " };
	
	public MessageSearcher() {
		this.setLayout(new BorderLayout());
		this.setSize(300,160);
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComponents();
		addComponents();
	}
	
	public void search() {
		this.setTitle("Buscar Mensajes");
		searchButton.setActionCommand("search");
		cancelButton.setActionCommand("cancel");
		this.setVisible(true);
	}
	
	private void initComponents() {
		componentsList.add(fromField = new JTextField());
		fromField.addKeyListener(this);
		fromField.setName("from");
		componentsList.add(toField = new JTextField());
		toField.addKeyListener(this);
		toField.setName("to");
		componentsList.add(fieldDate = new JDateChooser());
		fieldDate.addKeyListener(this);
		fieldDate.setName("date");
		componentsList.add(subjectField = new JTextField());
		subjectField.addKeyListener(this);
		subjectField.setName("subject");
		
		fieldDate.setDateFormatString("yyyy-MM-dd");
		
		fromField.setDocument(new FixedSizePlainDocument(50));
		toField.setDocument(new FixedSizePlainDocument(50));
		subjectField.setDocument(new FixedSizePlainDocument(100));
		
		searchButton = new JButton("Buscar");
		searchButton.setMnemonic('B');
		cancelButton = new JButton("Cancelar");
		cancelButton.setMnemonic('C');
		
		searchButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		centerPanel = new JPanel(new BorderLayout());
		southPanel  = new JPanel(new FlowLayout(FlowLayout.CENTER));
	}
	
	private void addComponents() {
		JPanel labelsPanel = new JPanel(new GridLayout(labels.length,0));
		JPanel fieldsPanel = new JPanel(new GridLayout(labels.length,0));
		
		for (int i=0 ; i< labels.length ; i++) {
			labelsPanel.add(new JLabel(labels[i]));
			fieldsPanel.add(componentsList.get(i));
		}

		centerPanel.add(labelsPanel,BorderLayout.WEST);
		centerPanel.add(fieldsPanel,BorderLayout.CENTER);
		southPanel.add(searchButton);
		southPanel.add(cancelButton);
		
		this.add(centerPanel,BorderLayout.CENTER);
		this.add(southPanel,BorderLayout.SOUTH);
		this.add(new JPanel(),BorderLayout.NORTH);
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(new JPanel(),BorderLayout.EAST);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("cancel")) {
			this.dispose();
		}
		else if (command.equals("search")) {
			openWaitingDialog();
			new Worker().start();
		}
	}
	
	private void openWaitingDialog() {
		int typeCursor = Cursor.WAIT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		setCursor(cursor);
		dialog = new JDialog(frame);
		dialog.setTitle("Realizando búsqueda...");
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
	
	private void setNormalCursor() {
		int typeCursor = Cursor.DEFAULT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		setCursor(cursor);		
	}
	
	class Worker extends Thread {		
		public void run() {
			Date date = fieldDate.getDate();
			frame.dispose();
			String [] argsArray = null;
			String sqlCode = "";
			if (date==null) {
				argsArray = new String[]{
						fromField.getText(),
						toField.getText(),
						subjectField.getText()
						};
				sqlCode ="SEL0021";
			}
			else {
				argsArray = new String[]{
						fromField.getText(),
						toField.getText(),
						fieldDate.getDate().toString(),
						subjectField.getText()		
						};
				sqlCode = "SEL0016";
			}

			try {
				Document doc = QuerySender.getResultSetFromST(sqlCode,argsArray);
				Element root = doc.getRootElement();
				Iterator rows = root.getChildren("row").iterator();
				Vector<Vector<Object>> data = new Vector<Vector<Object>>(); 
				while (rows.hasNext()) {
					Vector<Object> vector = new Vector<Object>();
					Element columns = (Element) rows.next();
					Iterator columnsIterator = columns.getChildren().iterator();
					while (columnsIterator.hasNext()) {
						Element element = (Element)columnsIterator.next();
						vector.add(element.getText());
					}
					data.add(vector);
				}
				dialog.setVisible(false);
				setNormalCursor();
				new MessageViewer(data);

			} catch (QuerySenderException e) {
				e.printStackTrace();
			}
			
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		String textField = ((JTextField) e.getSource()).getName();
		int keyCode = e.getKeyCode();
		
		if (keyCode==KeyEvent.VK_ENTER){
			if (textField.equals("from")) {
				toField.requestFocus();
			}
			if (textField.equals("to")) {
				fieldDate.requestFocus();
			}
			if (textField.equals("date")) {
				subjectField.requestFocus();
			}
			if (textField.equals("subject")) {
				searchButton.doClick();
			}
		}		
	}

	public void keyTyped(KeyEvent e) {
	}
}