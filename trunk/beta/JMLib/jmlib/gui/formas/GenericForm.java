package jmlib.gui.formas;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.beans.PropertyVetoException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import jmlib.comunicaciones.SendDATE;
import jmlib.comunicaciones.SendUPDATECODE;
import jmlib.comunicaciones.SocketConnect;
import jmlib.comunicaciones.WriteSocket;
import jmlib.gui.components.VoidPackageException;
import jmlib.miscelanea.Icons;
import jmlib.miscelanea.idiom.Language;
import jmlib.miscelanea.parameters.GenericParameters;
import jmlib.transactions.STException;
import jmlib.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * GenericForm.java Creado el 22-sep-2004
 * 
 * Este archivo es parte de JMServerII <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase es la encargada de interpretar los documentos <FORM>para generar
 * una ventana basada en componentes. Esta ventana se arma en tiempo de
 * ejecucion <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 */

public class GenericForm extends JInternalFrame{

    /*
     * Un evento puede llamar varios metodos de componentes distintos, estos se
     * almacenan en esta tabla hash.
     */

	private static final long serialVersionUID = 6473569902070432824L;
	private Hashtable <String,Componentes>Hcomps;
    private JDesktopPane JDPpanel;
    private Dimension size;
    private boolean child;
    private Hashtable <Object,Object>externalValues;

    /*
     * En esta variable se almacena el id de la transaccion que genera la forma,
     * este parametro es recibido por referencia
     */

    private String idTransaction = null;

    /*
     * Si la transaccion a ejecutarce necesita de un password, este es recibido
     * por referencia y almacenado en esta variable
     */
    private String password = null;
    private Vector <InitiateFinishListener>initiateFinishListener = new Vector<InitiateFinishListener>();

    /*
     * variables encargadas de solicitar un paquete sea de fecha o un 
     * consecutivo para un documento
     */
    
    private boolean date;
    private String consecutive;
    
    /*
     * Si una forma necesita parametrizar por defecto algunas llaves para 
     * la generacion de una transaccion, estas llaves se parametrizan en
     * la forma principal y se iran adicionando a un vector de llaves 
     */
    
    private Vector <String>keys;
    
     /*
     * Este objeto se lo utiliza cuando se instancia una forma hija
     */
    
    private GenericForm GFforma;

    /*
     * Este objeto se lo utiliza para generar eventos por cambios de valores exportados
     */
    
    //private ChangeExternalValueEvent CEVevent;
    private Vector <ChangeExternalValueListener>changeExternalValueListener = new Vector<ChangeExternalValueListener>();
    private Hashtable <Integer,String>exportFields;
	private boolean visible = true;
    
    /**
     * 
     * este construcor se instancia cuando se generara una forma hija
     * dentro de una forma principal, u otra forma hija.
     * 
     */
    
    public GenericForm(GenericForm GFforma,Element elm) {
    	this.GFforma=GFforma;
        this.JDPpanel = this.GFforma.getJDPpanel();
        this.size = this.GFforma.getSize();
        this.idTransaction = this.GFforma.getIdTransaction();
        this.password = this.GFforma.getPassword();
        this.child = true;
        Element e = (Element)elm.clone();
        Document form = new Document();
        form.setRootElement(e);
        generar(form);
        
        FinishEvent event = new FinishEvent(this);
        notificando(event);
    }
    
    /**
     * Este constructor se instancia, cuando la forma recibe aparte de los
     * parametros comunes un password adiciona.
     * 
     * @param doc
     *            Este parametro contiene la informacion para autogenerar la
     *            forma en tiempo de ejecuion
     * @param idTransaction
     *            Este parametro contiene el identicador de la transaccion que
     *            va a generar la forma
     */

    public GenericForm(Document doc, JDesktopPane JDPpanel, 
            Dimension size, String idTransaction, String password) {
        this.JDPpanel = JDPpanel;
        this.size = size;
        this.idTransaction = idTransaction;
        this.password = password;
        this.externalValues = new Hashtable<Object,Object>();
        generar(doc);
        FinishEvent event = new FinishEvent(this);
        notificando(event);
    }

    /**
     * Este constructor se instancia, cuando la forma no recibe password
     * 
     * @param doc
     *            Este parametro contiene la informaci�n para autogenerar la
     *            forma en tiempo de ejecucion
     * @param idTransaction
     *            Este parametro contiene el identificador de la transaccion que
     *            va a generar la forma
     */

    public GenericForm(Document doc, JDesktopPane JDPpanel, String key,
            Dimension size, String idTransaction) {
        this.JDPpanel = JDPpanel;
        this.size = size;
        this.idTransaction = idTransaction;
        this.externalValues = new Hashtable<Object,Object>();
        generar(doc);
        FinishEvent event = new FinishEvent(this);
        notificando(event);
    }

    /**
     * Este metodo se encarga de construir la forma basandoce en el documento
     * recibido
     * 
     * @param doc
     *            Este parametro contiene la informaci�n para autogenear la
     *            forma en tiempo de ejecucion
     */

    private void generar(Document doc) {
    	exportFields = new Hashtable<Integer,String>();
        Hcomps = new Hashtable<String,Componentes>();
        

        Element elm = doc.getRootElement();
        List listaRaiz = elm.getChildren();

        Iterator i = listaRaiz.iterator();

        this.getContentPane().setLayout(new BorderLayout());

        /*
         * Este ciclo lee las etiquetas principales para la autogeneracion de la
         * forma
         */

        while (i.hasNext()) {

            Element e = (Element) i.next();
            String nombre = e.getName();

            /*
             * Si la etiqueta encontrada es preferences, se llama al metodo
             * preferences para validar las preferencias de la forma
             */

            if (nombre.equals("preferences")) {
                if (!preferences(e)) {
                    JOptionPane.showInternalMessageDialog(JDPpanel,
                            							  Language.getWord("ERR_LOCAL_PARAMETERS"),
                            							  Language.getWord("ERROR_MESSAGE"),
                            							  JOptionPane.ERROR_MESSAGE);
                    this.close();
                    try {
                        this.finalize();
                    }
                    catch (Throwable e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    return;
                }
            }

            /*
             * Las siguientes etiquetas son las que definen como se visualizara
             * la forma y el numero de componentes que ella tendra
             */

            /*
             * Si esta etiqueta es encontrada, se instanciara un panel, y se
             * ubicara deacuerdo al valor que tenga el atributo locate, por
             * defecto la forma tendra como contenedor un BorderLayout
             */

            else if (nombre.equals("panel")) {

                String locate = e.getAttributeValue("locate");
                this.getContentPane().add((Component) Panel(e), locate);

            }

            /*
             * Si esta etiqueta es encontrada, se cargara un componente y se
             * ubicara en la posicion asignada por el atributo locate
             */

            else if (nombre.equals("component")) {

                String locate = e.getAttributeValue("locate");
                if (locate!=null) {
                	this.getContentPane().add(Component(e), locate);
                }
                else {
                	Component(e);
                }
            }

            /*
             * Si esta etiqueta es encontrada, se generara un tab para almacenar
             * tcomponents
             */

            else if (nombre.equals("tab")) {
                String locate = e.getAttributeValue("locate");
                this.getContentPane().add((Component) Tab(e), locate);
            }

            /*
             * Si esta etiqueta es encontrada, se genera una caja de relleno
             */
            
            else if (nombre.equals("box")) {
                String locate = e.getAttributeValue("locate");
                this.getContentPane().add((Component) Box(e), locate);
            }
            /* Si se encuentra esta etiqueta, se procede a generar una subforma */
            else if (nombre.equals("FORM")) {
            	makeSubForm(e);
            }

        }
        this.setVisible(visible);
        if (child) {
        	this.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        }
        JDPpanel.add(this);

        try {
            this.setSelected(true);
        }
        catch (java.beans.PropertyVetoException PVEe) {
        }

    }

    private void makeSubForm(Element e) {
    	class MakeSubForm extends Thread {
    		GenericForm fforma;
    		Element e;
    		public MakeSubForm(GenericForm fforma,Element e) {
				this.fforma = fforma;
				this.e= e;
			}

			public void run() {
    			GenericForm GFforma = new GenericForm(fforma,e);
    	        setComps(e.getChild("preferences").getChildText("id"),new Componentes(GenericForm.class,GFforma));
    		}
    	}
    	new MakeSubForm(this,e).start();
	}

	/**
     * Metodo encargado de validar las preferncias de la forma
     * 
     * @param elm
     *            contiene el elemento con las preferencias de la forma
     */

    private boolean preferences(Element elm) {

        List Lcomponent = elm.getChildren();
        Iterator i = Lcomponent.iterator();
        keys = new Vector<String>();
        
        /*
         * Este ciclo valida las etiqueta que asignan las preferencias de la
         * forma
         */

        while (i.hasNext()) {

            Element e = (Element) i.next();
            String nombre = e.getName();

            /*
             * Interpreta las dimensiones de la forma
             */

            if (nombre.equals("size")) {
                int ancho = Integer.parseInt(e.getAttributeValue("width"));
                int alto = Integer.parseInt(e.getAttributeValue("height"));
                this.setBounds((size.height / 2) - (ancho / 2),
                        (size.width / 2) - (alto / 2), ancho, alto);

            }

            /*
             * Almacena el titulo de la forma
             */

            else if (nombre.equals("name")) {
                this.setTitle(Language.getWord(e.getValue()));
            }

            /*
             * Almacena el icono de la forma
             */

            else if (nombre.equals("icon")) {
                try {
	                this.setFrameIcon(new ImageIcon(this.getClass().getResource(
	                        Icons.getIcon(e.getValue()))));
                }
                catch(NullPointerException NPEe) {
                    NPEe.printStackTrace();
                }
            }

            /*
             * Si esta etiqueta es encontrada, activara la propiedad de
             * maximizacion de la forma
             */
            else if (nombre.equals("maximizable")) {
                this.setMaximizable(true);

            }

            /*
             * Si esta etiqueta es encontrada, activara la propiedad de
             * iconificacion de la forma
             */

            else if (nombre.equals("iconificable")) {
                this.setIconifiable(true);
            }

            /*
             * Si esta etiqueta es encontrada, activara la propiedad de
             * redimensio de la forma
             */

            else if (nombre.equals("resizable")) {
                this.setResizable(true);

            }

            /*
             * Si esta etiqueta es encontrada, activara la opcion de cierre de
             * la forma
             */

            else if (nombre.equals("closable")) {
                this.setClosable(true);

            }
            
            else if (nombre.equals("visible")) {
            	visible = Boolean.parseBoolean(e.getValue());
            }
            
            /*
             * Si esta etiqueta es encontrada, entonces se solicitara la fecha
             * al servidor de transacciones, una vez terminada la instanciacion de 
             * todos los componentes
             */
            
            else if (nombre.equals("date")) {
                this.date=true;
            }
            
            /*
             * Si esta etiqueta es encontrada, entonces se solicitara el consecutivo
             * del documento solicitado deacuerdo al id del documento recibido
             */
            
            else if (nombre.equals("consecutive")) {
                this.consecutive=e.getValue();
            }
            
     
            /*
             * Si esta etiqueta es encontrada, entonces se adiciona una llave al 
             * vector de llaves
             */
            
            else if (nombre.equals("key")) {
                this.keys.addElement(e.getValue());
            }
            
            /*
             * Si esta etiqueta es encontrada se procede a evaluar que la configuracion local
             * coincida con la parametrizada en el Servidor de Transacciones, si no es asi,
             * se aborta el despliegue de la ventana y se saca un mensaje de error informando
             * que los parametros no coinciden por tanto la forma no puede ser ejecutada 
             * desde la terminal lanzada.
             */
            
            else if (nombre.equals("localConfigEquals")) {
                
                boolean sourceSQL = false;
                String keyLocal = "";
                String arg = null;
                String valueQuery="";
                
                if ("SQL".equals(e.getAttributeValue("sourceValue"))) {
                    sourceSQL=true;
                }
                
                if (e.getAttributeValue("keyLocal") != null) {
                    keyLocal = e.getAttributeValue("keyLocal");
                }
                
                if ("USER".equals(e.getAttributeValue("arg"))) {
                    arg = GenericParameters.getParameter("userLogin");
                }
                else {
                    arg = e.getAttributeValue("arg");
                    
                }
                
                
                if (sourceSQL) {
                    try {
	    	            Document doc = null;
	    	            if (arg == null) {
	        	            doc = STResultSet.getResultSetST(e.getValue());
	    	            }
	    	            else {
	        	            doc = STResultSet.getResultSetST(e.getValue(),new String[]{arg.trim()});
	        	        }
	    	            
	    	            
	    	            if (doc==null) {
   	    	                return false;
	    	            }
	    	            else {
	    	                
		    	            Iterator j = doc.getRootElement().getChildren("row").iterator();
		    	            int row = doc.getRootElement().getChildren("row").size();
		    	            if (row>0) {
		    	                while (j.hasNext()) {
					                Element f = (Element) j.next();
				                    Iterator k = f.getChildren().iterator();
				                    while (k.hasNext()) {
				                        Element g = (Element)k.next();
				                        valueQuery=g.getValue();
				                    }
					            }
					            return GenericParameters.equals(keyLocal,valueQuery);
		    	            }
		    	            else {
		    	                return false;
		    	            }
	    	            }
                    }
                    catch(STException STEe) {
                        STEe.printStackTrace();
                        return false;
                    }
                }
                else {
                    return GenericParameters.equals(keyLocal,e.getValue());
                }
            }
        }
        return true;
    }

    /**
     * Este metodo generara los componentes deacuerdo a sus parametros y los
     * retornara al metodo desde el que fue llamado.
     * 
     * @param elm
     *            contiene los parametros de configuracion del componente
     * @return retorna el componente generado
     */

    private Component Component(Element elm) {

        List Lcomponent = elm.getChildren();
        Iterator i = Lcomponent.iterator();

        /*
         * Un componentes esta definido por su driver, metodo a ejecutar si
         * existe, argumentos del constructor si los necesita, y argumentos del
         * metodo a invocar si tambien los necesita.
         */

        String driver = null;
        String idDriver ="";
        String method = null;
        String events = null;

        Object[] args_constructor = new Object[1];
        Object[] args_method = null;
        Object[] events_method = null;

        Class[] type_args = new Class[] {};
        Class[] type_args_method = new Class[] {};
        Class[] type_events_method = new Class[] {};

        /*
         * Este ciclo valida las etiqueta que arman el Elemento component
         */

        while (i.hasNext()) {

            Element e = (Element) i.next();
            String nombre = e.getName();

            /*
             * Esta etiqueta contiene el driver o clase que se instanciara como
             * componente
             */

            if (nombre.equals("driver")) {
                driver = e.getValue();
                if (e.getAttributeValue("id")!=null) {
                    idDriver=e.getAttributeValue("id");
                }
            }

            /*
             * Si el driver o clase necesita ejecutar uno o varios metodos,
             * ellos seran defini dos con la siguiente etiqueta
             */

            else if (nombre.equals("method")) {
                method = e.getValue();
            }

            /*
             * Si el constructor del driver o clase tiene argumentos, ellos
             * seran definidos en un sub-elemento con la etiqueta parameters,
             * todos los paremetros recibidos sea por un metodo o por un
             * constructor, deben ser de tipo Document (Document almacena una
             * estructura xml)
             */

            else if (nombre.equals("parameters")) {
                Document args_cons = new Document();
                args_cons.setRootElement((Element) e.clone());
                args_constructor = new Object[] { this, args_cons };
            }

            /*
             * Si el metodo a ejecutarce, necesita de argumentos, ellos seran
             * definidos en un sub-elemento con la etiqueta parameters-method.
             */

            else if (nombre.equals("parameters-method")) {
                Document args_meth = new Document();
                args_meth.setRootElement((Element) e.clone());
                args_method = new Object[] { args_meth };
                type_args_method = new Class[] { Document.class };
            }

            /*
             * Este sub-elemento contiene informaci�n necesaria para ejecutar
             * eventos, que tienen relacion con otros componentes diferentes al
             * que se esta generando. En ella se definen las llaves necesarias
             * para luego poder llamar a dicho metodo del elemento en cuestion
             */

            else if (nombre.equals("events")) {
                events = "setEvents";
                Document eventos = new Document();
                eventos.setRootElement((Element) e.clone());
                events_method = new Object[] { eventos };
                type_events_method = new Class[] { Document.class };

            }
        }

        /*
         * Una vez cargada la anterior informaci�n, se procede a instanciar el
         * componente
         */

        try {
            if (driver != null) {
                Class cls = Class.forName(driver);
                if (args_constructor.length > 1) {
                    type_args = new Class[] { GenericForm.class, Document.class };
                } else {
                    type_args = new Class[] { GenericForm.class };
                    args_constructor = new Object[] { this };
                }
                
                Constructor cons = cls.getConstructor(type_args);
                Object obj = cons.newInstance(args_constructor);

                if (method != null) {

                    Method meth = cls.getMethod(method, type_args_method);
                    meth.invoke(obj, args_method);
                } else if (events_method != null) {
                    Method event_meth = cls.getMethod(
                    								events,
                    								type_events_method);
                    event_meth.invoke(obj, events_method);
                }

                /*
                 * Todos los componentes generados, se almacenaran en una tabla
                 * hash, para asi poder tener referencia a ellos y poder
                 * ejecutar metodos desde un componente a otro.
                 */
                setComps(driver+idDriver, new Componentes(cls, obj));
                
                /*
                 * por ultimo se invoca el metodo getPanel del componente
                 * instanciado, el retornara el objeto construido apartir de la
                 * informacion anterior. Todos los objetos deben ser de tipo
                 * JPanel, por ello se amolda de esta forma antes de ser
                 * retornado.
                 */
                Method getPanel = cls.getMethod("getPanel", new Class[]{});

                /*
                 * Se retorna el objeto generado
                 */
                return (Component) getPanel.invoke(obj, new Object[]{});
            } else {
                return null;
            }
        }

        /*
         * Se define las excepciones del caso y rogemos a dios que ninguna de
         * estas ocurra a menos de que el documento este mal construido. El
         * codigo es bueno, no tienen porque..... ;-)
         */

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
            JOptionPane.showInternalMessageDialog(JDPpanel,
                    ITEe.getCause().getMessage(), Language
                            .getWord("ERROR_MESSAGE"),
                    JOptionPane.ERROR_MESSAGE);
            ITEe.printStackTrace();
        }

        /*
         * En caso de que el procedimiento anterior no terminara con exito, se
         * retorna null :'-(
         */

        return null;
    }

    private void setComps(String key, Componentes componente) {
        /*
         * Antes de adicionar los componentes en una tabla, debemos verificar
         * si la forma generada es una forma principal o es una forma hija,
         * deacuardo a ello se distribuiran en su correspondiente tabla.
         */

        if (child) {
        	GFforma.setComps(key,componente);
        }
        else {
        	Hcomps.put(key,componente);		
        }
	}

    private Componentes getComps(String key) {
    	if (child) {
        	return GFforma.getComps(key);
        }
        else {
        	return Hcomps.get(key);		
        }
    }
    
    private boolean containsComponent(String key) {
    	
    	if (child) {
        	return GFforma.containsComponent(key);
        }
        else {
        	return Hcomps.containsKey(key);		
        }
    }
    
	/**
     * Este metodo es utilizado para llamar metodos de de componentes que no
     * pueden ser referenciados por quien lo solicita. (Es con c o con S :-S)
     */
    public Object invokeMethod(String driver, String method,
            Class[] type_args_method, Object[] args_method)
            throws InvocationTargetException,NotFoundComponentException {
        if (driver.equals("this") || containsComponent(driver)) {
            Class cls;
            Object obj;
                
            if (driver.equals("this")) {
                cls = this.getClass();
                obj = this;
            }
            else {
	            Componentes comp = getComps(driver);
		        cls = comp.getCls();
		        obj = comp.getObj();
            }	        
	        Object retorno = null;
	        Method meth;
	
	        try {
	            meth = cls.getMethod(method, type_args_method);
	            retorno = (Object) meth.invoke(obj, args_method);
	        }
	        catch (SecurityException e) {
	            e.printStackTrace();
	        }
	        catch (NoSuchMethodException e) {
	            e.printStackTrace();
	        }
	        catch (IllegalArgumentException e) {
	            e.printStackTrace();
	        }
	        catch (IllegalAccessException e) {
	            e.printStackTrace();
	        }
            return retorno;
	        
        }
        else {
            throw new NotFoundComponentException(driver);
        }
    }

    public Object invokeMethod(String driver, String method)
            throws InvocationTargetException,NotFoundComponentException {
        return invokeMethod(driver,method,null,null);
    }

    /**
     * Este metodo se encarga de generar una caja de relleno
     */
    private Object Box(Element elm) {
        int lenght;
        int width;
        try {
	        lenght = Integer.parseInt(elm.getAttributeValue("length"));
	    }
        catch(NumberFormatException NFEe) {
            lenght=0;
        }
        try {
	        width = Integer.parseInt(elm.getAttributeValue("width"));
	    }
        catch(NumberFormatException NFEe) {
            width=0;
        }
        return Box.createRigidArea(new Dimension(width,lenght));
    }
    
    /**
     * Este metodo se encarga de generar panel de componenetes
     */
    private Object Panel(Element elm) {

        JPanel panel = new JPanel();

        // type dice que tipo de layout se manejara en el panel
        panel.setLayout(typeLayout(elm));
        List listaPanel = elm.getChildren();
        Iterator i = listaPanel.iterator();
        String title = elm.getAttributeValue("border");
        if (title!=null && !"".equals(title)) {
        	TitledBorder border = new TitledBorder(BorderFactory.createEtchedBorder());
        	String font = elm.getAttributeValue("font");
        	border.setTitle(Language.getWord(title));
        	border.setTitleFont(Font.decode(font));
        	border.setTitleJustification(TitledBorder.CENTER);
            panel.setBorder(border);
        }
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String nombre = e.getName();
            if (nombre.equals("component")) {
                String locate = e.getAttributeValue("locate");
                if (locate != null)
                    panel.add((Component) Component(e), locate);
                else
                    panel.add((Component) Component(e));
            } else if (nombre.equals("panel")) {
                String locate = e.getAttributeValue("locate");
                if (locate != null)
                    panel.add((Component) Panel(e), locate);
                else
                    panel.add((Component) Panel(e));
            } else if (nombre.equals("box")) {
                String locate = e.getAttributeValue("locate");
                if (locate != null)
                    panel.add((Component) Box(e), locate);
                else
                    panel.add((Component) Box(e));
            }
            
            /*
             * Si esta etiqueta es encontrada, se generara un tab para almacenar
             * tcomponents
             */

            else if (nombre.equals("tab")) {
                String locate = e.getAttributeValue("locate");
                panel.add((Component) Tab(e), locate);
            }
        }

        return panel;
    }

    /**
     * Este metodo se encarga de generar Tab de componentes
     */
    private Object Tab(Element elm) {

        JTabbedPane JTPtab = new JTabbedPane();

        List listaPanel = elm.getChildren();
        Iterator i = listaPanel.iterator();

        while (i.hasNext()) {
            Element e = (Element) i.next();
            String nombre = e.getName();
            String name = e.getAttributeValue("name");
            String icon = e.getAttributeValue("icon");
            if (nombre.equals("panel")) {
	            if (icon != null)
	                try {
	                    JTPtab.addTab(Language.getWord(name), new ImageIcon(this
	                            .getClass().getResource(icon)),
	                            (Component) Panel(e));
	                }
	                catch (NullPointerException NPEe) {
	                    try {
	                        JTPtab.addTab(
	                        		Language.getWord(name), 
	                        		new ImageIcon(this.getClass().getResource(Icons.getIcon(icon))),
	                                (Component) Panel(e));
	                    }
	                    catch (NullPointerException NPEe2) {
	                        JTPtab.addTab(Language.getWord(name),(Component) Panel(e));
	                    }
	                }
	            else {
	                JTPtab.addTab(Language.getWord(name), (Component) Panel(e));
	            }
            }
            else if (nombre.equals("component")) {
	            if (icon != null)
	                try {
	                    JTPtab.addTab(
	                    		Language.getWord(name),
	                    		new ImageIcon(this.getClass().getResource(icon)),
	                            (Component) Component(e));
	                }
	                catch (NullPointerException NPEe) {
	                    try {
	                        JTPtab.addTab(Language.getWord(name), new ImageIcon(
	                                this.getClass()
	                                        .getResource(Icons.getIcon(icon))),
	                                (Component) Component(e));
	                    }
	                    catch (NullPointerException NPEe2) {
	                        JTPtab.addTab(Language.getWord(name),
	                                (Component) Component(e));
	                    }
	                }
	            else
	                JTPtab.addTab(name, (Component) Component(e));
            }
        }

        return JTPtab;
    }

    public void setEnabledButton(String name,boolean bool) {
        try {
            if (bool)
	            invokeMethod("jmlib.gui.components.PanelButtons", 
                			 "setEnabled",
                			 new Class[] {String.class,boolean.class},
                			 new Object[] {name,Boolean.TRUE});
            else
                invokeMethod("jmlib.gui.components.PanelButtons", 
		           			 "setEnabled",
		           			 new Class[] {String.class,boolean.class},
		           			 new Object[] {name,Boolean.FALSE});

        }
        catch (InvocationTargetException ITEe) {
            JOptionPane.showInternalMessageDialog(getDesktopPane(),
                    							  ITEe.getCause().getMessage(), 
                    							  Language.getWord("ERROR_MESSAGE"),
                    							  JOptionPane.ERROR_MESSAGE);
        }
        catch (NotFoundComponentException NFCEe) {
            JOptionPane.showInternalMessageDialog(getDesktopPane(),
                    							  NFCEe.getMessage(), 
                    							  Language.getWord("ERROR_MESSAGE"),
                    							  JOptionPane.ERROR_MESSAGE);
        }
        
    }
    /**
     * Este metodo adiciona el LayoutManager que manejara el panel
     * 
     * @param type
     *            define el tipo de Layout
     * @return return el LayoutManager
     */

    private LayoutManager typeLayout(Element elm) {
    	String type = elm.getAttributeValue("type");
        if (type.equals("BorderLayout")) {
            return new BorderLayout();
        } else if (type.equals("FlowLayout")) {
        	int align = FlowLayout.RIGHT;
        	if ("center".equals(elm.getAttributeValue("align"))) {
        		align = FlowLayout.CENTER;
        	}
        	else if ("left".equals(elm.getAttributeValue("align"))) {
        		align = FlowLayout.LEFT;
        	} 
        	else if ("right".equals(elm.getAttributeValue("align"))) {
        		align = FlowLayout.RIGHT;
        	}
            return new FlowLayout(align);
        } else if (type.equals("GridLayout")) {
        	int rows=0;
        	int cols=0;
        	try {
        		rows=Integer.parseInt(elm.getAttributeValue("rows"));
        		cols=Integer.parseInt(elm.getAttributeValue("cols"));
        	}
        	catch(NumberFormatException NFEe) {
        		rows=0;
        		cols=0;
        	}
            return new GridLayout(rows,cols);
        }
        return null;
    }
    
    public Element getKeys() {
        Element pack = new Element("package");
        for (int i=0;i<keys.size();i++) {
            Element field = new Element("field");
            field.setAttribute("attribute","key");
            field.setText(keys.get(i));
            pack.addContent(field);
        }
        return pack;
    }

    public void sendTransaction(Document doc) {
        SocketChannel socket = SocketConnect.getSock();
        WriteSocket.writing(socket, doc);
    }

    public void close() {
        JDPpanel.remove(this);
        GFforma =null;
        JDPpanel.repaint();
    }

    public String getIdTransaction() {
        return idTransaction;
    }

    public String getPassword() {
        return password;
    }
    
    public synchronized void addInitiateFinishListener(InitiateFinishListener listener ) {
   		initiateFinishListener.addElement(listener);
    }

    public synchronized void removeInitiateFinishListener(InitiateFinishListener listener ) {
   		initiateFinishListener.removeElement(listener);
    }

    private synchronized void notificando(FinishEvent event) {
        Vector lista;
        lista = (Vector)initiateFinishListener.clone();
        for (int i=0; i<lista.size();i++) {
            InitiateFinishListener listener = (InitiateFinishListener)lista.elementAt(i);
            listener.initiateFinishEvent(event);
        }
        
        /*
         * Una vez notificados los componentes, se procede a solicitar los paquetes
         * parametrizados sea fecha o el consecutivo de un documento
         */
        
        if (date) {
            sendTransaction(SendDATE.getPackage());
        }
        
        if (consecutive!=null) {
            sendTransaction(SendUPDATECODE.getPackage(consecutive));
        }
    }

    public synchronized void addChangeExternalValueListener(ChangeExternalValueListener listener ) {
    	if (child) {
    		GFforma.addChangeExternalValueListener(listener);
        }
        else {
        	changeExternalValueListener.addElement(listener);
        }
        
    }

    public synchronized void removeChangeExternalValueListener(ChangeExternalValueListener listener ) {
    	if (child) {
    		GFforma.removeChangeExternalValueListener(listener);
        }
        else {
        	changeExternalValueListener.removeElement(listener);
        }
    }

    private void notificandoExternalValue(ChangeExternalValueEvent event) {
    		class notificacionExterna extends Thread {
    			private ChangeExternalValueEvent event;
    			
    			public notificacionExterna(ChangeExternalValueEvent event) {
    				this.event=event;
    			}
    			
    			public void run() {
		       Vector lista;
		        lista = (Vector)changeExternalValueListener.clone();
		        for (int i=0; i<lista.size();i++) {
		            ChangeExternalValueListener listener = (ChangeExternalValueListener)lista.elementAt(i);
		            listener.changeExternalValue(event);
		        }
   				
    			}
    		}
    		new notificacionExterna(event).start();
 
    }

    
	public JDesktopPane getJDPpanel() {
		return JDPpanel;
	}


	public Dimension getSize() {
		return size;
	}

	public void cleanExternalValues() {
		if (child) {
        	GFforma.cleanExternalValues();
        }
        else {
        	externalValues.clear();
    	}
	}
	
	public double getExteralValues(Object key) {
    	if (child) {
        	return GFforma.getExteralValues(key);
        }
        else {
        	try {
        		return ((Double)externalValues.get(key)).doubleValue();
        	}
        	catch(NullPointerException NPEe) {
        		return 0;
        	}
        	catch(NumberFormatException NFEe) {
        		return 0;
        	}
        }
	}

	public String getExteralValuesString(Object key) {
    	if (child) {
        	return GFforma.getExteralValuesString(key);
        }
        else {
        	try {
        		return (String) externalValues.get(key);
        	}
        	catch(NullPointerException NPEe) {
        		return "0";
        	}
        	catch(NumberFormatException NFEe) {
        		return "0";
        	}
        	catch(ClassCastException CCEe) {
        		return ((Double)externalValues.get(key)).toString();
        	}
        }
	}
	
	public void setExternalValues(Object key,double value) {
		if (child) {
        	GFforma.setExternalValues(key,value);
        }
        else {
        	if (externalValues.containsKey(key)) {
        		externalValues.remove(key);
        	}
        	externalValues.put(key,new Double(value));
        	ChangeExternalValueEvent CEVevent = new ChangeExternalValueEvent(this);
        	CEVevent.setExternalValue(String.valueOf(key));
            notificandoExternalValue(CEVevent);
        }
	}
	
	public void setExternalValues(Object key,String value) {
		if (child) {
        	GFforma.setExternalValues(key,value);
        }
        else {
        	if (externalValues.containsKey(key)) {
        		externalValues.remove(key);
        	}
        	externalValues.put(key,value);
        	ChangeExternalValueEvent CEVevent = new ChangeExternalValueEvent(this);
        	CEVevent.setExternalValue(String.valueOf(key));
        	notificandoExternalValue(CEVevent);
        }
	}
	
	public void addExportField(int Codigo,String key) {
		if (child) {
        	GFforma.addExportField(Codigo,key);
        }
        else {
        	if (exportFields.contains(Integer.valueOf(Codigo))) {
        		exportFields.remove(Integer.valueOf(Codigo));
        	}
        	exportFields.put(Integer.valueOf(Codigo),key);
            //notificandoExternalValue(CEVevent);
        }
	}

	public void setVisible(Element elm) {
		boolean visible = Boolean.parseBoolean(elm.getChildText("arg"));
		this.setVisible(visible);
		if (visible) {
			try {
				this.setSelected(visible);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		else {
			GFforma.setVisible(true);
			try {
				GFforma.setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Element getPackage() {
		Element elementXML = new Element("package");
		ArrayList <Integer>v = new ArrayList<Integer>(exportFields.keySet());
	    Collections.sort(v);
	    Iterator it = v.iterator();
	    while (it.hasNext()) {
	       Integer element =  (Integer) it.next();
	       elementXML.addContent(new Element("field").setText(getExteralValuesString(exportFields.get(element))));
	    }
		return elementXML;
	}
	
	public Element getPackage(Element element) throws VoidPackageException {
		

		Element pack = new Element("package");
		boolean blankPackage = false;
		int cont = 0;
		Element arg = element.getChild("arg");
		if (arg!=null && "blanckPackage".equals(arg.getAttributeValue("attribute"))) {
			blankPackage = Boolean.parseBoolean(arg.getValue());
		}
		
		Iterator it = element.getChild("subarg").getChildren().iterator();
		while(it.hasNext()) {
			Element elm = (Element) it.next();
			String value = elm.getValue();
			if ("importValue".equals(elm.getAttributeValue("attribute"))) {
				String text = getExteralValuesString(value);
				String attributeName = null;
				String attributeValue = null;
				if (!"".equals(elm.getAttributeValue("setAttributeName")) &&
					!"".equals(elm.getAttributeValue("setAttributeValue"))) {
					attributeName = elm.getAttributeValue("setAttributeName");
					attributeValue = elm.getAttributeValue("setAttributeValue");
				}

				if (!"".equals(text)) {
					cont++;
				}
				else if (!blankPackage) {
					throw new VoidPackageException(value);
				}
				Element e = new Element("field").setText(text);

				if (attributeName!=null) {
					e.setAttribute(attributeName,attributeValue);
				}
				pack.addContent(e);
			}
		}
		
		if (blankPackage && cont==0) {
			return new Element("package");
		}
		
		return pack;
	}
	
	public void validMultiPackage (Element elm) throws MultiPackageException {
		int size = elm.getChildren().size();
		if ( size > 0 ) {
			
			Iterator it = elm.getChildren().iterator();
			int cont = 0;
			String mensaje="";
			while(it.hasNext()) {
				
				Element element = (Element) it.next();
				String value = element.getValue();
				
				if ("driver".equals(element.getAttributeValue("attribute"))) {
					try {
						String id = element.getAttribute("id")!=null?element.getAttributeValue("id"):"";
						Boolean b;
						b = (Boolean) invokeMethod(value+id,"containData");
						if (b.booleanValue()) {
							cont++;
						}
					}
					catch (InvocationTargetException e) {}
					catch (NotFoundComponentException e) {}

				}
				if ("errorMessage".equals(element.getAttributeValue("attribute"))) {
					mensaje=value;
				}
			}
			if (cont!=1) {
				throw new MultiPackageException(mensaje);
			}
		}
		else {
			throw new MultiPackageException("Inconsistencia en la paramentrizacion del boton");
		}
	}
	
	public boolean containData() {

		Element elm = getPackage();
		Iterator it = elm.getChildren().iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			System.out.println("Field {"+element.getValue()+"}");
			if ("".equals(element.getValue().trim())) {
				return false;
			}
		}
		return true;
	}
}

class Componentes {
    private Class cls;
    private Object obj;

    public Componentes(Class cls, Object obj) {
        this.cls = cls;
        this.obj = obj;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}