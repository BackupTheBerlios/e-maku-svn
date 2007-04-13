package com.kazak.smi.server.businessrules;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.database.sql.CloseSQL;
import com.kazak.smi.server.database.sql.RunQuery;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;

public class UserManager {

	private Iterator itArgs;
	private ArrayList<RunQuery> querys;
	private String[] arrUserInfo;
	private Vector<String[]> arrWs = new Vector<String[]>();
	//private String oldLogin;

	public UserManager(SocketChannel sock, Element args, Element packet, String id) {
		this.itArgs = args.getChildren("arg").iterator();
		String type = args.getChildText("action");
		boolean ret = false;
		String message = "";
		querys = new ArrayList<RunQuery>();
		try {
			if ("add".equals(type)) {
				ret = addUser(packet);
			}
			else if ("edit".equals(type)) {
				ret = editUser(packet);
			}
			else if ("remove".equals(type)) {
				ret = removeUser(packet);
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
			
			/*if ("remove".equals(type)) {
				UsersCache.removeFromUID(oldLogin);
			}
			else if ("edit".equals(type)) {
				InfoUser ifu = new InfoUser();
				ifu.setFrom(arrUserInfo);
				ifu.addPointSale(arrWs);
				UsersCache.addUser(ifu);
			}
			else if ("add".equals(type)) {
				UsersCache.load();
			}*/
			
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
			RunTransaction.
			errorMessage
			(sock,id,"No se pudo insertar el usuario causa: " + message);
		}
	}
	
	private boolean addUser(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		Iterator it = transaction.getChildren("package").iterator();
		while(it.hasNext()) {
			Element e = (Element)it.next();
			List lspacks = e.getChildren("subpackage");
			int spacks = lspacks!=null ? lspacks.size() : 0;
			if ((e.getChildren().size() - spacks) > 0 ||
				(spacks>0)) {
				if (spacks>0) {
					Iterator itspacks = lspacks.iterator();
					String sqlCode = ((Element)itArgs.next()).getText();
					while (itspacks.hasNext()) {
						Element sp = (Element)itspacks.next();
						String[] sqlArgs = packArgs(sp);
						arrWs.add(sqlArgs);
						RunQuery rq = new RunQuery(sqlCode,sqlArgs);
						querys.add(rq);
						rq.setAutoCommit(false);
						rq.runSQL();
					}
				}
				else {
					arrUserInfo = packArgs(e);
					String sqlCode = ((Element)itArgs.next()).getText();
					RunQuery rq = new RunQuery(sqlCode,arrUserInfo);
					querys.add(rq);
					rq.setAutoCommit(false);
					rq.runSQL();
				}
			}
		}
		return true;
	}
	
	private boolean editUser(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		Iterator it = transaction.getChildren("package").iterator();
		
		Element e = (Element)it.next();
		arrUserInfo = packArgs(e);
		String sqlCode = ((Element)itArgs.next()).getText();
		RunQuery rq = new RunQuery(sqlCode,arrUserInfo);
		querys.add(rq);
		rq.setAutoCommit(false);
		rq.runSQL();
			
		e = (Element)it.next();
		String[] args = packArgs(e);
		sqlCode = ((Element)itArgs.next()).getText();
		rq = new RunQuery(sqlCode,args);
		querys.add(rq);
		rq.setAutoCommit(false);
		rq.runSQL();
		
		e = (Element)it.next();
		List lspacks = e.getChildren("subpackage");
		int spacks = lspacks!=null ? lspacks.size() : 0;
		if (spacks>0) {
			Iterator itspacks = lspacks.iterator();
			sqlCode = ((Element)itArgs.next()).getText();
			while (itspacks.hasNext()) {
				Element sp = (Element)itspacks.next();
				String[] sqlArgs = packArgs(sp);
				arrWs.add(sqlArgs);
				rq = new RunQuery(sqlCode,sqlArgs);
				querys.add(rq);
				rq.setAutoCommit(false);
				rq.runSQL();
			}
		}
		return true;
	}
	
	private boolean removeUser(Element transaction) throws 
	SQLNotFoundException, SQLBadArgumentsException, SQLException {
		Iterator it = transaction.getChildren("package").iterator();
		
		Element e = (Element)it.next();
		String[] args = packArgs(e);
		String sqlCode = ((Element)itArgs.next()).getText();
		RunQuery rq = new RunQuery(sqlCode,args);
		querys.add(rq);
		rq.setAutoCommit(false);
		rq.runSQL();
		//oldLogin = args[0];
		
		e = (Element)it.next();
		args = packArgs(e);
		sqlCode = ((Element)itArgs.next()).getText();
		rq = new RunQuery(sqlCode,args);
		querys.add(rq);
		rq.setAutoCommit(false);
		rq.runSQL();
		
		return true;
	}
	
	public String[] packArgs(Element pack) {
		List list = pack.getChildren("field");
		Iterator it = list.iterator();
		String[] ret = new String[list.size()];
		int index = 0;
		while(it.hasNext()) {
			Element e = (Element) it.next();
			Attribute at = e.getAttribute("arg");
			ret[index] = e.getValue();
			if (at!=null && at.getValue().equals("edit")) {
				ret[index] = oldPassword(ret[index-1]);
			}
			index++;
		}
		return ret;
	}
	
	private String oldPassword(String login){
		RunQuery runQuery = null;
		ResultSet rs = null;
		try {
			runQuery = new RunQuery("SEL0029",new String[]{login});
			rs = runQuery.runSELECT();
		    if (rs.next()) {
		    	return rs.getString(1);
		    }
		} catch (SQLNotFoundException e) {
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			CloseSQL.close(rs);
			runQuery.closeStatement();
		}
		return null;
	}
}