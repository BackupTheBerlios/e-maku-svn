package client.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import common.gui.components.AnswerEvent;
import common.gui.components.AnswerListener;
import common.gui.components.EmakuUIFieldFiller;
import common.gui.components.VoidPackageException;
import common.gui.components.XMLTextField;
import common.gui.forms.GenericForm;
import common.misc.language.Language;
import common.transactions.TransactionServerException;
import common.transactions.TransactionServerResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * Employees.java Creado el 07-jul-2005
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
 * Esta Componente es el encargado de generar un <package/> para la interpreta
 * cion de la informacion de la tabla info_empleados
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class EmployeesOld extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5796290310127496268L;

	private GenericForm GFforma;
    
    private XMLTextField XMLTFnitcc;
    private XMLTextField XMLTFnombre;
    private XMLTextField XMLTFsigla;
    private XMLTextField XMLTFfechaNac;
    private XMLComboBox XMLCBnacionalidad;
    private XMLTextField XMLTFprocedencia;
    private XMLComboBox XMLCBestadoCivil;
    private XMLComboBox XMLCBcargo;
    private XMLComboBox XMLCBtipoContrato;
    private XMLTextField XMLTFfechaIniciacion;
    private XMLTextField XMLTFfechaTerminacion;
    private XMLComboBox XMLCBestado;
    
    private JComboBox JCBsexo;

    private String namebutton;
    private String valueArgs="NEW";
    private boolean enablebutton = true;

	private Vector<String> sqlCode;
	private String returnValue = "";
    private Vector<AnswerListener> AnswerListener = new Vector<AnswerListener>();
    private boolean search;
    
    /**
     * Constructor del componente
     * @param GFforma Forma Generica
     * @param doc Parametros del componente
     */
    
    public EmployeesOld(GenericForm GFforma, Document doc) {
        this.GFforma=GFforma;
        
        Iterator i = doc.getRootElement().getChildren().iterator();
        namebutton = "SAVE";
        sqlCode = new Vector<String>();
        while (i.hasNext()) {
            Element elm = (Element) i.next();
            String value = elm.getValue();

	        if ("sqlCode".equals(elm.getAttributeValue("attribute"))) {
	        		sqlCode.add(value);
	        } else if ("mode".equals(elm.getAttributeValue("attribute"))) {
	        		valueArgs = value;
	        } else if ("returnValue".equals(elm.getAttributeValue("attribute"))) {
	            returnValue = value;
	        }
        }
        generar();
        if ("EDIT".equals(valueArgs)) {
            enablebutton = false;
            namebutton = "SAVEAS";
        }
        else if ("DELETE".equals(valueArgs)) {
        	enablebutton = false;
            namebutton = "DELETE";
            setDisabled();
        }
    }
    
    /**
     * Este metodo genera el componente y lo despliega, deacuerdo a
     * su parametrizacion
     */

    private void generar() {
     
        JPanel JPdatos = new JPanel(new BorderLayout());
        JPanel JPetiquetas = new JPanel();
        JPetiquetas.setLayout(new GridLayout(13, 1));

        XMLTFnitcc = new XMLTextField("IDENTIFICACION", 26, 50);
        XMLTFnombre = new XMLTextField("NOMBRE", 26, 50);
        XMLTFsigla = new XMLTextField("SIGLA", 26, 50);
        XMLTFfechaNac = new XMLTextField("FECHA_NAC", 26, 50);
        XMLCBnacionalidad = new XMLComboBox(GFforma,"SCS0009", "NACIONALIDAD");
        XMLTFprocedencia = new XMLTextField("PROCEDENCIA",26,50);
        JCBsexo = new JComboBox();
        JCBsexo.addItem("");
        JCBsexo.addItem(Language.getWord("MASCULINO"));
        JCBsexo.addItem(Language.getWord("FEMENINO"));
        
        XMLCBestadoCivil = new XMLComboBox(GFforma,"SCS0026", "ESTADO_CIVIL");
        XMLCBcargo = new XMLComboBox(GFforma,"SCS0028", "CARGO-EMPLEADO");
        XMLCBtipoContrato = new XMLComboBox(GFforma,"SCS0027", "TIPO_CONTRATO");
        XMLTFfechaIniciacion = new XMLTextField("FECHA_INICIACION",26,50);
        XMLTFfechaTerminacion = new XMLTextField("FECHA_TERMINACION",26,50);
        XMLCBestado = new XMLComboBox(GFforma,"SCS0029", "ESTADO_EMPLEADO");

        JPetiquetas.add(XMLTFnitcc.getLabel());
        JPetiquetas.add(XMLTFnombre.getLabel());
        JPetiquetas.add(XMLTFsigla.getLabel());
        JPetiquetas.add(XMLTFfechaNac.getLabel());
        JPetiquetas.add(XMLCBnacionalidad.getLabel());
        JPetiquetas.add(XMLTFprocedencia.getLabel());
        JPetiquetas.add(new JLabel(Language.getWord("SEXO")));
        JPetiquetas.add(XMLCBestadoCivil.getLabel());
        JPetiquetas.add(XMLCBcargo.getLabel());
        JPetiquetas.add(XMLCBtipoContrato.getLabel());
        JPetiquetas.add(XMLTFfechaIniciacion.getLabel());
        JPetiquetas.add(XMLTFfechaTerminacion.getLabel());
        JPetiquetas.add(XMLCBestado.getLabel());

        JPanel JPfields = new JPanel();
        JPfields.setLayout(new GridLayout(13, 1));

        JPanel JPsexo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPsexo.add(JCBsexo);
        
        JPfields.add(XMLTFnitcc.getJPtext());
        JPfields.add(XMLTFnombre.getJPtext());
        JPfields.add(XMLTFsigla.getJPtext());
        JPfields.add(XMLTFfechaNac.getJPtext());
        JPfields.add(XMLCBnacionalidad.getJPcombo());
        JPfields.add(XMLTFprocedencia.getJPtext());
        JPfields.add(JPsexo);
        JPfields.add(XMLCBestadoCivil.getJPcombo());
        JPfields.add(XMLCBcargo.getJPcombo());
        JPfields.add(XMLCBtipoContrato.getJPcombo());
        JPfields.add(XMLTFfechaIniciacion.getJPtext());
        JPfields.add(XMLTFfechaTerminacion.getJPtext());
        JPfields.add(XMLCBestado.getJPcombo());

        JPdatos.add(new JPanel(), BorderLayout.NORTH);
        JPdatos.add(JPetiquetas, BorderLayout.WEST);
        JPdatos.add(JPfields, BorderLayout.CENTER);
        
        XMLTFnitcc.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				search = true;
			}
		});

        XMLTFnitcc.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                class SearchQuery extends Thread {
                    public void run() {
                        //try {
                            EmakuUIFieldFiller QDsearch = new EmakuUIFieldFiller(
                                    GFforma,
                                    namebutton, enablebutton, "SCS0016",
                                    new String[] { returnValue }, XMLTFnitcc
                                            .getText(),
                                    new XMLTextField[] { XMLTFnombre });

                            if (QDsearch.searchQuery()) {


                                // Consultado info_empleado
                                
                                new EmakuUIFieldFiller(
                                        GFforma,
                                        "SCS0035",
                                        XMLTFnitcc.getText(),
                                        new XMLTextField[] { XMLTFsigla,
                                                             XMLTFfechaNac,
                                                             XMLTFprocedencia,
                                                             XMLTFfechaIniciacion,
                                                             XMLTFfechaTerminacion }).start();

                                // Consultar Sexo
                                searchSex();
                                
                                // Consultando Nacionalidad
                                new QueryComboBox(GFforma, "SCS0030",
                                        XMLTFnitcc.getText(), XMLCBnacionalidad)
                                        .start();

                                // Consultando Estado Civil
                                new QueryComboBox(GFforma, "SCS0031",
                                        XMLTFnitcc.getText(), XMLCBestadoCivil)
                                        .start();

                                // Consultando Cargo Empleado
                                new QueryComboBox(GFforma, "SCS0032",
                                        XMLTFnitcc.getText(), XMLCBcargo)
                                        .start();

                                // Consultando Tipo Contrato
                                new QueryComboBox(GFforma, "SCS0033",
                                        XMLTFnitcc.getText(), XMLCBtipoContrato)
                                        .start();
                                
                                // Consultando Estado Empleado
                                new QueryComboBox(GFforma, "SCS0034",
                                        XMLTFnitcc.getText(), XMLCBestado)
                                        .start();

                                searchOthersSqls();
                                search = false;
                            }
                        //}
/*                        catch (STException e1) {
                            e1.printStackTrace();
                        }*/
                    }
                }
                if (search) {
                	new SearchQuery().start();
                }
            }
        });

        this.add(JPdatos, BorderLayout.CENTER);

    }
    
    /**
     * El sexo es un problema :S ya que este componente no se deriba de XMLComboBox si no de
     * un JComboBox, se necesita una consulta para el despliegue de la informacion y un hilo
     * para que ejecute el procedimiento de carga del parametro
     */

    private void searchSex() {
        class Sexo extends Thread {
            public void run(){
                try {
                    Document doc = TransactionServerResultSet.getResultSetST("SCS0036",new String[]{XMLTFnitcc.getText()});
                    Iterator i = doc.getRootElement().getChildren("row").iterator();
                    int row = doc.getRootElement().getChildren("row").size();
                        
                    String data="";
                    
                    
                    if (row>0) {
        	            while (i.hasNext()) {
        	                Element e = (Element) i.next();
                            Iterator j = e.getChildren().iterator();
                            while (j.hasNext()) 
                                data= ((Element)j.next()).getValue();
        	            }
        	            data=data.trim();
        	            if (data.equals("t") ||
        	                data.equals("0") ||
        	                data.equals("True") ||
        	                data.equals("TRUE")) {
        	                JCBsexo.setSelectedIndex(1);
        	            }
        	            else {
        	                JCBsexo.setSelectedIndex(2);
        	            }
                    }
                    else {
                        JCBsexo.setSelectedIndex(0);
                    }
                }
                catch (TransactionServerException e) {
                    e.printStackTrace();
                }
            }
        }
        new Sexo().start();
    }
    
    /**
     * Este metodo se encarga de limpiar el componente
     */

    public void clean() {
        XMLTFnitcc.setText("");
        XMLTFnombre.setText("");
        XMLTFsigla.setText("");
        XMLTFfechaNac.setText("");
        XMLCBnacionalidad.setSelectedIndex(0);
        XMLTFprocedencia.setText("");
        JCBsexo.setSelectedIndex(0);
        XMLCBestadoCivil.setSelectedIndex(0);
        XMLCBcargo.setSelectedIndex(0);
        XMLCBtipoContrato.setSelectedIndex(0);
        XMLTFfechaIniciacion.setText("");
        XMLTFfechaTerminacion.setText("");
        XMLCBestado.setSelectedIndex(0);
    }

    /**
     * Este metodo retorna un <package/> el cual solo sera invocado
     * en el momento de eliminar un registro
     * @return retorna un objeto element que contiene el <package/>
     * @throws VoidPackageException se lanza esta excepcion en caso
     * de que exista inconcistencia en la informacion para la generacion
     * del <package/>
     */
    public Element getPackage() throws VoidPackageException {
        Element pack = new Element("package");
        if (!XMLTFnitcc.getText().equals(""))
            pack.addContent(XMLTFnitcc.getElementText());
        else
            throw new VoidPackageException(Language.getWord("IDENTIFICACION"));

        return pack;
    }

    /**
     * Metodo encargado de retornar el elemento package correspondiente a este
     * panel
     */

    public Element[] getMultiPackage() throws VoidPackageException {
        Element[] pack;
        pack = new Element[3];
        
        Element sbpack1 = new Element("package");
        Element sbpack2 = new Element("package");
        Element sbpack3 = new Element("package");
        
        // Estos 2 if's son para alterar el orden del paquete enviando primera
        // o ultima la llave primaria dependiendo si es una insercion o una
        // actualizacion

        if (!"EDIT".equals(valueArgs))
            if (!XMLTFnitcc.getText().equals(""))
                sbpack1.addContent(XMLTFnitcc.getElementText("key"));
            else
                throw new VoidPackageException(Language.getWord("NITCC"));

        if (!XMLTFnombre.getText().equals(""))
            sbpack1.addContent(XMLTFnombre.getElementText());
        else
            throw new VoidPackageException(Language.getWord("NOMBRE"));

        if ("EDIT".equals(valueArgs))
            if (!XMLTFnitcc.getText().equals(""))
                sbpack1.addContent(XMLTFnitcc.getElementText("key"));
            else
                throw new VoidPackageException(Language.getWord("NITCC"));

        if (!XMLTFsigla.getText().equals(""))
            sbpack2.addContent(XMLTFsigla.getElementText());
        else
            throw new VoidPackageException(Language.getWord("SIGLA"));

        if (!XMLTFfechaNac.getText().equals(""))
            sbpack2.addContent(XMLTFfechaNac.getElementText());
        else
            throw new VoidPackageException(Language.getWord("FECHA_NAC"));

        if (XMLCBnacionalidad.getStringCombo() != null)
            sbpack2.addContent(XMLCBnacionalidad.getElementCombo());
        else
            throw new VoidPackageException(XMLCBnacionalidad.getLabel().getName());
        
        if (!XMLTFprocedencia.getText().equals(""))
            sbpack2.addContent(XMLTFprocedencia.getElementText());
        else
            throw new VoidPackageException(Language.getWord("PROCEDENCIA"));

        /*
         * Armando el paquete del sexo XD
         */
        
        Element field = new Element("field");
        if (((String)JCBsexo.getSelectedItem()).equals(Language.getWord("MASCULINO"))) {
            field.setText("1");
        }
        else if (((String)JCBsexo.getSelectedItem()).equals(Language.getWord("FEMENINO"))) {
            field.setText("0");
        }
        else {
            throw new VoidPackageException(Language.getWord("SEXO"));
        }
        
        sbpack2.addContent(field);
        
        if (XMLCBestadoCivil.getStringCombo() != null)
            sbpack2.addContent(XMLCBestadoCivil.getElementCombo());
        else
            throw new VoidPackageException(XMLCBestadoCivil.getLabel().getName());

        if (XMLCBcargo.getStringCombo() != null)
            sbpack2.addContent(XMLCBcargo.getElementCombo());
        else
            throw new VoidPackageException(XMLCBcargo.getLabel().getName());

        if (XMLCBtipoContrato.getStringCombo() != null)
            sbpack2.addContent(XMLCBtipoContrato.getElementCombo());
        else
            throw new VoidPackageException(XMLCBtipoContrato.getLabel().getName());
        
        if (!XMLTFfechaIniciacion.getText().equals(""))
            sbpack2.addContent(XMLTFfechaIniciacion.getElementText());
        else
            throw new VoidPackageException(Language.getWord("FECHA_INICIACION"));

        if (!XMLTFfechaTerminacion.getText().equals(""))
            sbpack2.addContent(XMLTFfechaTerminacion.getElementText());
        else
            sbpack2.addContent(XMLTFfechaTerminacion.getElementText("NULL"));

        if (XMLCBestado.getStringCombo() != null)
            sbpack2.addContent(XMLCBestado.getElementCombo());
        else
            throw new VoidPackageException(XMLCBtipoContrato.getLabel().getName());

        sbpack3.addContent(new Element("field").setText(returnValue));
        
        Element packtmp = new Element("package");
        Element field1 = new Element("field");
        packtmp.addContent(field1);

        if ("NEW".equals(valueArgs)) {
            pack[0] = sbpack1;
            pack[1] = sbpack2;
            pack[2] = sbpack3;
        } else {
            pack[0] = sbpack1;
            pack[1] = (Element) packtmp.clone();
            pack[2] = sbpack2;
        }
        
        return pack;
    }

    public JPanel getPanel() {
        return this;
    }
    
    private void setDisabled() {
        XMLTFnombre.setEnabled(false);
        XMLTFsigla.setEnabled(false);
        XMLTFfechaNac.setEnabled(false);
        XMLCBnacionalidad.setEnabled(false);
        XMLTFprocedencia.setEnabled(false);
        JCBsexo.setEnabled(false);
        XMLCBestadoCivil.setEnabled(false);
        XMLCBcargo.setEnabled(false);
        XMLCBtipoContrato.setEnabled(false);
        XMLTFfechaIniciacion.setEnabled(false);
        XMLTFfechaTerminacion.setEnabled(false);
        XMLCBestado.setEnabled(false);
    }
/*--------------------------------------------------------------------------------------------*/
    
    /**
     * Metodo encargado de notificar la llegada de un paquete <answer/>
     * @param event 
     */
    private synchronized void notificando(AnswerEvent event) {
        for(AnswerListener l:AnswerListener) {
			if (l.containSqlCode(event.getSqlCode())) {
				l.arriveAnswerEvent(event);
			}
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
                        doc = TransactionServerResultSet.getResultSetST(sql,args);
                    }
                    catch (TransactionServerException e) {
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
