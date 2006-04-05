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
 * SwingThumbnailPanel.java
 * ---------------
 * (C) Copyright 2005, by IDRsolutions and Contributors.
 *
 * 
 * --------------------------
 */
package common.pdf.pdfviewer.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.media.jai.JAI;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import common.pdf.pdfviewer.Values;
import common.pdf.pdfviewer.gui.generic.GUIThumbnailPanel;
import common.pdf.pdfviewer.utils.SwingWorker;

import org.jpedal.PdfDecoder;
import org.jpedal.objects.PdfPageData;
import org.jpedal.utils.LogWriter;
import org.jpedal.utils.repositories.Vector_Object;

/**
 * Used in GUI example code Scope:<b>(All)</b>
 * <br>adds thumbnail capabilities to viewer, 
 * <br>shows pages as thumbnails within this panel, 
 * <br>So this panel can be added to the viewer
 * 
 */
public class SwingThumbnailPanel extends JPanel implements GUIThumbnailPanel {
	
	private static final long serialVersionUID = 7423353291901251301L;
	
	/**Swing thread to decode in background - we have one thread we use for various tasks*/
	SwingWorker worker=null;
	
	//<start-13>
	/**handles drawing of thumbnails if needed*/
	private ThumbPainter painter=new ThumbPainter();
	//<end-13>
	
	/**can switch on or off thumbnails - DOES NOT WORK ON JAVA 1.3*/
	private boolean showThumbnailsdefault=false;
	
	private boolean showThumbnails=showThumbnailsdefault;
	
	/**flag to allow interruption in orderly manner*/
	public boolean interrupt=false;
	
	/**flag to show drawig taking place*/
	public boolean drawing;
	
	/**
	 * thumbnails settings below
	 */
	/**buttons to display thumbnails*/
	private JButton[] pageButton;
	
	private boolean[] buttonDrawn;
	
	private boolean[] isLandscape;
	
	private int[] pageHeight;
	
	/**weight and height for thumbnails*/
	final private int thumbH=100,thumbW=70;
	
	/**Holds all the thumbnails images*/
	private JScrollPane thumbnailScrollPane=new JScrollPane();
	
	Values commonValues;
	PdfDecoder decode_pdf;
	
	public SwingThumbnailPanel(Values commonValues,PdfDecoder decode_pdf){
		
		thumbnailScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		thumbnailScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		this.commonValues=commonValues;
		this.decode_pdf=decode_pdf;
	}
	
//	<start-13>
	/** class to paint thumbnails */
	private class ThumbPainter extends ComponentAdapter {
		
		/** used to track user stopping movement */
		Timer trapMultipleMoves = trapMultipleMoves = new Timer(250,
				new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				
				if (worker != null){
					///tell our code to exit cleanly asap
					if(drawing){
						interrupt=true;
						while(drawing){
							
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
								// should never be called
								e.printStackTrace();
							}
						}
						interrupt=false; //ensure synched
					}
				}
				
				/** if running wait to finish */
				while (commonValues.isProcessing()) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// should never be called
						e.printStackTrace();
					}
				}
				
				/**create any new thumbnails revaled by scroll*/
				drawVisibleThumbnailsOnScroll(decode_pdf);
				
			}
		});
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
		 */
		public void componentMoved(ComponentEvent e) {
			
			//allow us to disable on scroll
			if (trapMultipleMoves.isRunning())
				trapMultipleMoves.stop();
			
			trapMultipleMoves.setRepeats(false);
			trapMultipleMoves.start();
			
		}
	}
	//<end-13>
	
	/**
	 * create thumbnails of pages
	 * @throws InterruptedException
	 */
	public void createThumbnailsOnDecode(int currentPage,PdfDecoder decode_pdf){
		
		drawing=true;
		
		/** draw thumbnails in background */
		int pages = decode_pdf.getPageCount();
		
		for (int j = -5; j < 5; j++) {
			
			int i = j + currentPage;
			
			if(interrupt){
				j=5;
			}
			if (i >= pages) {
				j = 5;
			} else if ((i > 0) && (!buttonDrawn[i])
					&& (pageButton[i] != null)) {
				
				int h = thumbH;
				if (isLandscape[i])
					h = thumbW;
				
				BufferedImage page = decode_pdf.getPageAsThumbnail(i + 1, h);
				
				if (interrupt)
					j=5;
				else
					createThumbnail(page, i + 1, false);
			}
		}
		
		//reset to show not running
		interrupt=false;
		
		drawing=false;
	}
	
	/**
	 * create thumbnails of general images
	 * @param thumbnailsStored
	 * @throws InterruptedException
	 */
	public void createThumbnailsFromImages(String[] imageFiles, Vector_Object thumbnailsStored) {
		
		drawing=true;
		
		/** draw thumbnails in background */
		int pages = imageFiles.length;
		
		for (int i = 0; i < pages; i++) {
			
			//load the image to process
			BufferedImage page = null;
			try {
				// Load the source image from a file or cache
				if(imageFiles[i]!=null){
					
					Object cachedThumbnail=thumbnailsStored.elementAt(i);
					
					if(cachedThumbnail==null){
						page = JAI.create("fileload", imageFiles[i]).getAsBufferedImage();
						thumbnailsStored.addElement(page);
					}else{
						page=(BufferedImage)cachedThumbnail;
					}
					
					if(page!=null){
						
						int w=page.getWidth();
						int h=page.getHeight();
						
						/**add a border*/
						Graphics2D g2=(Graphics2D) page.getGraphics();
						g2.setColor(Color.black);
						g2.draw(new Rectangle(0,0,w-1,h-1));
						
						/**scale and refresh button*/
						ImageIcon pageIcon;
						if(h>w)
							pageIcon=new ImageIcon(page.getScaledInstance(-1,100,BufferedImage.SCALE_SMOOTH));
						else
							pageIcon=new ImageIcon(page.getScaledInstance(100,-1,BufferedImage.SCALE_SMOOTH));
						
						pageButton[i].setIcon(pageIcon);
						pageButton[i].setVisible(true);
						buttonDrawn[i] = true;
						
						this.add(pageButton[i]);
						
					}
				}
			} catch (Exception e) {
				LogWriter.writeLog("Exception " + e + " loading " + imageFiles[i]);
			}	
		}
		
		drawing=false;
	}
	
	/**
	 * setup thumbnails if needed
	 */
	public void setupThumbnailsOnDecode(final int currentPage,PdfDecoder decode_pdf){
		
		int count = decode_pdf.getPageCount();
		
		for (int i1 = 0; i1 < count; i1++) {
			if ((i1 != currentPage - 1))
				pageButton[i1].setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		}
		
		//set button and scroll to
		if ((count > 1) && (currentPage > 0))
			pageButton[currentPage - 1].setBorder(BorderFactory.createLineBorder(Color.red));
		
		//update thumbnail pane if needed
		Rectangle rect = getVisibleRect();
		if (!rect.contains(pageButton[currentPage - 1].getBounds())) {
			
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					
					public void run() {
						scrollRectToVisible(pageButton[currentPage - 1].getBounds());
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		commonValues.setProcessing(false);
		
		/** draw thumbnails in background */
		createThumbnailsOnDecode(currentPage,decode_pdf);
		
	}
	
	/**
	 * redraw thumbnails if scrolled
	 */
	public void drawVisibleThumbnailsOnScroll(final PdfDecoder decode_pdf){
		
		//create the thread to just do the thumbnails
		SwingWorker worker = new SwingWorker() {
			
			
			public Object construct() {
				
				drawing=true;
				
				try {
					Rectangle rect = getVisibleRect();
					int pages = decode_pdf.getPageCount();
					
					for (int i = 0; i < pages; i++) {
						
						if (interrupt)
							i=pages;
						
						else if ((i>0)&&(!buttonDrawn[i])
								&& (pageButton[i] != null)
								&& (rect.intersects(pageButton[i].getBounds()))) {
							
							
							int h = thumbH;
							if (isLandscape[i]){
								
								h = thumbW;
							}
							//float scaleFactor = (float) h
							/// (float) pageHeight[i];
							
							BufferedImage page = decode_pdf
							.getPageAsThumbnail(
									i + 1, h);
							
							createThumbnail(page, i + 1, false);
						}
					}
					
				} catch (Exception e) {
					//stopped thumbnails
				}
				
				//always reset flag so we can interupt
				interrupt=false;
				
				drawing=false;
				
				return null;
			}
		};
		
		worker.start();
	}
	
	/**add any new thumbnails needed to display*/
	public void addNewThumbnails(int currentPage,PdfDecoder decode_pdf){
		//if not drawn get page and flag
		if ((buttonDrawn[currentPage - 1] == false)) {
			
			int h = thumbH;
			if (isLandscape[currentPage - 1])
				h = thumbW;
			
			BufferedImage page = decode_pdf.getPageAsThumbnail(h);
			
			createThumbnail(page, currentPage, true);
		}
	}
	
	/**
	 * create a blank tile with a cross to use as a thumbnail for unloaded page
	 */
	private BufferedImage createBlankThumbnail(int w, int h) {
		BufferedImage blank=new BufferedImage(w+1,h+1,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2=(Graphics2D) blank.getGraphics();
		g2.setColor(Color.white);
		g2.fill(new Rectangle(0,0,w,h));
		g2.setColor(Color.black);
		g2.draw(new Rectangle(0,0,w,h));
		g2.drawLine(0,0,w,h);
		g2.drawLine(0,h,w,0);
		return blank;
	}
	
	
	/**
	 *setup a thumbnail button in outlines
	 */
	private void createThumbnail(BufferedImage page, int i,boolean highLightThumbnail) {
		
		i--; //convert from page to array
		
		if(page!=null){
			/**add a border*/
			Graphics2D g2=(Graphics2D) page.getGraphics();
			g2.setColor(Color.black);
			g2.draw(new Rectangle(0,0,page.getWidth()-1,page.getHeight()-1));
			
			/**scale and refresh button*/
			ImageIcon pageIcon=new ImageIcon(page.getScaledInstance(-1,page.getHeight(),BufferedImage.SCALE_FAST));
			
			pageButton[i].setIcon(pageIcon);
			
			buttonDrawn[i] = true;
			
		}
	}
	
	/**
	 * setup thumbnails at start - use for general images
	 * @return number of pages, the number of Pages used
	 * and the pageCount
	 */
	public Component setupThumbnails(int pages,int[] pageUsed,int pageCount) {
		
		Font textFont=new Font("Serif",Font.PLAIN,12);
		
		//remove any added last time
		this.removeAll();
		
		//create dispaly for thumbnails
		thumbnailScrollPane.getViewport().add(this);
		setLayout(new GridLayout(pages,1,0,10));
		scrollRectToVisible(new Rectangle(0,0,1,1));
		
		thumbnailScrollPane.getVerticalScrollBar().setUnitIncrement(80);
		
		//create empty thumbnails and add to display
		BufferedImage blankPortrait = createBlankThumbnail(thumbW, thumbH);
		ImageIcon portrait=new ImageIcon(blankPortrait.getScaledInstance(-1,
				100,BufferedImage.SCALE_SMOOTH));
		
		isLandscape=new boolean[pages];
		pageHeight=new int[pages];
		pageButton=new JButton[pages];
		buttonDrawn=new boolean[pages];
		
		for(int i=0;i<pages;i++){
			
			int page=i+1;
			
			if(pageCount<2)
				pageButton[i]=new JButton(""+page,portrait); //$NON-NLS-2$
			else
				pageButton[i]=new JButton(""+page+" ( Page "+pageUsed[i]+" )",portrait); //$NON-NLS-2$
			isLandscape[i]=false;
			pageHeight[i]=100;
			
			pageButton[i].setVerticalTextPosition(AbstractButton.BOTTOM);
			pageButton[i].setHorizontalTextPosition(AbstractButton.CENTER);
			if((i==0)&&(pages>1))
				pageButton[0].setBorder(BorderFactory.createLineBorder(Color.red));
			else
				pageButton[i].setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			
			pageButton[i].setFont(textFont);
			add(pageButton[i],BorderLayout.CENTER);
			
		}
		
		return thumbnailScrollPane;
	}
	
	/**reset the highlights*/
	public void resetHighlightedThumbnail(int item){
		
		if(pageButton!=null){
			int pages=pageButton.length;
			
			for(int i=0;i<pages;i++){
				
				if((i==item))
					pageButton[i].setBorder(BorderFactory.createLineBorder(Color.red));
				else
					pageButton[i].setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			}
		}
	}
	
	/**
	 * setup thumbnails at start - use when adding pages
	 */
	public Component setupThumbnails(int pages,Font textFont,String message,PdfPageData pageData) {
		
		//create dispaly for thumbnails
		thumbnailScrollPane.getViewport().add(this);
		setLayout(new GridLayout(pages,1,0,10));
		scrollRectToVisible(new Rectangle(0,0,1,1));
		
		thumbnailScrollPane.getVerticalScrollBar().setUnitIncrement(80);
		
		//create empty thumbnails and add to display
		
		//empty thumbnails for unloaded pages
		BufferedImage blankPortrait = createBlankThumbnail(thumbW, thumbH);
		BufferedImage blankLandscape = createBlankThumbnail(thumbH,thumbW);
		ImageIcon landscape=new ImageIcon(blankLandscape.getScaledInstance(-1,
				70,BufferedImage.SCALE_SMOOTH));
		ImageIcon portrait=new ImageIcon(blankPortrait.getScaledInstance(-1,
				100,BufferedImage.SCALE_SMOOTH));
		
		isLandscape=new boolean[pages];
		pageHeight=new int[pages];
		pageButton=new JButton[pages];
		buttonDrawn=new boolean[pages];
		
		for(int i=0;i<pages;i++){
			
			int page=i+1;
			
			//create blank image with correct orientation
			final int ph;
			int cropWidth=pageData.getCropBoxWidth(page);
			int cropHeight=pageData.getCropBoxHeight(page);
			int rotation=pageData.getRotation(page);
			ImageIcon usedLandscape,usedPortrait;
			
			if((rotation==0)|(rotation==180)){
				ph=(pageData.getMediaBoxHeight(page));
				//pw=(pageData.getMediaBoxWidth(page));//%%
				usedLandscape = landscape;
				usedPortrait = portrait;
			}else{
				ph=(pageData.getMediaBoxWidth(page));
				//pw=(pageData.getMediaBoxHeight(page));//%%
				usedLandscape = portrait;
				usedPortrait = landscape;
			}
			
			if(cropWidth>cropHeight){
				pageButton[i]=new JButton(message+" "+page,usedLandscape); //$NON-NLS-2$
				isLandscape[i]=true;
				pageHeight[i]=ph;//w;%%
			}else{
				pageButton[i]=new JButton(message+" "+page,usedPortrait); //$NON-NLS-2$
				isLandscape[i]=false;
				pageHeight[i]=ph;
			}
			
			pageButton[i].setVerticalTextPosition(AbstractButton.BOTTOM);
			pageButton[i].setHorizontalTextPosition(AbstractButton.CENTER);
			if((i==0)&&(pages>1))
				pageButton[0].setBorder(BorderFactory.createLineBorder(Color.red));
			else
				pageButton[i].setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			
			pageButton[i].setFont(textFont);
			add(pageButton[i],BorderLayout.CENTER);
			
		}
		
		return thumbnailScrollPane;
	}
	
	/**
	 *return a button holding the image,so we can add listener
	 */
	public Object[] getButtons() {
		return pageButton;
	}
	
	public void setThumbnailsEnabled() {
		showThumbnailsdefault=true;
		showThumbnails=true;
		
	}
	
	public boolean isShownOnscreen() {
		
		return showThumbnails;
	}
	
	public void resetToDefault() {
		showThumbnails=showThumbnailsdefault;
		
		
	}
	
	public void setIsDisplayedOnscreen(boolean b) {
		showThumbnails=b;
		
	}
	
	public void stopProcessing() {
		if(isShownOnscreen()){
			//tell our code to exit cleanly asap
			if(drawing){
				interrupt=true;
				while(drawing){
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// should never be called
						e.printStackTrace();
					}
				}
				interrupt=false; //ensure synched
			}
		}
		
	}
	
	public void addComponentListener() {
		//<start-13>
		addComponentListener(painter);
		//<end-13>
		
	}
	
	public void removeComponentListener() {
		//<start-13>
		removeComponentListener(painter);
		//<end-13>
		
	}
	
	/**stop any drawing*/
	public void terminateDrawing() {
		
		//tell our code to exit cleanly asap
		if(drawing){
			interrupt=true;
			while(drawing){
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// should never be called
					e.printStackTrace();
				}
			}
			interrupt=false; //ensure synched
		}
		
	}
	
	public void refreshDisplay() {
		validate();
	}
	
}
