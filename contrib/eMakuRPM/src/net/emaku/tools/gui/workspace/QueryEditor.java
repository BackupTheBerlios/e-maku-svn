package net.emaku.tools.gui.workspace;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;

import net.emaku.tools.gui.CopyInterface;

import com.Ostermiller.Syntax.HighlightedDocument;

// This is the SQL Editor for the Query workspace

public class QueryEditor extends JTextPane implements ActionListener, MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private Object style = HighlightedDocument.SQL_STYLE;
	private HighlightedDocument document = new HighlightedDocument();
	private String form;
	//private NumbersPanel numberPanel;
	private int lines = 0;
	
	public QueryEditor(String form) {
		this.form = form;
		document.setHighlightStyle(style);
		setDocument(document);
		addMouseListener(this);
		addKeyListener(this);
		requestFocus();
		loadQuery();
	}
	
	/*
	public void setNumberPanel(NumbersPanel numberPanel) {
		this.numberPanel = numberPanel;
	}*/
	
	private void loadQuery() {
		String[] parts = form.split("\n");
		lines = parts.length;
		setText(form);
		setCaretPosition(0);
	}
	
	public String getQuery() {
		return this.getText();
	}
	
	public void updateText(String data) {
		setText(data);
		setCaretPosition(0);
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		if ( e.getButton() == MouseEvent.BUTTON3 && e.isPopupTrigger() ) {
			showMenu(e.getComponent(),e.getX(),e.getY());
		}		
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void keyPressed(KeyEvent e) {
		
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		
		if ("select".equals(action)) {
			updateUI();
			selectAll();
			return;
		}
		
		if ("copy".equals(action)) {
			copy();
			return;
		}
		
		if ("paste".equals(action)) {
			paste();
			return;
		}

		if ("clean".equals(action)) {
			setText("");
		}
	}	
	
	public void copy() {
	    CopyInterface.setCopyText(getSelectedText());
		CopyInterface.setCopyState(true);		
	}
	
	public void paste() {
		replaceSelection(CopyInterface.getCopyText());
	}
	
	public void unselectText() {
		String text = getSelectedText();
		if(text != null) {
			unselectText();
		}
	}
	
	public void showMenu(Component e, int x, int y) {	
		String text = getSelectedText();
		JPopupMenu jpopup = new JPopupMenu();
		JMenuItem item = null;

		if(text == null) {
			item = new JMenuItem("Select All");
			item.setActionCommand("select");
			item.addActionListener((ActionListener) this);
			jpopup.add(item);			
		} else {
			item = new JMenuItem("Copy");
			item.setActionCommand("copy");
			item.addActionListener((ActionListener) this);
			jpopup.add(item);
		} 			
		if (CopyInterface.isCopyOn()) {
			item = new JMenuItem("Paste");
			item.setActionCommand("paste");
			item.addActionListener((ActionListener) this);
			jpopup.add(item);
		}
		item = new JMenuItem("Clean");
		item.setActionCommand("clean");
		item.addActionListener((ActionListener) this);
		jpopup.add(item);
		
		jpopup.show(e, x, y);
	}
	
	public int getLinesTotal() {
		return lines;
	}
}