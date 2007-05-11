package com.kazak.smi.server.businessrules;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;

public class PointSaleManager {

	private Iterator argsIterator;
	private ArrayList<QueryRunner> queries;
	
	public PointSaleManager(SocketChannel sock, Element args, Element packet, String id) {
		this.argsIterator = args.getChildren("arg").iterator();
		String type = args.getChildText("action");
		boolean result = false;
		String message = "";
		queries = new ArrayList<QueryRunner>();
		try {
			if ("add".equals(type)) {
				result = processPointOfSale(packet);
			}
			else if ("edit".equals(type)) {
				result = processPointOfSale(packet);
			}
			else if ("remove".equals(type)) {
				result = processPointOfSale(packet);
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
			for (QueryRunner queryRunner :queries) {
				queryRunner.commit();
			}
			Element reload = new Element("RELOADTREE");
			try {
				SocketWriter.writing(sock,new Document(reload));
			} catch (IOException e) {
				e.printStackTrace();
			}
			message = "Los datos fueron almacenados satisfactoriamente";
			TransactionRunner.notifyMessageReception(sock,id,message);
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
			TransactionRunner.notifyErrorMessage(sock,id,
							"No se pudo procesar la transacción. Causa:\n" + message);
		}
	}
	
	private boolean processPointOfSale(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		Iterator iterator = transaction.getChildren("package").iterator();
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			String sqlCode = ((Element)argsIterator.next()).getText();
			QueryRunner queryRunner = new QueryRunner(sqlCode,packArgs(element));
			queries.add(queryRunner);
			queryRunner.setAutoCommit(false);
			queryRunner.runSQL();
		}
		return true;
	}
	
	
	public String[] packArgs(Element pack) {
		List list = pack.getChildren("field");
		Iterator listIterator = list.iterator();
		String[] argsArray = new String[list.size()];
		int index = 0;
		while(listIterator.hasNext()) {
			Element element = (Element) listIterator.next();
			argsArray[index] = element.getValue();
			index++;
		}
		return argsArray;
	}
}