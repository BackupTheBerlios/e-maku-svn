package common.gui.components.treeutils;

/**
 * NodeContainer.java Creado el 2008-03-23 10:23
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
 * Esta clase sirve para almacenar la información de un nodo de tipo checkbox 
 * dentro de un JTree
 * <br>
 * Basado en: 
 * Definitive Guide to Swing for Java 2, Second Edition
 * By John Zukowski     
 * ISBN: 1-893115-78-X
 * Publisher: APress
 * 
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

public class TreeCheckNode {

	private String text;
	private boolean selected;
	private String transaction;
	private boolean enabled = true;
	
	public TreeCheckNode(String text, boolean selected) {
		this.text = text;
		this.selected = selected;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean newValue) {
		selected = newValue;
	}

	public String getText() {
		return text;
	}

	public void setText(String newValue) {
		text = newValue;
	}

	public String toString() {
		return getClass().getName() + "[" + text + "/" + selected + "]";
	}

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}