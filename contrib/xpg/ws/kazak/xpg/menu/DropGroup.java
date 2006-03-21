/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS DropGroup v 0.1                                                   
* Descripcion:
* Esta clase se encarga de mostrar el dialogo mediante el
* cual se elimina un grupo del SMBD.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.menu;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.idiom.Language;

public class DropGroup extends JDialog {

    private JOptionPane optionPane;
    JTextArea LogWin;
    final JComboBox cmbUser;
    PGConnection conn;

    public DropGroup(JFrame jframe,PGConnection pg_konnection, JTextArea jtextarea) {

        super(jframe, true);
        conn = pg_konnection;
        LogWin = jtextarea;
        setTitle(Language.getWord("RMGRP"));
        String as[] = conn.getGroups();

        JLabel jlabel = new JLabel(Language.getWord("SELGRP"));
        cmbUser = new JComboBox(as);

        Object aobj[] = { jlabel, cmbUser };
        Object aobj1[] = { Language.getWord("DROP"), Language.getWord("CANCEL") };

        optionPane = new JOptionPane(((Object) (aobj)), 3, 0, null, aobj1, aobj1[0]);
        setContentPane(optionPane);
        setDefaultCloseOperation(0);
 
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowevent) {
                optionPane.setValue(new Integer(-1));
            }
         });

        optionPane.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent propertychangeevent) {

                String s = propertychangeevent.getPropertyName();
                if (isVisible() && propertychangeevent.getSource() == optionPane && (s.equals("value") || s.equals("inputValue"))) {
                    Object obj = optionPane.getValue();

                    if (obj == JOptionPane.UNINITIALIZED_VALUE)
                        return;

                    if (obj.equals(Language.getWord("DROP"))) {

                        String s1 = (String)cmbUser.getSelectedItem();
                        String s2 = "DROP GROUP " + s1 + ";";
                        String s3 = conn.SQL_Instruction(s2);

                        addTextLogMonitor(Language.getWord("EXEC") + " \"" + s2 + "\"");
                        addTextLogMonitor(Language.getWord("RES") + s3);

                        if (s3.equals("OK"))
                            setVisible(false);
                        else {
                              JOptionPane.showMessageDialog(DropGroup.this, s3, Language.getWord("ERROR!"), 0);
                              return;
                         }
                      } 
                     else
                        setVisible(false);
                 }
            }

        });

      pack();
      setLocationRelativeTo(jframe);
      setVisible(true);
    }

    public void addTextLogMonitor(String s) {

        LogWin.append(s + "\n");
        int i = LogWin.getDocument().getLength();

        if (i > 0)
            LogWin.setCaretPosition(i - 1);
     }

} //Fin de la Clase
