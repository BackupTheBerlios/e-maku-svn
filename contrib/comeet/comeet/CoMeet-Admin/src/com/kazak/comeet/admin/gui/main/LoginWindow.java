package com.kazak.comeet.admin.gui.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.kazak.comeet.admin.Run;
import com.kazak.comeet.admin.control.HeadersValidator;
import com.kazak.comeet.admin.misc.NumericDataValidator;
import com.kazak.comeet.admin.network.CNXSender;
import com.kazak.comeet.admin.network.SocketHandler;
import com.kazak.comeet.lib.misc.FixedSizePlainDocument;
import com.kazak.comeet.lib.misc.MD5Tool;
import com.kazak.comeet.lib.network.PackageToXMLConverter;

public class LoginWindow implements ActionListener, KeyListener {
	
	private static final long serialVersionUID = 4515846092744596420L;
	private JTextField hostTextField;
	private JTextField portTextField;
	private static JTextField userTextField;
	private JPasswordField passwdField;
	private static JButton acceptButton;
	private static JButton cancelButton;
	private static JFrame frame;
	private static boolean logged = false;
	
	public LoginWindow() {
		initComponents();
		frame.setVisible(true);
	}
	
	public void initComponents() {
		frame = new JFrame("Ingreso - Módulo Administrador");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				close();
			}
		});
		frame.setSize(275,200);
		frame.setLocationByPlatform(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		// Edit this section for debugging tasks
		hostTextField = new JTextField("",12);
		hostTextField.setName("host");
		hostTextField.addKeyListener(this);
		portTextField = new JTextField("",12);
		portTextField.setName("port");
		portTextField.addKeyListener(this);		
		userTextField = new JTextField("",12);
		userTextField.setName("user");
		userTextField.addKeyListener(this);
		passwdField = new JPasswordField("",12);
		passwdField.setName("passwd");
		passwdField.addKeyListener(this);
		
		// Edit this section for debugging tasks
		userTextField.setDocument(new FixedSizePlainDocument(30));
		portTextField.setDocument(new NumericDataValidator(4));
		
		acceptButton = new JButton("Aceptar");
		cancelButton = new JButton("Cancelar");
		acceptButton.setMnemonic('A');
		cancelButton.setMnemonic('C');
		acceptButton.addActionListener(this);
		cancelButton.addActionListener(this);
		acceptButton.setActionCommand("accept");
		cancelButton.setActionCommand("cancel");
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel southPanel  = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel labelsPanel = new JPanel(new GridLayout(4,1));
		JPanel fieldsPanel = new JPanel(new GridLayout(4,1));

		centerPanel.add(labelsPanel,BorderLayout.WEST);
		centerPanel.add(fieldsPanel,BorderLayout.CENTER);
		
		labelsPanel.add(new JLabel("Servidor:"));
		labelsPanel.add(new JLabel("Puerto:"));
		labelsPanel.add(new JLabel("Usuario:"));
		labelsPanel.add(new JLabel("Clave:"));
		
		fieldsPanel.add(addWithPanel(hostTextField));
		fieldsPanel.add(addWithPanel(portTextField));
		fieldsPanel.add(addWithPanel(userTextField));
		fieldsPanel.add(addWithPanel(passwdField));
		
		southPanel.add(acceptButton);
		southPanel.add(cancelButton);
		
		JPanel centerAux = new JPanel(new FlowLayout(FlowLayout.CENTER));
		centerAux.add(centerPanel);
		
		frame.add(new JPanel(),BorderLayout.NORTH);
		frame.add(centerAux,BorderLayout.CENTER);
		frame.add(southPanel,BorderLayout.SOUTH);
	}
	
	private JPanel addWithPanel(Component component) {
		JPanel panel = new JPanel();
		panel.add(component);
		
		return panel;
	}
	
	private String getUser() {
		return LoginWindow.userTextField.getText().trim();
	}
	
	private String getPassword() {
		return new String(this.passwdField.getPassword());
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ("accept".equals(action)) {
			connect();
		}
		else if ("cancel".equals(action)) {
			close();
		}
	}

	private boolean connect() {
		boolean success = true;
		acceptButton.setEnabled(false);
		int typeCursor = Cursor.WAIT_CURSOR;
		Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
		frame.setCursor(cursor);

		String host = hostTextField.getText().trim();
		int port = portTextField.getText().trim().equals("") ? 0 :
					Integer.parseInt(portTextField.getText().trim());
		
		if (!"".equals(host) && 
				!"".equals(portTextField.getText()) && 
				!"".equals(getUser()) && !"".equals(getPassword())) {
			SocketHandler connect;
			try {
				PackageToXMLConverter packageXML = new PackageToXMLConverter();
				HeadersValidator valid = new HeadersValidator();
				packageXML.addPackageComingListener(valid);
				connect = new SocketHandler(host,port,packageXML);
				connect.start();
				SocketChannel socket = SocketHandler.getSock();
				MD5Tool md5 = new MD5Tool(getPassword());
				String passwd = md5.getDigest();
				Run.login = getUser();
				new CNXSender(socket,getUser(),passwd); 
			} catch (ConnectException CEe){
				CEe.printStackTrace();
				JOptionPane.showMessageDialog(
						frame,
						"No se pudo establecer comunicación con el servidor\n"+
						"Servidor: " + host + "\n" +
						"Puerto: " + port,
						"Error de Conexión",
						JOptionPane.ERROR_MESSAGE);
				success = false;
			}catch (UnresolvedAddressException UAEe) {
				UAEe.printStackTrace();
				JOptionPane.showMessageDialog(
						frame,
						"No se pudo resolver la dirección\n" +
						"del Servidor de Mensajeria\n" +
						"Servidor: " + host + "\n" +
						"Puerto: " + port,
						"Error de Conexión",
						JOptionPane.ERROR_MESSAGE);
				success = false;
			} catch(NoRouteToHostException NRex) {
				JOptionPane.showMessageDialog(
						null,
						"El nombre del servidor o la dirección IP ingresada\n" +
						"no se encuentra disponible o no existe en la red.\n" +
				"Por favor, revise el valor ingresado en el campo Servidor.\n");
				success = false;
			} 
			catch(SocketException Nex) {
				JOptionPane.showMessageDialog(
						null,
						"Este equipo no tiene acceso a la red.\n" +
				"Por favor, revise la configuración de su sistema.\n");
				success = false;
			}
			catch (IOException IOEe) {
				IOEe.printStackTrace();
				success = false;
			}
		}
		if ("".equals(host)) {
			JOptionPane.showMessageDialog(
					frame,
					"Información incompleta.\n" +
			"Por favor, ingrese el nombre del servidor.");
			success = false;
		}
		else if ("".equals(portTextField.getText())) {
			JOptionPane.showMessageDialog(
					frame,
					"Información incompleta.\n" +
			"Por favor, ingrese el puerto.");
			success = false;
		}
		else if ("".equals(getUser()) && "".equals(getPassword())) {
			JOptionPane.showMessageDialog(
					frame,
					"Información incompleta.\n" +
			"Por favor, ingrese el nombre de usuario y la clave.");
			success = false;
		}
		else if ("".equals(getUser()) && "".equals(getPassword())) {
			JOptionPane.showMessageDialog(
					frame,
					"Información incompleta.\n" +
			"Por favor, ingrese el nombre de usuario y la clave.");
			success = false;
		}
		else if (!"".equals(getUser()) && "".equals(getPassword())) {
			JOptionPane.showMessageDialog(
					frame,
			"Por favor, digite su clave.\n");
			success = false;
		}
		else if ("".equals(getUser()) && !"".equals(getPassword())) {
			JOptionPane.showMessageDialog(
					frame,
			"Por favor, digite su nombre de usuario.\n");
			success = false;
		}
		acceptButton.setEnabled(true);
		
		typeCursor = Cursor.DEFAULT_CURSOR;
		cursor = Cursor.getPredefinedCursor(typeCursor);
		frame.setCursor(cursor);

		return success;
	}	

	private void close() {
		int option = JOptionPane.showConfirmDialog(
				frame,
				"Realmente desea salir?",
				"Salir",JOptionPane.YES_NO_OPTION);

		if (JOptionPane.YES_OPTION == option) {
			frame.setVisible(false);
			userTextField.setText("");
			passwdField.setText("");
			frame.dispose();
		}
	}

	public static void setEnabled() {
		acceptButton.setEnabled(true);
	}

	public static void setCursorState(int state) {
		frame.setCursor(Cursor.getPredefinedCursor(state));
	}

	public static void setVisible(boolean b) {
		frame.setVisible(b);
	}

	public static boolean isLogged() {
		return logged;
	}

	public static void setLogged(boolean logged) {
		LoginWindow.logged = logged;
	}

	public static String getLoginUser() {
		return LoginWindow.userTextField.getText();
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		String textField = ((JTextField) e.getSource()).getName();
		int keyCode = e.getKeyCode();

		if (keyCode==KeyEvent.VK_ENTER){
			if (textField.equals("host")) {
				portTextField.requestFocus();
			}
			if (textField.equals("port")) {
				userTextField.requestFocus();
			}
			if (textField.equals("user")) {
				passwdField.requestFocus();
			}
			if (textField.equals("passwd")) {
				if (!connect()) {
				    acceptButton.requestFocus();
				}
			}
		}
	}

	public void keyTyped(KeyEvent e) {
	}
}