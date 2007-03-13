package common.gui.components;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;

public class TouchButtons extends JPanel implements ActionListener {

	private static final long serialVersionUID = 5953946428251386838L;
	private JButton[][] buttons = {{new JButton("7"),new JButton("8"),new JButton("9")},
								   {new JButton("4"),new JButton("5"),new JButton("6")},
								   {new JButton("1"),new JButton("2"),new JButton("3")},
								   {new JButton("0"),new JButton("00"),new JButton("000")},
								   {new JButton("CLEAN"),new JButton("<<"),new JButton("OK")}};
	private EmakuTouchField touchField;

	public TouchButtons(EmakuTouchField emakuTouchField, Font font){
		this.touchField = emakuTouchField;
		this.setLayout(new BorderLayout());
		JPanel matriz = new JPanel(new GridLayout(5,3));
		for(JButton[] row:buttons) {
			for(JButton col:row) {
				matriz.add(col);
				col.setFont(font);
				col.addActionListener(this);
			}
		}
		this.add(matriz,BorderLayout.CENTER);
	}
	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton)e.getSource();
		String value = b.getText();
		if ("OK".equals(value)) {
			touchField.doFormat();
		}
		else if ("CLEAN".equals(value)) {
			touchField.setText("");
		}
		else if ("<<".equals(value)) {
			try {
				int offset = touchField.getText().length();
				touchField.getDocument().remove(offset-1,1);
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}
		else {
			try {
				int offset = touchField.getText().length();
				touchField.getDocument().insertString(offset,value,null);
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}
	}
}
