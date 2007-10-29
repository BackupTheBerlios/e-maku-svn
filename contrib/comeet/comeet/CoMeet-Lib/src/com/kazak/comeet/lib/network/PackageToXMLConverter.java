package com.kazak.comeet.lib.network;

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
 * Esta clase covierte el flujo de entrada de un socket en paquetes XML <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda </A>
 * 
 */
public class PackageToXMLConverter {

    private static ByteArrayOutputStream bufferOutputStream;
    private static Document doc;
    private ByteBuffer buffer;
    private SAXBuilder saxBuilder;
    private Vector <PackageComingListener>arrivedPackageListener = new Vector<PackageComingListener>();
    
    public PackageToXMLConverter() {
        bufferOutputStream = new ByteArrayOutputStream();
    }

    public synchronized boolean work(SocketChannel socket) {

        buffer = ByteBuffer.allocateDirect(8192);
        try {

            int bytesNum = 1;
            while (bytesNum > 0) {

                buffer.rewind();                
                bytesNum = socket.read(buffer);
                buffer.rewind();

                for (int i = 0; i < bytesNum; i++) {
                    int character = buffer.get(i);
                    if (character != 12) {
                        if (character!=0) {
                        	bufferOutputStream.write(character);
                        }
                    }
                    else {
                        ByteArrayInputStream bufferInputStream = null;
                        saxBuilder = new SAXBuilder(false);
                        bufferInputStream = new ByteArrayInputStream(bufferOutputStream.toByteArray());
                        try {
							doc = saxBuilder.build(bufferInputStream);
						} catch (JDOMException e) {
							e.printStackTrace();
						}
        	            ArrivedPackageEvent event = new ArrivedPackageEvent(this,doc);
        	            notifyArrivedPackage(event);
                    	bufferOutputStream.close();
                        bufferOutputStream = new ByteArrayOutputStream();
                    }

                }
                if (bytesNum == -1) {
                    socket.close();
                    socket = null;
                    return false;
                }
            }

        }
        catch (ClosedChannelException e) {
        	socket = null;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public synchronized void addPackageComingListener(PackageComingListener listener) {
        arrivedPackageListener.addElement(listener);
    }

    public synchronized void removeSuccessListener(PackageComingListener listener) {
        arrivedPackageListener.removeElement(listener);
    }

    private synchronized void notifyArrivedPackage(ArrivedPackageEvent packageEvent) {
        for (int i=0; i<arrivedPackageListener.size();i++) {
            PackageComingListener listener = (PackageComingListener)arrivedPackageListener.elementAt(i);
            listener.validPackage(packageEvent);
        }
    }
}