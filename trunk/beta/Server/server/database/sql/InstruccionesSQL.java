package server.database.sql;

import java.util.StringTokenizer;
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
    
    public static String getSentencia(String bd,String codigo, String[] args)
    throws SQLNotFoundException, SQLBadArgumentsException {
        
        String sentencia = "";
    	String sql = CacheEnlace.getSentenciaSQL("K-"+bd+"-"+codigo);
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
	            System.out.println("numero de tokens: "+(STsql.countTokens()-1)+" numero de args: "+args.length);
	            for (int i=0;i<args.length;i++) {
	            	System.out.println("argumento "+i+": "+args[i]);
	            }
	            throw new SQLBadArgumentsException(codigo);
	        }
    	}
    	else
    	    throw new SQLNotFoundException(codigo);
    	
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
    
    public static String getSentencia(String bd,String codigo) throws SQLNotFoundException {
    	String Query = CacheEnlace.getSentenciaSQL("K-"+bd+"-"+codigo);
    	if (Query!=null)
    		return Query;
    	else
    		throw new SQLNotFoundException(codigo);
    }
    
    /**
     * Metodo que verifica si un usuario tiene permisos
     * sobre una determinada sentencia sql
     * @param bd Base de datos
     * @param query Identificaros unico de usuario, no es el login.
     * @param login Identificador de la sentencia SQL
     */
    
    public static boolean permisoSentencia(String bd,
            							   String login,
            							   String query,
            							   String password) {
            if(CacheEnlace.getPermisosSQL("K-"+
                    				 bd+
                    				 "-"+
                    				 login+
                    				 "-"+
                    				 query+
                    				 "-"+
                    				 password)){
                return true;
            }
            else
                return false;
    }

    /**
     * Metodo que verifica si un usuario tiene permisos
     * sobre una determinada transaccion
     * @param bd Base de datos
     * @param id_transaction Identificaros unico de usuario, no es el login.
     * @param login Identificador de la sentencia SQL
     */
    
    public static boolean permisoTransaccion(String bd,
            							     String login,
            							     String id_transaction,
            							     String password) {
        
        if(CacheEnlace.getPermisosTransaccion("K-"+
                    					  bd+
                    					  "-"+
                    					  login+
                    					  "-"+
                    					  id_transaction+
                    					  "-"+
                    					  password)){
            return true;
        }
        else
            return false;
    }

}


