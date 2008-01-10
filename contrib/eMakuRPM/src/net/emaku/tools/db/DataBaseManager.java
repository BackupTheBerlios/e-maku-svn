package net.emaku.tools.db;

import java.awt.Cursor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.emaku.tools.gui.ExportBar;

// This class manages the database connection

public class DataBaseManager {
	
	private static Properties properties;
	private static Connection connection;
	
	public static void loadDBProperties(Properties properties) { 
		DataBaseManager.properties = properties;
	}
	
	public static void connect() {
		try {
			System.out.println("* Loading database connection...");
			Class.forName(properties.getProperty("driver"));
			connection = DriverManager.getConnection(
					properties.getProperty("url"),
					properties.getProperty("user"),"");
		}
		catch (ClassNotFoundException CNFEe){
			System.out.println("ERROR: Database Driver not Found");
			System.out.println("Message: " + CNFEe.getMessage());
		} catch (SQLException SQLEe) {
			System.out.println("ERROR: Can't Open Database Connection");
			System.out.println("Message: " + SQLEe.getMessage());
			System.exit(0);
		}
	}
	
	public static int addReport(String reportCode, String sqlCode, String name) {
		int error = 0;
		String query = "SELECT id_sentencia_sql FROM sentencia_sql WHERE codigo=\'"+sqlCode+"\'";
		ResultSet rs = null;
		String idSentencia = "";
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				idSentencia = rs.getString(1);
			} else {
				ExportBar.addRecord("ERROR: The report record " + reportCode + " has no SQL sentence linked");
				return 1;
			}
			st.close();
			rs.close();
			query = "SELECT count(*) FROM reportes WHERE codigo=\'" + reportCode + "\'";
			st = connection.createStatement();
			rs = st.executeQuery(query);
			rs.next();
			int records = rs.getInt(1);
			st.close();
			rs.close();
			
			if(records == 0) {
				ExportBar.addRecord("  Inserting SQL report record in database [ID:"+idSentencia+"]");
				query = "INSERT INTO reportes (codigo,id_sentencia_sql,nombre)" +
				"VALUES (\'" + reportCode + "\'," + idSentencia + ",\'" + name + "\')";
			} else {
				ExportBar.addRecord("  Updating SQL report record in database [ID:"+idSentencia+"]");
				query = "UPDATE reportes SET id_sentencia_sql=" + idSentencia + ",nombre=\'" + name
				+ "\' WHERE codigo=\'"+reportCode+"\'";
			}
			connection.createStatement().execute(query);
			
		} catch (SQLException e) {
			ExportBar.addRecord("ERROR: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			error = 2;
		}
		
		return error;
	}

	public static ResultSet getResultSet(JFrame frame, String query) {
		ResultSet resultSet = null;
		try {
			Statement st = connection.createStatement();
			resultSet = st.executeQuery(query);	
		} catch (SQLException e) {
			System.out.println("ERROR: Can not fetch result set at DataBaseManager Class");
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		JOptionPane.showMessageDialog(frame,e.getMessage(),"Error in SQL Query!",
    				JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return resultSet;
	}
	
	public static boolean updateReportDescription(String sqlCode,String text) {
		boolean result = false;
		String query = "UPDATE reportes SET descripcion='" + text + "' WHERE codigo=\'" + sqlCode +"\'";
		System.out.println("Q: " + query);
		try {
			Statement st = connection.createStatement();
			result = st.execute(query);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static boolean insertSQLRecord(String sqlCode) {
		boolean result = false;
		String query = "INSERT INTO sentencia_sql (codigo,nombre,descripcion,sentencia) VALUES ('"+sqlCode+"','','','')";
		System.out.println("Q: " + query);
		try {
			Statement st = connection.createStatement();
			result = st.execute(query);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return result;
	}
		
	public static boolean insertReportRecord(String reportCode, String sqlId) {
		boolean result = false;
		String query = "INSERT INTO reportes (id_reporte,codigo,nombre,descripcion,id_sentencia_sql,plantilla) " +
				"VALUES ((SELECT max(id_reporte)+1 FROM reportes),'"+reportCode+"','','',"+sqlId+",'')";
		System.out.println("Q: " + query);
		try {
			Statement st = connection.createStatement();
			result = st.execute(query);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return result;
	}
	
	public static String getReportDescription(String reportCode) {
		String sql = "SELECT descripcion FROM reportes WHERE codigo=\'"+reportCode+"\'";
		System.out.println("Q: " + sql);
		String description = "";
		try {
			Statement st = connection.createStatement();
			ResultSet resultSet = st.executeQuery(sql);
			if (resultSet.next()) {
				description = resultSet.getString("descripcion");
				if (description == null) {
					description = "No description available...";
				}
			} else {
				description = "No description available..."; 
			}
			st.close();
			resultSet.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Desc: " + description);
		
		return description;
	}
	
	public static boolean existsRecord(String reportCode) {
		boolean flag = true;
		String sql = "SELECT id_reporte FROM reportes WHERE codigo=\'"+reportCode+"\'";
		System.out.println("Q: " + sql);
		try {
			Statement st = connection.createStatement();
			ResultSet resultSet = st.executeQuery(sql);
			if (!resultSet.next()) {
				flag = false; 
			}
			st.close();
			resultSet.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	public static String getSQLId(String sqlCode) {
		String sql = "SELECT id_sentencia_sql FROM sentencia_sql WHERE codigo='" + sqlCode + "'";
		System.out.println("Q: " + sql);
		String id = "";
		try {
			Statement st = connection.createStatement();
			ResultSet resultSet = st.executeQuery(sql);
			if (resultSet.next()) {
				id = resultSet.getString("id_sentencia_sql");
			} else {
				id = "NO_ID"; 
			}
			st.close();
			resultSet.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("SQL Id: " + id);
		
		return id;
	}
	
	
	public static String getQuery(String code) {
		String query = "SELECT sentencia FROM sentencia_sql WHERE codigo='"+code+"'";
		try {
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				query = rs.getString("sentencia");
			} else {
				query = "NO_SQL";
			}
			st.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return query;
	}
	
	public static boolean setQuery (String code, String text) throws SQLException {
		boolean ret = true;
		StringBuffer s = new StringBuffer (text);
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt ( i ) == '\'' || s.charAt ( i ) == '\"')
				s.insert (i++, '\\');
		}
		text = s.toString();
		String query = "UPDATE sentencia_sql SET sentencia ='"+text+"' WHERE codigo='"+code+"'";
		try {
			Statement st = connection.createStatement();
			ret = st.execute(query);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static Connection getConnection() {
		return connection;
	}

	public static Properties getProperties() {
		return properties;
	}

	public static void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}