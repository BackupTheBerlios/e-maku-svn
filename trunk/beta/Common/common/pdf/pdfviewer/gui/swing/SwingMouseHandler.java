/**
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.jpedal.org
 * Project Lead:  Mark Stephens (mark@idrsolutions.com)
 *
 * (C) Copyright 2006, IDRsolutions and Contributors.
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
 * SwingMouseHandler.java
 * ---------------
 *
 * Original Author:  Mark Stephens (mark@idrsolutions.com)
 * Contributor(s):
 *
 */
package common.pdf.pdfviewer.gui.swing;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import common.pdf.pdfviewer.Commands;
import common.pdf.pdfviewer.Values;
import common.pdf.pdfviewer.gui.SwingGUI;
import common.pdf.pdfviewer.gui.generic.GUIMouseHandler;

import org.jpedal.PdfDecoder;
/**handles all mouse activity in GUI using Swing classes*/
public class SwingMouseHandler implements GUIMouseHandler{
	
	private static final long serialVersionUID = 7423353291901251301L;
	private PdfDecoder decode_pdf;
	private SwingGUI currentGUI;
	private Values commonValues;
	
	//private Commands currentCommands;
	
	/**tells user if we enter a link*/
	private String message="";
	
	/** cursor rectangle handles */
	private Rectangle[] boxes = new Rectangle[8];
	
	/** the extra gap for the cursorBox handlers highlighting */
	private int handlesGap = 5;
	
	/** old x and y values for where drag original location was */
	private int oldX=-1,oldY=-1;
	
	/** flag to tell whether drag altering currentRectangle */
	private boolean dragAltering=false;
	
	/** which handle box is being altered */
	private int boxContained = -1;
	
	/** to allow new cursor box to be drawn */
	private boolean drawingCursorBox=false;
	
	/**used to track changes when dragging rectangle around*/
	private int old_m_x2=-1,old_m_y2=-1;
	
	/** for MOUSE LISTENERS ONLY for update the current cursor position */
	private int x,y;
	
	/**current cursor position*/
	private int cx,cy;
	
	/**
	 * picks up clicks so we can draw an outline on screen
	 */
	protected class mouse_clicker extends MouseAdapter {
		
		//user has pressed mouse button so we want to use this 
		//as one point of outline
		public void mousePressed(MouseEvent event) {
			
			float scaling=currentGUI.getScaling();
			int inset=currentGUI.getPDFDisplayInset();
			int rotation=currentGUI.getRotation();
			
			//get co-ordinates of top point of outine rectangle
			int x=(int)((event.getX()-inset)/scaling);
			int y=(int)((event.getY()-inset)/scaling);
			
			//undo any viewport scaling (no crop assumed
			if(commonValues.maxViewY!=0){ // will not be zero if viewport in play
				x=(int)(((x-(commonValues.dx*scaling))/commonValues.viewportScale));
				y=(int)((currentGUI.mediaH-((currentGUI.mediaH-(y/scaling)-commonValues.dy)/commonValues.viewportScale))*scaling);
			}
			
			if (rotation == 90) {
				commonValues.m_y1 = x+currentGUI.cropY;
				commonValues.m_x1 = y+currentGUI.cropX;
			} else if ((rotation == 180)) {
				commonValues.m_x1 = currentGUI.mediaW - (x+currentGUI.mediaW-currentGUI.cropW-currentGUI.cropX);
				commonValues.m_y1 = y+currentGUI.cropY;
			} else if ((rotation == 270)) {
				commonValues.m_y1 = currentGUI.mediaH - (x+currentGUI.mediaH-currentGUI.cropH-currentGUI.cropY);
				commonValues.m_x1 = currentGUI.mediaW - (y+currentGUI.mediaW-currentGUI.cropW-currentGUI.cropX);
			} else {
				commonValues.m_x1 = x+currentGUI.cropX;
				commonValues.m_y1 = currentGUI.mediaH - (y+currentGUI.mediaH-currentGUI.cropH-currentGUI.cropY);
			}
			
			updateCords(event);
			
		}
		
		//show the description in the text box or update screen
		public void mouseClicked(MouseEvent event) {
		}
		
		//user has stopped clicking so we want to remove the outline rectangle
		public void mouseReleased(MouseEvent event) {
			
			old_m_x2 = -1;
			old_m_y2 = -1;
			
			updateCords(event);
			
			/** extract text */
			if (commonValues.isPDF() && event.isShiftDown()){
			}
			
			/** remove any outline and reset variables used to track change */
			decode_pdf.updateCursorBoxOnScreen(null, null); //remove box
			decode_pdf.setHighlightedAreas(null); //remove highlighted text
			
			decode_pdf.repaintArea(new Rectangle(commonValues.m_x1-currentGUI.cropX, commonValues.m_y2+currentGUI.cropY, commonValues.m_x2 - commonValues.m_x1+currentGUI.cropX,
					(commonValues.m_y1 - commonValues.m_y2)+currentGUI.cropY), currentGUI.mediaH);//redraw
			decode_pdf.repaint();
		}
	}
	
	/**listener used to update display*/
	protected class mouse_mover implements MouseMotionListener {
		
		public mouse_mover() {}
		
		public void mouseDragged(MouseEvent event) {
			
			int[] values = updateXY(event);
			commonValues.m_x2=values[0];
			commonValues.m_y2=values[1];
			
			scrollAndUpdateCoords(event);
			
				
			if(commonValues.isPDF())
				generateNewCursorBox();			
			
		}
		
		/**
		 * generate new  cursorBox and highlight extractable text,
		 * if hardware acceleration off and extraction on<br>
		 * and update current cursor box displayed on screen
		 */
		protected void generateNewCursorBox() {
			
			//redraw rectangle of dragged box onscreen if it has changed significantly
			if ((old_m_x2!=-1)|(old_m_y2!=-1)|(Math.abs(commonValues.m_x2-old_m_x2)>5)|(Math.abs(commonValues.m_y2-old_m_y2)>5)) {	
				
				//allow for user to go up
				int top_x = commonValues.m_x1;
				if (commonValues.m_x1 > commonValues.m_x2)
					top_x = commonValues.m_x2;
				int top_y = commonValues.m_y1;
				if (commonValues.m_y1 > commonValues.m_y2)
					top_y = commonValues.m_y2;
				int w = Math.abs(commonValues.m_x2 - commonValues.m_x1);
				int h = Math.abs(commonValues.m_y2 - commonValues.m_y1);
				
				//add an outline rectangle  to the display
				Rectangle currentRectangle=new Rectangle (top_x,top_y,w,h);
				currentGUI.setRectangle(currentRectangle);
				decode_pdf.updateCursorBoxOnScreen(currentRectangle,Color.blue);
				
				//tell JPedal to highlight text in this area (you can add other areas to array)
				Rectangle[] highlightedAreas=new Rectangle[2];
				highlightedAreas[0]=currentRectangle;//change to, redraw increase hieght area. chris
				//highlightedAreas[1]=new Rectangle(300,300,100,100);// - try me to see extra highlights
				/**to view highlighted ares ensure that setHardwareAccelerationforScreen(boolean) is set to false*/
				decode_pdf.setHighlightedAreas(highlightedAreas);
				
				//reset tracking
				old_m_x2=commonValues.m_x2;
				old_m_y2=commonValues.m_y2;
				
			}
		}
		
		public void mouseMoved(MouseEvent event) {			
			updateCords(event);
		}
		
	}
	
	
	/**listener used to update display*/
	protected class Extractor_mouse_clicker extends mouse_clicker {
		
		public void mousePressed(MouseEvent event){
			Rectangle currentRectangle=currentGUI.getRectangle();
			if(currentRectangle==null){
				//draw the first cursor box on screen
				super.mousePressed(event);
				
				//ensure we keep drawing the new cursor box
				drawingCursorBox = true;
			}else{
				int[] values = updateXY(event);
				
				//store current cursor point for use when dragging
				oldX=values[0];
				oldY=values[1];
			}
		}
		
		public void mouseReleased(MouseEvent event) {
			//turn off drawing new cursor box
			drawingCursorBox = false;
			
			old_m_x2 = -1;
			old_m_y2 = -1;
			
			updateCords(event);
			
			/* shuffle points to ensure cursorBox is setup correctly */
			int tmp;
			if(commonValues.m_x1>commonValues.m_x2){
				tmp=commonValues.m_x1;
				commonValues.m_x1=commonValues.m_x2;
				commonValues.m_x2=tmp;
			}
			if(commonValues.m_y1<commonValues.m_y2){
				tmp=commonValues.m_y1;
				commonValues.m_y1=commonValues.m_y2;
				commonValues.m_y2=tmp;
			}
			
			decode_pdf.repaint();//redraw
			
			//turn altering of current cursor box off
			dragAltering=false;
		}
	}
	
	
	
	/**listener used to update display*/
	protected class Extractor_mouse_mover extends mouse_mover {
		
		public void mouseDragged(MouseEvent event) {
			Rectangle currentRectangle=currentGUI.getRectangle();
			//if no rectangle or currently drawing a new rectangle 
			//use simpleViewer mouseDragged
			if(currentRectangle==null || drawingCursorBox){
				decode_pdf.setDrawCrossHairs(true,boxContained,Color.red);
				super.mouseDragged(event);
				boxContained=-1;
				return;
			}
			
			int[] values = updateXY(event);
			
			//generate handle boxes
			boxes=createNewRectangles(currentRectangle);
			
			//test if cursor was in cursor box handles when drag started
			//if we already have a handle selected don't look again
			if(boxContained==-1){
				for(int i=0;i<boxes.length;i++){
					if(boxes[i].contains(oldX,oldY)){
						boxContained = i;
						break;
					}
				}
			}
			
			//if there is a selected handle or we are altering the current cursor box 
			if(boxContained!=-1 || dragAltering){
				
				//turn new rectangle drawing off
				drawingCursorBox = false;
				
				
				//initialise box to be highlighted with current selected handle
				int highlightBox=boxContained;
				
				//get centre coords of selected box
				int boxCenterX = (int)boxes[boxContained].getCenterX();
				int boxCenterY = (int)boxes[boxContained].getCenterY();
				
//				boolean top=false,bottom=false,left=false,right=false;//Checking code
				/**check which line is to be altered in the x axis and change cursor box values*/
				if(currentRectangle.x==boxCenterX){//left
					commonValues.m_x1=values[0];
//					left =true;//Checking code
				}else if(currentRectangle.x+currentRectangle.width ==boxCenterX){//right
					commonValues.m_x2=values[0];
//					right =true;//Checking code
				}
				
				/**check which line is to be altered in the y axis and change cursor box values*/
				if(currentRectangle.y==boxCenterY){//bottom
					commonValues.m_y2=values[1];
//					bottom =true;//Checking code
				}else if(currentRectangle.y+currentRectangle.height ==boxCenterY){//top
					commonValues.m_y1=values[1];
//					top=true;//Checking code
				}
				
//				System.out.println("top="+top+" bottom="+bottom+" left="+left+" right="+right+" "+highlightBox);//Checking code
				/**
				 * work out whether the handle highlight should be changed
				 * and which way it should be changed
				 */
				boolean changeX=false,changeY=false;
				if(commonValues.m_x1>commonValues.m_x2){
					changeX=true;
				}
				if(commonValues.m_y2>commonValues.m_y1){
					changeY=true;
				}
				
				/**if a highlight should be changed, change it*/
				if(changeX || changeY){
					switch(highlightBox){
					case 0://left
						if(changeX)
							highlightBox = 3;//change to right
						//				    else if(!left)//Checking code
						//				        System.err.println("error 1");//Checking code
						break;
						
					case 1://bottom
						if(changeY)
							highlightBox = 2;//change to top
						//				    else if(!bottom)//Checking code
						//				        System.err.println("error 2");//Checking code
						break;
						
					case 2://top
						if(changeY)
							highlightBox = 1;//change to bottom
						//				    else if(!top)//Checking code
						//				        System.err.println("error 3");//Checking code
						break;
						
					case 3://right
						if(changeX)
							highlightBox = 0;//change to left
						//				    else if(!right)//Checking code
						//				        System.err.println("error 4");//Checking code
						break;
						
					case 4://bottom left
						if(changeX)
							highlightBox = 6;//change to bottom right
						else if(changeY)
							highlightBox = 5;//change to top left
						if(changeX && changeY)
							highlightBox = 7;//change to top right
						//				    if((!left) || (!bottom))//Checking code
						//				        System.err.println("error 5");//Checking code
						break;
						
					case 5://top left
						if(changeX)
							highlightBox = 7;//change to top right
						else if(changeY)
							highlightBox = 4;//change to bottom left
						if(changeX && changeY)
							highlightBox = 6;//change to bottom right
						//				    if((!left) || (!top))//Checking code
						//				        System.err.println("error 7");//Checking code
						break;
						
					case 6://bottom right
						if(changeX)
							highlightBox = 4;//change to bottom left
						else if(changeY)
							highlightBox = 7;//change to top right
						if(changeX && changeY)
							highlightBox = 5;//change to top left
						//			        if((!right) || (!bottom))//Checking code
						//				        System.err.println("error 9");//Checking code
						break;
						
					case 7://top right
						if(changeX)
							highlightBox = 5;//change to top left
						else if(changeY)
							highlightBox = 6;//change to bottom right
						if(changeX && changeY)
							highlightBox =4;//change to bottom left 
						//		            if((!right) || (!top))//Checking code
						//				        System.err.println("error 11");//Checking code
						break;
						
						//				default://Checking code
						//	                System.out.println("ERROR default");//Checking code
					}
				}
				
				//ensure crosshairs are drawn, and set current highlighted box to be drawn red
				decode_pdf.setDrawCrossHairs(true,highlightBox,Color.red);
				
				/**
				 * we have now changed the cursor coords, commonValues.m_x1 commonValues.m_y1 commonValues.m_x2 commonValues.m_y2 
				 * So now update displayed coords and cursor box on screen
				 */
				scrollAndUpdateCoords(event);
				generateNewCursorBox();
				
				//ensure we are altering the current cursor box and don't draw new one
				dragAltering=true;
				
				//store current cursor point for comparison next time
				oldX=values[0];
				oldY=values[1];
				
			}else{
				/**
				 * if there is no selected handle on drag, draw new cursorbox
				 */
				drawingCursorBox = true;
				
				//ensure highlight is not drawn
				boxContained=-1;
				
				decode_pdf.setDrawCrossHairs(true,boxContained,Color.red);
				
				//setup start point of new cursor box
				commonValues.m_x1=oldX;
				commonValues.m_y1=oldY;
				
				//setup current point for new cursor box
				commonValues.m_x2=values[0];
				commonValues.m_y2=values[1];
				
				scrollAndUpdateCoords(event);
				generateNewCursorBox();
			}
		}
		
		//variables used only in mouseMoved
		private boolean inRect=false;//whether cursor currently in cursor box
		private boolean handleChange=false;//whether the highlight should be changed
		
		public void mouseMoved(MouseEvent event) {
			
			super.mouseMoved(event);
			Rectangle currentRectangle=currentGUI.getRectangle();
			//generate handle boxes
			boxes=createNewRectangles(currentRectangle);
			
			//find which handle, if any cursor is in
			if(boxes!=null){
				int oldBox = boxContained;//save old selected value
				boxContained = -1;//reset current selected highlight
				
				for(int i=0;i<boxes.length;i++){
					if(boxes[i].contains(cx,cy)){
						boxContained = i;
						break;
					}
				}
				
				//if we find a handle and it is not already selected to highlight ensure redraw
				if(boxContained!=oldBox){
					handleChange = true;
				}
			}
			
			//if cursor in cursorbox or within handleGap pixels of it show crosshairs
			if(currentRectangle!=null){
				if((currentRectangle.x-handlesGap)<cx && (currentRectangle.x+currentRectangle.width+handlesGap)>cx &&
						(currentRectangle.y-handlesGap)<cy && (currentRectangle.y+currentRectangle.height+handlesGap)>cy){
					//cursor is in cursor box
					
					decode_pdf.setDrawCrossHairs(true,boxContained,Color.red);
					
					//if was not in rectangle repaint display
					if(!inRect || handleChange){
						decode_pdf.repaint();
						handleChange=false;
						inRect=true;
					}
				}else{
					//cursor is NOT in cursor box
					
					decode_pdf.setDrawCrossHairs(false,boxContained,Color.red);
					
					//if was in rectangle repaint display
					if(inRect || handleChange){
						decode_pdf.repaint();
						handleChange=false;
						inRect=false;
					}
				}
			}
			
		}
		
		/**
		 * creates the eight cursor box handles for the cursor box<br>
		 * returns Rectangle[] whos indexes are the same as those used to display them on screen<br>
		 */
		private Rectangle[] createNewRectangles(Rectangle currentRectangle) {
			if(currentRectangle!=null){
				
				int x1 = currentRectangle.x;
				int y1 = currentRectangle.y;
				int x2 = x1+currentRectangle.width;
				int y2 = y1+currentRectangle.height;
				
				Rectangle[] cursorBoxHandles = new Rectangle[8];
				//*draw centre of line handle boxs
				//left
				cursorBoxHandles[0] = new Rectangle(x1-handlesGap,(y1+(Math.abs(y2-y1))/2)-handlesGap,handlesGap*2,handlesGap*2);//0
				//bottom
				cursorBoxHandles[1] = new Rectangle((x1+(Math.abs(x2-x1))/2)-handlesGap,y1-handlesGap,handlesGap*2,handlesGap*2);//1
				//top
				cursorBoxHandles[2] = new Rectangle((x1+(Math.abs(x2-x1))/2)-handlesGap,y2-handlesGap,handlesGap*2,handlesGap*2);//2
				//right
				cursorBoxHandles[3] = new Rectangle(x2-handlesGap,(y1+(Math.abs(y2-y1))/2)-handlesGap,handlesGap*2,handlesGap*2);//3
				/**/
				
				//*draw corner handles
				//bottom left
				cursorBoxHandles[4] = new Rectangle(x1-handlesGap,y1-handlesGap,handlesGap*2,handlesGap*2);//4
				//top left
				cursorBoxHandles[5] = new Rectangle(x1-handlesGap,y2-handlesGap,handlesGap*2,handlesGap*2);//5
				//bottom right
				cursorBoxHandles[6] = new Rectangle(x2-handlesGap,y1-handlesGap,handlesGap*2,handlesGap*2);//6
				//top right
				cursorBoxHandles[7] = new Rectangle(x2-handlesGap,y2-handlesGap,handlesGap*2,handlesGap*2);//7
				/**/
				
				return cursorBoxHandles;
			}
			return null;
		}
	}
	
	
	
	public SwingMouseHandler(PdfDecoder decode_pdf, SwingGUI currentGUI,
			Values commonValues,Commands currentCommands) {
		
		this.decode_pdf=decode_pdf;
		this.currentGUI=currentGUI;
		this.commonValues=commonValues;
		//this.currentCommands=currentCommands;
	}
	
	public void setupExtractor() {
		decode_pdf.addMouseMotionListener(new Extractor_mouse_mover());
		decode_pdf.addMouseListener(new Extractor_mouse_clicker());
		
		
	}
	
	/**
	 * scroll to visible Rectangle and update Coords box on screen
	 */
	protected void scrollAndUpdateCoords(MouseEvent event) {
		//scroll if user hits side
		int interval=decode_pdf.getScrollInterval();
		Rectangle visible_test=new Rectangle(event.getX(),event.getY(),interval,interval);
		if((currentGUI.allowScrolling())&&(!decode_pdf.getVisibleRect().contains(visible_test)))
			decode_pdf.scrollRectToVisible(visible_test);
		
		updateCords(event);
	}
	
	/**update current page co-ordinates on screen*/
	public void updateCords(MouseEvent event){
		
		float scaling=currentGUI.getScaling();
		int inset=currentGUI.getPDFDisplayInset();
		int rotation=currentGUI.getRotation();
		
		int ex=event.getX()-inset;
		int ey=event.getY()-inset;
		
		//undo any viewport scaling
		if(commonValues.maxViewY!=0){ // will not be zero if viewport in play
			ex=(int)(((ex-(commonValues.dx*scaling))/commonValues.viewportScale));
			ey=(int)((currentGUI.mediaH-((currentGUI.mediaH-(ey/scaling)-commonValues.dy)/commonValues.viewportScale))*scaling);
		}
		
		cx=(int)((ex)/scaling);
		cy=(int)((ey/scaling));
		
		if(rotation==90){
			int tmp=(cx+currentGUI.cropY);
			cx = (cy+currentGUI.cropX);
			cy =tmp;	
		}else if((rotation==180)){
			cx =currentGUI.mediaW-(cx+currentGUI.mediaW-currentGUI.cropW-currentGUI.cropX);
			cy =(cy+currentGUI.cropY);
		}else if((rotation==270)){
			int tmp=currentGUI.mediaH-(cx+currentGUI.mediaH-currentGUI.cropH-currentGUI.cropY);
			cx =currentGUI.mediaW-(cy+currentGUI.mediaW-currentGUI.cropW-currentGUI.cropX);
			cy =tmp;
		}else{
			cx = (cx+currentGUI.cropX);
			cy =currentGUI.mediaH-(cy+currentGUI.mediaH-currentGUI.cropH-currentGUI.cropY);
		}
		
		if((commonValues.isProcessing())|(commonValues.getPdfArray()==null))
			currentGUI.setCoordText("  X: "+ " Y: " + " "+" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		else
			currentGUI.setCoordText("  X: " + cx + " Y: " + cy+ " "+" "+message); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		//scroll if user hits side and shift key not pressed
		if((currentGUI.allowScrolling())&&(!event.isShiftDown())){
			int interval=decode_pdf.getScrollInterval()*2;
			Rectangle visible_test=new Rectangle(event.getX()-interval,event.getY()-interval,interval*2,interval*2);
			if(!decode_pdf.getVisibleRect().contains(visible_test))
				decode_pdf.scrollRectToVisible(visible_test);
		}
	}
	
	
	public void updateRectangle() {
		
		Rectangle currentRectangle=currentGUI.getRectangle();
		
		if(currentRectangle!=null){
			Rectangle newRect = decode_pdf.getCombinedAreas(currentRectangle,false);
			if(newRect!=null){
				commonValues.m_x1=newRect.x;
				commonValues.m_y2=newRect.y;
				commonValues.m_x2=newRect.x+newRect.width;
				commonValues.m_y1=newRect.y+newRect.height;
				
				currentRectangle=newRect;
				decode_pdf.updateCursorBoxOnScreen(currentRectangle,Color.blue);
				decode_pdf.repaint();
			}
		}
		
	}
	
	public void setupMouse() {
		/**
		 * track and display screen co-ordinates and support links
		 */
		decode_pdf.addMouseMotionListener(new mouse_mover());
		decode_pdf.addMouseListener(new mouse_clicker());
	}
	
	/**
	 * get raw co-ords and convert to correct scaled units
	 * @return int[] of size 2, [0]=new x value, [1] = new y value
	 */
	protected int[] updateXY(MouseEvent event) {
		
		float scaling=currentGUI.getScaling();
		int inset=currentGUI.getPDFDisplayInset();
		int rotation=currentGUI.getRotation();
		
		//get co-ordinates of top point of outine rectangle
		x=(int)((event.getX()-inset)/scaling);
		y=(int)((event.getY()-inset)/scaling);
		
		//undo any viewport scaling
		if(commonValues.maxViewY!=0){ // will not be zero if viewport in play
			x=(int)(((x-(commonValues.dx*scaling))/commonValues.viewportScale));
			y=(int)((currentGUI.mediaH-((currentGUI.mediaH-(y/scaling)-commonValues.dy)/commonValues.viewportScale))*scaling);
		}
		
		int[] ret=new int[2];
		if(rotation==90){	        
			ret[1] = x+currentGUI.cropY;
			ret[0] =y+currentGUI.cropX;
		}else if((rotation==180)){
			ret[0]=currentGUI.mediaW- (x+currentGUI.mediaW-currentGUI.cropW-currentGUI.cropX);
			ret[1] =y+currentGUI.cropY;
		}else if((rotation==270)){
			ret[1] =currentGUI.mediaH- (x+currentGUI.mediaH-currentGUI.cropH-currentGUI.cropY);
			ret[0]=currentGUI.mediaW-(y+currentGUI.mediaW-currentGUI.cropW-currentGUI.cropX);
		}else{
			ret[0] = x+currentGUI.cropX;
			ret[1] =currentGUI.mediaH-(y+currentGUI.mediaH-currentGUI.cropH-currentGUI.cropY);    
		}
		return ret;
	}

}
