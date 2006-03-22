/**
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens (mark@idrsolutions.com)
 *
 * (C) Copyright 2005, IDRsolutions and Contributors.
 * 
 * 	This file is part of JPedal
 *
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


 *
 * ---------------
 * SwingGUI.java
 * ---------------
 * (C) Copyright 2006, by IDRsolutions and Contributors.
 *

 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package jmlib.pdf.pdfviewer.gui;

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
import java.awt.Point;
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
import javax.swing.JButton;
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
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import jmlib.miscelanea.idiom.Language;
import jmlib.pdf.pdfviewer.Commands;
import jmlib.pdf.pdfviewer.Values;
import jmlib.pdf.pdfviewer.gui.generic.GUIButton;
import jmlib.pdf.pdfviewer.gui.generic.GUICombo;
import jmlib.pdf.pdfviewer.gui.generic.GUIThumbnailPanel;
import jmlib.pdf.pdfviewer.gui.swing.CommandListener;
import jmlib.pdf.pdfviewer.gui.swing.FrameCloser;
import jmlib.pdf.pdfviewer.gui.swing.SwingButton;
import jmlib.pdf.pdfviewer.gui.swing.SwingCombo;
import jmlib.pdf.pdfviewer.gui.swing.SwingMenuItem;
import jmlib.pdf.pdfviewer.gui.swing.SwingOutline;
import jmlib.pdf.pdfviewer.utils.Printer;
import jmlib.pdf.pdfviewer.utils.SwingWorker;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.jpedal.io.StatusBar;
import org.jpedal.objects.PdfPageData;
import org.jpedal.utils.LogWriter;
import org.w3c.dom.Node;

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
	
	//<start-forms>
	/**tell user on first form change it can be saved*/
	private boolean firstTimeFormMessage=true;
	//<end-forms>
	
	/** visual display of current cursor co-ords on page*/
	private JLabel coords=new JLabel();
	
	/**root element to hold display*/
	private JInternalFrame frame=new JInternalFrame();
	
	/**Scrollpane for pdf panel*/
	private JScrollPane scrollPane = new JScrollPane();
	
	private final Font headFont=new Font("SansSerif",Font.BOLD,14);
	
	private final Font textFont=new Font("Serif",Font.PLAIN,12);
	
	
	private Color color = new Color(200,200,255);
	//private Color color = new Color(220,220,220);
	/**Interactive display object - needs to be added to PdfDecoder*/
	private StatusBar statusBar=new StatusBar(color);
	
	public JTextField pageCounter2 = new JTextField(4);
	
	private JLabel pageCounter3;	
			
	public SwingGUI(PdfDecoder decode_pdf,Values commonValues,GUIThumbnailPanel thumbnails){
	
		this.decode_pdf=decode_pdf;
		this.commonValues=commonValues;
		this.thumbnails=thumbnails;
		//this.properties=properties;
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		frame.setFrameIcon(new ImageIcon(getClass().getResource("/icons/ico_reporte.png")));
	}
	
	/**used when clicking on thumbnails to move onto new page*/
	private class PageChanger implements ActionListener {
		
		int page;
		public PageChanger(int i){
			i++;
			page=i;
		}
		
		public void actionPerformed(ActionEvent e) {
			if((!commonValues.isProcessing())&&(commonValues.getCurrentPage()!=page)){
				commonValues.setCurrentPage(page);
				
				statusBar.resetStatus("");
		
				setScalingToDefault();
				
				decode_pdf.setPageParameters(getScaling(), commonValues.getCurrentPage());
				
				decodePage(false);
				
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#initLayoutMenus(javax.swing.JMenu, java.lang.String[], int[])
	 *
	public void initLayoutMenus(JMenu pageLayout, String[] descriptions, int[] value) {
		
		ButtonGroup group = new ButtonGroup();
		int count=value.length;
		for(int i=0;i<count;i++){
			
			JCheckBoxMenuItem pageView=new JCheckBoxMenuItem(descriptions[i]);
			pageView.setBorder(BorderFactory.createEmptyBorder());
			group.add(pageView);
			if(i==0)
				pageView.setSelected(true);
			
			pageView.addActionListener(new PageViewChanger(value[i]));
			pageLayout.add(pageView);	
		}
	}	
	*/
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#init(java.lang.String[], jmlib.pdf.pdfviewer.Commands, jmlib.pdf.pdfviewer.utils.Printer)
	 */
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
		
		//if you enable, remember to change rotation and quality Comboboxes
		//scalingBox.setPreferredSize(new Dimension(85,25));
		
		/**
		 * add the pdf display to show page
		 **/
		scrollPane.getViewport().add(decode_pdf);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(80);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(80);
		
		//bottomNavButtons.setBorder(BorderFactory.createEmptyBorder());
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
		
		//JPanel middle = new JPanel();
		//middle.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		frame.getContentPane().add(top, BorderLayout.NORTH);
		//frame.getContentPane().add(middle, BorderLayout.CENTER);
		
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
		//middle.add(comboBar, BorderLayout.CENTER);
		/**
		 * navigation toolbar for moving between pages
		 */
		createNavbar();
		
		/**
		 * create other tool bars and add to display
		 */
		//topButtons.setBorder(BorderFactory.createEmptyBorder());
		topButtons.setBorder(BorderFactory.createEtchedBorder());
		topButtons.setLayout(new FlowLayout(FlowLayout.LEADING));
		topButtons.setFloatable(false);
		topButtons.setFont(new Font("SansSerif", Font.PLAIN, 8));
		//topButtons.add(comboBar, BorderLayout.NORTH);
		
		// Imprimir y Grabar Como
		top.add(topButtons);
		/**
		 * zoom,scale,rotation, status,cursor
		 */
		top.add(bottomNavButtons);
		
		//top.add(bottomNavButtons,BorderLayout.EAST);
						
		/**move over if we will display thumbnails*/
		//if (thumbnails.isShownOnscreen())
		//	displayPane.setDividerLocation(thumbLocation);
		
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
			frame.addInternalFrameListener(new FrameCloser(currentCommands, this,decode_pdf,currentPrinter,thumbnails,commonValues));
			
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
			//menuItem.setAccelerator( KeyStroke.getKeyStroke("LEFT") );
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
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#addButton(int, java.lang.String, java.lang.String, int)
	 */
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
		
		//add listener
		((AbstractButton) newButton).addActionListener(currentCommandListener);
		
		//add to toolbar
		if(line==BUTTONBAR){
			topButtons.add((AbstractButton) newButton);
			topButtons.add(Box.createHorizontalGlue());
		}else if(line==NAVBAR){
			comboBar.add((AbstractButton) newButton);
			comboBar.add(Box.createHorizontalGlue());
		}
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#addMenuItem(javax.swing.JMenu, java.lang.String, java.lang.String, int)
	 */
	public void addMenuItem(JMenu parentMenu,String text,String toolTip,final int ID) {
		
		SwingMenuItem menuItem = new SwingMenuItem(text);
		menuItem.setToolTipText(toolTip);
		menuItem.setID(ID);
		setKeyAccelerators(ID,menuItem);
		
		//add listener
		menuItem.addActionListener(currentCommandListener);
		parentMenu.add(menuItem);
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#addCombo(java.lang.String, java.lang.String, int)
	 */
	public void addCombo(String title,String tooltip,int ID){
		
		GUICombo combo=null;
		if (ID == Commands.SCALING)
			combo=scalingBox;
		
		combo.setID(ID);
		
		JLabel label = new JLabel(title); 
		combo.setToolTipText(tooltip); 
		
		bottomNavButtons.add(label);
		bottomNavButtons.add((SwingCombo) combo);
		
		//add listener
		((SwingCombo)combo).addActionListener(currentCommandListener);
		
	}
	
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setViewerTitle(java.lang.String)
	 */
	public void setViewerTitle(final String title) {
		
		if(title!=null) {
			frame.setTitle(title);
		} 
		else 
		 frame.setTitle("");
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#resetComboBoxes(boolean)
	 */
	public void resetComboBoxes(boolean value) {
		//qualityBox.setEnabled(value);
		scalingBox.setEnabled(value);
		//rotationBox.setEnabled(value);
		
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#getSelectedComboIndex(int)
	 */
	public int getSelectedComboIndex(int ID) {

		if (ID == Commands.SCALING){
			return scalingBox.getSelectedIndex();
		}
		else
	      return -1;
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setSelectedComboIndex(int, int)
	 */
	public void setSelectedComboIndex(int ID,int index) {
		if (ID == Commands.SCALING){
			scalingBox.setSelectedIndex(index);	
		}
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setSelectedComboItem(int, java.lang.String)
	 */
	public void setSelectedComboItem(int ID,String index) {
		if (ID == Commands.SCALING){
			scalingBox.setSelectedItem(index);	
		}
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#getSelectedComboItem(int)
	 */
	public Object getSelectedComboItem(int ID) {

		if (ID == Commands.SCALING){
			return scalingBox.getSelectedItem();	
		}
		else
			return null;
	}
	
	/**refresh screen display*/
	private void repaintScreen() {
		scrollPane.repaint();
		frame.validate();
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#zoom()
	 */
	public void zoom() {
		
		if(decode_pdf!=null){
			
			/** update value and GUI */
			int index=getSelectedComboIndex(Commands.SCALING);
			if(index==-1){
				String numberValue=(String)getSelectedComboItem(Commands.SCALING);
				
				float zoom=-1;
				if((numberValue!=null)&&(numberValue.length()>0)){
					try{
						zoom= Float.parseFloat(numberValue);
					}catch(Exception e){
						zoom=-1;
						//its got characters in it so get first valid number string
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
							try{
								zoom= Float.parseFloat(numberValue);
							}catch(Exception e1){zoom=-1;}
						}
					}
					if(zoom>1000){
						zoom=1000;
					}
				}
				
				//if nothing off either attempt, use window value
				if(zoom==-1){
					//its not set so use To window value
					index=defaultSelection;
					setSelectedComboIndex(Commands.SCALING, index);
				}else{
					scaling=zoom/100;
					setSelectedComboItem(Commands.SCALING, zoom+"");	
				}
			}
			
			if(index!=-1){
				if(index<3){ //handle scroll to width/height/window
					
					PdfPageData pageData = decode_pdf.getPdfPageData();
					int cw,ch;
					//int raw_rotation=pageData.getRotation(commonValues.getCurrentPage());
					if(rotation==90 || rotation==270){
						cw = pageData.getCropBoxHeight(commonValues.getCurrentPage());
						ch = pageData.getCropBoxWidth(commonValues.getCurrentPage());
					}else{
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
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#decodePage(boolean)
	 */
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
		
		/** if running terminate first */
		thumbnails.terminateDrawing();
		
		commonValues.setProcessing(true);
		
		/**
		 * add outline if appropriate in a scrollbar on the left to
		 * replicate L & F or Acrobat
		 */
		if (!hasOutlinesDrawn) {
			hasOutlinesDrawn=true;
			createOutlinePanels();
		}
		
		SwingWorker worker = new SwingWorker() {
			public Object construct() {
				
				
				try {
					
					statusBar.updateStatus("Decoding Page",0);
					
					/**
					 * make sure screen fits display nicely
					 */
					if ((resizePanel) && (thumbnails.isShownOnscreen())) 
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
												
						//<start-forms>
						//read annotations data
						//commonValues.setPageAnnotations(decode_pdf.getPdfAnnotsData(null));
						//<end-forms>
						
						statusBar.updateStatus("Displaying Page",0); 
						
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
					/**/
					if ((thumbnails.isShownOnscreen())) {
						thumbnails.addNewThumbnails(commonValues.getCurrentPage(),decode_pdf);
					} else
						commonValues.setProcessing(false);
					
					//make sure fully drawn
					decode_pdf.repaint();
					
					//<start-13>
					//setViewerTitle(mainTitle); //restore title
					//<end-13>
					
					
					if (thumbnails.isShownOnscreen()) {
						
						/**setup thumbnails in foreground*/
						thumbnails.setupThumbnailsOnDecode(commonValues.getCurrentPage(),decode_pdf);
						
					}
					
				} catch (Exception e) {
					//<start-13>
					//setViewerTitle(mainTitle); //restore title
					//<end-13>
				}
				
				statusBar.setProgress(100);

				//reanable user changing scaling 
				resetComboBoxes(true);
				
				
				//<start-forms>
				addFormsListeners();
				
				/*
				if(decode_pdf.isXFAForm()){
					showMessageDialog("This PDF contains XFA form objects\n" +
							"Support for these will be in JPedal release 3.0\n" +
					"Available from http://www.jpedal.org in early 2006");
				}
				*/
				
				//<end-forms>
				
				return null;
			}
		};
		
		worker.start();
		
	}
	
	//<start-forms>
	/**this method adds listeners to GUI widgets to track changes*/
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
					"Code is in addExampleListeners() in jmlib.pdf.pdfviewer.PDFViewer\n\n"+
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
	
	/**
	 *  put the outline data into a display panel which we can pop up 
	 * for the user - outlines, thumbnails
	 */
	private void createOutlinePanels() {
	
		/**
		 * set up first 10 thumbnails by default. Rest created as needed.
		 */
		//add if statement or comment out this section to remove thumbnails
		if(thumbnails.isShownOnscreen()){
			
			int pages=decode_pdf.getPageCount();
			
			if(pages>100){
				thumbnails.setIsDisplayedOnscreen(false);
				LogWriter.writeLog("Thumbnails not used on files over 100 pages long");
			}else{
				
				//add listener so clicking on button changes to page - has to be in SimpleViewer so it can update it
				Object[] buttons=thumbnails.getButtons();
				for(int i=0;i<pages;i++)
					((JButton)buttons[i]).addActionListener(new PageChanger(i));
				
				//<start-13>
				//add global listener
				thumbnails.addComponentListener();
				//<end-13>
				
			}
			
		}
		
		/**
		 * add any outline
		 */
		if(decode_pdf.hasOutline()&& showOutlines){
			
			/**graphical display*/
			
			Node rootNode= decode_pdf.getOutlineAsXML().getFirstChild();
			if(rootNode!=null){
				
				tree=new SwingOutline(rootNode);
				
				//Listen for when the selection changes - looks up dests at present
				((JTree) tree.getTree()).addTreeSelectionListener(new TreeSelectionListener(){
					
					/** Required by TreeSelectionListener interface. */
					public void valueChanged(TreeSelectionEvent e) {
						
						if(tree.isIgnoreAlteredBookmark())
							return;
						
						DefaultMutableTreeNode node = tree.getLastSelectedPathComponent();
						
						if (node == null) return;
										
						/**get title and open page if valid*/
						String title=(String)node.getUserObject();
						String page=tree.getPage(title);
						
						if((page!=null)&&(page.length()>0)){
							int pageToDisplay=Integer.parseInt(page);
							
							if((!commonValues.isProcessing())&&(commonValues.getCurrentPage()!=pageToDisplay)){
								commonValues.setCurrentPage(pageToDisplay);
								/**reset as rotation may change!*/
								setScalingToDefault();
								
								decode_pdf.setPageParameters(getScaling(), commonValues.getCurrentPage());
								decodePage(false);
							}
							
							Point p= tree.getPoint(title);
							if(p!=null)
								decode_pdf.ensurePointIsVisible(p);
							
						}else
							System.out.println("No dest page set for "+title);
					}
				});
				
			}
		}		
	}
	
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#removeOutlinePanels()
	 */
	public void removeOutlinePanels() {
		
		/**
		 * reset left hand nav bar
		 */
		thumbnails.removeAll();
		if(tree!=null)
			tree.setMinimumSize(new Dimension(50,frame.getHeight()));
		
		/**flag for redraw*/
		hasOutlinesDrawn=false;		
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#initStatus()
	 */
	public void initStatus() {
		decode_pdf.setStatusBarObject(statusBar);
		resetStatus();
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#resetStatus()
	 */
	public void resetStatus() {
		//set status bar child color
		statusBar.setColorForSubroutines(Color.blue);
		//and initialise the display
		bottomNavButtons.add(statusBar.getStatusObject());
		
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#initThumbnails(int, org.jpedal.utils.repositories.Vector_Int)
	 *
	public void initThumbnails(int itemSelectedCount, Vector_Int pageUsed) {
		navOptionsPanel.removeAll();
		displayPane.setDividerLocation(divLocation);
		navOptionsPanel.add(thumbnails.setupThumbnails(itemSelectedCount-1,pageUsed.get(),commonValues.getPageCount()),"Extracted items");

	}
	*/
	
//	<start-forms>
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
			//setViewerTitle(mainTitle);
			
			if(showMessage)
				JOptionPane.showMessageDialog(c,"FormName >>"+formName+"<<. Value changed to "+value);
			
		}
	}
	//<end-forms>
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setCoordText(java.lang.String)
	 */
	public void setCoordText(String string) {
		coords.setText(string);
	}
/*	
	private JLabel initCoordBox() {
		
		coords.setBackground(Color.white);
		coords.setOpaque(true);
		coords.setBorder(BorderFactory.createLineBorder(Color.black,1));
		
		return coords;
		
	}
	*/
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#toggleSnapshotButton()
	 *
	public void toggleSnapshotButton() {
			snapshotButton.setIcon(new ImageIcon(getClass().getResource("/jmlib/pdf/pdfviewer/res/snapshot.gif")));
		
		
	}
	*/
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setPageNumber()
	 */
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
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#addToMainMenu(javax.swing.JMenu)
	 *
	public void addToMainMenu(JMenu fileMenuList) {
		currentMenu.add(fileMenuList);
		
	}
	*/
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#getTopButtonBar()
	 */
	public JToolBar getTopButtonBar() {
		return topButtons;
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#showMessageDialog(java.lang.Object)
	 */
	public void showMessageDialog(Object message1){
		JOptionPane.showMessageDialog(frame,message1);
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#showMessageDialog(java.lang.Object, java.lang.String, int)
	 */
	public void showMessageDialog(Object message,String title,int type){
		JOptionPane.showMessageDialog(frame,message,title,type);
	}
	
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#showInputDialog(java.lang.Object, java.lang.String, int)
	 */
	public String showInputDialog(Object message, String title, int type) {
		return JOptionPane.showInputDialog(frame, message, title, type);
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#showInputDialog(java.lang.String)
	 */
	public String showInputDialog(String message) {
		
		return 	JOptionPane.showInputDialog(frame,message);
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#showOptionDialog(java.lang.Object, java.lang.String, int, int, java.lang.Object, java.lang.Object[], java.lang.Object)
	 */
	public int showOptionDialog(Object displayValue, String message, int option, int type, Object icon, Object[] options, Object initial) {
		
		return JOptionPane.showOptionDialog(frame, displayValue,message,option,type, (Icon)icon, options,initial);
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#showConfirmDialog(java.lang.String, java.lang.String, int)
	 */
	public int showConfirmDialog(String message, String message2, int option) {
		
		return JOptionPane.showConfirmDialog(frame, message,message2,option);
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#showOverwriteDialog(String file,boolean yesToAllPresent)
	 */
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
			
		}else{
			n = JOptionPane.showOptionDialog(frame, 
					file+"\nThe file already exists\nReplace the existing file?",
					"Overwrite?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,null,null);
		}
		
		return n;
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#showMessageDialog(javax.swing.JTextArea)
	 */
	public void showMessageDialog(JTextArea info) {
		JOptionPane.showMessageDialog(frame, info);
		
	}
	
	/*
	public void showItextPopup() {
		JEditorPane p = new JEditorPane(
				"text/html",
				"Itext is not on the classpath.<BR>"
						+ "JPedal includes code to take advantage of itext and<BR>"
						+ "provide additional functionality with options<BR>"
						+ "to spilt pdf files, and resave forms data<BR>"
						+ "\nItext website - <a href=http://www.lowagie.com/iText/>http://www.lowagie.com/iText/</a>");
		p.setEditable(false);
		p.setOpaque(false);
		p.addHyperlinkListener( new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
					try {
						BrowserLauncher.openURL("http://www.lowagie.com/iText/");
					} catch (IOException e1) {
						showMessageDialog("Unable to launch browser");
					}
				}
			}
		});
		
		//<start-13>
		showMessageDialog(p);
		//<end-13>
		
		// Hack for 13 to make sure the message box is large enough to hold the message
		//<start-13>
		/**
		//<end-13>
		JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage(p);
		optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		optionPane.setOptionType(JOptionPane.DEFAULT_OPTION);
		
		JDialog dialog = optionPane.createDialog(frame, "iText");
		dialog.pack();
		dialog.setSize(400,200);
		dialog.show(); 
		
	}
*/
	
/*	
	public void showFirstTimePopup(){
		
		try{
			final JPanel a = new JPanel();
			a.setLayout(new BorderLayout());
			JLabel lab=new JLabel(new ImageIcon(getClass().getResource("/org/jpedal/objects/acroforms/ceo.jpg")));
			
			//lab.setBorder(BorderFactory.createRaisedBevelBorder());
			a.add(lab,BorderLayout.NORTH);
			final JLabel message=new JLabel("<html>JPedal library from www.jpedal.org");
			message.setHorizontalAlignment(JLabel.CENTER);
			message.setForeground(Color.blue);
			message.setFont(new Font("Lucida",Font.PLAIN,16));
			
			message.addMouseListener(new MouseListener() {
				public void mouseEntered(MouseEvent e) {
					a.setCursor(new Cursor(Cursor.HAND_CURSOR));
					message.setText("<html><a href=http://www.jpedal.org>JPedal library from www.jpedal.org</a>");
				}
				
				public void mouseExited(MouseEvent e) {
					a.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					message.setText("<html>JPedal library from www.jpedal.org");
				}
				
				public void mouseClicked(MouseEvent e) {
					try {
						BrowserLauncher.openURL("http://www.jpedal.org");
					} catch (IOException e1) {
						showMessageDialog("Unable to launch browser");
					}
				}
				
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
			});	
			
			
			a.add(message,BorderLayout.CENTER);
			
			a.setPreferredSize(new Dimension(300,214));
			Object[] options = { "Run Software" };
			int n =
				JOptionPane.showOptionDialog(
						frame,
						a,
						"Running JPedal for first Time",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						null,
						options,
						options[0]);
		}catch(Exception e){
			//JOptionPane.showMessageDialog(null, "caught an exception "+e);
			System.err.println("Unable to find image and setup splash screen");
		}catch(Error e){
			//JOptionPane.showMessageDialog(null, "caught an error "+e);
			System.err.println("Unable to find image and setup splash screen");
		}
	}
	
	*/
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#showConfirmDialog(java.lang.Object, java.lang.String, int, int)
	 */
	public void showConfirmDialog(Object label, String message, int option, int plain_message) {
		JOptionPane.showConfirmDialog(frame,label,message,option,plain_message);
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#initPDFOutlines(org.w3c.dom.Node, java.lang.String)
	 */
	public String initPDFOutlines(Node rootNode, String bookmark) {
		
		tree=new SwingOutline(rootNode);
		
		//tree.readChildNodes(rootNode,null);
		
		return tree.getPage(bookmark);
		
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#removeThumbnails()
	 */
	public void removeThumbnails() {
		//displayPane.setDividerLocation(0);
		setPDFOutlineVisible(false);
		//navOptionsPanel.removeAll();
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setSplitDividerLocation(int)
	 */
	public void setSplitDividerLocation(int size) {
		//displayPane.setDividerLocation(size);
		
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#updateStatusMessage(java.lang.String)
	 */
	public void updateStatusMessage(String message) {
		statusBar.updateStatus(message,0);
		
		
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#resetStatusMessage(java.lang.String)
	 */
	public void resetStatusMessage(String message) {
		statusBar.resetStatus(message);
		
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setStatusProgress(int)
	 */
	public void setStatusProgress(int size) {
		statusBar.setProgress(size);	
	}
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#isPDFOutlineVisible()
	 *
	public boolean isPDFOutlineVisible() {
		return navOptionsPanel.isVisible();
		
	} */
	
	/* (non-Javadoc)
	 * @see jmlib.pdf.pdfviewer.gui.swing.GUIFactory#setPDFOutlineVisible(boolean)
	 */
	public void setPDFOutlineVisible(boolean visible) {
		//navOptionsPanel.setVisible(visible);
	}

	public void setQualityBoxVisible(boolean visible){
		//qualityBox.setVisible(visible);
		//optimizationLabel.setVisible(visible);
	}
	
	public void close() {
		frame.dispose();
	}

	public JInternalFrame getFrame() {
		return frame;
	}
}
