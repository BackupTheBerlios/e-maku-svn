package server;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import server.comunications.SocketServer;
import server.control.ReportsStore;
import server.database.connection.PoolConexiones;
import server.database.connection.PoolNotLoadException;
import server.database.sql.CacheEnlace;
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
			CacheEnlace.cargar();
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
/*	private void leerMensaje(SocketChannel SCcanal_lectura) {
        ByteBuffer buf = ByteBuffer.allocateDirect(36864);
        try {
            buf.clear();
            int numBytesRead = SCcanal_lectura.read(buf);
            System.out.println("********** Numero de bytes leidos: "
                    + numBytesRead + " ***************");
            buf.flip();
            System.out.println(this.decode(buf));
        }
        catch (IOException IOe) {
            System.out.println("Excepcion escribiendo :" + IOe);
        }

    }

    private String decode(ByteBuffer byteBuffer) {
        try {
            Charset charset = Charset.forName("iso-8859-1");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(byteBuffer);
            String result = charBuffer.toString();
            return result;
        }
        catch (CharacterCodingException CCEe) {
            System.out.println("Excepcion CharacterCodingException: "
                    + CCEe.getMessage());
            return "";
        }
    }

    private void escribirMensaje(SocketChannel channel) {
        try {

            ResultSetToXML test = new ResultSetToXML("001-as", "DBMidas4", "SEL0001");

            XMLOutputter out = new XMLOutputter();
            
            out.setFormat(Format.getPrettyFormat());
            Document d = test.Generar();

            ByteBuffer buf = ByteBuffer.allocate(32768);
            
            // Create an output stream on the ByteBuffer
            
            OutputStream os = newOutputStream(buf);
            
            out.output(d, os);
            os.close();
            //SocketChannel channel = ((Socket)(clientesActivos.elementAt(0))).getChannel();
            String prueba = "hola";
            System.out.println("que tiene buf: "+buf);
            buf.flip();
            channel.write(buf);
            
        }
        catch (IOException IOEe) {
            System.out.println("problemas escribiendo " + IOEe.getMessage());
        }

    }
    
    
    
    // Returns an output stream for a ByteBuffer.
    // The write() methods use the relative ByteBuffer put() methods.
    public static OutputStream newOutputStream(final ByteBuffer buf) {
        return new OutputStream() {
            public synchronized void write(int b) throws IOException {
                buf.put((byte)b);
            }
    
            public synchronized void write(byte[] bytes, int off, int len) throws IOException {
                System.out.println("bytes: "+bytes+" off "+off+" len "+len );
                buf.put(bytes, off, len);
            }
        };
    }*/
}