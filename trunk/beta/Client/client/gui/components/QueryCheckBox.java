package client.gui.components;

import java.util.Iterator;

import common.gui.formas.GenericForm;
import common.transactions.STException;
import common.transactions.STResultSet;

import org.jdom.Document;
import org.jdom.Element;

/**
 * QueryCheckBox.java Creado el 21-abr-2005
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
 */

public class QueryCheckBox extends Thread {

    private String sql;
    private String arg;
    private XMLCheckBox XMLCBout=null;
    
    /**
     * 
     */
    public QueryCheckBox(GenericForm GFforma,String sql,String arg,XMLCheckBox XMLCBout) { 
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
            XMLCBout.setSelected(false);
            while (i.hasNext()) {
                Element e = (Element) i.next();
                Iterator j = e.getChildren().iterator();
                while (j.hasNext()) {
                    String value= ((Element)j.next()).getValue();
                    if ("t".equals(value) || "T".equals(value) || 
                        "true".equals(value) || "TRUE".equals(value) || 
                        "True".equals(value) || "1".equals(value)) {
                        XMLCBout.setSelected(true);
                    }

                }
            }
        }
        catch (STException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
