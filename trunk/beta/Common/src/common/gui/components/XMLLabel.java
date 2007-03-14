package common.gui.components;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdom.Document;
import org.jdom.Element;

import common.gui.forms.EndEventGenerator;
import common.gui.forms.GenericForm;
import common.gui.forms.NotFoundComponentException;
import common.misc.language.Language;

/**
 * XMLLabel.java Creado el 16-may-2005
 * 
 * Este archivo es parte de JClient
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
 * Componente que representa a un JLabel.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class XMLLabel extends JLabel implements Couplable {

	private static final long serialVersionUID = -3279052585377122227L;
    private JPanel panel;
    private GenericForm GFforma;
    private Vector <String>driverEvent;
    private Vector <String>keySQL;
    private String mode;
	private String namebutton = "SAVE";
    private String exportValue;
    
    public XMLLabel(GenericForm GFforma,Document doc) {
        Iterator parameters = doc.getRootElement().getChildren().iterator();
        this.GFforma=GFforma;
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        driverEvent = new Vector<String>();
        keySQL = new Vector<String>();
        while (parameters.hasNext()) {
            Element e = (Element)parameters.next();
            String name = e.getName();
            if ("arg".equals(name) && e.getAttributeValue("attribute").equals("text")) {
            	String text = Language.getWord(e.getValue());
                this.setText(!text.equals("")?text:e.getValue());
            } else if ("arg".equals(name) && e.getAttributeValue("attribute").equals("alignment")) {
                if (e.getValue().equals("CENTER")) {
                	layout.setAlignment(FlowLayout.CENTER);
                }
                else if (e.getValue().equals("LEFT")) {
                	layout.setAlignment(FlowLayout.LEFT);
                }
                else if (e.getValue().equals("RIGTH")) {
                	layout.setAlignment(FlowLayout.RIGHT);
                }
            }
            else if ("driverEvent".equals(e.getAttributeValue("attribute"))) {
            	String id="";
            	if (e.getAttributeValue("id")!= null) {
            		id=e.getAttributeValue("id");
            	}
            	if (!driverEvent.contains(e.getValue()+id))
            		driverEvent.addElement(e.getValue()+id);
            }
            else if ("keySQL".equals(e.getAttributeValue("attribute"))) {
            	keySQL.addElement(e.getValue()); 
            }
            else if ("mode".equals(e.getAttributeValue("attribute"))) {
            	mode=e.getValue(); 
            }
            else if ("image".equals(e.getAttributeValue("attribute"))) {
            	setIcon(new ImageIcon(this.getClass().getResource(e.getValue()))); 
            }
            else if ("arg".equals(name) && e.getAttributeValue("attribute").equals("font")) {
            	try {
					StringTokenizer STfont = new StringTokenizer(e.getValue(),",");
					Font font = new Font(STfont.nextToken(),
					        		Integer.parseInt(STfont.nextToken()),
					        		Integer.parseInt(STfont.nextToken()));
		                setFont(font);
            	}
                catch(NumberFormatException NFEe) {
                }
                catch(NoSuchElementException NSEEe) {
                }
            }
        }
        panel = new JPanel(layout);
        panel.add(this);
    }

    public Component getPanel() {
        return panel;
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }

	public void clean() {
        this.setText("");
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

	public Element getPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Element getPackage() throws  VoidPackageException {
        Element pack = new Element("package");
        if (!this.getText().equals("")) {
            Element field = new Element("field");
            field.setText(this.getText());
            pack.addContent(field);
        }
        return pack;
	}

	public Element getPrintPackage() {
		try {
			return getPackage();
		}
		catch(VoidPackageException e) {
			return null;
		}
	}

	public void validPackage(Element args) throws Exception {
		getPackage(args);
	}

	public void arriveAnswerEvent(AnswerEvent e) {
		for (int i=0 ; i < keySQL.size() ; i++) {
			if (e.getSqlCode().equals(keySQL.get(i))) {
				try {
					Document doc = e.getDocument();
			        Element el = doc.getRootElement().getChild("row");
			        int row = el.getChildren().size();
			        if (e.getSqlCode().equals(keySQL.get(i)) && i==0) {
			        	clean();
			        }
			        if (row>0) {
			        	Element printPack = new Element("package");
			        	Iterator it = el.getChildren().iterator();
			        	while(it.hasNext()) {
			        		String val = ((Element)it.next()).getValue().trim();
			        		/*if (!"".equals(val))*/
                            Element field = new Element("field");
                            field.setText(val);
                            printPack.addContent(field);
			        		this.setText(val);
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
			        if (e.getSqlCode().equals(keySQL.get(i)) && i==0) {
			        	clean();
			        }
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
		}
	}

	public void initiateFinishEvent(EndEventGenerator e) {
		try {
			for (int i=0 ; i < driverEvent.size() ; i++) {
				GFforma.invokeMethod(
						(String)driverEvent.get(i),
						"addAnswerListener",
						new Class[]{AnswerListener.class},new Object[]{this});
			}
		}
		catch(NotFoundComponentException NFCEe) {
			NFCEe.printStackTrace();
		} 
		catch (InvocationTargetException ITEe) {
			ITEe.printStackTrace();
		}
	}
}
