package client.gui.components;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;

import common.gui.components.AnswerEvent;
import common.gui.components.AnswerListener;
import common.gui.components.ChangeValueEvent;
import common.gui.components.ChangeValueListener;
import common.gui.components.QueryData;
import common.gui.components.VoidPackageException;
import common.gui.components.XMLTextField;
import common.gui.formas.GenericForm;
import common.transactions.STException;
import common.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * AdminProdServ.java Creado el 18-abr-2005
 * 
 * Este archivo es parte de E-Maku
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
 * Esta clase se utiliza para generar un paquete necesario para la creacion de 
 * productos y/o servicios
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class AdminProdServ extends JPanel implements FocusListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2549815723035494574L;
	private GenericForm GFforma;
    private XMLTextField XMLTFcodigo;
    private XMLTextField XMLTFbarras;
    private XMLComboBox XMLCBgrupoImp;
    private XMLTextField XMLTFdescripcion;
    private XMLTextField XMLTFmarca;
    private XMLTextField XMLTFpeso;
    private XMLTextField XMLTFiva;
    private XMLTextField XMLTFpcosto;
    private XMLCheckBox XMLCBestado;

    private String valueArgs="NEW";
    private String namebutton = "SAVE";
    private Vector<String> sqlCode;
    private boolean enablebutton = true;
    
    private Vector<AnswerListener> AnswerListener = new Vector<AnswerListener>();
    private Vector<ChangeValueListener> changeValueListener = new Vector<ChangeValueListener>();
    
    private boolean search;
	private String keyExternalValue;

    public AdminProdServ(GenericForm GFforma, Document doc) throws ArgsException {
        this.GFforma = GFforma;
        this.setLayout(new BorderLayout());

        /*
         * Captura de argumentos de la clase
         */
        
        Element args = doc.getRootElement();
        Iterator i = args.getChildren().iterator();
        sqlCode = new Vector<String>();
        for (int j=0;i.hasNext();j++) {
            Element e = (Element) i.next();
            if ("sqlCode".equals(e.getAttributeValue("attribute"))) {
                sqlCode.add(e.getValue());
            } else if ("valueArgs".equals(e.getAttributeValue("attribute"))) {
                valueArgs=e.getValue();
            } else if ("keyExternalValue".equals(e.getAttributeValue("attribute"))) {
                keyExternalValue=e.getValue();
            } 
        }

        if (valueArgs.equals("EDIT")) {
            enablebutton = false;
            namebutton = "SAVEAS";
            
        }
        else if (valueArgs.equals("DELETE")) {
            enablebutton = false;
            namebutton = "DELETE";
        }
        
        JPanel JPetiquetas = new JPanel(new GridLayout(8,1));
        JPanel JPfields = new JPanel(new GridLayout(8,1));

        XMLTFcodigo = new XMLTextField("CODE", 10, 13);
        XMLTFbarras = new XMLTextField("BARRAS", 10, 13,XMLTextField.TEXT);

        XMLTFdescripcion = new XMLTextField("DESCRIPCION", 25, 50);
        XMLCBgrupoImp = new XMLComboBox(GFforma,"SEL0125", "GRUPOIMPUESTOS",true);
        XMLTFmarca = new XMLTextField("MARCA", 25, 50);
        XMLTFpeso = new XMLTextField("PESO", 10, 13,XMLTextField.NUMERIC);
        XMLTFiva = new XMLTextField("IVA", 10, 13,XMLTextField.NUMERIC);
        XMLTFpcosto = new XMLTextField("PCOSTO", 10, 13,XMLTextField.NUMERIC);
        
        XMLTFpeso.addFocusListener(this);
        XMLTFiva.addFocusListener(this);
        XMLTFpcosto.addFocusListener(this);
        
        XMLTFcodigo.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				search = true;
			}
		});
        XMLTFcodigo.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                class SearchQuery extends Thread {
                    public void run() {
                        searchQuery();
                    }
                }
                if (search) {
                		new SearchQuery().start();
                		search = false;
                }
            }
        });
        
        JPetiquetas.add(XMLTFcodigo.getLabel());
        JPetiquetas.add(XMLTFbarras.getLabel());
        JPetiquetas.add(XMLTFdescripcion.getLabel());
        JPetiquetas.add(XMLCBgrupoImp.getLabel());
        JPetiquetas.add(XMLTFmarca.getLabel());
        JPetiquetas.add(XMLTFpeso.getLabel());
        JPetiquetas.add(XMLTFiva.getLabel());
        JPetiquetas.add(XMLTFpcosto.getLabel());
        
        JPfields.add(XMLTFcodigo.getJPtext());
        JPfields.add(XMLTFbarras.getJPtext());
        JPfields.add(XMLTFdescripcion.getJPtext());
        JPfields.add(XMLCBgrupoImp.getJPcombo());
        JPfields.add(XMLTFmarca.getJPtext());
        JPfields.add(XMLTFpeso.getJPtext());
        JPfields.add(XMLTFiva.getJPtext());
        JPfields.add(XMLTFpcosto.getJPtext());
        
        XMLCBestado = new XMLCheckBox("ESTADOA");

        if (valueArgs.equals("DELETE")){
            setDisabled();
        }
        
        this.add(JPetiquetas,BorderLayout.WEST);
        this.add(JPfields,BorderLayout.CENTER);
        this.add(XMLCBestado.getJPcheck(),BorderLayout.SOUTH);
    }

    /**
     * Este metodo se encarga de consultar un producto
     */
    private void searchQuery() {
        QueryData QDsearch = new QueryData( 
                						   GFforma,
                						   namebutton, 
                						   enablebutton, 
                						   sqlCode.get(0),
                						   null, 
                						   XMLTFcodigo.getText(),
						                   new XMLTextField[] { XMLTFbarras, 
            													XMLTFdescripcion,
            													XMLTFmarca,
            													XMLTFpeso,
            													XMLTFiva,
            													XMLTFpcosto});
        // Consultando Asientos genericos
        new QueryComboBox(GFforma, 
        			      sqlCode.get(1),
        			      XMLTFcodigo.getText(), 
        			      XMLCBgrupoImp).start();

        if (QDsearch.searchQuery()) {
            new QueryCheckBox(GFforma, 
		      		  sqlCode.get(2),
		      		  XMLTFcodigo.getText(), 
		      		  XMLCBestado).start();
            
            othersQueries();
        }

    }
    
    /**
     * Metodo encargado de notificar la llegada de un paquete <answer/>
     * @param event 
     */
    private synchronized void notificando(AnswerEvent event) {
        for (int i=0; i< AnswerListener.size();i++) {
            AnswerListener listener = AnswerListener.elementAt(i);
            listener.arriveAnswerEvent(event);
        }
    }
    
    /**
     * Este metodo se encarga de hacer otras consultas si estas existen
     */
    private void othersQueries() {
        String sql;
        for (int i=3;i<sqlCode.size();i++) {
            Document doc = null;
            sql = sqlCode.get(i);
            try {
                doc = STResultSet.getResultSetST(sql,new String[]{XMLTFcodigo.getText()});
            }
            catch (STException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            AnswerEvent event = new AnswerEvent(this,sql,doc);
            notificando(event);
        }
    }
    
    /**
     * Metodo encargado de desactivar los componentes de la forma
     *
     */
    private void setDisabled() {
        XMLTFbarras.setEnabled(false);
        XMLTFdescripcion.setEnabled(false);
        XMLTFmarca.setEnabled(false);
        XMLTFpeso.setEnabled(false);
        XMLTFiva.setEnabled(false);
        XMLTFpcosto.setEnabled(false);
        XMLCBestado.setEnabled(false);
    }
    
    /**
     * Retorna el panel que contiene los todos los objetos graficos
     */
    public JPanel getPanel() {
        return this;
    }

    /** 
     * Metodo encargado de limpiar todos los objetos graficos
     */
    public void clean() {
        XMLTFcodigo.setText("");
        XMLTFbarras.setText("");
        XMLTFdescripcion.setText("");
        XMLTFmarca.setText("");
        XMLTFpeso.setText("");
        XMLTFiva.setText("");
        XMLTFpcosto.setText("");
        XMLCBestado.setSelected(false);
    }

    /**
     * Metodo encargado de generar un <package/>
     * @return retorna un elemento <package>
     * @throws VoidPackageException Esta excepcion se lanza en caso de que la informacion
     *         del componente este incompleta
     */
    
    public Element getPackage() throws VoidPackageException {
        Element pack = new Element("package");
        if (!XMLTFcodigo.getText().equals("")) {
            if (!XMLTFbarras.getText().equals("")) {
                    if (!XMLTFdescripcion.getText().equals("")) {
	                    if (!XMLTFmarca.getText().equals("")) {
	                        if (!XMLTFiva.getText().equals("")) {
	                            if (!XMLTFpcosto.getText().equals("")) {
	                                
	                            	if (valueArgs.equals("NEW")) {
	                                    pack.addContent(XMLTFcodigo.getElementText("key"));
	                                }
	                                
	                                if (valueArgs.equals("NEW") || valueArgs.equals("EDIT")) {
		                                pack.addContent(XMLTFbarras.getElementText());
			                            pack.addContent(XMLTFdescripcion.getElementText());
			                            
			                            if ("".equals(XMLCBgrupoImp.getStringCombo().trim())) {
			                            		pack.addContent(XMLCBgrupoImp.getNullValue());
			                            }
			                            else {
			                            		pack.addContent(XMLCBgrupoImp.getElementCombo());
			                            }
			                            pack.addContent(XMLTFmarca.getElementText());
			                            pack.addContent(XMLTFpeso.getElementText());
			                            pack.addContent(XMLTFiva.getElementText());
			                            pack.addContent(XMLTFpcosto.getElementText());
			                            pack.addContent(XMLCBestado.getElementCheck());
	                                }
	                                
	                                if (valueArgs.equals("EDIT")) {
	                                    pack.addContent(XMLTFcodigo.getElementText("key"));
	                                }
	                                
	                                if (valueArgs.equals("DELETE")) {
	                                    pack.addContent(XMLTFcodigo.getElementText());
	                                }
	                            }
	                            else {
	                                throw new VoidPackageException(XMLTFpcosto.getLabel().getName());
	                            }
	                        }
	                        else {
	                            throw new VoidPackageException(XMLTFiva.getLabel().getName());
	                        }
	                    }
	                    else {
	                        throw new VoidPackageException(XMLTFmarca.getLabel().getName());
	                    }
                    }
                    else {
                        throw new VoidPackageException(XMLTFdescripcion.getLabel().getName());
                    }
            }
            else {
                throw new VoidPackageException(XMLTFbarras.getLabel().getName());                
            }
        }
        else {
            throw new VoidPackageException(XMLTFcodigo.getLabel().getName());            
        }
        
        return pack;
    }
    
    public synchronized void addAnswerListener(AnswerListener listener ) {
        AnswerListener.addElement(listener);
    }

    public synchronized void removeAnswerListener(AnswerListener listener ) {
        AnswerListener.removeElement(listener);
    }

	public void focusGained(FocusEvent e) {
		
	}

	public void focusLost(FocusEvent e) {
		XMLTextField XMLRefText = (XMLTextField) e.getSource();
		if (XMLTextField.NUMERIC.equals(XMLRefText.getType())) {
			try {
				XMLRefText.setNumberValue(Double.parseDouble(XMLRefText.getText()));
			}
			catch (NumberFormatException NFEe) {
				NFEe.printStackTrace();
			}
		}
    	if (XMLRefText.equals(XMLTFpcosto)) {
    		if (XMLTFiva.getText().equals("") || XMLTFiva.getNumberValue() <= 0 ) {
    			int value = Double.valueOf(XMLRefText.getNumberValue()).intValue();
    			XMLRefText.setText(String.valueOf(value));
    			notificando(new ChangeValueEvent(this));
    		}
    	}
	}

	public synchronized void addChangeValueListener(ChangeValueListener listener ) {
        changeValueListener.addElement(listener);
    }

    public synchronized void removeChangeValueListener(ChangeValueListener listener ) {
        changeValueListener.removeElement(listener);
    }
    
    private synchronized void notificando(ChangeValueEvent event) {
        /*Vector lista;
        lista = (Vector)changeValueListener.clone();*/
        for (int i=0; i<changeValueListener.size();i++) {
            ChangeValueListener listener = changeValueListener.elementAt(i);
            listener.changeValue(event);
        }
    }

	public Double getDoubleValue(String key) {
		if (keyExternalValue!=null && key.equals(keyExternalValue)) {
			try {
				Double valret = new Double(XMLTFiva.getNumberValue());
				return valret;
			}catch (NumberFormatException NFEe) {
				return new Double(0); 
			}
		}
		return new Double(0);
	}
}
