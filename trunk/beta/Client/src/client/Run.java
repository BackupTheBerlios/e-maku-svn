package client;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import bsh.Interpreter;
import client.gui.components.MainWindow;
import client.gui.forms.Connection;
import client.gui.forms.SettingsDialog;
import client.gui.forms.Splash;
import client.misc.ClientConstants;
import client.misc.classpath.ClassPathUpdater;
import client.misc.settings.ConfigFileHandler;
import client.misc.settings.ConfigFileNotLoadException;

import common.comunications.SocketConnector;
import common.misc.language.Language;

/**
 * Run.java Creado el 29-jul-2004
 * 
 * Este archivo es parte de JMClient <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * Client es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * eMaku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de iniciar el cliente E-maku
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class Run {

	public static final Interpreter shellScript = new Interpreter();

	public static void main(String[] args) {
		
		if (ClientConstants.EMAKU_HOME == null) {
			String locale = getLocale();
			if (locale.equals("es_CO")) {
				System.out.println("ERROR: La variable EMAKU_HOME no esta definida! Por favor, asignele un valor!");
			} else if (locale.equals("pt_PT")) {
				System.out.println("ERROR: Variável EMAKU_HOME é indefinida! Por favor, designe-o um valor!");
			} else {
				System.out.println("ERROR: Variable EMAKU_HOME is undefined! Please, set it up!");
			}
			
			return;
		}
		
		initLocale();

		for(int i=0;i<args.length;i++) {
			if(args[i].equals("-r") || args[i].equals("--reconfigure")) {
				String path = ClientConstants.CONF + "client.conf";
            	File file = new File(path);
				if(file.exists()) {
					SettingsDialog dialog = new SettingsDialog(null,SettingsDialog.EDIT);
					dialog.pack();
					dialog.setLocation(
	                			(ClientConstants.MAX_WIN_SIZE_WIDTH / 2) - dialog.getWidth() / 2,
	                			(ClientConstants.MAX_WIN_SIZE_HEIGHT / 2) - dialog.getHeight() / 2);
					dialog.setVisible(true);
				} 
			} else {
				if(args[i].equals("-h") || args[i].equals("--help")) {
					System.out.println(" " + Language.getWord("CLIENT_VER"));
					System.out.println(" " + Language.getWord("OPT"));
					System.out.println(" " + Language.getWord("RECONFIG"));
					System.out.println(" " + Language.getWord("HELP"));
					System.out.println();
					System.exit(0);
				}
			}
		}
		
		try {
			ConfigFileHandler.loadSettings();
			setLookAndFeel();
			Splash.create();
			new Connection();
		}

		catch (ConfigFileNotLoadException e) {
			SettingsDialog dialog = new SettingsDialog(null,SettingsDialog.CREATE);
			dialog.pack();
			dialog.setLocation(
	                (ClientConstants.MAX_WIN_SIZE_WIDTH / 2) - dialog.getWidth() / 2,
	                (ClientConstants.MAX_WIN_SIZE_HEIGHT / 2) - dialog.getHeight() / 2);
			dialog.setVisible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void setLookAndFeel() {
		try {
			String theme = ConfigFileHandler.getLookAndFeel();
			String jarPath = ClientConstants.THEMES + theme + ".jar";
			File file = new File(jarPath);
			
			if (!file.exists()) {
				System.out.println(Language.getWord("THEME_ERROR1") + " " + jarPath + " " + Language.getWord("THEME_ERROR2"));
				System.out.println(Language.getWord("THEME_ERROR3"));
			} else {
				ClassPathUpdater.addFile(jarPath);
				String lookAndFeel = ClassPathUpdater.getMainClass();
				System.out.println(Language.getWord("LOOK1") + lookAndFeel);
				System.out.println(Language.getWord("LOOK2") + jarPath);
				UIManager.setLookAndFeel(lookAndFeel);
			}
			
			Font f = new Font("Tahoma", Font.PLAIN, 12);
			Font f2 = new Font("Tahoma",Font.BOLD,14);
			UIManager.put("Menu.font",			f);
			UIManager.put("MenuItem.font",		f);
			UIManager.put("Button.font",		f);
			UIManager.put("Label.font",			f);
			UIManager.put("TextField.font",		f);
			UIManager.put("ComboBox.font",		f);
			UIManager.put("CheckBox.font",		f);
			UIManager.put("TextPane.font",		f);
			UIManager.put("TextArea.font",		f);
			UIManager.put("List.font",			f);
			UIManager.put("Slider.font",		f);
			UIManager.put("TitledBorder.font",	f2);
			UIManager.put("RadioButton.font",	f);
			UIManager.put("InternalFrame.font",	f2);
			UIManager.put("Table.font",			f);
			UIManager.put("TabbedPane.font",	f);
			UIManager.put("DesktopColor.color",	Color.GRAY);		
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();		
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}	
	}
	
	private static String getLocale() {
		return Locale.getDefault().toString();
	}
	
	private static void initLocale() {
		String locale = getLocale();
		Language lang = new Language();
		lang.loadLanguage(ClientConstants.LANG,locale);
	}
	
	public static void exit() {
		int confirm = -1;
		try {
			confirm=JOptionPane.showConfirmDialog(
					MainWindow.getFrame(),
					Language.getWord("CLOSE_CURRENT_APP"),
					"",JOptionPane.YES_NO_OPTION);
			if(confirm == JOptionPane.YES_OPTION){
				SocketConnector.getSock().close();
			}
		}
		catch (NullPointerException e) {}
		catch (IOException e) {}
		finally {
			if (confirm == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}
}
