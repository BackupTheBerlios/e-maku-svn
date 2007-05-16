package com.kazak.smi.server.businessrules;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.database.sql.QueryClosingHandler;
import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;

public class UserManager {

	private Iterator argsIterator;
	private ArrayList<QueryRunner> queries;
	private String[] userInfoArray;
	private Vector<String[]> wsArray = new Vector<String[]>();

	public UserManager(SocketChannel sock, Element args, Element packet, String id) {
		this.argsIterator = args.getChildren("arg").iterator();
		String type = args.getChildText("action");
		boolean result = false;
		String message = "";
		queries = new ArrayList<QueryRunner>();
		try {
			if ("add".equals(type)) {
				result = addUser(packet);
			}
			else if ("edit".equals(type)) {
				result = editUser(packet);
			}
			else if ("remove".equals(type)) {
				result = removeUser(packet);
			}
		} catch (SQLNotFoundException e) {
			result = false;
			message = e.getMessage();
		} catch (SQLBadArgumentsException e) {
			result = false;
			message = e.getMessage();
		} catch (SQLException e) {
			result = false;
			message = e.getMessage();
		}
		if (result) {
			for (QueryRunner qRunner :queries) {
				qRunner.commit();
			}
						
			Element reload = new Element("RELOADTREE");
			try {
				SocketWriter.write(sock,new Document(reload));
			} catch (IOException e) {
				e.printStackTrace();
			}
			message = "La operación fue realizada satisfactoriamente.";
			TransactionRunner.notifyMessageReception(sock,id,message);
		}
		else {
			for (QueryRunner qRunner :queries) {
	 			 qRunner.rollback();
			}
			TransactionRunner.
			notifyErrorMessage
			(sock,id,"No se pudo insertar el usuario. Causa:\n" + message);
		}
	}
	
	private boolean addUser(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		LogWriter.write("INFO: Adicionando usuario al sistema...");
		Iterator iterator = transaction.getChildren("package").iterator();
		while(iterator.hasNext()) {
			Element e = (Element)iterator.next();
			List packetsList = e.getChildren("subpackage");
			int packetsListSize = packetsList!=null ? packetsList.size() : 0;
			if ((e.getChildren().size() - packetsListSize) > 0 ||
				(packetsListSize>0)) {
				if (packetsListSize>0) {
					Iterator packetsIterator = packetsList.iterator();
					String sqlCode = ((Element)argsIterator.next()).getText();
					while (packetsIterator.hasNext()) {
						Element element = (Element)packetsIterator.next();
						String[] sqlArgs = getPackArgs(element);
						wsArray.add(sqlArgs);
						QueryRunner queryRunner = new QueryRunner(sqlCode,sqlArgs);
						queries.add(queryRunner);
						queryRunner.setAutoCommit(false);
						queryRunner.executeSQL();
					}
				}
				else {
					userInfoArray = getPackArgs(e);
					String sqlCode = ((Element)argsIterator.next()).getText();
					QueryRunner qRunner = new QueryRunner(sqlCode,userInfoArray);
					queries.add(qRunner);
					qRunner.setAutoCommit(false);
					qRunner.executeSQL();
				}
			}
		}
		return true;
	}
	
	private boolean editUser(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		LogWriter.write("INFO: Editando usuario del sistema...");
		Iterator iterator = transaction.getChildren("package").iterator();
		
		Element element = (Element)iterator.next();
		userInfoArray = getPackArgs(element);
		String sqlCode = ((Element)argsIterator.next()).getText();
		QueryRunner queryRunner = new QueryRunner(sqlCode,userInfoArray);
		queries.add(queryRunner);
		queryRunner.setAutoCommit(false);
		queryRunner.executeSQL();
			
		element = (Element)iterator.next();
		String[] args = getPackArgs(element);
		sqlCode = ((Element)argsIterator.next()).getText();
		queryRunner = new QueryRunner(sqlCode,args);
		queries.add(queryRunner);
		queryRunner.setAutoCommit(false);
		queryRunner.executeSQL();
		
		element = (Element)iterator.next();
		List packetsList = element.getChildren("subpackage");
		int packetsListSize = packetsList!=null ? packetsList.size() : 0;
		if (packetsListSize>0) {
			Iterator packetsIterator = packetsList.iterator();
			sqlCode = ((Element)argsIterator.next()).getText();
			while (packetsIterator.hasNext()) {
				Element packetArg = (Element)packetsIterator.next();
				String[] sqlArgs = getPackArgs(packetArg);
				wsArray.add(sqlArgs);
				queryRunner = new QueryRunner(sqlCode,sqlArgs);
				queries.add(queryRunner);
				queryRunner.setAutoCommit(false);
				queryRunner.executeSQL();
			}
		}
		return true;
	}
	
	private boolean removeUser(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		LogWriter.write("INFO: Eliminando usuario del sistema...");
		Iterator iterator = transaction.getChildren("package").iterator();
		
		Element element = (Element)iterator.next();
		String[] args = getPackArgs(element);
		String sqlCode = ((Element)argsIterator.next()).getText();
		QueryRunner qRunner = new QueryRunner(sqlCode,args);
		queries.add(qRunner);
		qRunner.setAutoCommit(false);
		qRunner.executeSQL();
		
		element = (Element)iterator.next();
		args = getPackArgs(element);
		sqlCode = ((Element)argsIterator.next()).getText();
		qRunner = new QueryRunner(sqlCode,args);
		queries.add(qRunner);
		qRunner.setAutoCommit(false);
		qRunner.executeSQL();
		
		return true;
	}
	
	public String[] getPackArgs(Element pack) {
		List list = pack.getChildren("field");
		Iterator iterator = list.iterator();
		String[] argsArray = new String[list.size()];
		int index = 0;
		while(iterator.hasNext()) {
			Element e = (Element) iterator.next();
			Attribute attribute = e.getAttribute("arg");
			argsArray[index] = e.getValue();
			if (attribute!=null && attribute.getValue().equals("edit")) {
				argsArray[index] = getOldPassword(argsArray[index-1]);
			}
			index++;
		}
		return argsArray;
	}
	
	private String getOldPassword(String login){
		QueryRunner queryRunner = null;
		ResultSet resultSet = null;
		try {
			queryRunner = new QueryRunner("SEL0029",new String[]{login});
			resultSet = queryRunner.select();
		    if (resultSet.next()) {
		    	return resultSet.getString(1);
		    }
		} catch (SQLNotFoundException e) {
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			QueryClosingHandler.close(resultSet);
			queryRunner.closeStatement();
		}
		return null;
	}
}