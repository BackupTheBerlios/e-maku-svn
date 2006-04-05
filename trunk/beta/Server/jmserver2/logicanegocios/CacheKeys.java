package jmserver2.logicanegocios;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CacheKeys.java Creado el 22-jul-2005
 * 
 * Este archivo es parte de JMServerII
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Esta clase almacena en un cache todas las llaves que se van generando
 * a medida que se almacena la informacion, es necesaria, debido a que
 * los componentes de logica de negocios no pueden acceder a LNGenericSQL
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class CacheKeys {

    private static Map keys = new LinkedHashMap();
    private static String date = "";
    
    /*
    public static Hashtable getKeys() {
        return keys;
    }
    */
    public static void setKeys(Map keys) {
        CacheKeys.keys = keys;
    }
    
    public static void removeKey(Object key) {
        CacheKeys.keys.remove(key);
    }
     
    public static int size() {
        return keys.size();
    }
    
    public static String getKey(String idKey) {
    	return (String)keys.get(idKey);
    }
    /*
    public static String getKey(int index) {
    	Vector vector = new Vector(keys.values());
        return (String)vector.get(index);
    }
    */
    public static Iterator getKeys() {
    	return keys.values().iterator();
    }
    
    public static String getDate() {
        return date;
    }
    public static void setDate(String date) {
        CacheKeys.date = date;
    }
}
