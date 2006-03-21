/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ImportSeparatorField v 0.1                                                   
* Descripcion:
* Clase que se encarga de manejar el dialogo a traves del
* cual se define el separador para los campos de una consulta
* que vaya a ser almacenada en un archivo.
*
* Esta clase es instanciada desde la clase Queries.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/

package ws.kazak.xpg.misc.input;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ws.kazak.xpg.idiom.Language;

public class ImportSeparatorField extends JDialog {

 private String typedText = null;
 private JOptionPane optionPane;
 boolean wasDone = false;
 String answer=" ";
 final JTextField textField;

 /**
  * METODO Constructor ImportSeparatorField
  *
  */
 public ImportSeparatorField(Frame aFrame) {

   super(aFrame, true);
   setTitle(Language.getWord("SEPA"));

   JPanel block = new JPanel();
   block.setLayout(new FlowLayout());

   JLabel label = new JLabel(Language.getWord("IFSEP"),JLabel.CENTER);
   JPanel upPanel = new JPanel();
   upPanel.add(label);

   JPanel central = new JPanel();
   JLabel pre = new JLabel(Language.getWord("SEPA") + ": ",JLabel.LEFT);
   central.add(pre);

   JPanel side = new JPanel();
   textField = new JTextField(5);
   side.add(textField);

   block.add(central,BorderLayout.CENTER);
   block.add(side,BorderLayout.EAST);

   Object[] array = {upPanel,block};

   final String btnString1 = Language.getWord("OK");
   final String btnString2 = Language.getWord("CANCEL");
   Object[] options = {btnString1, btnString2};

   optionPane = new JOptionPane(array, 
                                JOptionPane.PLAIN_MESSAGE,
                                JOptionPane.YES_NO_OPTION,
                                null,
                                options,
                                options[0]);
   setContentPane(optionPane);

   addWindowListener(new WindowAdapter() 
     {
     public void windowClosing(WindowEvent we) 
      {
       optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
      }
     });

   textField.addActionListener(new ActionListener() 
    {
     public void actionPerformed(ActionEvent e) 
      {
       optionPane.setValue(btnString1);
      }
    });

   optionPane.addPropertyChangeListener(new PropertyChangeListener() {

     public void propertyChange(PropertyChangeEvent e) {

       String prop = e.getPropertyName();

       if (isVisible() && (e.getSource() == optionPane)
           && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
           prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
   
           Object value = optionPane.getValue();

           if (value == JOptionPane.UNINITIALIZED_VALUE) 
               return;

         if (value.equals(btnString1)) {
             wasDone = true;
             typedText = textField.getText();

             if (typedText.length()>0)
                 answer = typedText;
             else
                 answer = " ";             
          } 

         setVisible(false);
       }
     }
   });

   pack();
   setLocationRelativeTo(aFrame);
   show();
 }

 public boolean isDone() {
   return wasDone;
  }

 public String getLimiter() {
   return answer; 
  }

} //Fin de la Clase

