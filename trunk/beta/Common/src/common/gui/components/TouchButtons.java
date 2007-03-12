package common.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jdom.Document;
import org.jdom.Element;


import common.gui.forms.EndEventGenerator;
import common.gui.forms.GenericForm;
import common.misc.language.Language;

public class TouchButtons extends JPanel implements ActionListener,Couplable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5953946428251386838L;
	private JButton[][] buttons = {{new JButton("7"),new JButton("8"),new JButton("9")},
								   {new JButton("4"),new JButton("5"),new JButton("6")},
								   {new JButton("1"),new JButton("2"),new JButton("3")},
								   {new JButton("0"),new JButton("00"),new JButton("000")},
								   {new JButton("CLEAN"),new JButton("<<"),new JButton("OK")}};
	private String value="";
	private GenericForm GFforma;
	private String exportValue;
	private String exportOkValue;
	
	public TouchButtons(GenericForm GFforma,Document doc){
		this.GFforma=GFforma;
		Element parameters = doc.getRootElement();
        Iterator i = parameters.getChildren().iterator();

        while (i.hasNext()) {
            Element subargs = (Element) i.next();
            String value = subargs.getValue();
            if ("exportValue".equals(subargs.getAttributeValue("attribute"))) {
            	exportValue=value;
            }
            if ("exportOkValue".equals(subargs.getAttributeValue("attribute"))) {
            	exportOkValue=value;
            }
        }
		this.setLayout(new BorderLayout());
		JPanel matriz = new JPanel(new GridLayout(5,3));
		for(JButton[] row:buttons) {
			for(JButton col:row) {
				matriz.add(col);
				col.addActionListener(this);
			}
		}
		this.add(matriz,BorderLayout.CENTER);
	}
	
	public void clean() {
		value="";
	}

	public boolean containData() {
		// TODO Auto-generated method stub
		return false;
	}

	public Element getPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Element getPackage() throws VoidPackageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Component getPanel() {
		// TODO Auto-generated method stub
		return this;
	}

	public Element getPrintPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void validPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void initiateFinishEvent(EndEventGenerator e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		String name = ((JButton)e.getSource()).getText();
		try {
			Integer.parseInt(name);
			value+=name;
		}
		catch(NumberFormatException NFEe) {
			if (name.equals("<<")) {
				value=value.substring(0,value.length()-1);
			}
			else if (name.equals("CLEAN")){
				value="";
			}
		}
		if (name.equals("OK")) {
			GFforma.setExternalValues(exportOkValue,new Double(value));
			value="";
		}
		GFforma.setExternalValues(exportValue,new Double(value));
	}

}
