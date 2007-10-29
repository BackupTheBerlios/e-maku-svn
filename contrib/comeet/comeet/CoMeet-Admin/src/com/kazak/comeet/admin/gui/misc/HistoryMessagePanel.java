package com.kazak.comeet.admin.gui.misc;

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
	private GUIFactory guiFactory = new GUIFactory();
	private JTextField fromTextField = guiFactory.createTextField(20);
	private JTextField senderTextField = guiFactory.createTextField(20);
	private JTextField subjectTextField= guiFactory.createTextField(20);
	private JTextField dateTextField= guiFactory.createTextField(10);
	private JTextField timeTextField= guiFactory.createTextField(10);
	private JCheckBox checkBox = new JCheckBox();
	
	public HistoryMessagePanel() {
		
		super(new BorderLayout());
		messageArea = new MessageArea(false);
		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel auxPanel = new JPanel(new BorderLayout());
		JPanel fieldsPanel = new JPanel(new GridLayout(3,1));
		JPanel labelsPanel = new JPanel(new GridLayout(3,1));
		
		labelsPanel.add(new JLabel("Remitente: "));
		labelsPanel.add(new JLabel("Destinatario: "));
		labelsPanel.add(new JLabel("Asunto: "));
		
		fieldsPanel.add(fromTextField);
		fieldsPanel.add(senderTextField);
		fieldsPanel.add(subjectTextField);
		
		auxPanel.add(labelsPanel,BorderLayout.WEST);
		auxPanel.add(fieldsPanel,BorderLayout.CENTER);
		topPanel.add(auxPanel);
		
		topPanel.add(Box.createHorizontalStrut(10));
		
		auxPanel = new JPanel(new BorderLayout());
		fieldsPanel = new JPanel(new GridLayout(3,1));
		labelsPanel = new JPanel(new GridLayout(3,1));
		
		labelsPanel.add(new JLabel("Fecha: "));
		labelsPanel.add(new JLabel("Hora: "));
		labelsPanel.add(new JLabel("Confirmado: "));
		checkBox.setEnabled(false);
		fieldsPanel.add(dateTextField);
		fieldsPanel.add(timeTextField);
		fieldsPanel.add(checkBox);
		
		auxPanel.add(labelsPanel,BorderLayout.WEST);
		auxPanel.add(fieldsPanel,BorderLayout.CENTER);
		
		topPanel.add(auxPanel);
		
		this.add(new JPanel(),BorderLayout.WEST);
		this.add(new JPanel(),BorderLayout.EAST);
		this.add(new JPanel(),BorderLayout.SOUTH);
		
		this.add(topPanel,BorderLayout.NORTH);
		this.add(messageArea,BorderLayout.CENTER);
	}
	
	public String getFrom() {
		return fromTextField.getText();
	}
	
	public String getSubject() {
		return subjectTextField.getText();
	}
	
	public void setData(String[] data) {
		fromTextField.setText(data[2]);
		senderTextField.setText(data[3]);
		subjectTextField.setText(data[4]);
		dateTextField.setText(data[0]);
		timeTextField.setText(data[1]);
		messageArea.setText(data[5]);
		checkBox.setSelected(Boolean.parseBoolean(data[6]));
	}
}