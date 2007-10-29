package com.kazak.comeet.admin.gui.managers.tools.Transactions;

import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.admin.control.Cache;
import com.kazak.comeet.admin.transactions.QuerySender;

public class UserDocument {
	Document doc;
	private String login;
	private String passwd;
	private String name;
	private String mail;
	private String isAdmin;
	private String isEnabled;
	private String groupId;
	private String isAuditor;
	private String ipControl;	
	Vector<String> posCodesVector = new Vector<String>();

	public UserDocument() {
	}

	public UserDocument(String[] data) {
		setData(data);
	}
	
	public UserDocument(String[] data, Vector<String> posCodesVector) {
		setData(data);
		this.posCodesVector = posCodesVector;
	}
	
	public void setData(String[] data) {
		login     = data[0];
		passwd    = data[1];
		if(passwd.length() == 0) {
			passwd = Cache.getUser(login).getPasswd();
		}
		name      = data[2];
		mail      = data[3];
		isAdmin   = data[4];
		isEnabled = data[5];
		groupId   = data[6];
		isAuditor = data[7];
		ipControl = data[8];
	}
		
	public Document getDocumentToAdd() {
		Element transaction = new Element("Transaction");
		doc = new Document(transaction);
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        Element driver = new Element("driver");
        driver.setText("TR001");
        transaction.addContent(driver);
        
		Element pack = new Element("package");
		pack.addContent(createField(login));
		pack.addContent(createField(passwd));
		pack.addContent(createField(name));
		pack.addContent(createField(mail));
		pack.addContent(createField(isAdmin));
		pack.addContent(createField(isEnabled));
		pack.addContent(createField(groupId));
		pack.addContent(createField(isAuditor));		
		transaction.addContent(pack);
		
		// Saving pos data from table to package
		pack = new Element("package");
		int max = posCodesVector.size();
		for (int i=0 ; i < max ; i++) {
			Element subpackage = new Element("subpackage");
			subpackage.addContent(createField(login));
			subpackage.addContent(createField(posCodesVector.get(i).toString()));
			subpackage.addContent(createField(ipControl));
			pack.addContent(subpackage);
		}
		transaction.addContent(pack);
		
		return doc;
	}
		
	public Document getDocumentToEdit() {
		Element transaction = new Element("Transaction");
		doc = new Document(transaction);
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        Element driver = new Element("driver");
        driver.setText("TR002");
        transaction.addContent(driver);
        
        String uid = Cache.getUser(login).getId();
        
		Element pack = new Element("package");
		pack.addContent(createField(login));
		pack.addContent(createField(passwd));
		pack.addContent(createField(name));
		pack.addContent(createField(mail));
		pack.addContent(createField(isAdmin));
		pack.addContent(createField(groupId));
		pack.addContent(createField(isAuditor));
		pack.addContent(createField(uid));
		transaction.addContent(pack);
		
		pack = new Element("package");
		pack.addContent(createField(uid));
		transaction.addContent(pack);
		
		// Saving pos data from table to package
		pack = new Element("package");
		int max = posCodesVector.size();
		for (int i=0 ; i < max ; i++) {
			Element subpackage = new Element("subpackage");
			subpackage.addContent(createField(login));
			subpackage.addContent(createField(posCodesVector.get(i).toString()));
			subpackage.addContent(createField(ipControl));
			pack.addContent(subpackage);
		}
		transaction.addContent(pack);
		
        return doc;
	}
	
	public Document getDocumentToDelete(String login) {
		Element transaction = new Element("Transaction");
		Document doc = new Document(transaction);
		
		Element id = new Element("id");
        id.setText(QuerySender.getId());
        transaction.addContent(id);
        
        Element driver = new Element("driver");
        driver.setText("TR003");
        transaction.addContent(driver);
        
        Cache.User user = Cache.getUser(login);
        
		Element pack = new Element("package");
		pack.addContent(createField(user.getId()));
		transaction.addContent(pack);
		
		pack = new Element("package");
		pack.addContent(createField(user.getId()));
		transaction.addContent(pack);
		return doc;
	}
	
	private Element createField(String text) {
		Element element = new Element("field");
		element.setText(text);
		return element;
	}	
}
