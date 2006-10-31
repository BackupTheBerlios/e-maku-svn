package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import common.gui.components.XMLTextField;
import common.gui.forms.GenericForm;

public class EmakuDataSearch extends JPanel implements ActionListener,PopupMenuListener,FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 246103248621691834L;

	private JPanel JPNorth;
	private XMLTextField XMLTFkey;
	private SQLComboBox SQLCBselection;
	private JPopupMenu JPMpopup;
	private GenericForm GFforma;
	private boolean dataSelected;
	/**
	 * 
	 */
	private JTextField JTFvalue;
	private String keyValue;
	
	public EmakuDataSearch(GenericForm GFforma,String sql,String keyValue,int repeatData) {
		JTFvalue = new JTextField();
		JTFvalue.addActionListener(this);
		this.setLayout(new BorderLayout());
		this.add(JTFvalue,BorderLayout.CENTER);
		this.GFforma=GFforma;
		this.keyValue=keyValue;
		XMLTFkey = new XMLTextField("KEY", 16, 50);
		XMLTFkey.addFocusListener(this);
		String[] args = new String[repeatData];
		for (int i=0;i<args.length;i++) {
			args[i]=keyValue;
		}
		
		SQLCBselection = new SQLComboBox(GFforma,sql,args);
		SQLCBselection.addPopupMenuListener(this);
		SQLCBselection.setPreferredSize(new Dimension(100,20)); 
		JPMpopup = new JPopupMenu() {
			private static final long serialVersionUID = -6078272560337577761L;

			public void setVisible(boolean b) {
				Boolean isCanceled = (Boolean) getClientProperty("JPopupMenu.firePopupMenuCanceled");
				if (b
						|| (!b && dataSelected)
						|| ((isCanceled != null) && !b && isCanceled
								.booleanValue())) {
					super.setVisible(b);
				}
			}
		};

		JPMpopup.setLightWeightPopupEnabled(true);
		JPMpopup.add(dataSearch());

	}

	private JPanel dataSearch() {
		JPanel JPdataSearch = new JPanel(new BorderLayout());
		JPNorth = new JPanel(new BorderLayout());
		JPNorth.add(XMLTFkey.getLabel(), BorderLayout.WEST);
		JPNorth.add(XMLTFkey.getJPtext(), BorderLayout.CENTER);
		JPdataSearch.add(JPNorth, BorderLayout.CENTER);
		JPdataSearch.add(SQLCBselection, BorderLayout.SOUTH);
		return JPdataSearch;
	}

	protected void clean() {
		JTFvalue.setText("");
		XMLTFkey.setText("");
		SQLCBselection.clean();
	}
	
	protected String getValue() {
		return JTFvalue.getText();
	}

	public void updateUI() {
		super.updateUI();
		setEnabled(isEnabled());
	}

	public void actionPerformed(ActionEvent e) {
		showDataSearch();
	}

	private void showDataSearch() {
		int x = JTFvalue.getWidth()
		- (int) JPMpopup.getPreferredSize().getWidth();
		int y = JTFvalue.getY() + JTFvalue.getHeight();
		JPMpopup.show(JTFvalue,x,y);
		XMLTFkey.requestFocus();
		dataSelected = false;
	}
	
	public void popupMenuCanceled(PopupMenuEvent e) {
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		if (JPMpopup.isVisible()) {
			dataSelected = true;
			JTFvalue.setText(SQLCBselection.getStringCombo());
//			JPMpopup.transferFocus();
			JPMpopup.setVisible(false);
		}
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}

	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent e) {
		GFforma.setExternalValues(keyValue,XMLTFkey.getText());
	}

	public JTextField getJTFvalue() {
		return JTFvalue;
	}
}
