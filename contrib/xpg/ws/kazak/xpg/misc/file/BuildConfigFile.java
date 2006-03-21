/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS BuildConfigFile v 0.1
* Descripcion:
* Esta clase tiene la responsabilidad de modificar el archivo de    
* configuración de la aplicación. En este archivo se guardan los    
* datos de conexiones exitosas pasadas con una bandera de true sobre
* la última conexión utilizada.                                     
* Los objetos de este tipo se crean desde la clase XPg.             
* 
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*
* Fecha: 2001/10/01
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws
*          Gustavo Gonzalez - xtingray@kazak.ws
*/
package ws.kazak.xpg.misc.file;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;

import ws.kazak.xpg.db.ConnectionInfo;

public class BuildConfigFile {

 PrintStream configFile;
 int pos;

 /**
  * METODO CONSTRUCTOR, 1ra. Opción
  * Escribe de nuevo el archivo de configuración cambiando la bandera 
  * que indica cual fue el registro que logró la última conexión
  */
 public BuildConfigFile(Vector ListRegs,int num, String idiom) {

  pos = num;
  init();
  configFile.println("language=" + idiom);     
  for(int i=0;i<ListRegs.size();i++) {
     boolean onlyone;
     ConnectionInfo tmp = (ConnectionInfo) ListRegs.elementAt(i); 
     configFile.println("server=" + tmp.getHost());
     configFile.println("database=" + tmp.getDatabase());
     configFile.println("username=" + tmp.getUser());
     configFile.println("port=" + tmp.getPort());
     configFile.println("ssl=" + tmp.requireSSL());
     if(i==pos)
      onlyone = true;
     else
      onlyone = false;

     configFile.println("last=" + onlyone); 
   }
     configFile.close();
 }

 /**
  * METODO CONSTRUCTOR, 2da. Opción
  * Escribe de nuevo el archivo de configuración agregando los 
  * datos de una nueva configuración exitosa.
  */
 public BuildConfigFile(Vector ListRegs,ConnectionInfo online, String idiom) {

    init();
    configFile.println("language=" + idiom);     

    for(int i=0;i<ListRegs.size();i++) {
         boolean onlyone;
         ConnectionInfo tmp = (ConnectionInfo) ListRegs.elementAt(i);
         configFile.println("server=" + tmp.getHost());
         configFile.println("database="+tmp.getDatabase());
         configFile.println("username=" + tmp.getUser());
	 configFile.println("port=" + tmp.getPort());
         configFile.println("ssl=" + tmp.requireSSL());
         configFile.println("last=false");
       }
    configFile.println("server=" + online.getHost());
    configFile.println("database="+ online.getDatabase());
    configFile.println("username=" + online.getUser());
    configFile.println("port=" + online.getPort());
    configFile.println("ssl=" + online.requireSSL());
    configFile.println("last=true");
    configFile.close();
 }

 /**
  * Método init
  * Abre el archivo de configuración para escritura
  */
 public void init() 
 {
  try {
        String configPath = "xpg.cfg";
        String OS = System.getProperty("os.name");

        if(OS.equals("Linux") || OS.equals("Solaris") || OS.equals("FreeBSD")) {
            String UHome = System.getProperty("user.home");
            configPath = UHome + System.getProperty("file.separator")
                         + ".xpg" + System.getProperty("file.separator") + "xpg.cfg";
          }

        if(OS.startsWith("Windows")) {
            String xpgHome = System.getProperty("xpgHome");
            configPath = xpgHome + System.getProperty("file.separator") + "xpg.cfg";
          }

          configFile = new PrintStream(
	       new FileOutputStream(configPath));
      }
  catch(Exception ex) {
          System.out.println("Error: " + ex);
          ex.printStackTrace();
      }
 }

}
