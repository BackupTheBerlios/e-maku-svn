package server.businessrules;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jdom.Document;
import org.jdom.Element;

import server.comunications.EmakuServerSocket;
import server.database.sql.LinkingCache;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

import common.misc.language.Language;


/**
 * LNUserPermits.java Creado el 31-jul-2008
 * 
 * Este archivo es parte de E-Maku <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de insertar o eliminar permisos de usuario tanto en la
 * base de datos como en las caches del ST. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class LNUserPermits {
	
	private LNGenericSQL LNGtransaccion;
	private SocketChannel sock;
	
	public LNUserPermits(SocketChannel sock, Document doc, Element pack,String idTransaction) {
		
	    this.sock=sock;
        Iterator i = doc.getRootElement().getChildren().iterator();
        Iterator j = pack.getChildren().iterator();

        LNGtransaccion = new LNGenericSQL(sock);
        LNGtransaccion.setAutoCommit(false);
        boolean a = true;
        
        try {
	        while (i.hasNext()) {
	            
	            Element sql = (Element) i.next();
	            Element subpackage = (Element) j.next();
	
	            try {
	            	if (a) {
		            	verifyPermits(subpackage);
		            	a=false;
	            	}
	            	LNGtransaccion.setArgs(subpackage,idTransaction);
	                LNGtransaccion.generar(sql.getValue());
	            }
	            catch(NoSuchElementException NSEEe) {}
	        }
	        
	        LNGtransaccion.commit();
	        RunTransaction.successMessage(sock,
							          	  idTransaction,
							          	  Language.getWord("TRANSACTION_OK"));
	    }
        catch (SQLNotFoundException SQLNFEe) {
            RunTransaction.errorMessage(sock,
					                 	 idTransaction,
					                 	 SQLNFEe.getMessage());
            LNGtransaccion.rollback();
        }
        catch (SQLBadArgumentsException SQLBAEe) {
            RunTransaction.errorMessage(sock,
                	 idTransaction,
                	 SQLBAEe.getMessage());
            LNGtransaccion.rollback();
        }
        catch (SQLException SQLe) {
            RunTransaction.errorMessage(sock,
               	 idTransaction,
               	 SQLe.getMessage());
            LNGtransaccion.rollback();
        }
        catch(NullPointerException NPEe ) {
            NPEe.printStackTrace();
            LNGtransaccion.rollback();
        }
        finally {
        	doc=null;
        	pack=null;
        }
        LNGtransaccion.setAutoCommit(true);
        LNGtransaccion = null;
    	System.gc();
	}

	private void verifyPermits(Element subpack) {
        List j = subpack.getChildren();
		String idUsuario = ((Element)j.get(0)).getValue();
		String idTransaction = ((Element)j.get(1)).getValue();
		boolean action = Boolean.parseBoolean(((Element)j.get(2)).getValue());
		System.out.println("Usuario: "+idUsuario+" transaccion "+idTransaction+" accion "+action);
		if (action) {
			LinkingCache.setPermisosTransacciones("K-"+EmakuServerSocket.getBd(sock)+"-"+idUsuario+"-"+idTransaction+"-");
		}
		else {
			LinkingCache.removePermisosTransacciones("K-"+EmakuServerSocket.getBd(sock)+"-"+idUsuario+"-"+idTransaction+"-");
		}
        
	}
}
