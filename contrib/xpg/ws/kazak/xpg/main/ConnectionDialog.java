/*
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ConnectionDialog v 0.1                                                   
* Descripcion:
* Esta clase captura los datos iniciales de conexion.               
* Los objetos de esta clase son creados desde la clase XPg.         
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*          Angela Sandobal  - angesand@libertad.univalle.edu.co     
*/
package ws.kazak.xpg.main;

import ws.kazak.xpg.idiom.*;
import ws.kazak.xpg.db.*;
import ws.kazak.xpg.misc.file.*;
import java.beans.*; 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;
import java.net.URL;

class ConnectionDialog extends JDialog implements KeyListener,FocusListener {

  String fieldHost,fieldDatabase,fieldUser,fieldPass,fieldPort;
  private String typedText = null;
  private JOptionPane optionPane;
  JTextField textFieldHost;
  JTextField textFieldDatabase;  
  JTextField textFieldUser;
  JPasswordField textFieldPass;
  JTextField textFieldPort;
  ConnectionInfo conReg1;
  ConnectionInfo conReg2;
  boolean connected;
  PGConnection conn;
  ConfigFileReader elements;
  String selection;
  JTextArea logWin;
  boolean noNe = true;
  JScrollPane scrollPane;
  Vector bigList;
  Vector itemsList;
  int index;
  int numItems;
  final JList hostList;
  boolean noLast = false;
  String language = "";
  JFrame father;
  Vector tables = new Vector();
  ConnectionInfo initial;
  boolean lookOthers = false; 
  boolean link = false;
  boolean ssl = false;
 
public ConnectionDialog (JTextArea monitor, JFrame parent) {

  super(parent, true);
  logWin = monitor;
  connected = false;
  father = parent;
  setTitle(Language.getWord("TITCONNEC"));
  JPanel rowHost = new JPanel();     //panel host
  JPanel rowDatabase = new JPanel(); //panel base de datos
  JPanel rowUser = new JPanel();     //panel user
  JPanel rowPassword = new JPanel(); //panel password
  JPanel rowPort = new JPanel();     //panel port
  
  String configPath = "xpg.cfg";
  String OS = System.getProperty("os.name");

  if (OS.equals("Linux") || OS.equals("Solaris") || OS.equals("FreeBSD")) {
       String UHome = System.getProperty("user.home");
       configPath = UHome + System.getProperty("file.separator")
                      + ".xpg" + System.getProperty("file.separator") + "xpg.cfg";       
   }

  if (OS.startsWith("Windows")) {
            String xpgHome = System.getProperty("xpgHome");
            configPath = xpgHome + System.getProperty("file.separator") + "xpg.cfg";
   }

  /***1. Leer el archivo, traer los datos, inicializar las variables de la ultima conexion***/
  elements = new ConfigFileReader(configPath,0); //Clase que abre el archivo, lee los datos y los almacena
  String Host = "";     //inicialización para el campo host
  String Database = ""; //inicialización para el campo database
  String User = "";     //inicialización del campo user
  int Port = 5432;      //inicialización del campo port  

  if(elements.FoundLast()) {
   	initial = elements.getRegisterSelected(); //llama al metodo de ConfigFileReader que devuelve el ultimo registro de conexion
   	Host = initial.getHost(); //extrae el nombre del host del registro
   	Database = initial.getDatabase(); //extrae el nombre de la base de datos del registro
   	User = initial.getUser(); //extrae el nombre del usuario del 
   	Port = initial.getPort(); //extrae el nombre del puerto
   }	
   
  /***2. Formar los paneles de datos e inicializar los valores de los campos con la ultima conexion***/
  //instrucciones para introducir la etiqueta y el campo al panel de host
  JLabel msgString1 = new JLabel(Language.getWord("HOST") + ": "); 
  textFieldHost = new JTextField(Host,15);
  rowHost.setLayout(new BorderLayout());
  rowHost.add(msgString1,BorderLayout.WEST);
  rowHost.add(textFieldHost,BorderLayout.EAST);

  //instrucciones para introducir la etiqueta y el campo al panel de database
  JLabel msgString5 = new JLabel(Language.getWord("DB") + ": "); 
  textFieldDatabase = new JTextField(Database,15);
  rowDatabase.setLayout(new BorderLayout());
  rowDatabase.add(msgString5,BorderLayout.WEST);
  rowDatabase.add(textFieldDatabase,BorderLayout.EAST);

  //instrucciones para introducir la etiqueta y el campo al panel de user  
  JLabel msgString2 = new JLabel(Language.getWord("USER") + ": ");
  textFieldUser = new JTextField(User,15);
  rowUser.setLayout(new BorderLayout());
  rowUser.add(msgString2,BorderLayout.WEST);
  rowUser.add(textFieldUser,BorderLayout.EAST);  

  //instrucciones para introducir la etiqueta y el campo al panel de password
  JLabel msgString3 = new JLabel(Language.getWord("PASSWD")+ ": ");
  textFieldPass = new JPasswordField(15);
  textFieldPass.setEchoChar('*');         
  rowPassword.setLayout(new BorderLayout());      
  rowPassword.add(msgString3,BorderLayout.WEST);
  rowPassword.add(textFieldPass,BorderLayout.EAST);  

  //instrucciones para introducir la etiqueta y el campo al panel port
  JLabel msgString4 = new JLabel(Language.getWord("PORT") + ": ");
  textFieldPort = new JTextField(String.valueOf(Port),15);
  rowPort.setLayout(new BorderLayout());
  rowPort.add(msgString4,BorderLayout.WEST);
  rowPort.add(textFieldPort,BorderLayout.EAST);
  
  /***3. Añadir un boton para limpiar el formulario***/
  JPanel clearPanel = new JPanel();
  JButton cleaner = new JButton(Language.getWord("CLR"));   
  //instrucciones para limpiar los campos del formulario de conexión
  cleaner.addActionListener( new ActionListener() {
    public void actionPerformed(ActionEvent e) {
       clearForm();	
       hostList.clearSelection(); 
       textFieldHost.requestFocus();
    }
  }    	                   );       	                          	


 final JCheckBox checkLook = new JCheckBox(Language.getWord("LOOKDB"));
 checkLook.setSelected(false);
 checkLook.addActionListener( new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      if(checkLook.isSelected()) {
         lookOthers = true;
       } 
    }
 }                       );

 final JCheckBox checkLink = new JCheckBox(Language.getWord("CHKLNK"));
 checkLink.setSelected(false);
 checkLink.addActionListener( new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      if(checkLink.isSelected()) {
         link = true;
       }
    }
 }                       );

 final JCheckBox checkSSL = new JCheckBox(Language.getWord("CHKSSL"));
 checkSSL.setSelected(false);
 checkSSL.addActionListener( new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      if(checkSSL.isSelected()) {
         ssl = true;
       }
    }
 }                       );

 clearPanel.add(cleaner); //añade al panel el botón clear

 JPanel lookPanel = new JPanel();
 lookPanel.setLayout(new BoxLayout(lookPanel,BoxLayout.Y_AXIS));
 lookPanel.add(checkLook);
 lookPanel.add(checkSSL);
 lookPanel.add(checkLink);
  
  /***4. Añadir y llenar una lista de los servidores leidos del archivo***/  
  itemsList = elements.CompleteList(); //pone en el vector los datos de c/u de las conexiones
  numItems = itemsList.size(); //guarda el numero de conexiones definidas en el archivo
  bigList = new Vector();
  for(int j=0;j<numItems;j++) {
      ConnectionInfo conReg2 = (ConnectionInfo) itemsList.elementAt(j);	//se crea un objeto ConnectionInfo temporal 
      bigList.addElement(conReg2.getHost() + " - " + conReg2.getDatabase() + " (" + conReg2.getUser() + ")");
   }   

  hostList = new JList(bigList);//Se crea un scrollPane lista con el arreglo de hosts anterior
  hostList.setVisibleRowCount(5);  // la lista es de tamaño 5    

  scrollPane = new JScrollPane(hostList);//se añade un scroll a la lista
  hostList.addFocusListener(this);
  
   MouseListener mouseListener = new MouseAdapter() {
    public void mousePressed(MouseEvent e) {
         index = hostList.locationToIndex(e.getPoint());
         if (e.getClickCount() == 1 && index > -1) {
             conReg2 = (ConnectionInfo) itemsList.elementAt(index);
             selection = (String) bigList.elementAt(index); 
             textFieldHost.setText(conReg2.getHost());
             textFieldDatabase.setText(conReg2.getDatabase());
    	     textFieldUser.setText(conReg2.getUser());
    	     textFieldPort.setText(String.valueOf(conReg2.getPort()));             
          }
	 else
	     textFieldHost.requestFocus();
     }
   };
   hostList.addMouseListener(mouseListener);
  
  /***5. Formar un objeto con todos los componenetes anteriores***/
  Object[] array = {rowHost,rowDatabase,rowUser,rowPassword,rowPort,clearPanel,lookPanel,scrollPane}; // Se define un arreglo de objetos
  //en el se incluyen los cuatro paneles de datos el botón clear y la lista de hosts                                                              
  
  /***6. Formar un objeto con las cadenas de los botones que se usarán para el formulario***/
  //final en una variable es para decir que no puede ser modificada, es constante
  final String btnString1 = Language.getWord("CONNE2"); 
  final String btnString2 = Language.getWord("CANCEL");  
  Object[] options = {btnString1,btnString2};

  /***7. Formar el Frame : , añadirlo al frame***/
  //Inicializar el JOptionPane con sus scrollPanes y botones
  URL imgURL = getClass().getResource("/icons/16_connect.png");
  optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION, new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)),
                               options, options[0]); 
  setContentPane(optionPane); //añade el JOptionpane al frame

  //cerrar la ventana desde el boton X
  addWindowListener(new WindowAdapter() {
    public void windowClosing(WindowEvent we) {
    /*
     * A cambio de cerrar directamente la ventana,
     * el valor de JOptionPane es cambiado.
     */
    optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
    }
  }		   ); 
  
  /***9. Manejo de eventos ***/
  optionPane.addPropertyChangeListener(new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent e) {
      String prop = e.getPropertyName();

      if (isVisible() && (e.getSource() == optionPane)) {

//&& (prop.equals(JOptionPane.VALUE_PROPERTY)
//           ||  prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {

	    Object value = optionPane.getValue();
	    int Cancel = 1; //bandera cancelar indica seguir  

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
               //ignorar reset
               return;
             } 
       
      // Si da enter en el boton Conectar
      if (value.equals(btnString1)) {
          //capturar datos de los campos 
          char[] typedText = textFieldPass.getPassword();
          fieldPass = new String(typedText);
	  fieldHost = textFieldHost.getText();
          fieldHost = fieldHost.trim();
	  fieldDatabase = textFieldDatabase.getText();
          fieldDatabase = fieldDatabase.trim();
	  fieldUser = textFieldUser.getText();
          fieldUser = fieldUser.trim();
	  fieldPort = textFieldPort.getText();
          fieldPort = fieldPort.trim();
                                                            
         // Si algun campo esta clearPanel excepto la clave 
         if (fieldUser.equals("")||fieldHost.equals("")||fieldDatabase.equals("")||fieldPort.equals("")) {

             Cancel=0;	//bandera cancelar indica aborto
             JOptionPane.showMessageDialog( ConnectionDialog.this,
             Language.getWord("EMPTY"),
             Language.getWord("ERROR!"), JOptionPane.ERROR_MESSAGE);//muestra este mensaje
             typedText = null;
    	     // limpia el valor del scrollPane JOptionPane
	     optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

             return;
          }
        // Si encuentra caracter vacio dentro de las cadenas de los campos
        if ((fieldHost.indexOf(" ")!= -1)||(fieldDatabase.indexOf(" ")!= -1)||(fieldUser.indexOf(" ")!= -1)||(fieldPort.indexOf(" ")!= -1)) {
     	      Cancel=0;  //bandera cancelar indica aborto
              JOptionPane.showMessageDialog( ConnectionDialog.this,                                        
	      Language.getWord("NOCHAR"),
              Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);                                         
	      typedText = null;                                                                 
	      // limpia el valor del scrollPane JOptionPane                                                   
	      optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);                           
              return;
	   }

        if(fieldDatabase.equals("template1")) {
              Cancel=0;  //bandera cancelar indica aborto
              JOptionPane.showMessageDialog( ConnectionDialog.this,
              Language.getWord("DBRESER"),
              Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
              typedText = null;
              // limpia el valor del scrollPane JOptionPane                                                   
              optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
              return;
           }

        if(!isNumber(fieldPort) || (fieldPort.length() > 5) || fieldPort.compareTo("65500")>0) {
              Cancel=0;  //bandera cancelar indica aborto
              JOptionPane.showMessageDialog( ConnectionDialog.this,
              Language.getWord("ISNUM"),
              Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
              typedText = null;
              // limpia el valor de JOptionPane
              optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
              return;
           }

        //Si todo esta bien
        if (Cancel==1) {
             connected = true;
             int xport = Integer.parseInt(fieldPort);
             if(fieldPass.equals(""))
               fieldPass = "NO_PASS";
             conReg1 = new ConnectionInfo(fieldHost,fieldDatabase,fieldUser,fieldPass,xport,ssl);
             setVisible(false);
	   }	
       }			      

      // Si da enter en el boton Cancelar
      if (value.equals(btnString2)) {
          if(numItems > itemsList.size())
	     Writer();

          connected = false;
          setVisible(false); //ocultar ventana
       }
      }
    }//cierra public
  });

}//cierra constructor

 public boolean Connected() {
  return connected;
 }

 public ConnectionInfo getDataReg() {
    return conReg1;   
 }

 public PGConnection getConn() {
   return conn;
 }

 public Vector getConfigRegisters() {
   return itemsList;
 }

 /** Maneja el evento de tecla digitada dese el campo de texto */
  public void keyTyped(KeyEvent e) {
   }

 public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();                           
    String keySelected = KeyEvent.getKeyText(keyCode); //cadena que describe la tecla fÃ­sica presionada

    //si la tecla presionada es delete
    if(keySelected.equals("Delete") && !selection.equals("localhost - database01 (postgres)")) {
         String currentReg = conReg2.getDBChoosed();
         if(currentReg.equals("true"))
	   noLast = true;
	 bigList.remove(index);
	 itemsList.remove(index);
         hostList.setListData(bigList);
	 clearForm();
	 textFieldHost.requestFocus();
	 if(bigList.size()==1)
	   noNe = false;
        } 
	 else 
          { 
            if(keySelected.equals("Down")) {
	       if(index < bigList.size() - 1)
	         index++;
               else
                 index = bigList.size() - 1;
	     } 
	    else { 
	            if(keySelected.equals("Up")) {
	                if(index > 0 && index < bigList.size())
	                  index--;
			 else 
			  index = 0;
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
      Component conReg2 = e.getComponent();
      conReg2.addKeyListener(this);
      JList klist = (JList) conReg2;
      if(klist.isSelectionEmpty()) {
        klist.setSelectedIndex(0);
        index = 0;
        setForm();
       }
    }

 /**
 * METODO focusLost
 */
   public void focusLost(FocusEvent e) {
      Component conReg2 = e.getComponent();
      conReg2.removeKeyListener(this);
      hostList.clearSelection();
    }

 public void clearForm() {
    textFieldHost.setText("");
    textFieldDatabase.setText("");
    textFieldUser.setText("");
    textFieldPass.setText("");
    textFieldPort.setText("");
    }

 public void setForm() {
    conReg2 = (ConnectionInfo) itemsList.elementAt(index);
    selection = (String) bigList.elementAt(index); 
    textFieldHost.setText(conReg2.getHost());
    textFieldDatabase.setText(conReg2.getDatabase());
    textFieldUser.setText(conReg2.getUser());
    textFieldPort.setText(String.valueOf(conReg2.getPort()));
   }

 public int getRegisterSelected() {
     for(int k=0;k<itemsList.size();k++) {
        ConnectionInfo value = (ConnectionInfo) itemsList.elementAt(k);
	if(value.getDBChoosed().equals("true"))
	  return k;
      }
     return 0;
   }

 public void setLanguage(String idiom) {
    language = idiom;
   }

 public void Writer() {
      if(noLast)
             new BuildConfigFile(itemsList,0,language);
      else
             new BuildConfigFile(itemsList,getRegisterSelected(),language);
   }

 /**
  * Metodo addTextLogMonitor
  * Imprime mensajes en el Monitor de Eventos
  */
 public void addTextLogMonitor(String msg) {
  logWin.append(msg);	
  int longiT = logWin.getDocument().getLength();
  if(longiT > 0)
    logWin.setCaretPosition(longiT - 1);
  }	

 public boolean lookForOthers() {
    return lookOthers;
  }

 public boolean isNumber(String word) {
   for(int i=0;i<word.length();i++) {
      char c = word.charAt(i);
      if(!Character.isDigit(c))
        return false;
    }
   return true;
 }

 public boolean checkLink() {
   return link;
  }

 public boolean checkSSL() {
   return ssl;
  }
} // Fin de la Clase
