package com.kazak.smi.client.control;

import java.util.ArrayList;
import java.util.Iterator;

import org.jdom.Element;

public class MessageList {
	
	private ArrayList<Object> messageList = new ArrayList<Object>();
	
	public MessageList (Integer id,Element xml) {
		messageList.add(id); 
		Iterator it = xml.getChildren().iterator();
		messageList.add(((Element)it.next()).getValue());
		messageList.add(((Element)it.next()).getValue());
		messageList.add(((Element)it.next()).getValue());
		messageList.add(((Element)it.next()).getValue());
		messageList.add(((Element)it.next()).getValue());
		Boolean flag = ((Element)it.next()).getValue().equals("t") ? true : false;
		messageList.add(flag);
	}
	
	public Object getAt(int index) {
		return messageList.get(index);
	}
	
	public void setAt(int index,Object element) {
		messageList.set(index, element);
	}
}