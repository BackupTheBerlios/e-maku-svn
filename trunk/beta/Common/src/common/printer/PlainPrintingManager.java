package common.printer;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

import common.control.ClientHeaderValidator;
import common.control.SuccessEvent;
import common.control.SuccessListener;
import common.misc.text.NumberToLetterConversor;
import common.printer.PrintingManager.ImpresionType;

/**
 * Esta clase se encarga de manejar las plantillas de impresion de documentos
 * planos.
 */
public class PlainPrintingManager implements AbstractManager ,SuccessListener{
	
	private TextPrinterBuffer textPrinterBuffer = new TextPrinterBuffer();
	private HashMap<Integer,String[]> concatData = new HashMap<Integer, String[]>(); 
	private int currentRow = 1;
	//private String ndocument = "";
	private boolean success = false;
	private String idTransaction="";
	private HashMap<String,String> vars = new HashMap<String, String>();
	/*private int width;
	private int height;*/
	private ByteArrayInputStream in;
	private boolean successful;
	private ImpresionType impresionType= ImpresionType.PLAIN;
	private boolean withHeader;
	private Element header;
	private int rowPageSeparator;
	private int pageNumeration = 1;
	
	public PlainPrintingManager() {
		ClientHeaderValidator.addSuccessListener(this);
		textPrinterBuffer.clear();
	}
	
	public PlainPrintingManager(String ndocument) {
		this();
		impresionType = ImpresionType.PLAIN;
		vars.put("ndocument",ndocument);
		//this.ndocument = ndocument;
		textPrinterBuffer.clear();
	}
	
	public void process(Element rootTemplate,Element rootTransact) {
		textPrinterBuffer.clear();
		try {
			Attribute ATTRequesNumeration = rootTemplate.getAttribute("requestNumeration");
			if (ATTRequesNumeration!=null && ATTRequesNumeration.getBooleanValue()) {
				int times = 0;
				while (!success) {
					try {
						if (times<=100) {
							Thread.sleep(100);
						}
						else {
							System.out.println("No se pudo obtner la numeracion de " + rootTemplate.getAttributeValue("name"));
							return;
						}
						times++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			Calendar calendar = Calendar.getInstance();
			long init = calendar.getTimeInMillis();
			/*Element settings = rootTemplate.getChild("settings");
			
			super.width  = settings.getAttribute("width").getIntValue();
			super.height = settings.getAttribute("height").getIntValue();*/
			Iterator itTemplate = rootTemplate.getChildren().iterator();
			Iterator itTransact = rootTransact.getChildren("package").iterator();
			int countPacks = 0;
			while(itTemplate.hasNext()) {
				Element elmTemplate = (Element)itTemplate.next();
				String name = elmTemplate.getName();
				if ("metadata".equals(name)) {
					processMetadata(elmTemplate);	
				}
				else if ("package".equals(name) && itTransact.hasNext()) {
					Element elmTransact = (Element)itTransact.next();
					Attribute attr = elmTemplate.getAttribute("validate");
					Attribute attrInv = elmTemplate.getAttribute("invalidate");
					if (attrInv==null || (attrInv!=null && !attrInv.getBooleanValue()) ) {
						countPacks += elmTransact.getChildren().size();
					}
					boolean validate = attr != null ? attr.getBooleanValue() : false;
					if (validate) {
						if (elmTransact.getChildren().size() > 0) {
							processElement(elmTemplate,elmTransact);
						}
					}
					else {
						processElement(elmTemplate,elmTransact);
					}
				}
				else {
					processSingleTag(elmTemplate);
				}
			}
			if ( countPacks > 0 ) {
				this.in = textPrinterBuffer.getStream();
				this.successful = true;
				calendar = Calendar.getInstance();
				long end = calendar.getTimeInMillis();
				System.out.println("Generador en " + (end-init) + " milisegundos ");
			}
		}
		catch (DataConversionException e) {
			e.printStackTrace();
		}
	}
	
	private void processMetadata(Element element) throws DataConversionException {
		Iterator it = element.getChildren().iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			processSingleTag(e);
		}
	}
	private void processSingleTag(Element e) throws DataConversionException {
		String name = e.getName();
		Iterator itAttribs = e.getAttributes().iterator();
		HashMap<String,Attribute> attribs = new HashMap<String,Attribute>();
		
		while(itAttribs.hasNext()) {
			Attribute attribute = (Attribute) itAttribs.next();
			attribs.put(attribute.getName(),attribute);
		}
		Attribute attrRow = attribs.get("row");
		Attribute attrCol = attribs.get("col");
		int row = -1;
		boolean isValidate = false;
		boolean passed = false;
		if (attrRow!=null) {
			if (attrRow.getValue().equals("last")) {
				row = currentRow;
				isValidate = true;
			}
			else {
				row = attrRow.getIntValue();
			}
		}
		if (withHeader) {
			row = currentRow;
			isValidate = true;
		}
		int col =  attrCol!=null? attrCol.getIntValue() : -1;
		
		if ("line".equals(name)) {
			int length		= attribs.get("length"  ).getIntValue();
			String charfill = attribs.get("charfill").getValue();
			String align	= attribs.get("align"   ).getValue();
			for (int i=0;i < length ; i++) {
				if ("horizontal".equals(align)) {
					textPrinterBuffer.insertString(charfill,row,col++,null);
				}
				else if ("vertical".equals(align)) {
					textPrinterBuffer.insertString(charfill,row++,col,null);
				}
			}
			passed = true;
		}
		else if ("field".equals(name)) {
			String value = e.getTextTrim();
			value = " ".equals(value) || "".equals(value) ? "  " : value;
			textPrinterBuffer.insertString(value,row,col,null);
			passed = true;

		}
		else if ("ndocument".equals(name)) {
			String value = vars.get(name);
			textPrinterBuffer.insertString(value,row,col,null);
			passed = true;
		}
		else if ("abstract".equals(name)) {
			String  value = e.getText();
			int height = attribs.get("height").getIntValue();
			textPrinterBuffer.insertTextArea(value,row,col,null,height,false);
			passed = true;
		}
		else if ("scp".equals(name)) {
			String  value = e.getValue();
			textPrinterBuffer.insertString( textPrinterBuffer.Convert(value),row,col,null);
			passed = true;
		}
		else  if ("header".equals(name)) {
			withHeader = true;
			header = (Element) e.clone();
			rowPageSeparator =  attribs.get("repeatEach").getIntValue();
			passed = true;
		}
		else  if ("var".equals(name)) {
			String varName = attribs.get("name").getValue();
			String value = vars.get(varName);
			textPrinterBuffer.insertString(value,row,col,null);
			passed = true;
		}
		else  if ("pageNumeration".equals(name)) {
			textPrinterBuffer.insertString(String.valueOf(pageNumeration),row,col,null);
			passed = true;
		}
		if (isValidate && passed) {
			Attribute incrementRow = attribs.get("incrementRow");
			if (incrementRow== null || incrementRow.getBooleanValue()) {
				currentRow++;
			}
		}
	}
	private void processElement(Element pack_template, Element pack_transaction) throws DataConversionException {

		Iterator it_template = pack_template.getChildren().iterator();
		Iterator it_transaction = pack_transaction.getChildren().iterator();
		while(it_template.hasNext() && it_transaction.hasNext()) {
			int rowInit = -1;
			Element el_template = (Element)it_template.next();
			if (el_template.getName().equals("subpackage")) {
				Attribute attr = el_template.getAttribute("rowInit");
				boolean isValidate = false;
				if (attr.getValue().equals("last")) {
					rowInit = currentRow;
					isValidate = true;
				}
				else {
					rowInit = attr.getIntValue();
				}
				
				Iterator it = el_template.getChildren("metadata").iterator();
				while (it.hasNext()) { 
					Element element = (Element) it.next();
					processMetadata(element);
					rowInit = currentRow;
				}
				
				it = el_template.getChildren("field").iterator();
				ArrayList<HashMap<String,Attribute>> AttCols = new ArrayList<HashMap<String,Attribute>>(); 
				while (it.hasNext()) {
					HashMap<String,Attribute> attribs = new HashMap<String,Attribute>();
					Element element = (Element) it.next();
					Iterator itAttribs = element.getAttributes().iterator();
					while(itAttribs.hasNext()) {
						Attribute attribute = (Attribute) itAttribs.next();
						attribs.put(attribute.getName(),attribute);
					}
					AttCols.add(attribs);
				}
				
				while (it_transaction.hasNext()) {
					Element element = (Element) it_transaction.next();
					Iterator iterator = element.getChildren().iterator();
					int i=0;
					while(iterator.hasNext()) {
						Element elmt = (Element) iterator.next();
						Attribute att = new Attribute("row",String.valueOf(rowInit));
						AttCols.get(i).put("row",att);
						addValue(elmt.getValue(),AttCols.get(i));
						i++;
					}
					rowInit++;
					if (isValidate) {
						currentRow = rowInit + 1;
					}
					if (withHeader) {
						if ((currentRow%rowPageSeparator)==0) {
							pageNumeration ++;
							Element ff = new Element("scp");
							ff.setText("FF");
							ff.setAttribute("row","last");
							ff.setAttribute("col","2");
							processSingleTag(ff);
							processMetadata(header);
							rowInit = currentRow;
						}
					}
				}
			}
			else {
				Element el_transaction = (Element)it_transaction.next();

				Iterator itAttribs = el_template.getAttributes().iterator();
				HashMap<String,Attribute> attribs = new HashMap<String,Attribute>();
				
				while(itAttribs.hasNext()) {
					Attribute attribute = (Attribute) itAttribs.next();
					attribs.put(attribute.getName(),attribute);
				}
				if (withHeader) {
					if ((currentRow%rowPageSeparator)==0) {
						pageNumeration ++;
						Element ff = new Element("scp");
						ff.setText("FF");
						ff.setAttribute("row","last");
						ff.setAttribute("col","2");
						processSingleTag(ff);
						processMetadata(header);
						rowInit = currentRow;
					}
				}
				addValue(el_transaction.getValue(),attribs);
			}
			/*if (currentRow<rowInit)
				currentRow = rowInit;
			else
				currentRow++;*/
		}
	}
	
	private void addValue(String value,HashMap<String,Attribute> attribs) throws DataConversionException {
		
		int row = -1; 
		
		try {
			row=  attribs.get("row").getIntValue();
		}
		catch(DataConversionException e) {
			if (attribs.get("row").getValue().equals("last")) {
				row=  currentRow;
			}
			else {
				row= currentRow--;
			}
		}
		
		int col =  attribs.get("col").getIntValue();
		
		Attribute attrIncrement = attribs.get("incrementRow");
		boolean passed = false;
		Attribute attribute = attribs.get("type");
		String type = attribute!=null ? attribute.getValue() : null ;
		value = !"NULL".equals(value) && !"".equals(value) ?value:"";
		if ("TEXT".equals(type)) {
			int width = attribs.get("width").getIntValue();
			int height = attribs.get("height").getIntValue();
			textPrinterBuffer.insertTextArea(value,row,col,width,height,true);
			passed = true;
		}
		else if ("STRING".equals(type)) {
			/*value = " ".equals(value) || "".equals(value) ? "   " : value.trim();
			textPrinterBuffer.addString(value,row,col,null);*/
			if (!"".equals(value.trim())) {
				textPrinterBuffer.insertString(value,row,col,null);
				if (attribs.containsKey("var")) {
					String var = vars.get("var");
					vars.put(var,value);
				}
			}
			if (attribs.containsKey("separatorchar")){
				Attribute att = attribs.get("separatorchar");
				int colCharSeparator = attribs.get("separatorcol").getIntValue();
				textPrinterBuffer.insertString(att.getValue(),row,colCharSeparator,null);
			}
			passed = true;
		}
		else if ("DATE".equals(type)) {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = sdf.parse(value);
				String mask = attribs.get("mask").getValue();
				sdf.applyLocalizedPattern(mask);
				value = sdf.format(date);
			} catch (ParseException e) {
				try {
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = sdf.parse(value);
					String mask = attribs.get("mask").getValue();
					sdf.applyLocalizedPattern(mask);
					value = sdf.format(date);
				}
				catch(ParseException e1) {
					e1.printStackTrace();
				}
			}
			/*value = " ".equals(value) || "".equals(value) ? "   " : value.trim();
			textPrinterBuffer.addString(value,row,col,null);*/
			if (!"".equals(value.trim())) {
				textPrinterBuffer.insertString(value,row,col,null);
			}
			if (attribs.containsKey("separatorchar")){
				Attribute att = attribs.get("separatorchar");
				int colCharSeparator = attribs.get("separatorcol").getIntValue();
				textPrinterBuffer.insertString(att.getValue(),row,colCharSeparator,null);
			}
			passed = true;
		}
		else if ("STRINGCONCAT".equals(type)) {
			if (concatData.containsKey(row)) {
				String[] acumString = concatData.get(row);
				if (acumString[0].equals(attribs.get("link").getValue())) {
					String addVal = attribs.get("char").getValue()+value;
					textPrinterBuffer.insertString(addVal,row,acumString[1].length()+2,null);
					acumString[1] +=  addVal;
					concatData.put(row,acumString);
				}
			}
			else {
				textPrinterBuffer.insertString(value,row,col,null);
				concatData.put(row, new String[]{String.valueOf(col),value});
			}
			passed = true;
		}
		else if ("NUMERIC".equals(type)) {
			String mask = attribs.get("mask").getValue();
			NumberFormat formatter = new DecimalFormat(mask);
			value = !"NULL".equals(value) && !"".equals(value) ? formatter.format(Double.parseDouble(value)):"";
			textPrinterBuffer.insertString(value,row,col,attribs.get("width").getIntValue());
			if (attribs.containsKey("var")) {
				String var = vars.get("var");
				vars.put(var,value);
			}
			if (attribs.containsKey("separatorchar")){
				Attribute att = attribs.get("separatorchar");
				int colCharSeparator = attribs.get("separatorcol").getIntValue();
				textPrinterBuffer.insertString(att.getValue(),row,colCharSeparator,null);
			}
			passed = true;
		}
		else if ("ABSTRACT".equals(type)) {
			int height = attribs.get("height").getIntValue();
			textPrinterBuffer.insertTextArea(value,row,col,null,height,false);
		}
        else if ("NUMTOLETTERS".equals(type)) {
            try {
            	int width = attribs.get("width").getIntValue();
    			int height = attribs.get("height").getIntValue();
    			
                Double d = Double.parseDouble(value);
                String letters = String.valueOf(d.intValue());
                letters = NumberToLetterConversor.letters(letters, null);
                textPrinterBuffer.insertTextArea(letters,row,col,width,height,true);
                if (attrIncrement!=null && attrIncrement.getBooleanValue()) {
                	currentRow+=height;
                }
            } catch (NumberFormatException NFE) {
                // Pendiente por traducir
                System.out.printf("No se puede convertir %s  a letras\n%s",value,NFE.getMessage());
            }
        }
		if (passed && attrIncrement!=null && attrIncrement.getBooleanValue()) {
			currentRow++;
		} 
	}
	
	public String getBufferString() {
		return textPrinterBuffer.getBufferString();
	}
	
	public String toString() {
		return textPrinterBuffer.getBufferString();
	}

	public synchronized void cathSuccesEvent(SuccessEvent e) {
		String numeration = e.getNdocument();
		
		if (numeration!=null && !"".equals(numeration) && idTransaction.equals(e.getIdPackage())) {
			success = true;
			vars.put("ndocument",numeration);
		}
	}

	public ImpresionType getImpresionType() {
		return this.impresionType;
	}

	public ByteArrayInputStream getStream() {
		return this.in;
	}

	public boolean isSuccessful() {
		return this.successful;
	}

	public String getNdocument() {
		return vars.get("ndocument");
	}

	public void setIdTransaction(String idTransaction) {
		this.idTransaction = idTransaction;
	}
}
