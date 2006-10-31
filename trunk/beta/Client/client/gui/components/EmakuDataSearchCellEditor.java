package client.gui.components;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import common.gui.forms.GenericForm;

public class EmakuDataSearchCellEditor extends AbstractCellEditor implements
TableCellEditor {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7796012712881462868L;
	private EmakuDataSearch dataSearch;
	
	public EmakuDataSearchCellEditor(GenericForm gfforma,final JTable refJTable,String sql,String keyValue,int repeatData) {
		dataSearch = new EmakuDataSearch(gfforma,sql,keyValue,repeatData);
		
		dataSearch.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				dataSearch.getJTFvalue().requestFocus(false);
			}
		});
/*
		dataSearch.addKeyListener(new KeyAdapter(){
			public void keyPressed(final KeyEvent e) {
				Thread t = new Thread() {
					public void run() {
						try {
							Thread.sleep(100);
						}
						catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						int keyCode = e.getKeyCode();
						switch (keyCode) {
							case KeyEvent.VK_TAB:
							case KeyEvent.VK_LEFT:
							case KeyEvent.VK_RIGHT:
							case KeyEvent.VK_UP:
							case KeyEvent.VK_DOWN:
								refJTable.requestFocus(false);
								break;
						}						
					}
				};
				t.start();
				}						
			});
*/
	}
	
	public Object getCellEditorValue() {
		return dataSearch.getValue();
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		dataSearch.clean();
		return dataSearch;
	}

}
