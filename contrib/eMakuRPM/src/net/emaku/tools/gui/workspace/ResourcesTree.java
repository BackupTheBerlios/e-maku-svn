package net.emaku.tools.gui.workspace;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Arrays;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.emaku.tools.db.DataBaseManager;
import net.emaku.tools.gui.ReportManagerGUI;
import net.emaku.tools.gui.ReportDialog;

// This class represents the structure of one report in a JTree

public class ResourcesTree extends JTree implements ActionListener, MouseListener, KeyListener{

	private static final long serialVersionUID = 1L;
	private static SortableTreeNode rootNode;
	private SortableTreeNode reportsNode = new SortableTreeNode("Reports");
	private SortableTreeNode formsNode = new SortableTreeNode("Forms");
	private SortableTreeNode queriesNode = new SortableTreeNode("Queries");
	
    private Vector<String> categories = new Vector<String>();
    private ReportManagerGUI frame;
    private String currentCategory;
    private File directory;
    private String separator = System.getProperty("file.separator");
    private static DefaultTreeModel model; 
    private Vector<String> forms;
    private Vector<String> queries;
    private Hashtable<String,String> toolTipHash;
    
	public ResourcesTree(ReportManagerGUI frame, SortableTreeNode rootNode) {
		super(rootNode);
		model = new DefaultTreeModel(rootNode);
		this.frame = frame;
		ResourcesTree.rootNode = rootNode;
		setFont(new Font("Monospaced", 0, 10));
		addMouseListener(this);
		addKeyListener(this);
	}
	
	public void loadReports(File directory) {
		this.directory = directory;
		File [] reports = directory.listFiles();
		reportsNode.removeAllChildren();
		
		for (File component : reports ) {
			String categoryName = component.getName();
			if (component.isDirectory() && !categoryName.startsWith(".")) {
				SortableTreeNode category = new SortableTreeNode(categoryName);
				categories.add(categoryName);
				String[] childs = component.list();
				Arrays.sort(childs);
				
				if (childs!=null) {
					SortableTreeNode group = null;
					int before = 0;
					for ( String child : childs ) {
						if ( child.endsWith(".xml") ) {
							int ind = child.indexOf(".");
							String name = child.substring(0,ind);
							int k = getNumberFromReport(name); 
							int t = before/10;
							int m = k/10;
														
							if (k % 10 == 0 || (t != m)) {
								if (t != m) {
									m = m*10;
									group = new SortableTreeNode(m + " - " + (m+9));									
								} else {
									group = new SortableTreeNode(k + " - " + (k+9));
								}
								category.add(group);
							}
							
							SortableTreeNode report = new SortableTreeNode(name);
							if(group != null) {
								group.add(report);
								before = k;
							}
						}
					}
					if (category.getChildCount()>0) {
						reportsNode.add(category);
					}
				}
			}
		}
		if (reportsNode.getChildCount()>0) {
			this.updateUI();
		}
		else {
			JOptionPane.showMessageDialog(frame,"There are not reports availables!");
		}
	}
	
	public void loadForms() {
		//formHash = DataBaseManager.getForms();
		//forms = new Vector<String>(formHash.keySet());
		forms = DataBaseManager.getForms();
		
		SortableTreeNode trNode = new SortableTreeNode("General");
		SortableTreeNode dgtNode = new SortableTreeNode("DrugStore");
		SortableTreeNode group = null;		
		int before = 0;
		
		Collections.sort(forms);
		int size = forms.size();
				
		for(int i=0; i< size; i++) {
			String code = forms.elementAt(i);
			int reportNumber = getNumberFromTransaction(code);
			int floor = before/10;
			int tenGroups = reportNumber/10;
			
			if ((reportNumber % 10 == 0) || (floor != tenGroups)) {
				if (floor != tenGroups) {
					tenGroups = tenGroups*10;
					group = new SortableTreeNode(tenGroups + " - " + (tenGroups+9));									
				} else {
					group = new SortableTreeNode(reportNumber + " - " + (reportNumber+9));
				}
				if(code.startsWith("TR")) {
					trNode.add(group);
				} else if(code.startsWith("DGT")) {
					dgtNode.add(group);
				}
			}
			
			SortableTreeNode transaction = new SortableTreeNode(code);
			if(group != null) {
				group.add(transaction);
				before = reportNumber;
			}			
		}
		
		formsNode.add(trNode);
		formsNode.add(dgtNode);
	}
	
	public void loadQueries() {
		queries = DataBaseManager.getQueries();
		SortableTreeNode selNode = new SortableTreeNode("SEL");
		SortableTreeNode crpNode = new SortableTreeNode("CRP");
		SortableTreeNode scsNode = new SortableTreeNode("SCS");
		SortableTreeNode srpNode = new SortableTreeNode("SRP");
		SortableTreeNode dgselNode = new SortableTreeNode("DGSEL");
		SortableTreeNode dgupdNode = new SortableTreeNode("DGUPD");
		SortableTreeNode dgdelNode = new SortableTreeNode("DGDEL");
		SortableTreeNode group = null;		
		int before = 0;
		
		Collections.sort(queries);
		int size = queries.size();
				
		for(int i=0; i< size; i++) {
			String code = queries.elementAt(i);
			int reportNumber = getNumberFromQuery(code);
			int floor = before/10;
			int tenGroups = reportNumber/10;
			
			if ((reportNumber % 10 == 0) || (floor != tenGroups)) {
				if (floor != tenGroups) {
					tenGroups = tenGroups*10;
					group = new SortableTreeNode(tenGroups + " - " + (tenGroups+9));									
				} else {
					group = new SortableTreeNode(reportNumber + " - " + (reportNumber+9));
				}
				if(code.startsWith("SEL")) {
					selNode.add(group);
				} else if(code.startsWith("CRP")) {
					crpNode.add(group);
				} else if(code.startsWith("SCS")) {
					scsNode.add(group);
				} else if(code.startsWith("SRP")) {
					srpNode.add(group);
				} else if(code.startsWith("DGSEL")) {
					dgselNode.add(group);
				} else if(code.startsWith("DGUPD")) {
					dgupdNode.add(group);
				} else if(code.startsWith("DGDEL")) {
					dgdelNode.add(group);
				}
			}
			
			SortableTreeNode transaction = new SortableTreeNode(code);
			if(group != null) {
				group.add(transaction);
				before = reportNumber;
			}			
		}
		
		queriesNode.add(crpNode);
		queriesNode.add(dgselNode);
		queriesNode.add(dgupdNode);
		queriesNode.add(dgdelNode);
		queriesNode.add(scsNode);
		queriesNode.add(selNode);
		queriesNode.add(srpNode);
	}
	
	public void setTree() {
		rootNode.add(formsNode);
		rootNode.add(queriesNode);
		rootNode.add(reportsNode);
		toolTipHash = DataBaseManager.getToolTips();
	}

	private int getNumberFromReport(String name) {
		String number = name.substring(3,name.length());
		while(number.startsWith("0")) {
			if (number.length()==1) {
				break;
			}
			number = number.substring(1,number.length());
		}
		int k = Integer.parseInt(number);
		return k;
	}
	
	private int getNumberFromTransaction(String name) {		
		String number = name.substring(name.length()-5,name.length());
		while(number.startsWith("0")) {
			if (number.length()==1) {
				break;
			}
			number = number.substring(1,number.length());
		}
		int k = Integer.parseInt(number); 
		
		return k;
	}
	
	private int getNumberFromQuery(String name) {
		char letter = name.charAt(0);
		while(!Character.isDigit(letter) || letter == '0') {
			name = name.substring(1,name.length());
			letter = name.charAt(0);
		}
		int k = Integer.parseInt(name);
		
		return k;
	}
	
	public int getChildCount() {
		return rootNode.getChildCount();
	}
	
	public TreeNode getTreeNode(int i) {
		TreeNode tn = rootNode.getChildAt(i);
		
	    return tn;
	}
	
	public TreeNode getCategoryNode(String category) {
		boolean exist = false;
		SortableTreeNode initNode = rootNode;
		TreeNode treeNode = null;

		int nodes = initNode.getChildCount();	
		for (int i=0; i < nodes ; i++) {
			treeNode = rootNode.getChildAt(i);
			if (treeNode.toString().equals(category)) {
				exist = true;
				break;
			}
		}

		if(!exist) {
			return null;
		}

		return treeNode;
	}
	
	public TreeNode getSubCategoryNode(String range) {
		TreeNode subCategory = null; 
		boolean flag = false;
		
		int nodes = rootNode.getChildCount();	
		for (int i=0; i < nodes ; i++) {
			TreeNode treeNode = null;
			treeNode = rootNode.getChildAt(i);
			int internalNodes = treeNode.getChildCount();
			for (int j=0; j < internalNodes ; j++) {
				subCategory = treeNode.getChildAt(j);
				if (subCategory.toString().equals(range)) {
					flag = true;
					break;
				}
			}
			if(flag) {
				break;
			}
		}

		return subCategory;
	}
	
	public boolean existsReport(String reportCode) {
		int nodes = rootNode.getChildCount();
		boolean existValue = false;

		for (int j=0; j < nodes ; j++) {
			TreeNode tn = rootNode.getChildAt(j);
			int nodesValue = tn.getChildCount();
			for (int i=0; i < nodesValue ; i++) {
				String leaf = tn.getChildAt(i).toString();
				if (leaf.equals(reportCode)) {
					existValue = true;
					break;
				}
			}
		}
		return existValue;
	}
		
	public void addNewLeaf(String reportCode, SortableTreeNode treeNode) {
		SortableTreeNode tn = treeNode;
		SortableTreeNode nNode = new SortableTreeNode(reportCode); 
		tn.add(nNode);
		rootNode.sort();
		setSelectionPath(new TreePath(model.getPathToRoot(nNode)));
	}
		
	public void addNewReport(String reportCode,TreeNode treeNode) {

		SortableTreeNode tn = (SortableTreeNode) treeNode;
		int nodes = tn.getChildCount();	
		int reportNumber = getNumberFromReport(reportCode);

		for (int i=0; i < nodes ; i++) {
			TreeNode leaf = tn.getChildAt(i);
			String range = leaf.toString();
			String minor = range.substring(0,range.indexOf("-")).trim();
			String max = range.substring(range.indexOf("-")+1,range.length()).trim();
			if((Integer.parseInt(minor) <= reportNumber) && (Integer.parseInt(max) >= reportNumber)) {
				SortableTreeNode treeNode2 = (SortableTreeNode) getSubCategoryNode(range);
				addNewLeaf(reportCode, treeNode2);
				break;
			}
		}
	}
	
		
	public void mouseClicked(MouseEvent e) {
		
		if ( e.getClickCount() == 2 ) {
			TreePath treepath = getSelectionPath();
			int num = treepath.getPathCount();	
			
			if (num == 5) {
				String category = treepath.getPathComponent(2).toString();
				String object = treepath.getPathComponent(4).toString();
				frame.openObject(category,object);
			}
		} 
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		if ( arg0.getButton() == MouseEvent.BUTTON3 && arg0.isPopupTrigger() ) {      
	        TreePath selPath = getPathForLocation(arg0.getX(), arg0.getY());

	        if (selPath != null) {
	        	int nodesNum = selPath.getPathCount();
	        	if(nodesNum == 1) {
	        		JPopupMenu jpopup = new JPopupMenu();
	        		JMenuItem item = new JMenuItem("Add New Category");
	        		item.setActionCommand("newCategory");
	        		item.addActionListener(this);
	        		jpopup.add(item);
	        		jpopup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
	        	} else {
	        		currentCategory = selPath.getPathComponent(1).toString();	
	        		showNewReportMenu(arg0.getComponent(),"Add New Report","new",arg0.getX(),arg0.getY());
	        	}
	        } else {
	        	currentCategory = "";	
	        	showNewReportMenu(arg0.getComponent(),"Add New Report","new",arg0.getX(),arg0.getY());	        	
	        }      
		}
	}
	
	public void showNewReportMenu(Component e, String type, String command, int x, int y) {
		JPopupMenu jpopup = new JPopupMenu();
		JMenuItem item = new JMenuItem(type);
		item.setActionCommand(command);
		item.addActionListener(this);
		jpopup.add(item);
		jpopup.show(e, x, y);
	}
	
	public void mouseReleased(MouseEvent arg0) {
	}
 
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_ENTER) {
			TreePath branch = getSelectionPath();
			int level = branch.getPathCount();

			switch(level) {
			case 1:
			case 2:
			case 3:
				if (!isExpanded(branch)) {
					expandPath(branch);
				} else {
					this.collapsePath(branch);
				}
				break;				
			case 4:
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				TreeNode node = (TreeNode) branch.getLastPathComponent();
				String reportCode = node.toString();
				currentCategory = branch.getPathComponent(1).toString();

				// If the report was already open
				if (categories.contains(reportCode)) {
					return;
				}

				String path = directory.getAbsolutePath() + separator + currentCategory 
								+ separator + reportCode+".xml";

				if (!frame.containsObject(frame.REPORT,path)) {
					frame.loadReport(currentCategory,reportCode);
					frame.addObjectTab(frame.REPORT);

					if(frame.objectTabCount(frame.REPORT)==1)
						frame.setBarButtonsState(frame.REPORT,true);	
				} 
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

		if (key == KeyEvent.VK_N) {
			createNewReport();
		}
	}

	public void keyReleased(KeyEvent arg0) {		
	}

	public void keyTyped(KeyEvent arg0) {
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ("new".equals(action)) {
			createNewReport();
		}
		else if ("newCategory".equals(action)) {
			System.out.println("Method is missing");
		}	
	}

	private void createNewReport() {
		boolean next = false;
		
		do {				
			ReportDialog dialog = new ReportDialog(frame,categories,currentCategory);
			dialog.pack();
			dialog.setLocationRelativeTo(frame);
			dialog.setVisible(true);

			String reportCode = dialog.getReportName();				
			if (reportCode == null) {
				return;
			}
			String category = dialog.getCategoryName();
			TreeNode treeNode = getCategoryNode(category);

			if (treeNode != null) {
				frame.setCurrentCategory(category);					
				if (!existsReport(reportCode)) {
					frame.loadNewReport(reportCode,treeNode);
					break;
				}
				else {
					JOptionPane.showMessageDialog(frame,"The report "+ reportCode + " already exists!");
					next = true;
				}
			}
			else {
				JOptionPane.showMessageDialog(frame,"The category "+ category + " doesn't exists!");
				next = true;
			}

		} while (next);
	}
	
	public Vector<String> getCategories() {
		return categories;
	}
	
	public SortableTreeNode getRoot() {
		return rootNode;
	}
	
	public static DefaultMutableTreeNode findNode(String target) {
		//System.out.println("Looking for " + target + "...");
		DefaultMutableTreeNode node = null;
		Enumeration<DefaultMutableTreeNode> enumeration = rootNode.breadthFirstEnumeration(); 
		
        while(enumeration.hasMoreElements())
        {
            //get the node
            node = (DefaultMutableTreeNode)enumeration.nextElement();
            //match the string with the user-object of the node
            if(target.equals(node.getUserObject().toString()))
            {
                //tree node with string found
            	return node;                        
            }
        } 
        return node;
	}
	
	public static String searchCategory(String target) {
		DefaultMutableTreeNode node = findNode(target);
		String category = null;
        if(node != null)
        {
            //make the node visible by scroll to it
            TreeNode[] nodes = model.getPathToRoot(node);
            TreePath path = new TreePath(nodes);
            /* int total = path.getPathCount();
            for(int i=0;i<total;i++) {
            	System.out.println("NODE: " + path.getPathComponent(i).toString());
            } */  
            category = path.getPathComponent(2).toString();
        } 
        
        return category;
	}
	
	public static class SortableTreeNode extends DefaultMutableTreeNode implements Comparable<SortableTreeNode> {

		private static final long serialVersionUID = 1L;

		public SortableTreeNode(Object userObject) {
			super(userObject);
		}
		
		public void add(SortableTreeNode newChild) {
			int childsTotal = getChildCount();
			for (int i = 0; i < childsTotal; i++) {
				SortableTreeNode child = (SortableTreeNode)getChildAt(i);
				if (child.compareTo(newChild) == 0) {
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
			super.removeFromParent();
		}

		public void sort() {
			int cc = getChildCount();
			for (int i = 0; i < cc - 1; i++) {
				for (int j = i+1; j <= cc - 1; j++) {
					SortableTreeNode here = (SortableTreeNode)getChildAt(i);
					SortableTreeNode there = (SortableTreeNode)getChildAt(j);
                   	if (here.compareTo(there) == 0) {
                    		super.remove(here);
                    		super.remove(there);
                    		super.insert(there,i);
                    		super.insert(here, j);
                   	}	
				}
			}
		}
		
		public int compareTo(SortableTreeNode object) throws ClassCastException {
			String first = this.toString();
			String second = object.toString();
            char c = first.charAt(0);
            if(Character.isDigit(c)) {
    			String minor = first.substring(0,first.indexOf("-")).trim();
    			String max = second.substring(0,second.indexOf("-")).trim();
    			if(Integer.parseInt(minor) > Integer.parseInt(max)) {
                   return 0;
    			}
            } else {
            	if (first.compareTo(second) == 1) {
            		return 0;
            	}	
            }
			return 1;
		}
	}
	
	public String getToolTipText(MouseEvent e) {
		Object tip = null;
		TreePath path = getPathForLocation(e.getX(), e.getY());
		if (path != null) {
			tip = path.getLastPathComponent();
		}
		return tip == null ? null : toolTipHash.get(tip.toString());
	}
}
