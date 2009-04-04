package client.gui.components;

/**
 * EmakuSendRecordButton.java Creado el 18-feb-2009
 * 
 * Este archivo es parte de E-Maku <A
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase crea un panel de botones que traslada registros de una tabla  <br>
 * 
 * @author <A href='mailto:pipelx@gmail.com'>Luis Felipe Hernandez </A>
 */

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jdom.Document;
import org.jdom.Element;

import client.gui.components.StructureSubPackage;

import common.gui.components.AnswerEvent;
import common.gui.components.Couplable;
import common.gui.components.RecordEvent;
import common.gui.components.RecordListener;
import common.gui.components.VoidPackageException;
import common.gui.forms.EndEventGenerator;
import common.gui.forms.GenericForm;
import common.gui.forms.NotFoundComponentException;
import common.misc.formulas.BeanShell;
import common.misc.language.Language;

public class EmakuSendRecordButton extends JPanel 
implements ActionListener,Couplable {

	/**
	 * 
	 */

	private ArrayList<RecordListener> recordListener = new ArrayList<RecordListener>();
	private static final long serialVersionUID = -8116155986599740103L;
	private JButton JBsend; 
	private Element element;
	private String masterTable;
	private GenericForm GFforma;
	private String conditional;
	private boolean recalculable = true;
	
	public EmakuSendRecordButton(GenericForm GFforma, Document doc) {
		this.GFforma=GFforma;
		Element rootElement = doc.getRootElement();
		Iterator args = rootElement.getChildren().iterator();
		
		while (args.hasNext()) {
			final Element elm = (Element) args.next();
			if ("masterTable".equals(elm.getAttributeValue("attribute"))) {
				StringTokenizer st = new StringTokenizer(elm.getValue(),",");
				masterTable=st.nextToken()+st.nextToken();
			}
			if ("conditional".equals(elm.getAttributeValue("attribute"))) {
				conditional=elm.getValue();
			}
			if ("recalculable".equals(elm.getAttributeValue("attribute"))) {
				recalculable=Boolean.parseBoolean(elm.getValue());
			}
		}
		
		// TODO Auto-generated constructor stub
		JBsend = new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_sendRecord_16x16.png")));
		this.add(JBsend);
		JBsend.setName("sendRecord");
		JBsend.addActionListener(this);

	}

	public EmakuSendRecordButton(LayoutManager arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public EmakuSendRecordButton(boolean arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public EmakuSendRecordButton(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		EmakuTableModel table;
		try {
			table = (EmakuTableModel) GFforma.invokeMethod(masterTable, "getEmakuTableModel");
	        element = new Element("table");
	        for (int i=0;i<table.getRowCount(); i++) {
	        		Element row = new Element("row");
	        		boolean addRow = false;
		            for (int j=0;j<table.getColumnCount();j++) {
		            	Object obj = table.getValueAt(i,j);
		            	if (j==0) {
		            		if (((String)obj).equals("")) {
		            			break;
		            		}
		            	}
		            	if (conditional!=null && !BeanShell.eval(formulaReplacer(conditional,table,i))) {
		            		break;
		            	}
		            	Element record = new Element("col");
		            	if (obj instanceof Boolean) {
		            		record.setText(String.valueOf(((Boolean)obj).booleanValue()));
		            	} else if (obj instanceof Number) {
		            		record.setText(String.valueOf(((Number)obj).doubleValue()));
		            	}
		            	else {
		            		record.setText((String)table.getValueAt(i,j));
		            	}
	            		row.addContent(record);
	            		addRow=true;
		            }		        
		            if (addRow) {
		            	element.addContent(row);
		            }
	        }
			GFforma.invokeMethod(masterTable, "clean");
			notificando();

		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundComponentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private String formulaReplacer(String var,EmakuTableModel table,int row) {
		String newVar = "";
		for (int j = 0; j < var.length(); j++) {
			if (j+10<var.length() && var.substring(j,j+11).equals(".startsWith")) {
				String s = ".startsWith("+var.substring(j+12,var.indexOf(")",j+12))+")";
				newVar+=s;
				j=var.indexOf(")",j);
			}
			else if (j+7<var.length() && var.substring(j,j+7).equals(".equals")) {
				String s = ".equals("+var.substring(j+8,var.indexOf(")",j+8))+")";
				newVar+=s;
				j=var.indexOf(")",j);
			}
			else if ((var.charAt(j) >= 65 && var.charAt(j) <= 90) ||
				(var.charAt(j) >= 97 && var.charAt(j) <= 122)) {
					int col;

					if (var.charAt(j) <= 90) {
						col = var.charAt(j) - 65;
					}
					/* cuando es minuscula */
					else {
						col = var.charAt(j) - 97;
					}
					newVar += ((Number)table.getValueAt(row,col)).doubleValue();
					
			} else {
				newVar += var.substring(j, j + 1);
			}
		}
		return newVar;
	}

	public void clean() {
		// TODO Auto-generated method stub

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

	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	public void initiateFinishEvent(EndEventGenerator e) {
		// TODO Auto-generated method stub

	}

	public void addRecordListener(RecordListener listener) {
		recordListener.add(listener);
	}

	public  void removeRecordListener(RecordListener listener) {
		recordListener.remove(listener);
	}

	public void notificando() {
		RecordEvent event = new RecordEvent(this,element);
		event.setRecalculable(recalculable);
		for(RecordListener l:recordListener) {
			l.arriveRecordEvent(event);
		}
		event = null;
	}


}
