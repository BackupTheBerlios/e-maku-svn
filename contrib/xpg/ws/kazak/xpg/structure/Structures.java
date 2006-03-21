/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS Structures v 0.1                                                   
* Descripcion:
* Esta clase se encarga de construir la pestaña de la estructura de
* una tabla. También incluye métodos para modificar la tabla
* visualizada con nuevos campos.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*          Angela Sandobal  - angesand@libertad.univalle.edu.co     
*/
package ws.kazak.xpg.structure;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import ws.kazak.xpg.db.ForeignKey;
import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.db.Table;
import ws.kazak.xpg.db.TableFieldRecord;
import ws.kazak.xpg.db.TableHeader;
import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.menu.TablesGrant;

public class Structures extends JPanel implements ActionListener, SwingConstants, FocusListener, KeyListener {

 PGConnection current_conn;
 Object[][] data = {{"", "", "", ""}};
 String[] columnNames = new String[4];
 private boolean DEBUG = true;
 JFrame frameFather;
 JTextField title;
 JTextArea LogWin;
 JTable table;
 JScrollPane tableSpace2;
 JToolBar StructureBar;
 JButton addField,editField,properties,details;
 JList indexList; 
 JTextField fieldJText;
 JLabel indexLabel, propertiesLabel;
 JLabel uniqueLabel, primaryLabel, treeLabel; 
 TitledBorder title1, title2;
 Color currentColor;
 JPanel indexPanel;
 MouseListener mouseListener;
 String currentTable;
 boolean isEmpty=true;
 int numIndex = 0;
 Vector indexN = new Vector();
 Vector fk = new Vector();
 Hashtable hashFk = new Hashtable();
 Border etched;

 /**
  *  METODO CONSTRUCTOR
  */
 public Structures(JFrame parent,JTextArea monitor) {
   frameFather = parent;
   LogWin = monitor;
   setLayout(new BorderLayout()); //Divide el panel como BorderLayout

   StructureBar = new JToolBar(SwingConstants.VERTICAL);
   StructureBar.setFloatable(false);
   CreateToolBar();
   activeToolBar(false);
   title = new JTextField(Language.getWord("NOSELECT"));
   title.setHorizontalAlignment(JTextField.CENTER);
   title.setEditable(false);

   JPanel upSide = new JPanel();
   upSide.setLayout(new BorderLayout());
   upSide.add(title);

   etched = BorderFactory.createEtchedBorder();
   title1 = BorderFactory.createTitledBorder(etched);

   upSide.setBorder(title1);


   formatTable();
   table.setEnabled(false);
   tableSpace2.setEnabled(false);
   add(upSide,BorderLayout.NORTH);       //Añadir Título al norte
   add(StructureBar,BorderLayout.WEST);  //Añadir Barra de iconos a la izquierda
 
   // Creación de panel de index izquierdo
   indexLabel = new JLabel(Language.getWord("INDEX"),JLabel.CENTER); 
   indexList = new JList(new Vector()); 
   indexList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
   indexList.setVisibleRowCount(4);  // la lista es de tamaño 4
   indexList.addFocusListener(this);

   mouseListener = new MouseAdapter() {

       public void mousePressed(MouseEvent e) {

         int index = indexList.locationToIndex(e.getPoint());

         if (index != -1) {
             String indexName = (String) indexList.getSelectedValue();
             settingIndex(indexName);
          }
      }
   };
  indexList.addMouseListener(mouseListener);


   JScrollPane indexScroll = new JScrollPane(indexList);
   indexScroll.setPreferredSize(new Dimension(200,65));

   JPanel bloke = new JPanel();
   bloke.add(indexScroll);

   JPanel indexLeft = new JPanel(); 
   indexLeft.setLayout(new BorderLayout());
   indexLeft.add(indexLabel,BorderLayout.NORTH);
   indexLeft.add(bloke,BorderLayout.CENTER);

   JPanel todo = new JPanel();
   todo.setLayout(new FlowLayout());
   todo.add(indexLeft);

   // Create the radio buttons.
   URL imgURL = getClass().getResource("/icons/dot.png");
   uniqueLabel = new JLabel(Language.getWord("UKEY"),new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)),SwingConstants.LEFT);
   primaryLabel = new JLabel(Language.getWord("PKEY"),new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)),SwingConstants.LEFT); 
   treeLabel = new JLabel(Language.getWord("FK"),new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)),SwingConstants.LEFT); 

   JPanel radioPanel = new JPanel();
   radioPanel.setLayout(new BorderLayout());
   radioPanel.add(primaryLabel,BorderLayout.NORTH);
   radioPanel.add(uniqueLabel,BorderLayout.CENTER);
   radioPanel.add(treeLabel,BorderLayout.SOUTH);       

   JPanel radioBig = new JPanel();
   radioBig.setLayout(new FlowLayout(FlowLayout.CENTER)); 
   radioBig.add(radioPanel);

   JPanel indexColumns = new JPanel(); 
   propertiesLabel = new JLabel(Language.getWord("FIELD") + ": ",JLabel.CENTER); 
   JPanel titleI = new JPanel();
   titleI.setLayout(new BorderLayout());
   titleI.add(propertiesLabel);
   fieldJText = new JTextField(15); 
   fieldJText.setBackground(Color.white);
   fieldJText.setEditable(false);
   details = new JButton(Language.getWord("DETAILS"));
   details.setActionCommand("Details");
   details.addActionListener(this);
   details.setEnabled(false);
   JPanel button = new JPanel();
   button.add(details);
   button.setPreferredSize(new Dimension(150,45));

   indexColumns.setLayout(new BoxLayout(indexColumns,BoxLayout.Y_AXIS));
   indexColumns.add(titleI);
   indexColumns.add(fieldJText);
   indexColumns.add(button);

   JPanel glob = new JPanel();
   glob.setLayout(new FlowLayout(FlowLayout.CENTER));
   glob.add(indexColumns);

   JPanel indexProp = new JPanel(); 
   indexProp.setLayout(new BoxLayout(indexProp,BoxLayout.X_AXIS));
   indexProp.add(radioBig);
   indexProp.add(glob);

   title1 = BorderFactory.createTitledBorder(etched, Language.getWord("INDEXPR"));
   currentColor = title1.getTitleColor();
   indexProp.setBorder(title1);

   indexPanel = new JPanel();
   indexPanel.setLayout(new BoxLayout(indexPanel,BoxLayout.X_AXIS));
   indexPanel.add(todo);
   indexPanel.add(indexProp);

   title2 = BorderFactory.createTitledBorder(etched, Language.getWord("TITINDEX"));
   indexPanel.setBorder(title2);
   indexPanel.setPreferredSize(new Dimension(100,125));

   add(indexPanel,BorderLayout.SOUTH);
   activeIndexPanel(false);  
 }

 /**
  * METODO CreateToolBar()
  * Crea Barra de Iconos
  */
 public void CreateToolBar() {  
 
  URL imgURL = getClass().getResource("/icons/16_AddField.png"); 
  addField = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  addField.setActionCommand("ButtonAddField");
  addField.addActionListener(this);
  addField.setToolTipText(Language.getWord("ADDF"));
  StructureBar.add(addField);

  imgURL = getClass().getResource("/icons/16_Grant.png");
  editField = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  editField.setActionCommand("ItemGrant");
  editField.addActionListener(this);
  editField.setToolTipText(Language.getWord("PERMI"));
  StructureBar.add(editField);

  imgURL = getClass().getResource("/icons/16_Index.png");
  properties = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  properties.setActionCommand("ButtonProperties");
  properties.addActionListener(this);
  properties.setToolTipText(Language.getWord("PROPTABLE"));
  StructureBar.add(properties);
 }

 /**
  * METODO actionPerformed
  * Manejador de Eventos para la barra de botones y el menu desplegable
  */
 public void actionPerformed(java.awt.event.ActionEvent e) {

  if (e.getActionCommand().equals("Details")) {

   String indexName = (String) indexList.getSelectedValue();

   Object o = hashFk.get(indexName);

   if (o != null) {

       ForeignKey fkey = (ForeignKey) o;

       String phrA = Language.getWord("FKN") + fkey.getForeignKeyName();
       String phrB = Language.getWord("OPC") +  ": " + fkey.getOption();
       String phrC = Language.getWord("FTAB") + fkey.getForeignTable();
       String phrD = Language.getWord("LFI") + fkey.getLocalField(); 
       String phrE = Language.getWord("RFI") + fkey.getForeignField();

       JOptionPane.showMessageDialog(frameFather,
       phrA + "\n" + phrB + "\n" + phrD + "\n" + phrC + "\n" + phrE,
       Language.getWord("INFO"),JOptionPane.INFORMATION_MESSAGE);
    }

   return;
  }

  if(e.getActionCommand().equals("ButtonProperties")) {                  
    PropertiesTable  winSeq = new PropertiesTable(frameFather,current_conn,currentTable,LogWin);
    winSeq.pack();
    winSeq.setLocationRelativeTo(frameFather);
    winSeq.show();	    
  
    return;
  }

  if(e.getActionCommand().equals("ButtonAddField")) {          
    InsertTableField iFi = new InsertTableField(frameFather,currentTable);
    if(iFi.wellDone) {
      boolean good = true;
      String result = current_conn.SQL_Instruction(iFi.InstructionA);
      addTextLogMonitor (Language.getWord("EXEC")+iFi.InstructionA+"\"");  
      String value = "OK";
      if(!result.equals("OK")) {
         good = false;       
         value = result.substring(0,result.length()-1);
      }
      addTextLogMonitor (value);

      if(good) {
        if(iFi.InstructionB.length()>0) {
          result = current_conn.SQL_Instruction(iFi.InstructionB);
          addTextLogMonitor(Language.getWord("EXEC") + iFi.InstructionB+ "\"");
          value = "OK";
          if(!result.equals("OK")) {              
            value = result.substring(0,result.length()-1);
            good = false;
	  }
          addTextLogMonitor(value);
        }

      Object[][] auxData = new Object[data.length + 1 ][4];
      for (int row=0; row<data.length; row++) {  
          for (int col=0; col<4; col++) {
	    auxData[row][col]= data[row][col];
                                        }
        }
      auxData[data.length][0]= iFi.NF;
      auxData[data.length][1]= iFi.TF;
      auxData[data.length][2]= new Boolean(false);
      String constrain = "";
      if (iFi.InstructionB.length()>0 && good) {
          constrain = iFi.DV;
        }
      auxData[data.length][3]= constrain;
      data = new Object[auxData.length + 1][4];
      data = auxData;
      remove(tableSpace2);
      formatTable(); 

      }
    }

   return;
  }

  if(e.getActionCommand().equals("ItemGrant")) 
   {
    String[] tb = current_conn.getTablesNames(true);

    if(tb.length < 1) {
       JOptionPane.showMessageDialog(frameFather,
       Language.getWord("NOTOW") + current_conn.getDBname() + "'",
       Language.getWord("INFO"),JOptionPane.INFORMATION_MESSAGE);
    } 
    else {
           TablesGrant winUser = new TablesGrant(frameFather,current_conn,LogWin,tb);
     }

    return;
  }

 } 

 /**
  * METODO activeToolBar
  * Activa o desactiva la barra de iconos
  */
 public void activeToolBar(boolean value) {
  addField.setEnabled(value);
  editField.setEnabled(value);
  properties.setEnabled(value);
 }

 /**
  * METODO setLabel
  * Titulo de la tabla en la pestaña
  */
 public void setLabel(String dbName,String table,String owner)  {
   String mesg = "";
   currentTable = table;
   if(dbName.length()>0)
    mesg = Language.getWord("TABLESTRUC") + "'" + table + "'" + Language.getWord("DBOFTABLE") + dbName + "  [" + Language.getWord("OWNER") + ": " + owner + "]"; 
   else 
    mesg = Language.getWord("NOSELECT");
   
   title.setText(mesg);
 }

 /**
  * METODO setTableStruct
  * Con los datos recibidos construye nuevamente la tabla de estructuras
  */
 public void setTableStruct(Table currentTable) {
   //Nuevos datos de la tabla
   TableHeader headT = currentTable.getTableHeader();
   int numFields = headT.getNumFields();
   data = new Object[numFields][4];

   for(int k=0;k<numFields;k++) 
    {
      Object o = (String) headT.getNameFields().elementAt(k);
      String field_name = o.toString();
      TableFieldRecord tmp = (TableFieldRecord) headT.getHashtable().get(field_name);
      data[k][0] = tmp.getName();

      if ("char".equals(tmp.getType())  || "varchar".equals(tmp.getType()))
       {
        int longStr = tmp.getOptions().charLong;
        if(longStr>0)
          data [k][1] = tmp.getType() + "(" + tmp.options.charLong + ")";
        else
          data[k][1] = tmp.getType();
       }
      else 
        data[k][1] = tmp.getType();  
            
      Boolean tmpbool = new Boolean(tmp.options.isNull);
      data[k][2] = tmpbool;
      String defaultV = tmp.getOptions().getDefaultValue();

      if(defaultV.endsWith("::bool"))
       {
        if(defaultV.indexOf("t")!=-1)
           data[k][3] = "true"; 
        else
           data[k][3] = "false";
       }
      else
        data[k][3] = defaultV;
   } 
   //Destruir tabla anterior
   remove(tableSpace2); 
   //Formatear la tabla
   formatTable();
 }

 /**
  * METODO setNullTable()
  * Construye una tabla vacia
  */
 public void setNullTable() {
   //Nuevos datos de la tabla
   title.setText(Language.getWord("DSCNNTD"));
   data = new Object[1][4];
   for (int col=0; col<4; col++) 
       data[0][col]="";
   
   //Destruir tabla anterior
   remove(tableSpace2);
   //Formatear la tabla
   formatTable();
 }

 /**
  * METODO activeIndexPanel
  * Habilita o deshabilita el panel de Index
  */
 public void activeIndexPanel(boolean state) {

   String blanco[] = {};

   indexList.setListData(blanco);
   indexList.setEnabled(state);
   indexLabel.setEnabled(state);

   fieldJText.setEnabled(state);
   propertiesLabel.setEnabled(state);

   resetIndex();

   if (state) {
       title2.setTitleColor(currentColor);
       title1.setTitleColor(currentColor);
    } 
   else {
         title2.setTitleColor(new Color(153,153,153));
         title1.setTitleColor(new Color(153,153,153));
    }
 }

 /**
  * METODO formatTable()
  * Da formato a las tablas que se crean
  */
 public void formatTable() {
  //Definir un modelo

  MyTableModel myModel = new MyTableModel(data);
  table = new JTable(myModel);

  //Añadir scroll y seleccionar tamaño predefinido de la tabla
  tableSpace2 = new JScrollPane(table);
  table.setPreferredScrollableViewportSize(new Dimension(410, 70));

  //Personalizar anchura de columnas
  TableColumn column = null;
  column = table.getColumnModel().getColumn(0);
  column.setPreferredWidth(100);
  column = table.getColumnModel().getColumn(1);
  column.setPreferredWidth(60);
  column = table.getColumnModel().getColumn(2);
  column.setPreferredWidth(30);
  column = table.getColumnModel().getColumn(3);
  column.setPreferredWidth(150);
 
  //Situarla en el centro del panel 
  add(tableSpace2,BorderLayout.CENTER);
 }

 /**
  * CLASE MyTableModel
  * Inicializa y controla la tabla
  */ 
 class MyTableModel extends AbstractTableModel {
   String[] columnNames = {Language.getWord("NAME"), Language.getWord("TYPE"), Language.getWord("NOTNULL"), Language.getWord("DEFAULT")};
   
   public MyTableModel(Object[][] xdata) {
     data = xdata;
   }

   public void setValues(Object xinfo[][]) {
     data = xinfo; 
   }

  public int getColumnCount() {
     return columnNames.length;
   }

   public int getRowCount() {
     return data.length;
   }

   public String getColumnName(int col) {
     return columnNames[col];
   }

   public Object getValueAt(int row, int col) {    
    return data[row][col];
   }

   /*
    * JTable uses this method to determine the default renderer
    * editor for each cell. 
    */
   public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
   }

   /*
    * Método para definir columnas editables
    */
   public boolean isCellEditable(int row, int col) {
      if (col == 0 || col == 3)  
            return true;
      else 
            return false;
   }

   /**
    * Implementado porque los datos de la tabla pueden cambiar
    */
    public void setValueAt(Object value, int row, int col) {
      if (DEBUG) {
          String oldName = (String) getValueAt(row,col);

          //ALTER TABLE para cambiar nombre del campo
          if (col == 0 && !oldName.equals(value)) 
           {
            String newN = value.toString();
            if(newN.indexOf(" ") != -1)
             { 
              JOptionPane.showMessageDialog(frameFather,
              Language.getWord("NOCHAR"),
              Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
              return;
             }

            String result = current_conn.SQL_Instruction("ALTER TABLE \"" + currentTable + "\" RENAME \"" + getValueAt(row,col) + "\" TO \"" + value + "\"");
            addTextLogMonitor (Language.getWord("EXEC")+"ALTER TABLE \"" + currentTable + " RENAME COLUMN \"" + getValueAt(row,col) + "\" TO \"" + value + "\";\" ");
 

            if(!result.equals("OK")) 
              result = result.substring(0,result.length()-1);

            addTextLogMonitor (Language.getWord("RES")+result);
            setTableStruct( current_conn.getSpecStrucTable(currentTable) );
          }

          //ALTER TABLE para cambiar valor por defecto
          if (col == 3 && !oldName.equals(value)) 
           {
	    String typex = (String) getValueAt(row,col-2);            
	    String val = (String) value;

            if (typex.startsWith("int") || typex.startsWith("decimal") || typex.startsWith("serial")
                || typex.startsWith("float")) {

                if (!isNum(value.toString())) {
                    JOptionPane.showMessageDialog(frameFather,
                    Language.getWord("NVE"),
                    Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

                    return;
                 }
             }

            if (typex.startsWith("bool")) {
                if (!value.equals("true") && !value.equals("false")) {
                    JOptionPane.showMessageDialog(frameFather,
                    Language.getWord("BVE"),
                    Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
                    return;
                 }
             }

            if ((typex.startsWith("varchar") || typex.startsWith("char") || typex.startsWith("date") 
                 || typex.startsWith("text") || typex.startsWith("name") || typex.startsWith("time")) 
                 && !val.startsWith("\'"))

	         value = "'" + value + "'";

            String result = current_conn.SQL_Instruction("ALTER TABLE \"" + currentTable + "\" ALTER COLUMN \"" 
                                                         + getValueAt(row,0) + "\" DROP DEFAULT");

            addTextLogMonitor (Language.getWord("EXEC")+"ALTER TABLE \"" + currentTable + "\" ALTER COLUMN \"" 
                               + getValueAt(row,0) + "\" DROP DEFAULT\" "); 

            if (!result.equals("OK")) 
                result = result.substring(0,result.length()-1);
            
            addTextLogMonitor (Language.getWord("RES")+result);

            if (val.length()>0) {

                result = current_conn.SQL_Instruction("ALTER TABLE \"" + currentTable + "\" ALTER COLUMN \"" + getValueAt(row,0) + "\" SET DEFAULT " + value);
                addTextLogMonitor (Language.getWord("EXEC") + "ALTER TABLE \"" + currentTable + "\" ALTER COLUMN \"" + getValueAt(row,0) + "\" SET DEFAULT "+ value +"\" ");

                if (!result.equals("OK"))
                    result = result.substring(0,result.length()-1);
              
                addTextLogMonitor (Language.getWord("RES") + result);
             }

            setTableStruct( current_conn.getSpecStrucTable(currentTable) );
           }
      }
    }                   
 }

 /**
  * METODO setIndexTable
  * Dado el vector de indices vuelve a llenar el area
  * de información sobre índices de la tabla 
  */
 public void setIndexTable (Vector Indices, PGConnection conn) {

   current_conn = conn;

   Vector fk = current_conn.getForeignKeys(currentTable);

   if (!fk.isEmpty()) {

       for (int j=0;j<fk.size();j++) {
            Vector dataFK = (Vector) fk.elementAt(j);
            String fkName = (String) dataFK.elementAt(0);
            String itemN = "";

            if (fkName.equals("$1"))
                itemN = "foreign_key_unnamed_" + j; 
            else
                itemN = "foreign_key_" + fkName;

            Indices.addElement(itemN);
            hashFk.put(itemN,new ForeignKey(fkName,(String)dataFK.elementAt(2),(String)dataFK.elementAt(3),(String)dataFK.elementAt(4),(String)dataFK.elementAt(5)));

        }
    }
   else {
          if (details.isEnabled())      
              details.setEnabled(false);
    }

   indexList.setListData(Indices);
   if (Indices.size()>0) {

       indexN = Indices;
       numIndex = Indices.size();
       indexList.requestFocus();
    }
   else
       numIndex = 0;
 }

 /**
  * METODO setIndexProp
  * Este método se encarga de llenar las propiedades
  * de un indice determinado
  */
 public void setIndexProp (Boolean radioActive, String fieldsIndex) {

   //Boolean primaryBool = (Boolean) radioActive.elementAt(2);
   boolean primarybool = radioActive.booleanValue(); 

   resetIndex();

   if (primarybool)
       primaryLabel.setEnabled(true);
   else
       uniqueLabel.setEnabled(true);

   fieldJText.setText(" " + fieldsIndex);
   isEmpty = false;
 }  

 public void resetIndex() {

   if (!isEmpty)
       fieldJText.setText("");

   if (uniqueLabel.isEnabled())
       uniqueLabel.setEnabled(false);

   if (primaryLabel.isEnabled())
       primaryLabel.setEnabled(false);

   if (treeLabel.isEnabled())
       treeLabel.setEnabled(false);

  }

 /** Maneja el evento de tecla digitada dese el campo de texto */
  public void keyTyped(KeyEvent e) {
   }

  public void keyPressed(KeyEvent e) 
   {
    int keyCode = e.getKeyCode();

    String keySelected = KeyEvent.getKeyText(keyCode); 
    //cadena que describe la tecla presionada

    if(keySelected.equals("Down"))
      {

       int indexV = indexList.getSelectedIndex();

       if(indexV < numIndex - 1)
          indexV++;
       else
          indexV = numIndex - 1;

       String indexName = (String) indexN.elementAt(indexV);
       settingIndex(indexName);

      }
    else 
      {

       if(keySelected.equals("Up"))
        {
          int indexV = indexList.getSelectedIndex();

          if(indexV > 0 && indexV < numIndex)
             indexV--;
          else
             indexV = 0;

          String indexName = (String) indexN.elementAt(indexV);
          settingIndex(indexName);

        }
       else
            Toolkit.getDefaultToolkit().beep();

      }
   }

   /*
    * METODO keyReleased
    * Handle the key released event from the text field.
    */
    public void keyReleased(KeyEvent e)
     {
     }

 /**
   * METODO focusGained
   * Es un foco para los eventos del teclado
   */
   public void focusGained(FocusEvent e)
    {
      Component tmp = e.getComponent();
      tmp.addKeyListener(this);

      if(numIndex>0)
      {
       JList klist = (JList) tmp;

       if(klist.getModel().getSize() > 0)
        {
         klist.setSelectedIndex(0);

         String indexName = (String) klist.getSelectedValue();
         settingIndex(indexName);
        }
      }

    }

 /**
 * METODO focusLost
 */
   public void focusLost(FocusEvent e) {

      Component tmp = e.getComponent();
      tmp.removeKeyListener(this);

    }

 public void settingIndex(String indexName) {

     if (indexName.startsWith("foreign_key_")) {

         if (!details.isEnabled())
             details.setEnabled(true);

         if (!treeLabel.isEnabled())
             treeLabel.setEnabled(true);

         ForeignKey fkey = (ForeignKey) hashFk.get(indexName);
         fieldJText.setText(" " + fkey.getLocalField());
      }
     else {

           if (details.isEnabled())
               details.setEnabled(false);

           Vector opc = current_conn.getIndexProp(indexName);
           addTextLogMonitor (Language.getWord("EXEC") + current_conn.SQL + "\"");

           Vector row = (Vector) opc.elementAt(0);
           Object o = row.elementAt(0);
           String cod = o.toString();

           Vector fieldsName = current_conn.getIndexFields(cod);
           addTextLogMonitor (Language.getWord("EXEC") + current_conn.SQL + "\"");

           Vector field = (Vector) fieldsName.elementAt(0);

           setIndexProp((Boolean) row.elementAt(2),(String) field.elementAt(0));
      }
   }

 /**
  * Metodo addTextLogMonitor
  * Imprime mensajes en el Monitor de Eventos
  */
 public void addTextLogMonitor(String msg)
 {
  LogWin.append(msg + "\n");
  int longiT = LogWin.getDocument().getLength();

  if(longiT > 0)
     LogWin.setCaretPosition(longiT - 1);
 }

 public boolean isNum(String s) {

    for (int i = 0; i < s.length(); i++) {

         char c = s.charAt(i);
         if (!Character.isDigit(c))
          return false;
     }

    return true;
  }

} // Fin de la Clase

