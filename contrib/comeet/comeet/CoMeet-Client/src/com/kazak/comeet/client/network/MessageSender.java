package com.kazak.comeet.client.network;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.client.control.Cache;
import com.kazak.comeet.client.gui.LoginWindow;

public class MessageSender {
	
	public MessageSender(String to,String subject, String message) {
		Element xml = new Element("Message");
		String groupID = Cache.getGroupID(to);
		xml.addContent(new Element("idgroup").setText(groupID));
		xml.addContent(new Element("from").setText(LoginWindow.getLoginUser()));
		xml.addContent(new Element("subject").setText(subject));
		xml.addContent(new Element("message").setText(message));
		SocketChannel sock = SocketHandler.getSock();
		try {
			SocketWriter.writing(sock,new Document(xml));
		} catch (IOException e) {
			System.out.println("ERROR: Falla de entrada / salida");
			System.out.println("Causa: " + e.getMessage());
			e.printStackTrace();
		}
	}
}