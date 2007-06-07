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
import com.kazak.smi.server.control.MessageDistributor;
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
			LogWriter.write("INFO: Iniciando demonio de sincronizacion");
			loadSettings();
			for (OracleSyncTask oraclesync:ConfigFileHandler.getSyncTaskList()) {				
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
	 * @throws SQLException 
	 */
	public SyncManager (SocketChannel sock) throws SQLException {
		this.sock = sock;
		
		// If there is not access to Oracle, stop sync task
    	if (!ConfigFileHandler.isConnectOnInit(1)) {
    		LogWriter.write("ADVERTENCIA: Proceso de sincronizacion con Oracle aplazado");
    		LogWriter.write("ADVERTENCIA: Motivo -> Base de datos deshabilitada desde configuracion [" 
    				+ ConfigFileHandler.getDBName(1) + "]");	

    		String msg = "ERROR: La base de datos Oracle se encuentra " + 
    					"deshabilitada desde la configuración del sistema.\n" +
    					"Para reactivar el servicio, modifique el archivo server.conf " + 
    					"y reinicie el servidor SMI.";
    		
			Element syncErr = new Element("ERRSYNC");
			Element message = new Element("message");
			message.setText(msg);
			syncErr.addContent(message);
			
			try {
				SocketWriter.write(sock,new Document(syncErr));
			} catch (IOException ex) {
				ex.printStackTrace();
			}	
    		return;
    	}

    	try {
            oracleSQL =	getOracleSQLString();
			loadSettings();
		} catch (FileNotFoundException e) {
			String msg = "ERROR: No se encontró el archivo de configuración de la sincronización.";
			MessageDistributor.sendAlarm("Archivo oracle.sql no encontrado",msg + "\nCausa:\n" + e.getMessage());
			Element syncErr = new Element("ERRSYNC");
			Element message = new Element("message");
			message.setText(msg);
			syncErr.addContent(message);
			
			try {
				SocketWriter.write(sock,new Document(syncErr));
			} catch (IOException ex) {
				ex.printStackTrace();
			}	
			e.printStackTrace();
			
		} catch (IOException e) {
			Element syncErr = new Element("ERRSYNC");
			Element message = new Element("message");
			message.setText("ERROR: Falla de E/S durante la sincronización.");
			syncErr.addContent(message);
			try {
				SocketWriter.write(sock,new Document(syncErr));
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
					SocketWriter.write(sock,new Document(reload));
					SocketWriter.write(sock,new Document(succesSync));
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
	public String getOracleSQLString() throws FileNotFoundException, IOException {
		String sql = "";
		String path = ServerConstants.CONF + ServerConstants.SEPARATOR + "oracle.sql";
		String line = "";
		
		BufferedReader in = new BufferedReader(new FileReader(path));
		while ((line = in.readLine()) != null)   {
			sql += " " + line;
		}
		in.close();

		if(sql==null || sql.equals("")) {
			LogWriter.write("ERROR: Sentencia SQL para sincronizacion es nula o vacia.");
			LogWriter.write("ERROR: Revisar archivo de configuracion: " + path);
		}

		return sql;
	}
	
	/**
	 * This method initializes the vectors where the users and POS info is stored
	 * 
	 */
	public void loadSettings() { //throws FileNotFoundException, IOException {
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
				String msg = "ERROR: La conexion a la base de datos " + oracleDB + " es invalida.";
				LogWriter.write(msg);
				MessageDistributor.sendAlarm("Conexion a Oracle -> " + oracleDB, msg);
			
				return false;
			}
			
			statement = oracleConnection.createStatement();
			LogWriter.write("SENTENCIA: " + oracleSQL);
			resultSet = statement.executeQuery(oracleSQL);
			int i=0;
			while (resultSet.next()) {
				String code = resultSet.getString(1).trim();
				String wsCode = resultSet.getString(2).trim();
				String wsPOSName = resultSet.getString(3).trim();
				String userName = resultSet.getString(4).trim();
				User user = new User();
				user.code = code;
				user.password =  generatePassword(code);
				user.name = userName; 
				user.posCode = wsCode;
				
				usersHash.put(code,user);
				if (!wsHash.containsKey(wsCode)) {
					wsHash.put(wsCode,wsPOSName);
				}
				i++;
			}
			LogWriter.write("INFO: Total de usuarios consultados desde Oracle = " + i);
			resultSet.close();
			statement.close();
			oracleConnection.close();
			
			return true;
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			
			return false;
		} catch (SQLException e) {
			String msg = "ERROR: No se pudo realizar la consulta en la base de datos " + oracleDB + ".\n" +
							"Proceso de sincronizacion cancelado." + 
							"\nCausa:\n" + e.getMessage();
			MessageDistributor.sendAlarm("Consulta a Oracle -> " + oracleDB, msg);
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
			resultSet = statement.executeQuery("SELECT login FROM usuarios WHERE login LIKE 'CV%' ORDER BY login");
			int i=0;
			while (resultSet.next()) {
				String code = resultSet.getString(1).trim();
				currentUsersVector.add(code);
				i++;
			}
			LogWriter.write("INFO: Total de colocadores actualmente registrados en PostgreSQL = " + i);
			resultSet.close();
			
		} catch (SQLException e) {
			String msg = "ERROR: Falla al consultar la base de datos PostgreSQL [" + pgdb + "] durante la sincronizacion.\n" +
								"Causa:\n" + e.getMessage();
			MessageDistributor.sendAlarm("Conexion a PostgreSQL -> " + pgdb, msg);
			processSQLException(pgdb,e,resultSet);
			
			return false;
		}
		
		try {	
			resultSet = statement.executeQuery("SELECT codigo FROM puntosv ORDER BY codigo");
			int i=0;
			while (resultSet.next()) {
				String code = resultSet.getString(1).trim();
				currentWSVector.add(code);
				i++;
			}
			LogWriter.write("INFO: Total de puntos de venta actualmente registrados en PostgreSQL = " + i);
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			String msg = "ERROR: Falla al consultar la base de datos PostgreSQL [" + pgdb + "].\nCausa:\n" + e.getMessage();
			MessageDistributor.sendAlarm("Conexion a PostgreSQL -> " + pgdb, msg);
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
		String text = "ERROR: Falla en la base de datos " + db + "\nCausa: " + e.getMessage();
		message.setText(text);
		errSync.addContent(message);
		
		try {
			SocketWriter.write(sock,new Document(errSync));
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
	 * @throws SQLException 
	 */
	private boolean storePostgresData() {
		
		String pgdb = ConfigFileHandler.getMainDataBase();
		Connection pgConnection = null;
		
		try {
			LogWriter.write("INFO: Sincronizando... [ Oracle -> PostgreSQL ]");
			pgConnection = ConnectionsPool.getConnection(pgdb);
			statement = pgConnection.createStatement();
		} catch (SQLException e) {
			processSQLException(pgdb,e,null);

			return false;
		}

		String SQL = "";
		LogWriter.write("INFO: Eliminando usuarios deshabilitados (Total = " + deletedUsersVector.size() + ")");
		for (String userCode : deletedUsersVector) {
			try {
				 SQL = "DELETE FROM " + "usuarios_pventa " + "WHERE uid=(SELECT uid FROM usuarios WHERE login='" + userCode + "')";
				 statement.execute(SQL);
				 SQL = "DELETE FROM usuarios WHERE login='" + userCode + "'";
				 statement.execute(SQL);
			} catch (SQLException e) {
				String msg = "ERROR: Falla mientras se eliminaba el usuario (deshabilitado) con codigo: " + userCode;
				MessageDistributor.sendAlarm("Eliminando usuario deshabilitado",msg 
						+ "\nSENTENCIA: " + SQL + "\nCausa:\n" + e.getMessage());
				LogWriter.write(msg);
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);

				return false;
			}
		}

		LogWriter.write("INFO: Eliminando puntos de venta deshabilitados (Total = " + deletedWSVector.size() + ")");
		for (String wsCode : deletedWSVector) {
			try {
				SQL = "DELETE FROM puntosv WHERE codigo='" + wsCode + "'";
				statement.execute(SQL);
			} catch (SQLException e) {
				String msg = "ERROR: Falla mientras se eliminaba el punto de venta (deshabilitado) con codigo: " + wsCode;
				MessageDistributor.sendAlarm("Eliminando punto de venta deshabilitado",msg 
						+ "\nSENTENCIA: " + SQL + "\nCausa:\n" + e.getMessage());
				LogWriter.write(msg);
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
					SQL = "INSERT INTO puntosv (codigo,nombre) values('" + key + "','" + name + "')";
					pgConnection.setAutoCommit(false);
					statement.execute(SQL);
					pgConnection.commit();
					LogWriter.write("INFO: Punto de colocacion => " + name + " adicionado");
				} else {
					LogWriter.write("ERROR: Codigo " + key + " no pertenece a ningun punto de venta.");
				}
			} catch (SQLException e) {
				try {
					pgConnection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				String msg = "ERROR: Falla mientras se ingresaba el punto de colocacion: {" + name + "} con codigo {" + key + "}";
				MessageDistributor.sendAlarm("Ingresando punto de colocacion",msg 
						+ "\nSENTENCIA: " + SQL + "\nCausa:\n" + e.getMessage());
				LogWriter.write(msg);
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);
				
				return false;
			}
			finally {
				try {
					pgConnection.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}				
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
					      "values('" + user.code + "',md5('" + user.password + "'),'" + user.name + "')";
					pgConnection.setAutoCommit(false);
					statement.execute(SQL);
					pgConnection.commit();
					LogWriter.write("INFO: Colocador => " + user.name + " adicionado");
				} else {
					LogWriter.write("ERROR: Codigo " + key + " no pertenece a ningun usuario");
				}
				
			} catch (SQLException e) {
				try {
					pgConnection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}				
				String msg = "ERROR: Falla mientras se ingresaba el usuario: {" + user.code + "," + user.name + "}";
				MessageDistributor.sendAlarm("Ingresando usuario",msg 
						+ "\nSENTENCIA: " + SQL + "\nCausa:\n" + e.getMessage());
				LogWriter.write(msg);
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);
				
				return false;
			}
			finally {
				try {
					pgConnection.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}				
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
				ResultSet uidResultSet = null;
				ResultSet posResultSet = null;

				SQL = "SELECT uid FROM usuarios WHERE usuarios.login='" + user.code + "'";
				try { 
					uidResultSet = statement.executeQuery(SQL);	 
					while(uidResultSet.next()) {
						uid = uidResultSet.getString("uid").trim();
					} 

				} catch (SQLException e) {
					String msg = "ERROR: Falla mientras se consultaba el usuario: {" + user.code + "}";
					MessageDistributor.sendAlarm("Consultando usuario",msg 
							+ "\nSENTENCIA: " + SQL + "\nCausa:\n" + e.getMessage());					
					LogWriter.write(msg);
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
					String msg = "ERROR: Falla mientras se consultaba el punto de venta: {" + user.posCode + "}";
					MessageDistributor.sendAlarm("Consultando punto de venta",msg 
							+ "\nSENTENCIA: " + SQL + "\nCausa:\n" + e.getMessage());					
					LogWriter.write(msg);
					LogWriter.write("SENTENCIA: " + SQL);
					processSQLException(pgdb,e,null);
					
					return false;
				}

				if (uid!=null) {
					if (posCode!=null) {
						SQL = "INSERT INTO usuarios_pventa (uid,codigo_pventa) VALUES (" + uid + "," + posCode + ")";
						try {
							pgConnection.setAutoCommit(false);
							statement.execute(SQL);
							pgConnection.commit();							
						} catch (SQLException e) {
							try {
								pgConnection.rollback();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}				
							String msg = "ERROR: Falla mientras se ingresaba relacion usuario/pos: {" + uid + "," + posCode + "}";
							MessageDistributor.sendAlarm("Insertando relacion usuario/pos",msg 
									+ "\nSENTENCIA: " + SQL + "\nCausa:\n" + e.getMessage());					
							LogWriter.write(msg);
							LogWriter.write("SENTENCIA: " + SQL);
							processSQLException(pgdb,e,null);
							
							return false;
						}
						finally {
							try {
								pgConnection.setAutoCommit(true);
							} catch (SQLException e) {
								e.printStackTrace();
							}				
						}
					} else {			
						String msg = "ERROR: Falla sincronizando, el codigo del punto de venta {" +
			              user.posCode + "} no existe, por favor verifique las bases de datos";
						MessageDistributor.sendAlarm("Punto de venta inexistente",msg); 
													
						Element syncErr = new Element("ERRSYNC");
						Element message = new Element("message");
						message.setText(msg);
						syncErr.addContent(message);
						
						try {
							SocketWriter.write(sock,new Document(syncErr));
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						LogWriter.write(msg);
						
						return false;
					}
				} else {
					String msg = "Error sincronizando, el uid de usuario para el codigo {" +
		              user.code + "} no existe, por favor verifique las bases de datos.";
					MessageDistributor.sendAlarm("Usuario inexistente",msg); 
					
					Element syncErr = new Element("ERRSYNC");
					Element message = new Element("message");
					message.setText(msg);
					syncErr.addContent(message);
					
					try {
						SocketWriter.write(sock,new Document(syncErr));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					LogWriter.write(msg);
					
					return false;
				}
				LogWriter.write("INFO: Asignando Colocador => " + user.name + " (" + user.code + ") " +
						        "al punto => " + posName + " {" + user.posCode + "}");
			} else {
				String msg = "ERROR: Codigo " + key + " no esta asignado a ningun punto de venta";
				MessageDistributor.sendAlarm("Codigo de POS inexistente",msg);
				LogWriter.write(msg);
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
        int i = 0;
		for (String userCode : currentUsersVector) {
			// If current user 
			if (usersHash.containsKey(userCode)) {
				usersHash.remove(userCode);
			}
			else {
				deletedUsersVector.add(userCode); // Invalid users vector
				i++;
			}
		}
		LogWriter.write("INFO: Cantidad = " + i);
		i=0;
        LogWriter.write("INFO: Filtrando puntos de venta a eliminar...");
		for (String wsCode : currentWSVector) {
			if (wsHash.containsKey(wsCode)) {
				wsHash.remove(wsCode);
			}
			else {
				deletedWSVector.add(wsCode); // Invalid pos vector
				i++;
			}
		}
		LogWriter.write("INFO: Cantidad = " + i);
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
        private String currentDate =  dateFormat.format(new Date());
	    private OracleSyncTask oracleSyncTask;
	    
	    public Cron(OracleSyncTask oraclesync){
	    	this.oracleSyncTask = oraclesync;
	    }
	    
	    public void start() {
	    		    	
	        scheduler.schedule(new SchedulerTask() {
	            public void run() {
	            	
	            	if (!ConfigFileHandler.isConnectOnInit(1)) {
	            		LogWriter.write("ADVERTENCIA: Tarea de sincronizacion con Oracle aplazada");
	            		LogWriter.write("ADVERTENCIA: Motivo -> Base de datos deshabilitada desde configuracion [" 
	            				+ ConfigFileHandler.getDBName(1) + "]");	
	            		return;
	            	}
 	            	
	                LogWriter.write("INFO: Iniciando sincronizacion programada [" + currentDate + "]");
	                LogWriter.write("INFO: Procesando usuarios...");
	                
	                loadSettings();
	                //Users sync
					if (loadOracleData()) {
						if (loadPostgresData()){
							filterInvalidData();
							String result = " con errores.";
							if (storePostgresData()) {
								result = " satisfactoriamente.";
							}
							LogWriter.write("INFO: Usuarios sincronizados" + result);
						} else {
							LogWriter.write("ERROR: Ocurrio un problema mientras se consultaban los datos desde PostgreSQL");
						}
					} else {
						LogWriter.write("ERROR: Ocurrio un problema mientras se consultaban los datos desde Oracle");
					}
					
					//Messages sync 
					LogWriter.write("INFO: Procesando mensajes...");
					int lifeTimeInDB = ConfigFileHandler.getMessageLifeTimeInDataBase();
					ResultSet resultSet = null;
					String sql = "";
					
					LogWriter.write("INFO: Tiempo de vida de mensajes en DB segun configuracion = " + lifeTimeInDB);
					
					if (lifeTimeInDB>0) { 
						
						int total = 0;

						try {	
							sql = "SELECT count(*) FROM mensajes WHERE current_date=(fecha+" + lifeTimeInDB + ")";
							LogWriter.write("INFO: Revisando cantidad de mensajes vencidos en la base de datos...");
							resultSet = statement.executeQuery(sql);
							resultSet.next();
							total = resultSet.getInt(1);
							LogWriter.write("INFO: Total de mensajes vencidos = " + total);
							resultSet.close();
						} catch (SQLException e) {
							String msg = "ERROR: Falla durante la sincronizacion\n" +
							"Causa: " +  e.getMessage(); 
							MessageDistributor.sendAlarm("Error de SQL",msg);
							LogWriter.write(msg);
							e.printStackTrace();
						}
												
						if(total > 0) {
							try {
								statement = ConnectionsPool.getConnection(ConfigFileHandler.getMainDataBase()).createStatement();
								sql = "DELETE FROM mensajes WHERE current_date=(fecha+" + lifeTimeInDB + ")";
								LogWriter.write("INFO: Eliminando mensajes con tiempo de vida superior a " + lifeTimeInDB + " dias");
								if (statement.execute(sql)) {
									LogWriter.write("SENTENCIA: " + sql);
									LogWriter.write("INFO: Mensajes antiguos borrados satisfactoriamente");
								}

							} catch (SQLException e) {
								String msg = "ERROR: Falla durante la sincronizacion\n" +
								"Causa: " +  e.getMessage(); 
								MessageDistributor.sendAlarm("Error de SQL",msg);
								LogWriter.write(msg);
								e.printStackTrace();
							}
						}
					}
					
					int msgMaxTotal = ConfigFileHandler.getMaxMessagesNumAllowed();
					
					LogWriter.write("INFO: Maximo numero de mensajes permitidos en DB: " + msgMaxTotal);
					
					if(msgMaxTotal>0) {

						int difference = 0;
						int total = 0;

						try {	
							sql = "SELECT count(*) FROM mensajes";
							LogWriter.write("INFO: Revisando cantidad de mensajes almacenados en la base de datos...");
							resultSet = statement.executeQuery(sql);
							resultSet.next();
							total = resultSet.getInt(1);
							LogWriter.write("INFO: Total de mensajes en el sistema = " + total);
							resultSet.close();
						} catch (SQLException e) {
							String msg = "ERROR: Falla durante la sincronizacion\n" +
							"Causa: " +  e.getMessage(); 
							MessageDistributor.sendAlarm("Error de SQL",msg);
							LogWriter.write(msg);
							e.printStackTrace();
						}

						difference = total - ConfigFileHandler.getMaxMessagesNumAllowed();

						try {
							if (difference > 0 ) {
								LogWriter.write("INFO: El numero maximo de mensajes permitidos en la base de datos ha sido alcanzado.");
								LogWriter.write("INFO: Procediendo a eliminar los " + difference + " mensajes mas antiguos...");
								sql = "SELECT mid FROM mensajes ORDER BY fecha ASC ,hora ASC LIMIT " + difference;
								resultSet = statement.executeQuery(sql);
								ArrayList<Integer> mids = new ArrayList<Integer>();
								while (resultSet.next()) {
									mids.add(resultSet.getInt(1));
								}
								resultSet.close();	
								for (Integer mid : mids) {
									sql = "DELETE FROM mensajes WHERE mid=" + mid + ";";
									statement.addBatch(sql);
								}
								statement.executeBatch();
							}
							ConnectionsPool.CloseConnections();
						} catch (SQLException e) {
							String msg = "ERROR: Falla durante la sincronizacion\n" +
							"Causa: " +  e.getMessage(); 
							MessageDistributor.sendAlarm("Error de SQL",msg);
							LogWriter.write(msg);
							e.printStackTrace();
						}
						finally {
							LogWriter.write("INFO: Mensajes sincronizados satisfactoriamente");
							try {
								if (resultSet!=null) { 
									resultSet.close();
								}
								statement.close();
							} catch (SQLException e1) {
								LogWriter.write("ERROR: " + e1.getMessage());
								e1.printStackTrace();
							}
						}
					}
					
	            }
	        },  new DailyIterator(oracleSyncTask.getHour(), oracleSyncTask.getMinute(), oracleSyncTask.getSecond()));
	    }
	}

	class User {
		public String code;
		public String password;
		public String name;
		public String posCode;
	}
}
