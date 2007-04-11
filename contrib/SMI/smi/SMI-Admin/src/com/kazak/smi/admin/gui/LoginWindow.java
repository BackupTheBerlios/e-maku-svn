package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.kazak.smi.admin.Run;
import com.kazak.smi.admin.control.HeadersValidator;
import com.kazak.smi.admin.misc.NumericDataValidator;
import com.kazak.smi.admin.network.CNXSender;
import com.kazak.smi.admin.network.SocketHandler;
import com.kazak.smi.lib.misc.FixedSizePlainDocument;
import com.kazak.smi.lib.misc.MD5Tool;
import com.kazak.smi.lib.network.PackageToXML;

public class LoginWindow implements ActionListener {
	
	private static final long serialVersionUID = 4515846092744596420L;
	private JTextField JTFHost;
	private JTextField JTFPort;
	private static JTextField JTFUser;
	private JPasswordField JPFPassword;
	private static JButton JBAccept;
	private static JButton JBCancel;
	private static JFrame frame;
	private static boolean loged = false;
	
	public LoginWindow() {
		initComps();
		frame.setVisible(true);
	}
	
	public void initComps() {
		frame = new JFrame("Ingreso - Modulo Administrador");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				close();
			}
		});
		frame.setSize(250,200);
		frame.setLocationByPlatform(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		JTFHost     = new JTextField(12);
		JTFPort     = new JTextField(12);
		JTFUser     = new JTextField(12);
		JPFPassword = new JPasswordField(12);
		
		JTFUser.setDocument(new FixedSizePlainDocument(30));
		JTFPort.setDocument(new NumericDataValidator(4));
		
		JBAccept = new JButton("Aceptar");
		JBCancel = new JButton("Cancelar");
		JBAccept.setMnemonic('A');
		JBCancel.setMnemonic('C');
		JBAccept.addActionListener(this);
		JBCancel.addActionListener(this);
		JBAccept.setActionCommand("accept");
		JBCancel.setActionCommand("cancel");
		
		JPanel JPCenter = new JPanel(new BorderLayout());
		JPanel JPSouth  = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel JPLabels = new JPanel(new GridLayout(4,1));
		JPanel JPFields = new JPanel(new GridLayout(4,1));

		JPCenter.add(JPLabels,BorderLayout.WEST);
		JPCenter.add(JPFields,BorderLayout.CENTER);
		
		JPLabels.add(new JLabel("Host:"));
		JPLabels.add(new JLabel("Puerto:"));
		JPLabels.add(new JLabel("Usuario:"));
		JPLabels.add(new JLabel("Clave:"));
		
		JPFields.add(addWithPanel(JTFHost));
		JPFields.add(addWithPanel(JTFPort));
		JPFields.add(addWithPanel(JTFUser));
		JPFields.add(addWithPanel(JPFPassword));
		
		JPSouth.add(JBAccept);
		JPSouth.add(JBCancel);
		
		JPanel centerAux = new JPanel(new FlowLayout(FlowLayout.CENTER));
		centerAux.add(JPCenter);
		
		frame.add(new JPanel(),BorderLayout.NORTH);
		frame.add(centerAux,BorderLayout.CENTER);
		frame.add(JPSouth,BorderLayout.SOUTH);
	}
	
	private JPanel addWithPanel(Component comp) {
		JPanel panel = new JPanel();
		panel.add(comp);
		return panel;
	}
	
	private String getUser() {
		return LoginWindow.JTFUser.getText().trim();
	}
	
	private String getPassword() {
		return new String(this.JPFPassword.getPassword());
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ("accept".equals(action)) {
			JBAccept.setEnabled(false);
			int typeCursor = Cursor.WAIT_CURSOR;
			Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
			String host = JTFHost.getText().trim();
            int port = 
            		JTFPort.getText().trim().equals("") ? 
            		0 :
            		Integer.parseInt(JTFPort.getText().trim());
            
			if (!"".equals(host)              && 
				!"".equals(JTFPort.getText()) && 
				!"".equals(getUser())         &&
				!"".equals(getPassword())) {
				SocketHandler connect;
	            frame.setCursor(cursor);
				try {
					PackageToXML packageXML = new PackageToXML();
					HeadersValidator valid = new HeadersValidator();
					packageXML.addArrivePackageistener(valid);
					connect = new SocketHandler(host,port,packageXML);
		            connect.start();
		            SocketChannel socket = SocketHandler.getSock();
		            MD5Tool md5 = new MD5Tool(getPassword());
		            String pass = md5.getDigest();
		            Run.login = getUser();
		    		new CNXSender(socket,getUser(),pass); 
				} catch (ConnectException CEe){
					CEe.printStackTrace();
					JOptionPane.showMessageDialog(
		                    frame,
		                    "No se pudo establecer comunicación con el servidor\n"+
		                    "Host: "+host+"\n"+
		                    "Puerto "+port,
		                    "Error de Conexión",
		                    JOptionPane.ERROR_MESSAGE);
					
					JBAccept.setEnabled(true);
					typeCursor = Cursor.DEFAULT_CURSOR;
					cursor =Cursor.getPredefinedCursor(typeCursor);
					frame.setCursor(cursor);
				}catch (UnresolvedAddressException UAEe) {
					UAEe.printStackTrace();
					JOptionPane.showMessageDialog(
		                    frame,
		                    "No se pudo resolver la dirección\n" +
		                    "del servidor de mensajeria\n"+
		                    "Host: "+host+ 
		                    "Puerto:"+port,
		                    "Error de Conexión",
		                    JOptionPane.ERROR_MESSAGE);
					JBAccept.setEnabled(true);
					typeCursor = Cursor.DEFAULT_CURSOR;
					cursor =Cursor.getPredefinedCursor(typeCursor);
				} catch (IOException IOEe) {
					IOEe.printStackTrace();
				}
			}
			if ("".equals(host)) {
				JOptionPane.showMessageDialog(
						frame,
						"Información incompleta\n" +
						"debe ingresar el host");
			}
			else if ("".equals(JTFPort.getText())) {
				JOptionPane.showMessageDialog(
						frame,
						"Información incompleta\n" +
						"debe ingresar el puerto");
			}
			else if ("".equals(getUser()) && "".equals(getPassword())) {
				JOptionPane.showMessageDialog(
						frame,
						"Información incompleta\n" +
						"debe digitar su nombre de usuario y clave");
			}
			else if ("".equals(getUser()) && "".equals(getPassword())) {
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
		}
		else if ("cancel".equals(action)) {
			close();
		}
	}
	
	private void close() {
		int op = JOptionPane.showConfirmDialog(
					frame,
					"Realmente desea salir?",
					"",JOptionPane.YES_NO_OPTION);
		
		if (JOptionPane.YES_OPTION == op) {
			frame.setVisible(false);
			JTFUser.setText("");
			JPFPassword.setText("");
			frame.dispose();
		}
	}
	
	public static void setEnabled() {
    	JBAccept.setEnabled(true);
    }
    
    public static void setCursorState(int state) {
    	frame.setCursor(Cursor.getPredefinedCursor(state));
    }
    
	public static void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	public static boolean isLoged() {
		return loged;
	}

	public static void setLoged(boolean loged) {
		LoginWindow.loged = loged;
	}
	
	public static String getLoginUser() {
		return LoginWindow.JTFUser.getText();
	}
}