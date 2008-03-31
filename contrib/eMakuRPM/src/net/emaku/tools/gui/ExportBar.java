package net.emaku.tools.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

//import net.emaku.tools.db.DataBaseManager;
import net.emaku.tools.jar.JarManager;
import net.emaku.tools.xml.TemplateManager;

// Dialog displayed when reports are being exported to the JAR file

public class ExportBar extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static JProgressBar progress;
	private static JTextPane infoArea;
	private static StyledDocument doc;
	private static JButton close;
	private String separator = System.getProperty("file.separator");
	private Thread thread;
	private int error = 0;
	
	public ExportBar(ReportManagerGUI frame, DefaultMutableTreeNode root, int total) {
		super(frame,true);		
		initInterface();
		processAll(root,total);
	}
	
	public ExportBar(ReportManagerGUI frame, Vector<String> modified) {
		super(frame,true);		
		initInterface();
		processModified(modified);
	}
	
	private void initInterface() {
		setTitle("Exporting Reports...");
		setLayout(new BorderLayout());
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		
		progress = new JProgressBar();
		progress.setStringPainted(true);
		
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());
		north.add(progress,BorderLayout.CENTER);
		north.setSize(200,30);

		infoArea = new JTextPane();
		infoArea.setEditable(false);
		doc = infoArea.getStyledDocument();
		addStylesToDocument();
		JScrollPane scroll = new JScrollPane(infoArea);
		
		close = new JButton("Close");
		close.setActionCommand("close");
		close.addActionListener(this);
		close.setEnabled(false);
		
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		south.add(close,BorderLayout.EAST);
		
		JPanel general = new JPanel();
		general.setLayout(new BorderLayout());
		general.add(north,BorderLayout.NORTH);
		general.add(scroll,BorderLayout.CENTER);
		general.add(south,BorderLayout.SOUTH);
		
		getContentPane().add(general,BorderLayout.CENTER);
	}
	
	private void processAll(DefaultMutableTreeNode root, int sum) {
		final int total = sum;
		final DefaultMutableTreeNode rootNode = root;
		final int max = rootNode.getChildCount();
		final String reportRoot = ReportManagerGUI.getPathString();

		thread = new Thread() {
			public void run() {
				try {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					setLimits(0, total);
					addRecord("* Starting export process...");
					String[] cursor = {"|","/","--","\\"};
					int p = 0;

					for (int i=0; i < max ; i++) {
						TreeNode tn = rootNode.getChildAt(i);
						int maxChilds = tn.getChildCount();
						String category = tn.toString();
						addRecord("* Exporting category {"+category+"}");

						for (int j=0; j < maxChilds; j++) {	
							if(error == 2) {
								setProcessEnd();
								return;
							}
							TreeNode branch = tn.getChildAt(j);
							int maxGroups = branch.getChildCount();
							for (int k=0; k < maxGroups; k++) {
								updateBar(cursor[p]);
								if (p<=2) {
									p++;
								} else {
									p=0;
								}
								String report = branch.getChildAt(k).toString();
								error = TemplateManager.createReport(reportRoot+separator+
										category+separator+report);
							}
						}
					}
					setProcessEnd();
				} catch(Exception e) {
					System.out.println("Error processing thread...");
				}					
			}
		};
		thread.start();
	}
	
	private void processModified(Vector<String> modified) {
		final Vector<String> updates = modified;
		final int max = updates.size();
		final String root = ReportManagerGUI.getPathString();
		
		thread = new Thread() {
			public void run() {
				setLimits(0, max);
				addRecord("* Starting export process...");
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				for (int i=0; i < max ; i++) {
					if(error == 2) {
						setProcessEnd();
						return;
					}
					String report = updates.get(i);
					setLimits(0, max);
					setTitle("Exporting Report "+report);
					updateBar(report);
					error = TemplateManager.createReport(root+separator+report);
				}
				setProcessEnd();
			}
		};
		thread.start();
	}
	
	private void setProcessEnd() {
		JarManager.createFile();
		updateBar("");
		setTitle("DONE");
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
	}
	
	public void setLimits(int min, int max) {
		progress.setMinimum(min);
		progress.setMaximum(max);
	}
	
	public void updateBar(String report) {
		progress.setValue(progress.getValue()+1);
		progress.setString(report);
	}
	
	public static void activeCloseButton() {
		close.setEnabled(true);
	}
	
	public static void addRecord(String record) {
		try {
			if(record.startsWith("ERROR")) {
				doc.insertString(doc.getLength(),record + "\n",doc.getStyle("bold"));
			} else {
				doc.insertString(doc.getLength(),record + "\n",doc.getStyle("regular"));
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception at addRecord method");
		}
		
		int textLength = infoArea.getDocument().getLength();
		if(textLength > 0) {
			infoArea.setCaretPosition(textLength - 1);
		} 
	}
	
    protected void addStylesToDocument() {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
    }
		
	public void close() {
		if(thread.isAlive()) {
			error = 2;
		}
		JarManager.clean();
		dispose();
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ("close".equals(action)) {
			close();
		}
	}
	
}
