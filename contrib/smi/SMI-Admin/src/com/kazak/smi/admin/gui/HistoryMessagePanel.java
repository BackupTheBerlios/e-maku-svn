package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HistoryMessagePanel extends JPanel {
	
	private static final long serialVersionUID = 4362803187589726889L;
	private MessageArea messageArea;
	private GUIFactory guiFact = new GUIFactory();
	private JTextField JTFFrom = guiFact.createTextField(20);
	private JTextField JTFTo = guiFact.createTextField(20);
	private JTextField JTFSubject= guiFact.createTextField(20);
	private JTextField JTFDate= guiFact.createTextField(10);
	private JTextField JTFTime= guiFact.createTextField(10);
	private JCheckBox JCheck = new JCheckBox();
	
	public HistoryMessagePanel() {
		
		super(new BorderLayout());
		messageArea = new MessageArea(false);
		
		JPanel jpnort = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel jpaux = new JPanel(new BorderLayout());
		JPanel jpfields = new JPanel(new GridLayout(3,1));
		JPanel jplabels = new JPanel(new GridLayout(3,1));
		
		jplabels.add(new JLabel("Remitente: "));
		jplabels.add(new JLabel("Destinatario: "));
		jplabels.add(new JLabel("Asunto: "));
		
		jpfields.add(JTFFrom);
		jpfields.add(JTFTo);
		jpfields.add(JTFSubject);
		
		jpaux.add(jplabels,BorderLayout.WEST);
		jpaux.add(jpfields,BorderLayout.CENTER);
		jpnort.add(jpaux);
		
		jpnort.add(Box.createHorizontalStrut(10));
		
		jpaux = new JPanel(new BorderLayout());
		jpfields = new JPanel(new GridLayout(3,1));
		jplabels = new JPanel(new GridLayout(3,1));
		
		jplabels.add(new JLabel("Fecha: "));
		jplabels.add(new JLabel("Hora: "));
		jplabels.add(new JLabel("Confirmado: "));
		JCheck.setEnabled(false);
		jpfields.add(JTFDate);
		jpfields.add(JTFTime);
		jpfields.add(JCheck);
		
		jpaux.add(jplabels,BorderLayout.WEST);
		jpaux.add(jpfields,BorderLayout.CENTER);
		
		jpnort.add(jpaux);
		
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(new JPanel(),BorderLayout.EAST);
		this.add(new JPanel(),BorderLayout.SOUTH);
		
		this.add(jpnort,BorderLayout.NORTH);
		this.add(messageArea,BorderLayout.CENTER);
	}
	
	public String getFrom() {
		return JTFFrom.getText();
	}
	
	public String getSubject() {
		return JTFSubject.getText();
	}
	
	public void setData(String[] data) {
		JTFFrom.setText(data[2]);
		JTFTo.setText(data[3]);
		JTFSubject.setText(data[4]);
		JTFDate.setText(data[0]);
		JTFTime.setText(data[1]);
		messageArea.setText(data[5]);
		JCheck.setSelected(Boolean.parseBoolean(data[6]));
	}
}