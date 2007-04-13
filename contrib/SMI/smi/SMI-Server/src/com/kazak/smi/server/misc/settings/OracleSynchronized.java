package com.kazak.smi.server.misc.settings;


public class OracleSynchronized {
    private int    hour;
    private int    minute;
    private int    second;
    private int    timeAlifeMessageInDataBase;
    private int    maxMessagesDataBase;
	
	public int getHour() {
		return hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}
	
	public int getMaxMessagesDataBase() {
		return maxMessagesDataBase;
	}
	
	public void setMaxMessagesDataBase(int maxMessagesDataBase) {
		this.maxMessagesDataBase = maxMessagesDataBase;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	public int getSecond() {
		return second;
	}
	
	public void setSecond(int second) {
		this.second = second;
	}
	
	public int getTimeAlifeMessageInDataBase() {
		return timeAlifeMessageInDataBase;
	}
	
	public void setTimeAlifeMessageInDataBase(int timeAlifeMessageInDataBase) {
		this.timeAlifeMessageInDataBase = timeAlifeMessageInDataBase;
	}

}
