package com.kazak.smi.server.database.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * CloseSQL.java Creado el 2-jul-2004
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
 * Esta clase recibe todos los Statement y ResultSet para se cerrados
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class QueryClosingHandler {
    
    /**
     * Metodo encargado de cerrar los Statements
     * @param st Objeto a referenciado para cerrar
     */
    public static void close(Statement st) {
        try {
            st.close();
        }
        catch(SQLException SQLEe) {
            SQLEe.printStackTrace();
        }
        catch(NullPointerException NPEe) {
        	NPEe.printStackTrace();
        }
        
    }
    
    /**
     * Metod encargado de cerrar los ResultSets
     * @param rs Objeto a referenciado para cerrar
     */
    
    public static void close(ResultSet rs) {
        try {
            rs.close();
        }
        catch(SQLException SQLEe) {
            SQLEe.printStackTrace();
        }
    }
    
    /**
     * Metodo encargado de cerrar la referencia a la conexion
     * @param cn Objeto a referenciado para cerrar
     */
    
    public static void close(Connection cn) {
        try {
            cn.close();
        }
        catch(SQLException SQLEe) {
            SQLEe.printStackTrace();
        }
    }
}
