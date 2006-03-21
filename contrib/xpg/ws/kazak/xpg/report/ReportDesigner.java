/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ReportDesigner v 0.1                                                   
* Descripcion:
* Esta clase se encarga de mostrar la ventana inicial de reportes   
* donde el usuario escoge los currentField a incluir en el reporte.       
*                                                                 
* Los objetos de este tipo se crean desde la clase Queries        
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*          Angela Sandobal  - angesand@libertad.univalle.edu.co     
*/
package ws.kazak.xpg.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;
import java.util.Calendar;
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
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.idiom.Language;

public class ReportDesigner extends JDialog implements ActionListener,FocusListener,KeyListener{

 JTextArea LogWin;
 JFrame frame;
 Vector nameFields;
 JList fields;
 JTextField reportLocation,patternString,recordsNumber;
 boolean trigger = false;
 JList selected;
 Vector choosed = new Vector();
 Hashtable titlesDefined = new Hashtable();
 Vector operationsVector = new Vector();
 Vector currentIndexOperation = new Vector();
 Vector data = new Vector();
 Hashtable indexes = new Hashtable();
 HtmlProperties htmlInfo = new HtmlProperties();
 JTextField title = new JTextField();
 JTextField titleTextField;
 String component = "";
 JCheckBox titleCheckBox;
 JComboBox operationComboBox;
 JCheckBox resultCheckBox;
 JButton appe,stylex,view,adds,dels,selectAll,clear,updateChanges;
 Vector tableN = new Vector(); 
 PGConnection pgconn;
 String path = "";
 String pattern = "";
 String numPerPage = "30";

 public ReportDesigner(JFrame aframe,Vector columNames,Vector info, JTextArea lw, String tables, PGConnection pg) 
  {
   super(aframe,true);
   frame = aframe;
   LogWin = lw;
   pgconn = pg;
   nameFields = (Vector) columNames.clone();
   data = info;

   for (int p=0;p<nameFields.size();p++) {
        String nome = (String) nameFields.elementAt(p); 
        Integer intCast = new Integer(p);
        indexes.put(nome,intCast);
    }

   nameFields = sorting(nameFields);

   StringTokenizer st = new StringTokenizer(tables,",");

   while (st.hasMoreTokens()) {
          String word = st.nextToken();
          word = word.trim();
          tableN.addElement(word);
    }

   setTitle(Language.getWord("REPTED"));
   getContentPane().setLayout(new BorderLayout());

   JPanel center = new JPanel();
   center.setLayout(new FlowLayout(FlowLayout.CENTER));

   Border etched1 = BorderFactory.createEtchedBorder();
   TitledBorder title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("FIELED"));
   center.setBorder(title1);

   fields = new JList(nameFields);

   fields.setVisibleRowCount(5);
   JScrollPane scrollC = new JScrollPane(fields);

   selectAll = new JButton(Language.getWord("SELALL"));
   selectAll.setActionCommand("SELALL");
   selectAll.addActionListener(this);

   JPanel buttons = new JPanel();
   buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
   buttons.add(selectAll);

   JPanel options = new JPanel();
   options.setLayout(new BorderLayout());
   options.add(scrollC,BorderLayout.CENTER);
   options.add(buttons,BorderLayout.SOUTH);

   String init[] = {" "};
   selected = new JList(init);
   selected.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
   selected.setVisibleRowCount(5);
   selected.addFocusListener(this);

   updateChanges = new JButton(Language.getWord("SAVECH"));
   updateChanges.setActionCommand("SAVECHANGES");
   updateChanges.addActionListener(this);
   updateChanges.setEnabled(false);

   JPanel setChanges = new JPanel();
   setChanges.setLayout(new FlowLayout());
   setChanges.add(updateChanges);

   MouseListener mouseListener = new MouseAdapter() {

	public void mousePressed(MouseEvent e) {

          int index = selected.locationToIndex(e.getPoint());

          if (index > -1 && !(choosed.isEmpty())) {

            titleCheckBox.setEnabled(true);
            String currentField = (String) selected.getSelectedValue();
            title.setText(Language.getWord("FIELD")+" "+ currentField);

            String isTitleDefined = (String) titlesDefined.get(currentField); 
            titleTextField.setText(isTitleDefined);

            setTitleField(currentField,isTitleDefined);
            setOperationsCombo(currentField, index);
          }
         }
        };

    selected.addMouseListener(mouseListener);

    JScrollPane scrollE = new JScrollPane(selected);
    clear = new JButton(Language.getWord("CLR") + " " + Language.getWord("ALL"));
    clear.setActionCommand("CLEAR");
    clear.addActionListener(this);

    JPanel bit = new JPanel();
    bit.setLayout(new FlowLayout(FlowLayout.CENTER));
    bit.add(clear);

    JPanel elec = new JPanel();
    elec.setLayout(new BorderLayout());
    elec.add(scrollE,BorderLayout.CENTER);
    elec.add(bit,BorderLayout.SOUTH);

    JPanel properties = new JPanel();
    properties.setLayout(new BorderLayout());
    title = new JTextField(Language.getWord("NOFSEL"));
    title.setHorizontalAlignment(JTextField.CENTER);
    title.setEditable(false);

    titleCheckBox = new JCheckBox(Language.getWord("SETTIT"));
    titleCheckBox.setActionCommand("TITLE");
    titleCheckBox.addActionListener(this);

    titleTextField = new JTextField(10);
    titleTextField.setEditable(false);
    titleTextField.setEnabled(false);
    titleCheckBox.setEnabled(false);

    resultCheckBox = new JCheckBox(Language.getWord("INCRES"));
    resultCheckBox.setActionCommand("RESULT");
    resultCheckBox.addActionListener(this);

    String[] valuex = {Language.getWord("NONE"),Language.getWord("TOTAL"),Language.getWord("AVERG")};
    operationComboBox = new JComboBox(valuex);
    operationComboBox.setEnabled(false);

    resultCheckBox.setEnabled(false);

    JPanel block = new JPanel();
    block.setLayout(new GridLayout(2,0));
    block.add(titleCheckBox);
    block.add(resultCheckBox);

    JPanel pTit= new JPanel();
    pTit.setLayout(new FlowLayout(FlowLayout.CENTER));
    pTit.add(titleTextField);

    JPanel pop = new JPanel();
    pop.setLayout(new FlowLayout(FlowLayout.CENTER));
    pop.add(operationComboBox);

    JPanel block2 = new JPanel();
    block2.setLayout(new GridLayout(2,0));
    block2.add(pTit);
    block2.add(pop);

    properties.add(title,BorderLayout.NORTH);
    properties.add(block,BorderLayout.CENTER);
    properties.add(block2,BorderLayout.EAST);
    properties.add(setChanges,BorderLayout.SOUTH);

    title1 = BorderFactory.createTitledBorder(etched1);
    properties.setBorder(title1);

    URL imgURL = getClass().getResource("/icons/16_Right.png");
    adds = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
    adds.setActionCommand("ADDS");
    adds.addActionListener(this);

    imgURL = getClass().getResource("/icons/16_Left.png");
    dels = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
    dels.setActionCommand("DELS");
    dels.addActionListener(this);
    dels.setEnabled(false);

    JLabel space = new JLabel(" ");

    JPanel medium = new JPanel();
    medium.setLayout(new BorderLayout());
    medium.add(adds,BorderLayout.NORTH);
    medium.add(dels,BorderLayout.CENTER);
    medium.add(space,BorderLayout.SOUTH);

    JPanel block3 = new JPanel();
    block3.setLayout(new FlowLayout(FlowLayout.CENTER));
    block3.add(options);
    block3.add(medium);
    block3.add(elec);
    block3.setBorder(title1);

    center.add(block3);
    center.add(properties);

    JPanel buttons2 = new JPanel();
    buttons2.setLayout(new FlowLayout(FlowLayout.CENTER));

    appe = new JButton(Language.getWord("LOOK"));
    appe.setActionCommand("APPE");
    appe.addActionListener(this);
    appe.setEnabled(false);

    stylex = new JButton(Language.getWord("PRESTY"));
    stylex.setActionCommand("STYLES");
    stylex.addActionListener(this);
    stylex.setEnabled(false);

    view = new JButton(Language.getWord("VIEW"));
    view.setActionCommand("VIEW");
    view.addActionListener(this);
    view.setEnabled(false);

    JButton close = new JButton(Language.getWord("CLOSE"));
    close.setActionCommand("CLOSE");
    close.addActionListener(this);

    buttons2.add(appe);
    buttons2.add(stylex);
    buttons2.add(view);
    buttons2.add(close);

    JPanel filesLocation = new JPanel();
    filesLocation.setLayout(new BorderLayout());
    filesLocation.setLayout(new BoxLayout(filesLocation, BoxLayout.Y_AXIS));

    String OS = System.getProperty("os.name");

    if(OS.equals("Linux") || OS.equals("Solaris") || OS.equals("FreeBSD"))
       path = System.getProperty("user.home") + System.getProperty("file.separator") 
              + ".xpg" + System.getProperty("file.separator") + "reports" 
              + System.getProperty("file.separator");

    if(OS.startsWith("Windows"))
       path = System.getProperty("xpgHome") + System.getProperty("file.separator")
              + "reports" + System.getProperty("file.separator");

    JLabel reportLabel = new JLabel(Language.getWord("RFP"));
    reportLocation = new JTextField(path,20);

    JButton browse = new JButton(Language.getWord("BROWSE"));
    browse.setActionCommand("BROWSE");
    browse.addActionListener(this);

    JPanel line = new JPanel();
    line.setLayout(new FlowLayout());
    line.add(reportLabel);
    line.add(reportLocation);
    line.add(browse);

    JPanel line2 = new JPanel();
    line2.setLayout(new FlowLayout());

    JLabel patternFile = new JLabel(Language.getWord("FNP"));
    pattern = "report" + DateReportName(getTime()); 

    patternString = new JTextField(pattern,15);

    JLabel recordsLimit = new JLabel(Language.getWord("RPP"));
    recordsNumber = new JTextField("30",3);

    line2.add(patternFile);
    line2.add(patternString);
    line2.add(new JPanel());
    line2.add(recordsLimit);
    line2.add(recordsNumber);

    filesLocation.add(line);
    filesLocation.add(line2);

    title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("FEATURES"));
    filesLocation.setBorder(title1);

    JPanel mediumBlock = new JPanel();    
    mediumBlock.setLayout(new BorderLayout());
    mediumBlock.add(center,BorderLayout.CENTER);
    mediumBlock.add(filesLocation,BorderLayout.SOUTH);

    getContentPane().add(mediumBlock,BorderLayout.CENTER);
    getContentPane().add(buttons2,BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(frame);
    setVisible(true);
  }

/*** Manejo de Eventos ***/

public void actionPerformed(java.awt.event.ActionEvent e) {

  if (e.getActionCommand().equals("SAVECHANGES")) {

      if (titleCheckBox.isSelected()) {
          String newTitle = titleTextField.getText();
          String currentField = (String) selected.getSelectedValue();

          if (titlesDefined.containsKey(currentField))
              titlesDefined.remove(currentField);

          titlesDefined.put(currentField,newTitle); 
       }

      if (resultCheckBox.isSelected()) {

          int pos = selected.getSelectedIndex();
          int index = operationComboBox.getSelectedIndex();

          if (pos != -1 && index != 0)
              currentIndexOperation.setElementAt("" + index,pos);
       }

      return;     
   }

  if (e.getActionCommand().equals("BROWSE")) {

      String s = "file:" + System.getProperty("user.home");

      JFileChooser fc = new JFileChooser(s);
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int returnVal = fc.showDialog(ReportDesigner.this,Language.getWord("CHDIR"));

      if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file = fc.getSelectedFile();
          String FileName = file.getAbsolutePath();
          reportLocation.setText(FileName + System.getProperty("file.separator"));
       }

      return;
   }

  if (e.getActionCommand().equals("APPE")) {

      ReportAppearance dialog = new ReportAppearance(ReportDesigner.this,frame);

      if (dialog.isWellDone())
          htmlInfo = dialog.getHtmlProperties();

      return;
   }

  /* if(e.getActionCommand().equals("STYLES")) 
   {
    StyleSelector win = new StyleSelector(ReportDesigner.this,idiom);
    if(win.wellDone)
     htmlInfo = new HtmlProperties(win.indice);
   }
  */

  if (e.getActionCommand().equals("VIEW")) {
   
    if (choosed.size()==0) {
        JOptionPane.showMessageDialog(ReportDesigner.this,                               
        Language.getWord("NFIR"),                       
        Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
        return;
     }
    else {
          String path2       = reportLocation.getText(); 
          String pattern2    = patternString.getText();
          String numPerPage2 = recordsNumber.getText();

          if (path2.length() > 0)
              path = path2;
          else {
                JOptionPane.showMessageDialog(ReportDesigner.this,
                Language.getWord("RFPEMP"),
                Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                return;
           }

          if (pattern2.length() > 0)
              pattern = pattern2;
          else {
                JOptionPane.showMessageDialog(ReportDesigner.this,
                Language.getWord("FNPEMP"),
                Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                return;
           }


          if (numPerPage2.length() > 0) {
             if (isNum(numPerPage2))
                 numPerPage = numPerPage2;
             else {
                   recordsNumber.setText("");
                   JOptionPane.showMessageDialog(ReportDesigner.this,
                   Language.getWord("RPPNUM"),
                   Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                   return;
                  }
           }
          else {
                JOptionPane.showMessageDialog(ReportDesigner.this,
                Language.getWord("RPPEMP"),
                Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                return;
           }

          int[] posic = new int[choosed.size()];
          Vector names = new Vector();

          for (int k=0;k<choosed.size();k++) {
               String tmp = (String) choosed.elementAt(k);
               names.addElement((String) titlesDefined.get(tmp));
               Integer value = (Integer) indexes.get(tmp);
               posic[k] = value.intValue();
           }

          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

          ReportMaker reportMaker = new ReportMaker(path,pattern,numPerPage,names,data,posic,currentIndexOperation,htmlInfo,LogWin);
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

          if (reportMaker.isFail()) {
                  JOptionPane.showMessageDialog(ReportDesigner.this,
                  "No browsers found in the system.",
                  Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                  setVisible(false);
           }
       }

    return;
   }

  if (e.getActionCommand().equals("CLOSE")) {
     setVisible(false);
     return;
   }

  if (e.getActionCommand().equals("ADDS")) {

    if (!fields.isSelectionEmpty()) {

        Object[] fieldsArray = fields.getSelectedValues();

        for (int i=0;i< fieldsArray.length;i++) {

             String tempo = fieldsArray[i].toString();

             nameFields.remove(fieldsArray[i]);

             if (!choosed.contains(tempo))
                 choosed.add(tempo); 

             titlesDefined.put(fieldsArray[i],fieldsArray[i]);
             operationsVector.addElement("0");
             currentIndexOperation.addElement("0");
          }

        fields.setListData(nameFields);
        selected.setListData(choosed);


        if (nameFields.size() == 0) {
            adds.setEnabled(false);
            selectAll.setEnabled(false);
         }

        if (choosed.size() >= 1) {

            if (!clear.isEnabled())
                clear.setEnabled(true);

            if (!dels.isEnabled())
                dels.setEnabled(true);
         }

        if (!view.isEnabled()) {
            appe.setEnabled(true);
            view.setEnabled(true);
            //stylex.setEnabled(true);
         }

        title.setText(Language.getWord("NOFSEL"));
        titleTextField.setText("");
       }

  return;
 }

  if (e.getActionCommand().equals("DELS")) {

      if (!selected.isSelectionEmpty()) {

          Object[] fieldsArray = selected.getSelectedValues();

          for (int i=0;i< fieldsArray.length;i++) {

               choosed.remove(fieldsArray[i]);
               nameFields.add(fieldsArray[i]);

               String tmp = (String) titlesDefined.remove(fieldsArray[i]);
               operationsVector.removeElementAt(operationsVector.size()-1);
               currentIndexOperation.removeElementAt(currentIndexOperation.size()-1);
           }

          if (choosed.size() == 0) {

              dels.setEnabled(false);
              view.setEnabled(false);
              appe.setEnabled(false);
              clear.setEnabled(false);
           }

          if (nameFields.size() >= 1) {

              if (!adds.isEnabled())
                  adds.setEnabled(true);

              if (!selectAll.isEnabled())
                  selectAll.setEnabled(true);
           }

          nameFields = sorting(nameFields);
          fields.setListData(nameFields);
          selected.setListData(choosed);

          title.setText(Language.getWord("NOFSEL"));
          titleTextField.setText("");

          titleCheckBox.setEnabled(false);
          resultCheckBox.setEnabled(false);
          updateChanges.setEnabled(false);
        }

   return;
 }


 if (e.getActionCommand().equals("SELALL")) {

     for (int i=0;i<nameFields.size();i++) {
          String tempo = (String) nameFields.elementAt(i);
          choosed.addElement(tempo);

          titlesDefined.put(tempo,tempo);
          operationsVector.addElement("0");
          currentIndexOperation.addElement("0");
     }

     nameFields = new Vector();
     fields.setListData(nameFields);

     selected.setListData(choosed);
     selectAll.setEnabled(false);
     adds.setEnabled(false);

     if (!dels.isEnabled())
          dels.setEnabled(true);

     if (!clear.isEnabled())
          clear.setEnabled(true);

     if (!view.isEnabled()) {
         appe.setEnabled(true);
         view.setEnabled(true);
      }

     return;
  }

 if (e.getActionCommand().equals("CLEAR")) {

     for (int i=0;i<choosed.size();i++) {
          String tempo = (String) choosed.elementAt(i);
          nameFields.addElement(tempo);
      }

     choosed = new Vector();
     selected.setListData(new Vector());
      
     nameFields = sorting(nameFields);
     fields.setListData(nameFields);

     dels.setEnabled(false);

     if (!selectAll.isEnabled())
         selectAll.setEnabled(true);

     appe.setEnabled(false);
     //stylex.setEnabled(false);
     view.setEnabled(false);

     titlesDefined.clear();
     operationsVector = new Vector();

     title.setText(Language.getWord("NOFSEL"));
     titleTextField.setText("");

     if (titleCheckBox.isSelected())
         titleCheckBox.setSelected(false);

     if (resultCheckBox.isSelected())
         resultCheckBox.setSelected(false);

     titleCheckBox.setEnabled(false);
     titleTextField.setEnabled(false);
     resultCheckBox.setEnabled(false);
     updateChanges.setEnabled(false);
     operationComboBox.setEnabled(false);
     clear.setEnabled(false);

     adds.setEnabled(true);

     return;
  }	

 if (e.getActionCommand().equals("TITLE")) {

     int pos = selected.getSelectedIndex();

     if (titleCheckBox.isSelected()) {
         titleTextField.setEnabled(true);
         titleTextField.setEditable(true);
         updateChanges.setEnabled(true);
    }
   else {
         String mesg = (String) selected.getSelectedValue(); 
         titlesDefined.remove(mesg);
         titlesDefined.put(mesg,mesg);
         titleTextField.setText(mesg); 
         titleTextField.setEditable(false);
         titleTextField.setEnabled(false);

         if (!resultCheckBox.isSelected())
             updateChanges.setEnabled(false);
    }
   return;
 }

 if (e.getActionCommand().equals("RESULT")) {

     int pos = selected.getSelectedIndex();

     if (resultCheckBox.isSelected()) {
         operationComboBox.setEnabled(true);
         operationsVector.setElementAt("1",pos);
         updateChanges.setEnabled(true); 
      }
     else {
           operationComboBox.setSelectedIndex(0);
           operationComboBox.setEnabled(false);
           currentIndexOperation.setElementAt("0",pos); 
           operationsVector.setElementAt("0",pos);

           if (!titleCheckBox.isSelected())
               updateChanges.setEnabled(false);
      }

     return;
  }

/*
 if (e.getActionCommand().equals("COMBO")) {

     int pos = selected.getSelectedIndex();
     int index = operationComboBox.getSelectedIndex();

     if (pos != -1)
         currentIndexOperation.setElementAt("" + index,pos);

     return;
  } 
*/
 }

 public void focusGained(FocusEvent e) {

    Component tmp = e.getComponent();
    tmp.addKeyListener(this);
  }

 public void focusLost(FocusEvent e) {

    Component tmp = e.getComponent();
    tmp.removeKeyListener(this);
  } 

 public void keyTyped(KeyEvent e) { 
  }   

 public void keyPressed(KeyEvent e) {

   int keyCode = e.getKeyCode();
   String keySelected = KeyEvent.getKeyText(keyCode);

   if (keySelected.equals("Down")) {

        int pos = selected.getSelectedIndex();

        if (pos<choosed.size() - 1 && (pos != -1)) {

            String currentField = (String) choosed.elementAt(pos + 1);
            title.setText(Language.getWord("FIELD")+" "+ currentField);

            String isTitleDefined = (String) titlesDefined.get(currentField); 
            setTitleField(currentField,isTitleDefined);

            setOperationsCombo(currentField, pos + 1);
         }
      }

     if (keySelected.equals("Up")) { 

         int pos = selected.getSelectedIndex();

        if (pos>0) {

            String currentField = (String) choosed.elementAt(pos - 1);
            title.setText(Language.getWord("FIELD")+" "+ currentField);
            //titleTextField.setText(real);
            String isTitleDefined = (String) titlesDefined.get(currentField); 

            setTitleField(currentField,isTitleDefined);
            setOperationsCombo(currentField, pos - 1);
         }

      }
 }

 public void keyReleased(KeyEvent e) {
  } 

 /**
  * METODO DateReportName
  * Crea el nombre del archivo de logs segun la fecha
  */
  public String DateReportName(String[] val) {

   String dformat = val[0] + val[1] + val[2] + "_" + val[3] + "-" + val[4];
   return dformat;

  }

 /**
  * METODO getDate
  * Retorna la fecha y hora 
  */
 public String[] getTime() {

   Calendar today = Calendar.getInstance();
   String[] val = new String[5];
   int monthInt = today.get(Calendar.MONTH);
   int minuteInt = today.get(Calendar.MINUTE);
   int dayInt = today.get(Calendar.DAY_OF_MONTH);
   String zero = "" + monthInt;
   String min = "" + minuteInt;
   String day = "" + dayInt;

   if (monthInt < 10)
       zero = "0" + zero;

   if (minuteInt < 10)
       min = "0" + min;

   if (dayInt < 10)
       day = "0" + day;

   String year = "" + today.get(Calendar.YEAR);
   year = year.substring(2,4);

   val[0] = day;
   val[1] = zero;
   val[2] = year;
   val[3] = "" + today.get(Calendar.HOUR_OF_DAY);
   val[4] = min;
   return val;
  }

 /**
  * METODO isNumericType 
  * Retorna si un currentField es de tipo numerico 
  */

 boolean isNumericType(String fieldN) {

   int i = tableN.size();     

   for (int j=0;j<i;j++) {

        Vector res;
        String nameT = (String) tableN.elementAt(j);
        Vector field = pgconn.TableQuery("SELECT t.typname FROM pg_class c, pg_attribute a, pg_type t WHERE c.relname='" + nameT + "' AND a.attrelid = c.oid AND a.atttypid = t.oid AND a.attname='" + fieldN + "';"); 

        if (!field.isEmpty()) {

            res = (Vector) field.elementAt(0);
 
            if (!res.isEmpty()) {

                String type = (String) res.elementAt(0);

            if (type.startsWith("int") || type.equals("serial") || type.equals("smallint") || type.equals("real") || type.equals("double"))
                return true;
            else
                return false;
           }
        }
     }
    return false;
  }

 /**
  * METODO isNum 
  * Retorna si una cadena solo posee caracteres numericos 
  */

 public boolean isNum(String s) {

    for (int i=0;i<s.length();i++) {

       char c = s.charAt(i);

       if(!Character.isDigit(c))
           return false;
     }

    return true;
  }

 /**
  * METODO sorting 
  * Retorna un Vector ordenado de cadenas de caracteres 
  */

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

 public void setOperationsCombo(String currentField, int index) {

  if (index > -1 && !(choosed.isEmpty())) {

      if (isNumericType(currentField)) {

          if (!resultCheckBox.isEnabled()) 
              resultCheckBox.setEnabled(true);

          String operationItem = (String) operationsVector.elementAt(index);

          if (operationItem.equals("1")) {  
              resultCheckBox.setSelected(true);
              operationComboBox.setEnabled(true);
              int pox = Integer.parseInt((String) currentIndexOperation.elementAt(index));
              operationComboBox.setSelectedIndex(pox);
           }
       }
      else {
             if (resultCheckBox.isSelected())
                 resultCheckBox.setSelected(false);

             resultCheckBox.setEnabled(false);

             if (operationComboBox.isEnabled()) {
                 operationComboBox.setEnabled(false);
                 operationComboBox.setSelectedIndex(0);
              }
           }
   }

 }

 public void setTitleField(String currentField, String isTitleDefined) {

  boolean flag = false;
  titleTextField.setText(isTitleDefined);

  if (!isTitleDefined.equals(currentField)) 
      flag = true;
       
  titleCheckBox.setSelected(flag);
  titleTextField.setEditable(flag);
  titleTextField.setEnabled(flag);
 } 

} // Fin de la Clase
