/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS TableHeader v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar la estructura de Datos
* asociada al conjunto de atributos que definen una tabla.
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

public class TableHeader {

  public Vector fields = new Vector();
  public Hashtable hashFields = new Hashtable();
  public int NumFields;

  public TableHeader() {
    NumFields = 0;
  }

  public TableHeader(Vector columns) {
    NumFields = columns.size();    
    for(int k=0;k<NumFields;k++)
     {
       TableFieldRecord tmp = (TableFieldRecord)columns.elementAt(k);
       hashFields.put(tmp.getName(),tmp);
       fields.addElement(tmp.getName());
     }

  }

  public String getType(String oneColumn) {
    String Type = "";
    TableFieldRecord tmp = (TableFieldRecord) hashFields.get(oneColumn);
    Type = tmp.getType(); 
    return Type;
  }

  public TableFieldRecord getTableFieldRecord(String nameColumn) {
    TableFieldRecord tmp = (TableFieldRecord) hashFields.get(nameColumn);
    return tmp;
  }

  public Vector getNameFields() {
    return fields;
  }

  public int getNumFields() {
    return NumFields;
  }

  public Hashtable getHashtable() {
    return hashFields;
  }

} // Fin de la Clase
