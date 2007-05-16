package com.kazak.smi.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

//import javax.swing.UIManager;
//import org.jdom.Element;

import com.kazak.smi.client.network.MessageSender;
import com.kazak.smi.lib.misc.FixedSizePlainDocument;

/**
 * @author     Cristian Cepeda
 */
public class MessageWindow implements ActionListener {
	
	private JFrame frame;
	private MessageArea messageArea = new MessageArea(true);
	private GroupsCombo groupsCombo;
	private JButton sendButton;
	private JButton cancelButton;
	private JTextField subjectField;
	private GUIFactory factory;
	
	public MessageWindow() {
		factory = new GUIFactory();
		subjectField = new JTextField(25);
		subjectField.setDocument(new FixedSizePlainDocument(256));
		Color color = Color.BLACK;
		subjectField.setEnabled(true);
		subjectField.setDisabledTextColor(color);
		initComponents();
		frame.setVisible(true);
	}
	
	public void forReply(String destination, String subject) {
		groupsCombo.setSelectedItem(destination);
		subjectField.setText("[RE:"+subject+"]");
		subjectField.setEditable(false);
		subjectField.setCaretPosition(0);
	}
	
	private void initComponents() {
		frame = new JFrame("Envío de mensajes");
		frame.setLayout(new BorderLayout());
		frame.setSize(400,300);
		frame.setLocationByPlatform(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {
				frame.setState(JFrame.NORMAL);
			}
		});
		
		groupsCombo = new GroupsCombo();
		sendButton = factory.createButton("Enviar",'E',"send","send.png",SwingConstants.LEFT);
		cancelButton = factory.createButton("Cancelar", 'C', "cancel","close.png",SwingConstants.LEFT);
		sendButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel panelAux = new JPanel(new BorderLayout());
		JPanel fieldsPanel = new JPanel(new GridLayout(2,1));
		JPanel labelsPanel = new JPanel(new GridLayout(2,1));
	
		labelsPanel.add(new JLabel("Destino:  "));
		labelsPanel.add(new JLabel("Asunto:   "));
		panelAux.add(labelsPanel,BorderLayout.WEST);
	
		fieldsPanel.add(groupsCombo);
		fieldsPanel.add(subjectField);
		panelAux.add(fieldsPanel,BorderLayout.CENTER);
		northPanel.add(panelAux);

		southPanel.add(sendButton);
		southPanel.add(cancelButton);
		
		frame.add(northPanel,BorderLayout.NORTH);
		frame.add(messageArea,BorderLayout.CENTER);
		frame.add(southPanel,BorderLayout.SOUTH);
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public JFrame getWindow() {
		return frame;
	}
	
	// TODO: Preguntar que hace este metodo?
/*
	public Element getPackage() {
		return null;
	}
*/	
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if ("send".equals(command)) {
			JButton button = (JButton) e.getSource();
			button.setEnabled(false);
			String to      = (String) groupsCombo.getSelectedItem();
			String subject = subjectField.getText();
			String text    = messageArea.getText();
			if (!"".equals(subject) && !"".equals(text)) {
				new MessageSender(to,subject,text);
				frame.dispose();
			} else {
				JOptionPane.showMessageDialog(
						frame,
						"Información incompleta",
						"Información",
						JOptionPane.INFORMATION_MESSAGE);
				if ("".equals(subject) && "".equals(text) ||
					"".equals(subject) && !"".equals(text)) {
					subjectField.requestFocus();
				}
				else if (!"".equals(subject) && "".equals(text)) {
					messageArea.requestFocus();
				}
				button.setEnabled(true);
			}
		}
		else if ("cancel".equals(command)) {
			frame.dispose();
		}
	}

}