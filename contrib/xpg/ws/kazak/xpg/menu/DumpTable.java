/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS DumpTable v 0.1                                                   
* Descripcion:
* Clase encargada de manejar el dialogo mediante el cual se
* realiza el dump de una o varias bases de datos.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.db.Table;
import ws.kazak.xpg.db.TableFieldRecord;
import ws.kazak.xpg.db.TableHeader;
import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.misc.file.ExtensionFilter;
import ws.kazak.xpg.misc.input.GenericQuestionDialog;

public class DumpTable extends JDialog implements ActionListener {

 boolean offline = false;
 boolean ready = false;
 private String typedText = null;
 JList tablesList;
 JComboBox combo1;
 Vector TableList;
 JFrame frame;
 JTextField textField2;
 JRadioButton strOnlyButton;
 JRadioButton strDataButton;
 JButton ok;
 Vector vecConn;
 Vector dbNames;
 PGConnection dbDump;
 String tablesString = "";
 String DBWinner = "";
 String destiny = "";
 boolean listOk=false;
 boolean checkOk;
 boolean fileOk=false;
 boolean wellDone = false;

 public DumpTable (JFrame aFrame,Vector dbnm,Vector VecC) {

  super(aFrame, true);
  frame = aFrame;
  vecConn = VecC;
  dbNames = dbnm;

  setTitle(Language.getWord("DUMPT"));
  JPanel global = new JPanel();
  global.setLayout(new BoxLayout(global,BoxLayout.Y_AXIS));

  String[] dataBases = new String[dbNames.size()];

  for (int i=0;i<dbNames.size();i++) {
       String db = (String) dbNames.elementAt(i);

       if(i==0)
          DBWinner = db;

       dataBases[i] = db;
   }
  
  int index = dbNames.indexOf(dataBases[0]);
  dbDump = (PGConnection) vecConn.elementAt(index);
  TableList = dbDump.TableQuery("SELECT tablename FROM pg_tables WHERE tablename !~ '^pg_' AND tablename  !~ '^pga_' ORDER BY tablename");
  String[] tables = new String[TableList.size()];

  for (int i=0;i<TableList.size();i++) {

       Vector o = (Vector) TableList.elementAt(i);
       tables[i] = (String) o.elementAt(0);
   }

  /*** Construción Barra de botones ***/

  JButton buttonAll = new JButton(Language.getWord("SELALL"));
  buttonAll.setMnemonic('A');
  buttonAll.setActionCommand("sALL");
  buttonAll.addActionListener(this);
  
  JButton buttonNone = new JButton(Language.getWord("CLR"));
  buttonNone.setMnemonic('N');
  buttonNone.setActionCommand("sNOE");
  buttonNone.addActionListener(this);

  JPanel buttonPanel = new JPanel();
  buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
  buttonPanel.add(buttonAll);
  buttonPanel.add(buttonNone);  

  /** Construción parte izquierda de la ventana **/

  JPanel leftTop = new JPanel();
  JLabel msgString1 = new JLabel(Language.getWord("SELECTDB"),JLabel.CENTER);
  combo1 = new JComboBox(dataBases);
  combo1.setActionCommand("COMBO");
  combo1.addActionListener(this);

  tablesList = new JList(tables);
  tablesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

  MouseListener mouseListener = new MouseAdapter() {

     public void mousePressed(MouseEvent e) {

        int index = tablesList.locationToIndex(e.getPoint());

        if (e.getClickCount() == 1 && index > -1) {

            if (fileOk && checkOk && !ok.isEnabled())
                ok.setEnabled(true);
         }
      }
   };

  tablesList.addMouseListener(mouseListener);

  JScrollPane componente = new JScrollPane(tablesList);
  componente.setPreferredSize(new Dimension(40, 70));

  JPanel block = new JPanel();
  block.setLayout(new BoxLayout(block,BoxLayout.Y_AXIS));
  block.add(combo1);
  block.add(componente);

  leftTop.setLayout(new BorderLayout());
  leftTop.add(msgString1,BorderLayout.NORTH);
  leftTop.add(block,BorderLayout.CENTER);

  JPanel got = new JPanel();
  got.setLayout(new FlowLayout(FlowLayout.CENTER));
  got.add(leftTop);

  JPanel leftPanel = new JPanel();
  leftPanel.setLayout(new BorderLayout());
  leftPanel.add(got,BorderLayout.NORTH);
  leftPanel.add(buttonPanel,BorderLayout.SOUTH);
  
  /*** Construcción parte derecha de la ventana ***/
  //Creacion radio Button

  strOnlyButton = new JRadioButton(Language.getWord("SDG"));
  strOnlyButton.setSelected(true);
  checkOk = true;
  strOnlyButton.setMnemonic('u'); 
  strOnlyButton.setActionCommand("SOnly"); 
  strOnlyButton.addActionListener(this);

  strDataButton = new JRadioButton(Language.getWord("RECS"));
  strDataButton.setMnemonic('e'); 
  strDataButton.setActionCommand("SOnly"); 
  strDataButton.addActionListener(this);
  
  JPanel rightTop = new JPanel();
  rightTop.setLayout(new BoxLayout(rightTop,BoxLayout.Y_AXIS));
  rightTop.add(strOnlyButton);
  rightTop.add(strDataButton);

  Border etched = BorderFactory.createEtchedBorder();
  TitledBorder title = BorderFactory.createTitledBorder(etched,Language.getWord("DUMP"));
  title.setTitleJustification(TitledBorder.LEFT);
  rightTop.setBorder(title);
  
  JPanel rightDown = new JPanel();
  JLabel msgString2 = new JLabel(Language.getWord("FN"));
  textField2 = new JTextField(15); 

  JButton buttonOpen = new JButton(Language.getWord("BROWSE"));
  buttonOpen.setActionCommand("OPEN");
  buttonOpen.addActionListener(this);

  rightDown.setLayout(new BorderLayout());
  rightDown.add(msgString2,BorderLayout.NORTH);
  rightDown.add(textField2,BorderLayout.WEST);
  rightDown.add(buttonOpen,BorderLayout.EAST);  
 
  JPanel intern = new JPanel();
  intern.setLayout(new FlowLayout(FlowLayout.CENTER));
  intern.add(rightDown);

  JPanel rightPanel = new JPanel();
  rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
  rightPanel.add(rightTop);
  rightPanel.add(intern);
  
  /** Unión de todos los paneles de la ventana ***/
  
  JPanel downPanel = new JPanel();
  downPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
  downPanel.add(leftPanel);
  downPanel.add(rightPanel);

  title = BorderFactory.createTitledBorder(etched);
  downPanel.setBorder(title);

  ok = new JButton(Language.getWord("OK"));
  ok.setEnabled(false);
  ok.setActionCommand("OK");
  ok.addActionListener(this);

  JButton cancel = new JButton(Language.getWord("CANCEL"));
  cancel.setActionCommand("ButtonCancel");
  cancel.addActionListener(this);

  JPanel botons = new JPanel();
  botons.setLayout(new FlowLayout(FlowLayout.CENTER));
  botons.add(ok);
  botons.add(cancel);

  global.add(downPanel); 
  global.add(botons);

  getContentPane().add(global);
  pack();
  setLocationRelativeTo(frame);
  setVisible(true);

 }

 public void actionPerformed(java.awt.event.ActionEvent e) {

  if (e.getActionCommand().equals("ButtonCancel")) {
      setVisible(false);
      return;
   }		

  if (e.getActionCommand().equals("SOnly")) {

      if (strOnlyButton.isSelected() || strDataButton.isSelected()) {

          checkOk = true;

          if (!tablesList.isSelectionEmpty())
              listOk = true;

          if (listOk && fileOk)
              ok.setEnabled(true);
       }
      else {
             checkOk = false;

             if (ok.isEnabled())
                 ok.setEnabled(false);
       }
   }

  if (e.getActionCommand().equals("COMBO")) {

      DBWinner = (String) combo1.getSelectedItem();

      int index = dbNames.indexOf(DBWinner);
      dbDump = (PGConnection) vecConn.elementAt(index);
      TableList = dbDump.TableQuery("SELECT tablename FROM pg_tables where tablename !~ '^pg_' AND tablename  !~ '^pga_' ORDER BY tablename");
      String[] tables = new String[TableList.size()];
 
      for (int i=0;i<TableList.size();i++) {

           Vector o = (Vector) TableList.elementAt(i);
           String db = (String) o.elementAt(0);
           tables[i] = db;
       }

      tablesList.setListData(tables);

      if ((TableList.size() == 0) && ok.isEnabled())
           ok.setEnabled(false);
      
      return;
   }

 if (e.getActionCommand().equals("sALL")) {

     int[] indices = new int[TableList.size()];

     for (int k=0;k<TableList.size();k++)
          indices[k] = k;

     tablesList.setSelectedIndices(indices);
     listOk = true;

     if (checkOk && fileOk)
         ok.setEnabled(true);

     return;
  }

 if (e.getActionCommand().equals("sNOE")) {

     if (!(tablesList.isSelectionEmpty())) {

         tablesList.clearSelection();
         listOk = false;

         if (ok.isEnabled())
             ok.setEnabled(false);
      }

     return;
  }

 if (e.getActionCommand().equals("OPEN")) {

     String s = "file:" + System.getProperty("user.home");
     File file;
     boolean Rewrite = true;
     String FileName = "";

     JFileChooser fc = new JFileChooser(s);
     ExtensionFilter filter = new ExtensionFilter("sql",Language.getWord("SQLF"));
     fc.addChoosableFileFilter(filter);    

     int returnVal = fc.showDialog(frame,Language.getWord("EXPORTAB"));

     if (returnVal == JFileChooser.APPROVE_OPTION) {

         file = fc.getSelectedFile();
         FileName = file.getAbsolutePath();

         if (file.exists()) {

             GenericQuestionDialog win = new GenericQuestionDialog(frame,Language.getWord("YES"),Language.getWord("NO"),Language.getWord("ADV"),
                        Language.getWord("FILE") + " \"" + FileName + "\" " + Language.getWord("SEQEXIS2") + " " + Language.getWord("OVWR"));
             Rewrite = win.getSelecction();
          } 

         if (Rewrite) {

             textField2.setText(FileName);
             fileOk = true;

             if (!tablesList.isSelectionEmpty())
                 listOk = true;

             if (checkOk && listOk)
                 ok.setEnabled(true);
         }
       } 

    return;
  }

 if (e.getActionCommand().equals("OK")) {

     destiny = textField2.getText();    

     if (destiny.length()>0) {

         Object[] tables = tablesList.getSelectedValues();

         try {

              if (!destiny.endsWith(".sql"))
                  destiny += ".sql";

              if ((destiny.indexOf("/") == -1) && (destiny.indexOf("\\") == -1))
                   destiny = System.getProperty("user.home") + System.getProperty("file.separator") + destiny;

              PrintStream sqlFile = new PrintStream(new FileOutputStream(destiny)); 

              for (int k=0;k<tables.length;k++) {

                   String nameT = (String)tables[k];
                   tablesString += "'" + nameT + "'";

                   if (k<(tables.length-1))
                       tablesString += ",";

                   Table table = dbDump.getSpecStrucTable(nameT);

                   if (strOnlyButton.isSelected()) {
                       String sql = BuildSQLStructure(nameT,table);
                       sqlFile.print(sql);
                    }

                   if (strDataButton.isSelected()) {
                       TableHeader headT = table.base;
                       String sql = BuildSQLRecords(nameT,headT);
                       sqlFile.print(sql);
                    }

                   sqlFile.print("\n");
              }

             sqlFile.close(); 

            }
           catch(Exception ex)
             {
               System.out.println("Error: " + ex);
               ex.printStackTrace();
             }

           wellDone = true;
           setVisible(false);

           return;
          }
        else
          {
            JOptionPane.showMessageDialog(DumpTable.this,Language.getWord("DFINS"),Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
          }

        }

 }

String BuildSQLStructure(String tableName,Table table) {

   //Table table = dbDump.getSpecStrucTable(tableName);
   String sql = "CREATE TABLE " + tableName + " (\n";
   TableHeader headT = table.getTableHeader();
   int numFields = headT.getNumFields();

   for (int k=0;k<numFields;k++) {

        Object o = (String) headT.fields.elementAt(k);
        String field_name = o.toString();
        TableFieldRecord tmp = (TableFieldRecord) headT.getHashtable().get(field_name);
        sql += tmp.getName() + " ";

        String typeF = tmp.getType();

        if ("char".equals(typeF) || "varchar".equals(typeF)) {

            int longStr = tmp.getOptions().getCharLong();

            if (longStr>0)
                typeF = typeF + "(" + longStr + ")";
            //else
            //    typeF = tmp.Type;
         }

        sql += typeF + " ";

        Boolean tmpbool = new Boolean(tmp.getOptions().isNullField());

        if (tmpbool.booleanValue())
            sql += "NOT NULL ";

        String defaultV = tmp.getOptions().getDefaultValue();

        if (defaultV.endsWith("::bool")) {

            if (defaultV.indexOf("t")!=-1)
                defaultV = "true";
            else
                defaultV = "false";
         }

        if (defaultV.length()>0)
            sql += " DEFAULT " + defaultV;

        if (k < numFields-1)
            sql += ",\n";
   }

   sql += "\n);\n";
   
   return sql;
 } 

String BuildSQLRecords(String table,TableHeader headT) {

   Vector data = dbDump.TableQuery("SELECT * FROM " + table);
   Vector col = dbDump.getTableHeader();
   String sql = "";

   if (!dbDump.queryFail()) {

      for (int p=0;p<data.size();p++) {

           sql += "INSERT INTO " + table + " VALUES(";
           Vector tempo = (Vector) data.elementAt(p);
           int numCol = tempo.size();

           for (int j=0;j<numCol;j++) {

                String colName = (String) col.elementAt(j);
                String type = headT.getType(colName);
                Object o = tempo.elementAt(j);

                if (o != null) {

                    if (type.startsWith("varchar") || type.startsWith("char") || type.startsWith("text") 
                        || type.startsWith("name") || type.startsWith("date") || type.startsWith("time"))
                        sql += "'" + o.toString() + "'"; 
                    else {
                        sql += o.toString();
                     }
                 }
                else
                    sql += "NULL";

                if (j < (numCol - 1))
                   sql += ",";
            }

           sql += ");\n";
       }
     }

   return sql;
 }

public boolean isDone() {
  return wellDone;
 }

public String getDBName() {
  return DBWinner;
 }

public String getTables() {
  return tablesString;
 }

public String getFile() {
  return destiny;
 }

} //Fin de la Clase
