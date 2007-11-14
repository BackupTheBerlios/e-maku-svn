package client.misc.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
    private static Element root;
    private static Language lang = new Language();
    private static int serverport;
    private static String host;
    private static String language;
    private static String logMode;
    private static String jarDirectory;
    //private static String classLookAndFeel;
    //private static String URLJarLookAndFeel;
    private static String lookAndFeel;
    private static String cash;
    private static ArrayList<Element> company;
    /**
     * Este metodo sirve para crear un nuevo archivo de configuracion
     * 
     * @param host
     *            Direccion ip o nombre de la maquina servidor
     * @param port
     *            puerto del servidor
     * @param language
     *            idioma
     * @param log
     *            tipo de log a generar
     */
    public static void buildNewFile(String host, String port, String language, String log,String cash) {
        
        
        Element rootNode = new Element("configuration");
        doc = new Document(rootNode);
        
        rootNode.addContent(new Element("language").setText(language));
        rootNode.addContent(new Element("host").setText(host));
        rootNode.addContent(new Element("serverport").setText(port));
        rootNode.addContent(new Element("log").setText(log));
        rootNode.addContent(new Element("cash").setText(cash));
        rootNode.addContent(new Element("classLookAndFeel").setText("default"));
        //rootNode.addContent(new Element("jarLookAndFeel").setText(URLJarLookAndFeel));

        /*
        Element company = new Element("Company");
        company.addContent(new Element("name").setText("mi_empresa"));
        company.addContent(new Element("jarFile").setText("mi_empresa.jar"));
        company.addContent(new Element("directory").setText("mi_empresa"));
        */
        for(Element element:company) {
        	rootNode.addContent(element);
        }
        
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        
        File file = new File(ClientConstants.CONF);//,"client.conf");

        try {
        	if (!file.exists()) {
        		file.mkdir();
        	}
        	
        	if (file.isDirectory()) {
        		file = new File(ClientConstants.CONF,"client.conf");
        		FileOutputStream outFile = new FileOutputStream(file);
        		out.output(doc, outFile);
        		outFile.close();
        		ConfigFileHandler.host = host;
        		ConfigFileHandler.serverport = Integer.parseInt(port);
        		lang.loadLanguage(language);
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
            company = new ArrayList<Element>();
            builder = new SAXBuilder(false);
            String path = ClientConstants.CONF + "client.conf";
            File file = new File(path);
            doc = builder.build(file);
            root = doc.getRootElement();
            List configList = root.getChildren();
            Iterator i = configList.iterator();

            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML,
             * en este caso Configuración
             */

            int counter = 0;
            Vector<String> parameters = new Vector<String>();
            while (i.hasNext()) {
                Element data = (Element) i.next();
                String name = data.getName(); 
                if (name.equals("language")) {
                	language = data.getValue();
                	parameters.add("language");
                	counter++;
                } 
                else if (name.equals("host")) {
                    host = data.getValue();
                	parameters.add("host");
                	counter++;
                } else if (name.equals("serverport")) {
                    serverport = Integer.parseInt(data.getValue());
                	parameters.add("serverport");
                	counter++;
                } else if (name.equals("log")) {
                	logMode = data.getValue();
                	parameters.add("log");
                	counter++;
                } 
                else if (name.equals("lookAndFeel")) {
                    lookAndFeel = data.getValue();
                    parameters.add("lookAndFeel");
                	counter++;
	            }
                /* else if (name.equals("jarLookAndFeel")) {
                    URLJarLookAndFeel = data.getValue();
	            } */
                else if (name.equals("cash")) {
                	cash = data.getValue();
                	parameters.add("cash");
                	counter++;
                }
                else if (name.equals("company")) {
                	company.add((Element)data.clone());
                	parameters.add("company");
                	counter++;
                }
                EmakuParametersStructure.addParameter(name,data.getValue());
            }
            
            if(counter < 7) {
            	System.out.println("ERROR: The config file (client.conf) is incorrect or incomplete.");
            	System.out.println("       Please, check and fix the tags missing.");
            	System.exit(0);
            }
           
            lang.loadLanguage(language);
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
    	Iterator i = root.getChildren("company").iterator();
        boolean isCompany = false;
        String jarFile = null;
        String directory = "";
        
        while (i.hasNext() && !isCompany) {
            Element data = (Element) i.next();
            Iterator j = data.getChildren().iterator();
            jarFile="";
            directory="";
            while (j.hasNext()) {
                Element config = (Element) j.next();
                String name = config.getName();
                String value = config.getValue();
	            if (name.equals("name") && value.trim().equals(nameCompany.trim())) {
	            	isCompany = true;
	            } 
	            if (name.equals("jarFile")) {
	            	jarFile = config.getValue();
	            } 
	            if (name.equals("directory")) {
	            	directory = config.getValue();
	            } 
            }
        }

		String jar = "jar:file:"+System.getenv("EMAKU_HOME")+"/lib/emaku/"+jarFile+"!/";

		jarDirectory = jar+directory;
        EmakuParametersStructure.setJarDirectoryTemplates(jarDirectory+"/printer-templates");

		lang.loadLanguage(jarDirectory+"/misc",language);
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
    
    public static String getLanguage() {
        return language;
    }

    public static String getLogMode() {
        return logMode;
    }
   
    public static ArrayList getCompany() {
    	return company;
    }
    
    public static String getCash() {
    	return cash;
    }
    
    /*
    public static String getClassLookAndFeel() {
    	return classLookAndFeel;
    }*/
    
    public static String getLookAndFeel() {
    	return lookAndFeel;
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
        root.getChild("host").setText(newhost);
    }
    
    /**
     * 
     * @param newport
     */
    public static void setPort(int newport) {
        root.getChild("serverport").setText(Integer.toString(newport));
    }

	public static String getJarDirectory() {
		return jarDirectory;
	}

	/*
	public static String getClassForLookAndFeel() {
		return classLookAndFeel;
	}
	
	public static String getURLJarForLookAndFeel() {
		return URLJarLookAndFeel;
	}*/
}
