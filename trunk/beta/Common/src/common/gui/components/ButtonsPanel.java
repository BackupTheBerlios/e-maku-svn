package common.gui.components;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.print.PrintException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import common.control.ClientHeaderValidator;
import common.control.SuccessEvent;
import common.control.SuccessListener;
import common.gui.forms.GenericForm;
import common.gui.forms.NotFoundComponentException;
import common.misc.Icons;
import common.misc.language.Language;
import common.pdf.pdfviewer.PDFViewer;
import common.printer.PlainManager;
import common.printer.PostScriptManager;
import common.printer.PrintManager;
import common.printer.PrintManager.ImpresionType;
import common.transactions.STResultSet;

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
public class ButtonsPanel extends JPanel implements ActionListener, KeyListener,SuccessListener{

    private static final long serialVersionUID = 8883108186183650115L;

    private GenericForm GFforma;
    private Hashtable <String,Vector<?>>Heventos;
    private Hashtable <String,Vector>Hform;
    private Hashtable <String,JButton>Hbuttons;
    private String idTransaction;
    private String accel = "";
	private String idReport;
    private PlainManager plainManager = new PlainManager("");
    private PostScriptManager postScriptManager = new PostScriptManager();
	private String typePackage = "TRANSACTION";
	private String lastNumber;
	private boolean actionFinish;

    
    public ButtonsPanel(GenericForm GFforma, Document doc) {

        this.GFforma = GFforma;
        this.setLayout(new FlowLayout());
		ClientHeaderValidator.addSuccessListener(this);

        Heventos = new Hashtable<String,Vector<?>>();
        Hbuttons = new Hashtable<String,JButton>();
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
            if (label!=null) {
            	button = buildButton(label,icon,enabled,keyStroke);
            	name = label;
            }
            else {
	            button = buildButton(name,enabled);
            }
            Hbuttons.put(name, button);
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
		button.addActionListener(this);
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
                         ((JButton)evt.getSource()).doClick();
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
            Hbuttons.get(name).setEnabled(bool);
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
        Vector<Componentes> componentes = new Vector<Componentes>();
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

    private Componentes loadComponent(Element elm) {
        Iterator i = elm.getChildren().iterator();

        Componentes comp = new Componentes();

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
        if (obj instanceof Componentes) {
        	builTransaction(vec,action,null,null);
        }
        else if (obj instanceof Element) {
        	for (Object object : vec) {
        		actionFinish=false;
	        	Element element= (Element) object;
	    		String type = element.getChildText("type");
	    		String idManualTransaction = element.getChildText("idManualTransaction");
	    		Iterator it = element.getChildren("component").iterator();
    			Vector<Componentes> vector = new Vector<Componentes>();
    			while (it.hasNext()) {
        			Componentes comp = loadComponent((Element) it.next());
            		vector.add(comp);
    			}
    			System.out.println("Generando transaccion tipo: "+type);
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
	    			try {
						SAXBuilder sax = new SAXBuilder(false);
						Document template = null;
						URL url = this.getClass().getResource(pathTemplate);
						if (url!=null){
							template = sax.build(url);
							Element rootTemplate= template.getRootElement();
							Attribute ATType    = rootTemplate.getAttribute("type");
							Attribute ATSilent  = rootTemplate.getAttribute("silent");
							Attribute ATCopies  = rootTemplate.getAttribute("copies");
							Attribute ATprinter  = rootTemplate.getAttribute("printer");
							
							boolean silent = ATSilent!=null ? ATSilent.getBooleanValue() : false;
							int copies     = ATCopies!=null ? ATCopies.getIntValue() : 1;
							String _type   = ATType.getValue();
							String printer = ATprinter!=null && 
											 !ATprinter.getValue().trim().equals("") ?  
											 ATprinter.getValue() : 
											 null ;
							if ("PLAIN".equals(_type) ) {
								plainManager.process(rootTemplate,printJob);
								if (plainManager.isSusseful()) {
									System.out.println("================================");
									System.out.println(plainManager.toString());
									System.out.println("================================");
									ImpresionType        IType     = plainManager.getImpresionType();
									ByteArrayInputStream IStream   = plainManager.getStream();
									new PrintManager(IType,IStream, silent, copies,printer);
									plainManager = new PlainManager(plainManager.getNdocument());
								}
							}
							if ("GRAPHIC".equals(_type) ) {
								postScriptManager.load(rootTemplate,printJob);
								new PrintManager(postScriptManager,silent, copies,printer);
								postScriptManager = new PostScriptManager();
							}
							System.gc();
						}
						else {
							System.out.println("Plantilla "+pathTemplate+" no encontrada");
							JOptionPane.showInternalMessageDialog(GFforma,"NO SE ENCONTRO LA PLANTILLA DE IMPRESION");
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
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
    
    private synchronized void builTransaction(Vector<Componentes> vec,String action,Element printJob,String idManualTransaction) 
    throws InvocationTargetException, NotFoundComponentException, IOException {
        Vector <Element>pack = new Vector<Element>();
    	Element multielementos[] = null;
        Element elementos = null;
    	for (int i = 0; i < vec.size(); i++) {
    		Componentes comp = vec.get(i);
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
    		if (!"REPORT".equals(action)) {
    			namePackage="TRANSACTION";
    		}
    		else {
    			namePackage=typePackage;
    		}
    		
		    if (pack.size() > 0 || !namePackage.equals("TRANSACTION")) {
		   		try {
		   			formatPackageStructure(pack,typePackage,idManualTransaction);
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
    
    public void actionPerformed(ActionEvent e) {
        actionEvent(e.getActionCommand());
    }

    private void actionEvent(String action) {
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

                    button = (JButton) Hbuttons.get("SAVE");
                    if (button != null) {
                        button.setEnabled(true);
                    }
                    button = (JButton) Hbuttons.get("SAVEAS");
                    if (button != null) {
                        button.setEnabled(true);
                    }
                    
                    button = (JButton) Hbuttons.get(action);
//                    button.setEnabled(false);

                } else if (action.equals("SAVE") || action.equals("SAVEAS")) {

                    button = (JButton) Hbuttons.get("NEW");
                    if (button != null) {
                        button.setEnabled(true);
                    }
                    button = (JButton) Hbuttons.get("SAVEAS");
                    if (button != null) {
                        button.setEnabled(true);
                    }
                    button = (JButton) Hbuttons.get("PRINT");
                    if (button != null) {
                        button.setEnabled(true);
                    }
                    button = (JButton) Hbuttons.get(action);
                    button.setEnabled(false);
                }
                
            }
            catch (InvocationTargetException ITEe) {
                ITEe.printStackTrace();
                JOptionPane.showInternalMessageDialog(GFforma.getDesktopPane(),
                        ITEe.getCause().getMessage(), Language
                                .getWord("ERROR_MESSAGE"),
                        JOptionPane.ERROR_MESSAGE);
                Hbuttons.get(action).setEnabled(true);
            }
            catch (NotFoundComponentException NFCEe) {
                NFCEe.printStackTrace();
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

    }
    
    private void formatPackageStructure(Vector pack,String packageName,String idManualTransaction) 
    throws MalformedProfileException, IOException {
        Document transaction = new Document();
        transaction.setRootElement(new Element(packageName));

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
        else {
        	throw new MalformedProfileException(Language.getWord("MALFORMED_PROFILE")+GFforma.getIdTransaction());
        }
        
        Element id = new Element("id");
        idTransaction = "T"+STResultSet.getId();
        id.setText(idTransaction);
        plainManager.setIdTransaction(idTransaction);

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
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        try {
			out.output(transaction,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        GFforma.sendTransaction(transaction);

        int times = 0;
		while (!actionFinish && "TRANSACTION".equals(packageName)) {
			try {
				if (times<=100) {
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
    public synchronized String getIdTransaction() {
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
    			((JButton)Hbuttons.get(name)).setEnabled(Boolean.parseBoolean(state));
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
    	System.out.println("Valor de lastNumber: "+lastNumber);
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
			actionFinish=true;
		}

	}
}

class Componentes {

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