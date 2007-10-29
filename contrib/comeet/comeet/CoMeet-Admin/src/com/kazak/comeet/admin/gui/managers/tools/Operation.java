package com.kazak.comeet.admin.gui.managers.tools;

import java.io.IOException;

import org.jdom.Document;

import com.kazak.comeet.admin.network.SocketHandler;
import com.kazak.comeet.admin.network.SocketWriter;

public class Operation {
			
	public static void execute(Document document) {
		if (document!=null) {
			try {
				SocketWriter.write(SocketHandler.getSock(),document);
			} catch (IOException ex) {
				System.out.println("ERROR: Falla de entrada/salida");
				System.out.println("Causa: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

}
