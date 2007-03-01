package client.gui.components;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class EmakuDetailedProductCellEditor  extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = -1848478314896428718L;
	private EmakuDetailedProduct detailedProduc;
	
	public EmakuDetailedProductCellEditor() {
		detailedProduc = new EmakuDetailedProduct();
	}
	
	public Component getTableCellEditorComponent(
			JTable table, Object value, boolean isSelected, int row, int column) {
		table.addKeyListener(detailedProduc);
		return detailedProduc;
	}

	public String getCellEditorValue() {
		return detailedProduc.getValue();
	}
}
