package jmclient.gui.components.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import jmlib.miscelanea.idiom.Language;

/**
 * PAutentication.java Creado el 03-ago-2004
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
 * Informacion de la clase <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class PAutentication extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8944845098293887709L;

	private JTextField JTFBaseDatos;

    private JTextField JTFusuario;

    private JPasswordField JPFclave;

    public static final int ALL = 0;

    public static final int PASSWORD = 1;

    /**
     * Este constructor crea una nueva instancia del PAutenticacion segun sus
     * parametro
     * 
     * @param Format
     *            Parametro que define los Objetos que sean Desplegados.
     */

    public PAutentication(int Format) {

        this.setLayout(new BorderLayout());
        
        int grid = 3;

        if (Format == PASSWORD) {
            grid = 1;
        }

        JPanel JPetiquetas = new JPanel();
        JPanel JPfields = new JPanel();
        
        JPetiquetas.setLayout(new GridLayout(grid, 1));
        JPfields.setLayout(new GridLayout(grid, 1));

        if (Format == PAutentication.ALL) {

            JLabel JLBaseDatos = new JLabel(Language.getWord("BD"));
            JLabel JLusuario = new JLabel(Language.getWord("USER"));
            JPetiquetas.add(JLBaseDatos);
            JPetiquetas.add(JLusuario);

            JPanel JPBaseDatos = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTFBaseDatos = new JTextField(10);
            
            JTFBaseDatos.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JTFBaseDatos.transferFocus();
                }
            });
            JPBaseDatos.add(JTFBaseDatos);

            JPanel JPusuario = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTFusuario = new JTextField(10);
            JTFusuario.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JTFusuario.transferFocus();
                }
            });
            JPusuario.add(JTFusuario);

            JPfields.add(JPBaseDatos);
            JPfields.add(JPusuario);
        }

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

        this.add(JPetiquetas, BorderLayout.WEST);
        this.add(JPfields, BorderLayout.CENTER);

    }

    /**
     * @return Este metodo retorna el nombre de la base de datos del campo de
     *         texto base de datos
     */

    public String getBaseDatos() {
        return JTFBaseDatos.getText();
    }

    public void setBaseDatos(String value ) {
    	JTFBaseDatos.setText(value);
    }
    /**
     * @return Este metodo retorna el nombre de usuario del campo de texto
     *         Usuario
     */

    public String getUsuario() {
        return JTFusuario.getText();
    }
    
    public void setUsuario(String value) {
    	JTFusuario.setText(value);
    }

    /**
     * @return Este metodo retorna la contraseña del campo de texto base de
     *         Clave
     */
    public char[] getClave() {
        return JPFclave.getPassword();
    }

    public JTextField getJTFBaseDatos() {
        return JTFBaseDatos;
    }

	public JPasswordField getJPFclave() {
		return JPFclave;
	}
}