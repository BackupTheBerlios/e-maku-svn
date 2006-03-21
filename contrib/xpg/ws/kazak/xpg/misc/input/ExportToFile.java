/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ExportToFile v 0.1                                                   
* Descripcion:
* Clase encargada de manejar el dialogo mediante el cual se
* define la operacion (importar/exportar) a realizar entre una tabla y un archivo.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/

package ws.kazak.xpg.misc.input;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.idiom.Language;

public class ExportToFile extends JDialog implements ActionListener {

 JRadioButton tableToFileButton;
 JRadioButton fileToTableButton;
 boolean wellDone = false;
 int option = -1;
 int regs;

 public ExportToFile (JFrame aFrame,int numReg) {

  super(aFrame, true);
  regs = numReg;

  setTitle(Language.getWord("COPY") + " " + Language.getWord("DATA"));

  JPanel global = new JPanel();
  global.setLayout(new BoxLayout(global,BoxLayout.Y_AXIS));


  /*** Construcción componentes de la ventana ***/
  //Creacion radio Button

  tableToFileButton = new JRadioButton(Language.getWord("FTF"));
  tableToFileButton.setMnemonic('a'); 

  fileToTableButton = new JRadioButton(Language.getWord("FFT"));
  fileToTableButton.setMnemonic('t'); 

  if (regs > 0) {
      tableToFileButton.setEnabled(true);
      tableToFileButton.setSelected(true);
   }
  else {
        tableToFileButton.setEnabled(false);
        fileToTableButton.setSelected(true);
   }

  ButtonGroup group = new ButtonGroup();
  group.add(tableToFileButton);
  group.add(fileToTableButton);
  
  JPanel rightTop = new JPanel();
  rightTop.setLayout(new BoxLayout(rightTop,BoxLayout.Y_AXIS));
  rightTop.add(tableToFileButton);
  rightTop.add(fileToTableButton);

  Border etched = BorderFactory.createEtchedBorder();
  TitledBorder title = BorderFactory.createTitledBorder(etched,Language.getWord("COPY") + " " + Language.getWord("DATA"));
  title.setTitleJustification(TitledBorder.LEFT);
  rightTop.setBorder(title);
  
  JPanel rightPanel = new JPanel();
  rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
  rightPanel.add(rightTop);
  
  /*** Unión de todos los componentes de la ventana ***/
  
  JPanel downPanel = new JPanel();
  downPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
  downPanel.add(rightPanel);

  title = BorderFactory.createTitledBorder(etched);
  downPanel.setBorder(title);

  JButton ok = new JButton(Language.getWord("OK"));
  ok.setActionCommand("OK");
  ok.addActionListener(this);

  JButton cancel = new JButton(Language.getWord("CANCEL"));
  cancel.setActionCommand("CANCEL");
  cancel.addActionListener(this);

  JPanel botons = new JPanel();
  botons.setLayout(new FlowLayout(FlowLayout.CENTER));
  botons.add(ok);
  botons.add(cancel);

  global.add(downPanel); 
  global.add(botons);

  getContentPane().add(global);
  pack();
  setLocationRelativeTo(aFrame);
  setVisible(true);

 }

 public void actionPerformed(java.awt.event.ActionEvent e) {

  if (e.getActionCommand().equals("CANCEL")) {
      setVisible(false);
      return;
   }		

  if (e.getActionCommand().equals("OK")) {

      if (tableToFileButton.isSelected())
          option = 1;

     if (fileToTableButton.isSelected())
         option = 2;

     wellDone = true;
     setVisible(false);
     return;
   }

 }

public boolean isWellDone() {
  return wellDone;
 } 

public int getOption() {
  return option;
 }

} //Fin de la Clase
