package com.kazak.smi.admin.control;

import java.util.ArrayList;

public abstract class ICache{
	
	protected String id;
	protected String key;
	protected ArrayList<ICache> collection; 
	protected int index=0;
	
	public ArrayList<ICache> getCollection() {
		return collection; 
	}
	
	protected void setCollection(ArrayList<ICache> c) {
		this.collection = c;
	}

	protected boolean contains(String iD) {
		for (ICache obj : collection) {
			if (obj.getId().equals(iD)) {
				return true;
			}
		}
		return false;
	}
	
	protected void add(ICache iCache) {
		collection.add(iCache);
	}
	
	public ICache get(String ID) {
		for (ICache obj : collection) {
			if (obj.getId().equals(ID)) {
				return obj;
			}
		}
		return null;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public int size() {
		return collection.size();
	}
}
