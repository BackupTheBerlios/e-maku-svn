package client.miscelanea.configuracion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import client.miscelanea.ClientConst;
import common.misc.Icons;
import common.miscelanea.idiom.Language;
import common.miscelanea.parameters.GenericParameters;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
/**
 * ConfigFile.java Creado el 25-jun-2004
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
 * Esta clase es la encargada de almacenar los datos de configuracion necesarios
 * para el common <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class ConfigFile extends GenericParameters {

    private static SAXBuilder builder;
    private static Document doc;
    private static Element raiz;
    private static Language idioma = new Language();
    private static Icons icons = new Icons();
    private static int serverport;
    private static String host;

    /**
     * Este metodo sirve para crear un nuevo archivo de configuracion
     * 
     * @param Host
     *            Direccion ip o nombre de la maquina servidor
     * @param Port
     *            puerto del servidor
     * @param Language
     *            idioma
     * @param log
     *            tipo de log a generar
     */
    public static void New(String Host, String Port, String Language, String log,String cash) {
        
        doc = new Document();
        doc.setRootElement(new Element("Configuration"));
        
        doc.getRootElement().addContent(
                new Element("language").setText(Language));
        doc.getRootElement().addContent(
                new Element("host").setText(Host));
        doc.getRootElement().addContent(
                new Element("serverport").setText(Port));
        doc.getRootElement().addContent(
                new Element("log").setText(log));
        doc.getRootElement().addContent(
        		    new Element("cash").setText("CA001"));
        GenericParameters.addParameter("cash","CA001");
        icons.loadIcons();

        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        
        File file = 
            new File(ClientConst.CONF);//,"client.conf");

        try {
        	if (!file.exists()) {
        		file.mkdir();
        	}
        	
        	if (file.isDirectory()) {
        		file = new File(ClientConst.CONF,"client.conf");
        		FileOutputStream outFile = new FileOutputStream(file);
        		out.output(doc, outFile);
        		outFile.close();
        		ConfigFile.host = Host;
        		ConfigFile.serverport = Integer.parseInt(Port);
        		idioma.CargarLenguaje(Language);
        	}
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Este metodo se encarga de cargar el archivo de configuracion
     * 
     * @throws ConfigFileNotLoadException
     */
    public static void Cargar() throws ConfigFileNotLoadException {
        try {
            
            builder = new SAXBuilder(false);
            doc = builder.build(ClientConst.CONF+"client.conf");
            raiz = doc.getRootElement();
            java.util.List Lconfig = raiz.getChildren();
            Iterator i = Lconfig.iterator();

            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML,
             * en este caso Configuraci�n
             */

            while (i.hasNext()) {

                Element datos = (Element) i.next();
                String nombre = datos.getName(); 
                if (nombre.equals("language")) {
                    idioma.CargarLenguaje(datos.getValue());
                } /*
                   * else if (datos.getName().equals("Log")) { new
                   * AdminLog(datos.getValue()); }
                   */
                else if (nombre.equals("host")) {
                    host = datos.getValue();
                } else if (nombre.equals("serverport")) {
                    serverport = Integer.parseInt(datos.getValue());
                } else {
                    GenericParameters.addParameter(nombre,datos.getValue());
                }
            }
            
            icons.loadIcons();
            //AdminLog.setMessage(Language.getWord("LOADING_CF"),
            // ClientConst.MESSAGE);
        }
        catch (FileNotFoundException FNFEe) {

            throw new ConfigFileNotLoadException();
        }
        catch (JDOMException JDOMEe) {
            JDOMEe.printStackTrace();
            throw new ConfigFileNotLoadException();
        }
        catch (IOException IOEe) {
            throw new ConfigFileNotLoadException();
        }
    }

    /**
     * Este metodo retorna el host servidor
     * 
     * @return la ip o el nombre del servidor de transacciones
     */
    public static String getHost() {
        return host;
    }
    
    /**
     * 
     * @return algo
     */
    public static int getServerport() {
        return serverport;
    }
    
    /**
     * 
     * @param newhost
     */
    public static void setHost(String newhost) {
        raiz.getChild("host").setText(newhost);
    }
    
    /**
     * 
     * @param newport
     */
    public static void setPort(int newport) {
        raiz.getChild("serverport").setText(Integer.toString(newport));
    }
}
