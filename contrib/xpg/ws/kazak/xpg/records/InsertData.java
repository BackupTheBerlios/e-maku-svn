/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS InsertData v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el dialogo a traves del
* cual se inserta un registro en una tabla.
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
import ws.kazak.xpg.db.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class InsertData extends JDialog implements ActionListener {

 JTextField area;
 Hashtable hashText = new Hashtable();
 Hashtable dataText = new Hashtable();

 String[] fieldName;
 int numFields;
 Table mytable;
 String SQLinsert = "";

 String values = "";
 String fields = "";
 String tableName = "";

 boolean wellDone = false;

 public InsertData(String realTableName, Table table,JFrame frame) {

    super(frame, true);
    tableName = realTableName;
    JPanel global = new JPanel();
    global.setLayout(new BoxLayout(global,BoxLayout.Y_AXIS));
    JScrollPane scroll;
    setTitle(Language.getWord("INSFORM"));
    mytable = table;

    JPanel base = new JPanel();
    base.setLayout(new GridLayout(0,1));
    JPanel data = new JPanel();
    data.setLayout(new GridLayout(0,1));

    numFields = mytable.getTableHeader().getNumFields();
    Hashtable hashFields = mytable.getTableHeader().getHashtable();
    Vector fields = mytable.getTableHeader().getNameFields();
    fieldName = new String[numFields];

    for (int i=0; i<numFields ;i++) {
         String nfield = (String) fields.elementAt(i);
         area = new JTextField(10);

         String typeField = mytable.getTableHeader().getType(nfield);

         JLabel check = new JLabel(nfield + " [" + typeField + "] = ");

         String boolArray[] = {"true","false"};
         JComboBox booleanCombo = new JComboBox(boolArray);

         String label = "check-" + i;
         fieldName[i] = nfield;
         base.add(check);

         if (typeField.equals("bool")) {
             hashText.put(label,booleanCombo);
             data.add(booleanCombo);
          }
          else {
                 if (typeField.equals("text")) {
                     JButton text = new JButton(Language.getWord("ADDTXT"));
                     text.setActionCommand(label);
                     text.addActionListener(this);

                     hashText.put(label,text);
                     data.add(text);
                  }  
                 else {
                       hashText.put(label,area);
                       data.add(area);
                  }
           }
     }

     Border etched1 = BorderFactory.createEtchedBorder();
     TitledBorder title1 = BorderFactory.createTitledBorder(etched1);

     JPanel center = new JPanel();
     center.setLayout(new BorderLayout());
     center.add(base,BorderLayout.WEST);
     center.add(data,BorderLayout.CENTER);

     //String tableName = "\"" +  + "\""; 

     /* System.out.println("Booleano: " + mytable.isUserSchema());

     if (mytable.isUserSchema())
         tableName = mytable.getSchema() + "." + tableName ; */

     JLabel title = new JLabel("INSERT INTO " + tableName,JLabel.CENTER);
     JPanel first = new JPanel();
     first.setLayout(new FlowLayout(FlowLayout.CENTER));
     first.add(title);
     title1 = BorderFactory.createTitledBorder(etched1);
     first.setBorder(title1);

     title1 = BorderFactory.createTitledBorder(etched1,"VALUES");
     center.setBorder(title1);

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

     global.add(first);

     if (numFields > 15) {
         scroll = new JScrollPane(center);
         scroll.setPreferredSize(new Dimension(400,400));
         global.add(scroll);
      }
     else
         global.add(center);

     global.add(botons);

     getContentPane().add(global);
     pack();
     setLocationRelativeTo(frame);
     setVisible(true);
}

 public boolean wasOk(){
   return wellDone;
  }

 public String getSQLString(){
   return SQLinsert;
  }

 public void actionPerformed(java.awt.event.ActionEvent e) {

   if (e.getActionCommand().startsWith("check")) {

       String strEvent = e.getActionCommand();
       int num = Integer.parseInt(strEvent.substring(strEvent.indexOf("-")+1,strEvent.length()));

       String preStr = (String) dataText.get(strEvent);

       if (preStr == null)
           preStr = ""; 

       TextDataInput textWindow = new TextDataInput(InsertData.this,fieldName[num], preStr);

       if (textWindow.isWellDone()) {
           String text = textWindow.getValue(); 
           dataText.put(strEvent,text);
        }

       return;
    }


   if (e.getActionCommand().equals("OK")) {

       int i=0;
       values = "";
       fields = "";

       for (; i<numFields ;i++) {

            String label = "check-" + i;
            String data = "";
            int typeComponent = -1;
            JTextField tmp = new JTextField();

            Object obj = (Object) hashText.get(label);            

            if (obj instanceof JTextField) {
                tmp = (JTextField) obj;
                data = tmp.getText();
                typeComponent = 0;
             }
            else {
                  if (obj instanceof JComboBox) {
                      JComboBox bool = (JComboBox) obj;
                      data = (String) bool.getSelectedItem();
                      typeComponent = 1;
                   }
                  else {
                        if (obj instanceof JButton) {
                            data = (String) dataText.get(label);
                            typeComponent = 2;
                            if (data == null)
                                data = "";
                         }
                   }
             }

            data = data.trim();

            TableFieldRecord fieldData = mytable.getTableHeader().getTableFieldRecord(fieldName[i]);
            OptionField options = fieldData.getOptions();

            if (data.length()!=0 || typeComponent == 2) {

                String type = mytable.getTableHeader().getType(fieldName[i]);
                int k = getTypeCode(type);

                if (k==1) {

                    if (typeComponent != 2) {

                        if (!data.startsWith("'"))
                            data = "'" + data;

                        if (!data.endsWith("'"))
                            data = data + "'";
                     }
                    else
                         data = "'" + data + "'";
                 }

                if (k==2) {

                    if (!isNum(data)) {

                        JOptionPane.showMessageDialog(InsertData.this,
                        Language.getWord("TNE1") + fieldName[i] + Language.getWord("TNE2"),
                        Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

                        if (typeComponent == 0)
                            tmp.requestFocus();

                        return;
                     }
                 }

                if (typeComponent == 2 && data.length() == 0) {

                    if (options.isNullField()) {

                        JOptionPane.showMessageDialog(InsertData.this,
                        fieldName[i] + Language.getWord("FNN"),
                        Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

                        return;
                     }

                    fixDefaultValue(options,data,i);
                 }
                else {
                      values += data;
                      fields += "\"" + fieldName[i] + "\"";

                      if (i < numFields-1) {
                          values += ",";
                          fields += ",";
                       }
                 }
            }
           else {
                   if (options.isNullField()) {

                       JOptionPane.showMessageDialog(InsertData.this,
                       fieldName[i] + Language.getWord("FNN"),
                       Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

                       return;
                    }

                   fixDefaultValue(options,data,i);

               } //fin else

       } //Fin for

       fields = "(" + fields + ")";
       SQLinsert = "INSERT INTO " + tableName + " " + fields + " VALUES(" + values + ");";

       wellDone = true;
       setVisible(false);
   }

 if (e.getActionCommand().equals("CANCEL")) {
     setVisible(false);
  }

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
                   else
                       dataText.clear();
             }
      }
   }

  }

 public int getTypeCode(String typeStr)
  {
   if(typeStr.startsWith("varchar") || typeStr.startsWith("char") || typeStr.startsWith("text") || typeStr.startsWith("name") || typeStr.startsWith("date") || typeStr.startsWith("time"))
     return 1;

   if(typeStr.startsWith("int") || typeStr.equals("serial") || typeStr.equals("smallint") || typeStr.equals("real") || typeStr.equals("double"))
     return 2;

   if(typeStr.startsWith("bool"))
     return 3;
   else
     return 4;
 }

 public void fixDefaultValue(OptionField options, String data, int i) {

   String defaultV = options.getDefaultValue();

   if (defaultV == null || defaultV.length() == 0) {

       values += "NULL";
       fields += "\"" + fieldName[i] + "\"";

       if (i < numFields-1) {
           values += ",";
           fields += ",";
        }
    }
   else {

          if ((i == numFields-1) && (data.length() == 0) && (values.length() > 0)) {

              values = values.substring(0,values.length() - 1);
              fields = fields.substring(0,fields.length() - 1);

           } //fin if

          if (numFields == 1) {

              values += defaultV;
              fields += "\"" + fieldName[i] + "\"";

           } //fin if
    } //fin else  
 }

 public boolean isNum(String word) {

   for (int i=0;i<word.length();i++) {
        char c = word.charAt(i);
        if (!Character.isDigit(c))
            return false;
    }

   return true;
 }

} // Fin de la Clase
