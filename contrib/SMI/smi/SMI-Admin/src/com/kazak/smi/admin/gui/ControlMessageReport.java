package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.admin.network.SocketWriter;
import com.kazak.smi.admin.transactions.QuerySender;
import com.toedter.calendar.JDateChooser;

public class ControlMessageReport extends JFrame implements ActionListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private JDateChooser dateField1;
	private JDateChooser dateField2;
		
	private JPanel centerPanel;
	private JPanel southPanel;
	
	private JButton cancelButton;
	private JButton searchButton;
	private ArrayList<Component> componentsList = new ArrayList<Component>();
	final JFrame frame = this;
	
	private String[] labels = { "Fecha Inicio ", "Fecha Fin ",};
	
	public ControlMessageReport() {
		this.setLayout(new BorderLayout());
		this.setSize(300,130);
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComps();
		addComps();
	}
	
	public void init() {
		this.setTitle("Buscar mensajes");
		searchButton.setActionCommand("search");
		cancelButton.setActionCommand("cancel");
		this.setVisible(true);
	}
	
	private void initComps() {
		
		componentsList.add(dateField1 = new JDateChooser());
		componentsList.add(dateField2 = new JDateChooser());
		
		dateField1.setDateFormatString("yyyy-MM-dd h:mm a");
		dateField2.setDateFormatString("yyyy-MM-dd h:mm a");
		dateField1.getCalendarButton().addActionListener(new MyActionListener(dateField1));
		dateField2.getCalendarButton().addActionListener(new MyActionListener(dateField2));
		searchButton = new JButton("Buscar");
		cancelButton = new JButton("Cancelar");
		
		searchButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		centerPanel = new JPanel(new BorderLayout());
		southPanel  = new JPanel(new FlowLayout(FlowLayout.CENTER));
	}
	
	private void addComps() {
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
			Date date1 = dateField1.getDate();
			Date date2 = dateField2.getDate();
			if (date1==null) {
				JOptionPane.showMessageDialog(frame,"Debe Digitar la fecha de inicio");
				return;
			}
			if (date2==null) {
				JOptionPane.showMessageDialog(frame,"Debe Digitar la fecha de fin");
				return;
			}
			
			Calendar calendar1 = Calendar.getInstance(); 
		    Calendar calendar2 = Calendar.getInstance();		    
		    calendar1.setTime(date1);
		    calendar2.setTime(date2);
		    
		    if (!calendar1.equals(calendar2)) {
				if (calendar2.after(calendar1)) {
					Element transaction = new Element("Transaction");
					Document doc = new Document(transaction);
					
					Element id = new Element("id");
			        id.setText(QuerySender.getId());
			        transaction.addContent(id);
			        
			        Element driver = new Element("driver");
			        driver.setText("TR013");
			        transaction.addContent(driver);
			        
					Element pack = new Element("package");
					pack.addContent(createField(date1.toString()));
					pack.addContent(createField(date2.toString()));
					pack.addContent(createField(LoginWindow.getLoginUser()));
					
					transaction.addContent(pack);
					
					try {
						SocketWriter.write(SocketHandler.getSock(),doc);
					} catch (IOException ex) {
						System.out.println("ERROR: Falla de entrada/salida");
						System.out.println("Causa: " + ex.getMessage());
						ex.printStackTrace();
					}
					frame.dispose();
				}
				else {
					JOptionPane.showMessageDialog(frame,"La fecha de fin debe ser posterior a la de inicio.");
				}
		    }
		    else {
				JOptionPane.showMessageDialog(frame,"La fecha de fin debe ser posterior a la de inicio.");
			}
		}
	}
	
	class MyActionListener implements ActionListener {
		
		private JDateChooser chooser;
		
		public MyActionListener(JDateChooser chooser) {
			this.chooser = chooser;
		}
		
		public void actionPerformed(ActionEvent e) {
			((JTextComponent)this.chooser.getDateEditor()).requestFocus();
		}
	}
	
	private Element createField(String text) {
		Element element = new Element("field");
		element.setText(text);
		return element;
	}
}