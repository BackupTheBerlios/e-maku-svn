package server.database.connection;

import java.sql.*;
import java.util.*;

public class PooledConnections {
	private String driver, url, username, password;
	private Vector<Connection> connections;
	private int index = 0;
	private int maxConnections;
	
	public PooledConnections(String driver, String url, String username,
			String password, int maxConnections) throws SQLException {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		this.maxConnections = maxConnections;
		connections = new Vector<Connection>(maxConnections);
		for (int i = 0; i < maxConnections; i++) {
			connections.add(makeNewConnection());
		}
	}

	public Connection getConnection() {
		Connection c = connections.get(index);
		index++;
		if (index==maxConnections) {
			index=0;
		}
		return c;
	}

	private Connection makeNewConnection() throws SQLException {
		try {
			Class.forName(driver);
			Connection c = DriverManager.getConnection(url, username, password);
			return (c);
		} catch (ClassNotFoundException cnfe) {
			throw new SQLException("Can't find class for driver: " + driver);
		}
	}

	public synchronized void closeAllConnections() {
		closeConnections(connections);
		connections = new Vector<Connection>();
	}

	private void closeConnections(Vector<Connection> connections) {
		try {
			for (int i = 0; i < connections.size(); i++) {
				Connection connection = (Connection) connections.elementAt(i);
				if (!connection.isClosed()) {
					connection.close();
				}
			}
		} catch (SQLException sqle) {
		}
	}
}