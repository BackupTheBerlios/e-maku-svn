package jmadmin.miscelanea.configuracion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import jmadmin.miscelanea.JMAdminCons;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import jmlib.miscelanea.idiom.Language;
/**
 * ConfigFile.java Creado el 25-jun-2004
 * 
 * Este archivo es parte de JMServerII
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
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
    private static int serverport;
    private static String host;
    
    public static void New(String Host, String Port, String Language, String log) {
        
        doc = new Document();
        doc.setRootElement(new Element("Configuracion"));
        
        doc.getRootElement().addContent(
                new Element("language").setText(Language));
        doc.getRootElement().addContent(
                new Element("host").setText(Host));
        doc.getRootElement().addContent(
                new Element("serverport").setText(Port));
        doc.getRootElement().addContent(
                new Element("log").setText(log));

        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        
        File file = 
            new File(JMAdminCons.HOME,".JMAdmin.conf.xml");

        try {
            FileOutputStream outFile = new FileOutputStream(file);
            out.output(doc, outFile);
            outFile.close();
            ConfigFile.host = Host;
            ConfigFile.serverport = Integer.parseInt(Port);
            idioma.CargarLenguaje(Language);
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
     * @throws ConfigFileNotLoadException
     */
    public static void Cargar() throws ConfigFileNotLoadException{
        try {
            builder = new SAXBuilder(false);
            doc = builder.build(
                                JMAdminCons.HOME+
                                JMAdminCons.SEPARATOR+
                                ".JMAdmin" +
                                JMAdminCons.SEPARATOR+
                                "JMAdmin.conf.xml");
            raiz = doc.getRootElement();
            java.util.List Lconfig = raiz.getChildren();
            Iterator i = Lconfig.iterator();
            
            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML, en este
             * caso Configuración
             */
            
            while (i.hasNext()) {
                
                Element datos = (Element)i.next();
                if (datos.getName().equals("lenguaje")) {
                    idioma.CargarLenguaje(datos.getValue());
                } /*else if (datos.getName().equals("Log")) {
                    new AdminLog(datos.getValue());
                }*/ else if (datos.getName().equals("host")) {
                    host = datos.getValue();
                } else if (datos.getName().equals("serverport")) {
                    serverport = Integer.parseInt(datos.getValue());
                } 
            }
            //AdminLog.setMessage(Language.getWord("LOADING_CF"), JMClientCons.MESSAGE);
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
    
    public static String getHost() {
        return host;
    }
    
    public static int getServerport() {
        return serverport;
    }
    
    public static void setHost(String newhost){
        raiz.getChild("host").setText(newhost);
    }
    
    public static void setPort(int newport){
        raiz.getChild("serverport").setText(Integer.toString(newport));
    }
}
    
