package com.kazak.smi.client.gui;

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

import com.kazak.smi.client.Run;
import com.kazak.smi.client.misc.TextDataValidator;
import com.kazak.smi.client.network.QuerySender;
import com.kazak.smi.client.network.SocketHandler;
import com.kazak.smi.client.network.SocketWriter;
import com.kazak.smi.lib.misc.MD5Tool;

public class PasswordExchange implements ActionListener {
	
	private static boolean displayed;
	private JFrame frame;
	private JPasswordField textField1;
	private JPasswordField textField2;
	private static JButton JBAccept;
	private static JButton JBCancel;
	
	public static void show() {
		if (!displayed) {
			PasswordExchange.displayed = true;
			new PasswordExchange();
		}
	}
	
	public PasswordExchange() {
		frame = new JFrame("Cambio de Clave");
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				PasswordExchange.displayed = false;
			}
			public void windowIconified(WindowEvent e) {
				frame.setState(JFrame.NORMAL);
			}
		});
		textField1 = new JPasswordField(15);
		textField1.setDocument(new TextDataValidator(10));
		
		textField2 = new JPasswordField(15);
		textField2.setDocument(new TextDataValidator(10));
		textField2.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String pass1 = new String(textField1.getPassword());
				String pass2 = new String(textField2.getPassword());
				if (!pass1.equals(pass2)) {
					JOptionPane.showMessageDialog(
							frame,
							"Las claves digitadas no coinciden");
					textField1.setText("");
					textField2.setText("");
					textField1.requestFocus();
				}
			}
		});
		JBAccept = new JButton("Aceptar");
		JBCancel = new JButton("Cancelar");
		JBAccept.setMnemonic('A');
		JBCancel.setMnemonic('C');
		JBAccept.addActionListener(this);
		JBCancel.addActionListener(this);
		JBAccept.setActionCommand("accept");
		JBCancel.setActionCommand("cancel");
		
		JPanel jpsouth = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jpsouth.add(JBAccept);
		jpsouth.add(JBCancel);
		JLabel jl1 =new JLabel("Nueva clave: ");
		JLabel jl2 =new JLabel("Repita la clave: ");
		
		JPanel jplabels = new JPanel(new GridLayout(2,1));
		JPanel jpfields = new JPanel(new GridLayout(2,1));
		
		jplabels.add(jl1);
		jplabels.add(jl2);
	
		jpfields.add(textField1);
		jpfields.add(textField2);
		
		//jl.setFont(new Font("Dialog",Font.BOLD,14));
		frame.add(jplabels,BorderLayout.WEST);
		frame.add(jpfields,BorderLayout.CENTER);
		frame.add(jpsouth,BorderLayout.SOUTH);
		
		frame.setSize(300,110);
		frame.setLocationByPlatform(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("accept".equals(command)) {
			String pass1 = new String(textField1.getPassword());
			String pass2 = new String(textField2.getPassword());
			if (!"".equals(pass1) && !"".equals(pass2) && pass1.equals(pass2)) {
				int op = JOptionPane.showConfirmDialog(
						frame,
						"Realmente desea cambiar la clave\n" +
						"para el sistema de correo?",
						"Cambio de Clave SMI",
						JOptionPane.YES_NO_OPTION);
				if (op == JOptionPane.YES_OPTION) {
					MD5Tool md = new MD5Tool(pass1);
					Element transaction = new Element("Transaction");
					Document doc = new Document(transaction);
					
					Element id = new Element("id");
			        id.setText(QuerySender.getId());
			        transaction.addContent(id);
			        
			        Element driver = new Element("driver");
			        driver.setText("TR010");
			        transaction.addContent(driver);
			        
					Element pack = new Element("package");
					pack.addContent(new Element("field").setText(md.getDigest()));
					pack.addContent(new Element("field").setText(Run.user));
					transaction.addContent(pack);
					SocketChannel sock = SocketHandler.getSock();
					try {
						SocketWriter.writing(sock,doc);
					} catch (IOException ex) {
						System.out.println("Error de entrada y salida");
						System.out.println("mensaje: " + ex.getMessage());
						ex.printStackTrace();
					}
					frame.dispose();
					PasswordExchange.displayed = false;
				}
			}
			if (!pass1.equals(pass2)) {
				JOptionPane.showMessageDialog(
						frame,
						"Las claves digitadas no coinciden");
				textField1.setText("");
				textField2.setText("");
				textField1.requestFocus();
			}
			if (pass1.equals("")) {
				textField1.requestFocus();
			}
		}
		else if ("cancel".equals(command)) {
			frame.dispose();
			PasswordExchange.displayed = false;
		}
	}
}
