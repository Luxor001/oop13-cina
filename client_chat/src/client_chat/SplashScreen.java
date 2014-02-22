package client_chat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import net.java.balloontip.BalloonTip;

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
	private JLabel loadingCircle = new JLabel();

	private static int offsetcenter = 30;
	private static int centerscaling = 120;

	public SplashScreen() {

		frame = new JFrame();
		frame.setTitle(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(frameSize.x, frameSize.y);
		frame.setLocationRelativeTo(null); /* centers jframe on screen. */

		pnl_main = new JPanel();
		pnl_main.setLayout(null); /* sorry but i'm in a short */
		frame.getContentPane().add(pnl_main);

		JLabel lbl_image = new JLabel();
		lbl_image.setIcon(new ImageIcon("resources/logo.png"));
		lbl_image.setSize(lbl_image.getPreferredSize());
		lbl_image.setLocation(CenteredX(lbl_image)+5, 0);
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
		txt_nickname.setText(System.getProperty("user.name"));
		// txt_nickname.setText("DEBUG_NICK" + (Math.round(Math.random() *
		// 10)));
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
						WebsocketHandler.DEBUG_NICKNAME = txt_nickname
								.getText();
						Application.chat_initialization();
					} catch (IOException e1) {
					}

				}
			}
		});

		JButton BUTTON_DELETEME = new JButton("Test, Delete me!");
		BUTTON_DELETEME.setSize(BUTTON_DELETEME.getPreferredSize());
		BUTTON_DELETEME.setLocation(20, 340);
		BUTTON_DELETEME.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Credits c = new Credits();

			}
		});

		pnl_main.add(BUTTON_DELETEME);

		frame.setResizable(false);
		frame.setVisible(true);

	}

	public int CenteredX(Component a) {

		return frameSize.x / 2 - (a.getSize().width / 2);

	}

	public void disposeFrame() {
		frame.dispose();
	}

	public int buildChoiceMessageBox(String Message, String title,
			Object[] options, int IconType) {
		// Object[] options = { "Yes, please", "No way!" };
		int n = JOptionPane.showOptionDialog(frame, Message, title,
				JOptionPane.YES_NO_OPTION, IconType, null, options, null);

		return n;
	}

	public void setVisibilityLoadingCircle(boolean visibile) {
		loadingCircle.setVisible(visibile);
	}

	public void nicknameInvalid() {

		BalloonTip bln_invalidnick = new BalloonTip(txt_nickname,
				NICKNAME_ALREADY_USED_MESSAGE);
		bln_invalidnick.setVisible(true);
	}

}
