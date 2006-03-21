/*
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS CreateDB v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el dialogo mediante el
* cual se crea una base de datos. 
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.menu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.idiom.Language;

public class CreateDB extends JDialog implements ActionListener {

 private String typedText = null;
 private JOptionPane optionPane;
 final JTextField textField;
 JTextArea LogWin;
 PGConnection conn;
 boolean wasDone = false;
 Frame fr;

 /**
  * METODO Constructor CreateDB
  *
  */
 public CreateDB (Frame aFrame, PGConnection currentConn, JTextArea monitor) {

   super(aFrame, true);
   fr = aFrame;
   conn = currentConn;
   LogWin = monitor;
   setTitle(Language.getWord("NEWDB"));

   final String msgString1 = Language.getWord("QUESTDB");
   JLabel msg = new JLabel(msgString1,JLabel.CENTER);
   JPanel label = new JPanel();
   label.setLayout(new BorderLayout());
   label.add(msg,BorderLayout.CENTER);
   JPanel out = new JPanel(); 
   out.add(label);

   textField = new JTextField(20);

   textField.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Creating();
      }
    });

   JPanel text = new JPanel();
   text.setLayout(new BoxLayout(text,BoxLayout.X_AXIS));
   text.add(new JPanel());
   text.add(textField);
   text.add(new JPanel());

   JButton ok = new JButton(Language.getWord("CREATE"));
   ok.setActionCommand("OK");
   ok.addActionListener(this);
   JButton cancel = new JButton(Language.getWord("CANCEL"));
   cancel.setActionCommand("CANCEL");
   cancel.addActionListener(this);

   JPanel topOne = new JPanel();
   topOne.setLayout(new BoxLayout(topOne,BoxLayout.Y_AXIS)); 
   topOne.add(out);
   topOne.add(text);

   JPanel botones = new JPanel();
   botones.setLayout(new FlowLayout(FlowLayout.CENTER));
   botones.add(ok);
   botones.add(cancel);

  JPanel global = new JPanel();
  global.setLayout(new BoxLayout(global,BoxLayout.Y_AXIS));
  global.add(topOne);
  global.add(botones);

  getContentPane().add(global);

  pack();
  setLocationRelativeTo(fr);
  setVisible(true);
    
 }

 public void actionPerformed(java.awt.event.ActionEvent e) {

   if (e.getActionCommand().equals("CANCEL")) {

       setVisible(false);
       return;
    }

   if (e.getActionCommand().equals("OK")) {

       Creating();
       return;
    }
 }

 public boolean isDone() {
  return wasDone;
 }

 /**
  * METODO getDBname
  * Retorna en una cadena el nombre de la 
  * base de datos digitado por el usuario
  */
 public String getDBname() {
  return typedText; 
 }

 public void Creating() {

    typedText = textField.getText();

    if (typedText.indexOf(" ") != -1) {

        JOptionPane.showMessageDialog(fr,Language.getWord("NOCHAR"),
                                      Language.getWord("ERROR!"),
                                      JOptionPane.ERROR_MESSAGE);
        return;
     }

    if (typedText.length()==0) {

        JOptionPane.showMessageDialog(fr,Language.getWord("EMPTYDB"),
                                      Language.getWord("ERROR!"),
                                      JOptionPane.ERROR_MESSAGE);
        return;
     }

    setVisible(false);
    String result = conn.SQL_Instruction("CREATE DATABASE \"" + typedText + "\"");
    addTextLogMonitor(Language.getWord("EXEC") + "CREATE DATABASE "+ typedText + "\"");

    if (result.equals("OK"))
        wasDone = true;
    else {
           textField.selectAll();
           result = result.substring(7,result.length() - 1);

           JOptionPane.showMessageDialog(fr,Language.getWord("ERRORPOS") + result,
                                         Language.getWord("ERROR!"),
                                         JOptionPane.ERROR_MESSAGE);
           typedText = null;
           textField.setText("");
      }

    addTextLogMonitor(Language.getWord("RES") + result);

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
