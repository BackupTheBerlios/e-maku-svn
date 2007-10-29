package com.kazak.comeet.client.control;

import java.util.EventObject;
import org.jdom.Element;

/**
 * @author    Cristian Cepeda
 */

public class MessageEvent extends EventObject {

	private static final long serialVersionUID = -63911540053380830L;
	private Element element;
	
	public MessageEvent(Object source,Element element) {
		super(source);
		this.element = element;
	}
	
	/**
	 * @return    the element
	 * @uml.property  name="element"
	 */
	public Element getElement() {
		return element;
	}
}
