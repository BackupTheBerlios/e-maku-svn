package com.kazak.smi.server.control;

import java.util.Date;

import com.kazak.smi.server.comunications.MailHandler;

public class EmailSender extends Thread {
	private String from;
	private String to;
	private String subject;
	private String message;
	private Date date;
	private String toFullName;
	private String workStation;
	
	public void setFrom(String from) {
		this.from = from;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setSender(String to) {
		this.to = to;
	}
	
	public void send() {
		start();
	}
	
	public void run() {
		String fulltext = 
			toFullName+" escribio desde "+workStation+":\n" +
			"--------------------------------------------\n"+
			message+ "\n"+
			"--------------------------------------------\n" +
			".";
		MailHandler.sendMessage(from, to, date, subject, fulltext);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setToFullName(String toFullName) {
		this.toFullName = toFullName;
	}

	public void setWorkStation(String workStation) {
		this.workStation = workStation;
	}
}
