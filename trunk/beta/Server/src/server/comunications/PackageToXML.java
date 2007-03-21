package server.comunications;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import server.control.HeadersValidator;
import server.misc.ServerConstants;
import server.misc.settings.ConfigFileHandler;

import common.misc.language.Language;
import common.misc.log.LogAdmin;

/**
 * PackageToXML.java Creado el 05-oct-2004
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
 * Informacion de la clase <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class PackageToXML extends Thread {

    private ByteBuffer buf;
    private SAXBuilder builder;
    
    private static Document doc;

    private SocketChannel channel;

    public PackageToXML(SocketChannel channel) {
        this.channel = channel;
        this.setPriority(Thread.MIN_PRIORITY);
        
    }

    public void run() {

        buf = ByteBuffer.allocateDirect(8192);
        
        try {

            
            int numRead = 1;
 
            while (numRead > 0) {

                buf.rewind();
                numRead = channel.read(buf);
                buf.rewind();

                for (int i = 0; i < numRead; i++) {
                    int character = buf.get(i);
                    if (character != 12) {
                        if (character!=0) {
                        	EmakuServerSocket.getBufferTmp(channel).write(buf.get(i));
                        }
                    }
                    else {

                        try {
                            builder = new SAXBuilder();
                            ByteArrayInputStream bufferIn = new ByteArrayInputStream(EmakuServerSocket.getBufferTmp(channel).toByteArray());
                            doc = builder.build(bufferIn);
                            if (channel.socket().getLocalPort() == ConfigFileHandler.getAdminSocket()) {
                                HeadersValidator.ValidAdmin(doc, channel);
                            }
                            else {
                                HeadersValidator.ValidClient(doc, channel);
                            }
                        }
                        catch (JDOMException e1) {
                            EmakuServerSocket.getBufferTmp(channel).write(EmakuServerSocket.getBufferTmp(channel).toByteArray());
                            String tmp = Language.getWord("ERR_FORMAT_PROTOCOL")
                            + " " + channel.socket();
                            LogAdmin.setMessage(tmp+"\n"+EmakuServerSocket.getBufferTmp(channel).toString(), ServerConstants.ERROR);
                        }
                        finally {
                        	EmakuServerSocket.getBufferTmp(channel).close();
                        	EmakuServerSocket.setBufferTmp(channel,new ByteArrayOutputStream());

                        }
                    }
                    
                }

                if (numRead == -1) {
                	EmakuServerSocket.getBufferTmp(channel).close();
                    channel.close();
                    return;
                }
            }

        }
        catch (ClosedChannelException e) {
            //
        }
        catch (IOException e) {
            //
        }
    }
}