package server.businessrules;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import server.comunications.SocketServer;
import server.database.sql.CacheEnlace;
import server.database.sql.DontHaveKeyException;
import server.database.sql.RunQuery;
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
    /*
     * Estos cuatro objetos son necesarios para poder generar un 
     * documento, ellos pueden venir con la transaccion, o 
     * tambien pueden ser obtenidos por el servidor de transacciones
     * deacuerdo a la parametrizacion que se de.
     */
    
    private static String idDocument;
    private static String linkDocument;
    private static String consecutive;
    private static boolean cash;
    
    // Este ultimo objeto define si se insertara un nuevo registro en la tabla documentos
    
    private static String actionDocument;
    private static String bd;
    private static final String CREATE_DOCUMENT = "createDocument";
    private static final String ANNUL_DOCUMENT = "annulDocument";
    private static final String DELETE_DOCUMENT = "deleteDocument";
    private static final String EDIT_DOCUMENT = "editDocument";
    
    /**
     * 
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
    	LNDocuments.sock=_sock;
        LNDocuments.doc=_doc;
        LNDocuments.pack=_pack;
        LNDocuments.idTransaction=_idTransaction;
        partidaDoble = 0;
        /*
         * Se procede a validar la informacion de la parametrizacion
         */
        bd = SocketServer.getBd(sock);
        Iterator i = doc.getRootElement().getChildren().iterator();
        Iterator j = pack.getChildren().iterator();
        LNGtransaccion = new LNGenericSQL(sock);
        LNGtransaccion.setAutoCommit(false);
        
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
	                    if (consecutive==null || !"".equals(CacheKeys.getDate())) {
	                        parameters = getParameters(subpackage);
	                    }
	                    
	                    if (parameters) {
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
	                        
	                        String primaryKey=null;
	                        RunQuery RQkey = new RunQuery(bd,"SEL0073",new String[]{idDocument,consecutive});
	        	            ResultSet RSdatos = RQkey.ejecutarSELECT();
	        	            while (RSdatos.next()) {
	        	                primaryKey=RSdatos.getString(1);
	        	            }
	    	                LNGtransaccion.setKey("ndocumento",primaryKey);
	    	                RSdatos.close();
	    	                RQkey.closeStatement();
	    	                
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
	                        infoDocumentPack.addContent(new Element("field").setText(SocketServer.getLoging(sock)));
	                        
	                        /*
	                         * Se verifica si para el almacenamiento de la transaccion se adicionara el campo caja 
	                         */
	                        
	                        if (cash) {
		                        infoDocumentPack.addContent(new Element("field").setText(SocketServer.getLoging(sock)));
	                            getTransaction(LNGtransaccion,"INS0036",infoDocumentPack);
	                        }
	                        else {
	                            getTransaction(LNGtransaccion,"INS0035",infoDocumentPack);
	                        }
	                        
	                        /*
	                         * Si existe un documento link entonces se procede a generarlo.
	                         */
	                        if (linkDocument!=null) {
		                    	createDocument(linkDocument.trim(),CacheEnlace.getConsecutive(bd,linkDocument));
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

                    if (primaryKey==null) {
                    	
                    		undoTransaction(Language.getWord("ERR_ANNUL_DOCUMENT_NOT_FOUND"));
                    		break;
                    }
    	                /*
    	                 * Se verifica que el documento no haya sido anulado anteriormente
    	                 */
    	                boolean annul = true;
    	                RunQuery RQkey = new RunQuery(bd,"SEL0129",new String[]{primaryKey});
        	            ResultSet RSdatos = RQkey.ejecutarSELECT();
        	            while (RSdatos.next()) {
        	                annul=RSdatos.getBoolean(1);
        	            }
    	                RSdatos.close();
    	                RQkey.closeStatement();
    	                
        	            
        	            /*
        	             * Si el documento ya fue anulado entonces se aborta el proceso de 
        	             * anulacion
        	             */
        	            if (!annul) {
        	            	throw new AnnulDocumentException(idDocument,numero);
        	            }
        	            
                        /*
                         * Se genera un paquete para hacer una transaccion, en este caso se modificara
                         * el estado del documento campo estado igual a false, significa documento
                         * anulado
                         */

    	                Element documentPack = new Element("package");
                        documentPack.addContent(new Element("field"));
                        getTransaction(LNGtransaccion,"UPD0022",documentPack);

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
		                	 * El primer paquete recibido del cliente debe ser el numero de documento
		                	 * a borrar, se lo obtiene para adicionar su llave primaria para poder
		                	 * hacer los procedimientos necesarios para la anulacion
		                	 */
		                	String numero = subpackage.getValue();
	    	                String key = getPrimaryKey(numero);
	    	             
                        if (key==null) {
                        	
                        		undoTransaction(Language.getWord("ERR_DELETE_DOCUMENT_NOT_FOUND"));
                        		break;
                        }
	    	                if (i.hasNext()) {
                        		subpackage = (Element) j.next();
                        }
                        else {
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
	                else {
	                    undoTransaction(Language.getWord("ERR_PACKAGE_NOT_FOUND")+" "+sql.getName());
	                    return;
	                    
	                }
	            }
	            catch(NoSuchElementException NSEEe) {
	            	
	            } 

	        }
	        
	        
	        if (partidaDoble==0) {
		        LNGtransaccion.commit();
		        RunTransaction.successMessage(sock,
								          	  idTransaction,
								          	  Language.getWord("TRANSACTION_OK"));
	
		        /*
		         * Se verifica si la accion generada es crear un documento nuevo
		         * si es asi, entonces se incrementa el consecutivo del documento
		         * correspondiente.
		         */
		        if (CREATE_DOCUMENT.equals(LNDocuments.actionDocument)) {
	        		CacheEnlace.incrementeConsecutive(bd,idDocument);
	        	} else if (DELETE_DOCUMENT.equals(LNDocuments.actionDocument)) {
	        		CacheEnlace.reloadConsecutive(bd,idDocument);
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
		}
        finally {
            CacheKeys.setKeys(new Hashtable());
        }
        LNGtransaccion.setAutoCommit(true);

    }

    
    private static String getPrimaryKey(String numero) throws SQLNotFoundException, SQLBadArgumentsException, SQLException {
        String primaryKey=null;
        RunQuery RQkey = new RunQuery(bd,"SEL0073",new String[]{idDocument,numero});
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
     */
    
    private static void validLNData(Element args,Element pack) throws SecurityException, 
    IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, 
    InstantiationException, IllegalAccessException, InvocationTargetException, LNErrorProcecuteException, 
    SQLBadArgumentsException, SQLNotFoundException, SQLException, DontHaveKeyException, DontHaveBalanceException {
 
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
            LNInventarios LNIprocesar = new LNInventarios(parameters,SocketServer.getBd(sock));
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
        	LNContabilidad LNCprocesar = new LNContabilidad(parameters,SocketServer.getBd(sock)); 
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
        	LNSelectedField LNSprocesar = new LNSelectedField(parameters,SocketServer.getBd(sock));
        	//if ("getSubpackage".equals(method)) {
        		LNSprocesar.getFields(pack);
        		
        	//}
        }
        else {
            validExternalClass(driver,method,parameters,pack);
        }
        
    }
    
    private static void validExternalClass(String driver,String method,Element parameters,Element pack) 
    throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, 
    InstantiationException, IllegalAccessException, InvocationTargetException {
        Class cls = Class.forName(driver);
        Class[] type_args_constructor = null;
        Object[] args_constructor = null;
        Constructor cons;
        Object obj;
        Method meth;
        
        if (method!=null) {
            type_args_constructor = new Class[] { Element.class,String.class };
            
            args_constructor = new Object[] { parameters,SocketServer.getBd(sock)};
        } 
        
        cons = cls.getConstructor(type_args_constructor);
        obj = cons.newInstance(args_constructor);
        meth = cls.getMethod(method,new Class[]{Element.class});
        meth.invoke(obj, new Object[]{pack});
    
    }
    
    private static void getTransaction(LNGenericSQL LNGtransaccion,String sql,Element pack) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
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
    
    private static void createDocument(String idDocument,String consecutive) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
        Element documentPack = new Element("package");
        documentPack.addContent(new Element("field").setText(idDocument));
        documentPack.addContent(new Element("field").setText(consecutive));
        documentPack.addContent(new Element("field").setText(CacheKeys.getDate().trim()));
        getTransaction(LNGtransaccion,"INS0029",documentPack);
    }

    /**
     * Este metodo es utilizado para obtener los parametros de configuracion que no fueron definidos 
     * el el paquete de configuracion del documento.
     * @param config almacena la configuracion
     */
    private static boolean getParameters(Element config) {
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
            
            // date
            
            else if ("date".equals(subpackage.getAttributeValue("attribute"))) {
                CacheKeys.setDate(value);
            }
        }
        
        if (!"".equals(CacheKeys.getDate()) && consecutive!=null) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Metodo encargado de obtener la configuracion del documento parametrizado
     * @param config contiene la configuracion del documento en <xml/>
     */
    
    private static void getDocumentConfig(Element config) {
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
            // consecutive
            
            else if (subpackage.getAttributeValue("attribute").equals("consecutive")) {
                String bd = SocketServer.getBd(sock);
                LNDocuments.consecutive = CacheEnlace.getConsecutive(bd,idDocument);
            }
            
            // date
            
            else if (subpackage.getAttributeValue("attribute").equals("date")) {
                GregorianCalendar fecha = new GregorianCalendar();
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                CacheKeys.setDate(formato.format(fecha.getTime()));
            }
            
            // createDocument
            
            else if (subpackage.getAttributeValue("attribute").equals("actionDocument")) {
                LNDocuments.actionDocument=subpackage.getValue();
            }
            // asignacion de caja
            
            else if (subpackage.getAttributeValue("attribute").equals("cash")) {
                cash = true;
            }
        }
    }
    
    /**
     * Este metodo deshace una transaccion
     */
    
    private static void undoTransaction(String message) {
    	LNGtransaccion.rollback();
        LNUndoSaldos.undoSaldos();
        RunTransaction.errorMessage(sock,idTransaction,message);
    }
}
