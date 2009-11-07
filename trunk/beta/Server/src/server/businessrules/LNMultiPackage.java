package server.businessrules;

import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import common.misc.language.Language;
import server.comunications.EmakuServerSocket;
import server.database.sql.LinkingCache;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

import org.jdom.Document;
import org.jdom.Element;

/**
 * LNMultiPackage.java Creado el 31-ene-2005
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
 * Esta clase se encarga de distribuir la informacion a diferentes tablas,
 * por lo genera se utiliza en transacciones simples compuestas por subpackages.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */
public class LNMultiPackage {

    private String idTransaction;
    private LNGenericSQL LNGtransaccion;
    
    public LNMultiPackage(SocketChannel sock,
			 Document doc,
			 Element pack,
			 String idTransaction) {
        this.idTransaction=idTransaction;
        
        Iterator i = doc.getRootElement().getChildren().iterator();
        Iterator j = pack.getChildren().iterator();

        LNGtransaccion = new LNGenericSQL(sock);
        LNGtransaccion.setAutoCommit(false);
        
        try {
	        while (i.hasNext()) {
	            
	            Element sql = (Element) i.next();
	            Element subpackage = (Element) j.next();
	
	            try {
	            	/*
	            	 * Estas validaciones informan al componente de logica de negocios debe generar
	            	 * una sentencia sql o por el contrario solo genera una accion como adicionar
	            	 * o remover una llave.
	            	 */
	            	if (sql.getName().equals("arg")) {
		            	if ("addKey".equals(sql.getAttributeValue("attribute"))) {
	                		LNGtransaccion.setGenerable(false);
	                	} else if("removeKey".equals(sql.getAttributeValue("attribute"))	){
	                		LNGtransaccion.removeKey(sql.getValue());
	                		CacheKeys.removeKey(sql.getValue());
		            		LNGtransaccion.setGenerable(false);
		            	}
		            	else {
		            		LNGtransaccion.setGenerable(true);
		            	}
	            	}
	                /*
	                 * Es necesario validar si el paquete no tiene subpaquetes, en caso de tenerlos
	                 * cada subpaquete debe ser procesado por separado, en caso de que ocurra
	                 * la excepcion quiere decir que el paquete venia vacio
	                 */
		            if (((Element)subpackage.getChildren().iterator().next()).getName().equals("field")) {
		                getTransaction(sql.getValue(), subpackage);
		            }
		            else {
	                    getfields(sql.getValue(),subpackage);
		            }
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
        
        /*
         * Solo para mostrar restaurantes...
         */
        
        LinkingCache.reloadCombo(EmakuServerSocket.getBd(sock));
        
        /*
         * -----------------------------------------
         */
        LNGtransaccion.setAutoCommit(true);
        LNGtransaccion = null;
    	System.gc();
    }
    
    private void getfields(String sql,Element pack) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
        Iterator k = pack.getChildren().iterator();
        while (k.hasNext()) {
            Element subpackage = (Element) k.next();
            getTransaction(sql, subpackage);
        }
    }
    
    private void getTransaction(String sql,Element pack) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
        System.out.println("Sentencia: "+sql);
        LNGtransaccion.setArgs(pack,idTransaction);
        LNGtransaccion.generar(sql);
    }
    

}
