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
import javax.swing.JTabbedPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.emaku.tools.db.DataBaseManager;
import net.emaku.tools.structures.FormsData;
import net.emaku.tools.xml.TemplateManager;
import net.emaku.tools.gui.workspace.ETabbedPane;
import net.emaku.tools.gui.workspace.ResourcesTree.SortableTreeNode;
import net.emaku.tools.gui.workspace.FormButtonBar;
import net.emaku.tools.gui.workspace.ReportButtonBar;
import net.emaku.tools.gui.workspace.FormWorkSpace;
import net.emaku.tools.gui.workspace.ResourcesTree;
import net.emaku.tools.gui.workspace.ReportWorkSpace;

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
	public final int REPORT = 0;
	public final int FORM = 1; 
	private ReportButtonBar reportButtonBar;
	private FormButtonBar formButtonBar;
	private ResourcesTree tree;
	private JScrollPane jscroll;
	private File root;
	public Hashtable<String,ReportWorkSpace> reportEditors;
	public Hashtable<String,FormWorkSpace> formEditors;
	private ETabbedPane reportsTab, formsTab;
	private Hashtable<String,String> codeQuerys = new Hashtable<String,String>();
	private static String reportRoot;
	private String currentCategory;
	public static String separator = System.getProperty("file.separator");
	private ReportWorkSpace reportWorkSpace;
	private FormWorkSpace formWorkSpace;
	private JTabbedPane global;
	
	private Vector<String> updates = new Vector<String>();
    private static int MAX_WIN_SIZE_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static int MAX_WIN_SIZE_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	
	public ReportManagerGUI(String reportRoot) {
		ReportManagerGUI.reportRoot = reportRoot;
		this.setSize(800,600);
		this.setTitle("E-Maku Resources Manager 1.0");
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
		
		this.reportEditors = new Hashtable<String,ReportWorkSpace>();
		this.formEditors = new Hashtable<String,FormWorkSpace>();
		
		this.getContentPane().add(jsplitpane,BorderLayout.CENTER);
        this.setLocation(
                (MAX_WIN_SIZE_WIDTH / 2) - this.getWidth() / 2,
                (MAX_WIN_SIZE_HEIGHT / 2) - this.getHeight() / 2);

		this.setVisible(true);
	}
	
	public JScrollPane getPanelList() {
		jscroll = new JScrollPane();
		SortableTreeNode rootNode = new SortableTreeNode("eMaku Resources");
		tree = new ResourcesTree(this,rootNode);
		jscroll.setViewportView(tree);
		return jscroll;
	}
		
	public JScrollPane getPanelList(String path) {

		root = new File(path);
		jscroll = new JScrollPane();
		SortableTreeNode rootNode = new SortableTreeNode("eMaku Resources");
    	
		tree = new ResourcesTree(this,rootNode);
        tree.loadReports(root);
        tree.loadForms();
		tree.expandRow(0);
		System.gc();

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
		reportsTab = new ETabbedPane(this,REPORT);
		formsTab = new ETabbedPane(this,FORM);
		
		JPanel reportsPanel = new JPanel(new BorderLayout());
		reportButtonBar = new ReportButtonBar(this);
		JPanel jpSouth	= new JPanel(new BorderLayout());
		jpSouth.add(reportButtonBar,BorderLayout.EAST);
		reportsPanel.add(reportsTab,BorderLayout.CENTER);
		reportsPanel.add(jpSouth,BorderLayout.SOUTH);
		
		JPanel formsPanel = new JPanel(new BorderLayout());
		formButtonBar = new FormButtonBar(this);
		JPanel tempo	= new JPanel(new BorderLayout());
		tempo.add(formButtonBar,BorderLayout.EAST);
		formsPanel.add(formsTab,BorderLayout.CENTER);
		formsPanel.add(tempo,BorderLayout.SOUTH);		

		global = new JTabbedPane();
		global.addTab("REPORTS",reportsPanel);
		global.addTab("FORMS",formsPanel);
		
		jpanel.add(global,BorderLayout.CENTER);
				
		return jpanel;
	}
	
	public void setActiveResource(int resource) {
		global.setSelectedIndex(resource);
	}

	public void removeObjectTabFromHash(int resource,Object key) {
		if (resource == REPORT) {
			reportEditors.remove(key);
		} else {
			formEditors.remove(key);			
		}
	}	  
	
	public void exit() {
		int paneOption = JOptionPane.YES_NO_OPTION;
		String message = "Do you want to exit?";
		int op = JOptionPane.showConfirmDialog(this,message,"Exit",paneOption);
		if (op == JOptionPane.YES_OPTION) {
			DataBaseManager.close();
			System.exit(0);
		}
	}
	
	public void conf() {
		new SettingsDialog(this);
	}
	
	public String loadXML(String path) {
		path = root.getAbsolutePath() + path;
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
		setDatabaseRecords(sqlCode,reportCode);
		tree.addNewReport(reportCode,treeNode);
		String reportPath = separator+currentCategory+separator+reportCode;
		reportWorkSpace = new ReportWorkSpace(this,reportCode,xmlTemplate);
		reportsTab.addTab(reportPath,reportWorkSpace);
		reportEditors.put(reportPath,reportWorkSpace);	
		reportsTab.setSelectedIndex(reportsTab.getTabCount()-1); 

		if (reportsTab.getTabCount() == 1) {
			setReportButtonsState(REPORT,true);
		}

		updateGUI();
		reportsTab.setSelectedIndex(reportsTab.getTabCount()-1);		
		saveReport();
	}
	
	private void setDatabaseRecords(String sqlCode,String reportCode) {
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
    		String key = reportsTab.getTitleAt(reportsTab.getSelectedIndex());
    		String path = root.getAbsolutePath()+key+".xml";

    		ReportWorkSpace ws = reportEditors.get(key);
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
	
    public void saveForm() {
    	String key = formsTab.getTitleAt(formsTab.getSelectedIndex());
    	String[] parser = key.split("/");
    	FormsData data = formWorkSpace.getData();
    	if(parser[2] != null) {
    		DataBaseManager.updateForm(parser[2],data);
    	}
    }
	
	public void loadReport(String category, String reportCode) {
		String reportPath = "/" + category + "/" + reportCode;	
		String report = loadXML(reportPath + ".xml");
		
		if(report == null) {
			return;
		}
		
		reportWorkSpace = new ReportWorkSpace(this,reportCode,report);
		reportsTab.addTab(reportPath,reportWorkSpace);
		reportEditors.put(reportPath,reportWorkSpace);	
		reportsTab.setSelectedIndex(reportsTab.getTabCount()-1);
	}

	public void loadForm(String category, String formCode) {
		String formPath = "/" + category + "/" + formCode;	
		FormsData form = DataBaseManager.getForm(formCode);

		formWorkSpace = new FormWorkSpace(formCode,form);
		formsTab.addTab(formPath,formWorkSpace);
		formEditors.put(formPath,formWorkSpace);	
		formsTab.setSelectedIndex(formsTab.getTabCount()-1);
	}
	
	public void updateGUI() {
		tree.updateUI();
		reportsTab.updateUI();		
	}	
	
	public boolean containsObject(int resource, String path) {
		if (resource == REPORT) {
			return reportEditors.containsKey(path);
		} else {
			return formEditors.containsKey(path);
		}
	} 

	public void addObjectTab(int resource) {
		if (resource == REPORT) {
			reportsTab.plusTab();
		} else {
			formsTab.plusTab();
		}
	}
	
	public int objectTabCount(int resource) {
		if (resource == REPORT) {
			return reportsTab.getTabCount();
		} else {
			return formsTab.getTabCount();
		}
	}
	
	public String getObjectTabTitle(int resource,int index) {
		if (resource == REPORT) {
			return reportsTab.getTitleAt(index);
		} else {
			return formsTab.getTitleAt(index);
		}
	}
	
	public void setActiveObjectTab(int resource,int index) {
		if (resource == REPORT) {
			reportsTab.setSelectedIndex(index);
		} else {
			formsTab.setSelectedIndex(index);
		}
	}
	
	public void closeObjectTab(int resource) {
		ETabbedPane tab = reportsTab;
		if (resource == FORM) {
			tab = formsTab;
		} 		
		if (tab.getTabCount() > 0) { 
			removeObjectTabFromHash(resource,tab.getTitleAt(tab.getSelectedIndex()));
			tab.removeTabAt(tab.getSelectedIndex());
			if(tab.getTabCount() == 0) {
				setReportButtonsState(resource,false);
			}	
		}
	}
	
	public void previewReport() {
		String path = reportsTab.getTitleAt(reportsTab.getSelectedIndex());		
		ReportWorkSpace ws = reportEditors.get(path);
		String str = ws.getReport();
		
		byte[] bytes = null;			
		if (str != null) {
	    	bytes = str.getBytes();
		}

		TemplateManager.showGenericReportPreview(new ByteArrayInputStream(bytes));
	}
	
	public void previewForm() {
		// This method execute the xml form and produce a graphical form
	}
	
	public void reloadReport() {
		String key = reportsTab.getTitleAt(reportsTab.getSelectedIndex());
		String path = reportRoot+key+".xml";
		ReportWorkSpace ws = reportEditors.get(path);
		if (ws != null) {
			String str = loadXML(path);
			ws.reloadReport(str);
		} else {
			System.out.println("Problems at reloadReport()...");
		}
	}
	
	public void reloadForm() {
		String key = formsTab.getTitleAt(formsTab.getSelectedIndex());
    	String[] parser = key.split("/");
    	if(parser[2] != null) {
    		FormsData data = DataBaseManager.getForm(parser[2]);
    		FormWorkSpace ws = formEditors.get(key);
    		ws.reloadForm(data);
    	}
	}

	public void openQuery() {
		Thread thread = new Thread() {
			public void run() {			
				String report = reportsTab.getTitleAt(reportsTab.getSelectedIndex());
				String prefix = report.substring(report.length() - 7, report.length()-4);
				String flag = "SRP";
			    if (prefix.equals("CRE"))
			    	flag = "CRP";
			    	
				String key = report.substring(report.length() - 3, report.length());				
				String sqlCode = flag + key; 
				String query = DataBaseManager.getQuery(sqlCode);
				QueryEditor editor = new QueryEditor(true,ReportManagerGUI.this,query,sqlCode);
				editor.setSize(700,500);
				editor.setLocationRelativeTo(ReportManagerGUI.this);
				editor.setVisible(true);
			}
		};
		thread.start();	
	}
	
	public void setReportButtonsState(int resource, boolean flag) {
		if (resource == REPORT) {
			reportButtonBar.setButtonsState(flag);
		} else {
			formButtonBar.setButtonsState(flag);
		}
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