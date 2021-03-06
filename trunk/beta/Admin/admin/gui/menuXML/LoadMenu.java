/**
 * Este archivo es parte de E-Maku (http://comunidad.qhatu.net)
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
 *
 *
 * @author  Luis Felipe Hernandez Z.
 * @see e-mail felipe@qhatu.net
 * MenuXML.java
 *
 * Created on 25 de marzo de 2004, 14:38
 */

/**
 * Esta clase se encarga de generar de forma dinamica y recursiva los Menus de
 * E-Maku o JMServer, dependiendo de la información obtenida de un archivo XML
 */

package jmadmin.gui.menuXML;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import jmlib.miscelanea.idiom.Language;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class LoadMenu extends JMenuBar {

    /**
	 * 
	 */
	private static final long serialVersionUID = -363336290161258429L;

	/** Creates a new instance of MenuXML */

    private JMenuItemXML JMItmp;

    private URL FileXML;

    private static Vector Items;

    /**
     * Constructor
     */

    public LoadMenu() {

    }

    /**
     * Constructor con parametro relacionado al archivo XML del menu
     */

    public LoadMenu(URL FileXML) {
        this.FileXML = FileXML;
    }

    /**
     * Metodo encargado de cargar el menu
     */

    public void Loading() {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(FileXML);
            Element raiz = doc.getRootElement();
            java.util.List Ljmenu = raiz.getChildren("JMenu");
            Iterator i = Ljmenu.iterator();
            Items = new Vector();
            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML,
             * en este caso JMenu
             */

            while (i.hasNext()) {
                Element datos = (Element) i.next();

                java.util.List Lsubdatos = datos.getChildren();
                Iterator j = Lsubdatos.iterator();
                /**
                 * Se adicionara todos los menus principales a la clase LoadMenu
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

    private JMenu CargarJMenu(Iterator j) {
        JMenu JMtmp = new JMenu();
        while (j.hasNext()) {
            Element subdatos = (Element) j.next();
            if (subdatos.getName().equals("Text"))
                JMtmp.setText(Language.getWord(subdatos.getValue()));
            else if (subdatos.getName().equals("Mnemonic"))
                JMtmp.setMnemonic(Language.getNemo(subdatos.getValue()));
            else if (subdatos.getName().equals("Icon"))
                JMtmp.setIcon(new ImageIcon(this.getClass().getResource(
                        subdatos.getValue())));
            else if (subdatos.getName().equals("JSeparator"))
                JMtmp.add(new JSeparator());
            else {
                java.util.List Ljmenuitem = subdatos.getChildren();
                Iterator k = Ljmenuitem.iterator();
                if (subdatos.getName().equals("JMenuItem")) {
                    JMtmp.add(CargarJMenuItem(k));
                } else if (subdatos.getName().equals("JMenu")) {
                    /**
                     * LLamado Recursivo a CargarJMenu
                     */
                    JMtmp.add(CargarJMenu(k));
                }
            }
        }
        return JMtmp;
    }

    /**
     * Metodo encargado de cargar los Items al menu padre
     */

    private JMenuItem CargarJMenuItem(Iterator j) {
        JMItmp = new JMenuItemXML();
        while (j.hasNext()) {
            Element subdatos = (Element) j.next();
            if (subdatos.getName().equals("Activo")) {
                JMItmp.setActivo(true);
            } else if (subdatos.getName().equals("Acelerator")) {
            } else if (subdatos.getName().equals("ActionCommand"))
                JMItmp.setActionCommand(subdatos.getValue());
            else if (subdatos.getName().equals("Icon"))
                JMItmp.setIcon(new ImageIcon(this.getClass().getResource(
                        subdatos.getValue())));
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
            ((JMenuItemXML) Items.elementAt(i)).setEnabled(true);
        }
    }

    /**
     * Este metodo se encarga de deshabilitar todos los items del menu,
     * exceptuando los que tienn la etiqueta activos
     */

    public void setDisabledAll() {
        for (int i = 0; i < Items.size(); i++) {
            if (!((JMenuItemXML) Items.elementAt(i)).getActivo())
                ((JMenuItemXML) Items.elementAt(i)).setEnabled(false);
        }
    }

    /**
     * Este metodo se encarga de habilitar el item que tenga como etiqueta en su
     * actioncommand el parametro recogido como argumento
     */

    public void setEnabled(String actioncommand) {
        for (int i = 0; i < Items.size(); i++) {
            if (((JMenuItemXML) Items.elementAt(i)).getActionCommand().equals(
                    actioncommand))
                ((JMenuItemXML) Items.elementAt(i)).setEnabled(true);
        }
    }

    /**
     * Este metodo se encarga de deshabilitar el item que tenga como etiqueta en
     * su actioncommand el parametro recogido como argumento
     */

    public void setDisabled(String actioncommand) {
        for (int i = 0; i < Items.size(); i++) {
            if (((JMenuItemXML) Items.elementAt(i)).getActionCommand().equals(
                    actioncommand))
                ((JMenuItemXML) Items.elementAt(i)).setEnabled(false);
        }
    }
}

/**
 * Clase encargada de generar los JMenuItem y la manipulación de los eventos de
 * cada uno de ellos
 */

class JMenuItemXML extends JMenuItem implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3885778947737386441L;

	private String ClassName;

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
        try {
            Class cls = Class.forName(ClassName);
            if (method == null) {
                if (TypeArgConstructor != null)
                    validarArgumentos();

                Constructor cons = cls.getConstructor(TypeArgConstructor);
                cons.newInstance(ArgConstructor);
            } else {
                Method meth = cls.getMethod(method, new Class[] {});
                meth.invoke(cls.newInstance(), null);
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
}