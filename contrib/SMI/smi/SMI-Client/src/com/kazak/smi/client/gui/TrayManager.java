package com.kazak.smi.client.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.border.LineBorder;

import com.kazak.smi.lib.misc.ClientConst;

public class TrayManager implements ActionListener {

	private JFrame window;
	private static boolean loged = false;
	private JPanel menu;
	private static JButton JBSend;
	private static JButton JBView;
	private static JButton JBChange;
	
	public TrayManager() {
		window = new JFrame("Mensajería Interna");
		window.setSize(195,40);
		window.setUndecorated(true);
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.setResizable(false);
		window.setLocation(0,0);
		window.setAlwaysOnTop(true);
		window.addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {
				window.setState(JFrame.NORMAL);
			}
			public void windowClosing(WindowEvent e) {
				window.setVisible(true);
			}
		});
		ToolTipManager.sharedInstance().setInitialDelay(0);
		
		menu = new JPanel(new GridLayout(1,3));
		menu.setBorder(new LineBorder(Color.BLACK,3));
		URL url = getClass().getResource(ClientConst.iconsPath + "new_message.png");
		ImageIcon icon = new ImageIcon(url);

		JBSend = new JButton(icon);
		JBSend.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				window.requestFocus();
				if (!LoginWindow.isLoged()) {
					LoginWindow.getFrame().toBack();
					LoginWindow.getFrame().toFront();
				}
			}
		});
		JBSend.setToolTipText("<html><h1>Enviar Nuevo Mensaje</h1></html>");
		JBSend.setActionCommand("new");
		JBSend.addActionListener(this);
		JBSend.setEnabled(false);
		JBSend.setBorder(new LineBorder(Color.BLACK,2));

		menu.add(JBSend);
		
		
		url = getClass().getResource(ClientConst.iconsPath + "view_message.png");
		icon = new ImageIcon(url);
		JBView = new JButton(icon);
		JBView.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				window.requestFocus();
				if (!LoginWindow.isLoged()) {
					LoginWindow.getFrame().toBack();
					LoginWindow.getFrame().toFront();
				}
			}
		});
		JBView.setToolTipText("<html><h1>Ver Mensajes recibidos</h1></html>");
		JBView.addActionListener(this);
		JBView.setActionCommand("view");
		JBView.setEnabled(false);
		JBView.setBorder(new LineBorder(Color.BLACK,2));
		menu.add(JBView);
		
		url = getClass().getResource(ClientConst.iconsPath + "password.png");
		icon = new ImageIcon(url);
		JBChange = new JButton(icon);
		JBChange.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				window.requestFocus();
				if (!LoginWindow.isLoged()) {
					LoginWindow.getFrame().toBack();
					LoginWindow.getFrame().toFront();
				}
			}
		});
		JBChange.setToolTipText("<html><h1>Cambiar clave del correo<h1></html>");
		JBChange.addActionListener(this);
		JBChange.setActionCommand("change_password");
		JBChange.setEnabled(false);
		JBChange.setBorder(new LineBorder(Color.BLACK,2));
		menu.add(JBChange);
		
		JToolTip jtp = new JToolTip();
		jtp.setFont(new Font("Sans",Font.BOLD,20));
		jtp.setComponent(JBSend);
		
		window.add(menu);
		window.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("new".equals(command)) {
			if (loged) {
				JBSend.setEnabled(false);
				new MessageWindow();
				JBSend.setEnabled(true);
			}
		}
		else if ("view".equals(command)) {
			JBView.setEnabled(false);
			MessageViewer.show();
			JBView.setEnabled(true);
		}
		else if ("change_password".equals(command)) {
			JBChange.setEnabled(false);
			PasswordExchange.show();
			JBChange.setEnabled(true);
		}
	}

	public static boolean isLoged() {
		return loged;
	}

	public static void setLoged(boolean loged) {
		TrayManager.loged = loged;
		JBSend.setEnabled(loged);
		JBView.setEnabled(loged);
		JBChange.setEnabled(loged);
	}
}