package jmclient.control;

import java.util.EventObject;

public class ACPFormEvent extends EventObject {

	private static final long serialVersionUID = 5445186563629595184L;
	private String transaction;
	
	public ACPFormEvent(Object source,String transaction) {
		super(source);
		this.transaction = transaction;
	}

	public String getTransaction() {
		return this.transaction;
	}
}
