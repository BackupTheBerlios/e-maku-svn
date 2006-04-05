package server.control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.sf.jasperreports.engine.JasperReport;


public class ReportsStore {
	
	private static Hashtable<String,JasperReport>  reports;
	
	public static void Load(URL url) {
		
		reports = new Hashtable<String,JasperReport>();
		try {
			System.out.println("Reportes compilados: " + url);
			JarURLConnection conn = (JarURLConnection)url.openConnection();
			JarFile jarfile = conn.getJarFile();
			Enumeration enumEntries = jarfile.entries();
			enumEntries.nextElement(); // META-INF
			enumEntries.nextElement(); // MANIFEST.MF
			enumEntries.nextElement(); // /reports
			/*  Listing reports */
			while(enumEntries.hasMoreElements()) {
				JarEntry entry = (JarEntry) enumEntries.nextElement();
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
			jarfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static JasperReport getReportClass(String codeReport) {
		return reports.get("reports/"+codeReport);
	}
}