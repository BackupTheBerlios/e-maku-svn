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
import com.kazak.smi.server.database.sql.CloseSQL;
import com.kazak.smi.server.database.sql.RunQuery;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFile;

public class Pop3Handler extends Thread {
	
	private static String user;
	private static String password;
	private static String host;
	
	public Pop3Handler() {
		LogWriter.write("Iniciando demonio pop3");
		LogWriter.write("Host:      "+ ConfigFile.getMailServer());
		Pop3Handler.host = ConfigFile.getMailServer();
		Pop3Handler.user = ConfigFile.getUserMail();
		Pop3Handler.password = ConfigFile.getPassWordMail();
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
					String fullsubject =  message.getSubject();
					fullsubject = fullsubject!=null ? fullsubject.trim() : null;
					int ind1 = addr.getAddress().indexOf('@');
					String from = addr.getAddress().substring(0,ind1);
					int ind2 = fullsubject!=null ? fullsubject.indexOf(',') : -1;
					/*int ind3 = ind2 > 0 ?
							   message.getSubject().indexOf(',',(ind2+1)) : 
							   -1;*/
					String to = "";
					String sub = "";
					String content = message.getContent().toString();
					LogWriter.write("Leyendo correo del buzon de mensajes");
					LogWriter.write("Nuevo mensaje desde: " + addr.getAddress() + " asunto  " +  fullsubject);
					if (ind2==-1) {
						if (!"Mailer-Daemon".equals(from)){
						LogWriter.write("Erro en el asunto del mesaje desde: "+addr.getAddress());
						LogWriter.write("asunto escrito: "+fullsubject);
						EmailSender ems = new EmailSender();
						ems.setFrom	   (user+"@"+host);
						ems.setTo	   (addr.getAddress());
						ems.setSubject ("Error");
						ems.setDate	   (new Date());
						ems.setMessage (
								"El correo no tiene el formato debido.\n" +
								"Por favor verifique el asunto del mensaje\n" +
								"Contenido Original\n" +
								"-------------------------------------\n" +
								"Asunto:"   + fullsubject+"\n" +
								"Mensage:\n"+ content    +"\n" +
								"-------------------------------------\n" +
								"Este mensaje fue enviado por el SMI Apuestas Palmira" );
						ems.send();
						}
					}
					else {
						String[] strings = fullsubject.split(":");
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
						to = fullsubject.substring(0,ind2);
						sub = fullsubject.substring(ind2+1,fullsubject.length());
						to = to.toUpperCase();
						
						RunQuery runQuery = null;
					    ResultSet rs = null;
					    String idgroup = null;
					    boolean all = false;
					    if ("TODOS".equals(to)) { all=true; }
						try {
							runQuery = all ?
									new RunQuery("SEL0028") :
									new RunQuery("SEL0024",new String[]{to,to});
							rs = runQuery.ejecutarSELECT();
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
									LogWriter.write("La cuenta de correo " + to + " no existe en el sistema");
								}
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
					message.setFlag(Flags.Flag.DELETED, true);
				}
				folder.close(true);
				store.close();
			} catch (AuthenticationFailedException e) {
				LogWriter.write("Falló la autenticación del demonio pop3");
				LogWriter.write("mensaje: " + e.getMessage());
				LogWriter.write("no se podran obtener los correos");
				LogWriter.write("revise el archivo de configuración");
				LogWriter.write("y vuelva a iniciar el servidor.");
				LogWriter.write("saliendo..");
				Run.killServer();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(ConfigFile.getTimeIntervalConnect());
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
