 package com.kazak.smi.server.misc;

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

import com.kazak.smi.server.comunications.SocketWriter;


/**
 * LogAdmin.java Creado el 29-jun-2004
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
	private static RandomAccessFile RAFlog;
	private static String fileName = System.getenv("SMI_HOME")+"/log/smi_server.log";
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
			RAFlog.seek(RAFlog.length());
			long max = logFile.length();
			Date now = new Date();
			SimpleDateFormat format = null;
			if (max >= MAX_SIZE_FILE_LOG) {
				byte[] Blog = new byte[(int) max];
				format = new SimpleDateFormat("yyyy-MM-dd");
				File dateFile = new File(fileName + "-" + format.format(now) + ".gz");
				FileOutputStream FOSfile = new FileOutputStream(dateFile);
				GZIPOutputStream gzipfile = new GZIPOutputStream(FOSfile);
				RAFlog.seek(0);
				RAFlog.read(Blog);
				gzipfile.write(Blog);
				gzipfile.close();
				FOSfile.close();
				logFile.delete();
				newFile();
			}
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			RAFlog.writeBytes(format.format(now) + " " + message + "\n");
		} catch (IOException IOEe) {
			IOEe.getMessage();
		}

	}

	private static void newFile() {
		try {
			logFile = new File(fileName);
			RAFlog = new RandomAccessFile(logFile, "rw");
		} catch (FileNotFoundException FNFEe) {
			FNFEe.printStackTrace();
		}
	}
	
	public static void getFullLog(SocketChannel sock) throws IOException {
		
		try {
			Element lci = new Element("LogContentInit");
	    	SocketWriter.writing(sock, new Document(lci));
	    	
			FileInputStream in = new FileInputStream(fileName);
		    int len;
		    byte [] buf = new byte[255]; 
	        while ((len = in.read(buf)) > 0) {
	        	Element root = new Element("LogContent");
	    		Element text = new Element("text");
	    		root.addContent(text);
	    		Document document = new Document(root);
	        	text.setText(new String(buf,0,len));
	        	SocketWriter.writing(sock, document);
	        }
	        in.close();	        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}