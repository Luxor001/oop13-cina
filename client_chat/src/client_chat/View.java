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
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
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
import javax.swing.ListSelectionModel;
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

/**
 * Generates an output representation to the user with a GUI
 * 
 * @author Francesco Cozzolino
 * @author Stefano Belli
 */
@SuppressWarnings("serial")
public class View extends JFrame implements ViewInterface {

	public enum SFX {
		REQUEST,
		PLAIN_TEXT
	}

	
	final private static String TITLE = "CryptoChat";
	final private static int WIDTH = 600;
	final private static int HEIGTH = 400;
	final private static int HGAP = 10;
	final private static int VGAP = 10;
	final private static int WIDTH_USERJLIST = 130;

	Preferences prefs = Preferences.userRoot();
	private ViewObserver controller;
	private JTabbedPane tabView = new JTabbedPane();
	private JButton enter = new JButton("Send");
	private JButton send = new JButton("Send File");
	private JFileChooser chooser;
	private JMenu menu_chat = new JMenu("Chat");
	private JMenu menu_options = new JMenu("Options");
	private JMenu menu_help = new JMenu("Help");
	private String icon_path = "resources/Icon.png";
	private String frame_title = "CryptoChat";
	private List<JTextArea> textList = new ArrayList<>();
	private List<JTextArea> chatList = new ArrayList<>();
	private JList<String> usersJList;
	private DefaultListModel<String> usersList = new DefaultListModel<String>();

	/**
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public View() throws IOException {

		super(TITLE);
		this.setSize(WIDTH, HEIGTH);
		this.buildGUI();
		this.setAction();
		this.setResizable(false);
		this.setVisible(true);

	}

	/**
	 * Creates a GUI for the "main" channel and other components as menu,context
	 * menu
	 * 
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 * 
	 * @author Francesco Cozzolino
	 * @author Stefano Belli
	 */
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
		this.getContentPane().add(tabView);

		this.addWindowListener(new CustomWindowAdapter(this));
		chat.setLineWrap(true);
		chat.setEditable(false);
		DefaultCaret caret = (DefaultCaret) chat.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		usersJList = new JList<String>(usersList);

		JScrollPane scroll = new JScrollPane(usersJList);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(WIDTH_USERJLIST, usersJList
				.getPreferredSize().height));

		usersJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		usersJList.remove(0);

		textList.add(this.getMyText());
		chatList.add(chat);

		south.add(enter);
		south.add(send);
		this.add(south, BorderLayout.SOUTH);

		textPanel.add(textList.get(0), BorderLayout.CENTER);
		mainPanel.add(/* scrollList.get(0) */getMyScroll(chat),
				BorderLayout.CENTER);
		mainPanel.add(textPanel, BorderLayout.SOUTH);
		mainPanel.add(new JPanel().add(scroll), BorderLayout.EAST);

		tabView.addTab("Main", mainPanel);

		InputStream imgStream = new FileInputStream(new File(icon_path));
		BufferedImage myImg = ImageIO.read(imgStream);
		this.setIconImage(myImg);
		this.setLocationRelativeTo(null);
		this.setTitle(frame_title);

	}


	/**
	 * Close the selected tab
	 * 
	 * @author Francesco Cozzolino
	 * 
	 */
	public void closeTab(ActionEvent e) {

		JButton button = (JButton) e.getSource();
		for (int i = 1; i < tabView.getTabCount(); i++) {
			JPanel panel = (JPanel) tabView.getTabComponentAt(i);
			JButton button1 = (JButton) panel.getComponent(1);
			if (button.equals(button1)) {
				tabView.remove(i);
				chatList.remove(i);
				textList.remove(i);
				return;
			}

		}
	}

	/**
	 * Creates a new tab for a private chat with an user
	 * 
	 * @param title
	 *            name for the tab
	 * 
	 * @author Francesco Cozzolino
	 * 
	 */
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

			textPanelTmp.add(textList.get(textList.size() - 1),
					BorderLayout.CENTER);

			main.add(getMyScroll(chatTmp), BorderLayout.CENTER);
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

	/**
	 * Get the message from the textarea for writing message and shows the
	 * message on the textarea sended/received message
	 * 
	 * @return message written on the textarea
	 * 
	 * @author Francesco Cozzolino
	 */
	public String sendMessage() {

		int index = getTabIndex();
		String message = textList.get(index).getText();
		if (!message.equals("")) {
			chatList.get(index).append(
					" " + User.getNickName() + ": " + message + "\n");
			textList.get(index).setText("");
			textList.get(index).requestFocus();
		}

		return message;
	}
	
	
	/**
	 * Shows the received messages from other user in different tabs
	 * 
	 * @author Francesco Cozzolino
	 */
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

	/**
	 * Shows the received message (written in main chat) from other users
	 * 
	 * @author Stefano Belli
	 */
	public void showMessageMain(String message) {

		boolean dfsounds = User.getStoredSounds();

		if (getTabIndex() != 0 && dfsounds) {
			tabView.setBackgroundAt(0, Color.ORANGE);
			playSound(SFX.PLAIN_TEXT);
		}
		chatList.get(0).append(" " + message + "\n");
	}

	/**
	 * Set action event
	 * 
	 * @author Francesco Cozzolino
	 */
	private void setAction() {
		enter.addActionListener(getActionListener());
		send.addActionListener(getActionListener());
		menu_chat.addMouseListener(getMouseListener());
		menu_options.addMouseListener(getMouseListener());
		menu_help.addMouseListener(getMouseListener());
		usersJList.addMouseListener(getMouseListener());
		tabView.addMouseListener(getMouseListener());
	}

	/**
	 * Set the event of JButton and some component of JMenu:
	 * 
	 * When you press JButton "Send",is invoked the routine for send a message.
	 * When you clickon JMenuItem "exit",is invoked the routine for exit from
	 * chat.
	 * When you click on "x" in a tab,is invoked the routine for close the
	 * selected tab.
	 * When you click on JMenuItem "Downloads",is invoked the routine for shows
	 * frame of downloads.
	 * When you click on JMenuItem "Preferences",is invoked the routine for 
	 * shows frame of preferences.
	 * When you press JButton"Send file", is invoked the routine for send a file
	 * to a specific user
	 * 
	 * 
	 * @return ActionListener object
	 * 
	 * @author Francesco Cozzolino
	 * @author Stefano Belli
	 */
	private ActionListener getActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				switch (e.getActionCommand()) {
				case "Send":{
					new Thread() {
						public void run() {
							controller.commandSendMessage(sendMessage(),
									getTabName());
						}
					}.start();
				}
					break;

				case "Exit":{
					try {
						controller.closeChat();
					} catch (Exception e1) {
					}
				}
					break;

				case "x":{
					controller.commandCloseTab(e);
				}
					break;

				case "Downloads":{
					controller.commandShowDownloads();
				}
					break;
				case "Preferences":{
					controller.commandShowPreferences();
				}
					break;
				case "Send File":{
					if (!getTabName().equals("Main")) {
						sendFile(true);
					}
				}
					break;

				case "Chat to..":{
					class ChatTo {
						private JFrame frame = new JFrame();
						private JTextArea txtIp = new JTextArea();
						private JTextArea txtKeyport = new JTextArea();
						private JTextArea txtSSLport = new JTextArea();
						private JLabel lblIp = new JLabel("Ip : ");
						private JLabel lblportKey = new JLabel(
								"Keystore port :");
						private JLabel lblportSSL = new JLabel("SSL port :");
						private JButton chat = new JButton("Chat");
						private JPanel panel = new JPanel(null);
						private String ip = "";

						public ChatTo() {
							frame.setTitle("Chat to");
							frame.setSize(400, 200);
							frame.setVisible(true);
							frame.setLocationRelativeTo(null);
							frame.getContentPane().add(panel);
							frame.setResizable(false);

							lblIp.setSize(lblIp.getPreferredSize());
							lblIp.setLocation(frame.getSize().width / 2 - 189,
									25);

							lblportKey.setSize(lblportKey.getPreferredSize());
							lblportKey.setLocation(
									frame.getSize().width / 2 - 189, 65);

							lblportSSL.setSize(lblportSSL.getPreferredSize());
							lblportSSL.setLocation(
									frame.getSize().width / 2 - 189, 105);

							txtIp.setSize(new Dimension(125, 20));
							txtIp.setLocation(frame.getSize().width / 2 - 102,
									25);
							txtIp.setBorder(BorderFactory
									.createLineBorder(Color.BLACK));

							txtKeyport.setSize(new Dimension(50, 20));
							txtKeyport.setLocation(
									frame.getSize().width / 2 - 102, 65);
							txtKeyport.setBorder(BorderFactory
									.createLineBorder(Color.BLACK));

							txtSSLport.setSize(new Dimension(50, 20));
							txtSSLport.setLocation(
									frame.getSize().width / 2 - 102, 105);
							txtSSLport.setBorder(BorderFactory
									.createLineBorder(Color.BLACK));

							chat.setSize(50, 20);
							chat.setLocation(frame.getSize().width / 2 + 50, 25);
							chat.setBorder(BorderFactory
									.createLineBorder(Color.BLACK));

							chat.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {

									ip = txtIp.getText();
									try {
										final int keyPort = Integer
												.parseInt(txtKeyport.getText());
										final int sslPort = Integer
												.parseInt(txtSSLport.getText());

										if (keyPort < 1 || keyPort > 9999
												|| sslPort < 1
												|| sslPort > 9999) {
											JOptionPane.showOptionDialog(null,
													"Incorrect values",
													"Error",
													JOptionPane.PLAIN_MESSAGE,
													JOptionPane.ERROR_MESSAGE,
													null, null, null);
										} else {

											new Thread() {
												public void run() {
													controller
															.notifyChatUserIp(
																	ip,
																	keyPort,
																	sslPort);
												}
											}.start();
											frame.dispose();
										}
									} catch (NumberFormatException e1) {
										JOptionPane.showOptionDialog(null,
												"Incorrect values", "Error",
												JOptionPane.PLAIN_MESSAGE,
												JOptionPane.ERROR_MESSAGE,
												null, null, null);
									}

								}

							});

							panel.add(lblIp);
							panel.add(lblportKey);
							panel.add(lblportSSL);
							panel.add(txtIp);
							panel.add(txtKeyport);
							panel.add(txtSSLport);
							panel.add(chat);
						}

					}
					new ChatTo();
				}
					break;
				}

			}
		};
	}

	/**
	 * Set event of JList,JTabbedPane and JMenu component:
	 * 
	 *When you click two times on an item from JList,is invoked the routine 
	 *for a private chat with this user.
	 *When you right click on an item from JList,shows a context menu with some
	 *option like as private chat,send file,poke.
	 *When you click on JMenuItem "Help",is shows a frame with credits.
	 *When you click on a tab, if it was an another color than the default 
	 *(a message has arrived),is reset to the default color
	 * 
	 * @return MouseAdapter object
	 * 
	 * @author Francesco Cozzolino
	 * @author Stefano Belli
	 */
	private MouseAdapter getMouseListener() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				if (e.getComponent() instanceof JList) {
					if (e.getClickCount() == 2) {

						privateChat();

					}

					if (SwingUtilities.isRightMouseButton(e) && e.isMetaDown()
							&& e.getClickCount() == 1) {

						PopUpDemo a = new PopUpDemo();

						int sIndex = usersJList.locationToIndex(e.getPoint());
						usersJList.setSelectedIndex(sIndex);

						Rectangle rSelection = usersJList.getCellBounds(sIndex,
								sIndex + 1);
						if (rSelection != null) {
							a.doPop(e, (int) rSelection.getCenterX(),
									rSelection.y);
						}
					}
				} else {
					if (e.getComponent() instanceof JMenu) {
						JMenu menu = (JMenu) e.getComponent();

						if (menu.getText().equals("Help")) {
							new Thread() {
								public void run() {
									try {
										new Credits();
									} catch (BadLocationException e) {
									}
								};
							}.start();

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

	/**
	 * When you press enter key (from keyboard) and the cursor of mouse is
	 * focused in JTextarea for sends message,invokes the routine for sends
	 * message
	 * 
	 * @return KeyListener object
	 * 
	 * @author Francesco Cozzolino
	 */
	private KeyListener getKeyListener() {
		return new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

					e.consume();
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

	/**
	 * 
	 * Creates a JscrollPane
	 * 
	 * @param chat
	 *            textarea for writes message
	 * @return JScrollPane
	 * 
	 * @author Francesco Cozzolino
	 */
	
	private JScrollPane getMyScroll(JTextArea chat) {

		JScrollPane scroll = new JScrollPane(chat);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setAutoscrolls(true);
		return scroll;
	}

	/**
	 * 
	 * Creates a JTextArea for shows written/received messages
	 * 
	 * @return JTextArea
	 * 
	 * @author Francesco Cozzolino
	 */
	private JTextArea getMyText() {
		JTextArea text = new JTextArea();
		text.setLineWrap(true);
		text.setPreferredSize(new Dimension(text.getPreferredSize().width, text
				.getPreferredSize().height * 3));

		text.addKeyListener(this.getKeyListener());
		return text;
	}

	/**
	 * @return the selected value from JList
	 * 
	 * @author Francesco Cozzolino
	 */
	private String getTitleTab() {
		return usersJList.getSelectedValue();
	}

	/**
	 * @return the name of current tab
	 * 
	 * @author Francesco Cozzolino
	 */
	public String getTabName() {
		return tabView.getTitleAt(getTabIndex());
	}

	/**
	 * @return the index of current tab
	 * 
	 * @author Francesco Cozzolino
	 */
	public int getTabIndex() {
		return tabView.getSelectedIndex();
	}

	/**
	 * 
	 * Check if a tab with a specific name exist
	 * 
	 * @param title
	 *            name of tab
	 * @return an index that represent the position of tab, returns -1 if
	 *         doesn't exist a tab with that title
	 * 
	 * @author Francesco Cozzolino
	 */
	private int checkTab(String title) {
		for (int i = 0; i < tabView.getTabCount(); i++) {
			if (tabView.getTitleAt(i).equals(title)) {

				return i;
			}
		}
		return -1;
	}

	/**
	 *JOptionPane building method, usually called from Controller class.
	 * 
	 * @author Stefano Belli
	 */
	public int buildChoiceMessageBox(String Message, String title,
			Object[] options, int IconType) {
		int n = JOptionPane.showOptionDialog(this, Message, title,
				JOptionPane.YES_NO_OPTION, IconType, null, options, null);

		return n;
	}

	/**
	 * Appends the given user in the JList of users.
	 * 
	 * @author Stefano Belli
	 */
	public void appendUser(String user) {
		usersList.addElement(user);
	}

	/**
	 * 
	 * @author Stefano Belli
	 */
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

	/**
	 * 
	 * Play a SFX based on the given SFX enum type variable.
+	 * The soundeffects file address are stored here.
	 * 
	 * @author Stefano Belli
	 */
	public void playSound(SFX soundeffect) {
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

	
	public void closeChat() throws IOException {

		this.dispose();
		Application.start();

	}

	/**
	 * Shows a JFileChooser that permits to select a file to send. Once time you
	 * chooses the file, is invoked the routine for to send the file
	 * 
	 * @param clickFromButton
	 * 
	 * @author Francesco Cozzolino
	 */
	private void sendFile(final boolean clickFromButton) {

		chooser = new JFileChooser();

		int returnVal = chooser.showDialog(new JButton("Send"), "Send");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			new Thread() {
				public void run() {
					try {
						String name = "";
						if (clickFromButton) {
							name = getTabName();
						} else {
							name = getTitleTab();
						}
						controller.notifySendFileUser(chooser.getSelectedFile()
								.getAbsolutePath(), name);
					} catch (Exception e) {
					}
				}
			}.start();
		}

	}

	/**
	 * Invokes the routine for send a request of private chat
	 * 
	 * @author Francesco Cozzolino
	 */
	private void privateChat() {

		new Thread() {
			public void run() {
				try {
					controller.notifyChatUser(getTitleTab());
				} catch (Exception e1) {
				}
			}
		}.start();

	}
	
	/**
	 * 
	 *  
	 * Catches the closing attempt of the window.
	 * Here all the closing connections stream methods of Controller are
	 * called.
	 * 
	 * @author Stefano Belli
	 * 
	 */
	class CustomWindowAdapter extends WindowAdapter {

		JFrame window = null;

		CustomWindowAdapter(JFrame window) {
			this.window = window;
		}

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



	/**
	 * Refers to the "context menu" created by right clicking an username.
	 * 
	 * @author Stefano Belli
	 * 
	 */
	private class PopUpDemo extends JPopupMenu {
		private JMenuItem anItem;

		public PopUpDemo() {
			anItem = new JMenuItem("Private Chat (ssl)");
			add(anItem);
			anItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					privateChat();
				}
			});

			anItem = new JMenuItem("Send File");
			add(anItem);
			anItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					sendFile(false);
				}
			});

			anItem = new JMenuItem("Poke");
			anItem.setEnabled(false);
			add(anItem);
			anItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Clicked Poke!");

				}
			});
		}

		public void doPop(MouseEvent e, int x, int y) {

			this.show(e.getComponent(), x, y);

		}
	}

}
