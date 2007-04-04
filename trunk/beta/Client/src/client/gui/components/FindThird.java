package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jdom.Document;
import org.jdom.Element;

import common.gui.components.AnswerEvent;
import common.gui.components.AnswerListener;
import common.gui.components.VoidPackageException;
import common.gui.components.XMLTextField;
import common.gui.forms.EndEventGenerator;
import common.gui.forms.GenericForm;
import common.gui.forms.InstanceFinishingListener;
import common.gui.forms.NotFoundComponentException;
import common.misc.Icons;
import common.misc.language.Language;
import common.transactions.TransactionServerException;
import common.transactions.TransactionServerResultSet;

/**
 * FindThird.java Creado el 25-abr-2005
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de hacer una consulta a un tercero, por una llave 
 * determinada, esta llave se captura en un XMLTextField y se la envia 
 * al ST, este retorna todas las concordancias posibles y las almacena en
 * un XMLComboBox, una vez seleccionado el tercero elegido, este envia 
 * una nueva consulta, la cual desplegara sus direcciones y telefonos
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class FindThird extends JTabbedPane implements AnswerListener, InstanceFinishingListener {

	private static final long serialVersionUID = 716651304487269232L;
    private XMLTextField XMLTFfind;
    private JComboBox JCBfined;
    private JComboBox JCBver;
    private JComboBox JCBdireccion;
    private JComboBox JCBtelefono;
    private Hashtable<String,DataThird> Hthird;
    private Hashtable<String, Vector<DataAddress>> Haddress;
    private Hashtable<String, Vector<DataPhone>> Hphones;
    private Vector<String> Vkeys;
    private Vector<DataAddress> Vaddress;
    private Vector<DataPhone> Vphones;
    private boolean search = false;
    private String tab1 = "";
    private String tab2 = "";
	private boolean singleIcon= false;
	private GenericForm GFforma;
	private int maxRecords;
	private boolean returnBlankPackage;
	private String driverEvent;
	private Object keySQL;
	private String sqlSizeRecords;
	private String sqlRecords;
	private String sqlAddress;
	private String sqlPhones;
	private boolean enabled = true;
	private String driverEventClass;
	private String method;
	private String keySwitch;
	private HashMap<String,String> casos;
	private String defaultSQL;
	private Vector<AnswerListener> answerListener = new Vector<AnswerListener>();
	private ArrayList<String> sqlCode;
	private ArrayList<String> sqlCodeWT;
	private String typeDocument;
	private Vector<String> constantValue;
	
    public FindThird(GenericForm GFforma,Document doc) {
        
        /*
         * Captura de argumentos de la clase
         */
    	this.GFforma = GFforma; 
        Element args = doc.getRootElement();
        Iterator i = args.getChildren().iterator();
        Hthird = new Hashtable<String,DataThird>();
        Vkeys = new Vector<String>();
        casos = new HashMap<String,String>();
        sqlCode = new ArrayList<String>();
        sqlCodeWT = new ArrayList<String>();
        
        GFforma.addInitiateFinishListener(this);
        for (int j=0;i.hasNext();j++) {
            Element e = (Element) i.next();
           if ("placement".equals(e.getAttributeValue("attribute"))) {
            	if ("LEFT".equals(e.getValue())) {
            		this.setTabPlacement(JTabbedPane.LEFT);            		
            	}
            	else if ("BOTTOM".equals(e.getValue())) {
            		this.setTabPlacement(JTabbedPane.BOTTOM);            		
            	}
            	else if ("TOP".equals(e.getValue())) {
            		this.setTabPlacement(JTabbedPane.TOP);            		
            	}
            }
            else if ("singleIcon".equals(e.getAttributeValue("attribute"))) {
            		singleIcon = Boolean.parseBoolean(e.getValue());
            }
            else if ("maxRecords".equals(e.getAttributeValue("attribute"))) {
            		maxRecords = Integer.parseInt(e.getValue());
            }
            else if("returnBlankPackage".equals(e.getAttributeValue("attribute"))) {
            		returnBlankPackage = Boolean.parseBoolean(e.getValue());
            }
            else if("keySQL".equals(e.getAttributeValue("attribute"))) {
            		keySQL = e.getValue();
            }
            else if("driverEvent".equals(e.getAttributeValue("attribute"))) {
	            	String id="";
	            	if (e.getAttributeValue("id")!= null) {
	            		id=e.getAttributeValue("id");
	            	}
	            	driverEvent=e.getValue()+id;
            }
            else if("sqlSizeRecords".equals(e.getAttributeValue("attribute"))) {
            		sqlSizeRecords = e.getValue();
            }
            else if("sqlRecords".equals(e.getAttributeValue("attribute"))) {
            		sqlRecords = e.getValue();
            }
            else if("sqlAddress".equals(e.getAttributeValue("attribute"))) {
            		sqlAddress = e.getValue();
            }
            else if("sqlPhones".equals(e.getAttributeValue("attribute"))) {
            		sqlPhones = e.getValue();
            }
            else if("sqlCode".equals(e.getAttributeValue("attribute"))) {
            		sqlCode.add(e.getValue());
            }
            else if("sqlCodeWT".equals(e.getAttributeValue("attribute"))) {
            		sqlCodeWT.add(e.getValue());
            }
            else if ("constantValue".equals(e.getAttributeValue("attribute"))) {
				if (constantValue == null) {
					constantValue = new Vector<String>();
				}
				constantValue.addElement(e.getValue());
			}           
            else if("typeDocument".equals(e.getAttributeValue("attribute"))) {
            		typeDocument = e.getValue();
            }
            else if ("enabled".equals(e.getAttributeValue("attribute"))) {
            		enabled = Boolean.parseBoolean(e.getValue());
            }
            else if ("tab1".equals(e.getAttributeValue("attribute"))) {
            		tab1 = Language.getWord(e.getValue());
            }
            else if ("tab2".equals(e.getAttributeValue("attribute"))) {
            		tab2 = Language.getWord(e.getValue());
            }
            else if ("subarg".equals(e.getName())) {
	            	Iterator it = e.getChildren().iterator();
	            	while (it.hasNext()) {
	            		Element subarg = (Element) it.next();
	            		if ("driverEventClass".equals(subarg.getAttributeValue("attribute"))) {
	            			String id="";
	                    	if (subarg.getAttributeValue("id")!= null) {
	                    		id=subarg.getAttributeValue("id");
	                    	}
	                    	driverEventClass = subarg.getValue()+id;
	            		}
	            		else if ("method".equals(subarg.getAttributeValue("attribute"))) {
	                    	method = subarg.getValue();
	            		}
	            		else if ("keySwitch".equals(subarg.getAttributeValue("attribute"))) {
	            			keySwitch = subarg.getValue();
	                }
	            		else if ("case".equals(subarg.getAttributeValue("attribute"))) {
	        				StringTokenizer stk = new StringTokenizer(subarg.getValue(),",");
	        				casos.put(stk.nextToken(),stk.nextToken());
	            		}
	            		else if ("default".equals(subarg.getAttributeValue("attribute"))) {
	            			defaultSQL = subarg.getValue();
	            		}
	            	}
            }
        }
        JPanel JPgeneral = new JPanel(new BorderLayout());
        JPanel JPnorth = new JPanel(new BorderLayout());
        UIManager.getDefaults().put("ComboBox.disabledForeground",Color.BLACK);
        XMLTFfind = new XMLTextField("FIND",20,50);
        XMLTFfind.setEnabled(enabled);
        JCBfined = new JComboBox();
//        JCBfined.setMaximumRowCount(3);
        JCBfined.setPreferredSize(new Dimension(300,80));
        JCBfined.setRenderer(new WrappableListCellRenderer());
        JCBfined.setEnabled(enabled);
        JPnorth.add(XMLTFfind.getLabel(),BorderLayout.WEST);
        JPnorth.add(XMLTFfind.getJPtext(),BorderLayout.CENTER);
       
        JPgeneral.add(JPnorth,BorderLayout.NORTH);
        JPgeneral.add(JCBfined,BorderLayout.CENTER);
        
        JPanel JPgen = new JPanel(new BorderLayout());
        JPgen.add(new JPanel(),BorderLayout.NORTH);
        JPgen.add(new JPanel(),BorderLayout.WEST);
        JPgen.add(new JPanel(),BorderLayout.EAST);
        JPgen.add(new JPanel(),BorderLayout.SOUTH);
        JPgen.add(JPgeneral,BorderLayout.CENTER);
        
        JPanel JPmodificar = new JPanel(new BorderLayout());
        JPanel JPnorth1 = new JPanel(new BorderLayout());

        JPanel JPetiquetas1 = new JPanel(new GridLayout(3,1));
        JPanel JPfields1 = new JPanel(new GridLayout(3,1));

        JPanel JPver = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCBver = new JComboBox();
        JCBver.setEnabled(enabled);
        JCBver.addItem(Language.getWord("NOMBRE"));
        JCBver.addItem(Language.getWord("ESTABLECIMIENTO"));
        JPver.add(JCBver);
        
        JCBdireccion = new JComboBox();
        JCBdireccion.setEnabled(enabled);
        JCBtelefono = new JComboBox();
        JCBtelefono.setEnabled(enabled);

        JPetiquetas1.add(new JLabel(Language.getWord("VER")));
        JPetiquetas1.add(new JLabel(Language.getWord("ADDRESS")));
        JPetiquetas1.add(new JLabel(Language.getWord("PHONE")));
        
        JPfields1.add(JPver);
        JPfields1.add(new JPanel().add(JCBdireccion));
        JPfields1.add(new JPanel().add(JCBtelefono));

        JPnorth1.add(JPetiquetas1,BorderLayout.WEST);
        JPnorth1.add(JPfields1,BorderLayout.CENTER);
        JPanel JPmod = new JPanel(new BorderLayout());
        JPmodificar.add(JPnorth1,BorderLayout.NORTH);
        JPmod.add(new JPanel(),BorderLayout.NORTH);
        JPmod.add(new JPanel(),BorderLayout.WEST);
        JPmod.add(JPmodificar,BorderLayout.CENTER);
        
        XMLTFfind.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				search = true;
			}
		});
        XMLTFfind.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                class SearchQuery extends Thread {
                    public void run() {
                        find();
                    }
                }
                if (search) {
	                	clean(false);
	                	if (!XMLTFfind.getText().equals("")) {
			                new SearchQuery().start();
	                	}
	                	else {
	                    	XMLTFfind.requestFocus();
	                	}
	                	search = false;
                }
                
            }
        });

        JCBfined.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            public void popupMenuCanceled(PopupMenuEvent e) {}
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	            	if (Vkeys.size() > 0) {
	            		JCBver.setSelectedIndex(0);
	    	        	JCBdireccion.removeAllItems();
		    	        JCBtelefono.removeAllItems();
		    	        String key = Vkeys.get(JCBfined.getSelectedIndex());
		    	        showDetail(key);
			            asignExternalQuery();
		    	        searchOthersSQL();
	            	}
            }
        });

        JCBver.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            public void popupMenuCanceled(PopupMenuEvent e) {}
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (JCBver.getSelectedIndex()==0 && JCBfined.getSelectedIndex() >= 0) {
	                	int indice = JCBfined.getSelectedIndex();
	        	        String key = Vkeys.get(indice);
	        	        DataThird DT =Hthird.get(key);
	        	        DT.setMostrarEstablecimiento(false);
	        	        JCBfined.removeItemAt(indice);
	        	        JCBfined.insertItemAt(DT.getItem(),indice);
	        	        JCBfined.setSelectedIndex(indice);
	        	        JCBfined.updateUI();
	        	        asignExternalQuery();
                }
                else if (JCBver.getSelectedIndex()>0  && JCBfined.getSelectedIndex() >= 0 ) {
	                	int indice = JCBfined.getSelectedIndex();
	        	        String key = Vkeys.get(indice);
	        	        DataThird DT = Hthird.get(key);
	        	        DT.setMostrarEstablecimiento(true);
	        	        JCBfined.removeItemAt(indice);
	        	        JCBfined.insertItemAt(DT.getItem(),indice);
	        	        JCBfined.setSelectedIndex(indice);
	        	        JCBfined.updateUI();
	        	        asignExternalQuery();
                }
            }
        });

        JCBdireccion.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            public void popupMenuCanceled(PopupMenuEvent e) {}
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            	changeAddress();
            }
        });

        JCBtelefono.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            public void popupMenuCanceled(PopupMenuEvent e) {}
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            		changePhone();
            }
        });
        if (!singleIcon) {
        	tab1 = "".equals(tab1)?Language.getWord("GENERAL"):tab1;
        	tab2 = "".equals(tab2)?Language.getWord("MODIFICAR"):tab2;
        }
        
        this.addTab(tab1, new ImageIcon(this.getClass().getResource(Icons.getIcon("NTERCERO"))),JPgen);
        this.addTab(tab2, new ImageIcon(this.getClass().getResource(Icons.getIcon("ETERCERO"))),JPmod);
        
    }

    /**
     * Metodo encargado de cambiar la direccion y la ciudad del tercero
     *
     */
    private void changeAddress() {
    	if (JCBdireccion.getItemCount()>0) {
    		int indice = JCBfined.getSelectedIndex();
	        String key = Vkeys.get(indice);
	        String direccion = (String) JCBdireccion.getSelectedItem();
	        String ciudad = Haddress.get(key).get(JCBdireccion.getSelectedIndex()).getCiudad();
	        String keyDir = Haddress.get(key).get(JCBdireccion.getSelectedIndex()).getKey();
	        DataThird DT  = Hthird.get(key);
	        DT.setIdDireccion(keyDir);
	        DT.setDireccion(direccion);
	        DT.setCiudad(ciudad);
	        JCBfined.removeItemAt(indice);
	        JCBfined.insertItemAt(DT.getItem(),indice);
	        JCBfined.setSelectedIndex(indice);
	        JCBfined.updateUI();
    	}
    }
    
    /**
     * Metodo encargado de cambiar el telefono del tercero
     *
     */
    private void changePhone() {
	    	if (JCBtelefono.getItemCount()>0) {
    			int indice = JCBfined.getSelectedIndex();
	        String key = Vkeys.get(indice);
	        String keyPhone = Hphones.get(key).get(JCBtelefono.getSelectedIndex()).getKeyPhone();
	        String telefono = (String) JCBtelefono.getSelectedItem();
	        DataThird DT = Hthird.get(key);        
	        DT.setIdTelefono(keyPhone);
	        DT.setTelefono(telefono);
	        JCBfined.removeItemAt(indice);
	        JCBfined.insertItemAt(DT.getItem(),indice);
	        JCBfined.setSelectedIndex(indice);
	        JCBfined.updateUI();
	    	}
    }
        
    

    /**
     * Este metodo se encarga de iniciar el proceso de consultas, especificamete lo que hace
     * es generar varias consultas si es el caso, dependiendo del numero de palabras claves
     * recibidas en el XMLTFfind.
     * El proceso de busqueda se hace de la siguiente forma:
     * 1. Se genera una consulta con la palabra completa
     * 2. Si la palabra consta de mas de una frase, entonces se generan consultas por cada
     *    una de las fraces recibidas
     */
    private synchronized void find() {

        /*
         * Se generara una busqueda por palabra completa y luego
         * otra por cada una de las fraces que componen la palabra
         */
        StringTokenizer STargs = new StringTokenizer(XMLTFfind.getText()," ");
        
        String args[] = new String[3];
        String arg = XMLTFfind.getText();
        for (int k=0;k<3;k++) {
            args[k]= arg;
        }
        
        fined(args);

        if (STargs.countTokens()>1) {
            for (int i=0;STargs.hasMoreTokens();i++) {
                args = new String[3];
                arg = STargs.nextToken();
                for (int k=0;k<3;k++) {
                    args[k]= arg;
                }
                fined(args);
            }
        }
    }
        
    /**
     * En si, este es el metodo encargado de generar las consultas dependiendo de los argumentos
     * recibidos.
     * @param args contiene los argumentos para generar la consulta
     */
    
    private void fined(String[] args) {
        try {
        	Document doc = TransactionServerResultSet.getResultSetST(sqlSizeRecords,args);
        	
 	        int row = Integer.parseInt(doc.getRootElement().getChild("row").getChildText("col"));
 	        if (row < maxRecords) {
		        doc = TransactionServerResultSet.getResultSetST(sqlRecords,args);
		        LoadDocument(doc);
	            asignExternalQuery();
	            searchOthersSQL();
 	        }
 	        else {
 	        	message();
 	        }
 	        doc = null;
        }
        catch(TransactionServerException STEe) {
           STEe.printStackTrace();
        }
    }
    
    public void LoadDocument(Document doc) {
    		Iterator i = doc.getRootElement().getChildren("row").iterator();
    		int row = doc.getRootElement().getChildren("row").size();
    		if (row>0) {
            while(i.hasNext()) {
                Element e = (Element) i.next();
    	        		String nombre="";
    	        		String establecimiento="";
    	        		String nitcc="";
                String key="";
                String idRegimen="";
                String descRegimen="";
                String idCatalgo="";
                String descCatalogo="";
                
                Iterator j = e.getChildren().iterator();
                for (int k=0;j.hasNext();k++) {
                    Element el = (Element)j.next();
                    
                    /*
                     * Las dos primeras columnas de la consulta contienen el nombre del tercero
                     * y del establecimiento
                     */
                    if (k==0) {
                        nombre= el.getValue();
                    }
                    else if(k==1) {
                        if (!el.getValue().equals("null")) {
                            establecimiento = el.getValue();
                        }
                    }
                    /*
                     * La tercera columna contiene el id_char del tercero, en este caso seria el nit o rut
                     */
                    else if (k==2) {
                        nitcc=el.getValue();
                    }
                    /*
                     * La cuarta columna contiene el id del tercero
                     */
                    else if (k==3) {
                        key=el.getValue();
                    }
                    else if (k==4) {
                        idRegimen=el.getValue();
                    }
                    else if (k==5) {
                        descRegimen=el.getValue();
                    }
                    else if (k==6) {
                        idCatalgo=el.getValue();
                    }
                    else if (k==7) {
                        descCatalogo=el.getValue();
                    }
                    
                }
                /*
                 * Como la forma de generar las consultas por fraces, puede retornar clientes duplicados,
                 * entonces, con este if, se verifica esto para que estos no sean almacenados por seguna vez. 
                 */
                if (!Hthird.containsKey(key)) {
		            DataThird DTfind = new DataThird();
		            Vkeys.addElement(key);
		            DTfind.setNombre(nombre);
		            DTfind.setEstablecimiento(establecimiento);
		            DTfind.setNitcc(nitcc);
		            DTfind.setIdRegimen(idRegimen);
		            DTfind.setDescRegimen(descRegimen);
		            DTfind.setIdCatalogo(idCatalgo);
		            DTfind.setDescCatalogo(descCatalogo);
		            Hthird.put(key, DTfind);
		            loadData(key);
		            JCBfined.addItem(DTfind.getItem());
		            
                }
            }
            showDetail((String) Vkeys.get(0));
    		}
        else {
        		XMLTFfind.requestFocus();
        }
    }
    
    private void searchOthersSQL() {
    		int ind = JCBfined.getSelectedIndex();
    		if (ind < 0)
    			return;
    		String[] args = null;
    		if (constantValue!=null) {
    			args = new String[constantValue.size()+1];
    			int i=0;
    			for (;i<constantValue.size();i++) {
        			args[i]= constantValue.get(i);
        		}
        		args[i]=Vkeys.get(ind);
    		}
    		else {
    			args = new String[1];
    			args[0]=Vkeys.get(ind);
    		}
    		String keyFind = Vkeys.get(ind);
    		for (int i=0; i < sqlCode.size() ; i++) {
    			Document doc = null;
            String sql = sqlCode.get(i);
         
            try {
                doc = TransactionServerResultSet.getResultSetST(sql,args);
            }
            catch (TransactionServerException e) {
                e.printStackTrace();
            }
            AnswerEvent event = new AnswerEvent(this,sql,doc);
	        notificando(event);
    		}
    		if (typeDocument!=null) {
    			for (int i=0; i < sqlCodeWT.size() ; i++) {
    				Document doc = null;
	            String sql = sqlCodeWT.get(i);
	            try {
	                doc = TransactionServerResultSet.getResultSetST(sql,new String[] {keyFind,typeDocument});
	            }
	            catch (TransactionServerException e) {
	                e.printStackTrace();
	            }
	            AnswerEvent event = new AnswerEvent(this,sql,doc);
		        notificando(event);
    			}
    		}
    }
    
    private void asignExternalQuery(){
    	if (Vkeys.size() ==0 ) {
    		return;
    	}
    	String keyFind = Vkeys.get(JCBfined.getSelectedIndex()>=0?JCBfined.getSelectedIndex():0);
        DataThird DTdata = Hthird.get(keyFind);
        String caso = null;

        if ("regimen".equals(keySwitch) || "catalogo".equals(keySwitch)) {
            
        	if ("regimen".equals(keySwitch)) {
        		caso = (String) casos.get(DTdata.getIdRegimen());
        	}
	        else if ("catalogo".equals(keySwitch)) {
	        		caso = (String) casos.get(DTdata.getIdatalogo());
	        }

        	String newSQLCode = null;

        	if (caso!=null && !"".equals(caso)) {
	        	newSQLCode = caso;
        	}
        	else {
        		newSQLCode = defaultSQL;
        	}
        	System.out.println("consulta de catalogo: "+newSQLCode);
        	if (newSQLCode!=null && !"".equals(newSQLCode)) {
        		try {
	            	Object [] argsMethod  = new Object[]{newSQLCode};
		        	Class [] argsClass = new Class[] {String.class};
				GFforma.invokeMethod(driverEventClass,method,argsClass,argsMethod);
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			} catch (NotFoundComponentException e1) {
				e1.printStackTrace();
			}
        	}
        }
    }
    /**
     * Una vez seleccionado el tercero que se busco con anterioridad, esta seleccion
     * procede a traer la informacion complementaria de el, como es su direccion, 
     * telefono y ciudad, este metodo se encarga de efectuar ese proceso
     */

    private synchronized void loadData(String key) {
        try {
            Vaddress = new Vector<DataAddress>();
            Vphones = new Vector<DataPhone>();
            
	        String args[] = {key};
            /*
             * Consultando direcciones
             */
	        Document doc = TransactionServerResultSet.getResultSetST(sqlAddress,args);

			Iterator i = doc.getRootElement().getChildren("row").iterator();
			int row = doc.getRootElement().getChildren("row").size();
			if (row>0) {
				for(int w=0;i.hasNext();w++) {
	                Element e = (Element) i.next();
	                String direccion="";
	                String ciudad="";
	                String keyAddress="";
	                Iterator j = e.getChildren().iterator();
					for (int k=0;j.hasNext();k++) {
	                    Element el = (Element)j.next();
	                    if (k==0) {
	                        if (!el.getValue().equals("null")) {
	                            direccion= el.getValue();
	                        }
	                    }
	                    else if(k==1) {
	                        if (!el.getValue().equals("null")) {
	                            ciudad = el.getValue();
	                        }
	                    }
	                    else if (k==2) {
	                        keyAddress=el.getValue();
	                    }
					}
		            DataAddress DAfind = new DataAddress(ciudad,direccion,keyAddress);
		            Vaddress.add(DAfind);
					if (w==0) {
					    Hthird.get(key).setDireccion(direccion);
					    Hthird.get(key).setCiudad(ciudad);
					    Hthird.get(key).setIdDireccion(keyAddress);
					}
				}
			}
			if (Haddress.containsKey(key)) {
				Haddress.remove(key);
			}
            Haddress.put(key,Vaddress);
			
            /*
             * Consultando telefonos
             */
	        
			doc = TransactionServerResultSet.getResultSetST(sqlPhones,args);

			i = doc.getRootElement().getChildren("row").iterator();
			row = doc.getRootElement().getChildren("row").size();
			if (row>0) {
				for(int w=0;i.hasNext();w++) {
	                Element e = (Element) i.next();
	    	        		String numero="";
	                String keyPhone="";
	                Iterator j = e.getChildren().iterator();
					for (int k=0;j.hasNext();k++) {
	                    Element el = (Element)j.next();
	                    if (k==0) {
	                        if (!el.getValue().equals("null")) {
	                            numero= el.getValue();
	                        }
	                    }
	                    else if (k==1) {
	                        keyPhone=el.getValue();
	                    }
					}
					DataPhone dphone = new DataPhone(numero,keyPhone);
		            Vphones.addElement(dphone);
					if (w==0) {
					    Hthird.get(key).setTelefono(numero);
					    Hthird.get(key).setIdTelefono(keyPhone);
					}
				}
			}
			if (Hphones.containsKey(key)) {
				Hphones.remove(key);
			}
            Hphones.put(key, Vphones);
        }
        catch(TransactionServerException STEe) {
            STEe.printStackTrace();
        }
        catch(ArrayIndexOutOfBoundsException AIOOBEe) {
        	AIOOBEe.printStackTrace();
        }
    }
    
    private void message() {
    	
	    	/*
	    	 * JOptionPane.showInternalMessageDialog tiene huevo ....
	    	 * @author pastuxso
	    	 */
    		try {
			SwingUtilities.invokeAndWait(new Runnable() {
			          public void run() {
			          	JOptionPane.showInternalMessageDialog(
				     			GFforma.getDesktopPane(),
				     			Language.getWord("ERR_EXCESS_RECORDS"),
				     			Language.getWord("ERROR_MESSAGE"),
				     			JOptionPane.ERROR_MESSAGE);
			        	  
			          };
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
    }
    
    private void showDetail(String key) {
    	
    		Vector<DataPhone> vPhones = Hphones.get(key);
        Vector<DataAddress> vDirec = Haddress.get(key);
        
        for (int i=0; i < vPhones.size(); i++) {
        		JCBtelefono.addItem(vPhones.get(i).getPhone());
        }
        
        for (int i=0; i < vDirec.size(); i++) {
        		JCBdireccion.addItem(vDirec.get(i).getDireccion());
        }	
    }
    
    public JTabbedPane getPanel() {
        return this;
    }

    /**
     * Este metodo retorna un elemento de un terecero definido tabla: tercero_def
     * @return retorna un Elemento
     * @throws VoidPackageException 
     */
    
    public Element getPackage() throws VoidPackageException {

        Element pack = new Element("package");

	    	try {
	        String keyFind = Vkeys.get(JCBfined.getSelectedIndex());
	        DataThird DTdata = Hthird.get(keyFind);
	        
	        Element id = new Element("field");
	        id.setText(keyFind);
	        id.setAttribute("attribute","key");
	        id.setAttribute("name","idTercero");
	        Element idDireccion = new Element("field");
	        idDireccion.setText(DTdata.getIdDireccion());
	        if ("".equals(DTdata.getIdDireccion())) {
	        		idDireccion.setAttribute("attribute","NULL");
	        }
	        Element idTelefono = new Element("field");
	        idTelefono.setText(DTdata.getIdTelefono());
	        if ("".equals(DTdata.getIdTelefono())) {
	        		idTelefono.setAttribute("attribute","NULL");
	        }
	        pack.addContent(id);
	        pack.addContent(idDireccion);
	        pack.addContent(idTelefono);
	    	}
	    	catch(ArrayIndexOutOfBoundsException AIOOBE) {
	    		if (!returnBlankPackage) {
	    			throw new VoidPackageException(Language.getWord("ERR_NOT_SELECTED_THIRD"));
	    		}
	    	}
        return pack;
    }
    
    public Element getPrintPackage() throws VoidPackageException {

        Element pack = new Element("package");
        int index = JCBfined.getSelectedIndex();
        if (index==-1) {
        	return pack;
        }
        String keyFind = Vkeys.get(index);
        DataThird DTdata = Hthird.get(keyFind);
        
        Element id = new Element("field");
        id.setText(DTdata.getNombre());
        pack.addContent(id);
        id = new Element("field");
        id.setText(DTdata.getNitcc());
        pack.addContent(id);
        id = new Element("field");
        id.setText(DTdata.getDireccion());
        pack.addContent(id);
        id = new Element("field");
        id.setText(DTdata.getTelefono());
        pack.addContent(id);
        id = new Element("field");
        id.setText(DTdata.getCiudad());
        pack.addContent(id);
        id = new Element("field");
        id.setText(DTdata.getDescRegimen());
        pack.addContent(id);
        
        return pack;
    }
    public boolean containData() {
    		try {
    			Element elm = getPackage();
			if (elm.getChildren().size() > 0) {
				return true;
			}
		} catch (VoidPackageException e) {
			return false;
		}
    		return false;
    }
    
	public void clean() {
		clean(true);
	}
	
	public void clean(boolean text) {
		if (text) {
			XMLTFfind.setText("");
		}
		JCBfined.removeAllItems();
        JCBdireccion.removeAllItems();
        JCBtelefono.removeAllItems();
        Vkeys = null;
        Hthird = null;
        Hphones = null;
        Haddress = null;
        Vkeys = new Vector<String>();
        Hthird = new Hashtable<String,DataThird>();
        Hphones = new Hashtable<String,Vector<DataPhone>>();
        Haddress = new Hashtable<String,Vector<DataAddress>>();
	}

	public void arriveAnswerEvent(AnswerEvent AEe) {
		try {
			Document doc = AEe.getDocument();
			clean();
			LoadDocument(doc);
			searchOthersSQL();			}
		catch (NullPointerException NPEe) {
	    		clean();
		}
	}

	public void initiateFinishEvent(EndEventGenerator e) {
		try {
			if (driverEvent!=null)
				GFforma.invokeMethod(driverEvent,"addAnswerListener",new Class[]{AnswerListener.class},new Object[]{this});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (NotFoundComponentException e1) {
			e1.printStackTrace();
		}	
	}
	
	public void paintComponent(Graphics g) {
	    Graphics2D g2 = (Graphics2D)g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                        RenderingHints.VALUE_ANTIALIAS_ON);
	    super.paintComponent(g);
	}
	
	public void addAnswerListener(AnswerListener listener ) {
		 answerListener.addElement(listener);
	}

	public void removeAnswerListener(AnswerListener listener ) {
		 answerListener.removeElement(listener);
	}
	
	/**
	 * Metodo encargado de notificar la llegada de un paquete <answer/>
	 * 
	 * @param event
	 */
	private void notificando(AnswerEvent event) {
		for(AnswerListener l:answerListener) {
			if (l.containSqlCode(event.getSqlCode())) {
				l.arriveAnswerEvent(event);
			}
		}
	}
	
	public boolean containSqlCode(String sqlCode) {
		return sqlCode.equals(keySQL);
	}
}

/**
 * FindThird.java Creado el 05-may-2005
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Esta clase almacena una estructura de posibles terceros consultados
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

class DataThird {
    private String nitcc;
    private String idRegimen;
    private String idCatalogo;
    private String descRegimen;
    private String descCatalogo;
    private String nombre;
    private String establecimiento;
    private String direccion;
    private String telefono;
    private String ciudad;
    
    private String idDireccion;
    private String idTelefono;
    
    private boolean MostrarEstablecimiento = false;
    protected DataThird() {
        nitcc="";
        nombre="";
        establecimiento="";
        direccion="";
        telefono="";
        ciudad="";
        idDireccion="";
        idTelefono="";
    }
    
    public String getItem() {
    	String tercero = MostrarEstablecimiento ? establecimiento : nombre;
		return new String(tercero+"\n"+nitcc+"\n"+direccion+"\n"+telefono+"\n"+ciudad+"\n"+descRegimen).trim();
	}

	public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getEstablecimiento() {
        return establecimiento;
    }
    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }
    public String getIdDireccion() {
        return idDireccion;
    }
    public void setIdDireccion(String idDireccion) {
        this.idDireccion = idDireccion;
    }
    public String getIdTelefono() {
        return idTelefono;
    }
    public void setIdTelefono(String idTelefono) {
        this.idTelefono = idTelefono;
    }
    public String getNitcc() {
        return nitcc;
    }
    public void setNitcc(String nitcc) {
        this.nitcc = nitcc;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

	public boolean isMostrarEstablecimiento() {
		return MostrarEstablecimiento;
	}

	public void setMostrarEstablecimiento(boolean mostrarEstablecimiento) {
		MostrarEstablecimiento = mostrarEstablecimiento;
	}

	public String getDescCatalogo() {
		return descCatalogo;
	}

	public void setDescCatalogo(String descCatalogo) {
		this.descCatalogo = descCatalogo;
	}

	public String getDescRegimen() {
		return descRegimen;
	}

	public void setDescRegimen(String descRegimen) {
		this.descRegimen = descRegimen;
	}

	public String getIdatalogo() {
		return idCatalogo;
	}

	public void setIdCatalogo(String idCatalogo) {
		this.idCatalogo = idCatalogo;
	}

	public String getIdRegimen() {
		return idRegimen;
	}

	public void setIdRegimen(String idRegimen) {
		this.idRegimen = idRegimen;
	}
}

/**
 * FindThird.java Creado el 05-may-2005
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Esta clase almacena las direcciones de un tercero seleccionado
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

class DataAddress {
    private String direccion;
    private String ciudad;
    private String key;
    
    public DataAddress(String ciudad, String direccion, String key) {
		this.ciudad = ciudad;
		this.direccion = direccion;
		this.key = key;
	}
	public String getCiudad() {
        return ciudad;
    }
    public String getDireccion() {
        return direccion;
    }
	public String getKey() {
		return key;
	}

}
class DataPhone {
	
	String keyPhone;
	String phone;
	
	public DataPhone(String phone, String keyPhone) {
		this.keyPhone = keyPhone;
		this.phone = phone;
	}
	public String getKeyPhone() {
		return keyPhone;
	}
	public String getPhone() {
		return phone;
	}
}
class WrappableListCellRenderer extends JTextArea implements ListCellRenderer {

	private static final long serialVersionUID = -3663060147703431280L;

	WrappableListCellRenderer() {
		setOpaque(true);
		setLineWrap(false);
		setWrapStyleWord(false);
		setRows(4);
		setColumns(20);
    }
	
    public Component getListCellRendererComponent(JList list, Object value, int index,boolean isSelected, boolean cellHasFocus) {
	    	try {
			setText(value.toString());
			setBackground(isSelected ? Color.lightGray : Color.white);
			setForeground(isSelected ? Color.white : Color.black);
	    	} catch(NullPointerException NPEe) {
	    		setText("");
	    	}
   		return this;
    }
  }
