/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ConfigFileReader v 0.1 
* Esta clase se encarga de abrir el archivo de configuraci�n de la
* aplicaci�n y leer los datos de este.                            
* Los objetos de este tipo se crean desde la clase ConnectionDialog        
*                                                                 
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*
* Fecha: 2001/07/31                                              
*
* Autores: Beatriz Flori�n  - bettyflor@kazak.ws                 
*          Gustavo Gonzalez - xtingray@kazak.ws
*/
package ws.kazak.xpg.misc.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.util.Vector;

import ws.kazak.xpg.db.ConnectionInfo;

public class ConfigFileReader {

 boolean thereIsLast = false;
 boolean thereIsData = false;
 RandomAccessFile ConfigFile;

 Vector ListRegisters = new Vector();
 ConnectionInfo selected;
 String election = "none";
 int posChamp = 0;

 /**
  * METODO CONSTRUCTOR
  * Abre el archivo de configuraci�n, si no existe lo crea.
  */
 public ConfigFileReader(){
  }

 public ConfigFileReader(String fileX,int oper) {

  File varfile = new File( fileX );

  if (varfile.exists() && varfile.isFile()) {
      try {
            ConfigFile = new RandomAccessFile (fileX, "r" ); //Abrir el archivo para lectura
            String firstLine = ConfigFile.readLine(); //Leer la primera linea
            String secondLine = ConfigFile.readLine(); //Leer la segunda linea     	      

            if (firstLine.startsWith("language=") && secondLine.startsWith("server=")) {

     	        thereIsData = true;  //Encontr� datos en el archivo
     	        ConfigFile.seek(0); 

     	        if (oper == 0)
     	            getData();	

     	        if (oper == 1) 
    	            getLanguage();

    	        if (oper == 2) 
    	            ReplyData();
             }

            ConfigFile.close();
        }
      catch(Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
         }	    
    }	    
   else
      	Create_File(fileX);
 }

 /**
  * M�todo getLanguage()
  * Lee la primera l�nea del archivo de configuraci�n referente al idioma
  */ 
 public void getLanguage() {

    try {
          String idiom = ConfigFile.readLine();	
          StringTokenizer st = new StringTokenizer(idiom,"=");
          idiom = st.nextToken();
          idiom = st.nextToken();                                                         
          election = idiom;
      }
    catch(Exception e) {
          System.out.println("Error: " + e);
          e.printStackTrace();
       }
  }

 /**
  * METODO getIdiom
  * Retorna la cadena que indica el idioma leido 
  * del archivo de configuraci�n
  */  
  public String getIdiom() {
       return election;
  }

 /**
  * METODO getData
  * Recorre el archivo de configuraci�n creando un arreglo
  * de Objetos ConnectionInfo (registros de conexi�n).
  */
 public void getData() {

    String[] parameters = new String[6]; 
    ListRegisters = new Vector(); 
    int j=0;

    try {
         String idiom = ConfigFile.readLine();	

         do {
              for (int i=0;i<6;i++) {
                   String line = ConfigFile.readLine();
                   StringTokenizer st = new StringTokenizer(line,"=");
                   line = st.nextToken();
                   parameters[i] =  st.nextToken();
               }
              
              boolean ssl = Boolean.getBoolean(parameters[4]);
              
              if (parameters[5].startsWith("true")) {
                  posChamp = j;
                  thereIsLast = true;
                  selected = new ConnectionInfo(parameters[0],parameters[1],parameters[2],Integer.parseInt(parameters[3]),ssl,parameters[5]);
               }

              ConnectionInfo OneRegister = new ConnectionInfo(parameters[0],parameters[1],parameters[2],Integer.parseInt(parameters[3]),ssl,parameters[5]);
              ListRegisters.addElement(OneRegister);
              j++;
           } while ( ConfigFile.getFilePointer() < ConfigFile.length() ); 

         }
       catch(Exception e) {

             System.out.println("Error: " + e);
             e.printStackTrace();
         }
 }

 /**
  * METODO Create_File
  * Cuando el archivo de configuraci�n no existe se crea uno
  * preliminar.
  */
 public void Create_File(String ConfigFile) {

    try {
    	//Diego cambio el configFile 
         PrintStream outStream = new PrintStream(new FileOutputStream(ConfigFile));
         outStream.println("language=none");
         outStream.println("server=localhost");
	 outStream.println("database=database01");         
	 outStream.println("username=postgres");
	 outStream.println("port=5432");
         outStream.println("ssl=false");
	 outStream.println("last=true");
	 outStream.close();
	 posChamp = 0;
	 thereIsLast = true;
     selected = new ConnectionInfo("localhost","database01","postgres",5432,false,"true");
	 ConnectionInfo OneRegister = selected;
         ListRegisters.addElement(OneRegister);//El unico registro ser� el creado actualmente
       }
     catch(Exception e) {
           System.out.println("Error: " + e);
           e.printStackTrace();
      }
 }

 /**
  * METODO FoundLast
  * Retorna un booleano que indica si se encontr� la bandera del
  * �ltimo registro o no.
  */
 public boolean FoundLast() {
  	return thereIsLast;
 }	

 /**
  * METODO CompleteList
  * Retorna el vector de registros que se form� con el archivo de
  * configuraci�n.
  */  
 public Vector CompleteList() {
   return ListRegisters;
 }

 /**
  * METODO getChampion
  * Retorna el registro del que se eligi� como �ltima conexi�n exitosa.
  */  
 public ConnectionInfo getRegisterSelected() {
   return selected; 	       
 }  	       

 /**
  * METODO getPosCham
  * Retorna la posici�n en el vector de registros del que es el 
  * �ltimo usado anteriormente.
  */   
 public int getPosCham() {
   return posChamp;
 }

 public void ReplyData() {
   String[] parameters = new String[6]; 
   ListRegisters = new Vector(); 
   int j=0;

   try {
         String idiom = ConfigFile.readLine();	

         do {
              for (int i=0;i<6;i++) {
                   String line = ConfigFile.readLine();
                   StringTokenizer st = new StringTokenizer(line,"=");
                   line = st.nextToken();
                   parameters[i] =  st.nextToken();                                                         
                }
              boolean ssl = Boolean.getBoolean(parameters[5]);
              ConnectionInfo OneRegister = new ConnectionInfo(parameters[0],parameters[1],parameters[2],Integer.parseInt(parameters[3]),ssl,parameters[5]);
              ListRegisters.addElement(OneRegister);
              j++;
            } while ( ConfigFile.getFilePointer() < ConfigFile.length() ); 
     }
   catch(Exception e) {
         System.out.println("Error: " + e);
         e.printStackTrace();
       }
 }

} // Fin de la Clase
