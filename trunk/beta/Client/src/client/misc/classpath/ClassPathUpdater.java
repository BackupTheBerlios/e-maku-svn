package client.misc.classpath;

import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

// This class updates the CLASSPATH value at runtime adding new Jar files

public class ClassPathUpdater {

	private static final Class[] parameters = new Class[]{URL.class};
	private static String mainClass = "";

	public static void addFile(String s) throws IOException {
		String path = "jar:file://" + s + "!/" ;
		mainClass = getMainClassString(s);
		URL url = new URL(path);
		addURL(url);
	}
	
	public static String getMainClassString(String path)  {
		String className = "";
		try {
			JarFile jar = new JarFile(path);
			Manifest manifest = jar.getManifest();
			Attributes data = manifest.getMainAttributes();
			className = data.getValue("Main-Class");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return className;
	}
	
	public static String getMainClass()  {
		return mainClass;
	}

	public static void addURL(URL u) throws IOException {
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			method.invoke(sysloader,new Object[]{ u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}

	}

}
