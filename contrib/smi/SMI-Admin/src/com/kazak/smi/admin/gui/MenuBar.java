package com.kazak.smi.admin.gui;

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
	private JMenuItemXML JMItmp;
	private static Vector<JMenuItemXML> Items;
	private String userLevel;
	
	public MenuBar(String userLevel) {
		try {
			this.userLevel = userLevel; 
			SAXBuilder builder = new SAXBuilder(false);
			URL url = this.getClass().getResource("/resources/menu.xml");
			Document doc = builder.build(url);
			Element raiz = doc.getRootElement();
			String string = "JMenu";
			java.util.List Ljmenu = raiz.getChildren(string);
			Iterator i = Ljmenu.iterator();
			Items = new Vector<JMenuItemXML>();
			/**
			 * Ciclo encargado de leer las primeras etiquetas del archivo XML,
			 * en este caso JMenu
			 */
			while (i.hasNext()) {
				Element datos = (Element) i.next();

				java.util.List Lsubdatos = datos.getChildren();
				Iterator j = Lsubdatos.iterator();
				/**
				 * Se adicionara todos los menus principales a la clase
				 * MenuLoader
				 */
				this.add(CargarJMenu(j));
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
	private JMenu CargarJMenu(Iterator j) {
		String value = "";
		try {
			JMenu JMtmp = new JMenu() {
				private static final long serialVersionUID = 0L;
				public void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					super.paintComponent(g);
				}
			};
			while (j.hasNext()) {
				Element subdatos = (Element) j.next();
				value = subdatos.getValue();
				if (subdatos.getName().equals("Text")) {
					JMtmp.setText(subdatos.getValue());
				}
				else if (subdatos.getName().equals("Mnemonic")) {
					JMtmp.setMnemonic(value.charAt(0));
				}
				else if (subdatos.getName().equals("JSeparator")) {
					JMtmp.add(new JSeparator());
				}
				else {
					java.util.List Ljmenuitem = subdatos.getChildren();
					Iterator k = Ljmenuitem.iterator();
					if (subdatos.getName().equals("JMenuItem")) {
						JMenuItem JMItmp = CargarJMenuItem(k);
						if (JMItmp != null)
							JMtmp.add(JMItmp);
					} else if (subdatos.getName().equals("JMenu")) {
						JMtmp.add(CargarJMenu(k));
					}
				}
			}
			return JMtmp;
		}

		catch (NullPointerException NPEe) {
			NPEe.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Metodo encargado de cargar los Items al menu padre
	 */
	private JMenuItem CargarJMenuItem(Iterator j) {
		JMItmp = new JMenuItemXML();
		while (j.hasNext()) {
			Element subdatos = (Element) j.next();
			String value = subdatos.getValue();
			if (subdatos.getName().equals("ActionCommand")) {
				JMItmp.setActionCommand(value);
			}
			else if (subdatos.getName().equals("Text")) {
				JMItmp.setText(value);
			}
			else if (subdatos.getName().equals("Mnemonic")) {
				JMItmp.setMnemonic(value.charAt(0));
			}
			else if (subdatos.getName().equals("JMenu")) {
				java.util.List Ljmenuitem = subdatos.getChildren();
				Iterator k = Ljmenuitem.iterator();
				JMItmp.add(CargarJMenu(k));
			}
			else if (subdatos.getName().equals("Clase")) {
                JMItmp.setClassName(value);
            }
			else if (subdatos.getName().equals("Metodo")) {
                JMItmp.setMethod(value);
            }
            else if (subdatos.getName().equals("UserLevel")) {
            	if (value.equals(userLevel)) {
            		JMItmp.setEnabled(true);	
            	}
            	else {
            		JMItmp.setEnabled(false);
            	}
            }
		}
		Items.addElement(JMItmp);
		return JMItmp;
	}

	static class JMenuItemXML extends JMenuItem implements ActionListener {

		private static final long serialVersionUID = -2350472869777111566L;
		private String ClassName = null;
		private String method;
		private Object[] ArgConstructor = null;
		private Class[] TypeArgConstructor = null;

		public JMenuItemXML() {
			this.addActionListener(this);
		}
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			super.paintComponent(g);
		}
		
		public void setClassName(String ClassName) {
	        this.ClassName = ClassName;
	    }

	    /**
	     * Este metodo almacenara el metodo a ejecutarce
	     * en caso de que exista, una vez instanciada la clase
	     */

	    public void setMethod(String method) {
	        this.method = method;
	    }
	    
		public void actionPerformed(ActionEvent e) {
			Thread t = new Thread() {
				
				public void run() {
			        try {
			            Class<?> cls = Class.forName(ClassName);
			            if (method == null) {
			                Constructor cons = cls.getConstructor(TypeArgConstructor);
			                cons.newInstance(ArgConstructor);
			            } else {
			                Method meth = cls.getMethod(method, new Class[] {});
			                meth.invoke(cls.newInstance(), new Object[]{});
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
