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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.Run;
import client.comunications.SendCNX;
import client.control.HeadersValidator;
import client.gui.components.panels.PAutentication;
import client.misc.ClientConst;
import client.misc.settings.ConfigFile;

import common.comunications.PackageToXML;
import common.comunications.SocketConnector;
import common.comunications.SocketWriter;
import common.gui.forms.FirstDialog;
import common.misc.language.Language;
import common.misc.parameters.GenericParameters;

/**
 * Connection.java Creado el 03-ago-2004
 * 
 * Este archivo es parte de JMClient <A
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

    private static JFrame JFConexion;
    private PAutentication JPAutenticacion;
    private static JButton JBconectar;
    
    public Connection() {
                
        JFConexion = new JFrame(Language.getWord("TITLE-CONEC"));
        JFConexion.setResizable(false);
        JFConexion.getContentPane().setLayout(new BorderLayout());

        JPAutenticacion = new PAutentication(PAutentication.ALL);
       
        JPanel JPsur = new JPanel();
        JBconectar = new JButton();
        JBconectar.setIcon(new ImageIcon(this.getClass().getResource(
                "/icons/ico_conectar.png")));
        JBconectar.setToolTipText(Language.getWord("CONECTAR"));

        JButton JBsettings = new JButton();
        JBsettings.setIcon(new ImageIcon(this.getClass().getResource(
                "/icons/ico_configuracion.png")));
        JBsettings.setToolTipText(Language.getWord("SETTINGS"));
        
        JButton JBcancelar = new JButton();
        JBcancelar.setIcon(new ImageIcon(this.getClass().getResource(
                "/icons/ico_cancelar.png")));
        JBcancelar.setToolTipText(Language.getWord("CANCEL"));        

        JBconectar.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER ||
                   e.getKeyCode() == KeyEvent.VK_SPACE ) { 
                    new Boolean(conexion()); }
            }
        });

        JBconectar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) { new Boolean(conexion()); }
            }
        });

        
        JBsettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent AEe) { 
                FirstDialog dialogo = new FirstDialog(new JFrame(),ClientConst.KeyClient);
                dialogo.setLocationRelativeTo(dialogo.getParent());
                dialogo.setVisible(true);
            }
        });
        
        JBcancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent AEe) { Run.salir(); }
        });
        
        JPsur.add(JBconectar);
        JPsur.add(JBsettings);
        JPsur.add(JBcancelar);

        /*
         * Mostrando un historial
         */
        FileInputStream FIShistory = null;
        Properties Phistory = new Properties();
        boolean history = false;
         try {
			FIShistory = new FileInputStream(new File(ClientConst.CONF+"history"));
			Phistory.load(FIShistory);
			JPAutenticacion.setBaseDatos(Phistory.getProperty("database"));
			JPAutenticacion.setUsuario(Phistory.getProperty("user"));
			history =true;
			FIShistory.close();
			FIShistory = null;
			Phistory = null;
			
		} catch (FileNotFoundException e1) {}
		catch (IOException e1) {}
        
        JFConexion.getContentPane().add(new JPanel(), BorderLayout.WEST);
        JFConexion.getContentPane().add(JPsur, BorderLayout.SOUTH);
        JFConexion.getContentPane().add(JPAutenticacion, BorderLayout.CENTER);

        JPAutenticacion.setVisible(true);
        
        JFConexion.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JFConexion.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { Run.salir(); }
        });

        JFConexion.pack();
        JFConexion.setLocation(
                (ClientConst.MAX_WIN_SIZE_WIDTH / 2) - JFConexion.getWidth() / 2,
                (ClientConst.MAX_WIN_SIZE_HEIGHT / 2) - JFConexion.getHeight() / 2);
        JFConexion.setVisible(true);
        if (history) 
        	JPAutenticacion.getJPFclave().requestFocus();
    }

    private boolean conexion() {
   	
            SocketConnector connect;
            JBconectar.setEnabled(false);
            JFConexion.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				PackageToXML packageXML = new PackageToXML();
				HeadersValidator valid = new HeadersValidator();
				packageXML.addArrivePackageistener(valid);
				connect = new SocketConnector(ConfigFile.getHost(),
						  ConfigFile.getServerport(),packageXML);
				
	            connect.start();
	            
	            GenericParameters.removeParameter("userLogin");
	            GenericParameters.addParameter("userLogin",JPAutenticacion.getUsuario());
	            SocketChannel socket = SocketConnector.getSock();
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            String password = new String(JPAutenticacion.getClave());
	    		md.update(password.getBytes());
	    		byte[] bytes = md.digest();
	    		String md5 ="";
	    		for (byte b : bytes) {
	    			int sec = (b & 0xff);
	    			if ( sec<10 ) {
	    				md5+="0";	
	    			}
					md5+= Integer.toHexString( sec );
	    		}
	            SocketWriter.writing(socket,
				                    SendCNX.getPackage(
				                            JPAutenticacion.getBaseDatos(),
				                            JPAutenticacion.getUsuario(),md5.toCharArray()));
	            md = null;
			} catch (ConnectException CEe){
				JOptionPane.showMessageDialog(
	                    JFConexion,Language.getWord("ERR_CONNECT")+"\n"+
	                    Language.getWord("HOST")+" "+ConfigFile.getHost()+"\n"+
	                    Language.getWord("PORT")+" "+ConfigFile.getServerport(),
	                    Language.getWord("ERR_TITLE_CONNECT"),
	                    JOptionPane.ERROR_MESSAGE,
	                    new ImageIcon(this.getClass().getResource("/icons/ico_database.png")));
				JBconectar.setEnabled(true);
				JFConexion.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}catch (UnresolvedAddressException e) {
				JOptionPane.showMessageDialog(
	                    JFConexion,
	                    Language.getWord("ERR_UNRESOLVED_ADDRESS")+"\n"+
	                    Language.getWord("HOST")+" "+ConfigFile.getHost()+ 
	                    Language.getWord("PORT")+" "+ConfigFile.getServerport(),
	                    Language.getWord("ERR_TITLE_CONNECT"),
	                    JOptionPane.ERROR_MESSAGE,
	                    new ImageIcon(this.getClass().getResource("/icons/ico_database.png")));
				JBconectar.setEnabled(true);
				JFConexion.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			
			/*
			 * Capturemos para el historial 
			 */
			
			try {
				FileOutputStream FOShistory = new FileOutputStream(new File(ClientConst.CONF+"history"));
				String database="database="+JPAutenticacion.getBaseDatos()+"\n";
				String user="user="+JPAutenticacion.getUsuario()+"\n";
				FOShistory.write(database.getBytes());
				FOShistory.write(user.getBytes());
				FOShistory.close();
				FOShistory=null;
			} catch (FileNotFoundException e) {}
			catch (IOException e) {}
			
            return true;
    }
    
    public static void dispose(){
        JFConexion.dispose();
    }
    
    public static void setEnabled() {
    	JBconectar.setEnabled(true);
    }
    
    public static void setCursorState(int state) {
    	JFConexion.setCursor(Cursor.getPredefinedCursor(state));
    }
}
