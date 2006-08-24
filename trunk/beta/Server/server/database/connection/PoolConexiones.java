package server.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;

import server.misc.ServerConst;
import server.misc.settings.ConfigFile;
import common.misc.language.Language;
import common.misc.log.LogAdmin;


/**
 * PoolConexiones.java Creado el 25-jun-2004
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
 * Esta clase se encarga se cargar las bases de datos existentes en fichero de
 * configuracion.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class PoolConexiones {
    
    private static Hashtable <String,Connection>Hpoolbds;
        
    /**
     * Este metodo carga todas las bases de datos existentes en el
     * fichero de configuracion
     */
    
    public static void CargarBD() throws PoolNotLoadException {
        
        Hpoolbds = new Hashtable<String,Connection>();
        int max = ConfigFile.SizeDB();
        for (int i=0;i<max;i++)
            try {
                LogAdmin.setMessage(
                		Language.getWord("LOADING_DB") +" "+ConfigFile.getNombreBD(i),
						ServerConst.MESSAGE);
                
                Class.forName(ConfigFile.getDriver(i));
                
                Hpoolbds.put(
                		ConfigFile.getNombreBD(i),
						DriverManager.getConnection(
								ConfigFile.getUrl(i),
								ConfigFile.getUsuario(i),
								ConfigFile.getPassword(i)));
            } 
            catch (ClassNotFoundException CNFEe) {
                LogAdmin.setMessage(
                		Language.getWord("ERR_CLASS") + " " + CNFEe.getMessage(),
						ServerConst.ERROR);
                
                throw new PoolNotLoadException(ConfigFile.getNombreBD(i));
            } 
            catch (SQLException SQLEe){
                LogAdmin.setMessage("ERR_SQL",
                		Language.getWord("ERR_SQL") + " / " + SQLEe.getMessage(),
						ServerConst.ERROR);
                throw new PoolNotLoadException(ConfigFile.getNombreBD(i));
            }
    }
    
    /**
     * Metodo encargado de retornar el objeto Connection de la base de datos
     * recibida como parametro
     * @param nombreBD recibe el nombre de la base de datos
     * @return retorna el objeto connection de la base de datos recibida como
     * parametro
     */
    public static Connection getConnection(String nombreBD) {
        return Hpoolbds.get(nombreBD);
    }
    
    public static boolean chekDataBase(String DataBase) {
    	return Hpoolbds.containsKey(DataBase);
    } 
}
