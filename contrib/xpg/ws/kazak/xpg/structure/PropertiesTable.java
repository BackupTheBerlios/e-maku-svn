/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS PropertiesTable v 0.1
* Descripcion:
* Esta clase se encarga de manejar el dialogo mediante el cual se
* pueden ver y modificar algunas de las caracteristicas de una tabla.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*
* Fecha: 2001/10/01
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws
*          Gustavo Gonzalez - xtingray@kazak.ws
*/
package ws.kazak.xpg.structure;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.misc.input.ErrorDialog;

class PropertiesTable extends JDialog implements ActionListener,KeyListener,FocusListener{

  private JOptionPane optionPane;
  private boolean DEBUG = true;
  Object[][] data = {{"", "", "", ""}};
  String[] columnNames = new String[5];
  JTable table;
  JScrollPane tableSpace2;
  PGConnection current_conn;
  String currentTable;
  String SeqName,ViewName;
  JList sequenList,viewList;
  JScrollPane componente,vcomp;
  Vector sequences,views;
  JTextField nameText,incrementText,minText,maxText,cacheText,vnText;
  JComboBox cyCombo;
  JTextArea LogWin,selectArea;
  JFrame parent;
  int index,vindex;

 public PropertiesTable (JFrame aFrame,PGConnection kon,String Ctable,JTextArea LW) {

  super(aFrame, true);
  parent = aFrame; 
  current_conn = kon;
  currentTable = Ctable;
  LogWin = LW;
  setTitle(Language.getWord("PROPTABLE"));

  URL imgURL = getClass().getResource("/icons/16_table.png");
  ImageIcon iconPermission = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL));
  imgURL = getClass().getResource("/icons/16_Datas.png");
  ImageIcon iconConstraint = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL));
  imgURL = getClass().getResource("/icons/16_Datas.png");
  ImageIcon iconCheck = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL));
  JTabbedPane tabbedPane = new JTabbedPane();

  //Pestaña secuencias
  tabbedPane.addTab(Language.getWord("VIEWS"), iconConstraint, Views(),Language.getWord("VIEWS"));

  tabbedPane.addTab(Language.getWord("SEQUENCE"), iconConstraint, Sequences(),Language.getWord("SEQUENCE"));

  JPanel constraintPanel = new JPanel();
  tabbedPane.addTab(Language.getWord("CONST"),iconConstraint, constraintPanel,Language.getWord("CONST"));

  JPanel checkPanel = new JPanel();
  tabbedPane.addTab(Language.getWord("CHECK"), iconCheck, checkPanel,Language.getWord("CHECK"));

  tabbedPane.addTab(Language.getWord("COMM"), iconCheck, Comment(),Language.getWord("COMM"));

  JPanel Global = new JPanel();
  Global.setLayout(new BoxLayout(Global,BoxLayout.Y_AXIS));
  Global.add(tabbedPane);
  JButton btnCancel = new JButton(Language.getWord("CLOSE"));
  btnCancel.setActionCommand("CANCEL");
  btnCancel.addActionListener(this);
  JPanel botons = new JPanel();
  botons.setLayout(new FlowLayout(FlowLayout.CENTER));
  botons.add(btnCancel);
  Global.add(botons);

  getContentPane().add(Global);

}

 public boolean isValid(String name,int type) {

   String errMsg = Language.getWord("INVSEQ"); 

   if(type==0)
      errMsg = Language.getWord("INVVIE");

   if(name.length()<1 || name.indexOf(" ")!= -1) 
      {
          JOptionPane.showMessageDialog(PropertiesTable.this,                               
          errMsg,                       
          Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
           return false;
      }
   return true;
  }


 public void actionPerformed(java.awt.event.ActionEvent e) {

   if (e.getActionCommand().equals("CLEAN")) {
       clearForm();

       return;
    }

   if (e.getActionCommand().equals("VCLEAN")) {
       VclearForm();

       return;
    }

   if (e.getActionCommand().equals("VCREATE")) {

       ViewName = vnText.getText();

       String SQL = "CREATE VIEW \"" + ViewName + "\" AS ";

       if (ViewName.length() == 0) {

           JOptionPane.showMessageDialog(PropertiesTable.this,
           Language.getWord("EMPTY"),
           Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

           return;
        }

       if (!isValid(ViewName,0))
           return;

       if (views.contains(ViewName)) {

           JOptionPane.showMessageDialog(PropertiesTable.this,
           Language.getWord("VEXIS") + ViewName + "'" + Language.getWord("SEQEXIS2"),
           Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
           return;
         }

       SQL += selectArea.getText();

       addTextLogMonitor(Language.getWord("EXEC")+ SQL + ";\"");

       String result = current_conn.SQL_Instruction(SQL);

       if (result.equals("OK")) {
           views.addElement(ViewName);
           views = sorting(views);
           viewList.setListData(views);
           viewList.setSelectedValue(ViewName,true);
        }
       else {
             result = result.substring(0,result.length()-1);
             ErrorDialog showError = new ErrorDialog(PropertiesTable.this,current_conn.getErrorMessage());
             showError.pack();
             showError.setLocationRelativeTo(parent);
             showError.show();
        }

       addTextLogMonitor(Language.getWord("RES") + result);

    return;
  }

 if (e.getActionCommand().equals("CREATE")) {

     SeqName = nameText.getText();

     String SQL = "CREATE SEQUENCE \"" + SeqName + "\"";

     if (SeqName.length() == 0) { 
         JOptionPane.showMessageDialog(PropertiesTable.this,
         Language.getWord("EMPTY"),
         Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

         return;
      }

     if (!isValid(SeqName,1))
         return; 

     if (sequences.contains(SeqName)) {

         JOptionPane.showMessageDialog(PropertiesTable.this,
         Language.getWord("SEQEXIS") + SeqName + Language.getWord("SEQEXIS2"),
         Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

         return;
      }

     String Incr = incrementText.getText();

     if (Incr.length() > 0) {

         if (isNum(Incr))
             SQL += " INCREMENT " + Incr;
         else {
               showErrorInfo(Language.getWord("INCR"));
               return;
          }
      }

     String Min = minText.getText();

     if (Min.length() > 0) {
         if (isNum(Min))
             SQL += " MINVALUE " + Min;
         else {
               showErrorInfo(Language.getWord("MV"));
               return;
          }
      }

     String Max = maxText.getText();

     if (Max.length() > 0) {
         if (isNum(Max))
             SQL += " MAXVALUE " + Max;
         else {
               showErrorInfo(Language.getWord("MXV"));
               return;
          }
      }

     String Cach = cacheText.getText();

     if (Cach.length() > 0) {
         if (isNum(Cach))
             SQL += " CACHE " + Cach;
         else {
               showErrorInfo(Language.getWord("CACH"));
               return;
          }
      }

     String boolV = (String) cyCombo.getSelectedItem();

     if (boolV.equals(Language.getWord("YES")))
         SQL += " CYCLE";  

     addTextLogMonitor(Language.getWord("EXEC")+ SQL + ";\"");
     String result = current_conn.SQL_Instruction(SQL);

     if (result.equals("OK")) {

         sequences.addElement(SeqName);
         sequences = sorting(sequences);
         sequenList.setListData(sequences);
         sequenList.setSelectedValue(SeqName,true);
      }
     else {
            result = result.substring(0,result.length()-1);
            ErrorDialog showError = new ErrorDialog(PropertiesTable.this,current_conn.getErrorMessage());
            showError.pack();
            showError.setLocationRelativeTo(parent);
            showError.show();
      }

     addTextLogMonitor(Language.getWord("RES") + result);

     return;
 }

 if (e.getActionCommand().equals("VUPDATE")) {

     if (!viewList.isSelectionEmpty()) {

         String SQL1 = "DROP VIEW ";
         String SQL2 = "CREATE VIEW ";

         ViewName = vnText.getText();

         if (ViewName.length() == 0) {
             JOptionPane.showMessageDialog(PropertiesTable.this,
             Language.getWord("EMPTY"),
             Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

             return;
          }

         if (!isValid(ViewName,0))
             return;

         if (!views.contains(ViewName))
             return;

         String viewValue = selectArea.getText();

         if (viewValue.length()<1) {
             JOptionPane.showMessageDialog(PropertiesTable.this,
             Language.getWord("EMPTY"),
             Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

             return;
          }

         SQL1 += "\"" + ViewName + "\"";
         SQL2 += "\"" + ViewName + "\" AS " + viewValue;

         SQLEXEC(SQL1);
         SQLEXEC(SQL2);
      }

     return;
 }

 if (e.getActionCommand().equals("UPDATE")) {

     if (!sequenList.isSelectionEmpty()) {

         String SQL1 = "DROP SEQUENCE ";
         String SQL = "CREATE SEQUENCE ";
         SeqName = nameText.getText();

         if (SeqName.length() == 0) {

             JOptionPane.showMessageDialog(PropertiesTable.this,
             Language.getWord("EMPTY"),
             Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

             return;
          }

         if (!isValid(SeqName,1))
             return;

         if (!sequences.contains(SeqName))
             return;

         SQL += "\"" + SeqName + "\"";
         SQL1 += "\"" + SeqName + "\""; 

         String Incr = incrementText.getText();

         if (Incr.length() > 0) {

             if (isNum(Incr))
                 SQL += " INCREMENT " + Incr;
             else {
                   showErrorInfo(Language.getWord("INCR"));
                   return;
              }
          }

         String Min = minText.getText();

         if (Min.length() > 0) {
             if (isNum(Min))
                 SQL += " MINVALUE " + Min;
             else {
                   showErrorInfo(Language.getWord("MV"));
                   return;
              }
          }

         String Max = maxText.getText();

         if (Max.length() > 0) {

             if (isNum(Max))
                 SQL += " MAXVALUE " + Max;
             else {
                   showErrorInfo(Language.getWord("MXV"));
                   return;
              }
          }

         String Cach = cacheText.getText();

         if (Cach.length() > 0) {
             if (isNum(Cach))
                 SQL += " CACHE " + Cach;
             else {
                   showErrorInfo(Language.getWord("CACH"));
                   return;
                  }
          }

         String boolV = (String) cyCombo.getSelectedItem();

         if (boolV.equals(Language.getWord("YES")))
             SQL += " CYCLE";

         SQLEXEC(SQL1);
         SQLEXEC(SQL);
     }
 
  return;
 }

 if (e.getActionCommand().equals("VDROP")) { 

     if (!viewList.isSelectionEmpty())
         DropVIE();
  }


 if (e.getActionCommand().equals("DROP")) {

     if (!sequenList.isSelectionEmpty()) 
         DropSEQ();
  }

 if (e.getActionCommand().equals("CANCEL")) {

     setVisible(false);
  }

 }

 public JPanel Views() {

  views = new Vector();
  String select = "SELECT relname FROM pg_class WHERE relname !~ '^pg_' AND relkind = 'v' ORDER BY relname";
  Vector vres = current_conn.TableQuery(select);
  addTextLogMonitor(Language.getWord("EXEC")+ select + ";\"");

  if (vres.size()>0) {

      for (int p=0;p<vres.size();p++) {
           Vector tmp = (Vector) vres.elementAt(p);
           String str = (String) tmp.elementAt(0);
           views.addElement(str);
       }
   }

  viewList = new JList (views);
  vcomp = new JScrollPane(viewList);
  vcomp.setPreferredSize(new Dimension(90,100));
  vcomp.addFocusListener(this);

  MouseListener mouseListener = new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
         vindex = viewList.locationToIndex(e.getPoint());
         if (e.getClickCount() == 1 && vindex > -1) {
             vcomp.requestFocus();
             ViewsetForm();
          }
         else
             vnText.requestFocus();
       }
    };

  viewList.addMouseListener(mouseListener);

  vnText = new JTextField(12);
  selectArea = new JTextArea();
  selectArea.setLineWrap(true);
  JScrollPane win = new JScrollPane(selectArea);
  win.setPreferredSize(new Dimension(300,90));

  JPanel winP = new JPanel();
  winP.add(win);
  Border etched1 = BorderFactory.createEtchedBorder();
  TitledBorder title1 = BorderFactory.createTitledBorder(etched1,"AS"); 
  winP.setBorder(title1);

  JPanel oneR = new JPanel();
  oneR.setLayout(new BorderLayout());
  oneR.add(new JLabel(Language.getWord("NAME") + " : "),BorderLayout.WEST);
  oneR.add(vnText,BorderLayout.CENTER);

  JPanel cop = new JPanel();
  cop.add(oneR);

  JPanel left = new JPanel(new BorderLayout());
  left.add(cop,BorderLayout.NORTH);
  left.add(winP,BorderLayout.CENTER);
  Border margin = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
  left.setBorder(margin);

  JPanel sequenceAux2 = new JPanel();
  sequenceAux2.setLayout(new BorderLayout());
  sequenceAux2.add(left,BorderLayout.CENTER);
  sequenceAux2.add(vcomp,BorderLayout.EAST);

  JButton clrButton = new JButton(Language.getWord("CLR"));
  clrButton.setVerticalTextPosition(AbstractButton.CENTER);
  clrButton.setMnemonic(Language.getNemo("CLR"));
  clrButton.setActionCommand("VCLEAN");
  clrButton.addActionListener(this);

  JButton dButton = new JButton(Language.getWord("CREATE"));
  dButton.setVerticalTextPosition(AbstractButton.CENTER);
  dButton.setMnemonic(Language.getNemo("CREATE"));
  dButton.setActionCommand("VCREATE");
  dButton.addActionListener(this);

  JButton updateButton = new JButton(Language.getWord("UPDT"));
  updateButton.setVerticalTextPosition(AbstractButton.CENTER);
  updateButton.setMnemonic(Language.getNemo("UPDT"));
  updateButton.setActionCommand("VUPDATE");
  updateButton.addActionListener(this);

  JButton dropButton = new JButton(Language.getWord("DROP"));
  dropButton.setVerticalTextPosition(AbstractButton.CENTER);
  dropButton.setMnemonic(Language.getNemo("DROP"));
  dropButton.setActionCommand("VDROP");
  dropButton.addActionListener(this);

  JPanel ButtonP = new JPanel();
  ButtonP.setLayout(new FlowLayout(FlowLayout.CENTER));
  ButtonP.add(clrButton);
  ButtonP.add(dButton);
  ButtonP.add(updateButton);
  ButtonP.add(dropButton);

  JPanel vPanel = new JPanel();
  vPanel.setLayout(new BorderLayout());
  vPanel.add(sequenceAux2,BorderLayout.CENTER);
  vPanel.add(ButtonP,BorderLayout.SOUTH);

  JPanel glob = new JPanel();
  glob.add(vPanel);

  return glob;

 }

 public JPanel Sequences()
 {
  sequences = new Vector();
  String select = "SELECT relname FROM pg_class WHERE relkind = 'S' ORDER BY relname";
  Vector seq = current_conn.TableQuery(select);
  addTextLogMonitor(Language.getWord("EXEC")+ select + ";\"");

  if (seq.size()>0) {

      for (int p=0;p<seq.size();p++) {
           Vector tmp = (Vector) seq.elementAt(p);
           String str = (String) tmp.elementAt(0);   
           sequences.addElement(str);
       }
   }

  sequenList = new JList (sequences);
 
  JPanel rowName = new JPanel();
  nameText = new JTextField(12);
  rowName.setLayout(new BorderLayout());
  rowName.add(new JLabel(Language.getWord("NAME") + ": "),BorderLayout.WEST);
  rowName.add(nameText,BorderLayout.EAST);

  JPanel rowIncrement = new JPanel();
  incrementText = new JTextField(12);
  rowIncrement.setLayout(new BorderLayout());
  rowIncrement.add(new JLabel(Language.getWord("INCR") + ": "),BorderLayout.WEST);
  rowIncrement.add(incrementText,BorderLayout.EAST);
  
  JPanel rowMin = new JPanel();
  minText = new JTextField(12);
  rowMin.setLayout(new BorderLayout());      
  rowMin.add(new JLabel(Language.getWord("MV") + ": "),BorderLayout.WEST);
  rowMin.add(minText,BorderLayout.EAST);
 
  JPanel rowMax = new JPanel();
  maxText = new JTextField(12);
  rowMax.setLayout(new BorderLayout());      
  rowMax.add(new JLabel(Language.getWord("MXV") + ": "),BorderLayout.WEST);
  rowMax.add(maxText,BorderLayout.EAST);

  JPanel rowCache = new JPanel();
  cacheText = new JTextField(12);
  rowCache.setLayout(new BorderLayout());
  rowCache.add(new JLabel(Language.getWord("CACH") + ": "),BorderLayout.WEST);
  rowCache.add(cacheText,BorderLayout.EAST);

  JPanel rowCy = new JPanel();

  String[] boolvar = {Language.getWord("NO"),Language.getWord("YES")};
  cyCombo = new JComboBox(boolvar);

  rowCy.setLayout(new GridLayout(1,2));
  rowCy.add(new JLabel(Language.getWord("CYC") + ": "));
  rowCy.add(cyCombo);

  JButton clrButton = new JButton(Language.getWord("CLR"));
  clrButton.setVerticalTextPosition(AbstractButton.CENTER);
  clrButton.setMnemonic(Language.getNemo("CLR"));
  clrButton.setActionCommand("CLEAN");
  clrButton.addActionListener(this);
 
  JButton defineButton = new JButton(Language.getWord("CREATE"));
  defineButton.setVerticalTextPosition(AbstractButton.CENTER);
  defineButton.setMnemonic(Language.getNemo("CREATE"));
  defineButton.setActionCommand("CREATE");
  defineButton.addActionListener(this);

  JButton updateButton = new JButton(Language.getWord("UPDT"));
  updateButton.setVerticalTextPosition(AbstractButton.CENTER);
  updateButton.setMnemonic(Language.getNemo("UPDT"));
  updateButton.setActionCommand("UPDATE");
  updateButton.addActionListener(this);

  JButton dropButton = new JButton(Language.getWord("DROP"));
  dropButton.setVerticalTextPosition(AbstractButton.CENTER);
  dropButton.setMnemonic(Language.getNemo("DROP"));
  dropButton.setActionCommand("DROP");
  dropButton.addActionListener(this);

  JPanel ButtonP = new JPanel();
  ButtonP.setLayout(new FlowLayout(FlowLayout.CENTER));
  ButtonP.add(clrButton);
  ButtonP.add(defineButton);
  ButtonP.add(updateButton);
  ButtonP.add(dropButton);

  Border etched = BorderFactory.createEtchedBorder();
  JPanel sequenceAux1 = new JPanel();
  sequenceAux1.setLayout(new BoxLayout(sequenceAux1,BoxLayout.Y_AXIS));
  sequenceAux1.add(rowName);
  sequenceAux1.add(rowIncrement);
  sequenceAux1.add(rowMin);
  sequenceAux1.add(rowMax);
  sequenceAux1.add(rowCache);

  JPanel otro = new JPanel();
  otro.setLayout(new FlowLayout(FlowLayout.CENTER));
  otro.add(rowCy);
  sequenceAux1.add(otro);

  TitledBorder title = BorderFactory.createTitledBorder(etched);
  sequenceAux1.setBorder(title);

  componente = new JScrollPane(sequenList);
  componente.setPreferredSize(new Dimension(100,120));   
  componente.addFocusListener(this);

  MouseListener mouseListener = new MouseAdapter() {

       public void mousePressed(MouseEvent e) {

         index = sequenList.locationToIndex(e.getPoint());
         if (e.getClickCount() == 1 && index > -1) {
             componente.requestFocus();
             setForm();
          }
         else
             nameText.requestFocus(); 
        } 
   };

  sequenList.addMouseListener(mouseListener);
  
  JPanel sequenceAux2 = new JPanel();
  sequenceAux2.setLayout(new BorderLayout());      
  sequenceAux2.add(sequenceAux1,BorderLayout.WEST);
  sequenceAux2.add(componente,BorderLayout.CENTER);

  JPanel sequencePanel = new JPanel();
  sequencePanel.setLayout(new BorderLayout());
  sequencePanel.add(sequenceAux2,BorderLayout.CENTER);
  sequencePanel.add(ButtonP,BorderLayout.SOUTH);

  JPanel glob = new JPanel();
  glob.add(sequencePanel);

  return glob;
 }

 public JPanel Comment() {
   String select = "SELECT b.description FROM pg_class a,pg_description b WHERE a.relname='" + currentTable + "' AND a.relfilenode=b.objoid";
   Vector rescom = current_conn.TableQuery(select);
   addTextLogMonitor(Language.getWord("EXEC")+ select + ";\"");

   String valComment = "";
   Vector val = new Vector();

   if(rescom.size()==0)
     valComment= "";
   else {
         val = (Vector) rescom.elementAt(0);
         valComment = (String) val.elementAt(0); 
    }

   JPanel coP = new JPanel(); 
   coP.setLayout(new BorderLayout());
   JLabel msgC = new JLabel("COMMENT ON TABLE '" + currentTable + "' IS : ",JLabel.CENTER);
   JTextArea coTA = new JTextArea();

   if(valComment.length()>0)
      coTA.setText(valComment);

   JScrollPane win = new JScrollPane(coTA);
   win.setPreferredSize(new Dimension(300,90));

   coP.add(msgC,BorderLayout.NORTH);
   coP.add(win,BorderLayout.CENTER);

   Border etched = BorderFactory.createEtchedBorder();
   TitledBorder title = BorderFactory.createTitledBorder(etched);
   coP.setBorder(title);

   JButton clrButton = new JButton(Language.getWord("CLR"));
   clrButton.setVerticalTextPosition(AbstractButton.CENTER);
   clrButton.setMnemonic(Language.getNemo("CLR"));
   clrButton.setActionCommand("COMMCLEAN");
   clrButton.addActionListener(this);

   JButton updateButton = new JButton(Language.getWord("UPDT"));
   updateButton.setVerticalTextPosition(AbstractButton.CENTER);
   updateButton.setMnemonic(Language.getNemo("UPDT"));
   updateButton.setActionCommand("COMMUPDATE");
   updateButton.addActionListener(this);

   JButton dropButton = new JButton(Language.getWord("DROP"));
   dropButton.setVerticalTextPosition(AbstractButton.CENTER);
   dropButton.setMnemonic(Language.getNemo("DROP"));
   dropButton.setActionCommand("COMMDROP");
   dropButton.addActionListener(this);

   JPanel ButtonP = new JPanel();
   ButtonP.setLayout(new FlowLayout(FlowLayout.CENTER));
   ButtonP.add(clrButton);
   ButtonP.add(updateButton);
   ButtonP.add(dropButton);

   JPanel commentPanel = new JPanel();
   commentPanel.setLayout(new BorderLayout());
   commentPanel.add(coP,BorderLayout.CENTER);
   commentPanel.add(ButtonP,BorderLayout.SOUTH);

   return commentPanel;
  }

  /** Handle the key typed event from the text field. */
  public void keyTyped(KeyEvent e) {
   }

  public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();                           
    String keySelected = KeyEvent.getKeyText(keyCode); //cadena que describe la tecla presionada

    if (keySelected.equals("Delete"))  //si la tecla presionada es delete                                                 
        DropSEQ(); 
     else {
            if (keySelected.equals("Down")) {

                sequenList.clearSelection();
	        if (index < sequences.size() - 1) {
                    sequenList.setSelectedIndex(index + 1); 
	            index++;
	         }
	        else {
                      sequenList.setSelectedIndex(sequences.size() - 1);
                      index = sequences.size() - 1;
                 }
              } 
	     else {

                    if (keySelected.equals("Up")) {

                        if (index > 0 && index < sequences.size()) {
                            sequenList.clearSelection();
                            sequenList.setSelectedIndex(index - 1);
                            index--;
	                 }
	               else {
                              sequenList.setSelectedIndex(0);
                              index = 0;
                        }
                     }
                    else
	                 Toolkit.getDefaultToolkit().beep(); 
	      }

             setForm();
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
      Component tmp = e.getComponent();
      tmp.addKeyListener(this);
    }

 /**
 * METODO focusLost
 */
    public void focusLost(FocusEvent e) {
       Component tmp = e.getComponent();
       tmp.removeKeyListener(this);
     }

    public void clearForm() {

      nameText.setText("");
      incrementText.setText("");
      maxText.setText("");
      minText.setText("");
      cacheText.setText("");
      cyCombo.setSelectedIndex(0);
      sequenList.clearSelection(); 
      nameText.requestFocus();
     }

    public void VclearForm() {

      vnText.setText("");
      selectArea.setText("");
      viewList.clearSelection();
      vnText.requestFocus();
     }

    public void ViewsetForm() {

      ViewName = (String) views.elementAt(vindex);
      String select = "SELECT definition FROM pg_views WHERE viewname='" + ViewName + "'";
      Vector s = current_conn.TableQuery(select);
      addTextLogMonitor(Language.getWord("EXEC")+ select + ";\"");

      if (s.size()>0) {
          Vector data = (Vector) s.elementAt(0);
          vnText.setText(ViewName);
          String description = (String) data.elementAt(0);
          selectArea.setText(description);
       }
    }

  public void setForm() {

      SeqName = (String) sequences.elementAt(index);
      String select = "SELECT increment_by,max_value,min_value,cache_value,is_cycled" + " FROM \"" + SeqName + "\"";
      Vector s = current_conn.TableQuery(select);
      addTextLogMonitor(Language.getWord("EXEC")+ select + ";\"");
      Vector data = (Vector) s.elementAt(0);
      nameText.setText(SeqName);
      Long val = (Long) data.elementAt(0);
      incrementText.setText(val.toString());
      val = (Long) data.elementAt(1);
      maxText.setText(val.toString());
      val = (Long) data.elementAt(2);
      minText.setText(val.toString());
      val = (Long) data.elementAt(3);
      cacheText.setText(val.toString());

      Object iscy = (Object) data.elementAt(4);
      String boolvar = iscy.toString();

      if (boolvar.startsWith("f"))
        cyCombo.setSelectedIndex(1); 
      else 
        cyCombo.setSelectedIndex(0);
    }

 public void DropVIE() {

      String drop = "DROP VIEW \"" + ViewName + "\"";
      addTextLogMonitor(Language.getWord("EXEC")+ drop + ";\"");
      String result = current_conn.SQL_Instruction(drop);

      if (result.equals("OK")) {

          views.remove(vindex);
          viewList.setListData(views);
          VclearForm();
          vnText.requestFocus();
       }
      else {
         result = result.substring(0,result.length()-1);
         ErrorDialog showError = new ErrorDialog(PropertiesTable.this,current_conn.getErrorMessage());
         showError.pack();
         showError.setLocationRelativeTo(parent);
         showError.show();
       }
      addTextLogMonitor(Language.getWord("RES")+ result);
    }

 public void DropSEQ() {

      String drop = "DROP SEQUENCE \"" + SeqName + "\"";
      addTextLogMonitor(Language.getWord("EXEC")+ drop + ";\"");
      String result = current_conn.SQL_Instruction(drop);

      if (result.equals("OK")) {

  	  sequences.remove(SeqName);
          sequenList.setListData(sequences);
	  clearForm();
	  nameText.requestFocus();
        }
       else {
             result = result.substring(0,result.length()-1);
             ErrorDialog showError = new ErrorDialog(PropertiesTable.this,current_conn.getErrorMessage());
             showError.pack();
             showError.setLocationRelativeTo(parent);
             showError.show();
        }

       addTextLogMonitor(Language.getWord("RES")+ result);
     }

  public void SQLEXEC(String SQL) {

   if (!SQL.endsWith(";"))
       SQL += ";";

   addTextLogMonitor(Language.getWord("EXEC")+ SQL + "\"");
   String result = current_conn.SQL_Instruction(SQL);

   if (!result.equals("OK")) {

       result = result.substring(0,result.length()-1);
       ErrorDialog showError = new ErrorDialog(PropertiesTable.this,current_conn.getErrorMessage());
       showError.pack();
       showError.setLocationRelativeTo(parent);
       showError.show();
    }

   addTextLogMonitor(Language.getWord("RES") + result);
  }

 /**
  * Metodo isNum 
  * Retorna verdadero si una cadena solo posee numeros 
  */

 public boolean isNum(String s) {

   for(int i = 0; i < s.length(); i++) {

       char c = s.charAt(i);

       if (!Character.isDigit(c))
           return false;
     }

   return true;
 }

 public void showErrorInfo(String field) {
   JOptionPane.showMessageDialog(PropertiesTable.this,
   Language.getWord("TNE1") + field + Language.getWord("TNE2"),
   Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
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

} //Final de la Clase
