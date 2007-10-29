package com.kazak.comeet.admin.gui.managers.tools.Transactions;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.admin.transactions.QuerySender;

public class GroupDocument {
	private String name;
	private String isVisible;
	private String isZone;
	private String newName;
	
	public GroupDocument(String[] data) {
		name = data[0];
		isVisible = data[1];
		isZone = data[2];
		newName = data[3];
	}
	
	public Document getDocumentToAdd() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR004");
        transaction.addContent(driver);
        
		Element pack = new Element("package");

		pack.addContent(createField(name));
		pack.addContent(createField(isVisible));
		pack.addContent(createField(isZone));

		transaction.addContent(pack);
		
		return doc;
	}
		
	public Document getDocumentToEdit() {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		Element driver = new Element("driver");
        driver.setText("TR005");
        transaction.addContent(driver);
        
		Element pack = new Element("package");

		pack.addContent(createField(newName));
		pack.addContent(createField(isVisible));
		pack.addContent(createField(isZone));
		pack.addContent(createField(name));
		
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
        driver.setText("TR006");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		pack.addContent(createField(name));
		transaction.addContent(pack);
		
		return doc;
	}
	
	private Element createField(String text) {
		Element element = new Element("field");
		element.setText(text);
		return element;
	}
}
