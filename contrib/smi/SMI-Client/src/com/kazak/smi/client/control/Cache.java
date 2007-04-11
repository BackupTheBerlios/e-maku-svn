package com.kazak.smi.client.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.client.Run;
import com.kazak.smi.client.network.QuerySender;
import com.kazak.smi.client.network.QuerySenderException;

public class Cache {

	private static final long serialVersionUID = -2796119857538194265L;
	private static HashMap<String, String> cacheGroups;
	private static ArrayList<Message> cacheMessages;
	
	public Cache () {
		cacheGroups = new HashMap<String, String>();
		cacheMessages = new ArrayList<Message>();
	}
	
	public static void loadCacheGroups() {
		cacheGroups.clear();
		Thread t = new Thread() {
			public void run() {
				try {
					Document doc = QuerySender.getResultSetST("SEL0001",null);
					Element root = doc.getRootElement();
					Iterator it = root.getChildren("row").iterator();
					while (it.hasNext()) {
						Element el = (Element)it.next();
						Iterator itCols = el.getChildren().iterator();
						String id = ((Element)itCols.next()).getValue();
						String desc = ((Element)itCols.next()).getValue();
						cacheGroups.put(desc,id);  
					}
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	public static String getIdGroup(String key) {
		return cacheGroups.get(key);
	}
	
	public static void addMessages (Element groups) {
		Iterator it = groups.getChildren().iterator();
		while (it.hasNext()) {
			Element cols = (Element)it.next();
			int index = cacheMessages.size() + 1;
			Message message = new Message(index,cols);
			cacheMessages.add(0,message);
		}
	}
	
	public static ArrayList<Message> getMessages() {
		return cacheMessages;
	}
	
	public static Vector<String> getGroups() {
		Vector<String> v = new Vector<String>(cacheGroups.keySet());
		return v;
	}
	
	public static void loadHistoryMessages() {
		cacheMessages.clear();
		Thread t = new Thread() {
			public void run() {
				try {
					String[] args = {Run.user,Run.user};
					Document doc = QuerySender.getResultSetST("SEL0012",args);
					Element root = doc.getRootElement();
					Iterator it = root.getChildren("row").iterator();
					while (it.hasNext()) {
						Element cols = (Element)it.next();
						int index = cacheMessages.size() + 1;
						Message message = new Message(index,cols);
						cacheMessages.add(message);  
					}
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
}