package com.kazak.comeet.server.comunications;

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

import com.kazak.comeet.lib.misc.Language;
import com.kazak.comeet.server.control.HeadersValidator;
import com.kazak.comeet.server.misc.LogWriter;
import com.kazak.comeet.server.misc.ServerConstants;

/**
 * PackageToXMLConverter.java Creado el 05-oct-2004
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
 * Esta clase se encarga de convertir un paquete de red en un documento XML 
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class PackageToXMLConverter extends Thread {

    private ByteBuffer buffer;
    private SAXBuilder saxBuilder;
    
    private static Document doc;
    private SocketChannel channel;

    public PackageToXMLConverter(SocketChannel channel) {
        this.channel = channel;
        this.setPriority(Thread.MIN_PRIORITY);
    }

    public void run() {

        buffer = ByteBuffer.allocateDirect(8192);
        
        try {

            int bytesNumber = 1;
            
            while (bytesNumber > 0) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write((SocketServer.getTemporalBuffer(channel)).toByteArray());
                SocketServer.setTemporalBuffer(channel,new ByteArrayOutputStream());

                Vector <ByteArrayOutputStream>bufferVector = new Vector<ByteArrayOutputStream>();
                bufferVector.addElement(outputStream);

                buffer.rewind();
                bytesNumber = channel.read(buffer);
                buffer.rewind();

                int j=0;
                for (int i = 0; i < bytesNumber; i++) {
                    int character = buffer.get(i);
                    if (character != 12) {
                        if (character!=0) {
	                        bufferVector.get(j).write(buffer.get(i));
                        }
                    }
                    else {
                        if (i != (bytesNumber - 1)) {
	                        outputStream = new ByteArrayOutputStream();
	                        bufferVector.add(outputStream);
	                        j++;
                        }
                    }
                    
                }

                for (int i = 0; i < bufferVector.size(); i++) {
                    ByteArrayOutputStream docStream = new ByteArrayOutputStream();
                    try {
                        saxBuilder = new SAXBuilder();
                        docStream = (ByteArrayOutputStream) bufferVector.get(i);
                        if (docStream.size() > 2) {
                            ByteArrayInputStream bufferIn = new ByteArrayInputStream(
                                    docStream.toByteArray());
                            doc = saxBuilder.build(bufferIn);
                            HeadersValidator.validClient(doc, channel);
                        }
                    }
                    catch (JDOMException e1) {
                        SocketServer.getTemporalBuffer(channel).write(docStream.toByteArray());
                        XMLError error = new XMLError();
                        String message = Language.getWord("ERR_FORMAT_PROTOCOL")
                                + " " + channel.socket();
                        LogWriter.write(message);
                        SocketWriter.write(channel, error.returnErrorMessage(ServerConstants.ERROR, "", message));
                    }
                }

                if (bytesNumber == -1) {
                    outputStream.close();
                    channel.close();
                    SocketServer.removeSock(channel);
                    return;
                }
            }

        }
        catch (ClosedChannelException e) {
        	e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
    }
}