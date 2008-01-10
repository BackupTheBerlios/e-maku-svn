package common.printer.plainViewer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Vector;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;

import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import common.gui.forms.GenericForm;
import common.misc.CommonConstants;
import common.printer.PrintingManager;
import common.printer.PrintingManager.ImpresionType;

import common.misc.language.Language;

/**
 * PlainReportPrinterDialog.java Creado el 1 de Febrero 2007
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 * @author <A href='mailto:xtingray@qhatu.net'>Gustavo González</A>
 */

// Printer Dialog for plain text reports

public class PlainReportPrinterDialog extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = -2549815723035494574L;
	JComboBox printers;
	Vector <String>printerNames;
	JTextField pages;
	JTextField copiesTF;
	JRadioButton all;
	JRadioButton range;
	Vector <ByteArrayOutputStream>printerViews;
	int pagesTotal = 0;
	int copiesTotal = 0;
	String printer;
	int charactersPerLine;
	
	
	// Class Constructor
	public PlainReportPrinterDialog(GenericForm parent, int pagesTotal, Vector <ByteArrayOutputStream>printerViews, int charactersPerLine) {
	    this.printerViews = printerViews;
	    this.pagesTotal = pagesTotal;
	    this.charactersPerLine = charactersPerLine;
		this.setTitle(Language.getWord("PRINTER_OP"));
		this.setAlwaysOnTop(true);
		
		printerNames = new Vector<String>();
				
		for (PrintService ps : PrintServiceLookup.lookupPrintServices(null,null)) {
			printerNames.add(ps.getName());	
		}
		
		/**
         * Inicializando servicio de impresion
         */
        CommonConstants.lookupDefaultPrintService();
         
		JLabel printerName = new JLabel(Language.getWord("PRINTER_NAME"));
		printers = new JComboBox(printerNames);
		JButton accept = new JButton(Language.getWord("ACCEPT"));
		accept.addActionListener(this);
		accept.setActionCommand("PRINT");
		
		JButton cancel = new JButton(Language.getWord("CANCEL"));
		cancel.addActionListener(this);
		cancel.setActionCommand("CLOSE");
		
	    ButtonGroup rangeGroup = new ButtonGroup();
		all = new JRadioButton(Language.getWord("ALL"));
		all.setSelected(true);
		range = new JRadioButton(Language.getWord("RANGE"));
		rangeGroup.add(all);
		rangeGroup.add(range);
		
		pages = new JTextField(14);
		JLabel copies = new JLabel(Language.getWord("COPIES"));
        copiesTF = new JTextField("1",2);		
		
		JPanel base = new JPanel();
		base.setLayout(new BorderLayout());
		
		JPanel upSide = new JPanel();
		upSide.setLayout(new FlowLayout());
		upSide.add(printerName);
		upSide.add(printers);
		
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new BorderLayout());
		westPanel.add(upSide, BorderLayout.WEST);
				
		JPanel rangeLinePanel = new JPanel();
		rangeLinePanel.setLayout(new FlowLayout());
		rangeLinePanel.add(pages);
		
		JLabel proof = new JLabel(" ");
		JPanel panel = new JPanel();
		panel.add(proof);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(panel);
		rightPanel.add(rangeLinePanel);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(all);
		leftPanel.add(range);
		
		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new FlowLayout());
		gridPanel.add(leftPanel);
		gridPanel.add(rightPanel);
	
		JPanel copiesPanel = new JPanel();
		copiesPanel.setLayout(new FlowLayout());
		copiesPanel.add(copies);
		copiesPanel.add(copiesTF);
		
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BorderLayout());
		eastPanel.add(copiesPanel,BorderLayout.EAST);

		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		center.add(gridPanel,BorderLayout.CENTER);
		center.add(eastPanel,BorderLayout.SOUTH);
		center.setBorder(
	            BorderFactory.createCompoundBorder(
	                BorderFactory.createCompoundBorder(
	                                BorderFactory.createTitledBorder(Language.getWord("PRINTING_AREA")),
	                                BorderFactory.createEmptyBorder(5,5,5,5)),
	                center.getBorder()));
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		buttons.add(accept);
		buttons.add(cancel);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BorderLayout());
		buttonsPanel.add(buttons,BorderLayout.EAST);
		
		base.add(westPanel,BorderLayout.NORTH);
		base.add(center,BorderLayout.CENTER);
		base.add(buttonsPanel,BorderLayout.SOUTH);
		
		this.add(base);
		this.pack();
		this.setLocationRelativeTo(parent);
	}
	
	// This method handles events
	public void actionPerformed(ActionEvent e) {
		
		// Close the Report Viewer
		if (e.getActionCommand().equals("CLOSE")) {
			setVisible(false);
			return;
		}

		// Print selected pages
		if (e.getActionCommand().equals("PRINT")) {

			String errorMessage = Language.getWord("BAD_RANGE");
			printer = (String) printers.getSelectedItem();
			
			// Getting number of copies
			String copiesNum = copiesTF.getText();
            if (TextReportUtils.isNumber(copiesNum)) {
            	copiesTotal = Integer.parseInt(copiesNum);
            } 
            else {
            	  copiesTF.setText("1");
            	  copiesTF.requestFocus();
            	  JOptionPane.showMessageDialog(this,Language.getWord("BAD_COPIES"),Language.getWord("ERROR"),
                        JOptionPane.ERROR_MESSAGE);
            	  return;
            }
            
            if (all.isSelected()) {
            	// Print all the pages
            	this.setVisible(false);
            	for (int i=0;i<copiesTotal;i++) {
            		 printPagesRange(0,pagesTotal-1);
            	}

            } else {
            		// Print a range
            		String secuence = pages.getText();
            		if (secuence.length() == 0) {
            			pages.setText("");
            			pages.requestFocus();
            			JOptionPane.showMessageDialog(this,errorMessage,Language.getWord("ERROR"),
            					JOptionPane.ERROR_MESSAGE);
            			return;            		
            		}
            		else {
            				StringTokenizer tokens = new StringTokenizer(secuence,",");
            				int parameters = tokens.countTokens();
            				Vector<int[]> pairs = new Vector<int[]>();
            				
            				if (parameters > 0) {
            					while(tokens.hasMoreTokens()){
            	       
            						  String miniRange = tokens.nextToken();
            						  int[] numbers = new int[2];
                	                					
            						  if (miniRange.indexOf("-") != -1) {
            							  StringTokenizer split = new StringTokenizer(miniRange,"-");
            							  int j=0;
            							  while(split.hasMoreTokens()){
            								    String limit = split.nextToken();
            								    if (limit.length() > 0) {
            								    	if (TextReportUtils.isNumber(limit)) {
            								    		numbers[j] = Integer.parseInt(limit);
            								    		j++;
            								    	} else {
            								    			JOptionPane.showMessageDialog(this,errorMessage
            								    					,Language.getWord("ERROR"), JOptionPane.ERROR_MESSAGE);
            								    			return;            									
            								    	}
            								    } else {
            								    		JOptionPane.showMessageDialog(this,errorMessage
            								    				,Language.getWord("ERROR"), JOptionPane.ERROR_MESSAGE);
            								    		return;
            								    }
            							  } // while end
            							  if (j>2) {
            								  JOptionPane.showMessageDialog(this,errorMessage
            										  ,Language.getWord("ERROR"), JOptionPane.ERROR_MESSAGE);
            								  return;
            							  }
            							  if (numbers[0] > numbers[1]) {
            								  JOptionPane.showMessageDialog(this,errorMessage
            										  ,Language.getWord("ERROR"), JOptionPane.ERROR_MESSAGE);
            								  return;            							
            							  }
            							  pairs.add(numbers); 
            					} 
            					else {
            						    // One page process
            							if (TextReportUtils.isNumber(miniRange)) {
            								int index = Integer.parseInt(miniRange);
            								numbers[0] = index;
            								numbers[1] = index;
            								pairs.add(numbers);
            							} else {
                							JOptionPane.showMessageDialog(this,errorMessage
                               	        		  ,Language.getWord("ERROR"), JOptionPane.ERROR_MESSAGE);
                  							return;            								
            							}
            					}
            				} // While end
                      		
                      		for(int i=0;i<pairs.size();i++) {
                      		    int[] indexes = (int[]) pairs.get(i);
                      		    if (indexes[0] == indexes[1])
                      		    	printOnePage(indexes[0]-1);
                      		    else	
                      		        printPagesRange(indexes[0]-1,indexes[1]-1);
                      		}
            			} // if end
            				else {
            						JOptionPane.showMessageDialog(this,errorMessage
            								,Language.getWord("ERROR"), JOptionPane.ERROR_MESSAGE);
        							return;            								
            				}		
            		} // else end
            } // else end
            
			return;
		}
		
	}

	// Print a range of pages
	public void printPagesRange(int firstPage,int lastPage) {
    	for (int i=firstPage;i<=lastPage;i++) {
    		 printOnePage(i);
    	}
	}
	
	// Print one page
    public void printOnePage(int index) {
    	ByteArrayOutputStream byteArray = (ByteArrayOutputStream) printerViews.elementAt(index);
    	String text = byteArray.toString();
    	String pageFooter = TextReportUtils.getRightAlignedString(Language.getWord("PAGE") + " " + (index + 1) + " " + Language.getWord("OF") + " " + pagesTotal,charactersPerLine);
        text += pageFooter;
    	
    	ByteArrayInputStream currentPage = new ByteArrayInputStream(text.getBytes());
		try {
			 new PrintingManager(ImpresionType.PLAIN,currentPage,true,1,printer,0,0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PrintException e) {
			e.printStackTrace();
		}
    }


}