/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ReportHeader v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el dialogo mediante el
* cual se definen las caracteristicas del enheaderStrezado de un
* reporte.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.idiom.Language;

public class ReportHeader extends JDialog implements ActionListener {

 JTextArea headerText;
 JComboBox fontStyleCombo;
 JComboBox fontSizeCombo; 
 JComboBox types;

 JTextField theTitle;

 JButton browseButton;
 JButton fontColorButton;
 boolean isWell = false;
 JPanel pColor;
 File fileLogo;

 String strTitle = "";
 String headerD;
 String headerStr = "";

 String fontStyleValue = "";
 String fontColorValue = "";
 String fontSizeValue = "";

 String RGB[] = new String[256];

 public ReportHeader(JDialog diag,JFrame parent) {

   super(diag,true);
   setTitle(Language.getWord("REPHSETT"));
   getContentPane().setLayout(new BorderLayout()); 
   headerText = new JTextArea(5,5);

   String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
   int k=0;                        

   for (int i = 0; i < 16; i++) {
        for (int j = 0; j < 16; j++) {
             RGB[k] = hex[i] + hex[j];
             k++;
         }
   }

   JScrollPane holdHeader = new JScrollPane(headerText);
   JPanel header = new JPanel();
   header.setLayout(new BorderLayout());

   Border etched1 = BorderFactory.createEtchedBorder();
   TitledBorder title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("HEADER"));

   JPanel nH = new JPanel();
   nH.setLayout(new GridLayout(1,0));

   JPanel cH = new JPanel();
   cH.setLayout(new BorderLayout());
   title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("HEADERT"));
   cH.setBorder(title1);

   JPanel bloke = new JPanel();
   bloke.setLayout(new BorderLayout());
   theTitle = new JTextField();
   title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("TITTEXT"));
   bloke.setBorder(title1);
   bloke.add(theTitle,BorderLayout.CENTER);
 
   JPanel fontPanel = new JPanel();
   fontPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
 
   String[] values = {"Arial","Arial Black","Arial Narrow","Book Antiqua","Bookman Old Style","Calixto MT","Century Gothic","Comic Sans MS","Copperplate Gothic Bold","Copperplate Gothic Light","Courier New","Garamond","Helvetica","Impact","Lucida Console","Lucida Handwriting","Lucida Sans","Lucida Sans Unicode","Map Symbols","Marlett","Matisse ITC","Monotype Sorts","MS Outlook","MT Extra","News Gothic MT","OCR A Extended","Symbol","Tahoma","Tempus Sans ITC","Times New Roman","Verdana","Webdings","Westminster","Wingdings"};

   fontStyleCombo = new JComboBox(values);

   String[] values1 = {"8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
   fontSizeCombo = new JComboBox(values1);
   fontSizeCombo.setSelectedIndex(4);
   fontColorButton = new JButton(Language.getWord("FCOLOR"));
   fontColorButton.setActionCommand("COLOR");
   fontColorButton.addActionListener(this);

   pColor = new JPanel();
   pColor.setPreferredSize(new Dimension(15,15));
   title1 = BorderFactory.createTitledBorder(etched1);
   pColor.setBorder(title1);
   title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("HEADER")+Language.getWord("FSETT"));

   fontPanel.setBorder(title1);
   pColor.setBackground(Color.black);
 
   fontPanel.add(new JLabel(Language.getWord("STYLE")+":"));
   fontPanel.add(fontStyleCombo);
   fontPanel.add(new JPanel());
   fontPanel.add(new JLabel(" "+Language.getWord("LONGTYPE")+":"));
   fontPanel.add(fontSizeCombo);
   fontPanel.add(new JLabel("pt"));
   fontPanel.add(new JPanel());
   fontPanel.add(fontColorButton); 
   fontPanel.add(pColor);

   JPanel pDate = new JPanel();
   pDate.setLayout(new GridLayout(0,1));
   title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("DATE"));
   pDate.setBorder(title1);

   String[] dates = {Language.getWord("NODATE"),Language.getWord("DATE0"),Language.getWord("DATE1"),Language.getWord("DATE2"),Language.getWord("DATE3")};
   types = new JComboBox(dates);

   JPanel level = new JPanel();
   level.setLayout(new FlowLayout(FlowLayout.CENTER));
   level.add(new JLabel(Language.getWord("FORMAT")+":"));
   level.add(types);
   level.setBorder(title1);
 
   browseButton = new JButton(Language.getWord("BROWSE"));
   browseButton.setActionCommand("BROWSE");
   browseButton.addActionListener(this);

   JPanel sH = new JPanel();
   sH.setLayout(new GridLayout(0,1));
   sH.add(level); 

   nH.add(fontPanel);
   cH.add(holdHeader,BorderLayout.CENTER);

   JPanel pri = new JPanel();
   pri.setLayout(new BorderLayout());
   pri.add(nH,BorderLayout.NORTH);
   pri.add(cH,BorderLayout.CENTER);

   header.add(bloke,BorderLayout.NORTH);
   header.add(sH,BorderLayout.CENTER);
   header.add(pri,BorderLayout.SOUTH); 

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

   getContentPane().add(header,BorderLayout.CENTER);
   getContentPane().add(botonD,BorderLayout.SOUTH); 
   pack();
   setLocationRelativeTo(parent);
   setVisible(true); 

 }

 public void actionPerformed(java.awt.event.ActionEvent e) {

 if (e.getActionCommand().equals("CANCEL")) {
      setVisible(false);
  }

 if (e.getActionCommand().equals("COLOR")) {

     Color newColor = JColorChooser.showDialog(ReportHeader.this,
                                               Language.getWord("CTC"),
                                               Color.white);
     if (newColor != null) {
         pColor.setBackground(newColor);
         fontColorValue = setColor(newColor.getRed(),newColor.getGreen(),newColor.getBlue());
       }
  }

 if (e.getActionCommand().equals("OK")) {

     String headerTextString = "";
     String mainTitle = "";
     String tStrDate = "";
     String logo = "";
     String strDate = (String) types.getSelectedItem();

     int typeDate = types.getSelectedIndex();
     headerD = headerText.getText();
     strTitle = theTitle.getText();

     if (headerD.length()>0) {
         fontStyleValue = (String) fontStyleCombo.getSelectedItem();
         fontSizeValue = (String) fontSizeCombo.getSelectedItem();
         headerTextString = "<p class=\"header\">" + headerD + "</p>\n";
     }

    if (strTitle.length()>0) 
         mainTitle = "<b><u>" + strTitle + "</u></b>\n";

    if (!strDate.equals(Language.getWord("NODATE"))) {

        switch(typeDate) {
               case 1 : tStrDate = getFormatZero(getTime());
                        break;
               case 2 : tStrDate = getFormatOne(getTime()); 
                        break;
               case 3 : tStrDate = getFormatTwo(getTime()); 
                        break;
               case 4 : tStrDate = getFormatThree(getTime());
        }
     }

    headerStr += "<center>\n" + mainTitle + "&nbsp;&nbsp;&nbsp;" + tStrDate + "\n<br clear=all>\n</center>\n" + headerTextString;
    headerStr += "\n<center><hr width=100% size=1></center>\n";

    isWell = true;
    setVisible(false);
   }
 }

 public boolean isWellDone() {

   return isWell;
  }

 public String getHeader() {

   return headerStr;
  }

 public String getTheTitle() {

   return strTitle;
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

 /**
  * METODO getTime
  * Retorna la hora
  */
 public String[] getTime() {

   Calendar today = Calendar.getInstance();
   String[] val = new String[5];
   int dayInt = today.get(Calendar.DAY_OF_MONTH);
   int monthInt = today.get(Calendar.MONTH) + 1;
   int minuteInt = today.get(Calendar.MINUTE);
   String day = "";
   String zero = "";
   String min = "";

   if(dayInt < 10)
       day = "0";
   if(monthInt < 10)
       zero = "0";
   if(minuteInt < 10)
       min = "0";

   val[0] = day + today.get(Calendar.DAY_OF_MONTH);
   val[1] = zero + monthInt;
   val[2] = "" + today.get(Calendar.YEAR);
   val[3] = "" + today.get(Calendar.HOUR_OF_DAY);
   val[4] = min + today.get(Calendar.MINUTE);
   return val;
 }

 public String getFormatZero(String[] val) {

   String date = val[3] + ":" + val[4] + " - " + val[0] + "/" + val[1] + "/" + val[2];
   return date;
 }

 public String getFormatOne(String[] val) {

   String date = val[0] + "/" + val[1] + "/" + val[2];
   return date;
 }

 public String getFormatTwo(String[] val) {

   String date = val[1] + "/" + val[0] + "/" + val[2];
   return date;
 }

 public String getFormatThree(String[] val) {

   String months[] = {Language.getWord("JANUARY"),Language.getWord("FEBRUARY"),Language.getWord("MARCH"),Language.getWord("APRIL"),Language.getWord("MAY"),Language.getWord("JUNE"),Language.getWord("JULY"),Language.getWord("AUGUST"),Language.getWord("SEPTEMBER"),Language.getWord("OCTOBER"),Language.getWord("NOVEMBER"),Language.getWord("DECEMBER")};

   int month = Integer.parseInt(val[1]) - 1;
   String date = months[month] + " " + val[0] + " of " + val[2];
   return date;
 }

} //Fin de la Clase
