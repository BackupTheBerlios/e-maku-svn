/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS Inherit v 0.1                                                   
* Descripcion:
* Esta clase se encarga de seleccionar las tablas de las 
* cuales se desea heredar atributos.
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ws.kazak.xpg.idiom.Language;

public class Inherit extends JDialog
    implements ActionListener {

    JList usrList;
    JList groupList;
    Vector tables;
    boolean semaforo = false;
    int num = 0;
    String TableList = ""; 
    boolean Done = false;

    public Inherit(JDialog jframe,String as[], Vector ht) {

        super(jframe, true);
        setTitle(Language.getWord("INHE"));
        num = as.length;
        tables = ht;

        JPanel jpanel = new JPanel();
        JLabel jlabel = new JLabel(Language.getWord("INFT"),JLabel.CENTER);
        jpanel.setLayout(new BorderLayout());
        jpanel.add(jlabel, "Center");
        usrList = new JList(tables);
        JScrollPane jscrollpane = new JScrollPane(usrList);
        jscrollpane.setPreferredSize(new Dimension(100, 120));

        groupList = new JList(as);
        JScrollPane jscrollpane1 = new JScrollPane(groupList);
        jscrollpane1.setPreferredSize(new Dimension(100, 120));

        URL imgURL = getClass().getResource("/icons/16_Right.png");
        JButton jbutton = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
        jbutton.setVerticalTextPosition(0);
        jbutton.setActionCommand("RIGHT");
        jbutton.addActionListener(this);

        imgURL = getClass().getResource("/icons/16_Left.png");
        JButton jbutton1 = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
        jbutton1.setVerticalTextPosition(0);
        jbutton1.setActionCommand("LEFT");
        jbutton1.addActionListener(this);

        JPanel jpanel2 = new JPanel();
        jpanel2.setLayout(new BoxLayout(jpanel2, 1));
        jpanel2.add(Box.createVerticalGlue());
        jpanel2.add(jbutton);
        jpanel2.add(jbutton1);
        jpanel2.add(Box.createVerticalGlue());

        JButton j01 = new JButton(Language.getWord("SELALL"));
        j01.setActionCommand("SELALL");
        j01.addActionListener(this);
        j01.setMnemonic(Language.getNemo("SELALL"));
        j01.setAlignmentX(0.5F);

        JButton j02 = new JButton(Language.getWord("CLRSEL"));
        j02.setActionCommand("CLEAN");
        j02.addActionListener(this);
        j02.setMnemonic(Language.getNemo("CLRSEL"));
        j02.setAlignmentX(0.5F);

        JPanel jpanel0 = new JPanel();
        jpanel0.setLayout(new FlowLayout());
        jpanel0.add(j01);
        jpanel0.add(j02);

        JButton jbutton2 = new JButton(Language.getWord("OK"));
        jbutton2.setActionCommand("OK");
        jbutton2.addActionListener(this);
        jbutton2.setMnemonic(Language.getNemo("OK"));
        jbutton2.setAlignmentX(0.5F);

        JButton jbutton3 = new JButton(Language.getWord("CANCEL"));
        jbutton3.setActionCommand("CANCEL");
        jbutton3.addActionListener(this);
        jbutton3.setMnemonic(Language.getNemo("CANCEL"));
        jbutton3.setAlignmentX(0.5F);

        JPanel jpanel3 = new JPanel();
        jpanel3.setLayout(new FlowLayout());
        jpanel3.add(jbutton2);
        jpanel3.add(jbutton3);

        JPanel jpanel4 = new JPanel();
        jpanel4.setLayout(new BoxLayout(jpanel4,BoxLayout.X_AXIS));
        jpanel4.add(jscrollpane1);
        jpanel4.add(jpanel2);
        jpanel4.add(jscrollpane);

        JPanel jpanel5 = new JPanel();
        jpanel5.setLayout(new BoxLayout(jpanel5, 1));
        jpanel5.add(jpanel);
        jpanel5.add(Box.createRigidArea(new Dimension(0, 10)));
        jpanel5.add(jpanel4);
        jpanel5.add(jpanel0);
        jpanel5.add(jpanel3);

        JPanel jpanel6 = new JPanel();
        jpanel6.add(jpanel5);
        getContentPane().add(jpanel6);

        pack();
        setLocationRelativeTo(jframe);
        setVisible(true);

    }

    public void actionPerformed(ActionEvent actionevent) {

        if (actionevent.getActionCommand().equals("SELALL")) {

            if (!semaforo) {

                int[] indices = new int[num];

                for (int k=0;k<num;k++)
                     indices[k] = k;
                groupList.setSelectedIndices(indices);
                semaforo = true;
              }
            else {
                  semaforo = false;
                  groupList.clearSelection();
              }
         }

        if (actionevent.getActionCommand().equals("CLEAN")) {

            if (tables.size()>0) {
                tables = new Vector();
                usrList.setListData(tables);
             }
            return;
         }

        if (actionevent.getActionCommand().equals("RIGHT")) {

            if (!groupList.isSelectionEmpty()) {
                Object[] fields = groupList.getSelectedValues();

                for (int i=0;i< fields.length;i++) {

                     if (!tables.contains(fields[i]))
                         tables.addElement(fields[i]);
                 }
                usrList.setListData(tables);
             }
            return;
         }

        if (actionevent.getActionCommand().equals("LEFT")) {

            String s1 = (String)usrList.getSelectedValue();

            if (tables.removeElement(s1))
                usrList.setListData(tables);

            return;
         }

        if (actionevent.getActionCommand().equals("OK")) {

            if (tables.size() > 0) {

                for (int i = 0; i < tables.size(); i++) {
                     String db = (String) tables.elementAt(i);
                     TableList += "\"" + db + "\""; 

                     if (i < tables.size() - 1)
                         TableList += ", ";
             }

             Done = true;
            }

            setVisible(false);
            return;
         }

        if (actionevent.getActionCommand().equals("CANCEL"))
            setVisible(false);
    }

   public Vector getVector() {
     return tables;
   }

   public boolean isWellDone() {
     return Done;
   }

   public String getTableList() {
     return TableList;
   }

   public boolean isNum(String s) {

        for(int i = 0; i < s.length(); i++) {

            char c = s.charAt(i);

            if (!Character.isDigit(c))
                return false;
         }

        return true;
    }

}
