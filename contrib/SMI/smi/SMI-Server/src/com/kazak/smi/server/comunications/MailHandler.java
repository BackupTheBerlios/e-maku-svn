package com.kazak.smi.server.comunications;


import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFile;

public class MailHandler {
    
    private static Properties props = new Properties();
    
    public static void send (
    		String from,
    		String to,
    		Date date,
    		String subject,
    		String message
    		) {
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.host", ConfigFile.getMailServer());
    	props.put("mail.smtp.port", "25");
        Session sesion = Session.getDefaultInstance(props);
        Address para;
		try {
			para = new InternetAddress(to);
	        Address de = new InternetAddress(from);
	        MimeMessage mimeMenssage = new MimeMessage(sesion);
	
	        mimeMenssage.setFrom(de);
	        mimeMenssage.addRecipients(Message.RecipientType.TO,new Address[]{para});
	        mimeMenssage.setSubject(subject);
	        mimeMenssage.setSentDate(date);
	        mimeMenssage.setText(message);
	        Transport.send(mimeMenssage);
		} catch (SendFailedException e) {
			LogWriter.write("Falló el envio del mensaje a " + to);
			LogWriter.write("error :" +e.getMessage());
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }        

}