package com.kazak.smi.admin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;

public class LogMessage extends JFrame implements ActionListener, TreeSelectionListener {

	private static final long serialVersionUID = 1L;
	private String[] months = {
			"Enero","Febrero","Marzo","Abril","Mayo","Junio",
			"Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
	};
	
	private DefaultMutableTreeNode rootNode;
	private JTree jTree;
	private Hashtable<String,DefaultMutableTreeNode> years;
	private JButton JBView;
	private JButton JBClose;
	private String[] args = null;
	private String sqlCode = null;
	public LogMessage() {
		super("Registro de mensajes");
		this.setSize(400,500);
		this.setLayout(new BorderLayout());
		this.setLocationByPlatform(true);
		this.setLocationRelativeTo(null);
		rootNode = new DefaultMutableTreeNode(MainWindow.getAppOwner());
		rootNode.setAllowsChildren(true);
		jTree = new JTree(rootNode) {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				super.paintComponent(g);
			}
		};
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		renderer.setIcon(null);
		renderer.setBackgroundSelectionColor(Color.LIGHT_GRAY);
		jTree.setCellRenderer(renderer);
		jTree.addTreeSelectionListener(this);
		int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
		jTree.getSelectionModel().setSelectionMode(mode);
		
		JScrollPane jscroll = new JScrollPane(jTree);
		this.add(jscroll,BorderLayout.CENTER);
		years = new Hashtable<String, DefaultMutableTreeNode>();
		
		JBView = new JButton("Ver");
		JBClose = new JButton("Cerrar");
		JBView.setActionCommand("view");
		JBView.setEnabled(false);
		JBClose.setActionCommand("close");
		JBView.addActionListener(this);
		JBClose.addActionListener(this);
		JPanel jpsout = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jpsout.add(JBView);
		jpsout.add(JBClose);
		
		this.add(jpsout,BorderLayout.SOUTH);
		
	}
	
	public void display() {
		new Loader().start();
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("view".equals(command)) {
			if (sqlCode!=null && args!=null) {
				Worker worker = new Worker(sqlCode,args);
				worker.start();
			}
		}
		if ("close".equals(command)) {
			this.dispose();
		}
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = e.getPath();
		int patCount = path.getPathCount();
		String date1;
		String date2;
		
		switch (patCount) {
		case 2:
			date1 = path.getPathComponent(1)+"-01-01";
			date2 = path.getPathComponent(1)+"-12-31";
			args = new String[] {date1,date2};
			sqlCode = "SEL0018";
			break;
		case 3:
			int indexMonth = getIndexOfMonth(path.getPathComponent(2).toString());
			date1 = path.getPathComponent(1)+ "-" + indexMonth;
			date2 = path.getPathComponent(1)+ "-" + (indexMonth+1);
			args = new String[] {date1,date2};
			sqlCode = "SEL0019";
			break;
		case 4:
			date1 = 
				path.getPathComponent(1) + "-" + 
				getIndexOfMonth(path.getPathComponent(2).toString()) + "-" +
				path.getPathComponent(3);
			args = new String[] {date1};
			sqlCode = "SEL0020";
			break;
		default :
			args = null;
			sqlCode = null;
		}
		if (sqlCode!=null && args!=null) {
			JBView.setEnabled(true);
		}
		else {
			JBView.setEnabled(false);
		}
	}

	public void update() {
		Thread t = new Thread() {
			public void run() {
				jTree.updateUI();
			}
		};
		SwingUtilities.invokeLater(t);
	}
	private int getIndexOfMonth(String month) {
		for (int i=0; i < months.length ; i++) {
			if (months[i].equals(month)) {
				return (i+1);
			}
		}
		return -1;
	}
	
	class Loader extends Thread {
		public void run() {
			try {
				Document doc = QuerySender.getResultSetST("SEL0017",null);
				Element root = doc.getRootElement();
				Iterator rows = root.getChildren("row").iterator();
				while ( rows.hasNext() ) {
					Element element = (Element)rows.next();
					Iterator itCols = element.getChildren().iterator();
					String year = ((Element)itCols.next()).getText();
					String month = ((Element)itCols.next()).getText();
					String day = ((Element)itCols.next()).getText();
					String monthString = months[Integer.valueOf(month)-1];
					if (years.containsKey(year)) {
						DefaultMutableTreeNode tn = years.get(year);
						int nodes = tn.getChildCount();
						DefaultMutableTreeNode treeNode = null;
						int i = 0;
						for (; i < nodes ; i++) {
							treeNode = (DefaultMutableTreeNode) tn.getChildAt(i);
							if (treeNode.toString().equals(monthString)) {
								DefaultMutableTreeNode tnd = new DefaultMutableTreeNode(day);
								treeNode.add(tnd);
								break;
							}
						}
						if (i==nodes) {
							DefaultMutableTreeNode tnm = new DefaultMutableTreeNode(monthString);
							DefaultMutableTreeNode tnd = new DefaultMutableTreeNode(day);
							tnm.add(tnd);
							tn.add(tnm);
						}
					}
					else {
						DefaultMutableTreeNode tn = new DefaultMutableTreeNode(year);
						DefaultMutableTreeNode tnm = new DefaultMutableTreeNode(monthString);
						DefaultMutableTreeNode tnd = new DefaultMutableTreeNode(day);
						tn.add(tnm);
						tnm.add(tnd);
						years.put(year,tn);
						rootNode.add(tn);
					}
					update();
				}
			} catch (QuerySenderException e) {
				e.printStackTrace();
			}
		}
	}
	
	class Worker extends Thread {
		private String sqlCode;
		private String[] args;
		
		public Worker(String sqlCode , String[] args) {
			this.sqlCode = sqlCode;
			this.args = args;
		}
		
		public void run() {
			Document doc;
			try {
				doc = QuerySender.getResultSetST(sqlCode,args);
				Element root = doc.getRootElement();
				Iterator rows = root.getChildren("row").iterator();
				Vector<Vector<Object>> data = new Vector<Vector<Object>>(); 
				while (rows.hasNext()) {
					Vector<Object> v = new Vector<Object>();
					Element cols = (Element) rows.next();
					Iterator itCols = cols.getChildren().iterator();
					while (itCols.hasNext()) {
						Element e = (Element)itCols.next();
						v.add(e.getText());
					}
					data.add(v);
				}
				new MessageViewer(data);
			} catch (QuerySenderException e) {
				e.printStackTrace();
			}
		}
	}
	
}
