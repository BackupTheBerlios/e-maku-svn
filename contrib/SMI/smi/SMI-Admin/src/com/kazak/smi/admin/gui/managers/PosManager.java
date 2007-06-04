package com.kazak.smi.admin.gui.managers;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.admin.transactions.QuerySender;
import com.kazak.smi.admin.transactions.QuerySenderException;
import com.kazak.smi.admin.gui.managers.tools.MainForm;
import com.kazak.smi.admin.gui.managers.tools.ToolsConstants;

public class PosManager {

	public MainForm form;
	private String newCode = "-1";
	
	public PosManager() {
	}

	public void addPos() {
		openForm();
	}
	
	public void editPos() {
		form = new MainForm(ToolsConstants.POS,ToolsConstants.EDIT,"");
	}

	public void editPos(String target) {
		form = new MainForm(ToolsConstants.POS,ToolsConstants.EDIT_PREFILLED,target);
	}

	public void deletePos() {
		form = new MainForm(ToolsConstants.POS,ToolsConstants.DELETE,"");
	}

	public void deletePos(String target) {
		form = new MainForm(ToolsConstants.POS,ToolsConstants.DELETE_PREFILLED,target);
	}
	
	public void searchPos() {
		form = new MainForm(ToolsConstants.POS,ToolsConstants.SEARCH,"");
	}
	
	public void searchPos(String target) {
		form = new MainForm(ToolsConstants.POS,ToolsConstants.SEARCH_PREFILLED,target);
	}
	
	private void openForm() {
        Thread t = new Thread() {
            public void run() {
            	try {
            		Document doc = QuerySender.getResultSetFromST("SEL0034",new String[] {});
		    		Element element = doc.getRootElement();
		    		List list = element.getChildren("row");
		    		Element columns = (Element)list.get(0);
		    		newCode = columns.getValue();
		    		form = new MainForm(ToolsConstants.POS,ToolsConstants.ADD,newCode.trim());
            	} catch(QuerySenderException e) {
            		System.out.println("ERROR: No se pudo capturar el codigo del punto de venta");
            		e.printStackTrace();
            	}
            }
        };
        t.start();        
	}
}