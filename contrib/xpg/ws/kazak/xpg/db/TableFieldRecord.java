/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS TableFieldRecord v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar una estructura de datos para
* un campo dentro de una tabla.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.db;

public class TableFieldRecord {

 public String Name;
 public String Type;
 public OptionField options;

public TableFieldRecord(String NameField,String NameType,OptionField opc)
 {
  Name = NameField;
  Type = NameType;
  options = opc;
 }

public String getName()
 {
  return Name;
 }

public String getType()
 {
   return Type;
 }

public OptionField getOptions()
 {
   return options;
 }

} // Fin de la Clase
