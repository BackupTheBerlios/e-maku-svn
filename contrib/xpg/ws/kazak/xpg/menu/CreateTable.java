/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS CreateTable v 0.1
* Esta clase se encarga de mostrar un dialogo para crear una tabla. 
* Se manejan eventos para capturar los datos ingresados por el      
* usuario.                                                          
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*
* Fecha: 2001/07/31                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.db.DBStructure;
import ws.kazak.xpg.db.OptionField;
import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.db.Table;
import ws.kazak.xpg.db.TableFieldRecord;
import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.misc.input.GenericQuestionDialog;

public class CreateTable extends JDialog implements ActionListener {

 JComboBox cmbDB;
 JComboBox cmbReferences;
 private String typedText = null;
 private JOptionPane optionPane;
 Hashtable HashFields = new Hashtable();
 Vector TableList;
 JPanel rowInherit,rowReferences,rowFields,combosPanel,checkPanel;
 JComboBox cmbFields,typeFieldCombo;
 public String CurrentTable;
 public String dbn;
 Table cTable;
 JCheckBox CBIn,CBCheck,CBr3,CBr4,ForeingButton,isKey,notNullButton;
 JRadioButton primaryButton,uniqueButton;
 final JTextField textCheck2,textField3,textField6,textField7,textField5,textTable;
 JLabel msgFields,msgReferences;
 DBStructure tmp;
 PGConnection current;
 TitledBorder titleK;
 TitledBorder titleFK;
 Color currentColor;
 JList fieldJList;
 Vector fieldsN = new Vector();
 JButton delField,delAll;
 Vector tablesH = new Vector();
 boolean wellDone = false;
 boolean inheritActive = false;
 boolean consActive = false;
 String inheritString = "";
 Vector vecConn;
 Vector dbNames;
 JTextArea LogWin;
 int num=0;
 Vector ConstNames = new Vector();
 Vector ConstDef = new Vector();
 JFrame fmain;

 /**
  * Metodo Constructor
  */
  public CreateTable (JFrame aFrame, Vector dbNm, Vector Conn, String currentDB, JTextArea log ) {

    super(aFrame, true);
    fmain = aFrame;
    dbNames = dbNm;
    vecConn = Conn;

    Border etched = BorderFactory.createEtchedBorder();
    TitledBorder title = BorderFactory.createTitledBorder(etched);
    LogWin = log;
    setTitle(Language.getWord("CREATET"));

    //construcción parte superior de la ventana

    //Captura campo Database
    JPanel rowDB = new JPanel();
    cmbDB = new JComboBox(dbNames);

    if (currentDB != null)
        cmbDB.setSelectedItem(currentDB);

    cmbDB.setActionCommand("COMBO");
    cmbDB.addActionListener(this);

    rowDB.setLayout(new BoxLayout(rowDB,BoxLayout.X_AXIS));
    rowDB.add(new JLabel(Language.getWord("DB")+": "));
    rowDB.add(cmbDB);

    //Captura campo Table-Name
    JPanel rowTable= new JPanel();
    textTable = new JTextField(14); 
    rowTable.setLayout(new BoxLayout(rowTable,BoxLayout.X_AXIS));
    rowTable.add(new JLabel(Language.getWord("NAME")+": "));
    rowTable.add(textTable);

    //El panel superior se divide en dos este es el panel uno
    JPanel vacio1 = new JPanel();
    JPanel topOne = new JPanel();
    topOne.setLayout(new FlowLayout(FlowLayout.CENTER));
    topOne.add(rowTable);
    topOne.add(rowDB);
    topOne.setBorder(title);

    //Captura campo Inherit   
    rowInherit= new JPanel();
    JButton Inherit = new JButton(Language.getWord("INHE"));
    Inherit.setActionCommand("BUT-INHE");
    Inherit.addActionListener(this);

    dbn = (String) cmbDB.getSelectedItem();
    int index = dbNames.indexOf(dbn);
    current = (PGConnection) vecConn.elementAt(index);

    TableList = current.TableQuery("SELECT tablename FROM pg_tables where tablename !~ '^pg_' AND tablename  !~ '^pga_' ORDER BY tablename");

    if (TableList.size()>0) {

        String[] tables = new String[TableList.size()];

        for (int i=0;i<TableList.size();i++) {
             Object o = TableList.elementAt(i);
             String db = o.toString();
             tables[i] = db.substring(1,db.length()-1);
         }

        Vector listTables = new Vector();
        listTables.addElement(tables[0]);
        Vector tR = current.getTablesStructure(listTables);
        cTable = (Table) tR.elementAt(0);

        cmbReferences = new JComboBox(tables);
        cmbFields = new JComboBox(cTable.getTableHeader().getNameFields());
     }
    else {
          String[] listNull = {Language.getWord("NOTABLES")};
          String[] listNull2 = {Language.getWord("NOREG")};
          cmbReferences = new JComboBox(listNull);
          cmbFields = new JComboBox(listNull2);
       }

     cmbReferences.setActionCommand("CMB-REF");
     cmbReferences.addActionListener(this);

     JButton constr = new JButton(Language.getWord("CONST"));
     constr.setActionCommand("BUT-CONST");
     constr.addActionListener(this);

     rowInherit.setLayout(new FlowLayout(FlowLayout.CENTER));
     rowInherit.add(Inherit);
     rowInherit.add(constr);

     CBCheck = new JCheckBox();
     CBCheck.setActionCommand("CHECK-Check");
     CBCheck.addActionListener(this);

     JPanel checks = new JPanel();
     checks.setLayout(new GridLayout(0,1));
     checks.add(CBCheck );

     JPanel rowConstraint= new JPanel();
     JPanel labels = new JPanel();
     labels.setLayout(new GridLayout(0,1));
     labels.add(new JLabel(Language.getWord("CHECK")+": "));

     textCheck2 = new JTextField(35);
     textCheck2.setEditable(false);
     textCheck2.setEnabled(false);

     JPanel tfields = new JPanel();
     tfields.setLayout(new GridLayout(0,1));
     tfields.add(textCheck2);

     rowConstraint.setLayout(new FlowLayout(FlowLayout.CENTER));

     rowConstraint.add(checks); 
     rowConstraint.add(labels); 
     rowConstraint.add(tfields); 

     //Creación segundo panel superior
     JPanel topTwo = new JPanel();
     topTwo.setLayout(new BorderLayout());
     topTwo.add(rowInherit,BorderLayout.NORTH);
     topTwo.add(rowConstraint,BorderLayout.CENTER);
     topTwo.setBorder(title);

     //Panel superior total 
     JPanel topPanel = new JPanel();
     topPanel.setLayout(new BorderLayout());
     topPanel.add(topOne,BorderLayout.NORTH);
     topPanel.add(topTwo,BorderLayout.SOUTH);
  
     //Construción parte inferior izquierda de la ventana

     JPanel leftPanel = new JPanel();
     textField3 = new JTextField(12);
     final String[] fields = {" "};
     fieldJList = new JList (fields);

     final JScrollPane componente = new JScrollPane(fieldJList);

     MouseListener mouseListener = new MouseAdapter() {

        public void mousePressed(MouseEvent e) {

         int index = fieldJList.locationToIndex(e.getPoint());

         if ((e.getClickCount() == 1) && (index > -1) && (fieldsN.size() > 0)) {

             componente.requestFocus();
             String fieldMod = (String) fieldsN.elementAt(index);
             TableFieldRecord field = (TableFieldRecord) HashFields.get(fieldMod);
             OptionField opF = field.getOptions();
             textField3.setText(field.getName());
             String typeTmp = field.getType(); 

             if (typeTmp.startsWith("varchar") || typeTmp.startsWith("char")) {

                 int i = typeTmp.indexOf("(");

                 if (i > 0) {

                     String subs = typeTmp.substring(0,i);
                     typeFieldCombo.setSelectedItem(subs);
                     int charL = opF.getCharLong();
                     if (charL > 0)
                         textField5.setText("" + charL);
                  }
                 else {
                       typeFieldCombo.setSelectedItem(typeTmp);
                       textField5.setText("");
                  }
               }
             else
               typeFieldCombo.setSelectedItem(typeTmp);

             if (opF.getDefaultValue().length()>0) {

                 CBr3.setSelected(true);
                 textField6.setEnabled(true);
                 textField6.setEditable(true);
                 textField6.setText(opF.getDefaultValue());
              }
             else {
                    if (CBr3.isSelected()) {

                        CBr3.setSelected(false);
                        textField6.setText("");
                        textField6.setEditable(false);
                        textField6.setEnabled(false);
                     }
                  }

             if (opF.getCheck().length()>0) {

                 CBr4.setSelected(true);
                 textField7.setEnabled(true);
                 textField7.setEditable(true);
                 textField7.setText(opF.getCheck());
              }
             else {
                    if (CBr4.isSelected()) {
                        CBr4.setSelected(false);
                        textField7.setText("");
                        textField7.setEditable(false);
                        textField7.setEnabled(false);
                     }
              }

            if (opF.isNullField())
                notNullButton.setSelected(true);
            else
                notNullButton.setSelected(false);

            int flag = 0;

            if (opF.isPrimaryKey()) {

                isKey.setSelected(true);
                setAvalaibleKey(true);
                primaryButton.setSelected(true);
                uniqueButton.setSelected(false);
                flag = 1;
             }
            else {
                  if(opF.isUnicKey()) {

                     isKey.setSelected(true);
                     setAvalaibleKey(true);
                     uniqueButton.setSelected(true);
                     primaryButton.setSelected(false);
                     flag = 2;
                   }
             }

            if (opF.isForeingKey()) {

                 switch (flag) {
                         case 0: 
                                uniqueButton.setSelected(false);
                                primaryButton.setSelected(false);
                                break;
                         case 1:
                                uniqueButton.setSelected(false);
                                primaryButton.setSelected(true);
                                break;
                         case 2:
                                uniqueButton.setSelected(true);
                                primaryButton.setSelected(false);
                                break;
                  }

                 isKey.setSelected(true);
                 setAvalaibleKey(true);
                 ForeingButton.setSelected(true);
                 setAvalaibleFKey(true);

                 cmbReferences.setSelectedItem(opF.getTableR());
                 cmbFields.setSelectedItem(opF.getFieldR());

                 flag = 1;        
              }
            else {
                  ForeingButton.setSelected(false);
                  setAvalaibleFKey(false);
              }

            if (flag == 0) {

                isKey.setSelected(false);
                setAvalaibleKey(false);
                setAvalaibleFKey(false);
             }

           }

      }
     };

     fieldJList.addMouseListener(mouseListener);


     leftPanel.setLayout(new BorderLayout());

     JToolBar iconBar = new JToolBar(SwingConstants.VERTICAL);
     iconBar.setFloatable(false);

     URL imgURL = getClass().getResource("/icons/16_AddField.png");
     JButton addField = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
     addField.setActionCommand("ADD-F");
     addField.setToolTipText(Language.getWord("ADDF"));
     addField.addActionListener(this);

     iconBar.add(addField);

     imgURL = getClass().getResource("/icons/16_UpdateRecord.png");
     JButton updateField = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
     updateField.setActionCommand("UP-F");
     updateField.setToolTipText(Language.getWord("UPDREC"));
     updateField.addActionListener(this);
     iconBar.add(updateField);

     imgURL = getClass().getResource("/icons/16_del.png"); 
     delField = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
     iconBar.add(delField);
     delField.setActionCommand("DEL-ONE");
     delField.setToolTipText(Language.getWord("DROPF"));
     delField.addActionListener(this);

     imgURL = getClass().getResource("/icons/16_delAll.png");
     delAll = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
     delAll.setActionCommand("DEL-ALL");
     delAll.setToolTipText(Language.getWord("DELALL"));
     delAll.addActionListener(this);

     iconBar.add(delAll);
     delField.setEnabled(false);
     delAll.setEnabled(false);

     JPanel downLeft = new JPanel();
     downLeft.setLayout(new BorderLayout());
     downLeft.add(iconBar,BorderLayout.WEST);
     downLeft.add(componente,BorderLayout.CENTER);

     leftPanel.add(downLeft,BorderLayout.CENTER);
  
     // Construcción parte inferior derecha de la ventana

     JPanel line1 = new JPanel();

     String[] values = {"int2","int4","int8","varchar","char","text","decimal","float4","float8","numeric","serial",
     "money","name","time","timetz","timestamp","interval","date",
     "bool","point","line","lseg","box","path","polygon","circle","cidr","inet"};

      typeFieldCombo = new JComboBox(values);
      typeFieldCombo.setActionCommand("TYPES");
      typeFieldCombo.addActionListener(this);

      JPanel bar = new JPanel();
      bar.setLayout(new GridLayout(1,2));
      bar.add(new JLabel(Language.getWord("NAME")+": ")); 
      bar.add(textField3);
  
      line1.setLayout(new GridLayout(1,2));
      line1.add(new JLabel(Language.getWord("TYPE")+": ")); 
      line1.add(typeFieldCombo); 
  
      JPanel line2 = new JPanel();
      JLabel msgString5 = new JLabel(Language.getWord("LENGHT")+": ");
      textField5 = new JTextField(12);
      textField5.setEditable(false);
      textField5.setEnabled(false);

      line2.setLayout(new GridLayout(1,2));
      line2.add(msgString5); 
      line2.add(textField5);
  
      JPanel line3 = new JPanel();
      CBr3 = new JCheckBox();
      CBr3.setActionCommand("CHECK-DEFV");
      CBr3.addActionListener(this);
      textField6 = new JTextField(12);
      textField6.setEditable(false);
      textField6.setEnabled(false);

      JPanel side = new JPanel();
      side.setLayout(new BorderLayout());
      side.add(CBr3,BorderLayout.WEST);
      side.add(new JLabel(Language.getWord("DEFVALUE")+": "),BorderLayout.CENTER);
      
      line3.setLayout(new GridLayout(0,2));
      line3.add(side); 
      line3.add(textField6);

      JPanel line4 = new JPanel();
      CBr4 = new JCheckBox();
      CBr4.setActionCommand("CHECK-REV");
      CBr4.addActionListener(this);

      textField7 = new JTextField(12);
      textField7.setEditable(false);
      textField7.setEnabled(false);

      JPanel side2 = new JPanel();
      side2.setLayout(new BorderLayout());
      side2.add(CBr4,BorderLayout.WEST);
      side2.add(new JLabel(Language.getWord("CHECK")+": "),BorderLayout.CENTER);
      
      line4.setLayout(new GridLayout(0,2));
      line4.add(side2); 
      line4.add(textField7);

      rowReferences = new JPanel();
      msgReferences = new JLabel(Language.getWord("REFER")+": ");
      rowReferences.setLayout(new GridLayout(0,2));
      rowReferences.add(msgReferences);
      rowReferences.add(cmbReferences);

      rowFields = new JPanel();
      msgFields = new JLabel(Language.getWord("FIELD")+": ");
      rowFields.setLayout(new GridLayout(0,2));
      rowFields.add(msgFields);
      rowFields.add(cmbFields);

      //Creacion Grid de CheckBox

      primaryButton = new JRadioButton(Language.getWord("PKEY"));
      primaryButton.setMnemonic('p'); 
      primaryButton.setEnabled(false);
      primaryButton.setActionCommand("PB");
      primaryButton.addActionListener(this);

      uniqueButton = new JRadioButton(Language.getWord("UKEY"));
      uniqueButton.setMnemonic('u'); 
      uniqueButton.setEnabled(false);
      uniqueButton.setActionCommand("UB");
      uniqueButton.addActionListener(this);

      ForeingButton = new JCheckBox(Language.getWord("FOKEY"));
      ForeingButton.setMnemonic('F');
      ForeingButton.setActionCommand("FKEY");
      ForeingButton.addActionListener(this);

      notNullButton = new JCheckBox(Language.getWord("NOTNULL"));
      notNullButton.setMnemonic('o'); 
      notNullButton.setSelected(false);

      isKey = new JCheckBox(Language.getWord("ISKEY"));
      isKey.setMnemonic('n'); 
      isKey.setSelected(false);
      isKey.setActionCommand("SKEY");
      isKey.addActionListener(this);

      JPanel line5 = new JPanel();
      line5.setLayout(new FlowLayout(FlowLayout.CENTER));
      line5.add(isKey);
      line5.add(notNullButton);

      checkPanel = new JPanel();
      checkPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      checkPanel.add(primaryButton);
      checkPanel.add(uniqueButton);
      checkPanel.add(ForeingButton);

      titleK = BorderFactory.createTitledBorder(etched,Language.getWord("OPKEY"));
      currentColor = titleK.getTitleColor();
      checkPanel.setBorder(titleK);
    
      combosPanel = new JPanel();
      combosPanel.setLayout(new BoxLayout(combosPanel,BoxLayout.Y_AXIS));
      combosPanel.add(rowReferences);
      combosPanel.add(rowFields);

      titleFK = BorderFactory.createTitledBorder(etched,Language.getWord("FORS"));
      combosPanel.setBorder(titleFK); 
      combosPanel.setPreferredSize(new Dimension(100,80));

      JPanel block = new JPanel();
      block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
      block.add(bar);
      block.add(line1);
      block.add(line2);
      block.add(line3);
      block.add(line4);
      block.add(line5);

      TitledBorder titleBlock = BorderFactory.createTitledBorder(etched);
      block.setBorder(titleBlock);

      JPanel rightTop = new JPanel();
      rightTop.setLayout(new BoxLayout(rightTop, BoxLayout.Y_AXIS));

      rightTop.add(block);
      rightTop.add(checkPanel);
      rightTop.add(combosPanel);

      JPanel rightPanel = new JPanel();
      rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
      rightPanel.add(rightTop);

      title = BorderFactory.createTitledBorder(etched, Language.getWord("PROPF"));
      title.setTitleJustification(TitledBorder.LEFT);
      rightPanel.setBorder(title);

      //Unión de todos los paneles inferiores de la ventana en un GridBadLayout
      title = BorderFactory.createTitledBorder(etched,Language.getWord("FLIST"));
      title.setTitleJustification(TitledBorder.CENTER);
      leftPanel.setBorder(title);

      setAvalaibleKey(false);
      setAvalaibleFKey(false);

      JPanel downPanel = new JPanel();
      downPanel.setLayout(new BoxLayout(downPanel,BoxLayout.X_AXIS));
      downPanel.add(rightPanel);
      downPanel.add(leftPanel);

      JPanel botones = new JPanel();
      botones.setLayout(new FlowLayout(FlowLayout.CENTER));

      JButton ok = new JButton(Language.getWord("CREATE"));
      ok.setActionCommand("OK");
      ok.addActionListener(this);

      JButton cancel = new JButton(Language.getWord("CANCEL"));
      cancel.setActionCommand("CANCEL");
      cancel.addActionListener(this);
      botones.add(ok);
      botones.add(cancel);
 
      JPanel global = new JPanel();
      global.setLayout(new BoxLayout(global,BoxLayout.Y_AXIS));
      global.add(topPanel);
      global.add(downPanel);
      global.add(botones);

      getContentPane().add(global);
      pack();
      setLocationRelativeTo(fmain);
      setVisible(true);

 }

 public void actionPerformed(java.awt.event.ActionEvent e) {

   if (e.getActionCommand().equals("OK")) {

          String SQL = "";
          CurrentTable = textTable.getText();

          if (CurrentTable.length()>0) {

              if (CurrentTable.indexOf(" ") == -1) {

                  if (fieldsN.size()==0) {

                      JOptionPane.showMessageDialog(CreateTable.this,
                      Language.getWord("NOFCR"),
                      Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

                      return;
                   }

                SQL = "CREATE TABLE \"" + CurrentTable + "\" (";

                for (int i=0;i<fieldsN.size();i++) {

                     TableFieldRecord tmp = (TableFieldRecord) HashFields.get(fieldsN.elementAt(i));
                     SQL += " \"" + tmp.getName() + "\" " + tmp.getType();

                     if (tmp.getOptions().isNullField())
                         SQL += " NOT NULL";

                     if (tmp.getOptions().isUnicKey())
                         SQL += " UNIQUE";

                     if (tmp.getOptions().isPrimaryKey())
                         SQL += " PRIMARY KEY";

                     if (tmp.getOptions().getDefaultValue().length()>0)
                         SQL += " DEFAULT " + tmp.getOptions().getDefaultValue();

                     if (tmp.getOptions().getCheck().length()>0)
                         SQL += " CHECK " + tmp.getOptions().getCheck();

                     if (tmp.getOptions().isForeingKey())
                         SQL += " REFERENCES \"" + tmp.getOptions().getTableR() + "\" (\"" + tmp.getOptions().getFieldR() + "\")";

                     if (i!=fieldsN.size()-1)
  		         SQL += ",";
		 }

                 if (consActive) {

                     String ctr = "";
                     for(int i=0;i<ConstNames.size();i++)
                       ctr += ", CONSTRAINT " + ConstNames.elementAt(i) + " " + ConstDef.elementAt(i); 
                     SQL += ctr;
                  }

                 if (CBCheck.isSelected()) {

                     String tC = textCheck2.getText();

                     if (tC.length()>0)
                         SQL += ", CHECK (" + tC + ")";    
                  }

                 SQL += ")";

                 if (inheritActive)
                     SQL += " INHERITS (" + inheritString + ")";

                 SQL += ";";
		 addTextLogMonitor(Language.getWord("EXEC")+ SQL + "\"");

                 String result = current.SQL_Instruction(SQL);

                 if (result.equals("OK")) {
		     wellDone = true;
                     setVisible(false); 
                  }
                 else {
                       result = result.substring(0,result.length() - 1);
                       JOptionPane.showMessageDialog(CreateTable.this,                               
                       Language.getWord("ERRORPOS") + result,                       
                       Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                  }
                 addTextLogMonitor(Language.getWord("RES")+ result);
                 return;
	       }
              else {
                     JOptionPane.showMessageDialog(CreateTable.this,
                     Language.getWord("TNIVCH"),                       
                     Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                     return;
                }   
           }
          else {
                 JOptionPane.showMessageDialog(CreateTable.this,                               
                 Language.getWord("TNNCH"),                       
                 Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);               
           }
          return;
      }

   if (e.getActionCommand().equals("CANCEL")) {

       setVisible(false);
       return;
    }

   if (e.getActionCommand().equals("COMBO")) {

       dbn = (String) cmbDB.getSelectedItem();

       int index = dbNames.indexOf(dbn);
       current = (PGConnection) vecConn.elementAt(index);

       TableList = current.TableQuery("SELECT tablename FROM pg_tables where tablename !~ '^pg_' AND tablename  !~ '^pga_' ORDER BY tablename");
       rowReferences.remove(cmbReferences);
       rowFields.remove(cmbFields);

       if (TableList.size()>0) {

           if (isKey.isSelected()) {

               if (!ForeingButton.isEnabled())
                   ForeingButton.setEnabled(true);
            }

           String[] tables = new String[TableList.size()];

           for (int i=0;i<TableList.size();i++) {

                Object o = TableList.elementAt(i);
                String db = o.toString();
                tables[i] = db.substring(1,db.length()-1);
            }

           Vector listTables = new Vector();
           listTables.addElement(tables[0]);
           Vector tR = current.getTablesStructure(listTables);
           cTable = (Table) tR.elementAt(0);

           cmbFields = new JComboBox(cTable.getTableHeader().getNameFields());
           cmbReferences = new JComboBox(tables);
         }
       else {

             String[] listNull = {Language.getWord("NOTABLES")};
             String[] listNull2 = {Language.getWord("NOREG")};
             cmbFields = new JComboBox(listNull2);
             cmbReferences = new JComboBox(listNull);

             if (isKey.isSelected() && ForeingButton.isEnabled()) {

                 ForeingButton.setSelected(false);
                 ForeingButton.setEnabled(false);
              }
         }

       if (!ForeingButton.isSelected()) {
           cmbReferences.setEnabled(false);
           cmbFields.setEnabled(false);
        }

       cmbReferences.setActionCommand("CMB-REF");
       cmbReferences.addActionListener(this);

       rowReferences.add(cmbReferences);
       rowReferences.updateUI();
       rowFields.add(cmbFields);
       rowFields.updateUI();
   }

   if (e.getActionCommand().equals("BUT-INHE")) {

       String as[] = current.getTablesNames(false);
       num = as.length;

       if (num == 0) {

           JOptionPane.showMessageDialog(CreateTable.this,
           Language.getWord("TNTAB") + current.getDBname() + "'.",
           Language.getWord("INFO"),JOptionPane.INFORMATION_MESSAGE);
           return;
        }

       Inherit inDialog = new Inherit(CreateTable.this,as,tablesH);

     if (inDialog.isWellDone()) { 

         inheritActive = true; 
         inheritString = inDialog.getTableList();
         tablesH = inDialog.getVector();
      }                      
     else { 
           inheritActive = false;
           inheritString = "";
           tablesH = new Vector();
      }
   }

   if (e.getActionCommand().equals("BUT-CONST")) {

       Constraint winCons = new Constraint(CreateTable.this,ConstNames,ConstDef);

       if (winCons.isWellDone()) {

           consActive = true;
           ConstNames = winCons.getConsN();
           ConstDef = winCons.getConsD(); 
        }
       else {
             consActive = false;
             ConstNames = new Vector();
             ConstDef = new Vector();
        }
    }

  if (e.getActionCommand().equals("CHECK-Check")) {

      boolean hab = false;

      if(CBCheck.isSelected())
         hab = true;

      textCheck2.setEnabled(hab);
      textCheck2.setEditable(hab);

      return;
   }

  if (e.getActionCommand().equals("CMB-REF")) {

      String tableN = (String) cmbReferences.getSelectedItem();

      if (!tableN.startsWith(Language.getWord("NOTABLES"))) {

          Vector listTables = new Vector();
          listTables.addElement(tableN);
          Vector tR = current.getTablesStructure(listTables);
          cTable = (Table) tR.elementAt(0);

          rowFields.remove(cmbFields);
          cmbFields = new JComboBox(cTable.getTableHeader().getNameFields());
          rowFields.add(cmbFields);
          rowFields.updateUI();
       }

      return;
   }

  if (e.getActionCommand().equals("CHECK-DEFV")) {

      boolean hab = false;

      if (CBr3.isSelected())
          hab = true;

      textField6.setEnabled(hab);
      textField6.setEditable(hab);

      return;
   }

  if (e.getActionCommand().equals("CHECK-REV")) {

      boolean hab = false;

      if (CBr4.isSelected())
          hab = true;

      textField7.setEnabled(hab);
      textField7.setEditable(hab);

      return;
   }

  if (e.getActionCommand().equals("SKEY")) {

      if (isKey.isSelected())
          setAvalaibleKey(true);
      else 
          setAvalaibleKey(false);

      combosPanel.updateUI();

      return;
   }

  if (e.getActionCommand().equals("PB")) {

      if (primaryButton.isSelected())
          uniqueButton.setSelected(false);

      return;
   }

  if (e.getActionCommand().equals("UB")) {

      if (uniqueButton.isSelected())
          primaryButton.setSelected(false);

      return;
   }

  if (e.getActionCommand().equals("FKEY")) {

      if (ForeingButton.isSelected()) 
          setAvalaibleFKey(true);
      else 
          setAvalaibleFKey(false);

      combosPanel.updateUI();

      return;
   }

  if (e.getActionCommand().equals("TYPES")) {

      String Stype = (String) typeFieldCombo.getSelectedItem();
      boolean flag = false; 

      if (Stype.equals("varchar") || Stype.equals("char"))
          flag = true;

      textField5.setEditable(flag);
      textField5.setEnabled(flag);
      textField5.setText("");

      return;
   }

  if (e.getActionCommand().equals("ADD-F")) {

      capture(0);

      return;
   }

  if (e.getActionCommand().equals("UP-F")) {

      capture(1);

      return;
   }

  if (e.getActionCommand().equals("DEL-ALL")) {

      Vector empty = new Vector();
      fieldJList.setListData(empty);
      fieldsN = new Vector();
      cleanCreateT();
      delField.setEnabled(false);
      delAll.setEnabled(false);

      return;
   }

   if (e.getActionCommand().equals("DEL-ONE")) {

       String del = (String) fieldJList.getSelectedValue();

       if (fieldsN.remove(del)) {

           fieldJList.setListData(fieldsN);
           HashFields.remove(del);

           if (fieldsN.size() == 0) {

               delField.setEnabled(false);
               delAll.setEnabled(false);
            }
        }
     }
  }

 public void setAvalaibleKey(boolean state) {

   if (!state) {

       titleK.setTitleColor(new Color(153,153,153));
       uniqueButton.setSelected(false);
       primaryButton.setSelected(false);
       ForeingButton.setSelected(false);
       ForeingButton.setEnabled(false);
       setAvalaibleFKey(false);
    }
   else {
         titleK.setTitleColor(currentColor);

         String table = (String) cmbReferences.getSelectedItem();

         if (table.equals(Language.getWord("NOTABLES")))
             ForeingButton.setEnabled(false);
         else
             ForeingButton.setEnabled(true);
    }

   checkPanel.updateUI();
   primaryButton.setEnabled(state);
   uniqueButton.setEnabled(state);
 }
 
 public void setAvalaibleFKey(boolean state) {

   if (state) 
       titleFK.setTitleColor(currentColor);
   else {  
         titleFK.setTitleColor(new Color(153,153,153));
         cmbReferences.setSelectedIndex(0);
         cmbFields.setSelectedIndex(0);
    }

   msgFields.setEnabled(state);
   cmbFields.setEnabled(state);
   msgReferences.setEnabled(state);
   cmbReferences.setEnabled(state);
  }

 public void cleanCreateT() {

   textField3.setText("");
   typeFieldCombo.setSelectedIndex(0);
   isKey.setSelected(false);
   setAvalaibleKey(false);
   combosPanel.updateUI();
  }

 public boolean isNum(String value) {

   for (int i=0;i<value.length();i++) {

        char a = value.charAt(i);

        if (!Character.isDigit(a))
            return false;
    }

   return true;
 }

 public void capture(int i) {

   String nameF = textField3.getText();
   boolean inside = false;

   if (nameF.length()>0) {

       if (nameF.indexOf(" ") != -1) {

           JOptionPane.showMessageDialog(CreateTable.this,
           Language.getWord("FNIVCH"),
           Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

           return;
        }  
    }
   else {
          JOptionPane.showMessageDialog(CreateTable.this,
          Language.getWord("FEMPT"),
          Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
          return;
        }

   if (i==0) {

       if (fieldsN.contains(nameF)) {
           JOptionPane.showMessageDialog(CreateTable.this,
           Language.getWord("EMPTEX"),
           Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

           return;
        }
    }
   else {
         if (!fieldsN.contains(nameF)) {
             GenericQuestionDialog addNF = new GenericQuestionDialog(fmain,Language.getWord("OK"),Language.getWord("CANCEL"),Language.getWord("NOEXISF"),Language.getWord("NOEXISF2"));

         boolean sure = addNF.getSelecction();

         if (sure) 
             inside = true;
         else 
             return;
         }
    }

   String Stype = (String) typeFieldCombo.getSelectedItem();
   String DefaultValue = "";
   String referT = "";
   String referF = "";
   boolean notNull = false;
   boolean fkey = false;
   boolean key = false;
   boolean pkey = false;
   boolean ukey = false;
   int chL = -1;
   int IntL = -1;

   if (Stype.equals("varchar") || Stype.equals("char")) {

       if (textField5.isEnabled()) {

           String longi = textField5.getText();

           if (!isNum(longi)) {
               JOptionPane.showMessageDialog(CreateTable.this,
               Language.getWord("INVLENGHT"),
               Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

               return;
            }

           if (longi.length() > 0) {
               Stype = Stype + "(" + longi + ")";
               chL = Integer.parseInt(longi);
            }
        }
    }
   else
     IntL = 2;

   if (CBr3.isSelected())
       DefaultValue = textField6.getText();

   if (notNullButton.isSelected())
       notNull = true;

   if (isKey.isSelected()) {

      key = true;

      if (ForeingButton.isSelected()) {

          fkey = true;
          referT = (String) cmbReferences.getSelectedItem();
          referF = (String) cmbFields.getSelectedItem();
       }

      if (primaryButton.isSelected())
          pkey = true;

      if (uniqueButton.isSelected())
          ukey = true;
    }

   OptionField opF = new OptionField(Stype,chL,IntL,notNull,pkey,ukey,fkey,DefaultValue);

   if (fkey)
       opF.setRefVal(referT,referF);

   if (CBr4.isSelected())
       opF.Check = textField7.getText();

   TableFieldRecord field = new TableFieldRecord(nameF,Stype,opF);
   HashFields.put(nameF,field);

   if (i==0 || inside) {

       if (fieldsN.size() == 0) {
           delField.setEnabled(true);
           delAll.setEnabled(true);
        }

       fieldsN.addElement(nameF);
       fieldJList.setListData(fieldsN);
       cleanCreateT();
    }
  }

 /**
  * Metodo addTextLogMonitor
  * Imprime mensajes en el Monitor de Eventos
  */
  public void addTextLogMonitor(String msg) {

    LogWin.append(msg + "\n");
    int longiT = LogWin.getDocument().getLength();

    if (longiT > 0)
        LogWin.setCaretPosition(longiT - 1);
   } 

 /**
  * Metodo getWellDone 
  * 
  */
  public boolean getWellDone() {
     return wellDone;
   }

} //Fin de la Clase
