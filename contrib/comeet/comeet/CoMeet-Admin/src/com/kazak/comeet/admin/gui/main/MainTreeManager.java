package com.kazak.comeet.admin.gui.main;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.kazak.comeet.admin.control.Cache;
import com.kazak.comeet.admin.gui.managers.GroupManager;
import com.kazak.comeet.admin.gui.managers.PosManager;
import com.kazak.comeet.admin.gui.managers.UserManager;
import com.kazak.comeet.admin.gui.misc.PopUpCombo;

public class MainTreeManager {
	
	private static SortableTreeNode rootNode;
	private JScrollPane jscroll;
	private static JTree tree;
	private Actions actions;
	private ArrayList<String> lastPath = new ArrayList<String>();
	public static TreePath currentTreePath;
	public JFrame frame;
	private PopUpCombo menuCombo;
		
	public MainTreeManager(JFrame frame) {
		this.frame = frame;
		rootNode = new SortableTreeNode(MainWindow.getAppOwner());
		rootNode.setAllowsChildren(true);
		tree = new JTree(rootNode);
		actions = new Actions();
		menuCombo = new PopUpCombo(actions);
		
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
                	case 1: // Nodo Raiz
                		menuCombo.showMenu(4,e.getComponent(), e.getX(), e.getY());
                	    break;
                	case 2: // Puntos de Venta
                		lastPath.clear();
                		node = (SortableTreeNode) currentTreePath.getPathComponent(1);
                		if(node.toString().equals("COMEET")) {
                			menuCombo.showMenu(1,e.getComponent(), e.getX(), e.getY());
                		} else {
                			menuCombo.showMenu(0,e.getComponent(),e.getX(),e.getY());
                		}
            			break;
            		case 3: // Usuarios 
            			node = (SortableTreeNode) currentTreePath.getPathComponent(2);
            			String name = node.toString();
            			if (Cache.containsWs(name)){
            				menuCombo.showMenu(3,e.getComponent(), e.getX(), e.getY());
            			}
            			if (Cache.containsUser(name)){
            				menuCombo.showMenu(2,e.getComponent(), e.getX(), e.getY());
            			}
            			break;
            		case 4:
        				menuCombo.showMenu(2,e.getComponent(), e.getX(), e.getY());
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
			//SortableTreeNode parent = (SortableTreeNode)getParent();
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
			int count = MainTreeManager.currentTreePath.getPathCount();
			name = MainTreeManager.currentTreePath.getPathComponent(count -1).toString();

			if ("reload".equals(command)) {
				Cache.loadInfoTree(1);
			}
			if ("new_op_user".equals(command)) {
				UserManager userManager = new UserManager();
				userManager.addUser();
			}
			if ("new_admin_user".equals(command)) {
				UserManager userManager = new UserManager();
				userManager.addAdmin();
			}			
			else if ("edit_user".equals(command)) {
				UserManager userManager = new UserManager();
				if(Cache.isAdminUser(name)) {
					userManager.editAdmin(name);
				} else {
					userManager.editUser(name);
				}
			}
			else if ("edit_op_user".equals(command)) {
				UserManager userManager = new UserManager();
				userManager.editUser();
			}			
			else if ("edit_admin_user".equals(command)) {
				UserManager userManager = new UserManager();
				userManager.editAdmin();
			}			
			else if ("delete_user".equals(command)) {
				UserManager userManager = new UserManager();
				userManager.deleteUser(name);
			}
			else if ("delete_op_user".equals(command)) {
				UserManager userManager = new UserManager();
				userManager.deleteUser();
			}
			else if ("delete_admin_user".equals(command)) {
				UserManager userManager = new UserManager();
				userManager.deleteAdmin();
			}
			else if ("search_user".equals(command)) {
				UserManager userManager = new UserManager();
				userManager.searchUser(name);
			}
			else if ("search_op_user".equals(command)) {
				UserManager userManager = new UserManager();
				userManager.searchUser();
			}			
			else if ("search_admin_user".equals(command)) {
				UserManager userManager = new UserManager();
				userManager.searchAdmin();
			}						
			else if ("new_point".equals(command)) {				
				PosManager posManager = new PosManager();
				posManager.addPos();
			}
			else if ("edit_point".equals(command)) {
				PosManager posManager = new PosManager();
				posManager.editPos(name);
			}
			else if ("edit_unknown_point".equals(command)) {
				PosManager posManager = new PosManager();
				posManager.editPos();
			}				
			else if ("delete_point".equals(command)) {
				PosManager posManager = new PosManager();
				posManager.deletePos(name);
			}
			else if ("delete_unknown_point".equals(command)) {
				PosManager posManager = new PosManager();
				posManager.deletePos();
			}			
			else if ("search_point".equals(command)) {				
				PosManager posManager = new PosManager();
				posManager.searchPos(name);
			}
			else if ("search_unknown_point".equals(command)) {				
				PosManager posManager = new PosManager();
				posManager.searchPos();
			}			
			else if ("new_group".equals(command)) {
				GroupManager group = new GroupManager();
				group.addGroup();
			}
			else if ("edit_group".equals(command)) {
				GroupManager group = new GroupManager();
				group.editGroup(name);
			}
			else if ("edit_unknown_group".equals(command)) {
				GroupManager group = new GroupManager();
				group.editGroup();
			}
			else if ("delete_group".equals(command)) {
				GroupManager group = new GroupManager();
				group.deleteGroup(name);				
			}
			else if ("delete_unknown_group".equals(command)) {
				GroupManager group = new GroupManager();
				group.deleteGroup();				
			}
			else if ("search_group".equals(command)) {
				GroupManager group = new GroupManager();
				group.searchGroup(name);
			}
			else if ("search_unknown_group".equals(command)) {
				GroupManager group = new GroupManager();
				group.searchGroup();
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
		
		if (MainTreeManager.currentTreePath!=null) {
			Thread t = new Thread () {
				public void run() {

					int pcount = currentTreePath.getPathCount();
					if(pcount<2) {
					   return;	
					}
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
