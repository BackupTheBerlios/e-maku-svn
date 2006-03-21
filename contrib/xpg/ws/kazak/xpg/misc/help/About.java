/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS About v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el dialogo mediante el
* cual se muestra la version de XPg. 
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.misc.help;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ws.kazak.xpg.idiom.Language;

public class About extends JDialog {

public About(JFrame aFrame) {

   super(aFrame, true);
   setTitle(Language.getWord("TITABOUT"));

   URL imgURL = getClass().getResource("/icons/about.png");

   JLabel image = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)),JLabel.CENTER);

   JButton ok = new JButton(Language.getWord("CLOSE"));
   ok.setHorizontalAlignment(SwingConstants.CENTER);
   ok.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });

   JPanel up = new JPanel();
   up.setLayout(new FlowLayout());
   up.add(image);

   JPanel down = new JPanel();
   down.setLayout(new FlowLayout());
   down.add(ok);

   JPanel global = new JPanel();
   global.setLayout(new BoxLayout(global,BoxLayout.Y_AXIS));
   global.add(up);
   global.add(down);
   getContentPane().add(global);

   pack();
   setLocationRelativeTo(aFrame);
 }

}
