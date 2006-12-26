package client.gui.xmlmenu;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import client.control.ACPFormEvent;
import client.control.ACPFormListener;
import client.control.ACPHandler;
import client.gui.components.MainWindow;
import client.misc.ClientConst;
import common.gui.forms.GenericForm;
import common.misc.Icons;
import common.misc.language.Language;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
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

	private JMenuItemXML JMItmp;

    private URL FileXML;

    private static Vector<JMenuItemXML> Items;

    /**
     * Constructor con parametro relacionado al archivo XML del menu
     */
    public MenuLoader(URL FileXML) {
        this.FileXML = FileXML;
    }

    /**
     * Metodo encargado de cargar el menu
     */
    public void Loading() {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(this.FileXML);
            Element raiz = doc.getRootElement();
            String string = "JMenu";
			java.util.List Ljmenu = raiz.getChildren(string);
            Iterator i = Ljmenu.iterator();
            Items = new Vector<JMenuItemXML>();
            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML,
             * en este caso JMenu
             */

            while (i.hasNext()) {
                Element datos = (Element) i.next();

                java.util.List Lsubdatos = datos.getChildren();
                Iterator j = Lsubdatos.iterator();
                /**
                 * Se adicionara todos los menus principales a la clase MenuLoader
                 */
                
                this.add(CargarJMenu(j));
            }
        }
        catch (JDOMException JDOMEe) {
            System.out.println(JDOMEe.getMessage());
        }
        catch (IOException IOEe) {
            System.out.println(IOEe.getMessage());
        }
    }

    /**
     * Metodo encargado de cargar los submenus al menu padre
     */

    private JMenuXML CargarJMenu(Iterator j) {
        String value="";
        boolean menuVisible = false;
        try {
	        JMenuXML JMtmp = new JMenuXML();
	        JMtmp.setVisible(false);
	        while (j.hasNext()) {
	            Element subdatos = (Element) j.next();
	            value=subdatos.getValue();
	            if (subdatos.getName().equals("Text"))
	                JMtmp.setText(Language.getWord(subdatos.getValue()));
	            else if (subdatos.getName().equals("Mnemonic"))
	                JMtmp.setMnemonic(Language.getNemo(value));
	            else if (subdatos.getName().equals("Icon"))
	            	try {
	            		JMtmp.setIcon(new ImageIcon(this.getClass().getResource(Icons.getIcon(value))));
	            	}
	            	catch(NullPointerException NPEe) {
	            		JMtmp.setIcon(new ImageIcon(this.getClass().getResource(value)));
	            	}
	            else if (subdatos.getName().equals("JSeparator"))
	                JMtmp.add(new JSeparator());
	            else {
	                java.util.List Ljmenuitem = subdatos.getChildren();
	                Iterator k = Ljmenuitem.iterator();
	                if (subdatos.getName().equals("JMenuItem")) {
	                    JMenuItemXML JMItmp = CargarJMenuItem(k);
	                    if (JMItmp != null) {
	                        JMtmp.add(JMItmp); 
	                        if (JMItmp.getTransaction()!= null) {
	                        	JMtmp.setTransaction(JMItmp.getTransaction());
	                        }
		                    if (JMItmp.isVisible()) {
		                    	menuVisible=true;
		                    }
	                    }
	                } else if (subdatos.getName().equals("JMenu")) {
	                    /**
	                     * LLamado Recursivo a CargarJMenu
	                     */
	                	JMenuXML JMsub = CargarJMenu(k);
	                	if (JMsub!=null) {
	                		JMtmp.add(JMsub);
	                		JMtmp.setTransactions(JMsub.getTransactions());
	                	}
	                }
	            }
	        }
		    JMtmp.setVisible(menuVisible);
	    	return JMtmp;
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

    private JMenuItemXML CargarJMenuItem(Iterator j) {
        JMItmp = new JMenuItemXML();
        while (j.hasNext()) {
            Element subdatos = (Element) j.next();
            if (subdatos.getName().equals("Transaction")) {
            	JMItmp.setTransaction(subdatos.getValue());
            } else if (subdatos.getName().equals("Activo")) {
                JMItmp.setActivo(true);
            } else if (subdatos.getName().equals("Acelerator")) {
            } else if (subdatos.getName().equals("ActionCommand"))
                JMItmp.setActionCommand(subdatos.getValue());
            else if (subdatos.getName().equals("Icon"))
            	try {
	                JMItmp.setIcon(new ImageIcon(this.getClass().getResource(
	                        Icons.getIcon(subdatos.getValue()))));
            	}
            	catch (NullPointerException NPEe) {
            	    JMItmp.setIcon(new ImageIcon(this.getClass().getResource(
	                        subdatos.getValue())));
            	}
            else if (subdatos.getName().equals("Text"))
                JMItmp.setText(Language.getWord(subdatos.getValue()));
            else if (subdatos.getName().equals("Mnemonic"))
                JMItmp.setMnemonic(Language.getNemo(subdatos.getValue()));
            else if (subdatos.getName().equals("Clase")) {
                JMItmp.setClassName(subdatos.getValue());
            } else if (subdatos.getName().equals("ArgConstructor")) {
                java.util.List Largs = subdatos.getChildren();
                JMItmp.setArgConstructor(ArgConstructor(Largs));
            } else if (subdatos.getName().equals("TypeArgConstructor")) {
                java.util.List Largs = subdatos.getChildren();
                JMItmp.setTypeArgConstructor(TypeArgConstructor(Largs));
            } else if (subdatos.getName().equals("Metodo")) {
                JMItmp.setMethod(subdatos.getValue());
            } else if (subdatos.getName().equals("JMenu")) {
                java.util.List Ljmenuitem = subdatos.getChildren();
                Iterator k = Ljmenuitem.iterator();
                JMItmp.add(CargarJMenu(k));
            }
        }
        Items.addElement(JMItmp);
        return JMItmp;
    }

    /**
     * Metodo encargado de cargar los argumentos de los constructores de las
     * clases en caso de que estas tengan argumentos
     */

    private Object[] ArgConstructor(java.util.List Largs) {
        if (Largs.size() > 0) {
            Object[] args = new Object[Largs.size()];
            Iterator j = Largs.iterator();
            int i = 0;
            while (j.hasNext()) {
                Element Earg = (Element) j.next();
                args[i] = Earg.getValue();
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

    private Class[] TypeArgConstructor(java.util.List Largs) {
        if (Largs.size() > 0) {
            Class[] typeargs = new Class[Largs.size()];
            Iterator j = Largs.iterator();
            int i = 0;
            while (j.hasNext()) {
                Element Earg = (Element) j.next();
                if (Earg.getName().equals("Int"))
                    typeargs[i] = int.class;
                if (Earg.getName().equals("String"))
                    typeargs[i] = String.class;
                i++;
            }
            return typeargs;
        }
        return null;
    }

    /**
     * Este metodo se encarga de habilitar todos los items del menu
     */

    public void setEnabledAll() {
        for (int i = 0; i < Items.size(); i++) {
            Items.elementAt(i).setEnabled(true);
            Items.elementAt(i).setVisible(true);
        }
    }

    /**
     * Este metodo se encarga de deshabilitar todos los items del menu,
     * exceptuando los que tienn la etiqueta activos
     */

    public void setDisabledAll() {
        for (int i = 0; i < Items.size(); i++) {
            if (!Items.elementAt(i).getActivo()) {
                Items.elementAt(i).setEnabled(false);
            	Items.elementAt(i).setVisible(false);
            }
        }
    }

    /**
     * Este metodo se encarga de habilitar el item que tenga como etiqueta en su
     * actioncommand el parametro recogido como argumento
     */

    public void setEnabled(String actioncommand) {
        for (int i = 0; i < Items.size(); i++) {
            if (Items.elementAt(i).getActionCommand().equals(actioncommand)) {
                Items.elementAt(i).setEnabled(true);
            	Items.elementAt(i).setVisible(true);
            }
        }
    }

    /**
     * Este metodo se encarga de deshabilitar el item que tenga como etiqueta en
     * su actioncommand el parametro recogido como argumento
     */

    public void setDisabled(String actioncommand) {
        for (int i = 0; i < Items.size(); i++) {
            if (Items.elementAt(i).getActionCommand().equals(actioncommand))
                Items.elementAt(i).setEnabled(false);
            	Items.elementAt(i).setVisible(false);
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
	
	public Hashtable getTransactions() {
		return transactions;
	}
	
	public void setTransactions(Hashtable transactions) {
		Enumeration e = transactions.keys();
		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			this.transactions.put(key,"");
		}
	}
	
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }

	public void arriveACPForm(ACPFormEvent e) {
		if (transactions.containsKey(e.getTransaction())) {
			setEnabled(true);
			setVisible(true);
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
    private String ClassName = null;
    private String method;
    private boolean activo;
    private Object[] ArgConstructor = null;
    private Class[] TypeArgConstructor = null;

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

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Este metodo retorna si el menu puede ser desactivado o no
     */

    public boolean getActivo() {
        return activo;
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
        this.ClassName = ClassName;
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
        this.ArgConstructor = ArgConstructor;
    }

    /**
     * Este metodo se encarga de almacenar el arreglo de Tipos de Argumentos
     * para el constructor
     */

    public void setTypeArgConstructor(Class[] TypeArgConstructor) {
        this.TypeArgConstructor = TypeArgConstructor;
    }

    /**
     * Metodo encargado de ejecutar el evento de accion
     */

    public void actionPerformed(ActionEvent e) {
    	class ActionOption extends Thread {
    		public void run() {
		        if (ClassName!=null) {
			        try {
			            Class cls = Class.forName(ClassName);
			            if (method == null) {
			                if (TypeArgConstructor != null)
			                    validarArgumentos();
			
			                Constructor cons = cls.getConstructor(TypeArgConstructor);
			                cons.newInstance(ArgConstructor);
			            } else {
			                Method meth = cls.getMethod(method, new Class[] {});
			                meth.invoke(cls.newInstance(), new Object[]{});
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
		            size.height = MainWindow.getAncho();
		            size.width = MainWindow.getAlto();
		            
		            new GenericForm(ACPHandler.getDocForm(transaction),
		                    						   MainWindow.getJDPanel(),
		                    						   ClientConst.KeyClient,
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

    private void validarArgumentos() {
        /**
         * Por el momento solo manejo argumentos de tipo entero, una vez existan
         * de otro tipo se formatearan adicionando sentencias if
         */
        for (int i = 0; i < TypeArgConstructor.length; i++) {
            if (TypeArgConstructor[i].getName().equals("int"))
                ArgConstructor[i] = new Integer(ArgConstructor[i].toString());
            if (TypeArgConstructor[i].getName().equals("String"))
                ArgConstructor[i] = ArgConstructor[i].toString();
        }
    }

	public void arriveACPForm(ACPFormEvent e) {
		if (e.getTransaction().equals(transaction)) {
			setEnabled(true);
			setVisible(true);
	     //   ACPHandler.removeACPFormListener(this);
		}
	}

	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }

}