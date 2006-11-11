package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import common.gui.components.XMLTextField;
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
 * Esta clase se de generar un componente de busqueda de rapida de codigos de 
 * diferentes tipos, sea productos, terceros o cuentas contables, dependiendo
 * de su parametrizacion
 *  <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 */

public class EmakuDataSearch extends JTextField implements KeyListener,PopupMenuListener,FocusListener {

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
	private String keyValue;
	
	public EmakuDataSearch(GenericForm GFforma,
						   String sql,
						   String keyValue,
						   boolean dataBeep,
						   String dataMessage,
						   int selected,
						   int repeatData) {
		this.addKeyListener(this);
		this.setLayout(new BorderLayout());
		this.GFforma=GFforma;
		this.keyValue=keyValue;
		XMLTFkey = new XMLTextField("KEY", 16, 50);
		XMLTFkey.addFocusListener(this);
		String[] args = new String[repeatData];
		for (int i=0;i<args.length;i++) {
			args[i]=keyValue;
		}
		SQLCBselection = new SQLComboBox(GFforma,sql,args,dataBeep,selected,dataMessage);
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
		JPdataSearch.add(SQLCBselection, BorderLayout.SOUTH);
		JPdataSearch.add(JPNorth, BorderLayout.CENTER);
		return JPdataSearch;
	}

	/**
	 * Este metodo limpia los elementos del panel de busqueda
	 */

	protected void clean() {
		this.setText("");
		XMLTFkey.setText("");
		SQLCBselection.clear();
	}
	
	/**
	 * Este metodo retorna el valor resultante de la busqueda
	 * @return retorna el codigo consultado
	 */
	protected String getValue() {
		return this.getText();
	}

	/**
	 * Este metodo visualiza el menu emergente con sus coordenadas respectivas
	 */
	
	private void showDataSearch() {
		updateUI();
		int x = this.getWidth() - (int) JPMpopup.getPreferredSize().getWidth();
		int y = this.getHeight();
		JPMpopup.show(this,x,y);
		XMLTFkey.requestFocusInWindow();
		dataSelected = false;
	}
	
	public void popupMenuCanceled(PopupMenuEvent e) {
	}

	/**
	 * Este metodo se genera al terminar el evento de seleccion en el combo
	 * y transfiere el codigo obtenido por la seleccion al componente principal
	 */
	
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		if (JPMpopup.isVisible()) {
			dataSelected = true;
			this.setText(SQLCBselection.getStringCombo());
			JPMpopup.setVisible(false);
		}
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}

	public void focusGained(FocusEvent e) {
	}

	/**
	 * Este metodo se genera cuando el field de consulta por palabra clave pierde
	 * el foco, se encarga de exportar su valor con la llave correspondiente para
	 * que luego el combo genere la respectiva consulta
	 */
	public void focusLost(FocusEvent e) {
		GFforma.setExternalValues(keyValue,XMLTFkey.getText());
	}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_F3) {
			showDataSearch();
		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}