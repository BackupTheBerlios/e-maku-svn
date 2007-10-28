package net.emaku.tools.jar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import net.emaku.tools.Run;
import net.emaku.tools.gui.ExportBar;
import net.emaku.tools.gui.FrontEnd;

// This class creates/handles/clean the JAR file within the reports

public class JarManager {

	private static JarFile jarFile = null;
	private static Hashtable<String,ByteArrayOutputStream> entries = new Hashtable<String,ByteArrayOutputStream>();
	private static int total;
	private static String separator = System.getProperty("file.separator");
    private static String output = FrontEnd.outputDir + separator;
	
	public static void init() {
		total = 0;
		try {
			File file = new File(output + separator + "reports.jar");
			if (!file.exists()) {
				String source = Run.root + separator + "lib" + separator + "reports.jar";
				String dest = output + separator + "reports.jar";
				copy(source,dest);
				file = new File(output + separator + "reports.jar");
			}			
			jarFile = new JarFile(file);
			Enumeration<JarEntry> enumeration =  jarFile.entries();
			while (enumeration.hasMoreElements()) {
				total++;
				JarEntry entry = enumeration.nextElement();
				ByteArrayOutputStream ouput = new ByteArrayOutputStream();
				InputStream input = jarFile.getInputStream(entry);
				byte[] buf = new byte[255];
				int data;
				while((data = input.read(buf)) > 0) {
					ouput.write(buf,0,data);
				}
				entries.put(entry.getName(),ouput);
			}
			total-=3;
			jarFile.close();
		} catch (IOException e) {
			clean();
			e.printStackTrace();
		}
	}
	
	private static void copy(String path1, String path2) throws IOException {
		File source = new File(path1);
		File dest = new File(path2);
		FileChannel in = null, out = null;
		try {          
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(dest).getChannel();
			long size = in.size();
			MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
			out.write(buf);
		} finally {
			if (in != null)          in.close();
			if (out != null)     out.close();
		}
	}

	public static boolean clean() {
		File file = new File(output + "reports.jar");	
		if (!file.exists()) {
			return true;
		}
		if (file.exists() && file.canWrite()) {
		    boolean success = file.delete();
		    if (success) {
		    	init();
		    	return true;
		    } 
		}
		return false;
	}
	
	public static void pack(String codeReport, ByteArrayOutputStream bytesReport){
			ExportBar.addRecord("  Adding to the Jar List...");
			entries.put("reports/"+codeReport,bytesReport);
	}
	
	public static void createFile() {
		try {
			ExportBar.addRecord("* Creating Jar file...");
			JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(output + "reports.jar"));
			Enumeration<String> keys = entries.keys();
			// Updating the jar file...
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				JarEntry newEntry  = new JarEntry(key);
				jarOut.putNextEntry(newEntry);
				byte[] buf = entries.get(key).toByteArray();
				jarOut.write( buf, 0, buf.length );
				jarOut.closeEntry();
			}
			
			jarOut.finish();
			jarOut.close();
			init();
			ExportBar.addRecord("* Jar File created successfully");
			ExportBar.addRecord("* Files in Jar File: " + total);
			ExportBar.activeCloseButton();
		}
		catch (IOException e) {
			ExportBar.addRecord("ERROR: " + e);
			e.printStackTrace();
		}
	}
	
	public static int getJarSize() {
		return total;
	}
}
