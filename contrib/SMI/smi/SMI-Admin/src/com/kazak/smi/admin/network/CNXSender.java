package com.kazak.smi.admin.network;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.Element;

public class CNXSender {
    
    public CNXSender(SocketChannel socket,String login,String password) {
        Document doc = new Document();
        doc.setRootElement(new Element("CNX"));
        String ipAddress = socket.socket().getLocalAddress().getHostAddress();
        Element ip       = new Element("ip").setText(ipAddress);
        Element user     = new Element("login").setText(login);
        Element passwd   = new Element("password").setText(password);
        Element validate = new Element("validate").setText("true");
        
        doc.getRootElement().addContent(ip);
        doc.getRootElement().addContent(user);
        doc.getRootElement().addContent(passwd);
        doc.getRootElement().addContent(validate);
        
        try {	
        	SocketWriter.write(socket,doc);
	    } catch (IOException e) {
			System.out.println("ERROR: Falla de entrada/salida");
			System.out.println("Causa: " + e.getMessage());
			e.printStackTrace();
		}
    }
}