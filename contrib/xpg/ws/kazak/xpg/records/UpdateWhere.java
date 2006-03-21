/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS UpdateWhere v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el dialogo a traves del
* cual se define la condicion "Where" para una actualizacion.
*
* Esta clase es instanciada desde la clase UpdateTable.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*          Angela Sandobal  - angesand@libertad.univalle.edu.co     
*/
package ws.kazak.xpg.records;

import ws.kazak.xpg.idiom.*;
import ws.kazak.xpg.db.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

public class UpdateWhere extends JDialog implements ActionListener{

 Table myTable;
 JTextField areaL,area2L;
 JComboBox combo,combo2,postSEL;

 JButton ok;
 JButton clear;

 Hashtable hashOp = new Hashtable();
 Hashtable hashText = new Hashtable();
 Hashtable hashBol = new Hashtable();

 boolean[] active;
 String[] fieldName;
 int numFields;
 int activeFields = 0;
 String where = "";

 public UpdateWhere(Table table,JDialog parent) {

   super(parent, true);
   setTitle(Language.getWord("CCOND"));
   myTable = table;

   JPanel global = new JPanel();
   global.setLayout(new BoxLayout(global,BoxLayout.Y_AXIS));

   JPanel base = new JPanel();
   base.setLayout(new GridLayout(0,1));
   JPanel data = new JPanel();
   data.setLayout(new GridLayout(0,1));
   JPanel union = new JPanel();
   union.setLayout(new GridLayout(0,1));
   JPanel operator = new JPanel();
   operator.setLayout(new GridLayout(0,1));

   numFields = myTable.getTableHeader().getNumFields();
   active = new boolean[numFields + 1];

   Hashtable hashFields = myTable.getTableHeader().getHashtable();
   String[] ANDOR = {"AND","OR"}; 
   String[] ops = {"=","!=","<",">","<=",">=","like","not like","~","~*","!~","!~*"};
   String[] boolOps = {"=","!="};
   String[] intOps = {"=","!=","<",">","<=",">="};

   fieldName = new String[numFields + 1];
   Vector fields = myTable.getTableHeader().getNameFields();
   int i=0;

   active[0] = false;
   JCheckBox check = new JCheckBox("oid [ int ] ");
   check.setForeground(Color.red);
   String label = "check-" + i;
   check.setActionCommand(label);
   check.addActionListener(this);

   fieldName[i] = "oid";
   JComboBox condition = new JComboBox(intOps);
   JTextField area = new JTextField(10);
   hashText.put(label,area);
   data.add(area);

   condition.setEnabled(false);
   area.setEditable(false);
   hashOp.put(label,condition);
   operator.add(condition);
   base.add(check);
 
   JComboBox logical = new JComboBox(ANDOR);
   logical.setEnabled(false);
   hashBol.put("" + i,logical);
   union.add(logical);

   i++;

   for (; i<numFields+1 ;i++) {

     active[i] = false;
     String nfield = (String) fields.elementAt(i-1);

     String typeField = myTable.getTableHeader().getType(nfield);

     area = new JTextField(10);
     logical = new JComboBox(ANDOR);

     check = new JCheckBox(nfield + " [" + typeField + "] ");
     label = "check-" + i;
     check.setActionCommand(label);
     check.addActionListener(this);
     fieldName[i] = nfield;

     if (typeField.equals("bool"))
         condition = new JComboBox(boolOps);
     else {
            if (typeField.startsWith("varchar") || typeField.startsWith("text"))
                condition = new JComboBox(ops);
            else
                condition = new JComboBox(intOps);
      }

     condition.setEnabled(false);
     area.setEditable(false);
     hashOp.put(label,condition);
     operator.add(condition);
     base.add(check);

     String boolArray[] = {"true","false"};
     JComboBox booleanCombo = new JComboBox(boolArray);
     booleanCombo.setEnabled(false);

     if (typeField.equals("bool")) {
         hashText.put(label,booleanCombo);
         data.add(booleanCombo);
      }
     else {
            hashText.put(label,area);
            data.add(area);
      }

     if (i<numFields) {
         logical.setEnabled(false);
         hashBol.put("" + i,logical);
         union.add(logical);
      } // fin if
     else {
           JLabel field = new JLabel(" ");
           union.add(field);
      } // fin else

    } // fin for

   JPanel center = new JPanel();
   center.setLayout(new BorderLayout());
   center.add(base,BorderLayout.WEST);
   center.add(operator,BorderLayout.CENTER);
   center.add(data,BorderLayout.EAST);

   JPanel up = new JPanel();
   up.setLayout(new FlowLayout(FlowLayout.CENTER));
   up.add(center);
   up.add(union);

   Border etched1 = BorderFactory.createEtchedBorder();
   TitledBorder title1 = BorderFactory.createTitledBorder(etched1,"WHERE");
   up.setBorder(title1);

   ok = new JButton(Language.getWord("OK"));
   ok.setActionCommand("OK");
   ok.addActionListener(this);
   ok.setEnabled(false);

   clear = new JButton(Language.getWord("CLR"));
   clear.setActionCommand("CLEAR");
   clear.addActionListener(this);
   clear.setEnabled(false);

   JButton cancel = new JButton(Language.getWord("CANCEL"));
   cancel.setActionCommand("CANCEL");
   cancel.addActionListener(this);

   JPanel botons = new JPanel();
   botons.setLayout(new FlowLayout(FlowLayout.CENTER));
   botons.add(ok);
   botons.add(clear);
   botons.add(cancel);

   if (numFields > 15) {
       JScrollPane scroll = new JScrollPane(up);
       scroll.setPreferredSize(new Dimension(400,400));
       global.add(scroll);
    }
   else
       global.add(up);

   global.add(botons);

   getContentPane().add(global);
   pack();
   setLocation(parent.getX() + 50,parent.getY() + 50);
   setVisible(true);
 }

 public void actionPerformed(java.awt.event.ActionEvent e) {

  if (e.getActionCommand().equals("CLEAR")) {

      for (Enumeration t = hashText.elements() ; t.hasMoreElements() ;) {
           Object obj = (Object) t.nextElement();

           if (obj instanceof JTextField) {
               JTextField tmp = (JTextField) obj;
               tmp.setText("");
            }
           else {
                 if (obj instanceof JComboBox) {
                     JComboBox tmp = (JComboBox) obj; 
                     tmp.setSelectedIndex(0);
                  }
           }
      }
    
     return;
   }


  if (e.getActionCommand().equals("OK")) {

     boolean fail = false;
     String condition = "";
     int t=0;

     for (int k=0;k<numFields+1;k++) {

          if (active[k]) {

              t++;

              boolean isText = false;
              String fieldValue;

              Object obj = (Object) hashText.get("check-" + k);

              if (obj instanceof JTextField) {
                  JTextField tmpTextField = (JTextField) obj;
                  fieldValue = tmpTextField.getText();
                  isText = true;
               }
              else {
                    JComboBox bool = (JComboBox) obj;
                    fieldValue = (String) bool.getSelectedItem();
               }

              if (!(fieldValue.length()>0)) {

                  fail = true;
                  JOptionPane.showMessageDialog(UpdateWhere.this,Language.getWord("EFIW") + fieldName[k] + "'.",
                                                Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                  break;
               } //fin if

              JComboBox tmp = (JComboBox) hashOp.get("check-" + k);
              JComboBox tmpCom = (JComboBox) hashBol.get("" + k);
              String val = ""; 

              if (k != numFields && tmpCom.isEnabled()) {

                  val = (String) tmpCom.getSelectedItem();
                  val += " ";
	       } // fin if 

              String op = (String) tmp.getSelectedItem();
              String type = "int";

              if (k != 0)
                  type = myTable.getTableHeader().getType(fieldName[k]);

              if (!validNumberFormat(type,fieldValue)) {

                  JOptionPane.showMessageDialog(UpdateWhere.this,Language.getWord("TNE1") + fieldName[k]
                                                + Language.getWord("TNE2"),
                                                Language.getWord("ERROR!"),
                                                JOptionPane.ERROR_MESSAGE);
                  return;
               }

              if (type.startsWith("varchar"))
                  fieldValue = "'" + fieldValue + "'";         

              condition += "\"" + fieldName[k] + "\" " + op + " " + fieldValue + " " + val;

       } // fin if
     } // fin for

    if (!fail) {

        if (t==0)
            where = "";
        else 
            where = condition;

        setVisible(false);

     } // fin if
  }


  if (e.getActionCommand().equals("CANCEL")) {
      setVisible(false);
   }

  if (e.getActionCommand().startsWith("check-")) {

      String cad = e.getActionCommand();
      int num = Integer.parseInt(cad.substring(cad.indexOf("-")+1,cad.length()));
      JCheckBox checktmp = (JCheckBox) e.getSource();
      JComboBox tmpOp = (JComboBox) hashOp.get(cad);

      boolean isText = false;
      JTextField tmp = new JTextField();
      JComboBox bool = new JComboBox();

      Object obj = (Object) hashText.get(cad);

      if (obj instanceof JTextField) {
          tmp = (JTextField) obj;
          isText = true;
       }
      else {
             bool = (JComboBox) obj;
       }

      if (checktmp.isSelected()) {

          if (!clear.isEnabled()) {
              clear.setEnabled(true);
              ok.setEnabled(true);
           }

          activeFields++;

          active[num] = true;

          for (int k=num+1;k<numFields+1;k++) {

               if (active[k]) {
                   JComboBox tmpCom = (JComboBox) hashBol.get("" + num);
                   tmpCom.setEnabled(true);
                   break;
                }
           } 

          tmpOp.setEnabled(true);

          if (isText) {
              tmp.setEnabled(true);
              tmp.setEditable(true);
              tmp.requestFocus();
           } 
          else {
                 bool.setEnabled(true);
           }

          if (num>0) {

              if (active[num - 1]) {
                  JComboBox tmpCom = (JComboBox) hashBol.get("" + (num-1));
                  tmpCom.setEnabled(true);
               } // fin if
              else {

                    if ((num-2) >= 0)
                        for (int j=num-2; j>=0; j--)
                             if (active[j]) {
                                 JComboBox tmpCom = (JComboBox) hashBol.get("" + j);
                                 tmpCom.setEnabled(true);
                                 break;
                              } // fin if

               } // fin else  
     }
   }
  else {

    tmpOp.setSelectedIndex(0);
    tmpOp.setEnabled(false);

    if (isText) {
        tmp.setText("");
        tmp.setEditable(false);
        tmp.setEnabled(false);
    }
    else {
          bool.setSelectedIndex(0);
          bool.setEnabled(false);
    }

    active[num] = false;

    activeFields--;

    if (activeFields == 0) {
        if (clear.isEnabled()) {
            clear.setEnabled(false);
            ok.setEnabled(false);
         }
     }

    if (num != numFields) {

        JComboBox combo = (JComboBox) hashBol.get("" + num);
        if (combo.isEnabled()) {
            combo.setSelectedIndex(0);
            combo.setEnabled(false);
        }
     }

    boolean deal = false;

    for (int m=num+1;m<numFields+1;m++) {

         if (active[m]) {
             deal = true;
             break;     
          }
     }

    if (!deal) {

        for(int j=num-1; j>=0; j--)
            if (active[j]) {
                  JComboBox tmpCom = (JComboBox) hashBol.get("" + j);
                  tmpCom.setSelectedIndex(0);
                  tmpCom.setEnabled(false);
                  break;
             } // fin if
     } //fin if
      
    } //fin else
   } //fin if

 } // fin del Metodo

public boolean validNumberFormat(String TypeField,String value) {

   boolean valid = true;

   if (TypeField.equals("decimal") || TypeField.startsWith("float")
        || TypeField.startsWith("int") || TypeField.equals("numeric") || TypeField.equals("serial")) {

        for (int i=0;i<value.length();i++) {
             char a = value.charAt(i);
             if (!Character.isDigit(a) && a != '.') {
                 valid = false;
                 break;
              }
         }
   }
   return valid;
 }

} //Fin de Clase
