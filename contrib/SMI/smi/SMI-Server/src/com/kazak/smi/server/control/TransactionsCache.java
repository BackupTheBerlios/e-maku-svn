package com.kazak.smi.server.control;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.kazak.smi.server.database.sql.CloseSQL;
import com.kazak.smi.server.database.sql.RunQuery;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;

public class TransactionsCache {

	private static Hashtable<String,Transaction> transactions;
	
	public static void loadCache() {
		transactions = new Hashtable<String,Transaction>();
		try {
			RunQuery runQuery = new RunQuery("SRV0001");
			ResultSet rs = runQuery.runSELECT();
			while (rs.next()) {
				String code = rs.getString("codigo");
				String driver = rs.getString("driver");
				String args = rs.getString("args");
				byte[] bytes = args.getBytes();
				ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				SAXBuilder sax = new SAXBuilder(false);
				Document doc = sax.build(in);
				Transaction tr = new Transaction(driver,doc.getRootElement());
				transactions.put(code,tr);
			}
			CloseSQL.close(rs);
		} catch (SQLNotFoundException e) {
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Transaction getTransaction(String code) {
		return transactions.get(code);
	}
	
	public static class Transaction {
		
		private String driver;
		private Element args;
		
		public Transaction(String driver, Element args) {
			super();
			this.driver = driver;
			this.args = args;
		}

		public Element getArgs() {
			return args;
		}

		public String getDriver() {
			return driver;
		}
	}
}
