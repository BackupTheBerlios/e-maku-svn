
package server.misc.settings;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import server.gui.ConfigDialog;
import server.misc.ServerConst;

import common.misc.language.Language;
import common.misc.log.LogAdmin;

/**
 * ConfigFile.java Creado el 25-jun-2004
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
 * Esta clase se encarga de administrar el archivo de configuración del
 * Servidor de Transacciones.
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class ConfigFile {
    
    private static SAXBuilder builder;
    private static Document doc;
    private static Element root;
    private static Language appLang = new Language();
    private static Vector <Connections>VConnectionsPool  = new Vector<Connections>();
    private static int clientSocket;
    private static int adminSocket;
    private static int MaxClients;
    private static String local;
    /**
     * Este metodo se encarga de cargar el archivo de configuracion
     * @throws ConfigFileNotLoadException
     */
    public static void loadConfigFile(String emakuConfigFile) throws ConfigFileNotLoadException{
        try {
            builder = new SAXBuilder(false);
            
            System.out.println("INFO: Config -> "+emakuConfigFile);
            
            doc = builder.build(emakuConfigFile);
            
            root = doc.getRootElement();
            java.util.List Lconfig = root.getChildren();
            Iterator i = Lconfig.iterator();
                
            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML, en este
             * caso Configuración
             */
            
            while (i.hasNext()) {
                
                Element records = (Element)i.next();
                if (records.getName().equals("PoolConnection")) {
                    loadConnectionsPool(records.getChildren());
                } else if (records.getName().equals("Lenguaje")) {
                	local = records.getValue();
                    appLang.CargarLenguaje(local);
                } else if (records.getName().equals("Log")) {
                    new LogAdmin(records.getValue(),ServerConst.KeyServer);
                } else if (records.getName().equals("SocketJClient")) {
                    clientSocket = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("SocketJAdmin")) {
                    adminSocket = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("MaxClients")) {
                    MaxClients = Integer.parseInt(records.getValue());
                }
            }
            LogAdmin.setMessage(Language.getWord("LOADING_CF"), ServerConst.MESSAGE);
        }
        catch (FileNotFoundException FNFEe) {       	
        	throw new ConfigFileNotLoadException();
        }
        catch (JDOMException JDOMEe) {
            throw new ConfigFileNotLoadException();
        } 
        catch (IOException IOEe) {
            throw new ConfigFileNotLoadException();
        }
    }

    /**
     * Metodo encargado de cargar cada una de las conexiones a las
     * Bases de Datos
     * @param LPoolConexiones Lista de datos xml que contienen el pool de conexiones de
     * las bases de datos existentes
     */
    
    private static void loadConnectionsPool(java.util.List LPoolConexiones) {
        Iterator i = LPoolConexiones.iterator();

        while (i.hasNext()) {
            Element records = (Element)i.next();
            VConnectionsPool.addElement(loadBD(records.getChildren()));
        }
    }
    
    /**
     * Metodo encargado de cargar las propiedades de las conexiones de 
     * las bases de datos
     * @param LBD Lista de datos xml que contienen los parametros de la conexion
     * @return Valor tipo Conexiones.
     */
    
    private static Connections loadBD(java.util.List LBD) {
        Iterator i = LBD.iterator();
        Connections connection = new Connections();
        
        while (i.hasNext()) {
            Element records = (Element)i.next();
            if (records.getName().equals("name")) {
                connection.setName(records.getValue());
            }
            else if(records.getName().equals("driver")) {
                connection.setDriver(records.getValue());
            }
            else if(records.getName().equals("url")) {
                connection.setUrl(records.getValue());
            }
            else if(records.getName().equals("username")) {
                connection.setUser(records.getValue());
            }
            else if(records.getName().equals("password")) {
                connection.setPassword(records.getValue());
            }
        }
        return connection;
    }
    
    public static void newConfigFile(String emakuConfigFile) {
    	
    	String lang = "es_CO";
    	String clientPort = "9117";
    	String adminPort = "28124";
    	String maxClients = "500";
    	String logType = "Verbose";
    	String company = "mi_empresa";
    	String jdbcDriver = "org.postgresql.Driver";
    	String url = "jdbc:postgresql://localhost:5432/mi_empresa";
        String user = "emaku";
    	String password = "";
    	
	    String os = System.getProperty("os.name");

	    if (os.startsWith("Windows")) {	    		
	    	    ConfigDialog newConfigDialog = new ConfigDialog(new JFrame());
	    		newConfigDialog.pack();
	    		newConfigDialog.setLocation(
	                    (ServerConst.MAX_WIN_SIZE_WIDTH / 2) - newConfigDialog.getWidth() / 2,
	                    (ServerConst.MAX_WIN_SIZE_HEIGHT / 2) - newConfigDialog.getHeight() / 2);
	    		newConfigDialog.setVisible(true);
	    		
	    		lang       = newConfigDialog.getLanguage();
	    		clientPort = newConfigDialog.getClientPort();
	    		adminPort  = newConfigDialog.getAdminPort();
	    		maxClients = newConfigDialog.getMaxClients();             
	    		logType    = newConfigDialog.getLogType();
	    		company    = newConfigDialog.getCompany();
	    		jdbcDriver = newConfigDialog.getJDBCDriver();
	    		url        = newConfigDialog.getUrl();
	    		user       = newConfigDialog.getUser();
	    		password   = newConfigDialog.getPasswd();
	    }    	
    	
    	try {
    		FileOutputStream serverConfFile = new FileOutputStream(emakuConfigFile);
    		PrintStream FOSServerConf = new PrintStream(serverConfFile);
    		FOSServerConf.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    		FOSServerConf.println("<!--\n");
    		FOSServerConf.println("    Document   : server.conf.xml");
    		FOSServerConf.println("    Created on : 29 de junio de 2004, 14:29");
    		FOSServerConf.println("    Author     : Luis Felipe Hernandes, Cristian David Cepeda");
    		FOSServerConf.println("-->");
    		FOSServerConf.println();
    		FOSServerConf.println("<Config>");
    		FOSServerConf.println("    <Lenguaje>"+lang+"</Lenguaje>");
    		FOSServerConf.println("    <SocketJClient>"+clientPort+"</SocketJClient>");
    		FOSServerConf.println("    <SocketJAdmin>"+adminPort+"</SocketJAdmin>");
    		FOSServerConf.println("        <MaxClients>"+maxClients+"</MaxClients>");
    		FOSServerConf.println("    <!-- Valores posibles:");
    		FOSServerConf.println("    Default:     Muestra solo mensajes de error");
    		FOSServerConf.println("    Verbose:     Muestra todos los mensajes");
    		FOSServerConf.println("    VerboseFile: Muestra todos los mensajes por la consola y genera un");
    		FOSServerConf.println("                 archivo log");
    		FOSServerConf.println("    LogFile:     Solo genera un archivo log con todos los mensajes");
    		FOSServerConf.println("    -->");
    		FOSServerConf.println("    <Log>"+logType+"</Log>");
    		FOSServerConf.println("    <PoolConnection>");
    		FOSServerConf.println("        <DataBase>");
    		FOSServerConf.println("            <name>"+company+"</name>");
    		FOSServerConf.println("            <driver>"+jdbcDriver+"</driver>");
    		FOSServerConf.println("            <url>"+url+"</url>");
    		FOSServerConf.println("            <username>"+user+"</username>");
    		FOSServerConf.println("            <password>"+password+"</password>");
    		FOSServerConf.println("        </DataBase>");
    		FOSServerConf.println("    </PoolConnection>");
    		FOSServerConf.println("</Config>");    		 
    		FOSServerConf.close();		 
        } catch (SecurityException ex) {
        	System.out.println("ERROR: El usuario emaku no tiene permisos para crear el archivo de configuración.");
    	}
          catch (IOException e) {
    		System.out.println("ERROR: No se pudo crear el archivo de configuración.");
    	} 	
     }
       
    /**
     * Metodo encargado de retornar el Numero de conexiones a bases de 
     * datos configuradas
     */
    
    public static int getDBSize() {
        return VConnectionsPool.size();
    }
    
    /**
     * Retorna el nombre del driver de la conexion a la base de datos indicada
     * por su indice correspondiente
     * @param index indice del vector que contiene el objeto conexiones variable driver
     * @return variable driver tipo String
     */
    
    public static String getDriver(int index) {
        return VConnectionsPool.get(index).getDriver();
    }

    /**
     * Retorna el nombre de la base de datos indicada
     * por su indice correspondiente
     * @param index indice del vector que contiene el objeto conexiones variable nombre
     * @return variable nombre tipo String
     */
    
    public static String getDBName(int index) {
        return VConnectionsPool.get(index).getName();
    }

    /**
     * Retorna la url de la conexion a la base de datos indicada
     * por su indice correspondiente
     * @param index indice del vector que contiene el objeto conexiones variable url
     * @return variable url tipo String
     */
    
    public static String getUrl(int index) {
        return VConnectionsPool.get(index).getUrl();
    }

    /**
     * Retorna el Usuario con el que se conectara la base de datos indicada
     * por su indice correspondiente
     * @param index inidice del vector que contiene el objeto conexiones variable Usuario
     * @return variable usuario tipo String
     */
    
    public static String getUser(int index) {
        return VConnectionsPool.get(index).getUser();
    }
    
    /**
     * Retorna el password con de la conexion a la base de datos indicada
     * por su indice correspondiente
     * @param index indice del vector que contiene el objeto conexiones variable password
     * @return variable password tipo String
     */
    
    public static String getPassword(int index) {
        return VConnectionsPool.get(index).getPassword();
    }
    
    public static int getAdminSocket() {
        return adminSocket;
    }
    public static int getClientSocket() {
        return clientSocket;
    }
    public static int getMaxClients() {
        return MaxClients;
    }

	public static String getLocal() {
		return local;
	}
}
    
/**
 * Esta clase almacena una estructura con la informaci�n necesaria
 * para poder instanciar una base de datos
 */
    
class Connections {
    
    private String name;
    private String url;
    private String driver;
    private String user;
    private String password;
    
    /** Getter for property nombre.
     * @return Value of property nombre.
     *
     */
    public java.lang.String getName() {
        return name;
    }
    
    /** Setter for property nombre.
     * @param name New value of property nombre.
     *
     */
    public void setName(java.lang.String nameVar) {
        this.name = nameVar;
    }
    
    /** Getter for property url.
     * @return Value of property url.
     *
     */
    public java.lang.String getUrl() {
        return url;
    }
    
    /** Setter for property url.
     * @param url New value of property url.
     *
     */
    public void setUrl(java.lang.String url) {
        this.url = url;
    }
    
    /** Getter for property driver.
     * @return Value of property driver.
     *
     */
    public java.lang.String getDriver() {
        return driver;
    }
    
    /** Setter for property driver.
     * @param driver New value of property driver.
     *
     */
    public void setDriver(java.lang.String driver) {
        this.driver = driver;
    }
    
    /** Getter for property usuario.
     * @return Value of property usuario.
     *
     */
    public java.lang.String getUser() {
        return user;
    }
    
    /** Setter for property usuario.
     * @param usuario New value of property usuario.
     *
     */
    public void setUser(java.lang.String userVar) {
        this.user = userVar;
    }
    
    /** Getter for property password.
     * @return Value of property password.
     *
     */
    public java.lang.String getPassword() {
        return password;
    }
    
    /** Setter for property password.
     * @param password New value of property password.
     *
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }
    
}