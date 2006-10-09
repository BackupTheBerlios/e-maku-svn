package server.database.sql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import server.comunications.SocketServer;
import server.control.SendUPDATECODE;
import server.database.connection.PoolConexiones;
import server.misc.ServerConst;
import server.misc.settings.ConfigFile;

import common.comunications.SocketWriter;
import common.misc.language.Language;
import common.misc.log.LogAdmin;
/**
 * CacheEnlace.java Creado el 1-jul-2004
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
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
 * Esta clase es la encargada de almacenar los saldos en cache. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */

public class LinkingCache {

    private static Statement st = null;
    private static Hashtable <String,String>HcompanyData = new Hashtable<String,String>();
	
    private static Hashtable <String,Object>Hinstrucciones = new Hashtable<String,Object>();
    private static Hashtable <String,Object>Htransacciones = new Hashtable<String,Object>();
    private static Hashtable <String,Object>Hpermisos      = new Hashtable<String,Object>();
    private static Hashtable <String,Object>Hasientos_pr = new Hashtable<String,Object>();
    private static Hashtable <String,Object>Hctas_asientos = new Hashtable<String,Object>();
    private static Hashtable <String,Object>Hctas_naturaleza = new Hashtable<String,Object>();
    private static Hashtable <String,Object>Hlibro_aux = new Hashtable<String,Object>();
    
    private static Hashtable <String,ClassLogicDriver>Hlogica_drivers = new Hashtable<String,ClassLogicDriver>();
    private static Hashtable <String,InfoInventario>Hinventarios = new Hashtable<String,InfoInventario>();
    private static Hashtable <String,String>Hconsecutive = new Hashtable<String,String>();
    private static Hashtable <String,PerfilCta>Hperfil_cta = new Hashtable<String,PerfilCta>();
    
    /**
     * Metodo encargado de llenar el cache de los saldos en las tablas de
     * dispersion.
     */
    public static void cargar() {

        ResultSet rs = null;
        /** Obtengo el numero de conexiones que maneja el ST */
        int max = ConfigFile.getDBSize();

        LogAdmin.setMessage(Language.getWord("LOADING_SL"),
                ServerConst.MESSAGE);

        /**
         * Este ciclo recorre todas las bases de datos configuradas en el
         * Servidor de Transacciones
         */

       
        for (int i = 0; i < max; i++)
            try {

                LogAdmin.setMessage(Language.getWord("LOADING_CACHE") + " "
                        + ConfigFile.getDBName(i), ServerConst.MESSAGE);

                /**
                 * Establezco la conexion con la base de datos
                 */

                st = PoolConexiones.getConnection(ConfigFile.getDBName(i)).createStatement();

                LogAdmin.setMessage(Language.getWord("INIT_BDS"), ServerConst.MESSAGE);
                LogAdmin.setMessage(Language.getWord("LOADING_ST") + " " + ConfigFile.getDBName( i ) , ServerConst.MESSAGE);

                /*
                 * Esta es la unica maldita sentencia que encontraras en el codigo de emaku
                 */
                
                         	  
                rs = st.executeQuery("SELECT codigo,sentencia FROM "+
                "sentencia_sql ORDER BY codigo desc");
                /*
                 * Se almacena la información en la hashtable Hinstrucciones 
                 */
                
                while(rs.next()){
                    Hinstrucciones.put("K-"+ConfigFile.getDBName(i)+"-"+
			                           rs.getString("codigo"),
				                       rs.getString("sentencia"));
                }                
                
                /*
                 * Esta sentencia carga las transacciones a las que tienen permisos
                 * los usuarios
                 */
                
                Htransacciones = loadCache(ConfigFile.getDBName(i),"SEL0001", new String[]{"login","codigo","password"},"ok");

                /*
                 * Esta consulta carga las sentencias a las que tienen permisos
                 * los usuarios 
                 */

                Hpermisos = loadCache(ConfigFile.getDBName(i),"SEL0002", new String[]{"login","codigo","password"},"ok");
                
                /*
                 * Se realiza la consulta para obtener los drivers de la tabla
                 * transacciones
                 */

                rs = st.executeQuery(InstruccionesSQL.getSentencia(ConfigFile.getDBName(i),
                					 "SEL0026"));

                /*
                 * Se almacena la información en la tabla hashtable Hlogica_drivers
                 */

                SAXBuilder builder = new SAXBuilder(false);

                while (rs.next()) {
                    try {
	                ByteArrayInputStream bufferInDrv;
	                ByteArrayInputStream bufferInMth;
	                Document docDrv = null;
	                Document docMth = null;
	                try {
	                    bufferInDrv = new ByteArrayInputStream(rs.getString("args_driver").getBytes());
	                    docDrv = builder.build(bufferInDrv);	                
	                }
	                catch(NullPointerException NPEe) {}
	                
	                try {
		                bufferInMth = new ByteArrayInputStream(rs.getString("args_metodo").getBytes());
		                docMth = builder.build(bufferInMth);
	                }
	                catch(NullPointerException NPEe) {}
	                Hlogica_drivers.put("K-" + ConfigFile.getDBName(i) + "-"
                            + rs.getString("codigo").trim(), 
                            new ClassLogicDriver(rs.getString("driver"),
                                    			  docDrv,
                                    			  rs.getString("metodo"),
                                    			  docMth));
                    }
                    catch(IOException IOEe) {
                        LogAdmin.setMessage(Language.getWord("ERR_LOADING_LG") + " "
                                + ConfigFile.getDBName(i) + " "+rs.getString("codigo")+" "+IOEe.getMessage(),
                                ServerConst.ERROR);
                    }
                    catch(JDOMException JDOMEe) {
                        LogAdmin.setMessage(Language.getWord("ERR_LOADING_LG") + " "
                                + ConfigFile.getDBName(i) + " "+rs.getString("codigo")+" " + JDOMEe.getMessage(),
                                ServerConst.ERROR);
                    }
                }
                
                /*
                 * Esta consulta captura el nombre de la empresa y su Nit
                 */
                
                rs = st.executeQuery(InstruccionesSQL.getSentencia(ConfigFile.getDBName(i),"SEL0305"));

                while (rs.next()) {                	
                    HcompanyData.put("K-" + ConfigFile.getDBName(i) + "-company",
                    		         String.valueOf(rs.getString("nombre")));
                    HcompanyData.put("K-" + ConfigFile.getDBName(i) + "-companyID",
                    		         String.valueOf(rs.getString("id_char")));
                }
                
                /*
                 * Esta sentencia consulta la numeracion actual de todos los documentos
                 * si el documento consultado no tiene numero, entonces se almacenara
                 * como numero inicial 000000000, si por el contrario el numero si existe
                 * a este se le suma un uno.
                 */

                rs = st.executeQuery(InstruccionesSQL.getSentencia(ConfigFile.getDBName(i),"SEL0072"));

				while (rs.next()) {
                    Hconsecutive.put("K-" + ConfigFile.getDBName(i) + "-"
                            + rs.getString("codigo_tipo").trim(), consecutive(ConfigFile.getDBName(i),
                                                                  rs.getString("max")));
				}
                
				
                /* Se realiza la consulta para obtener el precio de costo, el saldo y el valor del saldo
                 * de la tabla inventarios
                 */

                rs = st.executeQuery(InstruccionesSQL.getSentencia(ConfigFile.getDBName(i),"SEL0089"));

                /*
                 * Se almacena la informaci�n en un objeto InfoInventario y luego en la
                 * tabla hashtable Hinventarios
                 */

                while (rs.next()) {
                    Hinventarios.put("K-" + ConfigFile.getDBName(i) + "-"
                                          + rs.getInt("id_bodega")+ "-"
                                          + rs.getInt("id_prod_serv"),
                                          new InfoInventario(rs.getDouble("pinventario"),
                                        		  			 rs.getDouble("saldo"),
                                        		  			 rs.getDouble("valor_saldo")));
                }

                /*
                 * Esta metodo carga el perfil de todas las cuentas contables generadas.
                 */

                loadPerfilCta(ConfigFile.getDBName(i));

                /*
                 * Este metodo carga la informacion relacionada a asientos prefedinidos
                 */
                
                loadAsientosPredefinidos(ConfigFile.getDBName(i));

                /*
                 * Se realiza la consulta para obtener los saldos de la tabla
                 * libro_aux
                 */

                Hlibro_aux = loadCache(ConfigFile.getDBName(i),"SEL0097", new String[]{"centro","id_cta","id_tercero","id_prod_serv"},"saldo");
            }
            catch (SQLException SQLEe) {
            	SQLEe.printStackTrace();
                LogAdmin.setMessage(Language.getWord("ERROR_LOADING_SL") + " "
                        + ConfigFile.getDBName(i) + SQLEe.getMessage(),
                        ServerConst.ERROR);
            }
            catch (SQLNotFoundException SQLNFEe) {
            	SQLNFEe.printStackTrace();
                LogAdmin.setMessage(Language.getWord("ERROR_LOADING_SL") + " "
                        + ConfigFile.getDBName(i) + SQLNFEe.getMessage(),
                        ServerConst.ERROR);
            }
            
        CloseSQL.close(st);
        CloseSQL.close(rs);

    }

    private static void loadAsientosPredefinidos(String bd) 
    throws SQLException, SQLNotFoundException {
        /*
         * Esta consulta carga los id de todos los asientos predefinidos.
         */

        Hasientos_pr = loadCache(bd,"SEL0095", new String[]{"id_prod_serv","id_asientos_prod_serv"},"id_asientos_pr");
        /*
         * Esta consulta carga las cuentas de los asientos predefinidos con su respectiva naturaleza
         */

        Hctas_asientos = loadCache(bd,"SEL0096", new String[]{"id_asientos_pr","char_cta"},"naturaleza");
    }
    
    /**
     * Este metodo se encarga de abrir el perfil de una cuenta de detalle
     * @param bd
     * @throws SQLException
     * @throws SQLNotFoundException
     */
    public static void loadPerfilCta(String bd) 
    throws SQLException, SQLNotFoundException {
    	Statement st = PoolConexiones.getConnection(bd).createStatement();
        ResultSet rs = st.executeQuery(InstruccionesSQL.getSentencia(bd,"SEL0094"));

        /*
         * Se almacena la informacion en un objeto PerfilCta y luego en la
         * tabla hashtable Hperfil_cta
         */

        while (rs.next()) {
            Hperfil_cta.put("K-" + bd + "-"
                                  + rs.getString("char_cta").trim(),
                                  new PerfilCta(rs.getString("id_cta"),
                                		        rs.getBoolean("terceros"),
                                		  		rs.getBoolean("inventarios"),
                                		  		rs.getBoolean("centro"),
                                		  		rs.getBoolean("ajuste"),
                                		  		rs.getDouble("base"),
                                		  		rs.getDouble("porcentaje")));
        }
        
        /*
         * Esta consulta carga la naturaleza de las cuentas de detalle
         */

        Hctas_naturaleza = loadCache(bd,"SEL0315", new String[]{"id_cta"},"naturaleza");

        CloseSQL.close(st);
        CloseSQL.close(rs);
    }
    
    private static synchronized Hashtable<String,Object> loadCache(String bd,String sql,String key[],String rsValue) 
    throws SQLException, SQLNotFoundException {
        ResultSet rs = st.executeQuery(InstruccionesSQL.getSentencia(bd,sql));
        key.getClass().getName();
        
        Hashtable<String,Object> tabla = new Hashtable<String,Object>();
        while (rs.next()) {
        	String subkey="";
        	for (String subData:key) {
        		subkey+=rs.getString(subData).trim()+"-";
        	}
            tabla.put("K-" + 
            		  bd + 
            		  "-" +
            		  subkey.substring(0,subkey.length()-1),
                      rs.getString(rsValue));
        }
        CloseSQL.close(rs);
        return tabla;
    }

    /**
     * Este metodo retorna el nombre o el NIT de una empresa
     * @param key Codigo de la empresa
     * @return retorna el nombre o el NIT de una empresa
     */

    public static String getCompanyData(String key) {
        return HcompanyData.get(key);
    }
    
    
    private static String consecutive(String nombreBD,String value) {
        if (value!=null && !value.equals("")) {
            try {
                long nextValue = Long.parseLong(value.trim())+1;
                String sNextValue = "0000000000"+nextValue;
                return sNextValue.substring(sNextValue.length()-10);
            }
            catch (NumberFormatException NFEe){
                LogAdmin.setMessage(Language.getWord("ERR_CONSECUTIVE") + " "
                        + nombreBD + NFEe.getMessage(),
                        ServerConst.ERROR);
                return "0000000000";
            }
        }
        else {
            return "0000000000";
        }
    }
    
    /**
     * Este metodo retorna el Saldo de la tabla Inventarios
     * @param bd Nombre de la base de datos
     * @param bodega Codigo de la bodega en la que se va a buscar el producto
     * @param id_prod_serv Codigo del producto al que se le va a buscar el saldo
     * @return retorna el saldo de la consulta
     */

    public static double getSaldoInventario(
            String bd, String bodega,String id_prod_serv) {
        if (Hinventarios.containsKey("K-"+bd+"-"+bodega+"-"+id_prod_serv)) {
        	
            return Hinventarios.get("K-"+bd+"-"+bodega+"-"+id_prod_serv).getSaldo();
        }
        else {
            return (double)0;
        }
    }

    /**
     * Este metodo actualiza el saldo de la tabla inventarios
     * @param bd Nombre de la base de datos
     * @param bodega Codigo de la bodega en la que se va a buscar el producto
     * @param id_prod_serv Codigo del producto al que se le va a buscar el saldo
     * @param saldo el nuevo valor del saldo
     */

    public static void setSaldoInventario(String bd, String bodega,
            String id_prod_serv, double saldo) {
        String ktmp = "K-"+bd+"-"+bodega+"-"+id_prod_serv;
        if (Hinventarios.containsKey(ktmp)) {
            Hinventarios.get(ktmp).setSaldo(saldo);
        }
        else {
            Hinventarios.put(ktmp,new InfoInventario(0,saldo,0));
        }
    }

    /**
     * Este metodo retorna el valor del saldo de la tabla Inventarios
     * @param bd Nombre de la base de datos
     * @param bodega Codigo de la bodega en la que se va a buscar el producto
     * @param id_prod_serv Codigo del producto al que se le va a buscar el saldo
     * @return retorna el saldo de la consulta
     */

    public static double getVSaldoInventario(
            String bd, String bodega,String id_prod_serv) {
        if (Hinventarios.containsKey("K-"+bd+"-"+bodega+"-"+id_prod_serv)) {
            return Hinventarios.get("K-"+bd+"-"+bodega+"-"+id_prod_serv).getVsaldo();
        }
        else {
            return (double)0;
        }
    }

    /**
     * Este metodo actualiza el valor del saldo de la tabla inventarios
     * @param bd Nombre de la base de datos
     * @param bodega Codigo de la bodega en la que se va a buscar el producto
     * @param id_prod_serv Codigo del producto al que se le va a buscar el saldo
     * @param saldo el nuevo valor del saldo
     */

    public static void setVSaldoInventario(String bd, String bodega,
            String id_prod_serv, double saldo) {
        String ktmp = "K-"+bd+"-"+bodega+"-"+id_prod_serv;
        if (Hinventarios.containsKey(ktmp)) {
            Hinventarios.get(ktmp).setVsaldo(saldo);
        }
        else {
            Hinventarios.put(ktmp,new InfoInventario(0,0,saldo));
        }
    }

    /**
     * Este metodo actualiza el valor del saldo de la tabla inventarios
     * de una unica llave
     * @param key llave
     * @param saldo nuevo saldo
     */
    public static void setVSaldoInventario(String key,double saldo) {
        if (Hinventarios.containsKey(key)) {
            Hinventarios.get(key).setVsaldo(saldo);
        }
        else {
            Hinventarios.put(key,new InfoInventario(0,0,saldo));
        }
    }
    
    /**
     * Este metodo retorna el precio de costo de la tabla Inventarios
     * @param bd Nombre de la base de datos
     * @param bodega Codigo de la bodega en la que se va a buscar el producto
     * @param id_prod_serv Codigo del producto al que se le va a buscar el saldo
     * @return retorna el saldo de la consulta
     */

    public static double getPCosto(
            String bd, String bodega,String id_prod_serv) {
        if (Hinventarios.containsKey("K-"+bd+"-"+bodega+"-"+id_prod_serv)) {
            return Hinventarios.get("K-"+bd+"-"+bodega+"-"+id_prod_serv).getPcosto();
        }
        else {
            return (double)0;
        }
    }

    /**
     * Este metodo actualiza el valor del precio de costo de la tabla inventarios
     * @param bd Nombre de la base de datos
     * @param bodega Codigo de la bodega en la que se va a buscar el producto
     * @param id_prod_serv Codigo del producto al que se le va a buscar el saldo
     * @param saldoel nuevo valor del saldo
     */

    public static void setPCosto(String bd, String bodega,
            String id_prod_serv, double saldo) {
        String ktmp = "K-"+bd+"-"+bodega+"-"+id_prod_serv;
        if (Hinventarios.containsKey(ktmp)) {
            Hinventarios.get(ktmp).setPcosto(saldo);
        }
        else {
            Hinventarios.put(ktmp,new InfoInventario(saldo,0,0));
        }
    }

    
    /**
     * Este metodo retorna el Saldo de la tabla libro_aux
     * @param bd Nombre de la base de datos
     * @param centro Codigo del centro costo en el que se va a buscar la cuenta
     * @param cta Cuenta en la que se va a buscar el saldo
     * @return retorna el saldo de la consulta
     */

    public static double getSaldoLibroAux(String bd, String centro, String cta, String id_tercero, String id_prod_serv) {
        if (Hlibro_aux.containsKey("K-"+bd+"-"+centro+"-"+cta+"-"+id_tercero+"-"+id_prod_serv)) {
            return Double.parseDouble((String)Hlibro_aux.get("K-"+bd+"-"+centro+"-"+cta+"-"+id_tercero+"-"+id_prod_serv));
        }
        else {
        	return 0;
        }
    }

    /**
     * Este metodo actualiza el Saldo de la tabla libro_aux
     * @param bd Nombre de la base de datos
     * @param centro Codigo del centro costo en el que se va a buscar la cuenta
     * @param cta Cuenta en la que se va a buscar el saldo
     * @param saldo el nuevo valor del saldo
     */

    public static void setSaldoLibroAux(String bd, String centro, String cta,String id_tercero, String id_prod_serv,
            Double saldo) {
        String ktmp = "K-"+bd+"-"+centro+"-"+cta+"-"+id_tercero+"-"+id_prod_serv;
        Hlibro_aux.remove(ktmp);
        Hlibro_aux.put(ktmp, saldo);
    }

    /**
     * Este metodo actualiza el Saldo de la tabla libro_aux
     * @param key llave
     * @param saldo nuevo valor del saldo
     */
    
    public static void setSaldoLibroAux(String key,Double saldo) {
        Hlibro_aux.remove(key);
        Hlibro_aux.put(key, saldo);
    }


    /**
     * Este metodo retorna el los parametros de logica de negocios de
     * una transaccion
     * @param bd  Nombre de la base de datos
     * @param id_transaction Codigo del catalogo en el que se va a buscar el producto o
     *            servicio
     * @return retorna la clase ClassLoadDriver con sus respectivos parametros
     */

    public static ClassLogicDriver getDriver(String bd, String id_transaction){
        return Hlogica_drivers.get("K-"+bd+"-"+id_transaction);
    }

    /**
     * Este metodo retorna una sentencia SQL
     * @param key Codigo de la sentencia SQL
     * @return retorna la sentencia SQL
     */

    public static String getSentenciaSQL(String key) {
        return (String)Hinstrucciones.get(key);
    }
    
    /**
     * Este metodo consulta permisos en sentencias SQL
     * @param key Codigo de la sentencia SQL
     * @return retorna un boleano
     */

    public static boolean getPermisosSQL(String key) {
        if (Hpermisos.containsKey(key))
            return true;
        else
            return false;
    }
    
    /**
     * Este metodo consulta permisos en Transaccion
     * @param key Codigo de la Transaccion
     * @return retorna un boleano
     */

    public static boolean getPermisosTransaccion(String key) {
        if (Htransacciones.containsKey(key))
            return true;
        else
            return false;
    }
    

    /**
     * Este metodo actualiza la numeracion de un documento
     * @param bd Nombre de la base de datos
     * @param key identificador del documento
     * @param value nuevo valor del documento
     */

    public static void setConsecutive(String bd, String key,String value) {
        String ktmp = "K-"+bd+"-"+key;
        Hconsecutive.remove(ktmp);
        Hconsecutive.put(ktmp,value);
        
    }

    /**
     * Este metodo vuelve a consultar a la base de datos el consecutivo de un
     * documento
     * @throws SQLException 
     * @throws SQLNotFoundException 
     * @throws SQLBadArgumentsException 
     */
    public static void reloadConsecutive(String bd,String key) 
    throws SQLException, SQLNotFoundException, SQLBadArgumentsException {
        Connection cn = PoolConexiones.getConnection(bd);
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery(InstruccionesSQL.getSentencia(bd,"SEL0139",new String[]{key}));
        String consecutive="";
		while (rs.next()) {
			consecutive = rs.getString(1);
		}
		Hconsecutive.remove("K-"+bd+"-"+key);
		Hconsecutive.put("K-"+bd+"-"+key,consecutive);
		incrementeConsecutive(bd,key);
        CloseSQL.close(st);
        CloseSQL.close(rs);
    }
    
    /**
     * Este metodo incrementa el consecutivo de la llave recibida, y hace 
     * un envio de paquetes broadcast a todos los clientes que se encuentren
     * en linea de la misma base de datos
     * @param bd base de datos
     * @param key llave del cosecutivo
     */
    
    public static void incrementeConsecutive(String bd, String key) {
        String consecutive = getConsecutive(bd,key);
        try {
            long nextValue = Long.parseLong(consecutive.trim())+1;
            String sNextValue = "0000000000"+nextValue;
            setConsecutive(bd,key,sNextValue.substring(sNextValue.length()-10));
            Hashtable clientes = SocketServer.getHchannelclients();
            Enumeration sockets = clientes.keys();
            while (sockets.hasMoreElements()) {
                SocketChannel sock = (SocketChannel)sockets.nextElement();
                if (SocketServer.getBd(sock).equals(bd)) {
		            SocketWriter.writing(sock,
		                    SendUPDATECODE.getPackage(key,
		                                              LinkingCache.getConsecutive(bd,key)));
                }
            }
        }
        catch (NumberFormatException NFEe){
            LogAdmin.setMessage(Language.getWord("ERR_CONSECUTIVE") + " "
                    + bd + NFEe.getMessage(),
                    ServerConst.ERROR);
        }
        
    }
    
    /**
     * Este metodo retorna el consecutivo de un documento
     * @param bd Codigo de la base de datos
     * @param key Identificador del documento
     * @return String codigo consecutivo del documento solicitado
     */

    public static String getConsecutive(String bd,String key) {
        if (Hconsecutive.containsKey("K-"+bd+"-"+key)) {
            return Hconsecutive.get("K-"+bd+"-"+key);
        }
        else {
            return "0000000000";
        }
    }
    
    /**
     * Este metodo retorna si la clase maneja terceros o no
     * @param bd Codigo de la base de datos
     * @param account cuenta contable a consultar
     * @return boolean true si maneja terceros, false si no.
     * @throws DontHaveKeyException excepcion en caso de no encontrar la cuenta
     */
    
    public static boolean isPCTerceros(String bd,String account) throws DontHaveKeyException {
    	if (Hperfil_cta.containsKey("K-"+bd+"-"+account)) {
    		return ((PerfilCta)Hperfil_cta.get("K-"+bd+"-"+account)).isTerceros();
    	}
    	else {
    		throw new DontHaveKeyException(account);
    	}
    }
    
    /**
     * Este metodo retorna si la clase maneja centro costos o no
     * @param bd Codigo de la base de datos
     * @param account cuenta contable a consultar
     * @return boolean true si maneja centro costos, false si no.
     * @throws DontHaveKeyException excepcion en caso de no encontrar la cuenta
     */
    
    public static boolean isPCCentro(String bd,String account) throws DontHaveKeyException {
    	if (Hperfil_cta.containsKey("K-"+bd+"-"+account)) {
    		return ((PerfilCta)Hperfil_cta.get("K-"+bd+"-"+account)).isCentro();
    	}
    	else {
    		throw new DontHaveKeyException(account);
    	}
    }

    /**
     * Este metodo retorna si la clase maneja inventarios o no
     * @param bd Codigo de la base de datos
     * @param account cuenta contable a consultar
     * @return boolean true si maneja inventarios, false si no.
     * @throws DontHaveKeyException excepcion en caso de no encontrar la cuenta
     */
    
    public static boolean isPCInventarios(String bd,String account) throws DontHaveKeyException {
    	if (Hperfil_cta.containsKey("K-"+bd+"-"+account)) {
    		return Hperfil_cta.get("K-"+bd+"-"+account).isInventarios();
    	}
    	else {
    		throw new DontHaveKeyException(account);
    	}
    }

    /**
     * Este metodo retorna si la clase maneja ajustes o no
     * @param bd Codigo de la base de datos
     * @param account cuenta contable a consultar
     * @return boolean true si maneja ajuste, false si no.
     * @throws DontHaveKeyException excepcion en caso de no encontrar la cuenta
     */
    
    public static boolean isPCAjuste(String bd,String account) throws DontHaveKeyException {
    	if (Hperfil_cta.containsKey("K-"+bd+"-"+account)) {
    		return Hperfil_cta.get("K-"+bd+"-"+account).isAjuste();
    	}
    	else {
    		throw new DontHaveKeyException(account);
    	}
    }

    /**
     * Este metodo retorna el valor de la base para realizar un asiento
     * @param bd Codigo de la base de datos
     * @param account cuenta contable a consultar
     * @return base para realizar un asiento
     * @throws DontHaveKeyException excepcion en caso de no encontrar la cuenta
     */
    
    public static double getPCBase(String bd,String account) throws DontHaveKeyException {
    	if (Hperfil_cta.containsKey("K-"+bd+"-"+account)) {
    		return ((PerfilCta)Hperfil_cta.get("K-"+bd+"-"+account)).getBase();
    	}
    	else {
    		throw new DontHaveKeyException(account);
    	}
    }

    /**
     * Este metodo retorna el porcentaje para realizar un asiento
     * @param bd Codigo de la base de datos
     * @param account cuenta contable a consultar
     * @return porcentaje para realizar un asiento
     * @throws DontHaveKeyException excepcion en caso de no encontrar la cuenta
     */
    
    public static double getPCPorcentaje(String bd,String account) throws DontHaveKeyException {
    	if (Hperfil_cta.containsKey("K-"+bd+"-"+account)) {
    		return ((PerfilCta)Hperfil_cta.get("K-"+bd+"-"+account)).getPorcentaje();
    	}
    	else {
    		throw new DontHaveKeyException(account);
    	}
    }
    
    /**
     * Este metodo retorna el id de una cuenta contable
     * @param bd Codigo de la base de datos
     * @param account Cuenta contable a consultar
     * @return id_cta
     * @throws DontHaveKeyException excepcion en caso de no encontrar la cuenta.
     */
    
    public static String getPCIdCta(String bd,String account) throws DontHaveKeyException {
	    	if (Hperfil_cta.containsKey("K-"+bd+"-"+account)) {
	    		return ((PerfilCta)Hperfil_cta.get("K-"+bd+"-"+account)).getId_cta();
	    	}
	    	else {
	    		throw new DontHaveKeyException(account);
	    	}
    }
    
    /**
     * Este metodo retorna el codigo de un asiento predefinido
     * @param bd Codigo de la base de datos
     * @param code Codigo del producto
     * @param id_asientos_prod_serv Codigo de asignacion para el asiento del producto
     * @return codigo del asiento predefinido
     * @throws DontHaveKeyException excepcion en caso de no encontrar el asiento predefinido
     */
    
    public static String getIdAsientosPr(String bd,String id_prod_serv,String id_asientos_prod_serv) throws DontHaveKeyException {
	   	if (Hasientos_pr.containsKey("K-"+bd+"-"+id_prod_serv+"-"+id_asientos_prod_serv)) {
	    		return (String)Hasientos_pr.get("K-"+bd+"-"+id_prod_serv+"-"+id_asientos_prod_serv);
	    	}
	    	else {
	    		throw new DontHaveKeyException("K-"+bd+"-"+id_prod_serv+"-"+id_asientos_prod_serv);
	    	}
    }
    
    /**
     * Este metodo retorna si una cuenta debe debitarce o acreditarce en el momento de generar un asiento
     * @param bd Codigo de la base de datos
     * @param id_asiento_pr	Codigo del asiento predefinido
     * @param cta Codigo de la cuenta contable
     * @return Retorna true si la cuenta se debita o false si se acredita
     * @throws DontHaveKeyException excepcion en caso de no encontrar el asiento predefinido.
     */
    
    public static boolean isAsientoDebito(String bd,String id_asiento_pr,String cta) throws DontHaveKeyException {
    	if (Hctas_asientos.containsKey("K-"+bd+"-"+id_asiento_pr+"-"+cta)) {
    		return Boolean.parseBoolean((String)Hctas_asientos.get("K-"+bd+"-"+id_asiento_pr+"-"+cta));
    		
    	}
    	else {
    		throw new DontHaveKeyException("K-"+bd+"-"+id_asiento_pr+"-"+cta);
    	}
    }
    
    public static boolean isDebitAccount(String bd,String cta) throws DontHaveKeyException {
    	if (Hctas_naturaleza.containsKey("K-"+bd+"-"+cta)) {
    		return Boolean.parseBoolean((String)Hctas_naturaleza.get("K-"+bd+"-"+cta));
    	}
    	else {
    		throw new DontHaveKeyException("K-"+bd+"-"+cta);
    	}
    }
 
}

class PerfilCta {
	
	private String id_cta="";
	private boolean terceros;
	private boolean inventarios;
	private boolean centro;
	private boolean ajuste;
	private double base=0;
	private double porcentaje=0;
	
	public PerfilCta(String id_cta,boolean terceros,boolean inventarios,boolean centro,boolean ajuste,double base,double porcentaje) {
		this.id_cta=id_cta;
		this.terceros=terceros;
		this.inventarios=inventarios;
		this.centro=centro;
		this.ajuste=ajuste;
		this.base=base;
		this.porcentaje=porcentaje;
		
		
	}

	public boolean isAjuste() {
		return ajuste;
	}

	public void setAjuste(boolean ajuste) {
		this.ajuste = ajuste;
	}

	public double getBase() {
		return base;
	}

	public void setBase(double base) {
		this.base = base;
	}

	public boolean isCentro() {
		return centro;
	}

	public void setCentro(boolean centro) {
		this.centro = centro;
	}

	public boolean isInventarios() {
		return inventarios;
	}

	public void setInventarios(boolean inventarios) {
		this.inventarios = inventarios;
	}

	public double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(double porcentaje) {
		this.porcentaje = porcentaje;
	}

	public boolean isTerceros() {
		return terceros;
	}

	public void setTerceros(boolean terceros) {
		this.terceros = terceros;
	}

	public String getId_cta() {
		return id_cta;
	}

	public void setId_cta(String id_cta) {
		this.id_cta = id_cta;
	}
}

class InfoInventario {
    
    double pcosto=0;
    double saldo=0;
    double vsaldo=0;
    
    public InfoInventario(double pcosto,double saldo,double vsaldo) {
        this.pcosto=pcosto;
        this.saldo=saldo;
        this.vsaldo=vsaldo;
    }
    
    public double getPcosto() {
        return pcosto;
    }
    public void setPcosto(double pcosto) {
        this.pcosto = pcosto;
    }
    public double getSaldo() {
        return saldo;
    }
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    public double getVsaldo() {
        return vsaldo;
    }
    public void setVsaldo(double vsaldo) {
        this.vsaldo = vsaldo;
    }
}