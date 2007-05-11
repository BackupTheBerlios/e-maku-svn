package com.kazak.smi.server.control;

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

import com.kazak.smi.server.Run;
import com.kazak.smi.server.database.sql.QueryClosingHandler;
import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFileHandler;

public class Pop3Handler extends Thread {
	
	private static String user;
	private static String password;
	private static String host;
	
	public Pop3Handler() {
		LogWriter.write("INFO: Iniciando demonio pop3");
		LogWriter.write("INFO: Servidor " + ConfigFileHandler.getMailServer());
		Pop3Handler.host = ConfigFileHandler.getMailServer();
		Pop3Handler.user = ConfigFileHandler.getUserMail();
		Pop3Handler.password = ConfigFileHandler.getPassWordMail();
		start();
	}
	
	public void run() {
		Session session = Session.getDefaultInstance(new Properties(), null);
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
					InternetAddress addr = (InternetAddress) message.getFrom()[0];
					String fullSubject   =  message.getSubject();
					fullSubject          = fullSubject!=null ? fullSubject.trim() : null;
					int index1             = addr.getAddress().indexOf('@');
					String from          = addr.getAddress().substring(0,index1);
					int index2             = fullSubject!=null ? fullSubject.indexOf(',') : -1;

					String to = "";
					String sub = "";
					String content = message.getContent().toString();
					LogWriter.write("INFO: Leyendo correo del buzon de mensajes.");
					LogWriter.write("INFO: Nuevo mensaje desde {" + addr.getAddress() + "} con asunto [ " +  fullSubject + " ]");
					if (index2==-1) {
						if (!"Mailer-Daemon".equals(from)){
						LogWriter.write("INFO: Error en el asunto del mensaje desde: "+addr.getAddress());
						LogWriter.write("asunto escrito: "+fullSubject);
						EmailSender ems = new EmailSender();
						ems.setFrom	   (user+"@"+host);
						ems.setTo	   (addr.getAddress());
						ems.setSubject ("Error");
						ems.setDate	   (new Date());
						ems.setMessage (
								"El correo no tiene el formato apropiado.\n" +
								"Por favor verifique el asunto del mensaje\n" +
								"Contenido Original\n" +
								"-------------------------------------\n" +
								"Asunto:"   + fullSubject+"\n" +
								"Mensage:\n"+ content    +"\n" +
								"-------------------------------------\n" +
								"Este mensaje fue enviado por el Sistema de Mensajeria Instantanea" );
						ems.send();
						}
					}
					else {
						String[] strings = fullSubject.split(":");
						String timeAlife = "-1";
						if (strings.length > 0 ) {
							timeAlife = strings[strings.length-1];
							int timeAlifeInt = -1;
							try {
								timeAlifeInt= Integer.valueOf(timeAlife);
							} catch (NumberFormatException NFEe) {
								timeAlifeInt = -1;
							}
							if (timeAlifeInt < 1 || timeAlifeInt>99) {
								timeAlife = "-1";
							}
						}
						to = fullSubject.substring(0,index2);
						sub = fullSubject.substring(index2+1,fullSubject.length());
						to = to.toUpperCase();
						
						QueryRunner runQuery = null;
					    ResultSet rs = null;
					    String idgroup = null;
					    boolean all = false;
					    if ("TODOS".equals(to)) { all=true; }
						try {
							runQuery = all ?
									new QueryRunner("SEL0028") :
									new QueryRunner("SEL0024",new String[]{to,to});
							rs = runQuery.runSELECT();
							while (rs.next()) {
								idgroup =  rs.getString(1);
								if (idgroup!=null) {
									Element xml = new Element("Message");
									xml.addContent(create("idgroup",idgroup));
									xml.addContent(create("toName",	all ? rs.getString(2): to ));
									xml.addContent(create("from",   from));
									xml.addContent(create("subject",sub));
									xml.addContent(create("message",content));
									xml.addContent(create("timeAlife",timeAlife));
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
							QueryClosingHandler.close(rs);
							runQuery.closeStatement();
						}
					}
					message.setFlag(Flags.Flag.DELETED, true);
				}
				folder.close(true);
				store.close();
			} catch (AuthenticationFailedException e) {
				LogWriter.write("ERROR: Falla en la autenticacion del demonio pop3. No se podran obtener los correos.");
				LogWriter.write("ERROR: Por favor, revise el archivo de configuracion y vuelva a iniciar el SMI.");
				LogWriter.write("Causa: " + e.getMessage());
				Run.killServer();
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
	
	private Element create(String name,String value) {
		Element el = new Element(name);
		el.setText(value);
		return el;
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
