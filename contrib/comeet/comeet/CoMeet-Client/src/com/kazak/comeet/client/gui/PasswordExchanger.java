package com.kazak.comeet.client.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.channels.SocketChannel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.client.Run;
import com.kazak.comeet.client.misc.TextDataValidator;
import com.kazak.comeet.client.network.QuerySender;
import com.kazak.comeet.client.network.SocketHandler;
import com.kazak.comeet.client.network.SocketWriter;
import com.kazak.comeet.lib.misc.MD5Tool;

public class PasswordExchanger implements ActionListener {
	
	private static boolean displayed;
	private JFrame frame;
	private JPasswordField passwdField1;
	private JPasswordField passwdField2;
	private static JButton acceptButton;
	private static JButton cancelButton;
		
	public PasswordExchanger() {
		frame = new JFrame("Cambio de Clave");
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				PasswordExchanger.displayed = false;
			}
			public void windowIconified(WindowEvent e) {
				frame.setState(JFrame.NORMAL);
			}
		});
		passwdField1 = new JPasswordField(15);
		passwdField1.setDocument(new TextDataValidator(10));
		
		passwdField2 = new JPasswordField(15);
		passwdField2.setDocument(new TextDataValidator(10));
		passwdField2.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String pass1 = new String(passwdField1.getPassword());
				String pass2 = new String(passwdField2.getPassword());
				if (!pass1.equals(pass2)) {
					JOptionPane.showMessageDialog(
							frame,
							"Las claves digitadas no coinciden");
					passwdField1.setText("");
					passwdField2.setText("");
					passwdField1.requestFocus();
				}
			}
		});
		acceptButton = new JButton("Aceptar");
		cancelButton = new JButton("Cancelar");
		acceptButton.setMnemonic('A');
		cancelButton.setMnemonic('C');
		acceptButton.addActionListener(this);
		cancelButton.addActionListener(this);
		acceptButton.setActionCommand("accept");
		cancelButton.setActionCommand("cancel");
		
		JPanel jpsouth = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jpsouth.add(acceptButton);
		jpsouth.add(cancelButton);
		JLabel jl1 =new JLabel("Nueva clave: ");
		JLabel jl2 =new JLabel("Repita la clave: ");
		
		JPanel jplabels = new JPanel(new GridLayout(2,1));
		JPanel jpfields = new JPanel(new GridLayout(2,1));
		
		jplabels.add(jl1);
		jplabels.add(jl2);
	
		jpfields.add(passwdField1);
		jpfields.add(passwdField2);
		
		frame.add(jplabels,BorderLayout.WEST);
		frame.add(jpfields,BorderLayout.CENTER);
		frame.add(jpsouth,BorderLayout.SOUTH);
		
		frame.setSize(300,110);
		frame.setLocationByPlatform(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void show() {
		if (!displayed) {
			PasswordExchanger.displayed = true;
			new PasswordExchanger();
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("accept".equals(command)) {
			String passwd1 = new String(passwdField1.getPassword());
			String passwd2 = new String(passwdField2.getPassword());
			if (!"".equals(passwd1) && !"".equals(passwd2) && passwd1.equals(passwd2)) {
				int option = JOptionPane.showConfirmDialog(
						frame,
						"Realmente desea cambiar la clave\n" +
						"para el sistema de correo?",
						"Cambio de Clave - CoMeet",
						JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					MD5Tool md5 = new MD5Tool(passwd1);
					Element transaction = new Element("Transaction");
					Document doc = new Document(transaction);
					
					Element id = new Element("id");
			        id.setText(QuerySender.getId());
			        transaction.addContent(id);
			        
			        Element driver = new Element("driver");
			        driver.setText("TR010");
			        transaction.addContent(driver);
			        
					Element pack = new Element("package");
					pack.addContent(new Element("field").setText(md5.getDigest()));
					pack.addContent(new Element("field").setText(Run.user));
					transaction.addContent(pack);
					SocketChannel sock = SocketHandler.getSock();
					try {
						SocketWriter.writing(sock,doc);
					} catch (IOException ex) {
						System.out.println("ERROR: Falla de entrada/salida");
						System.out.println("Causa: " + ex.getMessage());
						ex.printStackTrace();
					}
					frame.dispose();
					PasswordExchanger.displayed = false;
				}
			}
			if (!passwd1.equals(passwd2)) {
				JOptionPane.showMessageDialog(
						frame,
						"Las claves digitadas no coinciden.");
				passwdField1.setText("");
				passwdField2.setText("");
				passwdField1.requestFocus();
			}
			if (passwd1.equals("")) {
				passwdField1.requestFocus();
			}
		}
		else if ("cancel".equals(command)) {
			frame.dispose();
			PasswordExchanger.displayed = false;
		}
	}
}
