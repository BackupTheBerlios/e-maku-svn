package com.kazak.smi.server.comunications;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

public class SocketWriter {

	public static void write(SocketChannel sock,Document doc) throws IOException {
		if (sock==null) { 
			return;
		}
		synchronized(sock) {
			ByteArrayOutputStream bufferOutStream = new ByteArrayOutputStream();

			XMLOutputter xmlOutputter = new XMLOutputter();
			xmlOutputter.output(doc, bufferOutStream);
			bufferOutStream.write(new String("\f").getBytes());

			ByteArrayInputStream bufferInputStream = new ByteArrayInputStream(bufferOutStream.toByteArray());
			bufferOutStream.close();
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

	private static void sendBuffer(SocketChannel sock,ByteArrayInputStream bufferInputStream) {

		try {
			ByteBuffer buffer = ByteBuffer.allocate(8192);
			byte[] bytes = new byte[8192];
			int count = 0;

			while (count >= 0) {
				buffer.clear();
				count = bufferInputStream.read(bytes);
				for (int i=0;i<count;i++) {
					buffer.put(bytes[i]);
				}
				buffer.flip();
				while (buffer.remaining()>0)
					sock.write(buffer);
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