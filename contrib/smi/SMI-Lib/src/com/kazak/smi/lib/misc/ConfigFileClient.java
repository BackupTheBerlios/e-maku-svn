package com.kazak.smi.lib.misc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class ConfigFileClient {

    private static SAXBuilder builder;
    private static Document doc;
    private static Element raiz;
    private static int serverport;
    private static String host;
    private static int time = -1;
    
    public static void loadSettings() throws ConfigFileNotLoadException {
        try {
            builder = new SAXBuilder(false);
            doc = builder.build("/etc/smi_client.conf");
            raiz = doc.getRootElement();
            java.util.List Lconfig = raiz.getChildren();
            Iterator i = Lconfig.iterator();
            while (i.hasNext()) {
                Element datos = (Element) i.next();
                String nombre = datos.getName();
                if (nombre.equals("host")) {
                    host = datos.getValue();
                } else if (nombre.equals("serverport")) {
                    serverport = Integer.parseInt(datos.getValue());
                } else if (nombre.equals("time")) {
                    time = Integer.parseInt(datos.getValue());
                }
            }
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
   
    public static int getServerPort() {
        return serverport;
    }
    
    public static int getTime() {
    	return time;
    }
}