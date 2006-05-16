package common.printer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;


public class PostScriptManager extends AbstractManager {
	
	private BufferedImage bufferedImage;
	private Graphics2D g2d;
	
	public PostScriptManager (Document template,Document transaction) {
		Element root_template = template.getRootElement();
		Element root_transaction = transaction.getRootElement();
		
		Element settings = root_template.getChild("settings");
		Iterator elements = root_template.getChildren().iterator();
		
		width = Integer.parseInt(settings.getAttributeValue("width"));
		height = Integer.parseInt(settings.getAttributeValue("height"));
		bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		g2d = bufferedImage.createGraphics();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.white);
		g2d.fillRect(0,0,width,height);
	    
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT,new File("DejaVuSansMono.ttf"));
			font = font.deriveFont(Font.PLAIN,10);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        g2d.setFont(font);
        g2d.setColor(Color.black);
        
		elements.next();
		Iterator it = root_transaction.getChildren("package").iterator();
		while(elements.hasNext()) {
			Element pack_template = (Element) elements.next();
			Element pack_transaction = (Element) it.next();
			processElement(pack_template,pack_transaction);
		}
		
		g2d.dispose();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferedImage, "png", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		in = new ByteArrayInputStream(out.toByteArray());

	}

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
						g2d.drawString(value,row,col);
					}
				}
				else if (el_template.getName().equals("subpackage")) {
					int rowInit = el_template.getAttribute("rowInit").getIntValue();
					Iterator it = el_template.getChildren().iterator();
					HashMap<String,String> args = new HashMap<String,String>();
					int i=0;
					while (it.hasNext()) {
						Element element = (Element) it.next();
						args.put("width"+i,element.getAttribute("width").getValue());
						args.put("col"+i,element.getAttribute("col").getValue());
						args.put("type"+i,element.getAttribute("type").getValue());
						args.put("mask"+i,element.getAttribute("mask").getValue());
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
							} else if ("STRING".equals(args.get("type"+i))) {
								value = !"NULL".equals(value) && !"".equals(value) ?value:"";
							}
							g2d.drawString(value,rowInit,Integer.parseInt(args.get("col"+i)));
							i++;
						}
						i=0;
						/* Aqui que pendiente segun la plantilla habra un etiqueta
						   con un factor de acumulacion. */
						rowInit+=10;	
					}
				}
				else {
					Element el_transaction = (Element)it_transaction.next();
					int row = el_template.getAttribute("row").getIntValue();
					int col = el_template.getAttribute("col").getIntValue();
					String value = el_transaction.getValue();
					value = !"NULL".equals(value) && !"".equals(value) ?value:"";
					g2d.drawString(value,row,col);
				}
			} catch (DataConversionException e) {
				e.printStackTrace();
			}
		}
	}
}