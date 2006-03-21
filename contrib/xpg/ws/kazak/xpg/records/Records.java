/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS Records v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el panel de Registros
* en la interfaz principal. A traves de este panel, se
* pueden realizar operaciones como ingresar, modificar y
* eliminar registros de una tabla.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.db.Table;
import ws.kazak.xpg.db.TableHeader;
import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.misc.input.ErrorDialog;
import ws.kazak.xpg.misc.input.ExportSeparatorField;
import ws.kazak.xpg.misc.input.ExportToFile;
import ws.kazak.xpg.misc.input.GenericQuestionDialog;
import ws.kazak.xpg.misc.input.ImportSeparatorField;
import ws.kazak.xpg.report.ExportToReport;
import ws.kazak.xpg.report.ReportDesigner;

public class Records extends JPanel implements ActionListener,SwingConstants,KeyListener,FocusListener {

 JToolBar StructureBar;
 JScrollPane tablaScroll;
 JPanel General;
 JTextField title;
 JScrollPane windowX;
 JPanel firstPanel;
 JPanel base;
 JButton insertRecord,delRecord,updateRecord,exportFile,reportBut;
 JCheckBox rectangle,rectangle2;
 JComboBox combo1,combo2;
 JTextField combo3,numRegTextField,limitText; 
 JLabel men1,men2;
 JButton button,advanced;
 JTable table;
 JPanel up;
 JFrame frame;

 final JButton queryB;
 final JButton queryLeft;
 final JButton queryRight;
 final JButton queryF;

 JTextField onScreen = new JTextField();
 JTextField onTable = new JTextField();
 JTextField onMem = new JTextField();
 JTextField currentStatistic = new JTextField();

 boolean firstBool = true;
 String currentTable = "";
 String oldTable = "";
 String operator = "";
 String field = "";
 Table tableStruct;
 PGConnection connReg;
 final JPopupMenu popup = new JPopupMenu();
 String sentence = "";
 String where = "";
 boolean refreshOn = false;

 int numReg = 0;
 int totalReg = 0;
 int recIM = 0; 
 int nPages = 0;
 int indexMin = 1;
 int indexMax = 50;
 int currentPage = 1;
 int oldPage = 0;
 int oldMem = 0;
 int start = 0;
 int limit = 50;

 String firstField;
 String recordFilter = "\"oid\"," + "*";
 String orderBy = "oid";
 Hashtable hashRecordFilter = new Hashtable();
 Hashtable hashDB = new Hashtable();
 MyTableModel myModel;

 Vector columnNamesVector = new Vector();

 Object[] columnNames;
 Object[][] data;

 JTextArea LogWin;

 boolean firstTime = true;
 boolean connected = true;

 String realTableName;

 /******************** METODO CONSTRUCTOR ********************/
 public Records (JFrame xframe,JTextArea log) {

  frame = xframe;
  LogWin = log;
  setLayout(new BorderLayout());
  StructureBar = new JToolBar(SwingConstants.VERTICAL);
  StructureBar.setFloatable(false);
  CreateToolBar();

  Border etched1 = BorderFactory.createEtchedBorder();

  title = new JTextField("");
  title.setHorizontalAlignment(JTextField.CENTER);
  title.setEditable(false);

  JPanel top = new JPanel();
  top.setLayout(new BorderLayout());
  top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));
  top.add(title);

  JPanel recordsControl = new JPanel();
  recordsControl.setLayout(new FlowLayout());

  onTable.setText(" " + Language.getWord("TOTAL") + " : 0 ");
  onTable.setEditable(false);

  onMem.setText(" " + Language.getWord("ONMEM") + " : 0 ");
  onMem.setEditable(false);

  onScreen.setText(" " + Language.getWord("ONSCR") + " : 0 ");
  onScreen.setEditable(false);

  recordsControl.add(onScreen);
  recordsControl.add(onMem);
  recordsControl.add(onTable);

  JPanel recordsButtons = new JPanel();
  recordsButtons.setLayout(new FlowLayout());

  URL imgURB = getClass().getResource("/icons/backup.png");
  queryB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURB)));
  queryB.setEnabled(false);
  queryB.setToolTipText(Language.getWord("FSET"));

  URL imgURLeft = getClass().getResource("/icons/queryLeft.png");
  queryLeft = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURLeft)));
  queryLeft.setEnabled(false);
  queryLeft.setToolTipText(Language.getWord("PSET"));

  URL imgURRight = getClass().getResource("/icons/queryRight.png");
  queryRight = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURRight)));
  queryRight.setEnabled(false);
  queryRight.setToolTipText(Language.getWord("NSET"));

  URL imgURF = getClass().getResource("/icons/forward.png");
  queryF = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURF)));
  queryF.setEnabled(false);
  queryF.setToolTipText(Language.getWord("LSET"));

  MouseListener mouseLB = new MouseAdapter() {

       public void mousePressed(MouseEvent e) {

          if(queryB.isEnabled()) {

              start = 0; 
              limit = 50;
              currentPage = 1;
              queryB.setEnabled(false);
              queryLeft.setEnabled(false);

              if (!queryRight.isEnabled()) {

                  queryRight.setEnabled(true);
                  queryF.setEnabled(true);
               }

              indexMin = 1;
              indexMax = 50;

              String sql = "SELECT " + recordFilter + " FROM \"" + currentTable + "\" ORDER BY " + orderBy + " LIMIT 50 OFFSET 0";

              if (where.length()!=0)

                  sql = "SELECT * FROM ("
                         + "SELECT " + recordFilter + " FROM \"" + currentTable + "\"" + where + ") AS foo ORDER BY " + orderBy + " LIMIT 50"
                         + " OFFSET 0";


              Vector res = connReg.TableQuery(sql);
              Vector col = connReg.getTableHeader();

              addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

              if (!connReg.queryFail()) {

                  addTextLogMonitor(Language.getWord("RES") + "OK");
                  showQueryResult(res,col);
                  updateUI();
               }
           }
        }
  };

  queryB.addMouseListener(mouseLB);

  MouseListener mouseLQL = new MouseAdapter() {

       public void mousePressed(MouseEvent e) {

          if (queryLeft.isEnabled()) {

              currentPage--;

              if (currentPage == 1) {

                  queryLeft.setEnabled(false);
                  queryB.setEnabled(false);

                  indexMin = 1;
                  indexMax = 50;
               }
              else {
                    if (currentPage == nPages - 1)
                        indexMax = indexMin - 1;
                    else
                        indexMax -= 50;

                    indexMin -= 50;
               }

              if (!queryRight.isEnabled()) {

                  queryRight.setEnabled(true);
                  queryF.setEnabled(true);
               }


              start = indexMin - 1;
              limit = 50;

              String sql = "SELECT " + recordFilter + " FROM \"" + currentTable + "\" ORDER BY " + orderBy + " LIMIT 50 OFFSET " + start;

              if (where.length()!=0)

                  sql = "SELECT * FROM ("
                         + "SELECT " + recordFilter + " FROM \"" + currentTable + "\"" + where + ") AS foo ORDER BY " + orderBy + " LIMIT 50"
                         + " OFFSET " + start;

              Vector res = connReg.TableQuery(sql);
              Vector col = connReg.getTableHeader();

              addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

              if (!connReg.queryFail()) {

                  addTextLogMonitor(Language.getWord("RES") + "OK");
                  showQueryResult(res,col);
                  updateUI();
               }
           }
        }
  };

  queryLeft.addMouseListener(mouseLQL);

  MouseListener mouseLQR = new MouseAdapter() {

       public void mousePressed(MouseEvent e) {

          if (queryRight.isEnabled()) {

              currentPage++;
              start = indexMax;

              if (currentPage > 1) {

                  if (!queryLeft.isEnabled()) {

                      queryLeft.setEnabled(true);
                      queryB.setEnabled(true);
                   }
               }

              int downLimit = 1;

              if (currentPage == nPages) {
                  indexMax = recIM;
                  downLimit = (nPages-1) * 50 + 1;
                  queryRight.setEnabled(false);
                  queryF.setEnabled(false);
               }

              int diff = (indexMax - downLimit) + 1;

              if (diff > 50)
                  diff = 50;

              limit = diff;

              String sql = "SELECT " + recordFilter + " FROM \"" + currentTable + "\" ORDER BY " + orderBy + " LIMIT " + diff + " OFFSET " + start;

              if (where.length()!=0)
                  
                  sql = "SELECT * FROM (" 
                         + "SELECT " + recordFilter + " FROM \"" + currentTable + "\"" + where + ") AS foo ORDER BY " + orderBy + " LIMIT " 
                         + diff + " OFFSET " + start; 

              Vector res = connReg.TableQuery(sql);
              Vector col = connReg.getTableHeader();

              addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

              indexMin += 50;
              indexMax += 50;

              if (!connReg.queryFail()) {

                  addTextLogMonitor(Language.getWord("RES") + "OK");
                  showQueryResult(res,col);
                  updateUI();
               }
           }
        }
  };

  queryRight.addMouseListener(mouseLQR);

  MouseListener mouseLF = new MouseAdapter() {

       public void mousePressed(MouseEvent e) {

          if (queryF.isEnabled()) {

              currentPage = nPages;
              queryRight.setEnabled(false);
              queryF.setEnabled(false);

              if (!queryLeft.isEnabled()) {

                  queryLeft.setEnabled(true);
                  queryB.setEnabled(true);
               }

              indexMin = ((nPages-1) * 50); 
              start = indexMin;
              limit = 50;

              indexMin++;

              String sql = "SELECT " + recordFilter + " FROM \"" + currentTable + "\" ORDER BY " + orderBy + " LIMIT 50 OFFSET " + start;

              if (where.length()!=0)

                  sql = "SELECT * FROM ("
                         + "SELECT " + recordFilter + " FROM \"" + currentTable + "\"" + where + ") AS foo ORDER BY " + orderBy + " LIMIT 50"
                         + " OFFSET " + start;

              Vector res = connReg.TableQuery(sql);
              Vector col = connReg.getTableHeader();

              addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

              if (!connReg.queryFail()) {

                  addTextLogMonitor(Language.getWord("RES") + "OK");
                  showQueryResult(res,col);
                  updateUI();
               }
           }
        }
  };

  queryF.addMouseListener(mouseLF);

  recordsButtons.add(queryB);
  recordsButtons.add(new JPanel());
  recordsButtons.add(queryLeft);
  recordsButtons.add(new JPanel());
  recordsButtons.add(queryRight);
  recordsButtons.add(new JPanel());
  recordsButtons.add(queryF);
  recordsButtons.add(new JPanel());

  currentStatistic.setHorizontalAlignment(JTextField.CENTER);
  currentStatistic.setEditable(false);

  JPanel dataStat = new JPanel();
  dataStat.setLayout(new BorderLayout());

  dataStat.add(recordsControl,BorderLayout.NORTH);
  dataStat.add(currentStatistic,BorderLayout.CENTER);
  dataStat.add(recordsButtons,BorderLayout.SOUTH);

  TitledBorder title1 = BorderFactory.createTitledBorder(etched1);
  dataStat.setBorder(title1);

  top.add(new JPanel(),BorderLayout.CENTER); 
  top.add(dataStat);
  top.setBorder(title1);

  add(top,BorderLayout.NORTH); 

  setLabel("","","");
  showQueryResult(new Vector(),new Vector());
  add(StructureBar,BorderLayout.WEST);
  pieDatos();
  add(firstPanel,BorderLayout.SOUTH);
  setSize(500,500);
}

/******************** METODO Filter() : Panel Filtro de Inf. ********************/

public void Filter() {

  base = new JPanel();
  base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));
  JPanel row1 = new JPanel();
  row1.setLayout(new FlowLayout(FlowLayout.LEFT));
  JPanel row2 = new JPanel();
  row2.setLayout(new FlowLayout(FlowLayout.LEFT));
  CheckBoxListener myListener = new CheckBoxListener();
  String[] datmp = {""};

  rectangle = new JCheckBox(Language.getWord("FILTER")+":");
  rectangle.setMnemonic('F'); 
  rectangle.addItemListener(myListener);

  rectangle2 = new JCheckBox(Language.getWord("LIMIT")+":");
  rectangle2.setMnemonic('L'); 
  rectangle2.addItemListener(myListener);

  combo1 = new JComboBox(datmp);
  combo2 = new JComboBox(datmp);
  combo3 = new JTextField(10);

  combo1.setActionCommand("COMBO1");
  combo1.addActionListener(this);
  combo2.setActionCommand("COMBO2");
  combo2.addActionListener(this);

  JPanel space = new JPanel();

  advanced = new JButton(Language.getWord("OPC"));
  advanced.setActionCommand("Options");
  advanced.addActionListener(this);

  JMenuItem Item = new JMenuItem(Language.getWord("DSPLY"));
  Item.setActionCommand("DISPLAY");
  Item.addActionListener(this);
  popup.add(Item);

  Item = new JMenuItem(Language.getWord("ADF")); 
  Item.setActionCommand("ADVANCED");
  Item.addActionListener(this);
  popup.add(Item);

  Item = new JMenuItem(Language.getWord("CUF")); 
  Item.setActionCommand("CUSTOMIZE");
  Item.addActionListener(this);
  popup.add(Item);

  MouseListener mouseListener = new MouseAdapter() {
	public void mousePressed(MouseEvent e) {
         if (!popup.isVisible() && advanced.isEnabled())
               popup.show(advanced,90,0);
          }
  };
  advanced.addMouseListener(mouseListener);

  button = new JButton(Language.getWord("UPDT"));
  button.setActionCommand("REFRESH");
  button.addActionListener(this);

  JPanel row0 = new JPanel();
  row0.setLayout(new FlowLayout(FlowLayout.CENTER));
  row0.add(button);
  row0.add(advanced);

  row1.add(rectangle); 
  row1.add(combo1);
  row1.add(combo2);
  row1.add(combo3);
  row1.add(space);

  numRegTextField = new JTextField(7);
  men1 = new JLabel(Language.getWord("STARTR")+":"); 
  limitText = new JTextField(7);
  men2 = new JLabel(Language.getWord("LRW"));

  row2.add(rectangle2);
  row2.add(men1);
  row2.add(numRegTextField);
  row2.add(men2);
  row2.add(limitText);

  setRow1(false);
  setRow2(false);

  JPanel right = new JPanel();
  right.setLayout(new BorderLayout());
  right.add(row2,BorderLayout.WEST);

  JPanel groupPanel = new JPanel();
  groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
  groupPanel.add(row1);
  groupPanel.add(right);

  Border etched1 = BorderFactory.createEtchedBorder();
  TitledBorder title1 = BorderFactory.createTitledBorder(etched1);

  groupPanel.setBorder(title1);

  base.add(row0);
  base.add(groupPanel); 
}

/******************** METODO pieDatos() : Texto de Operacion ********************/
public void pieDatos() {

   firstPanel = new JPanel();
   firstPanel.setLayout(new BorderLayout());
   Filter();
   firstPanel.add(base,BorderLayout.CENTER);
}

/******************** METODO CreateToolBar() : Crea Barra de Iconos ********************/
public void CreateToolBar() {
 
  URL imgURL = getClass().getResource("/icons/16_InsertRecord.png"); 
  insertRecord = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  insertRecord.setActionCommand("INSERT-RECORD");
  insertRecord.addActionListener(this);
  insertRecord.setToolTipText(Language.getWord("INSREC"));
  StructureBar.add(insertRecord);

  imgURL = getClass().getResource("/icons/16_DelRecord.png");
  delRecord = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  delRecord.setActionCommand("DELETE-RECORD");
  delRecord.addActionListener(this);
  delRecord.setToolTipText(Language.getWord("DELREC"));
  delRecord.setEnabled(false);
  StructureBar.add(delRecord);

  imgURL = getClass().getResource("/icons/16_UpdateRecord.png");
  updateRecord = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  updateRecord.setActionCommand("UPDATE-RECORD");
  updateRecord.addActionListener(this);
  updateRecord.setToolTipText(Language.getWord("UPDREC"));
  updateRecord.setEnabled(false);
  StructureBar.add(updateRecord);

  imgURL = getClass().getResource("/icons/16_ExportFile.png");
  exportFile = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  exportFile.setActionCommand("EXPORT-TO-FILE");
  exportFile.addActionListener(this);
  exportFile.setToolTipText(Language.getWord("EXPORTAB") + "/" + Language.getWord("ITT"));
  exportFile.setEnabled(false);
  StructureBar.add(exportFile);

  imgURL = getClass().getResource("/icons/16_NewTable.png");
  reportBut = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  reportBut.setActionCommand("EXPORT-REPORT");
  reportBut.addActionListener(this);
  reportBut.setToolTipText(Language.getWord("EXPORREP"));
  reportBut.setEnabled(false);
  StructureBar.add(reportBut);
    
}

/******************** METODO actionPerformed() : Manejador de Eventos ********************/
public void actionPerformed(java.awt.event.ActionEvent e) {


if (e.getActionCommand().equals("UPDATE-POP")) {

    updatingRecords();
    return;
 }

if (e.getActionCommand().equals("DELETE-POP")) {

    dropRecords();
    return;
 }

if (e.getActionCommand().equals("INSERT-RECORD")) {

    insertRecords();
    return;
 }
    
 if (e.getActionCommand().equals("DELETE-RECORD") ) {

    table.clearSelection();
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    DropTableRecord Eraser = new DropTableRecord(realTableName,tableStruct,frame);
    Eraser.setLocationRelativeTo(frame);
    Eraser.setVisible(true);

    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    if (Eraser.isWellDone()) {

        addTextLogMonitor(Language.getWord("EXEC")+ Eraser.getSQL() + "\"");
        String result = connReg.SQL_Instruction(Eraser.getSQL());

        if (result.equals("OK")) {
            result = refreshAfterDrop(result);
        }
       else {
              result = result.substring(0,result.length()-1);
              ErrorDialog showError = new ErrorDialog(new JDialog(),connReg.getErrorMessage());
              showError.pack();
              showError.setLocationRelativeTo(frame);

              showError.show();
        }

       addTextLogMonitor(Language.getWord("RES") + result);
    }

   return;
 }

 if (e.getActionCommand().equals("UPDATE-RECORD") ) {

     table.clearSelection();
     setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

     UpdateTable upper = new UpdateTable(realTableName,tableStruct,frame);
     setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

     if (upper.getResult()) {

         addTextLogMonitor(Language.getWord("EXEC")+ upper.getUpdate() + "\"");
         String result = connReg.SQL_Instruction(upper.getUpdate());

         if (result.equals("OK")) {

             String sql = "SELECT " + recordFilter + " FROM " + realTableName + " ORDER BY " + orderBy ;

             if (where.length()!=0)
                 sql = "SELECT " + recordFilter + " FROM " + realTableName + where;

             recIM = Count(realTableName,connReg,sql,true);

             if (recIM > 50) 
                 sql = "SELECT * FROM (" + sql + ") AS foo ORDER BY " + orderBy + " LIMIT " + limit + " OFFSET " + start;

             Vector res = connReg.TableQuery(sql);
             Vector col = connReg.getTableHeader();

             if (!connReg.queryFail()) {
                 showQueryResult(res,col);
                 updateUI(); 
              }    
          }
         else {
                result = result.substring(0,result.length()-1);
                ErrorDialog showError = new ErrorDialog(new JDialog(),connReg.getErrorMessage());
                showError.pack();
                showError.setLocationRelativeTo(frame);

                showError.show();
          }

         addTextLogMonitor(Language.getWord("RES") + result);
     }

    return;
 }

 if (e.getActionCommand().equals("EXPORT-TO-FILE")) {

     ExportToFile dialog = new ExportToFile(frame,numReg);

     int option = 0;

     if (dialog.isWellDone()) {

         option = dialog.getOption();
         String s = "file:" + System.getProperty("user.dir");
         File file;
         boolean Rewrite = true;
         String FileName = "";
         int returnVal;
         JFileChooser fc;

         switch(option) {

                case 1: 
                        fc = new JFileChooser(s);

                        returnVal = fc.showDialog(frame,Language.getWord("EXPORTAB"));

                        if (returnVal == JFileChooser.APPROVE_OPTION) {

                            file = fc.getSelectedFile();
                            FileName = file.getAbsolutePath(); // Camino Absoluto

                            if (file.exists()) {

                                GenericQuestionDialog win = new GenericQuestionDialog(frame,Language.getWord("YES"),
                                                                Language.getWord("NO"),Language.getWord("ADV"),
                                                                Language.getWord("FILE") + " \"" + FileName 
                                                                + "\" " + Language.getWord("SEQEXIS2") + " " 
                                                                + Language.getWord("OVWR"));
                                Rewrite = win.getSelecction();
                             }

                            if (Rewrite) {

                                try {
                                      ExportSeparatorField little = new ExportSeparatorField(frame);
      
                                      if (little.isDone()) {

                                          String limiter = little.getLimiter();
                                          PrintStream saveFile = new PrintStream(new FileOutputStream(FileName));
                                          String sentence = "SELECT * FROM " + realTableName + " ORDER BY " + orderBy;
                                          Vector resultGlobal = connReg.TableQuery(sentence);
                                          Vector columnNamesG = connReg.getTableHeader();
                                          String val = "OK";

                                          if (connReg.queryFail()) {
                                              val = connReg.problem;
                                              val = val.substring(0,val.length()-1);
                                           }

                                          addTextLogMonitor(Language.getWord("EXEC") + sentence + "\"");
                                          addTextLogMonitor(Language.getWord("RES") + val);
                                          printFile(saveFile,resultGlobal,columnNamesG,limiter);
                                       } // fin if

                                 } // fin try
                               catch (Exception ex) { System.out.println("Error: " + ex); }
                             } // fin if
                         } // fin if 
                        return;

                case 2:
                        fc = new JFileChooser(s);

                        returnVal = fc.showDialog(frame,Language.getWord("LFILE"));

                        if (returnVal == JFileChooser.APPROVE_OPTION) {

                            file = fc.getSelectedFile();
                            FileName = file.getAbsolutePath(); // Camino Absoluto

                            ImportSeparatorField little = new ImportSeparatorField(frame);

                            if (little.isDone()) {

                                String limiter = little.getLimiter();

                                try {
                                      BufferedReader in = new BufferedReader(new FileReader(file));
                                      String firstReg = in.readLine(); 
                                      Vector data = new Vector();
                                      int index = firstReg.indexOf(limiter);

                                      if (index != -1) {

                                          StringTokenizer filter = new StringTokenizer(firstReg,limiter);
                                          int i = 0;
                                          Vector tuple = new Vector();

                                          while (filter.hasMoreTokens()) {
                                                 i++;
                                                 String tmp = filter.nextToken();
                                                 tuple.addElement(tmp);
                                           } 

                                          int k = tableStruct.getTableHeader().getNumFields();

                                          if (i == k) {
                                              data.addElement(tuple); 

                                          while (true) {

                                                 String line = in.readLine();

                                                 if (line == null) 
                                                     break;

                                                 StringTokenizer filterFile = new StringTokenizer(line,limiter);
                                                 int counter = 0;
                                                 tuple = new Vector();

                                                 while (filterFile.hasMoreTokens()) {

                                                        counter += 1;
                                                        String tmp = filterFile.nextToken();
                                                        tuple.addElement(tmp);
                                                  }

                                                 data.addElement(tuple);
                                          }
             
                                         BuildSQLRecords(currentTable,tableStruct.getTableHeader(),data);
                                      }
                                    else {

                                          JOptionPane.showMessageDialog(new JDialog(),
                                          Language.getWord("NCNNA"),
                                          Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                                          return;
                                     }
                                  } 
                                else {
                                       JOptionPane.showMessageDialog(new JDialog(),
                                       Language.getWord("SEPNF"),
                                       Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                                       return;
                                 }
                             } 
                           catch (Exception ex) {
                                   System.out.println("Error" + ex);
                            }
                         }
                     }
                    return;
                default:
              }
    }

   return;
  }

 if (e.getActionCommand().equals("EXPORT-REPORT")) {

     Vector dataRecords = new Vector();
     Vector columns = new Vector();

     if (totalReg <= 50) {

         addTextLogMonitor(Language.getWord("EXEC") + "SELECT " + recordFilter + " FROM " + realTableName + ";\"");
         dataRecords = connReg.TableQuery("SELECT " + recordFilter + " FROM " + realTableName);
         columns = connReg.getTableHeader();

         String str = "OK";

         if (!connReg.queryFail()) {

             addTextLogMonitor(Language.getWord("RES") + str); 
             ReportDesigner format = new ReportDesigner(frame,columns,dataRecords,LogWin,currentTable,connReg);
          }
         else {
                str = connReg.getProblemString().substring(0,connReg.getProblemString().length()-1);
                addTextLogMonitor(Language.getWord("RES") + str);
          }
         
 
         return;
      }

     ExportToReport dialog = new ExportToReport(frame);
     int option = 0;

     if (dialog.isWellDone()) {

         option = dialog.getOption();

         String sql = "SELECT " + recordFilter + " FROM " + realTableName;

         switch (option) {

                case 1: if (where.length()>0) 
                            sql += where;  
                        sql = "SELECT * FROM (" + sql + ") AS foo ORDER BY " + orderBy + " LIMIT " + limit + " OFFSET " + start; 
                        break;

                case 2: sql += where;
                        break;

                case 3: sql += " ORDER BY " + orderBy;
          }

         addTextLogMonitor(Language.getWord("EXEC") + sql + ";\"");

         dataRecords = connReg.TableQuery(sql);
         columns = connReg.getTableHeader();

         String str = "OK";

         if (!connReg.queryFail()) {

             addTextLogMonitor(Language.getWord("RES") + str);
             ReportDesigner format = new ReportDesigner(frame,columns,dataRecords,LogWin,currentTable,connReg);
          }
         else {
               str = connReg.getProblemString().substring(0,connReg.getProblemString().length()-1);
               addTextLogMonitor(Language.getWord("RES") + str);

               ErrorDialog showError = new ErrorDialog(new JDialog(),connReg.getErrorMessage());
               showError.pack();
               showError.setLocationRelativeTo(frame);

               showError.show();
          }
      }

     return;
  }

 if (e.getActionCommand().equals("COMBO1")) {

     JComboBox cb = (JComboBox)e.getSource();
     int pos = cb.getSelectedIndex();

     if (pos < 0)
         pos = 0;
 
     field = (String) columnNamesVector.elementAt(pos);

    if (field == null || field.length() == 0) {

        field = firstField; 
        pos = columnNamesVector.indexOf(firstField);
        cb.setSelectedIndex(pos);
     }

    if (!field.equals("oid")) {

        String type = tableStruct.getTableHeader().getType(field);

        String[] varcharOpc = {"=","!=","<",">","<=",">=","like","not like","~","~*","!~","!~*"};
        String[] boolOpc = {"=","!="};
        String[] intOpc = {"=","!=","<",">","<=",">="};

        combo2.removeAllItems();

        if (type.startsWith("bool")) {

            for (int i=0;i<boolOpc.length;i++)
                 combo2.addItem(boolOpc[i]);

            return;
         }

        if (type.startsWith("int") || type.startsWith("serial") || type.startsWith("smallint")
            || type.startsWith("real") || type.startsWith("double") || type.startsWith("float")) {

            for (int i=0;i<intOpc.length;i++)
                 combo2.addItem(intOpc[i]);

            return;
         }

        for (int i=0;i<varcharOpc.length;i++)
             combo2.addItem(varcharOpc[i]);

     }
    else {
           combo2.removeAllItems();
           String[] ig = {"=","!=","<",">","<=",">="};

           for (int i=0;i<ig.length;i++)
                combo2.addItem(ig[i]);
     }

   return;
  } 

 if (e.getActionCommand().equals("COMBO2")) {

     JComboBox cb = (JComboBox)e.getSource();
     operator = (String) cb.getSelectedItem();

     return;
  }

 if (e.getActionCommand().equals("DISPLAY")) {

     boolean keepOn = false;
     Hashtable hashTmp = new Hashtable();

     if (hashDB.containsKey(connReg.getDBname())) {

         hashTmp = (Hashtable) hashDB.get(connReg.getDBname());

         if (hashTmp.containsKey(currentTable)) { 

             keepOn = true;
             recordFilter = "\"oid\"," + (String) hashTmp.get(currentTable); 
          }
         else 
             recordFilter = "\"oid\"," + "*";
      }
     else 
         recordFilter = "\"oid\"," + "*";

     if (rectangle2.isSelected())
         rectangle2.setSelected(false);

     if (rectangle.isSelected())
         rectangle.setSelected(false);

     DisplayControl regListPanel = new DisplayControl(tableStruct,frame,recordFilter,keepOn);

     if (regListPanel.isWellDone()) {

         recordFilter = regListPanel.getFilter();

         hashTmp.remove(currentTable);
         hashDB.remove(connReg.getDBname());

         if (recordFilter.length() == 0) {
             recordFilter = "\"oid\"," + "*";
             hashDB.put(connReg.getDBname(),hashTmp);
          }
         else {
               recordFilter = "\"oid\"," + recordFilter;

               if (regListPanel.isKeepIt()) { 

                   hashTmp.put(currentTable,recordFilter);
                   hashDB.put(connReg.getDBname(),hashTmp);
                }
               else
                   hashDB.put(connReg.getDBname(),hashTmp);

               refreshTable();
         }
        
    }

    return;
  }

 if (e.getActionCommand().equals("ADVANCED")) {

     if (rectangle2.isSelected())
         rectangle2.setSelected(false);

     if (rectangle.isSelected())
         rectangle.setSelected(false);

     setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

     AdvancedFilter button = new AdvancedFilter(tableStruct,frame);
     setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

     if (button.isWellDone()) {

         sentence = button.getSelect();
 
         if (button.isThereOrder())
             orderBy = button.getOrder();

         recIM = Count(currentTable,connReg,sentence,true);

         if (recIM > 50)
             sentence = "SELECT * FROM (" + sentence + ") AS foo ORDER BY " + orderBy + " LIMIT 50 OFFSET " + start;

         Vector res = connReg.TableQuery(sentence);
         Vector col = connReg.getTableHeader();

         addTextLogMonitor(Language.getWord("EXEC") + sentence + ";\"");
         String str = "OK";

         if (!connReg.queryFail()) {

             showQueryResult(res,col);
             updateUI(); 
          }
         else {
               str = connReg.getProblemString().substring(0,connReg.getProblemString().length()-1);
               ErrorDialog showError = new ErrorDialog(new JDialog(),connReg.getErrorMessage());
               showError.pack();
               showError.setLocationRelativeTo(frame);

               showError.show();
          }     

         addTextLogMonitor(Language.getWord("RES") + str);
      }

      return;
 } 

 if (e.getActionCommand().equals("CUSTOMIZE")) {

     if (rectangle2.isSelected())
         rectangle2.setSelected(false);

     if (rectangle.isSelected())
         rectangle.setSelected(false);

     CustomizeFilter custim = new CustomizeFilter(tableStruct,frame);

     if (custim.isWellDone()) {

         sentence = custim.getSelect();
         String order = custim.getOrder();

         if (order.length() > 0)
             orderBy = order;

         recIM = Count(currentTable,connReg,sentence,true);

         if (recIM > 50)
             sentence = "SELECT * FROM (" + sentence + ") AS foo ORDER BY " + orderBy + " LIMIT 50 OFFSET " + start;

         Vector res = connReg.TableQuery(sentence);
         Vector col = connReg.getTableHeader();
         String str = "OK";
         addTextLogMonitor(Language.getWord("EXEC") + sentence + ";\"");

         if (!connReg.queryFail())  {

             showQueryResult(res,col);
             updateUI(); 
          }    
         else {
               str = connReg.problem.substring(0,connReg.problem.length()-1);
               ErrorDialog showError = new ErrorDialog(new JDialog(),connReg.getErrorMessage());
               showError.pack();
               showError.setLocationRelativeTo(Records.this);
               showError.show();
          }

         addTextLogMonitor(Language.getWord("RES") + str);
     }

    return;
  }

 if (e.getActionCommand().equals("REFRESH")) {

     refreshOn = true;
     sentence = "SELECT " + recordFilter + " FROM \"" + currentTable + "\"";
     where = "";

     if (rectangle.isSelected()) {

         String var = combo3.getText();

         if (var.length()==0) {

             JOptionPane.showMessageDialog(frame,                               
             Language.getWord("ERRFIL"),                       
             Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

             return;
          }
        else 
          {
             String type = "int";

             if (!field.equals("oid"))
                 type = tableStruct.getTableHeader().getType(field);

             int code = getTypeCode(type);

             switch (code) {
                        case 1:
                                if (!var.startsWith("'"))
                                    var = "'" + var;

                                if (!var.endsWith("'"))
                                    var = var + "'";

                                combo3.setText(var);
                                break;
                        case 2:
                                if (!isNum(var)) {
                                    JOptionPane.showMessageDialog(frame,
                                    Language.getWord("FINTIV"),
                                    Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

                                    combo3.setText("");
                                    combo3.requestFocus();

                                    return;
                                 }
                                break;
                        case 3:
                                var = var.toLowerCase();
                                if (!var.equals("true") && !var.equals("false")) {
                                    JOptionPane.showMessageDialog(frame,
                                    Language.getWord("IBT"),
                                    Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                                    
                                    combo3.setText("");
                                    combo3.requestFocus();

                                    return;
                                 }
                 } // fin switch

            sentence += " WHERE \"" + field + "\" " + operator + " " + var;
            where += " WHERE \"" + field + "\" " + operator + " " + var;

          } // fin else
     }

    sentence += " ORDER BY " + orderBy;
    where += " ORDER BY " + orderBy;

    if (rectangle2.isSelected()) {

        int fail = 0;
        boolean firstvalid = false;
        boolean secondvalid = false;
        String num = numRegTextField.getText();
        String limit = limitText.getText();

        if (num.length()>0) {

            if (isNum(num))
                firstvalid = true;
            else {
                  JOptionPane.showMessageDialog(frame,                               
                  Language.getWord("ERRLIM") + " 1 " + Language.getWord("ERRLIM2"),                       
                  Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                  return;
            }
         }
        else
          fail += 1;

        if (limit.length()>0) {

            if (isNum(limit))
                secondvalid = true;
            else {
                  JOptionPane.showMessageDialog(frame,                               
                  Language.getWord("ERRLIM") + " 2 " + Language.getWord("ERRLIM2"),                       
                  Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                  return;
             }
         }
        else
          fail += 1;

        if (fail==2) {

            JOptionPane.showMessageDialog(frame,                               
            Language.getWord("LIMUS"),                       
            Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
            return;
      	 }
        else {

              if (fail==1) {

                  JOptionPane.showMessageDialog(frame,                               
                  Language.getWord("LIM1US"),                       
                  Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                  return;
               }
              else
                 if (firstvalid && secondvalid) {

                     int a = Integer.parseInt(num);
                     int b = Integer.parseInt(limit);

                     if (a <= b) {
                         int numrows = (b - a) + 1;
                         sentence += " LIMIT " + numrows + " OFFSET " + num;
                         where += " LIMIT " + numrows + " OFFSET " + num;
                      }
                     else {
                           JOptionPane.showMessageDialog(frame,                               
                           Language.getWord("MORELIM"),                       
                           Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                           return;
                      } // fin else
                 } // fin if
           } // fin else
    
     }

    recIM = Count(currentTable,connReg,sentence,true);

    if (recIM > 50) 
        sentence = "SELECT * FROM (" + sentence + ") AS foo ORDER BY " + orderBy + " LIMIT 50";

    sentence += ";";

    Vector res = connReg.TableQuery(sentence);
    addTextLogMonitor(Language.getWord("EXEC") + sentence + "\"");
    Vector col = connReg.getTableHeader();

    if (!connReg.queryFail()) {

         showQueryResult(res,col);
         updateUI(); 
     }
    else {
          String resStr = connReg.getProblemString();
          addTextLogMonitor(Language.getWord("ERRONRUN") + resStr.substring(0,resStr.length()-1));
     }
  }
}

 /**
  * METODO activeInterface()  
  * Activa o desactiva los Botones
  */ 
 public void activeInterface(boolean value) {

   insertRecord.setEnabled(value);
   updateRecord.setEnabled(value);
   rectangle.setEnabled(value);
   rectangle2.setEnabled(value);
   button.setEnabled(value);
   exportFile.setEnabled(value);

   if (!value) {

       connected = false;
       title.setText(Language.getWord("DSCNNTD"));
       showQueryResult(new Vector(),new Vector());
       hashDB.clear();
       advanced.setEnabled(false);

       onMem.setText(" " + Language.getWord("ONMEM") + " : 0 "); 
       onScreen.setText(" " + Language.getWord("ONSCR") + " : 0 ");
       onTable.setText(" " + Language.getWord("TOTAL") + " : 0 ");
       currentStatistic.setText(" " + Language.getWord("DSCNNTD") + " "); 
 
       if (queryB.isEnabled()) {

           queryB.setEnabled(false);
           queryLeft.setEnabled(false);
        }

       if (queryF.isEnabled()) {

           queryRight.setEnabled(false);
           queryF.setEnabled(false);
        }
    }
   else
       connected = true;
 }                                                                              

 public void setLabel(String dbName,String table,String owner) {

   currentTable = table;
   String mesg = "";

   if (dbName.length()>0) {

       String regTitle = Language.getWord("RECS");
       mesg = Language.getWord("TABLE") + ": '" + table + "' [Owner: " + owner + " / DB:" + dbName + "]";  
    }
   else
       mesg = Language.getWord("NOSELECT");

   title.setText(mesg);
  }

 public void activeBox(boolean state) {

   rectangle.setEnabled(state);
   rectangle2.setEnabled(state);
   advanced.setEnabled(state);
  }

 public void setRecordFilter(String TableN,String DBName) {

    if (hashDB.containsKey(DBName)) {

        hashRecordFilter = (Hashtable) hashDB.get(DBName);
        recordFilter = (String) hashRecordFilter.get(TableN);

        if (recordFilter == null)
            recordFilter = "\"oid\"," + "*";
     }
    else
        recordFilter = "\"oid\"," + "*";
  }

 public boolean updateTable(PGConnection conn, String TableN, Table structT) {

    realTableName = "\"" + TableN + "\"";

    if (conn.gotUserSchema(TableN)) { 
        realTableName = conn.getSchemaName(TableN) + "." + realTableName;
     }

    orderBy = "oid";

    if (queryB.isEnabled()) {

        queryB.setEnabled(false);
        queryLeft.setEnabled(false);
     }

    if (hashDB.containsKey(conn.getDBname())) {

        hashRecordFilter = (Hashtable) hashDB.get(conn.getDBname());
        recordFilter = (String) hashRecordFilter.get(TableN);

        if (recordFilter == null)
            recordFilter = "\"oid\",*";
     }
    else
      recordFilter = "\"oid\",*";

    where = "";

    String sentence = "SELECT " + recordFilter + " FROM " + realTableName + " ORDER BY " + orderBy + ";";

    recIM = Count(realTableName,conn,"",false);

    if (recIM == -1)
        return false;

    if (recIM > 50) {

        sentence = "SELECT " + recordFilter + " FROM " + realTableName + " ORDER BY " + orderBy + " LIMIT 50;";
        indexMax = 50;
        indexMin = 1;
     }

    currentPage = 1;

    connReg = conn;
    tableStruct = structT;
    //currentTable = TableN;
    currentTable = realTableName;
    rectangle.setSelected(false);
    rectangle2.setSelected(false);
    combo3.setText("");
    numRegTextField.setText("");
    limitText.setText("");

    String answer = "OK";
    Vector result = connReg.TableQuery(sentence);
    columnNamesVector = connReg.getTableHeader();

    if (!connReg.queryFail()) {

        String owner = connReg.getOwner(TableN);
        setLabel(connReg.getDBname(),TableN,owner);

        if (result.size()==0) 
            activeBox(false);
        else { 
              activeBox(true);
              button.setEnabled(true);
          }

        combo1.removeAllItems();
        firstField = "";

        for (int t=0;t<columnNamesVector.size();t++) {

             String element = (String) columnNamesVector.elementAt(t);

             if (element.length() > 25)
                 element = element.substring(0,25) + "...";

             combo1.insertItemAt(element, t); 

             if (t == 0 && element != null && element.length()>0)
                 firstField = element; 

             if ((firstField == null) || (element.length()==0))
                 firstField = element;
         }

        combo1.setSelectedIndex(0);

        showQueryResult(result,columnNamesVector);
        insertRecord.setEnabled(true);
        exportFile.setEnabled(true);
       }
      else {
            rectangle.setEnabled(false);
            rectangle2.setEnabled(false);
            insertRecord.setEnabled(false);
            combo1.removeAllItems();
            combo1.insertItemAt("",0);
            showQueryResult(new Vector(),new Vector());
            title.setText(Language.getWord("NRE"));

            answer = connReg.getProblemString().substring(0,connReg.getProblemString().length()-1);

            ErrorDialog showError = new ErrorDialog(new JDialog(), conn.getErrorMessage());
            showError.pack();
            showError.setLocationRelativeTo(frame);
            showError.show();
        }

   addTextLogMonitor(Language.getWord("EXEC")+ sentence + "\"");
   addTextLogMonitor(Language.getWord("RES") + answer);
   updateUI();

   return true;
  }


public void showQueryResult(Vector rowData,Vector columnNames) {

   int resultSize = rowData.size();

   boolean flag = false;

   if (oldMem != recIM) {

       onMem.setText(" " + Language.getWord("ONMEM") + " : " + recIM + " ");
       oldMem = recIM;
       flag = true;
    }

   if (recIM == 0) { 

       nPages = 1;
       indexMin = 0;
       indexMax = 0;

       if (queryRight.isEnabled()) {

           queryRight.setEnabled(false);
           queryF.setEnabled(false);
        }

       if (queryLeft.isEnabled()) {

           queryLeft.setEnabled(false);
           queryB.setEnabled(false);
        }
    }
   else {
         if (recIM <= 50) {

             nPages = 1;
             indexMin = 1; 
             indexMax = recIM;

             if (queryRight.isEnabled()) {

                 queryRight.setEnabled(false);
                 queryF.setEnabled(false);
              }

             if (queryLeft.isEnabled()) {

                 queryLeft.setEnabled(false);
                 queryB.setEnabled(false);
              }
          }
         else {

               nPages = getPagesNumber(recIM);

               if (nPages == 1 && recIM != 0)
                   indexMax = recIM;

               if (nPages == currentPage) {

                   indexMax = recIM;

                   if (queryRight.isEnabled()) {

                       queryRight.setEnabled(false);
                       queryF.setEnabled(false);
                    }
                }
               else {
                      if (nPages > currentPage) {

                          if (nPages > 1) {

                              if (!queryRight.isEnabled()) {

                                  queryRight.setEnabled(true);
                                  queryF.setEnabled(true);
                               }

                              if (indexMax < 50)
                                  indexMax = 50;
                           }
                          else {

                                 if (queryRight.isEnabled()) {

                                     queryRight.setEnabled(false);
                                     queryF.setEnabled(false);
                                  }
                           }
                       }
                }
         }
   }

   if (oldPage != currentPage || !oldTable.equals(currentTable) || flag || refreshOn ) {

       if (refreshOn) { 

           refreshOn = false;
           currentPage = 1;

           if (recIM > 50) {

               indexMax = 50;
               indexMin = 1;

               if (!queryRight.isEnabled()) {

                   queryRight.setEnabled(true);
                   queryF.setEnabled(true);
                }
            }
           else {
                  if (queryRight.isEnabled()) {

                      queryRight.setEnabled(false);
                      queryF.setEnabled(false);
                   }

                  if (recIM == 0) {
                      indexMax = indexMin = 0;
                   }
                  else {
                         indexMin = 1;
                         indexMax = recIM;
                   }
            }

           if (queryLeft.isEnabled()) {

               queryLeft.setEnabled(false);
               queryB.setEnabled(false);
            }
        }

       flag = false;

       if (!oldTable.equals(currentTable)) {

           oldTable = currentTable;
           where = "";

           if (recIM > 0) {

               indexMin = 1;

               if (recIM <= 50) {
                   indexMax = recIM;
                   nPages = 1;
                }
               else {
                      indexMax = 50;
                }
            }
           else {
                 indexMin = 0;
                 indexMax = 0;
                 nPages = 1;
            }

           currentPage = 1;
        }

       currentStatistic.setText(" " + Language.getWord("PAGE") + " " + currentPage + " " + Language.getWord("OF")
                                 + " " + nPages + " [ " + Language.getWord("RECS")
                                 + " " + Language.getWord("FROM") + " " + indexMin + " " + Language.getWord("TO")
                                 + " " + indexMax + " " + Language.getWord("ONMEM") + " ] ");
       oldPage = currentPage;
    }

   if (!firstTime && connected) {

       int tuples = Count(currentTable,connReg,"",true);

       if (totalReg != tuples) {
           onTable.setText(" " + Language.getWord("TOTAL") + " : " + tuples + " ");
           totalReg = tuples;
        }
    }
   else 
     firstTime = false;

   if (resultSize != numReg) {

       onScreen.setText(" " + Language.getWord("ONSCR") + " : " + resultSize + " ");
       numReg = resultSize;
    }

   String[] colNames = new String[columnNames.size()];
   Object[][] rowD = new Object[rowData.size()][columnNames.size()];

   if (columnNames.size()>0) {

       for (int p=0;p<columnNames.size();p++) {
            Object o = columnNames.elementAt(p);
            colNames[p] = o.toString();
        }

       for (int p=0;p<rowData.size();p++) {

            Vector tempo = (Vector) rowData.elementAt(p);

            for (int j=0;j<columnNames.size();j++) {

                 Object o = tempo.elementAt(j);
                 rowD[p][j] = o;
             }
        }
    }

   if (resultSize > 0) {

       int tableWidth = table.getWidth();
 
       myModel = new MyTableModel(rowD,colNames);
       table = new JTable(myModel);
       table.setPreferredScrollableViewportSize(new Dimension(tableWidth, 70));

       table.addFocusListener(this);
       addFocusListener(this);

       Integer v = (Integer) rowD[0][0]; 

       String val = v.toString();
       int longStr = val.length();

       longStr = longStr*10;

       DefaultTableCellRenderer renderer = new ColoredTableCellRenderer();
       renderer.setHorizontalAlignment(JLabel.CENTER);

       //Personalizar ancho de columnas
       TableColumn column = table.getColumnModel().getColumn(0);
       column.setPreferredWidth(longStr);
       column.setMaxWidth(longStr);
       column.setCellRenderer(renderer);

       int width = (tableWidth - longStr) / columnNames.size() - 1; 

       for (int p=1;p<columnNames.size();p++) {
            column = table.getColumnModel().getColumn(p);
            column.setPreferredWidth(width);

            String type = tableStruct.getTableHeader().getType((String) columnNames.elementAt(p));
            int code = getTypeCode(type);
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();

            switch(code) {
                   case 2:  
                          r.setHorizontalAlignment(JLabel.RIGHT);
                          break;
                   case 3:  
                          r.setHorizontalAlignment(JLabel.CENTER); 
                          break;
                   default: 
                          r.setHorizontalAlignment(JLabel.LEFT);
             }

           column.setCellRenderer(r);
      }

     final JPopupMenu popup = new JPopupMenu();

     JMenuItem Item = new JMenuItem("Update");
     Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
     Item.setActionCommand("UPDATE-POP");
     Item.addActionListener(this);
     popup.add(Item);

     Item = new JMenuItem("Delete");
     Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
     Item.setActionCommand("DELETE-POP");
     Item.addActionListener(this);
     popup.add(Item);

     table.addMouseListener(new MouseAdapter() {

           public void mouseClicked(MouseEvent e) {

                  int[] row = table.getSelectedRows();

                  if (row.length > 0) {

                      if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e) 
                          && row[0] != -1) {

                          if (!popup.isVisible())
                              popup.show(table,e.getX(),e.getY());
                       }
                   }

                  if (e.getClickCount() == 2) { 
                      updatingRecords();
                   }
            }
     });

      delRecord.setEnabled(true);
      updateRecord.setEnabled(true);
      exportFile.setEnabled(true); 
      reportBut.setEnabled(true);
   }
   else {
          delRecord.setEnabled(false);
          updateRecord.setEnabled(false);
	  reportBut.setEnabled(false);
          table = new JTable(rowData,columnNames);
    }

   if (!firstBool)
       remove(windowX);
   else
       firstBool = false;

   windowX = new JScrollPane(table);
   add(windowX,BorderLayout.CENTER);
}

 class MyTableModel extends AbstractTableModel {

  public MyTableModel(Object[][] xdata,String[] colN) {
       data = xdata;
       columnNames = colN;
      }
 
  public String getColumnName(int col) {
        return columnNames[col].toString();
      }

  public int getRowCount() {
        return data.length; 
      }

  public int getColumnCount() {
        return columnNames.length; 
      }

  public Object getValueAt(int row, int col) {
        return data[row][col];
      }

  public boolean isCellEditable(int row, int col) {
        return false;
      }

  public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col); 
      }

  }

  public void setRow1(boolean state) {
     combo1.setEnabled(state);  
     combo2.setEnabled(state);
     combo3.setEnabled(state);
   }

  public void setRow2(boolean state) {
     numRegTextField.setEnabled(state);
     men1.setEnabled(state);
     limitText.setEnabled(state);
     men2.setEnabled(state);
   }

  class CheckBoxListener implements ItemListener {

     public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        if (source == rectangle) {
            if (rectangle.isSelected()) {
                setRow1(true);
                combo1.setSelectedIndex(1);
            }
          else
                setRow1(false);

          combo2.setSelectedIndex(0);
        } 

        if (source == rectangle2) {

            if (rectangle2.isSelected()) {
                setRow2(true);
             }
            else {
                  setRow2(false);
                  numRegTextField.setText("");
                  limitText.setText("");
             }

        }
    }
 }

 public boolean isNum(String word) {

   for (int i=0;i<word.length();i++) {
        char c = word.charAt(i);
        if (!Character.isDigit(c))
            return false;
    }
   return true;
 }

 public int Count(String TableN,PGConnection konn,String sql, boolean log) {

   int val = -1;

   if (TableN.indexOf("\"")==-1 && konn.gotUserSchema(TableN)) {
       String schema = konn.getSchemaName(TableN); 
       TableN = schema + ".\"" + TableN + "\"";
    }

   String counting = "SELECT count(*) FROM " + TableN + ";";
  
   if (sql.length()>0) {

       if (sql.endsWith(";"))
           sql = sql.substring(0,sql.length()-1);

       counting = "SELECT count(*) FROM (" + sql + ") AS foo;";
    }

   String answer = "OK";

   if (log)
       addTextLogMonitor(Language.getWord("EXEC")+ counting + "\"");

   Vector result = new Vector();
   result = konn.TableQuery(counting);

   if (konn.queryFail()) {

       if (log) {
           answer = konn.getProblemString().substring(0,konn.getProblemString().length()-1);
           addTextLogMonitor(Language.getWord("RES") + answer);
        }

       ErrorDialog showError = new ErrorDialog(new JDialog(),konn.getErrorMessage());
       showError.pack();
       showError.setLocationRelativeTo(frame);
       showError.show();
    }
   else {
          Vector value = (Vector) result.elementAt(0);

          try {
                Long entero = (Long) value.elementAt(0);   
                val = entero.intValue();
           }
          catch(Exception ex){
                Integer entero = (Integer) value.elementAt(0);
                val = entero.intValue();
           }

          if (log) 
              addTextLogMonitor(Language.getWord("RES") + val + " " + Language.getWord("RECS"));
    }

   return val;
  }


 public int getTypeCode(String typeStr) {

   if (typeStr.startsWith("varchar") || typeStr.startsWith("char") || typeStr.startsWith("text") 
       || typeStr.startsWith("name") || typeStr.startsWith("date") || typeStr.startsWith("time"))

       return 1;

   if (typeStr.startsWith("int") || typeStr.equals("serial") || typeStr.equals("smallint") 
       || typeStr.equals("real") || typeStr.equals("double"))

       return 2;

   if (typeStr.startsWith("bool"))

       return 3;
   else
       return 4;
 }

 /**
  * Metodo addTextLogMonitor
  * Imprime mensajes en el Monitor de Eventos
  */
  public void addTextLogMonitor(String msg) {

    LogWin.append(msg + "\n");
    int longiT = LogWin.getDocument().getLength();

    if(longiT > 0)
        LogWin.setCaretPosition(longiT - 1);
   }

 /**
  * Metodo getNumRegs
  * Retorna el numero de registros de la tabla
  */
  public int getNumRegs() {
     return numReg;
   }

public void printFile(PrintStream xfile,Vector registers,Vector FieldNames,String Separator) {

   String limit = "";
   boolean isCSV = false;

   try {
        int TableWidth = FieldNames.size();

        if (Separator.equals("csv")) {
            limit = ",";
            isCSV = true;
         }
        else
          limit = Separator;

        for (int p=0;p<registers.size();p++) {

             Vector rData = (Vector) registers.elementAt(p);

             for (int i=0;i<TableWidth;i++) {

                  Object o = rData.elementAt(i);

                  String field = "NULL";

                  if (o != null)
                      field = o.toString();

                  if (isCSV)
                      xfile.print("\"" + field + "\"");
                  else
                      xfile.print(field);

                  if (i<TableWidth-1)
                     xfile.print(limit);
              }

             xfile.print("\n");
         }

       try {
             xfile.close();
        }
       catch(Exception ex) {
             ex.printStackTrace();
        }
       }
     catch(Exception e) {
           e.printStackTrace();
      }
 }

void BuildSQLRecords(String table,TableHeader headT,Vector data) {

   Vector col  = headT.getNameFields(); 
   String sql  = "";
   int numCol = col.size();

   try {

        for (int p=0;p<data.size();p++) {

             sql = "INSERT INTO \"" + table + "\" VALUES(";
             Vector tempo = (Vector) data.elementAt(p);

             if (tempo.size() != numCol) {

                 if (p > 0) 
                     refreshTable();

                 int k = p + 1;

                 JOptionPane.showMessageDialog(Records.this,
                 Language.getWord("TFIC") + k,
                 Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                 return;
              }          

             for (int j=0;j<numCol;j++) {

                  String colName = (String) col.elementAt(j);
                  String type = headT.getType(colName);
                  Object o = tempo.elementAt(j);

                  if (type.startsWith("varchar") || type.startsWith("char") || type.startsWith("text") 
                      || type.startsWith("name") || type.startsWith("date") || type.startsWith("time"))
                      sql += "'" + o.toString() + "'";
                  else
                      sql += o.toString();

                  if (j < (numCol - 1))
                      sql += ",";
              }

             sql += ");";
     
             addTextLogMonitor(Language.getWord("EXEC")+ sql + "\"");
             String result = connReg.SQL_Instruction(sql);

             if (result.equals("OK")) {
                 addTextLogMonitor(Language.getWord("RES") + result);
              }
             else {
                   result = result.substring(0,result.length()-1);
                   addTextLogMonitor(Language.getWord("RES") + result);

                   ErrorDialog showError = new ErrorDialog(new JDialog(),connReg.getErrorMessage());
                   showError.pack();
                   showError.setLocationRelativeTo(frame);
                   showError.show();
                   return;
              }

           }

          refreshTable();

        }
      catch (Exception ex) {
             System.out.println("Error: " + ex);
             ex.printStackTrace(); 
       }

 }

public void refreshTable() {

   String sql = "SELECT " + recordFilter + " FROM \"" + currentTable + "\" ORDER BY " + orderBy;

   if (where.length()!=0) 
       sql = "SELECT " + recordFilter + " FROM \"" + currentTable + "\"" + where; 

   recIM = Count(currentTable,connReg,sql,true);

   currentPage = 1;

   if (recIM > 50) {

       sql = "SELECT * FROM (" + sql + ") AS foo ORDER BY " + orderBy + " LIMIT 50";

       indexMin = 1;
       indexMax = 50;

    }
   else {
          nPages = 1;

          if (recIM == 0) {
              indexMin = indexMax = 0;
           } 
          else {
                indexMin = 1;
                indexMax = recIM;
           }
    }

   Vector res = connReg.TableQuery(sql);
   Vector colNames = connReg.getTableHeader();

   String owner = connReg.getOwner(currentTable);
   setLabel(connReg.getDBname(),currentTable,owner);
   showQueryResult(res,colNames);
   updateUI();
 }

 /** METODO keyTyped */

public void keyTyped(KeyEvent e) {
 }

public void keyPressed(KeyEvent e) {

    int keyCode = e.getKeyCode();
    String keySelected = KeyEvent.getKeyText(keyCode); 

    if (keySelected.equals("Delete")) {

        dropRecords();
        return;
     }

    if (keySelected.equals("Insert")) {

        updatingRecords();
        return;
     } 
 }

 /*
  * METODO keyReleased
  * Handle the key released event from the text field.
  */

public void keyReleased(KeyEvent e) {
 }

 /**
  * METODO focusGained
  * Es un foco para los eventos del teclado
  */

public void focusGained(FocusEvent e) {

    Component jTable = e.getComponent();
    jTable.addKeyListener(this);
 }

 /**
 * METODO focusLost
 */

public void focusLost(FocusEvent e) {

    Component jTable = e.getComponent();
    jTable.removeKeyListener(this);
 }

 /**
 * METODO insertRecords 
 */

public void insertRecords() {

   setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
   InsertData insert = new InsertData(realTableName,tableStruct,frame);
   setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

   if (insert.wasOk()) {

       addTextLogMonitor(Language.getWord("EXEC")+ insert.getSQLString() + "\"");
       String result = connReg.SQL_Instruction(insert.getSQLString());

       if (result.equals("OK")) {

           totalReg++;

           String sql = "SELECT " + recordFilter + " FROM " + realTableName + " ORDER BY " + orderBy;

           if (where.length()>0)
               sql = "SELECT " + recordFilter + " FROM " + realTableName + " " + where;         

           oldMem = recIM;
           recIM = Count(realTableName,connReg,sql,true);

           int oldNPages = nPages;
           nPages = getPagesNumber(recIM);

           onTable.setText(" " + Language.getWord("TOTAL") + " : " + totalReg + " ");

           if (recIM > 50) {

               if ((oldNPages == nPages - 1) && (currentPage == nPages - 1)) {

                   if (!queryLeft.isEnabled()) {

                       queryLeft.setEnabled(true);
                       queryB.setEnabled(true);
                    }

                   currentPage++;
                   limit = 1;
                   indexMin = indexMax = recIM;
                   start = indexMin - 1;

                   sql = "SELECT " + recordFilter + " FROM " + realTableName + "\" ORDER BY " + orderBy + " LIMIT " + limit + " OFFSET " + start;
                   if (where.length()!=0)

                       sql = "SELECT * FROM ("
                              + "SELECT " + recordFilter + " FROM " + realTableName + " " + where + ") AS foo ORDER BY " + orderBy + " LIMIT " + limit
                              + " OFFSET " + start;

                   Vector res = connReg.TableQuery(sql);
                   Vector col = connReg.getTableHeader();

                   addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

                   if (!connReg.queryFail()) {

                       addTextLogMonitor(Language.getWord("RES") + "OK");
                       showQueryResult(res,col);
                       updateUI();
                    }
                }
              else {
                     if (currentPage == nPages) {

                         if (limit < 50)
                             limit++;

                         sql = "SELECT " + recordFilter + " FROM " + realTableName + " ORDER BY " + orderBy + " LIMIT " + limit + " OFFSET " + start;

                         if (where.length()!=0)

                             sql = "SELECT * FROM ("
                                    + "SELECT " + recordFilter + " FROM " + realTableName + " " + where + ") AS foo ORDER BY " + orderBy + " LIMIT " + limit 
                                    + " OFFSET " + start;

                         Vector res = connReg.TableQuery(sql);
                         Vector col = connReg.getTableHeader();

                         addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

                         if (!connReg.queryFail()) {

                             addTextLogMonitor(Language.getWord("RES") + "OK");
                             showQueryResult(res,col);
                             updateUI();
                          }
                     }

                    if (currentPage < nPages) {

                        setStatistics(currentPage,nPages,indexMin,indexMax);

                        if (oldMem != recIM) {

                            onMem.setText(" " + Language.getWord("ONMEM") + " : " + recIM + " ");
                            oldMem = recIM;
                         }


                        if (!queryRight.isEnabled()) {

                            queryRight.setEnabled(true);
                            queryF.setEnabled(true);
                         } 
                    } 
              } 

            }
           else {
                  nPages = 1;

                  Vector res = connReg.TableQuery(sql);
                  Vector col = connReg.getTableHeader();
                  int oldNum = numReg;

                  if (!connReg.queryFail()) {

                      String owner = connReg.getOwner(currentTable);
                      setLabel(connReg.getDBname(),currentTable,owner);
                      showQueryResult(res,col);

                      if (oldNum==0) {
                          rectangle.setEnabled(true);
                          rectangle2.setEnabled(true);
                          advanced.setEnabled(true);
                       }

                      updateUI();
                   }

            } 
         }
       else {
             result = result.substring(0,result.length()-1);

             ErrorDialog showError = new ErrorDialog(new JDialog(),connReg.getErrorMessage());
             showError.pack();
             showError.setLocationRelativeTo(frame);
             showError.show();
        }

       addTextLogMonitor(Language.getWord("RES") + result);
    }
}

 /**
 * METODO dropRecords 
 */

public void dropRecords() {

    GenericQuestionDialog dropRecs = new GenericQuestionDialog(frame,Language.getWord("YES"),Language.getWord ("NO"),
                                         Language.getWord("CONFRM"),Language.getWord("DRCONF"));

    boolean sure = dropRecs.getSelecction();

    if (sure) {

        String[] oid = getRecordOid();
        String result = "";

        for (int i=0;i<oid.length;i++) {

            String sqlStr = "DELETE FROM " + realTableName + " WHERE oid=" + oid[i];
            addTextLogMonitor(Language.getWord("EXEC")+ sqlStr + ";\"");
            result = connReg.SQL_Instruction(sqlStr);

            if (!result.equals("OK")) 
                result = result.substring(0,result.length()-1);

            addTextLogMonitor(Language.getWord("RES") + result);
         }

        result = refreshAfterDrop(result);
        addTextLogMonitor("Deletion Report: " + result);
     }
 }

 /**
 * METODO getRecordOid 
 */

public String[] getRecordOid() {

    int[] rows = table.getSelectedRows();
    String[] oid = new String[rows.length];

    for (int i=0;i<rows.length;i++) {
         Object proof = myModel.getValueAt(rows[i],0);
         oid[i] = proof.toString();
     }

    return oid;
 }

 /**
 * METODO updatingRecords 
 */

public void updatingRecords() {

    String[] oid = getRecordOid();

    Vector oldData = new Vector();
    for (int i=1;i<table.getColumnCount();i++) {
        oldData.addElement(table.getValueAt(table.getSelectedRow(), i));
    }
    Vector data = new Vector();
    UpdateRecord upper = new UpdateRecord(realTableName,tableStruct,oldData,frame);

    if (upper.getResult()) {

        String SQL = upper.getUpdate() + " WHERE ";
        for (int i=0;i<oid.length;i++) {
             SQL += "oid=" + oid[i];
             if (i<oid.length-1)
                 SQL += " OR ";
         }

        addTextLogMonitor(Language.getWord("EXEC")+ SQL + ";\"");
        String result = connReg.SQL_Instruction(SQL);

        if (result.equals("OK")) {

            String sql = "SELECT " + recordFilter + " FROM " + realTableName + " ORDER BY " + orderBy;

            if (where.length()!=0)
                sql = "SELECT " + recordFilter + " FROM " + realTableName + where;

            recIM = Count(currentTable,connReg,sql,true);

            if (recIM > 50)
                sql = "SELECT * FROM (" + sql + ") AS foo ORDER BY " + orderBy + " LIMIT " + limit + " OFFSET " + start;

            Vector res = connReg.TableQuery(sql);
            Vector col = connReg.getTableHeader();

            if (!connReg.queryFail()) {

                showQueryResult(res,col);
                updateUI();
             }
          }
         else {
                result = result.substring(0,result.length()-1);
                ErrorDialog showError = new ErrorDialog(new JDialog(),connReg.getErrorMessage());
                showError.pack();
                showError.setLocationRelativeTo(frame);

                showError.show();
          }

        addTextLogMonitor(Language.getWord("RES") + result);
    }
 }

 /**
 * METODO getPagesNumber
 */

public int getPagesNumber(int rIM) {

    double fl = ((double)rIM)/50;
    String div = "" + fl;

    int nP = rIM/50;

    if (div.indexOf(".") != -1) {

        String str = div.substring(div.indexOf(".")+1,div.length());

        if (!str.equals("0"))
             nP++;
     }

    return nP;
}

 /**
 * METODO setStatistics
 */

public void setStatistics(int cPage, int nP, int iMin, int iMax) {

       currentStatistic.setText(" " + Language.getWord("PAGE") + " " + cPage + " " + Language.getWord("OF")
                                 + " " + nP + " [ " + Language.getWord("RECS")
                                 + " " + Language.getWord("FROM") + " " + iMin + " " + Language.getWord("TO")
                                 + " " + iMax + " " + Language.getWord("ONMEM") + " ] ");
 }

 /**
 * METODO refreshAfterDrop 
 */

public String refreshAfterDrop(String result) {

   String sql = "SELECT " + recordFilter + " FROM " + realTableName + " ORDER BY " + orderBy;

   if (where.length()!=0)
       sql = "SELECT " + recordFilter + " FROM " + realTableName + " " + where;

   int oldR = recIM;
   recIM = Count(realTableName,connReg,sql,true);

   int newTotalReg = Count(realTableName,connReg,"",true);
   int diff = totalReg - newTotalReg;

   String mesg = Language.getWord("DELOKS");

   if (diff == numReg) {
    
       if (diff == 1)
           mesg = Language.getWord("DELOK");

       if (currentPage == nPages && currentPage > 1) {

           currentPage--;
           start -= 50;
           limit = 50;
           indexMin = start + 1;
         }
    }

   result += " [ " + diff + " " + mesg + " ]";

   if (oldR > recIM) {

       if (recIM > 50) {

           sql = "SELECT * FROM (" + sql + ") AS foo ORDER BY " + orderBy + " LIMIT " + limit + " OFFSET " + start;
        }
       else {
              currentPage = 1;

              if (recIM < 0) {

                  indexMin = 1;
                  indexMax = recIM;
               }
        }

   Vector res = connReg.TableQuery(sql);
   Vector col = connReg.getTableHeader();

   if (!connReg.queryFail()) {

       String owner = connReg.getOwner(currentTable);
       setLabel(connReg.getDBname(),currentTable,owner);
       showQueryResult(res,col);

       if (numReg==0) {

           rectangle.setEnabled(false);
           rectangle2.setEnabled(false);
           advanced.setEnabled(false);
        }

       updateUI();
    }
  }

  return result;
 }

public void setOrder() {
    orderBy = "oid";
 }

} // Fin de la Clase

class ColoredTableCellRenderer extends DefaultTableCellRenderer {

public void setValue(Object value) {
            setForeground(Color.red);
            setText(value.toString());
 }
}
