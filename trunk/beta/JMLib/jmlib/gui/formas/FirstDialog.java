package jmlib.gui.formas;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.JOptionPane;

import javax.swing.ListCellRenderer;
import javax.swing.JList;

import java.util.TreeMap;
import javax.swing.GrayFilter;
import java.awt.Component;

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
 * Esta clase tiene com fin realizar la creacion del archivo de configuracion,
 * en el caso de que no exista. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class FirstDialog extends JDialog {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 7799638277510459773L;

    private JPIdioma idioma;

    private JPServer server;

    private JPLog log;

    private String key;

    /**
     * Este constructor ensambla toda el cuadro de dialogo de configuracion
     * 
     * @param padre
     *            Forma a la que pertenece el dialogo
     */

    public FirstDialog(JFrame padre, final String key) {

        super(padre, true);
        this.key = key;
        int ancho = (int) Toolkit.getDefaultToolkit().getScreenSize()
                .getHeight();
        int alto = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        this.getContentPane().setLayout(new BorderLayout());
        this.setBounds((ancho / 2) - 115, (alto / 2) - 77, 230, 350);

        this.setTitle("Configuración");
        this.setResizable(false);

        idioma = new JPIdioma();
        server = new JPServer();
        log = new JPLog();

        JPanel JBase = new JPanel();
        JBase.setLayout(new FlowLayout(FlowLayout.CENTER));
        JBase.add(idioma);
        JBase.add(server);
        JBase.add(log);
        
        JPanel JPsur = new JPanel();
        JPsur.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton JBAceptar = new JButton("Aceptar");
        JBAceptar.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	new Boolean (packingData());
            }
        });
        JBAceptar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    	new Boolean (packingData());
                }
            }
        });

        JPsur.add(JBAceptar);

        JButton JBCancelar = new JButton("Cancelar");
        JBCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        JPsur.add(JBCancelar);

        this.getContentPane().add(JPsur, BorderLayout.SOUTH);
        this.getContentPane().add(JBase, BorderLayout.CENTER);
        this.getContentPane().add(new JPanel(), BorderLayout.WEST);
        this.getContentPane().add(new JPanel(), BorderLayout.EAST);

    }
    
    private boolean packingData() {
    
        String serverAddress = server.getHost();
        String serverPort    = server.getPort();  
        String language      = idioma.getIdioma();
        String logType       = log.getLog();
        String pos           = "CA001";    
        
        if (serverAddress.length() < 1) {
            JOptionPane.showMessageDialog(this,"El campo servidor se encuentra vacio!\nPor favor, ingrese un valor.");
            server.cleanServer();
            return false;
         }
        
        if (!isNumber(serverPort)) {
            JOptionPane.showMessageDialog(this,"El campo puerto debe contener un valor numerico!\nPor favor, corrija el valor ingresado.");
            server.cleanPort();
            return false;     	
        }

        if (serverPort.length() < 1) {
    	    serverPort = "9117";
         }     

        try {
    	     callConfigFile(serverAddress,serverPort,language,logType,pos);
             callConexion();
             dispose();
         }
        catch (ClassNotFoundException CNFEe) {
            CNFEe.printStackTrace();
            System.out.println("Exception : " + CNFEe.getMessage());
         }
        catch (NoSuchMethodException NSMEe) {
            NSMEe.printStackTrace();
            System.out.println("Exception : " + NSMEe.getMessage());
         }
        catch (InstantiationException IEe) {
            IEe.printStackTrace();
            System.out.println("Exception : " + IEe.getMessage());
         }
       catch (IllegalAccessException IAEe) {
           IAEe.printStackTrace();
           System.out.println("Exception : " + IAEe.getMessage());
         }
       catch (InvocationTargetException ITEe) {
           ITEe.printStackTrace();
           System.out.println("Exception: " + ITEe.getMessage());
         }    

       return true;
    }
      
    
    private void callConfigFile(String serverAddress, String serverPort, String language, String logType, String pos) throws ClassNotFoundException,
            SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException,
            InstantiationException {
        
        Class[] typeargs = { String.class, String.class, String.class,
                String.class ,String.class};
        
        Object[] args = { serverAddress, serverPort, language, logType, pos };

        Class cls = Class.forName(key + ".miscelanea.configuracion.ConfigFile");
        Method meth = cls.getMethod("New", typeargs);
        meth.invoke(cls.newInstance(), args);
    }

    private void callConexion() throws ClassNotFoundException,
            SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        
        Class[] typeargs = {};
        Object[] args = {};
        Class cls = Class.forName(key + ".gui.formas.Conexion");
        Constructor cons = cls.getConstructor(typeargs);
        cons.newInstance(args);
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
 * Esta clase es la que muestra el panel de configuracion de idioma
 */

class JPIdioma extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2855160787462346245L; 
	private JComboBox JCBLang;

    /**
     * Este el constructor que emsambla todos los componentes del panel de
     * idioma
     */

    public JPIdioma() { 	
        super();
        Object[] items = { "es_CO", "es_ES", "en_US"};
        this.setLayout(new BorderLayout());
        JPanel JPcentro = new JPanel();
        JPanel JPCombo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JCBLang = new JComboBox(items);
        JCBLang.setRenderer(new FlagRenderer());
        JCBLang.setSelectedIndex(0);     
        
        JPCombo.add(JCBLang);
        JPcentro.add(JPCombo);
        this.add(JPcentro, BorderLayout.CENTER);
        
        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder title = BorderFactory.createTitledBorder(etched,"Idioma");
        title.setTitleJustification(TitledBorder.LEFT);
        this.setBorder(title);
        
    }

    /**
     * Este metodo retorna el idioma seleccionado del panel
     * 
     * @return Idioma seleccionado
     */

    public String getIdioma() {
           String language =  JCBLang.getSelectedItem().toString();
           return language;
    }
}

/**
 * Esta clase es la que muestra el panel de configuracion del Log
 */

class JPLog extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 679979184107987977L;
	private JComboBox JCBLogs;

    /**
     * Este el constructor que emsambla todos los componentes del panel del Log
     */

    public JPLog() {
        super();
        Object[] items = { "Default", "Verbose", "VerboseFile", "None" };
        this.setLayout(new BorderLayout());
        JPanel JPcentro = new JPanel();
        JLabel JLTipo = new JLabel("Tipo: ");
        JPcentro.add(JLTipo);
        JPanel JPCombo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCBLogs = new JComboBox(items);
        JPCombo.add(JCBLogs);
        JPcentro.add(JPCombo);
        this.add(new JPanel(), BorderLayout.NORTH);
        this.add(JPcentro, BorderLayout.CENTER);
        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder title = BorderFactory.createTitledBorder(etched,"Log");
        title.setTitleJustification(TitledBorder.LEFT);
        this.setBorder(title);
    }

    /**
     * Este metodo retorna el tipo de log seleccionado
     * 
     * @return Log seleccionado
     */
    public String getLog() {
        return JCBLogs.getSelectedItem().toString();
    }
}

/**
 * Esta clase es la que muestra el panel de configuracion del Servidor
 */

class JPServer extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5972294764788473170L;

	private JTextField JTFHost;

    private JTextField JTFPort;

    /**
     * Este el constructor que emsambla todos los componentes del panel Servidor
     */

    public JPServer() {
        super();

        this.setLayout(new BorderLayout());
        JPanel JPetiquetas = new JPanel();
        JPanel JPfields = new JPanel();

        JPetiquetas.setLayout(new GridLayout(2, 1));
        JPfields.setLayout(new GridLayout(2, 1));

        JLabel JLHost = new JLabel("Host: ");
        JLabel JLPort = new JLabel("Puerto: ");
        JPetiquetas.add(JLHost);
        JPetiquetas.add(JLPort);

        JPanel JPHost = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTFHost = new JTextField(10);
        JTFHost.setText("localhost");
        JTFHost.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTFHost.transferFocus();
            }
        });
        JPHost.add(JTFHost);

        JPanel JPPort = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTFPort = new JTextField(10);
        JTFPort.setText("9117");
        JPPort.add(JTFPort);

        JPfields.add(JPHost);
        JPfields.add(JPPort);

        JPanel JPdatos = new JPanel(new BorderLayout());
        JPdatos.add(JPetiquetas, BorderLayout.WEST);
        JPdatos.add(JPfields, BorderLayout.CENTER);

        this.add(new JPanel(), BorderLayout.NORTH);
        this.add(new JPanel(), BorderLayout.WEST);
        this.add(new JPanel(), BorderLayout.SOUTH);
        this.add(JPdatos, BorderLayout.CENTER);
        
        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder title = BorderFactory.createTitledBorder(etched,"Servidor");
        title.setTitleJustification(TitledBorder.LEFT);
        this.setBorder(title);

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
