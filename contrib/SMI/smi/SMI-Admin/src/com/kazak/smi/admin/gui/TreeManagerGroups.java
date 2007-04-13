package com.kazak.smi.admin.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

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
	private JPopupMenu jpopup1 = new JPopupMenu();
	private JPopupMenu jpopup2 = new JPopupMenu();
	private JPopupMenu jpopup3 = new JPopupMenu();
	private Actions actions;
	private ArrayList<String> lastPath = new ArrayList<String>();
	private static SortableTreeNode globalNode;
	private static SortableTreeNode globalLastNode;
	public static TreePath currTpath;
	
	public static void setNodes(SortableTreeNode node, SortableTreeNode finalNode) {
		globalNode = node;
		globalLastNode = finalNode;
	}
	
	public static String getFinalNode() {
		return globalLastNode.toString();
	}
	
	public static String getGroupNode() {
		return globalNode.toString();
	}
	
	public TreeManagerGroups() {
		rootNode = new SortableTreeNode(MainWindow.getAppOwner());
		rootNode.setAllowsChildren(true);
		tree = new JTree(rootNode);
		loadPopups();
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		renderer.setIcon(null);
		//renderer.setBackgroundSelectionColor(Color.LIGHT_GRAY);
		renderer.setRequestFocusEnabled(true);
		renderer.setAutoscrolls(true);
		//renderer.setBorderSelectionColor(Color.BLACK);
		
		tree.setCellRenderer(renderer);
		tree.setExpandsSelectedPaths(true);
		tree.setShowsRootHandles(true);
		tree.setAutoscrolls(true);
		tree.setScrollsOnExpand(true);
		tree.setDragEnabled(true);
		
		tree.addMouseListener(new MouseAdapter() {
			
			public void mousePressed(MouseEvent e) {
				//if ( e.getButton() == MouseEvent.BUTTON3 && e.isPopupTrigger() ) {
                if ( e.getButton() == MouseEvent.BUTTON3) {
                	currTpath = tree.getPathForLocation(e.getX(), e.getY());
                	tree.setSelectionPath(currTpath);
                	int count = currTpath.getPathCount();
                	SortableTreeNode node;
                	switch (count) {
                	case 2: // Puntos de Venta
                		lastPath.clear();
    	                jpopup1.show(e.getComponent(), e.getX(), e.getY());
            			break;
            		case 3: // Usuarios 
            			node = (SortableTreeNode) currTpath.getPathComponent(2);
            			String name = node.toString();
            			if (Cache.containsWs(name)){
            				jpopup2.show(e.getComponent(), e.getX(), e.getY());
            			}
            			if (Cache.containsUser(name)){
            				jpopup3.show(e.getComponent(), e.getX(), e.getY());
            			}
            			break;
            		case 4:
            			jpopup3.show(e.getComponent(), e.getX(), e.getY());
            			break;
                	}
                }
			}
		});
		
		int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
		tree.getSelectionModel().setSelectionMode(mode);
		jscroll = new JScrollPane(tree);
		Cache.load();
	}
	
	public void loadPopups() {
		actions = new Actions();
		
		//--- Popup Grupos
		jpopup1.add(new JLabel("Grupos"));
		jpopup1.add(new JSeparator());
		JMenuItem item = new JMenuItem("Nuevo");
        item.setActionCommand("new_group");
        item.addActionListener(actions);
        jpopup1.add(item);
        
        item = new JMenuItem("Editar");
        item.setActionCommand("edit_group");
        item.addActionListener(actions);
        jpopup1.add(item);
        
        item = new JMenuItem("Eliminar");
        item.setActionCommand("delete_group");
        item.addActionListener(actions);
        jpopup1.add(item);
        
        item = new JMenuItem("Buscar");
        item.setActionCommand("search_group");
        item.addActionListener(actions);
        jpopup1.add(item);
        
        //--- Popup Puntos Venta
        jpopup2.add(new JLabel("Puntos"));
		jpopup2.add(new JSeparator());
        item = new JMenuItem("Nuevo");
        item.setActionCommand("new_point");
        item.addActionListener(actions);
        jpopup2.add(item);
        
        item = new JMenuItem("Editar");
        item.setActionCommand("edit_point");
        item.addActionListener(actions);
        jpopup2.add(item);
        
        item = new JMenuItem("Eliminar");
        item.setActionCommand("delete_point");
        item.addActionListener(actions);
        jpopup2.add(item);
        
        item = new JMenuItem("Buscar");
        item.setActionCommand("search_point");
        item.addActionListener(actions);
        jpopup2.add(item);
        
        //--- Popup Usuarios
        jpopup3.add(new JLabel("Usuarios"));
		jpopup3.add(new JSeparator());
        item = new JMenuItem("Nuevo");
        item.setActionCommand("new_user");
        item.addActionListener(actions);
        jpopup3.add(item);
        
        item = new JMenuItem("Editar");
        item.setActionCommand("edit_user");
        item.addActionListener(actions);
        jpopup3.add(item);
        
        item = new JMenuItem("Eliminar");
        item.setActionCommand("delete_user");
        item.addActionListener(actions);
        jpopup3.add(item);
        
        item = new JMenuItem("Buscar");
        item.setActionCommand("search_user");
        item.addActionListener(actions);
        jpopup3.add(item);
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
				SortableTreeNode subnode = (SortableTreeNode) node.getChildAt(j);
				if (name.equals(subnode.toString())) {
					node.remove((SortableTreeNode)subnode);
					return;
				}
				for (int k=0 ; k < subnode.getChildCount() ; k++)  {
					SortableTreeNode lastnode = (SortableTreeNode) subnode.getChildAt(k);
					if (name.equals(lastnode.toString())) {
						subnode.remove((SortableTreeNode)lastnode);
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
				SortableTreeNode subnode = (SortableTreeNode) node.getChildAt(j);
				if (name.equals(subnode.toString())) {
					return subnode;
				}
				for (int k=0 ; k < subnode.getChildCount() ; k++)  {
					SortableTreeNode lastnode = (SortableTreeNode) subnode.getChildAt(k);
					if (name.equals(lastnode.toString())) {
						return subnode;
					}
				}
			}
		}
		return null;
	}
		
	public static synchronized void addGroup(String name) {
		SortableTreeNode df = new SortableTreeNode(name);
		rootNode.add(df);
	}
	
	public static synchronized void addChild(String z,String pv) {
		SortableTreeNode df = new SortableTreeNode(pv);
		int nodes = rootNode.getChildCount();
		SortableTreeNode treeNode = null;
		for (int i=0; i < nodes ; i++) {
			treeNode = (SortableTreeNode) rootNode.getChildAt(i);
			if (treeNode.toString().equals(z)) {
				treeNode.add(df);
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

	public static synchronized void addChild(String gname, String codepv, String login) {
		SortableTreeNode df = new SortableTreeNode(login);
		int nodes = rootNode.getChildCount();
		SortableTreeNode treeNode = null;
		for (int i=0; i < nodes ; i++) {
			treeNode = (SortableTreeNode) rootNode.getChildAt(i);
			if (treeNode.toString().equals(gname)) {
				int pvs = treeNode.getChildCount();
				for (int j=0; j < pvs ; j++) {
					SortableTreeNode treeNodePV = (SortableTreeNode) treeNode.getChildAt(j);
					if (treeNodePV.toString().equals(codepv)) {
						treeNodePV.add(df);
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
			int cc = getChildCount();
			Comparable newObject = (Comparable)newChild.getUserObject();
			for (int i = 0; i < cc; i++) {
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
						
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			int count = TreeManagerGroups.currTpath.getPathCount();
			String name = TreeManagerGroups.currTpath.getPathComponent(count -1).toString();
			if ("new_user".equals(command)) {
				UsersManager userManager = new UsersManager();
				userManager.clean();
				userManager.add();
				userManager.clean();
			}
			else if ("edit_user".equals(command)) {
				UsersManager userManager = new UsersManager();
				userManager.setFieldLogin(name);
				userManager.getJBSearch().doClick();
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
				userManager.getJBSearch().doClick();
				userManager.delete();
			}
			else if ("search_user".equals(command)) {
				UsersManager userManager = new UsersManager();
				userManager.setFieldLogin(name);
				userManager.getJBSearch().doClick();
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
				ws.getJBSearch().doClick();
				ws.edit();
				ws.getFieldCode().setEditable(true);
				ws.getFieldIp().setEditable(true);
				ws.getJCBGroups().setEnabled(true);
			}
				
			else if ("delete_point".equals(command)) {
				WorkStationsManager ws = new WorkStationsManager();
				ws.setFieldName(name);
				ws.getJBSearch().doClick();
				ws.delete();
			}
			else if ("search_point".equals(command)) {
				WorkStationsManager ws = new WorkStationsManager();
				ws.setFieldName(name);
				ws.getJBSearch().doClick();
				ws.delete();
			}
			else if ("new_group".equals(command)) {
				GroupsManager group = new GroupsManager();
				group.add();
			}
			else if ("edit_group".equals(command)) {
				GroupsManager group = new GroupsManager();
				group.setFieldName(name);
				group.getJBSearch().doClick();
				group.edit();
				group.getJCheckVisible().setEnabled(true);
				group.getJCheckZone().setEnabled(true);
				group.getJBAccept().setEnabled(true);
			}
			else if ("delete_group".equals(command)) {
				GroupsManager group = new GroupsManager();
				group.setFieldName(name);
				group.getJBSearch().doClick();
				group.delete();
				group.getJBAccept().setEnabled(true);
			}
			else if ("search_group".equals(command)) {
				GroupsManager group = new GroupsManager();
				group.setFieldName(name);
				group.getJBSearch().doClick();
				group.search();
			}
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
		
		if (TreeManagerGroups.currTpath!=null) {
			Thread t = new Thread () {
				public void run() {
					//collapseAll();
					int pcount = currTpath.getPathCount();
					SortableTreeNode globalNode = (SortableTreeNode) currTpath.getPathComponent(pcount-2);
					SortableTreeNode globalLastNode = (SortableTreeNode) currTpath.getPathComponent(pcount-1);
					int ccount = getChildCount(globalNode.toString());
					//System.out.println("child count " + ccount);
					//System.out.println("path count " + pcount);

					if (pcount==2) { // Seleccion de un grupo
						int row = getRowPath(currTpath);
						tree.expandRow(row);
						tree.setSelectionPath(currTpath);
						tree.scrollRowToVisible(row+ccount);
					}
					if (pcount==3) { // Seleccion de un usuario
						if (getChildCount(globalLastNode.toString())>0) {
							int row = getRowPath(currTpath);
							//System.out.println("Imprimiendo objeto: " + globalLastNode.toString());
							//System.out.println("Imprimiendo objeto: " + globalNode.toString());
							TreeManagerGroups.setNodes(globalNode, globalLastNode);
							tree.expandRow(row);
							tree.setSelectionPath(currTpath);
							tree.scrollPathToVisible(currTpath);
							TreePath tp = new TreePath(new Object[] {
									currTpath.getPathComponent(0),
									currTpath.getPathComponent(1),
									currTpath.getPathComponent(2),
									new SortableTreeNode("")});
							row = getRowPath(tp);
							tree.scrollRowToVisible(row);
						}
						else {
							//System.out.println("Imprimiendo objeto en else: " + globalLastNode.toString());
							//System.out.println("Imprimiendo objeto: " + globalNode.toString());
							int row = getRowPath(currTpath);
							tree.expandRow(row);
							tree.setSelectionPath(currTpath);
							tree.scrollRowToVisible(row+ccount);
							
						}
					}
					if (pcount==4) {
						int row = getRowPath(currTpath.getParentPath());
						tree.expandRow(row);
						row = getRowPath(currTpath);
						tree.expandRow(row);
						tree.setSelectionPath(currTpath);
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