package com.kazak.smi.admin.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.kazak.smi.admin.control.Cache;

public class TreeManagerGroups {
	
	private static SortableTreeNode rootNode;
	private JScrollPane jscroll;
	private static JTree tree;
	private JPopupMenu groupsMenu = new JPopupMenu();
	private JPopupMenu wsMenu = new JPopupMenu();
	private JPopupMenu usersMenu = new JPopupMenu();
	private Actions actions;
	private ArrayList<String> lastPath = new ArrayList<String>();
	public static TreePath currentTreePath;
	public JFrame frame;
		
	public TreeManagerGroups(JFrame frame) {
		this.frame = frame;
		rootNode = new SortableTreeNode(MainWindow.getAppOwner());
		rootNode.setAllowsChildren(true);
		tree = new JTree(rootNode);
		loadPopups();
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		renderer.setIcon(null);
		renderer.setRequestFocusEnabled(true);
		renderer.setAutoscrolls(true);
		
		tree.setCellRenderer(renderer);
		tree.setExpandsSelectedPaths(true);
		tree.setShowsRootHandles(true);
		tree.setAutoscrolls(true);
		tree.setScrollsOnExpand(true);
		tree.setDragEnabled(true);
		
		tree.addMouseListener(new MouseAdapter() {
			
			public void mousePressed(MouseEvent e) {
                if ( e.getButton() == MouseEvent.BUTTON3) {
                	currentTreePath = tree.getPathForLocation(e.getX(), e.getY());
                	tree.setSelectionPath(currentTreePath);
                	int count = currentTreePath.getPathCount();
                	SortableTreeNode node;
                	switch (count) {
                	case 2: // Puntos de Venta
                		lastPath.clear();
    	                groupsMenu.show(e.getComponent(), e.getX(), e.getY());
            			break;
            		case 3: // Usuarios 
            			node = (SortableTreeNode) currentTreePath.getPathComponent(2);
            			String name = node.toString();
            			if (Cache.containsWs(name)){
            				wsMenu.show(e.getComponent(), e.getX(), e.getY());
            			}
            			if (Cache.containsUser(name)){
            				usersMenu.show(e.getComponent(), e.getX(), e.getY());
            			}
            			break;
            		case 4:
            			usersMenu.show(e.getComponent(), e.getX(), e.getY());
            			break;
                	}
                }
			}
		});
		
		int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
		tree.getSelectionModel().setSelectionMode(mode);
		jscroll = new JScrollPane(tree);
		Cache.loadInfoTree(1);
	}
	
	public void loadPopups() {
		actions = new Actions();
		
		//--- Popup Grupos
		groupsMenu.add(new JLabel("Grupos"));
		groupsMenu.add(new JSeparator());
		JMenuItem item = new JMenuItem("Nuevo");
        item.setActionCommand("new_group");
        item.addActionListener(actions);
        groupsMenu.add(item);
        
        item = new JMenuItem("Editar");
        item.setActionCommand("edit_group");
        item.addActionListener(actions);
        groupsMenu.add(item);
        
        item = new JMenuItem("Eliminar");
        item.setActionCommand("delete_group");
        item.addActionListener(actions);
        groupsMenu.add(item);
        
        item = new JMenuItem("Buscar");
        item.setActionCommand("search_group");
        item.addActionListener(actions);
        groupsMenu.add(item);
        
        //--- Popup Puntos Venta
        wsMenu.add(new JLabel("Puntos"));
		wsMenu.add(new JSeparator());
        item = new JMenuItem("Nuevo");
        item.setActionCommand("new_point");
        item.addActionListener(actions);
        wsMenu.add(item);
        
        item = new JMenuItem("Editar");
        item.setActionCommand("edit_point");
        item.addActionListener(actions);
        wsMenu.add(item);
        
        item = new JMenuItem("Eliminar");
        item.setActionCommand("delete_point");
        item.addActionListener(actions);
        wsMenu.add(item);
        
        item = new JMenuItem("Buscar");
        item.setActionCommand("search_point");
        item.addActionListener(actions);
        wsMenu.add(item);
        
        //--- Popup Usuarios
        usersMenu.add(new JLabel("Usuarios"));
		usersMenu.add(new JSeparator());
        item = new JMenuItem("Nuevo");
        item.setActionCommand("new_user");
        item.addActionListener(actions);
        usersMenu.add(item);
        
        item = new JMenuItem("Editar");
        item.setActionCommand("edit_user");
        item.addActionListener(actions);
        usersMenu.add(item);
        
        item = new JMenuItem("Eliminar");
        item.setActionCommand("delete_user");
        item.addActionListener(actions);
        usersMenu.add(item);
        
        item = new JMenuItem("Buscar");
        item.setActionCommand("search_user");
        item.addActionListener(actions);
        usersMenu.add(item);
	}
		
	public void addMouseListener(MouseListener l) {
		tree.addMouseListener(l);
	}
	
	public void addTreeSelectionListener(TreeSelectionListener l) {
		tree.addTreeSelectionListener(l);
	}
	
	public Component getContentPane ()  {
		return jscroll;
	}
	
	public static void removeNode(String name) {
		for (int i =0 ; i < rootNode.getChildCount() ;  i++) {
			SortableTreeNode node = (SortableTreeNode) rootNode.getChildAt(i);
			if (name.equals(node.toString())) {
				rootNode.remove((SortableTreeNode)node);
				return;
			}
			for (int j=0 ; j < node.getChildCount() ; j++)  {
				SortableTreeNode internalNode = (SortableTreeNode) node.getChildAt(j);
				if (name.equals(internalNode.toString())) {
					node.remove((SortableTreeNode)internalNode);
					return;
				}
				for (int k=0 ; k < internalNode.getChildCount() ; k++)  {
					SortableTreeNode lastnode = (SortableTreeNode) internalNode.getChildAt(k);
					if (name.equals(lastnode.toString())) {
						internalNode.remove((SortableTreeNode)lastnode);
						return;
					}
				}
			}
		}		
	}
	
	public static SortableTreeNode getNode(String name) {
		for (int i =0 ; i < rootNode.getChildCount() ;  i++) {
			SortableTreeNode node = (SortableTreeNode) rootNode.getChildAt(i);
			if (name.equals(node.toString())) {
				return node;
			}
			for (int j=0 ; j < node.getChildCount() ; j++)  {
				SortableTreeNode internalNode = (SortableTreeNode) node.getChildAt(j);
				if (name.equals(internalNode.toString())) {
					return internalNode;
				}
				for (int k=0 ; k < internalNode.getChildCount() ; k++)  {
					SortableTreeNode lastnode = (SortableTreeNode) internalNode.getChildAt(k);
					if (name.equals(lastnode.toString())) {
						return internalNode;
					}
				}
			}
		}
		return null;
	}
		
	public static synchronized void addGroup(String name) {
		SortableTreeNode treeNode = new SortableTreeNode(name);
		rootNode.add(treeNode);
	}
	
	//Metodo para añadir puntos de venta y funcionarios al arbol
	public static synchronized void addChild(String user,String ws) {
		SortableTreeNode node = new SortableTreeNode(ws);
		int nodes = rootNode.getChildCount();
		SortableTreeNode treeNode = null;
		for (int i=0; i < nodes ; i++) {
			treeNode = (SortableTreeNode) rootNode.getChildAt(i);
			if (treeNode.toString().equals(user)) {
				treeNode.add(node);
				break;
			}
		}
	} 
	
	public static TreePath getSelectedPath() {
		return tree.getSelectionPath();
	}
	
	public static int getSelectedPathCount() {
		return tree.getSelectionPath().getPathCount();
	}
	
	public static synchronized void clearAll() {
		Thread t = new Thread() {
			public void run() {
				rootNode.removeAllChildren();
			}
		};
		SwingUtilities.invokeLater(t);
	}
	
	public static synchronized void updateUI() {
		Thread t = new Thread() {
			public void run() {
				tree.updateUI();
			}
		};
		SwingUtilities.invokeLater(t);
	}

	public static synchronized void addChild(String groupName, String pos, String login) {
		SortableTreeNode sortableTreeNode = new SortableTreeNode(login);
		int nodes = rootNode.getChildCount();
		SortableTreeNode treeNode = null;
		for (int i=0; i < nodes ; i++) {
			treeNode = (SortableTreeNode) rootNode.getChildAt(i);
			if (treeNode.toString().equals(groupName)) {
				int ws = treeNode.getChildCount();
				for (int j=0; j < ws ; j++) {
					SortableTreeNode posTreeNode = (SortableTreeNode) treeNode.getChildAt(j);
					if (posTreeNode.toString().equals(pos)) {
						posTreeNode.add(sortableTreeNode);
						break;
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static class SortableTreeNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = 1L;
		
		public SortableTreeNode(Object userObject) {
			super(userObject);
		}
		public void add(SortableTreeNode newChild) {
			int childsTotal = getChildCount();
			Comparable newObject = (Comparable)newChild.getUserObject();
			for (int i = 0; i < childsTotal; i++) {
				SortableTreeNode child = (SortableTreeNode)getChildAt(i);
				Comparable childObject = (Comparable)child.getUserObject();
				if (newObject.compareTo(childObject) <0) {
					super.insert(newChild, i);
					return;
				}
			}
			super.add(newChild);
		}

		public void insert(SortableTreeNode newChild, int childIndex) {
			add(newChild);
		}
		
		public void remove() {
			@SuppressWarnings("unused")
			SortableTreeNode parent = (SortableTreeNode)getParent();
			super.removeFromParent();
		}
		
		public void sort() {
			int cc = getChildCount();
			for (int i = 0; i < cc - 1; i++) {
				for (int j = i+1; j <= cc - 1; j++) {
					SortableTreeNode here = (SortableTreeNode)getChildAt(i);
					SortableTreeNode there = (SortableTreeNode)getChildAt(j);
					Comparable hereObject = (Comparable)here.getUserObject();
					Comparable thereObject = (Comparable)there.getUserObject();
					if (hereObject.compareTo(thereObject) > 0) {
						super.remove(here);
						super.remove(there);
						super.insert(there,i);
						super.insert(here, j);
					}
				}
			}
		}
	}
	
	class Actions implements ActionListener {
		
		String name="";
						
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();
			int count = TreeManagerGroups.currentTreePath.getPathCount();
			name = TreeManagerGroups.currentTreePath.getPathComponent(count -1).toString();

			if ("new_user".equals(command)) {
				UsersManager userManager = new UsersManager();
				userManager.clean();
				userManager.add();
				userManager.clean();
			}
			else if ("edit_user".equals(command)) {
				UsersManager userManager = new UsersManager();
				userManager.setFieldLogin(name);
				userManager.getSearchButton().doClick();
				userManager.edit();
				userManager.getFieldPassword().setEditable(true);
				userManager.getFieldMail().setEditable(true);
				userManager.getFieldNames().setEditable(true);
				userManager.getTable().enableButtons();
				userManager.setEnabled(true);
			}
			else if ("delete_user".equals(command)) {
				UsersManager userManager = new UsersManager();
				userManager.setFieldLogin(name);
				userManager.getSearchButton().doClick();
				userManager.delete();
			}
			else if ("search_user".equals(command)) {
				UsersManager userManager = new UsersManager();
				userManager.setFieldLogin(name);
				userManager.getSearchButton().doClick();
				userManager.search();
			}
			else if ("new_point".equals(command)) {
				WorkStationsManager ws = new WorkStationsManager();
				ws.clean();
				ws.add();
			}
			else if ("edit_point".equals(command)) {
				WorkStationsManager ws = new WorkStationsManager();
				ws.setFieldName(name);
				ws.getSearchButton().doClick();
				ws.edit();
				ws.getFieldCode().setEditable(true);
				ws.getFieldIp().setEditable(true);
				ws.getGroupsCombo().setEnabled(true);
			}
				
			else if ("delete_point".equals(command)) {
				WorkStationsManager ws = new WorkStationsManager();
				ws.setFieldName(name);
				ws.getSearchButton().doClick();
				ws.delete();
			}
			else if ("search_point".equals(command)) {
				WorkStationsManager ws = new WorkStationsManager();
				ws.setFieldName(name);
				ws.getSearchButton().doClick();
				ws.delete();
			}
			else if ("new_group".equals(command)) {
				GroupsManager group = new GroupsManager();
				group.add();
			}
			else if ("edit_group".equals(command)) {
				GroupsManager group = new GroupsManager();
				group.setFieldName(name);
				group.getSearchButton().doClick();
				group.edit();
				group.getVisibleCheck().setEnabled(true);
				group.getZoneCheck().setEnabled(true);
				group.getAcceptButton().setEnabled(true);
			}
			else if ("delete_group".equals(command)) {
				GroupsManager group = new GroupsManager();
				group.setFieldName(name);
				group.getSearchButton().doClick();
				group.delete();
				group.getAcceptButton().setEnabled(true);
			}
			else if ("search_group".equals(command)) {
				GroupsManager group = new GroupsManager();
				group.setFieldName(name);
				group.getSearchButton().doClick();
				group.search();
			}
		}
		
		public String getNodeName() {
			return name;
		}
	}
	
	public static int getRowPath(TreePath p) {
		int count = p.getPathCount();
		String name = p.getPathComponent( (count > 2 ? count : 3 ) -2 ).toString();
		for (int i =0 ; i < rootNode.getChildCount() ;  i++) {
			SortableTreeNode node = (SortableTreeNode) rootNode.getChildAt(i);
			if (name.equals(node.toString())) {
				return (i+1);
			}
			for (int j=0 ; j < node.getChildCount() ; j++)  {
				SortableTreeNode subnode = (SortableTreeNode) node.getChildAt(j);
				if (name.equals(subnode.toString())) {
					return (i+j+2);
				}
				for (int k=0 ; k < subnode.getChildCount() ; k++)  {
					SortableTreeNode lastnode = (SortableTreeNode) subnode.getChildAt(k);
					if (name.equals(lastnode.toString())) {
						return (i+j+k+3);
					}
				}
			}
		}
		return -1;
	}
	
	public static int getChildCount(String name) {
		for (int i =0 ; i < rootNode.getChildCount() ;  i++) {
			SortableTreeNode node = (SortableTreeNode) rootNode.getChildAt(i);
			if (name.equals(node.toString())) {
				return node.getChildCount();
			}
			for (int j=0 ; j < node.getChildCount() ; j++)  {
				SortableTreeNode subnode = (SortableTreeNode) node.getChildAt(j);
				if (name.equals(subnode.toString())) {
					return subnode.getChildCount();
				}
				for (int k=0 ; k < subnode.getChildCount() ; k++)  {
					SortableTreeNode lastnode = (SortableTreeNode) subnode.getChildAt(k);
					if (name.equals(lastnode.toString())) {
						return lastnode.getChildCount();
					}
				}
			}
		}
		return 0;
	}
	
	public static void expand() {
		
		if (TreeManagerGroups.currentTreePath!=null) {
			Thread t = new Thread () {
				public void run() {

					int pcount = currentTreePath.getPathCount();
					SortableTreeNode globalNode = (SortableTreeNode) currentTreePath.getPathComponent(pcount-2);
					SortableTreeNode globalLastNode = (SortableTreeNode) currentTreePath.getPathComponent(pcount-1);
					int ccount = getChildCount(globalNode.toString());
					
					if (pcount==2) { // Seleccion de un grupo
						int row = getRowPath(currentTreePath);
						tree.expandRow(row);
						tree.setSelectionPath(currentTreePath);
						tree.scrollRowToVisible(row+ccount);
					}
					if (pcount==3) { // Seleccion de un punto de venta - Extendiendo un Grupo
						int row = getRowPath(currentTreePath);
						if (getChildCount(globalLastNode.toString())>0) {
							tree.expandRow(row);
							tree.setSelectionPath(currentTreePath);
							tree.scrollPathToVisible(currentTreePath);
							TreePath tp = new TreePath(new Object[] {
									currentTreePath.getPathComponent(0),
									currentTreePath.getPathComponent(1),
									currentTreePath.getPathComponent(2),
									new SortableTreeNode("")});
							row = getRowPath(tp);
							tree.scrollRowToVisible(row);
						}
						else {
							tree.expandRow(row);
							tree.setSelectionPath(currentTreePath);
							tree.scrollRowToVisible(row+ccount);							
						}
					}
					if (pcount==4) { // Seleccion punto de venta?
						int row = getRowPath(currentTreePath.getParentPath());
						tree.expandRow(row);
						row = getRowPath(currentTreePath);
						tree.expandRow(row);
						tree.setSelectionPath(currentTreePath);
						tree.scrollRowToVisible(row+ccount);
					}
				}
			};
			SwingUtilities.invokeLater(t);
		}
	}
	
	public static void collapseAll() {
	    int row = tree.getRowCount() - 1;
	    while (row >= 0) {
	      tree.collapseRow(row);
	      row--;
	    }
	    tree.expandRow(0);
	}
}
