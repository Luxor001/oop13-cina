package client_chat;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

public interface ViewInterface {

	void attachViewObserver(ViewObserver controller);

	public String sendMessage();

	void closeTab(ActionEvent e);

	String getTitle();

	void showMessage(String message, String title);

	JTextArea createTab(String title);

	void writeText(String msg,int tab);
}
