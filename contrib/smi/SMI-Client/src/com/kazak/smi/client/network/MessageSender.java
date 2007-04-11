package com.kazak.smi.client.network;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.client.control.Cache;
import com.kazak.smi.client.gui.LoginWindow;

public class MessageSender {
	public MessageSender(String to,String subject, String message) {
		Element xml = new Element("Message");
		String idgroup = Cache.getIdGroup(to);
		xml.addContent(new Element("idgroup").setText(idgroup));
		xml.addContent(new Element("from").setText(LoginWindow.getLoginUser()));
		xml.addContent(new Element("subject").setText(subject));
		xml.addContent(new Element("message").setText(message));
		SocketChannel sock = SocketHandler.getSock();
		try {
			SocketWriter.writing(sock,new Document(xml));
		} catch (IOException e) {
			System.out.println("Error de entrada y salida");
			System.out.println("mensaje: " + e.getMessage());
			e.printStackTrace();
		}
	}
}