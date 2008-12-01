package server.database.connection;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import com.javaexchange.dbConnectionBroker.DbConnectionBroker;

public class PooledConnections {
	private String driver, url, username, password;
	private Integer index = 0;
	private Connection transConnection;
	private Iterator<Connection> its;
	private int minConnections;
	private int maxConnections;
	private DbConnectionBroker myBroker;
	
	public PooledConnections(String driver, String url, String username,
			String password, int maxConnections) throws SQLException {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		this.maxConnections = maxConnections;
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			String tmpDir  = System.getProperty("java.io.tmpdir");
			String fileSep = System.getProperty("file.separator");
			String logFile = osSeparator+tmpDir+osSeparator+"poolConections";
			myBroker = new DbConnectionBroker(driver,url,username,password,2,8,logFile,1);
		} 
		catch (IOException e)  { 
			e.printStackTrace();
		}
		transConnection = DriverManager.getConnection(url, username, password);
	}

	public Connection getConnection() {
		return transConnection;
	}

	public Connection getMultiConnection() {
		return myBroker.getConnection();
	}
	
	public int getIdMultiConnection(Connection conn) {
		return myBroker.idOfConnection(conn);
	}
	
	public void freeConnection(Connection conn) {
		myBroker.freeConnection(conn);
	}

}
