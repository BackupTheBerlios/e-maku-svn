package server.database.connection;

import java.sql.*;
import java.util.*;

public class PooledConnections {
	private String driver, url, username, password;
	//private Vector<Connection> connections;
	private Integer index = 0;
	//private int maxConnections;
	private LinkedList<Connection> linkedList;
	private Iterator<Connection> its;
	public PooledConnections(String driver, String url, String username,
			String password, int maxConnections) throws SQLException {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		//this.maxConnections = maxConnections;
		//connections = new Vector<Connection>(maxConnections);
		linkedList = new LinkedList<Connection>();
		for (int i = 0; i < maxConnections; i++) {
			linkedList.add(makeNewConnection());
		}
		its = linkedList.iterator();
	}

	public synchronized Connection getConnection() {
		if (!its.hasNext()){
			its = linkedList.iterator();
		}
		Connection c = its.next();
		return c;
	}

	/*private boolean checkConnection(Connection c, int index) {
		boolean valid = true;
		try {
			valid = c.isClosed();
		} catch (SQLException e) {
			valid = false;
		}
		if (valid) {
			c = null;
			try {
				c = makeNewConnection();
			} catch (SQLException e) {
				c = null;
				valid = false;
			}
			connections.set(index,c);
		}
		connections.set(index,c);
		return valid;
	}*/
	
	private Connection makeNewConnection() throws SQLException {
		try {
			Class.forName(driver);
			Connection c = DriverManager.getConnection(url, username, password);
			return (c);
		} catch (ClassNotFoundException cnfe) {
			throw new SQLException("Can't find class for driver: " + driver);
		}
	}

	/*public synchronized void closeAllConnections() {
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
	}*/
}