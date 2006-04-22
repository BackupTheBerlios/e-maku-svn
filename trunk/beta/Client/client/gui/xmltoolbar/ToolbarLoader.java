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

import common.misc.language.Language;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ToolbarLoader extends JPanel {


    /**
	 * 
	 */
	private static final long serialVersionUID = 6352754285306056099L;

	private JButtonXML JBtmp;

    private URL FileXML;

    private Vector<JButtonXML> Items;

    public ToolbarLoader() {

    }

    public ToolbarLoader(URL FileXML) {
        this.FileXML = FileXML;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        //this.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    public Vector Loading() {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(FileXML);
            Element raiz = doc.getRootElement();
            java.util.List Ljtoolbar = raiz.getChildren("JToolBar");
            Iterator i = Ljtoolbar.iterator();
            Items = new Vector<JButtonXML>();

            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML,
             * en este caso JToolBar
             */

            while (i.hasNext()) {
                Element datos = (Element) i.next();

                java.util.List Lsubdatos = datos.getChildren();
                Iterator j = Lsubdatos.iterator();
                /**
                 * Se adicionara todos los menus principales a la clase
                 * ToolbarLoader
                 */
                this.add(CargarJToolbar(j));
            }
        }
        catch (JDOMException JDOMEe) {
            System.out.println(JDOMEe.getMessage());
        }
        catch (IOException IOEe) {
            System.out.println(IOEe.getMessage());
        }

        return Items;
    }

    /**
     * Metodo encargado de cargar las Toolbar al JPanel
     */

    private JToolBar CargarJToolbar(Iterator j) {
        JToolBar JTBtmp = new JToolBar();
        while (j.hasNext()) {
            Element subdatos = (Element) j.next();
            java.util.List Ljmenuitem = subdatos.getChildren();
            Iterator k = Ljmenuitem.iterator();
            JTBtmp.add(CargarJButton(k));
        }
        return JTBtmp;
    }

    /**
     * Metodo encargado de cargar los Botones al JToolBar
     */

    private JButtonXML CargarJButton(Iterator j) {
        JBtmp = new JButtonXML();
        while (j.hasNext()) {
            Element subdatos = (Element) j.next();
            if (subdatos.getName().equals("Activo")) {
                JBtmp.setActivo(true);
            }
            else if (subdatos.getName().equals("Acelerator")) {
            }
            else if (subdatos.getName().equals("ActionCommand")) {
                JBtmp.setActionCommand(subdatos.getValue());
            }
            else if (subdatos.getName().equals("Icon")) {
                JBtmp.setIcon(new ImageIcon(this.getClass().getResource(subdatos.getValue())));
            }
            else if (subdatos.getName().equals("ToolTipText")) {
                JBtmp.setToolTipText(Language.getWord(subdatos.getValue()));
            }
            else if (subdatos.getName().equals("Transaction")) {
                JBtmp.setTransaction(subdatos.getValue());
            }
            else if (subdatos.getName().equals("Clase")) {
                JBtmp.setClassName(subdatos.getValue());
            }
            else if (subdatos.getName().equals("ArgConstructor")) {
                java.util.List Largs = subdatos.getChildren();
                JBtmp.setArgConstructor(ArgConstructor(Largs));
            }
            else if (subdatos.getName().equals("TypeArgConstructor")) {
                java.util.List Largs = subdatos.getChildren();
                JBtmp.setTypeArgConstructor(TypeArgConstructor(Largs));
            }
            else if (subdatos.getName().equals("Metodo")) {
                JBtmp.setMethod(subdatos.getValue());
            }
        }
        Items.addElement(JBtmp);
        return JBtmp;
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
}