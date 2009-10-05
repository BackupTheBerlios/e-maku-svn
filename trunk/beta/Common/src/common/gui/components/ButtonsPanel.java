package common.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import javax.print.*;
import javax.swing.*;

import org.jdom.*;
import org.jdom.input.*;

import common.comunications.*;
import common.control.*;
import common.gui.forms.*;
import common.misc.*;
import common.misc.language.*;
import common.misc.parameters.*;
import common.pdf.pdfviewer.*;
import common.printer.*;
import common.printer.PrintingManager.*;
import common.printer.plainViewer.*;
import common.transactions.*;

/**
 * ButtonsPanel.java Creado el 22-sep-2004
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Informacion de la clase <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class ButtonsPanel extends JPanel implements MouseListener, KeyListener,SuccessListener,ErrorListener{

    private static final long serialVersionUID = 8883108186183650115L;

    private GenericForm GFforma;
    private Hashtable <String,Vector<?>>Heventos;
    private Hashtable <String,Vector>Hform;
    private Hashtable <String,XMLButton>Hbuttons;
    private String idTransaction;
    private String accel = "";
	//private String idReport;
    private PlainPrintingManager plainManager = new PlainPrintingManager();
    private PostScriptManager postScriptManager = new PostScriptManager();
	//private String typePackage = "TRANSACTION";
	private String lastNumber;
	private boolean actionFinish;
	private boolean badActionFinish;
	private long lastTime = Calendar.getInstance().getTimeInMillis();
   
    public ButtonsPanel(GenericForm GFforma, Document doc) {

        this.GFforma = GFforma;
        this.setLayout(new FlowLayout(FlowLayout.TRAILING));
		ClientHeaderValidator.addSuccessListener(this);
		ClientHeaderValidator.addErrorListener(this);

        Heventos = new Hashtable<String,Vector<?>>();
        Hbuttons = new Hashtable<String,XMLButton>();
        Hform = new Hashtable<String,Vector>();
        
        Element args = doc.getRootElement();
        Iterator i = args.getChildren().iterator();
        JButton button;
        
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String name = e.getValue();
            String label = null;
            String icon = null;
            String keyStroke = null;
            String typePackage=null;
            String idReport=null;
            boolean enabled=true;
            
            if ("false".equals(e.getAttributeValue("enabled")))
                enabled=false;
            
            if ("subarg".equals(e.getName())) {
            	Iterator j = e.getChildren().iterator(); 
            	while (j.hasNext()) {
            		Element el = (Element)j.next();
            		if ("label".equals(el.getAttributeValue("attribute"))) {
            			label = el.getValue();
            		}
            		if ("icon".equals(el.getAttributeValue("attribute"))) {
            			icon = el.getValue();
            		}
            		if ("enabled".equals(el.getAttributeValue("attribute"))) {
            			enabled=Boolean.parseBoolean(el.getValue());
            		}
            		if ("keyStroke".equals(el.getAttributeValue("attribute"))) {
            			keyStroke=el.getValue();
            		}
            		if ("typePackage".equals(el.getAttributeValue("attribute"))) {
            			typePackage=el.getValue();
            		}
            		if ("idReport".equals(el.getAttributeValue("attribute"))) {
            			idReport=el.getValue();
            		}
            	}
            }
            XMLButton xbutton = null;
            if (label!=null) {
            	button = buildButton(label,icon,enabled,keyStroke);
            	name = label;
                xbutton = new XMLButton(button,typePackage,idReport);
            }
            else {
	            button = buildButton(name,enabled);
                xbutton = new XMLButton(button);

            }
            Hbuttons.put(name,xbutton);
            this.add(button);
        }
    }

    private JButton buildButton(String label,
    							String icon,
    							boolean enabled, 
    							String keyStroke) {
    	JButton button = new JButton();
    	try {
    		button.setIcon(new ImageIcon(this.getClass().getResource(Icons.getIcon(icon))));
    	}
    	catch (NullPointerException NPEe) {
    		try {
    			button.setIcon(new ImageIcon(this.getClass().getResource(icon)));
    		}
    		catch(NullPointerException NPEe2) {}
    	}
    	button.setActionCommand(label);
    	button.setEnabled(enabled);
    	setAccelerators(button,keyStroke);
		return button;
	}

	private JButton buildButton(String name,boolean enabled) {
    	JButton button = new JButton();
        try {
	        button.setEnabled(enabled);
	        button.setActionCommand(name);
	        button.setIcon(new ImageIcon(this.getClass().getResource(Icons.getIcon(name))));
        }
        catch(NullPointerException NPEe) {}
        
        setAccelerators(button,null);
        return button;
    }

	public void setAccelerators(JButton button, String keyStroke) {
		//button.addActionListener(this);
		button.addMouseListener(this);
        button.addKeyListener(this);
		String name = button.getActionCommand();
		if (keyStroke!=null) {
			accel=keyStroke;
		}
		else {
			if (name.equals("NEW")) {
	        	accel = "F5";
	        }
	        else if (name.equals("SAVE") || name.equals("SAVEAS")) {
	        	accel = "F7";
	        }
	        else if (name.equals("PRINT")) {
	        	accel = "F9";
	        }
	        else if (name.equals("EXIT")) {
	        	accel = "F4";
	        }
		}
        
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(accel), "answer");     
        button.getActionMap().put("answer", new AbstractAction("answer") {
			private static final long serialVersionUID = 7031745964120299130L;
					public void actionPerformed(ActionEvent evt) {
						JButton b = ((JButton)evt.getSource());
						if ((evt.getWhen() - lastTime) > 3000) {
							lastTime = evt.getWhen();
							actionThread(b.getActionCommand());
						}
					}
        });
	}
	
	public void setEnabled(String name, boolean bool) {
        if (Hbuttons.containsKey(name)) {
        	/* Comentada estas lineas funcionaban para el manejo del foco
        	 * en las subformas, ya que habia inconvenientes con el mismo
        	 */
        	/*if (!bool) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            }*/
            Hbuttons.get(name).getButton().setEnabled(bool);
        }
    }
    
    public JPanel getPanel() {
        return this;
    }

    public void setEvents(Document doc) {
        Element args = doc.getRootElement();
        Iterator i = args.getChildren().iterator();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            loadEvent(e);
        }
    }

    private void loadEvent(Element elm) {
        Iterator i = elm.getChildren().iterator();

        String key = "";
        Vector<Components> componentes = new Vector<Components>();
        Vector<Element> actions = new Vector<Element>();
        Vector<Element> formularios = new Vector<Element>();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String name = e.getName();
            if (name.equals("name")) {
                key = e.getValue();
            }
            else if (name.equals("component")) {
                componentes.add(loadComponent(e));
            }
            else if (name.equals("FORM")) {
            	formularios.add(e);
            }
            else if (name.equals("action")) {
            	actions.add(e);
            }
        }
        if (componentes.size()>0) {
        	Heventos.put(key, componentes);
        }
        if (actions.size()>0) {
        	Heventos.put(key, actions);
        }
        if (formularios.size()>0) {
        	Hform.put(key,formularios);
        }
    }

    private Components loadComponent(Element elm) {
        Iterator i = elm.getChildren().iterator();

        Components comp = new Components();

        while (i.hasNext()) {
            Element e = (Element) i.next();
            String name = e.getName();
            if (name.equals("driver")) {
                if (e.getAttributeValue("id")!=null){
                    comp.setDriver(e.getValue()+e.getAttributeValue("id"));
                }
                else {
                    comp.setDriver(e.getValue());
                }
            }
            else if (name.equals("method")) {
                comp.setMethod(e.getValue());
            }
            else if (name.equals("parameters")) {
                comp.setArgs(e);
            }
        }
        return comp;
    }

    private void close() {
        GFforma.close();
    }

	@SuppressWarnings("unchecked")
	private void callEvent(String action) 
    throws InvocationTargetException,NotFoundComponentException, IOException {
    	
    	Vector vec = Heventos.get(action);
        Object obj = vec.get(0);
        if (obj instanceof Components) {
        	builTransaction(vec,action,null,null);
        }
        else if (obj instanceof Element) {
        	for (Object object : vec) {
        		actionFinish=false;
        		badActionFinish=false;
	        	Element element= (Element) object;
	    		String type = element.getChildText("type");
	    		String idManualTransaction = element.getChildText("idManualTransaction");
	    		Iterator it = element.getChildren("component").iterator();
    			Vector<Components> vector = new Vector<Components>();
    			while (it.hasNext()) {
        			Components comp = loadComponent((Element) it.next());
            		vector.add(comp);
    			}
	    		if ("transaction".equals(type)) {
            		builTransaction(vector,action,null,null);
	    		}
	    		else if ("manualTransaction".equals(type)) {
	    			builTransaction(vector,action,null,idManualTransaction);
	    		}
	    		else if ("printer".equals(type)) {
	    			Document doc = new Document();
	    			Element printJob = new Element("printjob"); 
	    			doc.setRootElement(printJob);
	    			builTransaction(vector,action,printJob,null);
	    			String pathTemplate = element.getChildText("printerTemplate");
	    			String jarDirTemplates = EmakuParametersStructure.getJarDirectoryTemplates();
	    			try {
						SAXBuilder sax = new SAXBuilder(false);
						Document template = null;
						URL url = new URL(jarDirTemplates+pathTemplate);
						if (url!=null){
							template = sax.build(url);
							Element rootTemplate= template.getRootElement();
							Attribute ATType    = rootTemplate.getAttribute("type");
							Attribute ATSilent  = rootTemplate.getAttribute("silent");
							Attribute ATCopies  = rootTemplate.getAttribute("copies");
							Attribute ATprinter  = rootTemplate.getAttribute("printer");
							
							
							boolean silent = ATSilent!=null ? ATSilent.getBooleanValue() : false;
							int copies     = ATCopies!=null ? ATCopies.getIntValue() : 1;
							String typePrinter   = ATType.getValue();
							String printer = ATprinter!=null && 
											 !ATprinter.getValue().trim().equals("") ?  
											 ATprinter.getValue() : 
											 null ;
							System.out.println("template path: "+jarDirTemplates+pathTemplate);
							if ("PLAIN".equals(typePrinter) ) {
								plainManager.setNdocument(lastNumber);
								plainManager.processPostScript(rootTemplate,printJob);
								if (plainManager.isSuccessful()) {
									System.out.println("========================================");
									System.out.println(plainManager.toString());
									System.out.println("========================================");
									ImpresionType        IType     = plainManager.getImpresionType();
									ByteArrayInputStream IStream   = plainManager.getStream();
									new PrintingManager(IType,IStream, silent, copies,printer,0,0);
								}
								plainManager = new PlainPrintingManager(lastNumber);
							} else if ("GRAPHIC".equals(typePrinter) ) {
								postScriptManager.setNdocument(lastNumber);
								postScriptManager.processPDF(rootTemplate,printJob);
								ByteArrayInputStream IStream   = postScriptManager.getStream();
								ImpresionType        IType     = postScriptManager.getImpresionType();
								int width = postScriptManager.getWidth();
								int height = postScriptManager.getHeight();
								new PrintingManager(IType,IStream, silent, copies,printer,width,height);
								postScriptManager = new PostScriptManager(lastNumber);		
							} else {
								/*
								postScriptManager.setNdocument(lastNumber);
								postScriptManager.processPDF(rootTemplate,printJob);
								ByteArrayInputStream IStream   = postScriptManager.getStream();
								ImpresionType        IType     = ImpresionType.POSTSCRIPT;
								int width = postScriptManager.getWidth();
								int height = postScriptManager.getHeight();
								new PrintingManager(IType,IStream, silent, copies,printer,width,height);
								postScriptManager = new PostScriptManager(lastNumber);
								*/
								postScriptManager.load(rootTemplate,printJob);
								new PrintingManager(postScriptManager,silent, copies,printer);
								postScriptManager = new PostScriptManager(postScriptManager.getNdocument());
								
							}
							//TODO Comentado por cristian,
							//Esta llamada la garbage collector es verificación
							//System.gc();
						}
						else {
							System.out.println("Plantilla "+jarDirTemplates+pathTemplate+" no encontrada");
							JOptionPane.showInternalMessageDialog(GFforma,"NO SE ENCONTRO LA PLANTILLA DE IMPRESION");
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						System.out.println("Plantilla "+jarDirTemplates+pathTemplate+" no encontrada");
						JOptionPane.showInternalMessageDialog(GFforma,"NO SE ENCONTRO LA PLANTILLA DE IMPRESION");
					} catch (PrintException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JDOMException e) {
						e.printStackTrace();
					}
	    		}
        	}
        }
    }
    
	@SuppressWarnings("unchecked")
    private synchronized void builTransaction(Vector<Components> vec,String action,Element printJob,String idManualTransaction) 
    throws InvocationTargetException, NotFoundComponentException, IOException {
        Vector <Element>pack = new Vector<Element>();
    	Element multielementos[] = null;
        Element elementos = null;
    	for (int i = 0; i < vec.size(); i++) {
    		Components comp = vec.get(i);
    		if ("getMultiPackage".equals(comp.getMethod())) {
            	if (comp.isContainsArgs()) {
            		multielementos = (Element[]) GFforma.invokeMethod(comp.getDriver(), 
            														  comp.getMethod(),
            														  new Class[]{Element.class},
            														  new Object[]{comp.getArgs()});
            	}
            	else {
            		multielementos = (Element[]) GFforma.invokeMethod(comp.getDriver(), comp.getMethod());
            	}
                
                for (int j=0;j<multielementos.length;j++) {
                    pack.addElement(multielementos[j]);
                }
            }
            else { 
            	if (comp.isContainsArgs()) {
		            elementos = (Element) GFforma.invokeMethod(comp.getDriver(), 
		            										   comp.getMethod(),
		            										   new Class[]{Element.class},
		            										   new Object[]{comp.getArgs()});
            	}
            	else {
            		elementos = (Element) GFforma.invokeMethod(comp.getDriver(), comp.getMethod());
            	}
	            if (elementos != null)
	                pack.addElement(elementos);
            }
        }
    	if (printJob==null) {
    		String namePackage;
    		if (!("REPORT".equals(action) || "EXCEL".equals(action))) {
    			namePackage="TRANSACTION";
    		}
    		else {
    			System.out.println("action: "+Hbuttons.get(action).getTypePackage());
    			namePackage=Hbuttons.get(action).getTypePackage();
    		}
    		
		    if (pack.size() > 0 || !namePackage.equals("TRANSACTION")) {
		   		try {
		   			formatPackageStructure(pack,namePackage,idManualTransaction,Hbuttons.get(action).getIdReport());
				} catch (MalformedProfileException e) {
					e.printStackTrace();
				}
		    }
    	}
    	else {
    		for (int i = 0; i < pack.size(); i++) {
                printJob.addContent((Element) pack.elementAt(i).clone());
            }
    	}
    }
    
    /*public void actionPerformed(ActionEvent e) {
    	System.out.println("When: " + e.getWhen());
        actionEvent(e.getActionCommand());
    }*/

    private void actionEvent(String action) {
    	actionThread(action);
    }
    
    private void actionThread(String action) {
    	class ActionThread extends Thread {
    		String action;
    		ActionThread(String action){
    			this.action=action;
    		}
    		
    		public void run() {
    	        if (action.equals("EXIT")) {
    	            close();
    	        } else {
    	            try {
    	            	if (Heventos.containsKey(action)) {
    	            		callEvent(action);
    	            	}
    	            	if (Hform.containsKey(action)) {
    	            		Vector vforms = Hform.get(action);
    	            		for (int i=0 ; i< vforms.size() ; i++) {
    	            			new GenericForm(GFforma,(Element)vforms.get(i));
    	            		}
    	            	}
    	                JButton button = null;
    	                
    	                if (action.equals("NEW")) {
    	                	try {
	    	                    button = Hbuttons.get("SAVE").getButton();
	    	                    if (button != null) {
	    	                        button.setEnabled(true);
	    	                    }
    	                	}
    	                	catch(NullPointerException npe1) {}
    	                	try {
	    	                    button = Hbuttons.get("SAVEAS").getButton();
	    	                    if (button != null) {
	    	                        button.setEnabled(true);
	    	                    }
    	                	}
    	                	catch(NullPointerException npe1) {}
    	            
    	                    button = Hbuttons.get(action).getButton();
//    	                    button.setEnabled(false);

    	                } else if (action.equals("SAVE") || action.equals("SAVEAS")) {

    	                    button = Hbuttons.get("NEW").getButton();
    	                    if (button != null) {
    	                        button.setEnabled(true);
    	                    }
    	                    button = Hbuttons.get("SAVEAS").getButton();
    	                    if (button != null) {
    	                        button.setEnabled(true);
    	                    }
    	                    button = Hbuttons.get("PRINT").getButton();
    	                    if (button != null) {
    	                        button.setEnabled(true);
    	                    }
    	                    button = Hbuttons.get(action).getButton();
    	                    button.setEnabled(false);
    	                }
    	                
    	            }
    	            catch (InvocationTargetException ITEe) {
    	                ITEe.printStackTrace();
    	                JOptionPane.showInternalMessageDialog(GFforma.getDesktopPane(),
    	                        ITEe.getCause().getMessage(), Language
    	                                .getWord("ERROR_MESSAGE"),
    	                        JOptionPane.ERROR_MESSAGE);
    	                Hbuttons.get(action).getButton().setEnabled(true);
    	            }
    	            catch (NotFoundComponentException NFCEe) {
    	                NFCEe.printStackTrace();
    	            } catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    	        }
    		}
    	}
    	new ActionThread(action).start();
    }
    
    private void formatPackageStructure(Vector pack,String packageName,String idManualTransaction,String idReport) 
    throws MalformedProfileException, IOException {
        Document transaction = new Document();
        transaction.setRootElement(new Element(packageName));
        Element id = new Element("id");
        idTransaction = "T"+TransactionServerResultSet.getId();
        System.out.println("Id Transaccion :" + idTransaction);
        id.setText(idTransaction);
        
        Element driver = new Element("driver");
        if ("TRANSACTION".equals(packageName)) {
        	if (idManualTransaction==null) {
        		driver.setText(GFforma.getIdTransaction());
        	}
        	else {
        		driver.setText(idManualTransaction);
        	}
        }
        else if ("REPORTREQUEST".equals(packageName)) {

        	if (idReport!=null) { 
        		driver.setText(idReport);
        		PDFViewer pdf = new PDFViewer(GFforma,idReport);
            	JInternalFrame jiframe = pdf.getGUI().getFrame();
				GFforma.getJDPpanel().add(jiframe);
				try {
					jiframe.setVisible(true);
					jiframe.setSelected(true);
				} catch (java.beans.PropertyVetoException PVEe) {
					PVEe.printStackTrace();
				}
        	}
        	else {
        		throw new MalformedProfileException(Language.getWord("MALFORMED_PROFILE")+GFforma.getIdTransaction());
        	}
        }
        else if ("XLSREPORTREQUEST".equals(packageName)) {

        	if (idReport!=null) { 
        		Element jarFile = new Element("jarFile");
        		Element jarDirectory = new Element("jarDirectory");
        		jarFile.setText(EmakuParametersStructure.getParameter("jarFile"));
        		jarDirectory.setText(EmakuParametersStructure.getParameter("jarDirectory"));
        		transaction.getRootElement().addContent(jarFile);
        		transaction.getRootElement().addContent(jarDirectory);
        		driver.setText(idReport);
        		XLSReceiver xlsReceiver = new XLSReceiver(GFforma,idTransaction);
				GFforma.getJDPpanel().add(xlsReceiver);
				try {
					xlsReceiver.setVisible(true);
					xlsReceiver.setSelected(true);
				} catch (java.beans.PropertyVetoException PVEe) {
					PVEe.printStackTrace();
				}
        	}
        	else {
        		throw new MalformedProfileException(Language.getWord("MALFORMED_PROFILE")+GFforma.getIdTransaction());
        	}
        }
        else if ("PLAINREPORTREQUEST".equals(packageName)) {
        	
        	if (idReport!=null) { 
        		driver.setText(idReport);   
        		TextReportViewer plainViewer = new TextReportViewer(GFforma,idReport);
        		plainViewer.openViewer();
        		JInternalFrame jiframe = plainViewer;
				GFforma.getJDPpanel().add(jiframe);

				try {
					jiframe.setVisible(true);
					jiframe.setSelected(true);
				} catch (java.beans.PropertyVetoException PVEe) {
					PVEe.printStackTrace();
				}
        	}
        	else {
        		throw new MalformedProfileException(Language.getWord("MALFORMED_PROFILE")+GFforma.getIdTransaction());
        	}
        }
        else {
        	throw new MalformedProfileException(Language.getWord("MALFORMED_PROFILE")+GFforma.getIdTransaction());
        }
        
        plainManager.setIdTransaction(idTransaction);
        postScriptManager.setIdTransaction(idTransaction);
        transaction.getRootElement().addContent(driver);
        transaction.getRootElement().addContent(id);

        if (GFforma.getPassword() != null) {
            Element password = new Element("password");
            password.setText(GFforma.getPassword());
            transaction.getRootElement().addContent(password);
        }

        for (int i = 0; i < pack.size(); i++) {
            transaction.getRootElement().addContent((Element) pack.elementAt(i));
        }
        GFforma.sendTransaction(DateSender.getPackage());
        GFforma.sendTransaction(transaction);

        int times = 0;
		while (!actionFinish && !badActionFinish && "TRANSACTION".equals(packageName)) {
			try {
				if (times<=5000) {
					Thread.sleep(100);
				}
				else {
					throw new IOException();
				}
				times++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (badActionFinish) {
			System.out.println("Accion terminada por error ...");
		}
    }

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {
    	if (e.getKeyCode() == KeyEvent.VK_ENTER)
            actionEvent(((JButton) e.getSource()).getActionCommand());
    }

    public void keyTyped(KeyEvent e) {}
    
    /**
     *  Metodo encargado de devolver el Id de la ultima transaccion ejecutada
     * @return retorna el id de la transaccion
     */
    public String getIdTransaction() {
        return idTransaction;
    }
        
    public void setEnabled(Element args) {
    	Iterator it = args.getChildren().iterator();
    	String name = null;
    	String state = null;
    	while (it.hasNext()) {
    		Element element = (Element)it.next();
    		String value = element.getTextTrim();
    		if ("button".equals(element.getAttributeValue("attribute"))) {
    			name = value;
    		}
    		else if ("state".equals(element.getAttributeValue("attribute"))) {
    			state = value;
    		}
    	}
    	if (name!=null && !"".equals(name) &&
    			state!=null && !"".equals(state) ) {
    		if (Hbuttons.containsKey(name)) {
    			Hbuttons.get(name).getButton().setEnabled(Boolean.parseBoolean(state));
    		}
    		else {
    			JOptionPane.showInternalMessageDialog(GFforma.getDesktopPane(),
    					Language.getWord("ERROR_BUTTON_NOT_FOUND")+name,
    					Language.getWord("ERROR_MESSAGE"),
                        JOptionPane.ERROR_MESSAGE);    				
    		}
    	}
    	else {
			JOptionPane.showInternalMessageDialog(GFforma.getDesktopPane(),
					Language.getWord("ERROR_PARAMETERS_BUTTONS"),
					Language.getWord("ERROR_MESSAGE"),
                    JOptionPane.ERROR_MESSAGE);    				
    	}
    }

    public Element getLastNumber() {
        Element pack = new Element("package");
        if (lastNumber!=null && !lastNumber.equals("")) {
            Element field = new Element("field");
            field.setText(lastNumber);
            pack.addContent(field);
        }
        return pack;
    }
    
	public void cathSuccesEvent(SuccessEvent e) {
		String numeration = e.getNdocument();
		if (e.getIdPackage().equals(idTransaction)) {
			lastNumber = numeration;
			System.out.println("Exportando: "+lastNumber);
			GFforma.setExternalValues("lastnumber",lastNumber);
			actionFinish=true;
		}

	}

	public void cathErrorEvent(ErrorEvent e) {
		// TODO Auto-generated method stub
		String numeration = e.getNdocument();
		if (idTransaction!=null && idTransaction.equals(e.getIdPackage())) {
			lastNumber = numeration;
			badActionFinish=true;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getClickCount() == 1) { 
			JButton b = (JButton) e.getSource();
			this.actionThread(b.getActionCommand());
		}
	}
}

class Components {

    private String driver;
    private String method;
    private Element args;
 
    //private boolean report;

    /*public boolean isReport() {
		return report;
	}*/

	/*public void setReport(boolean report) {
		this.report = report;
	}*/

	public String getDriver() {
        return driver;
    }

    public String getMethod() {
        return method;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setMethod(String method) {
        this.method = method;
    }

	public Element getArgs() {
		return args;
	}

	public void setArgs(Element args) {
		this.args = args;
	}
	
	public boolean isContainsArgs() {
		if (args!=null) {
			return true;
		}
		return false;
	}
}

class XMLButton {
	JButton button;
	String typePackage;
	String idReport;
	
	public XMLButton(JButton button) {
		this.button=button;
	}
	public XMLButton(JButton button,String typePackage,String idReport) {
		this.button=button;
		this.typePackage=typePackage;
		this.idReport=idReport;
	}
	
	public JButton getButton() {
		return button;
	}
	public String getIdReport() {
		return idReport;
	}
	public String getTypePackage() {
		return typePackage;
	}
}
