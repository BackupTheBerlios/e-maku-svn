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
	private JButton viewButton;
	private JButton closeButton;
	private String[] argsArray = null;
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
		
		viewButton = new JButton("Ver");
		closeButton = new JButton("Cerrar");
		viewButton.setActionCommand("view");
		viewButton.setEnabled(false);
		closeButton.setActionCommand("close");
		viewButton.addActionListener(this);
		closeButton.addActionListener(this);
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(viewButton);
		southPanel.add(closeButton);
		
		this.add(southPanel,BorderLayout.SOUTH);
		
	}
	
	public void display() {
		new Loader().start();
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("view".equals(command)) {
			if (sqlCode!=null && argsArray!=null) {
				Worker worker = new Worker(sqlCode,argsArray);
				worker.start();
			}
		}
		if ("close".equals(command)) {
			this.dispose();
		}
	}
	
	public void valueChanged(TreeSelectionEvent event) {
		TreePath path = event.getPath();
		int pathCount = path.getPathCount();
		String date1;
		String date2;
		
		switch (pathCount) {
			case 2:
				date1 = path.getPathComponent(1)+"-01-01";
				date2 = path.getPathComponent(1)+"-12-31";
				argsArray = new String[] {date1,date2};
				sqlCode = "SEL0018";
				break;
			case 3:
				int monthIndex = getIndexOfMonth(path.getPathComponent(2).toString());
				date1 = path.getPathComponent(1)+ "-" + monthIndex;
				date2 = path.getPathComponent(1)+ "-" + (monthIndex+1);
				argsArray = new String[] {date1,date2};
				sqlCode = "SEL0019";
				break;
			case 4:
				date1 = path.getPathComponent(1) + "-" + 
						getIndexOfMonth(path.getPathComponent(2).toString()) + "-" +
						path.getPathComponent(3);
				argsArray = new String[] {date1};
				sqlCode = "SEL0020";
				break;
			default :
				argsArray = null;
				sqlCode = null;
		}
		
		if (sqlCode!=null && argsArray!=null) {
			viewButton.setEnabled(true);
		}
		else {
			viewButton.setEnabled(false);
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
				Document doc = QuerySender.getResultSetFromST("SEL0017",null);
				Element root = doc.getRootElement();
				Iterator rows = root.getChildren("row").iterator();
				while ( rows.hasNext() ) {
					Element element = (Element)rows.next();
					Iterator columnsIterator = element.getChildren().iterator();
					String year = ((Element)columnsIterator.next()).getText();
					String month = ((Element)columnsIterator.next()).getText();
					String day = ((Element)columnsIterator.next()).getText();
					String monthString = months[Integer.valueOf(month)-1];
					if (years.containsKey(year)) {
						DefaultMutableTreeNode mutableTreeNode = years.get(year);
						int nodes = mutableTreeNode.getChildCount();
						DefaultMutableTreeNode treeNode = null;
						int i = 0;
						for (; i < nodes ; i++) {
							treeNode = (DefaultMutableTreeNode) mutableTreeNode.getChildAt(i);
							if (treeNode.toString().equals(monthString)) {
								DefaultMutableTreeNode mutableTreeNode2 = new DefaultMutableTreeNode(day);
								treeNode.add(mutableTreeNode2);
								break;
							}
						}
						if (i==nodes) {
							DefaultMutableTreeNode mutableTreeNode2 = new DefaultMutableTreeNode(monthString);
							DefaultMutableTreeNode mutableTreeNode3 = new DefaultMutableTreeNode(day);
							mutableTreeNode2.add(mutableTreeNode3);
							mutableTreeNode.add(mutableTreeNode2);
						}
					}
					else {
						DefaultMutableTreeNode mutableTreeNode  = new DefaultMutableTreeNode(year);
						DefaultMutableTreeNode mutableTreeNode1 = new DefaultMutableTreeNode(monthString);
						DefaultMutableTreeNode mutableTreeNode2 = new DefaultMutableTreeNode(day);
						mutableTreeNode.add(mutableTreeNode1);
						mutableTreeNode1.add(mutableTreeNode2);
						years.put(year,mutableTreeNode);
						rootNode.add(mutableTreeNode);
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
		
		public Worker(String sqlCode , String[] argsArray) {
			this.sqlCode = sqlCode;
			this.args = argsArray;
		}
		
		public void run() {
			Document doc;
			try {
				doc = QuerySender.getResultSetFromST(sqlCode,args);
				Element root = doc.getRootElement();
				Iterator rows = root.getChildren("row").iterator();
				Vector<Vector<Object>> data = new Vector<Vector<Object>>(); 
				while (rows.hasNext()) {
					Vector<Object> vector = new Vector<Object>();
					Element columns = (Element) rows.next();
					Iterator iterator = columns.getChildren().iterator();
					while (iterator.hasNext()) {
						Element element = (Element)iterator.next();
						vector.add(element.getText());
					}
					data.add(vector);
				}
				new MessageViewer(data);
			} catch (QuerySenderException e) {
				e.printStackTrace();
			}
		}
	}
	
}
