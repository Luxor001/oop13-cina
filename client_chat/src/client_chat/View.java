package client_chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

/* SFX Used in this Class:
 * -Snap: for plain text notifications. Creative commons 0 license. 
 *   http://www.freesound.org/people/Nmb910/sounds/164079/ 
 *  
 * -Request: for request (file, private chat) notifications. Attribution License.
 http://www.freesound.org/people/cameronmusic/sounds/138413/
 * */

public class View extends JFrame implements ViewInterface {

	final private static String TITLE = "CryptoChat";
	final private static int WIDTH = 600;
	final private static int HEIGTH = 400;
	final private static int HGAP = 10;
	final private static int VGAP = 10;
	Downloaded d;
	private ViewObserver controller;

	public enum sfx {
		REQUEST, PLAIN_TEXT
	}

	// create Tabview
	private JTabbedPane tabView = new JTabbedPane();
	private JButton enter = new JButton("Send");
	private JButton send = new JButton("Send File");
	private JFileChooser chooser;
	private JMenu menu_chat = new JMenu("Chat");
	private JMenu menu_options = new JMenu("Options");
	private JMenu menu_help = new JMenu("Help");
	private String icon_path = "resources/Icon.png";
	private String frame_title = "CryptoChat";
	private List<JTextArea> textList = new ArrayList<>(); /*
														 * ## NEED TO IMPLEMENT
														 * PRIVATE PROPERTY! ##
														 */
	private List<JTextArea> chatList = new ArrayList<>();
	private List<JScrollPane> scrollList = new ArrayList<>();
	// create listview
	private JList<String> usersJList; /*
									 * it's not parametized, but oracle's
									 * official docs use so
									 */
	private DefaultListModel<String> usersList = new DefaultListModel<String>();

	public View() throws IOException {

		super(TITLE);
		// this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(WIDTH, HEIGTH);
		this.buildGUI();
		this.setAction();
		this.setResizable(false);
		this.setVisible(true);

	}

	private void buildGUI() throws IOException {
		JPanel mainPanel = new JPanel(new BorderLayout(HGAP, VGAP));
		JPanel textPanel = new JPanel(new BorderLayout(HGAP, VGAP));
		JPanel south = new JPanel();
		JTextArea chat = new JTextArea();

		JMenuBar menuBar = new JMenuBar();

		JMenuItem menuitem = new JMenuItem("Downloads");
		menu_options.add(menuitem);
		menuitem.addActionListener(getActionListener());
		menuitem = new JMenuItem("Preferences");
		menu_options.add(menuitem);
		menuitem.addActionListener(getActionListener());

		menuitem = new JMenuItem("Chat to..");
		menu_chat.add(menuitem);
		menuitem.addActionListener(getActionListener());
		menu_chat.add(new JSeparator());
		menuitem = new JMenuItem("Exit");
		menu_chat.add(menuitem);
		menuitem.addActionListener(getActionListener());

		menuBar.add(menu_chat);
		menuBar.add(menu_options);
		menuBar.add(menu_help);

		this.setJMenuBar(menuBar);
		// add tabview to form
		this.getContentPane().add(tabView);

		DefaultCaret caret = (DefaultCaret) chat.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		/* add the adapter to intercept the exiting operation from the app */
		this.addWindowListener(new CustomWindowAdapter(this));
		chat.setLineWrap(true);
		chat.setEditable(false);

		usersList.addElement("Pippo");
		usersList.addElement("Pluto");
		usersList.addElement("Francesco");
		usersJList = new JList<String>(usersList);

		// set dimension of userList
		usersJList.setPreferredSize(new Dimension(
				usersJList.getPreferredSize().width * 2, usersJList
						.getPreferredSize().height));

		// add to the list a custom TextArea
		textList.add(this.getMyText());
		chatList.add(chat);
		scrollList.add(this.getMyScroll(chat));

		south.add(enter);
		south.add(send);
		// add panel to the form
		this.add(south, BorderLayout.SOUTH);

		textPanel.add(textList.get(0), BorderLayout.CENTER);
		mainPanel.add(scrollList.get(0), BorderLayout.CENTER);
		mainPanel.add(textPanel, BorderLayout.SOUTH);
		mainPanel.add(new JPanel().add(usersJList), BorderLayout.EAST);

		tabView.addTab("Main", mainPanel);

		InputStream imgStream = new FileInputStream(new File(icon_path));
		BufferedImage myImg = ImageIO.read(imgStream);
		this.setIconImage(myImg);
		this.setTitle(frame_title);

	}

	public String sendMessage() {

		// get the selected tab
		int index = getTabIndex();
		String message = textList.get(index).getText();
		if (!message.equals("")) {
			chatList.get(index).append(
					" " + WebsocketHandler.DEBUG_NICKNAME + ": " + message
							+ "\n");
			textList.get(index).setText("");
			textList.get(index).requestFocus();
		}

		return message;
	}

	public void closeTab(ActionEvent e) {

		JButton button = (JButton) e.getSource();
		for (int i = 1; i < tabView.getTabCount(); i++) {
			JPanel panel = (JPanel) tabView.getTabComponentAt(i);
			JButton button1 = (JButton) panel.getComponent(1);
			if (button.equals(button1)) {
				tabView.remove(i);
				scrollList.remove(i);
				chatList.remove(i);
				textList.remove(i);
				return;
			}

		}
	}

	public void createTab(String title) {

		int index = checkTab(title);
		if (index == -1) {
			JTextArea chatTmp = new JTextArea();
			JPanel textPanelTmp = new JPanel(new BorderLayout(10, 10));
			JPanel main = new JPanel(new BorderLayout(10, 10));
			JPanel tab = new JPanel();

			chatTmp.setLineWrap(true);
			chatTmp.setEditable(false);

			DefaultCaret caret = (DefaultCaret) chatTmp.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

			chatList.add(chatTmp);
			textList.add(this.getMyText());

			scrollList.add(this.getMyScroll(chatTmp));

			textPanelTmp.add(textList.get(textList.size() - 1),
					BorderLayout.CENTER);

			main.add(scrollList.get(textList.size() - 1), BorderLayout.CENTER);
			main.add(textPanelTmp, BorderLayout.SOUTH);

			tabView.addTab(title, main);
			tabView.setSelectedIndex(tabView.getTabCount() - 1);

			tab.setOpaque(false);
			tab.add(new JLabel(title));
			JButton close = new JButton("x");
			close.setOpaque(false);

			close.setContentAreaFilled(false);
			close.addActionListener(this.getActionListener());
			tab.add(close);
			tabView.setTabComponentAt(tabView.getTabCount() - 1, tab);
		} else {
			tabView.setSelectedIndex(index);
		}
	}

	public void showMessage(String message, String title) {

		int index = checkTab(title);

		if (index != getTabIndex() && index != -1) {
			tabView.setBackgroundAt(index, Color.ORANGE);
		}

		if (index != -1) {
			chatList.get(index).append(" " + message + "\n");
		} else {
			createTab(title);
			chatList.get(chatList.size() - 1).append(" " + message + "\n");
		}
	}

	public void showMessageMain(String message) {

		if (getTabIndex() != 0) {
			tabView.setBackgroundAt(0, Color.ORANGE);
			playSound(sfx.PLAIN_TEXT);
		}
		chatList.get(0).append(" " + message + "\n");
	}

	private void setAction() {
		enter.addActionListener(getActionListener());
		send.addActionListener(getActionListener());
		menu_chat.addMouseListener(getMouseListener());
		menu_options.addMouseListener(getMouseListener());
		menu_help.addMouseListener(getMouseListener());
		usersJList.addMouseListener(getMouseListener());
		tabView.addMouseListener(getMouseListener());
	}

	private ActionListener getActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (e.getActionCommand().equals("Send")) {
					new Thread() {
						public void run() {
							controller.commandSendMessage(sendMessage(),
									getTabName());
						}
					}.start();
				}
				if (e.getActionCommand().equals("Exit")) {
					try {
						controller.closeChat();
					} catch (Exception e1) {
					}
				}

				if (e.getActionCommand().equals("x")) {
					controller.commandCloseTab(e);
				}

				if (e.getActionCommand().equals("Downloads")) {
					controller.commandShowDownloads();
				}
				if (e.getActionCommand().equals("Preferences")) {
					System.out.println("Clicked Preferences!");
				}
				if (e.getActionCommand().equals("Send File")) {

					chooser = new JFileChooser();

					int returnVal = chooser.showDialog(new JButton("Send"),
							"Send");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						new Thread() {
							public void run() {
								controller.notifyFileUser(chooser
										.getSelectedFile());
							}
						}.start();
					}
				}
			}
		};
	}

	private MouseAdapter getMouseListener() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				if (e.getComponent() instanceof JList) {
					if (e.getClickCount() == 2) {

						new Thread() {
							public void run() {
								try {
									controller.notifyChatUser(getTitle());
								} catch (Exception e1) {
								}
							}
						}.start();

					}

					// check if i click with righ mouse button
					if (SwingUtilities.isRightMouseButton(e) && e.isMetaDown()
							&& e.getClickCount() == 1) {

						PopUpDemo a = new PopUpDemo();

						int sIndex = usersJList.locationToIndex(e.getPoint());
						usersJList.setSelectedIndex(sIndex);

						Rectangle rSelection = usersJList.getCellBounds(sIndex,
								sIndex + 1);
						// centerx allows to have a small offset
						a.doPop(e, (int) rSelection.getCenterX(), rSelection.y);
					}
				} else {
					if (e.getComponent() instanceof JMenu) {
						JMenu menu = (JMenu) e.getComponent();

						switch (menu.getText()) {
						case "Chat": {

							System.out.println("Private Chat");
						}
							break;

						case "Help": {
							new Thread() {
								public void run() {
									try {
										Credits a = new Credits();
									} catch (BadLocationException e) {
									}
								};
							}.start();

						}

							break;
						}

					} else {
						if (e.getComponent() instanceof JTabbedPane) {
							if (tabView.getBackgroundAt(getTabIndex()) == Color.ORANGE) {
								tabView.setBackgroundAt(getTabIndex(),
										new Color(238, 238, 238));
							}
						}
					}
				}
			}
		};
	}

	private KeyListener getKeyListener() {
		return new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

					e.consume(); // ignore the key pressed
					if (!textList.get(getTabIndex()).getText().trim().isEmpty()) {
						new Thread() {
							public void run() {
								controller.commandSendMessage(sendMessage(),
										getTabName());
							}
						}.start();
					}

				}
			}

			public void keyReleased(KeyEvent e) {
			}
		};
	}

	public void attachViewObserver(ViewObserver controller) {
		this.controller = controller;
	}

	private JScrollPane getMyScroll(JTextArea chat) {

		JScrollPane scroll = new JScrollPane(chat);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setAutoscrolls(true);
		return scroll;
	}

	private JTextArea getMyText() {
		JTextArea text = new JTextArea();
		text.setLineWrap(true);
		text.setPreferredSize(new Dimension(text.getPreferredSize().width, text
				.getPreferredSize().height * 3));

		text.addKeyListener(this.getKeyListener());
		return text;
	}

	public String getTitle() {
		return usersJList.getSelectedValue();
	}

	public String getTabName() {
		return tabView.getTitleAt(getTabIndex());
	}

	public int getTabIndex() {
		return tabView.getSelectedIndex();
	}

	private int checkTab(String title) {
		for (int i = 0; i < tabView.getTabCount(); i++) {
			if (tabView.getTitleAt(i).equals(title)) {

				return i;
			}
		}
		return -1;
	}

	public int buildChoiceMessageBox(String Message, String title,
			Object[] options, int IconType) {
		// Object[] options = { "Yes, please", "No way!" };
		int n = JOptionPane.showOptionDialog(this, Message, title,
				JOptionPane.YES_NO_OPTION, IconType, null, options, null);

		return n;
	}

	public void appendUser(String user) {
		usersList.addElement(user);
	}

	public boolean removeUser(String user) {
		boolean found = false;
		for (int i = 0; i < usersList.size(); i++) {
			if (usersList.get(i).equals(user)) {
				usersList.remove(i);
				found = true;

				class RemoveUser extends Thread {
					String user;

					public RemoveUser(String user) {
						this.user = user;
					}

					public void run() {
						controller.commandRemoveUser(user);
					}
				}

				new RemoveUser(user).start();

			}
		}
		return found;
	}

	public void playSound(sfx soundeffect) {
		String path = "";

		switch (soundeffect) {
		case REQUEST:
			path = "resources/sfx/request.wav";
			break;

		case PLAIN_TEXT:
			path = "resources/sfx/plain.wav";
			break;
		}

		try {

			AudioInputStream audioIn = AudioSystem
					.getAudioInputStream(new File(path));
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* needed to intercept the action of closing window */

	class CustomWindowAdapter extends WindowAdapter {

		JFrame window = null;

		CustomWindowAdapter(JFrame window) {
			this.window = window;
		}

		// implement windowClosing method
		public void windowClosing(WindowEvent e) {
			controller.commandCloseAll();

			try {
				controller.notifyClosing();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			System.exit(0);
		}
	}

	public void closeChat() {

		this.dispose();
		Application.start();

	}

	class PopUpDemo extends JPopupMenu {
		private JMenuItem anItem;

		public PopUpDemo() {
			anItem = new JMenuItem("Private Chat (ssl)");
			add(anItem);
			anItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						controller.notifyChatUser(getTitle());
					} catch (Exception e1) {
					}
				}
			});

			anItem = new JMenuItem("Send File");
			add(anItem);
			anItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						controller.notifySendFileUser();
					} catch (Exception e1) {
					}
				}
			});

			anItem = new JMenuItem("Poke");
			add(anItem);
			anItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// View.This.notify()
					System.out.println("Clicked Poke!");

				}
			});
		}

		public void doPop(MouseEvent e, int x, int y) {

			/*
			 * this.setInvoker(e.getComponent()); this.setLocation(x, y);
			 * this.setSize(this.getPreferredSize()); this.setVisible(true);
			 */
			this.show(e.getComponent(), x, y);

			/*
			 * PopUpDemo menu = new PopUpDemo(); menu.show(e.getComponent(), x,
			 * y);
			 */
		}
	}

}
