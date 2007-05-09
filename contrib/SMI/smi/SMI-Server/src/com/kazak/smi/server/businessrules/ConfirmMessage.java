package com.kazak.smi.server.businessrules;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;

public class ConfirmMessage {

	private Iterator iterator;
	
	public ConfirmMessage(SocketChannel sock, Element args, Element packet, String id) {
		this.iterator = packet.getChildren("package").iterator();
		Iterator argsIterator = args.getChildren("args").iterator();
		QueryRunner runQuery = null;
		while(argsIterator.hasNext()) {
			Element element = (Element) argsIterator.next();
			String sqlCode = element.getValue();
			
			Element nextElement = (Element)iterator.next();
			List list = nextElement.getChildren();
			String[] sqlArgs = new String[5];
			Iterator it = list.iterator();

			sqlArgs[0] = ((Element) it.next()).getValue();
			sqlArgs[1] = new Date().toString();
			sqlArgs[2] = ((Element) it.next()).getValue();
			sqlArgs[3] = ((Element) it.next()).getValue();
			sqlArgs[4] = ((Element) it.next()).getValue();
			
			try {
				runQuery = new QueryRunner(sqlCode,sqlArgs);
				runQuery.setAutoCommit(false);
				runQuery.runSQL();
				runQuery.commit();
				LogWriter.write(
						"INFO: Confirmada lectura del mensaje con destino {" + 
						((Element)list.get(3)).getValue() + "} / Asunto: [" + 
						((Element)list.get(4)).getValue() + "] / Remitido por: {" + 
						((Element)list.get(5)).getValue() + "}");
			} catch (SQLException e) {
				runQuery.rollback();
				LogWriter.write("ERROR: " + e.getErrorCode());
				e.printStackTrace();
				if (runQuery!=null) {
					runQuery.rollback();
				}
				TransactionRunner.errorMessage(
						 sock,
                    	 id,
                    	 "No se pudo procesar la operacion:\n" +
 						 "Causa:\n" + e.getLocalizedMessage());
			} catch (SQLNotFoundException e) {
				e.printStackTrace();
				TransactionRunner.errorMessage(
						 sock,
						 id,
						 "La sentencia  " + sqlCode + " no existe.");
			} catch (SQLBadArgumentsException e) {
				e.printStackTrace();
				TransactionRunner.errorMessage(
						 sock,
						 id,
						 "Argumentos invalidos " +
						 "para la sentencia : " + sqlCode);
			}
		}
	}
}
