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

public class LimitDocument extends PlainDocument {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4060866073050927871L;
	private int limit;
    public LimitDocument(int limit) {
        super();
        setLimit(limit); // store the limit����
    }
    
    public final int getLimit() {
        return limit;
    }
    
    public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
        
    	int longitud = s.length();

        if (this.getLength() < limit) {
            if (longitud <= limit - offset) {
                if (offset < limit) {
                    String Snum = new String();
                    try {
                        for (int i = 0; i < s.length(); i++) {
                            Snum = Snum + s.substring(i, i + 1);
                        }
                        Integer.parseInt(Snum);
                        super.insertString(offset, s, attributeSet);
                    }
                    catch (NumberFormatException NFEe) {
                    	if (Snum.equals(".")) {
                    		if (super.getText(0,super.getLength()).indexOf(".")==-1)
                    			super.insertString(offset, s, attributeSet);
                    	}else {
                    		Toolkit.getDefaultToolkit().beep();
                    	}
                    }
                } else {
                    System.out.println("Aqui se putio");
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
