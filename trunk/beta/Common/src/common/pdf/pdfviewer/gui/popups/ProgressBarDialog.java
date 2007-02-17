package common.pdf.pdfviewer.gui.popups;

//import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.border.LineBorder;
import org.jpedal.io.StatusBar;
import java.awt.Color;
import common.misc.language.Language;

public class ProgressBarDialog extends JFrame {
	
	private static final long serialVersionUID = 7423353291901251301L;
	private Color color = new Color(200,200,255);
	public StatusBar statusBar = new StatusBar(color);
	
	public ProgressBarDialog(JInternalFrame parent) {
		//super(parent, true);
		super(Language.getWord("LOADING"));
		this.getRootPane().setBorder(new LineBorder(Color.BLACK,2));
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
	
		JPanel panel = new JPanel();
		panel.add(statusBar.getStatusObject());
		this.add(panel);
		this.pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
	
	public void resetBar() {
	    statusBar.updateStatus("Decoding Page",0);
	}
		
	public void resetStatusBar() { 
	    statusBar.setColorForSubroutines(Color.blue);
	}
	
	public void updateStatusMessage(String message, int value) {
		statusBar.updateStatus(message,value);
	}
	
	public void resetStatusMessage(String message) {
		statusBar.resetStatus(message);
	}
	
	public void setStatusProgress(int size) {
		statusBar.setProgress(size);	
	}
	
	public StatusBar getStatusBar() {
		return statusBar;
	}
	
	public void close() {
		setVisible(false);
	}
}
