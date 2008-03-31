package net.emaku.tools.gui.workspace;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;

import net.emaku.tools.gui.ReportManagerGUI;

/* This class represents the whole right side of the application GUI including the Report Structure Tree, 
   the Report Description and the Template Editor (XML)
*/

public class ReportWorkSpace extends JSplitPane {

	private static final long serialVersionUID = 1L;
	private ReportEditor editor;
	private String reportCode;
	private ReportDescription reportDescription;
		
	public ReportWorkSpace(ReportManagerGUI gui, String reportCode, String report) {
		super(JSplitPane.VERTICAL_SPLIT);
		
		this.reportCode = reportCode;				
		editor = new ReportEditor(report);
		NumbersPanel panel = new NumbersPanel(editor.getLinesTotal(0));
		editor.setNumberPanel(panel);
		
		setDividerLocation(130);
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());		
		Border etched1 = BorderFactory.createEtchedBorder();
		
		JPanel treePanel = new JPanel();
		treePanel.setLayout(new BorderLayout());
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(reportCode);
		treePanel.add(new XMLTree(root,editor),BorderLayout.CENTER);
		treePanel.setBorder(etched1);
		treePanel.setPreferredSize(new Dimension(120,100));
		north.add(treePanel,BorderLayout.WEST);
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		JLabel description = new JLabel(" Description:");
		description.setFont(new Font("Serif",Font.BOLD, 12));
		textPanel.add(description,BorderLayout.NORTH);
		reportDescription = new ReportDescription(reportCode);
		textPanel.add(reportDescription,BorderLayout.CENTER);

		textPanel.setBorder(etched1);
		north.add(textPanel,BorderLayout.CENTER);
		setTopComponent(north);
				
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		center.add(editor,BorderLayout.CENTER);
		center.add(panel,BorderLayout.WEST);
		JScrollPane scroll = new JScrollPane(center);
				
		JPanel editorPanel = new JPanel();
		editorPanel.setLayout(new BorderLayout());
		editorPanel.add(scroll,BorderLayout.CENTER);
		
		setBottomComponent(editorPanel);
	}
	
	public String getReport() {
		return editor.getReportText();
	}
	
	public void reloadReport(String xml) {
		editor.reloadReport(xml);
	}
	
	public ReportDescription getDescription() {
		return reportDescription;
	}
}
