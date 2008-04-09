package net.emaku.tools.structures;

public class QueriesData {
	String name;
	String description;
	String sentence;
	
	public QueriesData() {
	}
	
	public QueriesData(String name,String description,String sentence) {
		this.name = name;
		this.description = description;
		this.sentence = sentence;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getSQL() {
		return sentence;
	}
}
