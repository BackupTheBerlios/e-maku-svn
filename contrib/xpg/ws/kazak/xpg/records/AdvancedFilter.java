/*
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK Ltda
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS AdvancedFilterv 0.1
* Descripcion:
* Esta clase se encarga de presentar un dialogo para realizar
* un filtro sobre los registros que se desean visualizar en la
* interfaz grafica de la carpeta "Registros".
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.db.Table;
import ws.kazak.xpg.idiom.Language;

public class AdvancedFilter extends JDialog implements ActionListener {

 JTextField areaL,area2L;
 JRadioButton group,order,limit;
 JComboBox combo,combo2,postSEL;
 JComboBox orderCombo;
 Hashtable hashOp = new Hashtable();
 Hashtable hashText = new Hashtable();
 Hashtable hashBol = new Hashtable();
 boolean[] active;
 String[] fieldName;
 int numFields;
 Table myTable;
 String select = "";
 boolean wellDone = false;
 String orderBy = "";
 boolean thereIsOrder = false;
 Vector fields;

 public AdvancedFilter(Table table,JFrame frame) { 

   super(frame, true);

   setTitle(Language.getWord("ADF"));
   myTable = table;

   JPanel base = new JPanel();
   base.setLayout(new GridLayout(0,1));
   JPanel data = new JPanel();
   data.setLayout(new GridLayout(0,1));
   JPanel union = new JPanel();
   union.setLayout(new GridLayout(0,1));
   JPanel operator = new JPanel();
   operator.setLayout(new GridLayout(0,1));

   numFields = myTable.getTableHeader().getNumFields();
   active = new boolean[numFields];
   Hashtable hashFields = myTable.getTableHeader().getHashtable();
   String[] ANDOR = {"AND","OR"}; 
   String[] ops = {"=","!=","<",">","<=",">=","like","not like","~","~*","!~","!~*"}; 
   fieldName = new String[numFields + 1];
   fields = myTable.getTableHeader().getNameFields();
   int i=0;
   int maxlength = 0;

   for (; i<numFields ;i++) {

        active[i] = false;
        String nfield = (String) fields.elementAt(i);
        JTextField area = new JTextField(10);
        JComboBox logical = new JComboBox(ANDOR);
        String type = myTable.getTableHeader().getType(nfield);

        if (nfield.length()>8) {
            nfield = nfield.substring(0,8) + "...";     
            maxlength = 8;        
         }
        else {
               if (maxlength < nfield.length())
                   maxlength = nfield.length();
         }

        JRadioButton check = new JRadioButton(nfield + " [" + type + "] ");
        String label = "check-" + i;
        check.setActionCommand(label);
        check.addActionListener(this);
        fieldName[i] = nfield;

        JComboBox condition = new JComboBox(ops);
        condition.setEnabled(false);
        area.setEditable(false);
        hashOp.put(label,condition);
        operator.add(condition);
        base.add(check);
        hashText.put(label,area);
        data.add(area);

        if (i<numFields-1) {

            logical.setEnabled(false);
            hashBol.put("" + i,logical);
            union.add(logical);
         }
        else {
              JLabel field = new JLabel(" ");
              union.add(field);
         }
  }

  JPanel center = new JPanel();
  center.setLayout(new BorderLayout());
  center.add(base,BorderLayout.WEST);
  center.add(operator,BorderLayout.CENTER);
  center.add(data,BorderLayout.EAST);

  JPanel left = new JPanel();
  left.setLayout(new GridLayout(0,1));
  JPanel right = new JPanel();
  right.setLayout(new GridLayout(2,2));

  group = new JRadioButton("GROUP BY");
  group.setActionCommand("GROUP");
  group.addActionListener(this);
  left.add(group);

  order = new JRadioButton("ORDER BY");
  order.setActionCommand("ORDER");
  order.addActionListener(this);
  left.add(order);

  String fN[] = new String[numFields + 1];
  fN[0] = "oid";

  for (int j=0;j<numFields;j++)
       fN [j+1] = fieldName[j];

  combo = new JComboBox(fN);
  combo.setEnabled(false);
  right.add(combo);

  right.add(new JPanel());

  combo2 = new JComboBox(fN);
  combo2.setEnabled(false);
  right.add(combo2);

  String opc[] = {"ASC","DESC"};
  orderCombo = new JComboBox(opc);
  orderCombo.setEnabled(false);
  right.add(orderCombo);

  limit = new JRadioButton("LIMIT");
  limit.setActionCommand("LIMIT");
  limit.addActionListener(this);

  areaL = new JTextField(5);
  areaL.setEditable(false);
  area2L = new JTextField(5);
  area2L.setEditable(false);
  JLabel coma = new JLabel(",");
  JPanel areas = new JPanel();
  areas.setLayout(new GridLayout(1,0));
  areas.add(limit);
  areas.add(areaL);
  areas.add(area2L);

  coma = new JLabel(" ");
  JLabel rows = new JLabel("rows",JLabel.CENTER);
  JLabel start = new JLabel("start",JLabel.CENTER);
  JPanel texts = new JPanel();
  texts.setLayout(new GridLayout(1,0));
  texts.add(coma);
  texts.add(rows);
  texts.add(start);

  JPanel todo = new JPanel();
  todo.setLayout(new BorderLayout());
  JButton biton = new JButton("");
  biton.setPreferredSize(new Dimension(150 + maxlength*5,2));
  JPanel bitP = new JPanel();
  bitP.add(biton);
  todo.add(bitP,BorderLayout.NORTH);
  todo.add(areas,BorderLayout.CENTER);
  todo.add(texts,BorderLayout.SOUTH);

  JPanel minidown = new JPanel();
  minidown.setLayout(new BorderLayout());
  minidown.add(left,BorderLayout.CENTER);
  minidown.add(right,BorderLayout.EAST);
  minidown.add(todo,BorderLayout.SOUTH);

  Border etched1 = BorderFactory.createEtchedBorder();
  TitledBorder title1 = BorderFactory.createTitledBorder(etched1);
  minidown.setBorder(title1);

  JPanel down = new JPanel();
  down.setLayout(new FlowLayout(FlowLayout.CENTER));
  down.add(minidown);

  JLabel title = new JLabel("SELECT ",JLabel.CENTER);
  String[] diff = {"ALL","DISTINCT"};
  postSEL = new JComboBox(diff);
  JPanel first = new JPanel();
  first.setLayout(new FlowLayout(FlowLayout.CENTER));
  first.add(title);
  title = new JLabel(" ON " + myTable.Name,JLabel.CENTER); 
  first.add(postSEL);
  first.add(title);
  title1 = BorderFactory.createTitledBorder(etched1);
  first.setBorder(title1);

  JPanel up = new JPanel();
  up.setLayout(new FlowLayout(FlowLayout.CENTER));

  up.add(center);
  up.add(union);

  title1 = BorderFactory.createTitledBorder(etched1,"WHERE");
  up.setBorder(title1);

  JButton ok = new JButton(Language.getWord("RUN"));
  ok.setActionCommand("OK");
  ok.addActionListener(this);
  JButton cancel = new JButton(Language.getWord("CANCEL"));
  cancel.setActionCommand("CANCEL");
  cancel.addActionListener(this);

  JOptionPane optionPane;

  if (numFields > 10) {

      JScrollPane scroll = new JScrollPane(up);
      scroll.setPreferredSize(new Dimension(400,200));
      Object[] array = {first,scroll,down};
      Object[] options = {ok,cancel};
      optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION, null,
                               options, options[0]);
   }
  else {
         Object[] array = {first,up,down};
         Object[] options = {ok,cancel};
         optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION, null,
                                      options, options[0]);
   }

  setContentPane(optionPane);

  pack();
  setLocationRelativeTo(frame);
  setVisible(true);  
 } 

 public void actionPerformed(java.awt.event.ActionEvent e) {

 if (e.getActionCommand().equals("GROUP")) { 

     if (group.isSelected())
         combo.setEnabled(true);
     else {
           combo.setSelectedIndex(0);
           combo.setEnabled(false);
      }
  }

 if (e.getActionCommand().equals("ORDER")) {

     if (order.isSelected()) {

         combo2.setEnabled(true);
         orderCombo.setEnabled(true);
      }
     else {
           combo2.setSelectedIndex(0);
           combo2.setEnabled(false);
           orderCombo.setEnabled(false);
      }
  }

 if (e.getActionCommand().equals("LIMIT")) {

     if (limit.isSelected()) {

         areaL.setEditable(true);
         area2L.setEditable(true);
         areaL.requestFocus();
      }
     else {
           areaL.setText("");
           area2L.setText("");
           areaL.setEditable(false);
           area2L.setEditable(false);
      } 
 }

 if (e.getActionCommand().equals("OK")) {

     boolean fail = false;
     String firstVal = (String) postSEL.getSelectedItem();
     select = "SELECT " + firstVal + " \"oid\",* FROM \"" + myTable.getName() + "\"";
     String condition = " WHERE ";
     int t=0;

     for (int k=0;k<numFields;k++) {

          if (active[k]) {

              t++;
              JTextField areatmp = (JTextField) hashText.get("check-" + k);
              String xtring = areatmp.getText();
              JComboBox tmp = (JComboBox) hashOp.get("check-" + k);
              JComboBox tmpCom = (JComboBox) hashBol.get("" + k);
              String val = ""; 

              if (k != numFields-1 && tmpCom.isEnabled()) {

                  val = (String) tmpCom.getSelectedItem();
                  val += " ";
               }

              String op = (String) tmp.getSelectedItem();
              String type = myTable.getTableHeader().getType(fieldName[k]);

              if (type.startsWith("varchar") && type.startsWith("text"))
                  xtring = "'" + xtring + "'";         

              condition += "\"" + fieldName[k] + "\" " + op + " " + xtring + " " + val;

              if (!(xtring.length()>0)) {

                  fail = true;
                  JOptionPane.showMessageDialog(AdvancedFilter.this,Language.getWord("EFIW") + fieldName[k] + "'.",
                                                Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                  break;
               } // fin if

        } // fin if
      } // fin for 

      if (!fail) {

          if (t>0) 
              select += condition;        

          if (group.isSelected()) {

              String valGroup = (String) combo.getSelectedItem();
              select += " GROUP BY \"" + valGroup + "\"";
           }

          if (order.isSelected()) {

              int pos = combo2.getSelectedIndex();

              String fieldN = "";

              if (pos > 0)
                  fieldN = (String) fields.elementAt(pos-1);
              else
                  fieldN = "oid";  

              String flag = (String) orderCombo.getSelectedItem();
              orderBy = "\"" + fieldN + "\" " + flag;
              select += " ORDER BY " + orderBy;
              thereIsOrder = true;
           }

          if (limit.isSelected()) {

              String valLimit = areaL.getText();
              String valLimit2 = area2L.getText();

              if (valLimit2.length()==0)
                  valLimit2 = "0"; 

              if (valLimit.length()==0)
                  valLimit = "ALL";

              select += " LIMIT " + valLimit + " OFFSET " + valLimit2;
           }

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
     JRadioButton radiotmp = (JRadioButton) e.getSource();
     JComboBox tmp = (JComboBox) hashOp.get(cad);
     JTextField areatmp = (JTextField) hashText.get(cad);

     if (radiotmp.isSelected()) {

         active[num] = true;

         for (int k=num+1;k<numFields;k++) {

              if (active[k]) {

                  JComboBox tmpCom = (JComboBox) hashBol.get("" + num);
                  tmpCom.setEnabled(true);
                  break;
               }
          } 

     tmp.setEnabled(true);
     areatmp.setEnabled(true);
     areatmp.setEditable(true);
     areatmp.requestFocus();

     if (num>0) {

      if (active[num - 1]) {

          JComboBox tmpCom = (JComboBox) hashBol.get("" + (num-1));
          tmpCom.setEnabled(true);
       }
      else {

            if ((num-2) >= 0)

               for (int j=num-2; j>=0; j--)

                    if (active[j]) {
                        JComboBox tmpCom = (JComboBox) hashBol.get("" + j);
                        tmpCom.setEnabled(true);
                        break;
                     } //fin if
       } //fin else 
      } //fin if
     } //fin if
    else {
           tmp.setSelectedIndex(0);
           tmp.setEnabled(false);
           areatmp.setText("");
           areatmp.setEditable(false);
           areatmp.setEnabled(false);
           active[num] = false;

           if (num != (numFields - 1)) {

               JComboBox Com = (JComboBox) hashBol.get("" + num);

               if (Com.isEnabled()) {
                   Com.setSelectedIndex(0);
                   Com.setEnabled(false);
                }
     }

    boolean deal = false;

    for (int m=num+1;m<numFields;m++)

         if (active[m]) {
             deal = true;
             break;     
          }

         if (!deal) {

             for (int j=num-1; j>=0; j--)

                  if (active[j]) {
                      JComboBox tmpCom = (JComboBox) hashBol.get("" + j);
                      tmpCom.setSelectedIndex(0);
                      tmpCom.setEnabled(false);
                      break;
                   }
              }
          }
    } 
  }

 public boolean isWellDone(){
  return wellDone;
 }

 public String getSelect() {
  return select;
 }

 public String getOrder() {
  return orderBy;
 }

 public boolean isThereOrder() {
  return thereIsOrder;
 }

} // Fin de la Clase
