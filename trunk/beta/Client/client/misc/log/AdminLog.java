package client.misc.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.zip.GZIPOutputStream;

import client.misc.ClientConst;


/**
 * AdminLog.java Creado el 29-jun-2004
 * 
 * Este archivo es parte de JMClient
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * JMClient es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class AdminLog {

    private static File Flog;

    private static RandomAccessFile RAFlog;

    private static String ValueLog = "VerboseFile";

    /**
     * Este constructor inicializa el Administrador de logs con la prioridad
     * asignada en el archivo de configuracion
     * 
     * @param ValueLog
     *            Valor de la prioridad <br>
     *            Valores posibles: <br>
     *            Default: Muestra solo mensajes de error <br>
     *            Verbose: Muestra todos los mensajes <br>
     *            VerboseFile: Muestra todos los mensajes por la consola y
     *            genera un archivo log <br>
     *            LogFile: Solo genera un archivo log con todos los mensajes
     *            <br>
     */

    public AdminLog(String ValueLog) {
        AdminLog.ValueLog = ValueLog;
        newFile();
    }
    
    /**
     * 
     * @param mensaje
     * @param prioridad
     */
    public static void setMessage(String mensaje, int prioridad) {
        if (ValueLog.equals("Default") && (prioridad == ClientConst.ERROR)) {
            System.out.println(mensaje);
        } else if (ValueLog.equals("Verbose")) {
            System.out.println(mensaje);
        } else if (ValueLog.equals("VerboseFile")) {
            System.out.println(mensaje);
            EscribirArchivo(mensaje);
        } else if (ValueLog.equals("LogFile")) {
            if (prioridad == ClientConst.ERROR)
                System.out.println(mensaje);
            EscribirArchivo(mensaje);
        }

    }

    /**
     * Este metodo se encarga de escribir el fichero E-Maku.log 
     * @param mensaje Cadena de texto a a�adir al E-Maku.log
     */

    private synchronized static void EscribirArchivo(String mensaje) {
        try {
            long max = Flog.length();

            if (max >= ClientConst.MAX_SIZE_FILE_LOG) {
                byte[] Blog = new byte[(int) max];
                File Ffile = new File(ClientConst.TMP, "JMClient-"
                        + Calendar.getInstance().get(Calendar.YEAR) + "-"
                        + Calendar.getInstance().get(Calendar.MONTH) + "-"
                        + Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        + ".gz");
                FileOutputStream FOSfile = new FileOutputStream(Ffile);
                GZIPOutputStream gzipfile = new GZIPOutputStream(FOSfile);
                RAFlog.seek(0);
                RAFlog.read(Blog);
                gzipfile.write(Blog);
                gzipfile.close();
                FOSfile.close();
                Flog.delete();
                newFile();
            }

            RAFlog.writeBytes(Calendar.getInstance().getTime() + " " + mensaje + "\n");
        } catch (IOException IOEe) {
            IOEe.getMessage();
        }

    }
    
    
    private static void newFile() {
        try {
            Flog = new File(ClientConst.TMP, "JMClient.log");
            RAFlog = new RandomAccessFile(Flog, "rw");
            RAFlog.seek(RAFlog.length());
        } catch (FileNotFoundException FNFEe) {
            FNFEe.printStackTrace();
        } catch (IOException IOEe) {
            IOEe.printStackTrace();
        }
    }
}