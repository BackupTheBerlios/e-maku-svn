package com.kazak.smi.admin.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

public class SocketWriter {

    
    public static void writing(SocketChannel sock,Document doc) throws IOException {
        synchronized(sock) {
            ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
            
            XMLOutputter xmlOutputter = new XMLOutputter();
            //xmlOutputter.setFormat(Format.getPrettyFormat());
            xmlOutputter.output(doc, bufferOut);
            bufferOut.write(new String("\f").getBytes());
            
            ByteArrayInputStream bufferIn = new ByteArrayInputStream(bufferOut.toByteArray());
            bufferOut.close();
            sendBuffer(sock,bufferIn);
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
    private static void sendBuffer(SocketChannel sock,ByteArrayInputStream buffer) {
        
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
        }
        catch (ClosedChannelException CCE) {
        	CCE.printStackTrace();
        	return;
        }
        catch (IOException IOEe) {
        	IOEe.printStackTrace();
            /*LogAdmin.setMessage(Language.getWord("ERR_WRITING_SOCKET") + "\n"
                    + IOEe.getMessage(), CommonConst.ERROR);*/
        }
    }
}