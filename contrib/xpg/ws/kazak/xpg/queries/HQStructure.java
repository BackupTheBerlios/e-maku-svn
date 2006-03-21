/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2002
*
* CLASS HQStructure v 0.1                                                   
* Descripcion:
* Esta clase se encarga de almacenar la estructura de datos de  
* cada consulta predefinida por el usuario.
*
* Esta clase es instanciada desde la clase HotQueries.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.queries;

public class HQStructure {

 String fileName;
 String value;
 boolean run; 

public HQStructure(String file, String descrip, boolean doIt) {
   fileName = file;
   value = descrip;
   run = doIt;
 }

public String getFileName() {
   return fileName;
 }

public String getValue() {
   return value;
 }

public boolean isReady() {
   return run;
 }

}
