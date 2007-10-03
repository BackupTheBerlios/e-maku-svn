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
 * LoadToolbarXML.java
 *
 * Created on 25 de marzo de 2004, 14:38
 */

/**
 * Esta clase se encarga de generar de forma dinamica y recursiva los Menus de
 * E-Maku o JMServer, dependiendo de la informaci?n obtenida de un archivo XML
 */

package client.gui.xmltoolbar;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.util.List;

import common.misc.language.Language;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ToolBarLoader extends JPanel {

	private static final long serialVersionUID = 6352754285306056099L;

	private EmakuButtonGroup button;
    private URL xmlFile;
    private Vector<EmakuButtonGroup> items;

    public ToolBarLoader() {

    }

    public ToolBarLoader(URL xmlFile) {
        this.xmlFile = xmlFile;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        //this.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    public Vector load() {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(xmlFile);
            Element root = doc.getRootElement();
            List toolBarList = root.getChildren("JToolBar");
            Iterator i = toolBarList.iterator();
            items = new Vector<EmakuButtonGroup>();

            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML,
             * en este caso JToolBar
             */

            while (i.hasNext()) {
                Element element = (Element) i.next();
                List dataList = element.getChildren();
                Iterator j = dataList.iterator();
                /**
                 * Se adicionara todos los menus principales a la clase
                 * ToolbarLoader
                 */
                this.add(loadJToolbar(j));
            }
        }
        catch (JDOMException JDOMEe) {
            System.out.println(JDOMEe.getMessage());
        }
        catch (IOException IOEe) {
            System.out.println(IOEe.getMessage());
        }

        return items;
    }

    /**
     * Metodo encargado de cargar las Toolbar al JPanel
     */

    private JToolBar loadJToolbar(Iterator j) {
        JToolBar toolBar = new JToolBar();
        while (j.hasNext()) {
            Element element = (Element) j.next();
            List menuItemList = element.getChildren();
            Iterator k = menuItemList.iterator();
            toolBar.add(loadJButton(k));
        }
        return toolBar;
    }

    /**
     * Metodo encargado de cargar los Botones al JToolBar
     */

    private EmakuButtonGroup loadJButton(Iterator j) {
        button = new EmakuButtonGroup();
        while (j.hasNext()) {
            Element element = (Element) j.next();
            if (element.getName().equals("Activo")) {
                button.setActivo(true);
            }
            /* else if (element.getName().equals("Acelerator")) {
            } */
            else if (element.getName().equals("ActionCommand")) {
                button.setActionCommand(element.getValue());
            }
            else if (element.getName().equals("Icon")) {
            	try {
            		button.setIcon(new ImageIcon(this.getClass().getResource(element.getValue())));
            	}
            	catch(NullPointerException NPEe) {
            		System.out.println(element.getValue());
            		NPEe.printStackTrace();
            	}
            }
            else if (element.getName().equals("ToolTipText")) {
                button.setToolTipText(Language.getWord(element.getValue()));
            }
            else if (element.getName().equals("Transaction")) {
                button.setTransaction(element.getValue());
            }
            else if (element.getName().equals("Clase")) {
                button.setClassName(element.getValue());
            }
            else if (element.getName().equals("ArgConstructor")) {
                List argsList = element.getChildren();
                button.setArgConstructor(getConstructorArgs(argsList));
            }
            else if (element.getName().equals("TypeArgConstructor")) {
                List argsList = element.getChildren();
                button.setTypeArgConstructor(getConstructorArgsType(argsList));
            }
            else if (element.getName().equals("Metodo")) {
                button.setMethod(element.getValue());
            }
        }
        items.addElement(button);
        return button;
    }

    /**
     * Metodo encargado de cargar los argumentos de los constructores de las
     * clases en caso de que estas tengan argumentos
     */

    private Object[] getConstructorArgs(List argsList) {
        if (argsList.size() > 0) {
            Object[] args = new Object[argsList.size()];
            Iterator j = argsList.iterator();
            int i = 0;
            while (j.hasNext()) {
                Element element = (Element) j.next();
                args[i] = element.getValue();
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

    private Class[] getConstructorArgsType(List argsList) {
        if (argsList.size() > 0) {
            Class[] argsTypes = new Class[argsList.size()];
            Iterator j = argsList.iterator();
            int i = 0;
            while (j.hasNext()) {
                Element element = (Element) j.next();
                if (element.getName().equals("Int"))
                    argsTypes[i] = int.class;
                if (element.getName().equals("String"))
                    argsTypes[i] = String.class;
                i++;
            }
            return argsTypes;
        }
        return null;
    }
}