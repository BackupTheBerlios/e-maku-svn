/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS SQLCompiler v 0.1                                                   
* Descripcion:
* Esta clase se encarga de agrupar las instrucciones SQL
* contenidas en un archivo plano, eliminando espacios en
* blanco, tabulaciones y organizando cada uno de los comandos.
* por separado.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.queries;

import java.io.*;
import java.util.Vector;

class SQLCompiler {
 Vector Instructions = new Vector();
 String allQ = "";

 SQLCompiler(File file) 
  {
   try 
    { 
     BufferedReader in = new BufferedReader(new FileReader(file));
     int i = 0;
     int k = 0;
     
     while(true)
      { 
        String line = in.readLine();
	if (line == null)
	 break;
	allQ = allQ.concat(line); 
      }       
     while(k != -1)
       { 
         k = allQ.indexOf(";",i);	
         String oneQ = allQ.substring(i,k+1);
	 i = k + 1;
	 oneQ = clearSpaces(oneQ);
	 Instructions.addElement(oneQ);
       }  
    }
    catch(Exception ex)
      {     
      } 
  } 

 public String clearSpaces (String inS)
  {
    String valid = "";

    if((inS.indexOf("  ") != -1) || (inS.indexOf("\t") != -1) || (inS.indexOf("\n" ) != -1))
     {
       int x = 0;
       while (x < inS.length())
        {
	  char w = inS.charAt(x);
	  if (w == '\t'  && ( inS.charAt(x+1) != ' ') && ( inS.charAt(x+1) != '\t') && ( inS.charAt(x+1) != '\n' ))
            valid = valid + " ";
          if (w == '\t'  && (( inS.charAt(x+1) == ' ') || ( inS.charAt(x+1) == '\t') || ( inS.charAt(x+1) == '\n')))
            valid = valid + "";
	  if ( w == ' ' && ( inS.charAt(x+1) != ' ') && ( inS.charAt(x+1) != '\t') && ( inS.charAt(x+1) != '\n'))
            valid = valid + w;
	  if (w == ' '  && (( inS.charAt(x+1) == ' ') || ( inS.charAt(x+1) == '\t') || ( inS.charAt(x+1) != '\n')))
	    valid = valid + "";
	  if ( w == '\n' && ( inS.charAt(x+1) != ' ') && ( inS.charAt(x+1) != '\t') && ( inS.charAt(x+1) != '\n' ))
            valid = valid + " ";
	  if ( w == '\n' && (( inS.charAt(x+1) == ' ') || ( inS.charAt(x+1) == '\t') || ( inS.charAt(x+1) == '\n' )))
	    valid = valid + "";
	  if ( w != ' ' && w != '\t' && w != '\n')
	    valid = valid + w;
	  x++;
        }

    }
   else
     valid = inS;

   while(valid.startsWith(" "))
      valid = valid.substring(1,valid.length());

  return valid;
 }

 public Vector getInstructions()
  {
    return Instructions;
  }

} //Fin de la Clase

