/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS DropDB v 0.1                                                   
* Descripcion:
* Esta clase se encarga mostrar un dialogo donde la persona escoge 
* la base de datos a eliminar.            
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.menu;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import ws.kazak.xpg.idiom.Language;

public class DropDB extends JDialog {
 public String comboText = null;
 private JOptionPane optionPane;
 JComboBox combo1;
 boolean doIt = false;

 /**
  * METODO Constructor DropDB
  *
  */
 public DropDB (Frame aFrame,Vector dbnames) 
  {
   super(aFrame, true);
   setTitle(Language.getWord("DROPDB"));

   Vector dataB = new Vector();
   for(int i=0;i<dbnames.size();i++)
   {
    Vector o = (Vector) dbnames.elementAt(i);
    String db = (String) o.elementAt(0);
    dataB.addElement(db);
   }
   combo1 = new JComboBox(dataB);

   final String msgString1 = Language.getWord("QDROPDB");
   final JTextField textField = new JTextField(20);
   Object[] array = {msgString1, combo1};

   final String btnString1 = Language.getWord("DROP");
   final String btnString2 = Language.getWord("CANCEL");
   Object[] options = {btnString1, btnString2};

   optionPane = new JOptionPane(array, 
                                JOptionPane.QUESTION_MESSAGE,
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

  optionPane.addPropertyChangeListener(new PropertyChangeListener() 
   {
     public void propertyChange(PropertyChangeEvent e) 
      {
       String prop = e.getPropertyName();
       if (isVisible() && (e.getSource() == optionPane)
           && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
           prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) 
        {   
         Object value = optionPane.getValue();
         if (value == JOptionPane.UNINITIALIZED_VALUE) 
           return;

         if (value.equals(btnString1)) 
          {
           comboText = (String)combo1.getSelectedItem();
           doIt = true;
           setVisible(false);
          } 
         else 
            setVisible(false);
       }
     }
   });

   pack();
   setLocationRelativeTo(aFrame);
   setVisible(true);
 }

 public boolean confirmDropDB()
  {
    return doIt;
  }

} //Fin de la Clase

