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
 * LNAddUser.java Creado el 31-jul-2008
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
 * Esta clase se encarga de insertar nuevos usuarios al sistema. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class LNUserAdministrator {
	
	private LNGenericSQL LNGtransaccion;
	private SocketChannel sock;
	private String idTransaction;
	
	public LNUserAdministrator(SocketChannel sock, Document doc, Element pack,String idTransaction) {
		
	    this.sock=sock;
	    this.idTransaction=idTransaction;
        String action = ((Element)doc.getRootElement().getChildren().iterator().next()).getValue();
        Element subpack = (Element)pack.getChildren().iterator().next();

        LNGtransaccion = new LNGenericSQL(sock);
        LNGtransaccion.setAutoCommit(false);
        
        try {
	            
	
        	if (action!=null) {
        		if(action.equals("addUser")) {
        			addUser(subpack);
        		}
        		else if(action.equals("removeUser")) {
        			removeUser(subpack);
        		}
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

	private void addUser(Element subpack) throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
		LNGtransaccion.setArgs(subpack,idTransaction);
        LNGtransaccion.generar("SCI0019");
		LNGtransaccion.setArgs(subpack,idTransaction);
        LNGtransaccion.generar("SCI0018");
        List j = subpack.getChildren();
		String login = ((Element)j.get(1)).getValue();
		LinkingCache.setPermisosSQL(EmakuServerSocket.getBd(sock),login);
	}

	private void removeUser(Element subpack) throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
		LNGtransaccion.setArgs(subpack,idTransaction);
        LNGtransaccion.generar("SCD000000");
		LNGtransaccion.setArgs(subpack,idTransaction);
        LNGtransaccion.generar("SCD000000");
        List j = subpack.getChildren();
		String login = ((Element)j.get(0)).getValue();
	}
}
