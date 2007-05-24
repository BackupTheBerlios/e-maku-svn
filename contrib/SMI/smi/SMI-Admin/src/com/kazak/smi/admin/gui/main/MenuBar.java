package com.kazak.smi.admin.gui.main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = -802272130996470686L;
	private JMenuItemXML menuItem;
	private static Vector<JMenuItemXML> Items;
	private String userLevel;
	
	public MenuBar(String userLevel) {
		try {
			this.userLevel = userLevel; 
			SAXBuilder saxBuilder = new SAXBuilder(false);
			URL url = this.getClass().getResource("/menu.xml");
			Document doc = saxBuilder.build(url);
			Element root = doc.getRootElement();
			String string = "JMenu";
			List menuList = root.getChildren(string);
			Iterator iterator = menuList.iterator();
			Items = new Vector<JMenuItemXML>();
			/**
			 * Ciclo encargado de leer las primeras etiquetas del archivo XML,
			 * en este caso JMenu
			 */
			while (iterator.hasNext()) {
				Element data = (Element) iterator.next();

				List internalData = data.getChildren();
				Iterator dataIterator = internalData.iterator();
				/**
				 * Se adicionara todos los menus principales a la clase
				 * MenuLoader
				 */
				this.add(loadJMenu(dataIterator));
			}
		} catch (JDOMException JDOMEe) {
			System.out.println(JDOMEe.getMessage());
		} catch (IOException IOEe) {
			System.out.println(IOEe.getMessage());
		}
	}

	/**
	 * Metodo encargado de cargar los submenus al menu padre
	 */
	private JMenu loadJMenu(Iterator iterator) {
		String value = "";
		try {
			JMenu menu = new JMenu() {
				private static final long serialVersionUID = 0L;
				public void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					super.paintComponent(g);
				}
			};
			while (iterator.hasNext()) {
				Element internalData = (Element) iterator.next();
				value = internalData.getValue();
				if (internalData.getName().equals("Text")) {
					menu.setText(internalData.getValue());
				}
				else if (internalData.getName().equals("Mnemonic")) {
					menu.setMnemonic(value.charAt(0));
				}
				else if (internalData.getName().equals("JSeparator")) {
					menu.add(new JSeparator());
				}
				else {
					List menuItemList = internalData.getChildren();
					Iterator menuIterator = menuItemList.iterator();
					if (internalData.getName().equals("JMenuItem")) {
						JMenuItem menuItem = loadJMenuItem(menuIterator);
						if (menuItem != null)
							menu.add(menuItem);
					} else if (internalData.getName().equals("JMenu")) {
						menu.add(loadJMenu(menuIterator));
					}
				}
			}
			return menu;
		}

		catch (NullPointerException NPEe) {
			NPEe.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Metodo encargado de cargar los Items al menu padre
	 */
	private JMenuItem loadJMenuItem(Iterator iterator) {
		menuItem = new JMenuItemXML();
		while (iterator.hasNext()) {
			Element internalData = (Element) iterator.next();
			String value = internalData.getValue();
			
			if (internalData.getName().equals("ActionCommand")) {
				menuItem.setActionCommand(value);
			}
			else if (internalData.getName().equals("Text")) {
				menuItem.setText(value);
			}
			else if (internalData.getName().equals("Mnemonic")) {
				menuItem.setMnemonic(value.charAt(0));
			}
			else if (internalData.getName().equals("JMenu")) {
				List itemsList = internalData.getChildren();
				Iterator menuIterator = itemsList.iterator();
				menuItem.add(loadJMenu(menuIterator));
			}
			else if (internalData.getName().equals("Class")) {
                menuItem.setClassName(value);
            }
			else if (internalData.getName().equals("Method")) {
                menuItem.setMethod(value);
            }
            else if (internalData.getName().equals("UserLevel")) {
            	if (value.equals(userLevel)) {
            		menuItem.setEnabled(true);	
            	}
            	else {
            		menuItem.setEnabled(false);
            	}
            }
		}
		Items.addElement(menuItem);
		
		return menuItem;
	}

	static class JMenuItemXML extends JMenuItem implements ActionListener {

		private static final long serialVersionUID = -2350472869777111566L;
		private String className = null;
		private String method;
		private Object[] argsArray = null;
		private Class[] argsTypeArray = null;

		public JMenuItemXML() {
			this.addActionListener(this);
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			super.paintComponent(g);
		}
		
		public void setClassName(String className) {
	        this.className = className;
	    }

	    /**
	     * Este metodo almacenara el metodo a ejecutarce
	     * en caso de que exista, una vez instanciada la clase
	     */

	    public void setMethod(String method) {
	        this.method = method;
	    }
	    
		public void actionPerformed(ActionEvent event) {
			Thread t = new Thread() {
				
				public void run() {
			        try {
			            Class<?> classVar = Class.forName(className);
			            if (method == null) {
			                Constructor constructor = classVar.getConstructor(argsTypeArray);
			                constructor.newInstance(argsArray);
			            } else {
			                Method methodVar = classVar.getMethod(method, new Class[] {});
			                methodVar.invoke(classVar.newInstance(), new Object[]{});
			            }
			        }
			        catch (ClassNotFoundException CNFEe) {
			            CNFEe.printStackTrace();
			            System.out.println("Exception : " + CNFEe.getMessage());
			        }
			        catch (NoSuchMethodException NSMEe) {
			            NSMEe.printStackTrace();
			            System.out.println("Exception : " + NSMEe.getMessage());
			        }
			        catch (InstantiationException IEe) {
			            IEe.printStackTrace();
			            System.out.println("Exception : " + IEe.getMessage());
			        }
			        catch (IllegalAccessException IAEe) {
			            IAEe.printStackTrace();
			            System.out.println("Exception : " + IAEe.getMessage());
			        }
			        catch (InvocationTargetException ITEe) {
			            ITEe.printStackTrace();
			            System.out.println("Exception: " + ITEe.getMessage());
			        }
				
				}
		
			};
			t.start();
		}
	}
}
