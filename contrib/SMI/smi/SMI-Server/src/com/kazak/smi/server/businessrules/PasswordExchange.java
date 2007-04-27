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

public class PasswordExchange {

	private Iterator it;
	
	public PasswordExchange(SocketChannel sock, Element args, Element packet, String id) {
		this.it = packet.getChildren("package").iterator();
		Iterator itArgs = args.getChildren("args").iterator();
		QueryRunner runQuery = null;
		while(itArgs.hasNext()) {
			Element element = (Element) itArgs.next();
			String sqlCode = element.getValue();
			String[] sqlArgs = packArgs();
			try {
				runQuery = new QueryRunner(sqlCode,sqlArgs);
				runQuery.setAutoCommit(false);
				runQuery.runSQL();
				runQuery.commit();
				TransactionRunner.successMessage(sock,id,"Clave cambiada satisfactoriamente");
			} catch (SQLException e) {
				runQuery.rollback();
				e.printStackTrace();
				LogWriter.write("Codigo error: "+e.getErrorCode());
				if (runQuery!=null) {
					runQuery.rollback();
				}
				TransactionRunner.errorMessage(
						 sock,
                    	 id,
                    	 "No se pudo cambiar la clave:\n" +
 						 "causa:\n"+e.getLocalizedMessage());
			} catch (SQLNotFoundException e) {
				e.printStackTrace();
				TransactionRunner.errorMessage(
						 sock,
						 id,
						 "La sentencia  " + sqlCode + " no existe");
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
	
	public String[] packArgs() {
		if (!it.hasNext()) {
			return null;
		}
		Element element = (Element)it.next();
		List list = element.getChildren();
		Iterator it = list.iterator();
		String[] ret = new String[list.size()];
		int index = 0;
		while(it.hasNext()) {
			Element e = (Element) it.next();
			ret[index] = e.getValue();
			index++;
		}
		return ret;
	}
}