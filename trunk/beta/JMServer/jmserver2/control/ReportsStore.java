package jmserver2.control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jmserver2.miscelanea.JMServerIICons;
import net.sf.jasperreports.engine.JasperReport;

public class ReportsStore {
	
	private static Hashtable<String,JasperReport>  reports;
	
	public static void Load() {
		reports = new Hashtable<String,JasperReport>();
		try {
			System.out.println("Reportes compilados: " + JMServerIICons.JM_HOME+"/lib/midas/reports.jar");
			JarFile jarfile = new JarFile(JMServerIICons.JM_HOME+"/lib/midas/reports.jar");
			
			Enumeration enumEntries = jarfile.entries();
			enumEntries.nextElement();
			enumEntries.nextElement();
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static JasperReport getReportClass(String codeReport) {
		return reports.get(codeReport);
	}
}