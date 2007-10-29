package com.kazak.comeet.client.network;

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
            ByteArrayOutputStream bufferOutputStream = new ByteArrayOutputStream();
            
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.output(doc, bufferOutputStream);
            bufferOutputStream.write(new String("\f").getBytes());
            
            ByteArrayInputStream bufferInputStream = new ByteArrayInputStream(bufferOutputStream.toByteArray());
            bufferOutputStream.close();
            sendBuffer(sock,bufferInputStream);
        }
    }
    
    public static void write(SocketChannel sock, String data) {
        ByteArrayInputStream bufferInputStream = new ByteArrayInputStream(data.getBytes());
        sendBuffer(sock,bufferInputStream);
    }

    public static void write(SocketChannel sock, ByteArrayOutputStream data) {
        ByteArrayInputStream bufferInputStream = new ByteArrayInputStream(data.toByteArray());
        sendBuffer(sock,bufferInputStream);
    }
    
    private static void sendBuffer(SocketChannel sock,ByteArrayInputStream buffer) {
        
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(8192);
            byte[] bytes = new byte[8192];
            int count = 0;

            while (count >= 0) {
                byteBuffer.clear();
                count = buffer.read(bytes);
                for (int i=0;i<count;i++) {
                    byteBuffer.put(bytes[i]);
                }
                byteBuffer.flip();
                while (byteBuffer.remaining()>0)
                    sock.write(byteBuffer);
            }
        }
        catch (ClosedChannelException CCE) {
        	CCE.printStackTrace();
        	return;
        }
        catch (IOException IOEe) {
        	IOEe.printStackTrace();
        }
    }
}