/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS DropTableRecord v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el dialogo mediante el
* cual se elimina un registro en una tabla. 
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.records;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.db.Table;
import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.misc.input.GenericQuestionDialog;

public class DropTableRecord extends JDialog implements ActionListener {

 Table myTable;
 Hashtable hashOp = new Hashtable();
 Hashtable hashText = new Hashtable();
 Hashtable hashBol = new Hashtable();
 boolean[] active;
 String[] fieldName;
 int numFields;
 boolean wellDone=false;
 String delete;
 JFrame myFrame;
 String tableName;

 public DropTableRecord(String realtableName,Table table,JFrame frame) { 

  super(frame, true);
  tableName = realtableName;
  setTitle(Language.getWord("DELREC"));
  myTable = table;
  myFrame = frame;
  numFields = myTable.getTableHeader().getNumFields();
  active = new boolean[numFields + 1];

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

  int i=0;

  Vector fields = myTable.getTableHeader().getNameFields();
  fieldName = new String[numFields + 1];

  String[] ANDOR = {"AND","OR"}; 
  String[] ops = {"=","!=","<",">","<=",">=","like","not like","~","~*","!~","!~*"}; 
  String[] boolOps = {"=","!="};
  String[] intOps = {"=","!=","<",">","<=",">="};

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

  for (; i<numFields + 1 ;i++) {

       active[i] = false;
       String nfield = (String) fields.elementAt(i - 1);
       String typeField = myTable.getTableHeader().getType(nfield);

       area = new JTextField(10);
       area.setEditable(false);

       logical = new JComboBox(ANDOR);

       check = new JCheckBox (nfield + " [" + typeField + "] ");
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
        }
       else {
             JLabel field = new JLabel(" ");
             union.add(field);
        }

   } // fin for

 JLabel title = new JLabel("DELETE FROM " + tableName,JLabel.CENTER);
 JPanel first = new JPanel();
 first.setLayout(new FlowLayout(FlowLayout.CENTER));
 first.add(title);

 Border etched1 = BorderFactory.createEtchedBorder();
 TitledBorder title1 = BorderFactory.createTitledBorder(etched1);
 first.setBorder(title1);

 JPanel center = new JPanel();
 center.setLayout(new BorderLayout());
 center.add(base,BorderLayout.WEST);
 center.add(operator,BorderLayout.CENTER);
 center.add(data,BorderLayout.EAST);

 JPanel up = new JPanel();
 up.setLayout(new FlowLayout(FlowLayout.CENTER));
 up.add(center);
 up.add(union);
 title1 = BorderFactory.createTitledBorder(etched1,"WHERE");
 up.setBorder(title1);

 JButton ok = new JButton(Language.getWord("DEL"));
 ok.setActionCommand("OK");
 ok.addActionListener(this);
 JButton cancel = new JButton(Language.getWord("CANCEL"));
 cancel.setActionCommand("CANCEL");
 cancel.addActionListener(this);

 JPanel buttons = new JPanel();
 buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
 buttons.add(ok);
 buttons.add(cancel);

 global.add(first);

 if (numFields > 15) {

     JScrollPane scroll = new JScrollPane(up);
     scroll.setPreferredSize(new Dimension(400,400));
     global.add(scroll);
  }
 else
     global.add(up);

 global.add(buttons);

 getContentPane().add(global);
 pack();
					
}

 /*** Manejo de eventos ***/
 public void actionPerformed(java.awt.event.ActionEvent e) {

 if (e.getActionCommand().equals("OK")) {

     delete = "DELETE FROM " + tableName;
     String condition = " WHERE ";
     int t=0;

     for (int k=0;k<numFields+1;k++) {

          if (active[k]) {

              t++;
              boolean isText = false;
              JTextField tmpJTF = new JTextField();
              JComboBox bool = new JComboBox();
              String dataValue;

              Object obj = (Object) hashText.get("check-" + k);

              if (obj instanceof JTextField) {
                  tmpJTF = (JTextField) obj;
                  dataValue = tmpJTF.getText(); 
                  isText = true;
               }
              else {
                     bool = (JComboBox) obj;
                     dataValue = (String) bool.getSelectedItem();
               }

              if (!(dataValue.length()>0)) {

                  JOptionPane.showMessageDialog(DropTableRecord.this,Language.getWord("EFIW") + fieldName[k] + "'.",
                                                Language.getWord("ERROR!"),
                                                JOptionPane.ERROR_MESSAGE);
                  return;
               }

              JComboBox tmp = (JComboBox) hashOp.get("check-" + k);
              JComboBox tmpCom = (JComboBox) hashBol.get("" + k);
              String val = ""; 

              if (k != numFields && tmpCom.isEnabled()) {
                  val = (String) tmpCom.getSelectedItem();
                  val += " ";
               }        

              String op = (String) tmp.getSelectedItem();
              String type = "int";

              if (k != 0)
                  type = myTable.getTableHeader().getType(fieldName[k]);

              
              if (!validNumberFormat(type,dataValue)) {
                  JOptionPane.showMessageDialog(DropTableRecord.this,Language.getWord("TNE1") + fieldName[k] + Language.getWord("TNE2"),
                                                Language.getWord("ERROR!"),
                                                JOptionPane.ERROR_MESSAGE);
                  return;
               }

              if ((type.startsWith("varchar") || type.startsWith("char") || type.startsWith("name") 
                  || type.startsWith("time") || type.startsWith("text") || type.startsWith("date"))
                  && !dataValue.startsWith("'"))

                  dataValue = "'" + dataValue + "'";         

              condition += "\"" + fieldName[k] + "\" " + op + " " + dataValue + " " + val;

       } // fin if
    } // fin for

    boolean ignore = false;

    if (t==0) {
        condition = "";
        String mesg = Language.getWord("E1") + tableName + Language.getWord("E2");
        GenericQuestionDialog askhim = new GenericQuestionDialog(myFrame,Language.getWord("YES"),Language.getWord("NO"),
                                               Language.getWord("ADV"),mesg);

        if (!askhim.getSelecction())
            ignore = true;
     }

    if (!ignore) {
        delete += condition + ";";
        wellDone = true;
        setVisible(false);
     }                
  }

if (e.getActionCommand().equals("CANCEL")) {
    setVisible(false);
 }

if (e.getActionCommand().startsWith("check-")) {

    String cad = e.getActionCommand();
    int num = Integer.parseInt(cad.substring(cad.indexOf("-")+1,cad.length()));
    JCheckBox checktmp = (JCheckBox) e.getSource();
    JComboBox tmp = (JComboBox) hashOp.get(cad);

    boolean isText = false;
    JTextField tmpJTF = new JTextField();
    JComboBox bool = new JComboBox();

    Object obj = (Object) hashText.get(cad);

    if (obj instanceof JTextField) {
        tmpJTF = (JTextField) obj;
        isText = true;
     }
    else {
          bool = (JComboBox) obj;
     }

    if (checktmp.isSelected()) {

        active[num] = true;

        for (int k=num+1;k<numFields+1;k++) {

             if (active[k]) {

                 JComboBox tmpCom = (JComboBox) hashBol.get("" + num);
                 tmpCom.setEnabled(true);
                 break;
              }
    } 

    tmp.setEnabled(true);

    if (isText) {
        tmpJTF.setEnabled(true);
        tmpJTF.setEditable(true);
        tmpJTF.requestFocus();
     }
    else {
           bool.setEnabled(true);
    }

    if (num>0) {
        if(active[num - 1]) {
           JComboBox tmpCom = (JComboBox) hashBol.get("" + (num-1));
           tmpCom.setEnabled(true);
         }
        else {
              if((num-2) >= 0)

                 for (int j=num-2; j>=0; j--) {
                       if (active[j]) {
                           JComboBox tmpCom = (JComboBox) hashBol.get("" + j);
                           tmpCom.setEnabled(true);
                           break;
                        }
                  }
         } 
     }
   }
  else {
         tmp.setSelectedIndex(0);
         tmp.setEnabled(false);

         if (isText) {
             tmpJTF.setText("");
             tmpJTF.setEditable(false);
             tmpJTF.setEnabled(false);
          }
         else {
               bool.setSelectedIndex(0);
               bool.setEnabled(false);
          }

         active[num] = false;

         if (num != (numFields)) {

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

              for (int j=num-1; j>=0; j--) {

                   if (active[j]) {
                        JComboBox tmpCom = (JComboBox) hashBol.get("" + j);
                        tmpCom.setSelectedIndex(0);
                        tmpCom.setEnabled(false);
                        break;
                    } // fin if
               } // fin for
           } // fin if
      }
}


    } // fin del metodo

 public boolean isWellDone() {
    return wellDone;
  }

 public String getSQL() {
    return delete;
  }

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

} // Fin de la Clase
