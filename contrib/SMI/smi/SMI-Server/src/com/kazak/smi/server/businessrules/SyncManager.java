package com.kazak.smi.server.businessrules;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.control.DailyIterator;
import com.kazak.smi.server.control.Scheduler;
import com.kazak.smi.server.control.SchedulerTask;
import com.kazak.smi.server.database.connection.ConnectionsPool;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFileHandler;
import com.kazak.smi.server.misc.ServerConstants;
import com.kazak.smi.server.misc.settings.OracleSyncTask;

/**
 * This class handles all the sync processes between the SMI and the Oracle/PostgreSQL DB
 * 
 * @author Felipe Hernandez <felipe@qhatu.net>
 *
 */

public class SyncManager {
	
	private String oracleSQL = "";
	private Connection oracleConnection;
	private Hashtable<String,User> usersHash; 
	private Hashtable<String,String> wsHash;
	
	private Vector<String> currentUsersVector;
	private Vector<String> deletedUsersVector;
	private Vector<String> deletedWSVector;
	private Vector<String> currentWSVector;
	private Statement statement = null;
	private SocketChannel sock;

	/**
	 * This constructor method loads the schedule of sync tasks to the system
	 * 
	 */
	public SyncManager() {
		try {
            oracleSQL =	getOracleSQLString();
			LogWriter.write("INFO: Iniciando demonio de sincronización");
			loadSettings();
			for (OracleSyncTask oraclesync:ConfigFileHandler.getOraclesync()) {				
				String minute = Integer.toString(oraclesync.getMinute());
				String time = "am";
				if (minute.length() == 1) {
					minute = "0" + minute; 
				}
				if (oraclesync.getHour()>12) {
					time = "pm";
				}
				System.out.println("INFO: Cargando sincronizacion automatica de las " + oraclesync.getHour() + ":" + minute + " " + time);
				new Cron(oraclesync).start();
			}
						
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This constructor method executes once a sync task directly to the system
	 * @param sock
	 */
	public SyncManager (SocketChannel sock) {
		this.sock = sock;
		try {
            oracleSQL =	getOracleSQLString();
			loadSettings();
		} catch (FileNotFoundException e) {
			Element syncErr = new Element("ERRSYNC");
			Element message = new Element("message");
			message.setText(
					"ERROR: No se encontró el archivo de configuración de la sincronización.");
			try {
				SocketWriter.writing(sock,new Document(syncErr));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			Element syncErr = new Element("ERRSYNC");
			Element message = new Element("message");
			message.setText("ERROR: Falla de E/S durante la sincronización.");
			try {
				SocketWriter.writing(sock,new Document(syncErr));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
		
		if (loadOracleData()) {
			if (loadPostgresData()){
				filterInvalidData();
				String result = " con errores.";
				if (storePostgresData()) {
					result = " satisfactoriamente.";
				}				
				LogWriter.write("INFO: Base de datos sincronizada " + result);
				LogWriter.write("INFO: Limpiando y recargando el cache de usuarios.");
				Element reload = new Element("RELOADTREE");
				Element succesSync = new Element("SUCCESSSYNC");
				try {
					SocketWriter.writing(sock,new Document(reload));
					SocketWriter.writing(sock,new Document(succesSync));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				LogWriter.write("ERROR: Ocurrio un problema mientras se consultaban los datos desde PostgreSQL");
			}
			
		} else {
			LogWriter.write("ERROR: Ocurrio un problema mientras se consultaban los datos desde Oracle");
		}
	}
	
	/**
	 * This method loads the Oracle SQL sentence string from a config file
	 * @return
	 */
	public String getOracleSQLString() {
		String sql = "";
		try {
				String line="";
				BufferedReader in = new BufferedReader(new FileReader(ServerConstants.CONF + ServerConstants.SEPARATOR + "oracle.sql"));
				while ((line = in.readLine()) != null)   {
						sql += " " + line;
				}
				in.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(sql==null || sql.equals("")) {
				LogWriter.write("ERROR: Sentencia SQL para sincronización es nula o vacía.");
				LogWriter.write("ERROR: Revisar archivo de configuración: oracle.sql");
			}
			
			return sql;
	}
	
	/**
	 * This method initializes the vectors where the users and POS info is stored
	 * 
	 */
	public void loadSettings() throws FileNotFoundException, IOException {
		usersHash = new Hashtable<String, User>();
		wsHash = new Hashtable<String, String>();
		
		currentUsersVector = new Vector<String>();
		currentWSVector = new Vector<String>();
		deletedUsersVector = new Vector<String>();
		deletedWSVector = new Vector<String>();
	}	
	
	/**
	 * Load data from Oracle Data Base
	 */
	private boolean loadOracleData() {
		ResultSet resultSet = null;
		String oracleDB = ConfigFileHandler.getSecondDataBase();
		try {
			LogWriter.write("INFO: Cargando registros actualizados de la base de datos Oracle [" + oracleDB + "]");
			oracleConnection = ConfigFileHandler.getConnection(oracleDB);
						
			if (oracleConnection == null) {
				LogWriter.write("ERROR: La conexion a la base de datos " + oracleDB + " es invalida.");
				return false;
			}
			
			statement = oracleConnection.createStatement();
			LogWriter.write("SENTENCIA: " + oracleSQL);
			resultSet = statement.executeQuery(oracleSQL);
			int i=0;
			while (resultSet.next()) {
				String code     = resultSet.getString(1).trim();
				String wsCode   = resultSet.getString(2).trim();
				String wsPOSName = resultSet.getString(3).trim();
				String userName   = resultSet.getString(4).trim();
				User user     = new User();
				user.code     = code;
				user.password =  generatePassword(code);
				user.name     = userName; 
				user.posCode  = wsCode;
				
				usersHash.put(code,user);
				if (!wsHash.containsKey(wsCode)) {
					wsHash.put(wsCode,wsPOSName);
				}
				i++;
			}
			LogWriter.write("INFO: Total de usuarios registrados: " + i);
			resultSet.close();
			statement.close();
			oracleConnection.close();
			return true;
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			processSQLException(oracleDB,e,resultSet);
			return false;
		}
	}
	
	/**
	 * This method loads current info of the system from a PostgreSQL database
	 */
	private boolean loadPostgresData() {

		ResultSet resultSet = null;
		String pgdb = ConfigFileHandler.getMainDataBase();
		try {
			LogWriter.write("INFO: Cargando registros actuales de la base de datos PostgreSQL [" + pgdb + "]");
			statement = ConnectionsPool.getConnection(pgdb).createStatement();
			resultSet = statement.executeQuery("SELECT login FROM usuarios WHERE login LIKE 'CV%'");
			int i=0;
			while (resultSet.next()) {
				String code = resultSet.getString(1).trim();
				currentUsersVector.add(code);
				i++;
			}
			LogWriter.write("INFO: Total de colocadores registrados: " + i);
			resultSet.close();
			
		} catch (SQLException e) {
			processSQLException(pgdb,e,resultSet);
			return false;
		}
		
		try {	
			resultSet = statement.executeQuery("SELECT codigo FROM puntosv");
			int i=0;
			while (resultSet.next()) {
				String code   = resultSet.getString(1).trim();
				currentWSVector.add(code);
				i++;
			}
			LogWriter.write("INFO: Total de puntos de venta registrados: " + i);
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			processSQLException(pgdb,e,resultSet);
			return false;
		}
		
		return true;
	}	
	
	/**
	 * This method process the exception generated by the execution of a SQL sentence
	 */
	public void processSQLException(String db, SQLException e, ResultSet rs) {
		Element errSync = new Element("ERRSYNC");
		Element message = new Element("message");		
		String text = "Error en la base de datos " + db + "\nCausa: " + e.getMessage();
		message.setText(text);
		errSync.addContent(message);
		
		try {
			SocketWriter.writing(sock,new Document(errSync));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		LogWriter.write(text);
		e.printStackTrace();
		
		try {
			if (rs!=null) {
				rs.close();
			}
			if (statement!=null) {
				statement.close();
			}
		} catch (SQLException e1) {
			LogWriter.write("ERROR: No se pudo cerrar conexion a base de datos " + db);
			e1.printStackTrace();
		}		
	}	
	
	/**
	 * This method saves data in the Postgresql Data Base Server
	 */
	private boolean storePostgresData() {
		
		String pgdb = ConfigFileHandler.getMainDataBase();
		
		try {
			LogWriter.write("INFO: Sincronizando... [ Oracle -> PostgreSQL ]");
			statement = ConnectionsPool.getConnection(pgdb).createStatement();
		} catch (SQLException e) {
			processSQLException(pgdb,e,null);
			return false;
		}

		String SQL = "";
		LogWriter.write("INFO: Eliminando usuarios deshabilitados...");
		for (String userCode : deletedUsersVector) {
			try {
				 SQL = "DELETE FROM " + "usuarios_pventa " + "WHERE uid=(SELECT uid FROM usuarios WHERE login='"+userCode+"')";
				 statement.execute(SQL);
				 SQL = "DELETE FROM usuarios WHERE login='"+userCode+"'";
				 statement.execute(SQL);
			} catch (SQLException e) {
				LogWriter.write("ERROR: Falla mientras se eliminaba el usuario (deshabilitado) con codigo: " + userCode);
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);
				return false;
			}
		}

		LogWriter.write("INFO: Eliminando puntos de venta deshabilitados...");
		for (String wsCode : deletedWSVector) {
			try {
				SQL = "DELETE FROM puntosv WHERE codigo='"+wsCode+"'";
				statement.execute(SQL);
			} catch (SQLException e) {
				LogWriter.write("ERROR: Falla mientras se eliminaba el punto de venta (deshabilitado) con codigo: " + wsCode);
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);
				return false;
			}
		}
		
        String name = "";
		Set<String> keys = wsHash.keySet();
		LogWriter.write("INFO: Actualizando puntos de venta...");
		
		for (String key : keys) {
			try {
				name = wsHash.get(key);
				if (key!=null && name!=null) {
					SQL = "INSERT INTO puntosv (codigo,nombre) values('"+key+"','"+name+"')";
					statement.execute(SQL);
					LogWriter.write("INFO: Punto de colocacion => " + name+ " almacenado");
				} else {
					LogWriter.write("ERROR: Codigo " + key + " no pertenece a ningun punto de venta.");
				}
			} catch (SQLException e) {
				LogWriter.write("ERROR: Falla mientras se ingresaba el punto de colocacion: {" + name + "} con codigo {" + key + "}");
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);
				return false;
			}
		}

		keys = usersHash.keySet();
		LogWriter.write("INFO: Actualizando usuarios del sistema...");
		
		for (String key : keys) {
			User user = null;
			try {
				user = usersHash.get(key);
				if (user != null) {
					SQL = "INSERT INTO usuarios (login,clave,nombres) " + 
					      "values('"+user.code+"',md5('"+user.password+"'),'"+user.name+"')";
					statement.execute(SQL);
					LogWriter.write("INFO: Colocador => " + user.name + " almacenado");
				} else {
					LogWriter.write("ERROR: Codigo " + key + " no pertenece a ningun usuario.");
				}
				
			} catch (SQLException e) {
				LogWriter.write("ERROR: Falla mientras se ingresaba el usuario: {" + user.code + "," + user.name + "}");
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);
				return false;
			}
		}
		
		keys = usersHash.keySet();
		LogWriter.write("INFO: Actualizando relacion de usuarios y puntos de venta...");
		
		for (String key : keys) {
			User user = usersHash.get(key);

			if (user.posCode!=null) {
				String posCode      = null;
				String posName      = null;
				String uid          = null;
				ResultSet uidResultSet     = null;
				ResultSet posResultSet = null;

				SQL = "SELECT uid FROM usuarios WHERE usuarios.login='" + user.code + "'";
				try { 
					uidResultSet = statement.executeQuery(SQL);	 
					while(uidResultSet.next()) {
						uid = uidResultSet.getString("uid").trim();
					} 

				} catch (SQLException e) {
					LogWriter.write("ERROR: Falla mientras se consultaba el usuario: {" + user.code + "}");
					LogWriter.write("SENTENCIA: " + SQL);
					processSQLException(pgdb,e,null);
					return false;
				}

				SQL = "SELECT codigo,nombre FROM puntosv WHERE puntosv.codigo='" + user.posCode + "'";
				try {	
					posResultSet = statement.executeQuery(SQL);
					while(posResultSet.next()) {
						posCode = posResultSet.getString("codigo").trim();
						posName = posResultSet.getString("nombre").trim();
					}
					posResultSet.close();
					uidResultSet.close();
				} catch (SQLException e) {
					LogWriter.write("ERROR: Falla mientras se consultaba el punto de venta: {" + user.posCode + "}");
					LogWriter.write("SENTENCIA: " + SQL);
					processSQLException(pgdb,e,null);
					return false;
				}

				if (uid!=null) {
					if (posCode!=null) {
						SQL = "INSERT INTO usuarios_pventa (uid,codigo_pventa) VALUES (" + uid + "," + posCode + ")";
						try {
							statement.execute(SQL);
						} catch (SQLException e) {
							LogWriter.write("ERROR: Falla mientras se ingresaba relacion usuario/pos: {" + uid + "," + posCode + "}");
							LogWriter.write("SENTENCIA: " + SQL);
							processSQLException(pgdb,e,null);
							return false;
						}
					} else {			
						Element syncErr = new Element("ERRSYNC");
						Element message = new Element("message");
						String text = "Error sincronizando, el codigo del punto de venta {" +
						              user.posCode + "} no existe, por favor verifique las bases de datos";
						message.setText(text);
						try {
							SocketWriter.writing(sock,new Document(syncErr));
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						LogWriter.write(text);
						return false;
					}
				} else {
					Element syncErr = new Element("ERRSYNC");
					Element message = new Element("message");
					String text = "Error sincronizando, el uid de usuario para el codigo {" +
					              user.code + "} no existe, por favor verifique las bases de datos.";
					message.setText(text);
					try {
						SocketWriter.writing(sock,new Document(syncErr));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					LogWriter.write(text);
					return false;
				}
				LogWriter.write("INFO: Asignando Colocador => " + user.name + " (" + user.code + ") " +
						        "al punto => " + posName + " {" + user.posCode + "}");
			} else {
				LogWriter.write("ERROR: Codigo " + key + " no esta asignado a ningun punto de venta.");
			}
		}
		try {
			statement.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * This method selects the old records which must to be deleted 
	 */
	private void filterInvalidData() {
		
        LogWriter.write("INFO: Filtrando lista de usuarios a eliminar...");
		for (String userCode : currentUsersVector) {
			if (usersHash.containsKey(userCode)) {
				usersHash.remove(userCode);
			}
			else {
				deletedUsersVector.add(userCode);
			}
		}

        LogWriter.write("INFO: Filtrando puntos de venta a eliminar...");
		for (String wsCode : currentWSVector) {
			if (wsHash.containsKey(wsCode)) {
				wsHash.remove(wsCode);
			}
			else {
				deletedWSVector.add(wsCode);
			}
		}
	}
	
	/**
	 * This method creates a password from a given string
	 */
	private String generatePassword(String password) {
		int index = password.length() > 5 ? (password.length() - 3) : 0;
		if (index > 0) {
			String begin= password.substring(0,2);
			String end  = password.substring(index,password.length());
			return (begin+end);
		}
		return password;
	}
	
	class Cron {

	    private Scheduler scheduler = new Scheduler();
	    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS");
	    private OracleSyncTask oraclesync;
	    
	    public Cron(OracleSyncTask oraclesync){
	    	this.oraclesync=oraclesync;
	    }
	    
	    public void start() {
	        scheduler.schedule(new SchedulerTask() {
	            public void run() {
	                LogWriter.write("INFO: Iniciando sincronizacion programada [" + dateFormat.format(new Date()) + "]");
	                
	                LogWriter.write("INFO: Procesando usuarios...");
	                //Users sync
					if (loadOracleData()) {
						if (loadPostgresData()){
							filterInvalidData();
							String result = " con errores.";
							if (storePostgresData()) {
								result = " satisfactoriamente.";
							}
							LogWriter.write("INFO: Usuarios sincronizados " + result);
						} else {
							LogWriter.write("ERROR: Ocurrio un problema mientras se consultaban los datos desde PostgreSQL");
						}
					} else {
						LogWriter.write("ERROR: Ocurrio un problema mientras se consultaban los datos desde Oracle");
					}
					
					//Messages sync 
					LogWriter.write("INFO: Procesando mensajes...");
					ResultSet resultSet = null;
					String sql = "";
					
					try {
						statement = ConnectionsPool.getConnection(ConfigFileHandler.getMainDataBase()).createStatement();
						sql = "DELETE FROM mensajes WHERE current_date=(fecha+" + ConfigFileHandler.getMessageLifeTimeInDataBase() + ")";
						LogWriter.write("INFO: Eliminando mensajes con tiempo de vida igual a " + ConfigFileHandler.getMessageLifeTimeInDataBase());
						if (statement.execute(sql)) {
							LogWriter.write("INFO: Mensajes antiguos borrados satisfactoriamente.");
						}
						
					} catch (SQLException e) {
							LogWriter.write(
									"ERROR: Falla durante la sincronizacion.\n" +
									"Causa: " +  e.getMessage());
							e.printStackTrace();
					}
					
					int limit = 0;
						
					try {	
						sql = "SELECT count(*)-"+ConfigFileHandler.getMaxMessagesDataBase()+" FROM mensajes";
						LogWriter.write("INFO: Revisando cantidad de mensajes almacenados en la base de datos...");
						resultSet = statement.executeQuery(sql);
						resultSet.next();
						limit = resultSet.getInt(1);
						resultSet.close();
					} catch (SQLException e) {
						LogWriter.write(
								"ERROR: Falla durante la sincronizacion.\n" +
								"Causa: " +  e.getMessage());
						e.printStackTrace();
					}
					
					try {
						if (limit > 0 ) {
							LogWriter.write("INFO: El numero maximo de mensajes permitidos en la base de datos ha sido alcanzado.");
							LogWriter.write("INFO: Procediendo a eliminar " + limit + " mensajes...");
							sql = "SELECT mid FROM mensajes ORDER BY fecha ASC ,hora ASC LIMIT "+limit;
							resultSet = statement.executeQuery(sql);
							ArrayList<Integer> mids = new ArrayList<Integer>();
							while (resultSet.next()) {
								mids.add(resultSet.getInt(1));
							}
							resultSet.close();	
							for (Integer mid : mids) {
								sql = "DELETE FROM mensajes WHERE mid="+mid+";";
								statement.addBatch(sql);
							}
							statement.executeBatch();
						}
						ConnectionsPool.CloseConnections();
					} catch (SQLException e) {
						LogWriter.write(
								"ERROR: Falla durante la sincronizacion\n" +
								"Causa: " +  e.getMessage());
						e.printStackTrace();
					}
					finally {
						LogWriter.write("INFO: Mensajes sincronizados satisfactoriamente.");
						try {
							if (resultSet!=null) resultSet.close();
							statement.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
					
	            }
	        },  new DailyIterator(oraclesync.getHour(), oraclesync.getMinute(), oraclesync.getSecond()));
	    }
	}

	class User {
		public String code;
		public String password;
		public String name;
		public String posCode;
	}
}