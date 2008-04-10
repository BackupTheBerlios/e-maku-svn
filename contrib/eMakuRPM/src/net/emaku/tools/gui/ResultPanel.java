package net.emaku.tools.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import net.emaku.tools.db.DataBaseManager;
import net.emaku.tools.gui.workspace.ResourcesTree;

public class ResultPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private Vector<Vector<String>> fData,qData,rData;
	private JPanel forms,queries,reports;
	private int total;
	private ReportManagerGUI frame;
	
	public ResultPanel(ReportManagerGUI frame,String space,String keywords) {
		super();
		this.frame = frame;
		this.setLayout(new BorderLayout());
		total = 0;
		tabbedPane = new JTabbedPane();
		if(!space.equals("Everything")) {
			if(space.equals("Forms")) {		
				setForms(keywords);
				tabbedPane.addTab(space, forms);
			}
			else if(space.equals("Queries")) {
				setQueries(keywords);
				tabbedPane.addTab(space, queries);
			}
			else if(space.equals("Reports")) {
				setReports(keywords);
				tabbedPane.addTab(space, reports);
			}
		}
		else {
			setForms(keywords);
			tabbedPane.addTab("Forms", forms);
			
			setQueries(keywords);
			tabbedPane.addTab("Queries", queries);
			
			setReports(keywords);
			tabbedPane.addTab("Reports", reports);
		}
		
		this.add(tabbedPane,BorderLayout.CENTER);
	}
	
	private JPanel setFormTable(int size,Vector<Vector<String>> data) {
		JLabel label = new JLabel(size + " results found");
		label.setHorizontalAlignment(JLabel.CENTER);
		JPanel up = new JPanel(new BorderLayout());
		up.add(label,BorderLayout.CENTER);
		FormsResultTable table = new FormsResultTable(data);
		table.addMouseListener(this);
		JScrollPane scroll = new JScrollPane(table);
		JPanel center = new JPanel(new BorderLayout());
		center.add(scroll);
		
		JPanel global = new JPanel(new BorderLayout());
		global.add(up,BorderLayout.NORTH);
		global.add(center,BorderLayout.CENTER);
		
		return global;
	}
	
	private JPanel setQueryTable(int size,Vector<Vector<String>> data) {
		JLabel label = new JLabel(size + " results found");
		label.setHorizontalAlignment(JLabel.CENTER);
		JPanel up = new JPanel(new BorderLayout());
		up.add(label,BorderLayout.CENTER);
		QueryResultTable table = new QueryResultTable(data);
		table.addMouseListener(this);
		JScrollPane scroll = new JScrollPane(table);
		JPanel center = new JPanel(new BorderLayout());
		center.add(scroll);
		
		JPanel global = new JPanel(new BorderLayout());
		global.add(up,BorderLayout.NORTH);
		global.add(center,BorderLayout.CENTER);
		
		return global;
	}
	
	private JPanel setReportTable(int size,Vector<Vector<String>> data) {
		JLabel label = new JLabel(size + " results found");
		label.setHorizontalAlignment(JLabel.CENTER);
		JPanel up = new JPanel(new BorderLayout());
		up.add(label,BorderLayout.CENTER);
		ReportResultTable table = new ReportResultTable(data);
		table.addMouseListener(this);
		JScrollPane scroll = new JScrollPane(table);
		JPanel center = new JPanel(new BorderLayout());
		center.add(scroll);
		
		JPanel global = new JPanel(new BorderLayout());
		global.add(up,BorderLayout.NORTH);
		global.add(center,BorderLayout.CENTER);
		
		return global;
	}
	
	private JPanel setEmptyMessage() {
		JPanel global = new JPanel(new BorderLayout());
		JTextArea info = new JTextArea("   Sorry, no results found");
		info.setEditable(false);
		global.add(info,BorderLayout.CENTER);
		return global;
	}
	
	private void setForms(String keywords) {
		forms = new JPanel(new BorderLayout());
		fData = DataBaseManager.getFormsEntries(keywords);
		int size = fData.size();
		if(total < size) {
			total = size;
		}
		if(size > 0) {
			forms = setFormTable(size,fData);
		} else {
			forms = setEmptyMessage();
		}
	}
	
	private void setQueries(String keywords) {
		queries = new JPanel(new BorderLayout());
		qData = DataBaseManager.getQueriesEntries(keywords);
		int size = qData.size();
		if(total < size) {
		   total = size;
		}	
		if(size > 0) {
			queries = setQueryTable(size,qData);
		} else {
			queries = setEmptyMessage();
		}
		
	}
	
	private void setReports(String keywords) {
		reports = new JPanel(new BorderLayout());
		rData = DataBaseManager.getReportsEntries(keywords);
		int size = rData.size();
		if(total < size) {
		   total = size;
		}	
		if(size > 0) {
			reports = setReportTable(size,rData);
		} else {
			reports = setEmptyMessage();
		}
	}
	
	public int getRecordsTotal() {
		return total;
	}

	public void mouseClicked(MouseEvent e) {
		if ( e.getClickCount() == 2 ) {
			JTable table = (JTable) e.getComponent();
			String node = table.getValueAt(table.getSelectedRow(), 0).toString();
			String category = ResourcesTree.searchCategory(node.trim());
			if (category != null) {
				frame.openObject(category,node);
			}
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
}
