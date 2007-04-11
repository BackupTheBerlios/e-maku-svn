package com.kazak.smi.server.businesrules;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.database.sql.RunQuery;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;

public class GroupManager {

	private Iterator itArgs;
	private ArrayList<RunQuery> querys;
	
	public GroupManager(SocketChannel sock, Element args, Element packet, String id) {
		this.itArgs = args.getChildren("arg").iterator();
		String type = args.getChildText("action");
		boolean ret = false;
		String message = "";
		querys = new ArrayList<RunQuery>();
		try {
			if ("add".equals(type)) {
				ret = addGroup(packet);
			}
			else if ("edit".equals(type)) {
				ret = addGroup(packet);
			}
			else if ("remove".equals(type)) {
				ret = removeGroup(packet);
			}
		} catch (SQLNotFoundException e) {
			ret = false;
			message = e.getMessage();
		} catch (SQLBadArgumentsException e) {
			ret = false;
			message = e.getMessage();
		} catch (SQLException e) {
			ret = false;
			message = e.getMessage();
		}
		if (ret) {
			for (RunQuery rq :querys) {
				rq.commit();
			}
			Element reload = new Element("RELOADTREE");
			try {
				SocketWriter.writing(sock,new Document(reload));
			} catch (IOException e) {
				e.printStackTrace();
			}
			message = "Los datos fueron almacenados satisfactoriamente";
			RunTransaction.successMessage(sock,id,message);
		}
		else {
			for (RunQuery rq :querys) {
				rq.rollback();
			}
			if ("remove".equals(type)) {
				message =
					"El grupo debe estar vacio\n" +
					"para poder ser eliminado\n";
			}
			RunTransaction.
			errorMessage
			(sock,id,"No se pudo procesar la transacción, causa:\n" + message);
		}
	}
	
	private boolean addGroup(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		Iterator it = transaction.getChildren("package").iterator();
		while(it.hasNext()) {
			Element e = (Element)it.next();
			if (e.getChildren().size() > 0 ) {
				String sqlCode = ((Element)itArgs.next()).getText();
				RunQuery rq = new RunQuery(sqlCode,packArgs(e));
				querys.add(rq);
				rq.setAutoCommit(false);
				rq.ejecutarSQL();
			}
		}
		return true;
	}
	
	
	private boolean removeGroup(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		Iterator it = transaction.getChildren("package").iterator();
		
		Element e = (Element)it.next();
		String[] args = packArgs(e);
		String sqlCode = ((Element)itArgs.next()).getText();
		RunQuery rq = new RunQuery(sqlCode,args);
		querys.add(rq);
		ResultSet rs = rq.ejecutarSELECT();
		rs.next();
		int count = rs.getInt("count");
		rs.close();
		rq.closeStatement();
		if (count>0) {
			return false;
		}
		sqlCode = ((Element)itArgs.next()).getText();
		rq = new RunQuery(sqlCode,args);
		querys.add(rq);
		rs = rq.ejecutarSELECT();
		rs.next();
		count = rs.getInt("count");
		rs.close();
		rq.closeStatement();
		if (count>0) {
			return false;
		}
		
		sqlCode = ((Element)itArgs.next()).getText();
		rq = new RunQuery(sqlCode,packArgs(e));
		querys.add(rq);
		rq.setAutoCommit(false);
		rq.ejecutarSQL();
		
		return true;
	}
	
	public String[] packArgs(Element pack) {
		List list = pack.getChildren("field");
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