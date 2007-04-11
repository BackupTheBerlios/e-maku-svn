package com.kazak.smi.admin.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class AutoCompleteComboBox extends JComboBox implements KeySelectionManager {
	
	private static final long serialVersionUID = 1L;
	private String searchFor;
	private long lap;
	private boolean lowercase = true;
	private int length;
	private Vector<String> collection; 
	
	public class CBDocument extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private int maxSize;
		public CBDocument(int max) {
			this.maxSize = max;
		}
		public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
			if (str == null)
				return;
			if ((getLength() + str.length()) <= maxSize) {
				super.insertString(offset, lowercase ? str : str.toUpperCase(), a);
			}
			if (!isPopupVisible() && str.length() != 0)
				fireActionEvent();	
		}
	}
	
	public AutoCompleteComboBox(Vector<String> collection,boolean wordcase, int length) {
		addItem("");
		for (String val : collection) {
			addItem(val);
		}
		this.collection = collection;
		this.lowercase = wordcase;
		this.length = length;
		this.setEditable(true);
		initAuto();
	}
	
	private void initAuto() {
		lap = new Date().getTime();
		setLightWeightPopupEnabled(false);
		setKeySelectionManager(this);
		JTextField tf;
		if (getEditor() != null) {
			tf = (JTextField) getEditor().getEditorComponent();
			if (tf != null) {
				tf.setDocument(new CBDocument(length));
				addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						JTextField tf = (JTextField) getEditor().getEditorComponent();
						String text = tf.getText();
						ComboBoxModel aModel = getModel();
						String current;
						if (collection.contains(text)) { return; } 
						
						for (int i = 0; i < aModel.getSize(); i++) {
							current = aModel.getElementAt(i).toString();
							if (!lowercase){
								if (current.toUpperCase().startsWith(text.toUpperCase())) {
									tf.setText(current);
									tf.setSelectionStart(text.length());
									tf.setSelectionEnd(current.length());
									break;
								}
							}
							else {
								if (current.toLowerCase().startsWith(text.toLowerCase())) {
									tf.setText(current);
									tf.setSelectionStart(text.length());
									tf.setSelectionEnd(current.length());
									break;
								}
							} 
									
						}
					}
				});
			}
		}
	}
	
	public int selectionForKey(char aKey, ComboBoxModel aModel) {
		long now = new java.util.Date().getTime();
		if (searchFor != null && aKey == KeyEvent.VK_BACK_SPACE
				&& searchFor.length() > 0) {
			searchFor = searchFor.substring(0, searchFor.length() - 1);
		} else {
			// System.out.println(lap);
			// Kam nie hier vorbei.
			if (lap + 1000 < now)
				searchFor = "" + aKey;
			else
				searchFor = searchFor + aKey;
		}
		lap = now;
		String current;
		for (int i = 0; i < aModel.getSize(); i++) {
			if (!lowercase) {
				current = aModel.getElementAt(i).toString().toUpperCase();
				if (current.toUpperCase().startsWith(searchFor.toUpperCase()))
					return i;
			}
			else {
				current = aModel.getElementAt(i).toString().toLowerCase();
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
}
