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
import com.kazak.smi.server.comunications.SocketServer.SocketInfo;
import com.kazak.smi.server.database.sql.QueryClosingHandler;
import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFileHandler;

// Esta clase se encarga de distribuir un mensaje a sus destinos

public class MessageDistributor {
	
	private SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat formatHour = new SimpleDateFormat("hh:mm aaa");
	private Date date;
	private String groupIDString;
	private int    groupID;
	private String from;
	private String dateStr;
	private String hourStr;
	private String subject;
	private String body;
	
	private int timeAlife = -1;
	private boolean control = false;
	
	public MessageDistributor(Element element, boolean pop) {
		
		groupIDString  = element.getChildText("idgroup");
		groupID  = Integer.parseInt(groupIDString);
		date    = Calendar.getInstance().getTime();
		from    = element.getChildText("from");
		dateStr = formatDate.format(date);
		hourStr = formatHour.format(date);
		subject = element.getChildText("subject");
		body    = element.getChildText("message");
		Element elmTimeAlife = element.getChild("timeAlife");
		timeAlife = elmTimeAlife!=null ? Integer.parseInt(elmTimeAlife.getValue()) : 0;

		if (timeAlife > 0) {
			control = true;
			body+="\n====================================\n" +
				  "Este mensaje es de control\n";
		}
		
		int nroUsers = 0;
		SocketInfo sender = null;
		
		if (!pop) {	
			// Consultando usuarios de puntos de venta
			sender = SocketServer.getSocketInfo(from);
		}
		else {
			// Consultando usuarios de correo pop
			QueryRunner runQuery = null;
		    ResultSet rs = null;
			try {
				runQuery = new QueryRunner("SEL0026",new String[]{from});
				rs = runQuery.runSELECT();
				if (rs.next()) {
					sender = SocketServer.getInstaceOfSocketInfo();
					sender.setUid(rs.getInt(1));
					sender.setLogin(rs.getString(2));
					sender.setNames(rs.getString(3));
					sender.setEmail(rs.getString(4));
					sender.setAdmin(rs.getBoolean(5));
					sender.setAudit(rs.getBoolean(6));
					sender.setGid(rs.getInt(7));
					sender.setPsName(rs.getString(9));
					sender.setGName(rs.getString(12));	
				}
			} catch (SQLNotFoundException e) {
				e.printStackTrace();
			} catch (SQLBadArgumentsException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				QueryClosingHandler.close(rs);
				runQuery.closeStatement();
			}
		}
		
		Vector<SocketInfo> vusers;
		if (!pop) {
			vusers = SocketServer.getAllClients(groupID);
		}
		else {
			String toName = element.getChildText("toName");
			vusers = SocketServer.getAllClients(toName);
		}
			
		if (sender==null) {
			LogWriter.write("ERROR: Acceso denegado al usuario " + from + " para el envio de mensajes a traves de correo");
			return;
		}
		nroUsers = vusers.size();
		LogWriter.write("INFO: Enviando mensaje a "+ nroUsers + " usuarios.");

		// Aqui se define a quien se envia el mensage
		// Los Mensajes deben enviarse a los grupos.

		for (SocketInfo destination : vusers) {
			SocketChannel sock = destination.getSock()!=null ? destination.getSock().getChannel() : null;
			
			// Si el usuario esta en linea
			if (sock!=null) {
				Element Message = new Element("Message");
				Element root = new Element("root");
				
				Message.addContent(root);
				root.addContent(createCol(dateStr));
				root.addContent(createCol(hourStr));
				root.addContent(createCol(sender.getGName()));
				root.addContent(createCol(subject));
				root.addContent(createCol(body));
				root.addContent(createCol("f"));
				
				Document doc = new Document((Element) Message.clone());
				// Enviando mensaje a colocadores en linea
				try {
					SocketWriter.writing(sock,doc);
					LogWriter.write("INFO: [Envio a Punto de Venta] Remitente -> " + sender.getLogin() 
							        + " / Destino: " + destination.getLogin() 
							        + " / Asunto: " + subject);
				} catch (ClosedChannelException e) {
					LogWriter.write("INFO: El colocador " + destination.getLogin() + " no se encuentra en linea.");
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Enviando mensaje a usuarios del servidor de correo (personal administrativo)
			if (!pop) {
				EmailSender ems = new EmailSender();
				ems.setFrom	   (Pop3Handler.getUser()+"@"+Pop3Handler.getHost());
				ems.setTo	   (destination.getEmail());
				ems.setSubject (sender.getLogin() + "," +subject);
				ems.setDate	   (date);
				ems.setMessage (body);
				ems.setToFullName(sender.getNames());
				ems.setColocationPoint(sender.getPsName());
				ems.send();
				LogWriter.write("INFO: [Envio a Cuenta de Correo] Remitente -> " + sender.getLogin() 
				        + " / Destino: " + destination.getLogin() 
				        + " / Asunto: " + subject);
			}
			
			String isvalid   = nroUsers > 0 ? "true" : "false";
			String[] argsSql = 
			{String.valueOf(destination.getUid()),String.valueOf(sender.getUid()),
			dateStr,hourStr,subject.trim(),body.trim(),isvalid,
			String.valueOf(ConfigFileHandler.getMessageLifeTimeForClients()),
			String.valueOf(control),String.valueOf(timeAlife)};
			
			QueryRunner runQuery = null;
			try {
				LogWriter.write("INFO: Almacenando registro de mensaje en la base de datos...");
				runQuery = new QueryRunner("INS0003",argsSql);
				runQuery.setAutoCommit(false);
				runQuery.runSQL();
				runQuery.commit();
				runQuery.closeStatement();
			} catch (SQLNotFoundException e) {
				LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Instruccion SQL no encontrada");
				runQuery.rollback();
				e.printStackTrace();
			} catch (SQLBadArgumentsException e) {
				LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Argumentos Invalidos");
				e.printStackTrace();
			} catch (SQLException e) {
				LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Excepcion SQL");
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