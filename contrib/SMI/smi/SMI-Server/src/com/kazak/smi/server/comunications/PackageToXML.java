package com.kazak.smi.server.comunications;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.kazak.smi.lib.misc.Language;
import com.kazak.smi.server.control.HeaderValidator;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.ServerConst;

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
 
                ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
                bufferOut.write((SocketServer.getBufferTmp(channel)).toByteArray());
                SocketServer.setBufferTmp(channel,new ByteArrayOutputStream());

                Vector <ByteArrayOutputStream>vbuffer = new Vector<ByteArrayOutputStream>();
                vbuffer.addElement(bufferOut);

                buf.rewind();
                numRead = channel.read(buf);
                buf.rewind();

                int j=0;
                for (int i = 0; i < numRead; i++) {
                    int character = buf.get(i);
                    if (character != 12) {
                        if (character!=0) {
	                        vbuffer.get(j).write(buf.get(i));
                        }
                    }
                    else {
                        if (i != (numRead - 1)) {
	                        bufferOut = new ByteArrayOutputStream();
	                        vbuffer.add(bufferOut);
	                        j++;
                        }
                    }
                    
                }

                for (int i = 0; i < vbuffer.size(); i++) {

                    ByteArrayOutputStream docStream = new ByteArrayOutputStream();

                    try {
                        builder = new SAXBuilder();
                        docStream = (ByteArrayOutputStream) vbuffer.get(i);
                        if (docStream.size() > 2) {
                            ByteArrayInputStream bufferIn = new ByteArrayInputStream(
                                    docStream.toByteArray());
                            doc = builder.build(bufferIn);
                            HeaderValidator.ValidClient(doc, channel);
                        }
                    }
                    catch (JDOMException e1) {
                        SocketServer.getBufferTmp(channel).write(docStream.toByteArray());
                        ErrorXML error = new ErrorXML();
                        String tmp = Language.getWord("ERR_FORMAT_PROTOCOL")
                                + " " + channel.socket();
                        LogWriter.write(tmp);
                        SocketWriter.writing(channel, error.returnError(ServerConst.ERROR, "", tmp));
                    }
                }

                if (numRead == -1) {
                    bufferOut.close();
                    channel.close();
                    SocketServer.removeSock(channel);
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