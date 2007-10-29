package com.kazak.comeet.admin.gui.misc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class AutoCompleteComboBox extends JComboBox implements KeySelectionManager, KeyListener, PopupMenuListener {
	
	private static final long serialVersionUID = 1L;
	private String searchFor;
	private long milliSeconds;
	private boolean lowerCase = true;
	private int length;
	private String[] collection;
	private JButton searchButton;
	private boolean fromCombo = false;
	
	public AutoCompleteComboBox(String[] collection,boolean wordCase, int length) {
		this.addPopupMenuListener(this);
		addItem("");
		for (String value : collection) {
			 addItem(value);
		}
		this.collection = collection; 
		this.lowerCase = wordCase;
		this.length = length;
		this.setEditable(true);
		initAuto();
	}	
	
	public AutoCompleteComboBox(String[] collection,boolean wordCase, int length, JButton ws) {
		this.addPopupMenuListener(this);
		addItem("");
		for (String value : collection) {
			if(!value.equals("COMEET")) {
			   addItem(value);
			}
		}
		this.collection = collection; 
		this.lowerCase = wordCase;
		this.length = length;
		this.searchButton = ws;
		this.setEditable(true);
		initAuto();
	}		
	
	public class ComboDocument extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private int maxSize;
		
		public ComboDocument(int max) {
			this.maxSize = max;
		}
		
		public void insertString(int offset, String string, AttributeSet attribute) throws BadLocationException {
			if (string == null)
				return;
			if ((getLength() + string.length()) <= maxSize) {
				super.insertString(offset, lowerCase ? string : string.toUpperCase(), attribute);
			}
			if (!isPopupVisible() && string.length() != 0) {
				fireActionEvent();	
			}
		}
	}
	
	public void blankTextField() {
		JTextField textField = (JTextField) getEditor().getEditorComponent();
		textField.setText("");
		textField.requestFocus();
	}
		
	public void unselectTextField() {
		JTextField textField = (JTextField) getEditor().getEditorComponent();
		textField.select(0,0);
	}	
	
	private void initAuto() {
		milliSeconds = new Date().getTime();
		setLightWeightPopupEnabled(false);
		setKeySelectionManager(this);
		JTextField textField;
		
		if (getEditor() != null) {
			textField = (JTextField) getEditor().getEditorComponent();
			textField.setFocusTraversalKeysEnabled(false);
			textField.addKeyListener(this);
			if (textField != null) {
				textField.setDocument(new ComboDocument(length));
				addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						JTextField jTextField = (JTextField) getEditor().getEditorComponent();
						String text = jTextField.getText();
						ComboBoxModel comboModel = getModel();
						String current;
						if (collectionContains(text)) {
							return; 
						} 
						for (int i = 0; i < comboModel.getSize(); i++) {
							current = comboModel.getElementAt(i).toString();
							if (!lowerCase){
								if (current.toUpperCase().startsWith(text.toUpperCase())) {
									jTextField.setText(current);
									jTextField.setSelectionStart(text.length());
									jTextField.setSelectionEnd(current.length());									
									break;
								}
							}
							else {
								if (current.toLowerCase().startsWith(text.toLowerCase())) {
									jTextField.setText(current);
									jTextField.setSelectionStart(text.length());
									jTextField.setSelectionEnd(current.length());
									break;
								}
							} 
									
						}
						if(text.length()>2) {
							if(!text.startsWith("CV")) {
								jTextField.setText(text.toLowerCase());
							}
						}
					}
				});
			}
		}
	}
	
	private boolean collectionContains(String key) {
		for(int i=0;i<collection.length;i++) {
			if(collection[i].equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	public int selectionForKey(char key, ComboBoxModel comboModel) {
		long now = new Date().getTime();
		
		if (searchFor != null && key == KeyEvent.VK_BACK_SPACE
				&& searchFor.length() > 0) {
			searchFor = searchFor.substring(0, searchFor.length() - 1);
		} else {
			if (milliSeconds + 1000 < now)
				searchFor = "" + key;
			else
				searchFor = searchFor + key;
		}
		milliSeconds = now;
		String current;
		for (int i = 0; i < comboModel.getSize(); i++) {
			if (!lowerCase) {
				current = comboModel.getElementAt(i).toString().toUpperCase();
				if (current.toUpperCase().startsWith(searchFor.toUpperCase()))
					return i;
			}
			else {
				current = comboModel.getElementAt(i).toString().toLowerCase();
				if (current.toLowerCase().startsWith(searchFor.toLowerCase()))
					return i;
			}
		}
		return -1;
	}

	public void fireActionEvent() {
		super.fireActionEvent();
	}
	
	public String getText() {
		return getSelectedItem().toString();
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
        if (keyCode==KeyEvent.VK_ENTER || keyCode==KeyEvent.VK_TAB){
        	if (searchButton != null) {
        		JTextField textField = (JTextField) getEditor().getEditorComponent();
        		String text = textField.getText();
        		if (!lowerCase){
        			textField.setText(text.toUpperCase());
        		}
        		if(text.length()>0) {
         		   fromCombo = false;
        		   searchButton.doClick();
        		}
        	}
        }
	}

	public void keyTyped(KeyEvent e) {
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		if (searchButton != null) {
			fromCombo = true;
			searchButton.doClick();
			fromCombo = false;
		}
	}
	
	public void activeCombo(boolean flag) {
		this.setEditable(flag);
		this.setEnabled(flag);
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}
	
	public boolean eventFromCombo() {
		return fromCombo;
	}
}
