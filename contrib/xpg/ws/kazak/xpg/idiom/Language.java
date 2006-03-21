package ws.kazak.xpg.idiom;

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
 * Modificacion: Luis Felipe Hernandez Z. felipe@kazak.ws
 *
 * 2004/06/28
 * Se modifico los metodos y la tabla hashtable como estaticos para no tener que instanciar
 * esta clase cada que se la necesita, sino simplemente llamar al metodo que se necesite de
 * forma similar a como se manejan las interfaces.
 *
 * @author Beatriz Florián, Gustavo Gonzalez, Luis Felipe Hernandez, Cristian David Cepeda.
 *
 */
public class Language  {
    
    private static Hashtable glossary;
    private static String lenguaje = "SPANISH";
    
    /**
     * Metodo que carga se encarga de llenar el glosario para el idioma del ST
     * @param lenguaje idioma para el ST, Ej. <code>SPANISH</code>
     */
    public void CargarLenguaje(String lenguaje) {
        Language.lenguaje=lenguaje;
        Language.glossary = new Hashtable();
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(this.getClass().getResource("/Language.xml"));
            Element raiz = doc.getRootElement();
            List palabras = raiz.getChildren("palabra");
            Iterator i = palabras.iterator();
            while (i.hasNext()) {
                Element campos = (Element)i.next();
                if (lenguaje.equals("Spanish"))
                    glossary.put(campos.getChildText("clave"),campos.getChildText("espanol"));
                if (lenguaje.equals("English"))
                    glossary.put(campos.getChildText("clave"),campos.getChildText("ingles"));
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
        return (String) glossary.get(key);
    }
    
    public static char getNemo(String key) {   
        char nemo = ((String) glossary.get(key)).charAt(0); 
        return nemo;
    }
    
    
}
