package jmserver2;

import java.io.IOException;

import jmlib.miscelanea.idiom.Language;
import jmlib.miscelanea.log.AdminLog;
import jmserver2.basedatos.conexion.PoolConexiones;
import jmserver2.basedatos.conexion.PoolNotLoadException;
import jmserver2.basedatos.sql.CacheEnlace;
import jmserver2.comunicaciones.SocketServer;
import jmserver2.miscelanea.JMServerIICons;
import jmserver2.miscelanea.configuracion.ConfigFile;
import jmserver2.miscelanea.configuracion.ConfigFileNotLoadException;

/**
 * Run.java Creado el 28-jun-2004
 * 
 * Este archivo es parte de JMServerII
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * JMServerII es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class Run {

	/**
	 * 
	 */
	public Run() {
		try {		    
			ConfigFile.Cargar();
			PoolConexiones.CargarBD();
			CacheEnlace.cargar();
			new SocketServer();
			
		} catch (IOException IOEe) {
		    
            AdminLog.setMessage(Language.getWord("UNLOADING_ST") + " "
                    + IOEe.getMessage(), JMServerIICons.MESSAGE);
            
        } catch (ConfigFileNotLoadException e) {
			System.out
					.println("Error al cargar el archivo de configuracion "
							+ "en el servidor de transacciones");
			
		} catch (PoolNotLoadException e) {
			AdminLog.setMessage(e.getMessage(),JMServerIICons.ERROR);
		} 
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {	    
		new Run();
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

            SelectXML test = new SelectXML("001-as", "DBMidas4", "SEL0001");

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