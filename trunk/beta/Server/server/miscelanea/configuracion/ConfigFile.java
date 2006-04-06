
package server.miscelanea.configuracion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import common.miscelanea.idiom.Language;
import common.miscelanea.log.AdminLog;
import server.miscelanea.ServerConst;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

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
 * Penndiente 
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class ConfigFile {
    
    private static SAXBuilder builder;
    private static Document doc;
    private static Element raiz;
    private static Language idioma = new Language();
    private static Vector <Conexiones>VPoolConexiones  = new Vector<Conexiones>();
    private static int SocketJClient;
    private static int SocketJAdmin;
    private static int MaxClients;
    
    /**
     * Este metodo se encarga de cargar el archivo de configuracion
     * @throws ConfigFileNotLoadException
     */
    public static void Cargar() throws ConfigFileNotLoadException{
        try {
            builder = new SAXBuilder(false);
            System.out.println("Configuracion: "+ServerConst.CONF+
                    ServerConst.SEPARATOR+
                    "server.conf");
            doc = builder.build(
                                ServerConst.CONF+
                                ServerConst.SEPARATOR+
                                "server.conf");
            
            raiz = doc.getRootElement();
            java.util.List Lconfig = raiz.getChildren();
            Iterator i = Lconfig.iterator();
                
            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML, en este
             * caso Configuraci�n
             */
            
            while (i.hasNext()) {
                
                Element datos = (Element)i.next();
                if (datos.getName().equals("PoolConnection")) {
                    CargarPoolConexiones(datos.getChildren());
                } else if (datos.getName().equals("Lenguaje")) {
                    idioma.CargarLenguaje(datos.getValue());
                } else if (datos.getName().equals("Log")) {
                    new AdminLog(datos.getValue(),ServerConst.KeyServer);
                } else if (datos.getName().equals("SocketJClient")) {
                    SocketJClient = Integer.parseInt(datos.getValue());
                } else if (datos.getName().equals("SocketJAdmin")) {
                    SocketJAdmin = Integer.parseInt(datos.getValue());
                } else if (datos.getName().equals("MaxClients")) {
                    MaxClients = Integer.parseInt(datos.getValue());
                }
            }
            AdminLog.setMessage(Language.getWord("LOADING_CF"), ServerConst.MESSAGE);
        }
        catch (FileNotFoundException FNFEe) {
        	System.out.println("El archivo de configuracion no existe");
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
    
    private static void CargarPoolConexiones(java.util.List LPoolConexiones) {
        Iterator i = LPoolConexiones.iterator();

        while (i.hasNext()) {
            Element datos = (Element)i.next();
            VPoolConexiones.addElement(CargarBD(datos.getChildren()));
        }
    }
    
    /**
     * Metodo encargado de cargar las propiedades de las conexiones de 
     * las bases de datos
     * @param LBD Lista de datos xml que contienen los parametros de la conexion
     * @return Valor tipo Conexiones.
     */
    
    private static Conexiones CargarBD(java.util.List LBD) {
        Iterator i = LBD.iterator();
        Conexiones conect = new Conexiones();
        
        while (i.hasNext()) {
            Element datos = (Element)i.next();
            if (datos.getName().equals("name")) {
                conect.setNombre(datos.getValue());
            }
            else if(datos.getName().equals("driver")) {
                conect.setDriver(datos.getValue());
            }
            else if(datos.getName().equals("url")) {
                conect.setUrl(datos.getValue());
            }
            else if(datos.getName().equals("username")) {
                conect.setUsuario(datos.getValue());
            }
            else if(datos.getName().equals("password")) {
                conect.setPassword(datos.getValue());
            }
        }
        return conect;
    }
    
    /**
     * Metodo encargado de retornar el Numero de conexiones a bases de 
     * datos configuradas
     */
    
    public static int SizeDB() {
        return VPoolConexiones.size();
    }
    
    /**
     * Retorna el nombre del driver de la conexion a la base de datos indicada
     * por su indice correspondiente
     * @param index indice del vector que contiene el objeto conexiones variable driver
     * @return variable driver tipo String
     */
    
    public static String getDriver(int index) {
        return VPoolConexiones.get(index).getDriver();
    }

    /**
     * Retorna el nombre de la base de datos indicada
     * por su indice correspondiente
     * @param index indice del vector que contiene el objeto conexiones variable nombre
     * @return variable nombre tipo String
     */
    
    public static String getNombreBD(int index) {
        return VPoolConexiones.get(index).getNombre();
    }

    /**
     * Retorna la url de la conexion a la base de datos indicada
     * por su indice correspondiente
     * @param index indice del vector que contiene el objeto conexiones variable url
     * @return variable url tipo String
     */
    
    public static String getUrl(int index) {
        return VPoolConexiones.get(index).getUrl();
    }

    /**
     * Retorna el Usuario con el que se conectara la base de datos indicada
     * por su indice correspondiente
     * @param index inidice del vector que contiene el objeto conexiones variable Usuario
     * @return variable usuario tipo String
     */
    
    public static String getUsuario(int index) {
        return VPoolConexiones.get(index).getUsuario();
    }
    
    /**
     * Retorna el password con de la conexion a la base de datos indicada
     * por su indice correspondiente
     * @param index indice del vector que contiene el objeto conexiones variable password
     * @return variable password tipo String
     */
    
    public static String getPassword(int index) {
        return VPoolConexiones.get(index).getPassword();
    }
    
    public static int getSocketJAdmin() {
        return SocketJAdmin;
    }
    public static int getSocketJClient() {
        return SocketJClient;
    }
    public static int getMaxClients() {
        return MaxClients;
    }
}
    
/**
 * Esta clase almacena una estructura con la informaci�n necesaria
 * para poder instanciar una base de datos
 */
    
class Conexiones {
    
    private String nombre;
    private String url;
    private String driver;
    private String usuario;
    private String password;
    
    /** Getter for property nombre.
     * @return Value of property nombre.
     *
     */
    public java.lang.String getNombre() {
        return nombre;
    }
    
    /** Setter for property nombre.
     * @param nombre New value of property nombre.
     *
     */
    public void setNombre(java.lang.String nombre) {
        this.nombre = nombre;
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
    public java.lang.String getUsuario() {
        return usuario;
    }
    
    /** Setter for property usuario.
     * @param usuario New value of property usuario.
     *
     */
    public void setUsuario(java.lang.String usuario) {
        this.usuario = usuario;
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