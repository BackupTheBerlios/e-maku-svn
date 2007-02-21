package server.control;

import java.util.Iterator;
import java.util.List;

import server.database.sql.SQLFormatAgent;

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
public class QueryValidator {
    
    private String [] args;
    private String id;
    private String bd;
    private String login;
    private Element data;
    private String query;
    
    public QueryValidator (String bd, String login, Element data) {
        this.bd = bd;
        this.login = login;
        this.data = data;
    }
    
    public boolean isValid() {
        try {
            query = data.getChild("sql").getValue();
            String password;
            try {
                password = data.getChild("password").getValue();
            }
            catch(NullPointerException NPEe) {
                password="";
            }
            return SQLFormatAgent.permisoSentencia(bd,login,query,password);
        }
        catch(NullPointerException NPEe) {
            System.out.println("NPEe: "+NPEe.getMessage());
            return false;
        }
    }
    
    public boolean changeStructParam(){
        
        try {
            
            Element ref = data.getChild("params");
            id = data.getChild("id").getValue();
            
            int countParams = ref.getContentSize();
            args = new String[countParams];
            
            List lista = ref.getChildren();
            Iterator iterador = lista.iterator();
            
            for (int i =0 ;iterador.hasNext(); i++){
                ref = (Element)iterador.next();
                if(ref.getName().equals("arg")) {
                    args[i] = ref.getValue();
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
        return args;
    }
    
    public String getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }
}
