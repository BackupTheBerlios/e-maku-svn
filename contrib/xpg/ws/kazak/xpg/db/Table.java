/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS Table v 0.1
* Descripcion:
* Esta clase se encarga de manejar la estructura de Datos
* asociada a una tabla.
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

public class Table {

 public String Name;
 int NumRegs;
 Vector Registers;
 public TableHeader base;
 public String schema;
 public boolean userSchema;

 public Table(String TName) {

    Name = TName;
    NumRegs = 0;
    Registers = new Vector();
    base = new TableHeader();
    Registers = new Vector();
  }

 public Table(String nTable,TableHeader strucTable) {

   Name = nTable;
   base = strucTable;
  }

 public void setRegisters(Vector data) {
   Registers = data;
  }

 public TableHeader getTableHeader() {
   return base;
  }

 public String getName() {
   return Name;
  }

 public int getNumRegs() {
   return NumRegs;
  }

 public void setSchema(String schemaName) {
    schema = schemaName;
  }
                                                                                                                            
 public String getSchema() {
    return schema;
  }

 public void setSchemaType(boolean schemaType) {
    userSchema = schemaType;
  }

 public boolean isUserSchema() {
    return userSchema;
  }

} // Fin de la Clase
