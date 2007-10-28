package net.emaku.tools;

import java.io.File;
import net.emaku.tools.gui.FrontEnd;

// eMaku RPM Main Class

public class Run {

	public static String root = "";	
	
	public static void main(String[] args) {
		try {
			System.out.println("* Starting eMaku Report Manager...");
			
			// Getting the eMaku RPM home variable from environment
			root = System.getenv("RPM_HOME");
			checkEnvironment(root);
			
			// Check if eMaku is already installed	
			boolean exists = (new File("/usr/local/emaku")).exists();
			if (!exists) {
				System.out.println("ERROR: eMaku is not installed on this system.");
				System.out.println("       You must to install eMaku before try to run this program.");
				System.exit(0);  		
			} 

			// Init GUI
			new FrontEnd(root);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void checkEnvironment(String root) {	
		if(root == null) {
			System.out.println("ERROR: Variable RPM_HOME is unset!");
			System.exit(0);
		}
		File file = new File(root);
		if((root.length()==0) || !(file.exists()) || !(file.isDirectory())) {
			System.out.println("ERROR: Variable RPM_HOME has a wrong value");
			System.exit(0);
		}		
	}
}
