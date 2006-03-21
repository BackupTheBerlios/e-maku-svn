/*
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS OptionField v 0.1
* Descripcion:
* Esta clase se encarga de manejar la estructura de datos
* referente a las opciones de un campo de una tabla. 
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*
* Fecha: 2001/10/01
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws
*          Gustavo Gonzalez - xtingray@kazak.ws
*/
package ws.kazak.xpg.db;

public class OptionField {

 String dataType;
 public int charLong;
 int intLong;
 public boolean isNull;
 boolean PrimaryKey;
 boolean UnicKey;
 boolean ForeingKey;
 public String DefaultValue = null;
 String TableRef;
 String FieldRef;
 public String Check="";

 public OptionField(String TypeF,int chL,int IntL, boolean isN,boolean pK,boolean uK,boolean fK,String dV) 
  {
   dataType = TypeF;
   charLong = chL;
   intLong = IntL;
   isNull = isN;
   PrimaryKey = pK;
   UnicKey = uK;  
   ForeingKey = fK;
   DefaultValue = dV;
  }

 public void setRefVal(String TableR,String FieldR)
  {
   TableRef = TableR;
   FieldRef = FieldR;
  }

 public int getCharLong()
  {
    return charLong;
  }

 public boolean isNullField()
  {
    return isNull;
  }

 public boolean isPrimaryKey()
  {
    return PrimaryKey;
  }

 public boolean isUnicKey()
  {
    return UnicKey;
  }

 public boolean isForeingKey()
  {
    return ForeingKey;
  }

 public String getDefaultValue()
  {
   return DefaultValue;
  }

 public String getTableR()
  {
   return TableRef;
  }

 public String getFieldR()
  {
   return FieldRef;
  }

 public String getCheck()
  {
   return Check;
  }

/* Metodo en evaluacion
public int getCategory(String Type)
 {
   int category = 0;
   if(Type.equals("decimal") || Type.equals("float4") || Type.equals("float8") || Type.equals("int2") 
            || Type.equals("int4") || Type.equals("int8") || Type.equals("numeric") || Type.equals("serial"))
         category = 1;
   else
      if (Type.equals("money"))
	   category = 2;
      else
	 if(Type.equals("varchar(n)") || Type.equals("char") || Type.equals("text") || Type.equals("char(n)") 
	    || Type.equals("name"))
	      category = 3;
         else
            if(Type.equals("timeStamp") || Type.equals("timestWithtimeZone") || Type.equals("interval") 
	       || Type.equals("date")  || Type.equals("time") || Type.equals("timeWithTimeZ"))
                  category = 4; 
            else
               if(Type.equals("bool"))
	            category = 5; 
               else
		  if (Type.equals("point") || Type.equals("line") || Type.equals("lc") || Type.equals("box") 
		  || Type.equals("path") || Type.equals("pathN") || Type.equals("polygon") || Type.equals("circle"))
		       category = 6;
                  else
	             if (Type.equals("cidr") || Type.equals("inet"))
		           category = 7;

   return category;
 }

*/

} // Fin de la clase
