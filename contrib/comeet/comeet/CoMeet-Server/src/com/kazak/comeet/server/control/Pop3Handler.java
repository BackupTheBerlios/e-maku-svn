package com.kazak.comeet.server.control;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import org.jdom.Element;

import com.kazak.comeet.server.Run;
import com.kazak.comeet.server.database.sql.QueryClosingHandler;
import com.kazak.comeet.server.database.sql.QueryRunner;
import com.kazak.comeet.server.database.sql.SQLBadArgumentsException;
import com.kazak.comeet.server.database.sql.SQLNotFoundException;
import com.kazak.comeet.server.misc.LogWriter;
import com.kazak.comeet.server.misc.settings.ConfigFileHandler;

public class Pop3Handler extends Thread {
	
	private static String user;
	private static String password;
	private static String host;
	
	public Pop3Handler() {
		LogWriter.write("INFO: Iniciando demonio pop3");
		LogWriter.write("INFO: Servidor de correo {" + ConfigFileHandler.getMailServer() + "}");
		Pop3Handler.host = ConfigFileHandler.getMailServer();
		Pop3Handler.user = ConfigFileHandler.getMailUser();
		Pop3Handler.password = ConfigFileHandler.getMailPasswd();
		start();
	}
	
	public void run() {
		Session session = Session.getInstance(new Properties());
		Store store = null;
		try {
			store = session.getStore("pop3");
		} catch (NoSuchProviderException e2) {
			e2.printStackTrace();
		}
		while(true) {
			try {
				store.connect(host,user,password);	
				Folder folder = store.getFolder("INBOX");
				folder.open(Folder.READ_WRITE);
				Message messages[] = folder.getMessages();
				
				for (Message message : messages) {
					InternetAddress address = (InternetAddress) message.getFrom()[0];
					String fullSubject =  message.getSubject();
					fullSubject = fullSubject!=null ? fullSubject.trim() : null;

					if(fullSubject.startsWith("Re:") || fullSubject.startsWith("RE:")) {
						fullSubject = fullSubject.substring(3,fullSubject.length()).trim();
					}
								
					int index1  = address.getAddress().indexOf('@');
					String from = address.getAddress().substring(0,index1);
					int index2  = fullSubject!=null ? fullSubject.indexOf(',') : -1;

					String to = "";
					String subject = "";
					String content = message.getContent().toString();
					LogWriter.write("INFO: Leyendo correo del buzon de mensajes");
					LogWriter.write("INFO: Nuevo mensaje desde {" + address.getAddress() + "} con asunto [ " +  fullSubject + " ]");

					if (index2==-1 && !fullSubject.startsWith("[Error CoMeet]")) {
						if (!"Mailer-Daemon".equals(from)){
							LogWriter.write("ERROR: Inconsistencia en el asunto del mensaje escrito por {" + address.getAddress() + "}");
							LogWriter.write("ERROR: Asunto escrito [" + fullSubject + "]");
							LogWriter.write("Contenido del mensaje:");
							LogWriter.write(content);
							EmailSender mail = new EmailSender();
							mail.setFrom(user+"@"+host);
							mail.setSender(address.getAddress());
							mail.setSubject("Error");
							mail.setDate(new Date());
							mail.setMessage (
									"El correo no tiene el formato apropiado.\n" +
									"Por favor verifique el asunto del mensaje\n" +
									"Contenido Original\n" +
									"-------------------------------------\n" +
									"Asunto:"   + fullSubject+"\n" +
									"Mensage:\n"+ content    +"\n" +
									"-------------------------------------\n" +
							"Este mensaje fue generado automaticamente por el Sistema de Mensajeria Instantanea." );
							mail.send();
						}
					}
					else {
						System.out.println("ERROR: Entre Aqui!!!");
						String[] strings = fullSubject.split(":");
						String lifeTime = "-1";
						if (strings.length > 0 ) {
							lifeTime = strings[strings.length-1];
							int lifeTimeInteger = -1;
							try {
								lifeTimeInteger= Integer.valueOf(lifeTime);
							} catch (NumberFormatException NFEe) {
								lifeTimeInteger = -1;
							}
							if (lifeTimeInteger < 1 || lifeTimeInteger>99) {
								lifeTime = "-1";
							}
						}
						
						if (index2 == -1) {
							to = "COMEET";
						} else {
							to = fullSubject.substring(0,index2).trim();
						}
						
						subject = fullSubject.substring(index2+1,fullSubject.length()).trim();
						
						QueryRunner qRunner = null;
					    ResultSet resultSet = null;
					    String groupID = null;
					    boolean toAllUsers = false;
					    
					    if ("TODOS".equals(to)) { 
					    	toAllUsers=true; 
					    }
					    
						try {
							qRunner = toAllUsers ?
									new QueryRunner("SEL0028") :
									new QueryRunner("SEL0024",new String[]{to,to});
							resultSet = qRunner.select();
							while (resultSet.next()) {
								groupID =  resultSet.getString(1);
								if (groupID!=null) {
									Element xml = new Element("Message");
									xml.addContent(createXMLElement("idgroup",groupID));
									xml.addContent(createXMLElement("toName",toAllUsers ? resultSet.getString(2): to ));
									xml.addContent(createXMLElement("from",from));
									xml.addContent(createXMLElement("subject",subject));
									xml.addContent(createXMLElement("message",content));
									xml.addContent(createXMLElement("timeAlife",lifeTime));
									new MessageDistributor(xml,true);
								}
								else {
									LogWriter.write("ERROR: La cuenta de correo {" + to + "} no existe en el sistema.");
								}
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
					message.setFlag(Flags.Flag.DELETED, true);
				}
				folder.close(true);
				store.close();
			} catch (AuthenticationFailedException e) {
				LogWriter.write("ERROR: Falla en la autenticacion del demonio pop3. No se podran obtener los correos.");
				LogWriter.write("ERROR: Por favor, revise el archivo de configuracion y vuelva a iniciar el CoMeet.");
				LogWriter.write("Causa: " + e.getMessage());
				Run.shutDownServer();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(ConfigFileHandler.getTimeIntervalConnect());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private Element createXMLElement(String name,String value) {
		Element element = new Element(name);
		element.setText(value);
		return element;
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		Pop3Handler.host = host;
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		Pop3Handler.user = user;
	}
}
