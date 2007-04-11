package com.kazak.smi.server.businesrules;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.kazak.smi.server.database.sql.RunQuery;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;

public class ConfirmMessage {

	private Iterator it;
	
	public ConfirmMessage(SocketChannel sock, Element args, Element packet, String id) {
		this.it = packet.getChildren("package").iterator();
		Iterator itArgs = args.getChildren("args").iterator();
		RunQuery runQuery = null;
		while(itArgs.hasNext()) {
			Element element = (Element) itArgs.next();
			String sqlCode = element.getValue();
			
			Element elm = (Element)it.next();
			List list = elm.getChildren();
			String[] sqlArgs = new String[5];
			Iterator it = list.iterator();

			sqlArgs[0] = ((Element) it.next()).getValue();
			sqlArgs[1] = new Date().toString();
			sqlArgs[2] = ((Element) it.next()).getValue();
			sqlArgs[3] = ((Element) it.next()).getValue();
			sqlArgs[4] = ((Element) it.next()).getValue();
			
			try {
				runQuery = new RunQuery(sqlCode,sqlArgs);
				runQuery.setAutoCommit(false);
				runQuery.ejecutarSQL();
				runQuery.commit();
				LogWriter.write(
						"Confirmada lectura del mensaje con destino: " + 
						((Element)list.get(3)).getValue() + ", con el asunto :" + 
						((Element)list.get(4)).getValue() + ", remitido por: " + 
						((Element)list.get(5)).getValue());
			} catch (SQLException e) {
				runQuery.rollback();
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
			} catch (SQLNotFoundException e) {
				e.printStackTrace();
				RunTransaction.errorMessage(
						 sock,
						 id,
						 "La sentencia  " + sqlCode + " no existe");
			} catch (SQLBadArgumentsException e) {
				e.printStackTrace();
				RunTransaction.errorMessage(
						 sock,
						 id,
						 "Argumentos invalidos " +
						 "para la sentencia : " + sqlCode);
			}
		}
	}
}
