/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ExportSeparatorField v 0.1                                                   
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

import ws.kazak.xpg.idiom.*;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.*;
import java.beans.*; 
import java.awt.*;
import java.awt.event.*;

public class ExportSeparatorField extends JDialog implements ActionListener {

 private String typedText = null;
 private JOptionPane optionPane;
 boolean wasDone = false;
 JComboBox limiter;
 String answer=" ";
 final JTextField textField;

 /**
  * METODO Constructor ExportSeparatorField
  *
  */
 public ExportSeparatorField(Frame aFrame) {

   super(aFrame, true);
   setTitle(Language.getWord("SEPA"));

   JPanel block = new JPanel();
   block.setLayout(new BorderLayout());
   JLabel label = new JLabel(Language.getWord("SFS"),JLabel.CENTER);
   block.add(label,BorderLayout.NORTH);
   JPanel central = new JPanel();
   central.setLayout(new GridLayout(0,1));
   JLabel pre = new JLabel(Language.getWord("PD"),JLabel.LEFT);
   central.add(pre);
   pre = new JLabel(Language.getWord("CZ"),JLabel.LEFT);
   central.add(pre);
   JPanel side = new JPanel();
   side.setLayout(new GridLayout(0,1));
   String[] limiters = {Language.getWord("SB"),Language.getWord("REPCSV"),Language.getWord("TAB"),Language.getWord("COMMA"),
   Language.getWord("DOT"),Language.getWord("COLON"),Language.getWord("SCOLON")};

   limiter = new JComboBox(limiters);
   limiter.addActionListener(this); 
   side.add(limiter);
   textField = new JTextField();
   side.add(textField);
   block.add(central,BorderLayout.CENTER);
   block.add(side,BorderLayout.EAST);

   Object[] array = {block};

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

   addWindowListener(new WindowAdapter() {

     public void windowClosing(WindowEvent we) {
       optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
      }
   });

   textField.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
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

             setVisible(false);
             wasDone = true;
             typedText = textField.getText();

             if (typedText.length()>0)
                 answer = typedText;
          } 
         else 
            setVisible(false);
        }
     }
   });

   setSize(260,150);
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

 public void actionPerformed(ActionEvent e) {

    JComboBox cb = (JComboBox)e.getSource();
    int index = cb.getSelectedIndex();

    switch(index) {

           case 0: answer = " "; break;
           case 1: answer = "csv"; break;
           case 2: answer = "	"; break;
           case 3: answer = ","; break;
           case 4: answer = "."; break;
	   case 5: answer = ":"; break;
           case 6: answer = ";"; break;
        }

    textField.setText("");
  }

} //Fin de la Clase

