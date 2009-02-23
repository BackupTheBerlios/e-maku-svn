package client.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.*;
import java.math.*;
import java.text.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.jdom.*;
import org.jdom.output.XMLOutputter;

import com.toedter.calendar.*;
import common.gui.components.*;
import common.gui.forms.*;
import common.misc.text.*;
import common.transactions.*;

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
 * @author <A href='mailto:felipe@gmail.com'>Luis Felipe Hernandez </A>
 */

public class TableFindData extends JPanel implements AnswerListener,
		ExternalValueChangeListener, RecordListener, InstanceFinishingListener, DeleteRecordListener {

	private static final long serialVersionUID = 3348132353954885841L;

	private GenericForm GFforma;

	private JTable JTtabla;

	private EmakuTableModel TMFDtabla;

	private ColumnsArgsGenerator[] ATFDargs;

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

	private String singleSendRecord;

	private Vector<RecordListener> recordListener = new Vector<RecordListener>();

	private int rowHeight = -1;

	private Font font;

	private boolean sendRecordOnSelectedRow;

	private boolean arriveAnswerEvent;

	private String[] initImps;

	private HashMap<String, Integer> lastValue = new HashMap<String, Integer>();

	private Hashtable<String, Integer> rowsLoaded = new Hashtable<String, Integer>();
	
	private String deleteRecord;

	/**
	 * Constructor ...
	 * 
	 * @throws InvocationTargetException
	 */

	public TableFindData(GenericForm GFform, Document doc)
			throws InvocationTargetException, NotFoundComponentException {
		this.GFforma = GFform;
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
		String conditionatedRecord = null;

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
			} else if ("font".equals(args.getAttributeValue("attribute"))) {
				try {
					StringTokenizer STfont = new StringTokenizer(args
							.getValue(), ",");
					font = new Font(STfont.nextToken(), Integer.parseInt(STfont
							.nextToken()), Integer.parseInt(STfont.nextToken()));
				} catch (NumberFormatException NFEe) {
					font = null;
				} catch (NoSuchElementException NSEEe) {
					font = null;
				}
			}
			/* Se captura la formula aplicada en la tabla */
			else if (args.getAttributeValue("attribute").equals("formula")) {
				formulas.add(new Formula(args.getValue(), Formula.SIMPLE));
			} else if (args.getAttributeValue("attribute").equals("beanshell")) {
				formulas.add(new Formula(args.getValue(), Formula.BEANSHELL));
			} else if (args.getAttributeValue("attribute").equals(
					"superformulas")) {
				formulas.add(new Formula(args.getValue(), Formula.SUPER));
			} else if (args.getAttributeValue("attribute").equals("formulaNQ")) {
				formulas.add(new Formula(args.getValue(), Formula.SIMPLENQ));
			} else if (args.getAttributeValue("attribute")
					.equals("beanshellNQ")) {
				formulas.add(new Formula(args.getValue(), Formula.BEANSHELLNQ));
			} else if (args.getAttributeValue("attribute").equals(
					"superformulaNQ")) {
				formulas.add(new Formula(args.getValue(), Formula.SUPERNQ));
			} else if (args.getAttributeValue("attribute")
					.equals("superBeanNQ")) {
				formulas.add(new Formula(args.getValue(), Formula.SUPERBEANNQ));
			} else if (args.getAttributeValue("attribute").equals(
					"exportTotalCol")) {
				StringTokenizer STexportTotalCols = new StringTokenizer(args
						.getValue(), ",");
				exportTotalCols.put(new String(STexportTotalCols.nextToken())
						.toUpperCase(), STexportTotalCols.nextToken());
			} else if (args.getAttributeValue("attribute").equals("tagData")) {
				tagDataColumn = Integer.parseInt(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("conditionatedRecord")) {
				conditionatedRecord = args.getValue();
			} else if (args.getAttributeValue("attribute").equals("sendRecord")) {
				this.sendRecord = args.getValue();
			} else if (args.getAttributeValue("attribute").equals(
					"singleSendRecord")) {
				this.singleSendRecord = args.getValue();
			} else if (args.getAttributeValue("attribute").equals("deleteRecord")) {
				deleteRecord=args.getValue();
			}
			/*
			 * Se captura las columnas que generaran totales
			 */
			else if (args.getAttributeValue("attribute").equals("totales")) {
				StringTokenizer STtotal = new StringTokenizer(args.getValue(),
						",");
				totales = new int[STtotal.countTokens()];
				for (int j = 0; j < totales.length; j++) {
					int tmpTotal = 0;
					String col = STtotal.nextToken();
					if (col.charAt(0) <= 74) {
						tmpTotal = col.charAt(0) - 65;
					} else { /* cuando es minuscula */
						tmpTotal = col.charAt(0) - 97;
					}
					totales[j] = tmpTotal;
				}
			} else if (args.getAttributeValue("attribute")
					.equals("driverEvent")) {
				String id = "";
				if (args.getAttributeValue("id") != null) {
					id = args.getAttributeValue("id");
				}
				driverEvent.add(args.getValue() + id);
			} else if (args.getAttributeValue("attribute").equals(
					"driverEventRecord")) {
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
				StringTokenizer STargs = new StringTokenizer(args.getValue(),
						":");
				initArgs = new String[STargs.countTokens()];
				for (int j = 0; j < initArgs.length; j++) {
					initArgs[j] = STargs.nextToken();
				}
			} else if (args.getAttributeValue("attribute").equals("initImps")) {
				StringTokenizer STargs = new StringTokenizer(args.getValue(),
						":");
				initImps = new String[STargs.countTokens()];
				for (int j = 0; j < initImps.length; j++) {
					initImps[j] = GFform.getExternalValueString(STargs
							.nextToken());
				}
			} else if (args.getAttributeValue("attribute").equals(
					"colorSelected")) {
				colorSelected = getColor(args.getValue());
			} else if (args.getAttributeValue("attribute").equals(
					"colorBackground")) {
				colorBackground = getColor(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("enabled")) {
				enabled = Boolean.parseBoolean(args.getValue());
			} else if (args.getAttributeValue("attribute").equals(
					"protectSelected")) {
				protectSelected = Boolean.parseBoolean(args.getValue());
			} else if (args.getAttributeValue("attribute").equals(
					"sendRecordOnSelectedRow")) {
				sendRecordOnSelectedRow = Boolean.parseBoolean(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("valideLink")) {
				valideLink = Integer.parseInt(args.getValue());
			} else if (args.getAttributeValue("attribute").equals("keyLink")) {
				keyLink = Integer.parseInt(args.getValue());
			} else if (args.getAttributeValue("attribute")
					.equals("importValue")) {
				impValues.add(args.getValue());
			} else if (args.getAttributeValue("attribute").equals(
					"importTotalCol")) {
				StringTokenizer STexportTotalCols = new StringTokenizer(args
						.getValue(), ",");
				importTotalCol.put(STexportTotalCols.nextToken().toUpperCase(),
						STexportTotalCols.nextToken());
			} else if (args.getAttributeValue("attribute").equals(
					"returnNullValue")) {
				returnNullValue = Boolean.parseBoolean(args.getValue());
			} else if (args.getAttributeValue("attribute").equals(
					"externalValue")) {
				StringTokenizer STexternalValue = new StringTokenizer(args
						.getValue(), ",");
				try {
					String idDriver = STexternalValue.nextToken();
					String driver = STexternalValue.nextToken();
					String key = STexternalValue.nextToken();
					externalValues.put(key, driver + idDriver);
				} catch (NoSuchElementException NSEEe) {
					NSEEe.printStackTrace();
				}
			} else if (args.getAttributeValue("attribute").equals("rowHeight")) {
				rowHeight = Integer.parseInt(args.getValue());
			}
		}
		List Lsubarg = parameters.getChildren("subarg");
		int col = Lsubarg.size();
		ATFDargs = new ColumnsArgsGenerator[col];
		Iterator j = Lsubarg.iterator();

		for (int k = 0; j.hasNext(); k++) {
			Element args = (Element) j.next();
			ATFDargs[k] = new ColumnsArgsGenerator(args);
		}

		if (initSQL != null) {
			JTtabla = new JTable() {

				private static final long serialVersionUID = -9216942827014115821L;

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

			};
			loadingQuery();
		} else {
			if (valideLink > 0) {
				TMFDtabla = new EmakuTableModel(GFforma, sqlCode, rows,
						formulas, exportTotalCols, importTotalCol, impValues,
						totales, externalValues, ATFDargs, valideLink, keyLink);
			} else {
				TMFDtabla = new EmakuTableModel(GFforma, sqlCode, rows,
						formulas, exportTotalCols, importTotalCol, impValues,
						totales, externalValues, ATFDargs);
			}
			TMFDtabla.setTagDataColumn(tagDataColumn);
			TMFDtabla.setConditionatedRecord(conditionatedRecord);
			// TableSorter sorter = new TableSorter(TMFDtabla);
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
			};
			// sorter.setTableHeader(JTtabla.getTableHeader());
			propertiesTable();
		}

		/*
		 * Se adiciona el oyente que detectara llegada de paquetes
		 */

		GFform.addInitiateFinishListener(this);
		if (TMFDtabla!=null) {
			TMFDtabla.addDeleteRecordEventListener(this);
		}
		
		if (font != null) {
			JTtabla.setFont(font);
			JTtabla.getTableHeader().setFont(font);
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
			return new Color(Integer.parseInt(STcolor.nextToken()), Integer
					.parseInt(STcolor.nextToken()), Integer.parseInt(STcolor
					.nextToken()));
		} catch (NumberFormatException NFEe) {
			return null;
		} catch (NoSuchElementException NSEEe) {
			return null;
		}
	}

	private void propertiesTable() {

		// JTtabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JTtabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		if (rowHeight > 0) {
			JTtabla.setRowHeight(rowHeight);
		}
		JTtabla.setEnabled(enabled);

		JTtabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JTtabla.setAutoscrolls(true);
		JTtabla.setColumnSelectionAllowed(true);
		JTtabla.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE
						&& JTtabla.isCellEditable(JTtabla.getSelectedRow(),
								JTtabla.getSelectedColumn())) {
					if (valideLink >= 0
							&& !"".equals(((String) JTtabla.getValueAt(JTtabla
									.getSelectedRow(), keyLink)).trim())) {
						TMFDtabla.addKeyLink((String) JTtabla.getValueAt(
								JTtabla.getSelectedRow(), keyLink));
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
				String cname = JTtabla.getColumnName(k);
				TableColumn tc = JTtabla.getColumn(cname);
				int lengthCol = ATFDargs[k].getLengthCol();
				tc.setMinWidth(0);
				if (lengthCol >= 0) {
					tc.setPreferredWidth(lengthCol);
				}
				if (ATFDargs[k].getType().equals("COMBOSQL")) {
					// Use the combo box as the editor in the "Favorite Color"
					// column.
					String exportValue = ATFDargs[k].getExportValueCombo();

					if (ATFDargs[k].isImportValueCombo()) {
						if (ATFDargs[k].isExporValueCombo()) {
							tc.setCellEditor(new DefaultCellEditor(
									new ComboBoxFiller(GFforma, ATFDargs[k]
											.getSqlCombo(), ATFDargs[k]
											.getImportCombos(), exportValue,
											false)));
						} else {
							tc.setCellEditor(new DefaultCellEditor(
									new ComboBoxFiller(GFforma, ATFDargs[k]
											.getSqlCombo(), ATFDargs[k]
											.getImportCombos(), false)));
						}
					} else {
						if (ATFDargs[k].isExporValueCombo()) {
							tc
									.setCellEditor(new DefaultCellEditor(
											new ComboBoxFiller(GFforma,
													ATFDargs[k].getSqlCombo(),
													exportValue, false)));
						} else {
							tc.setCellEditor(new DefaultCellEditor(
									new ComboBoxFiller(GFforma, ATFDargs[k]
											.getSqlCombo(), false)));
						}
					}
				}

				/*
				 * Si el tipo de Editor es para consulta entonces ....
				 */
				else if (ATFDargs[k].getType().equals("DATASEARCH")) {
					TableColumn dataColumn = JTtabla.getColumn(JTtabla
							.getColumnName(k));
					EmakuDataSearchCellEditor cellEditor = null;
					cellEditor = new EmakuDataSearchCellEditor(GFforma, k,
							ATFDargs, JTtabla);
					if (null != font) {
						cellEditor.setFont(font);
					}
					dataColumn.setCellEditor(cellEditor);
				} else if (ATFDargs[k].getType().equals("DETAILEDPRODUCT")) {
					TableColumn dataColumn = JTtabla.getColumn(JTtabla
							.getColumnName(k));
					EmakuDetailedProductCellEditor cellEditor = null;
					cellEditor = new EmakuDetailedProductCellEditor(GFforma, k,
							ATFDargs, JTtabla);
					dataColumn.setCellEditor(cellEditor);
				} else if (ATFDargs[k].getType().equals("TEXTILEPRODUCT")) {
					TableColumn dataColumn = JTtabla.getColumn(JTtabla
							.getColumnName(k));
					EmakuTextileProductCellEditor cellEditor = null;
					cellEditor = new EmakuTextileProductCellEditor(GFforma, k,
							ATFDargs, JTtabla);
					dataColumn.setCellEditor(cellEditor);
				} else if (ATFDargs[k].getType().equals("TOUCHBUTTONS")) {
					TableColumn dataColumn = JTtabla.getColumn(JTtabla
							.getColumnName(k));
					EmakuTouchCellEditor cellEditor = null;
					cellEditor = new EmakuTouchCellEditor(GFforma, ATFDargs[k]
							.getElement());
					dataColumn.setCellEditor(cellEditor);
				}
				/*
				 * Si el editor es tipo String y se especifica maxima longitud
				 * de caracteres entonces
				 */
				else if (ATFDargs[k].getType().equals("STRING")
						&& ATFDargs[k].size() > 0) {
					TableColumn dataColumn = JTtabla.getColumn(JTtabla
							.getColumnName(k));
					EmakuCellEditor cellEditor = new EmakuCellEditor(
							String.class, ATFDargs[k].size());
					dataColumn.setCellEditor(cellEditor);
				}

			} catch (IllegalArgumentException IAEe) {
				IAEe.printStackTrace();
			}
		}

		JTtabla.setDefaultRenderer(BigDecimal.class, new FortmaCell(
				Double.class));
		JTtabla
				.setDefaultRenderer(Integer.class,
						new FortmaCell(Integer.class));
		JTtabla.setDefaultRenderer(Date.class, new FortmaCell(Date.class));

		JTtabla.setDefaultEditor(BigDecimal.class, new EmakuCellEditor(
				BigDecimal.class));

		JTtabla.setDefaultEditor(BigDecimal.class, new EmakuCellEditor(
				BigDecimal.class));
		JTtabla.setDefaultEditor(Integer.class, new EmakuCellEditor(
				Integer.class));

		JTtabla.setDefaultEditor(Date.class, new EmakuCellEditorDate(JTtabla));

		GFforma.addChangeExternalValueListener(this);
		TMFDtabla.addTableModelListener(new TModelListener(JTtabla));

		JTtabla.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {

						Runnable sselection = new Runnable() {
							public void run() {
									int sel = JTtabla.getSelectedRow();
									if (sel == -1) {
										return;
									}
									for (int i = 0; i < ATFDargs.length; i++) {
										ColumnsArgsGenerator c = ATFDargs[i];
										Object obj = TMFDtabla.getValueAt(sel,
												i);
										if (c.isExporValue() && obj != null
												&& !"".equals(obj.toString())) {
											GFforma.setExternalValues(
													(Object) ATFDargs[i]
															.getExportValue(),
													obj.toString());
										}
									}

									if (sendRecordOnSelectedRow) {
										Element element = new Element("table");
										// boolean fullRow =
										// sendRecord(sel,element,singleSendRecord);
										sendRecord(sel, element,singleSendRecord);
										// if (fullRow) {
										RecordEvent event = new RecordEvent(this, element);
										notificando(event,singleSendRecord);
										// }
									}

									if (protectSelected) {
										if (sel > 0) {
											if (TMFDtabla
													.getValueAt(sel - 1, 0) == null
													|| ""
															.equals(TMFDtabla
																	.getValueAt(
																			sel - 1,
																			0))) {
												JTtabla.changeSelection(
														sel - 1, 0, false,
														false);
											}
										}
								}
							}
						};
						new Thread(sselection, "sendRecordSelection").start();

					}
				});
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
					doc = TransactionServerResultSet.getResultSetST(initSQL,
							initArgs);
					TMFDtabla = new EmakuTableModel(GFforma, sqlCode, doc,
							formulas, exportTotalCols, importTotalCol, totales,
							externalValues, ATFDargs);
					JTtabla.setModel(TMFDtabla);
					propertiesTable();

				} catch (TransactionServerException e) {
					e.printStackTrace();
				}

			}
		}
		/*
		 * Eliminado el hilo por problemas de retorno del objeto TMDtabla. new
		 * loadingSQL(initSQL, initArgs).start();
		 */
		int numInitArgs = initArgs != null ? initArgs.length : 0;
		int numInitImps = initImps != null ? initImps.length : 0;
		String[] args = new String[numInitArgs + numInitImps];
		int i = 0;
		for (; i < numInitArgs; i++) {
			args[i] = initArgs[i];
		}
		for (int j = 0; j < numInitImps; i++, j++) {
			args[i] = initImps[j];
		}
		new loadingSQL(initSQL, args).start();
	}

	public JPanel getPanel() {
		return this;
	}

	public void deleteRecordCode(String code) {
		int j = TMFDtabla.getCurrentIndex();
		for(;j>=0;j--) {
			String value = TMFDtabla.getValueAt(j,0).toString();
			if (value.equals(code)) {
				deleteRow(j);
				JTtabla.changeSelection(j, 0, false, false);
				JTtabla.updateUI();
				TMFDtabla.totalizar();
				break;
			}
		}
	}
	
	public void deleteRow(int indice) {
		Element e = new Element("delete");
		verificaSendRecord(indice,e, singleSendRecord);
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
			} else if ("conceptEnd".equals(arg.getAttributeValue("attribute"))) {
				conceptEnd = arg.getValue();
			} else if ("avoidConnectorAtFinal".equals(arg
					.getAttributeValue("attribute"))) {
				avoidFinalConnector = Boolean.parseBoolean(arg.getValue());
			} else if ("validColumn".equals(arg.getAttributeValue("attribute"))) {
				try {
					validColumn = Integer.parseInt(arg.getValue());
				} catch (NumberFormatException NFEe) {
				}
			}
		}

		Element subargs = (Element) args.getChildren("subargs").iterator()
				.next();
		List dataCol = subargs.getChildren();
		int lenghtFinalConnector = 0;
		for (int i = 0; i < TMFDtabla.getRowCount(); i++) {
			if (((Number) TMFDtabla.getValueAt(i, validColumn)).intValue() > 0) {
				for (int j = 0; j < dataCol.size(); j++) {
					String typeData = ((Element) dataCol.get(j))
							.getAttributeValue("attribute");
					String value = ((Element) dataCol.get(j)).getValue();
					if (typeData.equals("column")) {
						try {
							Object obj = TMFDtabla.getValueAt(i, Integer
									.parseInt(value));
							System.out.println("columna: "+j+" valor "+obj);
							if (ATFDargs[Integer.parseInt(value)].getType().equals("DECIMAL")) {
								NumberFormat nf = NumberFormat
										.getNumberInstance();
								DecimalFormat form = (DecimalFormat) nf;
								form.applyPattern("###,####,###.00");
								concept += form.format(obj);
							} else {
								concept += obj;
							}

						} catch (NumberFormatException NFEe) {

						}
					} else if (typeData.equals("connector")) {
						concept += value;
						lenghtFinalConnector = value.length();
					}
				}
			}
		}

		if (avoidFinalConnector) {
			concept = concept.substring(0, concept.length()
					- lenghtFinalConnector);
		}
		if (!concept.equals("")) {
			concept = conceptStart + concept + conceptEnd;
		}
		field.setText(concept);
		pack.addContent(field);

		return pack;

	}

	/**
	 * Metodo generico para retornar un <package/> validando una condicion
	 * especial de una columna
	 */

	public Element getPackage(Element args) throws VoidPackageException {
		Iterator it = args.getChildren("arg").iterator();
		boolean maxmin = true; // Si maxmin es true entonces la condicion es
								// mayor si no es menor
		double validValue = 0;
		int column = 0;
		String message = "Tabla Data";
		while (it.hasNext()) {
			Element arg = (Element) it.next();
			if ("validMaxValue".equals(arg.getAttributeValue("attribute"))) {
				maxmin = true;
				column = Integer.parseInt(arg.getValue());
			} else if ("validMinValue".equals(arg
					.getAttributeValue("attribute"))) {
				maxmin = false;
				column = Integer.parseInt(arg.getValue());
			} else if ("validValue".equals(arg.getAttributeValue("attribute"))) {
				validValue = Double.parseDouble(arg.getValue());
			} else if ("validMessage"
					.equals(arg.getAttributeValue("attribute"))) {
				message = arg.getValue();
			}
		}
		Element pack = TMFDtabla.getPackage(maxmin, column, validValue);
		if (pack.getChildren().size() == 0 && !returnNullValue) {
			throw new VoidPackageException(message);
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

	public Element getPrintPackage(Element args) {
		return TMFDtabla.getPrintPackage(args);
	}

	public Element getPrintPackageOrderBy(Element args) {
		return TMFDtabla.getPrintPackageOrderBy(args);
	}

	public Element getAgrupedPrintPackage(Element args)
			throws VoidPackageException {
		Element element = TMFDtabla.getAgrupedPrintPackage(args);
		if (element.getChildren().size() == 0 && !returnNullValue) {
			throw new VoidPackageException("Tabla Data");
		}
		return element;
	}
	
	public EmakuTableModel getEmakuTableModel() {
		return TMFDtabla;
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
	 * @throws DataErrorException
	 *             en caso de que exista algun dato incorrecto
	 */
	public Element getUVPackage() throws DataErrorException {
		return TMFDtabla.getUVPackage();
	}

	/**
	 * Metodo utilizado solo para la forma asientos predefinidos
	 * 
	 * @return retorna un <package/>
	 * @throws DataErrorException
	 *             en caso de que exista algun dato incorrecto
	 */
	public Element getAPPackage() throws DataErrorException {
		return TMFDtabla.getAPPackage();
	}

	/**
	 * Metodo utilizado solo para el componente asignacion de asientos
	 * 
	 * @return retorna un <package/>
	 * @throws DataErrorException
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
		arriveAnswerEvent = true;
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
		JTtabla.getSelectionModel().clearSelection();
		TMFDtabla.setQuery(doc,false);
		/*Thread t = new Thread() {
			public void run() {
				if (sendRecord != null || singleSendRecord != null) {
					lastValue = new HashMap<String, Integer>();
					String record = sendRecord != null ? sendRecord : singleSendRecord;
					int max = JTtabla.getModel().getRowCount();
					Element element = new Element("table");
					for (int i = 0; i < max && JTtabla.getValueAt(i, 0) != null
							&& !JTtabla.getValueAt(i, 0).equals(""); i++) {
						sendRecord(i, element, record);
					}
					RecordEvent event = new RecordEvent(this, element);
					notificando(event);

				}
			}
		
		};
		SwingUtilities.invokeLater(t);*/
		arriveAnswerEvent = false;
	}

	/**
	 * Metodo encargado de limpiar la tabla
	 */

	public void clean() {
		TMFDtabla.clean();
		lastValue = new HashMap<String, Integer>();
		if (enabled)
			JTtabla.changeSelection(0, 0, false, false);
		JTtabla.getSelectionModel().clearSelection();
	}

	public EmakuTableModel getTMFDtabla() {
		int i = 0;
		while (TMFDtabla == null) {
			try {
				Thread.sleep(30);
				i++;
				if (i > 200) {
					return null;
				}
			} catch (InterruptedException e) {

			}
		}
		return TMFDtabla;
	}

	public void totalizar() {
		TMFDtabla.totalizar();
	}

	private void reloadData() {
		for (int i = 0;  i < TMFDtabla.getRowCount() && 
		                !"".equals(TMFDtabla.getValueAt(i, 0)) ; i++) {
			TMFDtabla.setValueAt(TMFDtabla.getValueAt(i, 0), i, 0);
		}
	}

	public void changeExternalValue(ExternalValueChangeEvent e) {

		if (TMFDtabla.impValuesSize() > 0 && e.getExternalValue() != null) {
			boolean valueChange = false;

			/*
			 * Como el evento se ejecuta cuando una llave exportada cambia, el
			 * siguiente codigo verifica si los valores que cambiaron
			 * corresponden a alguna llave importada de este componente, si asi
			 * fuera, entonces se procede a recargar la información de la tabla.
			 * 
			 * Codigo adicionado el 12-06-2007 por pipelx
			 */

			for (int i = 1; i < TMFDtabla.getSizeArgsQuery(); i++) {
				String valueImport = GFforma.getExternalValueString(TMFDtabla
						.getImpValue(i - 1));
				String valueTable = TMFDtabla.getArgQuery(i);
				if (!valueImport.equals(valueTable)) {
					valueChange = true;
					break;
				}
			}
			if (valueChange) {
				reloadData();
			}
		}
		
		String value = GFforma.getExternalValueString(deleteRecord);
		if (e.getExternalValue().equals(deleteRecord) && !value.equals("")) {
				deleteRecordCode(value);
		}
	}

	public void setQuery(String sqlCode) {
		TMFDtabla.setQuery(sqlCode);
	}

	public void addRecordListener(RecordListener listener) {
		recordListener.addElement(listener);
	}

	public void removeRecordListener(RecordListener listener) {
		recordListener.removeElement(listener);
	}

	private void notificando(RecordEvent event,String record) {
		for (RecordListener l : recordListener) {
			l.arriveRecordEvent(event);
			rowsLoaded = event.getRowsLoaded();
			if (rowsLoaded!=null && rowsLoaded.size() > 0) {
				for (int i = 0 ; i < TMFDtabla.getCurrentIndex(); i++)  {
					sendRecord(i,new Element("unknow"),record);
				}
			}
		}
	}

	public boolean sendRecord(int rowIndex, Element element, String record) {
		int j = 0;
		int cont = 0;
		String elmName = element.getName();
		/*
		 * Este codigo verifica que el registro este lleno en su totalidad,
		 * cuando se llena una tabla a partir de una consulta esta validación se
		 * obviara, esto se sabe porque la variable arriveAnswerEvent esta en
		 * true.
		 */

		if (!arriveAnswerEvent) {
			while (j < ATFDargs.length && rowIndex >= 0) {
				Object cell = TMFDtabla.getValueAt(rowIndex, j);
				if (cell != null && !"".equals(cell)) {
					cont++;
				}
				j++;
			}
		}

		boolean fullRow = cont == ATFDargs.length ? true : false;
		if (fullRow || arriveAnswerEvent) {

			Element row = new Element("row");
			StringTokenizer stk = new StringTokenizer(record, ",");
			boolean next = true;
			String currentVal = "";
			while (next) {
				try {
					Element col = new Element("col");
					String tok = stk.nextToken();
					try {
						int column = Integer.parseInt(tok);
						String cellVal = TMFDtabla.getValueAt(rowIndex, column)
								.toString();
						if (ATFDargs[column].getType().equals("COMBOSQL")) {
							String value = "";
							StringTokenizer stkVal = new StringTokenizer(
									cellVal, " ");
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
						currentVal += cellVal;
						row.addContent(col);
					} catch (NumberFormatException NFEe) {
						col.setText(tok.substring(1, tok.length() - 1));
						row.addContent(col);
					}
				} catch (NoSuchElementException NSEe) {
					next = false;
				}
			}
			element.addContent(row);

			/*
			 * Este codigo verifica que el registro no exista anteriormente si
			 * proviene de un arriveAnswer, la validación no se realiza
			 */
			if ("delete".equals(elmName)) {
				//lastValue.remove(currentVal);
				return true;
			} 
			if (!arriveAnswerEvent) {
				boolean containValue = false;
				for (int i = 0; i < lastValue.size(); i++) {
					if (lastValue.containsKey(currentVal)
							&& lastValue.get(currentVal) == rowIndex) {
						containValue = true;
						break;
					}
				}
				if (!containValue) {
					lastValue.put(currentVal, rowIndex);
					return true;
				} else {
					return false;
				}
			} else {
				lastValue.put(currentVal, rowIndex);
				return true;
			}
		}
		return fullRow;
	}

	class TModelListener implements TableModelListener {

		JTable table;

		TModelListener(JTable table) {
			this.table = table;
		}

		public void tableChanged(final TableModelEvent e) {
			Runnable t = new Runnable() {
				public void run() {
					if (sendRecord != null) {
						int max = JTtabla.getModel().getRowCount();
						Element element = new Element("table");
						for (int i = 0; i < max
								&& JTtabla.getValueAt(i, 0) != null
								&& !JTtabla.getValueAt(i, 0).equals(""); i++) {
							sendRecord(i, element, sendRecord);
						}
						XMLOutputter out = new XMLOutputter();
		            	out.setFormat(org.jdom.output.Format.getPrettyFormat());
		            	
						RecordEvent event = new RecordEvent(this, element);
						notificando(event,sendRecord);
					} else if (singleSendRecord != null) {
						int r = e.getFirstRow();
						Element e = new Element("table");
						verificaSendRecord(r,e, singleSendRecord);
					}
				}
			};
			
			if (!arriveAnswerEvent) {
				SwingUtilities.invokeLater(t);
			}
			// new Thread(t,"SendRecord").start();

		}
	}

	private void verificaSendRecord(int row, Element e, String recordType) {
		if (sendRecord(row, e, recordType)) {
			RecordEvent event = new RecordEvent(this, e);
			notificando(event,recordType);
		}
	}

	public void arriveRecordEvent(RecordEvent e) {
	
		/*
		 * class CargarDatos implements Runnable { Element e; public CargarDatos
		 * (Element e) { this.e=e; } public void run() { synchronized(TMFDtabla) {
		 * Document doc = new Document(); doc.setRootElement(e);
		 * TMFDtabla.setQuery(doc, true);
		 * JTtabla.scrollRectToVisible(JTtabla.getCellRect(TMFDtabla.getCurrentIndex(),0,false));
		 * JTtabla.updateUI(); } } }
		 */
		if (e.getElement().getChildren().size() > 0) {
			// SwingUtilities.invokeLater(new
			// CargarDatos((Element)e.getElement().clone()));
			Document doc = new Document();
			Element elm = (Element) ((Element) e.getElement()).clone();
			doc.setRootElement(elm);
			TMFDtabla.setQuery(doc, true);
			e.setRowsLoaded(TMFDtabla.getRowsLoaded());
			if (JTtabla.isFocusOwner()) {
				JTtabla.scrollRectToVisible(JTtabla.getCellRect(TMFDtabla.getCurrentIndex(), 0, false));
			}
			//JTtabla.updateUI();
		}
	}

	public synchronized void initiateFinishEvent(EndEventGenerator e) {

		Class[] ac = new Class[] { AnswerListener.class };
		Class[] rc = new Class[] { RecordListener.class };
		Object[] o = new Object[] { this };
		if (driverEvent != null && keySQL != null) {

			for (int n = 0; n < driverEvent.size(); n++) {
				try {
					GFforma.invokeMethod(driverEvent.get(n),
							"addAnswerListener", ac, o);
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotFoundComponentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		/**
		for (int n = 0; n < recordEvent.size(); n++) {
			try {
				GFforma.invokeMethod(recordEvent.get(n), "addRecordListener",
						rc, o);
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NotFoundComponentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		*/
		int n=0;
		try {
			if(recordEvent.size()>0) {
				while (true) {
					try {
						GFforma.invokeMethod(recordEvent.get(n),"addRecordListener",rc,o);
						n++;
						if (n==recordEvent.size())
							break;
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
						break;
					} catch (NotFoundComponentException e1) {
						Thread.sleep(500);
					}
	
				}
			}
		}
		catch(InterruptedException IEe) {
			IEe.printStackTrace();
		}
	}

	class EmakuCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = -4269349473866585354L;

		private Class _class;

		private JTextField textField;

		public EmakuCellEditor(Class _class) {
			super(new JTextField());
			textField = (JTextField) this.getComponent();
			if (null != font) {
				textField.setFont(font);
			}
			this._class = _class;
		}

		public EmakuCellEditor(Class _class, int size) {
			super(new JTextField());
			textField = (JTextField) this.getComponent();
			textField.setDocument(new TextDataValidator(size));
			if (null != font) {
				textField.setFont(font);
			}
			this._class = _class;
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			textField.setText("");
			return super.getComponent();
		}

		public Object getCellEditorValue() {
			Number value = null;
			if (_class.equals(BigDecimal.class)) {
				try {
					value = new Double(((JTextField) super.getComponent())
							.getText());
					BigDecimal bd = new BigDecimal(value.doubleValue());
					value = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				} catch (NumberFormatException NFEe) {
					BigDecimal bd = new BigDecimal(0.00);
					value = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				}
			} else if (_class.equals(Integer.class)) {
				try {
					value = new Integer(((JTextField) super.getComponent())
							.getText());
				} catch (NumberFormatException NFEe) {
					value = new Integer(0);
				}
			} else if (_class.equals(String.class)) {
				return ((JTextField) super.getComponent()).getText();
			}
			return value;
		}
	}

	class EmakuCellEditorDate extends AbstractCellEditor implements
			TableCellEditor {

		private static final long serialVersionUID = -4269349473866585354L;

		private JDateChooser jtfd;

		private JTable refJTable;

		public EmakuCellEditorDate(JTable table) {
			this.refJTable = table;
		}

		public void createChooser() {
			jtfd = new JDateChooser();
			jtfd.setDateFormatString("yyyy-MM-dd");
			jtfd.setFocusCycleRoot(true);
			jtfd.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					jtfd.getDateEditor().getUiComponent()
							.requestFocusInWindow();
				}
			});
			jtfd.getDateEditor().getUiComponent().addKeyListener(
					new KeyAdapter() {

						public void keyPressed(final KeyEvent e) {
							Thread t = new Thread() {
								public void run() {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e1) {
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

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			createChooser();
			boolean enabled = true;
			if (row > 0) {
				Object obj = table.getValueAt(row - 1, column);
				enabled = obj != null && !"".equals(obj) ? true : false;
			}
			if (value != null)
				jtfd.setDate((Date) value);
			jtfd.setEnabled(enabled);
			return jtfd;
		}

		public Object getCellEditorValue() {
			return jtfd.getDate();
		}
	}

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
			} else if (Integer.class.equals(c)) {
				this.setHorizontalAlignment(SwingConstants.RIGHT);
				mascara = "###,###,##0";
			} else if (Date.class.equals(c)) {
				mascara = "yyyy-MM-dd";
			}
		}

		public void setValue(Object value) {
			if (Double.class.equals(_class) || Integer.class.equals(_class)) {
				NumberFormat nf = NumberFormat.getNumberInstance();
				DecimalFormat form = (DecimalFormat) nf;
				form.applyPattern(mascara);
				if (value instanceof String) {
					value = Integer.parseInt(value.toString());
				}
				super.setValue(value != null ? form.format(value) : null);
			} else if (Date.class.equals(_class)) {
				SimpleDateFormat sdf = new SimpleDateFormat(mascara);
				super.setValue(value != null ? sdf.format(value) : null);
			}
		}
	}

	public boolean containSqlCode(String sqlCode) {
		if (keySQL.contains(sqlCode))
			return true;
		else
			return false;
	}

	public void deleteRecordEvent(DeleteRecordEvent e) {
		// TODO Auto-generated method stub
		int indice = e.getRow();
		deleteRow(indice);
		JTtabla.changeSelection(e.getRow(), 0, false, false);
		JTtabla.updateUI();
		TMFDtabla.totalizar();
	}
}