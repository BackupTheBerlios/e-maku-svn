package client.gui.components;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.IllegalComponentStateException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import common.gui.components.XMLTextField;
import common.gui.forms.GenericForm;
import common.misc.Icons;
import common.misc.language.Language;

public class EmakuDetailedProduct extends JComponent implements KeyListener, ActionListener  {

	private static final long serialVersionUID = 8386003217712730190L;
	private JPanel contentPane;
	private JPopupMenu JPMpopup;
	private XMLTextField XMLTFCode;
	private XMLTextField XMLTFDescription;
	private XMLTextField XMLTFPrice;
	private XMLTextField XMLTFAmount;
	private XMLTextField XMLTFValue;
	private XMLTextField XMLTFTotal;
	private JRadioButton JRDebit;
	private JRadioButton JRCredit;
	private JToolBar statusBar;
	private JPanel JPFields;
	private JPanel JPLabels;
	private JLabel statusLabel;
	private EmakuDataSearch dataSearch;
	private GenericForm genericForm;
	private boolean subPopup;
	private JButton JBAccept;
	private JButton JBCancel;
	
	public EmakuDetailedProduct(GenericForm GFforma,
							   String sql,
							   String[] externalValues,
							   String keyValue,
							   boolean blankArgs,
							   boolean dataBeep,
							   String dataMessage,
							   int selected,
							   int repeatData) {
		this.genericForm = GFforma;
		dataSearch = new EmakuDataSearch(
							genericForm,sql,externalValues,
							keyValue,blankArgs,dataBeep,
							dataMessage,selected,repeatData) {
			private static final long serialVersionUID = 6294167396739991593L;
			public void storeData() {
				super.storeData();
				XMLTFCode.setText(getValue());
				subPopup = false;
				showDetailed();
			}
		};
		dataSearch.addKeyListener(this);
		this.addKeyListener(this);
		initComps();
	}
	
	private void initComps() {
		contentPane = new JPanel(new BorderLayout());
		statusBar = new JToolBar();
		statusBar.setLayout(new GridLayout(1,1));
		statusBar.setFloatable(false);
		statusLabel = new JLabel(" ");
		statusLabel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		statusBar.add(statusLabel);
		JPFields = new JPanel(new GridLayout(7,1));
		JPLabels = new JPanel(new GridLayout(7,1));

		JPMpopup = new JPopupMenu() {
			private static final long serialVersionUID = 1L;
			public void setVisible(boolean b) {
				//System.out.println("Visible: " +b);
				if (subPopup) {
					super.setVisible(true);
				}
				else{
					if (!b) { this.requestFocusInWindow(); }
					super.setVisible(b);
				}
			}
		};
		JPMpopup.setLayout(new BorderLayout());
		JPMpopup.setLightWeightPopupEnabled(true);
		JPMpopup.setBorderPainted(true);
		JPMpopup.add(contentPane,BorderLayout.CENTER);
		JPMpopup.add(new JPanel(),BorderLayout.NORTH);
		JPMpopup.add(new JPanel(),BorderLayout.WEST);
		JPMpopup.add(statusBar,BorderLayout.SOUTH);
		
		XMLTFCode 		 = new XMLTextField("CODE", 9, 10, XMLTextField.TEXT);
		XMLTFDescription = new XMLTextField("DESCRIPCION", 9, 10, XMLTextField.TEXT);
		XMLTFPrice		 = new XMLTextField("PRICE", 9, 10, XMLTextField.TEXT);
		XMLTFAmount		 = new XMLTextField("CANTIDAD", 9, 10, XMLTextField.TEXT);
		XMLTFValue		 = new XMLTextField("VALOR", 9, 10, XMLTextField.TEXT);
		XMLTFTotal		 = new XMLTextField("TOTAL", 9, 10, XMLTextField.TEXT);
		JRDebit			 = new JRadioButton();
		JRCredit		 = new JRadioButton();
		
		XMLTFCode.addKeyListener(this);
		
		ButtonGroup group = new ButtonGroup();
		group.add(JRDebit);
		group.add(JRCredit);
		
		JPanel p1 = new JPanel();
		p1.add(new JLabel(Language.getWord("DEBITO")));
		p1.add(JRDebit);
		
		JPanel p2 = new JPanel();
		p2.add(new JLabel(Language.getWord("CREDIT")));
		p2.add(JRCredit);
		ImageIcon i1 = new ImageIcon(getClass().getResource(Icons.getIcon("DELETE")));
		ImageIcon i2 = new ImageIcon(getClass().getResource(Icons.getIcon("EXIT")));
		
		JBAccept = new JButton(i1);
		JBAccept.setActionCommand("accept");
		JBAccept.addActionListener(this);
		
		JBCancel = new JButton(i2);
		JBCancel.setActionCommand("cancel");
		JBCancel.addActionListener(this);
		
		JPanel jpbuttons = new JPanel();
		jpbuttons.add(JBAccept);
		jpbuttons.add(JBCancel);
		
		JPLabels.add(XMLTFCode.getLabel());
		JPLabels.add(XMLTFDescription.getLabel());
		JPLabels.add(XMLTFPrice.getLabel());
		JPLabels.add(XMLTFAmount.getLabel());
		JPLabels.add(XMLTFValue.getLabel());
		JPLabels.add(XMLTFTotal.getLabel());
		JPLabels.add(p1);
		
		JPFields.add(XMLTFCode.getJPtext());
		JPFields.add(XMLTFDescription.getJPtext());
		JPFields.add(XMLTFPrice.getJPtext());
		JPFields.add(XMLTFAmount.getJPtext());
		JPFields.add(XMLTFValue.getJPtext());
		JPFields.add(XMLTFTotal.getJPtext());
		JPFields.add(p2);
		
		contentPane.add(JPLabels,BorderLayout.WEST);
		contentPane.add(JPFields,BorderLayout.CENTER);
		contentPane.add(jpbuttons,BorderLayout.SOUTH);
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					showDetailed();
				}
			}
		});
	}
	
	public void showDetailed() {
		if (!JPMpopup.isVisible()) {
			updateUI();
			int psize = (int) JPMpopup.getPreferredSize().getWidth();
			int x = this.getWidth() - psize;
			int y = this.getY();
			try { JPMpopup.show(this,x,y); }
			catch (IllegalComponentStateException e) { e.printStackTrace(); }
		}
		XMLTFCode.requestFocusInWindow();
	}

	public String getValue() {
		return XMLTFCode.getText();
	}
	
	public void setCode(String code) {
		XMLTFCode.setText(code);
	}
	
	
	public void showDataSearch() {
		if (!dataSearch.getPopup().isVisible()) {
			updateUI();
			int psize = (int) dataSearch.getPopup().getPreferredSize().getWidth();
			int x = this.getWidth() - psize;
			int y = this.getHeight();
			try {
				dataSearch.getPopup().show(this,x,y);
				dataSearch.getXMLTFkey().requestFocusInWindow();
				dataSearch.setDataSelected(false);
			}
			catch (IllegalComponentStateException e) {}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		Object s = e.getSource();
		switch(keyCode) {
			case KeyEvent.VK_ESCAPE:
				JPMpopup.setVisible(false);
				break;
			case KeyEvent.VK_F2:
				if (s.equals(XMLTFCode)) {
					subPopup = true;
					showDataSearch();
				} else {
					showDetailed();
				}
				break;
			default :
				if (((keyCode >=60 &&  keyCode<=71) ||  keyCode>=65 &&  keyCode<=126)) {
					showDetailed();
				}
				break;
		}
	}

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("accept".equals(command)) {
			
		}
		else if ("cancel".equals(command)) {
			JPMpopup.setVisible(false);
		}
		
	}
}