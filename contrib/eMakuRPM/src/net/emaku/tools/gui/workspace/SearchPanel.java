package net.emaku.tools.gui.workspace;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
//import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SearchPanel extends JPanel implements KeyListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JTextField patternTF;
	//private String pattern = "";
	//private TemplateEditor editor;
	private JButton next;

	public SearchPanel(ReportEditor editor) {
		//this.editor = editor;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		next = new JButton("Search");
		next.setActionCommand("next");
		next.addActionListener(this);
		//next.setEnabled(false);
		JButton preview = new JButton("Previous");
		preview.setActionCommand("previous");
		preview.addActionListener(this);
		//preview.setEnabled(false);

		//JCheckBox match = new JCheckBox("Match Case");
		//match.setMnemonic(KeyEvent.VK_A);
		patternTF = new JTextField(15);
		patternTF.addKeyListener(this);
		
		add(new JLabel("Pattern:"));
		add(patternTF);
		add(next);
		add(preview);
		//add(match);
	}

	public void keyPressed(KeyEvent e) {
		/* int code = e.getKeyCode();
		char letter = e.getKeyChar();

		if (code == KeyEvent.VK_ENTER) { 
			pattern = patternTF.getText();
			if (pattern.length() > 0) {
				boolean flag = editor.selectPattern(pattern);
				if (flag) {
					System.out.println("Patron encontrado!");
				}
				return;
			} else {
				return;
			}
		}
		
		if(code != 8) {
			pattern += letter;
			if (editor.selectPattern(pattern) && !next.isEnabled()) {
				next.setEnabled(true);
			}
		} else {
			if (pattern.length() > 0) {
				pattern = pattern.substring(0,pattern.length() - 1);
				if (pattern.length() == 0) {
					editor.resetIndex();
					editor.setCaretPosition(0);
					if(next.isEnabled()) {
						next.setEnabled(false);
					}
					
				}
			} 
		} */		
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		
		if ("next".equals(action)) {
			return;
		}
		if ("previous".equals(action)) {
			return;
		}
	}	
}
