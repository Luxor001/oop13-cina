package client_chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

//Bozza per la GUI 
public class GUI extends JFrame implements KeyListener, ActionListener {

	// create tabView
	JTabbedPane tabView = new JTabbedPane();
	JButton enter = new JButton("Send");
	// textarea for received message
	JTextArea chat = new JTextArea();
	JTextArea chat1 = new JTextArea();
	// textarea for compose message
	JTextArea text = new JTextArea();
	JTextArea text1 = new JTextArea();
	List<JTextArea> textList = new ArrayList<>();
	List<JTextArea> chatList = new ArrayList<>();

	public GUI() {
		this.setTitle("CryptoChat");
		this.setSize(600, 400);
		this.initPanel();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);

	}

	public void initPanel() {

		// create panel
		JPanel privateChat = new JPanel(new BorderLayout(10, 10));
		JPanel south = new JPanel(new BorderLayout(10, 10));
		JPanel south1 = new JPanel(new BorderLayout(10, 10));
		JPanel panelMain = new JPanel(new BorderLayout(10, 10));
		JPanel test = new JPanel(new FlowLayout());

		// add tabview to form
		this.getContentPane().add(tabView);

		test.add(enter);
		this.getContentPane().add(test, BorderLayout.SOUTH);

		text.setLineWrap(true);
		chat.setLineWrap(true);
		chat.setEditable(false);

		text1.setLineWrap(true);
		chat1.setLineWrap(true);
		chat1.setEditable(false);

		// create scrollbar to attach textarea
		JScrollPane scroll = new JScrollPane(chat);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JScrollPane scroll1 = new JScrollPane(chat1);
		scroll1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// set dimension of textarea
		text.setPreferredSize(new Dimension(text.getPreferredSize().width, text
				.getPreferredSize().height * 3));

		text1.setPreferredSize(new Dimension(text1.getPreferredSize().width,
				text1.getPreferredSize().height * 3));

		textList.add(text);
		textList.add(text1);
		chatList.add(chat);
		chatList.add(chat1);

		String[] data = { "pippo", "pluto", "paperino" };
		// create listview
		JList<String> userList = new JList<>(data);

		// set dimension
		userList.setPreferredSize(new Dimension(
				userList.getPreferredSize().width * 2, userList
						.getPreferredSize().height));

		south.add(text, BorderLayout.CENTER);

		south1.add(text1, BorderLayout.CENTER);

		privateChat.add(scroll1, BorderLayout.CENTER);
		privateChat.add(south1, BorderLayout.SOUTH);

		panelMain.add(scroll, BorderLayout.CENTER);
		panelMain.add(south, BorderLayout.SOUTH);
		panelMain.add(new JPanel().add(userList), BorderLayout.EAST);

		// aggiungo dei tab
		tabView.addTab("Main", panelMain);
		tabView.addTab("Chat privata", privateChat);

		// this key work with command alt+N
		tabView.setMnemonicAt(1, KeyEvent.VK_2);
		tabView.setMnemonicAt(0, KeyEvent.VK_1);

		text.addKeyListener(this);
		text1.addKeyListener(this);

		enter.addActionListener(this);

	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			e.consume(); // ignore the key pressed

			int index = tabView.getSelectedIndex();

			chatList.get(index).append(textList.get(index).getText() + "\n");
			textList.get(index).setText("");
			textList.get(index).requestFocus();
			/*
			 * chat.append(text.getText() + "\n"); text.setText("");
			 * text.requestFocus();
			 */
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void actionPerformed(ActionEvent e) {

		int index = tabView.getSelectedIndex();

		chatList.get(index).append(textList.get(index).getText() + "\n");
		textList.get(index).setText("");
		textList.get(index).requestFocus();
	}

	public static void main(String[] Args) {
		new GUI();
	}
}
