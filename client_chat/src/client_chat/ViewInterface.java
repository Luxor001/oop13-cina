package client_chat;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

public interface ViewInterface {

	void attachViewObserver(ViewObserver controller);

	String sendMessage();

	void closeTab(ActionEvent e);

	String getTitle();

	void showMessage(String message, String title);

	JTextArea createTab(String title);

	void writeText(String msg, int tab);

	int getTabIndex();

	void showMessageMain(String Message);

	int buildChoiceMessageBox(String Message, String title, Object[] options,
			int IconType);

	String getTabName();

	void appendUser(String user);

	boolean removeUser(String user);
}
