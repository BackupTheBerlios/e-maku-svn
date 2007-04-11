package com.kazak.smi.client.network;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.Element;

public class CNXSender {
    
    public CNXSender(SocketChannel socket,String login,String password) {
        Document doc = new Document();
        doc.setRootElement(new Element("CNX"));
        String ipAddr = socket.socket().getLocalAddress().getHostAddress();
        Element ip = new Element("ip").setText(ipAddr);
        Element user = new Element("login").setText(login);
        Element secret = new Element("password").setText(password);
        
        doc.getRootElement().addContent(ip);
        doc.getRootElement().addContent(user);
        doc.getRootElement().addContent(secret);
        try {
        	SocketWriter.writing(socket,doc);
        } catch (IOException e) {
    		System.out.println("Error de entrada y salida");
    		System.out.println("mensaje: " + e.getMessage());
    		e.printStackTrace();
    	}
    }
}