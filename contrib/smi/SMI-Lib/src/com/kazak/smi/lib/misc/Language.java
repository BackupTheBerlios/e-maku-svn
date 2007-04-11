package com.kazak.smi.lib.misc;

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
     * @param lenguaje idioma para el ST, Ej. <code>SPANISH</code>
     */
    public void CargarLenguaje(String lenguaje) {
        Language.glossary = new Hashtable<String,messageStructure>();
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(this.getClass().getResource("/language.xml"));
            Element raiz = doc.getRootElement();
            List palabras = raiz.getChildren("sentence");
            Iterator i = palabras.iterator();
            while (i.hasNext()) {
                Element campos = (Element)i.next();
            	String message = campos.getChildText(lenguaje);
            	
                if (campos.getChild("key").getAttribute("errorCode")!=null) {
                	String codeError = campos.getChild("key").getAttribute("errorCode").getValue();
                	glossary.put(campos.getChildText("key"),new messageStructure(codeError,message));
                }
                else {
                	glossary.put(campos.getChildText("key"),new messageStructure(null,message));
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
    
    public static String getCodeError(String key) {
    	if (glossary.containsKey(key))
    		return glossary.get(key).getCodeError();
    	else
    		return "";
    }
    
    class messageStructure {
    	private String message = null;
    	private String codeError = null;
    	
    	private messageStructure(String codeError,String message){
    		this.codeError=codeError;
    		this.message=message;
    	}
    	
		public String getCodeError() {
			return codeError;
		}
		public String getMessage() {
			return message;
		}
    }
}