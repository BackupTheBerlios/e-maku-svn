package com.kazak.smi.client.network;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Iterator;

import javax.swing.JOptionPane;

import com.kazak.smi.client.gui.LoginWindow;
import com.kazak.smi.client.gui.TrayManager;
import com.kazak.smi.lib.network.PackageToXML;

public class SocketHandler extends Thread  {

	private static SocketChannel socket;
	private PackageToXML packageXML;
	private String host;
	private int port;
	
	public SocketHandler(String host,int port,PackageToXML packageXML) 
	throws UnresolvedAddressException, IOException {
		this.host = host;
		this.port = port;
		
		socket = SocketChannel.open();
        socket.configureBlocking(false);
        this.packageXML = packageXML;
        InetSocketAddress addr = new InetSocketAddress(host,port);
        socket.connect(addr);
        
        int times=0;
        while(!socket.finishConnect()){
    		try {
    			Thread.sleep(100);
    			times++;
    			if (times==50) {
    				throw new ConnectException();
				}
    		}
    		catch(InterruptedException IEe) {}
        }
        Ping.cancel();
	}
	
	public void run() {
        try {
            Selector selector = Selector.open();
            socket.register(selector, SelectionKey.OP_READ);
            boolean ret = true;
            while (true) {
                int n = selector.select();
                if (n > 0) {
                    Iterator iterador = selector.selectedKeys().iterator();
                    while (iterador.hasNext()) {
                        SelectionKey clave = (SelectionKey) iterador.next();
                        iterador.remove();
                        if (clave.isReadable()) {
                        	ret = packageXML.work(socket) ;
                        	if (!ret) {
                        		JOptionPane.showMessageDialog(
                        				null,
                        				"Se perdio la conexión con el servidor\n" +
                        				"Contacte con los administradores del sistema\n"+
                        				"Vuelva a conectarse mas tarde...");
                        	}
                        }
                        if (!ret) {
                        	break;
                        }
                    }
                }
                if (!ret) {
                	TrayManager.setLoged(false);
                	LoginWindow.Show();
                	LoginWindow.ontop = false;
                	new Ping(host,port);
                	break;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static SocketChannel getSock() {
        return socket;
    }
    
    public static boolean isConcected(){
        return socket.isConnected();
    }
}