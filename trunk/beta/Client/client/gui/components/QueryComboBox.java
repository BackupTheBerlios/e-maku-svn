package client.gui.components;

import java.util.Iterator;

import common.gui.forms.GenericForm;
import common.transactions.STException;
import common.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * QueryComboBox.java Creado el 25-feb-2005
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
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class QueryComboBox extends Thread {

    private String sql;
    private String arg;
    private XMLComboBox XMLCBout=null;
    /**
     * 
     */

    public QueryComboBox(GenericForm GFforma,String sql,String arg,XMLComboBox XMLCBout) { 
        this.sql=sql;
        this.arg=arg;
        this.XMLCBout=XMLCBout;
    }

    public void run() {
        searchQuery();
    }
    
    public boolean searchQuery() {
        try {
            Document doc = STResultSet.getResultSetST(sql,new String[]{arg});
            Iterator i = doc.getRootElement().getChildren("row").iterator();
            int row = doc.getRootElement().getChildren("row").size();
                
            String data="";
            
            
            if (row>0) {
	            while (i.hasNext()) {
	                Element e = (Element) i.next();
                    Iterator j = e.getChildren().iterator();
                    while (j.hasNext()) 
                        data+= ((Element)j.next()).getValue()+" ";
	            }
	            XMLCBout.setSelectedItem(data.trim());
            }
            else {
	            XMLCBout.setSelectedItem("");
            }
        }
        catch (STException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
