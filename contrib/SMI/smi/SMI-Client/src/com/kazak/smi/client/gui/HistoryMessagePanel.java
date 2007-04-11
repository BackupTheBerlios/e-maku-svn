package com.kazak.smi.client.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HistoryMessagePanel extends JPanel {
	
	private static final long serialVersionUID = 4362803187589726889L;
	private MessageArea messageArea;
	private GUIFactory guiFact = new GUIFactory();
	private JTextField JTFFrom = guiFact.createTextField(20);
	private JTextField JTFSubject= guiFact.createTextField(20);
	private JTextField JTFDate= guiFact.createTextField(10);
	private JTextField JTFTime= guiFact.createTextField(10);
	
	public HistoryMessagePanel() {
		
		super(new BorderLayout());
		messageArea = new MessageArea(false);
		
		JPanel jpnort = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel jpaux = new JPanel(new BorderLayout());
		JPanel jpfields = new JPanel(new GridLayout(2,1));
		JPanel jplabels = new JPanel(new GridLayout(2,1));
		
		jplabels.add(new JLabel("Remitente: "));
		jplabels.add(new JLabel("Asunto: "));
		
		jpfields.add(JTFFrom);
		jpfields.add(JTFSubject);
		
		jpaux.add(jplabels,BorderLayout.WEST);
		jpaux.add(jpfields,BorderLayout.CENTER);
		jpnort.add(jpaux);
		
		jpnort.add(Box.createHorizontalStrut(10));
		
		jpaux = new JPanel(new BorderLayout());
		jpfields = new JPanel(new GridLayout(2,1));
		jplabels = new JPanel(new GridLayout(2,1));
		
		jplabels.add(new JLabel("Fecha: "));
		jplabels.add(new JLabel("Hora: "));
		
		jpfields.add(JTFDate);
		jpfields.add(JTFTime);
		
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
		JTFSubject.setText(data[3]);
		JTFDate.setText(data[0]);
		JTFTime.setText(data[1]);
		messageArea.setText(data[4]);
	}
}