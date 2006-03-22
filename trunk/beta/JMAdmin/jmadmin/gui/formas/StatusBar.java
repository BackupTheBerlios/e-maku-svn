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
 * StatusBar.java
 *
 * Created on 29 de Mayo de 2003, 8:32
 */

package jmadmin.gui.formas;

import javax.swing.*;
import javax.swing.border.*;

public class StatusBar extends JToolBar {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5602861123808677465L;
	public static JLabel JLetiqueta1;
    public static JLabel JLetiqueta2;
    public static JLabel JLetiqueta3;
    public static JProgressBar JPBbarra;
    
    /** Creates new StatusBar */
    public StatusBar() {
        
        this.setFloatable(false);

        JLetiqueta1=new JLabel("         ");
        JLetiqueta1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        
        JLetiqueta2=new JLabel("         ");
        JLetiqueta2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));

        JLetiqueta3=new JLabel("         ");
        JLetiqueta3.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        
        JPBbarra=new JProgressBar();
        JPBbarra.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        
        this.add(JLetiqueta1);
        this.add(JLetiqueta2);
        this.add(JLetiqueta3);
        this.add(JPBbarra);
    }
    
    public static void setLabel1(String label) {
        JLetiqueta1.setText(label);
    }

    public static void setLabel2(String label) {
        JLetiqueta2.setText(label);
    }

    public static void setLabel3(String label) {
        JLetiqueta3.setText(label);
    }
}