package client.gui.xmlmenu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import client.control.ACPFormEvent;
import client.control.ACPFormListener;
import client.control.ACPHandler;
import client.gui.components.MainWindow;
import client.misc.ClientConstants;

import common.gui.forms.GenericForm;
import common.misc.Icons;
import common.misc.language.Language;
/**
 * 
 * MenuLoader.java Creado el 35-mar-2004
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
 * Esta clase se encarga de generar de forma dinamica y recursiva los Menus de
 * E-Maku o JMServer, dependiendo de la informaci�n obtenida de un archivo XML
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 */
public class MenuLoader extends JMenuBar {

	private static final long serialVersionUID = 1526271282346654939L;
	private JMenuItemXML xmlMenuItem;
    private URL xmlFile;
    private static Vector<JMenuItemXML> items;

    /**
     * Constructor con parametro relacionado al archivo XML del menu
     */
    public MenuLoader(URL xmlFile) {
        this.xmlFile = xmlFile;
    }

    /**
     * Metodo encargado de cargar el menu
     */
    public boolean loadMenu() {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(this.xmlFile);
            Element root = doc.getRootElement();
            String string = "JMenu";
			List<Element> menuList = root.getChildren(string);
            Iterator<Element> i = menuList.iterator();
            items = new Vector<JMenuItemXML>();
            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML,
             * en este caso JMenu
             */

            while (i.hasNext()) {
                Element elements = (Element) i.next();
                List<Element> subDataList = elements.getChildren();
                Iterator<Element> j = subDataList.iterator();
                /**
                 * Se adicionara todos los menus principales a la clase MenuLoader
                 */
                
                this.add(loadJMenu(j));
            }
            return true;
        }
        catch (JDOMException JDOMEe) {
            System.out.println(JDOMEe.getMessage());
            return false;
        }
        catch (IOException IOEe) {
           System.out.println(IOEe.getMessage());
           return false;
        }
    }

    /**
     * Metodo encargado de cargar los submenus al menu padre
     */

    private JMenuXML loadJMenu(Iterator<Element> j) {
        String value="";
        boolean isMenuVisible = false;
        try {
	        JMenuXML jMenu = new JMenuXML();
	        jMenu.setVisible(false);
	        while (j.hasNext()) {
	            Element subData = (Element) j.next();
	            value=subData.getValue();
	            if (subData.getName().equals("Text")){
	            	String text = "".equals(Language.getWord(subData.getValue()))?subData.getValue():Language.getWord(subData.getValue());
	                jMenu.setText(Language.getWord(text));
	            }
	            else if (subData.getName().equals("Mnemonic")) {
	            	//TODO Por Problemas con los menemonic
	            	if (!"".equals(value.trim())) {
		            	char langNemo = Language.getNemo(value);
		            	char nemo = ' '==langNemo?value.charAt(0):langNemo;
		                jMenu.setMnemonic(nemo);
	            	}
	            }
	            else if (subData.getName().equals("Icon"))
	            	try {
	            		jMenu.setIcon(new ImageIcon(this.getClass().getResource(Icons.getIcon(value))));
	            	}
	            	catch(NullPointerException NPEe) {
	            		try {
	            			jMenu.setIcon(new ImageIcon(this.getClass().getResource(value)));
	            		}
	            		catch(NullPointerException NPEe2) {
	            			//NPEe2.printStackTrace();
	            		}
	            	}
	            else if (subData.getName().equals("JSeparator"))
	                jMenu.add(new JSeparator());
	            else {
	                List<Element> menuItemList = subData.getChildren();
	                Iterator<Element> k = menuItemList.iterator();
	                if (subData.getName().equals("JMenuItem")) {
	                    JMenuItemXML xmlItem = loadJMenuItem(k);
	                    if (xmlItem != null) {
	                        jMenu.add(xmlItem);
	                        if (xmlItem.getTransaction()!= null) {
	                        	jMenu.setTransaction(xmlItem.getTransaction());
	                        }
		                    if (xmlItem.isVisible()) {
		                    	isMenuVisible=true;
		                    }
	                    }
	                } else if (subData.getName().equals("JMenu")) {
	                    /**
	                     * LLamado Recursivo a CargarJMenu
	                     */
	                	JMenuXML subMenu = loadJMenu(k);
	                	if (subMenu!=null) {
	                		jMenu.add(subMenu);
	                		jMenu.setTransactions(subMenu.getTransactions());
	                	}
	                }
	            }
	        }
		    jMenu.setVisible(isMenuVisible);
	    	return jMenu;
        }
    
        catch(NullPointerException NPEe) {
            System.out.println("Nemo: "+value);
            NPEe.printStackTrace();
            return null;
        }
    }

    /**
     * Metodo encargado de cargar los Items al menu padre
     */

    private JMenuItemXML loadJMenuItem(Iterator<Element> j) {
        xmlMenuItem = new JMenuItemXML();
        while (j.hasNext()) {
            Element subData = (Element) j.next();
            String value = subData.getValue();
            
            if (subData.getName().equals("Transaction")) {
            	xmlMenuItem.setTransaction(subData.getValue());
            } else if (subData.getName().equals("Activo")) {
                xmlMenuItem.setActive(true);
            } else if (subData.getName().equals("Acelerator")) {
            	
            } else if (subData.getName().equals("ActionCommand")) {
                xmlMenuItem.setActionCommand(subData.getValue());
            }
            else if (subData.getName().equals("Icon")) {
            	try {
	                xmlMenuItem.setIcon(new ImageIcon(this.getClass().getResource(
	                        Icons.getIcon(subData.getValue()))));
            	}
            	catch (NullPointerException NPEe) {
            		try {
            			xmlMenuItem.setIcon(new ImageIcon(this.getClass().getResource(
            					subData.getValue())));
            		}
            		catch(NullPointerException e) {
            			System.out.println("Icon: "+subData.getValue());
            		}
            		}
            }
            else if (subData.getName().equals("Text")) {
                xmlMenuItem.setText(Language.getWord(subData.getValue()));
            }
            else if (subData.getName().equals("Mnemonic")) {
            	//TODO Por Problemas con los menemonic
            	if (!"".equals(value.trim())) {
	            	char langNemo = Language.getNemo(value);
	            	char nemo = ' '==langNemo?value.charAt(0):langNemo;
	            	xmlMenuItem.setMnemonic(nemo);
            	}
            }   
            else if (subData.getName().equals("Clase")) {
                xmlMenuItem.setClassName(subData.getValue());
            } else if (subData.getName().equals("ArgConstructor")) {
                List<Element> listArgs = subData.getChildren();
                xmlMenuItem.setArgConstructor(ArgConstructor(listArgs));
            } else if (subData.getName().equals("TypeArgConstructor")) {
                List<Element> listArgs = subData.getChildren();
                xmlMenuItem.setTypeArgConstructor(typeArgConstructor(listArgs));
            } else if (subData.getName().equals("Metodo")) {
                xmlMenuItem.setMethod(subData.getValue());
            } else if (subData.getName().equals("JMenu")) {
                List<Element> listMenuItem = subData.getChildren();
                Iterator<Element> k = listMenuItem.iterator();
                xmlMenuItem.add(loadJMenu(k));
            }
        }
        items.addElement(xmlMenuItem);
        return xmlMenuItem;
    }

    /**
     * Metodo encargado de cargar los argumentos de los constructores de las
     * clases en caso de que estas tengan argumentos
     */

    private Object[] ArgConstructor(List<Element> argsList) {
        if (argsList.size() > 0) {
            Object[] args = new Object[argsList.size()];
            Iterator<Element> j = argsList.iterator();
            int i = 0;
            while (j.hasNext()) {
                Element argElements = (Element) j.next();
                args[i] = argElements.getValue();
                i++;
            }
            return args;
        }
        return null;
    }

    /**
     * Metodo encargado de cargar generar los Tipos de datos de los argumentos,
     * el arreglo tipo de dato, debe ser igual a los argumentos. En el modelo
     * actual se almacenaran todos los tipo de argumentos en un arreglo tipo
     * Class y los argumentos en otro tipo Object, sin embargo este ultimo
     * debera ser formateado validando la informacion segun el tipo de dato de
     * cada argumento, suministrado por el arreglo Class[]
     */

    private Class[] typeArgConstructor(List<Element> argsList) {
        if (argsList.size() > 0) {
            Class[] argsType = new Class[argsList.size()];
            Iterator<Element> j = argsList.iterator();
            int i = 0;
            while (j.hasNext()) {
                Element argElements = (Element) j.next();
                if (argElements.getName().equals("Int"))
                    argsType[i] = int.class;
                if (argElements.getName().equals("String"))
                    argsType[i] = String.class;
                i++;
            }
            return argsType;
        }
        return null;
    }

    /**
     * Este metodo se encarga de habilitar todos los items del menu
     */

    public void setEnabledAll() {
        for (int i = 0; i < items.size(); i++) {
            items.elementAt(i).setEnabled(true);
            items.elementAt(i).setVisible(true);
        }
    }

    /**
     * Este metodo se encarga de deshabilitar todos los items del menu,
     * exceptuando los que tienn la etiqueta activos
     */

    public void setDisabledAll() {
        for (int i = 0; i < items.size(); i++) {
            if (!items.elementAt(i).getState()) {
                items.elementAt(i).setEnabled(false);
            	items.elementAt(i).setVisible(false);
            }
        }
    }

    /**
     * Este metodo se encarga de habilitar el item que tenga como etiqueta en su
     * actioncommand el parametro recogido como argumento
     */

    public void setEnabled(String actioncommand) {
        for (int i = 0; i < items.size(); i++) {
            if (items.elementAt(i).getActionCommand().equals(actioncommand)) {
                items.elementAt(i).setEnabled(true);
            	items.elementAt(i).setVisible(true);
            }
        }
    }

    /**
     * Este metodo se encarga de deshabilitar el item que tenga como etiqueta en
     * su actioncommand el parametro recogido como argumento
     */

    public void setDisabled(String actioncommand) {
        for (int i = 0; i < items.size(); i++) {
            if (items.elementAt(i).getActionCommand().equals(actioncommand))
                items.elementAt(i).setEnabled(false);
            	items.elementAt(i).setVisible(false);
        }
    }
}

class JMenuXML extends JMenu implements ACPFormListener {

	private static final long serialVersionUID = 7636160917375080303L;
	private Hashtable<String,String> transactions;
	
	public JMenuXML() {
		transactions = new Hashtable<String,String>();
		ACPHandler.addACPFormListener(this);
   	}
	
	public void setTransaction(String key) {
		transactions.put(key,"");
	}
	
	public Hashtable<String,String> getTransactions() {
		return transactions;
	}
	
	public void setTransactions(Hashtable<String,String> transactions) {
		Enumeration<String> e = transactions.keys();
		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			this.transactions.put(key,"");
		}
	}

	public void arriveACPForm(ACPFormEvent e) {
		if (transactions.containsKey(e.getTransaction())) {
			setEnabled(true);
			setVisible(true);
			/*Object parent = this.getParent();
			if (parent instanceof JPopupMenu) {
				JPopupMenu popup = (JPopupMenu)parent;
				if (popup.isVisible()) {
					popup.pack();	
				}
			}*/
			//ACPHandler.removeACPFormListener(this);
		}
	}
}

/**
 * Clase encargada de generar los JMenuItem y la manipulaci�n de los eventos de
 * cada uno de ellos
 */

class JMenuItemXML extends JMenuItem implements ActionListener , ACPFormListener {

	private static final long serialVersionUID = -2350472869777111566L;
	private String transaction;
    private String className = null;
    private String method;
    private boolean isActive;
    private Object[] argConstructor = null;
    private Class[] typeArgConstructor = null;

    /**
     * Como esta clase es una implementacion de ActionListener, se aprovecha
     * esta propiedad para crear de si misma los eventos de cada JMenuItem
     */

    public JMenuItemXML() {
        this.addActionListener(this);
        ACPHandler.addACPFormListener(this);
    }

    /**
     * Este metodo asigna la propiedad si el menu puede ser desactivado o no
     */

    public void setActive(boolean activo) {
        this.isActive = activo;
    }

    /**
     * Este metodo retorna si el menu puede ser desactivado o no
     */

    public boolean getState() {
        return isActive;
    }

    public String getTransaction() {
    	return transaction;
    }
    
    public void setTransaction(String transaction) {
        this.transaction = transaction;
        this.setEnabled(false);
        this.setVisible(false);
    }

    /**
     * Este metodo se encarga de almacenar el nombre de la clase que se
     * instanciara en el evento del menu. Todo lo relacionado con los menus se
     * maneja de forma abstracta
     */

    public void setClassName(String ClassName) {
        this.className = ClassName;
    }

    /**
     * Este metodo almacenara el metodo a ejecutarce en caso de que exista, una
     * vez instanciada la clase
     */

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Este metodo se encarga de almacenar el arreglo de Argumentos para el
     * constructor a instanciarse
     */

    public void setArgConstructor(Object[] ArgConstructor) {
        this.argConstructor = ArgConstructor;
    }

    /**
     * Este metodo se encarga de almacenar el arreglo de Tipos de Argumentos
     * para el constructor
     */

    public void setTypeArgConstructor(Class[] TypeArgConstructor) {
        this.typeArgConstructor = TypeArgConstructor;
    }

    /**
     * Metodo encargado de ejecutar el evento de accion
     */

    public void actionPerformed(ActionEvent e) {
    	class ActionOption extends Thread {
    		public void run() {
		        if (className!=null) {
			        try {
			            Class<?> classObject = Class.forName(className);
			            if (method == null) {
			                if (typeArgConstructor != null)
			                    validArguments();
			
			                Constructor constructorVar = classObject.getConstructor(typeArgConstructor);
			                constructorVar.newInstance(argConstructor);
			            } else {
			                Method methodVar = classObject.getMethod(method, new Class[] {});
			                methodVar.invoke(classObject.newInstance(), new Object[]{});
			            }
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
		        }
		        else {
		            Dimension size = new Dimension();
		            size.height = MainWindow.getWidthValue();
		            size.width = MainWindow.getHeightValue();
		            new GenericForm(ACPHandler.getDocForm(transaction),
		                    						   MainWindow.getDesktopPane(),
		                    						   ClientConstants.KeyClient,
		                    						   size,
		                    						   transaction);
		        }
    		}
    	}
    	new ActionOption().start();
    }
        

    /**
     * Metodo encargado de reorganizar los argumentos dependiendo de su tipo, el
     * tipo se lo obtiene del arreglo TypeArgConstructor
     */

    private void validArguments() {
        /**
         * Por el momento solo manejo argumentos de tipo entero, una vez existan
         * de otro tipo se formatearan adicionando sentencias if
         */
        for (int i = 0; i < typeArgConstructor.length; i++) {
            if (typeArgConstructor[i].getName().equals("int"))
                argConstructor[i] = new Integer(argConstructor[i].toString());
            if (typeArgConstructor[i].getName().equals("String"))
                argConstructor[i] = argConstructor[i].toString();
        }
    }

	public void arriveACPForm(ACPFormEvent e) {
		if (e.getTransaction().equals(transaction)) {
			setEnabled(true);
			setVisible(true);
			/*JPopupMenu popup = (JPopupMenu)getParent();
			if (popup.isVisible()) {
				popup.pack();
			}*/
	     //   ACPHandler.removeACPFormListener(this);
		}
	}
}
