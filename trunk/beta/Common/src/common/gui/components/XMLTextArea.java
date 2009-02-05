package common.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jdom.Document;
import org.jdom.Element;

import common.gui.forms.EndEventGenerator;
import common.gui.forms.GenericForm;
import common.gui.forms.NotFoundComponentException;

/**
 * XMLTextArea.java Creado el 16-may-2005
 * 
 * Este archivo es parte de JMClient
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
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
 */
public class XMLTextArea extends JTextArea implements Couplable, FocusListener, RecordListener {

    private static final long serialVersionUID = -1097007812890286286L;
    private JScrollPane JSPpanel;
    private GenericForm GFforma;
    private Vector <String>driverEvent;
    private Vector <String>keySQL;
    //private Element printPack;
    private String mode;
	private String namebutton = "SAVE";
    private String exportValue;
    private int fieldIndexRecord = -1;
    private ArrayList<String> recordEvent;
    
    public XMLTextArea(GenericForm GFforma) {
    	this.GFforma = GFforma;
        JSPpanel = new JScrollPane(this);
    }
    
    public XMLTextArea(GenericForm GFforma, Document doc) {
    	this.GFforma = GFforma;
        JSPpanel = new JScrollPane(this);
        JSPpanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JSPpanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        driverEvent = new Vector<String>();
        recordEvent = new ArrayList<String>();
        keySQL = new Vector<String>();
        Font font = null;
        
        Element parameters = doc.getRootElement();
        Iterator i = parameters.getChildren().iterator();
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        while (i.hasNext()) {
            Element subargs = (Element) i.next();
            String value = subargs.getValue();
            if ("enabled".equals(subargs.getAttributeValue("attribute"))) {
            	this.setEditable(Boolean.parseBoolean(value));
            }
            else if ("keySQL".equals(subargs.getAttributeValue("attribute"))) {
            	keySQL.addElement(value); 
            }
            else if ("driverEvent".equals(subargs.getAttributeValue("attribute"))) {
            	String id="";
            	if (subargs.getAttributeValue("id")!= null) {
            		id=subargs.getAttributeValue("id");
            	}
            	if (!driverEvent.contains(value+id))
            		driverEvent.addElement(value+id);
            }
            else if ("rows".equals(subargs.getAttributeValue("attribute"))) {
            	this.setRows(Integer.parseInt(value));
            }
            else if ("cols".equals(subargs.getAttributeValue("attribute"))) {
            	this.setColumns(Integer.parseInt(value));
            }
            else if ("fieldIndexRecord".equals(subargs.getAttributeValue("attribute"))) {
            	this.fieldIndexRecord = Integer.parseInt(value);
            }
            else if ("colorBackground".equals(subargs.getAttributeValue("attribute"))) {
            	this.setBackground(getColor(subargs.getValue()));
            }
            else if ("colorForeground".equals(subargs.getAttributeValue("attribute"))) {
            	this.setForeground(getColor(subargs.getValue()));
            }
            else if ("background".equals(subargs.getAttributeValue("attribute"))) {
            	this.setBackground(getColor(subargs.getValue()));
            }
            else if ("foreground".equals(subargs.getAttributeValue("attribute"))) {
            	this.setForeground(getColor(subargs.getValue()));
            }
            else if ("font".equals(subargs.getAttributeValue("attribute"))) {
				try {
					StringTokenizer STfont = new StringTokenizer(subargs.getValue(), ",");
					font = new Font(
					                STfont.nextToken(),
					                Integer.parseInt(STfont.nextToken()),
					                Integer.parseInt(STfont.nextToken()));
				} catch (NumberFormatException NFEe) {
					font = null;
				} catch (NoSuchElementException NSEEe) {
					font = null;
				}
			} 
            else if ("mode".equals(subargs.getAttributeValue("attribute"))) {
				mode = value;
				if (mode.equals("NEW")) {
					namebutton="SAVE";
				}
				else if (mode.equals("EDIT")) {
					namebutton="SAVEAS";
				}
				else if (mode.equals("DELETE")){
					namebutton="DELETE";
				}
			} 
			else if ("exportValue".equals(subargs.getAttributeValue("attribute"))) {
				exportValue = value;
			}
			else if ("exportValue".equals(subargs.getAttributeValue("attribute"))) {
				exportValue = value;
			}
			else if ("driverEventRecord".equals(subargs.getAttributeValue("attribute"))) {
				String id = "";
				if (subargs.getAttributeValue("id") != null) {
					id = subargs.getAttributeValue("id");
				}
				recordEvent.add(subargs.getValue() + id);
			} 
        }
        if (font!=null) {
        	this.setFont(font);
        }
        this.GFforma.addInitiateFinishListener(this);
        this.addFocusListener(this);
    }
    
    /**
     * Este metodo retorna un objeto color, apartir de los argumentos recibidos
     * @param color argumetos de colores
     * @return objeto Color
     */
    private Color getColor(String color) {
        try {
	        StringTokenizer STcolor = new StringTokenizer(color,",");
	        return new Color(Integer.parseInt(STcolor.nextToken()),
			                 Integer.parseInt(STcolor.nextToken()),
			                 Integer.parseInt(STcolor.nextToken()));
        }
        catch (NumberFormatException NFEe) {
            return null;
        }
        catch(NoSuchElementException NSEEe) {
            return null;
        }
    }
    public String getTextPane() {
        return this.getText();
    }
    
    public void setTextPane(String text) {
        this.setText(text);
    }
    
    public Component getPanel() {
        return JSPpanel;
    }
    
    public JTextArea getJTPpane() {
        return this;
    }
    
    public void clean() {
        this.setText("");
    }
    
    public Element getPackage() {
        Element pack = new Element("package");
        if (!this.getText().equals("")) {
            Element field = new Element("field");
            field.setText(this.getText());
            pack.addContent(field);
        }
        return pack;
    }
    
    public Element getPrintPackage() {
        return getPackage();
    }
    
    public Element getSeparatedPrintPackage() {
    	String acumText = "";
    	Element pack = new Element("package");
    	for (char c : getText().toCharArray()) {
    		if (c!='\n') {
    			acumText+=c;
    		}
    		else {
    			Element field = new Element("field");
                field.setText(acumText);
                pack.addContent(field);
                acumText="";
    		}
    	}
    	Element field = new Element("field");
        field.setText(acumText);
        pack.addContent(field);
        
        return pack;
    }

	public void arriveAnswerEvent(AnswerEvent AEe) {
			try {
				Document doc = AEe.getDocument();
		        Element e = doc.getRootElement().getChild("row");
		        int row = e.getChildren().size();
		        clean();
		        if (row>0) {
                    //printPack = new Element("package");
		        	Iterator it = e.getChildren().iterator();
		        	while(it.hasNext()) {
		        		String val = ((Element)it.next()).getValue().trim();
		        		if (!"".equals(val)) {
		        			Element field = new Element("field");
                        	field.setText(val);
                        	//printPack.addContent(field);
		        		}
		        		this.append(val+"\n");
		        	}
					if (mode != null) {
						if ("NEW".equals(mode)) {
							GFforma.setEnabledButton(namebutton, false);
						} else {
							GFforma.setEnabledButton(namebutton, true);
							// clean();
						}
					}
					if (exportValue!=null) {
						GFforma.setExternalValues(exportValue,this.getText());
					}

		        }
			}
			catch (NullPointerException NPEe) {
	        	clean();
				if (mode != null) {
					if ("NEW".equals(mode)) {
						GFforma.setEnabledButton(namebutton, true);
					} else {
						GFforma.setEnabledButton(namebutton, false);
						// clean();
					}
				}
			}
	
	}

	public void initiateFinishEvent(EndEventGenerator e) {
		try {
			Class[] ac = new Class[]{AnswerListener.class};
			Class[] rc = new Class[]{RecordListener.class};
			Object[] o = new Object[]{this};
			for (int i=0 ; i < driverEvent.size() ; i++) {
				GFforma.invokeMethod(
						(String)driverEvent.get(i),"addAnswerListener",ac,o);
			}
			
			int n=0;
			try {
				while (true) {
					try {
						GFforma.invokeMethod(recordEvent.get(n),"addRecordListener",rc,o);
						n++;
						if (n==recordEvent.size())
							break;
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
						break;
					} catch (NotFoundComponentException e1) {
						Thread.sleep(500);
					}
	
				}
			}
			catch(InterruptedException IEe) {
				IEe.printStackTrace();
			}
		}
		catch(NotFoundComponentException NFCEe) {
			NFCEe.printStackTrace();
		} 
		catch (InvocationTargetException ITEe) {
			ITEe.printStackTrace();
		}
		
	}

	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent e) {
		if (exportValue!=null) {
			GFforma.setExternalValues(exportValue,this.getText());
		}
	}

	public boolean containData() {
		// TODO Auto-generated method stub
		return false;
	}

	public Element getPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void validPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public boolean containSqlCode(String sqlCode) {
		if (keySQL.contains(sqlCode))
			return true;
		else
			return false;
	}

	public synchronized void arriveRecordEvent(RecordEvent e) {
		Element element = e.getElement();
		Element row = element.getChild("row");
		String newText = "";
		boolean passed = true;
		int currIndex = 0;
		if (row!=null) {
			Iterator iterator = row.getChildren().iterator();
			if (iterator.hasNext()) {
				this.setText("");
			}
			while (iterator.hasNext()) {
				Element col = (Element) iterator.next();
				String text = col.getText();
				if (currIndex==fieldIndexRecord) {
					if (text.equals("") || text.equals("0")) {
						passed = false;
						break;
					}
				}
				if ("\\n".equals(text)) {
					newText+="\n";
				}
				else {
					newText+=text;	
				}
				currIndex++;
			}
		}
		if (passed) {
			this.setText(newText);
		}
	}
}
