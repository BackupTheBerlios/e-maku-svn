/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ReportAppearance v 0.1
* Esta clase se encarga de mostrar la ventana donde el usuario      
* define la apariencia personalizada de su reporte.                
*                                                                 
* Los objetos de este tipo se crean desde la clase ReportDesigner      
*                                                               
* Fecha: 2001/07/31                                            
* Autores: Beatriz Florián  - bettyflor@kazak.ws             
*          Gustavo Gonzalez - xtingray@kazak.ws
*
*/
package ws.kazak.xpg.report;

import ws.kazak.xpg.idiom.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class ReportAppearance extends JDialog implements ActionListener { 

 JFrame frame;
 String headerStruct = "<center><hr width=100% size=1></center>\n";
 String footerStruct = "<center><hr width=100% size=1></center>\n";
 JPanel bGColorP;
 JPanel pColor;
 JPanel bColorP;
 JPanel fCColor;
 JPanel bCColor;
 JTextField cp;
 JTextField sp;
 JTextField widthTextField;
 JComboBox borderTableCombo;
 JComboBox fontStyle;
 JComboBox fontSize;
 JComboBox fontStyle2;
 JComboBox fontSize2;
 boolean wellDone = false;

 String bGeneral = "#FFFFFF";
 String headerTableBackground = "#FFFFFF";
 String headerTableFontColor  = "#000000";
 String cellTableBackground   = "#FFFFFF";
 String cellTableFontColor    = "#000000";
 String reportTitle = "";

 String headerFontSize  = "8";
 String headerFontStyle = "arial";
 String headerFontColor = "#000000";

 String footerFontSize  = "8";
 String footerFontStyle = "arial";
 String footerFontColor = "#000000";

 HtmlProperties htmlInfo;

 int borderTable = 0;
 String RGB[] = new String[256];

 public ReportAppearance(JDialog extern, JFrame aframe) {

  super(extern,true);
  String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
  int k=0;                        

  for (int i = 0; i < 16; i++) {

       for (int j = 0; j < 16; j++) {

            RGB[k] = hex[i] + hex[j];
            k++;
        }
   }

  frame = aframe;
  setTitle(Language.getWord("REPAPP"));

  Border etched1 = BorderFactory.createEtchedBorder();
  TitledBorder title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("GENSETT"));
  getContentPane().setLayout(new BorderLayout());

  JButton butH = new JButton(Language.getWord("HEADER"));
  butH.setActionCommand("HEADER");
  butH.addActionListener(this);

  JButton butF = new JButton(Language.getWord("FOOTER")); 
  butF.setActionCommand("FOOTER");
  butF.addActionListener(this);

  JButton background = new JButton(Language.getWord("BACKCOLOR"));
  background.setActionCommand("BGCOLOR");
  background.addActionListener(this);

  TitledBorder title2 = BorderFactory.createTitledBorder(etched1);
  bGColorP = new JPanel();
  bGColorP.setPreferredSize(new Dimension(15,15));
  bGColorP.setBackground(Color.white);
  bGColorP.setBorder(title2);

  JPanel botons = new JPanel();
  botons.setLayout(new FlowLayout(FlowLayout.CENTER));
  botons.add(butH);
  botons.add(butF);
  botons.add(new JPanel());
  botons.add(background);
  botons.add(bGColorP);

  botons.setBorder(title1);

  JPanel table = new JPanel();
  table.setLayout(new BoxLayout(table,BoxLayout.Y_AXIS));
  title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("DATSETT"));
  table.setBorder(title1);

  JPanel struct = new JPanel();
  struct.setLayout(new FlowLayout(FlowLayout.CENTER));

  String[] valuesBorder = {"0","1","2","3","4","5"};
  borderTableCombo = new JComboBox(valuesBorder);
  borderTableCombo.setSelectedIndex(1);

  JLabel cellpadding = new JLabel(Language.getWord("CELLPAD")+":");
  cp = new JTextField(2);
  cp.setText("0");

  JLabel cellspacing = new JLabel(" " + Language.getWord("CELLSPA")+":");
  sp = new JTextField(2); 
  sp.setText("0");

  widthTextField = new JTextField(4);
  widthTextField.setText("100%");

  struct.setLayout(new FlowLayout(FlowLayout.CENTER));
  struct.add(new JLabel(Language.getWord("UBR") + ":"));
  struct.add(borderTableCombo);
  struct.add(new JLabel("pt"));
  struct.add(new JPanel());
  struct.add(new JLabel(Language.getWord("W") + ":"));
  struct.add(widthTextField);

  JPanel padding = new JPanel();
  padding.setLayout(new FlowLayout(FlowLayout.CENTER));
  padding.add(cellpadding);
  padding.add(cp);
  padding.add(cellspacing);
  padding.add(sp); 

  title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("TABDIM"));

  JPanel gStruct = new JPanel();
  gStruct.setLayout(new FlowLayout(FlowLayout.CENTER));
  gStruct.add(struct);
  gStruct.add(padding);
  gStruct.setBorder(title1);

  JPanel tableHeader = new JPanel();
  tableHeader.setLayout(new BorderLayout());

  title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("TABLEH"));
  tableHeader.setBorder(title1);

  JPanel fontPanel = new JPanel();
  fontPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
 
  String[] values = {"Arial","Arial Black","Arial Narrow","Book Antiqua","Bookman Old Style","Calixto MT","Century Gothic","Comic Sans MS","Copperplate Gothic Bold","Copperplate Gothic Light","Courier New","Garamond","Helvetica","Impact","Lucida Console","Lucida Handwriting","Lucida Sans","Lucida Sans Unicode","Map Symbols","Marlett","Matisse ITC","Monotype Sorts","MS Outlook","MT Extra","News Gothic MT","OCR A Extended","Symbol","Tahoma","Tempus Sans ITC","Times New Roman","Verdana","Webdings","Westminster","Wingdings"};
  JLabel style = new JLabel(Language.getWord("STYLE")+":");
  fontStyle = new JComboBox(values);
  JLabel size = new JLabel(" "+Language.getWord("LONGTYPE")+":");
  String[] values1 = {"8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
  fontSize = new JComboBox(values1);
  fontSize.setSelectedIndex(4);

  JButton fontColor = new JButton(Language.getWord("FCOLOR"));
  fontColor.setActionCommand("FCOLOR");
  fontColor.addActionListener(this);

  pColor = new JPanel();
  pColor.setPreferredSize(new Dimension(15,15));
  pColor.setBackground(Color.black);
  title1 = BorderFactory.createTitledBorder(etched1);
  pColor.setBorder(title1);
  title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("FSETT"));
  fontPanel.setBorder(title1);
 
  fontPanel.add(style);
  fontPanel.add(fontStyle);
  fontPanel.add(new JPanel());
  fontPanel.add(size);
  fontPanel.add(fontSize);
  fontPanel.add(new JLabel("pt"));
  fontPanel.add(new JPanel());
  fontPanel.add(fontColor);
  fontPanel.add(pColor);

  JPanel bc = new JPanel();
  bc.setLayout(new FlowLayout(FlowLayout.CENTER));
  JButton bcolor = new JButton(Language.getWord("BACKCOLOR")); 
  bcolor.setActionCommand("BCOLOR");
  bcolor.addActionListener(this);
  bColorP = new JPanel();
  bColorP.setPreferredSize(new Dimension(15,15));
  bColorP.setBackground(Color.white);
  title1 = BorderFactory.createTitledBorder(etched1);
  bColorP.setBorder(title1);

  bc.add(bcolor,BorderLayout.CENTER);
  bc.add(bColorP,BorderLayout.EAST);

  tableHeader.add(fontPanel,BorderLayout.CENTER);
  tableHeader.add(bc,BorderLayout.SOUTH);

  JPanel cells = new JPanel();
  cells.setLayout(new BorderLayout());
  title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("CELLS"));
  cells.setBorder(title1);

  fontPanel = new JPanel();
  fontPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
 
  style = new JLabel(Language.getWord("STYLE")+":");
  fontStyle2 = new JComboBox(values);
  size = new JLabel(" "+Language.getWord("LONGTYPE")+":");
  fontSize2 = new JComboBox(values1);
  fontSize2.setSelectedIndex(4);

  fontColor = new JButton(Language.getWord("FCOLOR"));
  fontColor.setActionCommand("CFCOLOR");
  fontColor.addActionListener(this);

  fCColor = new JPanel();
  fCColor.setPreferredSize(new Dimension(15,15));
  fCColor.setBackground(Color.black);
  title1 = BorderFactory.createTitledBorder(etched1);
  fCColor.setBorder(title1);

  title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("FSETT"));
  fontPanel.setBorder(title1);
 
  fontPanel.add(style);
  fontPanel.add(fontStyle2);
  fontPanel.add(new JPanel());
  fontPanel.add(size);
  fontPanel.add(fontSize2);
  fontPanel.add(new JLabel("pt"));
  fontPanel.add(new JPanel());
  fontPanel.add(fontColor);
  fontPanel.add(fCColor);

  cells.add(fontPanel,BorderLayout.NORTH);

  JPanel bx = new JPanel();
  bx.setLayout(new FlowLayout(FlowLayout.CENTER));

  JButton colB = new JButton(Language.getWord("BACKCOLOR"));
  colB.setActionCommand("BCCOLOR");
  colB.addActionListener(this);

  bCColor = new JPanel();
  bCColor.setPreferredSize(new Dimension(15,15));
  bCColor.setBackground(Color.white);

  title1 = BorderFactory.createTitledBorder(etched1);
  bCColor.setBorder(title1);

  bx.add(colB);
  bx.add(bCColor);

  cells.add(fontPanel,BorderLayout.NORTH);
  cells.add(bx,BorderLayout.CENTER);

  table.add(botons);
  table.add(gStruct);
  table.add(tableHeader);
  table.add(cells);

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

  getContentPane().add(botons,BorderLayout.NORTH);
  getContentPane().add(table,BorderLayout.CENTER);
  getContentPane().add(botonD,BorderLayout.SOUTH);
  pack();
  setLocationRelativeTo(frame);
  setVisible(true);
 }

 public void actionPerformed(java.awt.event.ActionEvent e) {

  if (e.getActionCommand().equals("BCCOLOR")) {

      Color newColor = JColorChooser.showDialog(ReportAppearance.this,Language.getWord("CCBC"),Color.white);

      if (newColor != null) {
          bCColor.setBackground(newColor);
          cellTableBackground = setColor(newColor.getRed(),newColor.getGreen(),newColor.getBlue());
       }

      return;
   }

  if (e.getActionCommand().equals("CFCOLOR")) {

      Color newColor = JColorChooser.showDialog(ReportAppearance.this,Language.getWord("CCTC"),Color.white);

      if (newColor != null) {
          fCColor.setBackground(newColor);
          cellTableFontColor = setColor(newColor.getRed(),newColor.getGreen(),newColor.getBlue());
      }

      return;
   } 

  if (e.getActionCommand().equals("BCOLOR")) {

      Color newColor = JColorChooser.showDialog(ReportAppearance.this,
                                                Language.getWord("CBC"),
                                                Color.white);
      if (newColor != null) {
          bColorP.setBackground(newColor);
          headerTableBackground = setColor(newColor.getRed(),newColor.getGreen(),newColor.getBlue());
      }

      return;
   } 

  if (e.getActionCommand().equals("BGCOLOR")) {

      Color newColor = JColorChooser.showDialog(ReportAppearance.this,
                                                Language.getWord("CBC"),
                                                Color.white);
      if (newColor != null) {
          bGColorP.setBackground(newColor);
          bGeneral = setColor(newColor.getRed(),newColor.getGreen(),newColor.getBlue());
      }

      return;
   }

  if (e.getActionCommand().equals("FCOLOR")) {

      Color newColor = JColorChooser.showDialog(ReportAppearance.this,
                                                Language.getWord("CTC"),
                                                Color.white);
      if (newColor != null) {

          pColor.setBackground(newColor);
          headerTableFontColor = setColor(newColor.getRed(),newColor.getGreen(),newColor.getBlue());
      }
  
      return;
   }

  if (e.getActionCommand().equals("HEADER")) {

      ReportHeader ht = new ReportHeader(ReportAppearance.this,frame);

      if (ht.isWellDone()) {
          headerStruct = ht.getHeader();
          reportTitle = ht.getTheTitle();
          headerFontStyle = ht.getFontStyle();
          headerFontSize = ht.getFontSize();
          headerFontColor = ht.getFontColor();
       }

      return;
   }

  if (e.getActionCommand().equals("FOOTER")) {

      ReportFooter ft = new ReportFooter(ReportAppearance.this,frame);

      if (ft.isWellDone()) {
          footerStruct = ft.getFooter(); 
          footerFontStyle = ft.getFontStyle();
          footerFontSize  = ft.getFontSize();
          footerFontColor = ft.getFontColor();
       }

      return;
   }

  if (e.getActionCommand().equals("OK")) {

      borderTable = Integer.parseInt((String) borderTableCombo.getSelectedItem());

      String cellPadding = cp.getText();

      if (cellPadding.length()>0) {

          if (!isNum(cellPadding)) {
                 JOptionPane.showMessageDialog(ReportAppearance.this,
                                               Language.getWord("CPDNG"),
                                               Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                 return;
               }
       }
      else {
             JOptionPane.showMessageDialog(ReportAppearance.this,
                                           "CellPadding is empty",
                                           Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
             return;
       }
     
      String spacePadding = sp.getText();

      if (spacePadding.length()>0) {

         if (!isNum(spacePadding)) {
             JOptionPane.showMessageDialog(ReportAppearance.this,
                                           Language.getWord("CSCNG"),
                                           Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
             return;
          }
       }
      else {
             JOptionPane.showMessageDialog(ReportAppearance.this,
                                           "spacePadding is empty",
                                           Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
             return;
       }

      String fontStyleTH = (String) fontStyle.getSelectedItem();
      String fontSizeTH = (String) fontSize.getSelectedItem();

      String fontStyleTC = (String) fontStyle2.getSelectedItem();
      String fontSizeTC = (String) fontSize2.getSelectedItem();

      int thsf = Integer.parseInt(fontSizeTH);
      int tcsf = Integer.parseInt(fontSizeTC);

      int cellP = 0;

      if (cellPadding.length() > 0)
          cellP = Integer.parseInt(cellPadding);

      int cellSP = 0; 

      if (spacePadding.length() > 0)
          cellSP = Integer.parseInt(spacePadding);

      htmlInfo = new HtmlProperties(bGeneral,cellTableFontColor,headerTableBackground,headerTableFontColor,cellTableBackground,headerStruct,footerStruct);
      htmlInfo.setSettings(thsf,tcsf,fontStyleTH.toLowerCase(),fontStyleTC.toLowerCase(),borderTable,cellP,cellSP,widthTextField.getText());
      htmlInfo.setHeaderSettings(reportTitle,headerFontStyle,headerFontSize,headerFontColor);
      htmlInfo.setFooterSettings(footerFontStyle,footerFontSize,footerFontColor);

      wellDone = true;
      setVisible(false);
   }  

  if (e.getActionCommand().equals("CANCEL")) {
      setVisible(false);
   }
 }

 public String setColor(int red,int green,int blue) {
   return "#" + RGB[red] + RGB[green] + RGB[blue];
  }

 public boolean isNum(String s) {

    for(int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if(!Character.isDigit(c))
          return false;
     }

    return true;
  }

 public boolean isWellDone() {
    return wellDone;
  }

 public HtmlProperties getHtmlProperties() {
    return htmlInfo; 
  } 

} //Fin de la Clase
