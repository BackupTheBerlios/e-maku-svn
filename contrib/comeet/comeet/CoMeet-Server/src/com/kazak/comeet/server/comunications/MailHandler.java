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
    
    public static void sendMessage (String from, String to,	Date date, String subject,
    		String message) {
    	String host = ConfigFileHandler.getMailServer();
    	properties.put("mail.transport.protocol", "smtp");
    	properties.put("mail.smtp.host", host);
    	properties.put("mail.smtp.port", "25");
        Session session = Session.getInstance(properties, null);
        Address destinationAddress;    
        
		try {
			destinationAddress      = new InternetAddress(to);
	        Address senderAddress   = new InternetAddress(from);
	        
	        MimeMessage mimeMessage = new MimeMessage(session);
	        mimeMessage.setFrom(senderAddress);
	        mimeMessage.addRecipients(Message.RecipientType.TO,new Address[]{destinationAddress});
	        mimeMessage.setSubject(subject);
	        mimeMessage.setSentDate(date);
	        mimeMessage.setText(message);
	        Transport.send(mimeMessage);
	        
		} catch (SendFailedException e) {
			LogWriter.write("ERROR[SendFailedException]: Falla en el envio del mensaje desde " + from + " para " + to);
			LogWriter.write("ERROR: " + e.getMessage());
			e.printStackTrace();
		} catch (AddressException e) {
			LogWriter.write("ERROR[AddressException]: Falla en el envio del mensaje desde " + from + " para " + to);
			LogWriter.write("ERROR: " + e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			LogWriter.write("ERROR[MessagingException]: Falla en el envio del mensaje desde " + from + " para " + to);
			LogWriter.write("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
    }        

}