package client.misc.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import client.misc.ClientConstants;

import common.misc.Icons;
import common.misc.language.Language;
import common.misc.parameters.EmakuParametersStructure;
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
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda </A>
 */
public class ConfigFileHandler extends EmakuParametersStructure {

    private static SAXBuilder builder;
    private static Document doc;
    private static Element raiz;
    private static Language idioma = new Language();
    private static int serverport;
    private static String host;
    private static String language;
    private static String logMode;
    private static Hashtable<String,String> boxID = new Hashtable<String,String>();
    private static String jarDirectory;
    private static String classLookAndFeel;
    private static String URLJarLookAndFeel;
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
        
        
        Element rootNode = new Element("Configuration");
        doc = new Document(rootNode);
        
        rootNode.addContent(new Element("language").setText(Language));
        rootNode.addContent(new Element("host").setText(Host));
        rootNode.addContent(new Element("serverport").setText(Port));
        rootNode.addContent(new Element("log").setText(log));
        rootNode.addContent(new Element("cash").setText(cash));
        rootNode.addContent(new Element("classLookAndFeel"));
        rootNode.addContent(new Element("jarLookAndFeel"));
        
        Element company = new Element("Company");
        company.addContent(new Element("name").setText("mi_empresa"));
        company.addContent(new Element("jarFile").setText("mi_empresa.jar"));
        company.addContent(new Element("directory").setText("mi_empresa"));
        
        rootNode.addContent(company);
        
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        
        File file = 
            new File(ClientConstants.CONF);//,"client.conf");

        try {
        	if (!file.exists()) {
        		file.mkdir();
        	}
        	
        	if (file.isDirectory()) {
        		file = new File(ClientConstants.CONF,"client.conf");
        		FileOutputStream outFile = new FileOutputStream(file);
        		out.output(doc, outFile);
        		outFile.close();
        		ConfigFileHandler.host = Host;
        		ConfigFileHandler.serverport = Integer.parseInt(Port);
        		idioma.loadLanguage(Language);
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
    public static void loadSettings() throws ConfigFileNotLoadException {
        try {
            
            builder = new SAXBuilder(false);
            doc = builder.build(new File(ClientConstants.CONF+"client.conf"));
            raiz = doc.getRootElement();
            List Lconfig = raiz.getChildren();
            Iterator i = Lconfig.iterator();

            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML,
             * en este caso Configuración
             */

            while (i.hasNext()) {

                Element datos = (Element) i.next();
                String nombre = datos.getName(); 
                if (nombre.equals("language")) {
                	language = datos.getValue();
                } 
                else if (nombre.equals("host")) {
                    host = datos.getValue();
                } else if (nombre.equals("serverport")) {
                    serverport = Integer.parseInt(datos.getValue());
                } else if (nombre.equals("log")) {
                	logMode = datos.getValue();
                } else if (nombre.equals("cash")) {
                    boxID.put(datos.getValue(),"");
	            }
                else if (nombre.equals("classLookAndFeel")) {
                    classLookAndFeel = datos.getValue();
	            }
                else if (nombre.equals("jarLookAndFeel")) {
                    URLJarLookAndFeel = datos.getValue();
	            }
                EmakuParametersStructure.addParameter(nombre,datos.getValue());
            }
            
            idioma.loadLanguage(language);
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

    public static void loadJarFile(String nameCompany) {
    	Iterator i = raiz.getChildren("Company").iterator();
        

        boolean isCompany = false;
        String jarFile = null;
        String directory = "";
        
        while (i.hasNext() && !isCompany) {
            Element datos = (Element) i.next();
            Iterator j = datos.getChildren().iterator();
            jarFile="";
            directory="";
            while (j.hasNext()) {
                Element config = (Element) j.next();
                String nombre = config.getName();
                String value = config.getValue();
	            if (nombre.equals("name") && value.trim().equals(nameCompany.trim())) {
	            	isCompany = true;
	            } 
	            if (nombre.equals("jarFile")) {
	            	jarFile = config.getValue();
	            } 
	            if (nombre.equals("directory")) {
	            	directory = config.getValue();
	            } 
            }
        }

		String jar  = null;
		if (System.getProperty("os.name").equals("LINUX")) {
			jar="jar:file:/usr/local/emaku/lib/emaku/"+jarFile+"!/";
		}
		else {
			jar="jar:file:C:/ARCHIV~1/emaku/lib/emaku/"+jarFile+"!/";
		}
		jarDirectory = jar+directory;
        EmakuParametersStructure.setJarDirectoryTemplates(jarDirectory+"/printer-templates");

		idioma.loadLanguage(jarDirectory+"/misc",language);
        /*
         * Cargando iconos
         */
        new Icons().loadIcons(jarDirectory+"/misc");
    }
    /**
     * Este metodo retorna el host servidor
     * 
     * @return la ip o el nombre del servidor de transacciones
     */
    public static String getHost() {
        return host;
    }
    
    public static Hashtable<String,String> getBoxID() {
        return boxID;
    }
    
    public static String getLanguage() {
        return language;
    }

    public static String getLogMode() {
        return logMode;
    }
   
    /**
     * 
     * @return algo
     */
    public static int getServerPort() {
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

	public static String getJarDirectory() {
		return jarDirectory;
	}

	public static String getClassForLookAndFeel() {
		return classLookAndFeel;
	}
	
	public static String getURLJarForLookAndFeel() {
		return URLJarLookAndFeel;
	}
}
