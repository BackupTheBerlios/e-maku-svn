package net.emaku.tools.gui.workspace;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class NumbersPanel extends JTextPane {

	private static final long serialVersionUID = 1L;
	private static StyledDocument doc;
	
	public NumbersPanel(int settingsLines) {
		setEditable(false);
		doc = getStyledDocument();
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		Style regular = doc.addStyle("regular",def);
		StyleConstants.setFontFamily(def,"Monospaced");
		StyleConstants.setFontSize(regular, 10);    
		StyleConstants.setForeground(regular,Color.GRAY);
		setRange(0,settingsLines);
	}
	
	public void setRange(int init, int range) {
		setText("");
		try {
			for(int i=init;i<range;i++) {
				doc.insertString(doc.getLength(),i+"\n",doc.getStyle("regular"));
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void addOneLine(int value) {
		try {
			 doc.insertString(doc.getLength(),value+"\n",doc.getStyle("regular"));
		} catch (BadLocationException e) {
			 e.printStackTrace();
		}
	}
	
	public void removeOneLine() {
		try {
			String text = doc.getText(0,doc.getLength()-1);
			int index = text.lastIndexOf("\n");
			text = text.substring(0,index+1);
			setText("");
		    doc.insertString(0,text,doc.getStyle("regular"));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
