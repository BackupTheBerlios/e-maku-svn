package com.kazak.smi.client.control;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.client.Run;
import com.kazak.smi.client.network.QuerySender;
import com.kazak.smi.client.network.SocketHandler;
import com.kazak.smi.client.network.SocketWriter;

public class ConfirmMessage extends Thread {

	private Boolean confirm;
	private String date;
	private String time;
	private String subject;
	private String from;
	
	public ConfirmMessage(Boolean confirm,String date, String time,String subject,String from) {
		this.confirm = confirm;
		this.date = date;
		this.time = time;
		this.subject = subject;
		this.from = from;
		start();
	}
	
	public void run() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR011");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		pack.addContent(new Element("field").setText(confirm.toString()));
		pack.addContent(new Element("field").setText(date));
		pack.addContent(new Element("field").setText(time));
		pack.addContent(new Element("field").setText(Run.user));
		pack.addContent(new Element("field").setText(subject));
		pack.addContent(new Element("field").setText(from));
		
		transaction.addContent(pack);
		SocketChannel sock = SocketHandler.getSock();
		try {
			SocketWriter.writing(sock,doc);
		} catch (IOException ex) {
			System.out.println("Error de entrada y salida");
			System.out.println("mensaje: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
