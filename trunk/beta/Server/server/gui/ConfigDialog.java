package server.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeMap;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;


/**
 * ConfigDialog.java Creado el 04-ago-2004
 * 
 * Este archivo es parte del cliente E-Maku <A
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
 * Esta clase tiene como fin realizar la creación del archivo de configuración,
 * en el caso de que no exista. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class ConfigDialog extends JDialog {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 7799638277510459773L;
    
    private JComboBox JCBLang;
    
    private JTextField JTPortForClients;

    private JTextField JTPortForAdmin;
    
    private JTextField JTMaxClients;
    
    private JComboBox JCBLogs;  
    
    private JTextField JTCompany;  

    private JComboBox JTDriver;
    
    private JTextField JTUrl;
    
    private JTextField JTDBPort;
    
    private JTextField JTUser;
    
    private JPasswordField JTPasswd; 
    
    public boolean noFile = true;
    
	public String lang = "es_CO";
	
	public String clientPort = "9117";
	
	public String adminPort = "28124";
	
	public String maxClients = "500";
	
	public String logType = "Verbose";
	
	public String company = "mi_empresa";
	
	public String jdbcDriver = "org.postgresql.Driver";
	
	public String url = "jdbc:postgresql://localhost:5432/mi_empresa";
	
    public String user = "emaku";
    
	public String password = "";
    
    /**
     * Este constructor ensambla toda el cuadro de dialogo de configuracion
     * 
     * @param parent
     *            Forma a la que pertenece el dialogo
     */

    public ConfigDialog(JFrame parent) {
    	super(parent,true);
    	
        this.setTitle("Configuración");
        this.setResizable(false);
        
        Object[] items = { "es_CO", "es_ES", "en_US"};
        
        JPanel JPlabels = new JPanel();
        JPanel JPfields = new JPanel();
        JPlabels.setLayout(new GridLayout(11, 1));
        JPfields.setLayout(new GridLayout(11, 1));

        JLabel JLLang = new JLabel("Idioma: ");
        JPanel JPLLang = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLLang.add(JLLang);
        
        JLabel JLPortForClients = new JLabel("Puerto para Clientes: ");
        JPanel JPPFClients = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPPFClients.add(JLPortForClients);
        
        JLabel JLPortForAdmins = new JLabel("Puerto para Admins: ");
        JPanel JPPortForAdmins = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPPortForAdmins.add(JLPortForAdmins);
        
        JLabel JLMaxClients = new JLabel("Max Número de Clientes: ");
        JPanel JPMaxClients = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPMaxClients.add(JLMaxClients);        
        
        JLabel JLLog  = new JLabel("Tipo de Log:");
        JPanel JPJLLog = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJLLog.add(JLLog);     
        
        JLabel JLCompany  = new JLabel("Empresa:");
        JPanel JPJLCompany = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJLCompany.add(JLCompany);     
        
        JLabel JLDriver  = new JLabel("Driver:");
        JPanel JPJLDriver = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJLDriver.add(JLDriver);     
        
        JLabel JLUrl  = new JLabel("Servidor DB:");
        JPanel JPJLUrl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJLUrl.add(JLUrl);                
        
        JLabel JLDBPort  = new JLabel("Puerto DB:");
        JPanel JPJLDBPort = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJLDBPort.add(JLDBPort);                
        
        JLabel JLUser  = new JLabel("Usuario:");
        JPanel JPJLUser = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJLUser.add(JLUser);                

        JLabel JLPasswd  = new JLabel("Clave:");
        JPanel JPJLPasswd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJLPasswd.add(JLPasswd);                  
        
        JPlabels.add(JPLLang);
        JPlabels.add(JPPFClients);
        JPlabels.add(JPPortForAdmins);
        JPlabels.add(JPMaxClients);
        JPlabels.add(JPJLLog);
        JPlabels.add(JPJLCompany);
        JPlabels.add(JPJLDriver);
        JPlabels.add(JPJLUrl);
        JPlabels.add(JPJLDBPort);
        JPlabels.add(JPJLUser);
        JPlabels.add(JPJLPasswd);

        JCBLang = new JComboBox(items);
        JCBLang.setRenderer(new FlagRenderer());
        JCBLang.setSelectedItem(lang);

        JPanel JPJCLang = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJCLang.add(JCBLang);

        JTPortForClients = new JTextField(10);
        JTPortForClients.setText(clientPort);
        
        JTPortForClients.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTPortForClients.transferFocus();
            }
        });

        JPanel JPJTPortForClients = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJTPortForClients.add(JTPortForClients);

        JTPortForAdmin = new JTextField(10);
        JTPortForAdmin.setText(adminPort);
          
        JPanel JPJTPortForAdmin = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJTPortForAdmin.add(JTPortForAdmin);

        JTMaxClients = new JTextField(10);
        JTMaxClients.setText(maxClients);      

        JPanel JPJTMaxClients = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJTMaxClients.add(JTMaxClients);
        
        Object[] logItems = { "Default", "Verbose", "VerboseFile", "None" };
        JCBLogs = new JComboBox(logItems);
        JCBLogs.setSelectedItem(logType);

        JPanel JPCBLogs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPCBLogs.add(JCBLogs);
        
        JTCompany = new JTextField(10);
        JTCompany.setText(company);
        
        JPanel JPJTCompany = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJTCompany.add(JTCompany);

        Object[] driverItems = { "PostgreSQL", "Mysql"};
        JTDriver = new JComboBox(driverItems);        
        JTDriver.setSelectedItem(jdbcDriver);
        
        JPanel JPJTDriver = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJTDriver.add(JTDriver);
        
        JTUrl = new JTextField(10);
        JTUrl.setText("localhost");
                
        JPanel JPJTUrl= new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJTUrl.add(JTUrl);
        
        JTDBPort = new JTextField(10);
        JTDBPort.setText("5432");        

        JPanel JPJTDBPort= new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJTDBPort.add(JTDBPort);

        JTUser = new JTextField(10);
        JTUser.setText(user);
        
        JPanel JPJTUser = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJTUser.add(JTUser);

        JTPasswd = new JPasswordField(10);
        JTPasswd.setText(password);
        
        JPanel JPJTPasswd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPJTPasswd.add(JTPasswd);
        
        JPfields.add(JPJCLang);
        JPfields.add(JPJTPortForClients);
        JPfields.add(JPJTPortForAdmin);
        JPfields.add(JPJTMaxClients);
        JPfields.add(JPCBLogs);        
        JPfields.add(JPJTCompany);
        JPfields.add(JPJTDriver);
        JPfields.add(JPJTUrl);        
        JPfields.add(JPJTDBPort);
        JPfields.add(JPJTUser);
        JPfields.add(JPJTPasswd);    

        JPanel JBase = new JPanel();
        JBase.setLayout(new BorderLayout());
        JBase.add(new JPanel(), BorderLayout.NORTH);
        JBase.add(JPlabels, BorderLayout.WEST);
        JBase.add(JPfields, BorderLayout.CENTER);
        JBase.add(new JPanel(), BorderLayout.SOUTH);
        
        JPanel JPsouth = new JPanel();
        JPsouth.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton JBAccept = new JButton("Aceptar");
        
        JBAccept.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	if (packingData())
                    setVisible(false);
            }
        });
        
        JBAccept.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	if (packingData())
                        setVisible(false);
                }
            }
        });

        JPsouth.add(JBAccept);

        JButton JBCancel = new JButton("Cancelar");
        JBCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPsouth.add(JBCancel);

        add(JPsouth, BorderLayout.SOUTH);
        add(JBase, BorderLayout.CENTER);
        add(new JPanel(), BorderLayout.WEST);
        add(new JPanel(), BorderLayout.EAST);
    }
    
    public String getLanguage() {
        return lang;
    }

    public String getClientPort() {
        return clientPort;
    }

    public String getAdminPort() {
        return adminPort;
    }
    
    public String getMaxClients() {
        return maxClients;
    }
    
    public String getLogType() {
    	return logType;
    }

    public String getCompany() {
        return company;
    }
    
    public String getJDBCDriver() {
    	return jdbcDriver;
    }
    
    public String getUrl() {
        return url;
    }


    public String getUser() {
        return user;
    }

    public String getPasswd() {
        return password;
    }
    
    /**
     * Este metodo limpia un campo del formulario
     * 
     */
    public void cleanField(JTextField field) {
        field.setText("");
        field.requestFocus();
    }
    
    private boolean packingData() {

    	lang = JCBLang.getSelectedItem().toString();

    	clientPort = JTPortForClients.getText();
    	
    	if (clientPort.length() > 0) {

    		if (!isNumber(clientPort)) {
    			JOptionPane.showMessageDialog(this,"El puerto del cliente debe ser de tipo numérico!","Error!",
    					JOptionPane.ERROR_MESSAGE);
    			return false;
    		}

    	} else {
                 clientPort = "9117";
    	}    	
    	
    	
    	adminPort = JTPortForAdmin.getText();
    	
    	if (adminPort.length() > 0) {	
    		if (!isNumber(adminPort)) {
    			JOptionPane.showMessageDialog(this,"El puerto del admin debe ser de tipo numérico!","Error!",
    					JOptionPane.ERROR_MESSAGE);
    			return false;
    		}
    	} else {
                adminPort = "28124";
    	}
    	
    	maxClients = JTMaxClients.getText();

    	if (maxClients.length() > 0) {
    		if (!isNumber(maxClients)) {
    			JOptionPane.showMessageDialog(this,"El número máximo de clientes debe ser de tipo numérico!","Error!",
    					JOptionPane.ERROR_MESSAGE);
    			return false;
    		}
    	} else {
                maxClients = "500";           
    	}
    	
    	logType = JCBLogs.getSelectedItem().toString();
    	
    	company = JTCompany.getText();
    	
    	if (company.length() > 0) {
    		if (company.indexOf(" ") != -1) {
    			JOptionPane.showMessageDialog(this,"El nombre de la empresa no puede incluir espacios","Error!",
    					JOptionPane.ERROR_MESSAGE);
    			return false;
    		}
    	} else {                           
			    JOptionPane.showMessageDialog(this,"El campo del nombre de la empresa se encuentra vacio!","Error!",
				  	    JOptionPane.ERROR_MESSAGE);
			    return false;
    	}  	
    	
    	String dbEngine = JTDriver.getSelectedItem().toString();
    	
    	String urlEngine = "";
    	
    	if (dbEngine.equals("PostgreSQL")) {
    	    jdbcDriver = "org.postgresql.Driver";
    	    urlEngine = "postgresql";
    	} else if (dbEngine.equals("Mysql")) {
    		     jdbcDriver = "com.mysql.jdbc.Driver";
    		     urlEngine = "mysql";
    	}
    	
    	String server = JTUrl.getText();

    	if (server.length()==0) {
    		JOptionPane.showMessageDialog(this,"Ingrese el nombre o la dirección ip del servidor de bases de datos","Error!",
    				JOptionPane.ERROR_MESSAGE);
    		return false;
    	}
    	
    	String enginePort = JTDBPort.getText();

    	if (enginePort.length() > 0) {
    	    if (!isNumber(enginePort)) {
    		     JOptionPane.showMessageDialog(this,"El puerto del servidor de bases de datos debe ser numérico!","Error!",
    			   	         JOptionPane.ERROR_MESSAGE);    	
    		     return false;    	
    	     }
    	}else {
                enginePort = "5432";           
    	}
    	
        url = "jdbc:" + urlEngine + "://" + server + ":" + enginePort + "/" + company;
    	
    	user = JTUser.getText();

    	if (user.length()==0) {
    		JOptionPane.showMessageDialog(this,"Ingrese el nombre del usuario de la base de datos","Error!",
    				JOptionPane.ERROR_MESSAGE);    	
    		return false;    	
    	}
        
    	password   = new String(JTPasswd.getPassword());

    	if (password.length()==0) {
    		JOptionPane.showMessageDialog(this,"Ingrese la clave del usuario de la base de datos","Error!",
    				JOptionPane.ERROR_MESSAGE);
    		return false;
    	}

        return true;
    }
          
    public boolean isNumber(String s) {
        for(int i = 0; i < s.length(); i++) {
          char c = s.charAt(i);
          if(!Character.isDigit(c))
              return false;
         }
        return true;
      }

}
   
    /**
     * Esta clase se encarga de adicionar las banderas en el combo de idiomas
     * 
     *       
     * @author Julien Ponge
     */
    final class FlagRenderer extends JLabel implements ListCellRenderer {
    	
        private static final long serialVersionUID = 3832899961942782769L;

        /** Cache de Iconos. */    
        private TreeMap<String, ImageIcon> icons = new TreeMap<String, ImageIcon>();
   
        /** Cache de iconos grises. */
        private TreeMap<String, ImageIcon> grayIcons = new TreeMap<String, ImageIcon>();

        public FlagRenderer()
        {
            setOpaque(true);
        }

        /**
         * Retorna una celda personalizada
         * 
         * @param list La lista
         * @param value El objeto
         * @param index El indice
         * @param isSelected verdadero si esta seleccionada
         * @param cellHasFocus Descripcion del parametro
         * @return La celda
         */
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
        {	
            // We put the label
            String langCode = (String) value;
            
            if (langCode.equals("es_CO"))
                setText("Español (CO)");
            else {
                  if (langCode.equals("es_ES"))
                      setText("Español (ES)");
                  else
                	  if (langCode.equals("en_US"))
                          setText("English (US)");
            }
            
            if (isSelected) {
                setForeground(list.getSelectionForeground());
                setBackground(list.getSelectionBackground());
            }
            else {
                setForeground(list.getForeground());
                setBackground(list.getBackground());
            }
            // Colocamos el icono de la bandera

            if (!icons.containsKey(langCode))
            {
                ImageIcon icon;
                icon = new ImageIcon(this.getClass().getResource("/icons/ico_" + langCode + ".png"));
                icons.put(langCode, icon);
                icon = new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
                grayIcons.put(langCode, icon);
            }
            if (isSelected || index == -1)
                setIcon((ImageIcon) icons.get(langCode));
            else
                setIcon((ImageIcon) grayIcons.get(langCode));

            return this;
        }
}
