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

public class MessageSercher extends JFrame implements ActionListener {

	private static final long serialVersionUID = 3920757441925057976L;
	private JTextField FieldFrom;
	private JTextField FieldTo;
	private JDateChooser FieldDate;
	private JTextField FieldSubject;
	
	private JPanel JPCenter;
	private JPanel JPSouth;
	
	private JButton JBCancel;
	private JButton JBSearch;
	private ArrayList<Component> listComps = new ArrayList<Component>();

	final JFrame frame = this;
	
	private String[]
    labels = {
			"Remitente ",
			"Destino ",
			"Fecha",
			"Asunto " };
	
	public MessageSercher() {
		this.setLayout(new BorderLayout());
		this.setSize(300,160);
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(MainWindow.getFrame());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		initComps();
		addComps();
	}
	
	public void search() {
		this.setTitle("Buscar mensajes");
		JBSearch.setActionCommand("search");
		JBCancel.setActionCommand("cancel");
		this.setVisible(true);
	}
	
	private void initComps() {
		
		listComps.add(FieldFrom    = new JTextField());
		listComps.add(FieldTo      = new JTextField());
		listComps.add(FieldDate	   = new JDateChooser());
		listComps.add(FieldSubject = new JTextField());
		
		FieldDate.setDateFormatString("yyyy-MM-dd");
		
		FieldFrom.setDocument(new FixedSizePlainDocument(50));
		FieldTo.setDocument(new FixedSizePlainDocument(50));
		FieldSubject.setDocument(new FixedSizePlainDocument(100));
		
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
			Date date = FieldDate.getDate();
			frame.dispose();
			String [] args = null;
			String sqlCode = "";
			if (date==null) {
				args = new String[]{
						FieldFrom.getText(),
						FieldTo.getText(),
						FieldSubject.getText()
						};
				sqlCode ="SEL0021";
			}
			else {
				args = new String[]{
						FieldFrom.getText(),
						FieldTo.getText(),
						FieldDate.getDate().toString(),
						FieldSubject.getText()		
						};
				sqlCode = "SEL0016";
			}

			try {
				Document doc = QuerySender.getResultSetST(sqlCode,args);
				Element root = doc.getRootElement();
				Iterator rows = root.getChildren("row").iterator();
				Vector<Vector<Object>> data = new Vector<Vector<Object>>(); 
				while (rows.hasNext()) {
					Vector<Object> v = new Vector<Object>();
					Element cols = (Element) rows.next();
					Iterator itCols = cols.getChildren().iterator();
					while (itCols.hasNext()) {
						Element e = (Element)itCols.next();
						v.add(e.getText());
					}
					data.add(v);
				}
				new MessageViewer(data);
			} catch (QuerySenderException e) {
				e.printStackTrace();
			}
			
		}
	}
}