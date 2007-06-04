package com.kazak.smi.admin.gui.managers.tools.Transactions;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.transactions.QuerySender;

public class GroupDocument {
	private String name;
	private String code;
	private String ip;
	private String group;
	
	public GroupDocument(String[] data) {
		name = data[0];
		code = data[1];
		ip = data[2];
		group = data[3];
	}
	
	public Document getDocumentToAdd() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR007");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		 
		Cache.Group g = Cache.getGroup(group);
		pack.addContent(createField(code));
		pack.addContent(createField(name));
		pack.addContent(createField(ip));
		pack.addContent(createField(g.getId()));
		
		transaction.addContent(pack);
		
		return doc;
	}
		
	public Document getDocumentToEdit() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		Element driver = new Element("driver");
        driver.setText("TR008");
        transaction.addContent(driver);
        
		Element pack = new Element("package");

		Cache.Group groupObject = Cache.getGroup(group);
		pack.addContent(createField(ip));
		pack.addContent(createField(groupObject.getId()));
		pack.addContent(createField(name));
					
		transaction.addContent(pack);
		
		pack = new Element("package");
		transaction.addContent(pack);
		
		return doc;
	}
	
	public Document getDocumentToDelete() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR009");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		pack.addContent(createField(code));
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Element createField(String text) {
		Element element = new Element("field");
		element.setText(text);
		return element;
	}
}
