package com.kazak.smi.server.control;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.kazak.smi.lib.misc.Language;
import com.kazak.smi.server.businessrules.TransactionRunner;
import com.kazak.smi.server.businessrules.Sync;
import com.kazak.smi.server.comunications.AcpFailure;
import com.kazak.smi.server.comunications.ResultSetToXML;
import com.kazak.smi.server.comunications.SocketServer;
import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.ServerConst;
import com.kazak.smi.server.misc.settings.ConfigFile;

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

public class HeaderValidator {
    
    /**
     * Este metodo se encarga de revisar toda las raices de los documentos que llegan 
     * al servidor de transacciones desde el cliente JMClient.
     * @param doc Documento a validar
     * @param sock Socket por que se esta comunicando
     */

    public static void ValidClient(Document doc, final SocketChannel sock) {
        
        /*
         * Obtenemos la raiz del documento si el documento tiene raiz
         */
        final Element root = doc.getRootElement();
        String rootName = root.getName();
/*        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        try {
            xmlOutputter.output(doc,System.out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
*/        /*
         *  Validaci�n de solicitud de paquetes, se verifica si el socket ya fue
         *  autenticado, si lo fue entonces se procede a validar la solicitud 
         *  del paquete requerido, si no se procede a validar un paquete CNX o la
         *  solicitud de paquetes no autorizados (solicitud de paquetes sin previa
         *  autentificación)
         */
        
        if (SocketServer.isLogged(sock)) {

        	if (rootName.equals("Message")) {
                LogWriter.write("Nuevo mesaje desde " + sock);
                new MessageDistributor(root,false);
            }
        	else if (rootName.equals("Transaction")) {
                new TransactionRunner(sock,doc);
            }
        	else if (rootName.equals("Synchronization")) {
        		Thread t = new Thread() {
                	public void run() {
                		new Sync(sock);
                	}
                };
                t.start();
            }
        	else if (rootName.equals("RequestLogContent")) {
        		LogWriter.write("Solicitud de envio del registro del servidor");
        		
				Thread t = new Thread() {
				
					public void run() {
						try {
							LogWriter.getFullLog(sock);
						} catch (IOException e) {
							LogWriter.write("Error de entrada y salida");
							LogWriter.write("mensaje: " + e.getMessage());
							e.printStackTrace();
						}
					}
				
				};
				t.start();
				
				LogWriter.write("Solicitud de envio del registro del servidor enviada");
            }
           /* Validacion de una solicitud de consulta */
            else if (rootName.equals("QUERY")) {
            	Thread t = new Thread() {
                	public void run() {
                        ValidQuery valid = new ValidQuery(root);
                        String code = "";
                        code = root.getChild("sql").getValue();
                        ResultSetToXML answer;
                        if (valid.changeStructParam()) {
                            answer = new ResultSetToXML(code, valid.getArgs());
                        } else {
                            answer = new ResultSetToXML(code);
                        }
                        answer.transmition(sock,valid.getId());   
                	}
                };
                t.start();         
            } 
            else if (rootName.equals("ONLINELIST")) {
            	String id = root.getChildText("id");
            	Document list  = new Document();
            	if ("LIST".equals(id)) {
            		System.out.println("lista");
            		list = SocketServer.getUsersOnLine(root.getText());
            	}
            	else if("TOTAL".equals(id)) {
            		System.out.println("total");
            		list = SocketServer.getUsersTotal();
            	}
            	try {
            	SocketWriter.writing(sock,list);
            	} catch (IOException e) {
					LogWriter.write("Error de entrada y salida");
					LogWriter.write("mensaje: " + e.getMessage());
					e.printStackTrace();
				}
            }
            else if (rootName.equals("USERSTOTAL")) {
            	Document list = SocketServer.getUsersTotal();
            	try {
            	SocketWriter.writing(sock,list);
            	} catch (IOException e) {
					LogWriter.write("Error de entrada y salida");
					LogWriter.write("mensaje: " + e.getMessage());
					e.printStackTrace();
				}
            }
            else {
                  LogWriter.write(Language.getWord("ERR_FORMAT_PROTOCOL") + ": " + sock.socket());
            }

        }
        /* Validacion de una solicitud de un paquete conexión */
        else if (rootName.equals("CNX")) {
            LoginUser user = new LoginUser(root);
        	if (user.valid()) {
        		String login = user.getLogin();
        		SocketServer.setLogin(sock, ConfigFile.getMainDataBase(), login);
        		SocketServer.getSocketInfo(sock).setEmail(user.getCorreo());
        		SocketServer.getSocketInfo(sock).setUid(user.getUid());
        		SocketServer.getSocketInfo(sock).setGid(user.getGid());
        		SocketServer.getSocketInfo(sock).setAdmin(user.getAdmin());
        		SocketServer.getSocketInfo(sock).setAudit(user.getAudit());
        		SocketServer.getSocketInfo(sock).setCurrIp(user.getIp());
        		SocketServer.getSocketInfo(sock).setPsName(user.getPsName());
        		SocketServer.getSocketInfo(sock).setGName(user.getGName());
        		SocketServer.getSocketInfo(sock).setNames(user.getNombres());
                new ACPSender(sock,login,user.getUserLevel());
            } else {
                try {
					SocketWriter.writing(sock, new AcpFailure(Language.getWord("ACPFAILURE")));
				} catch (IOException e) {
					LogWriter.write("Error de entrada y salida");
					LogWriter.write("mensaje: " + e.getMessage());
					e.printStackTrace();
				}
            }
        } 
        /* Validación de solicitud de paquetes no autorizados */
        else {
            LogWriter.write("paquete no autorizado");
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            try {
                xmlOutputter.output(doc,System.out);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}