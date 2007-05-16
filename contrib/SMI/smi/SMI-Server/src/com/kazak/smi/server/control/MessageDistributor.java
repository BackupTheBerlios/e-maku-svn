package com.kazak.smi.server.control;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	
	private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat formatHour = new SimpleDateFormat("hh:mm aaa");
	private Date   date;
	private String groupIDString;
	private int    groupID;
	private String from;
	private String dateString;
	private String hourString;
	private String subject;
	private String body;
	
	private int lifeTime = -1;
	private boolean control = false;
	
	public MessageDistributor(Element element, boolean senderIsMailUser) {
		
		groupIDString  = element.getChildText("idgroup");
		groupID        = Integer.parseInt(groupIDString);
		date           = Calendar.getInstance().getTime();
		from           = element.getChildText("from");
		dateString     = formatDate.format(date);
		hourString     = formatHour.format(date);
		subject        = element.getChildText("subject");
		body           = element.getChildText("message");
		Element mailLifeTime = element.getChild("timeAlife");
		lifeTime = mailLifeTime != null ? Integer.parseInt(mailLifeTime.getValue()) : 0;

		if (lifeTime > 0) {
			control = true;
			body += "\n====================================\n" +
				  "Este mensaje es de control\n";
		}
		
		int groupSize = 0;
		SocketInfo sender = null;
		
		if (!senderIsMailUser) {	
			// Consultando usuarios de puntos de venta
			sender = SocketServer.getSocketInfo(from);
		}
		else {
			// Consultando usuarios de correo pop
			QueryRunner qRunner = null;
		    ResultSet resultSet = null;
			try {
				qRunner = new QueryRunner("SEL0026",new String[]{from});
				resultSet = qRunner.select();
				if (resultSet.next()) {
					sender = SocketServer.getInstaceOfSocketInfo();
					sender.setUid(resultSet.getInt(1));
					sender.setLogin(resultSet.getString(2));
					sender.setNames(resultSet.getString(3));
					sender.setEmail(resultSet.getString(4));
					sender.setAdmin(resultSet.getBoolean(5));
					sender.setAudit(resultSet.getBoolean(6));
					sender.setGroupID(resultSet.getInt(7));
					sender.setWsName(resultSet.getString(9));
					sender.setGroupName(resultSet.getString(12));	
				}
			} catch (SQLNotFoundException e) {
				e.printStackTrace();
			} catch (SQLBadArgumentsException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				QueryClosingHandler.close(resultSet);
				qRunner.closeStatement();
			}
		}

		// Si el usuario pertenece al servidor POP y no existe en el sistema, es ingresado
		if (sender==null) {
			LogWriter.write("ADVERTENCIA: El usuario {" + from + "} no esta registrado en el sistema. Adicionando usuario...");
			insertPopUser(from);
			return;
		}
		
		// Capturando lista de usuarios destino del mensaje 
		Vector<SocketInfo> usersVector;
		if (!senderIsMailUser) {
			usersVector = SocketServer.getAllClients(groupID);
		}
		else {
			String groupName = element.getChildText("toName");
			usersVector = SocketServer.getAllClients(groupName);
		}
				
		groupSize = usersVector.size();
		LogWriter.write("INFO: Enviando mensaje a "+ groupSize + " usuarios");

		// Aqui se define a quien se envia el mensage
		// Los Mensajes deben enviarse a los grupos.

		for (SocketInfo destination : usersVector) {
			SocketChannel sock = destination.getSock()!=null ? destination.getSock().getChannel() : null;
			
			// Si el usuario esta en linea
			if (sock!=null) {
				Element message = new Element("Message");
				Element root = new Element("root");
				
				message.addContent(root);
				root.addContent(addColumn(dateString));
				root.addContent(addColumn(hourString));
				root.addContent(addColumn(sender.getGroupName()));
				root.addContent(addColumn(subject));
				root.addContent(addColumn(body));
				root.addContent(addColumn("f"));
				
				Document doc = new Document((Element) message.clone());
				// Enviando mensaje a colocadores en linea
				try {
					SocketWriter.write(sock,doc);
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
			if (!senderIsMailUser) {
				EmailSender mail = new EmailSender();
				mail.setFrom(Pop3Handler.getUser()+"@"+Pop3Handler.getHost());
				mail.setSender(destination.getEmail());
				mail.setSubject(sender.getLogin() + "," + subject);
				mail.setDate(date);
				mail.setMessage(body);
				mail.setToFullName(sender.getNames());
				mail.setWorkStation(sender.getWsName());
				mail.send();
				LogWriter.write("INFO: [Envio a Cuenta de Correo] Remitente -> " + sender.getLogin() 
				        + " / Destino: " + destination.getLogin() 
				        + " / Asunto: " + subject);
			}
			
			String isValid = groupSize > 0 ? "true" : "false";
			String[] argsArray = {String.valueOf(destination.getUid()),String.valueOf(sender.getUid()),
									dateString,hourString,subject.trim(),body.trim(),isValid,
			String.valueOf(ConfigFileHandler.getMessageLifeTimeForClients()),
			String.valueOf(control),String.valueOf(lifeTime)};
			
			QueryRunner qRunner = null;
			try {
				LogWriter.write("INFO: Almacenando registro de mensaje en la base de datos...");
				qRunner = new QueryRunner("INS0003",argsArray);
				qRunner.setAutoCommit(false);
				qRunner.executeSQL();
				qRunner.commit();
				qRunner.closeStatement();
			} catch (SQLNotFoundException e) {
				LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Instruccion SQL no encontrada");
				qRunner.rollback();
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
	
	// This method sends an alarm message when the system fails
	public static void alarmSender(String subject, String body) {
		Vector<SocketInfo> usersVector = SocketServer.getAllClients("SMI");
		int groupSize = usersVector.size();
		Date date = Calendar.getInstance().getTime();
		String dateString = formatDate.format(date);
		String hourString = formatHour.format(date);
		
		for (SocketInfo destination : usersVector) {
			EmailSender mail = new EmailSender();
			mail.setFrom(Pop3Handler.getUser()+"@"+Pop3Handler.getHost());
			mail.setSender(destination.getEmail());
			mail.setSubject("[Error SMI]: " + subject);
			mail.setDate(date);
			mail.setMessage(body);
			mail.setToFullName("Sistema SMI");
			mail.setWorkStation("Servidor");
			mail.send();
			LogWriter.write("INFO: Enviando notificacion de alarma a {" + destination.getEmail() + "} / Asunto: [" + subject + "]");

			String isValid = groupSize > 0 ? "true" : "false";
			String[] argsArray = {String.valueOf(destination.getUid()),String.valueOf(0),
					dateString,hourString,subject.trim(),body.trim(),isValid,
					String.valueOf(ConfigFileHandler.getMessageLifeTimeForClients()),
					"false","-1"};

			QueryRunner qRunner = null;
			try {
				LogWriter.write("INFO: Almacenando registro de alarma en la base de datos...");
				qRunner = new QueryRunner("INS0003",argsArray);
				qRunner.setAutoCommit(false);
				qRunner.executeSQL();
				qRunner.commit();
				qRunner.closeStatement();
			} catch (SQLNotFoundException e) {
				LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Instruccion SQL no encontrada");
				qRunner.rollback();
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
	
	// This method insert a pop user into the SMI system
	private void insertPopUser(String login) {		
		try {
			String line="";
			String names = "";
			BufferedReader in = new BufferedReader(new FileReader("/etc/passwd"));
			while ((line = in.readLine()) != null)   {
			   if(line.startsWith(login)) {
				   String[] userData = line.split(":");
				   names = userData[4];
        		   if (names.length() == 0) {
        			   names = "Usuario de Correo";
        		   }
        		   int index = names.indexOf(',');
        		   if (index > 0) {
        			   names = names.substring(0,index);
                   }
                   break;
			   }
			}
			in.close();
			String[] userInfoArray = {login,
					"d41d8cd98f00b204e9800998ecf8427e",
					names,
					login+"@"+Pop3Handler.getHost(),
					"true",
					"false",
					"6"};
			try {
				QueryRunner qRunner = new QueryRunner("INS0001",userInfoArray);
				qRunner.setAutoCommit(false);
				qRunner.executeSQL();
				LogWriter.write("INFO: Usuario {" + login + "} adicionado satisfactoriamente al sistema");
			} catch(SQLNotFoundException SNFE) {
				SNFE.printStackTrace();
			} catch(SQLBadArgumentsException SBAE) {
				SBAE.printStackTrace();
			} catch(SQLException SQLE){
				SQLE.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private Element addColumn(String val) {
		Element element = new Element("col");
		element.setText(val);
		return element;
	}
}