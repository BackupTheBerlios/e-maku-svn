package common.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.jdom.*;
import org.jdom.input.*;

import common.gui.components.treeutils.*;
import common.gui.forms.*;
import common.misc.*;
import common.misc.language.*;
import common.misc.parameters.*;
import common.transactions.*;

/**
 * EmakuUSerPermissions.java Creado el 2008-03-23 10:23
 * 
 * Este archivo es parte de E-Maku <A 
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * E-maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Este es un componente grafico para la gestion de permisos para usuarios
 * <br>
 * Basado en: 
 * Definitive Guide to Swing for Java 2, Second Edition
 * By John Zukowski     
 * ISBN: 1-893115-78-X
 * Publisher: APress
 * 
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

public class EmakuUserPermissions extends JPanel implements Couplable, AnswerListener {

	private static final long serialVersionUID = -5560955524180191524L;
	private JTree tree;
	private NodeContainer root;
	private GenericForm genericForm;
	private JScrollPane scroll;
	private String path;
	private String importValue;
	private String keySQL = "";
	private String driverEvent = "";
	private Vector<String> sqlCode = new Vector<String>();
	private Vector<AnswerListener> AnswerListener = new Vector<AnswerListener>();
	
	public EmakuUserPermissions(GenericForm genericForm,Document doc) {
		this.genericForm = genericForm;
		this.setLayout(new BorderLayout());
		path = EmakuParametersStructure.getParameter("jarPath")+"/misc/menu.xml";
		Element rootElement = doc.getRootElement();
		Iterator args = rootElement.getChildren().iterator();
		while (args.hasNext()) {
			Element elm = (Element) args.next();
			String value = elm.getAttributeValue("attribute");
			if ("sqlCode".equals(value)) {
				sqlCode.add(elm.getValue());
			}
			else if ("importValue".equals(value)) {
				//importValues.addElement(elm.getValue());
				importValue = elm.getValue();
			}
			else if ("keySQL".equals(value)) {
				keySQL = elm.getValue();
			}
			else if ("driverEvent".equals(value)) {
				String id = elm.getAttributeValue("id");
				if (id!=null) {
					driverEvent = elm.getValue() + id;
				}
				else {
					driverEvent = elm.getValue();
				}
			}
		}
		this.genericForm.addInitiateFinishListener(this);
		loadUITree();
	}
	
	public void loadUITree() {
		try {
			URL url = new URL(path);
			InputStream in = url.openStream();
			SAXBuilder sax = new SAXBuilder(false);
			Document jar = sax.build(in);
			in.close();
			Element rootNode = jar.getRootElement();
			root = new NodeContainer(path);
			Iterator it = rootNode.getChildren().iterator();
			while (it.hasNext()) {
				root.add(loadTree((Element) it.next()));
			}
			this.tree = new JTree(root);
			CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
			tree.setCellRenderer(renderer);
			tree.setEditable(true);
			tree.setCellEditor(new CheckBoxNodeEditor(tree));
			tree.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					int keyCode = e.getKeyCode();
					if (KeyEvent.VK_SPACE == keyCode) {
						DefaultMutableTreeNode tnode = null;
						TreePath tp = tree.getSelectionPath();
						tnode = (DefaultMutableTreeNode)tp.getLastPathComponent();
						Object node = tnode.getUserObject();
						if (node instanceof TreeCheckNode) {
							boolean b = ((TreeCheckNode)node).isSelected();
							((TreeCheckNode)node).setSelected(!b);
							tree.treeDidChange();
							sendTransaction((TreeCheckNode) node);
						}
					}
				}
			});
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					String key = "";
					DefaultMutableTreeNode tnode = null;
					tnode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
					Object node = tnode.getUserObject();
					if (node instanceof TreeCheckNode) {
						key = ((TreeCheckNode)node).getTransaction();
					}
					search(key);
				}			
			});
			this.scroll = new JScrollPane(tree);
			this.add(this.scroll, BorderLayout.CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}

	public Object loadTree(Element e) {
		List list = e.getChildren();
		String name = e.getName();
		int max = list.size();
		if ("JMenu".equals(name)) {
			String value = e.getChildText("Text");
			value = Language.getWord(value);
			NodeContainer nm = new NodeContainer(value);
			nm.setIcon(e.getChildText("Icon"));
			for (int i = 0; i < max; i++) {
				Element cnode = (Element) list.get(i);
				Object obj = loadTree(cnode);
				if (obj!=null) {
					nm.add(obj);
				}
			}
			return nm;
		}
		else if ("JMenuItem".equals(name)) {
			String value = e.getChildText("Text");
			String transaction = e.getChildText("Transaction");
			value = Language.getWord(value);
			TreeCheckNode cnode = new TreeCheckNode(value,false);
			cnode.setTransaction(transaction);
			cnode.setIcon(e.getChildText("Icon"));
			cnode.setEnabled(transaction==null ? false : true);
			cnode.setSelected(!cnode.isEnabled());
			return cnode;
		}
		return null;
	}

	public void cleanRoot(DefaultMutableTreeNode p) {
		int max = p.getChildCount();
		for (int i=0; i < max ; i++) {
			DefaultMutableTreeNode q = (DefaultMutableTreeNode) p.getChildAt(i);
			if (q.getUserObject() instanceof NodeContainer) {
				cleanRoot(q);
			}
			else {
				if(((TreeCheckNode)q.getUserObject()).isEnabled()) {
					((TreeCheckNode)q.getUserObject()).setSelected(false);
				}
			}
		}
	}
	
	public void arriveAnswerEvent(AnswerEvent e) {
		Document doc = e.getDocument();
		Element rootNode = doc.getRootElement();
		if (e.getSqlCode().equals(this.keySQL))  {
			tree.clearSelection();
			DefaultMutableTreeNode rootTree = (DefaultMutableTreeNode) tree.getModel().getRoot();
			cleanRoot(rootTree);
			Iterator i = rootNode.getChildren("row").iterator();
			while (i.hasNext()) {
				Element element = (Element) i.next();
				loadChekValues(rootTree,element.getChildText("col").trim());
			}
			tree.treeDidChange();
		}
	}
	
	public boolean containSqlCode(String sql) {
		return sql.equals(this.keySQL);
	}
		
	public void loadChekValues(DefaultMutableTreeNode p, String code) {
		int max = p.getChildCount();
		for (int i=0; i < max ; i++) {
			DefaultMutableTreeNode q = (DefaultMutableTreeNode) p.getChildAt(i);
			if (q.getUserObject() instanceof NodeContainer) {
				loadChekValues(q,code);
			}
			else {
				TreeCheckNode n = (TreeCheckNode) q.getUserObject();
				if(code.equals(n.getTransaction())) {
					n.setSelected(true);
				}
			}
		}
	}
	
	public void initiateFinishEvent(EndEventGenerator e) {
		if (!"".equals(driverEvent) && !"".equals(keySQL)) try {
			System.out.println("finishEvent");
			Class<?>[] c = new Class[]{AnswerListener.class};
			Object[] args = new Object[]{this};
			genericForm.invokeMethod(driverEvent,"addAnswerListener",c,args);
		}
		catch(NotFoundComponentException NFCEe) {
			NFCEe.printStackTrace();
		} 
		catch (InvocationTargetException ITEe) {
			ITEe.printStackTrace();
		}
	}
	
	public void addAnswerListener(AnswerListener listener) {
		AnswerListener.addElement(listener);
	}

	public void removeAnswerListener(AnswerListener listener) {
		AnswerListener.removeElement(listener);
	}
	
	private void notifyEvent(AnswerEvent event) {
		for (AnswerListener l: AnswerListener) {
			if (l.containSqlCode(event.getSqlCode())) {
				l.arriveAnswerEvent(event);
			}
		}
	}
	
	public synchronized void sendTransaction(TreeCheckNode n) {
		Element transaction = new Element("TRANSACTION");
		Element pack = new Element("package");
		String user = genericForm.getExternalValueString(importValue);
		if (user!=null) {
			Element fieldUser = new Element("field").setText(user);
			Element fieldCode = new Element("field").setText(n.getTransaction());
			Element fieldState = new Element("field").setText(String.valueOf(n.isSelected()));
			pack.addContent(fieldUser);
			pack.addContent(fieldCode);
			pack.addContent(fieldState);
			
			Element driver = new Element("driver");
			driver.setText(genericForm.getIdTransaction());
			Element id = new Element("id");
			id.setText("T"+TransactionServerResultSet.getId());
			transaction.addContent(driver);
			transaction.addContent(pack);
			transaction.addContent((Element) pack.clone());
			transaction.addContent(id);
			Document doc = new Document(transaction);
			genericForm.sendTransaction(doc);
			System.out.println("Transaccion enviada");
		}
	}
	
	public void search(String key) {
		String[] args = {key};		
		for (int j = 0; j < sqlCode.size(); j++) {
			Document doc = null;
			String sql = sqlCode.get(j);
			try {
				doc = TransactionServerResultSet.getResultSetST(sql, args);
				AnswerEvent event = new AnswerEvent(this, sql, doc);
				notifyEvent(event);
			} catch (TransactionServerException e) {
				System.out.println("No se pudo realizar la consulta: " + sql);
			}
		}
	}

	public void clean()                                      {}
	public boolean containData()                             { return false; }
	public Element getPackage(Element args) throws Exception { return null;  }
	public Element getPackage() throws VoidPackageException  { return null;  }
	public Element getPrintPackage()                         { return null;  }
	public Component getPanel()                              { return this;  }
	public void validPackage(Element args) throws Exception {}

	/*
	 * Basado en: 
	 * Definitive Guide to Swing for Java 2, Second Edition
	 * By John Zukowski     
	 * ISBN: 1-893115-78-X
	 * Publisher: APress
	 */
	class CheckBoxNodeEditor extends AbstractCellEditor implements
	                                              TreeCellEditor, ItemListener {
	
		private static final long serialVersionUID = 1L;
		private CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		private JTree tree;
	
		public CheckBoxNodeEditor(JTree tree) {
			this.tree = tree;
		}
		
		public void itemStateChanged(ItemEvent itemEvent) {
			if (stopCellEditing()) {
				fireEditingStopped();
			}
		}
		public Object getCellEditorValue() {
			TreeCheckBox cbox = renderer.getLeafRenderer();
			TreeCheckNode checkBoxNode = null;
			checkBoxNode = new TreeCheckNode(cbox.getText(),cbox.isSelected());
			checkBoxNode.setTransaction(cbox.getTransaction());
			checkBoxNode.setEnabled(cbox.isEnabled());
			sendTransaction(checkBoxNode);
			return checkBoxNode;
		}
	
		public synchronized boolean isCellEditable(EventObject event) {
			boolean returnValue = false;
			if (event instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) event;
				TreePath path = tree.getPathForLocation(me.getX(),me.getY());
				if (path != null) {
					DefaultMutableTreeNode node = null ;
					node = (DefaultMutableTreeNode) path.getLastPathComponent();
					Object userObject = node.getUserObject();
					returnValue = ((node.isLeaf()) &&
								   (userObject instanceof TreeCheckNode));
					
				}
			}
			return returnValue;
		}
	
		public Component getTreeCellEditorComponent(
				                JTree tree, final Object value,boolean selected,
				                boolean expanded,boolean leaf, int row) {
	
			Component editor = renderer.getTreeCellRendererComponent(
					                    tree,value,true,expanded,leaf,row,true);

			if (editor instanceof TreeCheckBox) {
				((TreeCheckBox) editor).addItemListener(this);
			}
			return editor;
		}
	}
}

/*
 * Basado en: 
 * Definitive Guide to Swing for Java 2, Second Edition
 * By John Zukowski     
 * ISBN: 1-893115-78-X
 * Publisher: APress
 */
class CheckBoxNodeRenderer implements TreeCellRenderer {
	
	private TreeCheckBox checkBox = new TreeCheckBox();
	private JLabel label = new JLabel();
	private DefaultTreeCellRenderer defaultCell = new DefaultTreeCellRenderer();
	private Color selectionForeground;
	private Color selectionBackground;
	private Color textForeground;
	private Color textBackground;

	protected TreeCheckBox getLeafRenderer() {
		return checkBox;
	}

	public CheckBoxNodeRenderer() {
		Font fontValue;
		fontValue = UIManager.getFont("Tree.font");
		if (fontValue != null) {
			checkBox.setFont(fontValue);
		}
		Boolean b = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
		checkBox.setFocusPainted((b != null) && (b.booleanValue()));
		selectionForeground = UIManager.getColor("Tree.selectionForeground");
		selectionBackground = UIManager.getColor("Tree.selectionBackground");
		textForeground = UIManager.getColor("Tree.textForeground");
		textBackground = UIManager.getColor("Tree.textBackground");
	}

	public Component getTreeCellRendererComponent(
			         JTree tree, Object value,boolean selected,boolean expanded,
			         boolean leaf, int row,boolean hasFocus) {
		Component returnValue;
		Object obj = ((DefaultMutableTreeNode) value).getUserObject();
		if (leaf) {
			//String stringValue = tree.convertValueToText(
			//		                  value,selected,expanded,leaf,row,false);
			/*checkBox.setText(stringValue);
			checkBox.setSelected(false);
			ceckBox.setEnabled(tree.isEnabled());*/
			if (selected) {
				checkBox.setForeground(selectionForeground);
				checkBox.setBackground(selectionBackground);
			} else {
				checkBox.setForeground(textForeground);
				checkBox.setBackground(textBackground);
			}

			if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
				if (obj instanceof TreeCheckNode) {
					TreeCheckNode node = (TreeCheckNode) obj;
					checkBox.setText(node.getText());
					checkBox.setSelected(node.isSelected());
					checkBox.setToolTipText(node.getTransaction());
					checkBox.setTransaction(node.getTransaction());
					checkBox.setEnabled(node.isEnabled());
				}
			}
			returnValue = checkBox;
		} else {
			//returnValue = defaultCell.getTreeCellRendererComponent(
			//		            tree,value,selected,expanded,leaf,row,hasFocus);
			if (obj instanceof NodeContainer) {
				NodeContainer node = (NodeContainer)obj;
				if (node!=null && node.getIcon()!=null) {
					ImageIcon icon = null;
					try {
	            		icon = new ImageIcon(this.getClass().getResource(Icons.getIcon(node.getIcon())));
	            	}
	            	catch(NullPointerException NPEe) {
	            		icon = new ImageIcon(this.getClass().getResource(node.getIcon()));
	            	}			
					label.setText(node.toString());
					label.setIcon(icon);
					label.setFocusCycleRoot(true);
					if (selected) {
						label.setForeground(selectionForeground);
						label.setBackground(selectionBackground);
						label.setBorder(new LineBorder(selectionBackground));
					} else {
						label.setForeground(textForeground);
						label.setBackground(textBackground);
						label.setBorder(new LineBorder(textBackground));
					}
					returnValue = label;
				}
				else {
					returnValue = defaultCell.getTreeCellRendererComponent(
							    tree,value,selected,expanded,leaf,row,hasFocus);
				}
			}
			else {
				returnValue = defaultCell.getTreeCellRendererComponent(
								tree,value,selected,expanded,leaf,row,hasFocus);
			}
		}
		return returnValue;
	}
}