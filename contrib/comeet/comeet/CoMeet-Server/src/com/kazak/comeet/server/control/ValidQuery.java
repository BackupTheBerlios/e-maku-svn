package com.kazak.comeet.server.control;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * ValidQuery.java Creado el 26-jul-2004
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
public class ValidQuery {
    
    private String [] argsArray;
    private String id;
    private Element data;
    private String query;
    
    public ValidQuery (Element data) {
        this.data = data;
        this.query = data.getChild("sql").getValue();
    }

    public boolean changeStructParam(){
        
        try {     
            Element element = data.getChild("params");
            id = data.getChild("id").getValue();   
            int countParams = element.getContentSize();
            argsArray = new String[countParams];
            
            List list = element.getChildren();
            Iterator iterator = list.iterator();
            
            for (int i =0 ;iterator.hasNext(); i++){
                element = (Element)iterator.next();
                if(element.getName().equals("arg")) {
                    argsArray[i] = element.getValue();
                }
                else {
                    return false;
                }
            }
            return true;
            
        }
        catch(NullPointerException NPEe ) {
            return false;
        }
    }
    
    public String [] getArgs(){
        return argsArray;
    }
    
    public String getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }
}