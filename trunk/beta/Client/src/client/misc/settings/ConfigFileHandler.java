package client.misc.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import client.gui.forms.Splash;
import client.misc.ClientConstants;

import common.comunications.SocketConnector;
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
	private static String lookAndFeel;
	private static String cash;
	private static ArrayList<Element> companies;
	
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
	public static void buildNewFile(String host, String port, String language, String log, String cash, String theme, Vector<Element> companiesVector) {

		Element rootNode = new Element("configuration");
		doc = new Document(rootNode);

		rootNode.addContent(new Element("language").setText(language));
		rootNode.addContent(new Element("host").setText(host));
		rootNode.addContent(new Element("serverport").setText(port));
		rootNode.addContent(new Element("log").setText(log));
		rootNode.addContent(new Element("cash").setText(cash));
		rootNode.addContent(new Element("lookAndFeel").setText(theme));

		for(Element element:companiesVector) {
			rootNode.addContent(element);
		} 

		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());

		File file = new File(ClientConstants.CONF);

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
			companies = new ArrayList<Element>();
			builder = new SAXBuilder(false);
			String path = ClientConstants.CONF + "client.conf";
			System.out.println("path: "+path);
			if (ClientConstants.WEBSTART) {
				doc = builder.build(new URL(path));
				root = doc.getRootElement();
			}
			else {
				File file = new File(path);
				doc = builder.build(file);
				root = doc.getRootElement();
			}
			List<Element> configList = root.getChildren();
			Iterator<Element> i = configList.iterator();

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
				else if (name.equals("cash")) {
					cash = data.getValue();
					parameters.add("cash");
					counter++;
				}
				else if (name.equals("company")) {
					companies.add((Element)data.clone());
					parameters.add("company");
					counter++;
				}
				EmakuParametersStructure.addParameter(name,data.getValue());
			}

			if(counter < 7) {
				System.out.println(Language.getWord("MISS_CONFIG"));
				System.out.println(Language.getWord("MISS_CONFIG2"));
				System.exit(0);
			}

			lang.loadLanguage(language);
		}
		catch (FileNotFoundException FNFEe) {
			FNFEe.printStackTrace();
			throw new ConfigFileNotLoadException();
		}
		catch (JDOMException JDOMEe) {
			JDOMEe.printStackTrace();
			throw new ConfigFileNotLoadException();
		}
		catch (IOException IOEe) {
			IOEe.printStackTrace();
			throw new ConfigFileNotLoadException();
		}
	}

	public static void loadJarFile(String nameCompany) {
		Iterator<Element> i = root.getChildren("company").iterator();
		boolean isCompany = false;
		String jarFile = null;
		String directory = "";

		while (i.hasNext() && !isCompany) {
			Element data = (Element) i.next();
			Iterator<Element> j = data.getChildren().iterator();
			jarFile = "";
			directory = "";
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
		
		String jarBytes = ClientConstants.COMPANIES + jarFile;
		File file = new File(jarBytes);
		if (!file.exists() && !ClientConstants.WEBSTART) {
			Splash.hide();
    		JOptionPane.showMessageDialog(new JFrame(),Language.getWord("JAR_ERROR1") 
			+ " \"" + nameCompany + "\"\n" + Language.getWord("JAR_ERROR2") + " " + ClientConstants.COMPANIES 
			+ "\n" + Language.getWord("JAR_ERROR3") + " \"" + jarFile + "\" " + Language.getWord("JAR_ERROR4") 
			+ "\n" + Language.getWord("JAR_ERROR5"),Language.getWord("ERROR"), JOptionPane.ERROR_MESSAGE);
    		try {
				SocketConnector.getSock().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		} else {
			String jar = null;
			if (ClientConstants.WEBSTART) {
				jar="jar:" + jarBytes + "!/";
			}
			else {
				jar="jar:file:" + jarBytes + "!/";
			}
			jarDirectory = jar+directory;
			EmakuParametersStructure.setJarDirectoryTemplates(jarDirectory + "/printer-templates");
			EmakuParametersStructure.addParameter("jarFile",jarFile);
			EmakuParametersStructure.addParameter("jarDirectory",directory);
			EmakuParametersStructure.addParameter("jarPath",jarDirectory);
			lang.loadLanguage(jarDirectory + "/lang/",language);
			// Loading icons
			new Icons().loadIcons(jarDirectory + "/conf");
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

	public static String getLanguage() {
		return language;
	}

	public static String getLogMode() {
		return logMode;
	}

	public static ArrayList<Element> getCompanies() {
		return companies;
	}

	public static String getCash() {
		return cash;
	}

	public static String getLookAndFeel() {
		return lookAndFeel;
	}
	/**
	 * 
	 * @return serverport
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

}
