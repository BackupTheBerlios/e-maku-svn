
package com.kazak.smi.server.misc.settings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.kazak.smi.lib.misc.Language;
import com.kazak.smi.server.misc.LogWriter;

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
    private static int    port;
    private static String appOwner;
    private static String mainDataBase;
    private static String secondDataBase;
    private static String local;
    private static String userMail; 
    private static String passWordMail;
    private static String mailServer;
    private static int    hour;
    private static int    minute;
    private static int    second;
	private static int    timeIntervalConnect;
	private static int    timeAlifeMessageForClient;
	private static int    timeAlifeMessageInDataBase;
	private static int    maxMessagesDataBase;
    
    
    /**
     * Este metodo se encarga de cargar el archivo de configuracion
     * @throws ConfigFileNotLoadException
     */
    public static void loadConfigFile(String emakuConfigFile) throws ConfigFileNotLoadException{
        try {
            builder = new SAXBuilder(false);
            
            LogWriter.write("INFO: Config -> "+emakuConfigFile);
            
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
                } else if (records.getName().equals("MainDataBase")) {
                	mainDataBase = records.getValue();
                } else if (records.getName().equals("SecondDataBase")) {
                	secondDataBase = records.getValue();
                } else if (records.getName().equals("AppOwner")) {
                	appOwner = records.getValue();
                }else if (records.getName().equals("Lenguaje")) {
                	local = records.getValue();
                    appLang.CargarLenguaje(local);
                } else if (records.getName().equals("Port")) {
                    port = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("UserMail")) {
                	userMail = records.getValue();
                } else if (records.getName().equals("PasswordMail")) {
                	passWordMail = records.getValue();
                } else if (records.getName().equals("MailServer")) {
                	mailServer = records.getValue();
                } else if (records.getName().equals("Hour")) {
                    hour = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("Minute")) {
                    minute = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("Second")) {
                    second = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("timeIntervalConnect")) {
                	timeIntervalConnect = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("TimeAlifeMessageForClient")) {
                	timeAlifeMessageForClient = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("TimeAlifeMessageInDataBase")) {
                	timeAlifeMessageInDataBase = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("MaxMessagesDataBase")) {
                	maxMessagesDataBase = Integer.parseInt(records.getValue());
                } 
            }
            LogWriter.write(Language.getWord("LOADING_CF"));
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
            else if(records.getName().equals("connectOnInit")) {
                connection.setConnecOnInit(Boolean.parseBoolean(records.getValue()));
            }
        }
        return connection;
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
    
    public static boolean isConnectOnInit(int index) {
        return VConnectionsPool.get(index).isConnecOnInit();
    }
    
    public static Connection getConnection(String name) throws SQLException, ClassNotFoundException {
    	for (Connections con : VConnectionsPool) {
    		if (name.equals(con.getName())) {
    			Class.forName(con.getDriver());
    			return DriverManager.getConnection(con.getUrl(),con.getUser(),con.getPassword());
    		}
    	}
    	return null;
    }
    
    public static int getPort() {
        return port;
    }
	public static String getLocal() {
		return local;
	}

	public static String getMainDataBase() {
		return mainDataBase;
	}

	public static int getHour() {
		return hour;
	}

	public static String getMailServer() {
		return mailServer;
	}

	public static int getMinute() {
		return minute;
	}

	public static String getPassWordMail() {
		return passWordMail;
	}

	public static int getSecond() {
		return second;
	}

	public static String getUserMail() {
		return userMail;
	}

	public static String getSecondDataBase() {
		return secondDataBase;
	}

	public static String getAppOwner() {
		return appOwner;
	}

	public static int getTimeIntervalConnect() {
		return timeIntervalConnect;
	}

	public static void setTimeIntervalConnect(int timeIntervalConnect) {
		ConfigFile.timeIntervalConnect = timeIntervalConnect;
	}

	public static int getMaxMessagesDataBase() {
		return maxMessagesDataBase;
	}

	public static int getTimeAlifeMessageForClient() {
		return timeAlifeMessageForClient;
	}

	public static int getTimeAlifeMessageInDataBase() {
		return timeAlifeMessageInDataBase;
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
    private boolean connecOnInit = true;
    
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

	public boolean isConnecOnInit() {
		return connecOnInit;
	}

	public void setConnecOnInit(boolean connecOnInit) {
		this.connecOnInit = connecOnInit;
	}
    
}