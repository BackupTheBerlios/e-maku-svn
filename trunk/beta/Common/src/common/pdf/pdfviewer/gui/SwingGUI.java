/**
 * Esta clase esta basada en el archivo SwingGUI.java del proyecto JPedal.
 * Mayor informacion: 
 * 
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens (mark@idrsolutions.com)
 * (C) Copyright 2006, by IDRsolutions and Contributors.
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * 
 * SwingGUI.java moficiado a partir de el 23-mar-2006
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
 */
 
package common.pdf.pdfviewer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
//import org.jpedal.io.StatusBar;
import org.jpedal.objects.PdfPageData;
import org.w3c.dom.Node;

import common.misc.language.Language;
import common.pdf.pdfviewer.Commands;
import common.pdf.pdfviewer.Values;
import common.pdf.pdfviewer.gui.generic.GUIButton;
import common.pdf.pdfviewer.gui.generic.GUICombo;
import common.pdf.pdfviewer.gui.swing.CommandListener;
import common.pdf.pdfviewer.gui.swing.FrameCloser;
import common.pdf.pdfviewer.gui.swing.SwingButton;
import common.pdf.pdfviewer.gui.swing.SwingCombo;
import common.pdf.pdfviewer.gui.swing.SwingMenuItem;
import common.pdf.pdfviewer.gui.swing.SwingOutline;
import common.pdf.pdfviewer.utils.Printer;
import common.pdf.pdfviewer.utils.SwingWorker;
import common.pdf.pdfviewer.gui.popups.BarProgressDialog;

/**
 * Scope:<b>(All)</b>
 * <br>Description: Swing GUI functions in Viewer
 * 
 *
 */
public class SwingGUI extends GUI implements GUIFactory {
	
	/**listener on buttons, menus, combboxes to execute options (one instance on all objects)*/
	private CommandListener currentCommandListener;	
	
	/**holds OPEN, INFO,etc*/
	private JToolBar topButtons = new JToolBar();
	
	/**holds rotation, quality, scaling and status*/
	private JToolBar comboBar = new JToolBar();
	
	/**holds back/forward buttons at bottom of page*/
	private JToolBar bottomNavButtons = new JToolBar();
	
	/**tell user on first form change it can be saved*/
	private boolean firstTimeFormMessage=true;
	
	/** visual display of current cursor co-ords on page*/
	private JLabel coords=new JLabel();
	
	/**root element to hold display*/
	private JInternalFrame frame=new JInternalFrame();
	
	/**Scrollpane for pdf panel*/
	private JScrollPane scrollPane = new JScrollPane();
	
	private final Font headFont=new Font("SansSerif",Font.BOLD,14);
	
	private final Font textFont=new Font("Serif",Font.PLAIN,12);
	//private Color color = new Color(200,200,255);

	/**Interactive display object - needs to be added to PdfDecoder*/
	//private StatusBar statusBar=new StatusBar(color);
	
	public JTextField pageCounter2 = new JTextField(4);
	
	private JLabel pageCounter3;
	
	private BarProgressDialog dialog;
			
	public SwingGUI(PdfDecoder decode_pdf,Values commonValues){
	
		this.decode_pdf=decode_pdf;
		this.commonValues=commonValues;
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		frame.setFrameIcon(new ImageIcon(getClass().getResource("/icons/ico_reporte.png")));
	}
	
	public void init(String[] scalingValues,final Commands currentCommands,Printer currentPrinter) {
		
		/**
		 * single listener to execute all commands
		 */
		currentCommandListener=new CommandListener(currentCommands);			
				
		/**arrange insets*/
		decode_pdf.setInset(inset,inset);
		
		/**
		 * setup combo boxes
		 */
		scalingBox=new SwingCombo(scalingValues);
		scalingBox.setBackground(Color.white);
		scalingBox.setEditable(true);
		scalingBox.setSelectedIndex(defaultSelection); //set default before we add a listener
		
		/**
		 * add the pdf display to show page
		 **/
		scrollPane.getViewport().add(decode_pdf);
		scrollPane.getVerticalScrollBar().setUnitIncrement(80);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(80);
		
		bottomNavButtons.setBorder(BorderFactory.createEtchedBorder());
		bottomNavButtons.setLayout(new FlowLayout(FlowLayout.LEADING));
		bottomNavButtons.setFloatable(false);
		bottomNavButtons.setFont(new Font("SansSerif", Font.PLAIN, 8)); 

		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		/**
		 * setup global buttons
		 */
		if(!commonValues.isContentExtractor()){
			first    = new SwingButton();
			fback    = new SwingButton();
			back     = new SwingButton();
			forward  = new SwingButton();
			fforward = new SwingButton();
			end      = new SwingButton();
		}
	
		/**
		 * set colours on display boxes and add listener to page number
		 */	
		pageCounter2.setEditable(true);
		pageCounter2.setToolTipText(Language.getWord("NAVTIP"));
		pageCounter2.setBorder(BorderFactory.createLineBorder(Color.black));
		
		pageCounter2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String value=pageCounter2.getText().trim();
				currentCommands.gotoPage(value);
			}
		});
		pageCounter3=new JLabel(Language.getWord("OF")+" "); 
		pageCounter3.setOpaque(false);
		
		/**
		 * create a menu bar and add to display
		 */
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		
		frame.getContentPane().add(top, BorderLayout.NORTH);
		
		/** nav bar at bottom to select pages and setup Toolbar on it*/
		JPanel bottom = new JPanel();
		bottom.setLayout(new BorderLayout());
		frame.getContentPane().add(bottom, BorderLayout.SOUTH);
		
		comboBar.setBorder(BorderFactory.createEmptyBorder());
		comboBar.setLayout(new FlowLayout(FlowLayout.CENTER));
		comboBar.setFloatable(false);
		comboBar.setFont(new Font("SansSerif", Font.PLAIN, 8));
        // Barra de Navegacion
		bottom.add(comboBar, BorderLayout.NORTH);
		/**
		 * navigation toolbar for moving between pages
		 */
		createNavbar();
		
		/**
		 * create other tool bars and add to display
		 */
		topButtons.setBorder(BorderFactory.createEtchedBorder());
		topButtons.setLayout(new FlowLayout(FlowLayout.LEADING));
		topButtons.setFloatable(false);
		topButtons.setFont(new Font("SansSerif", Font.PLAIN, 8));
		
		// Imprimir y Grabar Como
		top.add(topButtons);
		/**
		 * zoom,scale,rotation, status,cursor
		 */
		top.add(bottomNavButtons);
		
		
		/**
		 * set display to occupy half screen size and display, add listener and
		 * make sure appears in centre
		 */
		if(commonValues.getModeOfOperation()!=Values.RUNNING_APPLET){
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			int width = d.width / 2, height = d.height / 2;
			if(width<minimumScreenWidth)
				width=minimumScreenWidth;
			
			frame.setSize(width, height);
			frame.setClosable(true);
			frame.setResizable(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			
			decode_pdf.addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
				@Override
				public void ancestorResized(HierarchyEvent e) {
					zoom();
				}
			
			});
			frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
			frame.addInternalFrameListener(new FrameCloser(currentCommands, this,decode_pdf,currentPrinter,commonValues));
			
		}
	}
	
	/**setup keyboard shortcuts*/
	private void setKeyAccelerators(int ID,JMenuItem menuItem){
		
		switch(ID){
		
		case Commands.SAVE:
			menuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
					java.awt.Event.CTRL_MASK));
			break;
		case Commands.PRINT:
			menuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P,
					java.awt.Event.CTRL_MASK));
			break;
		case Commands.EXIT:
			menuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q,
					java.awt.Event.CTRL_MASK));
			break;
		case Commands.OPENFILE:
			menuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
					java.awt.Event.CTRL_MASK));
			break;
		case Commands.FIRSTPAGE:
			menuItem.setAccelerator( KeyStroke.getKeyStroke("HOME") );
			break;
		case Commands.BACKPAGE:
			menuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT,
					java.awt.Event.CTRL_MASK));
			break;
		case Commands.FORWARDPAGE:
			menuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT,
					java.awt.Event.CTRL_MASK));
			break;
		case Commands.LASTPAGE:
			menuItem.setAccelerator( KeyStroke.getKeyStroke("END") );
			break;
		case Commands.GOTO:
			menuItem.setAccelerator( KeyStroke.getKeyStroke("shift ctrl N") );
			break;
			
		}
	}
	
	public void addButton(int line,String toolTip,String path,final int ID) {
		
		GUIButton newButton = new SwingButton();
		
		/**specific buttons*/
		switch(ID){
		
		case Commands.FIRSTPAGE:
			newButton=first;
			break;
		case Commands.FBACKPAGE:
			newButton=fback;
			break;
		case Commands.BACKPAGE:
			newButton=back;
			break;
		case Commands.FORWARDPAGE:
			newButton=forward;
			break;
		case Commands.FFORWARDPAGE:
			newButton=fforward;
			break;
		case Commands.LASTPAGE:
			newButton=end;
			break;
		}
		
		newButton.init(path, ID,toolTip);
		
		((AbstractButton) newButton).addActionListener(currentCommandListener);
		
		if(line==BUTTONBAR){
			topButtons.add((AbstractButton) newButton);
			topButtons.add(Box.createHorizontalGlue());
		}
		else if(line==NAVBAR){
			comboBar.add((AbstractButton) newButton);
			comboBar.add(Box.createHorizontalGlue());
		}
	}
	
	public void addMenuItem(JMenu parentMenu,String text,String toolTip,final int ID) {
		
		SwingMenuItem menuItem = new SwingMenuItem(text);
		menuItem.setToolTipText(toolTip);
		menuItem.setID(ID);
		setKeyAccelerators(ID,menuItem);
		
		menuItem.addActionListener(currentCommandListener);
		parentMenu.add(menuItem);
	}
	
	public void addCombo(String title,String tooltip,int ID){
		
		GUICombo combo=null;
		if (ID == Commands.SCALING)
			combo=scalingBox;
		
		combo.setID(ID);
		
		JLabel label = new JLabel(title); 
		combo.setToolTipText(tooltip); 
		
		bottomNavButtons.add(label);
		bottomNavButtons.add((SwingCombo) combo);
		
		((SwingCombo)combo).addActionListener(currentCommandListener);
		
	}
	
	public void setViewerTitle(final String title) {
		frame.setTitle(title!=null?title:"");
	}
	
	public void resetComboBoxes(boolean value) {
		scalingBox.setEnabled(value);
	}
	
	public int getSelectedComboIndex(int ID) {
		if (ID == Commands.SCALING){
			return scalingBox.getSelectedIndex();
		}
		return -1;
	}
	
	public void setSelectedComboIndex(int ID,int index) {
		if (ID == Commands.SCALING){
			scalingBox.setSelectedIndex(index);	
		}
	}
	
	public void setSelectedComboItem(int ID,String index) {
		if (ID == Commands.SCALING){
			scalingBox.setSelectedItem(index);	
		}
	}
	
	public Object getSelectedComboItem(int ID) {

		if (ID == Commands.SCALING){
			return scalingBox.getSelectedItem();	
		}
		return null;
	}
	
	/**refresh screen display*/
	private void repaintScreen() {
		scrollPane.repaint();
		frame.validate();
	}
	
	public void zoom() {
		
		if(decode_pdf!=null){
			
			/** update value and GUI */
			int index=getSelectedComboIndex(Commands.SCALING);
			if(index==-1){
				String numberValue=(String)getSelectedComboItem(Commands.SCALING);
				
				float zoom=-1;
				if((numberValue!=null)&&(numberValue.length()>0)){
					try {
						zoom= Float.parseFloat(numberValue);
					}
					catch(Exception e){
						zoom=-1;
							int length=numberValue.length();
						int ii=0;
						while(ii<length){
							char c=numberValue.charAt(ii);
							if(((c>='0')&&(c<='9'))|(c=='.'))
								ii++;
							else
								break;
						}
						
						if(ii>0)
							numberValue=numberValue.substring(0,ii);
						
						//try again if we reset above
						if(zoom==-1){
							try {
								zoom= Float.parseFloat(numberValue);
							}
							catch(Exception e1){
								zoom=-1;
							}
						}
					}
					if(zoom>1000){
						zoom=1000;
					}
				}
				
				//if nothing off either attempt, use window value
				if(zoom==-1) {
					//its not set so use To window value
					index=defaultSelection;
					setSelectedComboIndex(Commands.SCALING, index);
				}
				else {
					scaling=zoom/100;
					setSelectedComboItem(Commands.SCALING, zoom+"");	
				}
			}
			
			if(index!=-1){
				if(index<3) { //handle scroll to width/height/window
					
					PdfPageData pageData = decode_pdf.getPdfPageData();
					int cw,ch;
					//int raw_rotation=pageData.getRotation(commonValues.getCurrentPage());
					if(rotation==90 || rotation==270) {
						cw = pageData.getCropBoxHeight(commonValues.getCurrentPage());
						ch = pageData.getCropBoxWidth(commonValues.getCurrentPage());
					}
					else {
						cw = pageData.getCropBoxWidth(commonValues.getCurrentPage());
						ch = pageData.getCropBoxHeight(commonValues.getCurrentPage());
					}
					
					//define pdf view width and height
					// MAY not need dividersize
					//float width = scrollPane.getViewport().getWidth()-inset-inset-displayPane.getDividerSize();
					float width = scrollPane.getViewport().getWidth()-inset-inset;
					float height = scrollPane.getViewport().getHeight()-inset-inset;
					
					float x_factor=0,y_factor=0;
					x_factor = width / cw;
					y_factor = height / ch;
					
					if(index==0){//window
						if(x_factor<y_factor)
							scaling = x_factor;
						else
							scaling = y_factor;
					}else if(index==1)//height
						scaling = y_factor;
					else if(index==2)//width
						scaling = x_factor;
				}else{
					scaling=scalingFloatValues[index];
				}
			}
			
			//check for 0 to avoid error  and replace with 1
			PdfPageData pagedata = decode_pdf.getPdfPageData();
			if((pagedata.getCropBoxHeight(commonValues.getCurrentPage())*scaling<100) &&//keep the page bigger than 100 pixels high
					(pagedata.getCropBoxWidth(commonValues.getCurrentPage())*scaling<100) && commonValues.isPDF()){//keep the page bigger than 100 pixels wide
				scaling=1;
				setSelectedComboItem(Commands.SCALING,"100");
			}
					
			
			if(decode_pdf!=null) //allow for clicking on it before page opened
				decode_pdf.setPageParameters(scaling, commonValues.getCurrentPage(),rotation);
			
			decode_pdf.invalidate();
			//decode_pdf.repaint();
			repaintScreen();
			
		}
		
		
	}

	public void decodePage(final boolean resizePanel){		
		
		/**ensure text and color extracted. If you do not need color, take out 
		 * line for faster decode
		 */
		
		//remove any search highlight
		decode_pdf.setFoundTextAreas(null);
		
		//remove highlighted text
		decode_pdf.setHighlightedAreas(null); 
		
		setRectangle(null);
		
		//stop user changing scaling while decode in progress
		resetComboBoxes(false);
		
		decode_pdf.clearScreen();
		commonValues.setProcessing(true);
		
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				
				
				try {
					
					//statusBar.updateStatus("Decoding Page",0);
					dialog.resetBar();
					
					/**
					 * make sure screen fits display nicely
					 */
					if (resizePanel) 
						zoom();
					
					if (Thread.interrupted())
						throw new InterruptedException();
					
					/**
					 * decode the page
					 */
					try {
						decode_pdf.decodePage(commonValues.getCurrentPage());
						if(!decode_pdf.hasAllImages()){
							String status = "One or more images is not displayed\n"+
							"Most common cause is Insufficient memory to decode page\n"+
							"and display all images on this page\n\n"+
							"1) Try running java again, this time allocating it more memory.\n"+
							"This can be done using the -Xmx VM argument.\n"+
							"For example to allocate at least 256MB you would use java -Xmx256M ...\n\n"+
							"2) Set the Image Optimisation setting to Memory\n\n"
							+"Please contact IDRsolutions if you require any further advice";
							
							showMessageDialog(status); 
						}
						//read values for page display
						PdfPageData page_data = decode_pdf.getPdfPageData();
						
						mediaW  = page_data.getMediaBoxWidth(commonValues.getCurrentPage());
						mediaH = page_data.getMediaBoxHeight(commonValues.getCurrentPage());
						mediaX = page_data.getMediaBoxX(commonValues.getCurrentPage());
						mediaY = page_data.getMediaBoxY(commonValues.getCurrentPage());
						
						cropX = page_data.getCropBoxX(commonValues.getCurrentPage());
						cropY = page_data.getCropBoxY(commonValues.getCurrentPage());
						cropW = page_data.getCropBoxWidth(commonValues.getCurrentPage());
						cropH = page_data.getCropBoxHeight(commonValues.getCurrentPage());
						//statusBar.updateStatus("Displaying Page",0); 
						dialog.updateStatusMessage("Displaying Page", 0);
						dialog.setStatusProgress(80);
						
					} catch (Exception e) {
						System.err.println("Exception " + e + " decoding page");
						e.printStackTrace();
						commonValues.setProcessing(false);
					}
					
					pageCounter2.setForeground(Color.black);
					pageCounter2.setText(" " + commonValues.getCurrentPage()); 
					pageCounter3.setText(Language.getWord("OF") +" "+ commonValues.getPageCount());
					
					//tell user if we had a memory error on decodePage
					String status=decode_pdf.getPageDecodeReport();
					if(status.indexOf("java.lang.OutOfMemoryErro")!=-1){
						status = "Insufficient memory to decode page and display all images on this page\n\n"+
						"1) Try running java again, this time allocating it more memory.\n"+
						"This can be done using the -Xmx VM argument.\n"+
						"For example to allocate at least 256MB you would use java -Xmx256M ...\n\n"+
						"2) Set the Image Optimisation setting to Memory\n\n"
						+"Please contact IDRsolutions if you require any further advice";
						
						showMessageDialog(status); 
						
					}
					//tell user about embedded fonts in Open Source version 
					if((decode_pdf.hasEmbeddedFonts())&&(!decode_pdf.supportsEmbeddedFonts())){
						showMessageDialog("Page contains embedded fonts which may not display correctly using Font substitution."); 
						
					}
					
					//make sure fully drawn
					decode_pdf.repaint();
					
					
				} catch (Exception e) {

				}
				
				//statusBar.setProgress(100);
				dialog.setStatusProgress(100);
				dialog.close();

				//reanable user changing scaling 
				resetComboBoxes(true);
				
				//<start-forms>
				addFormsListeners();
				
				return null;
			}

		};
		
		worker.construct();
		
	}
	
	//<start-forms>
	/**this method adds listeners to GUI widgets to track changes*/
	@SuppressWarnings("deprecation")
	private void addFormsListeners(){
		
		//rest forms changed flag to show no changes
		commonValues.setFormsChanged(false);
		
		/**see if flag set - not default behaviour*/
		boolean showMessage=false;
		String formsFlag=System.getProperty("listenForms");
		if(formsFlag!=null)
			showMessage=true;
		
		//get the form renderer which also contains the processed form data.
		//if you want simple form data, also look at the ExtractFormData.java example
		org.jpedal.objects.acroforms.AcroRenderer formRenderer=decode_pdf.getCurrentFormRenderer();
		
		if(formRenderer==null)
			return;
		
		//get list of forms on page
		java.util.List formsOnPage=null;
		
		/**
		 * Or you can also use 
		 * formRenderer.getDisplayComponentsForPage(commonValues.getCurrentPage());
		 * to get all components directly - we have already checked formRenderer not null
		 */     
		try {
			formsOnPage = formRenderer.getComponentNameList(commonValues.getCurrentPage());
		} catch (PdfException e) {
			
		}
		
		//allow for no forms
		if(formsOnPage==null){
			
			if(showMessage)
				showMessageDialog("No fields on this page");
			
			return;
		}
		
		int formCount=formsOnPage.size();
		
		JPanel formPanel=new JPanel();
		/**
		 * create a JPanel to list forms and tell user abox example
		 **/
		if(showMessage){
			formPanel.setLayout(new BoxLayout(formPanel,BoxLayout.Y_AXIS));
			JLabel formHeader = new JLabel("This page contains "+formCount+" form objects");
			formHeader.setFont(headFont);
			formPanel.add(formHeader);
			
			formPanel.add(Box.createRigidArea(new Dimension(10,10)));
			JTextPane instructions = new JTextPane();
			instructions.setPreferredSize(new Dimension(450,180));
			instructions.setEditable(false);
			instructions.setText("This provides a simple example of Forms handling. We have"+
					" added a listener to each form so clicking on it shows the form name.\n\n"+
					"Code is in addExampleListeners() in common.pdf.pdfviewer.PDFViewer\n\n"+
					"This could be easily be extended to interface with a database directly "+
					"or collect results on an action and write back using itext.\n\n"+
					"Forms have been converted into Swing components and are directly accessible"+
					" (as is the original data).\n\n"+
			"If you don't like the standard SwingSet you can replace with your own set.");
			instructions.setFont(textFont);
			formPanel.add(instructions);
			formPanel.add(Box.createRigidArea(new Dimension(10,10)));
		}
		
		/**
		 * access all forms in turn and add a listener
		 */
		for(int i=0;i<formCount;i++){
			
			//get name of form
			String formName=(String) formsOnPage.get(i);
			
			//get actual component - do not display it separately -
			//at the moment this will not work on group objects (ie radio buttons and checkboxes)
			Component[] comp=formRenderer.getComponentsByName(formName);
			
			/**
			 * add listeners on first decode - not needed if we revisit page
			 * 
			 * DO NOT remove listeners from Components as used internally to control appearance
			 */
			Integer pageKey=new Integer(i);
			if(comp!=null && pagesDecoded.get(pageKey)==null){
				
				//simple device to prevent multiple listeners
				pagesDecoded.put(pageKey,"x");
				
				//loop through all components returned
				int count=comp.length;
				for(int index=0;index<count;index++){
					
					//add details to screen display, group objects have the same name so add them only once 
					if((showMessage)&&(index==0)){
						JLabel type = new JLabel();
						JLabel label = new JLabel("Form name="+formName);
						String labelS = "type="+comp[index].getClass();
						if(count>1){
							labelS = "Group of "+count+" Objects, type="+comp[index].getClass();
							type.setForeground(Color.red);
						}
						type.setText(labelS);
						label.setFont(headFont);
						type.setFont(textFont);
						formPanel.add(label);
						formPanel.add(type);
						
						formPanel.add(new JLabel(" "));
					}
					
					//add listeners to show proof of concept - this
					//could equally be inserting into database
					
					//combo boxes
					FormActionListener changeList=new FormActionListener(formName+index,frame,showMessage);
					if(comp[index] instanceof JComboBox){ 
						((JComboBox)comp[index]).addActionListener(changeList);
					}else if(comp[index] instanceof JCheckBox){ 
						((JCheckBox)comp[index]).addActionListener(changeList);
					}else if(comp[index] instanceof JRadioButton){ 
						((JRadioButton)comp[index]).addActionListener(changeList);
					}else if(comp[index] instanceof JTextField){ 
						((JTextField)comp[index]).addActionListener(changeList);
					}
				}
			}
		}		
	}
	//<end-forms>
	
	public void initStatus() {
		resetStatus();
		decode_pdf.setStatusBarObject(dialog.getStatusBar());
	}
	

	public void resetStatus() {
		//statusBar.setColorForSubroutines(Color.blue);
		//bottomNavButtons.add(statusBar.getStatusObject());
		dialog = new BarProgressDialog(frame);
		dialog.resetStatusBar();
	}
	
	
	class FormActionListener implements ActionListener{
		
		private Container c;
		private String formName;
		boolean showMessage;
		
		public FormActionListener(String formName, Container c,boolean showMessage) {
			
			this.c=c;
			this.formName=formName;
			this.showMessage=showMessage;
			
		}
		
		public void actionPerformed(ActionEvent arg0) {
			
			Object comp =arg0.getSource();
			Object value=null;
			if(comp instanceof JComboBox)
				value=((JComboBox)comp).getSelectedItem();
			else if(comp instanceof JCheckBox)
				value=""+((JCheckBox)comp).isSelected();
			else if(comp instanceof JRadioButton)
				value=""+((JRadioButton)comp).isSelected();
			else if(comp instanceof JTextField)
				value=""+((JTextField)comp).getText();
			
			{
				//boolean showSaveFormsMessage = properties.getValue("showsaveformsmessage").equals("true");
				
				//if(showSaveFormsMessage && firstTimeFormMessage && commonValues.isFormsChanged()==false){
				if(firstTimeFormMessage && commonValues.isFormsChanged()==false){
					
					firstTimeFormMessage=false;
					
					JPanel panel =new JPanel();
					panel.setLayout(new GridBagLayout());
					final GridBagConstraints p = new GridBagConstraints();
					
					p.anchor=GridBagConstraints.WEST;
					p.gridx = 0;
					p.gridy = 0;
					
					String str="You have changed a form value.\nYou can save a copy of the form from menu File-ReSave Forms As";
					if(!commonValues.isItextOnClasspath())
						str="You have changed a form value.\nIf you had itext on the classpath,\nJPedal would allow you to resave the form";
					
					JCheckBox cb=new JCheckBox();
					cb.setText("Don't show this again");
					Font font = cb.getFont();
					
					JTextArea ta=new JTextArea(str);
					ta.setOpaque(false);
					ta.setFont(font);
					
					p.ipady=20;
					panel.add(ta, p);
					
					p.ipady=0;
					p.gridy = 1;
					panel.add(cb,p);
					
					JOptionPane.showMessageDialog(c,panel);
					
					//if(cb.isSelected())
					//	properties.setValue("showsaveformsmessage","false");
					
				}
			}
			commonValues.setFormsChanged(true);
			if(showMessage)
				JOptionPane.showMessageDialog(c,"FormName >>"+formName+"<<. Value changed to "+value);
			
		}
	}

	public void setCoordText(String string) {
		coords.setText(string);
	}

	public void setPageNumber() {
		pageCounter2.setForeground(Color.black);
		pageCounter2.setText(" " + commonValues.getCurrentPage());
		pageCounter3.setText(Language.getWord("OF") +" "+ commonValues.getPageCount()); //$NON-NLS-1$
	}
	
	private void createNavbar() {
		
		JLabel pageCounter1 = new JLabel(Language.getWord("PAGE")); 
		pageCounter1.setOpaque(false);
		
		/**
		 * navigation toolbar for moving between pages
		 */
		addButton(NAVBAR,Language.getWord("REWIND_START"),"/icons/ico_flecha_inicio.png",Commands.FIRSTPAGE);
		
		addButton(NAVBAR,Language.getWord("REWIND_10"),"/icons/ico_flecha2_izq.png",Commands.FBACKPAGE);
		
		addButton(NAVBAR,Language.getWord("REWIND_1"),"/icons/ico_flecha_izq.png",Commands.BACKPAGE);
		
		/**put page count in middle of forward and back*/
		comboBar.add(pageCounter1);
		comboBar.add(pageCounter2);
		comboBar.add(pageCounter3);
		
		addButton(NAVBAR,Language.getWord("FORWARD_1"),"/icons/ico_flecha_der.png",Commands.FORWARDPAGE);
		
		addButton(NAVBAR,Language.getWord("FORWARD_10"),"/icons/ico_flecha2_der.png",Commands.FFORWARDPAGE);
		
		addButton(NAVBAR,Language.getWord("FORWARD_LAST"),"/icons/ico_flecha_fin.png",Commands.LASTPAGE);
		
	}
	

	public JToolBar getTopButtonBar() {
		return topButtons;
	}
	
	public void showMessageDialog(Object message1){
		JOptionPane.showInternalMessageDialog(frame,message1);
	}
	
	public void showMessageDialog(Object message,String title,int type){
		JOptionPane.showInternalMessageDialog(frame,message,title,type);
	}
	
	public String showInputDialog(Object message, String title, int type) {
		return JOptionPane.showInternalInputDialog(frame, message, title, type);
	}
	
	public String showInputDialog(String message) {
		return JOptionPane.showInternalInputDialog(frame,message);
	}
	
	public int showOptionDialog(Object displayValue, String message, int option, int type, Object icon, Object[] options, Object initial) {
		return JOptionPane.showInternalOptionDialog(frame, displayValue,message,option,type, (Icon)icon, options,initial);
	}
	
	public int showConfirmDialog(String message, String message2, int option) {
		return JOptionPane.showInternalConfirmDialog(frame, message,message2,option);
	}
	
	public int showOverwriteDialog(String file,boolean yesToAllPresent) {
		
		int n = -1;
		if(yesToAllPresent){
			
			final Object[] buttonRowObjects = new Object[] { 
					"Yes", "Yes To All", "No", "Cancel"
			};
			
			n = JOptionPane.showOptionDialog(frame, 
					file+"\nThe file already exists\nReplace the existing file?",
					"Overwrite?",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					buttonRowObjects,
					buttonRowObjects[0]);
			
		}
		else{
			n = JOptionPane.showOptionDialog(frame, 
					file+"\nThe file already exists\nReplace the existing file?",
					"Overwrite?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,null,null);
		}
		
		return n;
	}
	
	public void showMessageDialog(JTextArea info) {
		JOptionPane.showMessageDialog(frame, info);
	}
	
	public void showConfirmDialog(Object label, String message, int option, int plain_message) {
		JOptionPane.showConfirmDialog(frame,label,message,option,plain_message);
	}
	
	public String initPDFOutlines(Node rootNode, String bookmark) {
		tree=new SwingOutline(rootNode);
		return tree.getPage(bookmark);
	}
	
	public void updateStatusMessage(String message) {
	}
	
	public void resetStatusMessage(String message) {
	}
	
	public void setStatusProgress(int size) {	
	}
	
	public void close() {
		frame.dispose();
	}

	public JInternalFrame getFrame() {
		return frame;
	}
}