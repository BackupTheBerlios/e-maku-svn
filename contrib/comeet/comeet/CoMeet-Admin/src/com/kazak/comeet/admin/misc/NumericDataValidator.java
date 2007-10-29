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
 * NumericDataValidator.java
 *
 * Created on 29 de mayo de 2002, 10:02
 */

package com.kazak.comeet.admin.misc;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumericDataValidator extends PlainDocument {

	private static final long serialVersionUID = 4060866073050927871L;
	private int limit;
    public NumericDataValidator(int limit) {
        super();
        setLimit(limit);
    }
    
    public final int getLimit() {
        return limit;
    }
    
    public void insertString(int offset, String string, AttributeSet attributeSet) throws BadLocationException {
    	int size = string.length();
        if (this.getLength() < limit) {
            if (size <= limit - offset) {
                if (offset < limit) {
                    String finalString = new String();
                    try {
                        for (int i = 0; i < string.length(); i++) {
                            finalString = finalString + string.substring(i, i + 1);
                        }
                        Integer.parseInt(finalString);
                        super.insertString(offset, string, attributeSet);
                    }
                    catch (NumberFormatException NFEe) {
                  		Toolkit.getDefaultToolkit().beep();
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }


    public final void setLimit(int newValue) {
        this.limit = newValue;
    }
}
