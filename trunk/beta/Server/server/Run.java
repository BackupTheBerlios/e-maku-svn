package server;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import server.comunications.SocketServer;
import server.control.ReportsStore;
import server.database.connection.PoolConexiones;
import server.database.connection.PoolNotLoadException;
import server.database.sql.LinkingCache;
import server.database.sql.SQLBadArgumentsException;
import server.misc.ServerConst;
import server.misc.settings.ConfigFile;
import server.misc.settings.ConfigFileNotLoadException;
import server.reports.MakeReport;

import common.misc.formulas.BeanShell;
import common.misc.language.Language;
import common.misc.log.LogAdmin;

/**
 * Run.java Creado el 28-jun-2004
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de iniciar el Servidor de Transacciones
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class Run {

	/**
	 * 
	 */
	public Run() {
		
		String os = System.getProperty("os.name");
		
		if (os.equals("Linux")) {
			String owner = System.getProperty("user.name");
			if (owner.equals("root")) {			
				System.out.println("*** Excepción de Seguridad: El Servidor de Transacciones no puede ser");
				System.out.println("    ejecutado por el usuario root.");
				System.out.println("    El usuario indicado para iniciar este servicio es \"emaku\".");
				System.exit(0);	
			}
		}
		
		String emakuConfigFile = ServerConst.CONF + ServerConst.SEPARATOR + "server.conf";
		boolean existsConfigFile = (new File(emakuConfigFile)).exists();
		
		if (!existsConfigFile) {
        	ConfigFile.newConfigFile(emakuConfigFile);			
        	System.out.println("INFO: Archivo de configuracion no encontrado... creando uno nuevo...");
		}
		
		try {						
			ConfigFile.loadConfigFile(emakuConfigFile);
			PoolConexiones.CargarBD();
			ReportsStore.Load(this.getClass().getResource("/reports"));
			try {
				LinkingCache.cargar();
			} catch (SQLBadArgumentsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new BeanShell();
			Thread t = new Thread() {
				public void run() {
					try {
						new SocketServer();
					} catch (IOException IOEe) {
					    
			            LogAdmin.setMessage(Language.getWord("UNLOADING_ST") + " "
			                    + IOEe.getMessage(), ServerConst.MESSAGE);
			            killServer();
			            
			        }
				}
			};
			t.start();
			
		} catch (ConfigFileNotLoadException e) {

        	ConfigFile.newConfigFile(emakuConfigFile);
        	
        	if (os.startsWith("Windows")) {
        		JOptionPane.showMessageDialog(new JFrame(),"El archivo de configuracion estaba corrupto y ha sido corregido. Por favor, revise el archivo server.conf y reinicie el servicio.","Error!",
        				JOptionPane.ERROR_MESSAGE);
        	} else {
        		System.out.println("ERROR #003: El archivo de configuracion estaba corrupto y ha sido corregido. Por favor, revise el archivo server.conf y reinicie el servicio.\n"
        		+ "("+ServerConst.CONF+ServerConst.SEPARATOR+"server.conf)\n");
        	}
        	killServer();
        	
        } catch (PoolNotLoadException e) {
        	
			LogAdmin.setMessage(e.getErrorCode(),e.getMessage(),Language.getWord("NODEBUG"),ServerConst.ERROR);
			killServer();
		} 
	}
	
	public void killServer() {
		System.exit(0);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {	    
		new Run();
		new MakeReport();
	}
}