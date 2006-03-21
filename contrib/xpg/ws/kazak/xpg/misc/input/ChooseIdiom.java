/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ChooseIdiom v 0.1                                           
* Esta clase muestra una ventana de dialogo donde el usuario puede  
* escoger el idioma en el que desea visualizar la aplicación.       
*                                                                   
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*
* Fecha: 2001/07/31                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws 
*/
package ws.kazak.xpg.misc.input;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ChooseIdiom extends JDialog {
 
 String idiom = "English";
 boolean save = false;

 /**
  * METODO CONSTRUCTOR
  */
 public ChooseIdiom (JFrame aFrame) {
  
  super(aFrame, true);  
  setTitle("Your Language");    
  
  JLabel message = new JLabel("Please, choose your language option");
  
  URL imgURL = getClass().getResource("/icons/FlagUK.png");
  JRadioButton englishButton = new JRadioButton("English", new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)), true);
  englishButton.setMnemonic('n');
  englishButton.setActionCommand("english");

  imgURL = getClass().getResource("/icons/FlagSpain.png");
  JRadioButton spanishButton = new JRadioButton("Español", new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  spanishButton.setMnemonic('s');
  spanishButton.setActionCommand("spanish");
  
  ButtonGroup group = new ButtonGroup();
  group.add(englishButton);
  group.add(spanishButton);

  JPanel radioPanel = new JPanel();               
  radioPanel.setLayout(new GridLayout(0, 1));
  radioPanel.add(message);     
  radioPanel.add(englishButton);          
  radioPanel.add(spanishButton);
  
  RadioListener myListener = new RadioListener();
  spanishButton.addActionListener(myListener);
  englishButton.addActionListener(myListener);
 
  final String btnString1 = "Accept";
  final String btnString2 = "Cancel";
  Object[] options = { btnString1, btnString2 };
  Object[] array = { radioPanel };

  final JOptionPane optionPane = new JOptionPane(array, 
	                       JOptionPane.PLAIN_MESSAGE,
	                       JOptionPane.YES_NO_OPTION,
	                       null,
	                       options,
	                       options[0]);
  setContentPane(optionPane);

  optionPane.addPropertyChangeListener(new PropertyChangeListener() {
   public void propertyChange(PropertyChangeEvent e) {
    String prop = e.getPropertyName();
    if (isVisible() && (e.getSource() == optionPane) && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
		       prop.equals(JOptionPane.INPUT_VALUE_PROPERTY)))
     {
	    Object value = optionPane.getValue();
	    if (value == JOptionPane.UNINITIALIZED_VALUE)
		return;					
  	    if (value.equals(btnString1))   
		setVisible(false);
 	    if (value.equals(btnString2))
		 System.exit(0);
     }
   }
  });    

 }
 
 /** Clase para escuchar los botones de seleccion. */
 class RadioListener implements ActionListener 
  {
     public void actionPerformed(ActionEvent e) 
     {
       if(e.getActionCommand().equals("spanish"))
         idiom = "Spanish";
       if(e.getActionCommand().equals("english"))
         idiom = "English";
     }
  }
 
 /**
  * Método getIdiom
  */
 public String getIdiom() 
 {
  return idiom;
 } 
 
}              
