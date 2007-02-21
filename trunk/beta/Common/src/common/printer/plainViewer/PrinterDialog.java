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

public class PrinterDialog extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = -2549815723035494574L;
	JComboBox printers;
	Vector <String>printerNames;
	JTextField pages;
	JTextField copiesTF;
	JRadioButton all;
	JRadioButton range;
	ByteArrayOutputStream[] printerViews;
	int pagesTotal = 0;
	int copiesTotal = 0;
	String printer;
	
	public PrinterDialog(GenericForm parent, int pagesTotal, ByteArrayOutputStream[] printerViews) {
	    this.printerViews = printerViews;
	    this.pagesTotal = pagesTotal;
		this.setTitle("Printer Options");
		this.setAlwaysOnTop(true);
		printerNames = new Vector<String>();
				
		for (PrintService ps : PrintServiceLookup.lookupPrintServices(null,null)) {
			printerNames.add(ps.getName());	
		}
		
		/**
         * Inicializando servicio de impresion
         */
        CommonConstants.lookupDefaultPrintService();
         
		JLabel printerName = new JLabel("Printer Name:");
		printers = new JComboBox(printerNames);
		JButton accept = new JButton("Accept");
		accept.addActionListener(this);
		accept.setActionCommand("PRINT");
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setActionCommand("CLOSE");
		
	    ButtonGroup rangeGroup = new ButtonGroup();
		all = new JRadioButton("All");
		all.setSelected(true);
		range = new JRadioButton("Range");
		rangeGroup.add(all);
		rangeGroup.add(range);
		
		pages = new JTextField(14);
		JLabel copies = new JLabel("Copies:");
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
	                                BorderFactory.createTitledBorder("Printing Area"),
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

			printer = (String) printers.getSelectedItem();
			System.out.println("Printer: " + printer);
			
			// Getting number of copies
			String copiesNum = copiesTF.getText();
            if (TextReportUtils.isNumber(copiesNum)) {
            	copiesTotal = Integer.parseInt(copiesNum);
            } 
            else {
            	  copiesTF.setText("1");
            	  copiesTF.requestFocus();
            	  JOptionPane.showMessageDialog(this,"El numero de copias de impresion debe ser un valor numerico.","Error!",
                        JOptionPane.ERROR_MESSAGE);
            	  return;
            }

            System.out.println("Printing...");
            
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
            			JOptionPane.showMessageDialog(this,"1. El rango de paginas a imprimir debe ser de la forma A-B,C-D,F,G-H","Error!",
            					JOptionPane.ERROR_MESSAGE);
            			return;            		
            		}
            		else {
            				StringTokenizer tokens = new StringTokenizer(secuence,",");
            				int parameters = tokens.countTokens();
            				Vector<int[]> pairs = new Vector<int[]>();
            	    
            				System.out.println("Parametros: " + parameters);
            				
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
            								    			JOptionPane.showMessageDialog(this,"2. El rango de paginas a imprimir debe ser de la forma A-B,C-D,F,G-H"
            								    					,"Error!", JOptionPane.ERROR_MESSAGE);
            								    			return;            									
            								    	}
            								    } else {
            								    		JOptionPane.showMessageDialog(this,"3. El rango de paginas a imprimir debe ser de la forma A-B,C-D,F,G-H"
            								    				,"Error!", JOptionPane.ERROR_MESSAGE);
            								    		return;
            								    }
            							  } // while end
            							  if (j>2) {
            								  JOptionPane.showMessageDialog(this,"4. El rango de paginas a imprimir debe ser de la forma A-B,C-D,F,G-H"
            										  ,"Error!", JOptionPane.ERROR_MESSAGE);
            								  return;
            							  }
            							  if (numbers[0] > numbers[1]) {
            								  JOptionPane.showMessageDialog(this,"5. El rango de paginas a imprimir debe ser de la forma A-B,C-D,F,G-H"
            										  ,"Error!", JOptionPane.ERROR_MESSAGE);
            								  return;            							
            							  }
            							  pairs.add(numbers); 
            					} 
            					else {
            						    // One page process
            						    System.out.println("Una hoja: " + miniRange);
            							if (TextReportUtils.isNumber(miniRange)) {
            								int index = Integer.parseInt(miniRange);
            								numbers[0] = index;
            								numbers[1] = index;
            								pairs.add(numbers);
            							} else {
                							JOptionPane.showMessageDialog(this,"6. El rango de paginas a imprimir debe ser de la forma A-B,C-D,F,G-H"
                               	        		  ,"Error!", JOptionPane.ERROR_MESSAGE);
                  							return;            								
            							}
            					}
            				} // While end
            					
                      		System.out.println("*** Printing pages...");
                      		
                      		for(int i=0;i<pairs.size();i++) {
                      		    int[] indexes = (int[]) pairs.get(i);
                      		    if (indexes[0] == indexes[1])
                      		    	printOnePage(indexes[0]-1);
                      		    else	
                      		        printPagesRange(indexes[0]-1,indexes[1]-1);
                      		}
            			} // if end
            				else {
            						JOptionPane.showMessageDialog(this,"6. El rango de paginas a imprimir debe ser de la forma A-B,C-D,F,G-H"
            								,"Error!", JOptionPane.ERROR_MESSAGE);
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
    		ByteArrayInputStream currentPage = new ByteArrayInputStream(printerViews[i].toByteArray());
    		try {
				 new PrintingManager(ImpresionType.PLAIN,currentPage,true,1,printer);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (PrintException e) {
				e.printStackTrace();
			}
    	}
	}
	
	// Print one page
    public void printOnePage(int index) {
    	ByteArrayInputStream currentPage = new ByteArrayInputStream(printerViews[index].toByteArray());
		try {
			 new PrintingManager(ImpresionType.PLAIN,currentPage,true,1,printer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PrintException e) {
			e.printStackTrace();
		}
    }
}