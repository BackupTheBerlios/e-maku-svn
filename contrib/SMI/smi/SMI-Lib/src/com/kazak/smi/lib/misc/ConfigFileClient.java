package com.kazak.smi.lib.misc;

import java.io.File;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.kazak.smi.lib.misc.ClientConst;

public class ConfigFileClient {

    private static SAXBuilder builder;
    private static Document doc;
    private static Element root;
    private static int serverport;
    private static String host;
    private static int time = -1;
    
    public static void loadSettings() { //throws ConfigFileNotLoadException {

    	String os = System.getProperty("os.name");
        String path = ClientConst.unixConfigPath + "smi_client.conf";
        if (os.startsWith("Windows")) {
            path = ClientConst.winConfigPath + "smi_client.conf";
        } 
        
        File proof = new File(path);
        if (!proof.exists()) {
			JLabel label = new JLabel(
					"Ojo! No se encontró el archivo \n"+
					"de configuración.\n" +
					"Contacte al administrador del sistema.");
			label.setHorizontalTextPosition(JLabel.CENTER);
			JOptionPane.showMessageDialog(
					null,
					label,
					"Error al iniciar",
					JOptionPane.ERROR_MESSAGE);
			
			System.exit(0);
        }
    	
        try {
            builder = new SAXBuilder(false);
                        
            doc = builder.build(path);
            root = doc.getRootElement();
            java.util.List Lconfig = root.getChildren();
            Iterator i = Lconfig.iterator();
            while (i.hasNext()) {
                Element data = (Element) i.next();
                String name = data.getName();
                if (name.equals("host")) {
                    host = data.getValue();
                } else if (name.equals("serverport")) {
                    serverport = Integer.parseInt(data.getValue());
                } else if (name.equals("time")) {
                    time = Integer.parseInt(data.getValue());
                }
            }
        }
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
        return serverport;
    }
    
    public static int getTime() {
    	return time;
    }
}