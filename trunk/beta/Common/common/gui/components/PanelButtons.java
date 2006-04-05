package jmlib.gui.components;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import jmlib.gui.formas.GenericForm;
import jmlib.gui.formas.NotFoundComponentException;
import jmlib.miscelanea.Icons;
import jmlib.miscelanea.idiom.Language;
import jmlib.pdf.pdfviewer.PDFViewer;
import jmlib.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * PanelButtons.java Creado el 22-sep-2004
 * 
 * Este archivo es parte de JMServerII <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
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
public class PanelButtons extends JPanel implements ActionListener, KeyListener {

    private static final long serialVersionUID = 8883108186183650115L;

    private GenericForm GFforma;
    private Hashtable <String,Vector>Heventos;
    private Hashtable <String,Vector>Hform;
    private Hashtable <String,JButton>Hbuttons;
    private String idTransaction;
    private String accel = "";
	private String typePackage = "TRANSACTION";
	private String idReport;
    
    public PanelButtons(GenericForm GFforma, Document doc) {

        this.GFforma = GFforma;
        this.setLayout(new FlowLayout());

        Heventos = new Hashtable<String,Vector>();
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

    private JButton buildButton(String label,String icon,boolean enabled, String keyStroke) {
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
            ((JButton)(Hbuttons.get(name))).setEnabled(bool);
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
        Vector <Componentes>componentes = new Vector<Componentes>();
        Vector <Element>formularios = new Vector<Element>();

        while (i.hasNext()) {
            Element e = (Element) i.next();
            String name = e.getName();
            if (name.equals("name")) {
                key = e.getValue();
            } else if (name.equals("component")) {
                componentes.addElement(loadComponent(e));
            } else if (name.equals("FORM")) {
            	formularios.addElement(e);
            }
        }
        if (componentes.size()>0) {
        	Heventos.put(key, componentes);
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

    private void callEvent(String action) 
    throws InvocationTargetException,NotFoundComponentException {

    	Vector vec = Heventos.get(action);
        Vector <Element>pack = new Vector<Element>();
        Element elementos = null;
        Element multielementos[] = null;
        
        for (int i = 0; i < vec.size(); i++) {
            Componentes comp = (Componentes) vec.elementAt(i);
            
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
		String namePackage = !"REPORT".equals(action)?"TRANSACTION":typePackage;
        if (pack.size() > 0 || !namePackage.equals("TRANSACTION")) {
       		try {
       			formatPackageStructure(pack,typePackage);
			} catch (MalformedProfileException e) {
				e.printStackTrace();
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
            }
            catch (NotFoundComponentException NFCEe) {
                NFCEe.printStackTrace();
            }
        }

    }
    
    private void formatPackageStructure(Vector pack,String packageName) throws MalformedProfileException {
        Document transaction = new Document();
        transaction.setRootElement(new Element(packageName));

        Element driver = new Element("driver");
        if ("TRANSACTION".equals(packageName)) {
        	driver.setText(GFforma.getIdTransaction());
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

        GFforma.sendTransaction(transaction);
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
    		((JButton)Hbuttons.get(name)).setEnabled(Boolean.parseBoolean(state));
    	}
    	else {
    		System.out.println("Inconsistencia en la parametrizacion en los botones");
    	}
    }
}

class Componentes {

    private String driver;
    private String method;
    private Element args;
    private boolean report;

    public boolean isReport() {
		return report;
	}

	public void setReport(boolean report) {
		this.report = report;
	}

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


