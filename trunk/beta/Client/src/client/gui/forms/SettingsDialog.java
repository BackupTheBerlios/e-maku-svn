package client.gui.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.ArrayList;
import java.io.File;

import org.jdom.Element;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;

import common.misc.language.Language;
import client.misc.settings.ConfigFileHandler;
import client.misc.settings.ConfigFileNotLoadException;
import client.gui.forms.CompanyDialog;
import client.misc.ClientConstants;

/**
 * SettingsDialog.java Creado el 04-ago-2004
 * 
 * Este archivo es parte de eMaku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * eMaku es Software Libre; usted puede redistribuirlo y/o realizar
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

    private JTextField jtfCash;
    
    private JComboBox jcbLang;
    private JComboBox jcbLogs;
	private JComboBox jcbThemes;

	private static Vector<Element> companies = new Vector<Element>();

	private static JButton addButton, editButton, delButton;
    
    public static final int CREATE = 0;
    public static final int EDIT = 1;
    
    public boolean noFile = true;

	private JTabbedPane tabPane;

	private static JList names;
	private static DefaultListModel listModel;
    
    /**
     * Este constructor ensambla toda el cuadro de dialogo de configuracion
     * 
     * @param parent
     *            Forma a la que pertenece el dialogo
     */

    public SettingsDialog(JFrame parent,final int flag) {
    	
        super(parent, true);
        this.setTitle(Language.getWord("CONFIG"));
        this.setResizable(false);
        //this.setLocationByPlatform(true);
        this.setAlwaysOnTop(true);
        
        String cash = "CA001";
        String currentLanguage = "es_CO";
        String currentLogMode = "Default";
        String currentTheme = "Default";           
        
        JPanel JPlabels = new JPanel();
        JPanel JPfields = new JPanel();
        JPlabels.setLayout(new GridLayout(4, 1));
        JPfields.setLayout(new GridLayout(4, 1));

        JLabel JLLang = new JLabel(Language.getWord("LANG"));
        JPanel JPLLang = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLLang.add(JLLang);
                
        JLabel JLBox = new JLabel(Language.getWord("TERM"));
        JPanel JPLBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLBox.add(JLBox);        
        
        JLabel JLLog  = new JLabel(Language.getWord("LOG_TYPE"));
        JPanel JPLog = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLog.add(JLLog);     

        JLabel JLTheme  = new JLabel(Language.getWord("THEME"));
        JPanel JPLTheme = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLTheme.add(JLTheme);     

        JPlabels.add(JPLLang);
        JPlabels.add(JPLBox);
        JPlabels.add(JPLog);
        JPlabels.add(JPLTheme);

        listModel = new DefaultListModel();

        if (flag == EDIT) {
        	try {
        		noFile = false;
        		ConfigFileHandler.loadSettings();
        		cash = ConfigFileHandler.getCash();
        		currentLanguage = ConfigFileHandler.getLanguage();
        		currentLogMode = ConfigFileHandler.getLogMode();
        		currentTheme = ConfigFileHandler.getLookAndFeel();
        		proccessCompanies(ConfigFileHandler.getCompanies());
        	} catch (ConfigFileNotLoadException e1) {
        		e1.printStackTrace();
        	}
        }

        
        jcbLang = new JComboBox(getFileList(ClientConstants.LANG,".xml"));
        jcbLang.setRenderer(new FlagRenderer());
        jcbLang.setSelectedItem(currentLanguage);
   
        jtfCash = new JTextField(10);
        jtfCash.setText(cash);      
              
        Object[] logItems = {Language.getWord("DEFAULT"), Language.getWord("VERBOSE"), Language.getWord("V_FILE"), Language.getWord("NONE")};
        jcbLogs = new JComboBox(logItems);
        jcbLogs.setSelectedItem(currentLogMode);

        jcbThemes = new JComboBox(getFileList(ClientConstants.THEMES,".jar"));
        jcbThemes.setSelectedItem(currentTheme);
        
        JPanel JPLang = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLang.add(jcbLang);
        
        JPanel JPBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPBox.add(jtfCash);    
        
        JPanel JPLogs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPLogs.add(jcbLogs);

        JPanel JPThemes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPThemes.add(jcbThemes);
        
        JPfields.add(JPLang);
        JPfields.add(JPBox);
        JPfields.add(JPLogs);
        JPfields.add(JPThemes);

        JPanel JBase = new JPanel();
        JBase.setLayout(new BorderLayout());
        JBase.add(new JPanel(), BorderLayout.NORTH);
        JBase.add(JPlabels, BorderLayout.WEST);
        JBase.add(JPfields, BorderLayout.CENTER);
        JBase.add(new JPanel(), BorderLayout.SOUTH);
        
        JPanel JPsouth = new JPanel();
        JPsouth.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton JBAccept = new JButton(Language.getWord("ACCEPT"));
        JBAccept.setMnemonic(Language.getNemo("ACCEPT"));
        
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

        JButton JBCancel = new JButton(Language.getWord("CANCEL"));
        JBCancel.setMnemonic(Language.getNemo("CANCEL"));
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
        JPanel companiesPanel = new JPanel(new BorderLayout());

        JPanel JPbuttons = new JPanel();
        JPbuttons.setLayout(new FlowLayout(FlowLayout.CENTER));

        addButton = new JButton(Language.getWord("ADD"));
        addButton.setMnemonic(Language.getNemo("ADD"));
        
        addButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		CompanyDialog dialog = new CompanyDialog(SettingsDialog.this,Language.getWord("ADD_CMP"),Language.getWord("ADD"),"","","","","");
        		dialog.pack();
        		dialog.setLocationRelativeTo(SettingsDialog.this);
        		dialog.setVisible(true);
            }
        });

        editButton = new JButton(Language.getWord("EDIT"));
        editButton.setMnemonic(Language.getNemo("EDIT"));
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String name = names.getSelectedValue().toString();
                Element xml = findCompany(name);
                String jar  = xml.getChild("jarFile").getValue();
                String dir  = xml.getChild("directory").getValue();
                String host  = xml.getChild("host").getValue();
                String port  = xml.getChild("serverport").getValue();

                CompanyDialog dialog = new CompanyDialog(SettingsDialog.this,Language.getWord("EDIT_CMP"),Language.getWord("EDIT"),name,jar,dir,host,port);
                dialog.pack();
                dialog.setLocationRelativeTo(SettingsDialog.this);
                dialog.setVisible(true);
            }
        });

        delButton = new JButton(Language.getWord("REMOVE"));
        delButton.setMnemonic(Language.getNemo("REMOVE"));
        delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String item = names.getSelectedValue().toString();
            	int k = listModel.lastIndexOf(item); 
                 		listModel.remove(k);
                removeCompany(item);
                names.setSelectedIndex(0);
                if (listModel.size() == 0) {
                	editButton.setEnabled(false);
                	delButton.setEnabled(false);
		 }
            }
        });

        JPbuttons.add(addButton);
        JPbuttons.add(editButton);
        JPbuttons.add(delButton);
        companiesPanel.add(JPbuttons,BorderLayout.NORTH);

        names = new JList(listModel);
        names.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        if (names.getModel().getSize() > 0) {
        	names.setSelectedIndex(0);
        } else {
        	editButton.setEnabled(false);
        	delButton.setEnabled(false);
        }

        JScrollPane scrollPane = new JScrollPane(names);
        companiesPanel.add(scrollPane,BorderLayout.CENTER);

        tabPane = new JTabbedPane();
        tabPane.add(JBase,Language.getWord("GENERAL"));
        tabPane.add(companiesPanel,Language.getWord("COMPANIES"));

        this.getContentPane().add(JPsouth, BorderLayout.SOUTH);
        this.getContentPane().add(tabPane, BorderLayout.CENTER);
        this.getContentPane().add(new JPanel(), BorderLayout.WEST);
        this.getContentPane().add(new JPanel(), BorderLayout.EAST);
    }

    private void proccessCompanies(ArrayList<Element> companiesList) {
       for(int i=0;i<companiesList.size();i++) {
		Element e = (Element) companiesList.get(i);
		companies.add(e);
		String name = e.getChild("name").getText();
		listModel.addElement(name);
	}
    }

    public static boolean alreadyExists(String name) {
		return listModel.contains(name);
    }
    
    public String getLog() {
        return jcbLogs.getSelectedItem().toString();
    }

    public String getTheme() {
        return jcbThemes.getSelectedItem().toString();
    }
    
    public String getLanguage() {
        String language =  jcbLang.getSelectedItem().toString();
        return language;
    }
    
    public String getBox() {
    	String boxCode = jtfCash.getText();
    	return boxCode;
    }
    

    
    public void cleanServer() {
        jcbLang.requestFocus();
    }  

    
    private void packingData() {
    
    	String language      = getLanguage();
    	String logType       = getLog();
    	String pos           = getBox();
    	String theme         = getTheme();
   


    	if (companies.size() == 0) {
    		JOptionPane.showMessageDialog(this,Language.getWord("NO_COMPANY"));
    		tabPane.setSelectedIndex(1);
    		return;			
    	}     

    	callConfigFile(language,logType,pos,theme,companies);

    	if(noFile) {
    		callConnection();
    	}
    	dispose();

    }
      
    private void callConfigFile(String language, String logType, String pos, String theme, Vector<Element> companies) {
        ConfigFileHandler.buildNewFile(language,logType,pos,theme,companies);
    }

    private void callConnection()  {
        new Connection();
    }
    
    public boolean isNumber(String s) {
        for(int i = 0; i < s.length(); i++) {
          char c = s.charAt(i);
          if(!Character.isDigit(c)) {
              return false;
          }
        }
        return true;
    }

    public static void addCompany(Element company, String name) {
    	companies.add(company);
    	listModel.addElement(name);
    	
        if (!editButton.isEnabled()) {
        	editButton.setEnabled(true);
        }
        if (!delButton.isEnabled()) {
        	delButton.setEnabled(true);
        }

        names.setSelectedValue(name,true);
    }

    public static void editCompany(Element values, String name) {
    	int size = companies.size();
    	for(int i=0;i<size;i++) {
    		Element e = (Element) companies.elementAt(i);
    		String company = e.getChild("name").getValue();
    		if(company.equals(name)) {
    			String newName = values.getChild("name").getText();
    			e.getChild("name").setText(newName);
    			e.getChild("jarFile").setText(values.getChild("jarFile").getText());
    			e.getChild("directory").setText(values.getChild("directory").getText());
    			e.getChild("host").setText(values.getChild("directory").getText());
    			e.getChild("serverport").setText(values.getChild("directory").getText());
    			companies.setElementAt(e,i);
    			if(!name.equals(newName)) {
    				int index = names.getSelectedIndex();
    				listModel.setElementAt(newName,index);
    			} 			
    			return;	
    		}
    	}
    }

    private Element findCompany(String name) {
    	int size = companies.size();
    	for(int i=0;i<size;i++) {
    		Element e = (Element) companies.elementAt(i);
    		String company = e.getChild("name").getValue();
    		if(company.equals(name)) {
    			return e;
    		}
    	}
    	return null;
    }	

    private void removeCompany(String name) {
    	int size = companies.size();
    	for(int i=0;i<size;i++) {
    		Element e = (Element) companies.elementAt(i);
    		String company = e.getChild("name").getValue();
    		if(company.equals(name)) {
    			companies.removeElementAt(i);
    			return;
    		}
    	}
    }
    
    public Object[] getFileList(String directory, String extension) {
    	Object[] list = null;
        File dir = new File(directory);
        if(dir.exists() && dir.canRead() && dir.isDirectory()) {
        	String[] languages = dir.list();
        	list = new Object[languages.length];
        	int j = 0;
        	for (int i=0; i<languages.length; i++) {
                if(languages[i].endsWith(extension.toLowerCase()) 
                		|| languages[i].endsWith(extension.toUpperCase())) {
                	list[j] = languages[i].substring(0,languages[i].indexOf("."));
                	j++;
                 }       
            }
        }
        return list;
    }
    
}
