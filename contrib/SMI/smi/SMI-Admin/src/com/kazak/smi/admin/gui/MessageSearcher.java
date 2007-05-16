package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;
import com.kazak.smi.lib.misc.FixedSizePlainDocument;
import com.toedter.calendar.JDateChooser;

public class MessageSearcher extends JFrame implements ActionListener {

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
		this.setTitle("Buscar mensajes");
		searchButton.setActionCommand("search");
		cancelButton.setActionCommand("cancel");
		this.setVisible(true);
	}
	
	private void initComponents() {
		componentsList.add(fromField = new JTextField());
		componentsList.add(toField = new JTextField());
		componentsList.add(fieldDate = new JDateChooser());
		componentsList.add(subjectField = new JTextField());
		
		fieldDate.setDateFormatString("yyyy-MM-dd");
		
		fromField.setDocument(new FixedSizePlainDocument(50));
		toField.setDocument(new FixedSizePlainDocument(50));
		subjectField.setDocument(new FixedSizePlainDocument(100));
		
		searchButton = new JButton("Buscar");
		cancelButton = new JButton("Cancelar");
		
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
			new Worker().start();
		}
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
				new MessageViewer(data);
			} catch (QuerySenderException e) {
				e.printStackTrace();
			}
			
		}
	}
}