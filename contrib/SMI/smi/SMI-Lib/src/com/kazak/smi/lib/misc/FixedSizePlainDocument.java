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

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if ((getLength() + str.length()) <= maxSize) {
            super.insertString(offs, str, a);
        } else {
            throw new BadLocationException("Insertion exceeds max size of document", offs);
        }
    }
}