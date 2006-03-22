package jmlib.pdf.pdfviewer.gui;

import java.awt.Rectangle;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import jmlib.pdf.pdfviewer.Commands;
import jmlib.pdf.pdfviewer.utils.Printer;

import org.w3c.dom.Node;

public interface GUIFactory {

	public int BUTTONBAR = 0;
	public int NAVBAR = 1;
	
	
	/**sets up layout menu (controls page views - Multiple, facing,etc)
	public void initLayoutMenus(JMenu pageLayout, String[] descriptions,
			int[] value); */

	/**
	 * display a box giving user info about program
	 */
	//public void getInfoBox();

	/**
	 * align rotation combo box to default for page
	 *
	public void resetRotationBox(); */

	/**popup box with details on current PDF file on tabbed Window
	public void showDocumentProperties(String selectedFile, String inputDir,
			long size, int pageCount, int currentPage); */

	/**
	 * main method to initialise Swing specific code and create GUI display
	 */
	public void init(String[] scalingValues, final Commands currentCommands,
			Printer currentPrinter);

	/**add a visible box with updaing cursor location to toolbar
	public void addCursor(); */

	/**
	 *  add button to chosen menu 
	 */
	public void addButton(int line, String toolTip, String path, final int ID);

	/**
	 * setup menu items and add to menu
	 */
	public void addMenuItem(JMenu parentMenu, String text, String toolTip,
			final int ID);

	/**add selected combo - 3 possible values hard-coded (quality, scaling and rotation)*/
	public void addCombo(String title, String tooltip, int ID);

	/** set scaling value to 100% and align scaling box*/
	public void setScalingToDefault();

	/**
	 * set title or over-ride with message
	 */
	public void setViewerTitle(final String title);

	/**set all 3 combo boxes to isEnabled(value)*/
	public void resetComboBoxes(boolean value);

	/**
	 * do xml in popup as nicely coloured text
	 *
	public JScrollPane createPane(JTextPane text_pane, String content,
			boolean useXML) throws BadLocationException; */

	/**get current value for a combobox (options QUALITY,SCALING,ROTATION)*/
	public int getSelectedComboIndex(int ID);

	/**set current index for a combobox (options QUALITY,SCALING,ROTATION)*/
	public void setSelectedComboIndex(int ID, int index);

	/**get current Item for a combobox (options QUALITY,SCALING,ROTATION)*/
	public void setSelectedComboItem(int ID, String index);

	/**get current Item for a combobox (options QUALITY,SCALING,ROTATION)*/
	public Object getSelectedComboItem(int ID);

	/**set rectangle to draw onscreen*/
	public void setRectangle(Rectangle newRect);

	/**get rectangle to draw onscreen*/
	public Rectangle getRectangle();

	/** 
	 * zoom into page 
	 */
	public void zoom();

	/**
	 *get current rotation
	 */
	public int getRotation();

	/**
	 * get current scaling
	 */
	public float getScaling();

	/**
	 * get inset between edge of JPanel and PDF page
	 */
	public int getPDFDisplayInset();

	/**read value from rotation box and apply - called by combo listener
	public void rotate(); */

	/**toggle state of autoscrolling on/off*/
	public void toogleAutoScrolling();

	/**
	 * called by nav functions to decode next page
	 * (in GUI code as needs to manipulate large part of GUI)
	 */
	public void decodePage(final boolean resizePanel);

	/**
	 * remove outlines and flag for redraw
	 */
	public void removeOutlinePanels();

	/**
	 * show/hide PDF outline panel
	 *
	public void togglePDFOutline(); */

	public void initStatus();

	public void resetStatus();

	//public void initThumbnails(int itemSelectedCount, Vector_Int pageUsed);

	/**flush list of pages decoded*/
	public void setNoPagesDecoded();

	/**set text displayed in cursor co-ordinates box*/
	public void setCoordText(String string);

	/**change snapshot according to commonValues.isExtractImageOnSelection()*/
	//public void toggleSnapshotButton();

	/**set page number at bottom of screen*/
	public void setPageNumber();

	/**add MenuItem to main menu*/
	//public void addToMainMenu(JMenu fileMenuList);

	/**allow access to root frame if required*/
	public JInternalFrame getFrame();

	/**allow access to top button bar directly - used by ContentExtractor.
	 * For general use it is recommended you use addButton method
	 */
	public JToolBar getTopButtonBar();

	public void showMessageDialog(Object message1);

	public void showMessageDialog(Object message, String title, int type);

	public String showInputDialog(Object message, String title, int type);

	public String showInputDialog(String message);

	public int showOptionDialog(Object displayValue, String message,
			int option, int type, Object icon, Object[] options, Object initial);

	public void showMessageDialog(JTextArea info);

	public int showConfirmDialog(String message, String message2, int option);
	
	public int showOverwriteDialog(String file,boolean yesToAllPresent);

	//public void showItextPopup();
	
	public void showConfirmDialog(Object label, String message, int option,
			int plain_message);

	public String initPDFOutlines(Node rootNode, String bookmark);

	/**show if user has set auto-scrolling on or off - if on moves at edge of panel to show more*/
	public boolean allowScrolling();

	//public boolean isPDFOutlineVisible();

	public void setPDFOutlineVisible(boolean visible);

	/**
	 * remove the thumbnail display panel
	 */
	public void removeThumbnails();

	/**set location of split pane between main PDF and outline/thumbnail panel*/
	public void setSplitDividerLocation(int size);

	/**message to show in status object*/
	public void updateStatusMessage(String message);

	public void resetStatusMessage(String message);

	/**set current status value 0 -100*/
	public void setStatusProgress(int size);

	public void setQualityBoxVisible(boolean visible);
	
}