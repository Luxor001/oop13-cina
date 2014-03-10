package client_chat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import net.java.balloontip.BalloonTip;

/**
 * This class shows the entry screen of the application.
 * Here the user can select his entry nickname and if it's visible or not.
 * However it has not been implemented yet, here the user could select the 
 * main channel to connect.
 * This class references, for some uses, on the WebsocketHandler class and User.
 * @see User
 * @see WebsocketHandler
 * @author Stefano Belli
 * **/
public class SplashScreen {

	private JFrame frame;
	private static String TITLE = "CryptoChat V. 1.0 PreciseFatCat";
	private static String INVALID_NICKNAME_MESSAGE = "Invalid Nickname.";
	private static String NICKNAME_ALREADY_USED_MESSAGE = "Nickname Already in use";

	private static Point frameSize = new Point(300, 400);
	private static JComboBox<String> cmb_channel;
	private static DefaultComboBoxModel<String> channelist;
	private static JPanel pnl_main;
	private static JLabel lbl_channel;

	private static JLabel lbl_nickname;
	private static JTextArea txt_nickname;
	private static JButton btn_login;
	private static JCheckBox chb_visible;
	private JLabel loadingCircle = new JLabel();
	private String icon_path = "resources/Icon.png";

	private static int offsetcenter = 30;
	private static int centerscaling;

	public SplashScreen() throws IOException {

		centerscaling = 120;

		frame = new JFrame();
		frame.setTitle(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(frameSize.x, frameSize.y);
		frame.setLocationRelativeTo(null); /* centers jframe on screen. */
		InputStream imgStream = new FileInputStream(new File(icon_path));
		BufferedImage myImg = ImageIO.read(imgStream);
		frame.setIconImage(myImg);

		pnl_main = new JPanel();
		pnl_main.setLayout(null); /* sorry but i'm in a short */
		frame.getContentPane().add(pnl_main);

		JLabel lbl_image = new JLabel();
		lbl_image.setIcon(new ImageIcon("resources/logo.png"));
		lbl_image.setSize(lbl_image.getPreferredSize());
		lbl_image.setLocation(CenteredX(lbl_image) + 5, 0);
		pnl_main.add(lbl_image);

		centerscaling += 20;
		channelist = new DefaultComboBoxModel<String>();
		channelist.addElement("longname");
		channelist.addElement("main");
		cmb_channel = new JComboBox<String>(channelist);

		lbl_channel = new JLabel("Channel:");
		lbl_channel.setSize(lbl_channel.getPreferredSize());
		lbl_channel.setLocation(CenteredX(lbl_channel), centerscaling);
		centerscaling += 20;

		boolean choice = User.getStoredVisibility();
		chb_visible = new JCheckBox("Visible?", choice);
		chb_visible.setSize(chb_visible.getPreferredSize());
		chb_visible.setLocation(CenteredX(chb_visible) + 90, centerscaling);
		pnl_main.add(chb_visible);
		cmb_channel.setSize(cmb_channel.getPreferredSize());
		cmb_channel.setLocation(CenteredX(cmb_channel), centerscaling);
		centerscaling += 20;
		cmb_channel.removeItem("longname");
		pnl_main.add(lbl_channel);
		pnl_main.add(cmb_channel);

		centerscaling += 20;
		lbl_nickname = new JLabel("Nickname");
		lbl_nickname.setSize(lbl_nickname.getPreferredSize());
		lbl_nickname.setLocation(CenteredX(lbl_nickname), centerscaling);
		centerscaling += 20;
		txt_nickname = new JTextArea();
		txt_nickname.setSize(100, 15);
		txt_nickname.setSize(txt_nickname.getSize().width,
				txt_nickname.getSize().height + 5);
		txt_nickname.setLocation(CenteredX(txt_nickname), centerscaling);

		String dfnickname = User.getStoredNickname();
		txt_nickname.setText(dfnickname);
		
		/*check if user has entered an invalid char..*/
		txt_nickname.addKeyListener(new KeyListener() {			
			@Override
			public void keyTyped(KeyEvent e) {
			  String  invalidChars = "\\/:*?\"<>|";
				char keypressed = e.getKeyChar();
			  
				for(char c:invalidChars.toCharArray()){
					if(c == keypressed){
						e.consume();
					}
				}			  
			}
			/*useless methods i must declare ç_ç*/
			@Override
			public void keyReleased(KeyEvent e) {
			}			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		centerscaling += 20;
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		txt_nickname.setBorder(border);
		pnl_main.add(lbl_nickname);
		pnl_main.add(txt_nickname);

		centerscaling += 20;
		centerscaling += 20;
		btn_login = new JButton("Connect");
		btn_login.setSize(btn_login.getPreferredSize());
		btn_login.setSize(btn_login.getSize().width + 30,
				btn_login.getSize().height);
		btn_login.setLocation(CenteredX(btn_login), centerscaling);

		pnl_main.add(btn_login);

		loadingCircle.setIcon(new ImageIcon("resources/loadingif.gif"));
		loadingCircle.setSize(loadingCircle.getPreferredSize());
		loadingCircle.setLocation(5, 5);
		pnl_main.add(loadingCircle);
		loadingCircle.setVisible(false);
		
	
		btn_login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {				
				
				if (txt_nickname.getText().equals("")
						|| txt_nickname.getText().length() > 12) {
					BalloonTip bln_invalidnick = new BalloonTip(txt_nickname,
							INVALID_NICKNAME_MESSAGE);
					bln_invalidnick.setVisible(true);
				} else {
					try {
						User.setNickName(txt_nickname.getText());
						// WebsocketHandler.NICKNAME = txt_nickname.getText();
						Application.chat_initialization();
					} catch (IOException e1) {
					}

				}
			}
		});

		chb_visible.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				WebsocketHandler.VISIBLE_FLAG = chb_visible.isSelected();
			}
		});


		frame.setResizable(false);
		frame.setVisible(true);
	}

	/**
	 * Finds the centered X of the component, calculating it's size
	 * and the frame size.
	 * */
	public int CenteredX(Component a) {
		return frameSize.x / 2 - (a.getSize().width / 2);
	}

	public void disposeFrame() {
		frame.dispose();
	}

	public int buildChoiceMessageBox(String Message, String title,
			Object[] options, int IconType) {
		int n = JOptionPane.showOptionDialog(frame, Message, title,
				JOptionPane.YES_NO_OPTION, IconType, null, options, null);

		return n;
	}

	public void setVisibilityLoadingCircle(boolean visibile) {
		loadingCircle.setVisible(visibile);
	}

	/**shows an error tooltip for an invalid nickname*/
	public void nicknameInvalid() {
		BalloonTip bln_invalidnick = new BalloonTip(txt_nickname,
				NICKNAME_ALREADY_USED_MESSAGE);
		bln_invalidnick.setVisible(true);
	}

	public void setFrameEnabled(boolean enabled) {
		btn_login.setEnabled(enabled);
		frame.setEnabled(enabled);
	}

}
