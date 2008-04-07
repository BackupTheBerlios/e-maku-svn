package client.misc;

import java.awt.Toolkit;

/**
 * ClientConst.java Creado el 29-jul-2004
 * 
 * Este archivo es parte de JMClient <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * JMClient es Software Libre; usted puede redistribuirlo y/o realizar
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
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public interface ClientConstants {

	public final boolean WEBSTART = ClientConstants.class.getResource("").toString().substring(0,8).equals("jar:http")?true:false;
	public final String URL = ClientConstants.class.getResource("").getPath().substring(0,ClientConstants.class.getResource("").getPath().length()-35);
	public final String KeyClient = "client";
	
	public final String EMAKU_HOME = WEBSTART?URL:System.getenv("EMAKU_HOME");
	public final String HOME = WEBSTART?URL:System.getProperty("user.home");
    public final String SEPARATOR = WEBSTART?"/":System.getProperty("file.separator");
    
    public final String TMP = System.getProperty("java.io.tmpdir");
	public final String CONF = HOME + SEPARATOR + ".qhatu"+SEPARATOR;
	public final String LANG = EMAKU_HOME + SEPARATOR + "lib"	+ SEPARATOR + "lang" + SEPARATOR;
	public final String THEMES = EMAKU_HOME + SEPARATOR + "themes"+SEPARATOR;
	public final String COMPANIES = EMAKU_HOME + ClientConstants.SEPARATOR 
						+ "lib" + SEPARATOR + "companies" + SEPARATOR;

    public final int ERROR = 0;
    public final int WARNING = 1;
    public final int MESSAGE = 2;

    public final long MAX_SIZE_FILE_LOG = 5242880;

    public static int MAX_WIN_SIZE_HEIGHT = (int) Toolkit.getDefaultToolkit()
            .getScreenSize().getHeight();

    public static int MAX_WIN_SIZE_WIDTH = (int) Toolkit.getDefaultToolkit()
            .getScreenSize().getWidth();
}
