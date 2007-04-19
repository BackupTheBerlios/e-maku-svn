package com.kazak.smi.server.businessrules;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.comunications.SocketServer;

// Esta clase se encarga de enviar la lista de usuarios en linea

public class Statistics {
	private Iterator itArgs;
	private String[] arrUserInfo;
	private Vector<String[]> arrWs = new Vector<String[]>();
	
	public Statistics(SocketChannel sock, Element args, Element packet, String id) {
		this.itArgs = args.getChildren("arg").iterator();
		String type = args.getChildText("action");
		boolean ret = false;
		String message = "";

		if ("list".equals(type)) {
				ret = getOnlineUsers();
		}
		
		if (ret) {						
			Element usersOnline = new Element("ANSWER");
			try {
				SocketWriter.writing(sock,new Document(usersOnline));
			} catch (IOException e) {
				e.printStackTrace();
			}
			message = "Los datos fueron almacenados satisfactoriamente";
			RunTransaction.successMessage(sock,id,message);
		}
		else {
			RunTransaction.errorMessage(sock,id,"No se pudo insertar el usuario causa: " + message);
		}
	}
	
	public boolean getOnlineUsers() {
		
		return true;
	}
	

}
