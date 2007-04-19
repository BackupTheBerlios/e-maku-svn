package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.kazak.smi.admin.Run;
import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.LogServerViewer;
import com.kazak.smi.admin.control.Cache.Group;
import com.kazak.smi.admin.control.Cache.User;
import com.kazak.smi.admin.control.Cache.WorkStation;
import com.kazak.smi.admin.gui.TreeManagerGroups.SortableTreeNode;
import com.kazak.smi.admin.models.PointSaleModel;
import com.kazak.smi.admin.models.TableSorter;
import com.kazak.smi.admin.models.UsersModel;

public class MainWindow implements ActionListener, TreeSelectionListener {
	
	private static JFrame frame;
	private JSplitPane splitPane;
	private DataGrid dataGrid;
	private JPanel rightPanel;
	private TreeManagerGroups treeManagerGroups;
	private static String appOwner;
	public MainWindow(String appOwner,String UserLevel) {
		MainWindow.appOwner = appOwner;
		frame = new JFrame("Administración SMI -" + appOwner);
		frame.setSize(800,600);
		frame.setJMenuBar(new MenuBar(UserLevel));
		frame.setLayout(new BorderLayout());
		frame.setLocationByPlatform(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Run.exit();
			}
		});
		
		if (UserLevel.equals("1")) {
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setDividerLocation(250);
			
			treeManagerGroups = new TreeManagerGroups();
			treeManagerGroups.addTreeSelectionListener(this);
			splitPane.setLeftComponent(treeManagerGroups.getContentPane());
			
			rightPanel = new JPanel(new BorderLayout());
			dataGrid = new DataGrid();
			rightPanel.add(dataGrid.getWithScroll(),BorderLayout.CENTER);

			splitPane.setRightComponent(rightPanel);
			frame.add(splitPane,BorderLayout.CENTER);	
		}
		if (UserLevel.equals("2")) {
			LogServerViewer.loadGUI();
			frame.add(LogServerViewer.getPanel(),BorderLayout.CENTER);
		}
		
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		
	}
	
	public static JFrame getFrame() {
		return frame;
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		TreePath tp = e.getPath();
		TreeManagerGroups.currTpath = e.getPath();
		DefaultMutableTreeNode node;
		//System.out.println("Camino: " + tp);
		TableSorter ts;
		boolean affect = false;
		switch (tp.getPathCount()) {
		case 2:
			node = (SortableTreeNode) tp.getPathComponent(1);
			Group grp = Cache.getGroup(node.toString());
			Collection<WorkStation> collectionws = grp.getWorkStations();
			Vector<WorkStation> vws = new Vector<WorkStation>(collectionws);
			if (vws.size() > 0 ) {
				ts = new TableSorter(new PointSaleModel(vws));
				dataGrid.setModel(ts);
				ts.setTableHeader(dataGrid.getTableHeader());
				affect = true;
			}
			Collection<User> collectionus = grp.getUsers();
			Vector<User> usr = new Vector<User>(collectionus);
			if (usr.size() > 0 ) {
				ts = new TableSorter(new UsersModel(usr));
				dataGrid.setModel(ts);
				ts.setTableHeader(dataGrid.getTableHeader());
				affect = true;
			}
			break;
		case 3:
			node = (SortableTreeNode) tp.getPathComponent(2);
			String name = node.toString();
			//System.out.println("Nodo: " + name);
			if (Cache.containsWs(name)){
				WorkStation ws = Cache.getWorkStation(name);
				Collection<User> coll = ws.getUsers();
				Vector<User> usrcoll = new Vector<User>(coll);
				if (usrcoll.size() > 0 ) {
					ts =new TableSorter(new UsersModel(usrcoll));
					dataGrid.setModel(ts);
					ts.setTableHeader(dataGrid.getTableHeader());
					affect = true;
				}
			}
			if (Cache.containsUser(name)){
				User user = Cache.searchUser(name);
				Vector<User> usrcoll = new Vector<User>();
				usrcoll.add(user);
				if (usrcoll.size() > 0 ) {
					ts =new TableSorter(new UsersModel(usrcoll));
					dataGrid.setModel(ts);
					ts.setTableHeader(dataGrid.getTableHeader());
					affect = true;
				}
			}
			break;
		case 4:
			node = (SortableTreeNode) tp.getPathComponent(3);
			String login = node.toString();
			User user = Cache.searchUser(login);
			if (user!=null) {
				Vector<User> usrcoll = new Vector<User>();
				usrcoll.add(user);
				if (usrcoll.size() > 0 ) {
					ts =new TableSorter(new UsersModel(usrcoll));
					dataGrid.setModel(ts);
					ts.setTableHeader(dataGrid.getTableHeader());
					affect = true;
				}
			}
			break;
		}
		if (!affect) {
			dataGrid.setModel(new DefaultTableModel());
		}
	}

	public static String getAppOwner() {
		return appOwner;
	}
}