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
import org.jdom.Element;

import com.kazak.smi.server.comunications.SocketServer;
import com.kazak.smi.server.control.Pop3Handler;
import com.kazak.smi.server.database.sql.RunQuery;
import com.kazak.smi.server.database.sql.SQLBadArgumentsException;
import com.kazak.smi.server.database.sql.SQLNotFoundException;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFile;

public class MessageControlReporter {

	private static Properties props = new Properties();
	private Iterator it;
	@SuppressWarnings("deprecation")
	public MessageControlReporter(SocketChannel sock, Element args, Element packet, String id) {
		
		this.it = packet.getChild("package").getChildren().iterator();
		String[] sqlArgs = new String[2];
		sqlArgs[0] = ((Element) it.next()).getValue();
		sqlArgs[1] = ((Element) it.next()).getValue();
		//String login = ((Element) it.next()).getValue();
		
		RunQuery runQuery = null;
		//InfoUser ifu = null;
		ResultSet rs = null;
		try {
			runQuery = new RunQuery("SEL0022",sqlArgs);
			rs = runQuery.runSELECT();
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("REPORTE DE CONTROL");
			int i=0;
			while (rs.next()) {
			    HSSFRow row = sheet.createRow((short)i);
			    row.createCell((short)0).setCellValue(rs.getString(1));
			    row.createCell((short)1).setCellValue(rs.getString(2));
			    row.createCell((short)2).setCellValue(rs.getString(3));
			    row.createCell((short)3).setCellValue(rs.getString(4));
			    i++;
			}
			if (i==0) {
				 HSSFRow row = sheet.createRow((short)i);
				 row.createCell((short)0).setCellValue("La consulta no genero datos, verifique las fechas");
			}
			FileOutputStream fileOut = new FileOutputStream("/tmp/reporte.xls");
		    wb.write(fileOut);
		    fileOut.close();
			rs.close();
			runQuery.closeStatement();
			
			;
			props.put("mail.transport.protocol", "smtp");
	    	props.put("mail.smtp.host", ConfigFile.getMailServer());
	    	props.put("mail.smtp.port", "25");
	    	
	        Session sesion = Session.getDefaultInstance(props);
			Address to =  new InternetAddress(SocketServer.getSocketInfo(sock).getEmail());
	        Address from = new InternetAddress(Pop3Handler.getUser()+"@"+Pop3Handler.getHost());
	        
	        MimeMessage mimeMessage = new MimeMessage(sesion);
	
	        mimeMessage.setFrom(from);
	        mimeMessage.addRecipients(Message.RecipientType.TO,new Address[]{to});
	        mimeMessage.setSubject("Reporte de control");
	        mimeMessage.setSentDate(new Date());
	        mimeMessage.setText("Reporte de control de colocadores");
	        
	        Multipart multipart = new MimeMultipart();
	        BodyPart report = new MimeBodyPart();
	        File file = new File("/tmp/reporte.xls");
	        DataSource source = new FileDataSource(file);
	        report.setDataHandler(new DataHandler(source));
	        report.setFileName(file.getName());
	        multipart.addBodyPart(report);
	        mimeMessage.setContent(multipart);
	        Transport.send(mimeMessage);
	        RunTransaction.
			successMessage
			(sock,id,"El reporte fue enviado a :\n" +
					SocketServer.getSocketInfo(sock).getEmail()+".\n" +
					 "Por favor revise su correo.");
	        LogWriter.write("Reporte de control de usuarios enviado a  " + SocketServer.getSocketInfo(sock).getEmail());
		} catch (SendFailedException e) {
			RunTransaction.errorMessage(
					 sock,
					 id,
					 "No se pudo procesar la operacion:\n" +
					 "causa:\n"+e.getLocalizedMessage());
		} catch (AddressException e) {
			e.printStackTrace();
			RunTransaction.errorMessage(
					 sock,
					 id,
					 "No se pudo procesar la operación:\n" +
					 "posiblemente el usuario tiene una\n"+
					 "cuenta de correo inválida o no tenga\n"+
					 "una cuenta asignada en el sistema.\n"+ 
					 "Causa:\n"+e.getMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
			RunTransaction.errorMessage(
					 sock,
					 id,
					 "No se pudo procesar la operacion:\n" +
					 "causa:\n"+e.getLocalizedMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			RunTransaction.errorMessage(
					 sock,
					 id,
					 "No se pudo procesar la operacion:\n" +
					 "causa:\n"+e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			RunTransaction.errorMessage(
					 sock,
					 id,
					 "No se pudo procesar la operacion:\n" +
					 "causa:\n"+e.getMessage());
		} catch (SQLException e) {
			runQuery.closeStatement();
			e.printStackTrace();
			RunTransaction.errorMessage(
					 sock,
					 id,
					 "No se pudo procesar la operacion:\n" +
					 "causa:\n"+e.getMessage());
		} catch (SQLNotFoundException e) {
			
			e.printStackTrace();
			RunTransaction.errorMessage(
					 sock,
					 id,
					 "No se pudo procesar la operacion:\n" +
					 "causa:\n"+e.getMessage());
		} catch (SQLBadArgumentsException e) {
			e.printStackTrace();
			RunTransaction.errorMessage(
					 sock,
					 id,
					 "No se pudo procesar la operacion:\n" +
					 "causa:\n"+e.getMessage());
		} 
	}
}
