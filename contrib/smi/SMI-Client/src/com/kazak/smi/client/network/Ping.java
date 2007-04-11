package com.kazak.smi.client.network;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.nio.channels.SocketChannel;
import java.util.Timer;
import java.util.TimerTask;

import com.kazak.smi.client.gui.LoginWindow;
import com.kazak.smi.lib.misc.ConfigFileClient;

public class Ping {

	private int port;
	private String host;
	private static MyTimer myTimer;
	
	public Ping(String host, int port) {
		this.host = host;
		this.port = port;
		System.out.println("Iniciando el ping");
		myTimer = new MyTimer(ConfigFileClient.getTime());
		myTimer.start();
	}

	class MyTimer {
		private final Timer timer = new Timer();

		private final int minutes;

		public MyTimer(int minutes) {
			this.minutes = minutes * 1000 * 60;
			
		}

		public void start() {
			timer.schedule(new TimerTask() {
				public void run() {
					work();
					timer.cancel();
					myTimer = new MyTimer(ConfigFileClient.getTime());
					myTimer.start();
				}
			}, minutes);
		}

	}
	
	public static void cancel() {
		if (myTimer!=null) {
			System.out.println("Terminando el ping");
			myTimer.timer.cancel();	
		}
	}
	
	private void work() {
		try {
			SocketChannel socket = SocketChannel.open();
			socket.configureBlocking(false);
			InetSocketAddress addr = null;
			addr = new InetSocketAddress(host, port);
			socket.connect(addr);
			while (!socket.finishConnect()) {
				try {
					Thread.sleep(100);
					System.out.println("@");
				} catch (InterruptedException IEe) {}
			}
			LoginWindow.ontop = true;
			System.out.println("Conexion re-establecida");
			socket.close();
		} catch (NoRouteToHostException e) {
			System.out.println("No es posible conectarse");
		} catch (ConnectException e) {
			System.out.println("El servidor esta caido");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
