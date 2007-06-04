package com.kazak.smi.admin.gui.managers.tools.pos;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.kazak.smi.admin.control.Cache;
import com.kazak.smi.admin.gui.managers.tools.ToolsConstants;

public class PosData {

	private static JFrame frame; 
	private String[] data = new String[5];
	private JTextField ipText;
	private int action;

	public PosData(JFrame frame, String[] data, JTextField ipText, int action) {
		PosData.frame = frame;
		this.data = data;
		this.ipText = ipText;
		this.action = action;
	}
	
	public String[] getData() {
		return data;
	}
	
	public boolean verifyData() {
		
		if(data[0].length()==0) {
			return false;
		}		
		
		if(differentName()) {
			return false;
		}
		
		if(!isAValidIP(data[2])) {
			return false;
		}

		if(!groupIsZone(data[3])) {
			return false;
		}

		return true;
	}
	
	private boolean differentName() {

		if (!data[0].equals(data[4])) {
			
			String flag = "no";
			
			if(action == ToolsConstants.ADD) {
				flag = "ya";
			}
			
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(
					frame,
					"<html><center>" +
					"El nombre del punto de venta " + data[4] 
					 + " " + flag + " existe. " +
			"</center></html>");
			return true;		
		}

		return false;
	}
	
	private boolean groupIsZone(String group) {
		Cache.Group g = Cache.getGroup(group);

		if (!g.isZone()) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(
					frame,
					"<html><center>" +
					"El grupo seleccionado no es zona.<br>Por favor, escoja otro grupo. " +
			"</center></html>");
			return false;		
		}
		
		return true;
	}
	
	private boolean isAValidIP(String input) {
		if (!"".equals(input)) {
			String ipFields[] = input.split("\\.");
			
			if (ipFields.length < 4) {
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(
						frame,
						"<html><center>" +
						"Formato inválido de dirección IP. <br>Por favor, ingrese un valor apropiado. <br>Ej: 127.0.0.1 " +
				"</center></html>");
				ipText.requestFocus();
				return false;				
			}
			
			try {
				if (Integer.parseInt(ipFields[0]) < 255 &&
						Integer.parseInt(ipFields[1]) < 255 &&
						Integer.parseInt(ipFields[2]) < 255 &&
						Integer.parseInt(ipFields[3]) < 255) {
					return true;
				}
				else {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(
							frame,
							"<html><center>" +
							"Formato inválido de dirección IP. <br>Por favor, ingrese un valor apropiado. <br>Ej: 127.0.0.1 " +
					"</center></html>");
					ipText.requestFocus();
					return false;
				}

			} catch (NumberFormatException ex) {
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(
						frame,
						"<html><center>" +
						"Rangos inválidos de dirección IP. <br>Por favor, corrija los valores incorrectos. <br>Ej: 127.0.0.1 " +
				"</center></html>");
				ipText.requestFocus();
				return false;
			}
		}
		else {
			JOptionPane.showMessageDialog(
					frame,
					"<html><center>" +
					"Debe ingresar una dirección IP. <br>Ej: 127.0.0.1 " +
			"</center></html>");
			ipText.requestFocus();
			return false;
		}
	}

}
