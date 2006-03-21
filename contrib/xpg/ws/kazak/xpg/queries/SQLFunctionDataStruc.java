/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2002
*
* CLASS SQLFunctionDataStruc v 0.1                                                   
* Descripcion:
* Esta clase define la estructura de datos que contiene la 
* definicion de una funcion SQL propia de postgreSQL.
*
* Esta clase es instanciada desde la clase Queries.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.queries;

import ws.kazak.xpg.idiom.*;
import java.util.Vector;

public class SQLFunctionDataStruc
 {
   int polymorph = 0; 
   Vector dataStruc = new Vector();

   public SQLFunctionDataStruc() {}

   public SQLFunctionDataStruc(String funcName, String funcRet, String funcDescrip, String funcExample)
    {
     String[] description = new String[4];
     description[0] = funcName; 
     description[1] = funcRet;
     description[2] = funcDescrip;
     description[3] = funcExample;
     dataStruc.addElement(description);
     polymorph = 1;
    }

   public void addItem(String funcName2, String funcRet2, String funcDescrip2, String funcExample2)
    {
     String[] description = new String[4];
     description[0] = funcName2;
     description[1] = funcRet2;
     description[2] = funcDescrip2;
     description[3] = funcExample2;
     dataStruc.addElement(description);
     polymorph++;
    }

   public Vector getFunctionDescrip()
    {
     return dataStruc;
    }

   public String getHtml()
    {
     String th = "<th align=\"center\">";
     String td = "<td align=\"center\">";
     String nth = "</th>";
     String ntd = "</td>";
     String ntable = "</table></html>";
     String tr = "<tr>";
     String std = "<td>";
     String ntr = "</tr>";
     String table = "<html><table border=1>" + tr;
     String url1 = "<a href=\"";
     String url2 = "\">";
     String nurl = "</a>";
     String header = table + th + Language.getWord("FDNAME") + nth + th + Language.getWord("FDRETURN") + nth + th + Language.getWord("FDDESCR") + nth + th + Language.getWord("FDEXAMPLE") + nth + ntr;

     String data = "";
     data += header;

     for(int k=0;k<polymorph;k++)
      {
       String[] var = (String[]) dataStruc.elementAt(k);
      
       data += tr + td + url1;
       data += var[0];
       data += url2;
       data += var[0];
       data += nurl + ntd + td;
       data += var[1];
       data += ntd + std;
       data += var[2];
       data += ntd + td;
       data += var[3];
       data += ntd + ntr;
      }

     data += ntable;

     return data;
    }

   public String[] getSpecificDescr(int k)
    {
     
     if(k > (dataStruc.size() - 1))
      {
       String[] tmp = {}; 
       return tmp;
      }

     String[] dscrip = (String[]) dataStruc.elementAt(k); 

     return dscrip; 
    }

 }
