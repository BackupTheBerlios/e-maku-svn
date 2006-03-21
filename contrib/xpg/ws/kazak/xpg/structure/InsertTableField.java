/**
* CLASS InsertTableField v 1.0                                                   
* Esta clase se encarga de añadir un campo dentro
* de la estructura de una tabla                      
*                                                                   
* Fecha: 2001/10/01                                                 
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                              
* Copyright 2001  -  Soluciones KAZAK
*/
package ws.kazak.xpg.structure;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.idiom.Language;

public class InsertTableField extends JDialog implements ActionListener {

 boolean wellDone = false;
 JComboBox TypeField;
 JTextField LongText;
 JTextField DefaultField;
 JTextField NameField;

 JFrame parent;
 String InstructionA = "";
 String InstructionB = "";
 String table;
 String NF = "";
 String TF = "";
 String DV = "";

 public InsertTableField(JFrame frame,String TName) {

  super(frame, true);
  setTitle(Language.getWord("INSRT") + " " + Language.getWord("FIELD"));
  parent = frame;
  table = TName;
  JPanel global = new JPanel();
  global.setLayout(new BoxLayout(global,BoxLayout.Y_AXIS));
  JLabel NameLabel = new JLabel(Language.getWord("NAME"),JLabel.CENTER);
  NameField = new JTextField(15);
  JPanel first = new JPanel();
  first.setLayout(new GridLayout(0,1));
  first.add(NameLabel);
  first.add(NameField);

  JLabel TypeLabel = new JLabel("Tipo",JLabel.CENTER);
  String[] values = {"varchar","char","text","name","int2","int4","int8",
    "decimal","float4","float8","numeric","serial","date",
    "time","timetz","timestamp","interval","bool","point",
    "line","lseg","box","path","polygon","circle","cidr","inet"};

  TypeField = new JComboBox(values);
  TypeField.setBackground(Color.white);
  TypeField.setActionCommand("COMBO");
  TypeField.addActionListener(this);
  JPanel second = new JPanel();
  second.setLayout(new GridLayout(0,1));
  second.add(TypeLabel);
  second.add(TypeField);

  JLabel LongLabel = new JLabel(Language.getWord("LENGHT"),JLabel.CENTER);
  LongText = new JTextField(3);
  JPanel panelL = new JPanel();
  panelL.setLayout(new GridLayout(0,1));
  panelL.add(LongLabel);
  panelL.add(LongText);  

  JLabel DefaultLabel = new JLabel(Language.getWord("DEFVL"),JLabel.CENTER);
  DefaultField = new JTextField(15);
  JPanel four = new JPanel();
  four.setLayout(new GridLayout(0,1));
  four.add(DefaultLabel);
  four.add(DefaultField);

  JPanel OneRow = new JPanel();
  OneRow.setLayout(new FlowLayout(FlowLayout.CENTER));
  OneRow.add(first);
  OneRow.add(second);
  OneRow.add(panelL);
  OneRow.add(four);

  Border etched1 = BorderFactory.createEtchedBorder();
  TitledBorder title1 = BorderFactory.createTitledBorder(etched1);
  OneRow.setBorder(title1);

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

  global.add(OneRow);
  global.add(botons);

  getContentPane().add(global);
  pack();
  setLocationRelativeTo(frame);
  setVisible(true);
 }

 public void actionPerformed(java.awt.event.ActionEvent e) 
  {

  if(e.getActionCommand().equals("OK")) 
   {
    NF = NameField.getText();

    if(NF.length()==0) 
     {
      JOptionPane.showMessageDialog(InsertTableField.this,
      Language.getWord("FNEMPTY"),
      Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
      NameField.requestFocus();
     } 
    else 
     {
      if(NF.indexOf(" ") != -1) 
        {
          JOptionPane.showMessageDialog(InsertTableField.this,
          Language.getWord("NOCHAR"),
          Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
          NameField.requestFocus();
          return;
        } 
       else 
        {
         String longitud = "";
         TF = (String)TypeField.getSelectedItem();
         if (LongText.isEnabled()) 
          {
            longitud = LongText.getText();
            if(longitud.length()>0 && isNum(longitud))
                 TF = TF + "(" + longitud + ")";
            else 
              {
               JOptionPane.showMessageDialog(InsertTableField.this,
               Language.getWord("INVLENGHT"),
               Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
	       LongText.requestFocus();
               return;
              } 
         }

        DV = DefaultField.getText();
         
        boolean good = true;
        if(DV.length()>0) 
         {
          if((TF.startsWith("varchar") || TF.startsWith("char") || TF.startsWith("name") || TF.startsWith("time") 
                                      || TF.startsWith("text") || TF.startsWith("date")) && !DV.startsWith("'")) 
             DV = "'" + DV + "'";
 
          if(validType(TF,DV)) 
             InstructionB = "ALTER TABLE \"" + table + "\" ALTER COLUMN \"" + NF + "\" SET DEFAULT " + DV;
           else 
             {
	      good = false;
              JOptionPane.showMessageDialog(InsertTableField.this,
              Language.getWord("INVDEFAULT"),
              Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
	     }
        }

        if(good) 
         {
           InstructionA = "ALTER TABLE \"" + table + "\" ADD \"" + NF + "\" " + TF;
           wellDone = true;
           setVisible(false);
         }
      }
    } 
  }

  if(e.getActionCommand().equals("CANCEL")) 
   {
     setVisible(false);
   }

  if(e.getActionCommand().equals("COMBO")) 
   {
    JComboBox cb = (JComboBox)e.getSource();
    String newSelection = (String)cb.getSelectedItem();

    if(newSelection.equals("serial")) 
      {
       DefaultField.setEditable(false);
       DefaultField.setEnabled(false);
      } 
    else 
      {
       if(!DefaultField.isEnabled()) 
        {
         DefaultField.setEditable(true);
         DefaultField.setEnabled(true);
        }
      }

    if(newSelection.equals("varchar") || newSelection.equals("char")) 
     {
       LongText.setEditable(true);
       LongText.setEnabled(true); 
     } 
    else 
     {
      if(LongText.isEnabled()) 
         {
           LongText.setEditable(false);
           LongText.setEnabled(false);
         }
     }
   }
 }

 public boolean validType(String TypeField,String value) 
  {
   boolean valid = true;
   if(TypeField.equals("decimal") || TypeField.startsWith("float") || TypeField.startsWith("int")
      || TypeField.equals("numeric") || TypeField.equals("serial"))
   {
     for(int i=0;i<value.length();i++) 
      {
       char a = value.charAt(i);
       if(!Character.isDigit(a) && a != '.') 
        {
          valid = false;
          break;
        }
       }
   }
   return valid;
 }

 public boolean isNum(String value)
  {
     for(int i=0;i<value.length();i++) 
      {
       char a = value.charAt(i);
       if(!Character.isDigit(a)) 
           return false;
      }

     return true;
  }

} //Final de la Clase
