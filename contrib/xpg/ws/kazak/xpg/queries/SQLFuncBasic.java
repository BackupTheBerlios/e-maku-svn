
/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS SQLFuncBasic v 0.1                                                   
* Descripcion:
* Esta clase contiene la estructura de datos sobre la ayuda de las instrucciones  
* SQL basicas. 
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2002/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.queries;

import ws.kazak.xpg.idiom.Language;

public class SQLFuncBasic
 {
   int polymorph = 0; 
   String[] description = new String[3];

   public SQLFuncBasic() {}

   public SQLFuncBasic(String commandName, String commandDescrip, String commandSyntax)
    {
     description[0] = commandName; 
     description[1] = commandDescrip;
     description[2] = commandSyntax;
    }

   public String[] getFunctionDescrip()
    {
     return description;
    }

   public String getHtml()
    {
     String th = "<th align=\"center\">";
     String td = "<td align=\"center\">";
     String nctd = "<td align=\"left\">";
     String nth = "</th>";
     String ntd = "</td>";
     String ntable = "</table></html>";
     String tr = "<tr>";
     String std = "<td>";
     String ntr = "</tr>";
     String table = "<html><table border=1>" + tr;
     String nurl = "</a>";

     String header = table + th + Language.getWord("FDNAME") + ": " + description[0] + nth + ntr;

     String data = "";
     data += header;

     data += tr + td;
     data += "<b>" + Language.getWord("FDDESCR") + ":</b> " + description[1];
     data += ntd + ntr;

     data += tr + nctd;
     data += "<b>" + Language.getWord("SYNT") + ":</b><br>" + description[2];
     data += ntd + ntr;

     data += ntable;

     return data;
    }

 }
