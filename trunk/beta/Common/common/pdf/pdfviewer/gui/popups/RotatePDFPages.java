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
* saveBitmap.java
* ---------------
* (C) Copyright 2005, by IDRsolutions and Contributors.
*
* 
* --------------------------
*/
package common.pdf.pdfviewer.gui.popups;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.print.attribute.standard.PageRanges;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.jpedal.utils.LogWriter;

public class RotatePDFPages extends Save
{
	private static final long serialVersionUID = 7423353291901251301L;
	
	JLabel OutputLabel = new JLabel();
	ButtonGroup buttonGroup1 = new ButtonGroup();
	ButtonGroup buttonGroup2 = new ButtonGroup();
	
	JToggleButton jToggleButton3 = new JToggleButton();
	
	JToggleButton jToggleButton2 = new JToggleButton();
	
	JRadioButton printAll=new JRadioButton();
	JRadioButton printCurrent=new JRadioButton();
	JRadioButton printPages=new JRadioButton();
	
	JTextField pagesBox=new JTextField();

	final String[] rotationItems = {"Clockwise 90 degrees","Counter-clockwise 90 degrees"
			,"180 degrees"};
	
	JLabel direction = new JLabel("Direction");
	JComboBox directionBox = new JComboBox(rotationItems);
	public RotatePDFPages( String root_dir, int end_page, int currentPage ) 
	{
		super(root_dir, end_page, currentPage);

		try
		{
			jbInit();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * get root dir
	 */
	final public int[] getRotatedPages()
	{
		
		int[] pagesToExport=null;
		
		if(printAll.isSelected()){
			pagesToExport=new int[end_page];
			for(int i=0;i<end_page;i++)
				pagesToExport[i]=i+1;

		}else if( printCurrent.isSelected() ){
			pagesToExport=new int[1];
			pagesToExport[0]=currentPage;
			
		}else if( printPages.isSelected() ){
			
			//<start-13>
			try{
				PageRanges pages=new PageRanges(pagesBox.getText());
				
				int count=0;
				int i = -1;
				while ((i = pages.next(i)) != -1) 
					count++;
				
				pagesToExport=new int[count];
				count=0;
				i = -1;
				while ((i = pages.next(i)) != -1){
					if(i > end_page){
						JOptionPane.showMessageDialog(this,"Page "+i+" is out of bounds, " +
								"pagecount = "+end_page);
						return null;
					}
					pagesToExport[count]=i;
					count++;
				}
			}catch (IllegalArgumentException  e) {
				LogWriter.writeLog( "Exception " + e + " in exporting pdfs" );
				JOptionPane.showMessageDialog(this,"Invalid syntax");
			}
			//<end-13>
		}
		
		return pagesToExport;

	}
	
	public int getDirection(){
		return directionBox.getSelectedIndex();
	}
	
	private void jbInit() throws Exception
	{
		
		direction.setFont( new java.awt.Font( "Dialog", 1, 14 ) );
		direction.setDisplayedMnemonic( '0' );
		direction.setBounds( new Rectangle( 13, 13, 220, 26 ) );
		
		directionBox.setBounds( new Rectangle( 23, 40, 232, 23 ) );
		
		pageRangeLabel.setText( "Page range" );
		pageRangeLabel.setBounds( new Rectangle( 13, 71, 199, 26 ) );
		
		printAll.setText("All");
		printAll.setBounds( new Rectangle( 23, 100, 75, 22 ) );
		
		printCurrent.setText("Current Page");
		printCurrent.setBounds( new Rectangle( 23, 120, 100, 22 ) );
		printCurrent.setSelected(true);
		
		printPages.setText("Pages:");
		printPages.setBounds( new Rectangle( 23, 142, 70, 22 ) );
		
		pagesBox.setBounds( new Rectangle( 95, 142, 200, 22 ) );
		pagesBox.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {}

			public void keyReleased(KeyEvent arg0) {
				if(pagesBox.getText().equals(""))
					printCurrent.setSelected(true);
				else
					printPages.setSelected(true);
				
			}

			public void keyTyped(KeyEvent arg0) {}
		});

		JTextArea pagesInfo=new JTextArea("Enter page number and/or page ranges\n" +
				"seperated by commas.  For example, 1,3,5-12");
		pagesInfo.setBounds(new Rectangle(23,165,270,40));
		pagesInfo.setOpaque(false);
				
		optionsForFilesLabel.setBounds( new Rectangle( 13, 220, 199, 26 ) );
		
		this.add( printAll, null );
		this.add( printCurrent, null );
		
		//<start-13>
		this.add( printPages, null );
		this.add( pagesBox, null );
		this.add( pagesInfo, null );
		//<end-13>
		
		this.add( directionBox, null );
		this.add( direction, null );
		this.add( changeButton, null );
		this.add( pageRangeLabel, null );
		
		this.add( jToggleButton2, null );
		this.add( jToggleButton3, null );
		
		buttonGroup1.add( printAll );
		buttonGroup1.add( printCurrent );
		buttonGroup1.add( printPages );
	}
	
	final public Dimension getPreferredSize()
	{
		return new Dimension( 350, 230 );
	}
	
}
