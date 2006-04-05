/**
 * Este archivo es parte de E-Maku (http://comunidad.qhatu.net)
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
 *
 *
 * @author  Luis Felipe Hernandez Z.
 * @see e-mail felipe@qhatu.net
 *
 * LimitDocument.java
 *
 * Created on 29 de mayo de 2002, 10:02
 */

package common.miscelanea.texto;


import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LimitDocumentText extends PlainDocument {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5229828757349802786L;
	private int limit;
    
    public LimitDocumentText(int limit) {
        super();
        setLimit(limit); // store the limit����
    }
    
    public final int getLimit() {
        return limit;
    }
    
    public void insertString(int posicion, String s, AttributeSet attributeSet) throws BadLocationException {
        
        int longitud = s.length();
        
        if (this.getLength() < limit) {
            if (longitud <= limit - posicion) {
                if(posicion < limit) { 
                    super.insertString(posicion,s,attributeSet);
                }
                else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
            else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
        else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    public final void setLimit(int newValue) {
        this.limit = newValue;
    }
}