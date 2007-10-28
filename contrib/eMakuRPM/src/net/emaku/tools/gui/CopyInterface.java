package net.emaku.tools.gui;

// This class handles the copy/paste function for the Templates Editor

public class CopyInterface {
	private static boolean copyOn = false;
	public static String selection = "";
	
	public static void setCopyState(boolean flag) {
		copyOn = flag;
	}
	
	public static boolean isCopyOn() {
		return copyOn;
	}

	public static void setCopyText(String text) {
		selection = text;
	}
	
	public static String getCopyText() {
		return selection;
	}
		
}
