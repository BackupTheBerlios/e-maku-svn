package com.kazak.smi.admin.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Arrays;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.gui.TreeManagerGroups;
import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;


public class Cache{
	
	private static Hashtable<String, Group> listGroups;
	
	public static void load() {
		listGroups = new Hashtable<String, Group>();
		Thread t = new  Thread() {
			
			public void run() {
				
				try {
					TreeManagerGroups.clearAll();
					//Grupos
					//Vector<String> keys = new Vector<String>();
					Document doc1 = QuerySender.getResultSetST("SEL0004",null);
					Element root = doc1.getRootElement();
					Iterator rows = root.getChildren("row").iterator();
					while (rows.hasNext()) {
						Element row = (Element) rows.next();
						Iterator cols = row.getChildren().iterator();
						
						String gid = ((Element)cols.next()).getValue();
						String name = ((Element)cols.next()).getValue();
						String visible = ((Element)cols.next()).getValue();
						String zone = ((Element)cols.next()).getValue();
						
						Group group = new Group();
						group.id = gid;
						group.name = name;
						group.visible = visible.equals("t") ? true : false;
						group.zone = zone.equals("t") ? true : false;
						//keys.add(name);
						//if (!listGroups.containsKey(name)) {
							listGroups.put(name,group);
							TreeManagerGroups.addGroup(name);
						//}
					}
					
					/*Set<String> gkeys = listGroups.keySet();
					Object[] arr = gkeys.toArray();
					for (int i=0 ; i < arr.length ; i++) {
						String key = arr[i].toString();
						if (!keys.contains(key)){
							listGroups.remove(key);
							TreeManagerGroups.removeNode(key);
						}
					}*/
					TreeManagerGroups.updateUI();
					
					//keys = new Vector<String>();
					Document doc2 = QuerySender.getResultSetST("SEL0005",null);
					
					//Puntos de Venta
					
					root = doc2.getRootElement();
					rows = root.getChildren("row").iterator();
					while (rows.hasNext()) {
						Element row = (Element) rows.next();
						Iterator cols = row.getChildren().iterator();
						
						String code = ((Element)cols.next()).getValue();
						String name = ((Element)cols.next()).getValue();
						String ip = ((Element)cols.next()).getValue();
						String gid = ((Element)cols.next()).getValue();
						String gidName = ((Element)cols.next()).getValue();
						
						WorkStation ws = new WorkStation();
						ws.gid = gid;
						ws.name = name;
						ws.ip = ip;
						ws.code = code;
						ws.gidname = gidName;
						//if (!listGroups.get(gidName).containsWS(ws.name)) {
							listGroups.get(gidName).add(ws);
							TreeManagerGroups.addChild(gidName,ws.name);
							//}
						//keys.add(name);
					}
					/*for (Group group : listGroups.values()) {
						arr = group.getWorkStationsKeys().toArray();
						for (int i=0 ; i < arr.length ; i++) {
							String key = arr[i].toString();
							if (!keys.contains(key)){
								group.workStations.remove(key);
								TreeManagerGroups.removeNode(key);
							}
						}
					}*/
					TreeManagerGroups.updateUI();
					//keys = new Vector<String>();
					//Usuarios - Funcionarios
					Document doc3 = QuerySender.getResultSetST("SEL0006",null);
					
					root = doc3.getRootElement();
					rows = root.getChildren("row").iterator();
					
					while (rows.hasNext()) {
						Element row = (Element) rows.next();
						Iterator cols = row.getChildren().iterator();
						User user = new User();
						user.id = ((Element)cols.next()).getValue();
						user.login = ((Element)cols.next()).getValue();
						user.name =  ((Element)cols.next()).getValue();
						user.email = ((Element)cols.next()).getValue();
						user.admin = ((Element)cols.next()).getValue().equals("t") ? true : false;
						user.audit = ((Element)cols.next()).getValue().equals("t") ? true : false;
						user.gid = ((Element)cols.next()).getValue();
						user.gidname = ((Element)cols.next()).getValue();
						//if (!listGroups.get(user.gidname).containsUser(user.login)) {
							listGroups.get(user.gidname).add(user);
							TreeManagerGroups.addChild(user.gidname,user.login);
							//}
						//keys.add(user.login);
					}
					
					/*for (Group group : listGroups.values()) {
						arr = group.users.keySet().toArray();
						for (int i=0 ; i < arr.length ; i++) {
							String key = arr[i].toString();
							if (!keys.contains(key)){
								group.users.remove(key);
								TreeManagerGroups.removeNode(key);
							}
						}
					}*/
					TreeManagerGroups.updateUI();
					// Usuarios - Puntos de Venta
					//keys = new Vector<String>();
					Document doc4 = QuerySender.getResultSetST("SEL0007",null);
					root = doc4.getRootElement();
					rows = root.getChildren("row").iterator();
					
					while (rows.hasNext()) {
						Element row = (Element) rows.next();
						Iterator cols = row.getChildren().iterator();
						User user = new User();
						user.id = ((Element)cols.next()).getValue();
						user.login = ((Element)cols.next()).getValue();
						user.name = ((Element)cols.next()).getValue();
						user.email = ((Element)cols.next()).getValue();
						user.admin = ((Element)cols.next()).getValue().equals("t") ? true : false;
						user.audit = ((Element)cols.next()).getValue().equals("t") ? true : false;
						user.gid = ((Element)cols.next()).getValue();
						String namepv = ((Element)cols.next()).getValue();
						user.validIp = ((Element)cols.next()).getValue().equals("t") ? true : false;
						user.gidname = ((Element)cols.next()).getValue();
						Group g = searchByWorkStation(namepv);
						//if (!g.getWs(namepv).containsUser(user.login)) {
							g.getWs(namepv).add(user);
							TreeManagerGroups.addChild(g.name,namepv,user.login);
							//}
						//keys.add(user.login);
					}
					/*for (Group group : listGroups.values()) {
						for (WorkStation ws : group.workStations.values()) {
							arr = ws.user.keySet().toArray();
							for (int i=0 ; i < arr.length ; i++) {
								String key = arr[i].toString();
								if (!keys.contains(key)){
									ws.user.remove(key);
									TreeManagerGroups.removeNode(key);
								}
							}	
						}
					}*/
					TreeManagerGroups.updateUI();
					System.gc();
					TreeManagerGroups.expand();
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	
	public static boolean containsGroup(String gname) {
		return listGroups.containsKey(gname);
	}
	
	private static Group searchByWorkStation(String code) {
		Collection<Group> list = listGroups.values();
		for (Group g: list) {
			if (g.containsWS(code)) {
				return g;
			}
		}
		return null;
	}
	
	public static boolean containsWs(String name) {
		Collection<Group> list = listGroups.values();
		for (Group g: list) {
			for (WorkStation ws : g.getWorkStations()) {
				if (ws.name.equals(name)) {
					return true;	
				}
			}
		}
		return false;
	}
	
	public static boolean containsWsByCode(String code) {
		Collection<Group> list = listGroups.values();
		for (Group g: list) {
			for (WorkStation ws : g.getWorkStations()) {
				if (ws.code.equals(code)) {
					return true;	
				}
			}
		}
		return false;
	}
	
	public static WorkStation getWorkStation(String name) {
		Collection<Group> list = listGroups.values();
		for (Group g: list) {
			if (g.containsWS(name)) {
				return g.getWs(name);
			}
		}
		return null;
	}
	
	public static Group getGroup(String key) {
		return listGroups.get(key);
	}
	
	public static class Group  {
		
		private String name;
		private String id;
		private Boolean zone;
		private Boolean visible;
		private Hashtable<String, WorkStation> workStations;
		private Hashtable<String, User> users;
		
		public Group() {
			workStations = new Hashtable<String, WorkStation>();
			users = new Hashtable<String, User>();
		}
		
		public void add(WorkStation ws) {
			workStations.put(ws.name,ws);
		}
		
		public void add(User us) {
			users.put(us.login,us);
		}
		
		public WorkStation getWs(String name) {
			return workStations.get(name);
		}
		
		public boolean containsWS(String name) {
			return workStations.containsKey(name);
		}
		
		public boolean containsUser(String code) {
			return users.containsKey(code);
		}
		
		public User getUser(String code) {
			return users.get(code);
		}
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Boolean getZone() {
			return zone;
		}
		public void setZone(Boolean zone) {
			this.zone = zone;
		}
		public Boolean getVisible() {
			return visible;
		}
		public void setVisible(Boolean visible) {
			this.visible = visible;
		}

		public Collection<WorkStation> getWorkStations() {
			return workStations.values();
		}
		
		public Set<String> getWorkStationsKeys() {
			return workStations.keySet();
		}
		
		public Collection<User> getUsers() {
			return users.values();
		}
	}
	
	public static class WorkStation {

		private String ip;
		private String code;
		private String gid;
		private String gidname;
		private String name;
		private Hashtable<String, User> user;
		
		public WorkStation() {
			user = new Hashtable<String, User>();
		}
		
		public void add(User us) {
			user.put(us.login,us);
		}
		public User getUser(String code) {
			return user.get(code);
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getGid() {
			return gid;
		}
		public void setGid(String gid) {
			this.gid = gid;
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
		
		public boolean containsUser(String code) {
			return user.containsKey(code);
		}
		
		public Collection<User> getUsers() {
			return user.values();
		}

		public String getGidName() {
			return gidname;
		}
	}
	
	public static class User {
		
		private String id;
		private String login;
		private String name;
		private String gid;
		private String email;
		private Boolean admin = false;
		private Boolean audit = false;
		private Boolean validIp = false;
		private String gidname;
		
		public Boolean getAdmin() {
			return admin;
		}
		
		public void setAdmin(Boolean admin) {
			this.admin = admin;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getGid() {
			return gid;
		}
		public void setGid(String gid) {
			this.gid = gid;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getLogin() {
			return login;
		}
		public void setLogin(String login) {
			this.login = login;
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
		public void setValidIp(Boolean validIp) {
			this.validIp = validIp;
		}

		public String getGidname() {
			return gidname;
		}

		public void setGidname(String gidname) {
			this.gidname = gidname;
		}

		public Boolean getAudit() {
			return audit;
		}

		public void setAudit(Boolean audit) {
			this.audit = audit;
		}
	}

	public static Collection<Group> getList() {
		return listGroups.values();
	}
	
	public static String[] getListKeys() {
		Set <String>bag = listGroups.keySet();
		String[] sortedGroupList = (String[])bag.toArray(new String[bag.size()]);
		Arrays.sort(sortedGroupList);
		return sortedGroupList;
	}
	
	public static Vector<String> getListKeysWs() {
		Vector<String> vkeys = new Vector<String>();
		for (Group g : listGroups.values()) {
			for (WorkStation ws : g.getWorkStations()) {
				vkeys.add(ws.getName());
			}
		}
		return vkeys;
	}
	
	public static User searchUser (String login) {
		Cache.User user = null;
		boolean withgroup = false;
		boolean withpv = false;
		for (Cache.Group grp : Cache.getList()) {
			withgroup = grp.containsUser(login);
			if (withgroup) {
				user = grp.getUser(login);
				break;
			}
			for (Cache.WorkStation ws : grp.getWorkStations()) {
				withpv = ws.containsUser(login);
				if (withpv) {
					user = ws.getUser(login);
					break;
				}
			}
			if (withpv) {
				break;
			}
		}
		return user;
	}
	
	public static class UserPVenta {
		private String codepv;
		private String name;
		private Boolean validip;
		
		public String getCodepv() {
			return codepv;
		}
		public Boolean getValidip() {
			return validip;
		}
		public String getName() {
			return name;
		}
	}
	
	public static ArrayList<UserPVenta> getLisWorksStationsForUser(String code) {
		ArrayList<UserPVenta> list = new ArrayList<UserPVenta>();
		for (Cache.Group grp : Cache.getList()) {
			for (Cache.WorkStation ws : grp.getWorkStations()) {
				if (ws.containsUser(code)) {
					UserPVenta upv = new UserPVenta();
					upv.codepv = ws.code;
					upv.name = ws.name;
					upv.validip= ws.getUser(code).getValidIp();
					list.add(upv);
				}
			}
		}
		return list;
	}


	public static boolean containsUser(String login) {
		User user = searchUser(login);
		return user!=null && user.login.equals(login) ? true :false;
	}


	public static void removeWs(String name) {
		
		Collection<Group> list = listGroups.values();
		for (Group g: list) {
			for (WorkStation ws : g.getWorkStations()) {
				if (ws.name.equals(name)) {
					g.workStations.remove(name);
				}
			}
		}
	}
	
	public static void removeUser(String login) {
		
		Collection<Group> list = listGroups.values();
		for (Group g: list) {
			for (User user : g.getUsers() ) {
				if (user.login.equals(login)) {
					g.users.remove(login);
				}
			}
		}
	}


	public static Vector<String> getListKeysUsers() {
		Vector<String> vkeys = new Vector<String>();
		for (Group g : listGroups.values()) {
			for (WorkStation ws : g.getWorkStations()) {
				for (String code : ws.user.keySet()) {
					vkeys.add(code);
				}
				
			}
			for (String code : g.users.keySet()) {
				vkeys.add(code);
			}
		}
		return vkeys;
	}
}