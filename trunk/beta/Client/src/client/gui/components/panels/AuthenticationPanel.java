package client.gui.components.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import common.misc.language.Language;

/**
 * AuthenticationPanel.java Creado el 03-ago-2004
 * 
 * Este archivo es parte de eMaku <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * eMaku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * eMaku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>Informacion de la clase<br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda </A>
 */

public class AuthenticationPanel extends JPanel {

	private static final long serialVersionUID = 8944845098293887709L;
	private JTextField dataBaseTextField;
    private JTextField userTextField;
    private JPasswordField passwordTextField;
    public static final int ALL = 0;
    public static final int PASSWORD = 1;

    /**
     * Este constructor crea una nueva instancia de la clase AuthenticacionPanel segun sus
     * parametros
     * 
     * @param format
     *            Parametro que define los Objetos que sean Desplegados.
     */

    public AuthenticationPanel(int format) {

        this.setLayout(new BorderLayout());
        
        int grid = 3;

        if (format == PASSWORD) {
            grid = 1;
        }

        JPanel labelsPanel = new JPanel();
        JPanel fieldsPanel = new JPanel();
        
        labelsPanel.setLayout(new GridLayout(grid, 1));
        fieldsPanel.setLayout(new GridLayout(grid, 1));

        if (format == AuthenticationPanel.ALL) {

            JLabel dataBaseLabel = new JLabel(Language.getWord("BD"));
            JLabel userLabel = new JLabel(Language.getWord("USER"));
            labelsPanel.add(dataBaseLabel);
            labelsPanel.add(userLabel);

            JPanel dataBasePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            dataBaseTextField = new JTextField(10);
            
            dataBaseTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dataBaseTextField.transferFocus();
                }
            });
            dataBasePanel.add(dataBaseTextField);

            JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            userTextField = new JTextField(10);
            userTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    userTextField.transferFocus();
                }
            });
            userPanel.add(userTextField);

            fieldsPanel.add(dataBasePanel);
            fieldsPanel.add(userPanel);
        }

        JLabel JLclave = new JLabel(Language.getWord("PASS"));
        labelsPanel.add(JLclave);

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordTextField = new JPasswordField(10);
        passwordTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                passwordTextField.transferFocus();
            }
        });
        passwordPanel.add(passwordTextField);
        
        fieldsPanel.add(passwordPanel);
        
        JPanel internalPanel = new JPanel();
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        internalPanel.setLayout(new BorderLayout());
        
        internalPanel.add(labelsPanel,BorderLayout.WEST);
        internalPanel.add(fieldsPanel,BorderLayout.CENTER);
        mainPanel.add(internalPanel);
        
        this.add(new JPanel(), BorderLayout.EAST);
        this.add(new JPanel(), BorderLayout.WEST);
        this.add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * @return Este metodo retorna el nombre de la base de datos del campo de
     *         texto base de datos
     */

    public String getDataBase() {
        return dataBaseTextField.getText();
    }

    public void setDataBase(String value ) {
    	dataBaseTextField.setText(value);
    }
    /**
     * @return Este metodo retorna el nombre de usuario del campo de texto
     *         Usuario
     */

    public String getUser() {
        return userTextField.getText();
    }
    
    public void setUser(String value) {
    	userTextField.setText(value);
    }

    /**
     * @return Este metodo retorna la contrase�a del campo de texto base de
     *         Clave
     */
    public char[] getPassword() {
        return passwordTextField.getPassword();
    }

    public JTextField getDataBaseTextField() {
        return dataBaseTextField;
    }

	public JPasswordField getPasswordTextField() {
		return passwordTextField;
	}
}