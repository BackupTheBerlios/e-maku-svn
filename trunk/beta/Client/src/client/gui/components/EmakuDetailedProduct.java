package client.gui.components;

import java.awt.IllegalComponentStateException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import common.gui.components.XMLTextField;

public class EmakuDetailedProduct extends JComponent implements KeyListener {

	private static final long serialVersionUID = 8386003217712730190L;
	private JPanel contentPane;
	private JPopupMenu JPMpopup;
	private XMLTextField XMLTFCode;

	public EmakuDetailedProduct() {
		this.addKeyListener(this);
		initComps();
	}
	
	
	private void initComps() {
		contentPane = new JPanel();
		contentPane.setSize(200,200);

		JPMpopup = new JPopupMenu();
		JPMpopup.setLightWeightPopupEnabled(true);
		JPMpopup.add(contentPane);
		XMLTFCode = new XMLTextField("CODE", 9, 10, XMLTextField.TEXT);
		contentPane.add(XMLTFCode);
	}
	
	public void showDetailed() {
		if (!JPMpopup.isVisible()) {
			updateUI();
			int psize = (int) JPMpopup.getPreferredSize().getWidth();
			int x = this.getWidth() - psize;
			int y = this.getHeight();
			try { JPMpopup.show(this,x,y); }
			catch (IllegalComponentStateException e) { e.printStackTrace(); }
		}
	}

	public String getValue() {
		return "celda";
	}
	
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		//Object s = e.getSource();
		switch(keyCode) {
			case KeyEvent.VK_F2:
				showDetailed();
				break;
			default :
				if (((keyCode >=60 &&  keyCode<=71) ||  keyCode >=65 &&  keyCode<=126)) {
					showDetailed();
				}
				break;
		}
	}

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
}