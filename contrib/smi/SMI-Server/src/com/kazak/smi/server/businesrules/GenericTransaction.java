package com.kazak.smi.server.businesrules;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.kazak.smi.server.database.sql.RunQuery;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;

public class GenericTransaction {

	private Iterator it;
	private ArrayList<RunQuery> querys;
	public GenericTransaction(SocketChannel sock, Element args, Element packet, String id) {
		this.it = packet.getChildren("package").iterator();
		int count = args.getChildren("args").size();
		int passed = 0;
		Iterator itArgs = args.getChildren("args").iterator();
		querys = new ArrayList<RunQuery>();
		RunQuery runQuery = null;
		while(itArgs.hasNext()) {
			Element element = (Element) itArgs.next();
			String sqlCode = element.getValue();
			String[] sqlArgs = packArgs();
			try {
				runQuery = new RunQuery(sqlCode,sqlArgs);
				runQuery.setAutoCommit(false);
				querys.add(runQuery);
				runQuery.ejecutarSQL();
				passed ++;
			} catch (SQLException e) {
				e.printStackTrace();
				LogWriter.write("Codigo error: "+e.getErrorCode());
				if (runQuery!=null) {
					runQuery.rollback();
				}
				RunTransaction.errorMessage(
						 sock,
                    	 id,
                    	 "No se pudo procesar la operacion:\n" +
 						 "causa:\n"+e.getLocalizedMessage());
				break;
			} catch (SQLNotFoundException e) {
				e.printStackTrace();
				RunTransaction.errorMessage(
						 sock,
						 id,
						 "La sentencia  " + sqlCode + " no existe");
				break;
			} catch (SQLBadArgumentsException e) {
				e.printStackTrace();
				RunTransaction.errorMessage(
						 sock,
						 id,
						 "Argumentos invalidos " +
						 "para la sentencia : " + sqlCode);
				break;
			}
		}
		if (passed==count) {
			for (RunQuery rq :querys) {
				rq.commit();
			}
			
			RunTransaction.
			successMessage
			(sock,id,"Los datos fueron almacenados satisfactoriamente");
		}
		else {
			for (RunQuery rq :querys) {
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