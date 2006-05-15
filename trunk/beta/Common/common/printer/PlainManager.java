package common.printer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class PlainManager extends AbstractManager {
	
	private TextGenerator textGenerator;
	private int rowInit;
	
	public PlainManager(String template,String transaction) {
		SAXBuilder builder = new SAXBuilder(false);
		try {
			textGenerator = new TextGenerator();
			document_template = builder.build(template);
			document_transaction = builder.build(transaction);
			Element root_template = document_template.getRootElement();
			Element root_transaction = document_transaction.getRootElement();
			
			Element settings = root_template.getChild("settings");
			Iterator elements = root_template.getChildren().iterator();
			
			width = Integer.parseInt(settings.getAttributeValue("width"));
			height = Integer.parseInt(settings.getAttributeValue("height"));
			elements.next();
			Iterator it = root_transaction.getChildren("package").iterator();
			
			while(elements.hasNext()) {
				Element pack_template = (Element) elements.next();
				Element pack_transaction = (Element) it.next();
				processElement(pack_template,pack_transaction);
			}
			in = textGenerator.getStream();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void processElement(Element pack_template, Element pack_transaction) {
		Iterator it_template = pack_template.getChildren().iterator();
		Iterator it_transaction = pack_transaction.getChildren().iterator();
		while(it_template.hasNext()) {
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
					this.rowInit = el_template.getAttribute("rowInit").getIntValue();
					Iterator it = el_template.getChildren().iterator();
					HashMap<String,Integer> args = new HashMap<String,Integer>();
					int i=0;
					while (it.hasNext()) {
						Element element = (Element) it.next();
						args.put("width"+i,Integer.valueOf(element.getAttribute("width").getValue()));
						args.put("col"+i,Integer.valueOf(element.getAttribute("col").getValue()));
						i++;
					}
					
					while (it_transaction.hasNext()) {
						Element element = (Element) it_transaction.next();
						Iterator iterator = element.getChildren().iterator();
						i=0;
						while(iterator.hasNext()) {
							Element elmt = (Element) iterator.next();
							String value = elmt.getValue();
							value = !"NULL".equals(value) && !"".equals(value) ?value:"";
							textGenerator.addString(
											value,
											this.rowInit,
											args.get("col"+i).intValue(),
											args.get("width"+i));
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
					String value = el_transaction.getValue();
					value = !"NULL".equals(value) && !"".equals(value) ? value: "";
					textGenerator.addString(value,row,col,null);
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
