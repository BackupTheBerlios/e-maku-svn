package common.printer.plainViewer;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JEditorPane;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
//import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
//import java.util.Vector;

import org.jdom.Element;
import common.misc.ZipHandler;

import common.control.ClientHeaderValidator;
import common.control.ReportEvent;
import common.control.ReportListener;
import common.gui.forms.GenericForm;

// This class shows a text report view 

public class TextReportViewer extends JInternalFrame implements ActionListener,ReportListener {
		
	private static final long serialVersionUID = 7423353291901251301L;
	
	GenericForm GFforma;
	String idReport;
	
	ByteArrayOutputStream[] reportViews;
	ByteArrayOutputStream[] printerViews;
	
	ByteArrayInputStream textFile;
	String reportName = "";
	byte[] bytes;
	int pages = 0;
	
	JEditorPane reportArea;
	JButton printerButton   = new JButton();
	JButton closeButton     = new JButton();
	
	JButton firstPage       = new JButton();
	JButton backTenPages    = new JButton();
	JButton backOnePage     = new JButton();
	JButton forwardOnePage  = new JButton();
	JButton forwardTenPages = new JButton();
	JButton lastPage        = new JButton();
	
	JTextField currentPageTF = new JTextField(4);
	JLabel total = new JLabel("");
	int currentPage = 1;
	int currentIndex = 0;
	
	private Element data;
	private ZipHandler zip;
	
	// Constructor
	public TextReportViewer(GenericForm GFforma, String idReport) {
		this.GFforma = GFforma;
		this.idReport = idReport;
		ClientHeaderValidator.addReportListener(this);
		this.zip = new ZipHandler();
		
		openReport();
		
/*		this.reportViews = reportViews;
		this.printerViews = printerViews;
		pages = reportViews.length;

		if (pages == 0) {
			currentPage = 0;
		}
		total.setText("de " + pages); */
	}
	
	// Sets the graphic interface for the viewer
	public void openReport() {
		setTitle("Text File Report Viewer");
		
		JPanel global = new JPanel();
		
		global.setLayout(new BorderLayout());		
        global.add(setButtonsPanel(), BorderLayout.NORTH);        
        global.add(setViewerArea(), BorderLayout.CENTER);
        global.add(setNavigationPanel(), BorderLayout.SOUTH);

        setDefaultCloseOperation(JInternalFrame.EXIT_ON_CLOSE);
        setContentPane(global);
        pack();
        setSize(500,650);
	}

	// Sets the top panel 
	public JPanel setButtonsPanel() {
		
		JPanel buttons = new JPanel();
		JPanel container = new JPanel();
		container.setLayout(new FlowLayout());
		
		printerButton.setIcon(new ImageIcon(this.getClass().getResource("/icons/ico_impresora.png")));
		closeButton.setIcon(new ImageIcon(this.getClass().getResource("/icons/ico_cancelar.png")));
		
		closeButton.addActionListener(this);
		closeButton.setActionCommand("CLOSE");
		printerButton.addActionListener(this);
		printerButton.setActionCommand("PRINT");
		
		container.add(printerButton);
		container.add(closeButton);

		Border border = BorderFactory.createEtchedBorder();
		container.setBorder(border);
		
		buttons.add(container);
		return buttons;	
	}
	
	// Sets the botton panel
	public JPanel setNavigationPanel() {
		JPanel navigation = new JPanel();
		JLabel page = new JLabel("Pagina ");
		currentPageTF.setText("" + currentPage);
		currentPageTF.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String value = currentPageTF.getText().trim();
				if (isANumber(value)) {
				    int pageNum = Integer.parseInt(value);
				    setReportPage(pageNum-1);
				    setControls(pageNum-1);
				} else {
					setReportPage(currentPage-1);
				}
			}
		});
		
		JPanel container = new JPanel();
		container.setLayout(new FlowLayout());

		firstPage.setIcon(new ImageIcon(this.getClass().getResource("/icons/ico_flecha_inicio.png")));
		backTenPages.setIcon(new ImageIcon(this.getClass().getResource("/icons/ico_flecha2_izq.png")));
		backOnePage.setIcon(new ImageIcon(this.getClass().getResource("/icons/ico_flecha_izq.png")));
		forwardOnePage.setIcon(new ImageIcon(this.getClass().getResource("/icons/ico_flecha_der.png")));
		forwardTenPages.setIcon(new ImageIcon(this.getClass().getResource("/icons/ico_flecha2_der.png")));
		lastPage.setIcon(new ImageIcon(this.getClass().getResource("/icons/ico_flecha_fin.png")));

		firstPage.addActionListener(this);
		firstPage.setActionCommand("PAGE_ONE");
		backTenPages.addActionListener(this);
		backTenPages.setActionCommand("BACK_TEN");
		backOnePage.addActionListener(this);
		backOnePage.setActionCommand("BACK_ONE");
		forwardOnePage.addActionListener(this);
		forwardOnePage.setActionCommand("FWD_ONE");
		forwardTenPages.addActionListener(this);
		forwardTenPages.setActionCommand("FWD_TEN");
		lastPage.addActionListener(this);
		lastPage.setActionCommand("FWD_LAST");
		
		container.add(firstPage);
		container.add(backTenPages);
		container.add(backOnePage);
		
		container.add(page);
        container.add(currentPageTF);		
        container.add(total);
        
        container.add(forwardOnePage);
		container.add(forwardTenPages);
		container.add(lastPage);
		
		navigation.add(container);
		setBackControls(false);
		
		return navigation;
	}	

	// Set the viewer area
	public JPanel setViewerArea() {
		
		JPanel mainArea = new JPanel();
		mainArea.setLayout(new BorderLayout());
		        
        reportArea = new JEditorPane("text/html","");
        reportArea.setFont(new Font("Mono", Font.PLAIN, 12));
        reportArea.setEditable(false);
        
        JScrollPane areaScrollPane = new JScrollPane(reportArea);
        areaScrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        mainArea.add(areaScrollPane, BorderLayout.CENTER);
         
        return mainArea;
	}
	
	// This method handles events
	public void actionPerformed(ActionEvent e) {
		
		// Close the Report Viewer
		if (e.getActionCommand().equals("CLOSE")) {
			setVisible(false);
			return;
		}

		// Print text plain file
		if (e.getActionCommand().equals("PRINT")) {
			System.out.println("Printing file...");
			PrinterDialog dialog = new PrinterDialog(GFforma,pages,printerViews);
            dialog.setVisible(true);
            
			return;
		}
		
		// Go to page #1
		if (e.getActionCommand().equals("PAGE_ONE")) {
			if(currentPage == pages) {
				setFwdControls(true);
			}
			currentPage = 1;
			setBackControls(false);
			setReportPage(0);
			return;
		}
		// Go back 10 pages
		if (e.getActionCommand().equals("BACK_TEN")) {
			if (currentPage > 10) {
				if ((currentPage > pages-10)) {
	           	     forwardTenPages.setEnabled(true);
	             }
			    currentPage -= 10;
	            if (currentPage < 10) {
	            	if (currentPage == 1) {
               		    setBackControls(false);
               	     }
	            	else {
		            	backTenPages.setEnabled(false);
	            	}
	            } else {			
	                if ((currentPage == pages-10)) {
	            	    setFwdControls(true);
	                 } 
	               }
			} else {
            	currentPage = 1;
    			setBackControls(false);
            }       
		    setReportPage(currentPage-1);
			return;
		}
		// Go back 1 page
		if (e.getActionCommand().equals("BACK_ONE")) {
			currentPage--;
			if (currentPage == 1) {
    			setBackControls(false);
			} else {
			   if (currentPage == pages-1) {
    			   lastPage.setEnabled(true);
    			   forwardOnePage.setEnabled(true);		
			       } else {
		               if (currentPage == pages-10) {
    			           forwardTenPages.setEnabled(true);		
			            } else {
			            	if (currentPage == 10) {
			            		backTenPages.setEnabled(false);
			            	}
			            }
			          }
			}
			setReportPage(currentPage-1);
			return;
		}
		// Go forward 1 page
		if (e.getActionCommand().equals("FWD_ONE")) {
			currentPage++;
			if (currentPage == 2) {
				firstPage.setEnabled(true);
    			backOnePage.setEnabled(true);		
			} else {
			        if (currentPage == 11) {
    			        backTenPages.setEnabled(true);
			         } else { 
			    	         if (currentPage >= pages) {
				                 currentPage = pages;
				                 setFwdControls(false);							
			                 } else {
			                         if (currentPage == pages - 10) {
				                         forwardTenPages.setEnabled(false);							
			                          }
			                     } 
			             }
			}
			setReportPage(currentPage-1);
			return;
		}
		// Go forward 10 pages
		if (e.getActionCommand().equals("FWD_TEN")) {
			if (currentPage < 10) {
				setBackControls(true);
			}
			currentPage += 10;
			if (currentPage >= pages) {
				currentPage = pages;
				setFwdControls(false);							
			} else {
				if (currentPage >= pages - 10) {
				    forwardTenPages.setEnabled(false);	
				}
			}
			setReportPage(currentPage-1);			
			return;
		}
		// Go to last page
		if (e.getActionCommand().equals("FWD_LAST")) {
			if (currentPage == 1) {
    			setBackControls(true);
			} else {
			    if (currentPage < 10) {
    			    backTenPages.setEnabled(true);						
			     }
			}
			currentPage = pages;
			setFwdControls(false);			
			setReportPage(currentPage-1);
			return;
		}
	}
	
	// Checks if the index page is into the range
	public int checkIndexRange(int i) {
		if (i >= pages) {
			return pages-1;
		} else {
			if (i < 0) {
				return 0;
			}	
		}
		return i;
	}
	
	/* 
    // Returns a string variable with a report page 
	public String getReportPage(int i) {	
        int j = checkIndexRange(i);		
		String page1 = reportViews[j].toString();
		if (page1 == null) {
			return "";
		} 
		return page1;
	} */

	// Shows a report page in the viewer
	public void setReportPage(int i) {		
		int j = checkIndexRange(i);
		String page1 = reportViews[j].toString();
		if (page1 != null) {
		    reportArea.setText(page1);
		    currentPage = j + 1;
		    currentPageTF.setText("" + currentPage);
		    reportArea.setText(page1);
		    reportArea.setCaretPosition(0);
		} 
	}
	
	// Enables/Disables Back Navigation Buttons
	public void setBackControls(boolean flag) {
		firstPage.setEnabled(flag);
		backTenPages.setEnabled(flag);
		backOnePage.setEnabled(flag);		
	}

	// Enables/Disables Forward Navigation Buttons
	public void setFwdControls(boolean flag) {
		lastPage.setEnabled(flag);
		forwardTenPages.setEnabled(flag);
		forwardOnePage.setEnabled(flag);		
	}
	
	// Checks if a string var has a numeric value
	public boolean isANumber(String s) {
		for (int i = 0; i < s.length(); i++) {
		     char c = s.charAt(i);
		     if (!Character.isDigit(c))
		          return false;
		 }
		return true;
	}
	
	// Sets navigation buttons state 
	public void setControls(int pageIndex) {
		if (pageIndex == 0) {
			setBackControls(false);
			setFwdControls(true);
		} else {
		    if (pageIndex == pages-1) {
			    setFwdControls(false);
			    setBackControls(true);
		     } else {
		    	 if (pageIndex < 10) {
		    		 backTenPages.setEnabled(false);
		    		 backOnePage.setEnabled(true);
		    		 firstPage.setEnabled(true);
		    		 setFwdControls(true);
		    	 } else {
		    		 if (pageIndex >= pages-10) {
		    			 forwardTenPages.setEnabled(false);
		    			 forwardOnePage.setEnabled(true);
		    			 lastPage.setEnabled(true);
		    			 setBackControls(true);
		    		 } else {
		    			 if ((pageIndex > 10) && (pageIndex < pages-10)) {
		    				 setBackControls(true);
		    				 setFwdControls(true); 
		    			 }
		    		 }
		    	 }
		    	 
		     }
		}
	}

	// Shows the viewer interface
    public void openViewer() {
        setVisible(true);
    }

	/**
	 * This method receives a report through the ReportEvent object
	 */
	public void arriveReport(ReportEvent e) {

		if (e.getIdReport().equals(idReport) && e.isPlainReport()) {
			try {
			    //System.out.println("Cargando pagina 1 - " + e.getIdReport());
				data = e.getData();
				//Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
				// currentGUI.getFrame().setCursor(cursor);
				if (data != null) {
					// System.out.println("Descomprimiendo ...");
					bytes = zip.getDataDecode(data.getValue());
					// System.out.println("zip descomprimido...");
					reportName = e.getTitleReport();
					// Process bytes and convert them to plain reports
					// Set reportViews and printerViews
					// Set the view area with first 
					// Vector <String>fieldsTypes = new Vector<String>();
					//System.out.println("descomprimidos: \n"+new String(bytes));
					TextReportGenerator parser = new TextReportGenerator(80,bytes);
				    reportViews = parser.getReportViews();
				    printerViews = parser.getPrinterViews();
				    //System.out.println("Datos pre-procesados...");
				    setReportPage(0);
				} 
				
			}
			catch (IOException IOEe) {
				IOEe.printStackTrace();
			}
		}
	}

} // Class end 
