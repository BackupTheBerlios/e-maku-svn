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
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.jdom.Element;
import common.misc.ZipHandler;

import common.control.ClientHeaderValidator;
import common.control.ReportEvent;
import common.control.ReportListener;
import common.gui.forms.GenericForm;

import common.misc.language.Language;
import common.misc.CommonConstants;

/**
 * TextReportViewer.java Creado el 1 de Febrero 2007
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

// This class shows a text report view 

public class TextReportViewer extends JInternalFrame implements ActionListener,ReportListener {
		
	private static final long serialVersionUID = 7423353291901251301L;
	
	private GenericForm genericForm;
	private String idReport;
	
	private Vector <ByteArrayOutputStream>reportViews = new Vector<ByteArrayOutputStream>();
	private Vector <ByteArrayOutputStream>printerViews = new Vector<ByteArrayOutputStream>();;
	
	private byte[] bytes;
	private int pages = 0;
	
	private JEditorPane reportArea;
	private JButton printerButton   = new JButton();
	private JButton closeButton     = new JButton();
	
	private JButton firstPage       = new JButton();
	private JButton backTenPages    = new JButton();
	private JButton backOnePage     = new JButton();
	private JButton forwardOnePage  = new JButton();
	private JButton forwardTenPages = new JButton();
	private JButton lastPage        = new JButton();
	
	private JTextField currentPageTF = new JTextField(4);
	private JLabel total = new JLabel("");
	private int currentPage = 1;
	
	private Element data;
	private ZipHandler zip;
	
	private int charactersPerline = 80;
	
	// Class Constructor
	public TextReportViewer(GenericForm GFforma, String idReport) {

		setClosable(true);
		setIconifiable(true);
		setResizable(true);
		setMaximizable(true);
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
				
		this.genericForm = GFforma;
		this.idReport = idReport;
		ClientHeaderValidator.addReportListener(this);
		this.zip = new ZipHandler();		
		openReport();
	}
	
	// Sets the graphic interface for the viewer
	private void openReport() {		
		JPanel global = new JPanel();
		global.setLayout(new BorderLayout());		
        global.add(setButtonsPanel(), BorderLayout.NORTH);        
        global.add(setViewerArea(), BorderLayout.CENTER);
        global.add(setNavigationPanel(), BorderLayout.SOUTH);
        
        setContentPane(global);
        pack();
        setSize(900,400);
	}

	// Sets the top panel 
	private JPanel setButtonsPanel() {
		
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
	private JPanel setNavigationPanel() {
		JPanel navigation = new JPanel();
		JLabel page = new JLabel(Language.getWord("PAGE"));
		
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
	private JPanel setViewerArea() {
		
		JPanel mainArea = new JPanel();
		mainArea.setLayout(new BorderLayout());
		        
        reportArea = new JEditorPane("text/html","<br><br><br><b><center>" + Language.getWord("LOAD_REPORT") + "</center></b>");
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
			PlainReportPrinterDialog dialog = new PlainReportPrinterDialog(genericForm,pages,printerViews,charactersPerline);
    		dialog.setLocationRelativeTo(this);
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
	private int checkIndexRange(int i) {
		if (i >= pages) {
			return pages-1;
		} else {
			if (i < 0) {
				return 0;
			}	
		}
		return i;
	}
	
	// Shows a report page in the viewer
	private void setReportPage(int i) {		
		int j = checkIndexRange(i);
		ByteArrayOutputStream tmp = reportViews.elementAt(i);
		String page1 = tmp.toString();

		if (page1 != null) {
		    reportArea.setText(page1);
		    currentPage = j + 1;
		    currentPageTF.setText("" + currentPage);		    
		    String pageFooter = Language.getWord("PAGE") + " " + currentPage + " " + Language.getWord("OF") + " " + pages;
			page1 += "<table width=\"100%\"><tr><td><b>" + CommonConstants.QHATU + "</b></td><td align=\"right\">" 
			         + pageFooter + "&nbsp;&nbsp;&nbsp;</td></tr></table></td></tr></table></center><br><br>";		    
		    reportArea.setText(page1);
		    reportArea.setCaretPosition(0);
		} 
	}
	
	// Enables/Disables Back Navigation Buttons
	private void setBackControls(boolean flag) {
		firstPage.setEnabled(flag);
		backTenPages.setEnabled(flag);
		backOnePage.setEnabled(flag);		
	}

	// Enables/Disables Forward Navigation Buttons
	private void setFwdControls(boolean flag) {
		lastPage.setEnabled(flag);
		forwardTenPages.setEnabled(flag);
		forwardOnePage.setEnabled(flag);		
	}
	
	// Checks if a string var has a numeric value
	private boolean isANumber(String s) {
		for (int i = 0; i < s.length(); i++) {
		     char c = s.charAt(i);
		     if (!Character.isDigit(c))
		          return false;
		 }
		return true;
	}
	
	// Sets navigation buttons state 
	private void setControls(int pageIndex) {
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
    
    // Set a new report in the viewer area
    private void loadReport(String name) {
		class PlainReportThread extends Thread {
			private String name;
			PlainReportThread(String name) {
				this.name=name;
			}
			
			public void run() {
				try {
					bytes = zip.getDataDecode(data.getValue());
					TextReportGenerator parser = new TextReportGenerator(charactersPerline,bytes);
					reportViews = parser.getReportViews();
				    printerViews = parser.getPrinterViews();
				    pages = reportViews.size();
				    setTitle(name);
				    total.setText(Language.getWord("OF") + " " + pages + " ");
				    setReportPage(0);
				}
				catch (IOException IOEe) {
					IOEe.printStackTrace();
				}
			}
		}
		new PlainReportThread(name).start();
    } 

	/**
	 * This method receives a report through the ReportEvent object
	 */
	public void arriveReport(ReportEvent e) {
		
		if (e.getIdReport().equals(idReport) && e.isPlainReport()) {
				data = e.getData();
				if (data != null) {
					loadReport(e.getTitleReport());
				} 
			}
	}

} // Class end 
