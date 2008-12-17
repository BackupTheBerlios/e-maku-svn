package common.printer;

import java.awt.*;
import java.awt.Font;
import java.awt.geom.*;
import java.io.*;
import java.text.*;
import java.util.*;

import org.jdom.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.*;
import org.jdom.output.Format;

import com.lowagie.text.*;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import common.control.*;
import common.misc.XMLUtils;
import common.misc.text.*;
import common.printer.PrintingManager.*;
import common.transactions.*;


public class PostScriptManager implements AbstractManager, SuccessListener {
	
	private static final long serialVersionUID = 3641816256967941893L;
	private Graphics2D g2d;
	private boolean successful;
	private ImpresionType impresionType = ImpresionType.PDF;
	private String ndocument = "";
	private boolean success = false;
	private Element rootTemplate;
	private int width;
	private int height;
	private Element rootTransact;
	private String idTransaction="";
	private int rowNextPage = 0;
	private com.lowagie.text.Document document;
	private ByteArrayOutputStream outPut = new ByteArrayOutputStream();
	private PdfContentByte cb;
	private PdfWriter pdfWriter;
	private ArrayList<Graphics2D> objects = new ArrayList<Graphics2D>();
	private int pageCount = 1;
	private Vector<Query> sqlData = new Vector<Query>();
	private String documentPrefix;
	private int currentRow = 1;
	private int rowAcum=0;
	ByteArrayInputStream in;
	
	final static BasicStroke stroke = new BasicStroke(0.3f);
	
	public PostScriptManager() {
		ClientHeaderValidator.addSuccessListener(this);
	}
	
	public PostScriptManager(String iddoc) {
		ClientHeaderValidator.addSuccessListener(this);
		this.ndocument=iddoc;
	}

	public void process() {
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
			
			Element settings = rootTemplate.getChild("settings");
			width  = settings.getAttribute("width").getIntValue();
			height = settings.getAttribute("height").getIntValue();
			Attribute ATorientation  = rootTemplate.getAttribute("orientation");
			String orientation = ATorientation!=null ? ATorientation.getValue() : null;
			
			Rectangle pageSize = new Rectangle(width,height);

			document = new com.lowagie.text.Document(pageSize);
			try {
				pdfWriter = PdfWriter.getInstance(document,outPut);
				document.addTitle("outout.pdf");
				document.open();
				
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			cb = pdfWriter.getDirectContent();
			g2d = cb.createGraphicsShapes(width,height);
			if (orientation!=null && "LANDSCAPE".equals(orientation)) {
				g2d.translate(width-10,0);
				g2d.rotate(90*Math.PI/180);
			}
			
			objects.add(g2d);
			
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
				this.in = new ByteArrayInputStream(outPut.toByteArray());
				this.successful = true;
				calendar = Calendar.getInstance();
				long end = calendar.getTimeInMillis();
				System.out.println("Generador en " + (end-init) + " milisegundos ");
			}
			
		}
		catch (DataConversionException e) {
			e.printStackTrace();
		}		
		g2d.dispose();
		try {
			outPut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		document.close();
	}
	
	/**
	 * 
	 * @param element
	 * @throws DataConversionException
	 */
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
			
			boolean lrow = true;
			try {
				lrow = attribs.get("incrementRow").getBooleanValue();
			}
			
			catch(NullPointerException NPEe) {}
			int row = 0;
			String srow = attr.getValue();
			if (srow.equals("last")) {
				row=currentRow;
			}
			else {
				row=Integer.valueOf(srow);
			}
			
			int col =  attribs.get("col").getIntValue();
			g2d.setStroke(stroke);
			if ("line".equals(name)) {
				int row2 = 0;
				String srow2 = attribs.get("row2").getValue();
				if (srow2.equals("last")) {
					row2=row;
				}
				else {
					row2=Integer.parseInt(srow2);
				}
				int col2 = attribs.get("col2").getIntValue();
				g2d.drawLine(col,row, col2,row2);
			}
			else if ("roundedRectangle".equals(name)) {
				int width = attribs.get("width").getIntValue();
				int height = attribs.get("height").getIntValue();
				g2d.draw(new RoundRectangle2D.Double(col,row,width,height,10,10));
			}
			else if ("rectangle".equals(name)) {
				int width = attribs.get("width").getIntValue();
				int height = attribs.get("height").getIntValue();
				g2d.drawRect(col,row,width,height);
			}
			else if ("field".equals(name)) {
				String value = e.getTextTrim();
				value = " ".equals(value) || "".equals(value) ? "  " : value;
				g2d.drawString(value,col,row);
			}
			else if ("ndocument".equals(name)) {
				String value = ndocument==null ? "" : ndocument;
				g2d.drawString(value,col,row);
			}
			else if ("pagenumber".equals(name)) {
				String value = String.valueOf(pageCount);
				g2d.drawString(value,col,row);
			}
			else if ("font".equals(name)) {
				g2d.setFont(new Font(attribs.get("name").getValue(),Font.PLAIN,attribs.get("size").getIntValue()));
			}
			else if ("documentPrefix".equals(name)) {
				documentPrefix = e.getValue();
			}
			else if ("sqlData".equals(name)) {
				sqlData.add(new Query(e.getTextTrim(),col,row));
			}
			if (lrow) {
				currentRow=row+rowAcum;				
			}
			else {
				currentRow=row;
			}
		}
		
		Iterator<Query> sqls = sqlData.iterator();
		if (ndocument!=null) {
			System.out.println("ndocumento NO es nulo");
			Document request = null;
			while (sqls.hasNext()) {
				Query query = sqls.next();
				String sql = query.getSql();
				String [] args = {ndocument,documentPrefix};
				try {
					request = TransactionServerResultSet.getResultSetST(sql,args);
					//XMLUtils.debugDocument(request);
					Element rootElement = request.getRootElement();
					Element row = rootElement.getChild("row");
					String strVal = row.getChildText("col");
					g2d.drawString(strVal,query.getX(),query.getY());
					request = null;
				} catch (TransactionServerException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			System.out.println("ndocumento es nulo");
		}
		
	}
	
	/**
	 * 
	 * @param pack_template
	 * @param pack_transaction
	 * @throws DataConversionException
	 */
	private void processElement(Element pack_template, Element pack_transaction) throws DataConversionException {
		/*
		System.out.println("template: ");
		try {
	        XMLOutputter xmlOutputter = new XMLOutputter();
	        xmlOutputter.setFormat(Format.getPrettyFormat());
	        xmlOutputter.output(pack_template,System.out);
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    
		System.out.println("transaccion: ");
	    try {
	        XMLOutputter xmlOutputter = new XMLOutputter();
	        xmlOutputter.setFormat(Format.getPrettyFormat());
	        xmlOutputter.output(pack_transaction,System.out);
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    */
		Iterator it_template = pack_template.getChildren().iterator();
		Iterator it_transaction = pack_transaction.getChildren().iterator();
		while(it_template.hasNext() && it_transaction.hasNext()) {
			
			Element el_template = (Element)it_template.next();
			if (el_template.getName().equals("subpackage")) {

				int rowInit=0;
				String srowInit = el_template.getAttribute("rowInit").getValue();
				
				rowAcum = el_template.getAttribute("rowAcum").getIntValue();
				
				if (srowInit.equals("last")) {
					rowInit=currentRow+rowAcum;
				}
				else {
					rowInit=Integer.valueOf(srowInit);
				}

				Attribute attMaxAcum = el_template.getAttribute("maxRowsAcum");
				int maxAcum = attMaxAcum!=null ? attMaxAcum.getIntValue() : -1;
				Attribute attrowInitNewPage = el_template.getAttribute("rowInitNewPage");
				int rowInitNewPage = attrowInitNewPage!=null ? attrowInitNewPage.getIntValue() : -1;
				
				Attribute attmaxRowsAcumNewPage = el_template.getAttribute("maxRowsAcumNewPage");
				int maxRowsAcumNewPage = attmaxRowsAcumNewPage!=null ? attmaxRowsAcumNewPage.getIntValue() : -1;
				
				Iterator im = el_template.getChildren("metadata").iterator();
				while (im.hasNext()) { 
					Element element = (Element) im.next();
					processMetadata(element);
					rowInit = currentRow;
				}

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
				
				while ((it_transaction.hasNext() && maxAcum==-1)||
					   (rowInit<maxAcum)) {
					//FIXME HAY QUE OPTIMIZAR ESTO
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
					currentRow=rowInit;
				}
				if (it_transaction.hasNext()) {
					System.out.println("Hojas Separadas por maximo numero de filas: "+ maxAcum);
					int index = 0;
					while (it_transaction.hasNext()) {
						index++;
						//FIXME HAY QUE OPTIMIZAR ESTO
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
						currentRow=rowInit;
						if (index==maxAcum) {
							//FIXME PUNTO DE PARTIDA PARA LA GENERACION DE OTRA HOJA
							document.newPage();							
							rowInit=rowInitNewPage;
							maxAcum=maxRowsAcumNewPage;
							g2d = cb.createGraphicsShapes(width,height);
							processMetadata(rootTemplate.getChild("newpage"));
							++pageCount;
							index=0;
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
				addValue(el_transaction.getValue(),attribs);
			}
		}
	}
	
	/**
	 * 
	 * @param value
	 * @param attribs
	 * @throws DataConversionException
	 */
	private void addValue(String value,HashMap<String,Attribute> attribs) throws DataConversionException {
		if (attribs.size()==0) {
			return;
		}
		
		int row =  attribs.get("row").getIntValue() + rowNextPage;
		int col =  attribs.get("col").getIntValue();
		
		Attribute attribute = attribs.get("type");
		Attribute fontSize = attribs.get("fontSize");
		Attribute fontName = attribs.get("fontName");
		
		String type = attribute!=null ? attribute.getValue() : null ;
		value = !"NULL".equals(value) && !"".equals(value) ?value:"";
		Font currentFont =  g2d.getFont();
		if (fontSize!=null && fontName!=null) {
			Font newFont = new Font(fontName.getValue(),0,fontSize.getIntValue());
			g2d.setFont(newFont);
		} 
		else if (fontSize!=null && fontName==null) {
			Font newFont = new Font(currentFont.getFamily(),0,currentFont.getSize());
			g2d.setFont(newFont);
		}
		
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
			Attribute atl = attribs.get("lengthFill");
			Attribute atf = attribs.get("charFill");
			if (atl!=null && atf!=null) {
				int lengthFill = atl.getIntValue();
				while (value.length()<lengthFill) {
					value+=atf.getValue();
				}
			}
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
		else if ("NUMTOLETTERS".equals(type)) {
            try {
            	int width = attribs.get("width").getIntValue();
    			int height = attribs.get("height").getIntValue();
    			
                Double d = Double.parseDouble(value);
                String letters = String.valueOf(d.intValue());
                letters = NumberToLetterConversor.letters(letters, null);
    			int rowAcum = attribs.get("rowAcum").getIntValue();
    			
    			value = value.replaceAll("\n", " ");
    			StringBuffer buf = new StringBuffer(letters);
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
            } catch (NumberFormatException NFE) {
                // Pendiente por traducir
                System.out.printf("No se puede convertir %s  a letras\n%s",value,NFE.getMessage());
            }
        }
		g2d.setFont(currentFont);
	}

	/**
	 * 
	 */
	public synchronized void cathSuccesEvent(SuccessEvent e) {
		String numeration = e.getNdocument();
		
		if (numeration!=null && !"".equals(numeration) && idTransaction.equals(e.getIdPackage())) {
			success = true;
			ndocument = numeration;
		}
	}
	

	public ImpresionType getImpresionType() {
		return this.impresionType;
	}

	public ByteArrayInputStream getStream() {
		return in;
	}

	public boolean isSuccessful() {
		return this.successful;
	}
	
	
	public void process(Element template, Element packages) {
		this.rootTemplate = template;
		this.rootTransact = packages;
		currentRow=1;
		process();
	}
	
	public void setIdTransaction(String idTransaction) {
		this.idTransaction = idTransaction;
	}
	
	public String getNdocument() {
		return ndocument;
	}

	public void setNdocument(String lastNumber) {
		// TODO Auto-generated method stub
		ndocument=lastNumber;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	class Query {
		private int x;
		private int y;
		private String sql;
		
		Query(String sql,int x,int y) {
			this.x=x;
			this.y=y;
			this.sql=sql;
		}

		protected String getSql() {
			return sql;
		}

		protected int getX() {
			return x;
		}

		protected int getY() {
			return y;
		}
		
	}
}