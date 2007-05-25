package com.kazak.smi.server.database.sql;

import java.util.StringTokenizer;

import com.kazak.smi.server.misc.LogWriter;
/**
 * InstruccionesSQL.java Creado el 2-jul-2004
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de formatear consultas y transacciones.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

public class SQLInstructions {
    
    /**
     * Este metodo retorna una consulta formateada segun los argumentos que se le
     * envien.
     * <br>
     * Ej. el codigo de una consulta es INS0010 que corresponde a:<br>
     * <code>INSERT INTO tipos_telefono (clase,nombre) VALUES (?,?)</code>
     *<br>
     * entonces se procede a reemplazar los <code>?</code> por los
     * argumentos recibidos.
     *
     * @param code codigo de la sentencia SQL, ej. INS0010
     * @param args arreglo de String's que guarda los argumentos para la consulta
     * @return retorna la consulta formateada en un objeto String
     */
    
    public static String getSentence(String code, String[] args)
    throws SQLNotFoundException, SQLBadArgumentsException {
    	
        String sentence = "";
        args = addSlashes(args);
    	String sql = CacheLoader.getSQLSentence("K-"+code);
    	if (sql!=null) {
	        StringTokenizer sqlToken = new StringTokenizer(sql,"?");
	        
	        if(sqlToken.countTokens() - (1)  == args.length){
	            boolean nullFlag = false;
	            for (int i = 0 ; i<args.length; i++) {
            		String jump = nullToken(sqlToken.nextToken(),nullFlag);
	            	if (nullFlag) {
                        nullFlag=false;
                     } 
	            	if (!args[i].equals("NULL")) {
                        sentence += jump + args[ i ];
	                }
	                else {
                        sentence+=jump.substring(0,jump.length()-1) + "NULL";
	                    nullFlag=true;
	                }
		        }
            	sentence+=nullToken(sqlToken.nextToken(),nullFlag);
            	
		        return sentence;
	        }
	        else {
	            LogWriter.write("ERROR: Numero de tokens: " + (sqlToken.countTokens()-1) + " / Numero de args: " + args.length);
	            for (int i=0;i<args.length;i++) {
	            	LogWriter.write("ERROR: Argumento[" + i + "]: " + args[i]);
	            }
	            throw new SQLBadArgumentsException(code);
	        }
	        
    	}
    	else {
    	    throw new SQLNotFoundException(code);
    	}
    }
    
    public  static String[] addSlashes(String[] args) {
    	for(int i=0; i < args.length ; i++) {
    		args[i]= args[i].replace("\'", "\\'");
    	}
    	return args;
    }
    
    private static String nullToken(String value,boolean nullFlag) {
    	if (nullFlag) {
            return value.substring(1);
    	}
    	else {
    		return value;
    	}
    }
    
    /**
     * Este metodo retorna una consulta que no tiene argumentos.
     *
     * @param code codigo de la sentencia SQL, ej. SEL0010
     * @return retorna una sentencia sql en un objeto String
     */
    
    public static String getSentence(String code) throws SQLNotFoundException {
    	String Query = CacheLoader.getSQLSentence("K-"+code);
    	if (Query!=null)
    		return Query;
    	else
    		throw new SQLNotFoundException(code);
    }
}