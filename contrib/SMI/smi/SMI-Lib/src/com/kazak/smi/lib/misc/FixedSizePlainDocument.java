package com.kazak.smi.lib.misc;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class FixedSizePlainDocument extends PlainDocument {
	
	private static final long serialVersionUID = 3056490631903276108L;
	private int maxSize;
	
    public FixedSizePlainDocument(int limit) {
        maxSize = limit;
    }

    public void insertString(int offset, String string, AttributeSet attributeSet) throws BadLocationException {
        if ((getLength() + string.length()) <= maxSize) {
            super.insertString(offset, string, attributeSet);
        } else {
            throw new BadLocationException("ERROR: Insertion exceeds max size of document", offset);
        }
    }
}