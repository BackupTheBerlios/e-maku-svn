/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS GenericQuestionDialog v 0.1                                            
* Esta clase se encarga de mostrar ventanas de confirmación ante   
* eventos que requieran confirmación. Las opciones siempre son de 
* tipo booleano.                                                 
* Los objetos de este tipo se crean desde la clase XPg.         
*                                                              
* Fecha: 2001/07/31                                           
* Autores: Beatriz Florián  - bettyflor@kazak.ws             
*          Gustavo Gonzalez - xtingray@kazak.ws
*          Angela Sandobal  - angesand@libertad.univalle.edu.co
*/
package ws.kazak.xpg.misc.input;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.event.*;
import java.beans.*;

public class GenericQuestionDialog extends JDialog {

 private JOptionPane optionPane;
 boolean exit;
 
 /**
  * METODO CONSTRUCTOR
  */
 public GenericQuestionDialog (JFrame parent, String button1, String button2, String title, String message)  {        
   super(parent, true);        
   setTitle(title);                            
   final String btnString1 = button1;                                
   final String btnString2 = button2;                                 
   Object[] options = {btnString1, btnString2};                    
   String line = message;     
   JLabel msg = new JLabel(message,JLabel.CENTER);
   Object[] array = { msg };                                     
   optionPane = new JOptionPane(array,                        
                               JOptionPane.PLAIN_MESSAGE,     
                               JOptionPane.YES_NO_OPTION,     
                               null,                          
                               options,                       
                               options[0]);                                                                            
   setContentPane(optionPane);                                
   setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
   addWindowListener(new WindowAdapter() 
     {
      public void windowClosing(WindowEvent we) 
       {
        optionPane.setValue(new Integer(                 
                            JOptionPane.CLOSED_OPTION)); 
       }                                                        
      });       

   optionPane.addPropertyChangeListener(new PropertyChangeListener() 
    {
     public void propertyChange(PropertyChangeEvent e) 
      {
       String prop = e.getPropertyName();
                                  
       if (isVisible()&& (e.getSource() == optionPane)                 
             && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
             prop.equals(JOptionPane.INPUT_VALUE_PROPERTY)))
        {                                         
          Object value = optionPane.getValue();
          if (value == JOptionPane.UNINITIALIZED_VALUE) 
            return;

          if (value.equals(btnString1))
           {
	    setVisible(false);
	    exit = true;
	   }
          else
           {
	    setVisible(false);
	    exit = false;
	   }
        }               
     }
   });

   pack();
   setLocationRelativeTo(parent);
   setVisible(true);

 }
 
 /**
  * METODO getSelecction
  * Método que retorna la respuesta del usuario
  */
 public boolean getSelecction() {
   return exit;
  }

} //Fin de la Clase    
