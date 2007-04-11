 package com.kazak.smi.lib.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;


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

public class Log {

	private static File logFile;
	private static RandomAccessFile RAFlog;
	private static String fileName = System.getenv("SMI_HOME")+"log/smi.log";
	private static final long MAX_SIZE_FILE_LOG = 5242880;
	public Log() {
		newFile();
	}

	public static void write(String message)  {
		System.out.println(message);
		writeToFile(message);
    }


	private synchronized static void writeToFile(String message) {
		try {
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
			RAFlog.seek(RAFlog.length());
		} catch (FileNotFoundException FNFEe) {
			FNFEe.printStackTrace();
		} catch (IOException IOEe) {
			IOEe.printStackTrace();
		}
	}
}