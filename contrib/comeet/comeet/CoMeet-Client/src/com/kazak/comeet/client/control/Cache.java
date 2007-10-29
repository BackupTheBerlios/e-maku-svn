package com.kazak.comeet.client.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.client.Run;
import com.kazak.comeet.client.network.QuerySender;
import com.kazak.comeet.client.network.QuerySenderException;

public class Cache {

	private static final long serialVersionUID = -2796119857538194265L;
	private static HashMap<String, String> groupsCacheHash;
	private static ArrayList<MessageList> msgCacheList;
	
	public Cache () {
		groupsCacheHash = new HashMap<String, String>();
		msgCacheList = new ArrayList<MessageList>();
	}
	
	public static void loadGroupsCache() {
		groupsCacheHash.clear();
		Thread t = new Thread() {
			public void run() {
				try {
					Document doc = QuerySender.getResultSetFromST("SEL0001",null);
					Element root = doc.getRootElement();
					Iterator iterator = root.getChildren("row").iterator();
					while (iterator.hasNext()) {
						Element element = (Element)iterator.next();
						Iterator columnsIterator = element.getChildren().iterator();
						String id = ((Element)columnsIterator.next()).getValue();
						String name = ((Element)columnsIterator.next()).getValue();
						groupsCacheHash.put(name,id);  
					}
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	public static String getGroupID(String key) {
		return groupsCacheHash.get(key);
	}
	
	public static void addMessages (Element messages) {
		Iterator iterator = messages.getChildren().iterator();
		while (iterator.hasNext()) {
			Element columns = (Element)iterator.next();
			int index = msgCacheList.size() + 1;
			MessageList message = new MessageList(index,columns);
			msgCacheList.add(0,message);
		}
	}
	
	public static ArrayList<MessageList> getMessages() {
		return msgCacheList;
	}
	
	public static String[] getGroups() {
		Set <String>bag = groupsCacheHash.keySet();
		String[] sortedGroupList = (String[])bag.toArray(new String[bag.size()]);
		Arrays.sort(sortedGroupList);

		return sortedGroupList;	
	}
	
	public static void loadMessagesHistory() {
		msgCacheList.clear();
		Thread t = new Thread() {
			public void run() {
				try {
					String[] args = {Run.user,Run.user};
					Document doc = QuerySender.getResultSetFromST("SEL0012",args);
					Element root = doc.getRootElement();
					Iterator iterator = root.getChildren("row").iterator();
					while (iterator.hasNext()) {
						Element columns = (Element)iterator.next();
						int index = msgCacheList.size() + 1;
						MessageList message = new MessageList(index,columns);
						msgCacheList.add(message);  
					}
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
}