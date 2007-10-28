package net.emaku.tools.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.emaku.tools.db.DataBaseManager;
import net.emaku.tools.xml.TemplateManager;
import net.emaku.tools.gui.workspace.ETabbedPane;
import net.emaku.tools.gui.workspace.ReportTree.SortableTreeNode;
import net.emaku.tools.gui.workspace.ButtonBar;
import net.emaku.tools.gui.workspace.ReportTree;
import net.emaku.tools.gui.workspace.WorkSpace;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import net.emaku.tools.jar.JarManager;

/*
 *  This class represents the main GUI of the report manager
 */

public class ReportManagerGUI extends JFrame {  

	private static final long serialVersionUID = 1L;
	private ButtonBar buttonBar;
	private ReportTree tree;
	private JScrollPane jscroll;
	private File root;
	public Hashtable<String,WorkSpace> editors;
	private ETabbedPane jtabbedPane;
	private Hashtable<String,String> codeQuerys = new Hashtable<String,String>();
	private static String reportRoot;
	private String currentCategory;
	public static String separator = System.getProperty("file.separator");
	private WorkSpace workspace;
	private Vector<String> updates = new Vector<String>();
    private static int MAX_WIN_SIZE_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static int MAX_WIN_SIZE_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	
	public ReportManagerGUI(String reportRoot) {
		ReportManagerGUI.reportRoot = reportRoot;
		this.setSize(800,600);
		this.setTitle("E-Maku Report Manager 1.0");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				exit();
			}
		});
		
		this.setLayout(new BorderLayout());
		this.setJMenuBar(new MainMenu(this));
		
		JSplitPane jsplitpane = new JSplitPane();
		jsplitpane.setDividerLocation(150);
		jsplitpane.setLeftComponent(getPanelList(reportRoot));
		jsplitpane.setRightComponent(getPanelSource());
		
		JarManager.init();
		TemplateManager.setFrame(this);
		
		this.editors = new Hashtable<String,WorkSpace>();
		this.getContentPane().add(jsplitpane,BorderLayout.CENTER);
        this.setLocation(
                (MAX_WIN_SIZE_WIDTH / 2) - this.getWidth() / 2,
                (MAX_WIN_SIZE_HEIGHT / 2) - this.getHeight() / 2);

		this.setVisible(true);
	}
	
	public JScrollPane getPanelList() {
		jscroll = new JScrollPane();
		SortableTreeNode rootNode = new SortableTreeNode("eMaku Reports");
		tree = new ReportTree(this,rootNode);
		jscroll.setViewportView(tree);
		return jscroll;
	}
		
	public JScrollPane getPanelList(String path) {

		root = new File(path);
		jscroll = new JScrollPane();
		SortableTreeNode rootNode = new SortableTreeNode("eMaku Reports");
    	
		tree = new ReportTree(this,rootNode);
        tree.loadRoot(root);
		jscroll.setViewportView(tree);
		
		return jscroll;
	}
	
	
	public File getRootPath() {
		return root;
	}
	
	public static String getPathString() {
		return reportRoot;
	}
	
	public JPanel getPanelSource() {
		JPanel jpanel = new JPanel(new BorderLayout());
		jtabbedPane = new ETabbedPane(this);
		jpanel.add(jtabbedPane,BorderLayout.CENTER);
		
		JPanel jpsouth	= new JPanel(new BorderLayout());	
		buttonBar = new ButtonBar(this);
		jpsouth.add(buttonBar,BorderLayout.EAST);
		
		jpanel.add(jpsouth,BorderLayout.SOUTH);
		
		return jpanel;
	}
	
	public void removeTabFromHash(Object key) {
		System.out.println("Removing report " + key);
	    editors.remove(key);
	}	  
	
	public void exit() {
		int paneOption = JOptionPane.YES_NO_OPTION;
		String message = "Do you want to exit?";
		int op = JOptionPane.showConfirmDialog(this,message,"Exit",paneOption);
		if (op == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	public String loadXML(String path) {
		path = root.getAbsolutePath() + path;
		System.out.println("PATH from loadXML: " + path);
		String str = "";
		try {
			File file = new File(path);
			Document doc = null;
			RandomAccessFile input = new RandomAccessFile(file,"rw");
			long fileLength = input.length();
			
			if (fileLength == 0) {
				return str;
			}
			
			try {
				SAXBuilder build = new SAXBuilder(false);
				doc = build.build(path);
			}
			catch (JDOMException e) {
				JOptionPane.showMessageDialog(this,"ERROR: File template is misconfigured!");
				e.printStackTrace();
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return null;
			}
			String sqlCode = doc.getRootElement().getChildText("sqlCode");
			codeQuerys.put(path,sqlCode);
			XMLOutputter out = new XMLOutputter();
			ByteArrayOutputStream bou = new ByteArrayOutputStream();
			out.output(doc,bou);
			bou.close();
			byte [] bytes = bou.toByteArray();
			str = new String(bytes);
	    } catch (IOException IOEe) {
            return null;
		}
		
		return str;
	}
	
	public void loadNewReport(String reportCode, TreeNode treeNode) {

		String xmlTemplate = ""; 
		System.out.println("New report: " + reportCode);
		String sqlCode = reportCode.substring(reportCode.length()-3,reportCode.length());
		try {
			String prefix = "CRE";
			if(reportCode.startsWith("REP")) {
				prefix = "REP";
			}
			File file = new File("root_reports" + separator + currentCategory + separator + prefix + "0000.xml");
			RandomAccessFile input = new RandomAccessFile(file,"rw");
			String line = new String();
			while ((line = input.readLine())!=null){
				if(line.startsWith("<code>")) {
				   line = "<code>" + reportCode + "</code>";
				}
				if(line.startsWith("<sqlCode>")) {
					if (prefix.equals("REP")) {
						sqlCode = "SRP" + sqlCode;
						line = "<sqlCode>" + sqlCode + "</sqlCode>";
					} else {
						sqlCode = "CRP" + sqlCode;
						line = "<sqlCode>" + sqlCode + "</sqlCode>";
					}
				}				
				xmlTemplate += line+"\n";
			}
			input.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("Updating report record in database...I");
		setDatabaseRecords(sqlCode,reportCode);
		tree.addNewReport(reportCode,treeNode);
		String reportPath = separator+currentCategory+separator+reportCode;
		workspace = new WorkSpace(reportCode,xmlTemplate);
		jtabbedPane.addTab(reportPath,workspace);
		editors.put(reportPath,workspace);	
		jtabbedPane.setSelectedIndex(jtabbedPane.getTabCount()-1); 

		if (jtabbedPane.getTabCount() == 1) {
			setButtonsState(true);
		}

		updateGUI();
		jtabbedPane.setSelectedIndex(jtabbedPane.getTabCount()-1);		
		System.out.println("*** Saving new report...");
		saveReport();
	}
	
	private void setDatabaseRecords(String sqlCode,String reportCode) {
		DataBaseManager.connect();
		System.out.println("Updating report record in database...II");
		String sentenceID = DataBaseManager.getSQLId(sqlCode);
		if(sentenceID.equals("NO_ID")) {
			boolean ok = DataBaseManager.insertSQLRecord(sqlCode);
			if(ok) {
				sentenceID = DataBaseManager.getSQLId(sqlCode);
			} else {
				System.out.println("ERROR: Could not get the SQL Sentence Id!");
			}
		}
		if(!DataBaseManager.existsRecord(reportCode)) {
			boolean ok = DataBaseManager.insertReportRecord(reportCode,sentenceID);
			if(!ok) {
				System.out.println("ERROR: Report record could not be inserted!");
			}
		}
	}
	
    public void saveReport() {
    	try {
    		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    		String key = jtabbedPane.getTitleAt(jtabbedPane.getSelectedIndex());
    		String path = root.getAbsolutePath()+key+".xml";
    		System.out.println("PATH from saveFile: " + path);

    		WorkSpace ws = editors.get(key);
    		String str = ws.getReport();
    		ws.getDescription().updateDescription();
    		
    		byte [] bytes = str.getBytes();
    		FileOutputStream fos = new FileOutputStream(path);
    		fos.write(bytes);
    		fos.close();

    		updates.add(key);
    		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    	}
    	catch (ArrayIndexOutOfBoundsException AIOOFEe) {	
    		AIOOFEe.printStackTrace();
    	}
    	catch (FileNotFoundException FNFEe) {
    		FNFEe.printStackTrace();
    	}
    	catch (IOException IOEe) {
    		IOEe.printStackTrace();
    	}
    }	
	
	
	public void loadReport(String category, String reportCode) {
		String reportPath = "/" + category + "/" + reportCode;	
		System.out.println("PATH from loadReport: " + reportPath);
		String report = loadXML(reportPath + ".xml");
		
		if(report == null) {
			return;
		}
		
		workspace = new WorkSpace(reportCode,report);
		jtabbedPane.addTab(reportPath,workspace);
		editors.put(reportPath,workspace);	
		jtabbedPane.setSelectedIndex(jtabbedPane.getTabCount()-1);
	}
		
	public void updateGUI() {
		tree.updateUI();
		jtabbedPane.updateUI();		
	}	
	
	public boolean containsReport(String path) {
		return editors.containsKey(path);
	} 
	
	public void addTab() {
		jtabbedPane.plusTab();
	}
	
	public int tabCount() {
		return jtabbedPane.getTabCount();
	}
	
	public String getTabTitle(int i) {
		return jtabbedPane.getTitleAt(i);
	}
	
	public void setActiveTab(int i) {
		jtabbedPane.setSelectedIndex(i);
	}
	
	public void closeTab() {
		System.out.println("COUNT: " + jtabbedPane.getTabCount());
		if (jtabbedPane.getTabCount() > 0) { 
			removeTabFromHash(jtabbedPane.getTitleAt(jtabbedPane.getSelectedIndex()));
			jtabbedPane.removeTabAt(jtabbedPane.getSelectedIndex());
			if(jtabbedPane.getTabCount() == 0) {
			   setButtonsState(false);
			}	
		}
	}
	
	public void previewReport() {
		DataBaseManager.connect();
		String path = jtabbedPane.getTitleAt(jtabbedPane.getSelectedIndex());
		System.out.println("PATH from preview: " + path);
		
		WorkSpace ws = editors.get(path);
		String str = ws.getReport();
		
		byte[] bytes = null;			
		if (str != null) {
	    	bytes = str.getBytes();
		}

		TemplateManager.showGenericReportPreview(new ByteArrayInputStream(bytes));
		DataBaseManager.close();
	}
	
	public void reloadReport() {
		String key = jtabbedPane.getTitleAt(jtabbedPane.getSelectedIndex());
		String path = reportRoot+key+".xml";
		System.out.println("PATH from reload: " + path);
		WorkSpace ws = editors.get(path);
		if (ws != null) {
			String str = loadXML(path);
			ws.reloadReport(str);
		} else {
			System.out.println("Problemas...");
		}
	}
	
	public void openQuery() {
		Thread thread = new Thread() {
			public void run() {
				
				String report = jtabbedPane.getTitleAt(jtabbedPane.getSelectedIndex());
				String prefix = report.substring(report.length() - 7, report.length()-4);
				String flag = "SRP";
				
			    if (prefix.equals("CRE"))
			    	flag = "CRP";
			    	
				String key = report.substring(report.length() - 3, report.length());
				System.out.println("Query: " + flag + key);
				
				DataBaseManager.connect();
				String sqlCode = flag + key; 
				String query = DataBaseManager.getQuery(sqlCode);
				DataBaseManager.close();
				QueryEditor editor = new QueryEditor(true,ReportManagerGUI.this,query,sqlCode);
				editor.setSize(700,500);
				editor.setLocationRelativeTo(ReportManagerGUI.this);
				editor.setVisible(true);
			}
		};
		thread.start();	
	}
	
	public void setButtonsState(boolean flag) {
		buttonBar.setButtonsState(flag);
	}
		
	private void exportModifiedReports() {
		ExportBar exportBar = new ExportBar(this,updates);
		exportBar.setSize(480,150);
		exportBar.setLocationRelativeTo(ReportManagerGUI.this);
		exportBar.setVisible(true);	
		updates.clear();
	}

	public void exportAllReports() {
		final DefaultMutableTreeNode rootNode = tree.getRoot();
		ExportBar exportBar = new ExportBar(ReportManagerGUI.this,rootNode,getReportCount());
		exportBar.setSize(480,150);
		exportBar.setLocationRelativeTo(ReportManagerGUI.this);
		exportBar.setVisible(true);			
	}
	
	private int getReportCount() {
		DefaultMutableTreeNode rootNode = tree.getRoot();
		int max = rootNode.getChildCount();
		int total = 0;
		for (int i=0; i < max ; i++) {
			TreeNode tn = rootNode.getChildAt(i);
			int maxGroups = tn.getChildCount();
			for (int j=0; j < maxGroups; j++) {
				TreeNode branch = tn.getChildAt(j);
				int maxChilds = branch.getChildCount();
				total += maxChilds;
			}
		}
		return total;
	}
	
	public void updateReportsJar() {
		//System.out.println("Jar Size: " + JarManager.getJarSize() + " - Tree Size: " + getReportCount());

		if (JarManager.getJarSize() != getReportCount()) {
			int paneOption = JOptionPane.YES_NO_OPTION;
			String message = "The file reports.jar does not contain all the reports designed." + 
			"\nDo you want to rebuild the jar file?";
			int op = JOptionPane.showConfirmDialog(this,message,"Jar File",paneOption);
			if (op == JOptionPane.YES_OPTION) {
				exportAllReports();
			} 
		} else {
			if (updates.size() > 0) {
				exportModifiedReports();
			} else {
				JOptionPane.showMessageDialog(this,"There Jar File is update. Nothing to export.");
			}
		}
	}
	
	public void resetJarFile() {
		String msg = "There was a problem cleaning the Jar File";
		if (JarManager.clean()) {
			msg = "The Jar File was cleaned successfully!";
		}
		JOptionPane.showMessageDialog(this,msg);
	}
	
	public void setCurrentCategory(String category) {
		currentCategory = category;
	}
		
}
