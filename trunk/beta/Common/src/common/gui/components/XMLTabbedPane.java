package common.gui.components;

import java.util.Iterator;

import javax.swing.JTabbedPane;

import org.jdom.Element;

/**
 * XMLTabbedPane.java Creado el 04-nov-2006
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
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
 * Esta clase se extiende de JTabbedPane, su finalidad o fue creada para 
 * implementar el metodo setSelectedIndex con recepcion de argumentos tipo Element
 * para poder posicionar la tab inicial en desde generación de eventos por botones
 *  <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 */

public class XMLTabbedPane extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8808371519530823930L;
	
	public void setSelectedIndex(Element index) {
		Iterator it = index.getChildren("arg").iterator();
		while (it.hasNext()) { 
			Element arg = (Element) it.next();
			if ("index".equals(arg.getAttributeValue("attribute"))) {
				try {
					this.setSelectedIndex(Integer.parseInt(arg.getValue()));
				}
				catch(NumberFormatException NFEe) {
					this.setSelectedIndex(-1);
				}
			}
		}
	}
}
