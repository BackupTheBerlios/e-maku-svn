/**
 * Disponible en http://www.kazak.ws
 *
 * Desarrollado por Soluciones KAZAK
 * Grupo de Investigacion y Desarrollo de Software Libre
 * Santiago de Cali/Republica de Colombia 2001
 *
 * Este codigo se encuentra cubierto por los terminos 
 * definidos en la licencia GPL.
 *
 * Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
 *
 * Fecha: 2001/07/31
 *
 * Autores: Beatriz Flori�n  - bettyflor@kazak.ws
 *          Gustavo Gonzalez - xtingray@kazak.ws
 */

package ws.kazak.xpg.db;

/**
 *
 * <b>CLASS ConnectionInfo v 0.1</b><p align="justify"> 
 * Esta clase define la estructura de datos de los registros de      
 * una conexi�n.                                                         
 * Los objetos de este tipo se crean desde las clases ConfigFileReader,       
 * ConnectionDialog, PGConnection y UpdateDBTree. Las clases XPg y BuildConfigFile 
 * tambi�n utilizan objetos de este tipo. 
 * </p> 
 *
 *@author      Beatriz Flori�n  - bettyflor@kazak.ws 
 *@author      Gustavo Gonzalez - xtingray@kazak.ws 
 *@version     0.1 
 *@since       1.0
*/

public class ConnectionInfo {

 public String hostname;
 public String username;
 public String database;
 public String password = "";
 public int port;
 public String selectionPos = "";
 public boolean ssl = false;

 /**
  * METODO CONSTRUCTOR 1ra. Opci�n
  * define la estructura de datos para leer el archivo de configuraci�n
  *
  * @param host       Nombre o Direccion Ip del Servidor PostgreSQL
  * @param db         Nombre de la base de datos a conectarse
  * @param user       Nombre del usuario de la base de datos
  * @param numport    Numero del Puerto en el que escucha el Servidor PostgreSQL
  * @param sslSupport Booleano que indica si la conexion requiere de soporte SSL 
  */

 public ConnectionInfo(String host, String db, String user, int numport, boolean sslSupport) {

   hostname = host;
   database = db;
   username = user;
   port = numport;
   ssl = sslSupport;
  }

 /**
  * METODO CONSTRUCTOR 2ra. Opci�n
  * define la estructura de datos para leer el archivo de configuraci�n
  *
  * @param host      Nombre o Direccion Ip del Servidor PostgreSQL
  * @param db        Nombre de la base de datos a conectarse
  * @param user      Nombre del usuario de la base de datos
  * @param numPort   Numero del Puerto en el que escucha el Servidor PostgreSQL
  * @param sslSupport Booleano que indica si la conexion requiere de soporte SSL
  * @param selected  Valor booleano que indica si esa conexion ha sido seleccionada 
  */
 
 public ConnectionInfo(String host, String db, String user, int numPort, boolean  sslSupport, String selected) {

   hostname = host;
   database = db;
   username = user;
   port = numPort;
   ssl = sslSupport;
   selectionPos = selected;
  }
 
 /**
  * METODO CONSTRUCTOR 3da. Opci�n
  * define la estructura de datos para hacer pruebas de conexi�n
  *
  * @param host      Nombre o Direccion Ip del Servidor PostgreSQL
  * @param db        Nombre de la base de datos a conectarse
  * @param user      Nombre del usuario de la base de datos
  * @param passwd  Clave de acceso del usuario de la base de datos
  * @param numPort   Numero del Puerto en el que escucha el Servidor PostgreSQL
  * @param sslSupport Booleano que indica si la conexion requiere de soporte SSL
  */

 public ConnectionInfo(String host, String db, String user, String passwd, int numPort, boolean sslSupport) {

   hostname = host;
   database = db;
   username = user;
   password = passwd;
   port = numPort;
   ssl = sslSupport;
  }

 /**
  * Este m&eacute;todo retorna el nombre del servidor en el registro actual
  * @return hostname Nombre o Direccion Ip del Servidor PostgreSQL 
  */
 public String getHost() {
  return hostname;	
 }

 /**
  * Este m&eacute;todo retorna el nombre del usuario en el registro actual
  * @return username Nombre del usuario de la base de datos
  */
 public String getUser() {
  return username;	
 }

 /**
  * Este m&eacute;todo retorna la clave del usuario en el registro actual
  * @return password Clave de acceso del usuario de la base de datos 
  */
 public String getPassword() {
  return password;
 }

 /**
  * Este m&eacute;todo retorna el nombre de la base de datos en el registro actual
  * @return database  Nombre de la base de datos a conectarse
  */
 public String getDatabase() {
  return database;	
 }

 /**
  * Este m&eacute;todo retorna el n�mero del puerto en el registro actual
  * @return port Numero del Puerto en el que escucha el Servidor PostgreSQL
  */
 public int getPort() {
  return port;	
 }

 /**
  * Este m&eacute;todo retorna el n�mero del puerto en el registro actual
  * @return selectionPos Valor booleano que indica si esta conexion es la selccionada 
  */
 public String getDBChoosed() {
  return selectionPos;
 }

 /**
  * Este m&eacute;todo retorna verdadero si la conexion requiere soporte SSL 
  * @return ssl Valor booleano que indica si la conexion requiere soporte SSL
  */

 public boolean requireSSL() {
  return ssl;
 }

} //Fin de la Clase
