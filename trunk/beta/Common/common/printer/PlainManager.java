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

public class PlainManager extends AbstractManager {
	
	private TextGenerator textGenerator = new TextGenerator();
	
	public PlainManager(Element rootTemplate,Element rootTransact) {
		try {
			Calendar calendar = Calendar.getInstance();
			long init = calendar.getTimeInMillis();
			
			/*Element settings = rootTemplate.getChild("settings");
			
			super.width  = settings.getAttribute("width").getIntValue();
			super.height = settings.getAttribute("height").getIntValue();*/
			
			processMetadata(rootTemplate.getChild("metadata"));
			
			Iterator itTemplate = rootTemplate.getChildren("package").iterator();
			Iterator itTransact = rootTransact.getChildren("package").iterator();
			
			while(itTemplate.hasNext() && itTransact.hasNext()) {
				processElement((Element)itTemplate.next(),(Element)itTransact.next());
			}
			calendar = Calendar.getInstance();
			long end = calendar.getTimeInMillis();
			
			super.in = textGenerator.getStream();
			System.out.println("Generador en " + (end-init) + " milisegundos ");
			
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
			
			int row =  attribs.get("row").getIntValue();
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
			}
			else if ("field".equals(name)) {
				String value = e.getTextTrim();
				value = " ".equals(value) || "".equals(value) ? "  " : value;
				textGenerator.addString(value,row,col,null);
			}
			else if ("abstract".equals(name)) {
				String  value = e.getText();
				int height = attribs.get("height").getIntValue();
				textGenerator.addTextArea(value,row,col,null,height,false);
			}
			else if ("scp".equals(name)) {
				String  value = e.getValue();
				Object[] scpCode = new Object[3];
				scpCode[0] = row;
				scpCode[1] = col;
				scpCode[2] = textGenerator.Convert(value);
				textGenerator.addScpCode(scpCode);
			}
		}
	}
	
	private void processElement(Element pack_template, Element pack_transaction) throws DataConversionException {
		Iterator it_template = pack_template.getChildren().iterator();
		Iterator it_transaction = pack_transaction.getChildren().iterator();
		while(it_template.hasNext() && it_transaction.hasNext()) {
			
			Element el_template = (Element)it_template.next();
			if (el_template.getName().equals("subpackage")) {
				int rowInit = el_template.getAttribute("rowInit").getIntValue();
				Iterator it = el_template.getChildren().iterator();
				
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
				
				int i=0;
				while (it_transaction.hasNext()) {
					Element element = (Element) it_transaction.next();
					Iterator iterator = element.getChildren().iterator();
					i=0;
					while(iterator.hasNext()) {
						Element elmt = (Element) iterator.next();
						Attribute att = new Attribute("row",String.valueOf(rowInit));
						AttCols.get(i).put("row",att);
						addValue(elmt.getValue(),AttCols.get(i));
						i++;
					}
					i=0;
					rowInit++;	
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
		
		String type = attribs.get("type").getValue();

		value = !"NULL".equals(value) && !"".equals(value) ?value:"";
		if ("TEXT".equals(type)) {
			int width = attribs.get("width").getIntValue();
			int height = attribs.get("height").getIntValue();
			textGenerator.addTextArea(value,row,col,width,height,true);
		}
		else if ("STRING".equals(type)) {
			value = " ".equals(value) || "".equals(value) ? "   " : value.trim();
			textGenerator.addString(value,row,col,null);
		}
		else if ("NUMERIC".equals(type)) {
			String mask = attribs.get("mask").getValue();
			NumberFormat formatter = new DecimalFormat(mask);
			value = !"NULL".equals(value) && !"".equals(value) ? formatter.format(Double.parseDouble(value)):"";
			textGenerator.addString(value,row,col,attribs.get("width").getIntValue());
		}
	}
	
	public String getBufferString() {
		return textGenerator.getBufferString();
	}
	
	public String toString() {
		return textGenerator.getBufferString();
	}
}
