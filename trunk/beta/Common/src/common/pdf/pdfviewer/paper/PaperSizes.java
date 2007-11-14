package common.pdf.pdfviewer.paper;

import java.awt.print.*;
import java.util.*;

/**
 * Defines paper sizes
 */
public class PaperSizes {

    Map paperDefinitions=new HashMap();

    /**default for paper selection*/
    private int defaultPageIndex=0;

    public PaperSizes(){
        setPaperSizes();
    }

    public String[] getAvailablePaperSizes(){
        return (String[]) paperDefinitions.keySet().toArray(new String[paperDefinitions.keySet().size()]);
    }

    /**return selected Paper*/
    public Paper getSelectedPaper(Object id) {
        return (Paper) paperDefinitions.get(id);
    }

    /**
     * method to setup specific Paper sizes
     * - add your own here to extend list
     */
    private void setPaperSizes(){

    	String printDescription;
    	Paper paper;
    	//defintion for each Paper - must match

    	//set default value
    	defaultPageIndex=1;

    	/**
    	//A4 (border)
		printDescription= Messages.getMessage("PdfViewera4");
		paper = new Paper();
		paper.setSize(595, 842);
		paper.setImageableArea(43, 43, 509, 756);
		paperDefinitions.put(printDescription,paper);
	*/
		//A4 (borderless)
		printDescription="A4 (borderless)";
        paper = new Paper();
		paper.setSize(595, 842);
		paper.setImageableArea(0, 0, 595, 842);
		paperDefinitions.put(printDescription,paper);

		//A5
		printDescription="A5";
		paper = new Paper();
		paper.setSize(297, 421);
		paper.setImageableArea(23,23,254,378);
		paperDefinitions.put(printDescription,paper);
		
		//Added for Adobe
		printDescription="US Letter (8.5 x 11)";
		paper = new Paper();
		paper.setSize(612, 792);
		paper.setImageableArea(0,0,612,792);
		paperDefinitions.put(printDescription,paper);
	
		//custom
		printDescription="Custom 2.9cm x 8.9cm";
		int customW=(int) (29*2.83);
		int customH=(int) (89*2.83); //2.83 is scaling factor to convert mm to pixels
		paper = new Paper();
		paper.setSize(customW, customH);
		paper.setImageableArea(0,0,customW,customH); //MUST BE SET ALSO
		paperDefinitions.put(printDescription,paper);
		
		//Add your own here

    }

    public int getDefaultPageIndex() {
    	return defaultPageIndex;
        
    }
}
