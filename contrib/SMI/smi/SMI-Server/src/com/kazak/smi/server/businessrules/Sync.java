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
import com.kazak.smi.server.misc.settings.ConfigFile;
import com.kazak.smi.server.misc.ServerConst;
import com.kazak.smi.server.misc.settings.OracleSynchronized;

/**
 * This class handles all the sync processes between the SMI and the Oracle/PostgreSQL DB
 * 
 * @author spy
 *
 */

public class Sync {
	
	private String oracleSQL = "";
	private Connection cnOracle;
	private Hashtable<String,User> dataUser; 
	private Hashtable<String,String> dataWs;
	
	private Vector<String> CurrentDataUser;
	private Vector<String> ForDeleteDataUser;
	private Vector<String> ForDeleteDataWs;
	private Vector<String> CurrentDataWs;
	private Statement st = null;
	private SocketChannel sock;

	/**
	 * This constructor method loads the schedule of sync tasks to the system
	 * 
	 */
	public Sync() {
		try {
            oracleSQL =	getOracleSQLString();
			LogWriter.write("INFO: Iniciando demonio de sincronización");
			loadSettings();
			for (OracleSynchronized oraclesync:ConfigFile.getOraclesync()) {				
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
	public Sync (SocketChannel sock) {
		this.sock = sock;
		try {
            oracleSQL =	getOracleSQLString();
			loadSettings();
		} catch (FileNotFoundException e) {
			Element errSync = new Element("ERRSYNC");
			Element message = new Element("message");
			message.setText(
					"ERROR: No se encontró el archivo de configuración de la sincronización");
			try {
				SocketWriter.writing(sock,new Document(errSync));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			Element errSync = new Element("ERRSYNC");
			Element message = new Element("message");
			message.setText("ERROR: Falla de E/S durante la sincronización");
			try {
				SocketWriter.writing(sock,new Document(errSync));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
		
		if (loadOracleData()) {
			if (loadPostgresData()){
				filter();
				String result = " con errores.";
				if (storePostgresData()) {
					result = " satisfactoriamente.";
				}				
				LogWriter.write("Base de datos sincronizada " + result);
				LogWriter.write("Limpiando y recargando el cache de usuarios");
				Element reload = new Element("RELOADTREE");
				Element succesSync = new Element("SUCCESSSYNC");
				try {
					SocketWriter.writing(sock,new Document(reload));
					SocketWriter.writing(sock,new Document(succesSync));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				LogWriter.write("Ocurrio un problema mientras se consultaban los datos desde PostgreSQL");
			}
			
		} else {
			LogWriter.write("Ocurrio un problema mientras se consultaban los datos desde Oracle");
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
				BufferedReader in = new BufferedReader(new FileReader(ServerConst.CONF + ServerConst.SEPARATOR + "oracle.sql"));
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
		dataUser = new Hashtable<String, User>();
		dataWs = new Hashtable<String, String>();
		
		CurrentDataUser = new Vector<String>();
		CurrentDataWs = new Vector<String>();
		ForDeleteDataUser = new Vector<String>();
		ForDeleteDataWs = new Vector<String>();
	}	
	
	/**
	 * Load data from Oracle Data Base
	 */
	private boolean loadOracleData() {
		ResultSet rs = null;
		String oracleDB = ConfigFile.getSecondDataBase();
		try {
			LogWriter.write("Cargando registros actualizados de la base de datos Oracle");
			cnOracle = ConfigFile.getConnection(oracleDB);
						
			if (cnOracle == null) {
				LogWriter.write("La conexion a la base de datos es nula");
				return false;
			}
			
			st = cnOracle.createStatement();
			LogWriter.write("SENTENCIA: " + oracleSQL);
			rs = st.executeQuery(oracleSQL);
			int i=0;
			while (rs.next()) {
				String code     = rs.getString(1).trim();
				String wscode   = rs.getString(2).trim();
				String wsnamepv = rs.getString(3).trim();
				String nameus   = rs.getString(4).trim();
				User user     = new User();
				user.code     = code;
				user.password =  generatePassword(code);
				user.name     = nameus; 
				user.codepv   = wscode;
				
				dataUser.put(code,user);
				if (!dataWs.containsKey(wscode)) {
					dataWs.put(wscode,wsnamepv);
				}
				i++;
			}
			LogWriter.write("Total de usuarios registrados: " + i);
			rs.close();
			st.close();
			cnOracle.close();
			return true;
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			processSQLException(oracleDB,e,rs);
			return false;
		}
	}
	
	/**
	 * This method loads current info of the system from a PostgreSQL database
	 */
	private boolean loadPostgresData() {

		LogWriter.write("Cargando registros actuales de la base de datos PostgreSQL");
		ResultSet rs = null;
		String pgdb = ConfigFile.getMainDataBase();
		try {
			st = ConnectionsPool.getConnection(pgdb).createStatement();
			rs = st.executeQuery("SELECT login FROM usuarios WHERE login LIKE 'CV%'");
			int i=0;
			while (rs.next()) {
				String code = rs.getString(1).trim();
				CurrentDataUser.add(code);
				i++;
			}
			LogWriter.write("Total de usuarios registrados: " + i);
			rs.close();
			
		} catch (SQLException e) {
			processSQLException(pgdb,e,rs);
			return false;
		}
		
		try {	
			rs = st.executeQuery("SELECT codigo FROM puntosv");
			int i=0;
			while (rs.next()) {
				String code   = rs.getString(1).trim();
				CurrentDataWs.add(code);
				i++;
			}
			LogWriter.write("Total de puntos de venta registrados: " + i);
			rs.close();
			st.close();
		} catch (SQLException e) {
			processSQLException(pgdb,e,rs);
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
		// errSync.addContent(message); - TODO: Consultar esta posible falla
		
		String text = "Error en la base de datos " + db + "\nCausa: " + e.getMessage();
		message.setText(text);
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
			st.close();
		} catch (SQLException e1) {
			LogWriter.write("Error cerrando conexion a base de datos " + db);
			e1.printStackTrace();
		}		
	}	
	
	/**
	 * This method saves data in the Postgresql Data Base Server
	 */
	private boolean storePostgresData() {
		
		String pgdb = ConfigFile.getMainDataBase();
		
		try {
			LogWriter.write("Sincronizando... [ Oracle -> PostgreSQL ]");
			st = ConnectionsPool.getConnection(pgdb).createStatement();
		} catch (SQLException e) {
			processSQLException(pgdb,e,null);
			return false;
		}

		String SQL = "";
		LogWriter.write("Eliminando usuarios deshabilitados...");
		for (String code : ForDeleteDataUser) {
			try {
				 SQL = "DELETE FROM " + "usuarios_pventa " + "WHERE uid=(SELECT uid FROM usuarios WHERE login='"+code+"')";
				 st.execute(SQL);
				 SQL = "DELETE FROM usuarios WHERE login='"+code+"'";
				 st.execute(SQL);
			} catch (SQLException e) {
				LogWriter.write("Error procesando usuario deshabilitado con codigo: " + code);
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);
				return false;
			}
		}

		LogWriter.write("Eliminando puntos de venta deshabilitados...");
		for (String code : ForDeleteDataWs) {
			try {
				SQL = "DELETE FROM puntosv WHERE codigo='"+code+"'";
				st.execute(SQL);
			} catch (SQLException e) {
				LogWriter.write("Error procesando punto de venta deshabilitado con codigo: " + code);
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);
				return false;
			}
		}
		
        String name = "";
		Set<String> keys = dataWs.keySet();
		LogWriter.write("Actualizando puntos de venta...");
		
		for (String key : keys) {
			try {
				name = dataWs.get(key);
				if (key!=null && name!=null) {
					SQL = "INSERT INTO puntosv (codigo,nombre) values('"+key+"','"+name+"')";
					st.execute(SQL);
					LogWriter.write("Punto de colocacion => " + name+ " almacenado");
				} else {
					LogWriter.write("Advertencia: Codigo " + key + " no pertenece a ningun punto de venta.");
				}
			} catch (SQLException e) {
				LogWriter.write("Error procesando punto de colocacion: {" + name + "} con codigo {" + key + "}");
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);
				return false;
			}
		}

		keys = dataUser.keySet();
		LogWriter.write("Actualizando usuarios del sistema...");
		
		for (String key : keys) {
			User user = null;
			try {
				user = dataUser.get(key);
				if (user != null) {
					SQL = "INSERT INTO usuarios (login,clave,nombres) " + 
					      "values('"+user.code+"',md5('"+user.password+"'),'"+user.name+"')";
					st.execute(SQL);
					LogWriter.write("Colocador => " + user.name + " almacenado");
				} else {
					LogWriter.write("Advertencia: Codigo " + key + " no pertenece a ningun usuario.");
				}
				
			} catch (SQLException e) {
				LogWriter.write("Error mientras se ingresaba el usuario: {" + user.code + "," + user.name + "}");
				LogWriter.write("SENTENCIA: " + SQL);
				processSQLException(pgdb,e,null);
				return false;
			}
		}
		
		keys = dataUser.keySet();
		LogWriter.write("Actualizando relacion de usuarios y puntos de venta...");
		
		for (String key : keys) {
			User user = dataUser.get(key);

			if (user.codepv!=null) {
				String codigo_pventa      = null;
				String uid                = null;
				ResultSet RSuid           = null;
				ResultSet RScodigo_pventa = null;

				SQL = "SELECT uid FROM usuarios WHERE usuarios.login='" + user.code + "'";
				try { 
					RSuid = st.executeQuery(SQL);	 
					while(RSuid.next()) {
						uid = RSuid.getString("uid").trim();
					} 

				} catch (SQLException e) {
					LogWriter.write("Error mientras se consultaba el usuario: {" + user.code + "}");
					LogWriter.write("SENTENCIA: " + SQL);
					processSQLException(pgdb,e,null);
					return false;
				}

				SQL = "SELECT codigo FROM puntosv WHERE puntosv.codigo='" + user.codepv + "'";
				try {	
					RScodigo_pventa = st.executeQuery(SQL);
					while(RScodigo_pventa.next()) {
						codigo_pventa = RScodigo_pventa.getString("codigo").trim();
					}
					RScodigo_pventa.close();
					RSuid.close();
				} catch (SQLException e) {
					LogWriter.write("Error mientras se consultaba el punto de venta: {" + user.codepv + "}");
					LogWriter.write("SENTENCIA: " + SQL);
					processSQLException(pgdb,e,null);
					return false;
				}

				if (uid!=null) {
					if (codigo_pventa!=null) {
						SQL = "INSERT INTO usuarios_pventa (uid,codigo_pventa) VALUES (" + uid + "," + codigo_pventa + ")";
						try {
							st.execute(SQL);
						} catch (SQLException e) {
							LogWriter.write("Error mientras se ingresaba relacion usuario/pos: {" + uid + "," + codigo_pventa + "}");
							LogWriter.write("SENTENCIA: " + SQL);
							processSQLException(pgdb,e,null);
							return false;
						}
					} else {			
						Element errSync = new Element("ERRSYNC");
						Element message = new Element("message");
						String text = "Error sincronizando, el codigo del punto de venta {" +
						              user.codepv + "} no existe, por favor verifique las bases de datos";
						message.setText(text);
						try {
							SocketWriter.writing(sock,new Document(errSync));
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						LogWriter.write(text);
						return false;
					}
				} else {
					Element errSync = new Element("ERRSYNC");
					Element message = new Element("message");
					String text = "Error sincronizando, el uid de usuario para el codigo {" +
					              user.code + "} no existe, por favor verifique las bases de datos.";
					message.setText(text);
					try {
						SocketWriter.writing(sock,new Document(errSync));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					LogWriter.write(text);
					return false;
				}
				LogWriter.write("Asignando Colocador => " + user.name + "(" + user.code + ") " +
						        "al punto => "+ user.codepv);
			} else {
				LogWriter.write("Advertencia: Codigo " + key + " no esta asignado a ningun punto de venta.");
			}
		}
		try {
			st.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * This method selects the old records which must to be deleted 
	 */
	private void filter() {
		for (String code : CurrentDataUser) {
			if (dataUser.containsKey(code)) {
				dataUser.remove(code);
			}
			else {
				LogWriter.write("Adicionando usuario {" + code + "} a la lista de eliminacion.");
				ForDeleteDataUser.add(code);
			}
		}
		
		for (String code : CurrentDataWs) {
			if (dataWs.containsKey(code)) {
				dataWs.remove(code);
			}
			else {
				LogWriter.write("Adicionando punto de venta {" + code + "} a la lista de eliminacion.");
				ForDeleteDataWs.add(code);
			}
		}
	}
	
	/**
	 * This method creates a password from a given string
	 */
	private String generatePassword(String orig) {
		int index = orig.length() > 5 ? (orig.length() - 3) : 0;
		if (index > 0) {
			String begin= orig.substring(0,2);
			String end  = orig.substring(index,orig.length());
			return (begin+end);
		}
		return orig;
	}
	
	class Cron {

	    private Scheduler scheduler = new Scheduler();
	    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS");
	    private OracleSynchronized oraclesync;
	    
	    public Cron(OracleSynchronized oraclesync){
	    	this.oraclesync=oraclesync;
	    }
	    
	    public void start() {
	        scheduler.schedule(new SchedulerTask() {
	            public void run() {
	                LogWriter.write("Iniciando sincronizacion programada [" + dateFormat.format(new Date()) + "]");
	                
	                LogWriter.write("Procesando usuarios...");
	                //Users sync
					if (loadOracleData()) {
						if (loadPostgresData()){
							filter();
							String result = " con errores.";
							if (storePostgresData()) {
								result = " satisfactoriamente.";
							}
							LogWriter.write("Usuarios sincronizados " + result);
						} else {
							LogWriter.write("Ocurrio un problema mientras se consultaban los datos desde PostgreSQL");
						}
					} else {
						LogWriter.write("Ocurrio un problema mientras se consultaban los datos desde Oracle");
					}
					
					//Messages sync 
					LogWriter.write("Procesando mensajes...");
					ResultSet rs = null;
					String sql = "";
					
					try {
						st = ConnectionsPool.getConnection(ConfigFile.getMainDataBase()).createStatement();
						sql = "DELETE FROM mensajes WHERE current_date=(fecha+" + ConfigFile.getTimeAlifeMessageInDataBase() + ")";
						LogWriter.write("Borrando mensajes con tiempo de vida igual a " + ConfigFile.getTimeAlifeMessageInDataBase());
						if (st.execute(sql)) {
							LogWriter.write("Mensajes antiguos borrados satisfactoriamente.");
						}
						
					} catch (SQLException e) {
							LogWriter.write(
									"Error durante la sincronización\n" +
									"Causa: " +  e.getMessage());
							e.printStackTrace();
					}
					
					int limit = 0;
						
					try {	
						sql = "SELECT count(*)-"+ConfigFile.getMaxMessagesDataBase()+" FROM mensajes";
						LogWriter.write("Liberando mensajes de  base de datos, Cantidad: " + ConfigFile.getTimeAlifeMessageInDataBase());
						rs = st.executeQuery(sql);
						rs.next();
						limit = rs.getInt(1);
						rs.close();
					} catch (SQLException e) {
						LogWriter.write(
								"Error durante la sincronizacion\n" +
								"Causa: " +  e.getMessage());
						e.printStackTrace();
					}
					
					try {
						if (limit > 0 ) {
							LogWriter.write("Numero de mensajes a liberar: " + limit);
							sql = "SELECT mid FROM mensajes ORDER BY fecha ASC ,hora ASC LIMIT "+limit;
							rs = st.executeQuery(sql);
							ArrayList<Integer> mids = new ArrayList<Integer>();
							while (rs.next()) {
								mids.add(rs.getInt(1));
							}
							rs.close();	
							for (Integer mid : mids) {
								sql = "DELETE FROM mensajes WHERE mid="+mid+";";
								st.addBatch(sql);
							}
							st.executeBatch();
						}
						ConnectionsPool.CloseConnections();
					} catch (SQLException e) {
						LogWriter.write(
								"Error durante la sincronizacion\n" +
								"Causa: " +  e.getMessage());
						e.printStackTrace();
					}
					finally {
						LogWriter.write("Mensajes sincronizados satisfactoriamente.");
						try {
							if (rs!=null) rs.close();
							st.close();
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
		public String codepv;
	}
}