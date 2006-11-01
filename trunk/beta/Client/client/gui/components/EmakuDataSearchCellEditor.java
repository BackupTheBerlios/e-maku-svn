package client.gui.components;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import common.gui.forms.GenericForm;

/**
 * TableFindData.java Creado el 31-oct-2006
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
 * Esta clase se encarga de acoplar a la clase EmakuDataSearch a un editor de
 * celdas personalizado, para una tabla
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 */

public class EmakuDataSearchCellEditor extends AbstractCellEditor 
implements TableCellEditor {

	private static final long serialVersionUID = 7796012712881462868L;

	private EmakuDataSearch dataSearch;
	protected int clickCountToStart = 1;

	public EmakuDataSearchCellEditor(GenericForm gfforma, 
									 String sql, 
									 String keyValue, 
									 boolean dataBeep,
									 String noDataMessage,
									 int selected,
									 int repeatData) {
		dataSearch = new EmakuDataSearch(gfforma,sql,keyValue,dataBeep,noDataMessage,selected,repeatData);
		this.clickCountToStart = 2;
	}

	public void setClickCountToStart(int count) {
		clickCountToStart = count;
	}

	public int getClickCountToStart() {
		return clickCountToStart;
	}

	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
		}
		return true;
	}

	public Object getCellEditorValue() {
		return dataSearch.getValue();
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		dataSearch.clean();
		return dataSearch;
	}
}
