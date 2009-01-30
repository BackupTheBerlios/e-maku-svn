package common.misc.language;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;


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
 * @author <A href='mailto:xtingray@qhatu.net'>Gustavo Gonzalez</A>
 */

public class Language  {
    
    private static Hashtable <String,messageStructure>glossary = new Hashtable<String,messageStructure>();
    
    public void loadLanguage(String lang) {
    	loadLanguage(null,lang);
    }
    
    private void loadWords(Document doc) {
        Element root = doc.getRootElement();
        List<Element> words = root.getChildren("sentence");
        Iterator<Element> i = words.iterator();
        while (i.hasNext()) {
            Element fields = (Element)i.next();
        	String message = fields.getChildText("value");
            if (fields.getChild("key").getAttribute("errorCode")!=null) {
            	String codeError = fields.getChild("key").getAttribute("errorCode").getValue();
            	glossary.put(fields.getChildText("key"),new messageStructure(codeError,message));
            }
            else {
            	glossary.put(fields.getChildText("key"),new messageStructure(null,message));
            }
    		
        }
    }
    
    /**
     * Metodo que carga se encarga de llenar el glosario para el idioma del ST
     * @param lang idioma para el ST, Ej. <code>SPANISH</code>
     */
    public void loadLanguage(String directory,String lang) {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            if (directory!=null) {
            	System.out.println("directorio: "+directory + lang + ".xml");
            	loadWords(builder.build(directory + lang + ".xml"));
            } else {
            	System.out.println("language: "+  lang + ".xml");
            	URL url = this.getClass().getResource("/lang/" + lang + ".xml");
            	System.out.println(url.getFile());
            	File file = new File(url.getFile());
        		loadWords(builder.build(file));
            }
        }
        catch (JDOMException JDOMEe) {
            JDOMEe.printStackTrace();
        }
        catch (FileNotFoundException FNFEe) {
        	FNFEe.printStackTrace();
        }
        catch (IOException IOEe) {
        	IOEe.printStackTrace();
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
    		return key;
    }
    
    public static char getNemo(String key) {   
        if (glossary.containsKey(key)) {
        	return glossary.get(key).getMessage().charAt(0);	
        } 
        return 32;
    }
    
    public static String getErrorCode(String key) {
    	if (glossary.containsKey(key))
    		return glossary.get(key).getErrorCode();
    	else
    		return key;
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
