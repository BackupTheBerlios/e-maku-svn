/*
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS DBStructure v 0.1                                                   
* Descripcion:
* Esta clase representa la estructura de datos para una
* base de datos.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.db;

import java.util.Vector;
import java.util.Hashtable;

public class DBStructure { 

 String DBname;
 Hashtable HTables = new Hashtable();
 Vector Tables;
 boolean isOpen = false;
 int numTables;
 PGConnection conn;

 public DBStructure() {
   DBname="";
   isOpen = false;
   numTables = 0;
 }

 public DBStructure(String db,boolean open) {
     DBname = db;
     isOpen = open;
 }

 public DBStructure(String db,boolean open,Vector vecTables,PGConnection link) {
   DBname = db;
   isOpen = open;
   Tables = vecTables;
   numTables = Tables.size();
   conn = link;
   for(int k=0;k<numTables;k++)
     {
        try {
             Object tmp = vecTables.elementAt(k);   
             Table oneTable = new Table(tmp.toString());         
             HTables.put(tmp.toString(),tmp);       
	    }  
	catch(Exception e)    
	    {
             System.out.println("Error: " + e);
             e.printStackTrace();
	     System.exit(0);
	    }
     }
 }

 public void setTables(Vector vecTables) {
  HTables = new Hashtable();
  Tables = new Vector();
  numTables = vecTables.size();
  for(int k=0;k<numTables;k++)
    {
      Table oneTable;
      try {
             oneTable = (Table) vecTables.elementAt(k);
             HTables.put(oneTable.Name,oneTable);
             Tables.addElement(oneTable.Name);
           }
      catch(Exception e)
         {
          System.out.println("Error: " + e);
          e.printStackTrace();
          System.exit(0);
         }
    }
 }

 public Hashtable getTableSet() {
   return HTables;
 }

 public void setNTName(String oldname,String newName)
  {
   Table tmp = (Table) HTables.get(oldname);
   tmp.Name = newName;
   HTables.remove(oldname);
   HTables.put(newName,tmp);
   int pos = Tables.indexOf(oldname);
   Tables.setElementAt(newName,pos);
  }

 public Table getTable(String TabName) {
  Table tmp = null;
  try {
         tmp = (Table) HTables.get(TabName);
      }
  catch(Exception e)
      { 
        System.out.println("Error: " + e);
        e.printStackTrace();
        System.exit(0);
      } 
  return tmp;
 }

 public int NTables() {
   return numTables;
 }

} // Fin de la Clase
