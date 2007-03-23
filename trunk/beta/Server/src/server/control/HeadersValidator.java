package server.control;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import common.comunications.SocketWriter;
import common.misc.language.Language;
import common.misc.log.LogAdmin;
import server.businessrules.RunTransaction;
import server.comunications.AcpFailure;
import server.comunications.CacheXML;
import server.comunications.ErrorXML;
import server.comunications.ResultSetToXML;
import server.comunications.EmakuServerSocket;
import server.database.connection.ConnectionsPool;
import server.database.sql.LinkingCache;
import server.database.sql.AccountsTotalCalculator;
import server.misc.ServerConstants;
import server.reports.ReportMaker;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
/**
 * ClientHeaderValidator.java Creado el 22-jul-2004
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
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
 * Esta clase se encarga de Validar las cabeceras (raices) de los paquetes XML
 * que llegan al Servidor de transacciones. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */

public class HeadersValidator {
    
    /**
     * Este metodo se encarga de revisar toda las raices de los documentos que llegan 
     * al servidor de transacciones desde el cliente JMClient.
     * @param doc Documento a validar
     * @param sock Socket por que se esta comunicando
     */

    public static void ValidClient(Document doc, SocketChannel sock) {
        
        /*
         * Obtenemos la raiz del documento si el documento tiene raiz
         */
        Element raiz = doc.getRootElement();
        String nom_raiz = raiz.getName();
        
        /*
         *  Validaci�n de solicitud de paquetes, se verifica si el socket ya fue
         *  autenticado, si lo fue entonces se procede a validar la solicitud 
         *  del paquete requerido, si no se procede a validar un paquete CNX o la
         *  solicitud de paquetes no autorizados (solicitud de paquetes sin previa
         *  autentificaci�n)
         */
        
        if (EmakuServerSocket.isLoged(sock)) {

            /*
             *  Validaci�n de un paquete transaccion
             */
            if (nom_raiz.equals("TRANSACTION")) {
                System.out.println("Paquete TRANSACCION");
                XMLOutputter xmlOutputter = new XMLOutputter();
                xmlOutputter.setFormat(Format.getPrettyFormat());
                try {
                    xmlOutputter.output(doc,System.out);
                    new RunTransaction(sock,doc).start();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            } 
            
            /*
             *  Validacion de una solicitud de consulta
             */
            
            else if (nom_raiz.equals("QUERY")) {

                String bd = EmakuServerSocket.getBd(sock);
                QueryValidator valida = new QueryValidator(bd,EmakuServerSocket.getLoging(sock), raiz);
                String codigo = "";
                if (valida.isValid()) {
                    codigo = raiz.getChild("sql").getValue();
                    ResultSetToXML answer;
                    
                    if (valida.changeStructParam()) {
                        answer = new ResultSetToXML(bd, codigo, valida.getArgs(),sock,valida.getId());
                    } else {
                        answer = new ResultSetToXML(bd, codigo,sock,valida.getId());
                    }
                    new Thread(answer).start();
                    //answer.run();
                    //answer.transmition();
                    
/*                    try {
                        XMLOutputter xmlOutputter = new XMLOutputter();
                        xmlOutputter.setFormat(Format.getPrettyFormat());
                        xmlOutputter.output(doc,System.out);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
*/                    
                } else {
                    ErrorXML error = new ErrorXML();
                    String tmp = Language.getWord("SQL_ACCESS_DENIED") + 
                    		" "+valida.getQuery()+" "+
                            sock.socket();
                    LogAdmin.setMessage(tmp, ServerConstants.MESSAGE);
                    SocketWriter.writing(sock, error.returnError(ServerConstants.ERROR,
					            	      EmakuServerSocket.getBd(sock),
					            	      valida.getId(),
					            	      tmp));
                }

            } 
            
            /*
             *  Validacion de solicitud de paquetes de cache
             */
            
            else if (nom_raiz.equals("CACHE-QUERY")) {
                String codigo = raiz.getValue();
                String bd = EmakuServerSocket.getBd(sock);
                CacheXML cache_answer = new CacheXML(bd,codigo);
                cache_answer.transmition(sock);
            } 
    
            /*
             *  Validacion de solicitud de la fecha
             */
            
            else if (nom_raiz.equals("DATE")) {
                SocketWriter.writing(sock,DATESender.getPackage());
                
            }
            
            /*
             * Validacion para totalizacion de cuentas contables
             */
            
            else if (nom_raiz.endsWith("TOTALACCOUNT")) {
            	new AccountsTotalCalculator(EmakuServerSocket.getBd(sock),raiz);
            }

            /*
             *  Validacion de solicitud del codigo consecutivo de un documento
             */
            
            else if (nom_raiz.equals("UPDATECODE")) {
                String bd = EmakuServerSocket.getBd(sock);
                String key = raiz.getChild("idDocument").getValue();
                SocketWriter.writing(sock,
                                    UPDATECODESender.getPackage(key,
                                                              LinkingCache.getConsecutive(bd,key)));

            }
            /* Recepcion de un paquete de solicitud de un reporte */
            else if (nom_raiz.equals("PLAINREPORTREQUEST")) {
                System.out.println("Nueva solicitud de un reporte plano");
                new ReportMaker(raiz, sock, true);
            }            
            /* Recepcion de un paquete de solicitud de un reporte */
            else if (nom_raiz.equals("REPORTREQUEST")) {
                System.out.println("Nueva solicitud de un reporte");
                new ReportMaker(raiz, sock, false);
            }
            else {
                ErrorXML error = new ErrorXML();
                String tmp = Language.getWord("ERR_FORMAT_PROTOCOL") + " "
                        + sock.socket();

                LogAdmin.setMessage(tmp, ServerConstants.ERROR);

                System.out.println("ERROR FORMATO PROTOCOLO");
                XMLOutputter xmlOutputter = new XMLOutputter();
                xmlOutputter.setFormat(Format.getPrettyFormat());
                try {
                    xmlOutputter.output(doc,System.out);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                SocketWriter.writing(sock, error.returnError(
                        ServerConstants.ERROR, "", tmp));
            }

        } 
        
        /*
         *  Validacion de una solicitud de un paquete conexi�
         */
        
        else if (nom_raiz.equals("CNX")) {
        	if (ConnectionsPool.chekDataBase(raiz.getChild("db").getValue())) {
	            UserDataStructure loguser = new UserDataStructure(raiz);
	            
	        	if (loguser.valid()) {
	                String bd = loguser.getBD();
	                String login = loguser.getLogin();
	                EmakuServerSocket.setLogin(sock, bd, login);
	                ACPSender docacp = new ACPSender(sock,bd,login);
	                docacp.start();
	            } else {
	                SocketWriter.writing(sock, new AcpFailure(Language.getWord("ACPFAILURE")));
	            }
	        	
            } else {
            	LogAdmin.setMessage(Language.getWord("DBNFEX") + raiz.getChild("db").getValue(), ServerConstants.ERROR);
            	SocketWriter.writing(sock, new AcpFailure(Language.getWord("ACPFAILURE")));
            }

        } 
        /*
         *  Recepcion de un paquete error
         */
        
        else if (nom_raiz.equals("ERROR")) {
            System.out.println("Paquete ERROR");
        }
        
        /*
         * Validaci�n de solicitud de paquetes no autorizados 
         */
        
        else {
            System.out.println("paquete no autorizado");
        }

    }
    
    /**
     * Este metodo se encarga de revisar toda las raices de los documentos que llegan 
     * al servidor de transacciones desde el cliente JMAdmin.
     * @param doc Documento a validar
     * @param sock Socket por que se esta comunicando
     */

    public static void ValidAdmin(Document doc, SocketChannel sock) {
        
        /*
         * Obtenemos la raiz del documento si el documento tiene raiz
         */
        Element raiz = doc.getRootElement();
        
        if (EmakuServerSocket.isLoged(sock)) {

            if (raiz.getName().equals("TRANSACCION")) {
                System.out.println("Paquete TRANSACCION");
        	} else if (raiz.getName().equals("ERROR")) {
                System.out.println("Paquete ERROR");
            } else {
                ErrorXML error = new ErrorXML();
                String tmp = Language.getWord("ERR_FORMAT_PROTOCOL") + " "
                        + sock.socket();

                LogAdmin.setMessage(tmp, ServerConstants.ERROR);

                SocketWriter.writing(sock, error.returnError(
                        ServerConstants.ERROR, "", tmp));
                //SocketServer.setDecrementSocketsCount();
            }

        } else if (raiz.getName().equals("CNX")) {
            System.out.println("Paquete CNX");
        } else {
            System.out.println("paquete no autorizado");
        }
        
    }
}

