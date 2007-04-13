package com.kazak.smi.server.control;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.kazak.smi.lib.misc.Language;
import com.kazak.smi.server.businessrules.RunTransaction;
import com.kazak.smi.server.businessrules.Sync;
import com.kazak.smi.server.comunications.AcpFailure;
import com.kazak.smi.server.comunications.ResultSetToXML;
import com.kazak.smi.server.comunications.SocketServer;
import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.misc.LogWriter;
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
        final Element raiz = doc.getRootElement();
        String nom_raiz = raiz.getName();
        /*XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        try {
            xmlOutputter.output(doc,System.out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/
        /*
         *  Validaci�n de solicitud de paquetes, se verifica si el socket ya fue
         *  autenticado, si lo fue entonces se procede a validar la solicitud 
         *  del paquete requerido, si no se procede a validar un paquete CNX o la
         *  solicitud de paquetes no autorizados (solicitud de paquetes sin previa
         *  autentificación)
         */
        
        if (SocketServer.isLoged(sock)) {

        	if (nom_raiz.equals("Message")) {
                LogWriter.write("Nuevo mesaje desde " + sock);
                new MessageDistributor(raiz,false);
            }
        	else if (nom_raiz.equals("Transaction")) {
                new RunTransaction(sock,doc);
            }
        	else if (nom_raiz.equals("Synchronization")) {
        		Thread t = new Thread() {
                	public void run() {
                		new Sync(sock);
                	}
                };
                t.start();
            }
        	else if (nom_raiz.equals("RequestLogContent")) {
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
            else if (nom_raiz.equals("QUERY")) {
            	Thread t = new Thread() {
                	public void run() {
                        ValidQuery valida = new ValidQuery(raiz);
                        String codigo = "";
                        codigo = raiz.getChild("sql").getValue();
                        ResultSetToXML answer;
                        if (valida.changeStructParam()) {
                            answer = new ResultSetToXML(codigo, valida.getArgs());
                        } else {
                            answer = new ResultSetToXML(codigo);
                        }
                        answer.transmition(sock,valida.getId());   
                	}
                };
                t.start();         
            } 
            else {
                /*ErrorXML error = new ErrorXML();
                String tmp = Language.getWord("ERR_FORMAT_PROTOCOL") + " "
                        + sock.socket();

                LogAdmin.setMessage(tmp, ServerConst.ERROR);

                LogWriter.write("ERROR FORMATO PROTOCOLO");
                XMLOutputter xmlOutputter = new XMLOutputter();
                xmlOutputter.setFormat(Format.getPrettyFormat());
                try {
                    xmlOutputter.output(doc,LogWriter.write);
                    SocketWriter.writing(sock, error.returnError(
                            ServerConst.ERROR, "", tmp));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

        } 
        
        /* Validacion de una solicitud de un paquete conexión */
        else if (nom_raiz.equals("CNX")) {
            LoginUser loguser = new LoginUser(raiz);
        	if (loguser.valid()) {
        		String login = loguser.getLogin();
        		SocketServer.setLogin(sock, ConfigFile.getMainDataBase(), login);
        		SocketServer.getInfoSocket(sock).setEmail(loguser.getCorreo());
        		SocketServer.getInfoSocket(sock).setUid(loguser.getUid());
        		SocketServer.getInfoSocket(sock).setGid(loguser.getGid());
        		SocketServer.getInfoSocket(sock).setAdmin(loguser.getAdmin());
        		SocketServer.getInfoSocket(sock).setAudit(loguser.getAudit());
        		SocketServer.getInfoSocket(sock).setCurrIp(loguser.getIp());
        		SocketServer.getInfoSocket(sock).setPsName(loguser.getPsName());
        		SocketServer.getInfoSocket(sock).setGName(loguser.getGName());
        		SocketServer.getInfoSocket(sock).setNames(loguser.getNombres());
                new ACPSender(sock,login,loguser.getUserLevel());
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