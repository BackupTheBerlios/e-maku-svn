package jmlib.comunicaciones;

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
 * PackageToXML2.java Creado el 05-oct-2004
 * 
 * Este archivo es parte de JMServerII <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
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
public class PackageToXML2 {

    private static ByteArrayOutputStream bufferTmp;
    private static Document doc;
    private ByteBuffer buf;
    private SAXBuilder builder;
    private Vector <ArrivePackageListener>arrivePackageListener = new Vector<ArrivePackageListener>();
    
    public PackageToXML2() {
        //this.setPriority(Thread.MIN_PRIORITY);
        bufferTmp = new ByteArrayOutputStream();
    }


    public synchronized void work(SocketChannel socket) {

        buf = ByteBuffer.allocateDirect(8192);
        try {

            int numRead = 1;

            while (numRead > 0) {

                ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
                bufferOut.write(bufferTmp.toByteArray());
                bufferTmp = new ByteArrayOutputStream();

                Vector <ByteArrayOutputStream>vbuffer = new Vector<ByteArrayOutputStream>();
                vbuffer.add(bufferOut);

                buf.rewind();
                
                numRead = socket.read(buf);
                
                
                buf.rewind();

                int j = 0;

                for (int i = 0; i < numRead; i++) {
                    int character = buf.get(i);
                    if (character != 12) {
                        if (character!=0)
                            vbuffer.get(j).write(character);
                    }
                    else {
                        if (i != (numRead - 1)) {
                            // Nuevo Paquete
                            bufferOut = new ByteArrayOutputStream();
                            vbuffer.add(bufferOut);
                            j++;
                        }
                    }
                }

                for (int i = 0; i < vbuffer.size(); i++) {
 //                   String sbuff = "";
                    ByteArrayOutputStream docStream = new ByteArrayOutputStream();
                    try {
                        builder = new SAXBuilder();
                        docStream = (ByteArrayOutputStream) vbuffer.get(i);
                        ByteArrayInputStream bufferIn = new ByteArrayInputStream(
                                docStream.toByteArray());
 //                       sbuff = new String(docStream.toByteArray());
                        doc = builder.build(bufferIn);
	        	            ArrivePackageEvent event = new ArrivePackageEvent(this,doc);
	        	            notifyArrivePackage(event);

                    }
                    catch (JDOMException e1) {
                        // En caso de un paquete invalido
                        bufferTmp.write(docStream.toByteArray());
                    }
                }

                if (numRead == -1) {
                    bufferOut.close();
                    socket.close();
                    socket = null;
                    return;
                }

            }

        }
        catch (ClosedChannelException e) {
        	socket = null;
            //e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addArrivePackageistener(ArrivePackageListener listener ) {
        arrivePackageListener.addElement(listener);
    }

    public synchronized void removeSuccessListener(ArrivePackageListener listener ) {
        arrivePackageListener.removeElement(listener);
    }

    private synchronized void notifyArrivePackage(ArrivePackageEvent event) {
        Vector lista;
        lista = (Vector)arrivePackageListener.clone();
        for (int i=0; i<lista.size();i++) {
            ArrivePackageListener listener = (ArrivePackageListener)lista.elementAt(i);
            listener.validPackage(event);
        }
    }

}