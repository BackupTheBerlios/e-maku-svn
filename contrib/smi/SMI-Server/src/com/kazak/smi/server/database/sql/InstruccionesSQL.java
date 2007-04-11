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

public class InstruccionesSQL {
    
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
     * @param codigo codigo de la sentencia SQL, ej. INS0010
     * @param args arreglo de String's que guarda los argumentos para la consulta
     * @return retorna la consulta formateada en un objeto String
     */
    
    public static String getSentencia(String codigo, String[] args)
    throws SQLNotFoundException, SQLBadArgumentsException {
    	
        String sentencia = "";
        args = addSlashes(args);
    	String sql = CacheEnlace.getSentenciaSQL("K-"+codigo);
    	if (sql!=null) {
	        StringTokenizer STsql = new StringTokenizer(sql,"?");
	        
	        if(STsql.countTokens() - (1)  == args.length){
	            boolean nulo = false;
	            for (int i = 0 ; i<args.length; i++) {
            		String salto = nullToken(STsql.nextToken(),nulo);
	            	if (nulo) {
                        nulo=false;
                     } 
	            	if (!args[i].equals("NULL")) {
                        sentencia += salto + args[ i ];
	                }
	                else {
                        sentencia+=salto.substring(0,salto.length()-1) + "NULL";
	                    nulo=true;
	                }
		        }
            	sentencia+=nullToken(STsql.nextToken(),nulo);
            	
		        return sentencia;
	        }
	        else {
	            LogWriter.write("numero de tokens: "+(STsql.countTokens()-1)+" numero de args: "+args.length);
	            for (int i=0;i<args.length;i++) {
	            	LogWriter.write("argumento "+i+": "+args[i]);
	            }
	            throw new SQLBadArgumentsException(codigo);
	        }
	        
    	}
    	else
    	    throw new SQLNotFoundException(codigo);
    	
    	
    	
    }
    
    public  static String[] addSlashes(String[] args) {
    	for(int i=0; i < args.length ; i++) {
    		args[i]= args[i].replace("\'", "\\'");
    	}
    	return args;
    }
    
    private static String nullToken(String value,boolean nulo) {
    	if (nulo) {
            return value.substring(1);
    	}
    	else {
    		return value;
    	}
    }
    
    /**
     * Este metodo retorna una consulta que no tiene argumentos.
     *
     * @param codigo codigo de la sentencia SQL, ej. SEL0010
     * @return retorna una sentencia sql en un objeto String
     */
    
    public static String getSentencia(String codigo) throws SQLNotFoundException {
    	String Query = CacheEnlace.getSentenciaSQL("K-"+codigo);
    	if (Query!=null)
    		return Query;
    	else
    		throw new SQLNotFoundException(codigo);
    }
}