package com.kazak.smi.server.misc;

//import java.awt.Toolkit;

/**
 * ServerConstants.java Creado el 29-jun-2004
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
 * 
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public interface ServerConstants {
    
	public final String KeyServer = "SMI";

	public final String SMI_HOME       =  System.getenv("SMI_HOME");
	public final String HOME 		= System.getProperty("user.home");
    public final String SEPARATOR 	= System.getProperty("file.separator");
	public final String CONF            = SMI_HOME + SEPARATOR + "conf";
    public final String TMP 		= System.getProperty("java.io.tmpdir");
    public final int ERROR 			= 0;
    public final int WARNING 		= 1;
    public final int MESSAGE 		= 2;
    public final long MAX_SIZE_FILE_LOG = 5242880;
    public final String CONTEN_TYPE = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
    public final String [] TAGS_ANSWER = {"        <ANSWER>\n","        </ANSWER>\n"};
    public final String [] TAGS_CACHE_ANSWER = {"<CACHE-ANSWER>\n","</CACHE-ANSWER>\n"};
    public final String [] TAGS_VALUE = {"    <value>\n","    </value>\n"};
    public final String [] TAGS_KEY = {"    <key>","</key>\n"};
    public final String [] TAGS_ID = {"<id>","</id>"};
    public final String [] TAGS_HEAD = {"    <header>\n","    </header>\n"};
    public final String [] TAGS_SQL = {"    <sql>","</sql>\n"};
    public final String [] TAGS_ROW = {"            <row>\n",
            						   "            </row>\n"};
    public final String [] TAGS_COL_HEAD = {"       <col type=\"","\">"};
    public final String [] TAGS_COL = {"                <col>","</col>\n"};    
}
