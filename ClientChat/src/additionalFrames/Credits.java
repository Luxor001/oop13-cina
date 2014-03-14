package additionalFrames;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * Shows the JFrame associated with the credits of the application.
 * it uses a JTextPane as main component for displaying the credits. * 
 * @author Stefano Belli
 * */
public class Credits {

	private Dimension FRAME_SIZE = new Dimension(400, 310);
	private String FRAME_TITLE = "About CriptoChat";
	/*private String ABOUT_TXT = "CryptoChat allows you to have private, encrypted SSL chat with other users."
			+ "\nAll the private informations (such as IP and private informations) are NOT \nstored in our main server,\n allowing you "
			+ "to have also an anonimous public chat.\n\nRemember: you and you are the real responsable of your privacy.\nDon't share "
			+ "your personal informations with anyone in public chat!";*/
	private String CREDITS_TXT = "CryptoChat(c) Version 1.0 Precise Fat Cat\n\n"
			+ "Copyright(c) Stefano Belli & Francesco Cozzolino,\nAll right reserved.";
	
	private String SPECIAL_THANKS_TXT="Special Thanks to: \nA43 on freesound.org for Toc 02.wav\n"
			+ "The BalloonTip Team for Java\n"
			+ "The JUnite Team for Java";

	public Credits() throws BadLocationException {

		JFrame frame = new JFrame();
		frame.setSize(FRAME_SIZE);
		frame.setTitle(FRAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		JPanel pnl_main = new JPanel();
		pnl_main.setSize(FRAME_SIZE);
		frame.add(pnl_main);

		BorderLayout brd_layout = new BorderLayout();

		pnl_main.setLayout(brd_layout);

		String[] initString = { " ", // icon
				"\n\n" + CREDITS_TXT,"\n\n\n"+ SPECIAL_THANKS_TXT}; // regular
		String[] initStyles = { "icon", "regular","italic" };

		JTextPane textPane = new JTextPane();
		StyledDocument doc = textPane.getStyledDocument();
		addStylesToDocument(doc);

		for (int i = 0; i < initString.length; i++) {
			doc.insertString(doc.getLength(), initString[i],
					doc.getStyle(initStyles[i]));
		}
		
		textPane.setEditable(false);
		textPane.setSize(FRAME_SIZE);
		textPane.setMinimumSize(FRAME_SIZE);
		textPane.setPreferredSize(FRAME_SIZE);
		textPane.setMaximumSize(FRAME_SIZE);
		pnl_main.add(textPane, BorderLayout.NORTH);

		frame.setResizable(false);
		frame.setVisible(true);

	}

	/**
	 * Add some "static" styles to the StyledDocument of the JTextPane.
	 * This method is, for the large part, "inspired" directly from the 
	 * docs.oracle.com documentation.
	 * */
	protected void addStylesToDocument(StyledDocument doc) {
		// Initialize some styles.
		Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);

		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");
		StyleConstants.setAlignment(regular, StyleConstants.ALIGN_CENTER);

		doc.setParagraphAttributes(0, doc.getLength(), regular, false);


		
		
		Style s = doc.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);
		StyleConstants.setAlignment(s, StyleConstants.ALIGN_LEFT);
		StyleConstants.setAlignment(s, 0);
		
		s = doc.addStyle("bold", regular);
		StyleConstants.setBold(s, true);

		s = doc.addStyle("small", regular);
		StyleConstants.setFontSize(s, 10);

		s = doc.addStyle("large", regular);
		StyleConstants.setFontSize(s, 16);

		s = doc.addStyle("icon", regular);
		ImageIcon pigIcon = createImageIcon("resources/logoCredits.png", "");
		if (pigIcon != null) {
			StyleConstants.setIcon(s, pigIcon);
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path, String description) {
		return new ImageIcon(path, description);
	}

}
