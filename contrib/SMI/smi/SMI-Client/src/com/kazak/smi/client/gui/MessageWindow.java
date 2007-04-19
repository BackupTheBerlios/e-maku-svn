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
import javax.swing.UIManager;

import org.jdom.Element;

import com.kazak.smi.client.network.MessageSender;
import com.kazak.smi.lib.misc.FixedSizePlainDocument;

/**
 * @author     cristian
 */
public class MessageWindow implements ActionListener {
	
	private JFrame frame;
	private MessageArea messageArea = new MessageArea(true);
	private GroupSelector groupSelector;
	private JButton JBSend;
	private JButton JBCanel;
	private JTextField JTFSubject;
	private GUIFactory fact;
	
	public MessageWindow() {
		System.out.println("Nuevo Envio de mesaje");
		fact = new GUIFactory();
		JTFSubject = new JTextField(25);
		JTFSubject.setDocument(new FixedSizePlainDocument(256));
		Color color = Color.BLACK;
		JTFSubject.setEnabled(true);
		JTFSubject.setDisabledTextColor(color);
		initComps();
		frame.setVisible(true);
	}
	
	public void forReply(String dest, String subject) {
		//System.out.println("Reply Destino: " + dest);
		groupSelector.setSelectedItem(dest);
		JTFSubject.setText("[RE:"+subject+"]");
		JTFSubject.setEditable(false);
		JTFSubject.setCaretPosition(0);
	}
	
	private void initComps() {
		frame = new JFrame("Envio de mensajes");
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
		
		groupSelector = new GroupSelector();
		JBSend = fact.createButton("Enviar",'E',"send","send.png",SwingConstants.LEFT);
		JBCanel = fact.createButton("Cancelar", 'C', "cancel","close.png",SwingConstants.LEFT);
		JBSend.addActionListener(this);
		JBCanel.addActionListener(this);
		
		JPanel jpnorth = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel jpsouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel jpaux = new JPanel(new BorderLayout());
		JPanel jpfields = new JPanel(new GridLayout(2,1));
		JPanel jplabels = new JPanel(new GridLayout(2,1));
	
		jplabels.add(new JLabel("Destino:  "));
		jplabels.add(new JLabel("Asunto:   "));
		jpaux.add(jplabels,BorderLayout.WEST);
	
		jpfields.add(groupSelector);
		jpfields.add(JTFSubject);
		jpaux.add(jpfields,BorderLayout.CENTER);
		jpnorth.add(jpaux);

		jpsouth.add(JBSend);
		jpsouth.add(JBCanel);
		
		frame.add(jpnorth,BorderLayout.NORTH);
		frame.add(messageArea,BorderLayout.CENTER);
		frame.add(jpsouth,BorderLayout.SOUTH);
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public JFrame getRefWindow() {
		return frame;
	}

	public Element getPackage() {
		return null;
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if ("send".equals(command)) {
			JButton b = (JButton) e.getSource();
			b.setEnabled(false);
			String to = (String) groupSelector.getSelectedItem();
			String subject = escapeCharacters(JTFSubject.getText());
			String text = escapeCharacters(messageArea.getText());
			if (!"".equals(subject) && !"".equals(text)) {
				new MessageSender(to,subject,text);
				frame.dispose();
			} else {
				JOptionPane.showMessageDialog(
						frame,
						"Información incompleta",
						"Informacion",
						JOptionPane.INFORMATION_MESSAGE);
				if ("".equals(subject) && "".equals(text) ||
					"".equals(subject) && !"".equals(text)) {
					JTFSubject.requestFocus();
				}
				else if (!"".equals(subject) && "".equals(text)) {
					messageArea.requestFocus();
				}
				b.setEnabled(true);
			}
		}
		else if ("cancel".equals(command)) {
			frame.dispose();
		}
	}
	
	private String escapeCharacters(String word) {

		word = word.replaceAll("&","&#38;");
		word = word.replaceAll("'","&#39;");
		word = word.replaceAll("\"","&#34;");
		word = word.replaceAll("<","&#60;");
		word = word.replaceAll(">","&#62;");
		word = word.replaceAll("ñ","&#241;");
		word = word.replaceAll("Ñ","&#209;");
		word = word.replaceAll("á","&#225;");
		word = word.replaceAll("é","&#233;");
		word = word.replaceAll("í","&#237;");
		word = word.replaceAll("ó","&#243;");
		word = word.replaceAll("ú","&#250;");
		word = word.replaceAll("Á","&#201;");
		word = word.replaceAll("É","&#241;");
		word = word.replaceAll("Í","&#205;");
		word = word.replaceAll("Ó","&#211;");
		word = word.replaceAll("Ú","&#218;");
				
		return word;
	}

	public static void main(String []  args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new MessageWindow();
	}
}