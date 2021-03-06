package com.kazak.comeet.lib.misc;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class Language  {
    
    private static Hashtable <String,messageStructure>glossary;
    
    /**
     * Metodo que carga se encarga de llenar el glosario para el idioma del ST
     * @param language idioma para el ST, Ej. <code>SPANISH</code>
     */
    public void loadLanguage(String language) {
    	
        Language.glossary = new Hashtable<String,messageStructure>();
        try {
            SAXBuilder saxBuilder = new SAXBuilder(false);
            Document doc = saxBuilder.build(this.getClass().getResource("/language.xml"));
            Element root = doc.getRootElement();
            List words = root.getChildren("sentence");
            Iterator iterator = words.iterator();
            
            while (iterator.hasNext()) {
                Element fields = (Element)iterator.next();
            	String message = fields.getChildText(language);
            	
                if (fields.getChild("key").getAttribute("errorCode")!=null) {
                	String errorCode = fields.getChild("key").getAttribute("errorCode").getValue();
                	glossary.put(fields.getChildText("key"),new messageStructure(errorCode,message));
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
    
    public static String getWord(String key) {
    	if (glossary.containsKey(key))
    		return glossary.get(key).getMessage();
    	else
    		return "";
    }
    
    public static char getNemo(String key) {   
        char nemo = glossary.get(key).getMessage().charAt(0); 
        return nemo;
    }
    
    public static String getErrorCode(String key) {
    	if (glossary.containsKey(key))
    		return glossary.get(key).getErrorCode();
    	else
    		return "";
    }
    
    class messageStructure {
    	private String message = null;
    	private String errorCode = null;
    	
    	private messageStructure(String errorCode,String message){
    		this.errorCode = errorCode;
    		this.message = message;
    	}
    	
		public String getErrorCode() {
			return errorCode;
		}
		public String getMessage() {
			return message;
		}
    }
}