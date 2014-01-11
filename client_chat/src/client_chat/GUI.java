package client_chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	// textarea for compose message
	JTextArea text = new JTextArea();
	List<JTextArea> textList = new ArrayList<>();
	List<JTextArea> chatList = new ArrayList<>();
	List<JScrollPane> scrollList = new ArrayList<>();
	String[] data = { "pippo", "pluto", "paperino" };
	// create listview
	JList<String> userList = new JList<>(data);

	public GUI() {
		this.setTitle("CryptoChat");
		this.setSize(600, 400);
		this.initPanel();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);

	}

	public void initPanel() {

		// create panel

		JPanel south = new JPanel(new BorderLayout(10, 10));
		JPanel panelMain = new JPanel(new BorderLayout(10, 10));
		JPanel test = new JPanel(new FlowLayout());

		// add tabview to form
		this.getContentPane().add(tabView);

		test.add(enter);
		this.getContentPane().add(test, BorderLayout.SOUTH);

		text.setLineWrap(true);
		chat.setLineWrap(true);
		chat.setEditable(false);

		// create scrollbar to attach textarea
		JScrollPane scroll = new JScrollPane(chat);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// set dimension of textarea
		text.setPreferredSize(new Dimension(text.getPreferredSize().width, text
				.getPreferredSize().height * 3));

		textList.add(text);
		chatList.add(chat);

		// set dimension
		userList.setPreferredSize(new Dimension(
				userList.getPreferredSize().width * 2, userList
						.getPreferredSize().height));

		south.add(text, BorderLayout.CENTER);

		panelMain.add(scroll, BorderLayout.CENTER);
		panelMain.add(south, BorderLayout.SOUTH);
		panelMain.add(new JPanel().add(userList), BorderLayout.EAST);

		// aggiungo dei tab
		tabView.addTab("Main", panelMain);

		scrollList.add(scroll);

		userList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {

					for (int i = 0; i < tabView.getTabCount(); i++) {
						if (tabView.getTitleAt(i).equals(
								userList.getSelectedValue())) {
							tabView.setSelectedIndex(i);
							return;
						}
					}
					JTextArea textTmp = new JTextArea();
					JTextArea chatTmp = new JTextArea();
					textTmp.setLineWrap(true);
					chatTmp.setLineWrap(true);
					chatTmp.setEditable(false);

					// create scrollbar to attach textarea
					JScrollPane scrollTmp = new JScrollPane(chatTmp);
					scrollTmp
							.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					// set dimension of textarea
					textTmp.setPreferredSize(new Dimension(text
							.getPreferredSize().width, textTmp
							.getPreferredSize().height * 3));

					chatList.add(chatTmp);
					textList.add(textTmp);

					scrollList.add(scrollTmp);

					JPanel southTmp = new JPanel(new BorderLayout(10, 10));
					southTmp.add(textList.get(textList.size() - 1),
							BorderLayout.CENTER);

					JPanel privato = new JPanel(new BorderLayout(10, 10));
					privato.add(scrollList.get(textList.size() - 1),
							BorderLayout.CENTER);
					privato.add(southTmp, BorderLayout.SOUTH);

					textTmp.addKeyListener(GUI.this);
					tabView.addTab(userList.getSelectedValue(), privato);
					tabView.setSelectedIndex(tabView.getTabCount() - 1);
					JPanel tab = new JPanel();

					tab.setOpaque(false);
					tab.add(new JLabel(userList.getSelectedValue()));
					JButton close = new JButton("x");
					close.setOpaque(false);
					// close.setBorderPainted(false);
					close.setContentAreaFilled(false);
					close.addActionListener(GUI.this);
					tab.add(close);
					tabView.setTabComponentAt(tabView.getTabCount() - 1, tab);
				}
			}
		});
		text.addKeyListener(this);

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

		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("Send")) {
			int index = tabView.getSelectedIndex();

			chatList.get(index).append(textList.get(index).getText() + "\n");
			textList.get(index).setText("");
			textList.get(index).requestFocus();
		}

		if (e.getActionCommand().equals("x")) {
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
	}

	public static void main(String[] Args) {
		new GUI();
	}
}
