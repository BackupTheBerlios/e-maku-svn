/**
* Disponible en http://www.kazak.ws
*
* Desarrollado por Soluciones KAZAK 
* Grupo de Investigacion y Desarrollo de Software Libre
* Santiago de Cali/Republica de Colombia 2001
*
* CLASS ErrorDialog v 0.1                                                   
* Descripcion:
* Esta clase se encarga de manejar el dialogo que muestra
* los diferentes mensajes de error debido a excepciones en
* los procedimientos con el SMBD.
*
* Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
*                                                                   
* Fecha: 2001/10/01                                                 
*
* Autores: Beatriz Florián  - bettyflor@kazak.ws                    
*          Gustavo Gonzalez - xtingray@kazak.ws                     
*/
package ws.kazak.xpg.misc.input;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import ws.kazak.xpg.idiom.Language;

public class ErrorDialog extends JDialog {

 private JOptionPane optionPane;
 JSplitPane splitError;
 String[] atrib = new String[3];
 
 /********** METODO CONSTRUCTOR **********/
 public ErrorDialog (JDialog parent, String[] messages)
  {        
    super(parent, true);
    atrib = messages;
    setTitle(Language.getWord("NUMERROR") + atrib[0]); 
    makeSplit();                          
                      
    final String btnString1 = Language.getWord("OK");                         
    Object[] options = {btnString1};                                  
    Object[] info_error = {splitError};
    optionPane = new JOptionPane(info_error,  JOptionPane.PLAIN_MESSAGE,                                
                                 JOptionPane.YES_NO_OPTION, null,                                 
                                 options, options[0]);
    setContentPane(optionPane);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));                                        
      }
    }                 );
    
    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
    	public void propertyChange(PropertyChangeEvent e) {				
    	  String prop = e.getPropertyName();                 		               						
    	  if (isVisible()&& (e.getSource() == optionPane)    							
        		 && (prop.equals(JOptionPane.VALUE_PROPERTY) || 						        
        		 prop.equals(JOptionPane.INPUT_VALUE_PROPERTY)))				         
     	  {                                                 								
       	    Object value = optionPane.getValue();           					                  
       	    if (value == JOptionPane.UNINITIALIZED_VALUE)   								
             {                                              								
              //ignorar reset                            			                          
              return;                                   								
             }                                              							  
       	    if (value.equals(btnString1))
             {
              setVisible(false);   
	     }                    
       	    else
             { 
	      setVisible(false);
	     } 
      	  } 
      }
    });
    setSize(320,200);
  }//FIN METODO CONSTRUCTOR
  
 /******************** METODO makeSplit ********************/
 public void makeSplit() {
  //Parte superior del Split 
  //Un panel con un JLabel en el norte y otro JLabel en el centro
  JPanel panelDesc = new JPanel();
  JLabel errorNro = new JLabel(Language.getWord("NUMERROR") + atrib[0] ,JLabel.CENTER);
  JLabel errorDesc = new JLabel(atrib[1]);
  panelDesc.setLayout(new BorderLayout());
  panelDesc.add(errorNro,BorderLayout.NORTH);              
  panelDesc.add(errorDesc,BorderLayout.CENTER);              
  
  //Parte inferior del Split
  //Un panel con un JLabel en el norte y un JScrollPane en el centro
  JLabel details = new JLabel(Language.getWord("DETAILS"));
  String noTabs = atrib[2].replace('\t',' ');
  JTextArea detail = new JTextArea(noTabs, 5, 10);
  detail.setEditable(false);
  detail.setLineWrap(true);
  detail.setWrapStyleWord(true);
  detail.setCaretPosition(0);
  JPanel panelDetails = new JPanel();
  panelDetails.setLayout(new BorderLayout());
  panelDetails.add(detail,BorderLayout.CENTER);
  JScrollPane panelScroll = new JScrollPane(panelDetails); 
  
  JPanel panelBottom = new JPanel(); 
  panelBottom.setLayout(new BorderLayout());
  panelBottom.add(details,BorderLayout.NORTH);              
  panelBottom.add(panelScroll,BorderLayout.CENTER);              

  splitError = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
  splitError.setTopComponent(panelDesc);
  splitError.setBottomComponent(panelBottom);
  splitError.setOneTouchExpandable(true); 
  
  panelDesc.setMinimumSize(new Dimension(460,50));
  panelBottom.setMinimumSize(new Dimension(460,200));

  splitError.setDividerLocation(50); //Selecciona u obtiene la posición actual del divisor.   
  splitError.setPreferredSize(new Dimension(460,200)); // Da un tamaño por defecto al split
 }

}  
