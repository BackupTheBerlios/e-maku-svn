package com.kazak.smi.lib.misc;

import java.awt.Toolkit;

public interface ClientConstants {

	public final String KeyClient = "client";
	public final String winConfigPath = "C:\\GAMBLE\\smiclient\\";
	public final String unixConfigPath = "/etc/";
	public final String HOME = System.getProperty("user.home");
    public final String SEPARATOR = System.getProperty("file.separator");
    public final String TMP = System.getProperty("java.io.tmpdir");
	public final String CONF = HOME+SEPARATOR+".smi"+SEPARATOR;
	public final String iconsPath = "/icons/";

    public final int ERROR = 0;
    public final int WARNING = 1;
    public final int MESSAGE = 2;
    public final long MAX_SIZE_FILE_LOG = 5242880;
    public static int MAX_WIN_SIZE_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    public static int MAX_WIN_SIZE_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
}