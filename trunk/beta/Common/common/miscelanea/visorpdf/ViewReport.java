package common.miscelanea.visorpdf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import common.miscelanea.idiom.Language;

import org.jpedal.PdfDecoder;


public class ViewReport extends JInternalFrame {

	private static final long serialVersionUID = 4884184608765403733L;

	private PdfDecoder pdfDecoder;
	private String currentFile = null;
	private int currentPage = 1;
	private final JLabel pageCounter1 = new JLabel(Language.getWord("PAGE")+" ");
	private JTextField pageCounter2 = new JTextField(4);
	private JLabel pageCounter3 = new JLabel(Language.getWord("OF"));// 000 used to set prefered
	private Dimension size;

	public ViewReport(byte[] bytes, Dimension size) {

		this.size = size;
		pdfDecoder = new PdfDecoder();
		try {
			// this opens the PDF and reads its internal details
			pdfDecoder.openPdfArray(bytes);

			// these 2 lines opens page 1 at 100% scaling
			pdfDecoder.decodePage(currentPage);
			pdfDecoder.setPageParameters(1, 1); // values scaling (1=100%). page
												// number
		} catch (Exception e) {
			e.printStackTrace();
		}

		initializeViewer();
		pageCounter2.setText(currentPage + "");
		pageCounter3.setText(Language.getWord("OF") +" " + pdfDecoder.getPageCount());
	}

	private void initializeViewer() {
		
		this.setMaximizable(true);
		this.setIconifiable(true);
		this.setResizable(true);
		this.setClosable(true);
		this.setSize(800,600);
		this.setLocation((size.width / 2) - 400, (size.height / 2) - 300);
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

		Container cPane = getContentPane();
		cPane.setLayout(new BorderLayout());
		Component[] itemsToAdd = initChangerPanel();
		JPanel topBar = new JPanel();
		topBar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		for (int i = 0; i < itemsToAdd.length; i++) {
			topBar.add(itemsToAdd[i]);
		}

		cPane.add(topBar, BorderLayout.SOUTH);
		JScrollPane display = initPDFDisplay();
		cPane.add(display, BorderLayout.CENTER);
	}

	private JScrollPane initPDFDisplay() {
		JScrollPane currentScroll = new JScrollPane();
		currentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		currentScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		currentScroll.setViewportView(pdfDecoder);
		return currentScroll;
	}
	
	private Component[] initChangerPanel() {

		Component[] list = new Component[11];

		JButton start = new JButton();
		start.setBorderPainted(false);
		URL startImage = getClass().getResource("/org/jpedal/examples/simpleviewer/res/start.gif");
		start.setIcon(new ImageIcon(startImage));
		start.setToolTipText(Language.getWord("REWIND_FIRST"));
		list[0] = start;
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentFile != null && currentPage != 1) {
					currentPage = 1;
					try {
						pdfDecoder.decodePage(currentPage);
						pdfDecoder.invalidate();
						repaint();
					} catch (Exception e1) {
						System.err.println(Language.getWord("REWIND_FIRST"));
						e1.printStackTrace();
					}
					pageCounter2.setText(currentPage + "");
				}
			}
		});

		JButton fback = new JButton();
		fback.setBorderPainted(false);
		URL fbackImage = getClass().getResource("/org/jpedal/examples/simpleviewer/res/fback.gif");
		fback.setIcon(new ImageIcon(fbackImage));
		fback.setToolTipText(Language.getWord("REWIND_10"));
		list[1] = fback;
		fback.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentFile != null && currentPage > 10) {
					currentPage -= 10;
					try {
						pdfDecoder.decodePage(currentPage);
						pdfDecoder.invalidate();
						repaint();
					} catch (Exception e1) {
						System.err.println(Language.getWord("REWIND_10"));
						e1.printStackTrace();
					}
					pageCounter2.setText(currentPage + "");
				}
			}
		});

		JButton back = new JButton();
		back.setBorderPainted(false);
		URL backImage = getClass().getResource("/org/jpedal/examples/simpleviewer/res/back.gif");
		back.setIcon(new ImageIcon(backImage));
		back.setToolTipText(Language.getWord("REWIND_1"));

		list[2] = back;
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentFile != null && currentPage > 1) {
					currentPage -= 1;
					try {
						pdfDecoder.decodePage(currentPage);
						pdfDecoder.invalidate();
						repaint();
					} catch (Exception e1) {
						System.err.println(Language.getWord("REWIND_1"));
						e1.printStackTrace();
					}
					pageCounter2.setText(currentPage + "");
				}
			}
		});

		pageCounter2.setEditable(true);
		pageCounter2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent a) {

				String value = pageCounter2.getText().trim();
				int newPage;
				int oldPage = currentPage;
				try {
					newPage = Integer.parseInt(value);
					if ((newPage > pdfDecoder.getPageCount()) | (newPage < 1)) {
						return;
					}
					currentPage = newPage;
					
					try {
						pdfDecoder.decodePage(currentPage);
						pdfDecoder.invalidate();
						repaint();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					pageCounter2.setText(String.valueOf(oldPage));
					return;
				}
			}
		});

		/** put page count in middle of forward and back */
		list[3] = pageCounter1;
		list[4] = new JPanel();
		list[5] = pageCounter2;
		list[6] = new JPanel();
		list[7] = pageCounter3;

		/** forward icon */
		JButton forward = new JButton();
		forward.setBorderPainted(false);
		URL fowardImage = getClass().getResource("/org/jpedal/examples/simpleviewer/res/forward.gif");
		forward.setIcon(new ImageIcon(fowardImage));
		forward.setToolTipText(Language.getWord("FORWARD_1"));
		list[8] = forward;
		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentFile != null && 
					currentPage < pdfDecoder.getPageCount()) {
					currentPage += 1;
					try {
						pdfDecoder.decodePage(currentPage);
						pdfDecoder.invalidate();
						repaint();
					} catch (Exception e1) {
						System.err.println(Language.getWord("FORWARD_1"));
						e1.printStackTrace();
					}
					pageCounter2.setText(currentPage + "");
				}
			}
		});

		/** fast forward icon */
		JButton fforward = new JButton();
		fforward.setBorderPainted(false);
		URL ffowardImage = getClass().getResource("/org/jpedal/examples/simpleviewer/res/fforward.gif");
		fforward.setIcon(new ImageIcon(ffowardImage));
		fforward.setToolTipText(Language.getWord("FORWARD_10"));
		list[9] = fforward;
		fforward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentFile != null && 
					currentPage < pdfDecoder.getPageCount() - 9) {
					currentPage += 10;
					try {
						pdfDecoder.decodePage(currentPage);
						pdfDecoder.invalidate();
						repaint();
					} catch (Exception e1) {
						System.err.println(Language.getWord("FORWARD_10"));
						e1.printStackTrace();
					}
					pageCounter2.setText(currentPage + "");
				}
			}
		});

		/** goto last page */
		JButton end = new JButton();
		end.setBorderPainted(false);
		URL endImage = getClass().getResource("/org/jpedal/examples/simpleviewer/res/end.gif");
		end.setIcon(new ImageIcon(endImage));
		end.setToolTipText(Language.getWord("FORWARD_LAST"));
		list[10] = end;
		end.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentFile != null && 
					currentPage < pdfDecoder.getPageCount()) {
					currentPage = pdfDecoder.getPageCount();
					try {
						pdfDecoder.decodePage(currentPage);
						pdfDecoder.invalidate();
						repaint();
					} catch (Exception e1) {
						System.err.println(Language.getWord("FORWARD_LAST"));
						e1.printStackTrace();
					}
					pageCounter2.setText(currentPage + "");
				}
			}
		});
		return list;
	}
}