/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ReportFooter v 0.1                                                   
* Descripcion:
* Clase encargada de manejar el cuadro de dialogo mediante
* el cual se seleccionan las caracteristicas del pie de pagina
* para un reporte.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.report;

import ws.kazak.xpg.idiom.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*; 

public class ReportFooter extends JDialog implements ActionListener {

 JComboBox fontStyle;
 JComboBox fontSize; 
 JTextArea headerText;
 JPanel pColor;
 String fontColorValue = "#000000";
 String fontStyleValue = "arial";
 String fontSizeValue = "8";
 String footerStr = "";
 String RGB[] = new String[256];
 boolean wellDone = false;

public ReportFooter(JDialog dialog,JFrame parent) {

  super(dialog,true);                  
  setTitle(Language.getWord("REPFSETT"));
  getContentPane().setLayout(new BorderLayout()); 

  String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
  int k=0;                        

  for (int i = 0; i < 16; i++) {
       for (int j = 0; j < 16; j++) {
            RGB[k] = hex[i] + hex[j];
            k++;
        }
  }

  headerText = new JTextArea(5,5);
  JScrollPane holdHeader = new JScrollPane(headerText);
  JPanel headerPanel = new JPanel();
  headerPanel.setLayout(new BorderLayout());

  JPanel nH = new JPanel();
  nH.setLayout(new GridLayout(1,0));
  JPanel cH = new JPanel();
  cH.setLayout(new BorderLayout());
  Border etched1 = BorderFactory.createEtchedBorder();
  TitledBorder title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("FOOTT"));
  cH.setBorder(title1);
 
  JPanel fontPanel = new JPanel();
   fontPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
 
  String[] values = {"Arial","Arial Black","Arial Narrow","Book Antiqua","Bookman Old Style","Calixto MT","Century Gothic","Comic Sans MS","Copperplate Gothic Bold","Copperplate Gothic Light","Courier New","Garamond","Helvetica","Impact","Lucida Console","Lucida Handwriting","Lucida Sans","Lucida Sans Unicode","Map Symbols","Marlett","Matisse ITC","Monotype Sorts","MS Outlook","MT Extra","News Gothic MT","OCR A Extended","Symbol","Tahoma","Tempus Sans ITC","Times New Roman","Verdana","Webdings","Westminster","Wingdings"};

  fontStyle = new JComboBox(values);
  String[] values1 = {"8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
  fontSize = new JComboBox(values1);

  JButton fontColor = new JButton(Language.getWord("FCOLOR"));
  fontColor.setActionCommand("COLOR");
  fontColor.addActionListener(this);

  title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("FSETT"));

  fontPanel.setBorder(title1);
  pColor = new JPanel();
  pColor.setPreferredSize(new Dimension(15,15));
  title1 = BorderFactory.createTitledBorder(etched1);
  pColor.setBorder(title1);
  pColor.setBackground(Color.black);

  fontPanel.add(new JLabel(Language.getWord("STYLE") + ": "));
  fontPanel.add(fontStyle);
  fontPanel.add(new JPanel());
  fontPanel.add(new JLabel(" " + Language.getWord("LONGTYPE") + ": "));
  fontPanel.add(fontSize);
  fontPanel.add(new JLabel("pt"));
  fontPanel.add(new JPanel());
  fontPanel.add(fontColor); 
  fontPanel.add(pColor);

  JPanel sH = new JPanel();
  sH.setLayout(new FlowLayout(FlowLayout.CENTER));
  title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("SLINE"));
  sH.setBorder(title1);

  nH.add(fontPanel);
  cH.add(holdHeader,BorderLayout.CENTER);

  headerPanel.add(nH,BorderLayout.NORTH);
  headerPanel.add(cH,BorderLayout.CENTER);

  JButton ok = new JButton(Language.getWord("OK"));
  ok.setActionCommand("OK");
  ok.addActionListener(this);

  JButton cancel = new JButton(Language.getWord("CANCEL"));
  cancel.setActionCommand("CANCEL");
  cancel.addActionListener(this); 

  JPanel botonD = new JPanel();
  botonD.setLayout(new FlowLayout(FlowLayout.CENTER));
  botonD.add(ok);
  botonD.add(cancel);

  getContentPane().add(headerPanel,BorderLayout.CENTER);
  getContentPane().add(botonD,BorderLayout.SOUTH);
  pack();
  setLocationRelativeTo(parent);
  setVisible(true); 
 }

 public void actionPerformed(java.awt.event.ActionEvent e) {

   if (e.getActionCommand().equals("COLOR")) {

       Color newColor = JColorChooser.showDialog(ReportFooter.this,Language.getWord("CTC"),Color.white);
 
       if (newColor != null) {
         pColor.setBackground(newColor);
         fontColorValue = setColor(newColor.getRed(),newColor.getGreen(),newColor.getBlue());
       }

    }

   if (e.getActionCommand().equals("CANCEL")) {

       setVisible(false);
    }

   if (e.getActionCommand().equals("OK")) {

       String footText = headerText.getText();
       fontStyleValue = (String) fontStyle.getSelectedItem();
       fontSizeValue = (String) fontSize.getSelectedItem();
       String fontTag = "";

       if (footText.length()>0) 

           fontTag += "<center><hr width=100% size=1></center>" + "<p class=\"footer\">" + footText + "</p>";

       footerStr = fontTag;
       wellDone=true;
       setVisible(false);
   }
 }

 public boolean isNum(String s) {

    for (int i = 0; i < s.length(); i++) {

         char c = s.charAt(i);
         if (!Character.isDigit(c))
          return false;
     }

    return true;
  }

 public boolean isWellDone() {

   return wellDone;
 }

 public String getFooter() {

   return footerStr;
 }

 public String getFontStyle() {
   return fontStyleValue;
  }

 public String getFontSize() {
   return fontSizeValue;
  }

 public String getFontColor() {
   return fontColorValue;
  }

 public String setColor(int red,int green,int blue) {
   return "#" + RGB[red] + RGB[green] + RGB[blue];
  }

} //Fin de la Clase
