package jmclient.gui.formas;

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
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jmclient.Run;
import jmclient.comunicaciones.SendCNX;
import jmclient.control.ValidHeaders;
import jmclient.gui.components.panels.PAutentication;
import jmclient.miscelanea.JMClientCons;
import jmclient.miscelanea.configuracion.ConfigFile;
import jmlib.comunicaciones.PackageToXML2;
import jmlib.comunicaciones.SocketConnect;
import jmlib.comunicaciones.WriteSocket;
import jmlib.miscelanea.idiom.Language;
import jmlib.miscelanea.parameters.GenericParameters;

/**
 * Conexion.java Creado el 03-ago-2004
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
 * Esta clase se encarga de la parte de autenticacion del jmlib
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */

public class Conexion {

    private static JFrame JFConexion;
    private PAutentication JPAutenticacion;
    private static JButton JBconectar;
    
    public Conexion() {
                
        JFConexion = new JFrame(Language.getWord("TITLE-CONEC"));
        JFConexion.setResizable(false);
        JFConexion.getContentPane().setLayout(new BorderLayout());

        JPAutenticacion = new PAutentication(PAutentication.ALL);
       
        JPanel JPsur = new JPanel();
        JBconectar = new JButton();
        JBconectar.setIcon(new ImageIcon(this.getClass().getResource(
                "/icons/ico_conectar.png")));
        JBconectar.setToolTipText(Language.getWord("CONECTAR"));

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
        
        JBcancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent AEe) { Run.salir(); }
        });
        
        JPsur.add(JBconectar);
        JPsur.add(JBcancelar);

        /*
         * Mostrando un historial
         */
        FileInputStream FIShistory = null;
        Properties Phistory = new Properties();
        boolean history = false;
         try {
			FIShistory = new FileInputStream(new File(JMClientCons.CONF+"history"));
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
                (JMClientCons.MAX_WIN_SIZE_WIDTH / 2) - JFConexion.getWidth() / 2,
                (JMClientCons.MAX_WIN_SIZE_HEIGHT / 2) - JFConexion.getHeight() / 2);
        JFConexion.setVisible(true);
        if (history) 
        	JPAutenticacion.getJPFclave().requestFocus();
    }

    private boolean conexion() {
   	
            SocketConnect connect;
            JBconectar.setEnabled(false);
            JFConexion.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				PackageToXML2 packageXML = new PackageToXML2();
				ValidHeaders valid = new ValidHeaders();
				packageXML.addArrivePackageistener(valid);
				connect = new SocketConnect(ConfigFile.getHost(),
						  ConfigFile.getServerport(),packageXML);
				
	            connect.start();
	            
	            GenericParameters.removeParameter("userLogin");
	            GenericParameters.addParameter("userLogin",JPAutenticacion.getUsuario());
	            SocketChannel socket = SocketConnect.getSock();
	            WriteSocket.writing(socket,
				                    SendCNX.getPackage(
				                            JPAutenticacion.getBaseDatos(),
				                            JPAutenticacion.getUsuario(),
				                            JPAutenticacion.getClave()));
	             
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
			}
			
			/*
			 * Capturemos para el historial 
			 */
			
			try {
				FileOutputStream FOShistory = new FileOutputStream(new File(JMClientCons.CONF+"history"));
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
