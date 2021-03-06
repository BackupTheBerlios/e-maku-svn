package server.businessrules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import server.comunications.EmakuServerSocket;
import server.database.sql.DontHaveKeyException;
import server.database.sql.LinkingCache;
import server.database.sql.QueryRunner;
import server.database.sql.SQLBadArgumentsException;
import server.database.sql.SQLNotFoundException;

import common.misc.language.Language;

/**
 * LNDocuments.java Creado el 29-jun-2005
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
 * Esta clase se encarga de procesar la informacion de transacciones generadas 
 * por documentos, por ejemplo en una factura procesaria el almacenamiento
 * del documento como tal, informacion de inventarios e informacion contable.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class LNDocuments {

    private static SocketChannel sock;
    private static String idTransaction;
    private static Document doc;
    private static Element pack;
    private static LNGenericSQL LNGtransaccion;
    private static double partidaDoble = 0;
    private static String date;
    private static String minDate;
    /*
     * Estos seis atributos son necesarios para poder generar un 
     * documento, ellos pueden venir con la transaccion, o 
     * tambien pueden ser obtenidos por el servidor de transacciones
     * deacuerdo a la parametrizacion que se de.
     */
    
    private static String idDocument;
    private static String linkDocument;
    private static String multiDocument;
    private static String rfDocument;
    private static String consecutive;
    private static boolean cash;
    private static boolean lockDocument;
    
    // Este ultimo objeto define si se insertara un nuevo registro en la tabla documentos
    
    private static String actionDocument;
    private static String bd;
    private static final String CREATE_DOCUMENT = "createDocument";
    private static final String ANNUL_DOCUMENT = "annulDocument";
    public static final String DELETE_DOCUMENT = "deleteDocument";
    public static final String EDIT_DOCUMENT = "editDocument";
    public static String ndocument;
    public static boolean reloadProdServ;
    public static String initProdServ;
    /*
     *  Inicializando variables estaticas en instanciacion de la clase
     */
    
    
    public LNDocuments(SocketChannel sock,Document doc,	Element pack, String idTransaction) {
    	makeTransaction(sock,doc,pack,idTransaction);
    }
    
    /**
     * Este metodo es el encargado de procesar la transaccion recibida
     * la razon por la que es estatico sincronizado es debido a que por
     * lo general cuando se procesa un documento, este necesita informacion
     * como el consecutivo, saldos de cuentas, saldos de inventarios, los cuales
     * estan almacenados en caches y no deben cambiarce hasta que la transaccion
     * no halla sido terminada de procesar. El static hace que sin importar su 
     * instancia, siempre el metodo sera el mismo y el synchronized, sincroniza
     * la transaccion para que no sea accedida mas de una vez mientras se esta
     * procesando la informacion.
     * 
     * En esta version se sincronizaran todas las transacciones sin importar que 
     * estas sean para diferentes bases de datos.
     * 
     * Para futuras versiones se aspira que la sincronizacion sea independiente 
     * de cada base de datos.
     * @throws LNErrorProcecuteException
     */
    
    private static synchronized void makeTransaction(SocketChannel _sock,Document _doc,	Element _pack, String _idTransaction) {
        idDocument = null;
        linkDocument = null;
        multiDocument = null;
        rfDocument = null;
        consecutive = null;
        cash = false;
        lockDocument = false;
        reloadProdServ = false;
        
    	LNDocuments.sock=_sock;
        LNDocuments.doc=_doc;
        LNDocuments.pack=_pack;
        LNDocuments.idTransaction=_idTransaction;
        partidaDoble = 0;
        LNUndoSaldos.clearSaldosAnteriores();
        /*
         * Se procede a validar la informacion de la parametrizacion
         */
        bd = EmakuServerSocket.getBd(sock);
        Iterator i = doc.getRootElement().getChildren().iterator();
        Iterator j = pack.getChildren().iterator();
        LNGtransaccion = new LNGenericSQL(sock);
        LNGtransaccion.setAutoCommit(false);
        CacheKeys.cleanKeys();
        
        try {
	        for(int m=0;i.hasNext();m++) {
	            
	            Element sql = (Element) i.next();
	            Element subpackage = (Element) j.next();
	
                /*
                 * Primero se valida la configuracion para el procesamiento 
                 * de un documento
                 */
	            if (m==0) {
	                getDocumentConfig(sql);

	                /*
	                 * Una vez validada la configuracion del documento, se procede
	                 * a verificar si se creara un documento, este es el segundo
	                 * paso a segir  es verifica que las variables date y consecutive 
	                 * tengan valor, si no es asi este valor debe venir en el primer 
	                 * paquete de la transaccion
	                 */
	                
	               
	                
	                if (actionDocument.equals(CREATE_DOCUMENT)) {

	                    boolean parameters = true;
	                    if (consecutive==null) {
	                        parameters = getParameters(subpackage);
	                    }
	                    
	                    if (parameters) {
	                    	/*
	                    	 * Si la fecha esta vacia entonces el primer argumento recibido sera
	                    	 * la fecha.
	                    	 */
	                    	if ("".equals(CacheKeys.getDate())) {
	                        	String dateDocument = subpackage.getValue();
	                        	CacheKeys.setDate(dateDocument);
	                        	CacheKeys.setMinDate(dateDocument);
	                        	if (i.hasNext()) {
	                        		subpackage = (Element)j.next();
	                        	}
	                        }
	                    	
	                    	SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd HH:mm:SS");
	                    	if (!lockDocument &&
	                    		LinkingCache.getLockDate(bd)!=null && 
	                    	    (LinkingCache.getLockDate(bd).getTime() > sdf.parse(CacheKeys.getDate()).getTime())) {
	                    		throw new InvalidDateException();
	                    	}
	                    	/*
	                    	 *  Si el atributo lockDocument esta parametrizado en el documento entonces el primer argumento a 
	                    	 *  recibir sera la fecha, se obtiene la fecha  y se actualiza la fecha de bloqueo de documento.
	                    	 */
	                    	else if (lockDocument) {
	                    		lockDocument  = false;
	                        	String dateDocument = subpackage.getValue();
	                        	LinkingCache.setLockDate(bd,dateDocument);
	                    	}
	                    	/*
	                         * Si el metodo retorna true, se procede a almacenar
	                         * el documento
	                         */

	                    	createDocument(idDocument.trim(),consecutive.trim());
	                        /*
	                         * Una vez almacenado el documento, este genera automaticamente un campo
	                         * consecutivo llamado ndocumento, el cual sera la llave primaria necesaria
	                         * para el almacenamiento de la informacion de las demas tablas, para obtenerlo
	                         * se hace la siguiente consulta
	                         */
	                        
	                        ndocument = getDocumentKey(idDocument,consecutive);
	                    	
	                    
	                    	/*
	                         * Si existe un documento link entonces se procede a generarlo.
	                         */
	                    	
	                        if (linkDocument!=null) {
	                        	// Se obtiene su consecutivo
	                        	String consecutiveLinkDocument = LinkingCache.getConsecutive(bd,linkDocument);
	                        	// Se crea el documento alterno
		                    	createDocument(linkDocument.trim(),consecutiveLinkDocument);
		    	                
		    	                Element infoDocumentPack = new Element("package");
		                        infoDocumentPack.addContent(new Element("field").setText(getDocumentKey(linkDocument,consecutiveLinkDocument)));
		                        infoDocumentPack.addContent(new Element("field").setText(ndocument));
		                        infoDocumentPack.addContent(new Element("field").setText(EmakuServerSocket.getLoging(sock)));
		                        
		                        if (cash) {
			                        infoDocumentPack.addContent(new Element("field").setText(EmakuServerSocket.getLoging(sock)));
		                            getTransaction(LNGtransaccion,"SCI0016",infoDocumentPack);
		                        }
		                        else {
		                            getTransaction(LNGtransaccion,"SCI0017",infoDocumentPack);
		                        }
	                        }
	                        
	    	                LNGtransaccion.setKey("ndocumento",ndocument);
	    	                
	    	                /*
	    	                 * Una vez almacenado el documento se procede a almacenar la informacion de control
	    	                 * para el documento. Tabla info_documento esta tabla tiene 2 campos obligatorios
	    	                 * que son:
	    	                 * 
	    	                 * id_document: Contiene la llave foranea que enlaza al documento creado, este dato
	    	                 *              se lo obtiene de la consulta anterior
	    	                 * id_usuario: Contiene la llave foranea referente al id del usuario, este dato se lo
	    	                 *             obtiene info_socket.
	    	                 * 
	    	                 * en caso de necesitar guardar informacion adicional como id_caja o id_vendedor, 
	    	                 * la sentencia para este almacenamiento debe ser un update, este automaticamente 
	    	                 * obtendra como llave la misma asignada desde el almacenamiento del documento.            
	    	                 */
	    	                
	    	                Element infoDocumentPack = new Element("package");
	                        infoDocumentPack.addContent(new Element("field").setText(EmakuServerSocket.getLoging(sock)));
	                        
	                        /*
	                         * Se verifica si para el almacenamiento de la transaccion se adicionara el campo caja 
	                         */
	                        
	                        if (cash) {
		                        infoDocumentPack.addContent(new Element("field").setText(EmakuServerSocket.getLoging(sock)));
	                            getTransaction(LNGtransaccion,"SCI00O3",infoDocumentPack);
	                        }
	                        else {
	                            getTransaction(LNGtransaccion,"SCI00O2",infoDocumentPack);
	                        }
	                        

	                    }
                        else {
                            /*
                             * Si por el contrario retorna false, se deshace la 
                             * transaccion y se termina el procesamiento de la
                             * misma
                             */
                            undoTransaction(Language.getWord("ERR_PARAMETERS_DOCUMENT"));
                            
                            return;
                        }
	                    
	                } 
	                
	                /*
	                 * Si se anula un documento entonces ....
	                 */
	                else if (actionDocument.equals(ANNUL_DOCUMENT)) {
	                	/*
	                	 * El primer paquete recibido del cliente debe ser el numero de documento
	                	 * a anular, se lo obtiene para adicionar su llave primaria para poder
	                	 * hacer los procedimientos necesarios para la anulacion
	                	 */
	                	String numero = subpackage.getValue();
    	                String primaryKey = getPrimaryKey(numero);
	                	numero="0000000000".substring(0,10-numero.length())+numero;
	                	consecutive=numero;

                    if (primaryKey==null) {
                    		undoTransaction(Language.getWord("ERR_ANNUL_DOCUMENT_NOT_FOUND"));
                    		break;
                    }
    	                
        	            
        	            /*
        	             * Si el documento ya fue anulado entonces se aborta el proceso de 
        	             * anulacion
        	             */
        	            if (!isAnnullDocument(primaryKey)) {
        	            	throw new AnnulDocumentException(idDocument,numero);
        	            }
        	            
        	            /*
        	             * Se verifica si se debe anular documentos de referencias, si es asi, se vuelve
        	             * a hacer las verificaciones anteriores para este
        	             */
        	            
        	            if ((rfDocument != null) && !rfDocument.equals("")) {
        	            	QueryRunner RQrfkey = new QueryRunner(bd,"SCS0053",new String[]{primaryKey});
            	            ResultSet RSrfkey = RQrfkey.ejecutarSELECT();
        	            	String rfKey = "";
            	            while (RSrfkey.next()) {
            	                rfKey=RSrfkey.getString(1);
            	            }
            	            if ("".equals(rfKey)) {
                        		undoTransaction(Language.getWord("ERR_ANNUL_RF_DOCUMENT_NOT_FOUND"));
                        		break;
            	            }
            	            String tmpKey = LNGtransaccion.getKey(0);
            	            LNGtransaccion.removeKey("ndocumento");
        	                Element documentPack = new Element("package");
                            documentPack.addContent(new Element("field").setText(rfKey));
                            getTransaction(LNGtransaccion,"SCU0001",documentPack);
            	            LNGtransaccion.setKey("ndocumento",tmpKey);
            	            

        	            }
        	            
        	            /*
        	             * Si se esta anulando un documento de bloqueo, entonces se debe verificar cual
        	             * es el actual documento de bloqueo para ser actualizado en el sistema.
        	             */
        	            if (lockDocument) {
                    		LinkingCache.reloadLockDate(bd);
                    	}
        	            
                        /*
                         * Se genera un paquete para hacer una transaccion, en este caso se modificara
                         * el estado del documento campo estado igual a false, significa documento
                         * anulado
                         */

    	                Element documentPack = new Element("package");
                        documentPack.addContent(new Element("field"));
                        getTransaction(LNGtransaccion,"SCU0001",documentPack);

    	                /*
    	                 * una vez obtenido el numero de documento se avanza un elemento para empezar 
    	                 * el procesamiento del documento.
    	                 */
                        if (i.hasNext()) {
                        	subpackage = (Element) j.next();
                        }
                        else {
                        	break;
                        }
	                } else if (actionDocument.equals(DELETE_DOCUMENT)) {
		                	/*
		                	 * se recibe  el numero de documento a borrar
		                	 * se lo obtiene para adicionar su llave primaria para poder
		                	 * hacer los procedimientos necesarios para la anulacion
		                	 */
		                	String numero = subpackage.getValue();
	    	                String key = getPrimaryKey(numero);
                        	if (i.hasNext()) {
                        		subpackage = (Element)j.next();
                        	}
	    	                
	                        /*
	                         * Si no existe fecha definida en las caches, entonces el segundo argumento a recibir
	                         * debe ser la fecha
	                         */
	                        if ("".equals(CacheKeys.getDate())) {
	                        	String dateDocument = subpackage.getValue();
	                        	CacheKeys.setDate(dateDocument);
	                        	CacheKeys.setMinDate(dateDocument);
	                        	if (i.hasNext()) {
	                        		subpackage = (Element)j.next();
	                        	}
	                        }

	    	             
                        if (key==null) {
                    		undoTransaction(Language.getWord("ERR_DELETE_DOCUMENT_NOT_FOUND"));
                    		break;
                        }
	                	
	                } else if (actionDocument.equals(EDIT_DOCUMENT)) {
	                	/*
	                	 * El primer paquete recibido del cliente debe ser el numero del documento
	                	 * a editar, se lo obtiene para adicionar su llave primaria para poder
	                	 * hacer los procedimientos necesarios para la edicion
	                	 */
	                	
	                	String numero = subpackage.getValue();
    	                String key = getPrimaryKey(numero);
    	                System.out.println("llave primaria: "+key);
                        if (i.hasNext()) {
                        	subpackage = (Element) j.next();
                        }
                        else {
                        	break;
                        }
                        /*
                         * Si no existe fecha definida en las caches, entonces el segundo argumento a recibir
                         * debe ser la fecha
                         */
                        if ("".equals(CacheKeys.getDate())) {
                        	String dateDocument = subpackage.getValue();
                        	CacheKeys.setDate(dateDocument);
                        	CacheKeys.setMinDate(dateDocument);
                        	if (i.hasNext()) {
                        		subpackage = (Element)j.next();
                        	}
                        }
                        
                        updateDateDocument(key,CacheKeys.getDate());
	                }

	                /*
	                 * una vez leida la parametrizacion del documento se avanza una etiqueta al paquete de 
	                 * configuracion, para empezar a obtener las sentencias de procesamiento, de aqui en 
	                 * adelante el procesamiento es similar a LNMultipackage, con la diferencia de que
	                 * cuando se crea un documento, este obtiene por consulta la llave primaria lo cual
	                 * genera mayor eficiencia en el procesamiento.
	                 */
	                sql = (Element) i.next();
	                System.out.println("pasando al LN");
	            }
	            
	            try {
	                /*
	                 * Empezamos validando si lo que se esta recibiendo es un argumento que contiene una sentencia,
	                 * <arg>INS0000</arg>, o un paquete que contiene la parametrizacion de una clase encargada de 
	                 * validar logica de negocios <LNData/>
	                 */
	                
	                if (sql.getName().equals("arg")) {
		                System.out.println("SQL: "+sql.getValue());
		                /*
		                 * Es necesario validar si el paquete no tiene subpaquetes, en caso de tenerlos
		                 * cada subpaquete debe ser procesado por separado, en caso de que ocurra
		                 * la excepcion quiere decir que el paquete venia vacio
		                 */
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

		            	if (((Element)subpackage.getChildren().iterator().next()).getName().equals("field")) {
		            		XMLOutputter out = new XMLOutputter();
		            		out.setFormat(Format.getPrettyFormat());
		            		
		            		if ("discardBadArguments".equals(sql.getAttributeValue("attribute"))) {
		            			LNGtransaccion.setDiscardBadArgument(true);
		            		}
			                getTransaction(LNGtransaccion,sql.getValue(), subpackage);
			            }
			            else {
		            		getfields(LNGtransaccion,sql.getValue(),subpackage);
			            }
	                }
	                /*
	                 * Cuando se encuentran una etiqueta <subarg/> quiere decir que el paquete sera procesado varias
	                 * veces, sea por instrucciones sql o por clases especificas de logica de negocios.
	                 */
	                else if (sql.getName().equals("subarg")) {
	                    Iterator  isubargs = sql.getChildren().iterator();
	                    while (isubargs.hasNext()) {
	                        Element subargs = (Element)isubargs.next();
	                    	if ("addKey".equals(subargs.getAttributeValue("attribute"))) {
	                    		LNGtransaccion.setGenerable(false);
	                    	} else if("removeKey".equals(subargs.getAttributeValue("attribute"))	){
	                    		LNGtransaccion.removeKey(subargs.getValue());
	                    		CacheKeys.removeKey(subargs.getValue());
			            		LNGtransaccion.setGenerable(false);
			            	}
			            	else {
			            		LNGtransaccion.setGenerable(true);
			            	}
        	                System.out.println("SQL: "+subargs.getValue());
	    
	                        if (subargs.getName().equals("arg")) {
	    			            if (((Element)subpackage.getChildren().iterator().next()).getName().equals("field")) {
	    			                getTransaction(LNGtransaccion,subargs.getValue(), subpackage);
	    			            }
	    			            else {
	    			        		getfields(LNGtransaccion,subargs.getValue(),subpackage);
	    			            }
	                        }
	                        else if (subargs.getName().equals("LNData")) {
	                            if (((Element)subpackage.getChildren().iterator().next()).getName().equals("field")) {
		    	                    validLNData(subargs,subpackage);
	                            }
	                            else {
	                    	        Iterator ipack = subpackage.getChildren().iterator();
	                                while (ipack.hasNext()) {
	                                    Element epack = (Element)ipack.next();
	    	    	                    validLNData(subargs,epack);
	                                }
	                            }
	                        }
	                        
	                        else {
	    	                    undoTransaction(Language.getWord("ERR_PACKAGE_NOT_FOUND")+" "+subargs.getValue());
	    	                    return;
	                        }
	                    }
	                }
	                else if (sql.getName().equals("LNData")) {
		                System.out.println("SQL: "+sql.getValue());
                        if (((Element)subpackage.getChildren().iterator().next()).getName().equals("field")) {
    	                    validLNData(sql,subpackage);
                        }
                        else {
                	        Iterator ipack = subpackage.getChildren().iterator();
                            while (ipack.hasNext()) {
                                Element epack = (Element)ipack.next();
	    	                    validLNData(sql,epack);
                            }
                        }
	                }
	                else if (sql.getName().equals("multiDocument")) {
	                	int repeat = Integer.parseInt(subpackage.getValue());
	                	Element e = new Element("multipackage");
	                	int n=0;
	                    while (j.hasNext()) {
	                    	Element sb = (Element)((Element)j.next()).clone();
	                    	e.addContent(sb);
	                    	System.out.println("paquete "+(n++)+" valor "+sb.getValue());
	                    }
                		LNGtransaccion.setGenerable(true);
	                	for (int h=0;h<repeat;h++) {
	                        CacheKeys.cleanKeys();
	                        CacheKeys.setDate(LNDocuments.date);
	                        CacheKeys.setDate(LNDocuments.minDate);
	                		LNGtransaccion.setGenerable(true);
	                		System.out.println("Documento "+h);
	                		LNGtransaccion.removeKey("ndocumento");
	                    	// Se obtiene su consecutivo
	                    	String consecutiveLinkDocument = LinkingCache.getConsecutive(bd,multiDocument);
	                    	// Se crea el documento alterno
	                    	createDocument(multiDocument.trim(),consecutiveLinkDocument);
	                    	
	                    	String keyDocument = getDocumentKey(multiDocument,consecutiveLinkDocument);
	    	                
	    	                Element infoDocumentPack = new Element("package");
	                        infoDocumentPack.addContent(new Element("field").setText(getDocumentKey(multiDocument,consecutiveLinkDocument)));
	                        infoDocumentPack.addContent(new Element("field").setText(ndocument));
	                        infoDocumentPack.addContent(new Element("field").setText(EmakuServerSocket.getLoging(sock)));
	                        
	                        if (cash) {
		                        infoDocumentPack.addContent(new Element("field").setText(EmakuServerSocket.getLoging(sock)));
	                            getTransaction(LNGtransaccion,"SCI0016",infoDocumentPack);
	                        }
	                        else {
	                            getTransaction(LNGtransaccion,"SCI0017",infoDocumentPack);
	                        }
	    	                LNGtransaccion.setKey("ndocumento",keyDocument);
	    	                
		                    
		                    
		                    Iterator isubargs = sql.getChildren().iterator();
		                    Iterator ipackage = e.getChildren().iterator();

		                    while (isubargs.hasNext()) {
		                        Element subargs = (Element)isubargs.next();
		                        Element msubpackage = (Element)ipackage.next();
		                        
		                    	if ("addKey".equals(subargs.getAttributeValue("attribute"))) {
		                    		LNGtransaccion.setGenerable(false);
		                    	} else if("removeKey".equals(subargs.getAttributeValue("attribute"))	){
		                    		LNGtransaccion.removeKey(subargs.getValue());
		                    		CacheKeys.removeKey(subargs.getValue());
				            		LNGtransaccion.setGenerable(false);
				            	}
				            	else {
				            		LNGtransaccion.setGenerable(true);
				            	}
		    
		                        if (subargs.getName().equals("arg")) {
		    			            if (((Element)msubpackage.getChildren().iterator().next()).getName().equals("field")) {
		    			                getTransaction(LNGtransaccion,subargs.getValue(), msubpackage);
		    			            }
		    			            else {
		    			        		getfields(LNGtransaccion,subargs.getValue(),msubpackage);
		    			            }
		                        }
		                        else if (subargs.getName().equals("LNData")) {
		                            if (((Element)msubpackage.getChildren().iterator().next()).getName().equals("field")) {
			    	                    validLNData(subargs,msubpackage);
		                            }
		                            else {
		                    	        Iterator ipack = msubpackage.getChildren().iterator();
		                                while (ipack.hasNext()) {
		                                    Element epack = (Element)ipack.next();
		    	    	                    validLNData(subargs,epack);
		                                }
		                            }
		                        }
		                        
		                        else {
		    	                    undoTransaction(Language.getWord("ERR_PACKAGE_NOT_FOUND")+" "+subargs.getValue());
		    	                    return;
		                        }
		                    }
			        		LinkingCache.incrementeConsecutive(bd,multiDocument);

	                	}
    	                multiDocument=null;

                    }
	                else {
	                    undoTransaction(Language.getWord("ERR_PACKAGE_NOT_FOUND")+" "+sql.getName());
	                    return;
	                    
	                }
	            }
	            catch(NoSuchElementException NSEEe) {
	            	NSEEe.printStackTrace();
	            } 

	        }
	        
	        
	        if (partidaDoble==0) {
		        LNGtransaccion.commit();
		        Element ndocument = new Element("ndocument");
		        ndocument.setText(consecutive);
		        RunTransaction.successMessage(sock,
								          	  idTransaction,
								          	  Language.getWord("TRANSACTION_OK"),
								          	  ndocument);
		        /*
		         * Se verifica si la accion generada es crear un documento nuevo
		         * si es asi, entonces se incrementa el consecutivo del documento
		         * correspondiente.
		         */
		        if (CREATE_DOCUMENT.equals(LNDocuments.actionDocument)) {
		        	System.out.println("Se creo un documento, avanzando consecutivo...");
	        		LinkingCache.incrementeConsecutive(bd,idDocument);
	        		if (linkDocument!=null){
	        			System.out.println("Se creo un documento enlace, avanzando consecutivo...");
	        			LinkingCache.incrementeConsecutive(bd,linkDocument);
	        		}
	        	} else if (DELETE_DOCUMENT.equals(LNDocuments.actionDocument)) {
	        		LinkingCache.reloadConsecutive(bd,idDocument);
	        		if (linkDocument!=null){
	        			LinkingCache.incrementeConsecutive(bd,linkDocument);
	        		}
	        	}
		        
		        if (reloadProdServ) {
					LinkingCache.reloadAsientosPr(EmakuServerSocket.getBd(sock),"SCS0092",new String[]{initProdServ});
					reloadProdServ = false;
		        }
	        }
	        else {
	        	undoTransaction(Language.getWord("ERR_DONT_HAVE_BALANCE_EXCEPTION")+" "+partidaDoble);
	        }
	    }
        catch (SQLNotFoundException SQLNFEe) {
        	SQLNFEe.printStackTrace();
            undoTransaction(SQLNFEe.getMessage());
        }
        catch (SQLBadArgumentsException SQLBAEe) {
        	SQLBAEe.printStackTrace();
            undoTransaction(SQLBAEe.getMessage());
        }
        catch (SQLException SQLe) {
        	SQLe.printStackTrace();
            undoTransaction(SQLe.getMessage());
        }
        catch(NullPointerException NPEe ) {
        	NPEe.printStackTrace();
            undoTransaction(NPEe.getMessage());
        }
        catch (SecurityException SEe) {
        	SEe.printStackTrace();
            undoTransaction(SEe.getMessage());
        }
        catch (IllegalArgumentException IAEe) {
        	IAEe.printStackTrace();
            undoTransaction(IAEe.getMessage());
        }
        catch (ClassNotFoundException CNFEe) {
        	CNFEe.printStackTrace();
            undoTransaction(CNFEe.getMessage());
        }
        catch (NoSuchMethodException NSMEe) {
        	NSMEe.printStackTrace();
            undoTransaction(NSMEe.getMessage());
        }
        catch (InstantiationException IEe) {
        	IEe.printStackTrace();
            undoTransaction(IEe.getMessage());
        }
        catch (IllegalAccessException IAEe) {
        	IAEe.printStackTrace();
            undoTransaction(IAEe.getMessage());
        }
        catch (InvocationTargetException ITEe) {
        	ITEe.printStackTrace();
            undoTransaction(ITEe.getLocalizedMessage());
        }
        catch (LNErrorProcecuteException LNEPEe) {
        	LNEPEe.printStackTrace();
            undoTransaction(LNEPEe.getMessage());
        }
        catch (DontHaveKeyException DHEe) {
        	DHEe.printStackTrace();
			undoTransaction(DHEe.getMessage());
		}
        catch (DontHaveBalanceException DHBEe) {
        	DHBEe.printStackTrace();
        	undoTransaction(DHBEe.getMessage());
        } catch (AnnulDocumentException ADEe) {
			// TODO Auto-generated catch block
//			ADEe.printStackTrace();
        	undoTransaction(ADEe.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			undoTransaction(e.getMessage());
		}
		catch (InvalidDateException IDEe) {
			// TODO Auto-generated catch block
			undoTransaction(IDEe.getMessage());
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			undoTransaction(e.getMessage());
		} 
        finally {
            CacheKeys.setKeys(new Hashtable());
        }
        LNGtransaccion.setAutoCommit(true);

    }

    /**
     * Este metodo actualiza la fecha de un documento
     * @param key
     * @param date
     * @throws SQLException 
     * @throws SQLBadArgumentsException 
     * @throws SQLNotFoundException 
     */
    
    private static void updateDateDocument(String key,String date) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
		QueryRunner RQupdateDateDocument = new QueryRunner(bd,"SCU0004",new String[] { date,key });
		RQupdateDateDocument.ejecutarSQL();
		RQupdateDateDocument.closeStatement();
    }
    
    /**
     * Este metodo se encarga de verifica si un documento ha sido anulado anteriormente
     * @throws SQLBadArgumentsException 
     * @throws SQLNotFoundException 
     * @throws SQLException 
     */

    private static boolean isAnnullDocument(String keyDocument) 
    throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
        boolean annul = true;
        QueryRunner RQkey = new QueryRunner(bd,"SCS0045",new String[]{keyDocument});
        ResultSet RSdatos = RQkey.ejecutarSELECT();
        while (RSdatos.next()) {
            annul=RSdatos.getBoolean(1);
        }
        RSdatos.close();
        RQkey.closeStatement();
        return annul;
    }
    
    private static String getPrimaryKey(String numero) throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
        String primaryKey=null;
        QueryRunner RQkey = new QueryRunner(bd,"SCS0024",new String[]{idDocument,numero});
        ResultSet RSdatos = RQkey.ejecutarSELECT();
        while (RSdatos.next()) {
            primaryKey=RSdatos.getString(1);
        }
        LNGtransaccion.setKey("ndocumento",primaryKey);
        CacheKeys.setKeys(LNGtransaccion.getKeys());
        RSdatos.close();
        RQkey.closeStatement();
        return primaryKey;

    }
    /**
     * Este metodo se encarga de validar la parametrizacion para generar el procesamiento del paquete
     * por una clase especifica.
     * @param args contiene la estructura de <LNData/>
     * @param pack contiene el paquete a procesar
     * @throws InvocationTargetException npi
     * @throws IllegalAccessException npi
     * @throws InstantiationException npi
     * @throws NoSuchMethodException si el metodo llamado no existe
     * @throws ClassNotFoundException si la clase parametrizada no existe
     * @throws IllegalArgumentException si los parametros de un metodo o constructor son incorrectos
     * @throws SecurityException si no se tiene los permisos necesarios para ejecutar un metodo
     * @throws LNErrorProcecuteException
     * @throws SQLException
     * @throws SQLNotFoundException
     * @throws SQLBadArgumentsException
     * @throws DontHaveKeyException 
     * @throws DontHaveBalanceException 
     * @throws InterruptedException 
     */
    
    private static void validLNData(Element args,Element pack) throws SecurityException, 
    IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, 
    InstantiationException, IllegalAccessException, InvocationTargetException, LNErrorProcecuteException, 
    SQLBadArgumentsException, SQLNotFoundException, SQLException, DontHaveKeyException, DontHaveBalanceException, InterruptedException {
 
        Iterator i = args.getChildren().iterator();
        String driver = null;
        String method = null;
        Element parameters = null;
        while (i.hasNext()) {
            Element e = (Element)i.next();
            String name = e.getName();
            if (name.equals("driver")) {
                driver = e.getValue();
            }
            else if (name.equals("method")) {
                method = e.getValue();
            }
            else if (name.equals("parameters")) {
                parameters = e;
            }
        }
        /*
        System.out.println("paquete a procesar:");
		Document doc = new Document();
		doc.setRootElement((Element) pack.clone());
		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());
		try {
			out.output(doc, System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
        /*
         * Hagamos una trampa: resulta que en este paquete se parametriza tanto el driver o clase como
         * el metodo que se va a ejecutar, esto se instanciara en tiempo de ejecucion. La trampa es la
         * siguiete, se especificara en este codigo la instanciacion de inventarios y de contabilidad 
         * debido a que de esta forma la transaccion es mucho mas rapida que si se instanciara como dije
         * anteriormente en tiempo de ejecucion.
         * 
         * Cuando la cosa es en tiempo de ejecucion, entonces todas las clases deben ser de la siguiente
         * forma:
         * 
         * Deberan tener un constructor el cual puede tener un solo argumento que sera de tipo Element,
         * en el cual estaran los parametros necesarios para el correcto funcionamiento de la clase. Tambien
         * puede no tener parametros, en este caso el elemento parameters sera = a null por tanto la clase
         * sera instanciada sin argumentos.
         * 
         *  En cuanto a los metodos, el parametro que recibira el metodo sera el paquete que le corresponde 
         *  procesar.
         */
        
        if ("LNInventarios".equals(driver)) {
            LNInventarios LNIprocesar = new LNInventarios(parameters,EmakuServerSocket.getBd(sock));
            if (method.equals("movimientos")) {
            	LNIprocesar.movimientos(pack);
            }
            else if(method.equals("traslados")) {
            	LNIprocesar.traslados(pack);
            }
            else {
                throw new NoSuchMethodException(method);
            }
        }
        else if ("LNContabilidad".equals(driver)) {
        	LNContabilidad LNCprocesar;
        	if (parameters!=null) {
        		LNCprocesar = new LNContabilidad(parameters,EmakuServerSocket.getBd(sock)); 
        	}
        	else {
        		LNCprocesar = new LNContabilidad(EmakuServerSocket.getBd(sock));
        	}
        	
        	if ("columnData".equals(method)) {
            	partidaDoble += LNCprocesar.columnData(pack);
            	
            } 
            else if ("fieldData".equals(method)) {
            	partidaDoble += LNCprocesar.fieldData(pack);
            } 
            else if ("columnDataAccount".equals(method)) {
            	partidaDoble += LNCprocesar.columDataAccount(pack);
            }
            else if ("rowDataAccount".equals(method)) {
            	partidaDoble += LNCprocesar.rowDataAccount(pack);
            }
            else if ("anular".equals(method)) {
            	LNCprocesar.anular();
            }
            else if ("recover".equals(method)) {
            	LNCprocesar.recover();
            }
            else if ("recoverDocument".equals(method)) {
            	LNCprocesar.recoverDocument();
            }
            else if ("recoverCost".equals(method)) {
            	LNCprocesar.recoverDocument();
            }
            else if ("deleteDocument".equals(method)) {
            	LNCprocesar.deleteDocument();
            }
            else {
                throw new NoSuchMethodException(Language.getWord("NO_SUCH_METHOD")+method);
            }
		    try {
			    BigDecimal bigDecimal = new BigDecimal(partidaDoble);
			    bigDecimal = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
			    partidaDoble = bigDecimal.doubleValue();
		    }
		    catch(NumberFormatException NFEe) {}
        }
        else if("LNSelectedField".equals(driver)) {
        	LNSelectedField LNSprocesar = new LNSelectedField(parameters,EmakuServerSocket.getBd(sock));
        	//if ("getSubpackage".equals(method)) {
        		LNSprocesar.getFields(pack);
        		
        	//}
        }
        else if ("LNComboInventarios".equals(driver)) {
            LNComboInventarios LNIprocesar = new LNComboInventarios(parameters,EmakuServerSocket.getBd(sock));
            if (method.equals("movimientos")) {
            	LNIprocesar.movimientos(pack);
            }
            else if(method.equals("traslados")) {
            	LNIprocesar.traslados(pack);
            }
            else {
                throw new NoSuchMethodException(method);
            }
        }

        else {
            validExternalClass(driver,method,parameters,pack);
        }
        
    }
    
    private static void validExternalClass(String driver,String method,Element parameters,Element pack) 
    throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, 
    InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> cls = Class.forName(driver);
        Class[] type_args_constructor = null;
        Object[] args_constructor = null;
        Constructor cons;
        Object obj;
        Method meth;
        
        if (method!=null) {
            type_args_constructor = new Class[] { Element.class,String.class };
            
            args_constructor = new Object[] { parameters,EmakuServerSocket.getBd(sock)};
        } 
        
        cons = cls.getConstructor(type_args_constructor);
        obj = cons.newInstance(args_constructor);
        meth = cls.getMethod(method,new Class[]{Element.class});
        meth.invoke(obj, new Object[]{pack});
    
    }
    
    private static void getTransaction(LNGenericSQL LNGtransaccion,String sql,Element pack) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
    	//System.out.println("SQL: "+sql);
        LNGtransaccion.setArgs(pack,idTransaction);
        LNGtransaccion.generar(sql);
        CacheKeys.setKeys(LNGtransaccion.getKeys());
    }

    private static void getfields(LNGenericSQL LNGtransaccion,String sql,Element pack) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
        Iterator k = pack.getChildren().iterator();
        while (k.hasNext()) {
            Element subpackage = (Element) k.next();
            getTransaction(LNGtransaccion,sql, subpackage);
        }
    }
    
    private static String getDocumentKey(String idDocument,String consecutive) 
    throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
        String primaryKey=null;
        QueryRunner RQkey = new QueryRunner(bd,"SCS0024",new String[]{idDocument,consecutive});
        ResultSet RSdatos = RQkey.ejecutarSELECT();
        while (RSdatos.next()) {
            primaryKey=RSdatos.getString(1);
    }
        
        
        RSdatos.close();
        RQkey.closeStatement();
        return primaryKey;
    }
    
    private static void createDocument(String idDocument,String consecutive) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
        Element documentPack = new Element("package");
        documentPack.addContent(new Element("field").setText(idDocument));
        documentPack.addContent(new Element("field").setText(consecutive));
        documentPack.addContent(new Element("field").setText(CacheKeys.getDate().trim()));
        getTransaction(LNGtransaccion,"SCI00O1",documentPack);
    }

    /**
     * Este metodo es utilizado para obtener los parametros de configuracion que no fueron definidos 
     * el el paquete de configuracion del documento.
     * @param config almacena la configuracion
     * @throws SQLBadArgumentsException 
     * @throws SQLNotFoundException 
     * @throws SQLException 
     * @throws ParseException 
     */
    private static boolean getParameters(Element config) throws SQLNotFoundException, SQLBadArgumentsException, SQLException, ParseException {
        Iterator k = config.getChildren().iterator();
        while (k.hasNext()) {
            Element subpackage = (Element) k.next();
            String value = subpackage.getValue();
            /*
             * Se valida si la etiqueta contiene las los argumentos:
             */
            
            // consecutive
            if ("consecutive".equals(subpackage.getAttributeValue("attribute"))) {
                LNDocuments.consecutive = value;
            }
            
        }
        if (consecutive!=null) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Metodo encargado de obtener la configuracion del documento parametrizado
     * @param config contiene la configuracion del documento en <xml/>
     * @throws SQLBadArgumentsException 
     * @throws SQLNotFoundException 
     * @throws SQLException 
     */
    
    private static void getDocumentConfig(Element config) 
    throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
        Iterator k = config.getChildren().iterator();
        while (k.hasNext()) {
            Element subpackage = (Element) k.next();
            String value = subpackage.getValue();
            /*
             * Se valida si la etiqueta contiene los argumentos:
             */
            
            // idDocument
            
            if (subpackage.getAttributeValue("attribute").equals("idDocument")) {
                LNDocuments.idDocument=value;
            }
            // Link document
            else if (subpackage.getAttributeValue("attribute").equals("linkDocument")) {
            	LNDocuments.linkDocument=value;
            }
            // Multi Dcument
            else if (subpackage.getAttributeValue("attribute").equals("multiDocument")) {
            	LNDocuments.multiDocument=value;
            }
            else if (subpackage.getAttributeValue("attribute").equals("rfDocument")) {
            	LNDocuments.rfDocument=value;
            }
            // consecutive
            
            else if (subpackage.getAttributeValue("attribute").equals("consecutive")) {
                String bd = EmakuServerSocket.getBd(sock);
                LNDocuments.consecutive = LinkingCache.getConsecutive(bd,idDocument);
            }
            
            // date
            
            else if (subpackage.getAttributeValue("attribute").equals("date")) {
                GregorianCalendar fecha = new GregorianCalendar();
                SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                LNDocuments.date =formato.format(fecha.getTime());
                LNDocuments.minDate = formato.format(fecha.getTime());
                CacheKeys.setDate(formato.format(fecha.getTime()));
            	CacheKeys.setMinDate(formato.format(fecha.getTime()));
            }
            
            // createDocument
            
            else if (subpackage.getAttributeValue("attribute").equals("actionDocument")) {
                LNDocuments.actionDocument=subpackage.getValue();
            }
            // asignacion de caja
            
            else if (subpackage.getAttributeValue("attribute").equals("cash")) {
                cash = true;
            } 
            else if (subpackage.getAttributeValue("attribute").equals("lockDocument")) {
                lockDocument = true;
            }
            // recuenta nuevos productos
            else if (subpackage.getAttributeValue("attribute").equals("reloadProdServ")) {
                reloadProdServ = true;
    			System.out.println("Voy por reload..");
    			QueryRunner RQtransaction = new QueryRunner(EmakuServerSocket.getBd(sock),"SCS0093");
    			ResultSet rs = RQtransaction.ejecutarSELECT();
    			initProdServ = "85000";
    			if (rs.next()) {
    				initProdServ = rs.getString(1);
    			}
    			///System.out.println("Reload :"+initId);

            }
        }
        
    }
    
    /**
     * Este metodo deshace una transaccion
     */
    
    private static void undoTransaction(String message) {
    	LNGtransaccion.rollback();
        LNUndoSaldos.undoSaldos();
        Element ndocument = new Element("ndocument");
        ndocument.setText(consecutive);
        System.out.println("consecutivo: "+consecutive+" transaccion "+idTransaction);
        RunTransaction.errorMessage(sock,idTransaction,message,ndocument);
    }

	public static String getActionDocument() {
		return actionDocument;
	}

	public static void setActionDocument(String actionDocument) {
		LNDocuments.actionDocument = actionDocument;
	}
}
