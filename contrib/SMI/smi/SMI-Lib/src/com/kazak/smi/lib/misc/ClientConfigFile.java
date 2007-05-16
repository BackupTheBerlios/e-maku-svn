package com.kazak.smi.lib.misc;

import java.io.File;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.kazak.smi.lib.misc.ClientConstants;

public class ClientConfigFile {

    private static SAXBuilder saxBuilder;
    private static Document doc;
    private static Element root;
    private static int serverPort;
    private static String host;
    private static int time = -1;
    
    public static void loadSettings() { //throws ConfigFileNotLoadException {

    	String os = System.getProperty("os.name");
        String path = ClientConstants.unixConfigPath + "smi_client.conf";
        if (os.startsWith("Windows")) {
            path = ClientConstants.winConfigPath + "smi_client.conf";
        } 
        
        File configFile = new File(path);
        if (!configFile.exists()) {
			JOptionPane.showMessageDialog(
					null,
					"No se encontró el archivo de configuración.\n" +
					"Por favor, Contacte al administrador del sistema.",
					"Error del Sistema",
					JOptionPane.ERROR_MESSAGE);
			
			System.exit(0);
        }
    	
        try {
            saxBuilder = new SAXBuilder(false);
                        
            doc = saxBuilder.build(path);
            root = doc.getRootElement();
            List configList = root.getChildren();
            Iterator iterator = configList.iterator();
            while (iterator.hasNext()) {
                Element data = (Element) iterator.next();
                String name = data.getName();
                if (name.equals("host")) {
                    host = data.getValue();
                } else if (name.equals("serverport")) {
                    serverPort = Integer.parseInt(data.getValue());
                } else if (name.equals("time")) {
                    time = Integer.parseInt(data.getValue());
                }
            }
        }
        // TODO: Corregir el manejo de excepciones aqui / Precisar mensaje de error
        /* catch (FileNotFoundException FNFEe) {
            throw new ConfigFileNotLoadException();
        } */
        catch (JDOMException JDOMEe) {
            JDOMEe.printStackTrace();
            //throw new ConfigFileNotLoadException();
        }
        catch (IOException IOEe) {
            //throw new ConfigFileNotLoadException();
        }
    }

    public static String getHost() {
        return host;
    }
   
    public static int getServerPort() {
        return serverPort;
    }
    
    public static int getTime() {
    	return time;
    }
}