package com.kazak.smi.admin.gui.misc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import org.jdom.Document;

import com.kazak.smi.admin.gui.table.ControlDialog;
import com.kazak.smi.admin.gui.main.MainWindow;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;
import com.toedter.calendar.JDateChooser;

// This class creates the reports about control messages

public class ControlMessageReporter extends JFrame implements ActionListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private JDateChooser dateField1;
	private JDateChooser dateField2;
		
	private JPanel centerPanel;
	private JPanel southPanel;
	
	private JButton cancelButton;
	private JButton searchButton;
	private ArrayList<Component> componentsList = new ArrayList<Component>();
	public static JFrame frame;
	
	private String[] labels = { "Fecha Inicio ", "Fecha Fin ",};
	
	public ControlMessageReporter() {
		frame = this;
		this.setLayout(new BorderLayout());
		this.setSize(300,130);
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComponents();
		addComponents();
	}
	
	public void init() {
		this.setTitle("Buscar Mensajes de Control");
		searchButton.setActionCommand("search");
		cancelButton.setActionCommand("cancel");
		this.setVisible(true);
	}
	
	private void initComponents() {
		
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
			Date date1 = dateField1.getDate();
			Date date2 = dateField2.getDate();
			if (date1==null) {
				JOptionPane.showMessageDialog(frame,"Por favor, digite la fecha de inicio");
				return;
			}
			if (date2==null) {
				JOptionPane.showMessageDialog(frame,"Por favor, digite la fecha final");
				return;
			}
			
			Calendar calendar1 = Calendar.getInstance(); 
		    Calendar calendar2 = Calendar.getInstance();		    
		    calendar1.setTime(date1);
		    calendar2.setTime(date2);
		    		    
		    if (!calendar1.equals(calendar2)) {
				if (calendar2.after(calendar1)) {					
					getControlMessages(date1,date2);					
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
	
	// This method gets the inbox mail titles from a user
	public static void getControlMessages(final Date initDate, final Date finalDate) {
		Thread t = new Thread() {
			public void run() {
				try {
					System.out.println("Date1: " + initDate.toString());
					String[] args = {initDate.toString(),finalDate.toString()};
					Document doc = QuerySender.getResultSetFromST("SEL0015",args);
					SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy - HH:mm a");
					String date1 = dateFormat.format(initDate);
					String date2 = dateFormat.format(finalDate);

					if(!queryIsEmpty(doc)) {
						frame.dispose();
					    new ControlDialog(frame,date1 + " / " + date2,doc);
					} else {
                        JOptionPane.showMessageDialog(
                                frame,
                                "<html><center>" +
                                "No se encontraron mensajes de control<br>" +
                                "en el rango seleccionado.</center><br>" +
                                "Fecha inicial: " + date1 + "<br>" +
                                "Fecha final: " + date2 +
                                "</html>");
					}
				} catch (QuerySenderException e) {
					System.out.println("ERROR: No se pudieron consultar los mensajes de control" + 
							" en el rango: [" + initDate + "/" + finalDate + "]");
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	private static boolean queryIsEmpty(Document doc) {
        List messagesList = doc.getRootElement().getChildren("row");
        if (messagesList.size() == 0) {
        	return true;
        }
        return false;
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
}