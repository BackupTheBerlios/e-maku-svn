package client.gui.components;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import common.gui.forms.GenericForm;

public class EmakuDetailedProductCellEditor  extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = -1848478314896428718L;
	private EmakuDetailedProduct detailedProduc;
	
	public EmakuDetailedProductCellEditor(GenericForm gfforma,int index,ColumnsArgsGenerator[] ATFDargs,JTable table) {
		detailedProduc = new EmakuDetailedProduct(gfforma,
								ATFDargs[index].getSqlCombo(),
								ATFDargs[index].getImportCombos(),
								ATFDargs[index].getKeyDataSearch(),
								ATFDargs[index].isBlankArgs(),
								ATFDargs[index].isDataBeep(),
								ATFDargs[index].getNoDataMessage(),
								ATFDargs[index].getSelected(),
								ATFDargs[index].getRepeatData(),table);
		table.addKeyListener(detailedProduc);
		detailedProduc.setColumnAmount(ATFDargs[index].getColumnAmount());
		detailedProduc.setColumnDebit(ATFDargs[index].getColumnDebit());
		detailedProduc.setColumnCredit(ATFDargs[index].getColumnCredit());
		detailedProduc.setColumnIdProdServ(ATFDargs[index].getColumnIdProdServ());
		detailedProduc.setColumnIdWareHouse(ATFDargs[index].getColumnIdWareHouse());
		detailedProduc.setColumnValue(ATFDargs[index].getColumnValue());
	}
	
	public Component getTableCellEditorComponent(
			JTable table, Object value, boolean isSelected, int row, int column) {
		detailedProduc.setCode(value.toString());
		detailedProduc.setRowIndex(row);
		detailedProduc.setColumnIndex(column);
		detailedProduc.setEnabled(table.getModel().isCellEditable(row, column));
		return detailedProduc;
	}

	public String getCellEditorValue() {
		return detailedProduc.getValue();
	}
}
