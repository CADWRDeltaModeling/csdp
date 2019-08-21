package DWR.CSDP;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * Adapted from http://www.java2s.com/Code/Java/Swing-JFC/CreatingTextAreawithUndoRedoCapabilities.htm
 * @author btom
 *
 */
class UndoableJTextArea extends JTextArea implements UndoableEditListener, FocusListener, KeyListener {
	private UndoManager m_undoManager;

	public UndoableJTextArea() {
		this(new String());
	}

	public UndoableJTextArea(String text) {
		super(text);
		getDocument().addUndoableEditListener(this);
		this.addKeyListener(this);
		this.addFocusListener(this);
	}

	private void createUndoMananger() {
		m_undoManager = new UndoManager();
		m_undoManager.setLimit(20);
	}

	private void removeUndoMananger() {
		m_undoManager.end();
	}

	public void focusGained(FocusEvent fe) {
		createUndoMananger();
	}

	public void focusLost(FocusEvent fe) {
		removeUndoMananger();
	}

	public void undoableEditHappened(UndoableEditEvent e) {
		m_undoManager.addEdit(e.getEdit());
	}

	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_Z) && (e.isControlDown())) {
			try {
				m_undoManager.undo();
			} catch (CannotUndoException cue) {
				Toolkit.getDefaultToolkit().beep();
			}
		}

		if ((e.getKeyCode() == KeyEvent.VK_Y) && (e.isControlDown())) {
			try {
				m_undoManager.redo();
			} catch (CannotRedoException cue) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}


}
