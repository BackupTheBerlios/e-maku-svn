package com.kazak.comeet.server.businessrules;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.server.comunications.SocketWriter;
import com.kazak.comeet.server.database.sql.QueryRunner;
import com.kazak.comeet.server.database.sql.SQLBadArgumentsException;
import com.kazak.comeet.server.database.sql.SQLNotFoundException;
import com.kazak.comeet.server.misc.LogWriter;

public class GroupManager {

	private Iterator argsIterator;
	private ArrayList<QueryRunner> queries;
	
	public GroupManager(SocketChannel sock, Element args, Element packet, String id) {
		this.argsIterator = args.getChildren("arg").iterator();
		String type = args.getChildText("action");
		boolean result = false;
		String message = "";
		queries = new ArrayList<QueryRunner>();
		try {
			if ("add".equals(type)) {
				result = addGroup(packet);
			}
			else if ("edit".equals(type)) {
				result = addGroup(packet);
			}
			else if ("remove".equals(type)) {
				result = removeGroup(packet);
			}
		} catch (SQLNotFoundException e) {
			result = false;
			message = e.getMessage();
			LogWriter.write("ERROR: Ocurrio una falla mientras se procesaba operacion de Grupo [Tipo:"+type+"]");
			LogWriter.write("Causa: " + message);
		} catch (SQLBadArgumentsException e) {
			result = false;
			message = e.getMessage();
			LogWriter.write("ERROR: Ocurrio una falla mientras se procesaba operacion de Grupo [Tipo:"+type+"]");
			LogWriter.write("Causa: " + message);
		} catch (SQLException e) {
			result = false;
			message = e.getMessage();
			LogWriter.write("ERROR: Ocurrio una falla mientras se procesaba operacion de Grupo [Tipo:"+type+"]");
			LogWriter.write("Causa: " + message);
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
			message = "Los datos fueron almacenados satisfactoriamente.";
			TransactionRunner.notifyMessageReception(sock,id,message,"Transaccion sobre Grupos");
		}
		else {
			for (QueryRunner qRunner :queries) {
				qRunner.rollback();
			}
			if ("remove".equals(type)) {
				message =
					"El grupo debe estar vacio\n" +
					"para poder ser eliminado\n";
			}
			TransactionRunner.
			notifyErrorMessage
			(sock,id,"No se pudo procesar la transacción. Causa:\n" + message);
		}
	}
	
	private boolean addGroup(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		Iterator iterator = transaction.getChildren("package").iterator();
		while(iterator.hasNext()) {
			Element e = (Element)iterator.next();
			if (e.getChildren().size() > 0 ) {
				String sqlCode = ((Element)argsIterator.next()).getText();
				QueryRunner qRunner = new QueryRunner(sqlCode,packArgs(e));
				queries.add(qRunner);
				qRunner.setAutoCommit(false);
				qRunner.executeSQL();
			}
		}
		return true;
	}
	
	
	private boolean removeGroup(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		Iterator iterator = transaction.getChildren("package").iterator();
		
		Element element = (Element)iterator.next();
		String[] args = packArgs(element);
		String sqlCode = ((Element)argsIterator.next()).getText();
		QueryRunner queryRunner = new QueryRunner(sqlCode,args);
		queries.add(queryRunner);
		ResultSet resultSet = queryRunner.select();
		resultSet.next();
		int count = resultSet.getInt("count");
		resultSet.close();
		queryRunner.closeStatement();
		
		if (count>0) {
			return false;
		}
		sqlCode = ((Element)argsIterator.next()).getText();
		queryRunner = new QueryRunner(sqlCode,args);
		queries.add(queryRunner);
		resultSet = queryRunner.select();
		resultSet.next();
		count = resultSet.getInt("count");
		resultSet.close();
		queryRunner.closeStatement();
		if (count>0) {
			return false;
		}
		
		sqlCode = ((Element)argsIterator.next()).getText();
		queryRunner = new QueryRunner(sqlCode,packArgs(element));
		queries.add(queryRunner);
		queryRunner.setAutoCommit(false);
		queryRunner.executeSQL();
		
		return true;
	}
	
	public String[] packArgs(Element pack) {
		List list = pack.getChildren("field");
		Iterator iterator = list.iterator();
		String[] argsArray = new String[list.size()];
		int index = 0;
		while(iterator.hasNext()) {
			Element element = (Element) iterator.next();
			argsArray[index] = element.getValue();
			index++;
		}
		return argsArray;
	}
}