package net.emaku.tools.structures;

public class FormsData {
	String name;
	String description;
	String driver;
	String argsDriver;
	String method;
	String argsMethod;
	String profile;
	
	public FormsData() {
	}
	
	public FormsData(String name,String description,String driver,String argsDriver,String method,String argsMethod,String profile) {
		this.name = name;
		this.description = description;
		this.driver = driver;
		this.argsDriver = argsDriver;
		this.method = method;
		this.argsMethod = argsMethod;
		this.profile = profile;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getDriver() {
		return driver;
	}

	public String getDriverArgs() {
		return argsDriver;
	}
	
	public String getMethod() {
		return method;
	}
	
	public String getMethodArgs() {
		return argsMethod;
	}

	public String getProfile() {
		return profile;
	}
}
