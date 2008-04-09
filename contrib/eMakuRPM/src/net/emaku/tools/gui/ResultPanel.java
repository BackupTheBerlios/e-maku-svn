package net.emaku.tools.gui;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ResultPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public ResultPanel(Vector<Vector<String>> result) {
		super();
		this.setLayout(new BorderLayout());
		int total = result.size();
		if(total == 0) {
			JTextArea info = new JTextArea("   Sorry, no results found");
			info.setEditable(false);
			this.add(info,BorderLayout.CENTER);
		} else {
			JLabel label = new JLabel(total + " results found");
			label.setHorizontalAlignment(JLabel.CENTER);
			JPanel up = new JPanel(new BorderLayout());
			up.add(label,BorderLayout.CENTER);
			ResultTable table = new ResultTable(result);
			JScrollPane scroll = new JScrollPane(table);
			JPanel center = new JPanel(new BorderLayout());
			center.add(scroll);
			this.add(up,BorderLayout.NORTH);
			this.add(center,BorderLayout.CENTER);
		}
	}
	
}
