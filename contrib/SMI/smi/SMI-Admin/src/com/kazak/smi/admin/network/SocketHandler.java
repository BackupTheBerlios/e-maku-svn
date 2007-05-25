package com.kazak.smi.admin.network;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Iterator;

import javax.swing.JOptionPane;

import com.kazak.smi.admin.gui.main.LoginWindow;
import com.kazak.smi.admin.gui.main.MainWindow;
import com.kazak.smi.lib.network.PackageToXMLConverter;

public class SocketHandler extends Thread  {

	private static SocketChannel socket;
	private PackageToXMLConverter packageXML;
	
	public SocketHandler(String host,int port,PackageToXMLConverter packageXML) 
	throws UnresolvedAddressException, IOException, NoRouteToHostException {
		socket = SocketChannel.open();
        socket.configureBlocking(false);
        this.packageXML = packageXML;
        
        InetSocketAddress address = new InetSocketAddress(host,port);    
       	socket.connect(address);
       	int times = 0;
       	
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
	}
	
	public void run() {
        try {
            Selector selector = Selector.open();
            socket.register(selector, SelectionKey.OP_READ);
            boolean result = true;
            while (true) {
                int n = selector.select();
                if (n > 0) {
                    Iterator iterador = selector.selectedKeys().iterator();
                    while (iterador.hasNext()) {
                        SelectionKey key = (SelectionKey) iterador.next();
                        iterador.remove();
                        if (key.isReadable()) {
                        	result = packageXML.work(socket) ;
                        	if (!result) {
                        		JOptionPane.showMessageDialog(
                        				null,
                        				"Se ha perdido la conexión con el servidor.\n" +
                        				"Por favor, contacte a los administradores del sistema.");
                        	}
                        }
                        if (!result) {
                        	break;
                        }
                    }
                }
                if (!result) {
                	MainWindow.getFrame().dispose();
                	new LoginWindow();
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
    
    public static boolean isConnected(){
        return socket.isConnected();
    }
}