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
	private JDateChooser FieldDate1;
	private JDateChooser FieldDate2;
		
	private JPanel JPCenter;
	private JPanel JPSouth;
	
	private JButton JBCancel;
	private JButton JBSearch;
	private ArrayList<Component> listComps = new ArrayList<Component>();

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
		JBSearch.setActionCommand("search");
		JBCancel.setActionCommand("cancel");
		this.setVisible(true);
	}
	
	private void initComps() {
		
		listComps.add(FieldDate1	   = new JDateChooser());
		listComps.add(FieldDate2	   = new JDateChooser());
		
		FieldDate1.setDateFormatString("yyyy-MM-dd h:mm a");
		FieldDate2.setDateFormatString("yyyy-MM-dd h:mm a");
		FieldDate1.getCalendarButton().addActionListener(new MyActionListener(FieldDate1));
		FieldDate2.getCalendarButton().addActionListener(new MyActionListener(FieldDate2));
		JBSearch = new JButton("Buscar");
		JBCancel = new JButton("Cancelar");
		
		JBSearch.addActionListener(this);
		JBCancel.addActionListener(this);
		
		JPCenter = new JPanel(new BorderLayout());
		JPSouth  = new JPanel(new FlowLayout(FlowLayout.CENTER));
	}
	
	private void addComps() {
		JPanel jplabels = new JPanel(new GridLayout(labels.length,0));
		JPanel jpfields = new JPanel(new GridLayout(labels.length,0));
		
		for (int i=0 ; i< labels.length ; i++) {
			jplabels.add(new JLabel(labels[i]));
			jpfields.add(listComps.get(i));
		}

		JPCenter.add(jplabels,BorderLayout.WEST);
		JPCenter.add(jpfields,BorderLayout.CENTER);

		JPSouth.add(JBSearch);
		JPSouth.add(JBCancel);
		
		this.add(JPCenter,BorderLayout.CENTER);
		this.add(JPSouth,BorderLayout.SOUTH);
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
			Date date1 = FieldDate1.getDate();
			Date date2 = FieldDate2.getDate();
			if (date1==null) {
				JOptionPane.showMessageDialog(frame,"Debe Digitar la fecha de inicio");
				return;
			}
			if (date2==null) {
				JOptionPane.showMessageDialog(frame,"Debe Digitar la fecha de fin");
				return;
			}
			
			Calendar c1 = Calendar.getInstance(); 
		    Calendar c2 = Calendar.getInstance();
		    
		    c1.setTime(date1);
		    c2.setTime(date2);
		    if (!c1.equals(c2)) {
				if (c2.after(c1)) {
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
						SocketWriter.writing(SocketHandler.getSock(),doc);
					} catch (IOException ex) {
						System.out.println("Error de entrada y salida");
						System.out.println("mensaje: " + ex.getMessage());
						ex.printStackTrace();
					}
					frame.dispose();
				}
				else {
					JOptionPane.showMessageDialog(frame,"La fecha de fin debe ser posterior a la de inicio");
				}
		    }
		    else {
				JOptionPane.showMessageDialog(frame,"La fecha de fin debe ser posterior a la de inicio");
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