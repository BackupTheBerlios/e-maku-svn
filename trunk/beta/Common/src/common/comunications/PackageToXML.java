package common.comunications;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
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
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class PackageToXML {

    private static ByteArrayOutputStream bufferOut;
    private static Document doc;
    private ByteBuffer buf;
    private SAXBuilder builder;
    private Vector <ArrivedPackageListener>arrivePackageListener = new Vector<ArrivedPackageListener>();
    
    public PackageToXML() {
        bufferOut = new ByteArrayOutputStream();
    }


    public synchronized void work(SocketChannel socket) {

        buf = ByteBuffer.allocateDirect(8192);
        try {

            int numRead = 1;

            while (numRead > 0) {

                buf.rewind();
                
                numRead = socket.read(buf);
                
                
                buf.rewind();

                for (int i = 0; i < numRead; i++) {
                    int character = buf.get(i);
                    if (character != 12) {
                        if (character!=0) {
                        	bufferOut.write(character);
                        }
                    }
                    else {
                        ByteArrayInputStream bufferIn = null;
                        builder = new SAXBuilder(false);
                        bufferIn = new ByteArrayInputStream(bufferOut.toByteArray());
                        try {
							doc = builder.build(bufferIn);
						} catch (JDOMException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							FileOutputStream fos = new FileOutputStream("/home/felipe/jdomexception.txt");
							fos.write(bufferOut.toByteArray());
							fos.close();
						}
        	            ArrivedPackageEvent event = new ArrivedPackageEvent(this,doc);
        	            notifyArrivePackage(event);
                    	bufferOut.close();
                    	bufferOut = null;
                    	bufferIn.close();
                    	bufferIn = null;
                        bufferOut = new ByteArrayOutputStream();
                    }

                }
                if (numRead == -1) {
                    socket.close();
                    socket = null;
                    return;
                }
            }

        }
        catch (ClosedChannelException e) {
        	socket = null;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addArrivePackageistener(ArrivedPackageListener listener ) {
        arrivePackageListener.addElement(listener);
    }

    public synchronized void removeSuccessListener(ArrivedPackageListener listener ) {
        arrivePackageListener.removeElement(listener);
    }

    private synchronized void notifyArrivePackage(ArrivedPackageEvent event) {
        for (int i=0; i<arrivePackageListener.size();i++) {
            ArrivedPackageListener listener = (ArrivedPackageListener)arrivePackageListener.elementAt(i);
            listener.validPackage(event);
        }
    }
}