package com.kazak.smi.server.control;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.server.comunications.SocketServer;
import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.comunications.SocketServer.InfoSocket;
import com.kazak.smi.server.database.sql.CloseSQL;
import com.kazak.smi.server.database.sql.RunQuery;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFile;

public class MessageDistributor {
	
	private SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat formatHour = new SimpleDateFormat("hh:mm aaa");
	private Date date;
	private String gidStr;
	private int    gidInt;
	private String from;
	private String dateStr;
	private String hourStr;
	private String subject;
	private String body;
	private Element Message = new Element("Message");
	private Element root = new Element("root");
	private int timeAlife = -1;
	private boolean control = false;
	
	public MessageDistributor(Element elm, boolean pop) {
		
		gidStr  = elm.getChildText("idgroup");
		gidInt  = Integer.parseInt(gidStr);
		date    = Calendar.getInstance().getTime();
		from    = elm.getChildText("from");
		dateStr = formatDate.format(date);
		hourStr = formatHour.format(date);
		subject = elm.getChildText("subject");
		body    = elm.getChildText("message");
		Element elmTimeAlife = elm.getChild("timeAlife");
		timeAlife = elmTimeAlife!=null ? Integer.parseInt(elmTimeAlife.getValue()) : 0;
		if (timeAlife > 0) {
			control = true;
			body+="\n====================================\n" +
				  "Este mensaje es de control\n";
		}
		Message.addContent(root);
		root.addContent(createCol(dateStr));
		root.addContent(createCol(hourStr));
		root.addContent(createCol(from   ));
		root.addContent(createCol(subject));
		root.addContent(createCol(body   ));
		root.addContent(createCol("f"));
		int nroUsers = 0;
		InfoSocket ifuFrom = null;
		if (!pop) {
			ifuFrom = SocketServer.getInfoSocket(from);	
		}
		else {
			RunQuery runQuery = null;
		    ResultSet rs = null;
			try {
				runQuery = new RunQuery("SEL0026",new String[]{from});
				rs = runQuery.runSELECT();
				if (rs.next()) {
					ifuFrom = SocketServer.getInstaceOfInfoSocket();
					ifuFrom.setUid(rs.getInt(1));
					ifuFrom.setLogin(rs.getString(2));
					ifuFrom.setNames(rs.getString(3));
					ifuFrom.setEmail(rs.getString(4));
					ifuFrom.setAdmin(rs.getBoolean(5));
					ifuFrom.setAudit(rs.getBoolean(6));
					ifuFrom.setGid(rs.getInt(7));
					ifuFrom.setPsName(rs.getString(9));
					ifuFrom.setGName(rs.getString(12));	
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
		}
		
		Vector<InfoSocket> vusers;
		if (!pop) {
			vusers = SocketServer.getAllClients(gidInt);
		}
		else {
			String toName = elm.getChildText("toName");
			vusers = SocketServer.getAllClients(toName);
		}
			
		if (ifuFrom==null) {
			LogWriter.write(
					"Acceso denegado al usuario " +from+ " para el envio " +
					"de mensajes atravez de correo");
			return;
		}
		nroUsers = vusers.size();
		LogWriter.write("Enviando mensaje a "+ nroUsers + " usuarios");
		for (InfoSocket ifu : vusers) {
			// Aqui se define a quien se envia el mensage
			// Los Mensajes deben enviarse a los grupos.
			SocketChannel sock = ifu.getSock()!=null ? ifu.getSock().getChannel() : null;
			if (sock!=null) {
				Document doc = new Document((Element) Message.clone());
				try {
					SocketWriter.writing(sock,doc);
					LogWriter.write("Mensaje enviado a "+ ifu.getLogin() + " de " + ifuFrom.getLogin() + " asunto " + subject);					
				} catch (ClosedChannelException e) {
					LogWriter.write("El colocador "+ifu.getLogin()+" no se encuentra en linea");
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				LogWriter.write("El usuario "+ifu.getLogin()+" no se encuentra en linea");
			}
			if (!pop) {
				EmailSender ems = new EmailSender();
				ems.setFrom	   (Pop3Handler.getUser()+"@"+Pop3Handler.getHost());
				ems.setTo	   (ifu.getEmail());
				ems.setSubject (ifuFrom.getLogin()+","+subject);
				ems.setDate	   (date);
				ems.setMessage (body);
				ems.setToNameFull(ifuFrom.getNames());
				ems.setColocationPoint(ifuFrom.getPsName());
				ems.send();
			}
			
			String isvalid   = nroUsers > 0 ? "true" : "false";
			String[] argsSql = 
			{String.valueOf(ifu.getUid()),String.valueOf(ifuFrom.getUid()),
			dateStr,hourStr,subject.trim(),body.trim(),isvalid,
			String.valueOf(ConfigFile.getTimeAlifeMessageForClient()),
			String.valueOf(control),String.valueOf(timeAlife)};
			
			RunQuery runQuery = null;
			try {
				runQuery = new RunQuery("INS0003",argsSql);
				runQuery.setAutoCommit(false);
				runQuery.runSQL();
				runQuery.commit();
				runQuery.closeStatement();
				LogWriter.write("Mensaje almacenado en la base de datos");
			} catch (SQLNotFoundException e) {
				runQuery.rollback();
				e.printStackTrace();
			} catch (SQLBadArgumentsException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private Element createCol(String val) {
		Element e = new Element("col");
		e.setText(val);
		return e;
	}
}