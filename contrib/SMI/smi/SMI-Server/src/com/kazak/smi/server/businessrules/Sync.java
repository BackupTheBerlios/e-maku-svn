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
		
	public Sync() {
		try {
            oracleSQL =	getOracleSQLString();
			LogWriter.write("INFO: Inciando demonio de sincronización");
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
				storePostgresData();
				LogWriter.write("Bases de datos sincronizada");
				LogWriter.write("Limpiano y recargando el cache de usuarios");
				Element reload = new Element("RELOADTREE");
				Element succesSync = new Element("SUCCESSSYNC");
				try {
					SocketWriter.writing(sock,new Document(reload));
					SocketWriter.writing(sock,new Document(succesSync));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

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
	
	public void loadSettings() throws FileNotFoundException, IOException {
		dataUser = new Hashtable<String, User>();
		dataWs = new Hashtable<String, String>();
		
		CurrentDataUser = new Vector<String>();
		CurrentDataWs = new Vector<String>();
		ForDeleteDataUser = new Vector<String>();
		ForDeleteDataWs = new Vector<String>();
	}	
	
	/**
	 * Load current data from PostgreSQL Data Base
	 */
	private boolean loadPostgresData() {

		LogWriter.write("Cargando datos actuales de la base de datos PostgreSQL");
		ResultSet rs = null;
		String pgdb = ConfigFile.getMainDataBase();
		try {
			st = ConnectionsPool.getConnection(pgdb).createStatement();
			rs = st.executeQuery("SELECT login FROM usuarios WHERE login like 'CV%'");
			while (rs.next()) {
				String code   = rs.getString(1).trim();
				CurrentDataUser.add(code);
			}
			rs.close();
			
			rs = st.executeQuery("SELECT codigo FROM puntosv");
			while (rs.next()) {
				String code   = rs.getString(1).trim();
				CurrentDataWs.add(code);
			}
			rs.close();
			st.close();
			return true;
		} catch (SQLException e) {
			Element errSync = new Element("ERRSYNC");
			Element message = new Element("message");
			String text = "Error en la base de datos " + pgdb + "\nMensaje: " + e.getMessage();
			message.setText(text);
			try {
				SocketWriter.writing(sock,new Document(errSync));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			LogWriter.write(text);
			e.printStackTrace();
			
			try {
				if (rs!=null) rs.close();
				st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		}
	}
	
	/**
	 * Load data from Oracle Data Base
	 */
	private boolean loadOracleData() {
		ResultSet rs = null;
		String oracleDB = ConfigFile.getSecondDataBase();
		try {
			LogWriter.write("Cargando datos actuales de la base de datos Oracle");
			cnOracle = ConfigFile.getConnection(oracleDB);
			
			st = cnOracle.createStatement();
			LogWriter.write("SENTENCIA: " + oracleSQL);
			rs = st.executeQuery(oracleSQL);
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
			}
			rs.close();
			st.close();
			cnOracle.close();
			return true;
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			Element errSync = new Element("ERRSYNC");
			Element message = new Element("message");
			String text = "Error en la base de datos " + oracleDB + "\nMensaje: " + e.getMessage();
			message.setText(text);
			try {
				SocketWriter.writing(sock,new Document(errSync));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			LogWriter.write(text);
			e.printStackTrace();
			try {
				if (rs!=null) rs.close();
				st.close();
				cnOracle.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		}
	}
	
	/**
	 * Save data in the Postgresql Data Base Server
	 */
	private void storePostgresData() {
		
		String pgdb = ConfigFile.getMainDataBase();
		try {
			LogWriter.write("Sincronizando...");
			st = ConnectionsPool.getConnection(pgdb).createStatement();
		} catch (SQLException e) {
			Element errSync = new Element("ERRSYNC");
			Element message = new Element("message");
			String text = "Error en la base de datos " + pgdb + "\nMensaje: " + e.getMessage();
			message.setText(text);
			try {
				SocketWriter.writing(sock,new Document(errSync));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			LogWriter.write(text);
			e.printStackTrace();
			try {
				st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return;
		}
		for (String code : ForDeleteDataUser) {
			try {
				st.execute(
						"DELETE FROM " +
						"usuarios_pventa " +
						"WHERE uid=(SELECT uid FROM usuarios WHERE login='"+code+"')");
				st.execute(
						"DELETE FROM usuarios WHERE login='"+code+"'");
			} catch (SQLException e) {
				Element errSync = new Element("ERRSYNC");
				Element message = new Element("message");
				String text = "Error en la base de datos " + pgdb + " mientras se eliminan usuarios.\nMensaje: " + e.getMessage();
				message.setText(text);
				try {
					SocketWriter.writing(sock,new Document(errSync));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				LogWriter.write(text);
				e.printStackTrace();
			}
		}

		for (String code : ForDeleteDataWs) {
			try {
				st.execute("DELETE FROM puntosv WHERE codigo='"+code+"'");
			} catch (SQLException e) {
				Element errSync = new Element("ERRSYNC");
				Element message = new Element("message");
				String text = "Error en la base de datos " + pgdb + " mientras se eliminan puntos de venta.\nMensaje: " + e.getMessage();
				message.setText(text);
				try {
					SocketWriter.writing(sock,new Document(errSync));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				LogWriter.write(text);
				e.printStackTrace();
			}
		}
        String name = "";
		Set<String> keys = dataWs.keySet();
		for (String key : keys) {
			try {
				name = dataWs.get(key);
				if (key!=null && name!=null) {
					String sql = 
						"INSERT INTO puntosv (codigo,nombre) " +
						"values('"+key+"','"+name+"')";
					st.execute(sql);
					LogWriter.write("Punto de colocacion => " + name+ " almacenado");
				} else {
					LogWriter.write("Advertencia: Codigo " + key + " no pertenece a ningun punto de venta.");
				}
			} catch (SQLException e) {
				Element errSync = new Element("ERRSYNC");
				Element message = new Element("message");
				String text = "Error en la base de datos " + pgdb 
				               + " mientras se ingresaba el punto de colocacion \""+ name 
				               + "\" con codigo: "+ key +".\nMensaje: " + e.getMessage();
				message.setText(text);
				try {
					SocketWriter.writing(sock,new Document(errSync));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				LogWriter.write(text);
				e.printStackTrace();
			}
		}
		keys = dataUser.keySet();
		for (String key : keys) {
			User user = null;
			try {
				user = dataUser.get(key);
				if (user != null) {
					String sql = 
						"INSERT INTO usuarios (login,clave,nombres) " +
						"values('"+user.code+"',md5('"+user.password+"'),'"+user.name+"')";
					st.execute(sql);
					LogWriter.write("Colocador => " + user.name + " almacenado");
				} else {
					LogWriter.write("Advertencia: Codigo " + key + " no pertenece a ningun usuario.");
				}
				
			} catch (SQLException e) {
				
				Element errSync = new Element("ERRSYNC");
				Element message = new Element("message");
				String text = "Error en la base de datos " + pgdb + " mientras se ingresaba el usuario [" 
				               + user.code +"," + user.name + "].\nMensaje: " + e.getMessage();
				message.setText(text);
				try {
					SocketWriter.writing(sock,new Document(errSync));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				LogWriter.write(text);
				e.printStackTrace();
			}
		}
		
		keys = dataUser.keySet();
		for (String key : keys) {
			User user = dataUser.get(key);
			try {
				if (user.codepv!=null) {
					ResultSet RSuid = st.executeQuery("SELECT uid FROM usuarios WHERE usuarios.login='"+user.code+"'");
					String uid = null;
					while(RSuid.next()) {
						   uid = RSuid.getString("uid").trim();
					}
					ResultSet RScodigo_pventa = st.executeQuery("SELECT codigo FROM puntosv WHERE puntosv.codigo='"+user.codepv+"'");
					String codigo_pventa = null;
					while(RScodigo_pventa.next()) {
						   codigo_pventa = RSuid.getString("codigo_pventa").trim();
					}
					RScodigo_pventa.close();
					RSuid.close();
					
					if (uid!=null) {
						if (codigo_pventa!=null) {
							st.execute("INSERT INTO usuarios_pventa (uid,codigo_pventa) VALUES("+uid+","+codigo_pventa+")");
						}
						else {
							Element errSync = new Element("ERRSYNC");
							Element message = new Element("message");
							String text = "Error sincronizando, el codigo_pventa del punto de venta\n"+
							 user.codepv+" no existe en la base de datos, porfavor verifique las bases de datos";
							message.setText(text);
							try {
								SocketWriter.writing(sock,new Document(errSync));
							} catch (IOException ex) {
								ex.printStackTrace();
							}
							LogWriter.write(text);
						}
					}
					else {
						Element errSync = new Element("ERRSYNC");
						Element message = new Element("message");
						String text = "Error sincronizando, el uid de usuario para el codigo\n"+
						 user.code+" no existe en la base de datos, porfavor verifique las bases de datos";
						message.setText(text);
						try {
							SocketWriter.writing(sock,new Document(errSync));
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						LogWriter.write(text);
					}
					LogWriter.write(
							"Asignando Colocador => " + user.name+ "("+ user.code +") " +
							"al punto=> "+ user.codepv);
				} else {
					LogWriter.write("Advertencia: Codigo " + key + " no esta asignado a ningun punto de venta.");
				}
			} catch (SQLException e) {
				Element errSync = new Element("ERRSYNC");
				Element message = new Element("message");
				String text = "Error en la base de datos " + pgdb + " mientras se ingresaba relacion usuarios-pventa [" 
				              + user.code +","+ user.codepv + "]\n.Mensaje: " + e.getMessage();
				message.setText(text);
				try {
					SocketWriter.writing(sock,new Document(errSync));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				LogWriter.write(text);
				e.printStackTrace();
			}
		}
		try {
			st.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	private void filter() {
		for (String code : CurrentDataUser) {
			if (dataUser.containsKey(code)) {
				dataUser.remove(code);
			}
			else {
				ForDeleteDataUser.add(code);
			}
		}
		
		for (String code : CurrentDataWs) {
			if (dataWs.containsKey(code)) {
				dataWs.remove(code);
			}
			else {
				ForDeleteDataWs.add(code);
			}
		}
	}
	
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
	                LogWriter.write("Syncronizando fecha: " + dateFormat.format(new Date()));
	                
					if (loadOracleData()) {
						if (loadPostgresData()){
							filter();
							storePostgresData();
							LogWriter.write("Bases de datos sincronizada");
						}
					}
					//Mantenimiento 
					LogWriter.write("Inicio de mantenimiento de base de datos");
					ResultSet rs = null;
					try {
						st = ConnectionsPool.getConnection(ConfigFile.getMainDataBase()).createStatement();
						String sql = "DELETE FROM mensajes WHERE current_date=(fecha+"+oraclesync.getTimeAlifeMessageInDataBase()+")";
						LogWriter.write("Borrando mensajes con tiempo de vida igual a " + oraclesync.getTimeAlifeMessageInDataBase());
						if (st.execute(sql)) {
							LogWriter.write("Mensajes borrados...");
						}
						sql = "SELECT count(*)-"+oraclesync.getMaxMessagesDataBase()+" FROM mensajes";
						LogWriter.write("Liberando mensajes de  base de datos, Cantidad: " + oraclesync.getTimeAlifeMessageInDataBase());
						rs = st.executeQuery(sql);
						rs.next();
						int limit = rs.getInt(1);
						rs.close();
						if (limit > 0 ) {
							LogWriter.write("Nro de mensajes a liberar " + limit);
							sql = "SELECT mid from mensajes ORDER BY fecha asc ,hora asc limit "+limit;
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
						LogWriter.write("Mantenimiento de base de datos terminada");
						ConnectionsPool.CloseConnections();
					} catch (SQLException e) {
						LogWriter.write(
								"Error durante la sincronización\n" +
								"Causa: " +  e.getMessage());
						e.printStackTrace();
					}
					finally {
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