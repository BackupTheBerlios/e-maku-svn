package common.gui.components;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.*;

import org.jdom.*;
import org.jdom.input.*;

import common.gui.forms.*;
import common.misc.language.*;
import common.misc.parameters.*;

public class EmakuUserPermissions extends JPanel implements Couplable {

	private static final long serialVersionUID = -5560955524180191524L;
	private JTree tree;
	private DefaultMutableTreeNode root;
	private GenericForm genericForm;
	private Document document;
	private JScrollPane scroll;

	public EmakuUserPermissions(GenericForm genericForm) {
		this.genericForm = genericForm;
		this.setLayout(new BorderLayout());
		String path = EmakuParametersStructure.getParameter("jarPath")+"/misc/menu.xml";
		try {
			URL url = new URL(path);
			InputStream in = url.openStream();
			SAXBuilder sax = new SAXBuilder(false);
			Document doc = sax.build(in);
			Element rootNode = doc.getRootElement();
			root = loadTree(rootNode);
			this.tree = new JTree(root);
			this.scroll = new JScrollPane(tree);
			this.add(this.scroll,BorderLayout.CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}

	public DefaultMutableTreeNode loadTree(Element e) {
		List list = e.getChildren();
		String name = e.getChildText("Text");
		name = Language.getWord(name!=null ? name : "");
		int max = list.size();
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
		for (int i=0; i < max ; i++) {
			Element cnode = (Element)list.get(i);
			String cname = cnode.getChildText("Text");
			if (cname!=null && !"".equals(cname)) {
				DefaultMutableTreeNode n = loadTree(cnode);
				node.add(n);
			}
		}
		return node;
	}

	@Override
	public void clean() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Element getPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getPackage() throws VoidPackageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Component getPanel() {
		return this;
	}

	@Override
	public Element getPrintPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initiateFinishEvent(EndEventGenerator e) {
		// TODO Auto-generated method stub
		
	}
}