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
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class PostScriptManager extends IManager {
	
	private BufferedImage bufferedImage;
	private Graphics2D g2d;
	
	public PostScriptManager (String template,String transaction) {
		try {
			SAXBuilder builder = new SAXBuilder(false);
			document_template = builder.build(template);
			document_transaction = builder.build(transaction);
			Element root_template = document_template.getRootElement();
			Element root_transaction = document_transaction.getRootElement();
			
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
			ImageIO.write(bufferedImage, "png", out);
			in = new ByteArrayInputStream(out.toByteArray());
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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