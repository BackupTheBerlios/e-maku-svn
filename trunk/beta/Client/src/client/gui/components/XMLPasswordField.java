package client.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.jdom.Document;
import org.jdom.Element;

import client.misc.MD5;

import common.gui.components.AnswerEvent;
import common.gui.components.Couplable;
import common.gui.components.VoidPackageException;
import common.gui.forms.EndEventGenerator;
import common.gui.forms.GenericForm;
import common.misc.language.Language;
import common.misc.text.TextDataValidator;

public class XMLPasswordField extends JPasswordField implements Couplable, FocusListener {

	private static final long serialVersionUID = 1L;
	private GenericForm gform;
	private Font font;
	private JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private String exportValue;
	private boolean md5;
	private boolean withPanel = true;
	private String name;
	
	public XMLPasswordField(GenericForm GFforma, Document doc) {
		this.gform = GFforma;
		Element parameters = doc.getRootElement();
        Iterator i = parameters.getChildren().iterator();

        while (i.hasNext()) {
            Element subargs = (Element) i.next();
            String value = subargs.getValue();
            if ("enabled".equals(subargs.getAttributeValue("attribute"))) {
            	this.setEditable(Boolean.parseBoolean(value));
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
			else if ("exportValue".equals(subargs.getAttributeValue("attribute"))) {
				exportValue = value;
			}
			else if ("size".equals(subargs.getAttributeValue("attribute"))) {
				this.setColumns(Integer.parseInt(value));
			}
			else if ("maxlength".equals(subargs.getAttributeValue("attribute"))) {
				this.setDocument(new TextDataValidator(Integer.parseInt(value)));
			}
			else if ("md5".equals(subargs.getAttributeValue("attribute"))) {
				md5=Boolean.parseBoolean(value);
			}
			else if ("Panel".equals(subargs.getAttributeValue("attribute"))) {
				withPanel=Boolean.parseBoolean(value);
			}
			else if ("name".equals(subargs.getAttributeValue("attribute"))) {
				name=Language.getWord(value);
			}
        }
        if (font!=null) {
        	this.setFont(font);
        }
        this.addFocusListener(this);
        panel.add(this);
        gform.addInitiateFinishListener(this);
	}
	
	public void clean() {
		setText("");
	}

	public boolean containData() {
		char [] c = getPassword();
		String s = new String (c);
		if (!"".equals(s.trim())) {
			return true;
		}
		return false;
	}

	public Element getPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Element getPackage() throws VoidPackageException {
		char [] c = getPassword();
		String s = new String(c).trim();
		String sfinal = null;
		if (md5) {
			try {
				MD5 md5 = new MD5(s);
				sfinal=md5.getDigest();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		else {
			sfinal=s;
		}
		
		if ("".equals(sfinal)) {
			throw new VoidPackageException(name);
		} 
		
		Element pack = new Element("package");
		Element field = new Element("field");
		field.setText(sfinal);
		
		pack.addContent(pack);
		
		return pack;
	}

	public Component getPanel() {
		if (withPanel) {
			return panel;
		}
		return this;
	}

	public Element getPrintPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void validPackage(Element args) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	public void initiateFinishEvent(EndEventGenerator e) {
		// TODO Auto-generated method stub
	}
	
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

	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent e) {
		if (exportValue!=null) {
			String s = new String(getPassword()).trim();
			String sfinal = null;
			if (md5) {
				try {
					MD5 md5 = new MD5(s);
					sfinal=md5.getDigest();
				} catch (NoSuchAlgorithmException ex) {
					ex.printStackTrace();
				}
			}
			else {
				sfinal=s;
			}
			gform.setExternalValues(exportValue,sfinal);
		}
	}
}
