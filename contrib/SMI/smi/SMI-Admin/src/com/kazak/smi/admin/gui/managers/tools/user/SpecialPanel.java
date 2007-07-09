package com.kazak.smi.admin.gui.managers.tools.user;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SpecialPanel extends JDialog implements ActionListener  {

	private static final long serialVersionUID = 1L;
	private JButton myCloseButton = null;
	private JButton myDetailsButton = null;
	private JButton myHelpButton = null;
	private JPanel myDetailsPanel = null;
	JTextArea myMessageArea = null;
	JTextArea myDetailsArea = null;
	private boolean myHaveDetailsBeenVisible = false;
	private boolean myAreDetailsVisible = false;
	private static final String EXPAND_DETAILS = "Details>>";
	private static final String COLLAPSE_DETAILS = "<<Details";

	public static void main (String[] args)  {
		SpecialPanel testDialog = new SpecialPanel (null, "Collapse / Expand Test Dialoga", 
				"Test message.", "This is the expandable / collapsable details string.");
		testDialog.setVisible (true);

		System.exit (0);
	}

	public SpecialPanel (Frame owner, String title, String message, String details)  {
		super (owner, title, true);

		//  Create the controls
		createControls (message, details);

		positionErrorDialog (owner);    	
	}


	private void positionErrorDialog (Frame owner)  {
		if (owner == null  ||  !owner.isVisible ())  {
			Dimension screenDimension = Toolkit.getDefaultToolkit ().getScreenSize ();
			setLocation (screenDimension.width / 2 - getSize().width / 2,
					screenDimension.height / 2 - getSize().height / 2);
		}
	}


	public void actionPerformed (ActionEvent e)  {
		Object source = e.getSource ();
		if (source == myCloseButton)  {
			//  Teardown
			dispose ();
			setVisible (false);
		}  else if (source == myDetailsButton)  {
			//  Expand or colapse the details portion of the control
			if (myAreDetailsVisible)  {
				//  Collapse the details pane
				collapseDetails ();

				//  Change the details button to show expand text
				myDetailsButton.setText (EXPAND_DETAILS);
				myAreDetailsVisible = false;
			}  else  {
				//  Expand the details pane
				expandDetails ();

				//  Change the details button to show collapse text
				myDetailsButton.setText (COLLAPSE_DETAILS);
				myAreDetailsVisible = true;
			}
		}
	}


	public void collapseDetails ()  {

		//  Remove the details panel from the dialog
		getContentPane().remove (myDetailsPanel);

		//  Shrink the dialog the height of the removed panel
		Dimension detailsPanelSize = myDetailsPanel.getSize ();
		Dimension dialogSize = getSize ();
		dialogSize.height -= detailsPanelSize.height;
		setSize (dialogSize);

		//  Cause the new layout to take effect
		invalidate ();
		validate ();
	}


	public void expandDetails ()  {

		//  Add the details panel to the dialog
		getContentPane().add (myDetailsPanel, BorderLayout.CENTER);

		//  Resize the dialog to accomidate the added size
		Dimension dialogSize = getSize ();
		Dimension detailSize = null;
		if (myHaveDetailsBeenVisible)  {
			//  The details area already has a size from the last time it was displayed
			detailSize = myDetailsPanel.getSize ();
		}  else  {
			//  The details area has yet to be made visible
			detailSize = myDetailsPanel.getPreferredSize ();
		}

		//  Increase the height of the dialog to fit the added details area
		dialogSize.height += detailSize.height;
		setSize (dialogSize);

		//  Cause the new layout to take effect
		invalidate ();
		validate ();

		if (!myHaveDetailsBeenVisible)
			myHaveDetailsBeenVisible = true;
	}


	private void createControls (String fullMessage, String details)  {
		getContentPane().setLayout(new BorderLayout());

		//  Create the dialog buttons
		//  Create a box to hold the buttons - to give the right spacing between them
		Box buttonBox = Box.createHorizontalBox ();

		//  Create a panel to hold a box with the buttons in it - to give it the right space around them
		JPanel buttonPanel = new JPanel ();
		buttonPanel.add (buttonBox);
		buttonPanel.setBorder (BorderFactory.createEmptyBorder (10, 10, 10, 10));

		//  Create the buttons and add them to the box (leading strut will give the dialog box its width)
		buttonBox.add (myCloseButton = createButton ("Close", 'c'));
		buttonBox.add (Box.createHorizontalGlue ());
		buttonBox.add (Box.createHorizontalStrut (4));
		buttonBox.add (myDetailsButton = createButton (EXPAND_DETAILS, 'd'));
		buttonBox.add (Box.createHorizontalStrut (4));
		buttonBox.add (myHelpButton = createButton ("Help"));
		buttonBox.add (Box.createHorizontalStrut (10));

		//  Add the button panel to the bottom of the BorderLayout
		getContentPane().add (buttonPanel, BorderLayout.SOUTH);

		//  Add the message text
		//  Create a JPanel to hold the message with control space around it
		JPanel messagePanel = new JPanel ();
		messagePanel.setLayout(new BorderLayout ());
		messagePanel.setBorder (BorderFactory.createEmptyBorder (10, 10, 0, 10));

		//  Create the message area (no editing with word wrap)
		myMessageArea = new JTextArea (fullMessage, 0, 30);
		myMessageArea.setEditable (false);
		myMessageArea.setLineWrap (true);
		myMessageArea.setWrapStyleWord (true);
		//  Make the text area look like a label (we want a copyable label)
		myMessageArea.setBackground ((Color)UIManager.get ("Label.background"));
		myMessageArea.setForeground ((Color)UIManager.get ("Label.foreground"));
		myMessageArea.setFont ((Font)UIManager.get ("Label.font"));

		//  Retrieve the error icon
		Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
		if (errorIcon != null)  {
			//  Put the error icon into the message panel next to the message
			JLabel iconLabel = new JLabel (errorIcon);
			JPanel iconPanel = new JPanel ();
			iconPanel.setLayout(new BorderLayout ());
			iconPanel.setBorder (BorderFactory.createEmptyBorder (0, 0, 0, 10));
			iconPanel.add (iconLabel, BorderLayout.NORTH);
			messagePanel.add (iconPanel, BorderLayout.WEST);
		}

		//  Put the message into the diaog
		messagePanel.add (myMessageArea, BorderLayout.CENTER);
		getContentPane().add (messagePanel, BorderLayout.NORTH);

		//  Create a text area for the details but do not add - yet (see actionPerform method for 
		//  myDetailsButton handler)
		//  Create a JPanel to hold the details with space around it
		myDetailsPanel = new JPanel ();
		myDetailsPanel.setLayout(new BorderLayout());
		myDetailsPanel.setBorder (
				BorderFactory.createEmptyBorder (10, 10, 0, 10));

		//  Create the details area (no editing with word wrap)
		myDetailsArea = new JTextArea (details, 8, 0);
		myDetailsArea.setEditable (false);
		myDetailsArea.setLineWrap (true);
		myDetailsArea.setWrapStyleWord (true);
		myDetailsArea.setBorder (BorderFactory.createEmptyBorder (0, 2, 0, 2));

		//  Create a scroll pane to hold the details area
		JScrollPane detailsScrollPane = new JScrollPane (myDetailsArea);
		detailsScrollPane.setBorder (BorderFactory.createLoweredBevelBorder());

		//  Put the details panel together (but do not add it to the dilaog just yet)
		myDetailsPanel.add (detailsScrollPane, BorderLayout.CENTER);

		//  Layout the controls
		pack ();
		setVisible (false);

		//  Work around for incorrectly sized message text area
		Dimension messageSize = myMessageArea.getPreferredSize ();
		myMessageArea.setMinimumSize (messageSize);
		pack ();
	}


	private JButton createButton (String label)  {
		return createButton (label, '\0');
	}


	private JButton createButton (String label, char mnemonic)  {
		//  Create the new button object
		JButton newButton = new JButton (label);

		newButton.setPreferredSize (new Dimension (90, 30));
		newButton.setMargin (new Insets (2, 2, 2, 2));

		if (mnemonic != '\0')  {
			//  Specify the button's mnemonic
			newButton.setMnemonic (mnemonic);
		}

		//  Setup the dialog to listen to events
		newButton.addActionListener (this);

		return newButton;
	}

}