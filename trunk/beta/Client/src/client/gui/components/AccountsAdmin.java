package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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

import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.TableCellRenderer;

import org.jdom.Document;
import org.jdom.Element;

import common.control.ClientHeaderValidator;
import common.control.SuccessEvent;
import common.control.SuccessListener;
import common.gui.components.VoidPackageException;
import common.gui.components.XMLTextField;
import common.gui.forms.GenericForm;
import common.gui.forms.NotFoundComponentException;
import common.misc.language.Language;
import common.transactions.TransactionServerException;
import common.transactions.TransactionServerResultSet;

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
public class AccountsAdmin extends JPanel implements ActionListener,FocusListener,SuccessListener {

	private static final long serialVersionUID = -8164365878390790722L;

	private XMLTextField XMLTFcuenta;
	private XMLTextField XMLTFnombre;
	private XMLButtonGroup XMLBGgroup;
	private XMLCheckBox XMLCBnaturaleza;
	private XMLCheckBox XMLCBcentro;
	private XMLCheckBox XMLCBajuste;
	private XMLCheckBox XMLCBdeprecia;
	private XMLComboBox XMLCBtarifasDepre;
	private XMLCheckBox XMLCBterceros;
	private XMLCheckBox XMLCBinventarios;
	private XMLCheckBox XMLCBretencion;
	private XMLTextField XMLTFbase;
	private XMLTextField XMLTFcontraAjuste;
	private XMLTextField XMLTFctaAjuste;
	private XMLTextField XMLTFcontraDepre;
	private XMLTextField XMLTFctaDepre;
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
	public AccountsAdmin(GenericForm GFforma) {
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
	public AccountsAdmin(GenericForm GFforma, Document doc) {

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

		ClientHeaderValidator.addSuccessListener(this);

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
				JSsur.setDividerLocation(JSsur.getMaximumDividerLocation()-30);
			}

			public void ancestorMoved(HierarchyEvent e) {
				JSsur.setDividerLocation(JSsur.getMaximumDividerLocation()-30);
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
					doc = TransactionServerResultSet.getResultSetST("SCS0013", new String[] { texto });

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
						Document doc2 = TransactionServerResultSet.getResultSetST("SCS0014",
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
				} catch (TransactionServerException e1) {
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
				JTpuc.setModel(new TableRenderer(sql));
				JTpuc.getColumnModel().setColumnMargin(5);
				JTpuc.getColumn(JTpuc.getColumnName(0)).setPreferredWidth(
						(JTpuc.getWidth() * 25) / 100);
				JTpuc.getColumn(JTpuc.getColumnName(1)).setPreferredWidth(
						(JTpuc.getWidth() * 75) / 100);
				JTpuc.addAncestorListener(new AncestorListener() {

					public void ancestorMoved(AncestorEvent event) {
						JTpuc.getColumn(JTpuc.getColumnName(0))
								.setPreferredWidth(
										(JTpuc.getWidth() * 25) / 100);
						JTpuc.getColumn(JTpuc.getColumnName(1))
								.setPreferredWidth(
										(JTpuc.getWidth() * 75) / 100);
					}

					public void ancestorRemoved(AncestorEvent event) {
					}

					public void ancestorAdded(AncestorEvent event) {
					}

				});
			}
		}
		new LoadTable(JTpuc, "SCS0022").start();
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
		XMLCBajuste.addActionListener(this);
		
		XMLTFctaAjuste = new XMLTextField("CTAAJUSTE", 9, 10, XMLTextField.NUMERIC);
		XMLTFcontraAjuste = new XMLTextField("CONTRAPARTIDA", 9, 10, XMLTextField.NUMERIC);
		
		JPanel JPdeprecia = new JPanel(new BorderLayout());
		XMLCBdeprecia = new XMLCheckBox("DEPRECIACIONES");
		XMLCBdeprecia.addActionListener(this);
		XMLCBtarifasDepre = new XMLComboBox(GFforma,"SCS0058","TARIFAS");
		JPdeprecia.add(XMLCBdeprecia.getJPcheck(),BorderLayout.WEST);
		JPdeprecia.add(XMLCBtarifasDepre.getLabel(),BorderLayout.CENTER);
		JPdeprecia.add(XMLCBtarifasDepre.getJPcombo(),BorderLayout.EAST);
		
		XMLTFctaDepre = new XMLTextField("CTADEPRE", 9, 10, XMLTextField.NUMERIC);
		XMLTFcontraDepre = new XMLTextField("CONTRAPARTIDA", 9, 10, XMLTextField.NUMERIC);
		
		XMLCBterceros = new XMLCheckBox("TERCEROS_CTA");
		XMLCBinventarios = new XMLCheckBox("INVENTARIOS_CTA");
		XMLCBretencion = new XMLCheckBox("RETENCION");
		XMLCBretencion.addActionListener(this);
		XMLTFbase = new XMLTextField("BASE", 9, 10, XMLTextField.NUMERIC);
		XMLTFporcentaje = new XMLTextField("PORCENTAJE", 9, 10,	XMLTextField.NUMERIC);
		XMLCBmoneda = new XMLComboBox(GFforma, "SCS0012", "MONEDA");

		XMLTFbase.setHorizontalAlignment(SwingConstants.RIGHT);
		XMLTFporcentaje.setHorizontalAlignment(SwingConstants.RIGHT);
		XMLTFcontraAjuste.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JPanel JPctaAjuste = new JPanel(new BorderLayout());
		JPctaAjuste.add(Box.createRigidArea(new Dimension(20,0)),BorderLayout.WEST);
		JPctaAjuste.add(XMLTFctaAjuste.getLabel(),BorderLayout.CENTER);
		JPctaAjuste.add(XMLTFctaAjuste.getJPtext(),BorderLayout.EAST);

		JPanel JPcontraAjuste = new JPanel(new BorderLayout());
		JPcontraAjuste.add(XMLTFcontraAjuste.getLabel(),BorderLayout.WEST);
		JPcontraAjuste.add(XMLTFcontraAjuste.getJPtext(),BorderLayout.CENTER);
		
		JPanel JPajuste = new JPanel(new BorderLayout());
		JPajuste.add(JPctaAjuste,BorderLayout.WEST);
		JPajuste.add(JPcontraAjuste,BorderLayout.CENTER);
		JPajuste.add(Box.createRigidArea(new Dimension(50,0)),BorderLayout.EAST);
		
		JPanel JPctaDepre = new JPanel(new BorderLayout());
		JPctaDepre.add(Box.createRigidArea(new Dimension(20,0)),BorderLayout.WEST);
		JPctaDepre.add(XMLTFctaDepre.getLabel(),BorderLayout.CENTER);
		JPctaDepre.add(XMLTFctaDepre.getJPtext(),BorderLayout.EAST);

		JPanel JPcontraDepre = new JPanel(new BorderLayout());
		JPcontraDepre.add(XMLTFcontraDepre.getLabel(),BorderLayout.WEST);
		JPcontraDepre.add(XMLTFcontraDepre.getJPtext(),BorderLayout.CENTER);
		JPcontraDepre.add(Box.createRigidArea(new Dimension(50,0)),BorderLayout.EAST);

		JPanel JPdepre = new JPanel(new BorderLayout());
		JPdepre.add(JPctaDepre,BorderLayout.CENTER);
		JPdepre.add(JPcontraDepre,BorderLayout.EAST);

		JPanel JPbaseRetencion = new JPanel(new BorderLayout());
		JPbaseRetencion.add(Box.createRigidArea(new Dimension(20,0)),BorderLayout.WEST);
		JPbaseRetencion.add(XMLTFbase.getLabel(),BorderLayout.CENTER);
		JPbaseRetencion.add(XMLTFbase.getJPtext(),BorderLayout.EAST);

		JPanel JPporcentajeRetencion = new JPanel(new BorderLayout());
		JPporcentajeRetencion.add(XMLTFporcentaje.getLabel(),BorderLayout.WEST);
		JPporcentajeRetencion.add(XMLTFporcentaje.getJPtext(),BorderLayout.CENTER);

		JPanel JPretenciones = new JPanel(new BorderLayout());
		JPretenciones.add(JPbaseRetencion,BorderLayout.WEST);
		JPretenciones.add(JPporcentajeRetencion,BorderLayout.CENTER);
		JPretenciones.add(Box.createRigidArea(new Dimension(50,0)),BorderLayout.EAST);

		JPanel JPmoneda = new JPanel(new BorderLayout());
		JPmoneda.add(XMLCBmoneda.getLabel(),BorderLayout.WEST);
		JPmoneda.add(XMLCBmoneda.getJPcombo(),BorderLayout.CENTER);

		JPanel JPchecks = new JPanel(new GridLayout(12, 1));

		JPchecks.add(XMLBGgroup);
		JPchecks.add(XMLCBnaturaleza.getJPcheck());
		JPchecks.add(XMLCBcentro.getJPcheck());
		JPchecks.add(XMLCBajuste.getJPcheck());
		JPchecks.add(JPajuste);
		JPchecks.add(JPdeprecia);
		JPchecks.add(JPdepre);
		JPchecks.add(XMLCBterceros.getJPcheck());
		JPchecks.add(XMLCBinventarios.getJPcheck());
		JPchecks.add(XMLCBretencion.getJPcheck());
		JPchecks.add(JPretenciones);
		JPchecks.add(JPmoneda);
		
		XMLTFbase.addFocusListener(this);
		XMLTFporcentaje.addFocusListener(this);
		return JPchecks;
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
		XMLCBdeprecia.setEnabled(flag);
		XMLCBterceros.setEnabled(flag);
		XMLCBinventarios.setEnabled(flag);
		XMLCBretencion.setEnabled(flag);
		XMLCBmoneda.setEnabled(flag);
		XMLCBmoneda.getLabel().setEnabled(flag);

		if (!flag) {
			enabledAjustes(flag);
			enabledDepreciaciones(flag);
			enabledRetenciones(flag);
		}
	}

	private void enabledAjustes(boolean flag) {
		XMLTFctaAjuste.setEnabled(flag);
		XMLTFctaAjuste.getLabel().setEnabled(flag);
		XMLTFcontraAjuste.setEnabled(flag);
		XMLTFcontraAjuste.getLabel().setEnabled(flag);
	}
	
	private void enabledDepreciaciones(boolean flag) {
		XMLCBtarifasDepre.setEnabled(flag);
		XMLCBtarifasDepre.getLabel().setEnabled(flag);
		XMLTFctaDepre.setEnabled(flag);
		XMLTFctaDepre.getLabel().setEnabled(flag);
		XMLTFcontraDepre.setEnabled(flag);
		XMLTFcontraDepre.getLabel().setEnabled(flag);
	}

	private void enabledRetenciones(boolean flag) {
		XMLTFbase.setEnabled(flag);
		XMLTFbase.getLabel().setEnabled(flag);
		XMLTFporcentaje.setEnabled(flag);
		XMLTFporcentaje.getLabel().setEnabled(flag);
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
		XMLBGgroup.setSelected("MAYOR");
		XMLCBnaturaleza.setSelected(false);
		XMLCBcentro.setSelected(false);
		XMLCBajuste.setSelected(false);
		XMLCBdeprecia.setSelected(false);
		XMLCBterceros.setSelected(false);
		XMLCBinventarios.setSelected(false);
		XMLCBretencion.setSelected(false);
		XMLCBmoneda.setSelectedIndex(0);
		cleanAjustes();
		cleanDepreciaciones();
		cleanRetenciones();
	}

	private void cleanAjustes() {
		XMLTFctaAjuste.setText("");
		XMLTFcontraAjuste.setText("");
	}
	
	private void cleanDepreciaciones() {
		XMLCBtarifasDepre.setSelectedIndex(0);
		XMLTFctaDepre.setText("");
		XMLTFcontraDepre.setText("");
	}
	
	private void cleanRetenciones() {
		XMLTFbase.setText("");
		XMLTFporcentaje.setText("");
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
			String id_cta = ((Element) query.getChildren().get(0)).getTextTrim();
			Document Dperfil = TransactionServerResultSet.getResultSetST("SCS0021", new String[] { id_cta });
			Element pack = Dperfil.getRootElement().getChild("row");
			XMLCBnaturaleza.setSelected(getBoolean(pack, 0));
			XMLCBcentro.setSelected(getBoolean(pack, 1));
			XMLCBajuste.setSelected(getBoolean(pack, 2));
			XMLCBdeprecia.setSelected(getBoolean(pack, 3));
			XMLCBterceros.setSelected(getBoolean(pack, 4));
			XMLCBinventarios.setSelected(getBoolean(pack, 5));
			XMLCBretencion.setSelected(getBoolean(pack,6));
			XMLTFbase.setText(getString(pack, 7));
			XMLTFporcentaje.setText(getString(pack, 8));
			XMLCBmoneda.setSelectedItem(getString(pack, 9));
			
			/*
			 * Consultando informacion de ajustes por inflacion
			 */
			
			Document Dajuste = TransactionServerResultSet.getResultSetST("SCS0059", new String[] { id_cta });
			Element pack1 = Dajuste.getRootElement().getChild("row");

			if (pack1!=null) {
				XMLTFctaAjuste.setText(getString(pack1, 0));
				XMLTFcontraAjuste.setText(getString(pack1, 1));
			}
			
			/*
			 * Consultando informacion de depreciaciones
			 */
			
			Document Ddepreciaciones = TransactionServerResultSet.getResultSetST("SCS0060", new String[] { id_cta });
			Element pack2 = Ddepreciaciones.getRootElement().getChild("row");
			
			if (pack2!=null) {
				XMLCBtarifasDepre.setSelectedItem(getString(pack2, 0));
				XMLTFctaDepre.setText(getString(pack2, 2));
				XMLTFcontraDepre.setText(getString(pack2, 3));
			}
		} catch (TransactionServerException STe) {
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
		Element pack[] = new Element[4];

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
		Element pack3 = new Element("package");
		Element pack4 = new Element("package");

		if (XMLBGgroup.getText().equals("DETALLE")) {
			Etipo.setText("0");
			Eactiva.setText("1");
			pack2.addContent(XMLCBterceros.getElementCheck());
			pack2.addContent(XMLCBinventarios.getElementCheck());
			pack2.addContent(XMLCBnaturaleza.getElementCheck());
			pack2.addContent(XMLCBcentro.getElementCheck());
			pack2.addContent(XMLCBajuste.getElementCheck());
			pack2.addContent(XMLCBdeprecia.getElementCheck());
			pack2.addContent(XMLCBretencion.getElementCheck());

			/*
			 * Validando cuentas de ajuste
			 */
			if (XMLCBajuste.getTextCheck().equals("1")) {
				String idAjuste = getIdCta(XMLTFctaAjuste.getText());
				String idContrapartida = getIdCta(XMLTFcontraAjuste.getText());
				if (idAjuste==null) {
					throw new VoidPackageException(Language.getWord("CTAAJUSTE"));
				} 
				if (idContrapartida==null) {
					throw new VoidPackageException(Language.getWord("CONTRAPARTIDA"));
				}
				else {
					pack3.addContent(new Element("field").addContent(String.valueOf(idAjuste)));
					pack3.addContent(new Element("field").addContent(String.valueOf(idContrapartida)));
				}
			}
			
			/*
			 * Validando cuentas de depreciacion
			 */
			if (XMLCBdeprecia.getTextCheck().equals("1")) {
				String idDepreciacion = getIdCta(XMLTFctaDepre.getText());
				String idContrapartida = getIdCta(XMLTFcontraDepre.getText());
				if (XMLCBtarifasDepre.getStringCombo() != null) {
					pack4.addContent(XMLCBtarifasDepre.getElementCombo());
				}
				else {
					throw new VoidPackageException(XMLCBmoneda.getLabel().getName());
				}
				if (idDepreciacion==null) {
					throw new VoidPackageException(Language.getWord("CTADEPRE"));
				} 
				if (idContrapartida==null) {
					throw new VoidPackageException(Language.getWord("CONTRAPARTIDA"));
				}
				else {
					pack4.addContent(new Element("field").addContent(String.valueOf(idDepreciacion)));
					pack4.addContent(new Element("field").addContent(String.valueOf(idContrapartida)));
				}
			}
			
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
		pack[2] = pack3;
		pack[3] = pack4;

		return pack;
	}

	/**
	 * Este metodo se encarga de devolver el identificador de una cuenta contable
	 * apartir de su codigo
	 * @param charCta codigo de la cuenta
	 * @return retorna el identificador de la cuenta, si este no es encontrado retorna -1
	 */
	private String getIdCta(String charCta) {

		/*
		 * Se consulta si la cuenta digitada ya existe
		 */
		Document doc;
		try {
			doc = TransactionServerResultSet.getResultSetST("SCS0013", new String[] { charCta });
			List row = doc.getRootElement().getChild("row").getChildren();
			String idCta = ((Element)row.get(0)).getText();
			String estado = ((Element)row.get(4)).getText();
			String tipo = ((Element)row.get(5)).getText();

			if (tipo.equals("t") || tipo.equals("1")) { 
				return null;
			}
			if (estado.equals("f") || estado.equals("0")) {
				return null;
			}
			return idCta;
			
		} catch (TransactionServerException e) {
			e.printStackTrace();
			return null;
		} catch (NullPointerException NPEe) {
			return null;
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (!exists && e.getSource() instanceof IDRadioButton) {
			if (((IDRadioButton) e.getSource()).getId().equals("MAYOR")) {
				enabledForm(false);
			} else {
				enabledForm(true);
			}
			XMLBGgroup.selected(e);
		}
		
		if (e.getSource() instanceof XMLCheckBox) {
			XMLCheckBox box = (XMLCheckBox)e.getSource();
			if (box.getName().equals("AJUSTE")) {
				enabledAjustes(box.isSelected());
				cleanAjustes();
			}
			else if (box.getName().equals("DEPRECIACIONES")) {
				enabledDepreciaciones(box.isSelected());
				cleanDepreciaciones();
			}
			else if (box.getName().equals("RETENCION")) {
				enabledRetenciones(box.isSelected());
				cleanRetenciones();
			}
		}
	}

	public void cathSuccesEvent(SuccessEvent SEe) {
		try {
			String id = (String) GFforma.invokeMethod(
										"common.gui.components.ButtonsPanel",
										"getIdTransaction");
			if (id.equals(SEe.getIdPackage())) {
				loadingTablePUC();
			}
		} catch (NullPointerException NPEe) {
		} catch (InvocationTargetException ITEe) {
		} catch (NotFoundComponentException NFCEe) {
		}
	}

	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent e) {
		XMLTextField XMLTFtmp = (XMLTextField)e.getComponent();
		try {
			XMLTFtmp.setNumberValue(Double.parseDouble(XMLTFtmp.getText()));
		}
		catch(NumberFormatException NFEe){}
	}

}
