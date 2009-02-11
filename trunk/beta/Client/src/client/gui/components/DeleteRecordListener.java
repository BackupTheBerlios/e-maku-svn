package client.gui.components;

import java.util.EventListener;

/**
 * DeleteRecordListener.java Creado el 10-feb-2009
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
 * 
 * @author <A href='mailto:pipelx@gmail.com'>Luis Felipe Hernandez </A>
 */
public interface DeleteRecordListener extends EventListener {
	 public void deleteRecordEvent(DeleteRecordEvent e);
}