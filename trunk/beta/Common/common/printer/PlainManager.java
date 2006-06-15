package common.printer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

public class PlainManager extends AbstractManager {
	
	private TextGenerator textGenerator = new TextGenerator();
	
	public PlainManager(Element rootTemplate,Element rootTransact) {
		try {
 
			Element settings = rootTemplate.getChild("settings");
			
			super.width  = settings.getAttribute("width").getIntValue();
			super.height = settings.getAttribute("height").getIntValue();
			
			processElement(rootTemplate.getChild("metadata"));
			
			Iterator itTemplate = rootTemplate.getChildren("package").iterator();
			Iterator itTransact = rootTransact.getChildren("package").iterator();
			
			while(itTemplate.hasNext() && itTransact.hasNext()) {
				processElement((Element)itTemplate.next(),(Element)itTransact.next());
			}
			super.in = textGenerator.getStream();
			
		}
		catch (DataConversionException e) {
			e.printStackTrace();
		}
	}
	
	private void processElement(Element element) throws DataConversionException {

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
			if ("field".equals(name)) {
				String value = e.getTextTrim();
				System.out.println("field value = {"+value+"}");
				value = " ".equals(value) || "".equals(value) ? "  " : value;
				textGenerator.addString(value,row,col,null);
			}
			if ("abstract".equals(name)) {
				String  value = e.getText();
				int height = attribs.get("height").getIntValue();
				textGenerator.addTextArea(value,row,col,null,height,false);
			}
		}
	}
	
	private void processElement(Element pack_template, Element pack_transaction) {
		Iterator it_template = pack_template.getChildren().iterator();
		Iterator it_transaction = pack_transaction.getChildren().iterator();
		while(it_template.hasNext() && it_transaction.hasNext()) {
			try {
				Element el_template = (Element)it_template.next();
				if (el_template.getName().equals("subpackage")) {
					int rowInit = el_template.getAttribute("rowInit").getIntValue();
					Iterator it = el_template.getChildren().iterator();
					HashMap<String,String> args = new HashMap<String,String>();
					int i=0;
					while (it.hasNext()) {
						Element element = (Element) it.next();

						Iterator attribs = element.getAttributes().iterator();
						while(attribs.hasNext()) {
							Attribute attrib = (Attribute) attribs.next();
							String attribName =attrib.getName();
							args.put(attribName+i,attrib.getValue());
						}
						i++;
					}
					
					while (it_transaction.hasNext()) {
						Element element = (Element) it_transaction.next();
						Iterator iterator = element.getChildren().iterator();
						i=0;
						while(iterator.hasNext()) {
							Element elmt = (Element) iterator.next();
							String value = elmt.getValue();
							if ("NUMERIC".equals(args.get("type"+i))) {
								NumberFormat formatter = new DecimalFormat(args.get("mask"+i));
								value = !"NULL".equals(value) && !"".equals(value) ?
										formatter.format(Double.parseDouble(value)):"";
								textGenerator.addString(
										value,
										rowInit,
										Integer.parseInt(args.get("col"+i)),
										Integer.parseInt(args.get("width"+i)));
							} else if ("STRING".equals(args.get("type"+i))) {
								value = !"NULL".equals(value) && !"".equals(value) ?value:"";
								textGenerator.addString(
										value,
										rowInit,
										Integer.parseInt(args.get("col"+i)),null);
							}
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
					
					int row =  attribs.get("row").getIntValue();
					int col =  attribs.get("col").getIntValue();
					
					String type = attribs.get("type").getValue();
					String value = el_transaction.getValue();
					
					value = !"NULL".equals(value) && !"".equals(value) ?value:"";
					if ("TEXT".equals(type)) {
						int width = el_template.getAttribute("width").getIntValue();
						int height = el_template.getAttribute("height").getIntValue();
						textGenerator.addTextArea(value,row,col,width,height,true);
					}
					else if ("STRING".equals(type)) {
						value = " ".equals(value) || "".equals(value) ? "   " : value.trim();
						textGenerator.addString(value,row,col,null);
					}
					else if ("NUMERIC".equals(type)) {
						String mask = attribs.get("mask").getValue();
						NumberFormat formatter = new DecimalFormat(mask);
						value = !"NULL".equals(value) && !"".equals(value) ? 
								formatter.format(Double.parseDouble(value)):"";
						textGenerator.addString(
								value,
								row,
								col,
								attribs.get("width").getIntValue());
					}
				}
			} catch (DataConversionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getBufferString() {
		return textGenerator.getBufferString();
	}
	
	public String toString() {
		return textGenerator.getBufferString();
	}
}
