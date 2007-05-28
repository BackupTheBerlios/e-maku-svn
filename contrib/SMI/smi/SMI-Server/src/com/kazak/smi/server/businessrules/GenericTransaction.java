package com.kazak.smi.server.businessrules;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;

public class GenericTransaction {

	private Iterator iterator;
	private ArrayList<QueryRunner> queries;
	public GenericTransaction(SocketChannel sock, Element args, Element packet, String id) {
		this.iterator = packet.getChildren("package").iterator();
		int count = args.getChildren("args").size();
		int passed = 0;
		Iterator argsIterator = args.getChildren("args").iterator();
		queries = new ArrayList<QueryRunner>();
		QueryRunner queryRunner = null;
		while(argsIterator.hasNext()) {
			Element element = (Element) argsIterator.next();
			String sqlCode = element.getValue();
			String[] sqlArgs = packArgs();
			try {
				queryRunner = new QueryRunner(sqlCode,sqlArgs);
				queryRunner.setAutoCommit(false);
				queries.add(queryRunner);
				queryRunner.executeSQL();
				passed ++;
			} catch (SQLException e) {
				e.printStackTrace();
				LogWriter.write("ERROR: Excepcion en SQL -> " + e.getErrorCode());
				if (queryRunner!=null) {
					queryRunner.rollback();
				}
				TransactionRunner.notifyErrorMessage(
						 sock,
                    	 id,
                    	 "No se pudo procesar la operación:\n" +
 						 "causa:\n"+e.getLocalizedMessage());
				break;
			} catch (SQLNotFoundException e) {
				e.printStackTrace();
				TransactionRunner.notifyErrorMessage(
						 sock,
						 id,
						 "La sentencia  " + sqlCode + " no existe.");
				break;
			} catch (SQLBadArgumentsException e) {
				e.printStackTrace();
				TransactionRunner.notifyErrorMessage(
						 sock,
						 id,
						 "Argumentos inválidos " +
						 "para la sentencia : " + sqlCode);
				break;
			}
		}
		if (passed==count) {
			for (QueryRunner qRunner :queries) {
				 qRunner.commit();
			}
			
			TransactionRunner.
			notifyMessageReception
			(sock,id,"Los datos fueron almacenados satisfactoriamente.","Transaccion Generica");
		}
		else {
			for (QueryRunner rq :queries) {
				rq.rollback();
			}
		}
	}
	
	public String[] packArgs() {
		if (!iterator.hasNext()) {
			return null;
		}
		Element element = (Element)iterator.next();
		List list = element.getChildren();
		Iterator listIterator = list.iterator();
		String[] ret = new String[list.size()];
		int index = 0;
		while(listIterator.hasNext()) {
			Element e = (Element) listIterator.next();
			ret[index] = e.getValue();
			index++;
		}
		return ret;
	}
}