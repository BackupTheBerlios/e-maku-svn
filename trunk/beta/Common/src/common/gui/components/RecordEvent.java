package common.gui.components;

import java.util.EventObject;

import org.jdom.Element;

public class RecordEvent extends EventObject {

	private static final long serialVersionUID = -9028662223570110161L;
	private Element element;
	
	public RecordEvent(Object source,Element e) {
		super(source);
		this.element = e;
	}
	
	public Element getElement() {
		return element;
	}
}
