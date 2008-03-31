package net.emaku.tools.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
//import javax.swing.JToolBar;

import net.emaku.tools.db.DataBaseManager;
//import net.emaku.tools.syntax.HighlightedSQL;
import com.Ostermiller.Syntax.*;

// This class represents a dialog box to show and edit the SQL sentence for every report

public class QueryEditor extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private Object style = HighlightedDocument.SQL_STYLE;
	private HighlightedDocument document = new HighlightedDocument();
	private JButton jbSave;
	private JButton jbClose;
	private String sqlCode;
	private JTextPane editor;
	private JScrollPane container;
	private boolean state;
	private boolean action = false;
	
	public QueryEditor(boolean state,JFrame parent,String text, String sqlCode) {
		super(parent,true);
		this.state = state;
		this.sqlCode = sqlCode;
		String title = "SQL Code: " + sqlCode;
		
		setTitle(title);
		setLayout(new BorderLayout());

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				action = true;
				close();
			}
		}); 

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		editor = new JTextPane();
		document.setHighlightStyle(style);
		editor.setDocument(document);
		editor.setMargin(new Insets(5,5,5,5));
	    editor.setFont(new Font("Monospaced", 0, 10));
		editor.setText(text);
		editor.setCaretPosition(0);
		
		container = new JScrollPane(editor);
		panel.add(container, BorderLayout.CENTER);
		
		getContentPane().add(panel,BorderLayout.CENTER);
		getContentPane().add(getButtons(),BorderLayout.SOUTH);
		
		/* UndoAndRedoManager undo = new UndoAndRedoManager(editor);
		JBUndo.setAction(undo.getUndoAction());
		JBUndo.setText("Undo");
		JBRedo.setAction(undo.getRedoAction());
		JBRedo.setText("Redo"); */
		
		editor.requestFocus();
	}
	
	private JPanel getButtons() {
		JPanel panel = new JPanel();

		jbClose = new JButton();
		jbClose.addActionListener(this);
		jbClose.setActionCommand("close");
		jbClose.setMnemonic(KeyEvent.VK_C);
		
		if(state) {
			jbSave = new JButton("Save");
			jbSave.addActionListener(this);
			jbSave.setActionCommand("save");
			jbSave.setMnemonic(KeyEvent.VK_S);
			panel.add(jbSave);
			jbClose.setText("Close");
			panel.add(jbClose);
		} else {
			jbClose.setText("View Report");
			panel.add(jbClose);
			JButton JBCancel = new JButton("Cancel");
			JBCancel.addActionListener(this);
			JBCancel.setActionCommand("cancel");
			JBCancel.setMnemonic(KeyEvent.VK_C);
			panel.add(JBCancel);
		}
		
		//JBUndo = new JButton("Undo");
		//JBRedo = new JButton("Redo");
		
		//panel.add(new JToolBar.Separator());
		//panel.add(JBUndo);
		//panel.add(JBRedo);
		
		return panel;
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if ("close".equals(command)){
			close();
		}
		if ("cancel".equals(command)){
			action = true;
			close();
		}

		else if ("save".equals(command)) {
			//DataBaseManager.connect();
			try {
				DataBaseManager.setQuery(sqlCode,editor.getText());
			} catch (SQLException SQLE) {
				System.out.println(SQLE.getMessage());
			}
			//DataBaseManager.close();
		}
	}
	
	private void close() {
		dispose();
	}
	
	public String getSQL() {
		return editor.getText();
	}
	
	public boolean isCanceled() {
		return action;
	}
}