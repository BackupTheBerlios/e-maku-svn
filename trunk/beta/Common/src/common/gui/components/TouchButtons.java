package common.gui.components;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;

public class TouchButtons extends JPanel implements ActionListener {

	private static final long serialVersionUID = 5953946428251386838L;
	private JButton JBOk = new JButton("OK");
	private JButton[][] bills = {{new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_billete_50000_100x50.png"))),
								  new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_billete_20000_100x50.png")))},
								 {new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_billete_10000_100x50.png"))),
							      new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_billete_5000_100x50.png")))},
								 {new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_billete_2000_100x50.png"))),
								 new JButton(new ImageIcon(this.getClass().getResource("/icons/ico_billete_1000_100x50.jpg")))}};
	
	private JButton[][] buttons = {{new JButton("7"),new JButton("8"),new JButton("9")},
								   {new JButton("4"),new JButton("5"),new JButton("6")},
								   {new JButton("1"),new JButton("2"),new JButton("3")},
								   {new JButton("0"),new JButton("00"),new JButton("000")},
								   {new JButton("C"),new JButton("<<"),JBOk}};
	
	private EmakuTouchField touchField;

	public TouchButtons(EmakuTouchField emakuTouchField, Font font,boolean withBill){
		this.touchField = emakuTouchField;
		this.setLayout(new BorderLayout());
		
		JPanel mbills = new JPanel(new GridLayout(3,2));
		int i=0;
		for(JButton[] row:bills) {
			for(JButton col:row) {
				mbills.add(col);
				col.addActionListener(this);
				col.setName((i++)+"M");
			}
		}
		
		JPanel matriz = new JPanel(new GridLayout(5,3));
		for(JButton[] row:buttons) {
			for(JButton col:row) {
				matriz.add(col);
				col.setFont(font);
				col.addActionListener(this);
			}
		}
		if (withBill)
			this.add(mbills,BorderLayout.WEST);
		this.add(matriz,BorderLayout.CENTER);
	}
	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton)e.getSource();
		String value = !b.getText().equals("")?b.getText():b.getName();
		System.out.println(value);
		if ("OK".equals(value)) {
			touchField.doFormat();
		}
		else if ("C".equals(value)) {
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
		else if ("0M".equals(value)) {
			touchField.setText("50000");
		}else if ("1M".equals(value)) {
			touchField.setText("20000");
		}else if ("2M".equals(value)) {
			touchField.setText("10000");
		}else if ("3M".equals(value)) {
			touchField.setText("5000");
		}else if ("4M".equals(value)) {
			touchField.setText("2000");
		}else if ("5M".equals(value)) {
			touchField.setText("1000");
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
	public JButton getJBOk() {
		return JBOk;
	}
}
