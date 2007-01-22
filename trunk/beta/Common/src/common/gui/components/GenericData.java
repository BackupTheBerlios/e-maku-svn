package common.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdom.Document;
import org.jdom.Element;

import bsh.EvalError;

import common.comunications.DateSender;
import common.control.ClientHeaderValidator;
import common.control.DateEvent;
import common.control.DateListener;
import common.gui.forms.ChangeExternalValueEvent;
import common.gui.forms.ChangeExternalValueListener;
import common.gui.forms.FinishEvent;
import common.gui.forms.GenericForm;
import common.gui.forms.InitiateFinishListener;
import common.gui.forms.NotFoundComponentException;
import common.misc.formulas.FormulaCalculator;
import common.misc.language.Language;
import common.transactions.STException;
import common.transactions.STResultSet;

/**
 * GenericData.java Creado el 15-oct-2004
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
 * Informacion de la clase <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class GenericData extends JPanel implements DateListener,
		AnswerListener, InitiateFinishListener, ChangeExternalValueListener {

	private static final long serialVersionUID = 3911530078319143164L;
	private GenericForm GFforma;
	private String Sargs;
	private String sqlLocal = null;
	private String enlaceTabla = "";
	private String namebutton = "SAVE";
	private String driverEvent;
	private String returnValue;
	private boolean disableAll;
	private boolean havePanel = true;
	private boolean search;
	private boolean returnBlankPackage = false;
	private boolean enablebutton = true;
	private ChangeValueEvent CVEevent;
	private Vector<XMLTextField> VFields;
	private Vector<String> sql;
	private Vector<String> sqlCode = null;
	private Vector<String> keySQL;
	private Vector<AnswerListener> AnswerListener = new Vector<AnswerListener>();
	private Vector<ChangeValueListener> changeValueListener = new Vector<ChangeValueListener>();
	private Vector<RecordListener> recordListener = new Vector<RecordListener>();
	
	public GenericData() {}

	public GenericData(GenericForm newGFforma, Document doc)
			throws InvocationTargetException, NotFoundComponentException {
		this.GFforma = newGFforma;
		this.GFforma.addInitiateFinishListener(this);
		this.setLayout(new BorderLayout());
		this.VFields = new Vector<XMLTextField>();
		sqlCode = new Vector<String>();
		boolean key = false;
		CVEevent = new ChangeValueEvent(this);
		Element parameters = doc.getRootElement();
		// Sargs = parameters.getChildTextTrim("arg");
		Iterator i = parameters.getChildren().iterator();

		int componentes = parameters.getChildren("subarg").size();

		JPanel JPetiquetas = new JPanel(new GridLayout(componentes, 1));
		JPanel JPfields = new JPanel(new GridLayout(componentes, 1));

		/*
		 * Almacenara las cadenas de sentencias sql a consultar
		 */
		sql = new Vector<String>();
		while (i.hasNext()) {

			Element subargs = (Element) i.next();
			String name = subargs.getName();
			String value = subargs.getValue();
			if ("arg".equals(name) && "NEW".equals(value)) {
				Sargs = subargs.getValue();
				namebutton = "SAVE";
				enablebutton = true;
			} else if ("arg".equals(name) && "EDIT".equals(value)) {
				Sargs = subargs.getValue();
				namebutton = "SAVEAS";
				enablebutton = false;
			} else if ("arg".equals(name) && "DELETE".equals(value)) {
				Sargs = subargs.getValue();
				namebutton = "DELETE";
				enablebutton = false;
			} else if ("arg".equals(name) && "DISABLEALL".equals(value)) {
				disableAll = true;
			} else if ("arg".equals(name)
					&& subargs.getAttributeValue("attribute") != null
					&& subargs.getAttributeValue("attribute").equals(
							"returnBlankPackage")) {
				returnBlankPackage = Boolean.parseBoolean(subargs.getValue());
			} else if ("arg".equals(name)
					&& subargs.getAttributeValue("attribute") != null
					&& subargs.getAttributeValue("attribute").equals("Panel")) {
				havePanel = Boolean.getBoolean(subargs.getValue());
			} else if ("linkTable".equals(subargs
					.getAttributeValue("attribute"))) {
				enlaceTabla = subargs.getValue();
			} else if ("driverEvent".equals(subargs
					.getAttributeValue("attribute"))) {
				String id = "";
				if (subargs.getAttributeValue("id") != null) {
					id = subargs.getAttributeValue("id");
				}
				driverEvent = subargs.getValue() + id;
			} else if ("returnValue".equals(subargs
					.getAttributeValue("attribute"))) {
				returnValue = value;
			} else if ("mode".equals(subargs.getAttributeValue("attribute"))) {
				Sargs = value;
			} else if ("keySQL".equals(subargs.getAttributeValue("attribute"))) {
				if (keySQL == null) {
					keySQL = new Vector<String>();
				}
				keySQL.add(subargs.getValue());
			} else if ("arg".equals(name)) {
				sql.addElement(value);
			}

			else if ("subarg".equals(name)) {
				Iterator j = subargs.getChildren().iterator();
				Font font = null;
				Color foreground = null;
				Color background = null;
				boolean enabled = true;
				boolean returnValueXTF = true;
				key = false;
				String totalCol = null;
				String alignment = null;
				String keyExternalValue = null;
				boolean systemDate = false;
				String idDocument = null;
				boolean nullValue = false;
				boolean clean = true;
				String callExternalClass = null;
				String callExternalMethod = null;
				int orden = -1;
				String lab = "";
				String format = "";
				String mask = null;
				int width = 0;
				int nroChars = 0;
				XMLTextField XMLText = null;
				String exportValue = null;
				Vector<String> importValue = null;
				Vector<String> constantValue = null;
				String calculateExportValue = null;
				String calculateBSExportValue = null;
				String maxValue = null;
				String formatDate = null;
				boolean searchQuery = false;
				boolean printable = false;
				String calculateDate = null;
				String nameField = null;
				String addAttribute = null;
				String sendRecord = null;
				String cname = null;
				boolean queryOnInit = false;
				boolean withOutArgsQuery = false;
				String sqlInit = null;
				while (j.hasNext()) {
					final Element elm = (Element) j.next();
					if ("sqlCode".equals(elm.getAttributeValue("attribute"))) {
						this.sqlCode.add(elm.getValue());
						searchQuery = true;
					} else if ("sqlLocal".equals(elm.getAttributeValue("attribute"))) {
						this.sqlLocal = elm.getValue();
						searchQuery = true;
					} else if ("label".equals(elm.getAttributeValue("attribute"))) {
						lab = elm.getValue();
					} else if ("format".equals(elm.getAttributeValue("attribute"))) {
						format = elm.getValue();
					}
					 else if ("name".equals(elm.getAttributeValue("attribute"))) {
							cname = elm.getValue();
						}
					else if ("mask".equals(elm.getAttributeValue("attribute"))) {
						mask = elm.getValue();
					} else if ("size"
							.equals(elm.getAttributeValue("attribute"))) {
						width = Integer.parseInt(elm.getValue());
					} else if ("maxlength".equals(elm
							.getAttributeValue("attribute"))) {
						nroChars = Integer.parseInt(elm.getValue());
					} else if ("key".equals(elm.getAttributeValue("attribute"))) {
						lab = elm.getValue();
						key = true;
					} else if ("callExternalClass".equals(elm
							.getAttributeValue("attribute"))) {
						callExternalClass = elm.getValue();
					} else if ("callExternalMethod".equals(elm
							.getAttributeValue("attribute"))) {
						callExternalMethod = elm.getValue();
					} else if ("exportValue".equals(elm
							.getAttributeValue("attribute"))) {
						exportValue = elm.getValue();
						searchQuery = true;
					} else if ("importValue".equals(elm.getAttributeValue("attribute"))) {
						if (importValue == null) {
							importValue = new Vector<String>();
						}
						importValue.addElement(elm.getValue());
					} else if ("constantValue".equals(elm.getAttributeValue("attribute"))) {
						if (constantValue == null) {
							constantValue = new Vector<String>();
						}
						constantValue.addElement(elm.getValue());
					} else if ("calculateExportValue".equals(elm.getAttributeValue("attribute"))) {
						calculateExportValue = elm.getValue();
					} else if ("calculateBSExportValue".equals(elm.getAttributeValue("attribute"))) {
						calculateBSExportValue = elm.getValue();
					} else if ("maxValue".equals(elm.getAttributeValue("attribute"))) {
						maxValue = elm.getValue();
					} else if ("exportField".equals(elm.getAttributeValue("attribute"))) {
						try {
							orden = Integer.parseInt(elm.getValue());
						} catch (NumberFormatException NFEe) {
						}
					} else if ("enabled".equals(elm.getAttributeValue("attribute"))) {
						enabled = Boolean.parseBoolean(elm.getValue());
					} else if ("font"
							.equals(elm.getAttributeValue("attribute"))) {
						try {
							StringTokenizer STfont = new StringTokenizer(elm
									.getValue(), ",");
							font = new Font(
							                STfont.nextToken(),
							                Integer.parseInt(STfont.nextToken()),
							                Integer.parseInt(STfont.nextToken()));
						} catch (NumberFormatException NFEe) {
							font = null;
						} catch (NoSuchElementException NSEEe) {
							font = null;
						}
					} else if ("foreground".equals(elm.getAttributeValue("attribute"))) {
						foreground = getColor(elm.getValue());
					} else if ("background".equals(elm
							.getAttributeValue("attribute"))) {
						background = getColor(elm.getValue());
					} else if ("totalCol".equals(elm.getAttributeValue("attribute"))) {
						totalCol = elm.getValue();
					} else if ("alignment".equals(elm.getAttributeValue("attribute"))) {
						alignment = elm.getValue();

					} else if ("keyExternalValue".equals(elm.getAttributeValue("attribute"))) {
						keyExternalValue = elm.getValue();
						searchQuery = true;
					} else if ("systemDate".equals(elm
							.getAttributeValue("attribute"))) {
						systemDate = true;
						formatDate = elm.getValue();
					} else if ("idDocument".equals(elm
							.getAttributeValue("attribute"))) {
						idDocument = elm.getValue();
					} else if ("nullValue".equals(elm
							.getAttributeValue("attribute"))) {
						nullValue = Boolean.parseBoolean(elm.getValue());
					} else if ("clean".equals(elm
							.getAttributeValue("attribute"))) {
						clean = Boolean.parseBoolean(elm.getValue());
					} else if ("return".equals(elm
							.getAttributeValue("attribute"))) {
						returnValueXTF = Boolean.parseBoolean(elm.getValue());
					} else if ("nameField".equals(elm
							.getAttributeValue("attribute"))) {
						nameField = elm.getValue();
					} else if ("dateFormat".equals(elm
							.getAttributeValue("attribute"))) {
						formatDate = elm.getValue();
					} else if ("calculateDate".equals(elm
							.getAttributeValue("attribute"))) {
						calculateDate = elm.getValue();
					} else if ("addAttribute".equals(elm
							.getAttributeValue("attribute"))) {
						addAttribute = elm.getValue();
					} else if ("printable".equals(elm
							.getAttributeValue("attribute"))) {
						printable = Boolean.parseBoolean(elm.getValue());
					} else if ("sendRecord".equals(elm
							.getAttributeValue("attribute"))) {
						sendRecord = elm.getValue();
					} else if ("queryOnInit".equals(elm.getAttributeValue("attribute"))) {
						queryOnInit = Boolean.parseBoolean(elm.getValue());
						searchQuery = true;
					} else if ("withOutArgsQuery".equals(elm.getAttributeValue("attribute"))) {
						withOutArgsQuery = Boolean.parseBoolean(elm.getValue());
					} else if ("sqlInit".equals(elm.getAttributeValue("attribute"))) {
						sqlInit = elm.getValue();
					} 
				}

				if (mask == null) {
					XMLText = new XMLTextField(lab, width, nroChars, format);
				} else {
					XMLText = new XMLTextField(lab, width, nroChars, format,mask);
				}
				if (key) {
					XMLText.isKey(true);
				}
				XMLText.setKeyExternalValue(keyExternalValue);
				// XMLText.setEditable(enabled);
				XMLText.setEnabled(enabled);
				XMLText.setSystemDate(systemDate);
				XMLText.setReturnValue(returnValueXTF);
				XMLText.setNullValue(nullValue);
				XMLText.setClean(clean);
				XMLText.setPrintable(printable);
				XMLText.setSendRecord(sendRecord);
				XMLText.setCalculateBSExportValue(calculateBSExportValue);
				XMLText.setName(cname);
				XMLText.setQueryOnInit(queryOnInit);
				XMLText.setWithOutArgsQuery(withOutArgsQuery);
				XMLText.setSqlInit(sqlInit);
				
				if (exportValue != null) {
					XMLText.setExportvalue(exportValue);
				}
				if (importValue != null) {
					XMLText.setImportValue(importValue);
				}
				if (constantValue != null) {
					XMLText.setConstantValue(constantValue);
				}
				if (maxValue != null) {
					XMLText.setMaxValue(maxValue);
				}
				if (nameField != null) {
					XMLText.setNameField(nameField);
				}
				if (calculateExportValue != null) {
					XMLText.setCalculateExportValue(calculateExportValue);
				}
				if (callExternalClass != null && callExternalMethod != null) {
					XMLText.setExternalCall(callExternalClass,
							callExternalMethod);
				}
				if (orden > -1 && exportValue != null) {
					GFforma.addExportField(orden, exportValue);
				}
				JPetiquetas.add(XMLText.getLabel());

				if (systemDate) {
					XMLText.setFormatDate(formatDate);
					ClientHeaderValidator.addDateListener(this);
				}

				if (!systemDate && formatDate != null) {
					XMLText.setFormatDate(formatDate);
					ClientHeaderValidator.addDateListener(this);
				}
				if (calculateDate != null) {
					XMLText.setCalculateDate(calculateDate);
				}

				if (havePanel) {
					JPfields.add(XMLText.getJPtext());
				} else {
					JPfields.add(XMLText);
				}

				if (background != null) {
					XMLText.setBackground(background);
					if (XMLText.getBackground().equals(Color.BLACK)) {
						XMLText.setCaretColor(Color.WHITE);
					}
					XMLText.setDisabledTextColor(Color.WHITE);
				}

				if (foreground != null) {
					XMLText.setForeground(foreground);
				}

				if (font != null) {
					XMLText.getLabel().setFont(font);
					XMLText.setFont(font);
				}

				if (totalCol != null) {
					XMLText.setTotalCol(totalCol);
				}

				if (alignment != null) {
					if (alignment.equals("RIGHT")) {
						XMLText.setHorizontalAlignment(SwingConstants.RIGHT);
					} else if (alignment.equals("CENTER")) {
						XMLText.setHorizontalAlignment(SwingConstants.CENTER);
					}
				}
				if (idDocument != null) {
					XMLText.setIdDocument(idDocument);
				}
				if (addAttribute != null) {
					XMLText.setAddAttribute(addAttribute);
				}
				XMLText.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						
						XMLTextField field = (XMLTextField) e.getSource();
						String text = field.getText();
						if ("NUMERIC".equals(field.getType())) {
							try {
								NumberFormat nf = NumberFormat.getNumberInstance();
			        			DecimalFormat form = (DecimalFormat) nf;
			        			form.applyPattern("###,###,##0.00");
			        			field.setText(nf.format(Double.parseDouble(text)));
			        			field.setNumberValue(nf.parse(text).doubleValue());
							} catch (NumberFormatException NFEe) {
								//NFEe.printStackTrace();
							}
							catch (ParseException Pe) {
								//Pe.printStackTrace();
							}
						}
						if (!"".equals(text) &&
							(field.isExportvalue() || field.getKeyExternalValue() != null)) {
							exportar(field);
						}
					}
				});
				if (searchQuery) {
					XMLText.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent e) {
							XMLTextField XMLRefText = (XMLTextField) e.getSource();
							if (!XMLRefText.getText().trim().equals("")) {
								search = true;
							}
						}
					});

					XMLText.addFocusListener(new FocusAdapter() {
												
						public void focusLost(final FocusEvent e) {

							/*
							 * if (XMLText.isExportvalue() ||
							 * XMLText.getKeyExternalValue()!=null) {
							 * exportar(XMLText); } Comentado por que se lo
							 * envio al primer focusLost de XMLTextField
							 */
							XMLTextField field = (XMLTextField) e.getSource();
							processQuery(field);
						}
					});
				}
				if ("NUMERIC".equals(XMLText.getType())) {
					XMLText.setText("0.00");
					XMLText.setNumberValue(0.00);
					if (XMLText.isExportvalue()) {
						GFforma.setExternalValues(XMLText.getExportvalue(),0.00);
					}
				}
				VFields.add(XMLText);
			}
		}

		this.add(JPetiquetas, BorderLayout.WEST);
		this.add(JPfields, BorderLayout.CENTER);
		if ("DELETE".equals(Sargs))
			Disabled(1);

		if (disableAll)
			Disabled(0);
		Thread t = new Thread () {
			public void run() {
				for (XMLTextField field : VFields) {
					if (field.isQueryOnInit()) try {
						Document doc = null; 
						doc = STResultSet.getResultSetST(field.getSqlInit(),getArgsForQuery(field));
						Iterator i = doc.getRootElement().getChildren("row").iterator();
			            while (i.hasNext()) {
			                Element e = (Element) i.next();
		                    Iterator j = e.getChildren().iterator();
	                        Element f = (Element)j.next();
	                        String text = f.getValue();
	                        if ("NUMERIC".equals(field.getType())) {
	                        	NumberFormat nf = NumberFormat.getNumberInstance();
			        			DecimalFormat form = (DecimalFormat) nf;
			        			form.applyPattern("###,###,##0.00");
			        			field.setText(nf.format(Double.parseDouble(text)));
			        			field.setNumberValue(nf.parse(text).doubleValue());
	                        }
	                        else {
	                        	field.setText(text);
	                        }
			            }
					} catch (STException STEe) {
						//STEe.printStackTrace();
					} catch (ParseException PSe) {
						//PEe.printStackTrace();
					}
				}
			}
		};
		t.start();
	}

	private String[] getArgsForQuery(XMLTextField field) {
		if (field.isWithOutArgsQuery()) { return null; }
		String[] impValues = null;
		String[] imps = field.getImportValues();
		int argumentos = imps.length + field.getConstantSize();
		impValues = new String[argumentos];
		int i = 0;
		for (; i < field.getConstantSize(); i++) {
			impValues[i] = field.getConstantValue(i);
		}
		for (; i < imps.length; i++) {
			impValues[i] = GFforma.getExteralValuesString(imps[i]);
		}
		return impValues;
	}
	
	private void processQuery(XMLTextField field) {

		String text = field.getText();
		field.setSendQuery(true);
		if (sqlCode.size() > 0 || sqlLocal != null) {
			/*
			 * El primer elemento del vector sql siempre
			 * sera la consulta que almacenara la
			 * informacion del objeto local
			 */
			if (search || field.isQueryOnInit()) {
				if (sqlLocal != null) {
					String[] imps = getArgsForQuery(field);
					GFforma.cleanExternalValues();
					if (!"".equals(text) || field.isQueryOnInit() ) {
						
						new GenericDataFiller(GFforma,
								namebutton, false,
								sqlLocal, imps,
								text,
								VFields).start();
					}

				}
				/*
				 * Las demas consultas seran para almacenar
				 * la informacion de los demas objetos que
				 * lo requieran
				 */
				searchOthersSqls(field);
				search = false;
			}
		}
		if (field.isSendRecord()) {
			if ("NUMERIC".equals(field.getType())) {
				text = String.valueOf(field.getNumberValue());
			}
			notificando(field, text);
		}
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
			int r = Integer.parseInt(STcolor.nextToken());
			int g = Integer.parseInt(STcolor.nextToken());
			int v = Integer.parseInt(STcolor.nextToken());
			return new Color(r, g, v);
		} catch (NumberFormatException NFEe) {
			return null;
		} catch (NoSuchElementException NSEEe) {
			return null;
		}
	}

	public void exportar(XMLTextField xmltf) {
		notificando(CVEevent);
		if (xmltf.getType().equals("NUMERIC")) {
			if (xmltf.isExportvalue()) {
				GFforma.setExternalValues(xmltf.getExportvalue(), xmltf
						.getNumberValue());
			}
			xmltf.setText(formatear(xmltf.getNumberValue()));
		} else if (xmltf.isExportvalue()) {
			GFforma.setExternalValues(xmltf.getExportvalue(), xmltf.getText());
		}
	}

	protected String formatear(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		NumberFormat nf = NumberFormat.getNumberInstance();
		DecimalFormat form = (DecimalFormat) nf;
		form.applyPattern("###,###,##0.00");
		return form.format(bd);
	}

	/**
	 * Metodo encargado de generar sentencias sql de otros componentes
	 */
	public void searchOthersSqls(XMLTextField xmltf) {
		class SearchingSQL extends Thread {

			private String[] args;

			public SearchingSQL(String[] args) {
				this.args = args;
			}

			public void run() {

				String sql;
				for (int i = 0; i < sqlCode.size(); i++) {
					Document doc = null;
					sql = (String) sqlCode.get(i);
					try {
						doc = STResultSet.getResultSetST(sql, args);
					} catch (STException e) {
						e.printStackTrace();
					}
					AnswerEvent event = new AnswerEvent(this, sql, doc);
					notificando(event);
				}
			}
		}
		/*-----------------------------------------------------------*/
		String[] argumentos = new String[xmltf.getImportValues().length
				+ xmltf.getConstantSize() + 1];
		String[] XMLimpValues = xmltf.getImportValues();

		int i = 0;
		for (i = 0; i < xmltf.getConstantSize(); i++) {
			argumentos[i] = xmltf.getConstantValue(i);
		}

		for (; i < xmltf.getImportValues().length; i++) {
			argumentos[i] = GFforma.getExteralValuesString(XMLimpValues[i]);
		}
		argumentos[i] = xmltf.getText();
		new SearchingSQL(argumentos).run();

	}

	public JPanel getPanel() {
		return this;
	}
	
	public void requestFocus(int index) {
		VFields.get(index).requestFocus();
	}
	
	public void Disabled(int init) {
		int max = VFields.size();
		for (int i = init; i < max; i++) {
			((XMLTextField) VFields.get(i)).setEnabled(false);
		}
	}

	public void clean() {
		int max = VFields.size();
		for (int i = 0; i < max; i++) {
			XMLTextField xmltemp = (XMLTextField) VFields.get(i);
			xmltemp.setSendQuery(false);
			if (xmltemp.isClean()) {
				xmltemp.setText("");
				if (xmltemp.getType().equals("NUMERIC")) {
					xmltemp.setText("0,00");
					xmltemp.setNumberValue(0.00);
				}
			}
			if (xmltemp.isSystemDate()) {
				GFforma.sendTransaction(DateSender.getPackage());
			}
		}
	}

	public void validPackage(Element args) throws Exception {
		getPackage(args);
	}
	
	public Element getPackage(Element args) throws Exception {
		Element pack = new Element("package");
		String errorMessage = null;
		boolean error = false;
		
		Iterator it = args.getChildren().iterator();
		while (it.hasNext()) {

			Element arg = (Element) it.next();
			if ("calculate".equals(arg.getAttributeValue("attribute"))) {
				pack.addContent(new Element("field").setText(String.valueOf(formulaHandler(arg.getValue()))));
			}
			if ("maxValue".equals(arg.getAttributeValue("attribute"))) {
				try {
					StringTokenizer stk = new StringTokenizer(arg.getValue(),":");
					double maximo = formulaHandler(stk.nextToken());
					double checkValue = formulaHandler(stk.nextToken());
					if (maximo < checkValue) {
						error=true;
					}
				} catch (NoSuchElementException NSEe) {
					error=true;
				}
			}
			
			if ("equalsValue".equals(arg.getAttributeValue("attribute"))) {
				try {
					StringTokenizer stk = new StringTokenizer(arg.getValue(),":");
					double val1 = formulaHandler(stk.nextToken());
					double val2 = formulaHandler(stk.nextToken());
					
					if (val1 != val2) {
						error=true;
					}
				} catch (NoSuchElementException NSEe) {
					error=true;
				}
			}
			if ("errorMessage".equals(arg.getAttributeValue("attribute"))) {
				errorMessage = arg.getValue();
			}
		}
		
		if (error) {
			if (errorMessage!=null) {
				throw new Exception(errorMessage);
			}
			else {
				throw new Exception(Language.getWord("ERR_EQUALS_VALUE"));
			}
		}

		return pack;
	}

	public Element getPackage() throws VoidPackageException {

		Element pack = new Element("package");
		int max = VFields.size();

		if (returnBlankPackage) {
			boolean returnValue = true;
			for (int j = 0; j < VFields.size(); j++) {
				XMLTextField Field = (XMLTextField) VFields.get(j);
				if (Field.isReturnValue()) {
					if (!"".equals(Field.getText())) {
						returnValue = false;
						break;
					}
				}
			}

			if (returnValue) {
				return pack;
			}
		}

		int i = 0;
		if ("EDIT".equals(Sargs))
			i = 1;

		if (!"DELETE".equals(Sargs))
			for (; i < max; i++) {
				XMLTextField Field = (XMLTextField) VFields.get(i);
				String text = Field.getText();
				if (Field.isReturnValue()) {
					if (Field.isNullValue()) {
						pack.addContent(Field.getElementText());
					} else {
						if (!text.equals("")) {
							Element elm = Field.getElementText();
							if (Field.getAddAttribute() != null) {
								elm.setAttribute("attribute", Field.getAddAttribute());
							}
							pack.addContent(elm);
						} else {
							throw new VoidPackageException(Field.getLabel().getText());
						}
					}

					if (text.equals("") && "DELETE".equals(Sargs)) {
						throw new VoidPackageException(Field.getLabel().getName());
					}
				}
			}

		if ("EDIT".equals(Sargs) || "DELETE".equals(Sargs)) {
			XMLTextField Field = VFields.get(0);
			String text = Field.getText();
			if (Field.isNullValue()) {
				pack.addContent(Field.getElementText());
			} else {
				if (!text.equals("")) {
					pack.addContent(Field.getElementText());
				} else {
					throw new VoidPackageException(Field.getLabel().getText());
				}
			}
		}

		return pack;
	}

	public Element getPrintPackage() {

		Element pack = new Element("package");
		int cont = 0;
		int max = VFields.size();
		for (XMLTextField xmltf : VFields) {
			if (xmltf.isPrintable()) {
				String text = xmltf.getType().equals("NUMERIC") ? String
						.valueOf(xmltf.getNumberValue()) : xmltf.getText();
				if ("".equals(text)) {
					cont++;
				}
				pack.addContent(new Element("field").setText(text));
			}
		}
		if (cont == max) {
			return new Element("package");
		}
		return pack;
	}
	
	public synchronized void addAnswerListener(AnswerListener listener) {
		AnswerListener.addElement(listener);
	}

	public synchronized void removeAnswerListener(AnswerListener listener) {
		AnswerListener.removeElement(listener);
	}

	public void arriveAnswerEvent(AnswerEvent AEe) {
		for (int ind = 0; ind < keySQL.size(); ind++)
			if (AEe.getSqlCode().equals(keySQL.get(ind))) {
				Document doc = AEe.getDocument();
				Iterator i = doc.getRootElement().getChildren("row").iterator();
				int row = doc.getRootElement().getChildren("row").size();

				if (AEe.getSqlCode().equals(keySQL.get(ind)) && ind == 0) {
					clean();
				}
				if (row > 0) {
					while (i.hasNext()) {
						Element e = (Element) i.next();
						Iterator j = e.getChildren().iterator();
						for (int k = 0; j.hasNext(); k++) {
							Element f = (Element) j.next();
							XMLTextField XMLRefText = ((XMLTextField) VFields
									.get(k));

							if (XMLRefText.getFormatDate() != null) {
								try {
									Format formatter = new SimpleDateFormat(
											XMLRefText.getFormatDate());
									XMLRefText.setText(formatter
											.format(Timestamp.valueOf(f
													.getValue().trim())));
								} catch (IllegalArgumentException IAEe) {
									XMLRefText.setText(f.getValue().trim());
								}
							} else if (XMLTextField.NUMERIC.equals(XMLRefText
									.getType())) {
								try {
									XMLRefText.setNumberValue(Double
											.parseDouble(f.getValue().trim()));
									// XMLRefText.setText(Double.parseDouble(f.getValue().trim())));
								} catch (NumberFormatException NFEe) {
								}
							} else {
								XMLRefText.setText(f.getValue().trim());
							}
							if (XMLRefText.isExportvalue()) {
								exportar(XMLRefText);
							}
						}
					}
					if (Sargs != null) {
						if ("NEW".equals(Sargs)) {
							GFforma.setEnabledButton(namebutton, false);
						} else {
							GFforma.setEnabledButton(namebutton, true);
							// clean();
						}
					}
				} else {
					for (int j = 0; j < VFields.size(); j++) {
						XMLTextField XMLRefText = ((XMLTextField) VFields
								.get(j));
						if (XMLTextField.NUMERIC.equals(XMLRefText.getType())) {
							try {
								XMLRefText.setNumberValue(0);
							} catch (NumberFormatException NFEe) {
							}
							if (XMLRefText.isExportvalue()) {
								exportar(XMLRefText);
							}

						}
					}
					if (Sargs != null) {
						if ("NEW".equals(Sargs)) {
							GFforma.setEnabledButton(namebutton, true);
						} else {
							GFforma.setEnabledButton(namebutton, false);
							// clean();
						}
					}
				}
			}
	}

	public Vector getVFields() {
		return VFields;
	}

	public Element getReturnValue() {
		Element pack = new Element("package");
		pack.addContent(new Element("field").setText(returnValue));
		return pack;
	}

	/**
	 * Metodo encargado de notificar la llegada de un paquete <answer/>
	 * 
	 * @param event
	 */
	private synchronized void notificando(AnswerEvent event) {
		Vector lista;
		lista = (Vector) AnswerListener.clone();
		for (int i = 0; i < lista.size(); i++) {
			AnswerListener listener = (AnswerListener) lista.elementAt(i);
			listener.arriveAnswerEvent(event);
		}
	}

	public void callAddAnswerListener() throws InvocationTargetException,
			NotFoundComponentException {
		if (driverEvent != null) {
			GFforma.invokeMethod(driverEvent, "addAnswerListener",
							new Class[] { AnswerListener.class },
							new Object[] { this });
		}

	}

	public void initiateFinishEvent(FinishEvent e) {
		try {
			callAddAnswerListener();
			GFforma.addChangeExternalValueListener(this);
			
		} catch (NotFoundComponentException NFCEe) {
			NFCEe.printStackTrace();
		} catch (InvocationTargetException ITEe) {
			ITEe.printStackTrace();
		}
	}

	public String getDriverEvent() {
		return driverEvent;
	}

	public void setDriverEvent(String driverEvent) {
		this.driverEvent = driverEvent;
	}

	public boolean isSearch() {
		return search;
	}

	public void setSearch(boolean search) {
		this.search = search;
	}

	public String getEnlaceTabla() {
		return enlaceTabla;
	}

	public synchronized void addChangeValueListener(ChangeValueListener listener) {
		changeValueListener.addElement(listener);
	}

	public synchronized void removeChangeValueListener(
			ChangeValueListener listener) {
		changeValueListener.removeElement(listener);
	}

	private synchronized void notificando(ChangeValueEvent event) {

		/*
		 * Vector fields = this.getVFields(); int size = fields.size(); for (int
		 * i=0;i<size;i++) { try { double val =
		 * Double.parseDouble(((XMLTextField)fields.get(i)).getText());
		 * ((XMLTextField)fields.get(i)).setNumberValue(val) ; } catch
		 * (NumberFormatException NFEe) {
		 * ((XMLTextField)fields.get(i)).setNumberValue(0); } }
		 */
		Vector lista;
		lista = (Vector) changeValueListener.clone();
		for (int i = 0; i < lista.size(); i++) {
			ChangeValueListener listener = (ChangeValueListener) lista
					.elementAt(i);
			listener.changeValue(event);
		}
	}

	public void changeExternalValue(ChangeExternalValueEvent e) {
		
		Vector fields = this.getVFields();
		for (int i = 0; i < getVFields().size(); i++) {
			XMLTextField xmltf = ((XMLTextField) fields.get(i));
			synchronized (xmltf) {
				String oldValue = xmltf.getText();
				if (xmltf.isCalculateExportvalue()) {
					String formula = xmltf.getCalculateExportValue();
					double valor = formulaHandler(formula);
					xmltf.setText(formatear(valor));
					xmltf.setNumberValue(valor);
					if (xmltf.isSendRecord() && !xmltf.getText().equals(oldValue)) {
						notificando(xmltf, String.valueOf(valor));
					}
					if (xmltf.isExportvalue() && !xmltf.getText().equals(oldValue)) {
						GFforma.setExternalValues(xmltf.getExportvalue(),valor);
					}
				}
				if (xmltf.isCalculateBSExportValue()) {
					String formula = xmltf.getCalculateBSExportValue();
					double valor = formulaHandler(formula, true);
					xmltf.setText(formatear(valor));
					xmltf.setNumberValue(valor);
					if (xmltf.isSendRecord() && !xmltf.getText().equals(oldValue)) {
						notificando(xmltf, String.valueOf(valor));
					}
					if (xmltf.isExportvalue() && !xmltf.getText().equals(oldValue)) {
						GFforma.setExternalValues(xmltf.getExportvalue(),valor);
					}
				}
				if (xmltf.isCalculateDate()) {
					try {
						StringTokenizer stk = new StringTokenizer(xmltf
								.getCalculateDate(), "+");
						if (stk.countTokens() < 2)
							stk = new StringTokenizer(xmltf.getCalculateDate(),"-");
						String val1 = GFforma.getExteralValuesString(stk.nextToken());
						String val2 = GFforma.getExteralValuesString(stk.nextToken());
						if (val1 != null && val2 != null) {
							Date date = Date.valueOf(val1);
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(val2));
							Format formatter = new SimpleDateFormat(xmltf.getFormatDate());
							xmltf.setText(formatter.format(cal.getTime()));
							if (xmltf.isSendRecord() && !xmltf.getText().equals(oldValue)) {
								notificando(xmltf, formatter.format(cal.getTime()));
							}
						}
					} catch (NoSuchElementException NSEe) {

					} catch (NumberFormatException NFEe) {

					}
				}
				if (sqlLocal != null) {
					new GenericDataFiller(GFforma, namebutton, enablebutton,
							sqlLocal, new String[] {
													GFforma.getExteralValuesString(
							                                                        xmltf.getImportValues()[0]) }, null,
							                                                        VFields).start();
				}
			}
		}
	}

	public double formulaHandler(String formula) {
		return formulaHandler(formula, false);
	}

	/**
	 * Este metodo calcula una formula
	 * 
	 * @param formula
	 * @return Valor calculado
	 */
	
	public double formulaHandler(String formula, boolean beanshell) {
		String formulaFinal = GFforma.parseFormula(formula);
		if (beanshell) {
			try {
				Object val = GFforma.eval(formulaFinal);
				if (val instanceof Double) {
					return (Double) val;
				}
				if (val instanceof Integer) {
					return new Double((Integer)val);
				}
			} catch (EvalError e) {
				e.printStackTrace();
			}
		}
		return ((Double) FormulaCalculator.operar(formulaFinal)).doubleValue();
	}
	
	public void cathDateEvent(DateEvent e) {
		for (int i = 0; i < VFields.size(); i++) {
			XMLTextField xmltf = (XMLTextField) VFields.get(i);
			if (xmltf.getFormatDate() != null && xmltf.isSystemDate()) {
				// Format formatter = new
				// SimpleDateFormat(xmltf.getFormatDate());
				// xmltf.setText(formatter.format(Timestamp.valueOf(e.getDate())));
				xmltf.setText(e.getDate());
				if (xmltf.isExportvalue())
					GFforma.setExternalValues(xmltf.getExportvalue(), xmltf.getText());
			}
		}
	}

	public boolean containData() {
		try {
			Element elm = getPackage();
			if (elm.getChildren().size() > 0) {
				return true;
			}
		} catch (VoidPackageException e) {
			return false;
		}
		return false;
	}

	public synchronized void addRecordListener(RecordListener listener) {
		recordListener.addElement(listener);
	}

	public synchronized void removeRecordListener(RecordListener listener) {
		recordListener.removeElement(listener);
	}

	private synchronized void notificando(XMLTextField XMLTFtext, String value) {
		Element element = new Element("table");
		Element row = new Element("row");
		element.addContent(row);
		StringTokenizer stk = new StringTokenizer(XMLTFtext.getSendRecord(),",");

		while (true) {
			try {
				String tok = stk.nextToken();
				Element col = new Element("col");
				if ("value".equals(tok)) {
					col.setText(value);
				} else {
					col.setText(tok.substring(1, tok.length() - 1));
				}
				row.addContent(col);
			} catch (NoSuchElementException NSEe) {
				break;
			}
		}

		RecordEvent event = new RecordEvent(this, element);

		Vector lista;
		lista = (Vector) recordListener.clone();
		for (int i = 0; i < lista.size(); i++) {
			RecordListener listener = (RecordListener) lista.elementAt(i);
			listener.arriveRecordEvent(event);
		}
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
	
	public Component getFieldAt(int index) {
		return VFields.get(index);
	}
}