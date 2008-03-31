package net.emaku.tools.gui.workspace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class EditorMenu extends JMenuBar implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private ReportEditor editor;

	public EditorMenu(ReportEditor gui) {		
		this.editor = gui;		
		loadMenu("editor.menu.xml");
	}

	private void loadMenu(String menu) {
		SAXBuilder builder = new SAXBuilder(false);
		try {
			Document doc = builder.build(menu);
			Element root = doc.getRootElement();
			Iterator<Element> i = root.getChildren().iterator();

			while( i.hasNext() ) {
				Element element = (Element) i.next();
				String name = element.getName();
				if ("jmenu".equals(name)) {
					JMenu jmenu = new JMenu(element.getAttributeValue("name"));
					jmenu.setMnemonic(KeyEvent.VK_E);
					Iterator<Element> j = element.getChildren().iterator();
					while(j.hasNext()) {
						Element item = (Element) j.next();
						if ("item".equals(item.getName())) {
							JMenuItem mitem = new JMenuItem(item.getAttributeValue("name"));
							mitem.setActionCommand(item.getAttributeValue("action"));
							mitem.addActionListener(this);
							jmenu.add(mitem);
						}
						else if ("separator".equals(item.getName())) {
							jmenu.add(new JSeparator());
						}
					}
					add(jmenu);
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public JMenuBar getMenu() {
		return this;
	}
	
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		
		if ("selectAll".equals(action)) {
			editor.selectAll();
		} else if ("copy".equals(action)) {
			editor.copy();
		}  else if ("paste".equals(action)) {
			editor.paste();
		}
	}
}

