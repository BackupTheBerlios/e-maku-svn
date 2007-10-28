package net.emaku.tools.gui.workspace;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

// This class represents the XML general structure for a report template

public class XMLTree extends JTree implements MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private TemplateEditor editor;
	
	public XMLTree(DefaultMutableTreeNode root, TemplateEditor editor) {
		super(root);
		this.editor = editor;
		root.add(new DefaultMutableTreeNode("Settings"));
		root.add(new DefaultMutableTreeNode("Header"));
		root.add(new DefaultMutableTreeNode("Columns"));
		root.add(new DefaultMutableTreeNode("Records"));
		root.add(new DefaultMutableTreeNode("Footer"));
		expandRow(0);
		this.setSelectionRow(1);
		setInfo(0);
		addMouseListener(this);
		addKeyListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		TreePath treepath = getSelectionPath();
		int level = treepath.getPathCount();
		if (level == 2) {
			JTree tmpTree = (JTree) e.getComponent();
			int row = tmpTree.getLeadSelectionRow();
			setInfo(row-1);
		} else {
			setInfo(-1);
		}
	}

	public void mouseEntered(MouseEvent arg0) {
		
	}

	public void mouseExited(MouseEvent arg0) {
		
	}

	public void mousePressed(MouseEvent arg0) {
		
	}

	public void mouseReleased(MouseEvent arg0) {
		
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		Object obj = e.getComponent();
		if (obj==null) {
		    return;
		}
		 JTree tmpTree = (JTree) e.getComponent();
		 int index = tmpTree.getLeadSelectionRow();
		 if (index != 0) {
				setInfo(index-1);
		 } else {
			setInfo(-1);
		 }
	}

	public void keyTyped(KeyEvent e) {
	}
	
	private void setInfo(int section) {
		if(editor.getCurrentSection() != section) {
			editor.setSection(section);
		}
	}
	
	/*
	public 	DefaultMutableTreeNode getRootNode() {
		return this.get
	} */
	
}