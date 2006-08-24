package common.misc.log;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import common.misc.CommonConst;
import common.misc.language.Language;

//import common.misc.language.Language;

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
 * Informacion de la clase <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class LogAdmin {

	private static File Flog;

	private static RandomAccessFile RAFlog;

	private static String ValueLog = "VerboseFile";

	private static String namefile = "Nonamelog";

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

	public LogAdmin(String ValueLog, String namefile) {
		LogAdmin.ValueLog = ValueLog;
		LogAdmin.namefile = namefile;
		newFile();
	}

	public static void setMessage(String mensaje, int prioridad) {
		setMessage("", mensaje, "", prioridad);
	}

	/**
	 * 
	 * @param mensaje
	 * @param prioridad
	 */
public static void setMessage(String key,String mensaje,String debug, int prioridad) {
	
	    String os = System.getProperty("os.name");
    	
    	if (os.startsWith("Windows") && (prioridad == CommonConst.ERROR)) {  

    		JLabel mesg = new JLabel(mensaje);
    		
    		JTextArea mesg2 = new JTextArea(3,25);
    		mesg2.setText(debug);
    		mesg2.setWrapStyleWord(true);
    		mesg2.setLineWrap(true);	
    		mesg2.setEditable(false);
    		
    		JButton accept = new JButton(Language.getWord("ACCEPT"));
    		
    		Border etched1 = BorderFactory.createEtchedBorder();
    		TitledBorder title1 = BorderFactory.createTitledBorder(etched1);
    		
    		JPanel contentPane = new JPanel();
    		contentPane.setLayout(new BorderLayout());
    		JPanel panel1 = new JPanel();
    		panel1.setBorder(title1);
    		panel1.setLayout(new BorderLayout());
    		
    		JPanel panel2 = new JPanel();
    		panel2.setBorder(title1);
    		panel2.setLayout(new BorderLayout());
    		JPanel panel3 = new JPanel();
    		panel3.setLayout(new BorderLayout());
    		
    		panel1.add(mesg, BorderLayout.CENTER);
    		panel2.add(mesg2, BorderLayout.CENTER);
    		panel3.add(accept, BorderLayout.CENTER);
    		
    		contentPane.add(panel1, BorderLayout.NORTH);
    		contentPane.add(panel2,BorderLayout.CENTER);
    		contentPane.setSize(400,600);
    		
    		JOptionPane.showMessageDialog(new JFrame(),contentPane,Language.getWord("ERROR"),
    				JOptionPane.ERROR_MESSAGE);
    		
    		if (Language.getCodeError(key).equals("002")) {
    			System.exit(1);
    		} 
    	}
    	
        if (ValueLog.equals("Default") && (prioridad == CommonConst.ERROR)) {
            System.out.println(mensaje);
        } else if (ValueLog.equals("Verbose")) {
            System.out.println(mensaje);
        } else if (ValueLog.equals("VerboseFile")) {
            System.out.println(mensaje);
            EscribirArchivo(mensaje);
        } else if (ValueLog.equals("LogFile")) {
            if (prioridad == CommonConst.ERROR)
                System.out.println(mensaje);
            EscribirArchivo(mensaje);
        }
    }
	/**
	 * Este metodo se encarga de escribir el fichero E-Maku.log
	 * 
	 * @param mensaje
	 *            Cadena de texto a a�adir al E-Maku.log
	 */

	private synchronized static void EscribirArchivo(String mensaje) {
		try {
			long max = Flog.length();
			Date now = new Date();
			SimpleDateFormat format = null;
			if (max >= CommonConst.MAX_SIZE_FILE_LOG) {
				byte[] Blog = new byte[(int) max];

				format = new SimpleDateFormat("yyyy-MM-dd");

				File Ffile = new File(CommonConst.TMP, namefile + "-"
						+ format.format(now) + ".gz");
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
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			RAFlog.writeBytes(format.format(now) + " " + mensaje + "\n");
		} catch (IOException IOEe) {
			IOEe.getMessage();
		}

	}

	private static void newFile() {
		try {
			Flog = new File(CommonConst.TMP, namefile + ".log");
			RAFlog = new RandomAccessFile(Flog, "rw");
			RAFlog.seek(RAFlog.length());
		} catch (FileNotFoundException FNFEe) {
			FNFEe.printStackTrace();
		} catch (IOException IOEe) {
			IOEe.printStackTrace();
		}
	}
}