package common.comunications;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import common.misc.CommonConst;
import common.misc.language.Language;
import common.misc.log.LogAdmin;

/**
 * SocketWriter.java Creado el 23-jul-2004
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
public class SocketWriter {

    
    public static boolean writing(SocketChannel sock,Document doc) {
        synchronized(sock) {
	        try {
	            ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
	            
	            XMLOutputter xmlOutputter = new XMLOutputter();
	            //xmlOutputter.setFormat(Format.getPrettyFormat());
	            xmlOutputter.output(doc, bufferOut);
	            bufferOut.write(new String("\f").getBytes());
	            
	            ByteArrayInputStream bufferIn = new ByteArrayInputStream(bufferOut.toByteArray());
	            bufferOut.close();
	            return sendBuffer(sock,bufferIn);
	        }
	        catch (FileNotFoundException e) {
	            e.printStackTrace();
	            return false;
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	            return false;
	        }
        }
    }
    
    public static void writing(SocketChannel sock, String data) {
        ByteArrayInputStream bufferIn = new ByteArrayInputStream(data.getBytes());
        sendBuffer(sock,bufferIn);
    }

    public static void writing(SocketChannel sock, ByteArrayOutputStream data) {
        ByteArrayInputStream bufferIn = new ByteArrayInputStream(data.toByteArray());
        sendBuffer(sock,bufferIn);
    }
    private static boolean sendBuffer(SocketChannel sock,ByteArrayInputStream buffer) {
        
        try {
            ByteBuffer buf = ByteBuffer.allocate(8192);
            byte[] bytes = new byte[8192];

            int count = 0;

            while (count >= 0) {

                buf.clear();
                
                count = buffer.read(bytes);
                
                for (int i=0;i<count;i++) {
                    buf.put(bytes[i]);
                }
  
                buf.flip();
                
                while (buf.remaining()>0)
                    sock.write(buf);
                
            }
            return true;
        }
        catch (IOException IOEe) {
            LogAdmin.setMessage(Language.getWord("ERR_WRITING_SOCKET") + "\n"
                    + IOEe.getMessage(), CommonConst.ERROR);
            return false;
        }
    }
}