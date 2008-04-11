package net.emaku.tools.gui.workspace;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.Ostermiller.Syntax.HighlightedDocument;

import net.emaku.tools.gui.ReportManagerGUI;

public class ArgsDialog extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private JTextPane editor;
	private JScrollPane container;
	private Object style = HighlightedDocument.HTML_STYLE;
	private HighlightedDocument document;
	private FormWorkSpace fw;
	private JButton save,close;
	
	public ArgsDialog(FormWorkSpace fw) {
		//super(ReportManagerGUI.getFrame(),true);
		this.fw = fw;
		setTitle("Driver Args Editor");
		document = new HighlightedDocument();
		setLayout(new BorderLayout());
		setInterface();
		
		setSize(new Dimension(350,300));
		setLocationRelativeTo(ReportManagerGUI.getFrame());
		setAlwaysOnTop(true);
		setVisible(true);
	}
	
	private void setInterface() {
		editor = new JTextPane();
		document.setHighlightStyle(style);
		editor.setDocument(document);
		editor.setMargin(new Insets(5,5,5,5));
	    editor.setFont(new Font("Monospaced", 0, 10));
		editor.setText(fw.getDriverArgs());
		editor.setCaretPosition(0);
		editor.requestFocus();
		container = new JScrollPane(editor);
		
		save = new JButton("Save");
		save.setMnemonic('S');
		save.setActionCommand("SAVE");
		save.addActionListener(this);
		close = new JButton("Close");
		close.setMnemonic('C');
		close.setActionCommand("CLOSE");
		close.addActionListener(this);
		
		JPanel south = new JPanel(new FlowLayout());
		south.add(save);
		south.add(close);
		add(container,BorderLayout.CENTER);
		add(south,BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ("SAVE".equals(action)) {
			fw.setDriverArgs(editor.getText());
			setVisible(false);
		}
		if ("CLOSE".equals(action)) {
			setVisible(false);
		}		
	}
}
