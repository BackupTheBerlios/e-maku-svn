package com.kazak.comeet.admin.control;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collection;
//import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Arrays;

import javax.swing.JFrame;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.admin.gui.main.MainTreeManager;
import com.kazak.comeet.admin.transactions.QuerySender;
import com.kazak.comeet.admin.transactions.QuerySenderException;

public class Cache {
	
	private static Hashtable<String, Group> groupsList;
	private static JFrame frame;
	private static int VISUAL = 1;
	
	// This method updates the groups,pos and users jtree
	
	public static synchronized void loadInfoTree(final int mode) {
		groupsList = new Hashtable<String, Group>();
		Thread t = new  Thread() {
		
			public void run() {
				try {
					int typeCursor;
	
					if(mode == VISUAL) {
						typeCursor = Cursor.WAIT_CURSOR;
						Cursor cursor = Cursor.getPredefinedCursor(typeCursor);
						frame.setCursor(cursor);
						MainTreeManager.clearAll();
					}

					//Groups
					Document doc = QuerySender.getResultSetFromST("SEL0004",null);
					Element root = doc.getRootElement();
					Iterator rows = root.getChildren("row").iterator();
					
					//Loading jtree with groups
					while (rows.hasNext()) {
						String group = addGroupItem(rows);
						if(mode == VISUAL) {
							MainTreeManager.addGroup(group);
						}
					}
					
					doc = QuerySender.getResultSetFromST("SEL0005",null);
					
					//POS
					root = doc.getRootElement();
					rows = root.getChildren("row").iterator();
					
					//Loading jtree with workstations
					while (rows.hasNext()) {
						String[] data = addWsItem(rows); // Group name and ws name
						if(mode == VISUAL) {
							MainTreeManager.addChild(data[0],data[1]);
						}
					}

					//Users - Admin 
					doc = QuerySender.getResultSetFromST("SEL0006",null);
					root = doc.getRootElement();
					rows = root.getChildren("row").iterator();
					
					//Loading jtree with users (admin)
					while (rows.hasNext()) {						
						String[] data = addAdminUserItem(rows); // Group name and user name
						if(mode == VISUAL) {
							MainTreeManager.addChild(data[0],data[1]);
						}
					}

					//Users - POS
					doc = QuerySender.getResultSetFromST("SEL0007",null);
					root = doc.getRootElement();
					rows = root.getChildren("row").iterator();
					
					//Loading jtree with users (pos)
					while (rows.hasNext()) {
						Object[] data = addPOSUserItem(rows); // Workstation name and User object 		
						if(mode == VISUAL) {
							Group group = getGroupByWorkStation(data[0].toString());
							group.getWs(data[0].toString()).add((User)data[1]);
							MainTreeManager.addChild(group.name,data[0].toString(),((User)data[1]).login);
						}
					}

					if(mode == VISUAL) {
						MainTreeManager.updateUI();
						System.gc();
						MainTreeManager.expand();
						frame.setCursor(Cursor.getDefaultCursor());
					}
					
				} catch (QuerySenderException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	public static String addGroupItem(Iterator groupIterator) {
		Element row = (Element) groupIterator.next();
		Iterator columns = row.getChildren().iterator();

		String gid = ((Element)columns.next()).getValue();
		String name = ((Element)columns.next()).getValue();
		String visible = ((Element)columns.next()).getValue();
		String zone = ((Element)columns.next()).getValue();

		Group group = new Group();
		group.id = gid;
		group.name = name;
		group.isVisible = visible.equals("t") ? true : false;
		group.zone = zone.equals("t") ? true : false;
		groupsList.put(name,group);
		
		return name;
	}
	 
	public static String[] addWsItem(Iterator wsIterator) {
		String[] result = new String[2];
		Element row = (Element) wsIterator.next();
		Iterator columns = row.getChildren().iterator();

		String code = ((Element)columns.next()).getValue();		
		String name = ((Element)columns.next()).getValue();						
		String ip = ((Element)columns.next()).getValue();
		String gid = ((Element)columns.next()).getValue();
		String groupName = ((Element)columns.next()).getValue();
		
		WorkStation ws = new WorkStation();
		ws.gid = gid;
		ws.name = name;
		ws.ip = ip;
		ws.code = code;
		ws.groupName = groupName;
		groupsList.get(groupName).add(ws);
		
		result[0] = groupName;
		result[1] = name;
		
		return result;
	}

	public static String[] addAdminUserItem(Iterator userIterator) {
		String[] result = new String[2];
		Element row = (Element) userIterator.next();
		Iterator columns = row.getChildren().iterator();
		User user = new User();
		user.setId(((Element)columns.next()).getValue());
		user.setLogin(((Element)columns.next()).getValue());
		user.setPasswd(((Element)columns.next()).getValue());
		user.setName(((Element)columns.next()).getValue());
		user.setEmail(((Element)columns.next()).getValue());
		user.setAdmin(((Element)columns.next()).getValue().equals("t") ? true : false);
		user.setAudit(((Element)columns.next()).getValue().equals("t") ? true : false);
		user.setGid(((Element)columns.next()).getValue());
		user.setGroupName(((Element)columns.next()).getValue());
		user.setValidIp(false);
		user.setSeller(false);
		groupsList.get(user.groupName).add(user);

		result[0] = user.groupName;
		result[1] = user.login;
			
		return result;
	}
	
	public static Object[] addPOSUserItem(Iterator rows) {
		Object[] result = new Object[2];
		
		Element row = (Element) rows.next();
		Iterator columns = row.getChildren().iterator();
		User user = new User();
		
		user.setId(((Element)columns.next()).getValue());
		user.setLogin(((Element)columns.next()).getValue());
		user.setPasswd(((Element)columns.next()).getValue());
		user.setName(((Element)columns.next()).getValue());
		user.setEmail(((Element)columns.next()).getValue());
		user.setAdmin(((Element)columns.next()).getValue().equals("t") ? true : false);
		user.setAudit(((Element)columns.next()).getValue().equals("t") ? true : false);
		user.setGid(((Element)columns.next()).getValue());
		String wsName = ((Element)columns.next()).getValue();
		user.setValidIp(((Element)columns.next()).getValue().equals("t") ? true : false);
		user.setGroupName(((Element)columns.next()).getValue());
		user.setSeller(true);
		groupsList.get(user.groupName).add(user);
		
		result[0] = wsName;
		result[1] = user;
		
		return result;
	}
	
	public static boolean containsGroup(String groupName) {
		return groupsList.containsKey(groupName);
	}
	
	private static Group getGroupByWorkStation(String wsCode) {
		Collection<Group> list = groupsList.values();
		for (Group group: list) {
			if (group.containsWS(wsCode)) {
				return group;
			}
		}
		return null;
	}
	
	public static boolean containsWs(String wsName) {
		Collection<Group> list = groupsList.values();
		for (Group group: list) {
			for (WorkStation ws : group.getWorkStations()) {
				if (ws.name.equals(wsName)) {
					return true;	
				}
			}
		}
		return false;
	}
	
	public static boolean containsWsByCode(String wsCode) {
		Collection<Group> list = groupsList.values();
		for (Group group: list) {
			for (WorkStation ws : group.getWorkStations()) {
				if (ws.code.equals(wsCode)) {
					return true;	
				}
			}
		}
		return false;
	}
	
	public static WorkStation getWorkStation(String wsName) {
		Collection<Group> list = groupsList.values();
		for (Group group: list) {
			if (group.containsWS(wsName)) {
				return group.getWs(wsName);
			}
		}
		return null;
	}
	
	public static Group getGroup(String groupID) {
		return groupsList.get(groupID);
	}
	
	public static void setFrame(JFrame mainFrame) {
		frame = mainFrame;
	}	
	
	public static Collection<Group> getList() {
		return groupsList.values();
	}
	
	public static String[] getGroupsList() {
		Set <String>bag = groupsList.keySet();
		String[] sortedGroupList = (String[])bag.toArray(new String[bag.size()]);
		Arrays.sort(sortedGroupList);
		
		return sortedGroupList;
	}	

	public static HashMap<String,String> getGroupsHash() {
		Object[] objectArray = Cache.getList().toArray();
		HashMap <String,String>groupsHash = new HashMap<String,String>();
		for (Object infoGroup:objectArray) {
			Group group = (Group)infoGroup;
			groupsHash.put(group.getName(), group.getId());
		}

		return groupsHash;
	}
	
	public static String[] getWorkStationsList() {
		String[] wsNames = {};
		Vector<String> workstations = new Vector<String>();
		for (Group group : groupsList.values()) {
			for (WorkStation ws : group.getWorkStations()) {
				workstations.add(ws.getName());
			}
		}		
		wsNames = new String[workstations.size()];
		for(int i=0;i<workstations.size();i++)  {
			wsNames[i] = workstations.get(i);
		}		
		Arrays.sort(wsNames);
		
		return wsNames;
	}
	
	public static User getUser (String login) {
		Cache.User user = null;
		boolean withGroup = false;
		boolean withWs = false;
		for (Cache.Group group : Cache.getList()) {
			withGroup = group.containsUser(login);
			if (withGroup) {
				user = group.getUser(login);
				break;
			}
			for (Cache.WorkStation ws : group.getWorkStations()) {
				withWs = ws.containsUser(login);
				if (withWs) {
					user = ws.getUser(login);
					break;
				}
			}
			if (withWs) {
				break;
			}
		}
		return user;
	}
		
	public static ArrayList<POS> getWorkStationsListByUser(String code) {
		ArrayList<POS> list = new ArrayList<POS>();
		for (Cache.Group group : Cache.getList()) {
			for (Cache.WorkStation ws : group.getWorkStations()) {
				if (ws.containsUser(code)) {
					POS upos = new POS();
					upos.posCode = ws.code;
					upos.name    = ws.name;
					upos.validIP = ws.getUser(code).getValidIp();
					list.add(upos);
				}
			}
		}
		return list;
	}

	public static boolean containsUser(String login) {
		User user = getUser(login);
		return user!=null && user.login.equals(login) ? true :false;
	}

	public static void removeWs(String wsName) {
		
		Collection<Group> list = groupsList.values();
		for (Group group: list) {
			for (WorkStation ws : group.getWorkStations()) {
				if (ws.name.equals(wsName)) {
					group.workStationsHash.remove(wsName);
				}
			}
		}
	}
	
	public static void removeUser(String login) {
		Collection<Group> list = groupsList.values();
		for (Group group: list) {
			for (User user : group.getUsers() ) {
				if (user.login.equals(login)) {
					group.usersHash.remove(login);
				}
			}
		}
	}

	public static String[] getUsersList() {
		Vector<String> users = new Vector<String>();
		String[] usersArray = {};
		for (Group group : groupsList.values()) {
			for (String code : group.getUsersSet()) {
				users.add(code);
			}
		}		
		usersArray = new String[users.size()];
		for(int i=0;i<users.size();i++)  {
			usersArray[i] = users.get(i);
		}		
		Arrays.sort(usersArray);
		
		return usersArray;
	}
	
	public static class POS {
		
		private String posCode;
		private String name;
		private Boolean validIP;
		
		public String getPOSCode() {
			return posCode;
		}
		public Boolean getValidIP() {
			return validIP;
		}
		public String getName() {
			return name;
		}
		
	}
	
	public static class User {
		
		private String id;
		private String login;
		private String name;
		private String gid;
		private String email;
		private String passwd;
		private Boolean admin = false;
		private Boolean audit = false;
		private Boolean validIp = false;
		private String groupName;
		private Boolean seller=false;
		
		public Boolean getAdmin() {
			return admin;
		}
		
		public void setAdmin(Boolean admin) {
			this.admin = admin;
		}
		
		public Boolean isSeller() {
			return seller;
		}
		
		public void setSeller(Boolean seller) {
			this.seller = seller;
		}
		
		public String getEmail() {
			return email;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
		
		public String getPasswd() {
			return passwd;
		}
		
		public void setPasswd(String passwd) {
			this.passwd = passwd;
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

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public Boolean getAudit() {
			return audit;
		}

		public void setAudit(Boolean audit) {
			this.audit = audit;
		}
	}
	
	public static class WorkStation {

		private String ip;
		private String code;
		private String gid;
		private String groupName;
		private String name;
		private Hashtable<String, User> userHash;
		
		public WorkStation() {
			userHash = new Hashtable<String, User>();
		}
		
		public void add(User user) {
			userHash.put(user.login,user);
		}
		
		public User getUser(String code) {
			return userHash.get(code);
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
			return userHash.containsKey(code);
		}
		
		public Collection<User> getUsers() {
			return userHash.values();
		}

		public String getGroupName() {
			return groupName;
		}
	}	

	public static class Group {
		
		private String name;
		private String id;
		private Boolean zone;
		private Boolean isVisible;
		private Hashtable<String, WorkStation> workStationsHash;
		private Hashtable<String, User> usersHash;
		
		public Group() {
			workStationsHash = new Hashtable<String, WorkStation>();
			usersHash = new Hashtable<String, User>();
		}
		
		public void add(WorkStation ws) {
			workStationsHash.put(ws.name,ws);
		}
		
		public void add(User user) {
			usersHash.put(user.login,user);
		}
		
		public WorkStation getWs(String name) {
			return workStationsHash.get(name);
		}
		
		public boolean containsWS(String name) {
			return workStationsHash.containsKey(name);
		}
		
		public boolean containsUser(String code) {
			return usersHash.containsKey(code);
		}
		
		public User getUser(String code) {
			return usersHash.get(code);
		}
		
		public Set<String> getUsersSet() {
			return usersHash.keySet();
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
		
		public Boolean isZone() {
			return zone;
		}
		
		public void setZone(Boolean zone) {
			this.zone = zone;
		}
		
		public Boolean isVisible() {
			return isVisible;
		}
		
		public void setVisible(Boolean visible) {
			this.isVisible = visible;
		}

		public Collection<WorkStation> getWorkStations() {
			return workStationsHash.values();
		}
		
		public String[] getWorkStationsKeys() {
			Set <String>bag = workStationsHash.keySet();
			String[] array = (String[])bag.toArray(new String[bag.size()]);
			Arrays.sort(array);
			return array;
		}
		
		public Collection<User> getUsers() {
			return usersHash.values();
		}
	}

}