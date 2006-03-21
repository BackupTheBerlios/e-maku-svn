/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ChooseIdiomButton v 0.1                                     
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
import javax.swing.JTextArea;

import ws.kazak.xpg.idiom.Language;

public class ChooseIdiomButton extends JDialog {
 
 String idiom;
 boolean save = false;
 JTextArea LogWin;

 /**
  * METODO CONSTRUCTOR
  */
 public ChooseIdiomButton (JFrame aFrame,JTextArea monitor) {

  super(aFrame, true);
  LogWin = monitor;
  setTitle(Language.getWord("TITIDIOM"));    
  idiom = "English";
  
  JLabel message = new JLabel(Language.getWord("MSGIDIOM"));
  
  // Create the radio buttons.
  URL imgURL = getClass().getResource("/icons/FlagUK.png");
  JRadioButton englishButton = new JRadioButton("English", new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)), true);
  englishButton.setMnemonic('n');
  englishButton.setActionCommand("english");

  imgURL = getClass().getResource("/icons/FlagSpain.png");
  JRadioButton spanishButton = new JRadioButton("Español", new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  spanishButton.setMnemonic('s');
  spanishButton.setActionCommand("spanish");
  
  // Group the radio buttons.
  ButtonGroup group = new ButtonGroup();
  group.add(englishButton);
  group.add(spanishButton);
     
  // Put the radio buttons in a column in a panel 
  JPanel radioPanel = new JPanel();               
  radioPanel.setLayout(new GridLayout(0, 1));
  radioPanel.add(message);     
  radioPanel.add(englishButton);          
  radioPanel.add(spanishButton);
  
  // Register a listener for the radio buttons.
  RadioListener myListener = new RadioListener();
  spanishButton.addActionListener(myListener);
  englishButton.addActionListener(myListener);
 
  final String btnString1 = Language.getWord("OK");
  final String btnString2 = Language.getWord("CANCEL");
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
		       prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {

	    Object value = optionPane.getValue();

	    if (value == JOptionPane.UNINITIALIZED_VALUE)
		return;					

	    if (value.equals(btnString1)) {

		save = true;
                addTextLogMonitor (Language.getWord("IDIOMSEL") + idiom);         
		setVisible(false);
             }

            if (value.equals(btnString2))
		 setVisible(false);
     }
   }
  });    

  pack();
  setLocationRelativeTo(aFrame);
  setVisible(true);

 }
 
 /** Listens to the radio buttons. */
 class RadioListener implements ActionListener {

     public void actionPerformed(ActionEvent e) {

       if (e.getActionCommand().equals("spanish")) {
           idiom = "Spanish";
        }

       if (e.getActionCommand().equals("english")) {
           idiom = "English";
        }             
     }
 }
 
 /**
  * Método getIdiom
  */
 public String getIdiom() {
  return idiom;
 } 

 /**
  * Método getSave
  */
 public boolean getSave() {
  return save;
 } 

 /**
  * Metodo addTextLogMonitor
  * Imprime mensajes en el Monitor de Eventos
  */
 public void addTextLogMonitor(String msg) {
   LogWin.append(msg + "\n");
   int longiT = LogWin.getDocument().getLength();
   if(longiT > 0)
      LogWin.setCaretPosition(longiT - 1);
  }

} //Fin de la Clase              
