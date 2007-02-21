package common.misc;

import java.util.HashMap;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;


/**
 * commonCons.java Creado el 07-oct-2004
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
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
public class CommonConstants {
    public static final int ERROR 		= 0;
    public static final int WARNING 		= 1;
    public static final int MESSAGE 		= 2;
    public static final long MAX_SIZE_FILE_LOG = 5242880;
    public static final String TMP 		= System.getProperty("java.io.tmpdir");
    public static PrintService defaultPrintService;
    public static HashMap<String, Integer> ScpCodes;
    public static PrintService printServices[];
    public static PrintService printSelect = null;
    
    public static void lookupDefaultPrintService() {
    	printServices = PrintServiceLookup.lookupPrintServices(null, null);
    	ScpCodes = new HashMap<String, Integer>();
    	ScpCodes.put("ESC",27); // Escape
    	ScpCodes.put("FF",12);  // Fin de Pagina 
    	ScpCodes.put("EOT",7);  // Final de Transmision 
    	ScpCodes.put("DC2",18); // No Condensada
    	ScpCodes.put("ETB",23); // Fin de Comando
    	ScpCodes.put("SI",15);  // Si Condensada
    	ScpCodes.put("C",67);   // No. de Lineas
    	ScpCodes.put("E",69);   // 
    	ScpCodes.put("F",70);
    	ScpCodes.put("NL",13);
    }
}