package common.misc;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Icons.java Creado el 24-sep-2004
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
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class Icons {

    private static Hashtable <String,String>Hicons;

    public void loadIcons() {
        
	    Hicons = new Hashtable<String,String>();
	    
	    try {
	        
	        SAXBuilder builder = new SAXBuilder(false);
	        Document doc = builder.build(this.getClass().getResource("/icons.xml"));
	        Element raiz = doc.getRootElement();
	        List icon = raiz.getChildren("icon");
	        Iterator i = icon.iterator();
	        
	        while (i.hasNext()) {
	            Element campos = (Element)i.next();
	                Hicons.put(campos.getChildText("key"),campos.getChildText("path"));
	        }
	    }
	    catch (JDOMException JDOMEe) {
	        System.out.println(JDOMEe.getMessage());
	    }
	    catch (IOException IOEe) {
	        System.out.println(IOEe.getMessage());
	    }
    }

    public static String getIcon(String key) {
        return Hicons.get(key);
    }
}
