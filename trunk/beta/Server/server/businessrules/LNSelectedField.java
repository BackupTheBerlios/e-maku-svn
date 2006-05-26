
package server.businessrules;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Element;

import server.database.sql.RunQuery;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

/**
 * 
 * LNSelectedField.java Creado 10/10/2005 
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
 * Este metodo se encarga de sacar campos preseleccionados como argumentos
 * de un paquete para generar sentencia sql; Por ejemplo un paquete del tipo.
 * <package>
 * 	<subpackage>
 *  	<field>204</field>
 *  	<field>10</field>
 *  	<field>25</field>
 *  	<field>32</field>
 *  </subpackage>
 *  ...
 * </package>
 * 
 * si para generar una transaccion solo son necesarios los <fields> 0 y 2 entonces
 * esta clase se la parametrizara para efectuar esa labor; su parametrizacion sera de
 * la siguiente forma:
 * <LNData>
 * 		<driver>LNSelectedField</driver>
 * 		<method>getFields</method>
 * 		<parameters>
 * 			<arg attribute="sql">INS000</arg>
 * 			<arg attribute="fields">0,2</arg>
 * 		</parameters>
 * </LNData>
 * 
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */


public class LNSelectedField {

	private String fields;
	private int[] cols;
	private RunQuery RQfields;
	/**
	 * Constructo... 
	 * @throws SQLBadArgumentsException 
	 * @throws SQLNotFoundException 
	 */
	
	public LNSelectedField(Element parameters,String bd) throws SQLNotFoundException, SQLBadArgumentsException {
        Iterator i = parameters.getChildren().iterator();
        String sql="";
        while (i.hasNext()) {
            Element e = (Element)i.next();
            String attribute = e.getAttributeValue("attribute");
            String value = e.getValue();
            if ("fields".equals(attribute)) {
            	fields=value;
            } 
            else if ("sql".equals(attribute)) {
            	sql = value;
            }
        }
        
        RQfields = new RunQuery(bd,sql);
        
        /*
         * Se obtiene las columnas validas del objeto fields;
         */
        
        StringTokenizer STcols = new StringTokenizer(fields,",");
        cols = new int[STcols.countTokens()];
        for (int j=0;STcols.hasMoreTokens();j++) {
            cols[j] = Integer.parseInt(STcols.nextToken());
        }
	}

	/**
	 * Este metodo se encarga de generar la transaccion para paquetes que contiene
	 * subpaquetes.
	 * @param pack contiene subpaquetes
	 * @throws SQLBadArgumentsException 
	 * @throws SQLNotFoundException 
	 * @throws SQLException 
	 *
	
	public void getSubpackage(Element pack) throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
		
		Iterator isubpack = pack.getChildren().iterator();
		
		/*
		 * Recorriendo subpackeges ....
		 *

		while (isubpack.hasNext()) {
			Element epack = (Element)isubpack.next();
            getFields(epack);
		}
	}*/
	
	/**
	 * Este metodo se encarga de generar latransaccion para paquetes que contienen
	 * fields
	 * @param pack paquete que contiene fields
	 * @throws SQLException
	 * @throws SQLNotFoundException
	 * @throws SQLBadArgumentsException
	 */
	public void getFields(Element pack) throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
		/*
		 * Se define un arreglo en el que se almacenara la informacion para 
		 * generar la transaccion sql, este arreglo es igual al tama�o del 
		 * arreglo de fields mas el numero de llaves
		 */
		
		String[] fieldSQL = new String[cols.length+CacheKeys.size()];
		
		
		/*
		 * Primero se adiciona las llaves ...
		 */
		Iterator keys = CacheKeys.getKeys();
		int i=0;

		for (;keys.hasNext();i++) {
			fieldSQL[i] = (String)keys.next();
		}
		
		/*
		 * Luego se adicionan los valores del paquete 
		 */
		
	//	System.out.println("Numero de registros: "+pack.getChildren().size()+" numero de columnas: "+cols.length+" nombre de la cabecera "+pack.getName());
       for (int j=0;j<cols.length;j++,i++) {
            List lpack = pack.getChildren();
    //        System.out.println("registro: "+j);
    //        System.out.println("columna: "+cols[j]);
    //        System.out.println("valor: "+((Element)lpack.get(cols[j])).getValue());
            fieldSQL[i] = ((Element)lpack.get(cols[j])).getValue();
        }
        
       RQfields.ejecutarSQL(fieldSQL);
	}
}
