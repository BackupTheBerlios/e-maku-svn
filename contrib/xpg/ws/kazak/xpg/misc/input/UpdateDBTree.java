/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS UpdateDBTree v 0.1                                                   
* Descripcion:
* Esta clase se encarga de realizar un escaneo de conexiones
* a todas las bases de datos en un SMBD Postgres.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.misc.input;

import java.util.Vector;

import javax.swing.JTextArea;

import ws.kazak.xpg.db.ConnectionInfo;
import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.idiom.Language;

public class UpdateDBTree {

 public Vector validDB = new Vector();
 public Vector vecConn = new Vector();
 Vector listDB;
 PGConnection conn;
 ConnectionInfo user;
 boolean killing=false;
 JTextArea LogWin;

 public UpdateDBTree(JTextArea log,PGConnection pgconn,Vector DBs) {

   LogWin = log;
   conn = pgconn;
   user = conn.getConnectionInfo();
   listDB = DBs;
   makeSearch();
 }

 public void makeSearch()  {

  Vector tables;
  int numDB = listDB.size();

  if (numDB>0) {

      for (int i=0;i<numDB;i++) {

           Object o = listDB.elementAt(i);
           String dbname = o.toString();
           addTextLogMonitor(Language.getWord("LOOKDB") + ": \"" + dbname + "\"... ");
           ConnectionInfo tmp = new ConnectionInfo(user.getHost(),dbname,user.getUser(),user.getPassword(),user.getPort(),user.requireSSL()); 
           PGConnection proofConn = new PGConnection(tmp); 

           if (!proofConn.Fail()) {

               addTextLogMonitor(Language.getWord("OKACCESS"));

               if (!dbname.equals("template1") || !dbname.equals("postgres")) {
                   vecConn.addElement(proofConn);
                   validDB.addElement(listDB.elementAt(i));                     
                }
            }
           else
                addTextLogMonitor(Language.getWord("NOACCESS"));       
        }//fin del for
   }

 }

 public Vector getDatabases() {
   return validDB;
 }

 public Vector getConn() {
   return vecConn;
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

} //Fin de la Clase 
