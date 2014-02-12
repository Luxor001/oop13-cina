package client_chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class View extends JFrame implements ViewInterface {

	final private static String TITLE = "CryptoChat";
	final private static int WIDTH = 600;
	final private static int HEIGTH = 400;
	final private static int HGAP = 10;
	final private static int VGAP = 10;
	private ViewObserver controller;

	// create Tabview
	JTabbedPane tabView = new JTabbedPane();
	JButton enter = new JButton("Send");
	List<JTextArea> textList = new ArrayList<>();
	List<JTextArea> chatList = new ArrayList<>();
	List<JScrollPane> scrollList = new ArrayList<>();
	// create listview
	JList<String> usersJList; /*
							 * it's not parametized, but oracle's official docs
							 * use so.
							 */
	DefaultListModel<String> usersList = new DefaultListModel<String>();

	public View() {

		super(TITLE);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(WIDTH, HEIGTH);
		this.buildGUI();
		this.setAction();
		this.setResizable(false);
		this.setVisible(true);
	}

	private void buildGUI() {

		JPanel mainPanel = new JPanel(new BorderLayout(HGAP, VGAP));
		JPanel textPanel = new JPanel(new BorderLayout(HGAP, VGAP));
		JPanel south = new JPanel();
		JTextArea chat = new JTextArea();

		DefaultCaret caret = (DefaultCaret) chat.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// add tabview to form
		this.getContentPane().add(tabView);

		/* add the adapter to intercept the exiting operation from the app */
		this.addWindowListener(new CustomWindowAdapter(this));
		chat.setLineWrap(true);
		chat.setEditable(false);

		usersList.addElement("Pippo");
		usersList.addElement("Pluto");
		usersList.addElement("Paperino");
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
		// add panel to the form
		this.add(south, BorderLayout.SOUTH);

		textPanel.add(textList.get(0), BorderLayout.CENTER);
		mainPanel.add(scrollList.get(0), BorderLayout.CENTER);
		mainPanel.add(textPanel, BorderLayout.SOUTH);
		mainPanel.add(new JPanel().add(usersJList), BorderLayout.EAST);

		tabView.addTab("Main", mainPanel);
	}

	public String sendMessage() {

		// get the selected tab
		int index = getTabIndex();
		String message = textList.get(index).getText();
		if (!message.equals("")) {
			chatList.get(index).append(message + "\n");
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

	public JTextArea createTab(String title) {

		/*
		 * for (int i = 0; i < tabView.getTabCount(); i++) { if
		 * (tabView.getTitleAt(i).equals(title)) { tabView.setSelectedIndex(i);
		 * return null; } }
		 */

		JTextArea chatTmp = new JTextArea();
		JPanel textPanelTmp = new JPanel(new BorderLayout(10, 10));
		JPanel main = new JPanel(new BorderLayout(10, 10));
		JPanel tab = new JPanel();

		DefaultCaret caret = (DefaultCaret) chatTmp.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		chatTmp.setLineWrap(true);
		chatTmp.setEditable(false);

		chatList.add(chatTmp);
		textList.add(this.getMyText());

		scrollList.add(this.getMyScroll(chatTmp));

		textPanelTmp
				.add(textList.get(textList.size() - 1), BorderLayout.CENTER);

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

		return chatTmp;
	}

	public void showMessage(String message, String title) {

		int index = checkTab(title);
		if (index != -1) {
			chatList.get(index).append(message + "\n");
		} else {
			createTab(title).append(message + "\n");
		}
	}

	public void showMessageMain(String Message) {
		chatList.get(0).append("\n" + Message);
	}

	private void setAction() {
		enter.addActionListener(getActionListener());

		usersJList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = checkTab(getTitle());
					if (index == -1) {
						try {
							controller.notifyChatUser();
						} catch (Exception e1) {
						}

						// controller.commandCreateTab();
					} else {
						tabView.setSelectedIndex(index);
					}
				}

			}
		});

	}

	private ActionListener getActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (e.getActionCommand().equals("Send")) {
					controller.commandSendMessage();
				}

				if (e.getActionCommand().equals("x")) {
					controller.commandCloseTab(e);
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
					controller.commandSendMessage();
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
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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

	public int checkTab(String title) {
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
			}
		}
		return found;
	}

	/* needed to intercept the action of closing window */
	class CustomWindowAdapter extends WindowAdapter {

		JFrame window = null;

		CustomWindowAdapter(JFrame window) {
			this.window = window;
		}

		// implement windowClosing method
		public void windowClosing(WindowEvent e) {
			try {
				controller.notifyClosing();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}

}
