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
 * Disponible en http://www.kazak.ws
 *
 * Desarrollado por Soluciones KAZAK
 * Grupo de Investigacion y Desarrollo de Software Libre
 * Santiago de Cali/Republica de Colombia 2001
 *
 * CLASS Language v 0.1
 * Descripcion:
 * Esta clase se encarga de cargar la Tabla Hash con los valores
 * de las etiquetas del programa, segun el idioma que el usuario haya
 * seleccionado.
 *
 * Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
 *
 * Fecha: 2001/10/01
 *
 * Autores: Beatriz Florián  - bettyflor@kazak.ws
 *          Gustavo Gonzalez - xtingray@kazak.ws
 *
 * 2004/03/23
 * Esta clase fue modificada para almacenar la llave y sus correspondientes traducciones en un
 * archivo xml, eliminando las clases EnglishGlossary y SpanishGlossary.
 *
 * Modificacion: Luis Felipe Hernandez Z. felipe@qhatu.net
 *
 * 2004/06/28
 * Se modifico los metodos y la tabla hashtable como estaticos para no tener que instanciar
 * esta clase cada que se la necesita, sino simplemente llamar al metodo que se necesite de
 * forma similar a como se manejan las interfaces.
 *
 * @author Beatriz Flori�n, Gustavo Gonzalez, Luis Felipe Hernandez, Cristian David Cepeda.
 *
 */
public class Language  {
    
    private static Hashtable <String,messageStructure>glossary;
    
    public void CargarLenguaje(String lenguaje) {
    	CargarLenguaje(null,lenguaje);
    }
    /**
     * Metodo que carga se encarga de llenar el glosario para el idioma del ST
     * @param lenguaje idioma para el ST, Ej. <code>SPANISH</code>
     */
    public void CargarLenguaje(String directory,String lenguaje) {
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
    
    /**
     * Retorna un string en el lenguaje configurado
     * @param key Palabra clave
     * @return Retorna un string en el idioma configurado
     */
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
