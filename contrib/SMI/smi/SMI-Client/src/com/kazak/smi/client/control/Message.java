package com.kazak.smi.client.control;

//import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jdom.Element;
//import org.jdom.output.XMLOutputter;

public class Message {
	
	private ArrayList<Object> data = new ArrayList<Object>();
	
	public Message (Integer id,Element xml) {
		/*XMLOutputter out = new XMLOutputter();
		try {
			out.output(xml,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		data.add(id); 
		Iterator it = xml.getChildren().iterator();
		data.add(((Element)it.next()).getValue());
		data.add(((Element)it.next()).getValue());
		data.add(((Element)it.next()).getValue());
		data.add(((Element)it.next()).getValue());
		data.add(((Element)it.next()).getValue());
		Boolean b = ((Element)it.next()).getValue().equals("t") ? true : false;
		data.add(b);
	}
	
	public Object getAt(int index) {
		return data.get(index);
	}
	
	public void setAt(int index,Object element) {
		data.set(index, element);
	}
}