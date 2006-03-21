/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ConnectionWatcher v 0.1                                                   
* Descripcion:
* Esta clase se encarga de supervisar la conexion TCP/IP
* con el servidor en donde se encuentra el SMBD Postgresql.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.main;

import java.net.Socket;

public class ConnectionWatcher extends Thread {

 XPg App;
 Socket online;
 boolean keep; 
 String hostname;
 int num_port;

public ConnectionWatcher(String host, int port, XPg frame) {
  App = frame;
  hostname = host;
  num_port = port;
  online = null;
  keep = true;
 }

public void run() {
	
  while(keep) {

    try {
         online = new Socket(hostname,num_port);
         online.close();
     }		   
    catch(Exception ex){
	      App.connectionLost(hostname);
	      break;
     }

    try {
          sleep(3000);          
     } 
    catch(Exception e){  
          System.out.println("Error: " + e);
          e.printStackTrace();
	}	
   }
 }

 public void goOut() {
   keep = false;
 }

} // Fin de la Clase
