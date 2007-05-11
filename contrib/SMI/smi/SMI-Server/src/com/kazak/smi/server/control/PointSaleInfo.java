package com.kazak.smi.server.control;

public class PointSaleInfo {
	private String 	ip;
	private String 	code;
	private String 	name;
	private String 	groupname;
	private String 	groupid;
	private Boolean validIp;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getValidIp() {
		return validIp;
	}
	public void setValidIp(Boolean valid_ip) {
		this.validIp = valid_ip;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public String getGroupid() {
		return groupid;
	}
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
}
