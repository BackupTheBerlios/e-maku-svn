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

	private Iterator it;
	private ArrayList<QueryRunner> querys;
	public GenericTransaction(SocketChannel sock, Element args, Element packet, String id) {
		this.it = packet.getChildren("package").iterator();
		int count = args.getChildren("args").size();
		int passed = 0;
		Iterator itArgs = args.getChildren("args").iterator();
		querys = new ArrayList<QueryRunner>();
		QueryRunner runQuery = null;
		while(itArgs.hasNext()) {
			Element element = (Element) itArgs.next();
			String sqlCode = element.getValue();
			String[] sqlArgs = packArgs();
			try {
				runQuery = new QueryRunner(sqlCode,sqlArgs);
				runQuery.setAutoCommit(false);
				querys.add(runQuery);
				runQuery.runSQL();
				passed ++;
			} catch (SQLException e) {
				e.printStackTrace();
				LogWriter.write("Codigo error: "+e.getErrorCode());
				if (runQuery!=null) {
					runQuery.rollback();
				}
				TransactionRunner.errorMessage(
						 sock,
                    	 id,
                    	 "No se pudo procesar la operacion:\n" +
 						 "causa:\n"+e.getLocalizedMessage());
				break;
			} catch (SQLNotFoundException e) {
				e.printStackTrace();
				TransactionRunner.errorMessage(
						 sock,
						 id,
						 "La sentencia  " + sqlCode + " no existe");
				break;
			} catch (SQLBadArgumentsException e) {
				e.printStackTrace();
				TransactionRunner.errorMessage(
						 sock,
						 id,
						 "Argumentos invalidos " +
						 "para la sentencia : " + sqlCode);
				break;
			}
		}
		if (passed==count) {
			for (QueryRunner rq :querys) {
				rq.commit();
			}
			
			TransactionRunner.
			successMessage
			(sock,id,"Los datos fueron almacenados satisfactoriamente");
		}
		else {
			for (QueryRunner rq :querys) {
				rq.rollback();
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