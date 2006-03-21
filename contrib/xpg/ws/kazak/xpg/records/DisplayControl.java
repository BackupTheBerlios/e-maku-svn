/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2002
*
* CLASS DisplayControl v 0.1                                                   
* Descripcion:
* Esta clase se encarga de mostrar la lista de atributos de una 
* tabla, para que el usuario escoja cuales de ellos desea visualizar.
*
* Esta clase es instanciada desde la clase Records.
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
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.db.Table;
import ws.kazak.xpg.idiom.Language;

public class DisplayControl extends JDialog implements ActionListener {

 Table myTable;
 Hashtable checkFields = new Hashtable();
 JButton clear;
 int numFields;
 boolean selected = true;
 Vector fields; 
 String filter = ""; 
 JCheckBox keepCBox;
 boolean keepIt = false;
 boolean wellDone = false;

 public DisplayControl(Table table,JFrame parent, String nameFields, boolean keepOn) 
  {
   super(parent, true);
   setTitle(Language.getWord("DSPLY"));
   myTable = table;

   Hashtable previewFields = new Hashtable(); 

   if (!nameFields.equals("\"oid\",*")) {

        StringTokenizer st = new StringTokenizer(nameFields,",");

        while (st.hasMoreTokens()) {

               String field = st.nextToken();

               if (!field.equals("oid")) 
                   previewFields.put(field,field);
         }
    }

   JPanel global = new JPanel();
   global.setLayout(new BoxLayout(global,BoxLayout.Y_AXIS));

   JPanel base = new JPanel();
   base.setLayout(new GridLayout(0,1));

   numFields = myTable.getTableHeader().getNumFields();
   Hashtable hashFields = myTable.getTableHeader().getHashtable();

   fields = myTable.getTableHeader().getNameFields();

   for (int i=0; i<numFields ;i++) {

        String nfield = (String) fields.elementAt(i);

        JCheckBox check = new JCheckBox(nfield);
        checkFields.put("" + i,check);
        base.add(check);

        if (previewFields.containsKey("\"" + nfield + "\"")) 
            check.setSelected(true);

    } // fin for

   JPanel center = new JPanel();
   center.setLayout(new BorderLayout());
   center.add(base,BorderLayout.WEST);

   JPanel up = new JPanel();
   up.setLayout(new FlowLayout(FlowLayout.CENTER));
   up.add(center);

   Border etched1 = BorderFactory.createEtchedBorder();
   TitledBorder title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("VFIELDS"));
   up.setBorder(title1);

   JButton ok = new JButton(Language.getWord("OK"));
   ok.setActionCommand("OK");
   ok.addActionListener(this);

   clear = new JButton(Language.getWord("SELALL"));
   clear.setActionCommand("SELECTION");
   clear.addActionListener(this);

   JButton cancel = new JButton(Language.getWord("CANCEL"));
   cancel.setActionCommand("CANCEL");
   cancel.addActionListener(this);

   JPanel botons = new JPanel();
   botons.setLayout(new FlowLayout(FlowLayout.CENTER));
   botons.add(ok);
   botons.add(clear);
   botons.add(cancel);

   if(numFields > 15)
    {
     JScrollPane scroll = new JScrollPane(up);
     scroll.setPreferredSize(new Dimension(400,400));
     global.add(scroll);
    }
   else
     global.add(up);

   JPanel keep = new JPanel();
   keepCBox = new JCheckBox(Language.getWord("KSDS"));

   if (keepOn)
       keepCBox.setSelected(true);

   keep.add(keepCBox);
   global.add(keep);

   global.add(botons);

   getContentPane().add(global);
   pack();
   setLocationRelativeTo(parent);
   setVisible(true);
 }

 public void actionPerformed(java.awt.event.ActionEvent e) {

  if (e.getActionCommand().equals("OK")) {

     for (int k=0;k<numFields;k++) { 

       JCheckBox chTmp = (JCheckBox) checkFields.get("" + k);
       if (chTmp.isSelected()) {
           String nf = (String) fields.elementAt(k);
           nf = "\"" + nf + "\""; 
           filter += nf + ",";
        }
        
     } // fin for

     if (filter.length() > 0)
         filter = filter.substring(0,filter.length()-1);

     if (keepCBox.isSelected()) {
         keepIt = true;
       
         if (filter.length() == 0) {

             JOptionPane.showMessageDialog(DisplayControl.this,
             Language.getWord("ALOF"),
             Language.getWord("ERROR!"), JOptionPane.ERROR_MESSAGE);

             return; 
          }
     }

     wellDone = true; 
     setVisible(false);
     return; 
   }

  if (e.getActionCommand().equals("SELECTION")) {

      for (int k=0;k<numFields;k++) {
           JCheckBox chTmp = (JCheckBox) checkFields.get("" + k);
           chTmp.setSelected(selected);
       } // fin for

     if(selected){
        selected = false;
        clear.setText(Language.getWord("UNSELALL"));
      }
     else {
        selected = true;
        clear.setText(Language.getWord("SELALL"));
      }

     return;
   }

  if (e.getActionCommand().equals("CANCEL")) {
      setVisible(false);
      return;
   }

 } // fin del Metodo

 public String getFilter() {
   return filter;
  }

 public boolean isKeepIt() {
   return keepIt;
  }

 public boolean isWellDone() {
   return wellDone;
  }

} //Fin de Clase
