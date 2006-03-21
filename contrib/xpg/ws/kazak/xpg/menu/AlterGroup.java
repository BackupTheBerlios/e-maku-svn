/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
* 
* CLASS AlterGroup v 0.1
* Descripcion:   
* Esta clase se encarga de manejar el dialogo que permite
* alterar los parametros de un grupo del SMBD. 
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
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.idiom.Language;

public class AlterGroup extends JDialog implements ActionListener {

 JTextArea LogWin;
 PGConnection conn;
 JList usrList,groupList;
 Vector user;
 JComboBox nameText;
 Vector newU = new Vector();
 Vector deathU = new Vector();

public AlterGroup(JFrame aFrame, PGConnection pg, JTextArea area) {

  super(aFrame);
  conn = pg;
  LogWin = area;

  setTitle(Language.getWord("MODGRP"));
  String[] grupo = conn.getUsers();
  JPanel rowName = new JPanel();
  JLabel nameLabel = new JLabel(Language.getWord("NAMEGRP"));

  String[] values = conn.getGroups();

  nameText = new JComboBox(values);
  nameText.setActionCommand("COMBO");
  nameText.addActionListener(this);

  user = conn.getGroupUser(values[0]);

  rowName.setLayout(new FlowLayout(FlowLayout.CENTER));
  rowName.add(nameLabel);
  rowName.add(nameText);

  usrList = new JList (user);
  usrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  JScrollPane componente = new JScrollPane(usrList);
  componente.setPreferredSize(new Dimension(100,120));   
  
  groupList = new JList (grupo);
  groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  JScrollPane componente2 = new JScrollPane(groupList);
  componente2.setPreferredSize(new Dimension(100,120));   

  URL imgURL = getClass().getResource("/icons/16_Right.png");
  JButton addUserButton = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  addUserButton.setVerticalTextPosition(AbstractButton.CENTER);
  addUserButton.setActionCommand("RIGHT");
  addUserButton.addActionListener(this);

  imgURL = getClass().getResource("/icons/16_Left.png");
  JButton dropUserButton = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
  dropUserButton.setVerticalTextPosition(AbstractButton.CENTER);
  dropUserButton.setActionCommand("LEFT");
  dropUserButton.addActionListener(this);

  JPanel verticalButton = new JPanel();
  verticalButton.setLayout(new BoxLayout(verticalButton,BoxLayout.Y_AXIS));
  verticalButton.add(Box.createVerticalGlue());
  verticalButton.add(addUserButton);
  verticalButton.add(dropUserButton);
  verticalButton.add(Box.createVerticalGlue());
  verticalButton.setAlignmentY(CENTER_ALIGNMENT);

  JButton okButton = new JButton(Language.getWord("MODGR"));
  okButton.setActionCommand("OK");
  okButton.addActionListener(this);
  okButton.setMnemonic('A');
  okButton.setAlignmentX(CENTER_ALIGNMENT);

  JButton cancelButton = new JButton(Language.getWord("CANCEL"));
  cancelButton.setActionCommand("CANCEL");
  cancelButton.addActionListener(this);
  cancelButton.setMnemonic('A');
  cancelButton.setAlignmentX(CENTER_ALIGNMENT);

  JPanel horizontalButton = new JPanel();
  horizontalButton.setLayout(new FlowLayout());
  horizontalButton.add(okButton);
  horizontalButton.add(cancelButton);

  JPanel groupAux = new JPanel();
  groupAux.setLayout(new BorderLayout());      
  groupAux.add(componente,BorderLayout.EAST);
  groupAux.add(verticalButton,BorderLayout.CENTER);
  groupAux.add(componente2,BorderLayout.WEST);

  Border etched = BorderFactory.createEtchedBorder();
  TitledBorder title = BorderFactory.createTitledBorder(etched);
  groupAux.setBorder(title);

  JPanel groupPanel = new JPanel();
  groupPanel.setLayout(new BoxLayout(groupPanel,BoxLayout.Y_AXIS));
  groupPanel.add(rowName);
  groupPanel.add(Box.createRigidArea(new Dimension(0,10)));
  groupPanel.add(groupAux);
  groupPanel.add(horizontalButton);

  JPanel groupTotal = new JPanel();
  groupTotal.add(groupPanel);
  groupTotal.setBorder(title);

  getContentPane().add(groupTotal);
  pack();
  setLocationRelativeTo(aFrame);
  setVisible(true);
 }


 public void actionPerformed(java.awt.event.ActionEvent e) {

   if (e.getActionCommand().equals("COMBO")) {

       String groupN = (String) nameText.getSelectedItem();
       user = conn.getGroupUser(groupN);
       usrList.setListData(user);
       newU = new Vector();
       deathU = new Vector();

       return;
    }

   if (e.getActionCommand().equals("RIGHT")) {

       String userName = (String) groupList.getSelectedValue();

       if (!user.contains(userName)) {
           user.addElement(userName);
           usrList.setListData(user);
           newU.addElement(userName);
        }

       return;
    }

   if (e.getActionCommand().equals("LEFT")) {

       String userName = (String) usrList.getSelectedValue();

       if (user.removeElement(userName)) {

           usrList.setListData(user);
           deathU.addElement(userName);
        }

       return;    
    }

   if (e.getActionCommand().equals("OK")) {

       String nameG = (String) nameText.getSelectedItem();

       String SQL = "ALTER GROUP " + nameG;
       String next = "";

       if (newU.size()>0) {

           String addU = "";

           for (int m=0;m<newU.size();m++) {

                addU += (String) newU.elementAt(m);

                if (m!=newU.size()-1)
                    addU += ","; 
            }

           String result = conn.SQL_Instruction(SQL + " ADD USER " + addU + ";");

           if (!result.equals("OK")) {

               JOptionPane.showMessageDialog(AlterGroup.this,
               result,
               Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

               return; 
            }
        }

       if (deathU.size()>0) {

           String kill = "";

           for (int m=0;m<deathU.size();m++) {

                kill += (String) deathU.elementAt(m);

                if (m!=deathU.size()-1)
                    kill += ","; 
            }

           SQL += " DROP USER " + kill + ";";

           String result = conn.SQL_Instruction(SQL);
           addTextLogMonitor(Language.getWord("EXEC") + SQL + "\"");
           addTextLogMonitor(Language.getWord("RES") + result);

           if (!result.equals("OK")) {

               JOptionPane.showMessageDialog(AlterGroup.this,
               result,
               Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

               return; 
            }
        } 

       setVisible(false);

       return;		    
  }

 if (e.getActionCommand().equals("CANCEL")) {

     setVisible(false);					    
  }

 }

 /**
  * Metodo addTextLogMonitor
  * Imprime mensajes en el Monitor de Eventos
  */
 public void addTextLogMonitor(String msg) {

   LogWin.append(msg + "\n");	
   int longiT = LogWin.getDocument().getLength();

   if (longiT > 0)
       LogWin.setCaretPosition(longiT - 1);
  }

} //Fin de la Clase
