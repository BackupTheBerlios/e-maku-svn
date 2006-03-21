/*
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
* 
* CLASS AlterUser v 0.1
* Descripcion:   
* Esta clase se encarga de manejar el dialogo que permite
* alterar los parametros de un usuario del SMBD.
* 
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
* 
* Fecha: 2001/10/01
* 
* Autores: Beatriz Florián  - bettyflor@kazak.ws
*          Gustavo Gonzalez - xtingray@kazak.ws 
*/
package ws.kazak.xpg.menu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.idiom.Language;

public class AlterUser extends JDialog implements ActionListener {

 JComboBox groups;
 final JTextField textFieldUser = new JTextField(10);
 final JTextField textFieldValidez;
 final JTextField uidT = new JTextField(5);
 JPasswordField textFieldPassw;
 JPasswordField textFieldVePassw;
 JCheckBox createDBButton;
 JCheckBox createUserButton;
 boolean wellDone;
 PGConnection conn;
 JTextArea LogWin;
 JComboBox users;
 boolean superuser;
 boolean createdb;
 Timestamp datex;


 public AlterUser(JFrame jframe, PGConnection pg_konnection, JTextArea jtextarea) {

        super(jframe);
        wellDone = false;
        superuser = false;
        createdb = false;
        conn = pg_konnection;
        LogWin = jtextarea;

        setTitle(Language.getWord("ALTER") + " " + Language.getWord("USER"));
        JPanel jpanel = new JPanel();
        JPanel jpanel1 = new JPanel();
        JLabel jlabel = new JLabel(Language.getWord("SELUSR") + ": ");

        String as[] = conn.getUsers();
        users = new JComboBox(as);
        users.setActionCommand("COMBO");
        users.addActionListener(this);

        Vector vector = conn.getUserInfo(as[0]);
        Boolean boolean1 = (Boolean)vector.elementAt(0);

        if (boolean1.booleanValue())
            createdb = true;

        boolean1 = (Boolean)vector.elementAt(1);

        if (boolean1.booleanValue())
            superuser = true;

        datex = (Timestamp)vector.elementAt(2);
        jpanel1.setLayout(new FlowLayout(1));
        jpanel1.add(jlabel);
        jpanel1.add(users);

        JPanel jpanel2 = new JPanel();
        JLabel jlabel1 = new JLabel(Language.getWord("NAME") + ": ");
        jpanel2.setLayout(new GridLayout(0, 2));
        jpanel2.add(jlabel1);
        jpanel2.add(textFieldUser);

        JPanel jpanel3 = new JPanel();
        JLabel jlabel2 = new JLabel(Language.getWord("PASSWD") + ": ");
        textFieldPassw = new JPasswordField(10);
        textFieldPassw.setEchoChar('*');
        jpanel3.setLayout(new GridLayout(0, 2));
        jpanel3.add(jlabel2);
        jpanel3.add(textFieldPassw);

        JPanel jpanel4 = new JPanel();
        JLabel jlabel3 = new JLabel(Language.getWord("VRF") + " " + Language.getWord("PASSWD") + ": ");
        textFieldVePassw = new JPasswordField(10);
        textFieldVePassw.setEchoChar('*');
        jpanel4.setLayout(new GridLayout(0, 2));
        jpanel4.add(jlabel3);
        jpanel4.add(textFieldVePassw);

        JPanel jpanel5 = new JPanel();
        JLabel jlabel4 = new JLabel(Language.getWord("GROUP") + ": ");
        String as1[] = conn.getGroups();
        groups = new JComboBox(as1);
        jpanel5.setLayout(new GridLayout(0, 2));
        jpanel5.add(jlabel4);
        jpanel5.add(groups);

        JPanel jpanel6 = new JPanel();
        JLabel jlabel5 = new JLabel(Language.getWord("VLD") + " [" + Language.getWord("DATE") + "]: ");
        String s = "";

        if (datex != null) {
            String s1 = datex.toString();
            int i = s1.indexOf(" ");
            s = s1.substring(0, i);
         }

        textFieldValidez = new JTextField(s, 10);
        jpanel6.setLayout(new GridLayout(0, 2));
        jpanel6.add(jlabel5);
        jpanel6.add(textFieldValidez);

        JPanel jpanel7 = new JPanel();
        jpanel7.setLayout(new BoxLayout(jpanel7, 1));
        jpanel7.add(jpanel3);
        jpanel7.add(jpanel4);
        jpanel7.add(jpanel6);
        createDBButton = new JCheckBox(Language.getWord("CREATE") + " " + Language.getWord("DB"));

        if (createdb)
            createDBButton.setSelected(true);

        createDBButton.setMnemonic('p');
        createDBButton.setActionCommand("Create DataBase");
        createUserButton = new JCheckBox(Language.getWord("CREATE") + " " + Language.getWord("USER"));

        if (superuser)
            createUserButton.setSelected(true);

        createUserButton.setMnemonic('p');

        createUserButton.setActionCommand("Create User");

        JPanel jpanel8 = new JPanel();
        jpanel8.setLayout(new GridLayout(0, 2));

        JLabel jlabel6 = new JLabel("Uid:");
        jpanel8.add(jlabel6);
        jpanel8.add(uidT);

        JPanel jpanel9 = new JPanel();
        jpanel9.setLayout(new BoxLayout(jpanel9, 1));
        jpanel9.add(createDBButton);
        jpanel9.add(createUserButton);
        javax.swing.border.Border border = BorderFactory.createEtchedBorder();
        TitledBorder titledborder = BorderFactory.createTitledBorder(border, Language.getWord("PERMI"));
        titledborder.setTitleJustification(1);
        jpanel9.setBorder(titledborder);

        JPanel jpanel10 = new JPanel();
        jpanel10.setLayout(new BorderLayout());
        jpanel10.add(jpanel9, "North");

        JPanel jpanel11 = new JPanel();
        jpanel11.setLayout(new BorderLayout());
        titledborder = BorderFactory.createTitledBorder(border, Language.getWord("GENSETT"));
        jpanel11.add(jpanel7, "North");
        jpanel11.setBorder(titledborder);

        JPanel jpanel12 = new JPanel();
        jpanel12.setLayout(new BorderLayout());
        jpanel12.add(jpanel11, "West");
        jpanel12.add(jpanel, "Center");
        jpanel12.add(jpanel10, "East");

        JButton jbutton = new JButton(Language.getWord("ALTER"));
        jbutton.setActionCommand("OK");
        jbutton.addActionListener(this);
        jbutton.setMnemonic('A');
        jbutton.setAlignmentX(0.5F);

        JButton jbutton1 = new JButton(Language.getWord("CANCEL"));
        jbutton1.setActionCommand("CANCEL");
        jbutton1.addActionListener(this);
        jbutton1.setMnemonic('A');
        jbutton1.setAlignmentX(0.5F);

        JPanel jpanel13 = new JPanel();
        jpanel13.setLayout(new FlowLayout());
        jpanel13.add(jbutton);
        jpanel13.add(jbutton1);

        JPanel jpanel14 = new JPanel();
        jpanel14.setLayout(new BoxLayout(jpanel14, 1));
        jpanel14.add(jpanel1);
        jpanel14.add(jpanel12);
        jpanel14.add(jpanel13);

        JPanel jpanel15 = new JPanel();
        jpanel15.add(jpanel14);
        getContentPane().add(jpanel15);

        titledborder = BorderFactory.createTitledBorder(border);
        jpanel15.setBorder(titledborder);

        pack();
        setLocationRelativeTo(jframe);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent actionevent) {

        if (actionevent.getActionCommand().equals("OK")) {

            String s = (String)users.getSelectedItem();
            String s2 = "ALTER USER " + s;
            String s3 = " WITH";
            char ac[] = textFieldPassw.getPassword();
            String s5 = new String(ac);
            char ac1[] = textFieldVePassw.getPassword();
            String s6 = new String(ac1);

            if (!s5.equals(s6)){
                JOptionPane.showMessageDialog(this, Language.getWord("INVPASS"), Language.getWord("ERROR!"), 0);
                return;
             }

            s3 = s3 + " PASSWORD '" + s5 + "'";

            if (createDBButton.isSelected())
                s3 = s3 + " CREATEDB";
            else
                s3 = s3 + " NOCREATEDB";

            if (createUserButton.isSelected())
                s3 = s3 + " CREATEUSER";
            else
                s3 = s3 + " NOCREATEUSER";

            String s7 = textFieldValidez.getText();

            if (s7.length() > 0)
                s3 = s3 + " VALID UNTIL '" + s7 + "'";

            s2 = s2 + s3 + ";";

            String s8 = conn.SQL_Instruction(s2);
            addTextLogMonitor(Language.getWord("EXEC") + " \"" + s2 + "\"");
            addTextLogMonitor(Language.getWord("RES") + " " + s8);

            if (s8.equals("OK")) {

                wellDone = true;
                setVisible(false);
             } 
            else
                JOptionPane.showMessageDialog(this, s8, Language.getWord("ERROR!"), 0);

            return;
        }

        if (actionevent.getActionCommand().equals("COMBO")) {

            createdb = false;
            superuser = false;
            String s1 = (String)users.getSelectedItem();
            Vector vector = conn.getUserInfo(s1);
            Boolean boolean1 = (Boolean)vector.elementAt(0);

            if (boolean1.booleanValue())
                createdb = true;

            createDBButton.setSelected(createdb);
            boolean1 = (Boolean)vector.elementAt(1);

            if (boolean1.booleanValue())
                superuser = true;

            createUserButton.setSelected(superuser);
            datex = (Timestamp)vector.elementAt(2);

            if (datex != null) {

                String s4 = datex.toString();
                int i = s4.indexOf(" ");
                textFieldValidez.setText(s4.substring(0, i));
             } 
            else {
                  textFieldValidez.setText("");
             }

            return;
        }
 
        if (actionevent.getActionCommand().equals("CANCEL"))
            setVisible(false);
    }

    public boolean isNum(String s) {

        for (int i = 0; i < s.length(); i++) {

             char c = s.charAt(i);

             if (!Character.isDigit(c))
                 return false;
         }

        return true;
     }

    public void addTextLogMonitor(String s) {

        LogWin.append(s + "\n");
        int i = LogWin.getDocument().getLength();

        if (i > 0)
            LogWin.setCaretPosition(i - 1);
     }

} //Fin de la Clase
