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
import com.kazak.smi.lib.misc.ConfigFileClient;
import com.kazak.smi.lib.misc.ClientConst;
import com.kazak.smi.lib.misc.FixedSizePlainDocument;
import com.kazak.smi.lib.misc.MD5Tool;
import com.kazak.smi.lib.network.PackageToXMLConverter;


public class LoginWindow implements ActionListener {
	
	private static final long serialVersionUID = 4515846092744596420L;
	private static JTextField JTFUser;
	private JPasswordField JPFPassword;
	private static JButton JBAccept;
	private static JFrame frame;
	private static boolean loged = false;
	private static boolean displayed = false;
	public static boolean ontop = true;
	public static int sleepTime = 1000;
	
	public static synchronized  void Show() {
		if (!displayed) {
			LoginWindow.displayed = true;
			new LoginWindow();
		}
	}
	
	public LoginWindow() {
		initComps();
		LoginWindow.loged = false;
		frame.setVisible(true);
		new Displayer();
	}
	
	public void initComps() {
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(400,300);
		frame.setLocationByPlatform(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setUndecorated(true);
		Color background = new Color(3,1,100);
		//frame.setAlwaysOnTop(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {
				frame.setState(JFrame.NORMAL);
			}
			public void windowClosing(WindowEvent e) {
				frame.setVisible(true);
			}
		});
		frame.getRootPane().setBorder(new LineBorder(Color.BLACK,4));
		Font f = new Font("Dialog",Font.BOLD,13);
		LoginWindow.JTFUser  = new JTextField(12);
		LoginWindow.JTFUser.setFont(f);
		LoginWindow.JTFUser.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent arg0) {
				JTFUser.setText(JTFUser.getText().toUpperCase());
			}
		});
		JPFPassword = new JPasswordField(12);
		LoginWindow.JTFUser.setDocument(new FixedSizePlainDocument(10));
		JPFPassword.setDocument(new FixedSizePlainDocument(10));
		JPFPassword.setFont(f);
		
		JBAccept = new JButton("Aceptar");
		JBAccept.setMnemonic('A');
		JBAccept.addActionListener(this);
		JBAccept.setActionCommand("accept");
		JBAccept.setFont(f);
		
		JPanel JPCenter = new JPanel(new BorderLayout());
		JPanel JPSouth  = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel JPLabels = new JPanel(new GridLayout(2,1));
		JPanel JPFields = new JPanel(new GridLayout(2,1));

		JPCenter.add(JPLabels,BorderLayout.WEST);
		JPCenter.add(JPFields,BorderLayout.CENTER);
		JLabel lb1 = new JLabel("Usuario:");
		lb1.setFont(f);
		JLabel lb2 = new JLabel("Clave:");
		lb2.setFont(f);
		
		JPLabels.add(lb1);
		JPLabels.add(lb2);
		
		JPFields.add(addWithPanel(LoginWindow.JTFUser));
		JPFields.add(addWithPanel(JPFPassword));
		
		JPSouth.add(JBAccept);
		
		JPanel centerAux = new JPanel(new FlowLayout(FlowLayout.CENTER));
		centerAux.add(JPCenter);
		URL url = getClass().getResource(ClientConst.iconsPath + "logo.jpg");
		ImageIcon icon = new ImageIcon(url);
		JLabel logo = new JLabel(icon);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(logo,BorderLayout.NORTH);
		panel.add(centerAux,BorderLayout.CENTER);
		panel.add(JPSouth,BorderLayout.SOUTH);
		panel.setBackground(background);
		Component box1 = Box.createVerticalStrut(60);
		Component box2 = Box.createVerticalStrut(60);
			
		frame.add(panel,BorderLayout.CENTER);
		frame.add(box1,BorderLayout.NORTH);
		frame.add(box2,BorderLayout.SOUTH);
	}
	
	private JPanel addWithPanel(Component comp) {
		JPanel panel = new JPanel();
		panel.add(comp);
		return panel;
	}
	
	private String getUser() {
		return LoginWindow.JTFUser.getText();
	}
	
	private String getPassword() {
		return new String(this.JPFPassword.getPassword());
	}
	public static String getLoginUser() {
		return LoginWindow.JTFUser.getText();
	}
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ("accept".equals(action)) {
			JBAccept.setEnabled(false);
			int typeCursor = Cursor.WAIT_CURSOR;
			Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
			if (!"".equals(getUser()) && !"".equals(getPassword())) {
				SocketHandler connect;
	            frame.setCursor(cursor);
				try {
					PackageToXMLConverter packageXML = new PackageToXMLConverter();
					HeadersValidator valid = new HeadersValidator();
					packageXML.addPackageComingListener(valid);
					String host = ConfigFileClient.getHost();
					int port =  ConfigFileClient.getServerPort(); 
					connect = new SocketHandler(host,port,packageXML);
		            connect.start();
		            SocketChannel socket = SocketHandler.getSock();
		            MD5Tool md5 = new MD5Tool(getPassword());
		            String pass = md5.getDigest();
		            Run.user = getUser();
		    		new CNXSender(socket,Run.user,pass);
				} catch (NoRouteToHostException NRHEe) {
					NRHEe.printStackTrace();
					JOptionPane.showMessageDialog(
		                    frame,
		                    "No se pudo establecer comunicación con el servidor\n"+
		                    "Host: "+ConfigFileClient.getHost()+"\n"+
		                    "Puerto "+ConfigFileClient.getServerPort(),
		                    "Error de Conexión",
		                    JOptionPane.ERROR_MESSAGE);
					
					JBAccept.setEnabled(true);
					typeCursor = Cursor.DEFAULT_CURSOR;
					cursor =Cursor.getPredefinedCursor(typeCursor);
					frame.setCursor(cursor);
					LoginWindow.ontop = false;
					LoginWindow.sleepTime = 10000;
					frame.toBack();
				} catch (ConnectException CEe){
					CEe.printStackTrace();
					JOptionPane.showMessageDialog(
		                    frame,
		                    "No se pudo establecer comunicación con el servidor\n"+
		                    "Host: "+ConfigFileClient.getHost()+"\n"+
		                    "Puerto "+ConfigFileClient.getServerPort(),
		                    "Error de Conexión",
		                    JOptionPane.ERROR_MESSAGE);
					
					JBAccept.setEnabled(true);
					typeCursor = Cursor.DEFAULT_CURSOR;
					cursor =Cursor.getPredefinedCursor(typeCursor);
					frame.setCursor(cursor);
					LoginWindow.ontop = false;
					LoginWindow.sleepTime = 10000;
					frame.toBack();
				}catch (UnresolvedAddressException UAEe) {
					UAEe.printStackTrace();
					JOptionPane.showMessageDialog(
		                    frame,
		                    "No se pudo resolver la dirección\n" +
		                    "del servidor de mensajeria\n"+
		                    "Host: "+ConfigFileClient.getHost()+ 
		                    "Puerto:"+ConfigFileClient.getServerPort(),
		                    "Error de Conexión",
		                    JOptionPane.ERROR_MESSAGE);
					JBAccept.setEnabled(true);
					typeCursor = Cursor.DEFAULT_CURSOR;
					cursor =Cursor.getPredefinedCursor(typeCursor);
					LoginWindow.ontop = false;
					LoginWindow.sleepTime = 10000;
					frame.toBack();
				} catch (IOException IOEe) {
					IOEe.printStackTrace();
					LoginWindow.ontop = false;
					LoginWindow.sleepTime = 10000;
					frame.toBack();
				}
			}
			if ("".equals(getUser()) && "".equals(getPassword())) {
				JOptionPane.showMessageDialog(
						frame,
						"Información incompleta\n" +
						"debe digitar su nombre de usuario y clave");
			}
			else if (!"".equals(getUser()) && "".equals(getPassword())) {
				JOptionPane.showMessageDialog(
						frame,
						"Debe digitar su clave\n");
			}
			else if ("".equals(getUser()) && !"".equals(getPassword())) {
				JOptionPane.showMessageDialog(
						frame,
						"Debe digitar su nombre de usuario\n");
			}
			JBAccept.setEnabled(true);
			LoginWindow.JTFUser.requestFocus();
		}
	}

	
	public static void setEnabled() {
    	JBAccept.setEnabled(true);
    }
    
    public static void setCursorState(int state) {
    	frame.setCursor(Cursor.getPredefinedCursor(state));
    }
    
	public static void quit() {
		frame.setVisible(false);
		frame.dispose();
		LoginWindow.displayed = false;
	}

	public static boolean isLoged() {
		return loged;
	}

	public static void setLoged(boolean loged) {
		LoginWindow.loged = loged;
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
					if (!LoginWindow.isLoged()) {
						frame.setAlwaysOnTop(ontop);
						frame.setState(JFrame.NORMAL);
						if (!frame.isFocused() && ontop) {
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