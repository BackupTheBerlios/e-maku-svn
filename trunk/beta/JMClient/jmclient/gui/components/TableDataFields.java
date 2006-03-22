package jmclient.gui.components;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;

import jmlib.gui.components.GenericData;
import jmlib.gui.components.XMLTextField;
import jmlib.gui.formas.FinishEvent;
import jmlib.gui.formas.GenericForm;
import jmlib.gui.formas.InitiateFinishListener;
import jmlib.gui.formas.NotFoundComponentException;
import jmlib.miscelanea.formulas.CalculateFormula;

import org.jdom.Document;

/**
 * TableDataFields.java Creado el 17-jun-2005
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
 * @author <A href='mailto:cristian_david@universia.net.co'>Cristian David Cepeda</A>
 */

public class TableDataFields extends GenericData implements InitiateFinishListener,TableTotalListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5109883440180571978L;
	private TMFindData TMFDtabla;
    private GenericForm GFforma;


    
    /**
     * 
     */
    public TableDataFields(GenericForm GFforma, Document doc) 
    throws InvocationTargetException,NotFoundComponentException {
        super(GFforma,doc);
	    this.GFforma=GFforma;
        
    }
    
    public void totalColEvent(TableTotalEvent e) {
        Vector fields = this.getVFields();
        for (int i=0;i<fields.size();i++) {
            String totalValue = ((XMLTextField)fields.get(i)).getTotalCol();
            if (totalValue!=null) {
            	double total = calcule(totalValue);
                NumberFormat nf = NumberFormat.getNumberInstance(); 
                DecimalFormat form = (DecimalFormat)nf;
                form.applyPattern("###,###,##0.00");
                ((XMLTextField)fields.get(i)).setText(form.format(total));
                ((XMLTextField)fields.get(i)).setNumberValue(total);
                if (((XMLTextField)fields.get(i)).isExportvalue()) {
                	GFforma.setExternalValues(((XMLTextField)fields.get(i)).getExportvalue(),((XMLTextField)fields.get(i)).getNumberValue());
                }
               
            }
        }
    }
    
    private double calcule(String totalValue) {
        /*
         * Se reemplaza las variables por valores 
         */
        
        String newVar="";
        for (int j=0;j<totalValue.length();j++) {
            /*
             * se numerara las columnas en letras con un rango maximo de 10 columnas
             */
            
            if ((totalValue.charAt(j)>=65 && totalValue.charAt(j)<=90) || (totalValue.charAt(j)>=97 && totalValue.charAt(j)<=122)) {
                newVar+=TMFDtabla.getTotalCol(totalValue.substring(j,j+1));
            }
            else {
                newVar+=totalValue.substring(j,j+1);
            }
        }

        /*
         * Se calcula la formula 
         */
        
        NumberFormat nf = NumberFormat.getNumberInstance(); 
        DecimalFormat form = (DecimalFormat)nf;
        form.applyPattern("###,###,##0.00");
        
        //return form.format(((Double)CalculateFormula.operar(newVar)).doubleValue());
        return ((Double)(CalculateFormula.operar(newVar))).doubleValue();
    }


    /**
     * Este metodo se lanza cuando GenericForm se terminado de instanciar, por tanto
     * todos sus componentes ya son diferentes de null
     */
    
    public void initiateFinishEvent(FinishEvent e) {
        try {
          	TMFDtabla = (TMFindData) GFforma.invokeMethod("jmclient.gui.components.TableFindData"+getEnlaceTabla(),
	        "getTMFDtabla");
	        TMFDtabla.addTotalEventListener(this);
        	callAddAnswerListener();
        	GFforma.addChangeExternalValueListener(this);
            /*
		     * Se recorre todos los XMLTextField para ver cual interactua con TableFindData
		     * para asignar la generacion de eventos por cambio de valor; se le asigna el
		     * evento correspondiente 
		     */
		    Vector fields = this.getVFields();
		    
		    for (int i=0;i<fields.size();i++) {
		    	
		    	final XMLTextField xmltf = ((XMLTextField)fields.get(i));
    			
				if (xmltf.isImportvalue() && !xmltf.getType().equals(XMLTextField.TEXT)) {
				 	 if (xmltf.isExportvalue()) {
				 		 GFforma.setExternalValues(xmltf.getExportvalue(),xmltf.getNumberValue());
				 	 }
				 	 xmltf.setText(formatear(GFforma.getExteralValues(xmltf.getImportValue()[0])));
				 	 xmltf.setNumberValue(GFforma.getExteralValues(xmltf.getImportValue()));
				}
		    }
        }
        catch(InvocationTargetException ITEe) {
            ITEe.printStackTrace();
        }
        catch (NotFoundComponentException NFCEe) {
            NFCEe.printStackTrace();
        }

    }
    
    public Double getDoubleValue(String key) {
        Vector fields = this.getVFields();
        for (int i=0;i<fields.size();i++) {
            String keyValue = ((XMLTextField)fields.get(i)).getKeyExternalValue();
            if (key.equals(keyValue)) {
                Double value = new Double(((XMLTextField)fields.get(i)).getNumberValue());
                return value;
            }
        }
        return new Double(0);
    }
}
