package com.kazak.comeet.server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.kazak.comeet.lib.misc.Language;
import com.kazak.comeet.server.businessrules.SyncManager;
import com.kazak.comeet.server.comunications.SocketServer;
import com.kazak.comeet.server.control.Pop3Handler;
import com.kazak.comeet.server.control.TransactionsCache;
import com.kazak.comeet.server.database.connection.ConnectionsPool;
import com.kazak.comeet.server.database.connection.PoolNotLoadException;
import com.kazak.comeet.server.database.sql.CacheLoader;
import com.kazak.comeet.server.misc.LogWriter;
import com.kazak.comeet.server.misc.ServerConstants;
import com.kazak.comeet.server.misc.settings.ConfigFileHandler;
import com.kazak.comeet.server.misc.settings.ConfigFileNotLoadException;

public class Run {

	public Run() {		
		String comeetConfigFile = ServerConstants.CONF + ServerConstants.SEPARATOR + "server.conf";
		boolean existsConfigFile = (new File(comeetConfigFile)).exists();
		new LogWriter();
		if (!existsConfigFile) {
			LogWriter.write("ERROR: Archivo de configuracion no encontrado -> [ " + comeetConfigFile + " ]");
			shutDownServer();
		}
		try {	
			ConfigFileHandler.loadConfigFile(comeetConfigFile);
			ConnectionsPool.loadDB();
			new CacheLoader();
			TransactionsCache.loadCache();
			new Pop3Handler();
			new SyncManager();
			Thread t = new Thread() {
				public void run() {
					try {
						new SocketServer();
					} catch (IOException IOEe) {
			            LogWriter.write(
			            		Language.getWord("UNLOADING_ST") + " " +
			            		IOEe.getMessage());
			            shutDownServer();
			        }
				}
			};
			t.start();
		} catch (ConfigFileNotLoadException e) {
    		LogWriter.write(
    				"ERROR: El archivo de configuracion estaba corrupto.\n" +
    				"Por favor verifique el archivo  " +comeetConfigFile+" y reinicie el servidor");
        	shutDownServer();
        } catch (PoolNotLoadException e) {
        	LogWriter.write(
        			//e.getErrorCode()+","+
				"ERROR: "+
        			e.getMessage()+", "+
        			Language.getWord("NODEBUG"));
			shutDownServer();
		} 
	}
	
	public static void shutDownServer() {
		try {
			ConnectionsPool.getConnection(ConfigFileHandler.getMainDataBase()).close();
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
