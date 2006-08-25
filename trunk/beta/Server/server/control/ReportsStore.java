package server.control;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.sf.jasperreports.engine.JasperReport;

import common.misc.language.Language;


public class ReportsStore {
	
	private static Hashtable<String,JasperReport>  reports;
	
	public static void Load(URL url) {
		
		reports = new Hashtable<String,JasperReport>();
		try {
			System.out.println(Language.getWord("REPORTS") + ": " + url.openConnection());
			JarURLConnection conn = (JarURLConnection)url.openConnection();
			JarFile jarfile = conn.getJarFile();
			Enumeration enumEntries = jarfile.entries();
			/*  Listing reports */
			while(enumEntries.hasMoreElements()) {
				JarEntry entry = (JarEntry) enumEntries.nextElement();
				String name = entry.getName();
				if (!"META-INF/".equals(name) && 
					!"META-INF/MANIFEST.MF".equals(name) && 
					!"reports/".equals(name)) {
					ObjectInputStream obj = new ObjectInputStream(jarfile.getInputStream(entry));
					JasperReport jasperReport = null;
					try {
						jasperReport = (JasperReport)obj.readObject();
						obj.close();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					reports.put(entry.getName(),jasperReport);
				}
			}
			jarfile.close();
		}
		catch (EOFException e) {
			System.out.println("Exception message " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("Exception message " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static JasperReport getReportClass(String codeReport) {
		return reports.get("reports/"+codeReport);
	}
}