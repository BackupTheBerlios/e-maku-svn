package common.printer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;

public class PlainManager extends AbstractManager {
	
	private TextGenerator textGenerator;
	
	public PlainManager(Document template,Document transaction) {

		textGenerator = new TextGenerator();
		Element root_template = template.getRootElement();
		Element root_transaction = transaction.getRootElement();
		
		Element settings = root_template.getChild("settings");
		Iterator elements = root_template.getChildren().iterator();
		
		super.width = Integer.parseInt(settings.getAttributeValue("width"));
		super.height = Integer.parseInt(settings.getAttributeValue("height"));
		elements.next();
		Iterator it = root_transaction.getChildren("package").iterator();
		
		while(elements.hasNext()) {
			Element pack_template = (Element) elements.next();
			Element pack_transaction = (Element) it.next();
			processElement(pack_template,pack_transaction);
		}
		super.in = textGenerator.getStream();
	}
	
	public void processElement(Element pack_template, Element pack_transaction) {
		Iterator it_template = pack_template.getChildren().iterator();
		Iterator it_transaction = pack_transaction.getChildren().iterator();
		while(it_template.hasNext() && it_transaction.hasNext()) {
			try {
				Element el_template = (Element)it_template.next();
				if (el_template.getName().equals("labels")) {
					
					Iterator it = el_template.getChildren().iterator();
					while (it.hasNext()) {
						Element el = (Element) it.next();
						
						int row = el.getAttribute("row").getIntValue();
						int col = el.getAttribute("col").getIntValue();
						String value = el.getValue();
						value = !"NULL".equals(value) && !"".equals(value) ?value:"";
						textGenerator.addString(value,row,col,null);
					}
				}
				else if (el_template.getName().equals("subpackage")) {
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
					int row = el_template.getAttribute("row").getIntValue();
					int col = el_template.getAttribute("col").getIntValue();
					String type = el_template.getAttributeValue("type");
					String value = el_transaction.getValue();
					value = !"NULL".equals(value) && !"".equals(value) ?value:"";
					if ("TEXT".equals(type)) {
						int width = el_template.getAttribute("width").getIntValue();
						int height = el_template.getAttribute("height").getIntValue();
						textGenerator.addTextArea(value,row,col,width,height);
					}
					else if ("STRING".equals(type)) {
						textGenerator.addString(value,row,col,null);
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
}
