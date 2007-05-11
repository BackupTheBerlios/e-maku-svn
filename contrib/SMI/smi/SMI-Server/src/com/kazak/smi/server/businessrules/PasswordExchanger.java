package com.kazak.smi.server.businessrules;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;

public class PasswordExchanger {

	private Iterator iterator;
	
	public PasswordExchanger(SocketChannel sock, Element args, Element packet, String id) {
		this.iterator = packet.getChildren("package").iterator();
		Iterator argsIterator = args.getChildren("args").iterator();
		QueryRunner runQuery = null;
		while(argsIterator.hasNext()) {
			Element element = (Element) argsIterator.next();
			String sqlCode = element.getValue();
			String[] sqlArgs = packArgs();
			try {
				runQuery = new QueryRunner(sqlCode,sqlArgs);
				runQuery.setAutoCommit(false);
				runQuery.runSQL();
				runQuery.commit();
				TransactionRunner.notifyMessageReception(sock,id,"Clave cambiada satisfactoriamente");
			} catch (SQLException e) {
				runQuery.rollback();
				e.printStackTrace();
				LogWriter.write("Codigo error: "+e.getErrorCode());
				if (runQuery!=null) {
					runQuery.rollback();
				}
				TransactionRunner.notifyErrorMessage(
						 sock,
                    	 id,
                    	 "No se pudo cambiar la clave:\n" +
 						 "causa:\n"+e.getLocalizedMessage());
			} catch (SQLNotFoundException e) {
				e.printStackTrace();
				TransactionRunner.notifyErrorMessage(
						 sock,
						 id,
						 "La sentencia  " + sqlCode + " no existe.");
			} catch (SQLBadArgumentsException e) {
				e.printStackTrace();
				TransactionRunner.notifyErrorMessage(
						 sock,
						 id,
						 "Argumentos inválidos " +
						 "para la sentencia : " + sqlCode);
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
		String[] argsArray = new String[list.size()];
		int index = 0;
		while(listIterator.hasNext()) {
			Element e = (Element) listIterator.next();
			argsArray[index] = e.getValue();
			index++;
		}
		return argsArray;
	}
}