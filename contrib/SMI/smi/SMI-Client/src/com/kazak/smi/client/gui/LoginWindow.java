package com.kazak.smi.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.kazak.smi.client.Run;
import com.kazak.smi.client.control.HeadersValidator;
import com.kazak.smi.client.network.CNXSender;
import com.kazak.smi.client.network.SocketHandler;
import com.kazak.smi.lib.misc.ClientConfigFile;
import com.kazak.smi.lib.misc.ClientConstants;
import com.kazak.smi.lib.misc.FixedSizePlainDocument;
import com.kazak.smi.lib.misc.MD5Tool;
import com.kazak.smi.lib.network.PackageToXMLConverter;


public class LoginWindow implements ActionListener {
	
	private static final long serialVersionUID = 4515846092744596420L;
	private static JTextField userTextField;
	private JPasswordField passwordField;
	private static JButton acceptButton;
	private static JFrame frame;
	private static boolean logged = false;
	private static boolean isDisplayed = false;
	public static boolean onTop = true;
	public static int sleepTime = 1000;
	
	public static synchronized  void show() {
		if (!isDisplayed) {
			LoginWindow.isDisplayed = true;
			new LoginWindow();
		}
	}
	
	public LoginWindow() {
		initComponents();
		LoginWindow.logged = false;
		frame.setVisible(true);
		new Displayer();
	}
	
	public void initComponents() {
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(400,300);
		frame.setLocationByPlatform(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setUndecorated(true);
		Color background = new Color(3,1,100);

		frame.addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {
				frame.setState(JFrame.NORMAL);
			}
			public void windowClosing(WindowEvent e) {
				frame.setVisible(true);
			}
		});
		frame.getRootPane().setBorder(new LineBorder(Color.BLACK,4));
		Font font = new Font("Dialog",Font.BOLD,13);
		LoginWindow.userTextField  = new JTextField(12);
		LoginWindow.userTextField.setFont(font);
		LoginWindow.userTextField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent arg0) {
				userTextField.setText(userTextField.getText().toUpperCase());
			}
		});
		passwordField = new JPasswordField(12);
		LoginWindow.userTextField.setDocument(new FixedSizePlainDocument(10));
		passwordField.setDocument(new FixedSizePlainDocument(10));
		passwordField.setFont(font);
		
		acceptButton = new JButton("Aceptar");
		acceptButton.setMnemonic('A');
		acceptButton.addActionListener(this);
		acceptButton.setActionCommand("accept");
		acceptButton.setFont(font);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel southPanel  = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel labelsPanel = new JPanel(new GridLayout(2,1));
		JPanel textFieldsPanel = new JPanel(new GridLayout(2,1));

		centerPanel.add(labelsPanel,BorderLayout.WEST);
		centerPanel.add(textFieldsPanel,BorderLayout.CENTER);
		JLabel userLabel = new JLabel("Usuario:");
		userLabel.setFont(font);
		JLabel passwdLabel = new JLabel("Clave:");
		passwdLabel.setFont(font);
		
		labelsPanel.add(userLabel);
		labelsPanel.add(passwdLabel);
		
		textFieldsPanel.add(addWithPanel(LoginWindow.userTextField));
		textFieldsPanel.add(addWithPanel(passwordField));
		
		southPanel.add(acceptButton);
		
		JPanel centerAux = new JPanel(new FlowLayout(FlowLayout.CENTER));
		centerAux.add(centerPanel);
		URL url = getClass().getResource(ClientConstants.iconsPath + "logo.jpg");
		ImageIcon icon = new ImageIcon(url);
		JLabel logo = new JLabel(icon);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(logo,BorderLayout.NORTH);
		panel.add(centerAux,BorderLayout.CENTER);
		panel.add(southPanel,BorderLayout.SOUTH);
		panel.setBackground(background);
		Component box1 = Box.createVerticalStrut(60);
		Component box2 = Box.createVerticalStrut(60);
			
		frame.add(panel,BorderLayout.CENTER);
		frame.add(box1,BorderLayout.NORTH);
		frame.add(box2,BorderLayout.SOUTH);
	}
	
	private JPanel addWithPanel(Component component) {
		JPanel panel = new JPanel();
		panel.add(component);
		return panel;
	}
	
	private String getUser() {
		return LoginWindow.userTextField.getText();
	}
	
	private String getPassword() {
		return new String(this.passwordField.getPassword());
	}
	
	public static String getLoginUser() {
		return LoginWindow.userTextField.getText();
	}
	
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ("accept".equals(action)) {
			acceptButton.setEnabled(false);
			int typeCursor = Cursor.WAIT_CURSOR;
			Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
			if (!"".equals(getUser()) && !"".equals(getPassword())) {
				SocketHandler connect;
	            frame.setCursor(cursor);
				try {
					PackageToXMLConverter packageXML = new PackageToXMLConverter();
					HeadersValidator valid = new HeadersValidator();
					packageXML.addPackageComingListener(valid);
					String host = ClientConfigFile.getHost();
					int port =  ClientConfigFile.getServerPort(); 
					connect = new SocketHandler(host,port,packageXML);
		            connect.start();
		            SocketChannel socket = SocketHandler.getSock();
		            MD5Tool md5 = new MD5Tool(getPassword());
		            String passwd = md5.getDigest();
		            Run.user = getUser();
		    		new CNXSender(socket,Run.user,passwd);
				} catch (NoRouteToHostException NRHEe) {
					NRHEe.printStackTrace();
					JOptionPane.showMessageDialog(
		                    frame,
		                    "No se pudo establecer comunicación con el sistema.\n" +
		                    "Host: " + ClientConfigFile.getHost() + "\n" +
		                    "Puerto: " + ClientConfigFile.getServerPort() + "\n" +
		                    "Por favor, comuniquese con soporte técnico.",
		                    "Error de Conexión (No hay ruta al servidor)",
		                    JOptionPane.ERROR_MESSAGE);
					
					acceptButton.setEnabled(true);
					typeCursor = Cursor.DEFAULT_CURSOR;
					cursor = Cursor.getPredefinedCursor(typeCursor);
					frame.setCursor(cursor);
					LoginWindow.onTop = false;
					LoginWindow.sleepTime = 10000;
					frame.toBack();
				} catch (ConnectException CEe){
					CEe.printStackTrace();
					JOptionPane.showMessageDialog(
		                    frame,
		                    "No se pudo establecer comunicación con el sistema.\n"+
		                    "Host: " + ClientConfigFile.getHost() + "\n" +
		                    "Puerto: "+ClientConfigFile.getServerPort() + "\n" + 
		                    "Por favor, comuniquese con soporte técnico.",
		                    "Error de Conexión (Excepción de Conexión)",
		                    JOptionPane.ERROR_MESSAGE);
					
					acceptButton.setEnabled(true);
					typeCursor = Cursor.DEFAULT_CURSOR;
					cursor = Cursor.getPredefinedCursor(typeCursor);
					frame.setCursor(cursor);
					LoginWindow.onTop = false;
					LoginWindow.sleepTime = 10000;
					frame.toBack();
				}catch (UnresolvedAddressException UAEe) {
					UAEe.printStackTrace();
					JOptionPane.showMessageDialog(
		                    frame,
		                    "No se pudo resolver la dirección\n" +
		                    "del servidor de mensajería.\n"+
		                    "Host: "+ClientConfigFile.getHost()+ 
		                    "Puerto:"+ClientConfigFile.getServerPort(),
		                    "Error de Conexión",
		                    JOptionPane.ERROR_MESSAGE);
					acceptButton.setEnabled(true);
					typeCursor = Cursor.DEFAULT_CURSOR;
					cursor = Cursor.getPredefinedCursor(typeCursor);
					LoginWindow.onTop = false;
					LoginWindow.sleepTime = 10000;
					frame.toBack();
				} catch (SocketException SE) {
					SE.printStackTrace();
					JOptionPane.showMessageDialog(
		                    frame,
		                    "Este equipo no posee acceso a la red.\n" 
		                    + "Por favor, verifique la configuración del sistema.",
		                    "Error de Conexión",
		                    JOptionPane.ERROR_MESSAGE);					
				} catch (IOException IOEe) {
					IOEe.printStackTrace();
					LoginWindow.onTop = false;
					LoginWindow.sleepTime = 10000;
					frame.toBack();
				}
			}
			if ("".equals(getUser()) && "".equals(getPassword())) {
				JOptionPane.showMessageDialog(
						frame,
						"Información incompleta\n" +
						"debe digitar su nombre de usuario y clave.");
			}
			else if (!"".equals(getUser()) && "".equals(getPassword())) {
				JOptionPane.showMessageDialog(
						frame,
						"Por favor, digite su clave.\n");
			}
			else if ("".equals(getUser()) && !"".equals(getPassword())) {
				JOptionPane.showMessageDialog(
						frame,
						"Por favor, digite su nombre de usuario.\n");
			}
			acceptButton.setEnabled(true);
			LoginWindow.userTextField.requestFocus();
		}
	}

	
	public static void setEnabled() {
    	acceptButton.setEnabled(true);
    }
    
    public static void setCursorState(int state) {
    	frame.setCursor(Cursor.getPredefinedCursor(state));
    }
    
	public static void quit() {
		frame.setVisible(false);
		frame.dispose();
		LoginWindow.isDisplayed = false;
	}

	public static boolean isLogged() {
		return logged;
	}

	public static void setLogged(boolean logged) {
		LoginWindow.logged = logged;
	}
	
	public static JFrame getFrame(){
		return frame;
	}
	
	class Displayer extends Thread {
		public Displayer() {
			start();
		}
		
		public void run() {
			while(true) {
				try {
					Thread.sleep(sleepTime);
					if (!LoginWindow.isLogged()) {
						frame.setAlwaysOnTop(onTop);
						frame.setState(JFrame.NORMAL);
						if (!frame.isFocused() && onTop) {
							frame.requestFocus();
						}
					}
					else {
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}