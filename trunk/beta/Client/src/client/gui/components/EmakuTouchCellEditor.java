package client.gui.components;

import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jdom.Document;
import org.jdom.Element;

import common.gui.forms.GenericForm;

public class EmakuTouchCellEditor  extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = -1848478314896428718L;
	private EmakuTouchCell emakuTouchCell;
	private boolean selected;
	public EmakuTouchCellEditor(GenericForm gfforma,Element e) {
		Element root = new Element("parameters");
		root.addContent(e.cloneContent());
		Document doc = new Document(root);
		emakuTouchCell = new EmakuTouchCell(gfforma,doc) {
			private static final long serialVersionUID = 1L;
			public void displayButtons() {
				setNotified(false);
				if (!popupTouch.isVisible()) {
					updateUI();
					int psize = (int) popupTouch.getPreferredSize().getHeight();
					int x = this.getX();
					int y = this.getY() - psize - this.getHeight();
					try {
						popupTouch.show(this,x,y);
						this.setText("");
					}
					catch (IllegalComponentStateException ex) {}
				}
			}
		};
		emakuTouchCell.getJButtonOk().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected = true;
			}
		});
		emakuTouchCell.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (selected) { 
					stopCellEditing();
				}
			}
		});
	}
	
	public Component getTableCellEditorComponent(
			JTable table, Object value, boolean isSelected, int row, int column) {
		emakuTouchCell.setText(String.valueOf(value));
		selected = false;
		return emakuTouchCell.getJPtext();
	}

	public Object getCellEditorValue() {
		if ("NUMERIC".equals(emakuTouchCell.getType())) {
			return emakuTouchCell.getNumberValue();
		}
		return emakuTouchCell.getValue();
	}
}