package com.kazak.smi.server.database.sql;

import java.sql.SQLException;
import java.sql.Statement;

import com.kazak.smi.lib.misc.Language;
import com.kazak.smi.server.database.connection.PoolConexiones;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFile;


/**
 * RunIUD.java Creado el 6-jul-2004
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
 * Esta clase es la encargada de ejecutar las sentencias Insert, Update y Delete
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class RunIUD {
    
    private String sql;
    
    /**
     * Este constructor inicializa los objetos sql y bd nesesario para ejecutar 
     * la consulta
     * @param bd Base de datos
     * @param sql Sentencia SQL
     */
    public RunIUD(String sql) {
        this.sql = sql;
    }
    
    /**
     * Metodo encargado de ejcutar la sentencia sql
     * @return Un mensaje de exito o de error al ejecutar
     * la instruccion
     */
    public String ejecutarIUD() {
        try {
            Statement st = PoolConexiones.getConnection(ConfigFile.getMainDataBase()).createStatement();
            try {
                st.execute(sql);
            } 
            catch(SQLException SQLEe) {
                LogWriter.write(
                		Language.getWord("EXECUTE_IUD_ERR") + " "+ 
                		SQLEe.getMessage());
                return Language.getWord("EXECUTE_IUD_ERR");
            }
            CloseSQL.close(st);
            LogWriter.write(Language.getWord("EXECUTE_IUD_OK"));
            return Language.getWord("EXECUTE_IUD_OK");
        } 
        catch(SQLException SQLEe) {
        	LogWriter.write(
            		Language.getWord("ERR_ST") + " " + 
            		SQLEe.getMessage());
            return Language.getWord("ERR_ST");
        }
    }
}
