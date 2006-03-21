/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2002
*
* CLASS HotQueries v 0.1                                                   
* Descripcion:
* Esta clase se encarga de administrar la lista de consultas predefinidas
* por el usuario.
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
package ws.kazak.xpg.queries;

import ws.kazak.xpg.idiom.*;
import ws.kazak.xpg.misc.input.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.*;

public class HotQueries extends JDialog implements ActionListener {

 JTextField queryName;
 JTextArea queryValue;
 Vector queries = new Vector();
 JList queryList;
 JCheckBox loadCheck;
 Hashtable hashQueries = new Hashtable();
 boolean wellDone = false;
 String sqlString = "";
 boolean onFly = false;
 int numFiles = 0;
 String UHome = "";
 JFrame app;
 final JButton updateQ;
 final JButton deleteQ;
 final JButton loadQ;

 public HotQueries(JFrame parent) {

   super(parent, true);
   app = parent;
   setTitle(Language.getWord("HQ"));

   String OS = System.getProperty("os.name");

   if(OS.equals("Linux") || OS.equals("Solaris") || OS.equals("FreeBSD"))
      UHome = System.getProperty("user.home") + System.getProperty("file.separator") + ".xpg" 
              + System.getProperty("file.separator") + System.getProperty("file.separator") 
              + "queries" + System.getProperty("file.separator");
   else
      UHome = System.getProperty("xpgHome") + System.getProperty("file.separator") + "queries" + System.getProperty("file.separator");

   try {
   File queriesDir   = new File(UHome);
   File fileList[] = queriesDir.listFiles();
   numFiles = fileList.length;
    
   for (int i=0; i < numFiles; i++)
    {
      if (!fileList[i].isDirectory()) { 
          RandomAccessFile queryFile = new RandomAccessFile ("" + fileList[i], "r" ); 

          String name = queryFile.readLine();
          name = name.substring(name.indexOf("=")+1,name.length());
          queries.addElement(name);

          String value = queryFile.readLine();
          value = value.substring(value.indexOf("=")+1,value.length());

          StringTokenizer st = new StringTokenizer(value,"JUMP-LINE");

          value = "";

          while (st.hasMoreTokens())
           {
            value += st.nextToken() + "\n";
            //System.out.println(value);
           }

          String active = queryFile.readLine();
          active = active.substring(active.indexOf("=")+1,active.length()); 

          Boolean bool = new Boolean(active);
          boolean run = bool.booleanValue();

          HQStructure oneQuery = new HQStructure("" + fileList[i],value,run);
          hashQueries.put(name,oneQuery);

          queryFile.close();
         }
    }

   } catch(Exception ex) {
        System.out.println(ex);
     }

   queries = sorting(queries);

   JPanel general = new JPanel();

   JPanel global = new JPanel();
   global.setLayout(new BorderLayout());

   Border etched = BorderFactory.createEtchedBorder();
   TitledBorder titleBorder = BorderFactory.createTitledBorder(etched);

   JPanel listPanel = new JPanel(); 
   listPanel.setLayout(new BorderLayout());
   listPanel.setBorder(titleBorder);

   JLabel title = new JLabel(Language.getWord("QUERYS"),JLabel.CENTER);

   queryValue = new JTextArea();
   queryName = new JTextField(15);
   loadCheck = new JCheckBox(Language.getWord("RQOL"));

   queryList = new JList(queries);
   queryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

   updateQ = new JButton(Language.getWord("UPDT"));
   updateQ.setActionCommand("ButtonUpdate");
   updateQ.addActionListener(this);
   updateQ.setEnabled(false);

   deleteQ = new JButton(Language.getWord("DEL"));
   deleteQ.setActionCommand("ButtonDelete");
   deleteQ.addActionListener(this);
   deleteQ.setEnabled(false);

   loadQ = new JButton(Language.getWord("LOAD"));
   loadQ.setActionCommand("ButtonLoad");
   loadQ.addActionListener(this);
   loadQ.setEnabled(false);

   MouseListener mouseListener = new MouseAdapter()
   {
    public void mousePressed(MouseEvent e)
     {
         int index = queryList.locationToIndex(e.getPoint());
         if (e.getClickCount() == 1 && index > -1)
          {
            String item = (String) queries.elementAt(index);
            queryName.setText(item); 
            HQStructure tmp = (HQStructure) hashQueries.get(item);
            sqlString = tmp.getValue();
            queryValue.setText(sqlString);

            if (tmp.isReady())
                loadCheck.setSelected(true);
            else {
                  if(loadCheck.isSelected())
                     loadCheck.setSelected(false);
             }

            if (!deleteQ.isEnabled()) {
                deleteQ.setEnabled(true);
                updateQ.setEnabled(true);
                loadQ.setEnabled(true);
             }
          }
     }
   };
   queryList.addMouseListener(mouseListener);

   JScrollPane leftScroll = new JScrollPane(queryList);
   leftScroll.setPreferredSize(new Dimension(100, 120));

   listPanel.add(title,BorderLayout.NORTH);
   listPanel.add(leftScroll,BorderLayout.CENTER);
   
   JPanel editPanel = new JPanel();
   editPanel.setLayout(new BoxLayout(editPanel,BoxLayout.Y_AXIS));
   editPanel.setBorder(titleBorder);

   JPanel namePanel = new JPanel(); 
   namePanel.setLayout(new FlowLayout());
   JLabel queryLabel = new JLabel(Language.getWord("QQN") + ": ");
   namePanel.add(queryLabel);
   namePanel.add(queryName);

   JScrollPane textScroll = new JScrollPane(queryValue);
   textScroll.setPreferredSize(new Dimension(200, 120));

   JPanel queryPanel = new JPanel();
   queryPanel.setLayout(new BorderLayout());
   queryPanel.add(textScroll,BorderLayout.CENTER);

   titleBorder = BorderFactory.createTitledBorder(etched,Language.getWord("FDDESCR"));
   queryPanel.setBorder(titleBorder);

   editPanel.add(namePanel);
   editPanel.add(queryPanel);

   JPanel loadPanel = new JPanel();
   loadPanel.setLayout(new FlowLayout());

   loadPanel.add(loadQ);
   loadPanel.add(loadCheck);

   JButton newQ = new JButton(Language.getWord("NEWF"));
   newQ.setActionCommand("ButtonNew");
   newQ.addActionListener(this);

   JButton addQ = new JButton(Language.getWord("ADD"));
   addQ.setActionCommand("ButtonAdd");
   addQ.addActionListener(this);

   JButton close = new JButton(Language.getWord("CLOSE"));
   close.setActionCommand("ButtonClose");
   close.addActionListener(this);

   JPanel buttonPanel = new JPanel();
   buttonPanel.setLayout(new FlowLayout());
   buttonPanel.add(newQ);
   buttonPanel.add(addQ);
   buttonPanel.add(updateQ);
   buttonPanel.add(deleteQ);
   buttonPanel.add(close);

   titleBorder = BorderFactory.createTitledBorder(etched);
   buttonPanel.setBorder(titleBorder);

   JPanel downPanel = new JPanel();
   downPanel.setLayout(new BoxLayout(downPanel,BoxLayout.Y_AXIS));

   downPanel.add(loadPanel);
   downPanel.add(buttonPanel);

   global.add(listPanel,BorderLayout.WEST);
   global.add(editPanel,BorderLayout.CENTER);
   global.add(downPanel,BorderLayout.SOUTH);

   general.add(global);

   getContentPane().add(general);
   pack();
   setLocationRelativeTo(parent);
   setVisible(true);

 }

 public void actionPerformed(java.awt.event.ActionEvent e) {

  if (e.getActionCommand().equals("ButtonClose")) {
     setVisible(false);
     return; 
   }

  if (e.getActionCommand().equals("ButtonNew")) {
     queryList.clearSelection(); 
     queryName.setText("");
     queryValue.setText("");  
     loadCheck.setSelected(false);
     queryName.requestFocus();
     updateQ.setEnabled(false);
     deleteQ.setEnabled(false);
     loadQ.setEnabled(false);

     return;
   }

  if (e.getActionCommand().equals("ButtonAdd")) {
     String nameQ = queryName.getText();
     String valueQ =  queryValue.getText();

     if (nameQ.length() < 1 || valueQ.length() < 1) {
         JOptionPane.showMessageDialog(HotQueries.this,
         Language.getWord("EMPTY"),
         Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
         return;
      }

     if (!queries.contains(nameQ)) { 

         //if (!valueQ.endsWith(";"))
         //    valueQ += ";";

         addQuery(nameQ,valueQ);
      }
     else 
         JOptionPane.showMessageDialog(HotQueries.this,
         Language.getWord("EQQN"),
         Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

     return;
   }

  if (e.getActionCommand().equals("ButtonUpdate")) {
     String nameQ = queryName.getText();
     String valueQ =  queryValue.getText();

     if (nameQ.length() < 1 || valueQ.length() < 1) {
         JOptionPane.showMessageDialog(HotQueries.this,
         Language.getWord("EMPTY"),
         Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
         return;
      }

     if (queries.contains(nameQ)) {

   valueQ = Queries.clearSpaces(valueQ);

   if (!valueQ.endsWith(";"))
        valueQ += ";";

         HQStructure old = (HQStructure) hashQueries.remove(nameQ);
         String fileName = old.getFileName();
         HQStructure oneQuery = new HQStructure(fileName,valueQ,loadCheck.isSelected());
         hashQueries.put(nameQ,oneQuery);

         try {
               File deleter = new File(fileName);
               deleter.delete();

               PrintStream sqlFile = new PrintStream(new FileOutputStream(fileName));
               sqlFile.println("name=" + nameQ);
               sqlFile.println("value=" + valueQ);
               sqlFile.println("run=" + loadCheck.isSelected());
               sqlFile.close();
             }
         catch(Exception ex) {
               System.out.println(ex);
          }

      }
     else {
         addQuery(nameQ,valueQ);
      }

     return;
   }

  if (e.getActionCommand().equals("ButtonDelete")) {

      if (!queryList.isSelectionEmpty()) {

          GenericQuestionDialog delete = new GenericQuestionDialog(app,Language.getWord("YES"),Language.getWord("NO"),Language.getWord("CONFRM"),Language.getWord("DELIT"));
          boolean sure = delete.getSelecction();

          if (sure) {
              String queryTarget = (String) queryList.getSelectedValue();
              int pos = queryList.getSelectedIndex();

              if (queryTarget != null) {
                  String newItem = "";

                  if (pos < queries.size() - 1)
                      newItem = (String) queries.elementAt(pos + 1);
                  else
                      if (queries.size()>1)
                          newItem = (String) queries.elementAt(pos - 1);

                  queries.remove(queryTarget);
                  queryList.setListData(queries);

                  HQStructure next;

                  if (queries.size() != 0) {
                      queryList.setSelectedValue(newItem,true); 
                      queryName.setText(newItem);
                      next = (HQStructure) hashQueries.get(newItem);
                      queryValue.setText(next.getValue());
                      loadCheck.setSelected(next.isReady());                      
                   }
                  else {
                        queryName.setText("");
                        queryValue.setText("");
                        loadCheck.setSelected(false);
                   }

                  next = (HQStructure) hashQueries.get(queryTarget);
                  File deleter = new File(next.getFileName());
                  deleter.delete();
               }
          }
       }
      return;
   }

  if (e.getActionCommand().equals("ButtonLoad")) {
      wellDone = true;

      if (loadCheck.isSelected())
          onFly = true;

      setVisible(false);

      return;
   }

 }

 public void addQuery(String nameQ, String valueQ) {

   valueQ = Queries.clearSpaces(valueQ);

   if (!valueQ.endsWith(";"))
        valueQ += ";";

   queries.addElement(nameQ);
   queries = sorting(queries);

   queryList.setListData(queries);
   queryList.setSelectedValue(nameQ,true);
   queryList.requestFocus();

   updateQ.setEnabled(true);
   deleteQ.setEnabled(true);
   loadQ.setEnabled(true);

   boolean load = loadCheck.isSelected();
  
   try {
         String fileName = UHome;

         while (true) {

           if (numFiles < 10)
               fileName += "00" + numFiles;
           else 
               fileName += "0" + numFiles;

           File proof = new File(fileName);

           if (proof.exists()) {
               numFiles++;
               fileName = UHome;
            }
           else
               break;
         }

         HQStructure oneQuery = new HQStructure(fileName,valueQ,load);
         hashQueries.put(nameQ,oneQuery);

         StringTokenizer st = new StringTokenizer(valueQ,"\n");

         valueQ = "";

         while (st.hasMoreTokens())
                valueQ += st.nextToken() + "JUMP-LINE";


         PrintStream sqlFile = new PrintStream(new FileOutputStream(fileName));
         sqlFile.println("name=" + nameQ);
         sqlFile.println("value=" + valueQ);
         sqlFile.println("run=" + load);
         sqlFile.close();
       }
      catch(Exception ex) {
            System.out.println(ex);
       }
  }

 public boolean isWellDone() {
   return wellDone;
  }

 public String getSQL() {
   return sqlString;
  }

 public boolean isReady() {
   return onFly;
  }

 public Vector sorting(Vector in) {
    for(int i=0; i<in.size()-1;i++)
     {
      for(int j=i+1;j<in.size();j++)
       {
         String first = (String) in.elementAt(i);
         String second = (String) in.elementAt(j);
         if(second.compareTo(first) < 0) {
          in.setElementAt(second,i);
          in.setElementAt(first,j);
          }
       }

     }
    return in;
  }

}
