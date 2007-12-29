package client.gui.forms;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.Run;
import client.comunications.CNXSender;
import client.control.HeadersValidator;
import client.gui.components.panels.AuthenticationPanel;
import client.misc.ClientConstants;
import client.misc.MD5;
import client.misc.settings.ConfigFileHandler;

import common.comunications.PackageToXML;
import common.comunications.SocketConnector;
import common.comunications.SocketWriter;
import common.misc.language.Language;
import common.misc.parameters.EmakuParametersStructure;

/**
 * Connection.java Creado el 03-ago-2004
 * 
 * Este archivo es parte de Emaku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * JMClient es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de la parte de autenticacion del common
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */

public class Connection {

    private static JFrame connectionFrame;
    private AuthenticationPanel authPanel;
    private static JButton connectButton;
    
    public Connection() {
                
        connectionFrame = new JFrame(Language.getWord("TITLE-CONEC"));
        connectionFrame.setResizable(false);
        connectionFrame.setLayout(new BorderLayout());
        
        JLabel imgLabel = new JLabel(new ImageIcon(connectionFrame.getClass().getResource("/icons/e-maku_splash.png")),JLabel.CENTER);
        
        JPanel imgPanel = new JPanel();
        imgPanel.add(imgLabel);
        
        authPanel = new AuthenticationPanel(AuthenticationPanel.ALL, this);
       
        JPanel southPanel = new JPanel();
        connectButton = new JButton();
        connectButton.setIcon(new ImageIcon(this.getClass().getResource(
                "/icons/ico_conectar.png")));
        connectButton.setToolTipText(Language.getWord("CONNECT"));
        
        JButton cancelButton = new JButton();
        cancelButton.setIcon(new ImageIcon(this.getClass().getResource(
                "/icons/ico_cancelar.png")));
        cancelButton.setToolTipText(Language.getWord("CANCEL"));        

        connectButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER ||
                   e.getKeyCode() == KeyEvent.VK_SPACE ) { 
                    connect(); }
            }
        });

        connectButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) { connect(); }
            }
        });
        
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent AEe) { Run.exit(); }
        });
        
        southPanel.add(connectButton);
        southPanel.add(cancelButton);

        /*
         * Mostrando un historial
         */
        FileInputStream historyFile = null;
        Properties properties = new Properties();
        boolean history = false;
         try {
			historyFile = new FileInputStream(new File(ClientConstants.CONF+"history"));
			properties.load(historyFile);
			authPanel.setDataBase(properties.getProperty("database"));
			authPanel.setUser(properties.getProperty("user"));
			history = true;
			historyFile.close();
			historyFile = null;
			properties = null;
			
		} catch (FileNotFoundException e1) {}
		catch (IOException e1) {}
        
		//JPanel center = new JPanel(new BorderLayout());
		//center.add(JPAutenticacion,BorderLayout.CENTER);
		
		connectionFrame.add(imgPanel, BorderLayout.NORTH);
        connectionFrame.add(new JPanel(), BorderLayout.EAST);
        connectionFrame.add(authPanel, BorderLayout.CENTER);
        connectionFrame.add(new JPanel(), BorderLayout.WEST);        
        connectionFrame.add(southPanel, BorderLayout.SOUTH);
        
        //JPAutenticacion.setVisible(true);
        
        connectionFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        connectionFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { Run.exit(); }
        });
       
        connectionFrame.pack();
        connectionFrame.setLocation(
                (ClientConstants.MAX_WIN_SIZE_WIDTH / 2) - connectionFrame.getWidth() / 2,
                (ClientConstants.MAX_WIN_SIZE_HEIGHT / 2) - connectionFrame.getHeight() / 2);
        connectionFrame.setVisible(true);
        if (history) {
        	authPanel.getPasswordTextField().requestFocus();
        }
    }

    public boolean connect() {
   	
            SocketConnector socketConnector;
            connectButton.setEnabled(false);
            connectionFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				PackageToXML packageXML = new PackageToXML();
				HeadersValidator headers = new HeadersValidator();
				packageXML.addArrivePackageListener(headers);
				socketConnector = new SocketConnector(ConfigFileHandler.getHost(),
						  ConfigFileHandler.getServerPort(),packageXML);
				
	            socketConnector.start();
	            
	            EmakuParametersStructure.removeParameter("dataBase");
	            EmakuParametersStructure.addParameter("dataBase",authPanel.getDataBase());
	            EmakuParametersStructure.removeParameter("userLogin");
	            EmakuParametersStructure.addParameter("userLogin",authPanel.getUser());
	            SocketChannel socket = SocketConnector.getSock();
	            String password = new String(authPanel.getPassword());
	    		MD5 md5 = new MD5(password);
	            SocketWriter.writing(socket,
				                    CNXSender.getPackage(
				                            authPanel.getDataBase(),
				                            authPanel.getUser(),
				                            md5.getDigest()));
			} catch (ConnectException CEe){
				JOptionPane.showMessageDialog(
	                    connectionFrame,Language.getWord("ERR_CONNECT")+"\n"+
	                    Language.getWord("HOST")+": "+ConfigFileHandler.getHost()+"\n"+
	                    Language.getWord("PORT")+": "+ConfigFileHandler.getServerPort(),
	                    Language.getWord("ERR_TITLE_CONNECT"),
	                    JOptionPane.ERROR_MESSAGE,
	                    new ImageIcon(this.getClass().getResource("/icons/ico_database.png")));
				connectButton.setEnabled(true);
				connectionFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}catch (UnresolvedAddressException e) {
				JOptionPane.showMessageDialog(
	                    connectionFrame,
	                    Language.getWord("ERR_UNRESOLVED_ADDRESS")+"\n"+
	                    Language.getWord("HOST")+": "+ConfigFileHandler.getHost()+ 
	                    Language.getWord("PORT")+": "+ConfigFileHandler.getServerPort(),
	                    Language.getWord("ERR_TITLE_CONNECT"),
	                    JOptionPane.ERROR_MESSAGE,
	                    new ImageIcon(this.getClass().getResource("/icons/ico_database.png")));
				connectButton.setEnabled(true);
				connectionFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			
			/*
			 * Capturemos para el historial 
			 */
			
			try {
				FileOutputStream historyFile = new FileOutputStream(new File(ClientConstants.CONF+"history"));
				String database="database="+authPanel.getDataBase()+"\n";
				String user="user="+authPanel.getUser()+"\n";
				historyFile.write(database.getBytes());
				historyFile.write(user.getBytes());
				historyFile.close();
				historyFile=null;
			} catch (FileNotFoundException e) {}
			catch (IOException e) {}
			
            return true;
    }
    
    public static void dispose(){
        connectionFrame.dispose();
    }
    
    public static void setEnabled() {
    	connectButton.setEnabled(true);
    }
    
    public static void setCursorState(int state) {
    	connectionFrame.setCursor(Cursor.getPredefinedCursor(state));
    }
}
