package com.kazak.smi.server.control;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.smi.server.comunications.SocketWriter;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.settings.ConfigFileHandler;

/**
 * ACPSender.java Creado el 10-ago-2004
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
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class ACPSender extends Thread{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -757058034217759942L;
	private Document acpBeginDoc;
	private SocketChannel sock;
	private int userLevel;
	
	public ACPSender(SocketChannel sock, String login,int userLevel) {
        this.sock = sock;
        this.userLevel = userLevel;
		start();
    }
    
	public void run() {
		Element root = new Element("ACPBegin");
        acpBeginDoc = new Document();
        acpBeginDoc.setRootElement(root);
        root.addContent(new Element("AppOwner").setText(ConfigFileHandler.getAppOwner()));
        root.addContent(new Element("UserLevel").setText(String.valueOf(userLevel)));
        try {
			SocketWriter.write(sock,this.acpBeginDoc);
		} catch (IOException e) {
			LogWriter.write("ERROR: Falla de entrada/salida");
			LogWriter.write("Causa: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
