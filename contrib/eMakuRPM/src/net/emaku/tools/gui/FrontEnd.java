package net.emaku.tools.gui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.util.Properties;
import java.io.FileInputStream;

import net.emaku.tools.db.DataBaseManager;
import net.emaku.tools.gui.ReportManagerGUI;

// This class initializes the eMaku RPM Gui

public class FrontEnd {

	public static String outputDir = ""; 
	private static String separator = System.getProperty("file.separator");
	public static String confPath = separator + "conf" + separator + "rpm.conf";

	public FrontEnd(String root) {
		try {
			Properties properties = loadConfigFile(root);
			outputDir =  properties.getProperty("output");
			setLookAndFeel(properties.getProperty("theme"));
			DataBaseManager.loadDBProperties(properties);
			DataBaseManager.connect();
			new ReportManagerGUI(root + separator + "root_reports");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Properties loadConfigFile(String root) {
		Properties properties = null;
		try {	
			properties = new Properties();
			properties.load(new FileInputStream(root + confPath));
			checkProperties(properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return properties;
	}
	
	public static void checkProperties(Properties properties) {
		String args[] = {"theme","output","driver","url","user"};
		for(int i=0; i<args.length; i++) {
			String var = properties.getProperty(args[i]);
			if ((var == null) || (var.length()==0)) {
				System.out.println("ERROR: The configure file (rpm.conf) is misconfigured!");
				System.out.println("       Variable " + args[i] + " is undefined.");
				System.exit(0);  
			}
		}
	}
	
	private static void setLookAndFeel(String look) {		
		if (look!=null && !"".equals(look)) {
			try {
				UIManager.setLookAndFeel(look); 
			} catch (ClassNotFoundException e) {
				System.out.println("Error!!!");
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
        Font f = new Font("Tahoma", Font.PLAIN, 12);
        Font f2 = new Font("Tahoma",Font.BOLD,14);
        UIManager.put("Menu.font",                      f);
        UIManager.put("MenuItem.font",          f);
        UIManager.put("Button.font",            f);
        UIManager.put("Label.font",                     f);
        UIManager.put("TextField.font",         f);
        UIManager.put("ComboBox.font",          f);
        UIManager.put("CheckBox.font",          f);
        UIManager.put("TextPane.font",          f);
        UIManager.put("TextArea.font",          f);
        UIManager.put("List.font",                      f);
        UIManager.put("Slider.font",            f);
        UIManager.put("TitledBorder.font",      f2);
        UIManager.put("RadioButton.font",       f);
        UIManager.put("InternalFrame.font",     f2);
        UIManager.put("Table.font",                     f);
        UIManager.put("TabbedPane.font",        f);
        UIManager.put("DesktopColor.color",     Color.GRAY);
	}
}
