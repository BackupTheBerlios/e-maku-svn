package com.kazak.smi.server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.kazak.smi.lib.misc.Language;
import com.kazak.smi.server.businessrules.Sync;
import com.kazak.smi.server.comunications.SocketServer;
import com.kazak.smi.server.control.Pop3Handler;
import com.kazak.smi.server.control.TransactionsCache;
import com.kazak.smi.server.database.connection.ConnectionsPool;
import com.kazak.smi.server.database.connection.PoolNotLoadException;
import com.kazak.smi.server.database.sql.CacheEnlace;
import com.kazak.smi.server.misc.LogWriter;
import com.kazak.smi.server.misc.ServerConst;
import com.kazak.smi.server.misc.settings.ConfigFile;
import com.kazak.smi.server.misc.settings.ConfigFileNotLoadException;

public class Run {

	public Run() {
		
		String smiConfigFile = ServerConst.CONF + ServerConst.SEPARATOR + "server.conf";
		boolean existsConfigFile = (new File(smiConfigFile)).exists();
		new LogWriter();
		if (!existsConfigFile) {
			LogWriter.write("ERROR: Archivo de configuracion no encontrado...");
			LogWriter.write("ERROR: Debe reinstalar la aplicación...");
			killServer();
		}
		try {	
			ConfigFile.loadConfigFile(smiConfigFile);
			ConnectionsPool.loadDB();
			new CacheEnlace();
			TransactionsCache.loadCache();
			new Pop3Handler();
			new Sync();
			Thread t = new Thread() {
				public void run() {
					try {
						new SocketServer();
					} catch (IOException IOEe) {
			            LogWriter.write(
			            		Language.getWord("UNLOADING_ST") + ", "+
			            		IOEe.getMessage());
			            killServer();
			        }
				}
			};
			t.start();
		} catch (ConfigFileNotLoadException e) {
    		LogWriter.write(
    				"ERROR: El archivo de configuracion estaba corrupto.\n" +
    				"Por favor verifique el archivo  " +smiConfigFile+" y reinicie el servidor");
        	killServer();
        } catch (PoolNotLoadException e) {
        	LogWriter.write(
        			e.getErrorCode()+","+
        			e.getMessage()+", "+
        			Language.getWord("NODEBUG"));
			killServer();
		} 
	}
	
	public static void killServer() {
		try {
			ConnectionsPool.getConnection(ConfigFile.getMainDataBase()).close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {	    
		new Run();
	}
}