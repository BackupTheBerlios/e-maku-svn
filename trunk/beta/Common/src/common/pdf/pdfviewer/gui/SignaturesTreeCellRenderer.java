package common.pdf.pdfviewer.gui;
import java.awt.*;
import java.net.*;

import javax.swing.*;
import javax.swing.tree.*;

public class SignaturesTreeCellRenderer extends DefaultTreeCellRenderer {
    private Icon icon;

    public Icon getLeafIcon() {
        return icon;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {

        DefaultMutableTreeNode node = ((DefaultMutableTreeNode) value);
		value = node.getUserObject();
		int level = node.getLevel();
		
        String s = value.toString();
        icon = null;
        Font treeFont = tree.getFont();

        if(level== 2){
        	DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        	String text=parent.getUserObject().toString();
        	if(text.equals("The following signature fields are not signed")){
        		URL resource = getClass().getResource("/org/jpedal/examples/simpleviewer/res/unlock.png");
        		icon = new ImageIcon(resource);
        	} else {
        		URL resource = getClass().getResource("/org/jpedal/examples/simpleviewer/res/lock.gif");
        		icon = new ImageIcon(resource);
        		treeFont = new Font(treeFont.getFamily(), Font.BOLD, treeFont.getSize());
        	}
        }
        
        setFont(treeFont);
        setText(s);
        setIcon(icon);
        if (isSelected) {
            setBackground(new Color(236, 233, 216));
            setForeground(Color.BLACK);
        } else {
            setBackground(tree.getBackground());
            setForeground(tree.getForeground());
        }
        setEnabled(tree.isEnabled());
        
        setOpaque(true);

        return this;
    }
}