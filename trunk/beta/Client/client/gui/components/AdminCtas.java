package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.TableCellRenderer;

import client.control.SuccessEvent;
import client.control.SuccessListener;
import client.control.ValidHeaders;
import common.gui.components.VoidPackageException;
import common.gui.components.XMLTextField;
import common.gui.forms.GenericForm;
import common.gui.forms.NotFoundComponentException;
import common.misc.language.Language;
import common.transactions.STException;
import common.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * AdminCtas2.java Creado el 21-oct-2004
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
 * Informacion de la clase <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda </A>
 */
public class AdminCtas extends JPanel implements ActionListener,
		SuccessListener {

	private static final long serialVersionUID = -8164365878390790722L;

	private XMLTextField XMLTFcuenta;
	private XMLTextField XMLTFnombre;
	private XMLButtonGroup XMLBGgroup;
	private XMLCheckBox XMLCBnaturaleza;
	private XMLCheckBox XMLCBcentro;
	private XMLCheckBox XMLCBajuste;
	private XMLCheckBox XMLCBterceros;
	private XMLCheckBox XMLCBinventarios;
	private XMLCheckBox XMLCBretencion;
	private XMLTextField XMLTFbase;
	private XMLTextField XMLTFporcentaje;
	private XMLComboBox XMLCBmoneda;
	private JSplitPane JSsur;
	private GenericForm GFforma;
	private JPanel JPgeneral;
	private String padre;
	private JTable JTpuc;
	private String nameButton = "SAVE";
	private String valueArgs = "NEW";
	private boolean exists = false;

	/**
	 * Este constructor se instancia cuando se hace una llamada a la clase sin
	 * argumentos
	 * 
	 * @param GFforma
	 */
	public AdminCtas(GenericForm GFforma) {
		this.GFforma = GFforma;
		loading();
	}

	/**
	 * Este constructor se instancia cuando se hace una llamada a la clase con
	 * argumentos
	 * 
	 * @param GFforma
	 * @param doc
	 */
	public AdminCtas(GenericForm GFforma, Document doc) {

		super(new BorderLayout(10, 10));

		this.GFforma = GFforma;
		Iterator i = doc.getRootElement().getChildren().iterator();

		while (i.hasNext()) {
			Element elm = (Element) i.next();
			String value = elm.getValue();

			if ("NEW".equals(value)) {
				nameButton = "SAVE";
				valueArgs = "NEW";
			} else if ("EDIT".equals(value)) {
				nameButton = "SAVEAS";
				valueArgs = "EDIT";
			} else if ("DELETE".equals(value)) {
				nameButton = "DELETE";
				valueArgs = "DELETE";
			}
		}
		loading();
	}

	/**
	 * Este metodo se encarga de ensamblar la forma
	 */
	private void loading() {
		JSsur = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JPgeneral = getDetalle();

		JPanel JPconten = new JPanel(new BorderLayout());

		ValidHeaders.addSuccessListener(this);

		JTpuc = new JTable() {
			private static final long serialVersionUID = -6734025632190628817L;

			public Component prepareRenderer(TableCellRenderer renderer,
					int rowIndex, int vColIndex) {
				Component c = super.prepareRenderer(renderer, rowIndex,
						vColIndex);
				if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
					c.setBackground(new Color(230, 230, 230));
				} else {
					// If not shaded, match the table's background
					c.setBackground(getBackground());
				}
				return c;
			}
			public void paintComponent(Graphics g) {
		        Graphics2D g2 = (Graphics2D)g;
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                            RenderingHints.VALUE_ANTIALIAS_ON);
		        super.paintComponent(g);
		    }
		};

		JTpuc.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JTpuc.setSelectionBackground(Color.blue);
		JTpuc.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String codigo = null;
					codigo = (String) JTpuc.getValueAt(JTpuc.getSelectedRow(),
							0);
					if (codigo != null) {
						XMLTFcuenta.setText(codigo.trim());
						XMLTFcuenta.requestFocus();
						XMLTFcuenta.transferFocus();
						JTpuc.requestFocus();
					}
				}
			}

		});

		JTpuc.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() == 2) {
					String codigo = null;
					codigo = (String) JTpuc.getValueAt(JTpuc.getSelectedRow(),
							0);
					if (codigo != null) {
						XMLTFcuenta.setText(codigo.trim());
						XMLTFcuenta.requestFocus();
						XMLTFcuenta.transferFocus();
						JTpuc.requestFocus();
					}
				}
			}
		});
		JTpuc.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		loadingTablePUC();
		JScrollPane JSscroll = new JScrollPane(JTpuc);
		JSscroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JSscroll
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		JSsur.setLeftComponent(JSscroll);
		JSsur.setOneTouchExpandable(true);
		JSsur.setDividerLocation(250);
		JSsur.setResizeWeight(.5D);
		JSsur.addHierarchyBoundsListener(new HierarchyBoundsListener() {

			public void ancestorResized(HierarchyEvent e) {
				JSsur.setDividerLocation(JSsur.getMaximumDividerLocation());
			}

			public void ancestorMoved(HierarchyEvent e) {
				JSsur.setDividerLocation(JSsur.getMaximumDividerLocation());
			}
		});

		JPconten.add(JPgeneral, BorderLayout.CENTER);
		JPconten.add(new JPanel(), BorderLayout.WEST);
		JPconten.add(new JPanel(), BorderLayout.NORTH);
		JPconten.add(new JPanel(), BorderLayout.EAST);
		JSsur.setRightComponent(JPconten);
		this.add(JSsur, BorderLayout.CENTER);

	}

	public void procesarCta() {

		Document doc = null;
		Element row = null;
		List li = null;
		Element el = null;
		String texto = XMLTFcuenta.getText();
		int longitud = texto.length();

		/*
		 * Validaci�n nomenclatura valida 1 caracter: Clase 2 caracteres: Grupo
		 * 4 caracteres: Cuenta 6 caracteres: SubCuenta 8 caracteres: Auxiliar
		 * 10 caracteres: subAuxiliar
		 * 
		 * no se permite el ingreso de 3, 5 o 7 caracteres
		 */
		if (longitud > 0)
			if (longitud == 1 || (longitud % 2) == 0) {
				try {

					/*
					 * Se consulta si la cuenta digitada ya existe
					 */
					doc = STResultSet.getResultSetST("SEL0024", new String[] { texto });

					row = doc.getRootElement().getChild("row");

					/*
					 * En caso de existir se despliega su nombre y su
					 * configuracion
					 * 
					 */
					try {
						li = row.getChildren();
						clean();
						if (valueArgs.equals("EDIT"))
							enabledForm(true);
						else
							enabledForm(false);

						/*
						 * Este objeto almacena el tipo de cuenta, para asi
						 * genear su despliege
						 */

						el = (Element) li.get(2);
						XMLTFcuenta.setText(el.getTextTrim());
						/*
						 * Las dos siguientes lineas obtienen de la consulta el
						 * nombre y la almacenan en el XMLTFnombre
						 */

						el = (Element) li.get(3);
						XMLTFnombre.setText(el.getTextTrim());

						/*
						 * Las dos siguientes lineas obtienes de la consulta el
						 * tipo de cuenta contable.
						 */
						el = (Element) li.get(5);
						String tipo = el.getTextTrim();

						if (tipo.equals("f") || tipo.equals("0")) {
							XMLBGgroup.setSelected("DETALLE");
							loadInfo(row);
						} else {
							XMLBGgroup.setSelected("MAYOR");
						}

						if (valueArgs.equals("NEW"))
							GFforma.setEnabledButton(nameButton, false);
						else
							GFforma.setEnabledButton(nameButton, true);
						exists = true;
					} catch (NullPointerException e1) {
						/*
						 * En caso de no existir la cuenta, entonces se procede
						 * a desplegar el panel para la creacion de una nueva
						 * cuenta.
						 */

						if (valueArgs.equals("NEW"))
							GFforma.setEnabledButton(nameButton, true);
						else
							GFforma.setEnabledButton(nameButton, false);

						int fin = longitud - 2;

						/*
						 * Se verifica que la cuenta a crear tenga padre, esta
						 * consulta retorna 1 si la cuenta a crear tiene padre y
						 * 0 si no lo tiene
						 */
						Document doc2 = STResultSet.getResultSetST("SEL0025",
								new String[] { texto.substring(0, fin) });
						cleanData();

						/*
						 * Si la cuenta si tiene padre entonces ...
						 */

						try {

							row = doc2.getRootElement().getChild("row");
							li = row.getChildren();
							el = (Element) li.get(0);
							padre = el.getTextTrim();
							XMLTFnombre.requestFocus();
						} catch (NullPointerException NPEe) {
							clean();
							enabledForm(false);

							/*
							 * En caso de que la consulta de retorne null en la
							 * consulta del padre de la cuenta entonces se
							 * muestra un mensaje de error.
							 */
							JOptionPane.showInternalMessageDialog(GFforma,
									Language.getWord("ERROR_NOTFATHER"),
									Language.getWord("ERROR_MESSAGE"),
									JOptionPane.ERROR_MESSAGE);
						}

						exists = false;
					}
				} catch (STException e1) {
					e1.printStackTrace();
				}

			} else {
				JOptionPane.showInternalMessageDialog(GFforma, Language
						.getWord("ERR_INVALID_CODE"), Language
						.getWord("ERROR_MESSAGE"), JOptionPane.ERROR_MESSAGE);
				GFforma.setEnabledButton(nameButton, false);
			}
	}

	/**
	 * Metodo encargado de cargar el puc en la tabla
	 */
	private void loadingTablePUC() {
		class LoadTable extends Thread {
			private JTable JTpuc;

			private String sql;

			public LoadTable(JTable JTpuc, String sql) {
				this.JTpuc = JTpuc;
				this.sql = sql;
			}

			public void run() {
				JTpuc.setModel(new QueryTableData(sql));
				JTpuc.getColumnModel().setColumnMargin(5);
				JTpuc.getColumn(JTpuc.getColumnName(0)).setPreferredWidth(
						(JTpuc.getWidth() * 20) / 100);
				JTpuc.getColumn(JTpuc.getColumnName(1)).setPreferredWidth(
						(JTpuc.getWidth() * 80) / 100);
				JTpuc.addAncestorListener(new AncestorListener() {

					public void ancestorMoved(AncestorEvent event) {
						JTpuc.getColumn(JTpuc.getColumnName(0))
								.setPreferredWidth(
										(JTpuc.getWidth() * 20) / 100);
						JTpuc.getColumn(JTpuc.getColumnName(1))
								.setPreferredWidth(
										(JTpuc.getWidth() * 80) / 100);
					}

					public void ancestorRemoved(AncestorEvent event) {
					}

					public void ancestorAdded(AncestorEvent event) {
					}

				});
			}
		}
		new LoadTable(JTpuc, "SEL0050").start();
	}

	/**
	 * Este metodo retorna un panel con la parte superior de forma.
	 * 
	 * @return
	 */
	public JPanel getDetalle() {

		JPanel JPdetalle = new JPanel(new BorderLayout());
		JPanel JPcta = new JPanel(new BorderLayout());
		JPanel JPlabels = new JPanel(new GridLayout(2, 1));
		JPanel JPfields = new JPanel(new GridLayout(2, 1));

		XMLTFcuenta = new XMLTextField("CUENTA", 13, 10, XMLTextField.TEXT,
				XMLTextField.NUMERIC);
		XMLTFnombre = new XMLTextField("NOMBRE", 25, 120);
		XMLTFcuenta.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				procesarCta();
			}
		});

		JPlabels.add(XMLTFcuenta.getLabel());
		JPfields.add(XMLTFcuenta.getJPtext());
		JPlabels.add(XMLTFnombre.getLabel());
		JPfields.add(XMLTFnombre.getJPtext());
		JPcta.add(JPlabels, BorderLayout.WEST);
		JPcta.add(JPfields, BorderLayout.CENTER);

		JPanel JPcomps = new JPanel(new BorderLayout());
		JPcomps.add(getChecks(), BorderLayout.NORTH);
		JPcomps.add(getFields(), BorderLayout.CENTER);
		JPdetalle.add(JPcta, BorderLayout.NORTH);
		JPdetalle.add(JPcomps, BorderLayout.CENTER);
		enabledForm(false);
		return JPdetalle;
	}

	/**
	 * Este medoto retorna un panel con los componentes de tipo casilla de
	 * verificacion
	 * 
	 * @return
	 */
	public JPanel getChecks() {

		XMLBGgroup = new XMLButtonGroup(new String[] { "MAYOR", "DETALLE" },
				"MAYOR", 1, 2);
		Vector Vradio = XMLBGgroup.getVradio();
		for (int i = 0; i < Vradio.size(); i++) {
			((IDRadioButton) Vradio.get(i)).addActionListener(this);
		}
		XMLCBnaturaleza = new XMLCheckBox("NATURALEZA");
		XMLCBcentro = new XMLCheckBox("CENTRO");
		XMLCBajuste = new XMLCheckBox("AJUSTE");
		XMLCBterceros = new XMLCheckBox("TERCEROS_CTA");
		XMLCBinventarios = new XMLCheckBox("INVENTARIOS_CTA");
		XMLCBretencion = new XMLCheckBox("RETENCION");
		XMLTFbase = new XMLTextField("BASE", 9, 10, XMLTextField.NUMERIC);
		XMLTFporcentaje = new XMLTextField("PORCENTAJE", 9, 10,
				XMLTextField.NUMERIC);
		XMLCBmoneda = new XMLComboBox(GFforma, "SEL0023", "MONEDA");

		XMLTFbase.setHorizontalAlignment(SwingConstants.RIGHT);
		XMLTFporcentaje.setHorizontalAlignment(SwingConstants.RIGHT);
		JPanel JPchecks = new JPanel(new GridLayout(7, 1));

		JPchecks.add(XMLBGgroup);
		JPchecks.add(XMLCBnaturaleza.getJPcheck());
		JPchecks.add(XMLCBcentro.getJPcheck());
		JPchecks.add(XMLCBajuste.getJPcheck());
		JPchecks.add(XMLCBterceros.getJPcheck());
		JPchecks.add(XMLCBinventarios.getJPcheck());
		JPchecks.add(XMLCBretencion.getJPcheck());
		return JPchecks;
	}

	/**
	 * Este metodo retorna un panel con los ultimos 3 componenetes de la forma
	 * 
	 * @return
	 */
	public JPanel getFields() {

		JPanel JPbase = new JPanel(new BorderLayout());
		JPanel JPlabel = new JPanel(new GridLayout(3, 1));
		JPanel JPfields = new JPanel(new GridLayout(3, 1));
		JPanel JPValues = new JPanel(new BorderLayout());

		JPlabel.add(XMLTFbase.getLabel());
		JPlabel.add(XMLTFporcentaje.getLabel());
		JPlabel.add(XMLCBmoneda.getLabel());

		JPfields.add(XMLTFbase.getJPtext());
		JPfields.add(XMLTFporcentaje.getJPtext());
		JPfields.add(XMLCBmoneda.getJPcombo());

		JPbase.add(JPlabel, BorderLayout.WEST);
		JPbase.add(JPfields, BorderLayout.CENTER);
		JPValues.add(JPbase, BorderLayout.NORTH);

		return JPValues;
	}

	/**
	 * Este metodo se encarga de habilitar los componentes de la forma, segun su
	 * parametro
	 * 
	 * @param flag
	 *            si se pasa como argumento true, la forma se habilita, de lo
	 *            contrario la forma se deshabilita
	 */
	private void enabledForm(boolean flag) {

		XMLCBnaturaleza.setEnabled(flag);
		XMLCBcentro.setEnabled(flag);
		XMLCBajuste.setEnabled(flag);
		XMLCBterceros.setEnabled(flag);
		XMLCBinventarios.setEnabled(flag);
		XMLCBretencion.setEnabled(flag);
		XMLTFbase.setEnabled(flag);
		XMLTFporcentaje.setEnabled(flag);
		XMLCBmoneda.setEnabled(flag);

	}

	/**
	 * Este metodo se encarga de limpiar el valor de los componentes de la forma
	 */
	public void clean() {
		XMLTFcuenta.setText("");
		cleanData();
	}

	/**
	 * Este metodo se encarga de limpiar el valor de los componentes de la forma
	 */
	public void cleanData() {
		XMLTFnombre.setText("");
		XMLTFbase.setText("");
		XMLTFporcentaje.setText("");
		XMLBGgroup.setSelected("MAYOR");
		XMLCBnaturaleza.setSelected(false);
		XMLCBcentro.setSelected(false);
		XMLCBajuste.setSelected(false);
		XMLCBterceros.setSelected(false);
		XMLCBinventarios.setSelected(false);
		XMLCBretencion.setSelected(false);
		XMLCBmoneda.setSelectedIndex(0);

	}

	/**
	 * Este metodo retorna una referencia de si mismo
	 * 
	 * @return un JPanel
	 */
	public JPanel getPanel() {
		return this;
	}

	/**
	 * Este metodo se encarga de cargar la informacion de la cuenta consultada
	 * 
	 * @param pack
	 */
	public void loadInfo(Element query) {

		try {
			String id_cta = ((Element) query.getChildren().get(0))
					.getTextTrim();
			Document Dperfil = STResultSet.getResultSetST("SEL0048", new String[] { id_cta });
			Element pack = Dperfil.getRootElement().getChild("row");
			XMLCBnaturaleza.setSelected(getBoolean(pack, 0));
			XMLCBcentro.setSelected(getBoolean(pack, 1));
			XMLCBajuste.setSelected(getBoolean(pack, 2));
			XMLCBterceros.setSelected(getBoolean(pack, 3));
			XMLCBinventarios.setSelected(getBoolean(pack, 4));
			XMLCBretencion.setSelected(getBoolean(pack, 5));
			XMLTFbase.setText(getString(pack, 6));
			XMLTFporcentaje.setText(getString(pack, 7));
			XMLCBmoneda.setSelectedItem(getString(pack, 8) + " "
					+ getString(pack, 9));
		} catch (STException STe) {
			JOptionPane.showInternalMessageDialog(GFforma, Language
					.getWord("ERROR_PERFIL_CTA"), Language
					.getWord("ERROR_MESSAGE"), JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * consulta un boleano de un paquete xml
	 * 
	 * @param pack
	 *            paquete a consultar
	 * @param index
	 *            indice de la etiqueta consultada
	 * @return boolean retorna un valor booleano dependiendo de la etiqueta
	 *         consultada
	 */

	private boolean getBoolean(Element pack, int index) {
		Element bol = (Element) pack.getChildren().get(index);
		if (bol.getTextTrim().equals("t") || bol.getTextTrim().equals("true")
				|| bol.getTextTrim().equals("TRUE")
				|| bol.getTextTrim().equals("T")
				|| bol.getTextTrim().equals("1"))
			return true;
		else
			return false;
	}

	/**
	 * Este metodo retorna la cadena correspondiente al indice del elemto <xml/>
	 * recibido
	 * 
	 * @param pack
	 * @param index
	 * @return
	 */
	private String getString(Element pack, int index) {
		return ((Element) pack.getChildren().get(index)).getTextTrim();
	}

	/**
	 * Este metodo retorna el numero de la cuenta contable almacenada el
	 * XMLTFcuenta
	 * 
	 * @return
	 * @throws VoidPackageException
	 */
	public Element getPackage() throws VoidPackageException {
		Element pack = new Element("package");
		pack.addContent(XMLTFcuenta.getElementText());
		return pack;
	}

	/**
	 * Este metodo se encarga de retornar multiples paquetes <package/>,
	 * dependiendo de la parametrizacion recibida.
	 * 
	 * @return
	 * @throws VoidPackageException
	 */
	public Element[] getMultiPackage() throws VoidPackageException {
		Element pack[] = new Element[2];

		Element pack1 = new Element("package");
		Element Eid = new Element("field");
		Eid.setText(padre);

		Element Etipo = new Element("field").setText("1");
		Element Eactiva = new Element("field").setText("0");

		if (valueArgs.equals("NEW")) {
			pack1.addContent(Eid);
			pack1.addContent(XMLTFcuenta.getElementText("key"));
		}

		if (!XMLTFnombre.getText().equals("")) {
			Element element = new Element("field");
			element.setText(String.valueOf(XMLTFcuenta.getText().trim()
					.length()));
			pack1.addContent(element);
			pack1.addContent(XMLTFnombre.getElementText());

		} else
			throw new VoidPackageException(Language.getWord("NOMBRE"));

		Element pack2 = new Element("package");

		if (XMLBGgroup.getText().equals("DETALLE")) {
			Etipo.setText("0");
			Eactiva.setText("1");
			pack2.addContent(XMLCBterceros.getElementCheck());
			pack2.addContent(XMLCBinventarios.getElementCheck());
			pack2.addContent(XMLCBnaturaleza.getElementCheck());
			pack2.addContent(XMLCBcentro.getElementCheck());
			pack2.addContent(XMLCBajuste.getElementCheck());
			pack2.addContent(XMLCBretencion.getElementCheck());

			/*
			 * Se valida si el campo retencion esta vacio envie un 0
			 */

			if (XMLCBretencion.getTextCheck().equals("1")
					&& !XMLTFbase.getText().equals(""))
				pack2.addContent(XMLTFbase.getElementText());
			else if (XMLCBretencion.getTextCheck().equals("1"))
				throw new VoidPackageException(Language.getWord("BASE"));
			else
				pack2.addContent(new Element("field").setText("0"));

			if (!XMLTFbase.getText().equals("")) {
				pack2.addContent(XMLTFporcentaje.getElementText());
			} else {
				pack2.addContent(new Element("field").setText("0"));
			}

			if (XMLCBmoneda.getStringCombo() != null)
				pack2.addContent(XMLCBmoneda.getElementCombo());
			else
				throw new VoidPackageException(XMLCBmoneda.getLabel().getName());

		}

		if (valueArgs.equals("EDIT"))
			Eactiva.setText("1");

		pack1.addContent(Eactiva);
		pack1.addContent(Etipo);

		if (valueArgs.equals("EDIT"))
			pack1.addContent(XMLTFcuenta.getElementText("key"));

		pack[0] = pack1;
		pack[1] = pack2;

		return pack;
	}

	public void actionPerformed(ActionEvent e) {
		if (!exists) {
			if (((IDRadioButton) e.getSource()).getId().equals("MAYOR")) {
				enabledForm(false);
			} else {
				enabledForm(true);
			}
			XMLBGgroup.selected(e);
		}
	}

	public void cathSuccesEvent(SuccessEvent SEe) {
		try {
			String id = (String) GFforma.invokeMethod(
					"common.gui.components.PanelButtons", "getIdTransaction");
			if (id.equals(SEe.getIdPackage())) {
				loadingTablePUC();
			}
		} catch (NullPointerException NPEe) {
		} catch (InvocationTargetException ITEe) {
		} catch (NotFoundComponentException NFCEe) {
		}
	}

}
