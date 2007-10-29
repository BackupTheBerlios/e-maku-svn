package com.kazak.comeet.server.comunications;

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

import com.kazak.comeet.server.misc.LogWriter;
import com.kazak.comeet.server.misc.settings.ConfigFileHandler;

public class MailHandler {
    
    private static Properties properties = new Properties();
    
    public static void sendMessage (
    		String from,
    		String to,
    		Date date,
    		String subject,
    		String message
    		) {
    	properties.put("mail.transport.protocol", "smtp");
    	properties.put("mail.smtp.host", ConfigFileHandler.getMailServer());
    	properties.put("mail.smtp.port", "25");
        Session session = Session.getDefaultInstance(properties);
        Address destinationAddress;
		try {
			destinationAddress       = new InternetAddress(to);
	        Address senderAddress    = new InternetAddress(from);
	        MimeMessage mimeMessage = new MimeMessage(session);
	
	        mimeMessage.setFrom(senderAddress);
	        mimeMessage.addRecipients(Message.RecipientType.TO,new Address[]{destinationAddress});
	        mimeMessage.setSubject(subject);
	        mimeMessage.setSentDate(date);
	        mimeMessage.setText(message);
	        Transport.send(mimeMessage);
		} catch (SendFailedException e) {
			LogWriter.write("ERROR: Falla en el envio del mensaje a " + to);
			LogWriter.write("ERROR: " + e.getMessage());
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }        

}