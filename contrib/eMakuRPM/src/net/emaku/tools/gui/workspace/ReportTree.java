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

import net.emaku.tools.gui.ReportManagerGUI;
import net.emaku.tools.gui.ReportDialog;

// This class represents the structure of one report in a JTree

public class ReportTree extends JTree implements ActionListener, MouseListener, KeyListener{

	private static final long serialVersionUID = 1L;
	private SortableTreeNode rootNode;
    private Vector<String> categories = new Vector<String>();
    private ReportManagerGUI frame;
    private String currentCategory;
    private File directory;
    private String separator = System.getProperty("file.separator");
    private DefaultTreeModel model; 
    
	public ReportTree(ReportManagerGUI frame, SortableTreeNode rootNode) {
		super(rootNode);
		model = new DefaultTreeModel(rootNode);
		this.frame = frame;
		this.rootNode = rootNode;
		setFont(new Font("Monospaced", 0, 10));
		addMouseListener(this);
		addKeyListener(this);
	}
	
	public void loadRoot(File directory) {
		this.directory = directory;
		System.out.println("* Reports root: " + directory);
		File [] reports = directory.listFiles();
		rootNode.removeAllChildren();
		
		for (File component : reports ) {
			String categoryName = component.getName();
			if (component.isDirectory() && !categoryName.startsWith(".")) {
				SortableTreeNode category = new SortableTreeNode(categoryName);
				categories.add(categoryName);
				System.out.println("Category: " + categoryName);
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
						rootNode.add(category);
					}
				}
			}
		}
		if (rootNode.getChildCount()>0) {
			this.updateUI();
		}
		else {
			JOptionPane.showMessageDialog(frame,"There are no reports availables!");
		}
		System.gc();
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
			System.out.println("getCategoryNode: " + treeNode.toString() + " - " + category);
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
				System.out.println("getSubCategoryNode: " + subCategory.toString() + " - " + range);
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
					System.out.println("Compare: " + leaf + " - " + reportCode);
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
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			TreePath treepath = getSelectionPath();
			int num = treepath.getPathCount();	
			System.out.println("Treepath Num: " + num);
			
			if (frame.tabCount() == 0) {
				frame.setButtonsState(true);
			}
			
			if (num == 4) {
				String reportPath = separator + treepath.getPathComponent(1)+separator+treepath.getPathComponent(3);
				if (!frame.containsReport(reportPath)) {
					String category = treepath.getPathComponent(1).toString();
					String report = treepath.getPathComponent(3).toString();
					frame.loadReport(category,report);
				}
				int max = frame.tabCount();
				for (int i = 0 ; i < max ; i++) {
					String str = frame.getTabTitle(i);
					if (str.equals(reportPath)) {
						frame.setActiveTab(i);
						break;
					}
				} 
			}
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} 
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		if ( arg0.getButton() == MouseEvent.BUTTON3 && arg0.isPopupTrigger() ) {
			System.out.println("Objeto selecionado: " + arg0.getComponent().toString());
	        
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
			System.out.println("LEVEL: " + level);

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

				String path = directory.getAbsolutePath()+separator+currentCategory+separator+reportCode+".xml";
				System.out.println("PATH from keyPressed: " + path);

				if (!frame.containsReport(path)) {
					frame.loadReport(currentCategory,reportCode);
					frame.addTab();
					System.out.println("Total de Tabs: " + frame.tabCount());

					if(frame.tabCount()==1)
						frame.setButtonsState(true);	
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
			System.out.println("Metodo no implementado");
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
			System.out.println("Proof: " + reportCode);
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
	
	public static class SortableTreeNode extends DefaultMutableTreeNode implements Comparable {

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
                   	if (here.compareTo(there) == 0) {
                    		super.remove(here);
                    		super.remove(there);
                    		super.insert(there,i);
                    		super.insert(here, j);
                   	}	
				}
			}
		}

		public int compareTo(Object object) {
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

}
