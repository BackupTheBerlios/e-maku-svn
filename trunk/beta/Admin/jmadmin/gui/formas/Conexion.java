package jmadmin.gui.formas;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.nio.channels.SocketChannel;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import jmadmin.comunicacion.SendCNX;
import jmadmin.miscelanea.JMAdminCons;
import jmadmin.miscelanea.configuracion.ConfigFile;
import jmlib.comunicaciones.SocketConnect;
import jmlib.comunicaciones.WriteSocket;
import jmlib.miscelanea.idiom.Language;

/**
 * Conexion.java Creado el 18-ago-2004
 * 
 * Este archivo es parte de JMServerII
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class Conexion extends JInternalFrame {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -472465950790994724L;
	private JTextField JTFusuario;
    private JPasswordField JPFclave;
    
    public Conexion() {
        super(Language.getWord("TITLE-CONEC"));
        this.setBounds((MainWindow.getAncho()/2)-110,
                		 (MainWindow.getAlto()/2)-60,
                		 220,
                		 120);
        
        this.setMaximizable(false);
        this.getContentPane().setLayout(new BorderLayout());
        JPanel JPDatos = new JPanel(new BorderLayout());
        JPanel JPetiquetas = new JPanel();
        JPanel JPfields = new JPanel();
        
        JPetiquetas.setLayout(new GridLayout(2, 1));
        JPfields.setLayout(new GridLayout(2, 1));
        
        JLabel JLusuario = new JLabel(Language.getWord("USER"));
        JPetiquetas.add(JLusuario);

        JPanel JPusuario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTFusuario = new JTextField(10);
        JTFusuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTFusuario.transferFocus();
            }
        });
        JPusuario.add(JTFusuario);

        JPfields.add(JPusuario);
        
        JLabel JLclave = new JLabel(Language.getWord("PASS"));
        JPetiquetas.add(JLclave);

        JPanel JPclave = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPFclave = new JPasswordField(10);
        JPFclave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPFclave.transferFocus();
            }
        });
        JPclave.add(JPFclave);
        
        JPfields.add(JPclave);

        JPDatos.add(JPetiquetas, BorderLayout.WEST);
        JPDatos.add(JPfields, BorderLayout.CENTER);

        JPanel JPsur = new JPanel();
        JButton JBconectar = new JButton();
        JBconectar.setIcon(new ImageIcon(this.getClass().getResource(
                "/icons/ConectDB.png")));
        JBconectar.setToolTipText(Language.getWord("CONECTAR"));

        JButton JBcancelar = new JButton();
        JBcancelar.setIcon(new ImageIcon(this.getClass().getResource(
                "/icons/ico_cancelar.gif")));
        JBcancelar.setToolTipText(Language.getWord("CANCEL"));

        JBconectar.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) { conexion(); }
            }
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        });
        JBconectar.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) { conexion(); }
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });
        
        JBcancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                salir();

            }
        });
        
        JPsur.add(JBconectar);
        JPsur.add(JBcancelar);

        this.getContentPane().add(new JPanel(),BorderLayout.WEST);
        this.getContentPane().add(JPDatos,BorderLayout.CENTER);
        this.getContentPane().add(JPsur, BorderLayout.SOUTH);
        MainWindow.addInternalFrame(this);
        this.show();
        try {
            this.setSelected(true);
        }
        catch (PropertyVetoException e1) {
            e1.printStackTrace();
        }
    }
    
    private void conexion() {

        try {
            
            SocketConnect connect = 
                	new SocketConnect(JMAdminCons.KeyAdmin,
                					  ConfigFile.getHost(),
									  ConfigFile.getServerport());
            connect.start();
            
            SocketChannel socket = SocketConnect.getSock(JMAdminCons.KeyAdmin);
            WriteSocket.writing(socket,
			                    SendCNX.getPackage(
			                            JTFusuario.getText(),
			                            JPFclave.getPassword()));
        }
        catch (IOException e) {
            
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo establecer\nla comunicacion con el host:\n"+
                    ConfigFile.getHost()+"\n"+"Puerto: " + 
                    ConfigFile.getServerport() +"\n" +
                    "Puede ser que no esta bien\nconfigurado el servidor\n"+
                    "o la red no es accesible.\nVerfique y vuelva a intentarlo",
                    "Error en la conexion",
                    JOptionPane.ERROR_MESSAGE,
                    new ImageIcon(this.getClass().getResource("/icons/ico_conectar.png")));
        }
    }
    private void salir() {
        MainWindow.disposeJIF(this);
    }
}
