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

import com.kazak.smi.server.database.sql.QueryClosingHandler;
import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;

public class TransactionsCache {

	private static Hashtable<String,Transaction> transactions;
	
	public static void loadCache() {
		transactions = new Hashtable<String,Transaction>();
		try {
			QueryRunner runQuery = new QueryRunner("SRV0001");
			ResultSet resultSet = runQuery.select();
			while (resultSet.next()) {
				String code   = resultSet.getString("codigo");
				String driver = resultSet.getString("driver");
				String args   = resultSet.getString("args");
				byte[] bytes  = args.getBytes();
				ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				SAXBuilder sax = new SAXBuilder(false);
				Document doc   = sax.build(in);
				Transaction transaction = new Transaction(driver,doc.getRootElement());
				transactions.put(code,transaction);
			}
			QueryClosingHandler.close(resultSet);
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