package common.misc.parameters;

import java.util.Hashtable;

/**
 * GenericParameters.java Creado el 15-jul-2005
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
 * Clase utilizada para almacenar parametros genericos almacenados en el 
 * archivo de configuracion local.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class GenericParameters {

    public static Hashtable <String,String>parameters = new Hashtable<String,String>();
    
    /**
     * Este metodo se utiliza para comparar el valor de un parametro recibido, 
     * con un valor configurado localmente, para realizar la comparacion es
     * necesario la llave que identifica el parametro a comparar y el parametro
     * a comparar
     * @param key contiene el identificador del parametro
     * @param value es el parametro
     * @return retorna true si la comparacion es verdader o false si no.
     */
    
    public static boolean equals(String key,String value) {
        if (parameters.containsKey(key)) {
            String compareValue = parameters.get(key).trim();
            if (compareValue.equals(value.trim())) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    public static void addParameter(String key,String value) {
        parameters.put(key,value);
    }

    public static void removeParameter(String key) {
        parameters.remove(key);
    }
    
    public static String getParameter(String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        }
        else {
            return "";
        }
    }
}
