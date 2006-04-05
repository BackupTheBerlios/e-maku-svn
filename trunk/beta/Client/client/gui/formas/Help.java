package jmclient.gui.formas;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import jmclient.gui.components.MainWindow;

import jmlib.miscelanea.idiom.Language;

public class Help extends JInternalFrame {
	
	private static final long serialVersionUID = 3002744150779988330L;
	
	public Help() {
	
	  JPanel base = new JPanel();
	  JLabel title = new JLabel("Contenido");
	  base.add(title);
	  
      this.setClosable(true);
	  
	  this.add(base);
      this.setTitle(Language.getWord("HELP"));
      this.setVisible(true);
      this.setSize(400,300);
      this.getContentPane().add(base);
      
      MainWindow.getJDPanel().add(this);
      try {
          this.setSelected(true);
      }
      catch (java.beans.PropertyVetoException PVEe) {
      }
      
	}

}
