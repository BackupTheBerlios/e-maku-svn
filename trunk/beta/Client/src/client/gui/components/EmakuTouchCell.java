package client.gui.components;

import org.jdom.Document;

import common.gui.components.EmakuTouchField;
import common.gui.forms.GenericForm;

public class EmakuTouchCell extends EmakuTouchField  {
	
	private static final long serialVersionUID = 1L;
	
	public EmakuTouchCell(GenericForm genericForm, Document doc) {
		super(genericForm, doc);
	}

	public String getValue() {
		return getText();
	}
}