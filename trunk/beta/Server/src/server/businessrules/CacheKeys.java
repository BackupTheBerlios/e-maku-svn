package server.businessrules;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CacheKeys.java Creado el 22-jul-2005
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
 * Esta clase almacena en un cache todas las llaves que se van generando
 * a medida que se almacena la informacion, es necesaria, debido a que
 * los componentes de logica de negocios no pueden acceder a LNGenericSQL
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class CacheKeys {

    private static Map<Object,String> keys = new LinkedHashMap<Object,String>();
    private static String date = "";
    private static String minDate="";
    
    public static void cleanKeys(){
    	date="";
    	minDate="";
    	keys.clear();
    }
    /*
    public static Hashtable getKeys() {
        return keys;
    }
    */
    @SuppressWarnings("unchecked")
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
    
    public static void setKey(String key,String value) {
    	keys.put(key,value);
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

	public static String getMinDate() {
		return minDate;
	}

	public static void setMinDate(String minDate) {
		CacheKeys.minDate = minDate;
	}
}
