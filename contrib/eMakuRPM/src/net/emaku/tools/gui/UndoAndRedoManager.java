package net.emaku.tools.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class UndoAndRedoManager {
	
	private UndoManager undo;
	private AbstractAction undoAction;
	private AbstractAction redoAction;
	
	public UndoAndRedoManager(JTextPane component) {
		undo = new UndoManager();
		Document doc = component.getDocument();
		doc.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent evt) {
				undo.addEdit(evt.getEdit());
			}
		});
		
		undoAction = new AbstractAction() {
			private static final long serialVersionUID = -1329269878234538739L;
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canUndo()) {
						undo.undo();
					}
				}
				catch (CannotUndoException e) {
					e.printStackTrace();
				}
			}
		};
		
		redoAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canRedo()) {
						undo.redo();
					}
				}
				catch (CannotRedoException e) {
					e.printStackTrace();
				}
			}
		};
		component.getActionMap().put("Undo",undoAction);
		component.getInputMap().put(KeyStroke.getKeyStroke("control Z"),"Undo");

		component.getActionMap().put("Redo", redoAction);
		component.getInputMap().put(KeyStroke.getKeyStroke("control Y"),"Redo");
	}

	public AbstractAction getUndoAction() {
		return undoAction;
	}

	public AbstractAction getRedoAction() {
		return redoAction;
	}
}
