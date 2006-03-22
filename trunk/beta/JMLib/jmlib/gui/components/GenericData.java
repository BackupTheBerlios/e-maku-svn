package jmlib.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jmlib.control.DateEvent;
import jmlib.control.DateListener;
import jmlib.control.ValidHeadersClient;
import jmlib.gui.formas.ChangeExternalValueEvent;
import jmlib.gui.formas.ChangeExternalValueListener;
import jmlib.gui.formas.FinishEvent;
import jmlib.gui.formas.GenericForm;
import jmlib.gui.formas.InitiateFinishListener;
import jmlib.gui.formas.NotFoundComponentException;
import jmlib.miscelanea.formulas.CalculateFormula;
import jmlib.miscelanea.idiom.Language;
import jmlib.transactions.STException;
import jmlib.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * GenericData.java Creado el 15-oct-2004
 * 
 * Este archivo es parte de JMServerII
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class GenericData extends JPanel implements DateListener, AnswerListener,InitiateFinishListener, ChangeExternalValueListener {
    
	private static final long serialVersionUID = 3911530078319143164L;
    private GenericForm GFforma;
    private Vector <XMLTextField>VFields;
    private String Sargs;
    private Vector <String>sql;
    private Vector <String>sqlCode = null;
    private String sqlLocal = null;
    private String enlaceTabla = "";
    private String namebutton="SAVE";
    private boolean enablebutton=true;
    private Vector <AnswerListener>AnswerListener = new Vector<AnswerListener>();
    private String driverEvent;
    private Vector <String>keySQL;
    private boolean disableAll;
    private boolean havePanel = true;
    private boolean search;

    private ChangeValueEvent CVEevent;
    private Vector <ChangeValueListener>changeValueListener = new Vector<ChangeValueListener>();
    private String returnValue;
	private boolean returnBlankPackage = false;

    public GenericData() {
    	
    }
    
    public GenericData(GenericForm newGFforma, Document doc) 
    throws InvocationTargetException,NotFoundComponentException {
        
        this.GFforma = newGFforma;
        this.GFforma.addInitiateFinishListener(this);
        this.setLayout(new BorderLayout());
        this.VFields = new Vector<XMLTextField>();
        sqlCode = new Vector<String>();
        boolean key= false;
        CVEevent = new ChangeValueEvent(this);
        Element parameters = doc.getRootElement();
//        Sargs = parameters.getChildTextTrim("arg");
        Iterator i = parameters.getChildren().iterator();
        
        int componentes = parameters.getChildren("subarg").size();
        
        JPanel JPetiquetas = new JPanel(new GridLayout(componentes,1));
        JPanel JPfields = new JPanel(new GridLayout(componentes,1));

        /*
         * Almacenara las cadenas de sentencias sql a consultar
         */
        sql = new Vector<String>();
        while (i.hasNext()) {
            
            Element subargs = (Element) i.next();
            String name = subargs.getName();
            String value = subargs.getValue();
            if ("arg".equals(name) && "NEW".equals(value)) {
                Sargs=subargs.getValue();
                namebutton="SAVE";
                enablebutton=true;
            }
            else if ("arg".equals(name) && "EDIT".equals(value)) { 
                Sargs=subargs.getValue();
                namebutton="SAVEAS";
                enablebutton=false;
            }
            else if ("arg".equals(name) && "DELETE".equals(value)) {
                Sargs=subargs.getValue();
                namebutton="DELETE";
                enablebutton=false;
            }
            else if ("arg".equals(name) && "DISABLEALL".equals(value)) {
                disableAll=true;
            }
            else if("arg".equals(name) && subargs.getAttributeValue("attribute")!= null && subargs.getAttributeValue("attribute").equals("returnBlankPackage")) {
            	returnBlankPackage = Boolean.parseBoolean(subargs.getValue());
            }
            else if ("arg".equals(name) && subargs.getAttributeValue("attribute")!= null && subargs.getAttributeValue("attribute").equals("Panel")) {
                havePanel=Boolean.getBoolean(subargs.getValue());
            }
            else if ("linkTable".equals(subargs.getAttributeValue("attribute"))) {
                enlaceTabla=subargs.getValue();
            }
            else if ("driverEvent".equals(subargs.getAttributeValue("attribute"))) {
            	String id="";
            	if (subargs.getAttributeValue("id")!= null) {
            		id=subargs.getAttributeValue("id");
            	}
            	driverEvent=subargs.getValue()+id;
            }
            else if ("returnValue".equals(subargs.getAttributeValue("attribute"))) {
                returnValue = value;
            }
            else if ("mode".equals(subargs.getAttributeValue("attribute"))) {
                Sargs = value;
            }
            else if ("keySQL".equals(subargs.getAttributeValue("attribute"))) {
            	if (keySQL==null) {
            		keySQL= new Vector<String>();
            	}
                keySQL.add(subargs.getValue());
            }
            else if ("arg".equals(name)) {
                sql.addElement(value);
            }
            
            else if ("subarg".equals(name)) {
	            Iterator j = subargs.getChildren().iterator();
	            Font font=null;
	            Color foreground = null;
	            Color background = null;
	            boolean enabled = true;
	            boolean returnValueXTF=true;
	            key = false;
	            String totalCol=null;
	            String alignment=null;
	            String keyExternalValue=null;
	            boolean systemDate = false;
	            String idDocument = null;
	            boolean nullValue = false;
	            boolean clean = true;
	            String callExternalClass = null;
	            String callExternalMethod = null;
                int orden = -1;
                String lab = "";
 	            String format = "";
 	            String mask = null;
 	            int width = 0;
 	            int nroChars = 0;
 	            XMLTextField XMLText = null;
 	            String exportValue = null;
 	            Vector <String>importValue = null;
 	            String calculateExportValue = null;
 	            String maxValue = null;
 	            String formatDate = null;
 	            boolean searchQuery = false;
 	            String calculateDate = null;
 	            String nameField = null;
 	            String addAttribute = null;
 	            
 	           /******************        Validacion de atributos     PSTSO *****************************/
 	            while(j.hasNext()) {
  	            	final Element elm = (Element)j.next();
 	            	if ("sqlCode".equals(elm.getAttributeValue("attribute"))) {
 	            		this.sqlCode.add(elm.getValue());
 	            		searchQuery=true;
  	            	}
 	            	else if ("sqlLocal".equals(elm.getAttributeValue("attribute"))) {
 	            		this.sqlLocal=elm.getValue();
 	            		searchQuery=true;
 	            	}
 	            	else if ("label".equals(elm.getAttributeValue("attribute"))) {
 	            		lab=elm.getValue();
 	            	}
 	            	else if ("format".equals(elm.getAttributeValue("attribute"))) {
 	            		format=elm.getValue();
 	            	}
 	            	else if ("mask".equals(elm.getAttributeValue("attribute"))) {
 	            		mask=elm.getValue();
 	            	}
 	            	else if ("size".equals(elm.getAttributeValue("attribute"))) {
 	            		width=Integer.parseInt(elm.getValue());
 	            	}
 	            	else if ("maxlength".equals(elm.getAttributeValue("attribute"))) {
 	            		nroChars=Integer.parseInt(elm.getValue());
 	            	}
 	            	else if ("key".equals(elm.getAttributeValue("attribute"))) {
 	            		lab=elm.getValue();
 	            		key=true;
 	            	}
 	            	else if ("callExternalClass".equals(elm.getAttributeValue("attribute"))) {
 	            		callExternalClass=elm.getValue();
 	            	}
 	            	else if ("callExternalMethod".equals(elm.getAttributeValue("attribute"))) {
 	            		callExternalMethod=elm.getValue();
 	            	}
 	            	else if ("exportValue".equals(elm.getAttributeValue("attribute"))) {
 	            		exportValue=elm.getValue();
 	            		searchQuery = true;
 	            	}
 	            	else if ("importValue".equals(elm.getAttributeValue("attribute"))) {
 	            		if (importValue==null) {
 	            			importValue = new Vector<String>();
 	            		}
 	            		importValue.add(elm.getValue());
 	            	}
 	            	else if ("calculateExportValue".equals(elm.getAttributeValue("attribute"))) {
 	            		calculateExportValue=elm.getValue();
 	            	}
 	            	else if ("maxValue".equals(elm.getAttributeValue("attribute"))) {
 	            		maxValue=elm.getValue();
 	            	}
 	            	else if ("exportField".equals(elm.getAttributeValue("attribute"))) {
 	            		try {
 	            			orden = Integer.parseInt(elm.getValue());
 	            		}
 	            		catch (NumberFormatException NFEe) {
 	            		}
 	            	}
 	            	else if ("enabled".equals(elm.getAttributeValue("attribute"))) {
            			enabled = Boolean.parseBoolean(elm.getValue());
 	            	}
 	            	else if ("font".equals(elm.getAttributeValue("attribute"))) {
 	            		try {
 			                StringTokenizer STfont = new StringTokenizer(elm.getValue(),",");
 			                font = new Font(STfont.nextToken(),
 			                        		Integer.parseInt(STfont.nextToken()),
 			                        		Integer.parseInt(STfont.nextToken()));
 		                }
 		                catch(NumberFormatException NFEe) {
 		                    font=null;
 		                }
 		                catch(NoSuchElementException NSEEe) {
 		                    font=null;
 		                }
 	            	}
 	            	else if ("foreground".equals(elm.getAttributeValue("attribute"))) {
 		                foreground = getColor(elm.getValue());
 	            	}
 	            	else if ("background".equals(elm.getAttributeValue("attribute"))) {
 		                background = getColor(elm.getValue());
 	            	}
 	            	else if ("totalCol".equals(elm.getAttributeValue("attribute"))) {
 		                totalCol = elm.getValue();
 	            	}
 	            	else if ("alignment".equals(elm.getAttributeValue("attribute"))) {
 	            		alignment=elm.getValue();
 	            
 	            	}
 	            	else if ("keyExternalValue".equals(elm.getAttributeValue("attribute"))) {
 	            		keyExternalValue=elm.getValue();
 	            		searchQuery = true;
 	            	}
 	            	else if ("systemDate".equals(elm.getAttributeValue("attribute"))) {
 	            		systemDate=true;
 	            		formatDate=elm.getValue();
 	            	}
 	            	else if ("idDocument".equals(elm.getAttributeValue("attribute"))) {
 	            		idDocument=elm.getValue();
 	            	}
 	            	else if ("nullValue".equals(elm.getAttributeValue("attribute"))) {
 	            		nullValue=Boolean.parseBoolean(elm.getValue());
 	            	}
 	            	else if ("clean".equals(elm.getAttributeValue("attribute"))) {
 	            		clean=Boolean.parseBoolean(elm.getValue());
 	            	}
 	            	else if ("return".equals(elm.getAttributeValue("attribute"))) {
 	            		returnValueXTF=Boolean.parseBoolean(elm.getValue());
 	            	}
 	            	else if ("nameField".equals(elm.getAttributeValue("attribute"))) {
 	                   nameField = elm.getValue();
 	            	}
 	            	else if ("dateFormat".equals(elm.getAttributeValue("attribute"))) {
  	                   formatDate = elm.getValue();
  	            	}
 	            	else if ("calculateDate".equals(elm.getAttributeValue("attribute"))) {
   	                  	calculateDate = elm.getValue();
   	            	}
 	            	else if ("addAttribute".equals(elm.getAttributeValue("attribute"))) {
 	                   addAttribute = elm.getValue();
 	               }
 	            }

 	            if (mask==null) {
 	            	XMLText = new XMLTextField(lab,width,nroChars,format);
 	            }
 	            else {
 	            	XMLText = new XMLTextField(lab,width,nroChars,format,mask);
 	            }
            	if (key) {
	                XMLText.isKey(true);
	            }
	            XMLText.setKeyExternalValue(keyExternalValue);
	           // XMLText.setEditable(enabled);
	            XMLText.setEnabled(enabled);
	            XMLText.setSystemDate(systemDate);
	            XMLText.setReturnValue(returnValueXTF);
	            XMLText.setNullValue(nullValue);
	            XMLText.setClean(clean);

	            if (exportValue!=null) {
	            	XMLText.setExportvalue(exportValue);
	            }
	            if (importValue!=null) {
	            	XMLText.setImportValue(importValue);	            	
	            }
	            if (maxValue!=null) {
	            	XMLText.setMaxValue(maxValue);	            	
	            }
	            if (nameField!=null) {
	            	XMLText.setNameField(nameField);	            	
	            }
	            if (calculateExportValue!=null) {
	            	XMLText.setCalculateExportValue(calculateExportValue);	            	
	            }
	            if (callExternalClass!=null && callExternalMethod!=null) {
	            	XMLText.setExternalCall(callExternalClass,callExternalMethod);
	            }
	            if (orden > -1 && exportValue!=null) {
	    			GFforma.addExportField(orden,exportValue);
	    		}
	            JPetiquetas.add(XMLText.getLabel());

	            if (systemDate) {
	            	XMLText.setFormatDate(formatDate);
	            	ValidHeadersClient.addDateListener(this);
	            }
	            
	            if (!systemDate && formatDate!=null) {
	            	XMLText.setFormatDate(formatDate);
	            	ValidHeadersClient.addDateListener(this);
	            }
	            if (calculateDate!=null) {
	            	XMLText.setCalculateDate(calculateDate);
	            }
	            
	            if (havePanel) {
	                JPfields.add(XMLText.getJPtext());
	            }
	            else {
	                JPfields.add(XMLText);
	            }
	                
	            if (background!=null) {
	                XMLText.setBackground(background);
	                if (XMLText.getBackground().equals(Color.BLACK)) {
	                	XMLText.setCaretColor(Color.WHITE);
	                }
	            }
	            
	            if (foreground!=null) {
	                XMLText.setForeground(foreground);
	            }

	            if (font!=null) {
	                XMLText.getLabel().setFont(font);
	                XMLText.setFont(font);
	            }
	           
	            if (totalCol!=null) {
	                XMLText.setTotalCol(totalCol);
	            }
	            
	            if (alignment!=null) {
	                if (alignment.equals("RIGHT")) {
	    	            XMLText.setHorizontalAlignment(SwingConstants.RIGHT);
	                }
	                else if (alignment.equals("CENTER")) {
	    	            XMLText.setHorizontalAlignment(SwingConstants.CENTER);
	                }
	            }
	            if (idDocument!=null) {
	                XMLText.setIdDocument(idDocument);
	            }
	            if (addAttribute!=null) {
	                XMLText.setAddAttribute(addAttribute);
	            }
	            XMLText.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						super.focusLost(e);
						XMLTextField XMLRefText = (XMLTextField) e.getSource();
						if (XMLTextField.NUMERIC.equals(XMLRefText.getType())) {
							try {
								XMLRefText.setNumberValue(Double.parseDouble(XMLRefText.getText()));
							}
							catch (NumberFormatException NFEe) {
							}
						}
						if (!"".equals(XMLRefText.getText()) && 
							(XMLRefText.isExportvalue() || XMLRefText.getKeyExternalValue()!=null)) {
		        			  exportar(XMLRefText);
						}
					}
	            });
	            if (searchQuery) {
		            XMLText.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent e) {
								search = true;
						}
			          });
		            
			          XMLText.addFocusListener(new FocusAdapter() {
			        	  public void focusLost(FocusEvent e) {
			        		  
			        		  /*if (XMLText.isExportvalue() || XMLText.getKeyExternalValue()!=null) {
			        			  exportar(XMLText);
			        		  } Comentado por que se lo envio al primer focusLost de XMLTextField*/
			        		  XMLTextField XMLRefText = (XMLTextField) e.getSource();
			        		  if (sqlCode.size() > 0 || sqlLocal!=null) {
				                /*
				                 * El primer elemento del vector sql siempre sera la consulta que almacenara
				                 * la informacion del objeto local
				                 */
				            	if (search) {
				            		String [] impValues = null;
				            		if (sqlLocal!=null){
				            			
				            			impValues = new String[XMLRefText.getImportValue().length];
				            			for (int i = 0 ; i < XMLRefText.getImportValue().length ; i++) {
				            				impValues[i] =  GFforma.getExteralValuesString(XMLRefText.getImportValue()[i]);
				            			}
				            			GFforma.cleanExternalValues();
				            			if (!"".equals(XMLRefText.getText())) {
								            new QueryData(
								                    	  GFforma,
								                    	  namebutton,
								                    	  enablebutton,
								                    	  sqlLocal,
								                    	  impValues,
								                    	  XMLRefText.getText(),
								                    	  VFields).start();
				            			}
				            				
				            		}
						             /*
						             * Las demas consultas seran para almacenar la informacion de los demas
						             * objetos que lo requieran
						             */
				           			searchOthersSqls(XMLRefText);
						            search = false;
				            	}
			        		  }
			            }
			          });
	            }
	            VFields.add(XMLText);
            }
        }
        
        this.add(JPetiquetas,BorderLayout.WEST);
        this.add(JPfields,BorderLayout.CENTER);
        if ("DELETE".equals(Sargs))
            Disabled(1);
        
        if (disableAll)
            Disabled(0);
		
    }
    /**
     * Este metodo retorna un objeto color, apartir de los argumentos recibidos
     * @param color argumetos de colores
     * @return objeto Color
     */
    private Color getColor(String color) {
        try {
	        StringTokenizer STcolor = new StringTokenizer(color,",");
	        int r = Integer.parseInt(STcolor.nextToken());
	        int g = Integer.parseInt(STcolor.nextToken());
	        int v = Integer.parseInt(STcolor.nextToken());
	        return new Color(r,g,v);
        }
        catch (NumberFormatException NFEe) {
            return null;
        }
        catch(NoSuchElementException NSEEe) {
            return null;
        }
    }
    
    public void exportar(XMLTextField xmltf) {
    	notificando(CVEevent);
		if (xmltf.getType().equals("NUMERIC")) {
			if (xmltf.isExportvalue()) {
			 GFforma.setExternalValues(xmltf.getExportvalue(),xmltf.getNumberValue());
			}
	        xmltf.setText(formatear(xmltf.getNumberValue()));
		}
		else if (xmltf.isExportvalue()) {
			GFforma.setExternalValues(xmltf.getExportvalue(),xmltf.getText());
		}
    }
    
    protected String formatear(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
		NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat form = (DecimalFormat)nf;
        form.applyPattern("###,###,##0.00");
        return form.format(bd);
	}
    
    /**
     * Metodo encargado de generar sentencias sql de otros componentes
     */
    public void searchOthersSqls(XMLTextField xmltf) {
        class SearchingSQL extends Thread {
            
	        private String[] args;
	        
            public SearchingSQL(String[] args) {
                 this.args=args;
            }
            
            public void run() {

                String sql;
		        for (int i=0;i<sqlCode.size();i++) {
		            Document doc = null;
                    sql = (String)sqlCode.get(i);
                    try {
                        doc = STResultSet.getResultSetST(sql,args);
                    }
                    catch (STException e) {
                        e.printStackTrace();
                    }
                    AnswerEvent event = new AnswerEvent(this,sql,doc);
    	            notificando(event);
		        }
            }
        }
        /*-----------------------------------------------------------*/
        String [] XMLimpValues = xmltf.getImportValue();
		String [] impValues = new String[XMLimpValues.length+1];
		
		int i = 0;
		for ( i = 0; i < impValues.length -1 ; i++) {
			impValues[i] =  GFforma.getExteralValuesString(XMLimpValues[i]);
		}
		impValues[i] =  xmltf.getText();
        new SearchingSQL(impValues).start();
    
    }
    
    public JPanel getPanel() {
        return this;
    }

    public void Disabled(int init) {
        int max = VFields.size();
        for (int i=init; i < max ; i++) {
            ((XMLTextField)VFields.get(i)).setEnabled(false);
        }
    }
    
    public void clean() {
        int max = VFields.size();
        for (int i=0; i < max ; i++) {
        		XMLTextField xmltemp = (XMLTextField)VFields.get(i);
            if (xmltemp.isClean()) {
            		xmltemp.setText("");
                if (xmltemp.getType().equals("NUMERIC")) {
                		xmltemp.setNumberValue(0);
                }
            }
        }
    }
    
    public Element getPackage(Element args) throws Exception {
    	Element pack = new Element("package");
    	Iterator it = args.getChildren().iterator();
    	while(it.hasNext()) {
    		
        	Element arg = (Element) it.next();
	    	if ("calculate".equals(arg.getAttributeValue("attribute"))) {
	    		pack.addContent(new Element("field").setText(String.valueOf(OperateFormule(arg.getValue()))));
	    	}
	    	if ("maxValue".equals(arg.getAttributeValue("attribute"))) {
	    		try {
		    		StringTokenizer stk = new StringTokenizer(arg.getValue(),":");
		        	double maximo = OperateFormule(stk.nextToken());
		        	double checkValue = OperateFormule(stk.nextToken());
		        	if (maximo < checkValue ) {
		    			throw new Exception(Language.getWord("ERR_MAX_VALUE"));
		    		}
	    		}
	    		catch (NoSuchElementException NSEe) {
	    			throw new Exception(Language.getWord("ERR_MAX_VALUE"));
	    		}
	    	}
    	}
    	
		return pack;
    }
    
    public Element getPackage() throws VoidPackageException {
        
        Element pack = new Element("package");
        int max = VFields.size();

        if (returnBlankPackage) {
	        boolean returnValue =true;
	        for (int j=0;j<VFields.size();j++) {
	            XMLTextField Field= (XMLTextField)VFields.get(j);
	            if (Field.isReturnValue()) { 
	            	if (!"".equals(Field.getText())) {
	            		returnValue = false;
	            		break;
	            	}
	            }
	        }
	        
	        if (returnValue) {
	        	return pack;
	        }
        }
        
        int i=0;
        if ("EDIT".equals(Sargs))
            i=1;
        
        if (!"DELETE".equals(Sargs))
	        for (; i < max ; i++) {
	            XMLTextField Field= (XMLTextField)VFields.get(i);
	            String text = Field.getText();
	            if (Field.isReturnValue()) {
		            if (Field.isNullValue()) {
		                pack.addContent(Field.getElementText());
		            }
		            else {
		                if (!text.equals("")) {
		                	Element elm = Field.getElementText();
		                	if (Field.getAddAttribute()!=null) {
		                		elm.setAttribute("attribute",Field.getAddAttribute());
		                	}
	                		pack.addContent(elm);
			            }
			            else {
			                throw new VoidPackageException(Field.getLabel().getText());
			            }
		            }
		            
		            if (text.equals("") && "DELETE".equals(Sargs)) {
		                throw new VoidPackageException(Field.getLabel().getName());
		            }
	            }
	        }
        
        if ("EDIT".equals(Sargs) || "DELETE".equals(Sargs)) {
            XMLTextField Field= VFields.get(0);
        	String text = Field.getText();
        	if (Field.isNullValue()) {
                pack.addContent(Field.getElementText());
            }
            else {
                if (!text.equals("")) {
                    pack.addContent(Field.getElementText());
		        }
		        else {
		            throw new VoidPackageException(Field.getLabel().getText());
		        }
            }
        }

        return pack;
    }

    public synchronized void addAnswerListener(AnswerListener listener ) {
        AnswerListener.addElement(listener);
    }

    public synchronized void removeAnswerListener(AnswerListener listener ) {
        AnswerListener.removeElement(listener);
    }

    public void arriveAnswerEvent(AnswerEvent AEe) {
    	for (int ind=0; ind < keySQL.size() ; ind++) 
	    	if (AEe.getSqlCode().equals(keySQL.get(ind))) {
		        Document doc = AEe.getDocument();
		        Iterator i = doc.getRootElement().getChildren("row").iterator();
		        int row = doc.getRootElement().getChildren("row").size();

		        if (AEe.getSqlCode().equals(keySQL.get(ind)) && ind==0) {
		        	clean();
		        }
		        if (row>0) {
			        while (i.hasNext()) {
			            Element e = (Element) i.next();
			            Iterator j = e.getChildren().iterator();
			            for (int k=0;j.hasNext();k++) {
			                Element f = (Element)j.next();
			                XMLTextField XMLRefText = ((XMLTextField)VFields.get(k));
			                
			                if (XMLRefText.getFormatDate()!=null) {
			                	try {
			                		Format formatter = new SimpleDateFormat(XMLRefText.getFormatDate());
			                		XMLRefText.setText(formatter.format(Timestamp.valueOf(f.getValue().trim())));
			                	}catch (IllegalArgumentException IAEe) {
			                		XMLRefText.setText(f.getValue().trim());
			                	}
			                } 
			                else if (XMLTextField.NUMERIC.equals(XMLRefText.getType())) {
								try {
									XMLRefText.setNumberValue(Double.parseDouble(f.getValue().trim()));
									//XMLRefText.setText(Double.parseDouble(f.getValue().trim())));
								}
								catch (NumberFormatException NFEe) {
								}
							}
			                else {
								XMLRefText.setText(f.getValue().trim());
							}
			                if (XMLRefText.isExportvalue()) {
			        			  exportar(XMLRefText);
			                }
			            }
			        }
			        if (Sargs!=null) {
				        if ("NEW".equals(Sargs)) {
				        	GFforma.setEnabledButton(namebutton, false);
				        }
				        else {
				        	GFforma.setEnabledButton(namebutton, true);
				            //clean();
				        }   
			        }
		        }else {
		        	if (Sargs!=null) {
				        if ("NEW".equals(Sargs)) {
				        	GFforma.setEnabledButton(namebutton, true);
				        }
				        else {
				        	GFforma.setEnabledButton(namebutton, false);
				            //clean();
				        }
		        	}
		        }
	    	}
    }
    public Vector getVFields() {
        return VFields;
    }
 
    public Element getReturnValue() {
        Element pack = new Element("package");
        pack.addContent(new Element("field").setText(returnValue));
        return pack;
    }

    /**
     * Metodo encargado de notificar la llegada de un paquete <answer/>
     * @param event 
     */
    private synchronized void notificando(AnswerEvent event) {
        Vector lista;
        lista = (Vector)AnswerListener.clone();
        for (int i=0; i<lista.size();i++) {
            AnswerListener listener = (AnswerListener)lista.elementAt(i);
            listener.arriveAnswerEvent(event);
        }
    }

    public void callAddAnswerListener() throws InvocationTargetException, NotFoundComponentException {
		if (driverEvent!=null) {
		   GFforma.invokeMethod(driverEvent,"addAnswerListener",new Class[]{AnswerListener.class},new Object[]{this});
		}

    }
 	public void initiateFinishEvent(FinishEvent e) {
		try {
			callAddAnswerListener();
			/* Añadido por pastuxso para el calculo de fechas*/
			GFforma.addChangeExternalValueListener(this);
		}
		catch(NotFoundComponentException NFCEe) {
			NFCEe.printStackTrace();
		} 
		catch (InvocationTargetException ITEe) {
			ITEe.printStackTrace();
		}
		
	}

	public String getDriverEvent() {
		return driverEvent;
	}

	public void setDriverEvent(String driverEvent) {
		this.driverEvent = driverEvent;
	}

	public boolean isSearch() {
		return search;
	}

	public void setSearch(boolean search) {
		this.search = search;
	}

	public String getEnlaceTabla() {
		return enlaceTabla;
	}
	public synchronized void addChangeValueListener(ChangeValueListener listener ) {
        changeValueListener.addElement(listener);
    }

    public synchronized void removeChangeValueListener(ChangeValueListener listener ) {
        changeValueListener.removeElement(listener);
    }

    private synchronized void notificando(ChangeValueEvent event) {
    	
    	/*Vector fields = this.getVFields();
    	int size = fields.size();
    	for (int i=0;i<size;i++) {
	    	try {
	    		double val = Double.parseDouble(((XMLTextField)fields.get(i)).getText());
		    	((XMLTextField)fields.get(i)).setNumberValue(val) ;
			}
		    catch (NumberFormatException NFEe) {
		    	((XMLTextField)fields.get(i)).setNumberValue(0);                        
		    }
    	}*/
        Vector lista;
        lista = (Vector)changeValueListener.clone();
        for (int i=0; i<lista.size();i++) {
            ChangeValueListener listener = (ChangeValueListener)lista.elementAt(i);
            listener.changeValue(event);
        }
    }
    
	public void changeExternalValue(ChangeExternalValueEvent e) {
		Vector fields = this.getVFields();
		for (int i=0 ; i<getVFields().size() ; i++) {
    		XMLTextField xmltf = ((XMLTextField)fields.get(i));
    		synchronized(xmltf) {
	    		if (xmltf.isCalculateExportvalue()) {
	    			String formula = xmltf.getCalculateExportValue();
	    			xmltf.setText(formatear(OperateFormule(formula)));
	    		}
	    		if (xmltf.isCalculateDate()) {
	    			try {
		    			StringTokenizer stk = new StringTokenizer(xmltf.getCalculateDate(),"+");
		    			if (stk.countTokens() < 2)
		    				stk = new StringTokenizer(xmltf.getCalculateDate(),"-");
		    			String val1 = GFforma.getExteralValuesString(stk.nextToken());
		    			String val2 = GFforma.getExteralValuesString(stk.nextToken());
		    			if (val1!=null && val2!=null) {
			    			Date date = Date.valueOf(val1);
			    			Calendar cal = Calendar.getInstance();
			    			cal.setTime(date);
			    			cal.add(Calendar.DAY_OF_MONTH,Integer.parseInt(val2));
			    			Format formatter = new SimpleDateFormat(xmltf.getFormatDate());
							xmltf.setText(formatter.format(cal.getTime()));
		    			}
	    			}
	    			catch (NoSuchElementException NSEe) {
	    				
	    			}
	    			catch (NumberFormatException NFEe) {
	    				
	    			}
	    		}
	    		if (sqlLocal!=null) {
	    			new QueryData(
	                    	  GFforma,
	                    	  namebutton,
	                    	  enablebutton,
	                    	  sqlLocal,
	                    	  new String[]{GFforma.getExteralValuesString(xmltf.getImportValue()[0])},
	                    	  null,
	                    	  VFields).start();
	    		}
    		}
		}
	}
	
	public double OperateFormule(String formula) {
		String formulaFinal = "";
		int max = formula.length();
		String acumText = "";
		int j=0;
		boolean operador=true;
		for (j=0 ; j < max ; j++) {
			if (formula.charAt(j)>96 && formula.charAt(j)<123) {
				acumText += formula.substring(j,j+1);
				operador=false;
			}
			else  {
				if (!operador) {
					formulaFinal += GFforma.getExteralValues(acumText);
				}
				formulaFinal += formula.substring(j,j+1);
				acumText = "";
				operador=true;
			}
		}
		if (acumText.length() > 0) {
			formulaFinal += GFforma.getExteralValues(acumText);
		}
		return ((Double)CalculateFormula.operar(formulaFinal)).doubleValue();
	}

	public void cathDateEvent(DateEvent e) {
		for (int i =0 ; i< VFields.size() ; i++) {
			XMLTextField xmltf = (XMLTextField) VFields.get(i);
			if (xmltf.getFormatDate()!=null && xmltf.isSystemDate()) {
				//Format formatter = new SimpleDateFormat(xmltf.getFormatDate());
				//xmltf.setText(formatter.format(Timestamp.valueOf(e.getDate())));
				xmltf.setText(e.getDate());
				if (xmltf.isExportvalue())
					GFforma.setExternalValues(xmltf.getExportvalue(),xmltf.getText());
			}
		}
	}
	
	public boolean containData() {
		try {
			Element elm = getPackage();
			if (elm.getChildren().size() > 0) {
				return true;
			}
		} catch (VoidPackageException e) {
			return false;
		}
		return false;
	}
}