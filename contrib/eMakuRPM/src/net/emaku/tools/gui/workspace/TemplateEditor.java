package net.emaku.tools.gui.workspace;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.Ostermiller.Syntax.*;
import net.emaku.tools.gui.CopyInterface;

// This is the XML Editor for the workspace

public class TemplateEditor extends JTextPane implements ActionListener, MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private final int INIT = -1;
	private final int SETTINGS = 0;
	private final int HEADER   = 1;
	private final int COLUMNS  = 2;
	private final int RECORDS  = 3;
	private final int FOOTER   = 4;
	
	private Object style = HighlightedDocument.HTML_KEY_STYLE;
	private HighlightedDocument document = new HighlightedDocument();
	private int lastSection = INIT;
	private int currentSection = INIT;
	private String[] contents = new String[5];
	private int currentIndex = 0;
	private int lines[] = new int[5]; 
	private NumbersPanel numberPanel;

	public TemplateEditor(String xml) {
		loadReport(xml);
		document.setHighlightStyle(style);
		setDocument(document);	
		addMouseListener(this);
		addKeyListener(this);
		requestFocus();
	}
	
	public void setNumberPanel(NumbersPanel numberPanel) {
		this.numberPanel = numberPanel;
	}
	
	private void initSections() {
		for(int i=0;i<contents.length;i++) {
			contents[i] = "";
		}
		/*
		settings = "";
		header = "";
		columns = "";
		records = "";
		footer = "";
		*/
	}

	private void loadReport(String xml) {
		initSections();
		int k=0;
		String copy = xml;
		String[] firstLines = new String[3];
		while(k<3) {
			int index = copy.indexOf('>');
			firstLines[k] = copy.substring(0,index+1);
			copy = copy.substring(index+1,copy.length());
			contents[SETTINGS] += firstLines[k]; 
			k++;
		}
		contents[SETTINGS] += "\n";
		
		try {
			SAXBuilder builder = new SAXBuilder(false);
			//System.out.println("XML: " + xml);
			Document doc = builder.build(new StringReader(xml));
			Element root = doc.getRootElement();
			XMLOutputter xmlOutputter = new XMLOutputter();
			xmlOutputter.setFormat(Format.getPrettyFormat());		
			Iterator i = root.getChildren().iterator();

			while( i.hasNext() ) {
				Element element = (Element) i.next();
				String name = element.getName();
				if (!name.equals("pageHeader")) {
					contents[SETTINGS] += xmlOutputter.outputString(element) + "\n";
				} else {
					contents[HEADER] += xmlOutputter.outputString(element) + "\n";
					break;
				}
			}		
			
			contents[COLUMNS] = xmlOutputter.outputString(root.getChild("columnHeader"));
			contents[RECORDS] = xmlOutputter.outputString(root.getChild("detail"));
			contents[FOOTER] = xmlOutputter.outputString(root.getChild("columnFooter")) + "\n";
			contents[FOOTER] += xmlOutputter.outputString(root.getChild("pageFooter")) + "\n";
			
			Element summary = root.getChild("summary");
			if(summary != null) {
				contents[FOOTER] += xmlOutputter.outputString(summary);
			}
			contents[FOOTER] += "</jasperReport>";
		
			for(int j=0;j<lines.length;j++) {
				lines[j] = getSectionLines(contents[j]);
			}
									
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setSection(int section) {
		System.out.println("SECTION: " + section);
		lastSection = currentSection;
		updateSection(lastSection);

		if(section == -1) {
			setText("");
			currentSection = INIT;
			numberPanel.setText("");
		} else {
			int index = 0;
			int i = 0;
			if(section!=0) {
				for(i=0;i<section;i++) {
					index += lines[i];
				}
			}
			int range = index + lines[i];
			setText(contents[section]);
			currentSection = section;
			numberPanel.setRange(index,range);
		}

		setCaretPosition(0);
	}

	private void updateSection(int lastSection) {
		if (lastSection == SETTINGS) {
			contents[SETTINGS] = getText();
		} else if (lastSection == HEADER) {
			contents[HEADER] = getText();
		} else if (lastSection == COLUMNS) {
			contents[COLUMNS] = getText();
		} else if (lastSection == RECORDS) {
			contents[RECORDS] = getText();
		} else if (lastSection == FOOTER) {
			contents[FOOTER] = getText();
		} 
	}
	
	public String getReportText() {
		updateSection(currentSection);
		String document = "";
		for(int i=0;i<contents.length;i++) {
			document += contents[i];
		}
		return document;
	}	
	
	public void reloadReport(String xml) {
		loadReport(xml);
	}

	public void mouseClicked(MouseEvent arg0) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		if ( e.getButton() == MouseEvent.BUTTON3 && e.isPopupTrigger() ) {
			showMenu(e.getComponent(),e.getX(),e.getY());
		}
	}

	public void mouseReleased(MouseEvent e) {
		
	}
	
	public void showMenu(Component e, int x, int y) {	
		String text = getSelectedText();
		JPopupMenu jpopup = new JPopupMenu();
		JMenuItem item = null;

		if(text == null) {
			item = new JMenuItem("Select All");
			item.setActionCommand("select");
			item.addActionListener((ActionListener) this);
			jpopup.add(item);			
		} else {
			item = new JMenuItem("Copy");
			item.setActionCommand("copy");
			item.addActionListener((ActionListener) this);
			jpopup.add(item);
		} 			
		if (CopyInterface.isCopyOn()) {
			item = new JMenuItem("Paste");
			item.setActionCommand("paste");
			item.addActionListener((ActionListener) this);
			jpopup.add(item);
		}
		item = new JMenuItem("Clean");
		item.setActionCommand("clean");
		item.addActionListener((ActionListener) this);
		jpopup.add(item);
		
		jpopup.show(e, x, y);
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		
		if ("select".equals(action)) {
			updateUI();
			selectAll();
			return;
		}
		
		if ("copy".equals(action)) {
			copy();
			return;
		}
		
		if ("paste".equals(action)) {
			paste();
			return;
		}

		if ("clean".equals(action)) {
			setText("");
			updateSection(currentSection);
		}
	}
	
	public int getCurrentSection() {
		return currentSection;
	}
	
	public void copy() {
	    CopyInterface.setCopyText(getSelectedText());
		CopyInterface.setCopyState(true);		
	}
	
	public void paste() {
		replaceSelection(CopyInterface.getCopyText());
	}
	
	public boolean selectPattern(String text) {
		updateUI();
		int length = text.length();
		String input = getText();
		int k = input.indexOf(text);
		if (k == -1) {
			return false;
		}
		int total = k + length;
		select(k-total,total);
		currentIndex = total;
		
		return true;
	}	

	public void selectNextOccurrence(String text) {
		int length = text.length();
		String input = getText();
		int k = input.indexOf(text,currentIndex);
		if (k == -1) {
			currentIndex = 0;
			k = input.indexOf(text,currentIndex);
		}
		int total = k + length;
		currentIndex = total;
		select(k, total);
	}		
	
	public void unselectText() {
		String text = getSelectedText();
		if(text != null) {
			unselectText();
		}
	}	
	
	public void resetIndex() {
		currentIndex = 0;
	}
	
	public int getLinesTotal(int i) {
		return lines[i];
	}
	
	public int getSectionLines(String section){
		StringTokenizer token = new StringTokenizer(section,"\n");
		int total = 0;
		while (token.hasMoreTokens()) {
			token.nextToken();
			total++;
		}
		return total;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == 10) {
			int index = 0;
			for(int i=0;i<=currentSection;i++) {
				index += lines[i];
			}
			numberPanel.addOneLine(index);
			lines[currentSection]++;			
		}
		/*
		if(key == 8) {
		   numberPanel.removeOneLine();
		   lines[currentSection]--;
		}
		if(key == 127) {
			int position = getCaretPosition();
			char letter = getText().charAt(position);
			System.out.println("CHAR: " + letter);
        }*/
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
	
}
