package client.gui.components;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Element;

/**
 * ArgsTableFindData.java Creado el 07-abr-2005
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de generar y retornar los argumentos de cada fila
 * la tabla FindData
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class ColumnsArgsGenerator {

	private String name;
    private int lengthCol;
    private int size;
    private Object typeDate;
    private boolean editable;
    private int orderQuery= -1;
    private int orderReturn = -1;
    private boolean clean;
    private String type;
    private String sqlCombo;
    private String tmpDefaultValue;
    private Number defaultValue;
    private String exportValue = null;
    private String exportValueCombo = null;
    private Vector<String> importValueCombo = null;
    private Vector<String> importValues;
    private int decimals = 2;
    private String nameField;
    private boolean returnBlankCol;
    private boolean returnNullCol;
    private boolean printable = false;
    private Class ColumnClass;
    private String keyDataSearch;
    private int repeatData;
	private boolean dataBeep;
	private boolean blankArgs;
	private String noDataMessage;
	private int selected;
	private boolean specializedCellEditable = true;
	private Element args;
	private Double minValue;
	private boolean enabledTextField = true;
	
	private short columnDebit = -1;
	private short columnCredit = -1;
	private short columnAmount = -1;
	private short columnIdProdServ = -1;
	private short columnIdWareHouse = -1;
	private short columnValue = -1;
	private String valideEnabledCol = null;
	
    public boolean isDataBeep() {
		return dataBeep;
	}

	public String getNoDataMessage() {
		return noDataMessage;
	}

	/**
     * Este constructor recibe un StringTokenizer como parametro
     * y de este genera todos los argumentos necesarios para 
     * parametrizar la columna
     * 
     */
    public ColumnsArgsGenerator(Element sargs) {
    	this.args = sargs;
    	importValues = new Vector<String>();
    	importValueCombo = new Vector<String>();
    	Iterator it = sargs.getChildren().iterator();
    	BigDecimal bd = new BigDecimal(0.00);
    	while (it.hasNext()) {
    		Element arg = (Element) it.next();
    		String attrib = arg.getAttributeValue("attribute");
    		String value = arg.getValue();

    		if (attrib.equals("name")) {
    			name = value;
    		} 
    		else if (attrib.equals("returnBlankCol")) {
    			returnBlankCol = Boolean.parseBoolean(value);
    		}
    		else if (attrib.equals("returnNullCol")) {
    			returnNullCol = Boolean.parseBoolean(value);
    		}
    		else if (attrib.equals("length")) {
    			lengthCol = Integer.parseInt(value);
    		}
    		else if (attrib.equals("size")) {
    			size = Integer.parseInt(value);
    		}
    		else if (attrib.equals("exportValue")) {
    			exportValue = value;
    		}
    		else if (attrib.equals("importValue")) {
    			importValues.add(value);
    		}
    		else if (attrib.equals("exportValueCombo")) {
    			exportValueCombo = value;
    		}
    		else if (attrib.equals("importValueCombo")) {
    			importValueCombo.add(value);
    		}
    		else if("minValue".equals(attrib)) {
    			minValue = new Double(value);
    		}
    		else if (attrib.equals("type")) {
    			type = value;
    			defaultValue = new Double(0.00);
    			if (value.equals("STRING")) { 
    				typeDate = new String();
    				ColumnClass = String.class;
    			}
    			else if (value.equals("BOOLEAN")) {
    				typeDate = new Boolean(false);
    				ColumnClass = Boolean.class;
    			}
    			else if (value.equals("INT") || type.equals("INTEGER")) {
    				typeDate = new Integer(0);
    				ColumnClass = Integer.class;
    			}
    			else if (value.equals("DECIMAL")) {
    				typeDate = bd.setScale(decimals,BigDecimal.ROUND_HALF_UP);
    				ColumnClass = BigDecimal.class;
    			}
    			else if (value.equals("COMBOSQL")) {
    				typeDate = new String();
    				ColumnClass = String.class;
    			}
    			else if (value.equals("DATE")) {
    				typeDate = null;
    				ColumnClass = Date.class;
    			}
    			else if (value.equals("DATASEARCH")) {
    				typeDate = new String();
    				ColumnClass = String.class;
    			}
    			else if (value.equals("DETAILEDPRODUCT")) {
    				typeDate = new String();
    				ColumnClass = String.class;

    			}
    			else if (value.equals("TEXTILEPRODUCT")) {
    				typeDate = new String();
    				ColumnClass = String.class;

    			}
    			else if (value.equals("TOUCHBUTTONS")) {
    				typeDate = new Integer(0);
    				ColumnClass = Integer.class;
    			}
    		}
    		else if (attrib.equals("defaultValue")) {
    			tmpDefaultValue = value;
    		}
    		else if (attrib.equals("roundDecimal")) {
    			decimals = Integer.parseInt(value);
    			if (typeDate!=null) {
    				typeDate = bd.setScale(decimals,BigDecimal.ROUND_HALF_UP);
    			}
    		}
    		else if (attrib.equals("enabled")) {
    			editable = Boolean.parseBoolean(value);
    		}
    		else if (attrib.equals("specializedCellEditable")) {
    			specializedCellEditable = Boolean.parseBoolean(value);
    		}
    		else if (attrib.equals("printable")) {
    			printable = Boolean.parseBoolean(value);
    		}
    		else if (attrib.equals("enabledTextField")) {
    			enabledTextField = Boolean.parseBoolean(value);
    		}
    		else if (attrib.equals("queryCol")) {
    			try {
    				orderQuery = Integer.parseInt(value);
    			}
    			catch(NumberFormatException NFEe) {
    				orderQuery = -1;
    			}
    		}
    		else if (attrib.equals("returnCol")) {
    			try {
    				orderReturn = Integer.parseInt(value);
    			}
    			catch(NumberFormatException NFEe) {
    				orderReturn = -1;
    			}
    		}
    		else if (attrib.equals("clean")) {
    			clean = Boolean.parseBoolean(value);
    		}
    		else if (attrib.equals("sqlCombo")) {
    			sqlCombo = value;
    		}
    		else if ("nameField".equals(attrib)) {
    			nameField = value;
    		}
    		else if ("repeatData".equals(attrib)) {
    			try {
    				repeatData = Integer.parseInt(value);
    			}
    			catch(NumberFormatException NFEe) {
    				repeatData = 1;
    			}
    		}
    		else if ("keyDataSearch".equals(attrib)) {
    			keyDataSearch = value; 
    		}
    		else if ("noDataBeep".equals(attrib)) {
    			dataBeep = Boolean.parseBoolean(value);
    		}
    		else if ("blankArgs".equals(attrib)) {
    			blankArgs = Boolean.parseBoolean(value);
    		}
    		else if ("noDataMessage".equals(attrib)) {
    			noDataMessage = value;
    		}
    		else if ("noDataMessage".equals(attrib)) {
    			noDataMessage = value;
    		}
    		else if ("selectedIndex".equals(attrib)) {
    			try {
    				selected = Integer.parseInt(value);
    			}
    			catch(NumberFormatException NFEe) {
    				selected = 1;
    			}
    		}
    		else if ("debit".equals(attrib)) {
    			try {
    				columnDebit = (short) Integer.parseInt(value);
    			}
    			catch(NumberFormatException NFEe) {}
    		}
    		else if ("credit".equals(attrib)) {
    			try {
    				columnCredit = (short) Integer.parseInt(value);
    			}
    			catch(NumberFormatException NFEe) {}
    		}
    		else if ("amount".equals(attrib)) {
    			try {
    				columnAmount = (short) Integer.parseInt(value);
    			}
    			catch(NumberFormatException NFEe) {}
    		}
    		else if ("idprodserv".equals(attrib)) {
    			try {
    				columnIdProdServ = (short) Integer.parseInt(value);
    			}
    			catch(NumberFormatException NFEe) {}
    		}
    		else if ("idwarehouse".equals(attrib)) {
    			try {
    				columnIdWareHouse = (short) Integer.parseInt(value);
    			}
    			catch(NumberFormatException NFEe) {}
    		}
    		else if ("columnValue".equals(attrib)) {
    			try {
    				columnValue = (short) Integer.parseInt(value);
    			}
    			catch(NumberFormatException NFEe) {}
    		}
    		else if ("valideEnabledCol".equals(attrib)) {
   				valideEnabledCol = value;
    		}
    		
    	}

    	if (type.equals("INT") || type.equals("INTEGER")) { 
    		if (defaultValue!=null) {
    			try {
    				defaultValue = new Integer(tmpDefaultValue);
    			}
    			catch(NumberFormatException NFEe) {
    				defaultValue = new Integer(0);

    			}
    			catch(NullPointerException NPEe) {
    				defaultValue = new Integer(0);
    			}
    		}
    		else {
    			defaultValue = new Integer(0);
    		}

    	} else  if (type.equals("DECIMAL")) {
    		if (defaultValue!=null) {
    			try {
    				defaultValue = new BigDecimal(tmpDefaultValue).setScale(decimals);
    			}
    			catch(NumberFormatException NFEe) {
    				defaultValue = bd.setScale(decimals);
    			}
    			catch(NullPointerException NPEe) {
    				defaultValue = bd.setScale(decimals);
    			}
    		}
    		else {
    			defaultValue = bd.setScale(decimals);

    		}
    	}
    }

    public int getLengthCol() {
        return lengthCol;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }

    public int getOrderQuery() {
        return orderQuery;
    }
    
    public int getOrderReturn() {
        return orderReturn;
    }
    
    public int orderReturn() {
        return orderReturn;
    }
    
    public boolean isEditable() {
        return editable;
    }
    
    public Object getTypeDate() {
        return typeDate;
    }
    public boolean isClean() {
        return clean;
    }

	public Number getDefaultValue() {
		return defaultValue;
	}

	public String getSqlCombo() {
		return sqlCombo;
	}

	public String getKeyDataSearch() {
		return keyDataSearch;
	}
	
	public int getRepeatData() {
		return repeatData;
	}

	public boolean isExporValue() {
		if (exportValue!=null) {
			return true;
		}
		else {
			return false;
		}
			
	}
	
	public boolean isExporValueCombo() {
		if (exportValueCombo!=null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public String getExportValue() {
		return exportValue;
	}
	
	public boolean isImportValues() {
		if(importValues.size()>0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isImportValueCombo() {
		if(importValueCombo.size()>0) {
			return true;
		}
		else {
			return false;
		}
	}

	public String [] getImports() {
		String [] argsret = new String[importValues.size()];
		for (int i=0; i< argsret.length;i++) {
			argsret[i]=importValues.get(i);
		}
		return argsret;
	}

	public String [] getImportCombos() {
		String [] argsret = new String[importValueCombo.size()];
		
		for (int i=0; i< argsret.length;i++) {
			argsret[i]=importValueCombo.get(i);
		}
		return argsret;
	}

	public int getDecimals() {
		return decimals;
	}

	public String getNameField() {
		return nameField;
	}

	public void setNameField(String nameField) {
		this.nameField = nameField;
	}

	public String getExportValueCombo() {
		return exportValueCombo;
	}

	public Vector getImportValueCombo() {
		return importValueCombo;
	}

	public boolean isReturnBlankCol() {
		return returnBlankCol;
	}

	public boolean isReturnNullCol() {
		return returnNullCol;
	}

	public boolean isPrintable() {
		return printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

	public Class getColumnClass() {
		return ColumnClass;
	}

	public int getSelected() {
		return selected;
	}

	public boolean isBlankArgs() {
		return blankArgs;
	}

	public boolean isSpecializedCellEditable() {
		return specializedCellEditable;
	}
	
	public Element getElement() {
		return args;
	}

	public short getColumnAmount() {
		return columnAmount;
	}

	public short getColumnCredit() {
		return columnCredit;
	}

	public short getColumnDebit() {
		return columnDebit;
	}
	
	public short getColumnIdProdServ() {
		return columnIdProdServ;
	}

	public void setColumnIdProdServ(short columnIdProdServ) {
		this.columnIdProdServ = columnIdProdServ;
	}
	
	public short getColumnIdWareHouse() {
		return columnIdWareHouse;
	}

	public void setColumnIdWareHouse(short columnIdWareHouse) {
		this.columnIdWareHouse = columnIdWareHouse;
	}
	
	public int size() {
		return size;
	}

	public short getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(short columnValue) {
		this.columnValue = columnValue;
	}

	public String getValideEnabledCol() {
		return valideEnabledCol;
	}

	public void setValideEnabledCol(String valideEnabledCol) {
		this.valideEnabledCol = valideEnabledCol;
	}
	
	public boolean isValideEnabledCol() {
		if (valideEnabledCol==null) {
			return false;
		}
		return true;
	}

	public Double getMinValue() {
		return minValue;
	}

	public boolean isEnabledTextField() {
		return enabledTextField;
	}

	public void setEnabledTextField(boolean enabledTextField) {
		this.enabledTextField = enabledTextField;
	}
}
