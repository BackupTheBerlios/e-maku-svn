package com.kazak.smi.server.businesrules;
import java.io.FileNotFoundException;
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
import com.kazak.smi.server.database.connection.PoolConexiones;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFile;


public class Sync {
	 
	private String sqlOracle =
	" select 'CV'||H.CONTVTA_PRS_DOCUMENTO Login,H.ubcneg_trtrio_codigo Codigo_punto,"+
    " (select nombre from territorios T"+
    "        where H.ubcneg_trtrio_codigo=T.codigo"+
    "          and T.negocio='S'"+
    "         and T.tpotrt_codigo=15) Nombre_Punto,"+
    "      (select P.nombres||' '||P.apellido1 from personas P"+
    "        where H.contvta_prs_documento=P.documento) Nombre_Colocador"+
    " from horariopersonas H"+
    " where H.fechafinal is null"+
    " and (select nombre from territorios T"+
    "      where H.ubcneg_trtrio_codigo=T.codigo) NOT LIKE '%MANUAL%'";
	
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
			LogWriter.write("Inciando demonio de sincronización");
			loadSettings();
			int hour   = ConfigFile.getHour();
			int minute = ConfigFile.getMinute();
			int second = ConfigFile.getSecond();
			Cron cron = new Cron(hour,minute,second);
			cron.start();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadSettings() throws FileNotFoundException, IOException {
		dataUser = new Hashtable<String, User>();
		dataWs = new Hashtable<String, String>();
		
		CurrentDataUser = new Vector<String>();
		CurrentDataWs = new Vector<String>();
		ForDeleteDataUser = new Vector<String>();
		ForDeleteDataWs = new Vector<String>();
	}
	
	public Sync (SocketChannel sock) {
		this.sock = sock;
		try {
			loadSettings();
		} catch (FileNotFoundException e) {
			Element errSync = new Element("ERRSYNC");
			Element message = new Element("message");
			message.setText(
					"Error, no se encontro el archivo de configuración de la sincronización");
			try {
				SocketWriter.writing(sock,new Document(errSync));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			Element errSync = new Element("ERRSYNC");
			Element message = new Element("message");
			message.setText("Error de E/S durante la sincronización");
			try {
				SocketWriter.writing(sock,new Document(errSync));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
		
		if (loadOracleData()) {
			if (loadCurrentData()){
				filter();
				storePostgresData();
				LogWriter.write("Bases de datos sincronizada");
				LogWriter.write("Limpiano y recargando la cache de usuarios");
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
	
	
	/**
	 * Load current data from PostgreSQL Data Base
	 */
	private boolean loadCurrentData() {

		LogWriter.write("Cargando datos actuales de la base de datos PostgreSQL");
		ResultSet rs = null;
		try {
			st = PoolConexiones.getConnection(ConfigFile.getMainDataBase()).createStatement();
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
			String text = "Error en la base de datos\n mensaje: " + e.getMessage();
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
		try {
			LogWriter.write("Cargando datos actuales de la base de datos Oracle");
			cnOracle = ConfigFile.getConnection(ConfigFile.getSecondDataBase());
			
			st = cnOracle.createStatement();
			rs = st.executeQuery(sqlOracle);
			while (rs.next()) {
			/*RandomAccessFile raf = new RandomAccessFile("/datos/datos_oracle.csv","r");
			String line = null;
			while ((line = raf.readLine())!=null) {
				StringTokenizer stk = new StringTokenizer(line,",");
				String code     = stk.nextToken();
				String wscode   = stk.nextToken();
				String wsnamepv = stk.nextToken();
				String nameus   = stk.nextToken();
				User user = new User();
				user.code = code;
				user.password =  generatePassword(code);
				user.name = nameus; 
				user.codepv = wscode;
				
				dataUser.put(code,user);
				if (!dataWs.containsKey(wscode)) {
					dataWs.put(wscode,wsnamepv);
				}*/
				String code     = rs.getString(1).trim();
				String wscode   = rs.getString(2).trim();
				String wsnamepv = rs.getString(3).trim();
				String nameus   = rs.getString(4).trim();
				User user = new User();
				user.code = code;
				user.password =  generatePassword(code);
				user.name = nameus; 
				user.codepv = wscode;
				
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
			String text = "Error en la base de datos\n mensaje: " + e.getMessage();
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
		try {
			LogWriter.write("Sincronizando....");
			st = PoolConexiones.getConnection(ConfigFile.getMainDataBase()).createStatement();
		} catch (SQLException e) {
			Element errSync = new Element("ERRSYNC");
			Element message = new Element("message");
			String text = "Error en la base de datos\n mensaje: " + e.getMessage();
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
				String text = "Error en la base de datos\n mensaje: " + e.getMessage();
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
				String text = "Error en la base de datos\n mensaje: " + e.getMessage();
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

		Set<String> keys = dataWs.keySet();
		for (String key : keys) {
			try {
				String name = dataWs.get(key);
				if (key!=null && name!=null) {
					String sql = 
						"INSERT INTO puntosv (codigo,nombre) " +
						"values('"+key+"','"+name+"')";
					st.execute(sql);
					LogWriter.write("Punto de colocacion => " + name+ " almacenado");
				}
			} catch (SQLException e) {
				Element errSync = new Element("ERRSYNC");
				Element message = new Element("message");
				String text = "Error en la base de datos\n mensaje: " + e.getMessage();
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
			try {
				User user = dataUser.get(key);
				String sql = 
					"INSERT INTO usuarios (login,clave,nombres) " +
					"values('"+user.code+"',md5('"+user.password+"'),'"+user.name+"')";
				st.execute(sql);
				LogWriter.write("Colocador => " + user.name+ " almacenado");
			} catch (SQLException e) {
				Element errSync = new Element("ERRSYNC");
				Element message = new Element("message");
				String text = "Error en la base de datos\n mensaje: " + e.getMessage();
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
					String sql = 
						"INSERT INTO usuarios_pventa (uid,codigo_pventa) " +
						"values" +
						"(" +
						"(SELECT uid FROM usuarios WHERE usuarios.login='"+user.code+"')," +
						"(SELECT codigo FROM puntosv WHERE puntosv.codigo='"+user.codepv+"')"+
						")";
					st.execute(sql);
					LogWriter.write(
							"Asignando Colocador => " + user.name+ "("+ user.code +") " +
							"al punto=> "+ user.codepv);
				}
			} catch (SQLException e) {
				Element errSync = new Element("ERRSYNC");
				Element message = new Element("message");
				String text = "Error en la base de datos\n mensaje: " + e.getMessage();
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
	    private int hourOfDay, minute, second;
	    public Cron(int hourOfDay, int minute, int second) {
	        this.hourOfDay = hourOfDay;
	        this.minute = minute;
	        this.second = second;
	    }

	    public void start() {
	        scheduler.schedule(new SchedulerTask() {
	            public void run() {
	                LogWriter.write("Syncronizando  fecha: " + dateFormat.format(new Date()));
	                
					if (loadOracleData()) {
						if (loadCurrentData()){
							filter();
							storePostgresData();
							LogWriter.write("Bases de datos sincronizada");
						}
					}
					//Mantenimiento 
					LogWriter.write("Inicio de mantenimiento de base de datos");
					ResultSet rs = null;
					try {
						st = PoolConexiones.getConnection(ConfigFile.getMainDataBase()).createStatement();
						String sql = "DELETE FROM mensajes WHERE current_date=(fecha+"+ConfigFile.getTimeAlifeMessageInDataBase()+")";
						LogWriter.write("Borrando mensajes con tiempo de vida igual a " + ConfigFile.getTimeAlifeMessageInDataBase());
						if (st.execute(sql)) {
							LogWriter.write("Mensajes borrados...");
						}
						sql = "SELECT count(*)-"+ConfigFile.getMaxMessagesDataBase()+" FROM mensajes";
						LogWriter.write("Liberando mensajes de  base de datos, Cantidad: " + ConfigFile.getTimeAlifeMessageInDataBase());
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
						PoolConexiones.CloseConnections();
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
	        }, new DailyIterator(hourOfDay, minute, second));
	    }
	}
	
	class User {
		public String code;
		public String password;
		public String name;
		public String codepv;
	}
}