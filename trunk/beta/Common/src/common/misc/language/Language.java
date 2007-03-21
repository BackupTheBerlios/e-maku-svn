package common.misc.language;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Language.java Creado el 17-jun-2005
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
 * @author <A href='mailto:cristian@qhatu.net'>Gustavo Gonzalez</A>
 */

public class Language  {
    
    private static Hashtable <String,messageStructure>glossary;
    
    public void loadLanguage(String lang) {
    	loadLanguage(null,lang);
    }
    /**
     * Metodo que carga se encarga de llenar el glosario para el idioma del ST
     * @param lang idioma para el ST, Ej. <code>SPANISH</code>
     */
    public void loadLanguage(String directory,String lang) {
        Language.glossary = new Hashtable<String,messageStructure>();
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc= null;
            if (directory==null) {
            	doc= builder.build(this.getClass().getResource("/language.xml"));
            }
            else {
            	doc= builder.build(directory+"/language.xml");
            }
            Element root = doc.getRootElement();
            List words = root.getChildren("sentence");
            Iterator i = words.iterator();
            while (i.hasNext()) {
                Element fields = (Element)i.next();
            	String message = fields.getChildText(lang);
                if (fields.getChild("key").getAttribute("errorCode")!=null) {
                	String codeError = fields.getChild("key").getAttribute("errorCode").getValue();
                	glossary.put(fields.getChildText("key"),new messageStructure(codeError,message));
                }
                else {
                	glossary.put(fields.getChildText("key"),new messageStructure(null,message));
                }
        		
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
     * Retorna un string en el lenguaje configurado
     * @param key Palabra clave
     * @return Retorna un string en el idioma configurado
     */
    public static String getWord(String key) {
    	if (glossary.containsKey(key))
    		return glossary.get(key).getMessage();
    	else
    		return "Null String: " + key;
    }
    
    public static char getNemo(String key) {   
        char nemo = glossary.get(key).getMessage().charAt(0); 
        return nemo;
    }
    
    public static String getErrorCode(String key) {
    	if (glossary.containsKey(key))
    		return glossary.get(key).getErrorCode();
    	else
    		return "Null String: " + key;
    }
    
    class messageStructure {
    	private String message = null;
    	private String errorCode = null;
    	
    	private messageStructure(String errorCode,String message){
    		this.errorCode=errorCode;
    		this.message=message;
    	}
    	
		public String getErrorCode() {
			return errorCode;
		}
		public String getMessage() {
			return message;
		}
    }
}
