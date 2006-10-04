package common.printer;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

import common.control.ClientHeaderValidator;
import common.control.SuccessEvent;
import common.control.SuccessListener;
import common.printer.PrintManager.ImpresionType;


public class PostScriptManager implements AbstractManager, SuccessListener, Printable {
	
	private static final long serialVersionUID = 3641816256967941893L;
	private Graphics2D g2d;
	
	private boolean sussceful;
	private ImpresionType impresionType = ImpresionType.POSTSCRIPT;;
	private String ndocument = "";
	private boolean sucess = false;
	private Element rootTemplate;
	private int width;
	private int height;
	private Element rootTransact;
	public void load(Element rootTemplate,Element rootTransact) {
		this.rootTemplate = rootTemplate;
		this.rootTransact = rootTransact;
	}
	 
	public PostScriptManager() {
		ClientHeaderValidator.addSuccessListener(this);
	}

	public void process() {
		try {
			Attribute ATTRequesNumeration = rootTemplate.getAttribute("requestNumeration");
			if (ATTRequesNumeration!=null && ATTRequesNumeration.getBooleanValue()) {
				int times = 0;
				while (!sucess) {
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
			
			processMetadata(rootTemplate.getChild("metadata"));
			Element settings = rootTemplate.getChild("settings");
			
			width  = settings.getAttribute("width").getIntValue();
			height = settings.getAttribute("height").getIntValue();
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
				this.sussceful = true;
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
			int row = attr.getIntValue();
			int col =  attribs.get("col").getIntValue();
			
			if ("line".equals(name)) {
				int row2 = attribs.get("row2").getIntValue();
				int col2 = attribs.get("col2").getIntValue();
				g2d.drawLine(col,row, col2,row2);
			}
			else if ("field".equals(name)) {
				String value = e.getTextTrim();
				value = " ".equals(value) || "".equals(value) ? "  " : value;
				g2d.drawString(value,col,row);
			}
			else if ("ndocument".equals(name)) {
				String value = ndocument;
				g2d.drawString(value,col,row);
			}
			else if ("font".equals(name)) {
				g2d.setFont(new Font(attribs.get("name").getValue(),Font.PLAIN,attribs.get("size").getIntValue()));
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
				int rowAcum = el_template.getAttribute("rowAcum").getIntValue();
				
				Iterator it = el_template.getChildren("field").iterator();
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
					rowInit+=rowAcum;
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
			int rowAcum = attribs.get("rowAcum").getIntValue();
			
			value = value.replaceAll("\n", " ");
			StringBuffer buf = new StringBuffer(value);
			int lastspace = -1;
			int linestart = 0;
			int i = 0;
			while ( i < buf.length()) {
				if (buf.charAt(i) == ' ') {
					lastspace = i;
				}
				if (buf.charAt(i) == '\n') {
					lastspace = -1;
					linestart = i + 1;
				}
				if (i > linestart + width) {
					if (lastspace != -1) {
						buf.delete(lastspace,lastspace+1);
						buf.insert(lastspace,"\n");
						linestart = lastspace;
						lastspace = -1;
						
					} else {
						buf.insert(i, '\n');
						linestart = i + 1;
					}
				}
				i++;
			}
			StringTokenizer st = new StringTokenizer(buf.toString(),"\n");
			for (int j=0; j < height && st.hasMoreElements(); j++) {
				String tok = st.nextToken();
				g2d.drawString(tok,col,row);
				row+=rowAcum;
			}
		}
		else if ("STRING".equals(type)) {
			g2d.drawString(value,col,row);
		}
		else if ("DATE".equals(type)) {
			String mask = attribs.get("mask").getValue();
			SimpleDateFormat sdf = new SimpleDateFormat(mask);
			Calendar c = Calendar.getInstance();
			int year  = Integer.valueOf(value.substring(0,4));
			int month = Integer.valueOf(value.substring(5,7));
			int day   = Integer.valueOf(value.substring(8,10));
			c.set(Calendar.YEAR,year);
			c.set(Calendar.MONTH, (month-1));
			c.set(Calendar.DAY_OF_MONTH,day);
			
			value = sdf.format(c.getTime());
			g2d.drawString(value,col,row);
		}
		else if ("NUMERIC".equals(type)) {
			String mask = attribs.get("mask").getValue();
			NumberFormat formatter = new DecimalFormat(mask);
			value = !"NULL".equals(value) && !"".equals(value) ? formatter.format(Double.parseDouble(value)):"";
			FontMetrics m = g2d.getFontMetrics();
			g2d.drawString(value, col-m.stringWidth(value),row);
		}
	}

	public synchronized void cathSuccesEvent(SuccessEvent e) {
		String numeration = e.getNdocument();
		if (numeration!=null && !"".equals(numeration)) {
			sucess = true;
			ndocument = numeration;
		}
	}
	
	public ImpresionType getImpresionType() {
		return this.impresionType;
	}

	public ByteArrayInputStream getStream() { return null; }

	public boolean isSusseful() {
		return this.sussceful;
	}

	public int print(Graphics graphics, PageFormat pf, int pageIndex) throws PrinterException {
		Paper p = pf.getPaper();
		p.setImageableArea( 0, 0, width,height);
		pf.setPaper(p);
		g2d= (Graphics2D)graphics;
		g2d.setClip(0, 0,width,height);
		switch (pageIndex) {
			case 0:
				process();
				return PAGE_EXISTS;
			default:
				return NO_SUCH_PAGE;
		}
	}
	
	public void process(Element template, Element packages) {}
}