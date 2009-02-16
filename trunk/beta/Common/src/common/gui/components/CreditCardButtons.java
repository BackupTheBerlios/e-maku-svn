package common.gui.components;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;

import common.gui.forms.GenericForm;

/**
 * CreditCardButton.java Creado el 10-feb-2009
 * 
 * Este archivo es parte de E-Maku <A
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
 * Esta clase crea un panel de botones para pago con tarjetas <br>
 * 
 * @author <A href='mailto:pipelx@gmail.com'>Luis Felipe Hernandez </A>
 */
public class CreditCardButtons extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8559552850464433710L;
	
	private GenericForm genericForm;
	private EmakuTouchField emakuTouchField;
	private JButton[][] ccButtons = {{new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_american_express_100x65.png"))),
								  	  new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_diners_100x65.png"))),
									  new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_mastercard_100x65.png")))},
								     {new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_visa_100x65.png"))),
									  new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_visa_electron_100x65.png"))),
									  new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_american_debito_100x65.png")))},
									 {new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_maestro_debito_100x65.png"))),
									  new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_mastercard_debito_100x65.png"))),
									  new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_visa_debito_100x65.png")))}
														  
	};
	
	private JButton[][] buttons = {{new JButton("7"),new JButton("8"),new JButton("9")},
			   {new JButton("4"),new JButton("5"),new JButton("6")},
			   {new JButton("1"),new JButton("2"),new JButton("3")},
			   {new JButton("0"),new JButton("00"),new JButton("000")},
			   {new JButton("C"),new JButton("<<"),new JButton("X")}};

	private String importValueButton;
	private String keyValue;
	
	public CreditCardButtons(GenericForm genericForm,EmakuTouchField emakuTouchField,Font font,String importValueButton,String keyValue) {
		this.genericForm=genericForm;
		this.emakuTouchField=emakuTouchField;
		this.importValueButton=importValueButton;
		this.keyValue=keyValue;
		JPanel cards = new JPanel(new GridLayout(3,3));
		int i=0;
		for(JButton[] row:ccButtons) {
			for(JButton col:row) {
				cards.add(col);
				col.addActionListener(this);
				col.setName((i++)+"");
			}
		}
		
		JPanel matriz = new JPanel(new GridLayout(5,3));
		Font num = new Font("Dialog",1,30);
		for(JButton[] row:buttons) {
			for(JButton col:row) {
				if (col!=null) {
					matriz.add(col);
					col.setFont(num);
					col.addActionListener(this);
				}
			}
		}

		this.add(cards,BorderLayout.EAST);
		this.add(matriz,BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton)e.getSource();
		String value = b.getText();
		if ("".equals(value)) {
			genericForm.setExternalValues(keyValue, b.getName());
			if (emakuTouchField.getText().equals("0")) {
				System.out.println("valor: "+importValueButton);
				emakuTouchField.setText(genericForm.getExternalValueString(importValueButton));
			}
			emakuTouchField.doFormat();
		}
		else if ("X".equals(value)) {
			emakuTouchField.setText("0");
			emakuTouchField.doFormat();
		}
		else if ("C".equals(value)) {
			emakuTouchField.setText("");
		}
		else if ("<<".equals(value)) {
			try {
				int offset = emakuTouchField.getText().length();
				emakuTouchField.getDocument().remove(offset-1,1);
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}
		else {
			try {
				int offset = emakuTouchField.getText().length();
				emakuTouchField.getDocument().insertString(offset,value,null);
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}
	}

}
