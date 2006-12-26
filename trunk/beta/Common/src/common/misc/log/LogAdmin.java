package common.misc.log;

import java.awt.BorderLayout;
import java.io.File;
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
//import common.misc.exception.SuperException;
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
 * Esta clase se encarga de realizar la administracion de logs del Servidor
 * de Transacciones. 
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */

public class LogAdmin {

	private static File logFile;

	private static RandomAccessFile RAFlog;

	private static String logFlag = "VerboseFile";

	private static String fileName = "Nonamelog";

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
		LogAdmin.logFlag = ValueLog;
		LogAdmin.fileName = namefile;
		newFile();
	}


	/**
	 * Este método se encarga de administrar el tipo de salida que 
	 * debe asignarse a un error
	 * 
	 * @param key
	 *            Llave que identifica el tipo de error
	 * @param message
	 *            Titulo del error
	 * @param debug
	 *            Mensaje que especifica la causa técnica del error
	 * @param priority
	 *            Prioridad del mensaje
	 */

	public static void setMessage(String key,String message,String debug, int priority)  {
	
	    String os = System.getProperty("os.name");

	    if (priority == CommonConst.ERROR) {
    		
    		printConsoleError(key,message,debug);	    	
	    
	    	if (os.startsWith("Windows") && (priority == CommonConst.ERROR)) {  
	    
	    		showWindowsMessage(key,message,debug);
	    		
	    		if (Language.getCodeError(key).equals("002")) {
	//    			new SuperException(SuperException.PANIC);
	    		} 
	    	}
	    }
	    else {
	    	if (logFlag.equals("Verbose")) {	    		
	    		System.out.println(message);
	    		
	    	} else if (logFlag.equals("VerboseFile")) {
	    		System.out.println(message);
	    		writeToFile(message);
	    		
	    	} else if (logFlag.equals("LogFile")) {
	    		writeToFile(message);
	    	}
	    }
    }

	/**
	 * Este método se encarga de redireccionar mensajes sencillos
	 * del sistema a un método más complejo para procesarlos
	 * 
	 * @param message
	 *            Titulo del error
	 * @param priority
	 *            Prioridad del mensaje
	 */

	public static void setMessage(String message, int priority) {
		setMessage("", message, "", priority);
	}

	/**
	 * Este método se encarga de visualizar una ventana con un mensaje 
	 * de error cuando el sistema operativo es Windows
	 * 
	 * @param key
	 *            Llave que identifica el tipo de error
	 * @param message
	 *            Titulo del error
	 * @param debug
	 *            Mensaje que especifica la causa técnica del error
	 */
    
    private static void showWindowsMessage(String key, String message, String debug) {
		JLabel mesg = new JLabel(message);
		
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
		
		JOptionPane.showMessageDialog(new JFrame(),contentPane,Language.getWord("ERROR") + " #" + Language.getCodeError(key),
				JOptionPane.ERROR_MESSAGE);
    }

	/**
	 * Este metodo se encarga de escribir un mensaje de error a consola
	 * @param key
	 *            Llave que identifica el tipo de error
	 * @param message
	 *            Titulo del error
	 * @param debug
	 *            Mensaje que especifica la causa tecnica del error
	 */
    
    private static void printConsoleError(String key,String message,String debug) {
  	 System.out.println(Language.getWord("ERROR") + " #" + Language.getCodeError(key));
	 System.out.println(message);
	 System.out.println(debug);
	}   

	/**
	 * Este metodo se encarga de escribir el fichero E-Maku.log
	 * 
	 * @param message
	 *            Cadena de texto a añadir al E-Maku.log
	 */

	private synchronized static void writeToFile(String message) {
		try {
			long max = logFile.length();
			Date now = new Date();
			SimpleDateFormat format = null;
			if (max >= CommonConst.MAX_SIZE_FILE_LOG) {
				byte[] Blog = new byte[(int) max];

				format = new SimpleDateFormat("yyyy-MM-dd");

				File dateFile = new File(CommonConst.TMP, fileName + "-"
						+ format.format(now) + ".gz");
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

	/**
	 * Este método se encarga de inicializar el archivo en donde
	 * se van a almacenar los logs del sistema
	 * 
	 */

	private static void newFile() {
		try {
			logFile = new File(CommonConst.TMP, fileName + ".log");
			RAFlog  = new RandomAccessFile(logFile, "rw");
			RAFlog.seek(RAFlog.length());
		} catch (IOException FNFEe) {
			String user   = System.getProperty("user.name");
			String reason = FNFEe.getMessage();
			
			if (reason.contains("Permission denied")) { 
			    try {
			    	logFile = new File(CommonConst.TMP, fileName + "." + user + ".log");
			    	RAFlog = new RandomAccessFile(logFile, "rw");
			    	RAFlog.seek(RAFlog.length());
			    } catch(IOException ex) {

			    }
			}
			else
			    FNFEe.printStackTrace();
		}
	}
}