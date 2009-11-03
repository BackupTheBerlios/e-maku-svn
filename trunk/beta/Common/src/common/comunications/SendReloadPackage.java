package common.comunications;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;

import common.misc.MD5;

public class SendReloadPackage extends Thread {
	
	private SocketChannel socket;
	
	public SendReloadPackage(String host,int port,String bd,String login,String password) throws IOException, NoSuchAlgorithmException {
        socket = SocketChannel.open();
        socket.configureBlocking(false);
        InetSocketAddress addr = new InetSocketAddress(host,port);
        socket.connect(addr);
        int k=0;
        while(!socket.finishConnect()){
        		try {
        			Thread.sleep(100);
        			k++;
        			if (k==50) {
        				throw new ConnectException();
    				}
        		}
        		catch(InterruptedException IEe) {}
        }
        this.start();
        SocketWriter.writing(socket,getPackage(bd,login,password));
	}

	 public Document getPackage(String bd, String login, String password ) throws NoSuchAlgorithmException{
	        
    	Element rootNode = new Element("RELOAD");
        Document doc = new Document(rootNode);
        
        rootNode.addContent(new Element("db").setText(bd));
        rootNode.addContent(new Element("login").setText(login));
        rootNode.addContent(new Element("password").setText(new MD5(password).getDigest()));
        
        return doc;
	 }
	 
    public void run() {
        try {
            
            Selector selector = Selector.open();
            socket.register(selector, SelectionKey.OP_READ);

            while (true) {

                int n = selector.select();
                if (n > 0) {
                    Iterator iterador = selector.selectedKeys().iterator();
                    while (iterador.hasNext()) {

                        SelectionKey clave = (SelectionKey) iterador.next();
                        iterador.remove();
                        
                        if (clave.isReadable()) {
                        	SocketConnector.getPackageXML().work(socket);
                        } else if(clave.isValid()){
                            socket.close();
                            socket=null;
                            return;
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
