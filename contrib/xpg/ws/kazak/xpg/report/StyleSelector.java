/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS StyleSelector v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el dialogo mediante el
* cual se pueden visualizar y escoger los tipos de apariencias 
* predefinidas para crear un reporte.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*          Angela Sandobal  - angesand@libertad.univalle.edu.co     
*/
package ws.kazak.xpg.report;

import ws.kazak.xpg.idiom.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

public class StyleSelector extends JDialog implements ActionListener,ListSelectionListener{

 JLabel picture;
 int indice;
 boolean wellDone = false;
 String xpgHome = "";

 public StyleSelector(JDialog dialog)
  {
  super(dialog, true);
  xpgHome = System.getProperty("xpgHome") + System.getProperty("file.separator")
   + "styles" + System.getProperty("file.separator");
  setTitle(Language.getWord("PRS"));
  getContentPane().setLayout(new BorderLayout());
  JPanel Global = new JPanel();
  Global.setLayout(new BorderLayout());

  String[] values = {"Classic","Blue","Eternal","Green","Caribean","Steel","Woods","Mostaz",
                     "Century","Roman"};
  JList disenn = new JList(values);
  disenn.setSelectedIndex(0);
  disenn.addListSelectionListener(this);
  JScrollPane marco = new JScrollPane(disenn);
  JPanel panelL = new JPanel();
  panelL.setLayout(new FlowLayout(FlowLayout.CENTER));
  panelL.add(marco);

  JPanel imagen = new JPanel();
  imagen.setLayout(new BorderLayout());
  ImageIcon photo = new ImageIcon(xpgHome + "Image00.png"); 
  indice = 0;
  picture = new JLabel(photo,JLabel.CENTER);
  picture.setPreferredSize(new Dimension(photo.getIconWidth(),
                                               photo.getIconHeight()));
  JScrollPane pictureScroll = new JScrollPane(picture);
  imagen.add(pictureScroll);

  JButton ok = new JButton(Language.getWord("OK"));
  ok.setActionCommand("OK");
  ok.addActionListener(this);
  JButton cancel = new JButton(Language.getWord("CANCEL"));
  cancel.setActionCommand("CANCEL");
  cancel.addActionListener(this);
  JPanel botonD = new JPanel();
  botonD.setLayout(new FlowLayout(FlowLayout.CENTER));
  botonD.add(ok);
  botonD.add(cancel);

  JPanel up = new JPanel();
  up.setLayout(new BorderLayout());
  up.add(imagen,BorderLayout.CENTER);
  up.add(marco,BorderLayout.WEST);
  Border etched1 = BorderFactory.createEtchedBorder();
  TitledBorder title1 = BorderFactory.createTitledBorder(etched1,Language.getWord("CHO"));
  up.setBorder(title1);

  getContentPane().add(up,BorderLayout.CENTER);
  getContentPane().add(botonD,BorderLayout.SOUTH);
  pack();
  setLocationRelativeTo(dialog);
  setVisible(true);
 }

 public void valueChanged(ListSelectionEvent e) 
  {
   if (e.getValueIsAdjusting())
       return;

   JList theList = (JList)e.getSource();
   String pic = (String) theList.getSelectedValue();
   indice = theList.getSelectedIndex();
   ImageIcon newImage = new ImageIcon(xpgHome + "Image0" + indice + ".png");
            picture.setIcon(newImage);
   picture.setPreferredSize(new Dimension(newImage.getIconWidth(),
                                          newImage.getIconHeight() ));
   picture.revalidate();
  }


 public void actionPerformed(java.awt.event.ActionEvent e) 
  {

   if(e.getActionCommand().equals("CANCEL")) 
    {
     setVisible(false);
    }

   if(e.getActionCommand().equals("OK")) 
    {
     wellDone = true;
     setVisible(false);
    }
  }

} //Final de la Clase
