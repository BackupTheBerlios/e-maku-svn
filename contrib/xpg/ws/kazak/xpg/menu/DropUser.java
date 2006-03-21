/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS DropUser v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el dialogo mediante el cual
* se elimina un usuario del SMBD.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.menu;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.idiom.Language;

public class DropUser extends JDialog {
 
 private JOptionPane optionPane;
 PGConnection conn;
 JTextArea LogWin; 
 JComboBox cmbUser;

 public DropUser (JFrame aFrame,PGConnection pg, JTextArea area) {

   super(aFrame, true);
   
   conn = pg;
   LogWin = area;
   setTitle(Language.getWord("DROP") + " " + Language.getWord("USER"));
   String[] usuarios = conn.getUsers();
  
   JLabel msgString1 = new JLabel(Language.getWord("SELUSR"));
   cmbUser = new JComboBox(usuarios);
   Object[] array = {msgString1, cmbUser};

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
   setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

   addWindowListener(new WindowAdapter() {

     public void windowClosing(WindowEvent we) {
          optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
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

             String user = (String) cmbUser.getSelectedItem();
             String SQL = "DROP USER " + user + ";";
             String result = conn.SQL_Instruction(SQL);
             addTextLogMonitor(Language.getWord("EXEC") + SQL + "\"");
             addTextLogMonitor(Language.getWord("RES") + result);

             if (result.equals("OK"))
                 setVisible(false);
             else {
                   JOptionPane.showMessageDialog(DropUser.this,
                   result,
                   Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                   return;
              }
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

 /**
  * Metodo addTextLogMonitor
  * Imprime mensajes en el Monitor de Eventos
  */
 public void addTextLogMonitor(String msg) {

   LogWin.append(msg + "\n");	
   int longiT = LogWin.getDocument().getLength();

   if (longiT > 0)
       LogWin.setCaretPosition(longiT - 1);
 }
 
} //Fin de la Clase

