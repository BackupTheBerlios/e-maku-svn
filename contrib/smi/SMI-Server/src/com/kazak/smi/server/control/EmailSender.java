package com.kazak.smi.server.control;

import java.util.Date;

import com.kazak.smi.server.comunications.MailHandler;

public class EmailSender extends Thread {
	private String from;
	private String to;
	private String subject;
	private String message;
	private Date date;
	private String toNameFull;
	private String colocationPoint;
	
	public void setFrom(String from) {
		this.from = from;
	}

	public void setMessage(String message) {
		this.message = message;
		
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public void send() {
		start();
	}
	
	public void run() {
		String fulltext = 
			toNameFull+" escribio desde "+colocationPoint+":\n" +
			"--------------------------------------------\n"+
			message+ "\n"+
			"--------------------------------------------\n" +
			".";
		MailHandler.send(from, to, date, subject, fulltext);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setToNameFull(String toNameFull) {
		this.toNameFull = toNameFull;
	}

	public void setColocationPoint(String colocationPoint) {
		this.colocationPoint = colocationPoint;
	}
}
