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

package client.gui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import common.control.ClientHeaderValidator;
import common.control.SuccessEvent;
import common.control.SuccessListener;
import common.misc.language.Language;

public class StatusBar extends JToolBar implements SuccessListener {

    
	private static final long serialVersionUID = 5107890638919072678L;
	public static JProgressBar JPBbarra = new JProgressBar(); 
	
	public static JLabel JLetiqueta1 = new JLabel("         ") {
		private static final long serialVersionUID = 3440825260214310311L;
		public void paintComponent(Graphics g) {
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);
	        super.paintComponent(g);
	    }
	};
    
	public static JLabel JLetiqueta2 = new JLabel("         "){
		private static final long serialVersionUID = 3440825260214310311L;
		public void paintComponent(Graphics g) {
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);
	        super.paintComponent(g);
	    }
	};
    
	public static JLabel JLetiqueta3 = new JLabel("         "){
		private static final long serialVersionUID = 3440825260214310311L;
		public void paintComponent(Graphics g) {
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);
	        super.paintComponent(g);
	    }
	};
   

    /** Creates new StatusBar */
    public StatusBar() {
    	super();
        this.setFloatable(false);
        JLetiqueta1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        JLetiqueta2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        JLetiqueta3.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        this.add(JLetiqueta1);
        this.add(JLetiqueta2);
        this.add(JLetiqueta3);
        this.add(JPBbarra);
        JPBbarra.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        resetProgresValue();
        ClientHeaderValidator.addSuccessListener(this);
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
    
    public static void setProgresRank(int min, int max) {
    	JLetiqueta3.setText(Language.getWord("LOADING"));
        JPBbarra.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    	JPBbarra.setMinimum(min);
    	JPBbarra.setMaximum(max);
    	JPBbarra.setStringPainted(true);
    }
    
    public static void resetProgresValue() {
    	JLetiqueta3.setText("         ");
    	JPBbarra.setValue(0);
    	JPBbarra.setMinimum(0);
    	JPBbarra.setMaximum(0);
    }

	public void cathSuccesEvent(SuccessEvent e) {
		new ThreadMessage(e.getMessage());
	}
	
	class ThreadMessage extends Thread {
		private String message;
		public ThreadMessage(String message) {
			this.message = message;
			start();
		}
		
		public void run() {
			StatusBar.setLabel3(message);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			StatusBar.setLabel3("");
		}
	}
}