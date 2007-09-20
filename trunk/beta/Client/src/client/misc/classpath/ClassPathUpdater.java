package client.misc.classpath;

import java.lang.reflect.*;
import java.io.*;
import java.net.*;

// This class updates the CLASSPATH value at runtime adding new Jar files

public class ClassPathUpdater {

	private static final Class[] parameters = new Class[]{URL.class};

	public static void addFile(String s) throws IOException {
		String path = "jar:file://" + s + "!/" ;
		URL url = new URL(path);
		addURL(url);
	}

	public static void addFile(File f) throws IOException {
		String path = "jar:file://" + f.getAbsolutePath() + "!/" ;
		URL url = new URL(path);
		addURL(url);
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
