package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
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

public class MainWindow implements TreeSelectionListener { // ActionListener
	
	private static JFrame frame;
	private JSplitPane splitPane;
	private DataGrid dataGrid;
	private JPanel rightPanel;
	private TreeManagerGroups treeManagerGroups;
	private static String appOwner;

	public MainWindow(String appOwner,String UserLevel) {
		MainWindow.appOwner = appOwner;
		frame = new JFrame("Administración SMI - " + appOwner);
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
		
		Cache.setFrame(frame);

		if (UserLevel.equals("1")) {
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setDividerLocation(250);
			
			treeManagerGroups = new TreeManagerGroups();
			treeManagerGroups.addTreeSelectionListener(this);
			splitPane.setLeftComponent(treeManagerGroups.getContentPane());
			
			rightPanel = new JPanel(new BorderLayout());
			dataGrid = new DataGrid();
			rightPanel.add(dataGrid.getScrollPane(),BorderLayout.CENTER);

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

	/*
	public void actionPerformed(ActionEvent e) {
		
	}*/
	
	public static JFrame getFrame() {
		return frame;
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		TreePath treePath = e.getPath();
		TreeManagerGroups.currTpath = e.getPath();
		DefaultMutableTreeNode node;
		//System.out.println("Camino: " + tp);
		TableSorter tableSorter;
		boolean affect = false;
		switch (treePath.getPathCount()) {
		case 2:
			System.out.println("Seleccionando grupo...");
			node = (SortableTreeNode) treePath.getPathComponent(1);
			Group group = Cache.getGroup(node.toString());
			Collection<WorkStation> wsCollection = group.getWorkStations();
			
			// TODO: To order this vector
			Vector<WorkStation> wsVector = new Vector<WorkStation>(wsCollection);
			
			if (wsVector.size() > 0 ) {
				tableSorter = new TableSorter(new PointSaleModel(wsVector));
				dataGrid.setModel(tableSorter);
				tableSorter.setTableHeader(dataGrid.getTableHeader());
				affect = true;
			}
			Collection<User> collectionus = group.getUsers();
			Vector<User> usr = new Vector<User>(collectionus);
			if (usr.size() > 0 ) {
				tableSorter = new TableSorter(new UsersModel(usr));
				dataGrid.setModel(tableSorter);
				tableSorter.setTableHeader(dataGrid.getTableHeader());
				affect = true;
			}
			break;
		case 3:
			System.out.println("Seleccionando punto de venta...");
			node = (SortableTreeNode) treePath.getPathComponent(2);
			String name = node.toString();
			//System.out.println("Nodo: " + name);
			if (Cache.containsWs(name)){
				WorkStation ws = Cache.getWorkStation(name);
				Collection<User> coll = ws.getUsers();
				Vector<User> usersVector = new Vector<User>(coll);
				if (usersVector.size() > 0 ) {
					tableSorter = new TableSorter(new UsersModel(usersVector));
					dataGrid.setModel(tableSorter);
					tableSorter.setTableHeader(dataGrid.getTableHeader());
					affect = true;
				}
			}
			if (Cache.containsUser(name)){
				User user = Cache.getUser(name);
				if (user != null) {
					Vector<User> usersVector = new Vector<User>();
					usersVector.add(user);
					//if (usrcoll.size() > 0) {
					tableSorter = new TableSorter(new UsersModel(usersVector));
					dataGrid.setModel(tableSorter);
					tableSorter.setTableHeader(dataGrid.getTableHeader());
					affect = true;
				}
			}
			break;
		case 4:
			System.out.println("Seleccionando usuario...");
			node = (SortableTreeNode) treePath.getPathComponent(3);
			String login = node.toString();
			User user = Cache.getUser(login);
			if (user!=null) {
				Vector<User> usersVector = new Vector<User>();
				usersVector.add(user);
				//if (usrcoll.size() > 0 ) {
				tableSorter = new TableSorter(new UsersModel(usersVector));
				dataGrid.setModel(tableSorter);
				tableSorter.setTableHeader(dataGrid.getTableHeader());
				affect = true;
				//}
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