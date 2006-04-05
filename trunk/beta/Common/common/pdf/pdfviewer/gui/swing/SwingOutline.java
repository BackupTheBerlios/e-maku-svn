/**
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens (mark@idrsolutions.com)
 *
 * (C) Copyright 2006, IDRsolutions and Contributors.
 *
 * 	This file is part of JPedal
 *
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


 *
 * ---------------
 * SwingOutline.java
 * ---------------
 *
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package common.pdf.pdfviewer.gui.swing;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
//import java.util.StringTokenizer;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import common.pdf.pdfviewer.gui.generic.GUIOutline;

//import org.w3c.dom.Element;
import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

/**holds tree outline displayed in nav bar*/
public class SwingOutline extends JScrollPane implements GUIOutline{
	
	private static final long serialVersionUID = 7423353291901251301L;
	/**flag to stop page setting causing a spurious refresh*/
	private boolean ignoreAlteredBookmark=false;
	
	/**used by tree to convert page title into page number*/
	//private HashMap pageLookupTable=new HashMap();	
	private Map<String, String> pageLookupTable = new HashMap<String, String>();

	
	/**used by tree to find point to scroll to*/
	//private Map pointLookupTable=new HashMap();
	private Map<String,Point> pointLookupTable = new HashMap<String, Point>();
	
	private DefaultMutableTreeNode top =new DefaultMutableTreeNode("Root"); //$NON-NLS-1$
	
	private JTree tree;
	
	/**specify bookmark for each page*/
	//private String[] defaultRefsForPage;
	
	//private TreeNode[] defaultPageLookup;
	
	
	public SwingOutline(Node rootNode) {
		
		/**
		 * default settings for bookmarks for each page
		 */
		//defaultRefsForPage=decode_pdf.getOutlineDefaultReferences();
		//this.defaultPageLookup=new TreeNode[this.pageCount];
		
		//readChildNodes(rootNode,top);
		
		tree=new JTree(top);
		
		expandAll();
		
		tree.setRootVisible(false);
		
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		//create display for bookmarks
		getViewport().add(tree);
		
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		
	}
	
	/**
	 * Walk through any given node of a JTree.
	 *
	 * @param model TreeModel
	 * @param o Object
	 *
	private void walk(TreeModel model, Object o,int currentPage){
		
		int cc;
		cc = model.getChildCount(o);
		DefaultMutableTreeNode node;
		String title;
		String page;
		Object child;
		
		for(int i = 0; i < cc; i++) {
			
			child = model.getChild(o, i);
			
			if(model.isLeaf(child)) {
				
				// try to get the page anchor for this node
				
				node = (DefaultMutableTreeNode)child;
				
				// get title and open page if valid
				title = (String)node.getUserObject();
				page = (String)pageLookupTable.get(title);
				
				if(page.length()>0){
					try {
						if(Integer.parseInt(page) == currentPage) {
							ignoreAlteredBookmark=true;
							tree.setSelectionPath(new TreePath(node.getPath()));
							ignoreAlteredBookmark=false;
						}
					}
					catch(NumberFormatException nfe) {
						System.out.println("bad page number: " + page); 
						ignoreAlteredBookmark=false;
					}
				}
			}
			else {
				//System.out.print(child.toString() + "--");
				walk(model, child,currentPage);
			}
		}
	}
	*/
	/**
	 * Method to traverse all nodes of the Bookmarks JTree.  This is used when
	 * invoking the 'open to page' functionality, because we need to
	 * highlight the bookmark that we are opening to, if there is one.
	 *
	 *
	private void traverse(int currentPage) {
		
		//if(tree instanceof JTree) {
		
		TreeModel model = tree.getModel();
		
		if (model != null)
			walk(model, model.getRoot(),currentPage);
		
		//}
	} */
	
	/**
	 * Walk through any given node of a JTree.
	 *
	private void createLookupTable(TreeModel model, Object o,int currentPage){
		
		int cc;
		cc = model.getChildCount(o);
		DefaultMutableTreeNode node;
		String title;
		String page;
		Object child;
		
		for(int i = 0; i < cc; i++) {
			
			child = model.getChild(o, i);
			
			if(model.isLeaf(child)) {
				
				// try to get the page anchor for this node
				
				node = (DefaultMutableTreeNode)child;
				
				// get title and open page if valid
				title = (String)node.getUserObject();
				page = (String)pageLookupTable.get(title);
				
				try {
					if(Integer.parseInt(page) == currentPage) 
						tree.setSelectionPath(new TreePath(node.getPath()));
					
				}catch(NumberFormatException nfe) {
					System.out.println("bad page number: " + page); 
				}
			}
			else {
				//System.out.print(child.toString() + "--"); 
				walk(model, child,currentPage);
			}
		}
	}
	*/
	/**
	 * Expand all nodes found from the XML outlines for the PDF.
	 */
	private void expandAll() {
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
		
	}
	
	/**
	 * Scans sublist to get the children bookmark nodes.
	 *
	 * @param rootNode Node
	 * @param topNode DefaultMutableTreeNode
	 *
	public void readChildNodes(Node rootNode,DefaultMutableTreeNode topNode) {
		
		if(topNode==null)
			topNode=top;
		
		NodeList children=rootNode.getChildNodes();
		int childCount=children.getLength();
		
		for(int i=0;i<childCount;i++){
			
			Node child=children.item(i);
			
			Element currentElement = (Element) child;
			
			String title=currentElement.getAttribute("title"); 
			String page=currentElement.getAttribute("page"); 
			String rawDest=currentElement.getAttribute("Dest"); 
			//String ref=currentElement.getAttribute("objectRef");
			
			/**create the lookup table
			//pageLookupTable.put(title,page);
			
			/**create the point lookup table
			if((rawDest!=null)&&(rawDest.indexOf("/XYZ")!=-1)){
				
				rawDest=rawDest.substring(rawDest.indexOf("/XYZ")+4); 
				
				StringTokenizer values=new StringTokenizer(rawDest,"[] ");
				
				//ignore the first, read next 2
				
				String x=values.nextToken();
				if(x.equals("null")) 
					x="0"; 
				String y=values.nextToken();
				if(y.equals("null")) 
					y="0"; 
				
				//pointLookupTable.put(title,new Point((int) Float.parseFloat(x),(int) Float.parseFloat(y)));
			}
			
			DefaultMutableTreeNode childNode =new DefaultMutableTreeNode(title);
			
		/**add the nodes or initialise to top level	
			topNode.add(childNode);
			
			if(child.hasChildNodes())
				readChildNodes(child,childNode);
			
		}
	} */
	
	/**
	 * @return the ignoreAlteredBookmark
	 */
	public boolean isIgnoreAlteredBookmark() {
		return ignoreAlteredBookmark;
	}
	
	public String getPage(String title) {
		
		return (String)pageLookupTable.get(title);
	}
	
	/**
	 * Handles the functionality for highlighting the correct bookmark
	 * tree node for the page we opened the PDF to.
	 */
	public void selectBookmark() {
		
		
		/** code to walk not fully operational so only runs on example
	     
	      traverse();
	     
	        try{
	        System.out.println(defaultPageLookup[this.currentPage-1]);
	        ignoreAlteredBookmark=true;
	      tree.setSelectionPath(new TreePath(defaultPageLookup[this.currentPage]));
	      ignoreAlteredBookmark=false;
	      System.out.println(tree.getSelectionPath()+" "+currentPage+" "+defaultPageLookup[this.currentPage-1]);
	        }catch(Exception e){
	            e.printStackTrace();
	            System.exit(1);
	        }/***/
		
	}
	
	public Point getPoint(String title) {
		
		return (Point) pointLookupTable.get(title);
	}
	
	public Object getTree() {
		
		return tree;
	}
	
	public DefaultMutableTreeNode getLastSelectedPathComponent() {
		
		return (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
	}
	
}
