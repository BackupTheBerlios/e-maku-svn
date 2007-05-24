package com.kazak.smi.admin.gui.main;

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
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.kazak.smi.admin.Run;
import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.control.LogServerViewer;
import com.kazak.smi.admin.control.Cache.Group;
import com.kazak.smi.admin.control.Cache.User;
import com.kazak.smi.admin.control.Cache.WorkStation;
import com.kazak.smi.admin.gui.main.TreeManagerGroups.SortableTreeNode;
import com.kazak.smi.admin.gui.table.models.PosModel;
import com.kazak.smi.admin.gui.table.models.TableSorter;
import com.kazak.smi.admin.gui.table.models.UsersModel;
import com.kazak.smi.admin.gui.table.DataGrid;

public class MainWindow implements TreeSelectionListener {
	
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
			
			treeManagerGroups = new TreeManagerGroups(frame);
			treeManagerGroups.addTreeSelectionListener(this);
			splitPane.setLeftComponent(treeManagerGroups.getContentPane());
			
			rightPanel = new JPanel(new BorderLayout());
			dataGrid = new DataGrid();
			rightPanel.add(dataGrid.getScrollPane(),BorderLayout.CENTER);

			splitPane.setRightComponent(rightPanel);
			frame.add(splitPane,BorderLayout.CENTER);	
		}
		if (UserLevel.equals("2")) {
			Cache.loadInfoTree(0);
			LogServerViewer.loadGUI();
			frame.add(LogServerViewer.getPanel(),BorderLayout.CENTER);
		}
		
		UIManager.put("ComboBox.disabledForeground",Color.BLACK);
		
		frame.setVisible(true);
	}
	
	public static JFrame getFrame() {
		return frame;
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		TreePath treePath = e.getPath();		
		TreeManagerGroups.currentTreePath = e.getPath();
		DefaultMutableTreeNode node;
		TableSorter tableSorter;
		boolean affect = false;

		switch (treePath.getPathCount()) {
			case 2:
				// Seleccionando grupo
				node = (SortableTreeNode) treePath.getPathComponent(1);
				Group group = Cache.getGroup(node.toString());			
				if(group != null) {				
					Collection<WorkStation> wsCollection = group.getWorkStations();
					Vector<WorkStation> wsVector = new Vector<WorkStation>(wsCollection);
					if (wsVector.size() > 0 ) {
						tableSorter = new TableSorter(new PosModel(wsVector));
						tableSorter.setSortingStatus(1,TableSorter.ASCENDING);
						dataGrid.setModel(tableSorter);
						tableSorter.setTableHeader(dataGrid.getTableHeader());
						TableColumnModel columnModel = dataGrid.getColumnModel();
						int n = tableSorter.getColumnCount(); 
						int columnWidth[] = {50,300,100};
						for (int i=0;i<n;i++) {
							columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
						}
						affect = true;
					}
					Collection<User> collectionus = group.getUsers();
					Vector<User> users = new Vector<User>(collectionus);
					if (users.size() > 0 ) {
						tableSorter = new TableSorter(new UsersModel(users));
						tableSorter.setSortingStatus(1,TableSorter.ASCENDING);
						dataGrid.setModel(tableSorter);
						tableSorter.setTableHeader(dataGrid.getTableHeader());
						TableColumnModel columnModel = dataGrid.getColumnModel();
						int n = tableSorter.getColumnCount(); 
						int columnWidth[] = {80,80,200,200,120};
						for (int i=0;i<n;i++) {
							columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
						}
						affect = true;
					}
				}
				break;
			case 3:
				// Seleccionando punto de venta
				node = (SortableTreeNode) treePath.getPathComponent(2);
				String name = node.toString();
				if (Cache.containsWs(name)){
					WorkStation ws = Cache.getWorkStation(name);
					Collection<User> collection = ws.getUsers();
					Vector<User> usersVector = new Vector<User>(collection);
					if (usersVector.size() > 0 ) {
						tableSorter = new TableSorter(new UsersModel(usersVector));
		                tableSorter.setSortingStatus(1,TableSorter.ASCENDING);
						dataGrid.setModel(tableSorter);
						tableSorter.setTableHeader(dataGrid.getTableHeader());
					    TableColumnModel columnModel = dataGrid.getColumnModel();
					    int n = tableSorter.getColumnCount(); 
						int columnWidth[] = {80,80,200,200,120};
					    for (int i=0;i<n;i++) {
					      columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
					    }
						affect = true;
					}
				}
				
				if (Cache.containsUser(name)){
					User user = Cache.getUser(name);
					if (user != null) {
						Vector<User> usersVector = new Vector<User>();
						usersVector.add(user);
						tableSorter = new TableSorter(new UsersModel(usersVector));
		                tableSorter.setSortingStatus(1,TableSorter.ASCENDING);
						dataGrid.setModel(tableSorter);
						tableSorter.setTableHeader(dataGrid.getTableHeader());
					    TableColumnModel columnModel = dataGrid.getColumnModel();
					    int n = tableSorter.getColumnCount(); 
						int columnWidth[] = {80,80,200,200,120};
					    for (int i=0;i<n;i++) {
					      columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
					    }
						affect = true;
					}
				}
				break;
			case 4:
				// Seleccionando usuario
				node = (SortableTreeNode) treePath.getPathComponent(3);
				String login = node.toString();
				User user = Cache.getUser(login);
				if (user!=null) {
					Vector<User> usersVector = new Vector<User>();
					usersVector.add(user);
					tableSorter = new TableSorter(new UsersModel(usersVector));
	                tableSorter.setSortingStatus(1,TableSorter.ASCENDING);
					dataGrid.setModel(tableSorter);
					tableSorter.setTableHeader(dataGrid.getTableHeader());
				    TableColumnModel columnModel = dataGrid.getColumnModel();
				    int n = tableSorter.getColumnCount(); 
					int columnWidth[] = {80,80,200,200,120};
				    for (int i=0;i<n;i++) {
				      columnModel.getColumn(i).setPreferredWidth(columnWidth[i]);
				    }
					affect = true;
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