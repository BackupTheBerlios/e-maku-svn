package com.kazak.smi.server.businessrules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

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

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.server.comunications.SocketServer;
import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.control.Pop3Handler;
import com.kazak.smi.server.database.sql.QueryRunner;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFileHandler;

public class ControlMessageReporter {

	private static Properties properties = new Properties();
	private Iterator iterator;
	private Vector<Vector> usersVector = new Vector();
	
	@SuppressWarnings("deprecation")
	// TODO: Verificar si se pueden cambiar los metodos deprecados
	
	public ControlMessageReporter(SocketChannel sock, Element args, Element packet, String idValue) {
		
		this.iterator = packet.getChild("package").getChildren().iterator();
		String[] sqlArgs = new String[2];
		sqlArgs[0] = ((Element) iterator.next()).getValue();
		sqlArgs[1] = ((Element) iterator.next()).getValue();
		
		System.out.println("Fecha1: " + sqlArgs[0]);
		System.out.println("Fecha2: " + sqlArgs[1]);
		System.out.println("ID: " + idValue);
		
		QueryRunner queryRunner = null;
		ResultSet resultSet = null;
		
		try {
			queryRunner = new QueryRunner("SEL0015",sqlArgs);
			resultSet = queryRunner.select();
	    	Document doc = new Document();
	    	Element root = new Element("CONTROLRESULT");
	    	Element id = new Element("id").setText(idValue);
	    	doc.setRootElement(root);
	    	root.addContent(id);
	    	
			while (resultSet.next()) {
		    	Element rows, date, hour, subject, login, names;
				rows = new Element("row");
				date = new Element("cols").setText(resultSet.getString(1));
				hour = new Element("cols").setText(resultSet.getString(2));
				subject = new Element("cols").setText(resultSet.getString(3));
				login = new Element("cols").setText(resultSet.getString(4));
				names = new Element("cols").setText(resultSet.getString(5));				
				rows.addContent(date);
				rows.addContent(hour);
				rows.addContent(subject);
				rows.addContent(login);
				rows.addContent(names);	
				root.addContent(rows);
			}			

			try {
        		SocketWriter.write(sock,doc);
        	} catch (IOException e) {
				LogWriter.write("ERROR: Falla de entrada/salida");
				LogWriter.write("Causa: " + e.getMessage());
				e.printStackTrace();
			}

			/*
			queryRunner = new QueryRunner("SEL0022",sqlArgs);
			resultSet = queryRunner.select();
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("REPORTE DE CONTROL");
			int i=0;
			while (resultSet.next()) {
			    String code = resultSet.getString(1);
			    String name = resultSet.getString(2);
			    String pos = resultSet.getString(3);
			    String status = resultSet.getString(4);
			    
			    System.out.println("Enviando: " + code + " " + name + " " + pos + " " + status);
			    
			    Vector<String> user = new Vector<String>();
			    user.add(code);
			    user.add(name);
			    user.add(pos);
			    user.add(status);
			    usersVector.add(user);
			    
			    HSSFRow row = sheet.createRow((short)i);
			    row.createCell((short)0).setCellValue(code);
			    row.createCell((short)1).setCellValue(name);
			    row.createCell((short)2).setCellValue(pos);
			    row.createCell((short)3).setCellValue(status);
			    i++;
			}
			if (i==0) {
				 HSSFRow row = sheet.createRow((short)i);
				 row.createCell((short)0).setCellValue("La consulta no generó datos, verifique las fechas.");
			}
			FileOutputStream outputFile = new FileOutputStream("/tmp/reporte.xls");
		    wb.write(outputFile);
		    outputFile.close();
			resultSet.close();
			queryRunner.closeStatement();
			
			properties.put("mail.transport.protocol", "smtp");
	    	properties.put("mail.smtp.host", ConfigFileHandler.getMailServer());
	    	properties.put("mail.smtp.port", "25");
	    	
	        Session sesion = Session.getDefaultInstance(properties);
			Address to =  new InternetAddress(SocketServer.getSocketInfo(sock).getEmail());
	        Address from = new InternetAddress(Pop3Handler.getUser()+"@"+Pop3Handler.getHost());
	        
	        MimeMessage mimeMessage = new MimeMessage(sesion);
	
	        mimeMessage.setFrom(from);
	        mimeMessage.addRecipients(Message.RecipientType.TO,new Address[]{to});
	        mimeMessage.setSubject("Reporte de control");
	        mimeMessage.setSentDate(new Date());
	        mimeMessage.setText("Reporte de control de Colocadores");
	        
	        Multipart multipart = new MimeMultipart();
	        BodyPart report = new MimeBodyPart();
	        File file = new File("/tmp/reporte.xls");
	        DataSource source = new FileDataSource(file);
	        report.setDataHandler(new DataHandler(source));
	        report.setFileName(file.getName());
	        multipart.addBodyPart(report);
	        mimeMessage.setContent(multipart);
	        Transport.send(mimeMessage);
	        TransactionRunner.notifyMessageReception(sock,idValue,"El reporte de control fue enviado a :\n" +
											 SocketServer.getSocketInfo(sock).getEmail()+".\n" +
											 "Por favor revise su correo.");
	        LogWriter.write("Reporte de control de usuarios enviado a {" + SocketServer.getSocketInfo(sock).getEmail() + "}");
		} catch (SendFailedException e) {
			TransactionRunner.notifyErrorMessage(
					 sock,
					 idValue,
					 "No se pudo procesar la operacion:\n" +
					 "Causa:\n"+e.getLocalizedMessage());
		} catch (AddressException e) {
			e.printStackTrace();
			TransactionRunner.notifyErrorMessage(
					 sock,
					 idValue,
					 "No se pudo procesar la operación:\n" +
					 "posiblemente el usuario tiene una\n"+
					 "cuenta de correo inválidValuea o no tenga\n"+
					 "una cuenta asignada en el sistema.\n"+ 
					 "Causa:\n"+e.getMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
			TransactionRunner.notifyErrorMessage(
					 sock,
					 idValue,
					 "No se pudo procesar la operación:\n" +
					 "Causa:\n"+e.getLocalizedMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			TransactionRunner.notifyErrorMessage(
					 sock,
					 idValue,
					 "No se pudo procesar la operación:\n" +
					 "Causa:\n"+e.getMessage()); 
		} catch (IOException e) {
			e.printStackTrace();
			TransactionRunner.notifyErrorMessage(
					 sock,
					 idValue,
					 "No se pudo procesar la operación:\n" +
					 "Causa:\n"+e.getMessage()); */
		} catch (SQLException e) {
			queryRunner.closeStatement();
			e.printStackTrace();
			TransactionRunner.notifyErrorMessage(
					 sock,
					 idValue,
					 "No se pudo procesar la operación:\n" +
					 "Causa:\n"+e.getMessage());
		} catch (SQLNotFoundException e) {
			
			e.printStackTrace();
			TransactionRunner.notifyErrorMessage(
					 sock,
					 idValue,
					 "No se pudo procesar la operación:\n" +
					 "Causa:\n"+e.getMessage());
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
			TransactionRunner.notifyErrorMessage(
					 sock,
					 idValue,
					 "No se pudo procesar la operación:\n" +
					 "Causa:\n"+e.getMessage());
		} 
	}
	
	class User {
	   String code;
	   String name;
	   String pos;
		
	}
	
}
