package com.kazak.smi.server.misc.settings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class ConfigFileHandler {
    
    private static SAXBuilder builder;
    private static Document doc;
    private static Element root;
    private static Language appLang = new Language();
    private static Vector <Connections>VConnectionsPool  = new Vector<Connections>();
    private static int port;
    private static String appOwner;
    private static String mainDataBase;
    private static String secondDataBase;
	private static int checkMailEvery;
    private static String local;
    private static String mailUser; 
    private static String mailPassword;
    private static String mailServer;
    private static int messageLifeTimeForClients;
    private static int messageLifeTimeInDB;
    private static int maxMessagesDataBase;
    private static ArrayList<OracleSyncTask> oracleSync = new ArrayList<OracleSyncTask>();;
      
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
                if (records.getName().equals("ConnectionPool")) {
                    loadConnectionsPool(records.getChildren());
                } else if (records.getName().equals("MainDataBase")) {
                	mainDataBase = records.getValue();
                } else if (records.getName().equals("SourceDataBase")) {
                	secondDataBase = records.getValue();
                } else if (records.getName().equals("AppOwner")) {
                	appOwner = records.getValue();
                }else if (records.getName().equals("Language")) {
                	local = records.getValue();
                    appLang.loadLanguage(local);
                } else if (records.getName().equals("Port")) {
                    port = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("MailUser")) {
                	mailUser = records.getValue();
                } else if (records.getName().equals("MailPassword")) {
                	mailPassword = records.getValue();
                } else if (records.getName().equals("MailServer")) {
                	mailServer = records.getValue();
                } else if (records.getName().equals("CheckMailEvery")) {
                	checkMailEvery = Integer.parseInt(records.getValue());
                } else if (records.getName().equals("MessageLifeTimeForClients")) {
    	        	messageLifeTimeForClients = Integer.parseInt(records.getValue());
    	        }else if (records.getName().equals("MessageLifeTimeInDataBase")) {
    	        	messageLifeTimeInDB = Integer.parseInt(records.getValue());
    	        } else if (records.getName().equals("MaxMessagesDataBase")) {
    	        	maxMessagesDataBase = Integer.parseInt(records.getValue());
    	        } else if (records.getName().equalsIgnoreCase("SyncTask")) {
                	oracleSync.add(createSyncTask(records));
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

    private static OracleSyncTask createSyncTask(Element e) {
    	OracleSyncTask sync = new OracleSyncTask();
    	Iterator i = e.getChildren().iterator();
    	while (i.hasNext()) {
    		Element records = (Element)i.next();
	    	if (records.getName().equals("Hour")) {
	            sync.setHour(Integer.parseInt(records.getValue()));
	        } else if (records.getName().equals("Minute")) {
	            sync.setMinute(Integer.parseInt(records.getValue()));
	        } else if (records.getName().equals("Second")) {
	            sync.setSecond(Integer.parseInt(records.getValue()));
	        }
    	}
    	
    	return sync;
    }
    /**
     * Metodo encargado de cargar cada una de las conexiones a las
     * Bases de Datos
     * @param connectionsPool Lista de datos xml que contienen el pool de conexiones de
     * las bases de datos existentes
     */
    
    private static void loadConnectionsPool(java.util.List connectionsPool) {
        Iterator i = connectionsPool.iterator();

        while (i.hasNext()) {
            Element records = (Element)i.next();
            VConnectionsPool.addElement(loadDB(records.getChildren()));
        }
    }
    
    /**
     * Metodo encargado de cargar las propiedades de las conexiones de 
     * las bases de datos
     * @param LDB Lista de datos xml que contienen los parametros de la conexion
     * @return Valor tipo Conexiones.
     */
    
    private static Connections loadDB(java.util.List LDB) {
        Iterator i = LDB.iterator();
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
        return VConnectionsPool.get(index).isConnectedOnInit();
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


	public static String getUserMail() {
		return mailUser;
	}

	public static String getSecondDataBase() {
		return secondDataBase;
	}

	public static String getAppOwner() {
		return appOwner;
	}

	public static String getPassWordMail() {
		return mailPassword;
	}

	public static String getMailServer() {
		return mailServer;
	}
	public static int getTimeIntervalConnect() {
		return checkMailEvery;
	}

	public static void setTimeIntervalConnect(int timeIntervalConnect) {
		ConfigFileHandler.checkMailEvery = timeIntervalConnect;
	}

	public static ArrayList<OracleSyncTask> getOraclesync() {
		return oracleSync;
	}

	public static int getMessageLifeTimeForClients() {
		return messageLifeTimeForClients;
	}

	public static void setTimeAlifeMessageForClient(int MessageLifeTimeForClient) {
		ConfigFileHandler.messageLifeTimeForClients = MessageLifeTimeForClient;
	}

	public static int getMaxMessagesDataBase() {
		return maxMessagesDataBase;
	}

	public static int getMessageLifeTimeInDataBase() {
		return messageLifeTimeInDB;
	}}
    
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
    private boolean connectedOnInit = true;
    
    /** Getter for property nombre.
     * @return Value of property nombre.
     *
     */
    public String getName() {
        return name;
    }
    
    /** Setter for property nombre.
     * @param name New value of property nombre.
     *
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /** Getter for property url.
     * @return Value of property url.
     *
     */
    public String getUrl() {
        return url;
    }
    
    /** Setter for property url.
     * @param url New value of property url.
     *
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /** Getter for property driver.
     * @return Value of property driver.
     *
     */
    public String getDriver() {
        return driver;
    }
    
    /** Setter for property driver.
     * @param driver New value of property driver.
     *
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }
    
    /** Getter for property usuario.
     * @return Value of property usuario.
     *
     */
    public String getUser() {
        return user;
    }
    
    /** Setter for property usuario.
     * @param usuario New value of property usuario.
     *
     */
    public void setUser(String user) {
        this.user = user;
    }
    
    /** Getter for property password.
     * @return Value of property password.
     *
     */
    public String getPassword() {
        return password;
    }
    
    /** Setter for property password.
     * @param password New value of property password.
     *
     */
    public void setPassword(String password) {
        this.password = password;
    }

	public boolean isConnectedOnInit() {
		return connectedOnInit;
	}

	public void setConnecOnInit(boolean connecOnInit) {
		this.connectedOnInit = connecOnInit;
	}
}
