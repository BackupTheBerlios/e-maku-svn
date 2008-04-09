package net.emaku.tools.gui.workspace;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

import net.emaku.tools.gui.ReportManagerGUI;

/**
 * A JTabbedPane which has a close ('X') icon on each tab.
 * 
 * To add a tab, use the method addTab(String, Component)
 * 
 * To have an extra icon on each tab (e.g. like in JBuilder, showing the file
 * type) use the method addTab(String, Component, Icon). Only clicking the 'X'
 * closes the tab.
 */

public class ETabbedPane extends JTabbedPane implements MouseListener {

	private static final long serialVersionUID = 7423353291901251301L;
    private int tabsTotal;
    private ReportManagerGUI gui;
    private int resource;
	
	public ETabbedPane(ReportManagerGUI gui,int resource) {
		super();
		this.gui = gui;
		this.resource = resource;
		addMouseListener(this);
		tabsTotal = 0;
	}

	public void addTab(String title, Component component) {
		this.addTab(title, component, null);
	}

	public void addTab(String title, Component component, Icon extraIcon) {
		super.addTab(title, new CloseTabIcon(extraIcon), component);
        tabsTotal++;
	}

	public void removeTabAt(int resource,int index) {
		String key = getTitleAt(index);
		gui.removeObjectTabFromHash(resource,key);
		super.removeTabAt(index);
		tabsTotal--;
		if (tabsTotal==0) {
			gui.setBarButtonsState(resource,false);
		}
	}
		
	public void minusTab() {
		tabsTotal--;
	}

	public void plusTab() {
		tabsTotal++;
	}
	
	public void mouseClicked(MouseEvent e) {

		int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
		if (tabNumber < 0) {
			return;
		}
		
		Rectangle rect = ((CloseTabIcon) getIconAt(tabNumber)).getBounds();	
		if (rect.contains(e.getX(), e.getY())) {
			try {
			      removeTabAt(resource,tabNumber);
			}catch (Exception ex) {
				System.out.println("Error capturado... :S");
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}

/**
 * The class which generates the 'X' icon for the tabs. The constructor accepts
 * an icon which is extra to the 'X' icon, so you can have tabs like in
 * JBuilder. This value is null if no extra icon is required.
 */
class CloseTabIcon implements Icon {
	private int x_pos;
	private int y_pos;
	private int width;
	private int height;
	private Icon fileIcon;

	public CloseTabIcon(Icon fileIcon) {
		this.fileIcon = fileIcon;
		width = 16;
		height = 16;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		this.x_pos = x;
		this.y_pos = y;

		Color col = g.getColor();

		g.setColor(Color.black);
		int y_p = y + 2;
		g.drawLine(x + 1, y_p, x + 12, y_p);
		g.drawLine(x + 1, y_p + 13, x + 12, y_p + 13);
		g.drawLine(x, y_p + 1, x, y_p + 12);
		g.drawLine(x + 13, y_p + 1, x + 13, y_p + 12);
		g.drawLine(x + 3, y_p + 3, x + 10, y_p + 10);
		g.drawLine(x + 3, y_p + 4, x + 9, y_p + 10);
		g.drawLine(x + 4, y_p + 3, x + 10, y_p + 9);
		g.drawLine(x + 10, y_p + 3, x + 3, y_p + 10);
		g.drawLine(x + 10, y_p + 4, x + 4, y_p + 10);
		g.drawLine(x + 9, y_p + 3, x + 3, y_p + 9);
		g.setColor(col);
		if (fileIcon != null) {
			fileIcon.paintIcon(c, g, x + width, y_p);
		}
	}

	public int getIconWidth() {
		return width + (fileIcon != null ? fileIcon.getIconWidth() : 0);
	}

	public int getIconHeight() {
		return height;
	}

	public Rectangle getBounds() {
		return new Rectangle(x_pos, y_pos, width, height);
	}
}