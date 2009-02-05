package client.gui.components;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import common.gui.forms.GenericForm;

public class EmakuTextileProductCellEditor extends AbstractCellEditor implements TableCellEditor {

	/**
	 * 
	 */
	private EmakuTextileProduct textileProduct;
	private static final long serialVersionUID = -8528725127126670352L;
	
	public EmakuTextileProductCellEditor(GenericForm gfforma,int index,ColumnsArgsGenerator[] ATFDargs,JTable table) {
		textileProduct = new EmakuTextileProduct(gfforma,table);
		table.addKeyListener(textileProduct);
	}

	/**
	 * Retorno el componente
	 */
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		textileProduct.setEnabled(table.getModel().isCellEditable(row, column));
		return textileProduct;
	}

	/**
	 * Retorno el valor del componente
	 */
	
	public Object getCellEditorValue() {
		return textileProduct.getValue();
	}

}
