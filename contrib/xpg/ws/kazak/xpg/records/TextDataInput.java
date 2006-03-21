/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2002
*
* CLASS TextDataInput v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el dialogo a traves del
* cual se inserta un dato de tipo "text" en una tabla.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.records;

import ws.kazak.xpg.idiom.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TextDataInput extends JDialog implements ActionListener {
 
 JTextArea data;
 String dataText = "";
 boolean wellDone = false;

 public TextDataInput(JDialog dialog,String fieldName, String previewStr) {

    super(dialog, true);
    setTitle(fieldName);
    this.getContentPane().setLayout(new BorderLayout());
    data = new JTextArea(30,50);
    data.setText(previewStr);

    JScrollPane textScroll = new JScrollPane(data);

    JButton ok = new JButton(Language.getWord("OK"));
    ok.setActionCommand("OK");
    ok.addActionListener(this);
    JButton clear = new JButton(Language.getWord("CLR"));
    clear.setActionCommand("CLEAR");
    clear.addActionListener(this);
    JButton cancel = new JButton(Language.getWord("CANCEL"));
    cancel.setActionCommand("CANCEL");
    cancel.addActionListener(this);

    JPanel botons = new JPanel();
    botons.setLayout(new FlowLayout(FlowLayout.CENTER));
    botons.add(ok);
    botons.add(clear);
    botons.add(cancel);

    this.getContentPane().add(textScroll,BorderLayout.CENTER);
    this.getContentPane().add(botons,BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(this);
    setVisible(true);
  }

 public void actionPerformed(java.awt.event.ActionEvent e) {

   if (e.getActionCommand().equals("OK")) {
       dataText = data.getText(); 
       wellDone = true;
       setVisible(false);
    }

   if (e.getActionCommand().equals("CANCEL")) {
       setVisible(false);
    }

   if (e.getActionCommand().equals("CLEAR")) {
       data.setText("");
    }
  }

 public String getValue() {
    return dataText;
  }

 public boolean isWellDone() {
    return wellDone;
  }

}
