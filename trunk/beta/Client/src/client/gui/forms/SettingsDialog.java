package client.gui.forms;

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
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import client.misc.settings.ConfigFileHandler;
import client.misc.settings.ConfigFileNotLoadException;

/**
 * FirstDialog.java Creado el 04-ago-2004
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
 * Esta clase tiene como fin realizar la creación del archivo de configuración,
 * en el caso de que no exista. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class SettingsDialog extends JDialog {
	
	private static final long serialVersionUID = 7799638277510459773L;
	private JTextField JTFHost;
    private JTextField JTCash;
    private JTextField JTFPort;
    private JComboBox JCBLang;
    private JComboBox JCBLogs;
    
    public static final int CREATE = 0;
    
    public static final int EDIT = 1;
    
    public boolean noFile = true;
    
    /**
     * Este constructor ensambla toda el cuadro de dialogo de configuracion
     * 
     * @param parent
     *            Forma a la que pertenece el dialogo
     */

    public SettingsDialog(JFrame parent,final int flag) {
    	
        super(parent, true);
        this.setTitle("Configuración");
        this.setResizable(false);
        this.setLocationByPlatform(true);
        this.setAlwaysOnTop(true);
        
        String host = "localhost";
        String port = "9117";
        String cash = "CA001";
        String currentLanguage = "es_CO";
        String currentLogMode = "Default";
        
        if (flag == EDIT) {
        	try {
        		noFile = false;
        		ConfigFileHandler.loadSettings();        		
        		host = ConfigFileHandler.getHost();
        		port = String.valueOf(ConfigFileHandler.getServerPort());
        		cash = ConfigFileHandler.getCash();
        		currentLanguage = ConfigFileHandler.getLanguage();
        		currentLogMode = ConfigFileHandler.getLogMode();
        	} catch (ConfigFileNotLoadException e1) {
        		e1.printStackTrace();
        	}
        }
        
        Object[] items = { "es_CO", "es_ES", "en_US"};
        
        JPanel JPlabels = new JPanel();
        JPanel JPfields = new JPanel();
        JPlabels.setLayout(new GridLayout(5, 1));
        JPfields.setLayout(new GridLayout(5, 1));

        JLabel JLLang = new JLabel("Idioma: ");
        JPanel JPLLang = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLLang.add(JLLang);
        
        JLabel JLBox = new JLabel("Terminal: ");
        JPanel JPLBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLBox.add(JLBox);        
        
        JLabel JLHost = new JLabel("Host: ");
        JPanel JPLHost = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLHost.add(JLHost);
        
        JLabel JLPort = new JLabel("Puerto: ");
        JPanel JPLPort = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLPort.add(JLPort);
                
        JLabel JLLog  = new JLabel("Tipo de Log:");
        JPanel JPLog = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLog.add(JLLog);     
        
        JPlabels.add(JPLLang);
        JPlabels.add(JPLBox);
        JPlabels.add(JPLHost);
        JPlabels.add(JPLPort);
        JPlabels.add(JPLog);

        JTFHost = new JTextField(10);
        JTFHost.setText(host);
        JTFHost.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTFHost.transferFocus();
            }
        });

        JTFPort = new JTextField(10);
        JTFPort.setText(port);
        
        JCBLang = new JComboBox(items);
        JCBLang.setRenderer(new FlagRenderer());
        JCBLang.setSelectedItem(currentLanguage);
   
        JTCash = new JTextField(10);
        JTCash.setText(cash);      

        Object[] logItems = { "Default", "Verbose", "VerboseFile", "None" };
        JCBLogs = new JComboBox(logItems);
        JCBLogs.setSelectedItem(currentLogMode);
        
        JPanel JPLang = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLang.add(JCBLang);

        JPanel JPHost = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPHost.add(JTFHost);
       
        JPanel JPPort = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPPort.add(JTFPort);
        
        JPanel JPBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPBox.add(JTCash);    
        
        JPanel JPLogs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLogs.add(JCBLogs);
        
        JPfields.add(JPLang);
        JPfields.add(JPBox);
        JPfields.add(JPHost);
        JPfields.add(JPPort);
        JPfields.add(JPLogs);

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
            	        packingData();            }
        });
        
        JBAccept.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    	packingData();
                }
            }
        });

        JPsouth.add(JBAccept);

        JButton JBCancel = new JButton("Cancelar");
        JBCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //System.exit(0);
            	setVisible(false);
            	if (flag==CREATE) {
            		System.exit(0);
            	}
            }
        });
        JPsouth.add(JBCancel);

        this.getContentPane().add(JPsouth, BorderLayout.SOUTH);
        this.getContentPane().add(JBase, BorderLayout.CENTER);
        this.getContentPane().add(new JPanel(), BorderLayout.WEST);
        this.getContentPane().add(new JPanel(), BorderLayout.EAST);
    }
    
    public String getLog() {
        return JCBLogs.getSelectedItem().toString();
    }
    
    public String getLanguage() {
        String language =  JCBLang.getSelectedItem().toString();
        return language;
    }
    
    public String getBox() {
    	String boxCode = JTCash.getText();
    	return boxCode;
    }
    

    /**
     * Este metodo retorna la ip o el nombre del host correspondiente al
     * servidor
     * 
     * @return algo
     */
    public String getHost() {
        return JTFHost.getText();
    }

    /**
     * Este metodo retorna el puerto correspondiente al servidor
     * 
     * @return algo
     */
    public String getPort() {
        return JTFPort.getText();
    }
    
    /**
     * Este metodo limpia el campo correspondiente al puerto del servidor
     * 
     */
    public void cleanPort() {
        JTFPort.setText("");
        JTFPort.requestFocus();
    }
    
    /**
     * Este metodo limpia el campo correspondiente al servidor
     * 
     */
    public void cleanServer() {
        JTFHost.requestFocus();
    }  

    
    private boolean packingData() {
    
    	String serverAddress = getHost();
    	String serverPort    = getPort();  
    	String language      = getLanguage();
    	String logType       = getLog();
    	String pos           = getBox();    
   
    	if (serverAddress.length() < 1) {
    		JOptionPane.showMessageDialog(this,"El campo servidor se encuentra vacio!\nPor favor, ingrese un valor.");
    		cleanServer();
    		return false;
    	}

    	if (!isNumber(serverPort)) {
    		JOptionPane.showMessageDialog(this,"El campo puerto debe contener un valor numerico!\nPor favor, corrija el valor ingresado.");
    		cleanPort();
    		return false;     	
    	}

    	if (serverPort.length() < 1) {
    		serverPort = "9117";
    	}     

    	callConfigFile(serverAddress,serverPort,language,logType,pos);

    	if(noFile) {
    		callConnection();
    	}
    	dispose();

    	return true;
    }
      
    
    private void callConfigFile(String serverAddress, String serverPort, String language, String logType, String pos) {
        ConfigFileHandler.buildNewFile(serverAddress,serverPort,language, logType,pos);
    }

    private void callConnection()  {
        new Connection();
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
    final class FlagRenderer extends JLabel implements ListCellRenderer
    {
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
