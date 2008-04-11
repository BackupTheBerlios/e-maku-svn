package net.emaku.tools.db;

import java.awt.Cursor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.emaku.tools.gui.ExportBar;
import net.emaku.tools.structures.FormsData;
import net.emaku.tools.structures.QueriesData;

// This class manages the database connection

public class DataBaseManager {
	
	private static Properties properties;
	private static Connection connection;
	private static Hashtable<String,String> objectCodes = new Hashtable<String,String>();
	
	public static void loadDBProperties(Properties properties) { 
		DataBaseManager.properties = properties;
	}
	
	public static void connect() {
		try {
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
			System.out.println("ERROR: Can not fetch the result set at DataBaseManager Class");
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		JOptionPane.showMessageDialog(frame,e.getMessage(),"Error in SQL Query!",
    				JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return resultSet;
	}
	
	public static Vector<String> getForms() {
		Vector<String> formCodes = new Vector<String>();
		String query = "SELECT codigo,nombre FROM transacciones ORDER BY codigo";
		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				String code = rs.getString(1).trim();
				String name = rs.getString(2).trim();
				if (name == null) {
					name = "Description undefined";
				}
				formCodes.add(code);
				objectCodes.put(code, name);
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return formCodes;	
	}
	
	public static Vector<String> getQueries() {
		Vector<String> queryCodes = new Vector<String>();
		String query = "SELECT codigo,nombre FROM sentencia_sql ORDER BY codigo";
		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				String code = rs.getString(1).trim();
				String name = rs.getString(2);
				if (name == null) {
					name = "Description undefined";
				}
				queryCodes.add(code);
				objectCodes.put(code, name);
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return queryCodes;	
	}
	
	public static void getReportTips() {
		String query = "SELECT codigo,nombre FROM reportes ORDER BY codigo";
		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				String code = rs.getString(1).trim();
				String name = rs.getString(2).trim();
				if (name == null) {
					name = "Description undefined";
				}
				objectCodes.put(code, name);
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Hashtable<String,String> getToolTips() {
		getReportTips();
		return objectCodes;
	}

	public static FormsData getForm(String code) {
		FormsData form = new FormsData(); 
		String query = "SELECT nombre,descripcion,driver,args_driver,metodo,args_metodo,perfil FROM " +
				"transacciones WHERE codigo='" + code + "'";
		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				String name = rs.getString(1);
				String description = rs.getString(2);
				String driver = rs.getString(3);
				String argsDriver = rs.getString(4);
				String method = rs.getString(5);
				String argsMethod = rs.getString(6);
				String profile = rs.getString(7);
				form = new FormsData(name,description,driver,argsDriver,method,argsMethod,profile);
			} 
		} catch (SQLException e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}

		return form;	
	}
	
	public static QueriesData geteMakuQuery(String code) {
		QueriesData queries = new QueriesData(); 
		String query = "SELECT nombre,descripcion,sentencia FROM " +
				"sentencia_sql WHERE codigo='" + code + "'";
		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				String name = rs.getString(1);
				String description = rs.getString(2);
				String sentence = rs.getString(3);
				queries = new QueriesData(name,description,sentence);
			} 
		} catch (SQLException e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}

		return queries;	
	}
	
	public static Vector<Vector<String>> getQueriesEntries(String keywords) {
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		String query = "SELECT codigo,descripcion FROM " +
				"sentencia_sql WHERE nombre LIKE '%" + keywords + "%' OR descripcion LIKE '%" + keywords + "%'";
		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			while(rs.next()) {
				String code = rs.getString(1);
				String description = rs.getString(2);
				Vector<String> record = new Vector<String>();
				record.add(code);
				record.add(description);
				result.add(record);
			} 
		} catch (SQLException e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}

		return result;	
	}
	
	public static Vector<Vector<String>> getReportsEntries(String keywords) {
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		String query = "SELECT codigo,descripcion FROM " +
				"reportes WHERE nombre LIKE '%" + keywords + "%' OR descripcion LIKE '%" + keywords + "%'";
		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			while(rs.next()) {
				String code = rs.getString(1);
				String description = rs.getString(2);
				Vector<String> record = new Vector<String>();
				record.add(code);
				record.add(description);
				result.add(record);
			} 
		} catch (SQLException e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}

		return result;	
	}
	
	public static Vector<Vector<String>> getFormsEntries(String keywords) {
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		String query = "SELECT codigo,descripcion FROM " +
				"transacciones WHERE nombre LIKE '%" + keywords + "%' OR descripcion LIKE '%" + keywords + "%'";
		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			while(rs.next()) {
				String code = rs.getString(1);
				String description = rs.getString(2);
				Vector<String> record = new Vector<String>();
				record.add(code);
				record.add(description);
				result.add(record);
			} 
		} catch (SQLException e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}

		return result;	
	}
	
	public static String getSQLQuery(String code) {
		String sql = ""; 
		String query = "SELECT sentencia FROM sentencia_sql WHERE codigo='" + code + "'";
		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				sql = rs.getString(1);
			} 
		} catch (SQLException e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}

		return sql;	
	}
	
	public static String getProfile(String code) {
		String profile = ""; 
		String query = "SELECT perfil FROM transacciones WHERE codigo='" + code + "'";
		ResultSet rs = null;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				profile = rs.getString(1);
			} 
		} catch (SQLException e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}

		return profile;	
	}
	
	public static boolean updateForm(String sqlCode,FormsData data) {
		boolean result = false;
		String query = "UPDATE transacciones SET nombre='" + data.getName() + "',descripcion='" + data.getDescription() 
		+ "',driver='" + data.getDriver() + "',args_driver='" + data.getDriverArgs() + "',metodo='" + data.getMethod() 
		+ "',args_metodo='" + data.getMethodArgs() + "',perfil='" + data.getProfile() 
		+ "' WHERE codigo=\'" + sqlCode +"\'";
		
		try {
			Statement st = connection.createStatement();
			result = st.execute(query);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	public static boolean updateQuery(String sqlCode,QueriesData data) {
		boolean result = false;
		String query = "UPDATE sentencia_sql SET nombre='" + data.getName() + "',descripcion='" 
				+ data.getDescription()  + "',sentencia='" + data.getSQL() + "' WHERE codigo=\'" + sqlCode +"\'";
		try {
			Statement st = connection.createStatement();
			result = st.execute(query);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public static boolean updateEmakuSQL(String sqlCode,String xml) {
		boolean result = false;
		String query = "UPDATE sentencia_sql SET sentencia='" + xml 
		+ "' WHERE codigo=\'" + sqlCode +"\'";
		
		try {
			Statement st = connection.createStatement();
			result = st.execute(query);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	

	public static boolean updateProfile(String sqlCode,String xml) {
		boolean result = false;
		String query = "UPDATE transacciones SET perfil='" + xml 
		+ "' WHERE codigo=\'" + sqlCode +"\'";
		
		try {
			Statement st = connection.createStatement();
			result = st.execute(query);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static boolean updateReportDescription(String sqlCode,String text) {
		boolean result = false;
		String query = "UPDATE reportes SET descripcion='" + text + "' WHERE codigo=\'" + sqlCode +"\'";
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
		
		return description;
	}
	
	public static boolean existsRecord(String reportCode) {
		boolean flag = true;
		String sql = "SELECT id_reporte FROM reportes WHERE codigo=\'"+reportCode+"\'";
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
