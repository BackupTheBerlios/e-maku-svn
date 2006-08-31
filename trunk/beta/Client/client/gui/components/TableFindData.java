package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdom.Document;
import org.jdom.Element;

import com.toedter.calendar.JDateChooser;
import common.gui.components.AnswerEvent;
import common.gui.components.AnswerListener;
import common.gui.components.ErrorDataException;
import common.gui.components.RecordEvent;
import common.gui.components.RecordListener;
import common.gui.components.VoidPackageException;
import common.gui.forms.ChangeExternalValueEvent;
import common.gui.forms.ChangeExternalValueListener;
import common.gui.forms.GenericForm;
import common.gui.forms.NotFoundComponentException;
import common.transactions.STException;
import common.transactions.STResultSet;

/**
 * TableFindData.java Creado el 06-abr-2005
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
 * Esta clase se encarga de consultar informacion al ST y desplegar sus
 * resultados en las demas celdas <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 */

public class TableFindData extends JPanel implements AnswerListener,
		ChangeExternalValueListener, RecordListener {

	private static final long serialVersionUID = 3348132353954885841L;
	private GenericForm GFforma;
	private JTable JTtabla;
	private TMFindData TMFDtabla;
	private ArgsTableFindData[] ATFDargs;
	private int rows;
	private String sqlCode = "";
	private ArrayList<Formula> formulas;
	private HashMap<String, String> exportTotalCols;
	private Hashtable<String, String> externalValues;
	private int[] totales;
	private ArrayList<String> driverEvent;
	private ArrayList<String> recordEvent;
	private ArrayList<String> keySQL = null;
	private String initSQL;
	private String[] initArgs;
	private Color colorSelected;
	private Color colorBackground;
	private boolean enabled = true;
	private boolean protectSelected;
	private int valideLink = -1;
	private int keyLink = -1;
	private boolean returnNullValue;
	private String mode;
	private HashMap<String, String> importTotalCol;
	private String sendRecord;
	private Vector<RecordListener> recordListener = new Vector<RecordListener>();

	/**
	 * Constructor ...
	 * 
	 * @throws InvocationTargetException
	 */

	public TableFindData(GenericForm GFforma, Document doc)
			throws InvocationTargetException, NotFoundComponentException {
		this.GFforma = GFforma;
		formulas = new ArrayList<Formula>();
		exportTotalCols = new HashMap<String, String>();
		keySQL = new ArrayList<String>();
		driverEvent = new ArrayList<String>();
		recordEvent = new ArrayList<String>();
		externalValues = new Hashtable<String, String>();
		importTotalCol = new HashMap<String, String>();

		Element parameters = doc.getRootElement();
		Iterator i = parameters.getChildren("arg").iterator();
		Vector<String> impValues = new Vector<String>();
		int tagDataColumn = -1;

		while (i.hasNext()) {

			Element args = (Element) i.next();
			/*
			 * Se captura el codigo de la sentencia
			 */
			if (args.getAttributeValue("attribute").equals("Query")) {
				sqlCode = args.getValue();
			}

			if (args.getAttributeValue("attribute").equals("mode")) {
				mode = args.getValue();
			}
			/*
			 * Se captura el numero de filas de la tabla
			 */
			else if (args.getAttributeValue("attribute").equals("rows")) {
				try {
					rows = Integer.parseInt(args.getValue());
				} catch (NumberFormatException NFEe) {
					NFEe.printStackTrace();
				}
			}
			/* Se captura la formula aplicada en la tabla */
			else if (args.getAttributeValue("attribute").equals("formula")) {
				formulas.add(new Formula(args.getValue(), Formula.SIMPLE));
			}
			else if (args.getAttributeValue("attribute").equals("beanshell")) {
				formulas.add(new Formula(args.getValue(), Formula.BEANSHELL));
			} else if (args.getAttributeValue("attribute").equals("superformulas")) {
				formulas.add(new Formula(args.getValue(), Formula.SUPER));
			} else if (args.getAttributeValue("attribute").equals("formulaNQ")) {
				formulas.add(new Formula(args.getValue(), Formula.SIMPLENQ));
			} else if (args.getAttributeValue("attribute").equals("beanshellNQ")) {
				formulas.add(new Formula(args.getValue(), Formula.BEANSHELLNQ));
			} else if (args.getAttributeValue("attribute").equals("superformulaNQ")) {
				formulas.add(new Formula(args.getValue(), Formula.SUPERNQ));
			} else if (args.getAttributeValue("attribute").equals("superBeanNQ")) {
				formulas.add(new Formula(args.getValue(), Formula.SUPERBEANNQ));
			} else if (args.getAttributeValue("attribute").equals("exportTotalCol")) {
				StringTokenizer STexportTotalCols = new StringTokenizer(args.getValue(), ",");
				exportTotalCols.put(new String(STexportTotalCols.nextToken()).toUpperCase(), STexportTotalCols.nextToken());
			} else if (args.getAttributeValue("attribute").equals("tagData")) {
				tagDataColumn = Integer.parseInt(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("sendRecord")) {
				this.sendRecord = args.getValue();
			}
			/*
			 * Se captura las columnas que generaran totales
			 */
			else if (args.getAttributeValue("attribute").equals("totales")) {
				StringTokenizer STtotal = new StringTokenizer(args.getValue(),",");
				totales = new int[STtotal.countTokens()];
				for (int j = 0; j < totales.length; j++) {
					int tmpTotal = 0;
					String col = STtotal.nextToken();
					if (col.charAt(0) <= 74) {
						tmpTotal = col.charAt(0) - 65;
					}
					else { /* cuando es minuscula */
						tmpTotal = col.charAt(0) - 97;
					}
					totales[j] = tmpTotal;
				}
			}

			else if (args.getAttributeValue("attribute").equals("driverEvent")) {
				String id = "";
				if (args.getAttributeValue("id") != null) {
					id = args.getAttributeValue("id");
				}
				driverEvent.add(args.getValue() + id);
			} else if (args.getAttributeValue("attribute").equals("driverEventRecord")) {
				String id = "";
				if (args.getAttributeValue("id") != null) {
					id = args.getAttributeValue("id");
				}
				recordEvent.add(args.getValue() + id);
			} else if (args.getAttributeValue("attribute").equals("keySQL")) {
				keySQL.add(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("initSQL")) {
				initSQL = args.getValue();
			} else if (args.getAttributeValue("attribute").equals("initArgs")) {
				StringTokenizer STargs = new StringTokenizer(args.getValue(),":");
				initArgs = new String[STargs.countTokens()];
				for (int j = 0; j < initArgs.length; j++) {
					initArgs[j] = STargs.nextToken();
				}
			} else if (args.getAttributeValue("attribute").equals("colorSelected")) {
				colorSelected = getColor(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("colorBackground")) {
				colorBackground = getColor(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("enabled")) {
				enabled = Boolean.parseBoolean(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("protectSelected")) {
				protectSelected = Boolean.parseBoolean(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("valideLink")) {
				valideLink = Integer.parseInt(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("keyLink")) {
				keyLink = Integer.parseInt(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("importValue")) {
				impValues.add(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("importTotalCol")) {
				StringTokenizer STexportTotalCols = new StringTokenizer(args.getValue(), ",");
				importTotalCol.put(STexportTotalCols.nextToken().toUpperCase(),STexportTotalCols.nextToken());
			} else if (args.getAttributeValue("attribute").equals("returnNullValue")) {
				returnNullValue = Boolean.parseBoolean(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("externalValue")) {
				StringTokenizer STexternalValue = new StringTokenizer(args.getValue(), ",");
				try {
					String idDriver = STexternalValue.nextToken();
					String driver = STexternalValue.nextToken();
					String key = STexternalValue.nextToken();
					externalValues.put(key, driver + idDriver);
				} catch (NoSuchElementException NSEEe) {
					NSEEe.printStackTrace();
				}
			}
		}
		List Lsubarg = parameters.getChildren("subarg");
		int col = Lsubarg.size();
		ATFDargs = new ArgsTableFindData[col];
		Iterator j = Lsubarg.iterator();

		for (int k = 0; j.hasNext(); k++) {
			Element args = (Element) j.next();
			ATFDargs[k] = new ArgsTableFindData(args);
		}

		if (initSQL != null) {
			JTtabla = new JTable() {

				private static final long serialVersionUID = -9216942827014115821L;

				public Component prepareRenderer(TableCellRenderer renderer,int rowIndex, int vColIndex) {
					Component c = super.prepareRenderer(renderer, rowIndex,vColIndex);
					if (rowIndex % 2 == 0
							&& !isCellSelected(rowIndex, vColIndex)) {
						if (colorBackground != null) {
							c.setBackground(colorBackground);
						}
					} else {
						// If not shaded, match the table's background
						c.setBackground(getBackground());
					}

					if (isCellSelected(rowIndex, vColIndex)) {
						if (colorSelected != null) {
							c.setBackground(colorSelected);
						}
					}
					return c;
				}

				public void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					super.paintComponent(g);
				}
			};
			loadingQuery();
		} else {
			if (valideLink > 0) {
				TMFDtabla = new TMFindData(GFforma, sqlCode, rows, formulas,
						exportTotalCols, importTotalCol, impValues, totales,
						externalValues, ATFDargs, valideLink, keyLink);
			} else {
				TMFDtabla = new TMFindData(GFforma, sqlCode, rows, formulas,
						exportTotalCols, importTotalCol, impValues, totales,
						externalValues, ATFDargs);
			}
			TMFDtabla.setTagDataColumn(tagDataColumn);
			JTtabla = new JTable(TMFDtabla) {

				private static final long serialVersionUID = -8579166961142646633L;

				public Component prepareRenderer(TableCellRenderer renderer,
						int rowIndex, int vColIndex) {
					Component c = super.prepareRenderer(renderer, rowIndex,
							vColIndex);
					if (rowIndex % 2 == 0
							&& !isCellSelected(rowIndex, vColIndex)) {
						if (colorBackground != null) {
							c.setBackground(colorBackground);
						}
					} else {
						c.setBackground(getBackground());
					}

					if (isCellSelected(rowIndex, vColIndex)) {
						if (colorSelected != null) {
							c.setBackground(colorSelected);
						}
					}
					return c;
				}

				public void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					super.paintComponent(g);
				}
			};
			propertiesTable();
		}

		/*
		 * Se adiciona el oyente que detectara llegada de paquetes
		 */

		if (driverEvent != null && keySQL != null) {
			for (int n = 0; n < driverEvent.size(); n++) {
				GFforma.invokeMethod(
				                     driverEvent.get(n),
				                     "addAnswerListener",
				                     new Class[] { AnswerListener.class },
				                     new Object[] { this });
			}
		}

		for (int n = 0; n < recordEvent.size(); n++) {
			GFforma.invokeMethod(
			                     recordEvent.get(n),
			                     "addRecordListener",
			                     new Class[] { RecordListener.class },
			                     new Object[] { this });
		}

		JScrollPane JSPtabla = new JScrollPane(JTtabla);
		this.setLayout(new BorderLayout());
		this.add(JSPtabla, BorderLayout.CENTER);

	}

	/**
	 * Este metodo retorna un objeto color, apartir de los argumentos recibidos
	 * 
	 * @param color
	 *            argumetos de colores
	 * @return objeto Color
	 */
	private Color getColor(String color) {
		try {
			StringTokenizer STcolor = new StringTokenizer(color, ",");
			return new Color(
			                 Integer.parseInt(STcolor.nextToken()),
			                 Integer.parseInt(STcolor.nextToken()),
			                 Integer.parseInt(STcolor.nextToken()));
		} catch (NumberFormatException NFEe) {
			return null;
		} catch (NoSuchElementException NSEEe) {
			return null;
		}
	}

	private void propertiesTable() {

		JTtabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JTtabla.setEnabled(enabled);

		JTtabla.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (protectSelected) {
							int sel = JTtabla.getSelectedRow();
							if (sel > 0) {
								if (TMFDtabla.getValueAt(sel - 1, 0)==null || "".equals(TMFDtabla.getValueAt(sel - 1, 0))) {
									JTtabla.changeSelection(sel - 1, 0, false,false);
								}
							}
						}
					}
				});
		JTtabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JTtabla.setColumnSelectionAllowed(true);
		JTtabla.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE
						&& JTtabla.isCellEditable(JTtabla.getSelectedRow(),
								JTtabla.getSelectedColumn())) {
					if (valideLink >= 0 && 
						!"".equals(
						           ((String)JTtabla.getValueAt(JTtabla.getSelectedRow(), keyLink)).trim())) {
						TMFDtabla.addKeyLink((String) JTtabla.getValueAt(JTtabla.getSelectedRow(), keyLink));
					}

					int indice = JTtabla.getSelectedRow();
					TableCellEditor tableCellEditor = null;
					tableCellEditor = JTtabla.getCellEditor();
					if (tableCellEditor != null) {
						tableCellEditor.cancelCellEditing();
					}
					JTtabla.clearSelection();
					deleteRow(indice);
					if (indice == 0) {
						JTtabla.changeSelection(0, 0, false, false);
					} else if (indice >= 0) {
						JTtabla.changeSelection(indice - 1, 0, false, false);
					}
					JTtabla.updateUI();
					TMFDtabla.totalizar();
				}
			}
		});

		/*
		 * Se asigna el ancho de cada columna
		 */
		for (int k = 0; k < ATFDargs.length; k++) {
			try {
				JTtabla.getColumn(JTtabla.getColumnName(k)).setMinWidth(0);
				JTtabla.getColumn(JTtabla.getColumnName(k)).setPreferredWidth(ATFDargs[k].getLengthCol());
				if (ATFDargs[k].getType().equals("COMBOSQL")) {
					TableColumn comboColumn = JTtabla.getColumn(JTtabla.getColumnName(k));
					// Use the combo box as the editor in the "Favorite Color"
					// column.
					String exportValue = ATFDargs[k].getExportValueCombo();

					if (ATFDargs[k].isImportValueCombo()) {
						if (ATFDargs[k].isExporValueCombo()) {
							comboColumn.setCellEditor(new DefaultCellEditor(
									new SQLComboBox(GFforma, ATFDargs[k]
											.getSqlCombo(), ATFDargs[k]
											.getImportCombos(), exportValue)));
						} else {
							comboColumn.setCellEditor(new DefaultCellEditor(
									new SQLComboBox(
									                GFforma,
									                ATFDargs[k].getSqlCombo(),
									                ATFDargs[k].getImportCombos())));
						}
					} else {
						if (ATFDargs[k].isExporValueCombo()) {
							comboColumn.setCellEditor(new DefaultCellEditor(
									new SQLComboBox(GFforma, ATFDargs[k].getSqlCombo(), exportValue)));
						} else {
							comboColumn.setCellEditor(new DefaultCellEditor(
									new SQLComboBox(GFforma, ATFDargs[k].getSqlCombo())));
						}
					}
				}
			} catch (IllegalArgumentException IAEe) {
				IAEe.printStackTrace();
			}
		}

		JTtabla.setDefaultRenderer(BigDecimal.class, new FortmaCell(Double.class));
		JTtabla.setDefaultRenderer(Integer.class, new FortmaCell(Integer.class));
		JTtabla.setDefaultRenderer(Date.class, new FortmaCell(Date.class));
		
		JTtabla.setDefaultEditor(BigDecimal.class, new CellEditor(Double.class,JTtabla));
		JTtabla.setDefaultEditor(Integer.class, new CellEditor(Integer.class,JTtabla));
		JTtabla.setDefaultEditor(Date.class,new CellEditor(Date.class,JTtabla));
		JTtabla.setDefaultEditor(String.class,new CellEditor(String.class,JTtabla));

		GFforma.addChangeExternalValueListener(this);
		JTtabla.changeSelection(0, 0, false, false);
		TMFDtabla.addTableModelListener(new TModelListener(JTtabla));
	}

	private void loadingQuery() {
		class loadingSQL extends Thread {
			private String initSQL;

			private String[] initArgs;

			loadingSQL(String initSQL, String[] initArgs) {
				this.initSQL = initSQL;
				this.initArgs = initArgs;
			}

			public void run() {
				Document doc = null;
				try {
					doc = STResultSet.getResultSetST(initSQL, initArgs);
					TMFDtabla = new TMFindData(GFforma, sqlCode, doc, formulas,
							exportTotalCols, totales, externalValues, ATFDargs);
					JTtabla.setModel(TMFDtabla);
					propertiesTable();

				} catch (STException e) {
					e.printStackTrace();
				}

			}
		}
		/*
		 * Eliminado el hilo por problemas de retorno del objeto TMDtabla. new
		 * loadingSQL(initSQL, initArgs).start();
		 */
		new loadingSQL(initSQL, initArgs).run();
	}

	public JPanel getPanel() {
		return this;
	}

	public void deleteRow(int indice) {
		TMFDtabla.deleteRow(indice);
	}

	public Element[] getMultiPackage() throws VoidPackageException {
		return TMFDtabla.getMultiPackage();
	}

    public Element generateConcept(Element args) {
    	
    	Element pack = new Element("package");
    	Element field = new Element("field");
    	
    	String concept = "";
    	String conceptStart = "";
    	String conceptEnd = "";
    	boolean avoidFinalConnector = false;
    	int validColumn = 0;
    	
		Iterator it = args.getChildren("arg").iterator();
		while (it.hasNext()) { 
			Element arg = (Element) it.next();
			if ("conceptStart".equals(arg.getAttributeValue("attribute"))) {
				conceptStart = arg.getValue();
			}
			else if ("conceptEnd".equals(arg.getAttributeValue("attribute"))) {
				conceptEnd = arg.getValue();
			}
			else if ("avoidConnectorAtFinal".equals(arg.getAttributeValue("attribute"))) {
					avoidFinalConnector = Boolean.parseBoolean(arg.getValue());
			}
			else if ("validColumn".equals(arg.getAttributeValue("attribute"))) {
				try {	
					validColumn = Integer.parseInt(arg.getValue());
				}
				catch (NumberFormatException NFEe) {}
			}
		}
		
		Element subargs = (Element)args.getChildren("subargs").iterator().next();
		List dataCol = subargs.getChildren();
		int lenghtFinalConnector=0;
		for (int i=0;i<TMFDtabla.getRowCount();i++) {
			if (((Number)TMFDtabla.getValueAt(i,validColumn)).intValue()>0) {
				for (int j=0;j<dataCol.size();j++) {
					String typeData = ((Element)dataCol.get(j)).getAttributeValue("attribute");
					String value = ((Element)dataCol.get(j)).getValue();
					if (typeData.equals("column")) {
						try {
							concept+= TMFDtabla.getValueAt(i,Integer.parseInt(value));
						}
						catch(NumberFormatException NFEe) {
							
						}
					} 
					else if (typeData.equals("connector")) {
						concept+=value;
						lenghtFinalConnector=value.length();
					}
				}
			}
		}
		
		if (avoidFinalConnector) {
			concept = concept.substring(0,concept.length()-lenghtFinalConnector);
		}
		if (!concept.equals("")) {
			concept=conceptStart+concept+conceptEnd;
		}
		field.setText(concept);
		pack.addContent(field);
		
    	return pack;
    	
    }
	
    /**
     * Metodo generico para retornar un <package/> 
     * validando una condicion especial de una columna
     */
     
    public Element getPackage(Element args) throws VoidPackageException {
		Iterator it = args.getChildren("arg").iterator();
		boolean maxmin = true; // Si maxmin es true entonces la condicion es mayor si no es menor
		double validValue = 0;
		int column = 0;
		while (it.hasNext()) { 
			Element arg = (Element) it.next();
			if ("validMaxValue".equals(arg.getAttributeValue("attribute"))) {
				maxmin = true;
				column = Integer.parseInt(arg.getValue());
			}
			else if ("validMinValue".equals(arg.getAttributeValue("attribute"))) {
				maxmin = false;
				column = Integer.parseInt(arg.getValue());
			}
			else if ("validValue".equals(arg.getAttributeValue("attribute"))) {
				validValue = Double.parseDouble(arg.getValue());
			}
		}
		Element pack = TMFDtabla.getPackage(maxmin,column,validValue);
		if (pack.getChildren().size() == 0 && !returnNullValue) {
			throw new VoidPackageException("Tabla Data");
		}
		return pack;
    } 
    
	/**
	 * Metodo generico para retornar un <package/>
	 * 
	 * @return un <package/>
	 */
	public Element getPackage() throws VoidPackageException {
		Element pack = TMFDtabla.getPackage();
		if (pack.getChildren().size() == 0 && !returnNullValue) {
			throw new VoidPackageException("Tabla Data");
		}
		return pack;
	}

	public Element getPrintPackage() throws VoidPackageException {
		Element pack = TMFDtabla.getPrintPackage();
		if (pack.getChildren().size() == 0 && !returnNullValue) {
			throw new VoidPackageException("Tabla Data");
		}
		return pack;
	}

	/**
	 * Metodo utilizado solo para la forma de creacion de Grupos de Asientos
	 * 
	 * @return un <package/>
	 */

	public Element getGAPackage() {
		return TMFDtabla.getGAPackage();
	}

	/**
	 * Metodo utilizado solo para la forma productos componente unidades de
	 * venta
	 * 
	 * @return retorna un <package/>
	 * @throws ErrorDataException
	 *             en caso de que exista algun dato incorrecto
	 */
	public Element getUVPackage() throws ErrorDataException {
		return TMFDtabla.getUVPackage();
	}

	/**
	 * Metodo utilizado solo para la forma asientos predefinidos
	 * 
	 * @return retorna un <package/>
	 * @throws ErrorDataException
	 *             en caso de que exista algun dato incorrecto
	 */
	public Element getAPPackage() throws ErrorDataException {
		return TMFDtabla.getAPPackage();
	}

	/**
	 * Metodo utilizado solo para el componente asignacion de asientos
	 * 
	 * @return retorna un <package/>
	 * @throws ErrorDataException
	 *             en caso de que exista algun dato incorrecto
	 */
	public Element getAAPackage() {
		return TMFDtabla.getAAPackage();
	}

	/**
	 * Este metodo genera un paquete en blanco
	 * 
	 * @return retorna un paquete en blanco
	 */
	public Element getBlankPackage() {
		Element field = new Element("field");
		Element pack = new Element("package");
		pack.addContent(field);
		return pack;
	}

	public void ValidPackage(Element args) throws VoidPackageException {
		TMFDtabla.ValidPackage(args);
	}

	public void arriveAnswerEvent(AnswerEvent e) {

		/*
		 * boolean isKey = false; //System.out.println("AnswerEvent" +
		 * e.getSqlCode()); int max = keySQL.size(); String code =
		 * e.getSqlCode(); for (int i=0;i<max;i++) { if
		 * (code.equals(keySQL.get(i))) { isKey = true; break; } }
		 */

		if (keySQL.contains(e.getSqlCode())) {
			// if (isKey) {
			Document doc = e.getDocument();
			int size = doc.getRootElement().getChildren("row").size();
			if ("NEW".equals(mode)) {
				if (size > 0) {
					GFforma.setEnabledButton("SAVE", false);
				} else {
					GFforma.setEnabledButton("SAVE", true);
				}
			} else if ("EDIT".equals(mode)) {
				if (size == 0) {
					GFforma.setEnabledButton("SAVEAS", false);
				} else {
					GFforma.setEnabledButton("SAVEAS", true);
				}
			} else if ("DELETE".equals(mode)) {
				if (size > 0) {
					GFforma.setEnabledButton("DELETE", true);
				} else {
					GFforma.setEnabledButton("DELETE", false);
				}
			}
			TMFDtabla.setQuery(doc);
		}
	}

	/**
	 * Metodo encargado de limpiar la tabla
	 */

	public void clean() {
		TMFDtabla.clean();
		if (enabled)
			JTtabla.changeSelection(0, 0, false, false);
	}

	public TMFindData getTMFDtabla() {
		return TMFDtabla;
	}

	public void totalizar() {
		TMFDtabla.totalizar();
	}

	private void reloadData() {
		for (int i = 0; !"".equals(TMFDtabla.getValueAt(i, 0))
				&& i < TMFDtabla.getRowCount(); i++) {
			TMFDtabla.setValueAt(TMFDtabla.getValueAt(i, 0), i, 0);
		}
	}

	public void changeExternalValue(ChangeExternalValueEvent e) {
		if (TMFDtabla.impValuesSize() > 0) {
			reloadData();
		}

	}

	public void setQuery(String sqlCode) {
		TMFDtabla.setQuery(sqlCode);
	}

	public synchronized void addRecordListener(RecordListener listener) {
		recordListener.addElement(listener);
	}

	public synchronized void removeRecordListener(RecordListener listener) {
		recordListener.removeElement(listener);
	}

	private synchronized void notificando(RecordEvent event) {
		Vector lista;
		lista = (Vector) recordListener.clone();
		for (int i = 0; i < lista.size(); i++) {
			RecordListener listener = (RecordListener) lista.elementAt(i);
			listener.arriveRecordEvent(event);
		}
	}

	class TModelListener implements TableModelListener {

		JTable table;

		TModelListener(JTable table) {
			this.table = table;
		}

		public void tableChanged(TableModelEvent e) {

			if (sendRecord != null) {
				Element element = new Element("table");
				int max = JTtabla.getModel().getRowCount();
				boolean fullRow = true;

				for (int i = 0; i < max && fullRow; i++) {
					int j=0;
					int cont =0;
					while ( j < ATFDargs.length) {
						Object cell = TMFDtabla.getValueAt(i, j);
						if (cell!=null && !"".equals(cell)) {
							cont++;
						}
						j++;
					}
					fullRow = cont==ATFDargs.length ? true : false;
					if (fullRow) {
						
						Element row = new Element("row");
						StringTokenizer stk = new StringTokenizer(sendRecord,",");
						boolean next = true;

						while (next) {
							try {
								Element col = new Element("col");
								String tok = stk.nextToken();
								try {
									int column = Integer.parseInt(tok);
									String cellVal = TMFDtabla.getValueAt(i,column).toString();
									if (ATFDargs[column].getType().equals("COMBOSQL")) {
										String value = "";
										StringTokenizer stkVal = new StringTokenizer(cellVal, " ");
										while (true) {
											try {
												value = stkVal.nextToken();
											} catch (NoSuchElementException NSEe) {
												break;
											}
										}
										cellVal = value;
									}
									col.setText(cellVal);
									row.addContent(col);
								} catch (NumberFormatException NFEe) {
									col.setText(tok.substring(1,tok.length() - 1));
									row.addContent(col);
								}
							} catch (NoSuchElementException NSEe) {
								next = false;
							}
						}
						element.addContent(row);
					}
				}

				RecordEvent event = new RecordEvent(this, element);
				notificando(event);
			}
		}
	}

	public void arriveRecordEvent(RecordEvent e) {
		Document doc = new Document();
		Element element = e.getElement();
		if (element.getChildren().size() > 0) {
			doc.setRootElement(element);
			TMFDtabla.setQuery(doc, true);
		}
	}
	
	
}



/**
 * 
 * TableFindData.java Creado el 22-jun-2005
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
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
 * Esta clase se encarga de limpiar el valor anterior antes del ingreso de un
 * nuevo valor en una celda de la tabla, ademas valida que si el texto ingresado
 * no puede ser moldeado, entonces retorna un 0 <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

class CellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = -4269349473866585354L;
	private Class c;
	private JTextField jtf;
	private JDateChooser jtfd;
	private JTable refJTable;
	
	public CellEditor(Class c,JTable table) {
		this.c = c;
		this.refJTable = table;
		jtf = new JTextField();
		jtf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				fireEditingStopped();
				jtf.validate();
			}
		});
		jtfd = new JDateChooser();
		jtfd.setDateFormatString("yyyy-MM-dd");
		jtfd.setFocusCycleRoot(true);
		jtfd.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				jtfd.getDateEditor().getUiComponent().requestFocusInWindow();
			}
		});
		jtfd.getDateEditor().getUiComponent().addKeyListener(new KeyAdapter() {
			
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
	}

	public Component getTableCellEditorComponent(JTable table, Object value,boolean isSelected, int row, int column) {
		boolean enabled = true;
		if (row > 0) {
			Object obj= table.getValueAt(row-1,column);
			enabled = obj!=null && !"".equals(obj) ? true : false; 
		}
		if (Date.class.equals(c)) {
			if (value != null)
				jtfd.setDate((Date) value);
			jtfd.setEnabled(enabled);
			return jtfd;
		}
		else if (String.class.equals(c)){
			jtf.setText(value!=null ? value.toString() : "");
			jtf.setEnabled(enabled);
			return jtf; 
		}
		else {
			jtf.setText("");
			jtf.setEnabled(enabled);
			return jtf; 
		}
	}

	public Object getCellEditorValue() {
		Object valueRet = null;
		if (Double.class.equals(c)) {
			try {
				valueRet = new Double(jtf.getText());
				BigDecimal bd = new BigDecimal(((Number)valueRet).doubleValue());
				valueRet = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			} catch (NumberFormatException NFEe) {
				BigDecimal bd = new BigDecimal(0.00);
				valueRet = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}
		else if (Integer.class.equals(c)){
			try {
				valueRet = new Integer(jtf.getText());
			} catch (NumberFormatException NFEe) {
				valueRet = new Integer(0);
			}
		}
		else if (Date.class.equals(c)) {
			valueRet = jtfd.getDate();
		}
		else if (String.class.equals(c)) {
			valueRet = jtf.getText();
		}
		return valueRet;
	}
}


/**
 * 
 * TableFindData.java Creado el 22-jun-2005
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
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
 * Esta clase se encarga de formatear los valores tipo numerico, dependiendo de
 * si son enteros o dobles <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * 
 */
class FortmaCell extends DefaultTableCellRenderer {


	private static final long serialVersionUID = -1516957430275114235L;

	String mascara;
	Class _class;
	
	public FortmaCell(Class c) {
		super();
		this._class = c;
		if (Double.class.equals(c)) {
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			mascara = "###,###,##0.00";
		}
		else if (Integer.class.equals(c)){
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			mascara = "###,###,##0";
		}
		else if (Date.class.equals(c)){
			mascara = "yyyy-MM-dd";
		}
	}

	public void setValue(Object value) {
		if (Double.class.equals(_class) || Integer.class.equals(_class)) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			DecimalFormat form = (DecimalFormat) nf;
			form.applyPattern(mascara);
			super.setValue(value!=null?form.format(value):null);
		}
		else if (Date.class.equals(_class)) {
			SimpleDateFormat sdf = new SimpleDateFormat(mascara);
			super.setValue(value!=null?sdf.format(value):null);
		}
	}
}