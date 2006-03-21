/*
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ForeignKey v 0.1                                                   
* Descripcion:
* Esta clase maneja la estructura de datos de una llave foranea.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.db;

public class ForeignKey {

  String foreignKeyName = "unnamed";
  String foreignTable = "";
  String opt = "UNSPECIFIED";
  String localField = "";
  String foreignField = "";

  public ForeignKey(String fkn,String tf,String option,String f,String ff) {

    if(!fkn.startsWith("$1") || !fkn.startsWith("unnamed"))
      foreignKeyName = fkn;

    if(!option.startsWith("UNSPECIFIED"))
      opt = option;

    foreignTable = tf;
    localField = f;
    foreignField = ff;
  }

 public String getForeignKeyName() {
   return foreignKeyName;
  }

 public String getForeignTable() {
   return foreignTable;
  }

 public String getOption() {
   return opt;
  }

 public String getForeignField() {
   return foreignField;
  }

 public String getLocalField() {
   return localField;
  }

}

