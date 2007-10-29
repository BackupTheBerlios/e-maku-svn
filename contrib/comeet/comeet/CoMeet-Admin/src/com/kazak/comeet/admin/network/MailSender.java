package com.kazak.comeet.admin.network;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
//import javax.swing.JDialog;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.admin.Run;

public class MailSender {
	
	boolean success = false;
	
	public MailSender(JFrame frame, Document doc,String date,String hour,File file) {

		Element root = doc.getRootElement();
		String mailServer = root.getChild("mailServer").getText();
		String destination = root.getChild("to").getText();
		
		if (destination==null || destination.length() == 0) {
			JOptionPane.showMessageDialog(frame,"El usuario \"" + Run.login + "\" no tiene asociada\n" + 
					"una cuenta de correo electrónico.\n" +
					"Por favor, contacte al administrador del CoMeet\n" + 
					"para que modifique su perfíl.");
			return;
		}
		
		String sender = root.getChild("from").getText();
		Session sesion = Session.getDefaultInstance(getProperties(mailServer));
		
		try {
			Address to =  new InternetAddress(destination);
			Address from = new InternetAddress(sender);

			MimeMessage mimeMessage = new MimeMessage(sesion);
			mimeMessage.setFrom(from);
			mimeMessage.addRecipients(Message.RecipientType.TO,new Address[]{to});
			mimeMessage.setSubject("Reporte de control (" + date + "/" + hour + ")");
			mimeMessage.setSentDate(new Date());
			mimeMessage.setText("Reporte de control de Colocadores");

			Multipart multipart = new MimeMultipart();
			BodyPart report = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			report.setDataHandler(new DataHandler(source));
			report.setFileName(file.getName());
			multipart.addBodyPart(report);
			mimeMessage.setContent(multipart);
			Transport.send(mimeMessage);

			JOptionPane.showMessageDialog(frame,"El reporte de control fue enviado a :\n" +
					destination +".\n" +	"Por favor, revise su correo.");
			success = true;
		} catch (SendFailedException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,"Ocurrió un error mientras se enviaba el reporte.\nCausa:\n" + e.getMessage());
		} catch (AddressException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,"Ocurrió un error mientras se enviaba el reporte.\nCausa:\n" + e.getMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,"Ocurrió un error mientras se enviaba el reporte.\nCausa:\n" + e.getMessage());
		}		
	}
	
	public boolean sentOk() {
		return success;
	}

	private Properties getProperties(String mailServer) {
		Properties properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.host", mailServer);
		properties.put("mail.smtp.port", "25");

		return properties;
	}

}