 package com.kazak.comeet.server.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import org.jdom.Document;
import org.jdom.Element;

import com.kazak.comeet.server.comunications.SocketWriter;

/**
 * LogWriter.java Creado el 29-jun-2004
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
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
 * Esta clase se encarga de realizar la administracion de logs del Servidor
 * de Transacciones. 
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

public class LogWriter {

	private static File logFile;
	private static RandomAccessFile randomFile;
	private static String fileName = System.getenv("COMEET_HOME")+"/log/comeet_server.log";
	private static final long MAX_SIZE_FILE_LOG = 1048576;

	public LogWriter() {
		newFile();
	}

	public static synchronized void write(String message)  {
		System.out.println(message);
		writeToFile(message);
    }

	private synchronized static void writeToFile(String message) {
		try {
			randomFile.seek(randomFile.length());
			long max = logFile.length();
			Date now = new Date();
			SimpleDateFormat format = null;
			if (max >= MAX_SIZE_FILE_LOG) {
				byte[] byteArray = new byte[(int) max];
				format = new SimpleDateFormat("yyyy-MM-dd");
				File file = new File(fileName + "-" + format.format(now) + ".gz");
				FileOutputStream fileOuputStream = new FileOutputStream(file);
				GZIPOutputStream gzipFile = new GZIPOutputStream(fileOuputStream);
				randomFile.seek(0);
				randomFile.read(byteArray);
				gzipFile.write(byteArray);
				gzipFile.close();
				fileOuputStream.close();
				logFile.delete();
				newFile();
			}
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			randomFile.writeBytes(format.format(now) + " " + message + "\n");
		} catch (IOException IOEe) {
			IOEe.getMessage();
		}

	}

	private static void newFile() {
		try {
			logFile = new File(fileName);
			randomFile = new RandomAccessFile(logFile, "rw");
		} catch (FileNotFoundException FNFEe) {
			System.out.println("ERROR: The system could not to create the log file.");
			FNFEe.printStackTrace();
		}
	}
	
	public static void getFullLogFile(SocketChannel sock) throws IOException {
		
		try {
			Element element = new Element("LogContentInit");
	    	SocketWriter.write(sock, new Document(element));
	    	
			FileInputStream in = new FileInputStream(fileName);
		    int size;
		    byte [] buf = new byte[255]; 
	        while ((size = in.read(buf)) > 0) {
	        	Element root = new Element("LogContent");
	    		Element text = new Element("text");
	    		root.addContent(text);
	    		Document document = new Document(root);
	        	text.setText(new String(buf,0,size));
	        	SocketWriter.write(sock, document);
	        }
	        in.close();	        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
