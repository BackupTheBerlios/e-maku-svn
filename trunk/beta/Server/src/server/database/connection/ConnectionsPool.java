package server.database.connection;

import java.sql.*;
import java.util.*;

import com.javaexchange.dbConnectionBroker.DbConnectionBroker;

import server.misc.*;
import server.misc.settings.*;

import common.misc.language.*;
import common.misc.log.*;

/**
 * PoolConexiones.java Creado el 25-jun-2004
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga se cargar las bases de datos existentes en fichero de
 * configuracion. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class ConnectionsPool {

	private static Hashtable<String, PooledConnections> Hpoolbds;
	//private static Hashtable<String, Connection> Hpoolbds;
	private DbConnectionBroker myBroker;
	
	/**
	 * Este metodo carga todas las bases de datos existentes en el fichero de
	 * configuracion
	 */

	public static void CargarBD() throws ConnectionsPoolException {

		Hpoolbds = new Hashtable<String, PooledConnections>();
		//Hpoolbds = new Hashtable<String, Connection>();
		int max = ConfigFileHandler.getDBSize();
		for (int i = 0; i < max; i++)
			try {
				LogAdmin.setMessage(Language.getWord("LOADING_DB") + " "+ ConfigFileHandler.getDBName(i),ServerConstants.MESSAGE);

				String driver = ConfigFileHandler.getDriver(i);
				String url = ConfigFileHandler.getUrl(i);
				String user = ConfigFileHandler.getUser(i);
				String password = ConfigFileHandler.getPassword(i);
				Class.forName(driver);
				PooledConnections c = new PooledConnections(driver, url,user, password, 20);
				//Connection c = DriverManager.getConnection(url, user, password);
				Hpoolbds.put(ConfigFileHandler.getDBName(i), c);
			} catch (ClassNotFoundException CNFEe) {
				LogAdmin.setMessage("ERR_CLASS", Language.getWord("ERR_CLASS"),CNFEe.getMessage(), ServerConstants.ERROR);
				throw new ConnectionsPoolException(ConfigFileHandler.getDBName(i));
			} catch (SQLException SQLEe) {
				LogAdmin.setMessage("ERR_SQL", Language.getWord("ERR_SQL"),SQLEe.getMessage(), ServerConstants.ERROR);
				throw new ConnectionsPoolException(ConfigFileHandler
						.getDBName(i));
			}
	}

	/**
	 * Metodo encargado de retornar el objeto Connection de la base de datos
	 * recibida como parametro
	 * 
	 * @param nombreBD
	 *            recibe el nombre de la base de datos
	 * @return retorna el objeto connection de la base de datos recibida como
	 *         parametro
	 */

	public static Connection getConnection(String nombreBD) {
		return Hpoolbds.get(nombreBD).getConnection();
	}
	
	
	public static Connection getMultiConnection(String nombreBD) {
		return Hpoolbds.get(nombreBD).getMultiConnection();
	}

	public static void freeMultiConnection(String nombreBD,Connection conn) {
		if (conn!=null) {
			Hpoolbds.get(nombreBD).freeConnection(conn);
		}
	}

	public static boolean chekDataBase(String DataBase) {
		return Hpoolbds.containsKey(DataBase);
	}
}