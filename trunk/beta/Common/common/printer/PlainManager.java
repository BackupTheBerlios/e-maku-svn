package common.printer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

import common.control.ClientHeaderValidator;
import common.control.SuccessEvent;
import common.control.SuccessListener;
import common.misc.text.NumberToLetterConversor;

public class PlainManager extends AbstractManager implements SuccessListener {
	
	private TextGenerator textGenerator = new TextGenerator();
	private HashMap<Integer,String[]> concatData = new HashMap<Integer, String[]>(); 
	private int currentRow = 1;
	private String ndocument = "";
	private boolean sucess = false;
	
	public PlainManager() {
		ClientHeaderValidator.addSuccessListener(this);
	}
	
	public void process(Element rootTemplate,Element rootTransact) {
		try {
			Attribute ATTRequesNumeration = rootTemplate.getAttribute("requestNumeration");
			if (ATTRequesNumeration!=null && ATTRequesNumeration.getBooleanValue()) {
				int times = 0;
				while (!sucess) {
					try {
						if (times<=30) {
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
			
			
			processMetadata(rootTemplate.getChild("metadata"));
			
			Iterator itTemplate = rootTemplate.getChildren("package").iterator();
			Iterator itTransact = rootTransact.getChildren("package").iterator();
			int countPacks = 0;
			while(itTemplate.hasNext() && itTransact.hasNext()) {
				Element elmTemplate = (Element)itTemplate.next();
				Element elmTransact = (Element)itTransact.next();
				Attribute attr = elmTemplate.getAttribute("validate");
				countPacks += elmTransact.getChildren().size();
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
			if ( countPacks > 0 ) {
				super.in = textGenerator.getStream();
				sussceful = true;
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
			String name = e.getName();
			Iterator itAttribs = e.getAttributes().iterator();
			HashMap<String,Attribute> attribs = new HashMap<String,Attribute>();
			
			while(itAttribs.hasNext()) {
				Attribute attribute = (Attribute) itAttribs.next();
				attribs.put(attribute.getName(),attribute);
			}
			Attribute attr = attribs.get("row");
			int row = -1;
			boolean isValidate = false;
			boolean passed = false;
			if (attr.getValue().equals("last")) {
				row = currentRow;
				isValidate = true;
			}
			else {
				row = attr.getIntValue();
			}
			int col =  attribs.get("col").getIntValue();
			
			if ("line".equals(name)) {
				int length		= attribs.get("length"  ).getIntValue();
				String charfill = attribs.get("charfill").getValue();
				String align	= attribs.get("align"   ).getValue();
				for (int i=0;i < length ; i++) {
					if ("horizontal".equals(align)) {
						textGenerator.addString(charfill,row,col++,null);
					}
					else if ("vertical".equals(align)) {
						textGenerator.addString(charfill,row++,col,null);
					}
				}
				passed = true;
			}
			else if ("field".equals(name)) {
				String value = e.getTextTrim();
				value = " ".equals(value) || "".equals(value) ? "  " : value;
				textGenerator.addString(value,row,col,null);
				passed = true;

			}
			else if ("ndocument".equals(name)) {
				String value = ndocument;
				textGenerator.addString(value,row,col,null);
				passed = true;
			}
			else if ("abstract".equals(name)) {
				String  value = e.getText();
				int height = attribs.get("height").getIntValue();
				textGenerator.addTextArea(value,row,col,null,height,false);
				passed = true;
			}
			else if ("scp".equals(name)) {
				String  value = e.getValue();
				Object[] scpCode = new Object[3];
				scpCode[0] = row;
				scpCode[1] = col;
				scpCode[2] = textGenerator.Convert(value);
				textGenerator.addScpCode(scpCode);
				passed = true;
			}
			if (isValidate && passed) {
				Attribute incrementRow = attribs.get("incrementRow");
				if (incrementRow== null || incrementRow.getBooleanValue()) {
					currentRow++;
				}
			}
		}
	}
	
	private void processElement(Element pack_template, Element pack_transaction) throws DataConversionException {

		Iterator it_template = pack_template.getChildren().iterator();
		Iterator it_transaction = pack_transaction.getChildren().iterator();
		while(it_template.hasNext() && it_transaction.hasNext()) {
			
			Element el_template = (Element)it_template.next();
			if (el_template.getName().equals("subpackage")) {
				Attribute attr = el_template.getAttribute("rowInit");
				int rowInit = -1;
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
				}
				if (isValidate) {
					currentRow = rowInit + 1;
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
				addValue(el_transaction.getValue(),attribs);
			}
		}
	}
	
	private void addValue(String value,HashMap<String,Attribute> attribs) throws DataConversionException {
		
		int row =  attribs.get("row").getIntValue();
		int col =  attribs.get("col").getIntValue();
		
		Attribute attribute = attribs.get("type");
		String type = attribute!=null ? attribute.getValue() : null ;
		value = !"NULL".equals(value) && !"".equals(value) ?value:"";
		if ("TEXT".equals(type)) {
			int width = attribs.get("width").getIntValue();
			int height = attribs.get("height").getIntValue();
			textGenerator.addTextArea(value,row,col,width,height,true);
		}
		else if ("STRING".equals(type)) {
			/*value = " ".equals(value) || "".equals(value) ? "   " : value.trim();
			textGenerator.addString(value,row,col,null);*/
			if (!"".equals(value.trim())) {
				textGenerator.addString(value,row,col,null);
			}
		}
		else if ("STRINGCONCAT".equals(type)) {
			if (concatData.containsKey(row)) {
				String[] acumString = concatData.get(row);
				if (acumString[0].equals(attribs.get("link").getValue())) {
					String addVal = attribs.get("char").getValue()+value;
					textGenerator.addString(addVal,row,acumString[1].length()+2,null);
					acumString[1] +=  addVal;
					concatData.put(row,acumString);
				}
			}
			else {
				textGenerator.addString(value,row,col,null);
				concatData.put(row, new String[]{String.valueOf(col),value});
			}
		}
		else if ("NUMERIC".equals(type)) {
			String mask = attribs.get("mask").getValue();
			NumberFormat formatter = new DecimalFormat(mask);
			value = !"NULL".equals(value) && !"".equals(value) ? formatter.format(Double.parseDouble(value)):"";
			textGenerator.addString(value,row,col,attribs.get("width").getIntValue());
		}
		else if ("ABSTRACT".equals(type)) {
			int height = attribs.get("height").getIntValue();
			textGenerator.addTextArea(value,row,col,null,height,false);
		}
        else if ("NUMTOLETTERS".equals(type)) {
            try {
            	int width = attribs.get("width").getIntValue();
    			int height = attribs.get("height").getIntValue();
    			
                Double d = Double.parseDouble(value);
                String letters = String.valueOf(d.intValue());
                letters = NumberToLetterConversor.letters(letters, null);
                
                textGenerator.addTextArea(letters,row,col,width,height,true);
            } catch (NumberFormatException NFE) {
                // Pendiente por traducir
                System.out.printf("No se puede convertir %s  a letras\n%s",value,NFE.getMessage());
            }
        }
	}
	
	public String getBufferString() {
		return textGenerator.getBufferString();
	}
	
	public String toString() {
		return textGenerator.getBufferString();
	}

	public synchronized void cathSuccesEvent(SuccessEvent e) {
		String numeration = e.getNdocument();
		if (numeration!=null && !"".equals(numeration)) {
			sucess = true;
			ndocument = numeration;
		}
	}
}
