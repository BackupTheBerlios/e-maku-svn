package com.kazak.comeet.server.control;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.kazak.comeet.lib.misc.Language;
import com.kazak.comeet.server.businessrules.TransactionRunner;
import com.kazak.comeet.server.businessrules.SyncManager;
import com.kazak.comeet.server.comunications.AcpFailure;
import com.kazak.comeet.server.comunications.ResultSetToXMLConverter;
import com.kazak.comeet.server.comunications.SocketServer;
import com.kazak.comeet.server.comunications.SocketWriter;
import com.kazak.comeet.server.misc.LogWriter;
import com.kazak.comeet.server.misc.settings.ConfigFileHandler;

/**
 * HeaderValidator.java Creado el 22-jul-2004
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

    public static void validClient(Document doc, final SocketChannel sock) {
        
        /*
         * Obtenemos la raiz del documento si el documento tiene raiz
         */
        final Element root = doc.getRootElement();
        String rootName = root.getName();
        
        /* Codigo temporal para debuging 
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        try {
            xmlOutputter.output(doc,System.out);
        }
        catch (IOException e) {
            e.printStackTrace();
        } */
        
        /*
         *  Validación de solicitud de paquetes, se verifica si el socket ya fue
         *  autenticado, si lo fue entonces se procede a validar la solicitud 
         *  del paquete requerido, si no se procede a validar un paquete CNX o la
         *  solicitud de paquetes no autorizados (solicitud de paquetes sin previa
         *  autentificación)
         */
        
        if (SocketServer.isLogged(sock)) {
        	if (rootName.equals("Message")) { 
                LogWriter.write("INFO: Nuevo mensaje enviado por Colocador desde " + sock.socket().getInetAddress().getHostAddress());
                new MessageDistributor(root,false);
            }
        	else if (rootName.equals("Transaction")) {
                new TransactionRunner(sock,doc);
            }
        	else if (rootName.equals("Synchronization")) {
        		Thread t = new Thread() {
                	public void run() {
                		try {
							new SyncManager(sock);
						} catch (SQLException e) {
			                LogWriter.write("ERROR: Ocurrio una falla durante el proceso de sincronizacion");
							e.printStackTrace();
						}
                	}
                };
                t.start();
            }
        	else if (rootName.equals("RequestLogContent")) {
        		LogWriter.write("INFO: Solicitud de envio del registro del servidor.");
        		
				Thread t = new Thread() {
				
					public void run() {
						try {
							LogWriter.getFullLogFile(sock);
						} catch (IOException e) {
							LogWriter.write("ERROR: Falla de entrada/salida");
							LogWriter.write("Causa: " + e.getMessage());
							e.printStackTrace();
						}
					}
				
				};
				t.start();
				
				LogWriter.write("INFO: Solicitud de envio del registro del servidor enviada.");
            }
           /* Validacion de una solicitud de consulta */
            else if (rootName.equals("QUERY")) {
            	Thread t = new Thread() {
                	public void run() {
                        ValidQuery validQuery = new ValidQuery(root);
                        String code = "";
                        code = root.getChild("sql").getValue();
                        ResultSetToXMLConverter answer;
                        if (validQuery.changeStructParam()) {
                            answer = new ResultSetToXMLConverter(code, validQuery.getArgs());
                        } else {
                            answer = new ResultSetToXMLConverter(code);
                        }
                        answer.trancomeett(sock,validQuery.getId());   
                	}
                };
                t.start();         
            } 
            else if (rootName.equals("ONLINELIST")) {
            	String id = root.getChildText("id");
            	Document list = new Document();
            	if ("LIST".equals(id)) {
            		list = SocketServer.getUsersOnLine(root.getText(),"4");
            	}
            	else if("TOTAL".equals(id)) {
            		list = SocketServer.getUsersTotal();
            	}
            	try {
            		SocketWriter.write(sock,list);
            	} catch (IOException e) {
					LogWriter.write("ERROR: Falla de entrada/salida");
					LogWriter.write("Causa: " + e.getMessage());
					e.printStackTrace();
				}
            }
            else if (rootName.equals("GETMAILINFO")) {
            	Document mailInfo = SocketServer.getMailInfo(sock);
            	try {
            		SocketWriter.write(sock,mailInfo);
            	} catch (IOException e) {
					LogWriter.write("ERROR: Falla de entrada/salida");
					LogWriter.write("Causa: " + e.getMessage());
					e.printStackTrace();
				}
            }
            else if(rootName.equals("SEARCH")) {
            	    //String id = root.getChildText("id");
                	String pattern = root.getChildText("pattern");
                	String area = root.getChildText("area");
                	Document list = new Document();
               		list = SocketServer.getUsersOnLine(pattern,area);
                	try {
                		SocketWriter.write(sock,list);
                	} catch (IOException e) {
    					LogWriter.write("ERROR: Falla de entrada/salida");
    					LogWriter.write("Causa: " + e.getMessage());
    					e.printStackTrace();
    				}            		
            }
            else if (rootName.equals("USERSTOTAL")) {
            	Document list = SocketServer.getUsersTotal();
            	try {
            		SocketWriter.write(sock,list);
            	} catch (IOException e) {
            		LogWriter.write("ERROR: Falla de entrada y salida");
            		LogWriter.write("Causa: " + e.getMessage());
            		e.printStackTrace();
            	}
            }
            else {
                  LogWriter.write(Language.getWord("ERR_FORMAT_PROTOCOL") + ": " + sock.socket());
            }

        }
        /* Validacion de una solicitud de un paquete conexión */
        else if (rootName.equals("CNX")) {
            UserLogin user = new UserLogin(root);
        	if (user.isValid()) {
        		String login = user.getLogin();
        		SocketServer.setLogin(sock, ConfigFileHandler.getMainDataBase(), login);
        		SocketServer.getSocketInfo(sock).setEmail(user.getEmail());
        		SocketServer.getSocketInfo(sock).setUid(user.getUid());
        		SocketServer.getSocketInfo(sock).setGroupID(user.getGid());
        		SocketServer.getSocketInfo(sock).setAdmin(user.getAdmin());
        		SocketServer.getSocketInfo(sock).setAudit(user.getAudit());
        		SocketServer.getSocketInfo(sock).setCurrentIP(user.getIp());
        		SocketServer.getSocketInfo(sock).setWsName(user.getWsName());
        		SocketServer.getSocketInfo(sock).setGroupName(user.getGroupName());
        		SocketServer.getSocketInfo(sock).setNames(user.getNames());
        		SocketServer.getSocketInfo(sock).setConnectionTime();
                new ACPSender(sock,login,user.getUserLevel());
            } else {
                try {
					SocketWriter.write(sock, new AcpFailure(Language.getWord("ACPFAILURE")));
				} catch (IOException e) {
					LogWriter.write("ERROR: Falla de entrada/salida");
					LogWriter.write("Causa: " + e.getMessage());
					e.printStackTrace();
				}
            }
        } 
        /* Validación de solicitud de paquetes no autorizados */
        else {
            LogWriter.write("ERROR: Paquete no autorizado. Contenido:");
            XMLOutputter xmlOutputter2 = new XMLOutputter();
            xmlOutputter2.setFormat(Format.getPrettyFormat());
            try {
                xmlOutputter2.output(doc,System.out);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}