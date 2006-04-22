package client.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;

import common.gui.components.AnswerEvent;
import common.gui.components.AnswerListener;
import common.gui.components.GenericDataFiller;
import common.gui.components.VoidPackageException;
import common.gui.components.XMLTextField;
import common.gui.forms.GenericForm;
import common.misc.language.Language;
import common.transactions.STException;
import common.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * Third.java Creado el 11-oct-2004
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
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda </A>
 */
public class Third extends JPanel {

	private static final long serialVersionUID = 8563377749593766736L;
	private GenericForm GFforma;
    private XMLTextField XMLTFnitcc;
    private XMLTextField XMLTFnombre;
    private XMLTextField XMLTFestablecimiento;
    private XMLComboBox XMLCBregimen;
    private XMLComboBox XMLCBactividad;
    private XMLCheckBox XMLCBretiene;
    private XMLCheckBox XMLCBestado;
    private String returnValue = "";
    private boolean search;
    private boolean enablebutton = true;
    private String namebutton = "";
	private Vector<String> sqlCode;
    private Vector<AnswerListener> AnswerListener = new Vector<AnswerListener>();
	private XMLComboBox XMLCBcatalogo;
	private String mode = "NEW";
    public Third(GenericForm GFforma) {
        this.GFforma = GFforma;
        Generar();
    }

    public Third(GenericForm GFforma, Document doc) {
        this.GFforma = GFforma;
        Iterator i = doc.getRootElement().getChildren().iterator();
        namebutton = "SAVE";
        sqlCode = new Vector<String>();
        
        while (i.hasNext()) {
            Element elm = (Element) i.next();
            String value = elm.getValue();

            if ("sqlCode".equals(elm.getAttributeValue("attribute"))) {
            	sqlCode.add(value);
            } else if ("mode".equals(elm.getAttributeValue("attribute"))) {
            	mode = value;
            } else if ("returnValue".equals(elm.getAttributeValue("attribute"))) {
                returnValue = value;
            }
        }
        Generar();
        
        if ("EDIT".equals(mode)) {
            enablebutton = false;
            namebutton = "SAVEAS";
        }
        else if ("DELETE".equals(mode)) {
        	enablebutton = false;
            namebutton = "DELETE";
            setDisabled();
        }
    }

    private void Generar() {

        JPanel JPgeneral = new JPanel(new BorderLayout());
        JPanel JPdatos = new JPanel(new BorderLayout());

        XMLCBretiene = new XMLCheckBox("RETIENE");
        XMLCBestado = new XMLCheckBox("ESTADO");

        JPanel JPchecks = new JPanel(new FlowLayout(FlowLayout.LEFT));// new
                                                                        // GridLayout(1,
                                                                        // 2));
        JPchecks.add(XMLCBretiene.getJPcheck());
        JPchecks.add(XMLCBestado.getJPcheck());

        JPanel JPetiquetas = new JPanel();
        JPetiquetas.setLayout(new GridLayout(6, 1));


        XMLTFnitcc = new XMLTextField("NITCC", 26, 50);
        XMLTFnombre = new XMLTextField("NOMBRE", 26, 50);
        XMLTFestablecimiento = new XMLTextField("ESTABLECIMIENTO", 26, 50);
        XMLCBregimen = new XMLComboBox(GFforma,"SEL0018", "REGIMEN");
        XMLCBactividad = new XMLComboBox(GFforma,"SEL0202", "ACTIVIDAD");
        XMLCBactividad.setPreferredSize(new Dimension(250,20));
        XMLCBcatalogo = new XMLComboBox(GFforma,"SEL0019", "CATALOGO");
        JPetiquetas.add(XMLTFnitcc.getLabel());
        JPetiquetas.add(XMLTFnombre.getLabel());
        JPetiquetas.add(XMLTFestablecimiento.getLabel());
        JPetiquetas.add(XMLCBregimen.getLabel());
        JPetiquetas.add(XMLCBactividad.getLabel());
        JPetiquetas.add(XMLCBcatalogo.getLabel());

        JPanel JPfields = new JPanel();
        JPfields.setLayout(new GridLayout(6, 1));

        JPfields.add(XMLTFnitcc.getJPtext());
        JPfields.add(XMLTFnombre.getJPtext());
        JPfields.add(XMLTFestablecimiento.getJPtext());
        JPfields.add(XMLCBregimen.getJPcombo());
        JPfields.add(XMLCBactividad.getJPcombo());
        JPfields.add(XMLCBcatalogo.getJPcombo());

        JPdatos.add(new JPanel(), BorderLayout.NORTH);
        JPdatos.add(JPetiquetas, BorderLayout.WEST);
        JPdatos.add(JPfields, BorderLayout.CENTER);
        JPdatos.add(JPchecks, BorderLayout.SOUTH);

        JPgeneral.add(new JPanel(), BorderLayout.WEST);
        JPgeneral.add(JPdatos, BorderLayout.CENTER);


        XMLTFnitcc.addKeyListener(new KeyAdapter() {
		
			public void keyReleased(KeyEvent e) {
				search = true;
			}
		});
        XMLTFnitcc.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                class SearchQuery extends Thread {
                    public void run() {
                        try {
                            GenericDataFiller QDsearch = new GenericDataFiller(
                                    GFforma,
                                    namebutton, enablebutton, "SEL0037",
                                    new String[] { returnValue }, XMLTFnitcc
                                            .getText(),
                                    new XMLTextField[] { XMLTFnombre });

                            if (QDsearch.searchQuery()) {

                            	 // Consultado Estados

                                Document Destados = STResultSet.getResultSetST(
                                        "SEL0046",
                                        new String[] { XMLTFnitcc.getText() });

                                updateCheckBox(Destados);

                                // Consultado Establecimiento
                                new GenericDataFiller(
                                        GFforma,
                                        "SEL0043",
                                        XMLTFnitcc.getText(),
                                        new XMLTextField[] { XMLTFestablecimiento })
                                        .start();

                                // Consultando Regimen
                                new QueryComboBox(GFforma, "SEL0044",
                                        XMLTFnitcc.getText(), XMLCBregimen)
                                        .start();

                                // Consultando Activiadad
                                new QueryComboBox(GFforma, "SEL0203",
                                        XMLTFnitcc.getText(), XMLCBactividad)
                                        .start();
                                
//                              Consultando Catalogo Precios de Venta
                                new QueryComboBox(GFforma, "SEL0045",
                                        XMLTFnitcc.getText(), XMLCBcatalogo)
                                        .start();
                                searchOthersSqls();
                                search = false;
                            }

                        }
                        catch (STException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                if (search) {
                	new SearchQuery().start();
                }
            }
        });

        this.setLayout(new BorderLayout());

        this.add(JPgeneral, BorderLayout.NORTH);
    }

    private void updateCheckBox(Document doc) {
        Iterator i = doc.getRootElement().getChildren("row").iterator();
        XMLCBestado.setSelected(false);
        XMLCBretiene.setSelected(false);

        while (i.hasNext()) {
            Element e = (Element) i.next();
            Iterator j = e.getChildren().iterator();

            String value = ((Element) j.next()).getValue();

            if ("f".equals(value) || "F".equals(value) || "false".equals(value)
                    || "FALSE".equals(value) || "False".equals(value)
                    || "0".equals(value))
                XMLCBretiene.setSelected(false);
            else
                XMLCBretiene.setSelected(true);

            value = ((Element) j.next()).getValue();
            if ("f".equals(value) || "F".equals(value) || "false".equals(value)
                    || "FALSE".equals(value) || "False".equals(value)
                    || "0".equals(value))
                XMLCBestado.setSelected(false);
            else
                XMLCBestado.setSelected(true);

        }
    }

    /**
     * Metodo encargado de limpiar el panel
     */
    public void clean() {
        XMLTFnitcc.setText("");
        XMLTFnombre.setText("");
        XMLTFestablecimiento.setText("");
        XMLCBregimen.setSelectedIndex(0);
        XMLCBactividad.setSelectedIndex(0);
        XMLCBcatalogo.setSelectedIndex(0);
        XMLCBretiene.getJCBcheck().setSelected(false);
        XMLCBestado.getJCBcheck().setSelected(false);
    }

    public Element getPackage() throws VoidPackageException {
        Element pack = new Element("package");
        if (!XMLTFnitcc.getText().equals(""))
            pack.addContent(XMLTFnitcc.getElementText());
        else
            throw new VoidPackageException(Language.getWord("NITCC"));

        return pack;
    }

    /**
     * Metodo encargado de retornar el elemento package correspondiente a este
     * panel
     */

    public Element[] getMultiPackage() throws VoidPackageException {
        Element[] pack;
        if ("EDIT".equals(mode))
            pack = new Element[6];
        else
            pack = new Element[3];

        Element sbpack1 = new Element("package");
        Element sbpack2 = new Element("package");
        Element sbpack3 = new Element("package");

        // Estos 2 if's son para alterar el orden del paquete enviando primera
        // o ultima la llave primaria dependiendo si es una insercion o una
        // actualizacion

        if (!"EDIT".equals(mode))
            if (!XMLTFnitcc.getText().equals(""))
                sbpack1.addContent(XMLTFnitcc.getElementText("key"));
            else
                throw new VoidPackageException(Language.getWord("NITCC"));

        if (!XMLTFnombre.getText().equals(""))
            sbpack1.addContent(XMLTFnombre.getElementText());
        else
            throw new VoidPackageException(Language.getWord("NOMBRE"));

        if ("EDIT".equals(mode))
            if (!XMLTFnitcc.getText().equals(""))
                sbpack1.addContent(XMLTFnitcc.getElementText("key"));
            else
                throw new VoidPackageException(Language.getWord("NITCC"));

        if (!XMLTFestablecimiento.getText().equals(""))
            sbpack2.addContent(XMLTFestablecimiento.getElementText());

        if (XMLCBregimen.getStringCombo() != null)
            sbpack3.addContent(XMLCBregimen.getElementCombo());
        else
            throw new VoidPackageException(XMLCBregimen.getLabel().getName());
        
        if (XMLCBactividad.getStringCombo() != null)
            sbpack3.addContent(XMLCBactividad.getElementCombo());
        else
            throw new VoidPackageException(XMLCBactividad.getLabel().getName());

        if (XMLCBcatalogo.getStringCombo() != null)
            sbpack3.addContent(XMLCBcatalogo.getElementCombo());
        else
            throw new VoidPackageException(XMLCBcatalogo.getLabel().getName());
        
        sbpack3.addContent(XMLCBretiene.getElementCheck());
        sbpack3.addContent(XMLCBestado.getElementCheck());

        Element packtmp = new Element("package");
        Element field = new Element("field");
        packtmp.addContent(field);

        if ("EDIT".equals(mode)) {
            pack[0] = sbpack1;
            pack[1] = (Element) packtmp.clone();
            pack[2] = sbpack2;
            pack[3] = (Element) packtmp.clone();
            pack[4] = sbpack3;
            pack[5] = (Element) packtmp.clone();
        } else {
            pack[0] = sbpack1;
            pack[1] = sbpack2;
            pack[2] = sbpack3;
        }

        return pack;
    }

    public Element getReturnValue() {
        Element pack = new Element("package");
        pack.addContent(new Element("field").setText(returnValue));
        return pack;
    }
    
    public JPanel getPanel() {
        return this;
    }

    private void setDisabled() {
        XMLTFnombre.setEnabled(false);
        XMLTFestablecimiento.setEnabled(false);
        XMLCBcatalogo.setEnabled(false);
        XMLCBregimen.setEnabled(false);
        XMLCBretiene.setEnabled(false);
        XMLCBestado.setEnabled(false);
    }
    /*--------------------------------------------------------------------------------------------*/
    
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
    
    public void searchOthersSqls() {
        class SearchingSQL extends Thread {
            
	        private String[] args;
	        
            public SearchingSQL(String[] args) {
                 this.args=args;
            }
            
            public void run() {

                String sql;
		        for (int i=0;i<sqlCode.size();i++) {
		            Document doc = null;
                    sql = sqlCode.get(i);
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
        new SearchingSQL(new String[] {XMLTFnitcc.getText()}).start();
    }
    
    public synchronized void addAnswerListener(AnswerListener listener ) {
        AnswerListener.addElement(listener);
    }

    public synchronized void removeAnswerListener(AnswerListener listener ) {
        AnswerListener.removeElement(listener);
    }
}
