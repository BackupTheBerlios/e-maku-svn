package server.logicanegocios;

import java.util.Enumeration;

import server.basedatos.sql.CacheEnlace;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

/**
 * 
 * LNUndoSaldos.java Creado 10/10/2005 
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
 * Esta clase se encarga de almacenar los saldos iniciales cuando se empieza
 * a generar una transaccion, esto es necesario para poder dejar los saldos
 * anteriores como estaban, en caso de que la transaccion falle.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */


public class LNUndoSaldos {

	
	private static Hashtable HSaldoAntInv = new Hashtable();
	private static Hashtable HSaldoAntLibroAux = new Hashtable();
	
	/**
	 * 
	 */
	
	public LNUndoSaldos() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Este metodo inserta saldos anteriores de Inventarios
     * @param bd Nombre de la base de datos
     * @param bodega Codigo de la bodega en la que se va a buscar el producto
     * @param id_prod_serv Codigo del producto al que se le va a buscar el saldo
     * @param saldo el nuevo valor del saldo
	 */

	public static void setSaldoAntInv(String bd,String bodega,String idProdServ,double value) {
		String key = "K-"+bd+"-"+bodega+"-"+idProdServ+"-"+value;
		if (!HSaldoAntInv.containsKey(key)) {
			HSaldoAntInv.put(key,new Double(value));
		}
	}	
	/**
	 * Este metodo inserta saldos anteriores de Libros Auxiliares
     * @param bd Nombre de la base de datos
     * @param centro Codigo del centro costo en el que se va a buscar la cuenta
     * @param cta Cuenta en la que se va a buscar el saldo
     * @param saldo el nuevo valor del saldo
	 */
	public static void setSaldoAntLibroAux(String bd,String centro,String cta,String id_terceros,String id_prod_serv,Double saldo) {
        String key = "K-"+bd+"-"+centro+"-"+cta+"-"+id_terceros+"-"+id_prod_serv;
        if (!HSaldoAntLibroAux.containsKey(key)) {
        	HSaldoAntLibroAux.put(key,saldo);
        }
	}
	
	/**
	 * Este metodo deshace todos los saldos de las tablas que mueven saldos
	 */

	public static void undoSaldos() {
		if (HSaldoAntInv.size()>0) {
			Enumeration Ekeys = HSaldoAntInv.keys();
			Enumeration Evalues = HSaldoAntInv.elements();
			while(Ekeys.hasMoreElements()) {
				CacheEnlace.setVSaldoInventario((String)Ekeys.nextElement(),
												((Double)Evalues.nextElement()).doubleValue());
			}
			HSaldoAntInv.clear();
		}
		if (HSaldoAntLibroAux.size()>0) {
			Enumeration Ekeys = HSaldoAntLibroAux.keys();
			Enumeration Evalues = HSaldoAntLibroAux.elements();
			while(Ekeys.hasMoreElements()) {
				CacheEnlace.setVSaldoInventario((String)Ekeys.nextElement(),
												((Double)Evalues.nextElement()).doubleValue());
			}
			HSaldoAntLibroAux.clear();
		}
	}
}
