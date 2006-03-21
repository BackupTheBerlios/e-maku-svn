/*
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS SQLFunctionDisplay v 0.1                                                   
* Descripcion:
* Esta clase se encarga de mostrar la ayuda referente a una
* funcion SQL propia de postgres.
*
* Esta clase es instanciada desde la clase Queries.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.queries;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import ws.kazak.xpg.idiom.Language;

public class SQLFunctionDisplay extends JDialog implements ActionListener {

  JButton b1;
  JEditorPane blow;
  String functionName = "";
  boolean isWellDone = false;

  public SQLFunctionDisplay(JFrame app,String html) {

    super(app, true);
    setTitle(Language.getWord("FDTILE"));
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    blow = createEditorPane(html);
    blow.setEditable(false);
    blow.addHyperlinkListener(
         new HyperlinkListener() {

             public void hyperlinkUpdate(final HyperlinkEvent e) {

                if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                    blow.setCursor(new Cursor(Cursor.HAND_CURSOR));
                 }
                else
                   if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
                       blow.setCursor(Cursor.getDefaultCursor());
                    }
                   else
                     if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                         functionName = e.getDescription();
                         isWellDone = true;
                         setVisible(false);
                      }
              }
          }
    );

    panel.add(blow,BorderLayout.CENTER);

    b1 = new JButton(Language.getWord("CLOSE"));
    b1.setVerticalTextPosition(AbstractButton.CENTER);
    b1.setHorizontalTextPosition(AbstractButton.CENTER);
    b1.setActionCommand("CLOSE");
    b1.addActionListener(this);

    JPanel Panel2 = new JPanel();
    Panel2.setLayout(new FlowLayout());
    Panel2.add(b1);

    JPanel control = new JPanel();
    control.add(Panel2);
    panel.add(b1,BorderLayout.SOUTH);

    setContentPane(panel);
    pack();
    setLocationRelativeTo(app);
    show();
    
   }


  public JEditorPane createEditorPane(String FileName) {

    JEditorPane editorPane = new JEditorPane("text/html",FileName);
    editorPane.setEditable(false);

    return editorPane;
   }

  public void actionPerformed(ActionEvent e) {

    if (e.getActionCommand().equals("CLOSE")) {
        setVisible(false);
        return;
     }
  }

  public boolean isUsed() {
     return isWellDone;
   }

  public String getFuncName() {
     return functionName;
   }

}
