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
import com.kazak.smi.server.misc.ServerConstants;

// This class delivers all the messages received by the smi account

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
		from           = (element.getChildText("from")).trim();
		dateString     = formatDate.format(date);
		hourString     = formatHour.format(date);
		subject        = element.getChildText("subject");
		body           = element.getChildText("message");
		Element mailLifeTime = element.getChild("timeAlife");
		lifeTime = mailLifeTime != null ? Integer.parseInt(mailLifeTime.getValue()) : 0;

		// This is a message control
		if (lifeTime > 0) {
			control = true;
			body += "\n====================================\n" +
				  "Este mensaje es de control\n";
		}
		
		int groupSize = 0;
		SocketInfo sender = null;
		
		if (!senderIsMailUser) {	
			// Querying the info of the sender (a pos user)
			sender = SocketServer.getSocketInfo(from);
			// If sender doesn't exist, this operation is aborted
			if (sender==null) {
				String msg = "ERROR: El Colocador {" + from + "} no esta registrado en el sistema.";
				LogWriter.write(msg);
				sendAlarm("Error procesando mensaje de colocador",msg);
				return;
			}
		}
		else {
			// Querying users from the pop server (admin user)
			QueryRunner qRunner = null;
		    ResultSet resultSet = null;
		    int i=0;
			try {
				qRunner = new QueryRunner("SEL0026",new String[]{from});
				resultSet = qRunner.select();
				if (resultSet.next()) {
					i++;
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
				LogWriter.write("ERROR: Falla mientras se consultaba la informacion del usuario: " + from);
				e.printStackTrace();
			} finally {
				QueryClosingHandler.close(resultSet);
				qRunner.closeStatement();
			}

			if (i==0) {
				sender = insertPopUser(from);
				if (sender == null) {
					return;
				}
			}
		}
		
		// Getting the destination list for this message 
		Vector<SocketInfo> usersVector;
		if (!senderIsMailUser) {
			usersVector = SocketServer.getAllClients(groupID);
		}
		else {
			String groupName = element.getChildText("toName");
			usersVector = SocketServer.getAllClients(groupName);
		}
				
		groupSize = usersVector.size();
		
		String many = "";
		if (groupSize > 1) {
		    many = "s";
		}
			
		LogWriter.write("INFO: Enviando mensaje a "+ groupSize + " usuario" + many);

		// In this cycle, the message is sent to every user in the destination list 

		for (SocketInfo destination : usersVector) {
			SocketChannel sock = destination.getSock()!=null ? destination.getSock().getChannel() : null;
			
			// If the POS user is online
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
				// Sending message to POS user online
				try {
					SocketWriter.write(sock,doc);
					LogWriter.write("INFO: [Envio a Punto de Venta] Remitente {" + sender.getLogin() 
							        + "} - Destino: " + destination.getLogin() 
							        + " - Asunto: " + subject);
				} catch (ClosedChannelException e) {
					LogWriter.write("INFO: El colocador " + destination.getLogin() + " no se encuentra en linea.");
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Sending message to pop users (admin users)
			if (!senderIsMailUser) {
				EmailSender mail = new EmailSender();
				mail.setFrom(Pop3Handler.getUser()+"@"+Pop3Handler.getHost());
				mail.setSender(destination.getEmail());
				mail.setSubject(sender.getLogin() + "," + subject);
				mail.setDate(date);
				mail.setMessage(body);
				mail.setSenderFullName(sender.getNames());
				mail.setWorkStation(sender.getWsName() + " [" + sender.getCurrentIp() + "]");
				mail.send();
				LogWriter.write("INFO: [Envio a Cuenta de Correo] Remitente {" + sender.getLogin() 
				        + "} - Destino: " + destination.getLogin() 
				        + " - Asunto: " + subject);
			}
			
			if (!control || (control && (sock!=null))) {
				String isValid = groupSize > 0 ? "true" : "false";
				String[] argsArray = {String.valueOf(destination.getUid()),String.valueOf(sender.getUid()),
						dateString,hourString,subject.trim(),body.trim(),isValid,
						String.valueOf(ConfigFileHandler.getMessageLifeTimeForClients()),
						String.valueOf(control),String.valueOf(lifeTime)};

				QueryRunner qRunner = null;
				try {
					LogWriter.write("INFO: Almacenando registro de mensaje en la base de datos [" + destination.getLogin() + "]");
					qRunner = new QueryRunner("INS0003",argsArray);
					qRunner.setAutoCommit(false);
					qRunner.executeSQL();
					qRunner.commit();
				} catch (SQLNotFoundException e) {
					qRunner.rollback();
					LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Instruccion SQL no encontrada");
					e.printStackTrace();
				} catch (SQLBadArgumentsException e) {
					qRunner.rollback();
					LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Argumentos Invalidos");
					e.printStackTrace();
				} catch (SQLException e) {
					qRunner.rollback();
					LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Excepcion SQL");
					e.printStackTrace();
				} finally {
					qRunner.closeStatement();
					qRunner.setAutoCommit(true);
				}
			}
		}	
	}
	
	// This method sends an alarm message to the SMI group when the system fails
	public static void sendAlarm(String subject, String body) {
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
			mail.setSenderFullName("Sistema SMI");
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
			} catch (SQLNotFoundException e) {
				qRunner.rollback();
				LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Instruccion SQL no encontrada");
				e.printStackTrace();
			} catch (SQLBadArgumentsException e) {
				qRunner.rollback();
				LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Argumentos Invalidos");
				e.printStackTrace();
			} catch (SQLException e) {
				qRunner.rollback();
				LogWriter.write("ERROR: No se pudo almacenar mensaje en la base de datos. Excepcion SQL");
				e.printStackTrace();
			} finally {
				qRunner.closeStatement();	
				qRunner.setAutoCommit(true);
			}
		}
	}
	
	// This method insert a pop user into the SMI system
	private SocketInfo insertPopUser(String login) {
		SocketInfo socketInfo = null;
		String line = "";
		String names = "";
		boolean userExists = false;

		try {
			BufferedReader in = new BufferedReader(new FileReader("/etc/passwd"));

			while ((line = in.readLine()) != null)   {
				if(line.startsWith(login)) {
					userExists = true;
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

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// If the user doesn't exist in the file /etc/passwd, do not add him to the SMI server
		if(!userExists){
			LogWriter.write("ERROR: El usuario {" + login + "} no existe en el servidor de correo");
			return null;
		}
						
		String[] userInfoArray = {login,
				"d41d8cd98f00b204e9800998ecf8427e",
				names,
				login+"@"+Pop3Handler.getHost(),
				"true",
				"false",
		ServerConstants.ADMINGROUP + ""};

		QueryRunner qRunner = null;
		try {
			qRunner = new QueryRunner("INS0001",userInfoArray);
			qRunner.setAutoCommit(false);
			qRunner.executeSQL();
			qRunner.commit();
			LogWriter.write("INFO: Usuario {" + login + "} adicionado satisfactoriamente al sistema");
		} catch(SQLNotFoundException SNFE) {
			qRunner.rollback();
			SNFE.printStackTrace();
		} catch(SQLBadArgumentsException SBAE) {
			qRunner.rollback();
			SBAE.printStackTrace();
		} catch(SQLException SQLE){
			qRunner.rollback();
			String msg = "ERROR: Falla mientras se ingresaba un nuevo usuario de correo\n" +
				"que no estaba registrado en el sistema.\nLogin: " + login;
			LogWriter.write(msg);
			sendAlarm("Error adicionando usuario",msg);
			SQLE.printStackTrace();
			return null;
		} finally {
			qRunner.closeStatement();
			qRunner.setAutoCommit(true);
		}
		
		ResultSet resultSet = null;
		int uid = -1;
		String groupName = "";
		try {
			qRunner = new QueryRunner("SEL0008",new String[]{login});
			resultSet = qRunner.select();
			int i=0;
			if (resultSet.next()) {
				i++;
				uid = resultSet.getInt(1);
				groupName = resultSet.getString(2);
			}
		} catch (SQLNotFoundException e) {
			e.printStackTrace();
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			LogWriter.write("ERROR: Falla mientras se consultaba la informacion del usuario: " + login);
			e.printStackTrace();
			return null;
		} finally {
			QueryClosingHandler.close(resultSet);
			qRunner.closeStatement();
		}
	
		socketInfo = SocketServer.getInstaceOfSocketInfo();
		socketInfo.setUid(uid);
		socketInfo.setLogin(userInfoArray[0]);
		socketInfo.setNames(userInfoArray[2]);
		socketInfo.setEmail(userInfoArray[3]);
		socketInfo.setAdmin(new Boolean(userInfoArray[4]).booleanValue());
		socketInfo.setAudit(new Boolean(userInfoArray[5]).booleanValue());
		socketInfo.setGroupID(Integer.parseInt(userInfoArray[6]));
		socketInfo.setWsName("");
		socketInfo.setGroupName(groupName);	
		
		return socketInfo;
	}
	
	private Element addColumn(String value) {
		Element element = new Element("col");
		element.setText(value);
		return element;
	}
}
